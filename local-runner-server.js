const http = require('http');
const {spawn, spawnSync} = require('child_process');
const fs = require('fs');
const path = require('path');
const os = require('os');
const crypto = require('crypto');

const PORT = 8080;
const JAVA_HOME_24 = process.env.JAVA_HOME_24;

function getJavaEnv() {
	if (!JAVA_HOME_24) {
		console.log('JAVA_HOME_24 is not set.  Using default Java.');
		return process.env;
	}
	console.log(`Using Java from: ${JAVA_HOME_24}`);
	return {
		...process.env,
		JAVA_HOME: JAVA_HOME_24,
		PATH: path.join(JAVA_HOME_24, 'bin') + path.delimiter + process.env.PATH
	};
}

const javaEnv = getJavaEnv();
const JAVA_PATH = JAVA_HOME_24 ? path.join(JAVA_HOME_24, 'bin', 'java') : 'java';
const JAVAC_PATH = JAVA_HOME_24 ? path.join(JAVA_HOME_24, 'bin', 'javac') : 'javac';

// ウォームアップ
console.log('Warming up JVM...');
for (let i = 0; i < 3; i++) {
	spawnSync(JAVA_PATH, ['-version'], {env: javaEnv, stdio: 'ignore'});
	spawnSync(JAVAC_PATH, ['-version'], {env: javaEnv, stdio: 'ignore'});
}
console.log('JVM warmed up.');

// コンパイルキャッシュ: hash -> Promise<entry>
const compileCache = new Map();
const MAX_CACHE_SIZE = 10;

function hashCode(sourceCode) {
	return crypto.createHash('md5').update(sourceCode).digest('hex');
}

function cleanupOldCache() {
	if (compileCache.size > MAX_CACHE_SIZE) {
		const oldest = compileCache.keys().next().value;
		compileCache.get(oldest).then(entry => {
			if (entry && entry.tmpDir) {
				try {
					fs.rmSync(entry.tmpDir, {recursive: true, force: true});
				} catch {
				}
			}
		});
		compileCache.delete(oldest);
	}
}

function getCompiledEntry(sourceCode) {
	const hash = hashCode(sourceCode);

	if (compileCache.has(hash)) {
		return compileCache.get(hash);
	}

	cleanupOldCache();

	const promise = new Promise((resolve) => {
		const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'runner-'));
		const sourceFile = path.join(tmpDir, 'Main.java');
		fs.writeFileSync(sourceFile, sourceCode);

		const startTime = Date.now();
		const proc = spawn(JAVAC_PATH, ['Main.java'], {
			cwd: tmpDir,
			env: javaEnv
		});

		const compileTimeout = setTimeout(() => {
			proc.kill();
		}, 30000);

		let stderr = '';
		proc.stderr.on('data', (data) => {
			stderr += data;
		});

		proc.on('close', (code) => {
			clearTimeout(compileTimeout);
			const compileTime = Date.now() - startTime;
			if (code === 0) {
				console.log(`[Compile] OK (${compileTime}ms)`);
				resolve({tmpDir, status: 'compiled', error: null});
			} else {
				console.log(`[Compile] Error (${compileTime}ms)`);
				resolve({tmpDir, status: 'error', error: stderr});
			}
		});

		proc.on('error', (err) => {
			clearTimeout(compileTimeout);
			resolve({tmpDir: null, status: 'error', error: err.message});
		});
	});

	compileCache.set(hash, promise);
	return promise;
}

const server = http.createServer(async (req, res) => {
	res.setHeader('Access-Control-Allow-Origin', '*');
	res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
	res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

	if (req.method === 'OPTIONS') {
		res.writeHead(200);
		res.end();
		return;
	}

	if (req.method !== 'POST') {
		res.writeHead(405);
		res.end('Method Not Allowed');
		return;
	}

	let body = '';
	for await (const chunk of req) {
		body += chunk;
	}

	try {
		const request = JSON.parse(body);
		let response;

		if (request.mode === 'list') {
			response = [
				{
					language: 'Java',
					compilerName: 'java24',
					label: 'Java 24 (Local)'
				}
			];
		} else if (request.mode === 'precompile') {
			getCompiledEntry(request.sourceCode);
			response = {status: 'accepted'};
		} else if (request.mode === 'run') {
			response = await runCode(request);
		} else {
			throw new Error('Unknown mode');
		}

		res.writeHead(200, {'Content-Type': 'application/json'});
		res.end(JSON.stringify(response));
	} catch (error) {
		res.writeHead(500, {'Content-Type': 'application/json'});
		res.end(JSON.stringify({status: 'internalError', stderr: error.message}));
	}
});

async function runCode({compilerName, sourceCode, stdin}) {
	const overallStart = Date.now();

	const entry = await getCompiledEntry(sourceCode);

	const waitTime = Date.now() - overallStart;

	if (entry.status === 'error') {
		return {
			status: 'compileError',
			exitCode: 1,
			stdout: '',
			stderr: entry.error,
			time: waitTime,
			memory: 0
		};
	}

	const execStart = Date.now();
	const result = await new Promise((resolve) => {
		const proc = spawn(JAVA_PATH, [
			'-XX:+TieredCompilation',
			'-XX:TieredStopAtLevel=1',
			'Main'
		], {
			cwd: entry.tmpDir,
			env: javaEnv
		});

		const execTimeout = setTimeout(() => {
			proc.kill();
		}, 10000);

		let stdout = '';
		let stderr = '';

		proc.stdout.on('data', (data) => {
			stdout += data;
		});
		proc.stderr.on('data', (data) => {
			stderr += data;
		});

		if (stdin) {
			proc.stdin.write(stdin);
		}
		proc.stdin.end();

		proc.on('close', (code) => {
			clearTimeout(execTimeout);
			resolve({
				status: code === 0 ? 'success' : 'runtimeError',
				exitCode: code ?? -1,
				stdout,
				stderr,
				time: Date.now() - execStart,
				memory: 0
			});
		});

		proc.on('error', (err) => {
			clearTimeout(execTimeout);
			resolve({
				status: 'internalError',
				exitCode: -1,
				stdout: '',
				stderr: err.message,
				time: 0,
				memory: 0
			});
		});
	});

	const totalTime = Date.now() - overallStart;
	console.log(`Wait: ${waitTime}ms, Exec: ${result.time}ms, Total: ${totalTime}ms`);

	return result;
}

server.listen(PORT, () => {
	console.log(`LocalRunner server listening on http://localhost:${PORT}`);
});