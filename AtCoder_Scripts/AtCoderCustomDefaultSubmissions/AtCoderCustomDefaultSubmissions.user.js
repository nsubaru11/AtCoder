// ==UserScript==
// @name         AtCoder Custom Default Submissions
// @namespace    https://github.com/nsubaru11/AtCoder/AtCoder_Scripts
// @version      1.5.2
// @description  AtCoderのすべての提出の絞り込み、並び替え設定のデフォルトを設定します。メニューから設定を変更できます。
// @author       ktnyori (original), nsubaru11 (modified)
// @license      MIT
// @include      https://atcoder.jp/contests/*
// @grant        GM_getValue
// @grant        GM_setValue
// @grant        GM_registerMenuCommand
// @homepageURL  https://github.com/nsubaru11/AtCoder/tree/main/AtCoder_Scripts/AtCoderCustomDefaultSubmissions
// @supportURL   https://github.com/nsubaru11/AtCoder/issues
// @updateURL    https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/AtCoderCustomDefaultSubmissions/AtCoderCustomDefaultSubmissions.user.js
// @downloadURL  https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/AtCoderCustomDefaultSubmissions/AtCoderCustomDefaultSubmissions.user.js
// ==/UserScript==

(function () {
	'use strict';

	const DEFAULTS = {
		language: 'Java',
		status: 'AC',
		orderBy: 'time_consumption',
		includeTaskFilter: true,
	};

	function readConfig() {
		const raw = typeof GM_getValue === 'function' ? GM_getValue('config', {}) : {};
		if (raw && typeof raw === 'object') return Object.assign({}, DEFAULTS, raw);
		return Object.assign({}, DEFAULTS);
	}

	function writeConfig(config) {
		if (typeof GM_setValue === 'function') {
			GM_setValue('config', config);
		}
	}

	function configure() {
		const current = readConfig();
		const language = window.prompt('Language name (e.g. Java, C#, Python3, Rust):', current.language);
		if (language === null) return;
		const status = window.prompt('Status filter (AC/WA/TLE/... or empty for all):', current.status);
		if (status === null) return;
		const orderBy = window.prompt('Sort key (source_length/time_consumption/memory_consumption/score):', current.orderBy);
		if (orderBy === null) return;
		const includeTaskFilter = window.confirm('問題ページでは問題番号で絞り込みを追加しますか？');
		writeConfig({
			language: language.trim() || DEFAULTS.language,
			status: status.trim(),
			orderBy: orderBy.trim() || DEFAULTS.orderBy,
			includeTaskFilter,
		});
		window.alert('設定を保存しました。ページを再読み込みしてください。');
	}

	if (typeof GM_registerMenuCommand === 'function') {
		GM_registerMenuCommand('AtCoder Custom Default Submissions: 設定', configure);
	}

	const config = readConfig();

	// 問題ページにいるときは問題番号での絞り込みも追加
	const taskPage = location.href.match(/tasks\/(.+?)$/);
	let task = '';
	if (config.includeTaskFilter && taskPage && taskPage[1]) {
		task = taskPage[1];
	}
	const params = {
		'f.LanguageName': config.language,
		// AC, WA, TLE, MLE, RE, CE, QLE, OLE, IE, WJ, WR, Judging
		'f.Status': config.status,
		// source_length, time_consumption, memory_consumption, score
		'orderBy': config.orderBy,
	};
	if (task) params['f.Task'] = task;
	const esc = encodeURIComponent;
	const querystring = Object.keys(params).map(k => esc(k) + '=' + esc(params[k])).join('&');
	const links = document.querySelectorAll('#contest-nav-tabs a');
	for (let i = 0; i < links.length; i++) {
		const href = links[i].getAttribute('href');
		if (href && href.endsWith('submissions')) {
			links[i].setAttribute('href', `${href}?${querystring}`);
		}
	}
})();
