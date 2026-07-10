# AtCoder

[AtCoder](https://atcoder.jp/) 向けの解答・補助ツールをまとめたリポジトリです。

- **プロフィール**: https://atcoder.jp/users/nsubaru
- **問題フォルダ**: `ABC/` などコンテスト単位で `src/` に解答を配置
- **テンプレート**: `template/`のライブラリ版・自己完結版テンプレート

## リポジトリ構成

```text
AtCoder/
├── ABC/                 # コンテスト別の解答 (Java のみ)
├── library/             # パッケージ化された競プロライブラリ (submodule)
├── template/            # ライブラリ版・自己完結版テンプレート
├── setup-library.ps1    # IntelliJモジュール依存とFile Templateの設定
├── tools/
│   ├── shared/          # runner / userscripts 共通 TypeScript
│   ├── userscripts/     # Tampermonkey 用 UserScript (TypeScript → .user.js)
│   └── runner/          # ローカル実行・CLI (test / submit)
└── AtCoder_Scripts/     # 旧配布パス (@updateURL リダイレクト用)
```

## 補助ツール (tools)

| パッケージ       | 説明               | README                                                       |
|-------------|------------------|--------------------------------------------------------------|
| tools       | Bun ワークスペースの親    | [tools/README.md](./tools/README.md)                         |
| shared      | 共通ライブラリ          | [tools/shared/README.md](./tools/shared/README.md)           |
| userscripts | ブラウザ拡張スクリプト      | [tools/userscripts/README.md](./tools/userscripts/README.md) |
| runner      | CLI・Local Runner | [tools/runner/README.md](./tools/runner/README.md)           |

### クイックスタート

```powershell
cd tools
bun install
bun --cwd userscripts run build
bun --cwd runner run typecheck
```

競プロライブラリを初期化し、IntelliJの全解答モジュールから参照できるようにします。

```powershell
git submodule update --init --recursive
powershell -ExecutionPolicy Bypass -File .\setup-library.ps1
```

解答では`import lib.ds.UnionFind;`のように記述します。`run` / `test` / `tomain` / `submit`時に推移的依存を含む単一の`Main.java`へ自動展開されます。詳細は[LIBRARY_USAGE.md](./LIBRARY_USAGE.md)を参照してください。

ローカルランナー起動 (Windows):

```powershell
powershell -File "tools/runner/bin/start-local-runner.ps1" 24
```

## コミットメッセージの例

```text
Solved: ABC000 [A ~ G]
Resolved: ABC000 [A ~ G]
Update: JDK17→24.0.2 ABC000 [A ~ G]
```

## 補足

- UserScript のインストール URL は `tools/userscripts/<Name>/dist/<Name>.user.js` を参照してください。
- `AtCoder_Scripts/` はレガシー配布用です。新規開発は `tools/userscripts` を編集し、`bun run build` で `dist` を更新します。
