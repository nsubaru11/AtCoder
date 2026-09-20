# Copilot Instructions

## プロジェクト概要

このプロジェクトはAtCoderのJava解答、解答テンプレート、ローカルランナーを管理します。再利用ライブラリは library/ にあります。

## コーディング規則

- **JavaDoc コメント**: 圧縮版のクラス以外の全てのクラス・メソッドでJavaDocコメントを記述すること。
- **定数の命名規則**: 定数はUPPER_SNAKE_CASEで記述すること。
- **インデント**: インデントにはタブキーを使用すること。
- **ビット演算子の使用**: 限りなく高速化なコードにするため、可能ならビット演算子を使用すること。
- **オーバーロードの作成**: 汎用ライブラリを作成するため、様々なオーバーロードを作成すること。
- **プリミティブ型版とジェネリクス型版の実装**: 高速に動作させるため、プリミティブ型版とジェネリクス型版の両方を実装すること。
- **多次元配列の圧縮**: 内部的に多次元配列を用いる場合、1次元に圧縮すること。

## ディレクトリ構成

- ABC/、ARC/、AGC/、AHC/、ADT/、AWC/、Other/: コンテスト別の解答。
- template/: Java 24版とJava 17版の解答テンプレート。
- library/src/lib/: importして利用する競技プログラミングライブラリ。
- library/test/: ライブラリの検証コード。
- library/docs/: ライブラリのドキュメント。
- tools/runner/: ローカルランナーとJavaのバンドル・実行処理。
- tools/userscripts/、tools/shared/: UserScriptと共有処理。
- out/、logs/: 生成物。ソースとして管理しない。

## JDKバージョン

- 基本の開発・実行環境は JDK 24.0.2（Java 24）。
- template/TemplateCode17.java と library/src/lib/io/compat17/ は Java 17互換を維持する。
- 過去問に個別指定されたJDKは、対象問題の実行環境を確認せず一括変更しない。
- ライブラリを編集する場合は library/AGENTS.md と配下の指示に従う。
- 提出時は tools/runner による lib.* のバンドルを使用する。詳細は LIBRARY_USAGE.md を参照する。
