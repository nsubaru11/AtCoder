# 競プロライブラリの利用方法

## 初回セットアップ

AtCoderリポジトリのルートで実行します。

```powershell
git submodule update --init --recursive
powershell -ExecutionPolicy Bypass -File .\setup-library.ps1
```

スクリプトは次を行います。

- `library/src`をIntelliJの`competitive-programming-library`モジュールとして登録
- 既存の各コンテストモジュールへModule Dependencyを追加
- Project File Templateの`AtCoder Library Solution`を配置

新しいコンテストモジュールを作った後も、`setup-library.ps1`を再実行すれば依存が追加されます。

## 解答を書く

通常のJavaと同じようにimportします。

```java
import lib.ds.UnionFind;
import lib.graph.Kruskal;
import lib.io.FastPrinter;
import lib.io.FastScanner;

public final class D {
	public static void main(final String[] args) {
		final FastScanner sc = new FastScanner();
		try (final FastPrinter out = new FastPrinter()) {
			final int n = sc.nextInt();
			final UnionFind uf = new UnionFind(n);
			out.println(uf.groupCount());
		}
	}
}
```

IntelliJでは`File → New → AtCoder Library Solution`から雛形を作成できます。`lib.*`に対して補完、定義ジャンプ、Find Usages、Renameが利用できます。

`src/patterns`は読む・写経する資料であり、importや自動バンドルの対象ではありません。

## ローカル実行と提出

従来と同じrunnerコマンドを使います。すべての経路が実行前に`lib.*`を単一ソースへ展開します。

```powershell
# 1回実行
run D.java

# AtCoderサンプルテスト
test d

# 提出用Main.javaを目視確認
tomain -f D.java Main.java

# サンプルテスト後に提出
submit d
```

バンドルが行われると、次のようなログが出ます。

```text
Bundled library classes: lib.graph.Kruskal, lib.ds.UnionFind
```

`run`、`localtest`、`test`、`crosscheck`、`tomain`、`submit`はすべて同じバンドル処理を通るため、ローカル実行と提出コードが一致します。

## libraryが見つからない場合

通常はAtCoderルートの`library/src`が自動検出されます。別の配置を使う場合だけ環境変数を指定します。

```powershell
$env:ATCODER_LIB_SRC = "C:\path\to\competitive-programming-java-library\src"
```

## バンドラの制約

- `import lib.some.Class;`または`import lib.some.*;`を使う
- `import static lib...`は使わない
- 本文中で`lib.ds.UnionFind`のような完全修飾参照を使わない
- 解答と依存クラスでトップレベル型の単純名を重複させない

エラー時には未解決importのまま提出せず、runnerが停止します。

## 手貼りへのフォールバック

バンドラに問題がある場合は、次の順で切り戻せます。

1. `tomain`が出力した、最後に成功した`Main.java`を使用
2. `library/src/lib/...`からログに表示されたクラスを手動展開
3. 従来の自己完結テンプレート`template/TemplateCode.java`またはJava 17版を使用
