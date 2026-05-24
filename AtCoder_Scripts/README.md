# userscripts

旧 UserScript 配布パスです。

現在の開発元と新規配布先は `tools/userscripts` です。このディレクトリは、既存の Tampermonkey / Violentmonkey インストールを新しい `dist/*.user.js` へ引き継ぐために残しています。

## スクリプト一覧

### AtCoder Custom Default Submissions

AtCoder の提出一覧の絞り込み・並び替えのデフォルト設定を適用します。

- **[インストール](https://raw.githubusercontent.com/nsubaru11/AtCoder/main/tools/userscripts/AtCoderCustomDefaultSubmissions/dist/AtCoderCustomDefaultSubmissions.user.js)**

### AtCoder Easy Test for Java

サンプル入出力のテストを簡単に行えるようにします（Java 向け拡張）。

- **[インストール](https://raw.githubusercontent.com/nsubaru11/AtCoder/main/tools/userscripts/AtCoderEasyTestForJava/dist/AtCoderEasyTestForJava.user.js)**
- ローカル実行サーバーは `tools/runner/README.md` を参照してください（WSL + 常駐 JVM 構成）。

### AtCoder Highlighter

問題文の数字・変数（KaTeX）を強調し、実行時間制限も見やすくします。

- **[インストール](https://raw.githubusercontent.com/nsubaru11/AtCoder/main/tools/userscripts/AtCoderHighlighter/dist/AtCoderHighlighter.user.js)**

### AtCoder Listing Tasks

「問題」タブにホバーすると、コンテスト内の各問題ページへ移動できるドロップダウンを表示します。

- **[インストール](https://raw.githubusercontent.com/nsubaru11/AtCoder/main/tools/userscripts/AtCoderListingTasks/dist/AtCoderListingTasks.user.js)**

### AtCoder Perf Graph

レーティンググラフにパフォーマンスのグラフを重ねて表示します。

- **[インストール](https://raw.githubusercontent.com/nsubaru11/AtCoder/main/tools/userscripts/AtCoderRatingGraph/dist/AtCoderRatingGraph.user.js)**

### Java Code Submitter

Java 提出の補助機能（Main/DEBUG 自動修正、折りたたみ、ショートカットなど）を提供します。

- **[インストール](https://raw.githubusercontent.com/nsubaru11/AtCoder/main/tools/userscripts/JavaCodeSubmitter/dist/JavaCodeSubmitter.user.js)**

## 使い方

1. [Tampermonkey](https://www.tampermonkey.net/) をインストールします。
2. ブラウザの設定から `サイトが JavaScript を使用できるようにする` をチェックします。
3. 上記のリンクをクリックしてスクリプトをインストールしてください。

## 補足

本ディレクトリ (`AtCoder_Scripts/`) は **旧配布パス（互換用）** です。各 `*.user.js` の `@updateURL` / `@downloadURL` は基本的に新パス `tools/userscripts/<Name>/dist/<Name>.user.js` に向けてあるため、既存インストール済みの Tampermonkey は自動で新パスへ追従します。

例外として、AtCoder Highlighter は GreasyFork の配布 URL を維持しています。

新規インストール・今後の開発は以下を参照してください。

- UserScript ソース: `tools/userscripts/<Name>/src/main.ts`
- UserScript 生成物: `tools/userscripts/<Name>/dist/<Name>.user.js`
- CLI 提出 / ローカルランナー: `tools/runner/`
