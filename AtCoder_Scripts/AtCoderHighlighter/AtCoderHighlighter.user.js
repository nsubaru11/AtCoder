// ==UserScript==
// @name         AtCoder Highlighter
// @namespace    https://github.com/nsubaru11/AtCoder
// @version      1.0.0
// @description  Highlight numbers and variables in AtCoder task statements strictly for KaTeX
// @author       nsubaru11
// @license      MIT
// @match        https://atcoder.jp/contests/*/tasks/*
// @grant        none
// @run-at       document-idle
// @homepageURL  https://github.com/nsubaru11/AtCoder/tree/main/AtCoder_Scripts/AtCoderHighlighter
// @supportURL   https://github.com/nsubaru11/AtCoder/issues
// @updateURL    https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/AtCoderHighlighter/AtCoderHighlighter.user.js
// @downloadURL  https://raw.githubusercontent.com/nsubaru11/AtCoder/main/AtCoder_Scripts/AtCoderHighlighter/AtCoderHighlighter.user.js
// ==/UserScript==

(function () {
	'use strict';

	const TARGET_KEYWORDS = ['問題文', 'Problem Statement', '制約', 'Constraints'];
	const SKIP_TAGS = new Set(['SCRIPT', 'STYLE', 'CODE', 'PRE', 'VAR', 'KBD', 'SAMP']);

	const NUM_PATTERN = /(^|\W)([+-]?(?:\d{1,3}(?:,\d{3})+|\d+)(?:\.\d+)?(?:e[+-]?\d+)?)/gi;
	const NUM_PURE = /^[+-]?(?:\d{1,3}(?:,\d{3})+|\d+)(?:\.\d+)?(?:e[+-]?\d+)?$/i;

	function injectStyles() {
		if (document.getElementById('atcoder-highlighter-style')) return;

		const style = document.createElement('style');
		style.id = 'atcoder-highlighter-style';
		style.textContent = `
            .target-scope {
                --num-color: #0033B3; /* 数字：濃く深い青 */
                --var-color: #9E2927; /* 変数：濃く深い赤 */
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

	let scheduled = false;

	function scheduleHighlight() {
		if (scheduled) return;
		scheduled = true;
		setTimeout(() => {
			scheduled = false;
			injectStyles();
			markTargetSections();
			highlightNumbers();
		}, 100);
	}

	function observeTaskStatement() {
		const target = document.getElementById('task-statement') || document.body;
		if (!target) return;

		const observer = new MutationObserver(() => scheduleHighlight());
		observer.observe(target, {childList: true, subtree: true, characterData: true});
	}

	scheduleHighlight();
	observeTaskStatement();
})();
