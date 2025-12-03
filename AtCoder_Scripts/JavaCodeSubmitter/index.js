// ==UserScript==
// @name         Java Code Submitter
// @namespace    https://github.com/nsubaru11/AtCoder
// @version      1.0.0
// @description  Enhances Java submission workflow:
// @description  - Force class name to Main
// @description  - Replace DEBUG=true → false
// @description  - Auto-fold Main class after paste
// @description  - Add customizable submit shortcuts
// @author       nsubaru11
// @license      MIT
// @homepageURL  https://github.com/nsubaru11/AtCoder/tree/main/AtCoder_Scripts/UniversalSmartSubmitter
// @supportURL   https://github.com/nsubaru11/AtCoder/issues
// @match        https://onlinejudge.u-aizu.ac.jp/*
// @match        https://atcoder.jp/contests/*
// @match        https://judge.yosupo.jp/problem/*
// @grant        unsafeWindow
// @run-at       document-end
// @updateURL    https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/UniversalSmartSubmitter/index.meta.js
// @downloadURL  https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/UniversalSmartSubmitter/index.user.js
// ==/UserScript==

(function () {
	'use strict';

	// --------------- Utilities ---------------
	const DEFAULT_SETTINGS = {
		// Java のクラス名を Main に強制変更するか
		renameClass: true,
		// DEBUG = true を自動的に false に強制するか
		fixDebug: true,
		// 貼り付け後に Main クラスを自動折りたたみするか
		foldMainOnPaste: true,
		// デバッグログを有効化するか
		logEnabled: true,
	};

	function loadSettings() {
		try {
			const ls = (unsafeWindow || window).localStorage;
			if (!ls) return DEFAULT_SETTINGS;
			const raw = ls.getItem('smartSubmitterSettings');
			if (!raw) return DEFAULT_SETTINGS;
			const parsed = JSON.parse(raw);
			return Object.assign({}, DEFAULT_SETTINGS, parsed);
		} catch {
			return DEFAULT_SETTINGS;
		}
	}

	const SETTINGS = loadSettings();

	const LOG_PREFIX = '[Smart Submitter]';
	const log = (...args) => {
		if (!SETTINGS.logEnabled) return;
		console.log(LOG_PREFIX, ...args);
	};
	// public / final の有無を許容してクラス宣言にマッチ
	const CLASS_DECL_REGEX = /(?<!\/\/.*)(?<!\/\*[\s\S]*?)((?:public\s+)?(?:final\s+)?class\s+)([A-Za-z_][A-Za-z0-9_]*)/;
	const DEBUG_REGEX_REPLACE = /(DEBUG\s*=\s*)true(\s*;)/g;

	/**
	 * main メソッドを含むクラス、あるいはもっとも「それらしい」クラスを探す
	 * @param {string} text
	 * @returns {{prefix: string, name: string, index: number} | null}
	 */
	function findMainClassInfo(text) {
		const CLASS_ALL_REGEX = new RegExp(CLASS_DECL_REGEX.source, 'g');
		const matches = [];
		let m;
		while ((m = CLASS_ALL_REGEX.exec(text)) !== null) {
			matches.push({
				prefix: m[1],
				name: m[2],
				index: m.index,
			});
		}
		if (!matches.length) return null;

		const MAIN_REGEX = /public\s+static\s+void\s+main\s*\(/;
		let mainCandidate = null;

		for (let i = 0; i < matches.length; i++) {
			const current = matches[i];
			const start = current.index;
			const end = (i + 1 < matches.length) ? matches[i + 1].index : text.length;
			const body = text.slice(start, end);
			if (MAIN_REGEX.test(body)) {
				// main() を含むクラスを優先。public が付いていれば即採用
				mainCandidate = current;
				if (/^\s*public\s+/i.test(current.prefix)) break;
			}
		}

		if (mainCandidate) return mainCandidate;

		// main() が見つからなければ、public class を優先し、なければ先頭クラス
		const publicMatch = matches.find(c => /public\s+/i.test(c.prefix));
		return publicMatch || matches[0];
	}

	/**
	 * ペーストされたコードを自動修正する
	 * @param {string} text - ペーストされたテキスト
	 * @returns {{modified: string, didModify: boolean}} - 修正後のテキストと、修正したかどうかのフラグ
	 */
	function modifyPastedCode(text) {
		let modified = text;
		let classReplaced = false;
		let debugReplaced = false;

		// 1. クラス名を Main に置換（必要であれば）
		if (SETTINGS.renameClass) {
			const info = findMainClassInfo(modified);
			if (info) {
				const target = info.prefix + info.name;
				const before = modified.slice(0, info.index);
				const after = modified.slice(info.index + target.length);
				modified = before + info.prefix + 'Main' + after;
				classReplaced = true;
			}
		}

		// 2. DEBUG = true を false に置換（必要であれば）
		if (SETTINGS.fixDebug) {
			let modifiedDebug = modified.replace(DEBUG_REGEX_REPLACE, '$1false$2');
			if (modified !== modifiedDebug) {
				modified = modifiedDebug;
				debugReplaced = true;
			}
		}

		if (classReplaced) log('Class name replaced to Main');
		if (debugReplaced) log('DEBUG flag set to false');

		return {modified, didModify: classReplaced || debugReplaced};
	}

	// --------------- Editor Adapters ---------------
	class EditorAdapter {
		constructor(globalObj) {
			this.g = globalObj || unsafeWindow || window;
			this.initialized = false;
		}

		setup() {
			return false;
		}

		foldMain() {
		}
	}

	class AceEditorAdapter extends EditorAdapter {
		getAce() {
			return this.g && this.g.ace;
		}

		getEditorDiv() {
			return document.getElementById('editor');
		}

		getEditor() {
			const ace = this.getAce();
			const div = this.getEditorDiv();
			if (!ace || !div) return null;
			try {
				return ace.edit(div);
			} catch {
				return null;
			}
		}

		setup() {
			const editor = this.getEditor();
			if (!editor) return false;
			if (this.initialized) return true;

			// 言語が Java のときのみ有効化
			const session = editor.getSession && editor.getSession();
			const modeId =
				(session && (session.$modeId || (session.getMode && session.getMode().$id))) || '';
			if (modeId && !/java/i.test(modeId)) {
				return false;
			}

			editor.on('paste', (e) => {
				if (e && typeof e.text === 'string') {
					const {modified, didModify} = modifyPastedCode(e.text);
					if (didModify) {
						e.text = modified;
						if (SETTINGS.foldMainOnPaste) {
							setTimeout(() => this.foldMain(), 100);
						}
					}
				}
			});
			this.initialized = true;
			log('ACE Editor paste listener set up');
			return true;
		}

		foldMain() {
			const ace = this.getAce();
			const editor = this.getEditor();
			if (!ace || !editor) {
				log('ACE editor not found');
				return;
			}
			const session = editor.getSession();
			const lines = session.getValue().split('\n');
			const mainLine = lines.findIndex(l => /class\s+Main\s*(\{|extends|implements)/.test(l));
			if (mainLine === -1) {
				log('Main class not found in ACE editor');
				return;
			}
			const existingFold = (session.getAllFolds() || []).find(fold => fold.start.row === mainLine);
			if (existingFold) {
				session.expandFold(existingFold);
				log('Main class unfolded');
				return;
			}
			const widget = session.getFoldWidget(mainLine);
			if (widget) {
				const range = session.getFoldWidgetRange(mainLine);
				if (range) {
					session.addFold('...', range);
					log('Main class folded');
					return;
				}
			}
			let brace = 0, endLine = -1;
			for (let i = mainLine; i < lines.length; i++) {
				brace += (lines[i].match(/\{/g) || []).length;
				brace -= (lines[i].match(/}/g) || []).length;
				if (i > mainLine && brace === 0) {
					endLine = i;
					break;
				}
			}
			if (endLine > mainLine) {
				const Range = ace.require('ace/range').Range;
				const foldRange = new Range(mainLine, lines[mainLine].length, endLine, 0);
				session.addFold('...', foldRange);
				log('Main class folded (manual range)');
			}
		}
	}

	class MonacoEditorAdapter extends EditorAdapter {
		getMonaco() {
			return this.g && this.g.monaco;
		}

		getEditor() {
			const monaco = this.getMonaco();
			if (!monaco || !monaco.editor) return null;
			const editors = monaco.editor.getEditors();
			return (editors && editors.length) ? editors[0] : null;
		}

		setup() {
			const editor = this.getEditor();
			if (!editor) return false;
			if (this.initialized) return true;

			const model = editor.getModel && editor.getModel();
			if (!model) return false;
			// 言語が Java のモデルに対してのみ有効化
			if (model.getLanguageId && model.getLanguageId() !== 'java') return false;

			try {
				editor.onDidPaste((e) => {
					const pastedText = model.getValueInRange(e.range);
					const {modified, didModify} = modifyPastedCode(pastedText);
					if (didModify) {
						editor.executeEdits('uss-paste', [{range: e.range, text: modified}]);
						if (SETTINGS.foldMainOnPaste) {
							setTimeout(() => this.foldMain(), 100);
						}
					}
				});
			} catch (err) {
				log('Monaco onDidPaste not available, using onDidChangeContent');
				model.onDidChangeContent((e) => {
					if (e.isFlush) return;
					if (e.changes.length === 1) {
						const {text, range} = e.changes[0];
						if (text && text.length >= 30) {
							const {modified, didModify} = modifyPastedCode(text);
							if (didModify) {
								editor.executeEdits('uss-change', [{range, text: modified}]);
								log('Monaco Editor: Code modified (fallback)');
								if (SETTINGS.foldMainOnPaste) {
									setTimeout(() => this.foldMain(), 100);
								}
							}
						}
					}
				});
			}
			this.initialized = true;
			log('Monaco Editor paste listener set up');
			return true;
		}

		foldMain() {
			const editor = this.getEditor();
			if (!editor) return;
			const model = editor.getModel();
			if (!model) return;
			const lines = model.getValue().split('\n');
			let mainLine = -1;
			for (let i = 0; i < lines.length; i++) {
				if (/class\s+Main\s*(\{|extends|implements)/.test(lines[i])) {
					mainLine = i + 1;
					break;
				}
			}
			if (mainLine === -1) {
				log('Main class not found (Monaco)');
				return;
			}
			editor.setPosition({lineNumber: mainLine, column: 1});
			editor.focus();
			const action = editor.getAction && editor.getAction('editor.toggleFold');
			if (action && action.run) {
				action.run();
				log('Main class fold toggled');
			}
		}
	}

	// --------------- Site Model ---------------
	class Site {
		constructor(hostSubstr, shortcutFn, submitButtonGetter, editorAdapter) {
			this.hostSubstr = hostSubstr;
			this.shortcut = shortcutFn;
			this.getSubmitButton = submitButtonGetter;
			this.editor = editorAdapter;
			this._cachedBtn = null;
		}

		matches(hostname) {
			return hostname.includes(this.hostSubstr);
		}

		findSubmitButton() {
			if (this._cachedBtn && document.contains(this._cachedBtn)) return this._cachedBtn;
			const btn = this.getSubmitButton && this.getSubmitButton();
			if (btn) this._cachedBtn = btn;
			return btn;
		}
	}

	// --------------- Orchestrator ---------------
	class SmartSubmitter {
		constructor(globalObj) {
			this.g = globalObj || unsafeWindow || window;
			this.sites = [];
			this.active = null;
			this._keybound = false;
		}

		registerSite(site) {
			this.sites.push(site);
		}

		detect() {
			const host = window.location.hostname;
			this.active = this.sites.find(s => s.matches(host)) || null;
			if (this.active) log('Initialized for:', host);
			return !!this.active;
		}

		setupEditorLazy() {
			if (!this.active) return;
			const trySetup = () => this.active.editor && this.active.editor.setup();
			if (trySetup()) return;
			const observer = new MutationObserver(() => {
				if (trySetup()) observer.disconnect();
			});
			observer.observe(document.body, {childList: true, subtree: true});
			// エディタ生成が遅いケースに備えて待ち時間を延長
			setTimeout(() => observer.disconnect(), 30000);
		}

		registerKeybindings() {
			if (!this.active || this._keybound) return;
			this._keybound = true;
			document.addEventListener('keydown', (event) => {
				if (this.active.shortcut(event)) {
					const btn = this.active.findSubmitButton();
					if (btn) {
						event.preventDefault();
						event.stopPropagation();
						btn.click();
						log('Submit button clicked');
					} else {
						log('Submit button not found');
					}
				} else if (event.ctrlKey && event.shiftKey && event.key === 'M') {
					event.preventDefault();
					event.stopPropagation();
					this.active.editor && this.active.editor.foldMain();
				}
			}, true);
			log('Keybindings registered (submit + Ctrl+Shift+M)');
		}

		start() {
			if (!this.detect()) return;
			this.registerKeybindings();
			setTimeout(() => this.setupEditorLazy(), 500);
		}
	}

	// --------------- Boot ---------------
	// ★ 修正：すべてのクラス定義の *後* に起動コードを配置
	const g = unsafeWindow || window;
	const submitter = new SmartSubmitter(g);

	// AOJ (ACE)
	submitter.registerSite(new Site(
		'onlinejudge.u-aizu.ac.jp',
		(e) => e.ctrlKey && !e.shiftKey && e.key === 'Enter',
		() => document.querySelector('.editorFooter .submitBtn') || document.getElementById('submit_button'),
		new AceEditorAdapter(g)
	));

	// AtCoder (ACE)
	submitter.registerSite(new Site(
		'atcoder.jp',
		(e) => e.ctrlKey && e.shiftKey && e.key === 'Enter',
		() => document.getElementById('submit'),
		new AceEditorAdapter(g)
	));

	// Library Checker (Monaco)
	submitter.registerSite(new Site(
		'judge.yosupo.jp',
		(e) => e.ctrlKey && e.shiftKey && e.altKey && e.key === 'Enter',
		() => {
			const forms = document.querySelectorAll('form');
			for (const form of forms) {
				const buttons = form.querySelectorAll('button[type="submit"], input[type="submit"]');
				for (const btn of buttons) {
					const text = (btn.textContent || btn.innerText || '').trim().toLowerCase();
					if (text.includes('提出') || text.includes('submit')) return btn;
				}
			}
			return null;
		},
		new MonacoEditorAdapter(g)
	));

	submitter.start();

})();
