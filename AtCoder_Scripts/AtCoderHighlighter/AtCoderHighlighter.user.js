// ==UserScript==
// @name         AtCoder Highlighter
// @namespace    https://github.com/nsubaru11/AtCoder/AtCoder_Scripts
// @version      1.1.7
// @description  Highlight numbers and variables in AtCoder task statements strictly for KaTeX
// @author       nsubaru11
// @license      MIT
// @match        https://atcoder.jp/contests/*/tasks/*
// @grant        GM_getValue
// @grant        GM_setValue
// @grant        GM_registerMenuCommand
// @run-at       document-idle
// @homepageURL  https://github.com/nsubaru11/AtCoder/tree/main/AtCoder_Scripts/AtCoderHighlighter
// @supportURL   https://github.com/nsubaru11/AtCoder/issues
// @updateURL    https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/AtCoderHighlighter/AtCoderHighlighter.user.js
// @downloadURL  https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/AtCoderHighlighter/AtCoderHighlighter.user.js
// ==/UserScript==

(function () {
	'use strict';

	const TARGET_KEYWORDS = ['問題文', 'Problem Statement', '制約', 'Constraints'];
	const TIME_LIMIT_KEYWORDS = ['Time Limit', '実行時間制限'];
	const MEMORY_LIMIT_KEYWORDS = ['Memory Limit', 'メモリ制限'];
	const SKIP_TAGS = new Set(['SCRIPT', 'STYLE', 'CODE', 'PRE', 'VAR', 'KBD', 'SAMP']);

	const NUM_PATTERN = /(^|\W)([+-]?(?:\d{1,3}(?:,\d{3})+|\d+)(?:\.\d+)?(?:e[+-]?\d+)?)/gi;
	const NUM_PURE = /^[+-]?(?:\d{1,3}(?:,\d{3})+|\d+)(?:\.\d+)?(?:e[+-]?\d+)?$/i;

	const DEFAULT_COLORS = {
		num: '#0033B3',
		var: '#9E2927',
		time: '#b3542a',
		memory: '#1d643b',
	};

	function normalizeHexColor(input) {
		if (typeof input !== 'string') return null;
		const value = input.trim();
		if (/^#[0-9a-fA-F]{3}$/.test(value)) {
			return `#${value[1]}${value[1]}${value[2]}${value[2]}${value[3]}${value[3]}`;
		}
		if (/^#[0-9a-fA-F]{4}$/.test(value)) {
			return `#${value[1]}${value[1]}${value[2]}${value[2]}${value[3]}${value[3]}${value[4]}${value[4]}`;
		}
		if (/^#[0-9a-fA-F]{6}$/.test(value) || /^#[0-9a-fA-F]{8}$/.test(value)) {
			return value;
		}
		return null;
	}

	function normalizeColor(input) {
		if (typeof input !== 'string') return null;
		const trimmed = input.trim();
		const normalizedHex = normalizeHexColor(trimmed);
		if (normalizedHex) return normalizedHex;
		if (/^(rgb|rgba|hsl|hsla)\([^)]*\)$/.test(trimmed)) return trimmed;
		return null;
	}

	function readColors() {
		if (typeof GM_getValue !== 'function') return Object.assign({}, DEFAULT_COLORS);
		return {
			num: GM_getValue('numColor', DEFAULT_COLORS.num),
			var: GM_getValue('varColor', DEFAULT_COLORS.var),
			time: GM_getValue('timeLimitColor', DEFAULT_COLORS.time),
			memory: GM_getValue('memoryLimitColor', DEFAULT_COLORS.memory),
		};
	}

	function writeColor(key, value) {
		if (typeof GM_setValue !== 'function') return;
		GM_setValue(key, value);
	}

	function injectStyles() {
		if (document.getElementById('atcoder-highlighter-style')) return;
		const colors = readColors();

		const style = document.createElement('style');
		style.id = 'atcoder-highlighter-style';
		style.textContent = `
            .target-scope {
                --num-color: ${colors.num}; /* 数字：濃く深い青 */
                --var-color: ${colors.var}; /* 変数：濃く深い赤 */
                --font-weight: bold;  /* 視認性を最大化する太字 */
            }

            /* KaTeX の変数 (アルファベット) : .mathnormal クラスを持つ要素 */
            .target-scope .katex .mathnormal {
                color: var(--var-color) !important;
                font-weight: var(--font-weight) !important;
            }

            /* 数値（JSで抽出した部分） */
            .target-scope .number {
                color: var(--num-color) !important;
                font-weight: var(--font-weight) !important;
            }

            /* 実行時間/メモリ制限の値のみ強調 */
            .time-limit-value {
                color: ${colors.time};
                font-weight: 800;
            }

            .time-limit-value-number {
                color: ${colors.time};
                font-weight: 800;
                font-size: 1.08em;
            }

            .memory-limit-value {
                color: ${colors.memory};
                font-weight: 800;
            }
        `;
		(document.head || document.documentElement).appendChild(style);
	}

	function markTargetSections(root) {
		const sections = (root || document).querySelectorAll('#task-statement section');

		sections.forEach(sec => {
			const h3 = sec.querySelector('h3');
			if (!h3) return;

			const title = h3.textContent.trim();
			if (TARGET_KEYWORDS.some(kw => title.includes(kw))) {
				sec.classList.add('target-scope');
			}
		});
	}

	function isPureNumber(text) {
		return NUM_PURE.test(text.trim());
	}

	function highlightKaTeXNumbers(scope) {
		const elements = scope.querySelectorAll('.katex .mord, .katex .text, .katex .mord.text');
		elements.forEach(el => {
			if (el.classList.contains('number')) return;
			if (el.classList.contains('mathnormal')) return;
			if (isPureNumber(el.textContent)) {
				el.classList.add('number');
			}
		});
	}

	function highlightTextNumbers(scope) {
		const walker = document.createTreeWalker(
			scope,
			NodeFilter.SHOW_TEXT,
			{
				acceptNode: function (node) {
					const parent = node.parentNode;
					if (!parent || !parent.tagName) return NodeFilter.FILTER_REJECT;

					const tagName = parent.tagName.toUpperCase();
					if (SKIP_TAGS.has(tagName)) return NodeFilter.FILTER_REJECT;

					if (typeof parent.closest === 'function') {
						if (parent.closest('.katex, var, .number')) {
							return NodeFilter.FILTER_REJECT;
						}
					}

					return NodeFilter.FILTER_ACCEPT;
				}
			}
		);

		const nodesToProcess = [];
		let currentNode;
		while ((currentNode = walker.nextNode())) {
			if (/\d/.test(currentNode.nodeValue)) {
				nodesToProcess.push(currentNode);
			}
		}

		nodesToProcess.forEach(node => {
			const text = node.nodeValue;
			if (!NUM_PATTERN.test(text)) return;

			const fragment = document.createDocumentFragment();
			let lastIndex = 0;
			let match;

			NUM_PATTERN.lastIndex = 0;
			while ((match = NUM_PATTERN.exec(text)) !== null) {
				const fullStart = match.index;
				const prefix = match[1] || '';
				const numberText = match[2];
				const numberStart = fullStart + prefix.length;
				const numberEnd = numberStart + numberText.length;

				if (fullStart > lastIndex) {
					fragment.appendChild(document.createTextNode(text.slice(lastIndex, fullStart)));
				}
				if (prefix) {
					fragment.appendChild(document.createTextNode(prefix));
				}
				const span = document.createElement('span');
				span.className = 'number';
				span.textContent = numberText;
				fragment.appendChild(span);

				lastIndex = numberEnd;
			}
			if (lastIndex < text.length) {
				fragment.appendChild(document.createTextNode(text.slice(lastIndex)));
			}
			node.parentNode.replaceChild(fragment, node);
		});
	}

	function highlightNumbers() {
		const scopes = document.querySelectorAll('.target-scope');
		scopes.forEach(scope => {
			highlightKaTeXNumbers(scope);
			highlightTextNumbers(scope);
		});
	}

	function wrapLimitValue(element, keyword, className, options = {}) {
		const walker = document.createTreeWalker(
			element,
			NodeFilter.SHOW_TEXT,
			{
				acceptNode: function (node) {
					const parent = node.parentNode;
					if (!parent || !parent.tagName) return NodeFilter.FILTER_REJECT;

					const tagName = parent.tagName.toUpperCase();
					if (SKIP_TAGS.has(tagName)) return NodeFilter.FILTER_REJECT;
					if (typeof parent.closest === 'function') {
						if (parent.closest('.katex, var, .number, .time-limit-value, .time-limit-value-number, .memory-limit-value')) {
							return NodeFilter.FILTER_REJECT;
						}
					}
					return NodeFilter.FILTER_ACCEPT;
				}
			}
		);

		const nodes = [];
		let currentNode;
		while ((currentNode = walker.nextNode())) {
			if (currentNode.nodeValue && currentNode.nodeValue.includes(keyword)) {
				nodes.push(currentNode);
			}
		}

		const valuePattern = new RegExp(`${keyword}\\s*[:：]\\s*([0-9][0-9,]*(?:\\.[0-9]+)?)(\\s*[a-zA-Z]+)?`, 'g');

		nodes.forEach(node => {
			const text = node.nodeValue;
			if (!text || !text.includes(keyword)) return;
			const fragment = document.createDocumentFragment();
			let lastIndex = 0;
			let match;
			while ((match = valuePattern.exec(text)) !== null) {
				const fullStart = match.index;
				const valueNumber = match[1] || '';
				const valueUnit = match[2] || '';
				const valueStart = fullStart + match[0].lastIndexOf(valueNumber);
				const valueEnd = valueStart + valueNumber.length;
				if (fullStart > lastIndex) {
					fragment.appendChild(document.createTextNode(text.slice(lastIndex, fullStart)));
				}
				fragment.appendChild(document.createTextNode(text.slice(fullStart, valueStart)));
				if (options.numberOnly) {
					const span = document.createElement('span');
					span.className = options.numberClass || className;
					span.textContent = valueNumber;
					fragment.appendChild(span);
					if (valueUnit) fragment.appendChild(document.createTextNode(valueUnit));
				} else {
					const span = document.createElement('span');
					span.className = className;
					span.textContent = valueNumber + valueUnit;
					fragment.appendChild(span);
				}
				lastIndex = valueEnd;
			}
			if (lastIndex < text.length) {
				fragment.appendChild(document.createTextNode(text.slice(lastIndex)));
			}
			if (node.parentNode) node.parentNode.replaceChild(fragment, node);
		});
	}

	function emphasizeLimits() {
		const root = document.getElementById('main-container') || document.body;
		if (!root) return;

		const candidates = root.querySelectorAll('p, dt, dd, th, td, div, li');
		candidates.forEach(el => {
			const text = el.textContent || '';
			const hasTime = TIME_LIMIT_KEYWORDS.some(kw => text.includes(kw));
			const hasMemory = MEMORY_LIMIT_KEYWORDS.some(kw => text.includes(kw));
			if (!hasTime && !hasMemory) return;

			if (hasTime && !el.querySelector('.time-limit-value-number')) {
				TIME_LIMIT_KEYWORDS.forEach(kw => wrapLimitValue(el, kw, 'time-limit-value', {
					numberOnly: true,
					numberClass: 'time-limit-value-number',
				}));
			}
			if (hasMemory && !el.querySelector('.memory-limit-value')) {
				MEMORY_LIMIT_KEYWORDS.forEach(kw => wrapLimitValue(el, kw, 'memory-limit-value'));
			}
		});
	}

	let scheduled = false;

	function scheduleHighlight() {
		if (scheduled) return;
		scheduled = true;
		setTimeout(() => {
			scheduled = false;
			injectStyles();
			markTargetSections();
			highlightNumbers();
			emphasizeLimits();
		}, 100);
	}

	function resetStyles() {
		const style = document.getElementById('atcoder-highlighter-style');
		if (style) style.remove();
		injectStyles();
		scheduleHighlight();
	}

	function registerMenu() {
		if (typeof GM_registerMenuCommand !== 'function') return;

		GM_registerMenuCommand('Highlighter: 数字の色', () => {
			const current = readColors();
			const next = prompt('数字の色 (例: #0033B3 / #03b / rgb(0,51,179))', current.num);
			if (!next) return;
			const normalized = normalizeColor(next);
			if (!normalized) return alert('色の形式が正しくありません。');
			writeColor('numColor', normalized);
			resetStyles();
		});

		GM_registerMenuCommand('Highlighter: 変数の色', () => {
			const current = readColors();
			const next = prompt('変数の色 (例: #9E2927 / #c33 / rgb(158,41,39))', current.var);
			if (!next) return;
			const normalized = normalizeColor(next);
			if (!normalized) return alert('色の形式が正しくありません。');
			writeColor('varColor', normalized);
			resetStyles();
		});

		GM_registerMenuCommand('Highlighter: 実行時間制限の色', () => {
			const current = readColors();
			const next = prompt('実行時間制限の色 (例: #b3542a / #c73 / rgb(179,84,42))', current.time);
			if (!next) return;
			const normalized = normalizeColor(next);
			if (!normalized) return alert('色の形式が正しくありません。');
			writeColor('timeLimitColor', normalized);
			resetStyles();
		});

		GM_registerMenuCommand('Highlighter: メモリ制限の色', () => {
			const current = readColors();
			const next = prompt('メモリ制限の色 (例: #1d643b / #0a6 / rgb(29,100,59))', current.memory);
			if (!next) return;
			const normalized = normalizeColor(next);
			if (!normalized) return alert('色の形式が正しくありません。');
			writeColor('memoryLimitColor', normalized);
			resetStyles();
		});
	}

	function observeTaskStatement() {
		const target = document.getElementById('task-statement') || document.body;
		if (!target) return;

		const observer = new MutationObserver(() => scheduleHighlight());
		observer.observe(target, {childList: true, subtree: true, characterData: true});
	}

	scheduleHighlight();
	observeTaskStatement();
	registerMenu();
})();
