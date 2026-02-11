import java.io.*;
import java.math.*;
import java.util.*;
import java.util.ArrayList;
import java.util.function.*;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public class E {

	private static void solve(final FastScanner sc, final ContestPrinter out) {
		int t = sc.nextInt();
		int max = 200000;
		Pair[] a = new Pair[max];
		setAll(a, i -> new Pair(i, 0));
		int[] b = new int[max];
		Queue<Integer> pq = new PriorityQueue<>(max, (o1, o2) -> Integer.compare(o2, o1));
		while (t-- > 0) {
			int n = sc.nextInt();
			int k = sc.nextInt();
			for (int i = 0; i < n; i++) {
				a[i].index = i;
				a[i].value = sc.nextInt();
			}
			sort(a, 0, n);
			for (int i = 0; i < n; i++)
				b[i] = sc.nextInt();
			long sum = 0;
			pq.clear();
			for (int i = 0; i < k; i++) {
				pq.add(b[a[i].index]);
				sum += b[a[i].index];
			}
			long ans = a[k - 1].value * sum;
			for (int i = k; i < n; i++) {
				sum -= pq.poll();
				pq.add(b[a[i].index]);
				sum += b[a[i].index];
				ans = min(ans, a[i].value * sum);
			}
			out.println(ans);
		}
	}

	public static void main(String[] args) {
		try (final FastScanner sc = new FastScanner();
		     final ContestPrinter out = new ContestPrinter()) {
			solve(sc, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final class Pair implements Comparable<Pair> {
		int index, value;

		public Pair(int i, int j) {
			this.index = i;
			this.value = j;
		}

		@Override
		public int compareTo(Pair o) {
			return Integer.compare(value, o.value);
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof Pair other))
				return false;
			return index == other.index && value == other.value;
		}

		public String toString() {
			return index + " " + value;
		}
	}

	/**
	 * 競技プログラミング向けの高速入力クラスです。
	 * 内部バッファを利用して InputStream からの入力を効率的に処理します。
	 * ※ ASCII 範囲外の文字は正しく処理できません。入力は半角スペースまたは改行で区切られていることを前提とします.
	 */
	@SuppressWarnings("unused")
	private static class FastScanner implements AutoCloseable {

		//  ------------------------ 定数 ------------------------

		/**
		 * 入力用内部バッファのデフォルトサイズ（バイト単位）。
		 */
		private static final int DEFAULT_BUFFER_SIZE = 65536;

		//  --------------------- インスタンス変数 ---------------------

		/**
		 * 入力元の InputStream です。デフォルトは {@code System.in} です。
		 */
		private final InputStream in;

		/**
		 * 入力データを一時的に格納する内部バッファです。<br>
		 * 読み込み時に {@link #read()} でデータを取得し、バッファから消費します。
		 */
		private final byte[] buffer;

		/**
		 * 現在のバッファ内で次に読み込む位置
		 */
		private int pos = 0;

		/**
		 * バッファに読み込まれているバイト数
		 */
		private int bufferLength = 0;

		//  ---------------------- コンストラクタ ----------------------

		/**
		 * デフォルトの設定でFastScannerを初期化します。<br>
		 * バッファ容量: 65536 <br>
		 * InputStream: System.in <br>
		 */
		public FastScanner() {
			this(System.in, DEFAULT_BUFFER_SIZE);
		}

		/**
		 * 指定されたInputStreamを用いてFastScannerを初期化します。<br>
		 * バッファ容量: 65536 <br>
		 *
		 * @param in 入力元の InputStream
		 */
		public FastScanner(InputStream in) {
			this(in, DEFAULT_BUFFER_SIZE);
		}

		/**
		 * 指定されたバッファ容量でFastScannerを初期化します。<br>
		 * InputStream: System.in <br>
		 *
		 * @param bufferSize 内部バッファの容量（文字単位）
		 */
		public FastScanner(int bufferSize) {
			this(System.in, bufferSize);
		}

		/**
		 * 指定されたバッファ容量とInputStreamでFastScannerを初期化します。<br>
		 *
		 * @param in         入力元の InputStream
		 * @param bufferSize 内部バッファの容量（文字単位）
		 */
		public FastScanner(InputStream in, int bufferSize) {
			this.in = in;
			this.buffer = new byte[bufferSize];
		}

		//  -------------------- オーバーライドメソッド --------------------

		/**
		 * このInputStreamを閉じます。
		 * 入力元が {@code System.in} の場合、閉じません。
		 *
		 * @throws IOException {@code close}の際にエラーが発生した場合
		 */
		@Override
		public void close() throws IOException {
			if (in != System.in)
				in.close();
		}

		/**
		 * 内部バッファから 1 バイトを読み込みます。<br>
		 * バッファが空の場合、新たにデータを読み込みます。
		 *
		 * @return 読み込んだバイト
		 * @throws RuntimeException 入力終了または I/O エラー時
		 */
		public byte read() {
			if (pos >= bufferLength) {
				try {
					bufferLength = in.read(buffer, pos = 0, buffer.length);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (bufferLength < 0) {
					throw new RuntimeException(new IOException("End of input reached"));
				}
			}
			return buffer[pos++];
		}

		//  -------------------- 基本入力メソッド --------------------

		/**
		 * 次の int 値を読み込みます。
		 *
		 * @return 読み込んだ int 値
		 */
		public int nextInt() {
			int b = read();
			while (isDelimiter(b)) b = read();
			boolean negative = b == '-';
			if (negative) b = read();
			int result = 0;
			while ('0' <= b && b <= '9') {
				result = result * 10 + b - '0';
				b = read();
			}
			return negative ? -result : result;
		}

		/**
		 * 次の long 値を読み込みます。
		 *
		 * @return 読み込んだ long 値
		 */
		public long nextLong() {
			int b = read();
			while (isDelimiter(b)) b = read();
			boolean negative = b == '-';
			if (negative) b = read();
			long result = 0;
			while ('0' <= b && b <= '9') {
				result = result * 10 + b - '0';
				b = read();
			}
			return negative ? -result : result;
		}

		/**
		 * 次の double 値を読み込みます。
		 *
		 * @return 読み込んだ double 値
		 */
		public double nextDouble() {
			int b = read();
			while (isDelimiter(b)) b = read();
			boolean negative = b == '-';
			if (negative) b = read();
			double result = 0;
			while ('0' <= b && b <= '9') {
				result = result * 10 + b - '0';
				b = read();
			}
			if (b == '.') {
				b = read();
				double factor = 10;
				while ('0' <= b && b <= '9') {
					result += (b - '0') / factor;
					factor *= 10;
					b = read();
				}
			}
			return negative ? -result : result;
		}

		/**
		 * 次の char 値（非空白文字）を読み込みます。
		 *
		 * @return 読み込んだ char 値
		 */
		public char nextChar() {
			byte b = read();
			while (isDelimiter(b)) b = read();
			return (char) b;
		}

		/**
		 * 次の String（空白で区切られた文字列）を読み込みます。
		 *
		 * @return 読み込んだ String
		 */
		public String next() {
			return nextStringBuilder().toString();
		}

		/**
		 * 次の StringBuilder（空白で区切られた文字列）を読み込みます。
		 *
		 * @return 読み込んだ StringBuilder
		 */
		public StringBuilder nextStringBuilder() {
			StringBuilder sb = new StringBuilder();
			byte b = read();
			while (isDelimiter(b)) b = read();
			while (!isDelimiter(b)) {
				sb.appendCodePoint(b);
				b = read();
			}
			return sb;
		}

		/**
		 * 次の1行を読み込みます。（改行文字は読み飛ばされます）
		 *
		 * @return 読み込んだ String
		 */
		public String nextLine() {
			StringBuilder sb = new StringBuilder();
			int b = read();
			while (b != 0 && b != '\r' && b != '\n') {
				sb.appendCodePoint(b);
				b = read();
			}
			return sb.toString();
		}

		/**
		 * 次のトークンを BigInteger として読み込みます。
		 *
		 * @return 読み込んだ BigInteger
		 */
		public BigInteger nextBigInteger() {
			return new BigInteger(next());
		}

		/**
		 * 次のトークンを BigDecimal として読み込みます。
		 *
		 * @return 読み込んだ BigDecimal
		 */
		public BigDecimal nextBigDecimal() {
			return new BigDecimal(next());
		}

		// -------------------- プライベートヘルパーメソッド --------------------

		/**
		 * 指定した文字コードが空白文字（' '、'\n'、'\r'）かどうか判定します。
		 *
		 * @param c 判定対象の文字コード
		 * @return 空白文字の場合 true、それ以外の場合 false
		 */
		private boolean isDelimiter(int c) {
			return ' ' == c || '\n' == c || '\r' == c;
		}
	}

	/**
	 * {@code ContestScanner} は、競技プログラミング向けの高速入力ユーティリティです。<br>
	 * {@link FastScanner} を拡張し、各種配列、2次元・3次元配列、ソート済み配列、累積和配列、逆写像配列、各種コレクションの入力をサポートします。
	 */
	@SuppressWarnings("unused")
	private static final class ContestScanner extends FastScanner {

		//  --------------------- 一次元配列入力メソッド ---------------------

		/**
		 * 指定された長さの int 配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ int 配列
		 */
		public int[] nextInt(final int n) {
			final int[] a = new int[n];
			setAll(a, i -> nextInt());
			return a;
		}

		/**
		 * 指定された長さの long 配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ long 配列
		 */
		public long[] nextLong(final int n) {
			final long[] a = new long[n];
			setAll(a, i -> nextLong());
			return a;
		}

		/**
		 * 指定された長さの double 配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ double 配列
		 */
		public double[] nextDouble(final int n) {
			final double[] a = new double[n];
			setAll(a, i -> nextDouble());
			return a;
		}

		/**
		 * 次の文字列を char 配列として読み込みます。
		 *
		 * @return 読み込んだ char 配列
		 */
		public char[] nextChars() {
			return next().toCharArray();
		}

		/**
		 * 指定された長さの char 配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ char 配列
		 */
		public char[] nextChars(final int n) {
			final char[] c = new char[n];
			for (int i = 0; i < n; i++)
				c[i] = nextChar();
			return c;
		}

		/**
		 * 指定された長さの String 配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ String 配列
		 */
		public String[] nextStrings(final int n) {
			final String[] s = new String[n];
			setAll(s, x -> next());
			return s;
		}

		//  --------------------- 通常の2次元配列入力メソッド ---------------------

		/**
		 * 指定された行数・列数の int の2次元配列を読み込みます。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 読み込んだ2次元 int 配列
		 */
		public int[][] nextIntMat(final int h, final int w) {
			final int[][] a = new int[h][w];
			for (int i = 0; i < h; i++)
				setAll(a[i], x -> nextInt());
			return a;
		}

		/**
		 * 指定された行数・列数の long の2次元配列を読み込みます。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 読み込んだ2次元 long 配列
		 */
		public long[][] nextLongMat(final int h, final int w) {
			final long[][] a = new long[h][w];
			for (int i = 0; i < h; i++)
				setAll(a[i], x -> nextLong());
			return a;
		}

		/**
		 * 指定された行数・列数の double の2次元配列を読み込みます。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 読み込んだ2次元double 配列
		 */
		public double[][] nextDoubleMat(final int h, final int w) {
			final double[][] a = new double[h][w];
			for (int i = 0; i < h; i++)
				setAll(a[i], x -> nextDouble());
			return a;
		}

		/**
		 * 複数の文字列を2次元の char 配列として読み込みます。
		 *
		 * @param n 行数（文字列の個数）
		 * @return 読み込んだ2次元 char 配列
		 */
		public char[][] nextCharMat(final int n) {
			final char[][] c = new char[n][];
			setAll(c, x -> nextChars());
			return c;
		}

		/**
		 * 指定された行数・列数の char 2次元文字配列を読み込みます。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 読み込んだ2次元 char 配列
		 */
		public char[][] nextCharMat(final int h, final int w) {
			final char[][] c = new char[h][w];
			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++)
					c[i][j] = nextChar();
			return c;
		}

		/**
		 * 指定された行数・列数の String の2次元配列を読み込みます。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 読み込んだ2次元 String 配列
		 */
		public String[][] nextStringMat(final int h, final int w) {
			final String[][] s = new String[h][w];
			for (int i = 0; i < h; i++)
				setAll(s[i], x -> next());
			return s;
		}

		//  --------------------- 3次元配列入力メソッド ---------------------

		/**
		 * 指定されたサイズの3次元 int 配列を読み込みます。
		 *
		 * @param x サイズX
		 * @param y サイズY
		 * @param z サイズZ
		 * @return 読み込んだ3次元 int 配列
		 */
		public int[][][] nextInt3D(final int x, final int y, final int z) {
			final int[][][] a = new int[x][y][z];
			for (int i = 0; i < x; i++)
				for (int j = 0; j < y; j++)
					for (int k = 0; k < z; k++)
						a[i][j][k] = nextInt();
			return a;
		}

		/**
		 * 指定されたサイズの3次元 long 配列を読み込みます。
		 *
		 * @param x サイズX
		 * @param y サイズY
		 * @param z サイズZ
		 * @return 読み込んだ3次元 long 配列
		 */
		public long[][][] nextLong3D(final int x, final int y, final int z) {
			final long[][][] a = new long[x][y][z];
			for (int i = 0; i < x; i++)
				for (int j = 0; j < y; j++)
					for (int k = 0; k < z; k++)
						a[i][j][k] = nextLong();
			return a;
		}

		//  --------------------- ソート済み配列入力メソッド ---------------------

		/**
		 * 指定された長さの int 配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた int 配列
		 */
		public int[] nextSortedInt(final int n) {
			final int[] a = nextInt(n);
			sort(a);
			return a;
		}

		/**
		 * 指定された長さの long 配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた long 配列
		 */
		public long[] nextSortedLong(final int n) {
			final long[] a = nextLong(n);
			sort(a);
			return a;
		}

		/**
		 * 指定された長さの double 配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた double 配列
		 */
		public double[] nextSortedDouble(final int n) {
			final double[] a = nextDouble(n);
			sort(a);
			return a;
		}

		/**
		 * 次の文字列を char 配列として読み込み、ソートして返します。
		 *
		 * @return ソートされた char 配列
		 */
		public char[] nextSortedChars() {
			final char[] c = nextChars();
			sort(c);
			return c;
		}

		/**
		 * 指定された長さの char 配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた char 配列
		 */
		public char[] nextSortedChars(final int n) {
			final char[] c = nextChars(n);
			sort(c);
			return c;
		}

		/**
		 * 指定された長さの String 配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた String 配列
		 */
		public String[] nextSortedStrings(final int n) {
			final String[] s = nextStrings(n);
			sort(s);
			return s;
		}

		//  --------------------- 累積和配列入力メソッド ---------------------

		/**
		 * 整数の累積和配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 累積和配列(int[])
		 */
		public int[] nextIntPrefixSum(final int n) {
			final int[] prefixSum = new int[n];
			setAll(prefixSum, i -> i > 0 ? nextInt() + prefixSum[i - 1] : nextInt());
			return prefixSum;
		}

		/**
		 * 長整数の累積和配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 累積和配列(long[])
		 */
		public long[] nextLongPrefixSum(final int n) {
			final long[] prefixSum = new long[n];
			setAll(prefixSum, i -> i > 0 ? nextLong() + prefixSum[i - 1] : nextLong());
			return prefixSum;
		}

		/**
		 * 整数の2次元累積和配列を読み込みます。<br>
		 * 戻り値の配列サイズは (h+1) x (w+1) となります。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 2次元累積和配列(int[][])
		 */
		public int[][] nextIntPrefixSum(final int h, final int w) {
			final int[][] prefixSum = new int[h + 1][w + 1];
			for (int i = 1; i <= h; i++) {
				int j = i;
				setAll(prefixSum[i], k -> k > 0 ? nextInt() + prefixSum[j - 1][k] + prefixSum[j][k - 1] - prefixSum[j - 1][k - 1] : 0);
			}
			return prefixSum;
		}

		/**
		 * 長整数の2次元累積和配列を読み込みます。<br>
		 * 戻り値の配列サイズは (rows+1) x (cols+1) となります。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 2次元累積和配列(long[][])
		 */
		public long[][] nextLongPrefixSum(final int h, final int w) {
			final long[][] prefixSum = new long[h + 1][w + 1];
			for (int i = 1; i <= h; i++) {
				int j = i;
				setAll(prefixSum[i], k -> k > 0 ? nextLong() + prefixSum[j - 1][k] + prefixSum[j][k - 1] - prefixSum[j - 1][k - 1] : 0);
			}
			return prefixSum;
		}

		/**
		 * 整数の3次元累積和配列を読み込みます。<br>
		 * 戻り値の配列サイズは (x+1) x (y+1) x (z+1) となります。
		 *
		 * @param x サイズ X
		 * @param y サイズ Y
		 * @param z サイズ Z
		 * @return 3次元累積和配列（int[][][]）
		 */
		public int[][][] nextIntPrefixSum(final int x, final int y, final int z) {
			final int[][][] e = new int[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++) {
					int A = a, B = b;
					setAll(e[A][B], c -> c > 0 ? nextInt() + e[A - 1][B][c] + e[A][B - 1][c] + e[A][B][c - 1]
							- e[A - 1][B - 1][c] - e[A - 1][B][c - 1] - e[A][B - 1][c - 1] + e[A - 1][B - 1][c - 1] : 0);
				}
			return e;
		}

		/**
		 * 長整数の3次元累積和配列を読み込みます。<br>
		 * 戻り値の配列サイズは (x+1) x (y+1) x (z+1) となります。
		 *
		 * @param x サイズ X
		 * @param y サイズ Y
		 * @param z サイズ Z
		 * @return 3次元累積和配列（long[][][]）
		 */
		public long[][][] nextLongPrefixSum(final int x, final int y, final int z) {
			final long[][][] e = new long[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++) {
					int A = a, B = b;
					setAll(e[A][B], c -> c > 0 ? nextLong() + e[A - 1][B][c] + e[A][B - 1][c] + e[A][B][c - 1]
							- e[A - 1][B - 1][c] - e[A - 1][B][c - 1] - e[A][B - 1][c - 1] + e[A - 1][B - 1][c - 1] : 0);
				}
			return e;
		}

		//  --------------------- 逆写像配列入力メソッド ---------------------

		/**
		 * 入力値が1-indexedの整数に対する逆写像を生成します。<br>
		 * 例：入力が「3 1 2」の場合、返される配列は {1, 2, 0} となります。
		 *
		 * @param n 配列の長さ
		 * @return 各入力値に対して、入力された順序（0-indexed）を格納した逆写像
		 */
		public int[] nextIntInverseMapping(final int n) {
			final int[] a = new int[n];
			for (int i = 0; i < n; i++)
				a[nextInt() - 1] = i;
			return a;
		}

		//  --------------------- Collection<Integer>入力メソッド ---------------------

		/**
		 * 整数を読み込み、指定したコレクションに格納して返します。
		 *
		 * @param <T>      コレクションの型
		 * @param n        要素数
		 * @param supplier コレクションのインスタンスを生成するサプライヤ
		 * @return 読み込んだ整数のコレクション
		 */
		private <T extends Collection<Integer>> T nextIntCollection(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				c.add(nextInt());
			}
			return c;
		}

		/**
		 * 指定された長さの整数 ArrayList を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ ArrayList&lt;Integer&gt;
		 */
		public ArrayList<Integer> nextIntAL(int n) {
			return nextIntCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの整数 HashSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ HashSet&lt;Integer&gt;
		 */
		public HashSet<Integer> nextIntHS(int n) {
			return nextIntCollection(n, () -> new HashSet<>(n));
		}

		/**
		 * 指定された長さの整数 TreeSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ TreeSet&lt;Integer&gt;
		 */
		public TreeSet<Integer> nextIntTS(int n) {
			return nextIntCollection(n, TreeSet::new);
		}

		//  --------------------- Collection<Long>入力メソッド ---------------------

		/**
		 * 長整数を読み込み、指定したコレクションに格納して返します。
		 *
		 * @param <T>      コレクションの型
		 * @param n        要素数
		 * @param supplier コレクションのインスタンスを生成するサプライヤ
		 * @return 読み込んだ長整数のコレクション
		 */
		private <T extends Collection<Long>> T nextLongCollection(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				c.add(nextLong());
			}
			return c;
		}

		/**
		 * 指定された長さの長整数 ArrayList を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ ArrayList&lt;Long&gt;
		 */
		public ArrayList<Long> nextLongAL(int n) {
			return nextLongCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの長整数 HashSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ HashSet&lt;Long&gt;
		 */
		public HashSet<Long> nextLongHS(int n) {
			return nextLongCollection(n, () -> new HashSet<>(n));
		}

		/**
		 * 指定された長さの長整数 TreeSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ TreeSet&lt;Long&gt;
		 */
		public TreeSet<Long> nextLongTS(int n) {
			return nextLongCollection(n, TreeSet::new);
		}

		//  --------------------- Collection<Character>入力メソッド ---------------------

		/**
		 * 文字を読み込み、指定したコレクションに格納して返します。
		 *
		 * @param <T>      コレクションの型
		 * @param n        要素数
		 * @param supplier コレクションのインスタンスを生成するサプライヤ
		 * @return 読み込んだ文字のコレクション
		 */
		private <T extends Collection<Character>> T nextCharacterCollection(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				c.add(nextChar());
			}
			return c;
		}

		/**
		 * 指定された長さの文字 ArrayList を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ ArrayList&lt;Character&gt;
		 */
		public ArrayList<Character> nextCharacterAL(int n) {
			return nextCharacterCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの文字 HashSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ HashSet&lt;Character&gt;
		 */
		public HashSet<Character> nextCharacterHS(int n) {
			return nextCharacterCollection(n, () -> new HashSet<>(n));
		}

		/**
		 * 指定された長さの文字 TreeSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ TreeSet&lt;Character&gt;
		 */
		public TreeSet<Character> nextCharacterTS(int n) {
			return nextCharacterCollection(n, TreeSet::new);
		}

		//  --------------------- Collection<String>入力メソッド ---------------------

		/**
		 * 文字列を読み込み、指定したコレクションに格納して返します。
		 *
		 * @param <T>      コレクションの型
		 * @param n        要素数
		 * @param supplier コレクションのインスタンスを生成するサプライヤ
		 * @return 読み込んだ文字列のコレクション
		 */
		private <T extends Collection<String>> T nextStringCollection(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				c.add(next());
			}
			return c;
		}

		/**
		 * 指定された長さの文字列 ArrayList を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ ArrayList&lt;String&gt;
		 */
		public ArrayList<String> nextStringAL(int n) {
			return nextStringCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの文字列 HashSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ HashSet&lt;String&gt;
		 */
		public HashSet<String> nextStringHS(int n) {
			return nextStringCollection(n, () -> new HashSet<>(n));
		}

		/**
		 * 指定された長さの文字列 TreeSet を読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだ TreeSet&lt;String&gt;
		 */
		public TreeSet<String> nextStringTS(int n) {
			return nextStringCollection(n, TreeSet::new);
		}

		// -------------------- Multiset (Map) 入力メソッド --------------------


		/**
		 * 整数の出現回数をカウントしたマルチセットを読み込みます。<br>
		 * キーが整数、値が出現回数となるマップを返します。
		 *
		 * @param <T>      マップの型
		 * @param n        要素数
		 * @param supplier マップのインスタンスを生成するサプライヤ
		 * @return 整数のマルチセット（マップ）
		 */
		private <T extends Map<Integer, Integer>> T nextIntMultiset(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				int a = nextInt();
				c.put(a, c.getOrDefault(a, 0) + 1);
			}
			return c;
		}

		/**
		 * 整数のマルチセットを HashMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 整数のマルチセット（HashMap）
		 */
		public HashMap<Integer, Integer> nextIntMultisetHM(int n) {
			return nextIntMultiset(n, () -> new HashMap<>(n));
		}

		/**
		 * 整数のマルチセットを TreeMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 整数のマルチセット（TreeMap）
		 */
		public TreeMap<Integer, Integer> nextIntMultisetTM(int n) {
			return nextIntMultiset(n, TreeMap::new);
		}

		/**
		 * 長整数の出現回数をカウントしたマルチセットを読み込みます。<br>
		 * キーが長整数、値が出現回数となるマップを返します。
		 *
		 * @param <T>      マップの型
		 * @param n        要素数
		 * @param supplier マップのインスタンスを生成するサプライヤ
		 * @return 長整数のマルチセット（マップ）
		 */
		private <T extends Map<Long, Integer>> T nextLongMultiset(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				long a = nextLong();
				c.put(a, c.getOrDefault(a, 0) + 1);
			}
			return c;
		}

		/**
		 * 長整数のマルチセットを HashMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 長整数のマルチセット（HashMap）
		 */
		public HashMap<Long, Integer> nextLongMultisetHM(int n) {
			return nextLongMultiset(n, () -> new HashMap<>(n));
		}

		/**
		 * 長整数のマルチセットを TreeMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 長整数のマルチセット（TreeMap）
		 */
		public TreeMap<Long, Integer> nextLongMultisetTM(int n) {
			return nextLongMultiset(n, TreeMap::new);
		}

		/**
		 * 文字の出現回数をカウントしたマルチセットを読み込みます。<br>
		 * キーが文字、値が出現回数となるマップを返します。
		 *
		 * @param <T>      マップの型
		 * @param n        要素数
		 * @param supplier マップのインスタンスを生成するサプライヤ
		 * @return 文字のマルチセット（マップ）
		 */
		private <T extends Map<Character, Integer>> T nextCharMultiset(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				char a = nextChar();
				c.put(a, c.getOrDefault(a, 0) + 1);
			}
			return c;
		}

		/**
		 * 文字のマルチセットを HashMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 文字のマルチセット（HashMap）
		 */
		public HashMap<Character, Integer> nextCharMultisetHM(int n) {
			return nextCharMultiset(n, () -> new HashMap<>(n));
		}

		/**
		 * 文字のマルチセットを TreeMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 文字のマルチセット（TreeMap）
		 */
		public TreeMap<Character, Integer> nextCharMultisetTM(int n) {
			return nextCharMultiset(n, TreeMap::new);
		}

		/**
		 * 文字列の出現回数をカウントしたマルチセットを読み込みます。<br>
		 * キーが文字列、値が出現回数となるマップを返します。
		 *
		 * @param <T>      マップの型
		 * @param n        要素数
		 * @param supplier マップのインスタンスを生成するサプライヤ
		 * @return 文字列のマルチセット（マップ）
		 */
		private <T extends Map<String, Integer>> T nextStringMultiset(int n, Supplier<T> supplier) {
			T c = supplier.get();
			while (n-- > 0) {
				String a = next();
				c.put(a, c.getOrDefault(a, 0) + 1);
			}
			return c;
		}

		/**
		 * 文字列のマルチセットを HashMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 文字列のマルチセット（HashMap）
		 */
		public HashMap<String, Integer> nextStringMultisetHM(int n) {
			return nextStringMultiset(n, () -> new HashMap<>(n));
		}

		/**
		 * 文字列のマルチセットを TreeMap で読み込みます。
		 *
		 * @param n 要素数
		 * @return 文字列のマルチセット（TreeMap）
		 */
		public TreeMap<String, Integer> nextStringMultisetTM(int n) {
			return nextStringMultiset(n, TreeMap::new);
		}
	}

	/**
	 * 競技プログラミング向けの高速出力クラスです。<br>
	 * ※注意: 内部バッファが満杯になると自動的に指定の OutputStream に書き出します。<br>
	 * 処理途中で結果をすぐに反映させる必要がある場合は、autoFlush を true にするか、明示的に {@link #flush()} を呼び出してください。
	 * ASCII範囲外の文字は取り扱えません。
	 */
	@SuppressWarnings("unused")
	private static class FastPrinter implements AutoCloseable {

		//  ------------------------ 定数 ------------------------

		/**
		 * int 型の値を文字列に変換した際に必要となる最大桁数（符号込み）<br>
		 * 例: Integer.MIN_VALUE は "-2147483648"（11バイト）
		 */
		protected static final int MAX_INT_DIGITS = 11;

		/**
		 * long 型の値を文字列に変換した際に必要となる最大桁数（符号込み）<br>
		 * 例: Long.MIN_VALUE は "-9223372036854775808"（20バイト）
		 */
		protected static final int MAX_LONG_DIGITS = 20;

		/**
		 * 出力用内部バッファのデフォルトサイズ（バイト単位）<br>
		 * ※64バイト未満の場合、内部的に64バイトに調整されます。
		 */
		private static final int DEFAULT_BUFFER_SIZE = 65536;

		/**
		 * 00～99 の2桁の数字を連続した1次元配列として格納
		 */
		private static final byte[] TWO_DIGIT_NUMBERS = new byte[200];

		//  -------------------- 静的イニシャライザ --------------------
		/*
		 * TWO_DIGIT_NUMBERS の初期化を行います。
		 */
		static {
			byte tens = '0', ones = '0';
			for (int i = 0; i < 100; i++) {
				TWO_DIGIT_NUMBERS[i << 1] = tens;
				TWO_DIGIT_NUMBERS[(i << 1) + 1] = ones;
				if (++ones > '9') {
					ones = '0';
					tens++;
				}
			}
		}

		//  --------------------- インスタンス変数 ---------------------
		/**
		 * 出力先の内部バッファです。書き込みはこの配列に対して行い、必要に応じて {@link #flush()} で出力します。
		 */
		protected final byte[] buffer;

		/**
		 * 出力後に自動的にバッファを flush するかどうかを示すフラグです。<br>
		 * true の場合、各出力操作後に自動的に {@link #flush()} が呼ばれます。
		 */
		protected final boolean autoFlush;

		/**
		 * 出力先の OutputStream です。デフォルトは {@code System.out} です。
		 */
		private final OutputStream out;

		/**
		 * 現在のバッファ内での書き込み位置
		 */
		protected int pos = 0;

		//  ---------------------- コンストラクタ ----------------------

		/**
		 * デフォルトの設定でFastPrinterを初期化します。<br>
		 * バッファ容量: 65536 <br>
		 * OutputStream: System.out <br>
		 * autoFlush: false
		 */
		public FastPrinter() {
			this(System.out, DEFAULT_BUFFER_SIZE, false);
		}

		/**
		 * 指定されたOutputStreamを用いてFastPrinterを初期化します。<br>
		 * バッファ容量: 65536 <br>
		 * autoFlush: false
		 *
		 * @param out 出力先の OutputStream
		 */
		public FastPrinter(final OutputStream out) {
			this(out, DEFAULT_BUFFER_SIZE, false);
		}

		/**
		 * 指定されたバッファ容量でFastPrinterを初期化します。<br>
		 * OutputStream: System.out <br>
		 * autoFlush: false
		 *
		 * @param bufferSize 内部バッファの容量（バイト単位）。
		 */
		public FastPrinter(final int bufferSize) {
			this(System.out, bufferSize, false);
		}

		/**
		 * autoFlush を指定して FastPrinter を初期化します。<br>
		 * バッファ容量: 65536 <br>
		 * OutputStream: System.out
		 *
		 * @param autoFlush true の場合、各出力操作後に自動的に {@link #flush()} が呼ばれます。
		 */
		public FastPrinter(final boolean autoFlush) {
			this(System.out, DEFAULT_BUFFER_SIZE, autoFlush);
		}

		/**
		 * 指定された OutputStream と autoFlush 設定で FastPrinter を初期化します。<br>
		 * バッファ容量: 65536
		 *
		 * @param out       出力先の OutputStream
		 * @param autoFlush true を指定すると、各出力操作後に自動的に {@link #flush()} が呼ばれ、出力結果が即座に反映されます。
		 */
		public FastPrinter(final OutputStream out, final boolean autoFlush) {
			this(out, DEFAULT_BUFFER_SIZE, autoFlush);
		}

		/**
		 * 指定されたバッファ容量と autoFlush 設定で FastPrinter を初期化します。<br>
		 * OutputStream: System.out
		 *
		 * @param bufferSize 内部バッファの初期容量（バイト単位）。64 バイト未満の場合、内部的に 64 バイトに調整されます。
		 * @param autoFlush  true を指定すると、各出力操作後に自動的に {@link #flush()} が呼ばれ、出力結果が即座に反映されます。
		 */
		public FastPrinter(final int bufferSize, final boolean autoFlush) {
			this(System.out, bufferSize, autoFlush);
		}

		/**
		 * 指定されたバッファ容量と OutputStream で FastPrinter を初期化します。<br>
		 * autoFlush: false
		 *
		 * @param out        出力先の OutputStream
		 * @param bufferSize 内部バッファの初期容量（バイト単位）。64 バイト未満の場合、内部的に 64 バイトに調整されます。
		 */
		public FastPrinter(final OutputStream out, final int bufferSize) {
			this(out, bufferSize, false);
		}

		/**
		 * 指定されたバッファ容量、OutputStream、autoFlush 設定で FastPrinter を初期化します。
		 *
		 * @param out        出力先の OutputStream
		 * @param bufferSize 内部バッファの初期容量（バイト単位）。64 バイト未満の場合、内部的に 64 バイトに調整されます。
		 * @param autoFlush  true を指定すると、各出力操作後に自動的に {@link #flush()} が呼ばれ、出力結果が即座に反映されます。
		 */
		public FastPrinter(final OutputStream out, final int bufferSize, final boolean autoFlush) {
			this.out = out;
			buffer = new byte[max(bufferSize, 64)];
			this.autoFlush = autoFlush;
		}

		//  -------------------- オーバーライドメソッド --------------------

		/**
		 * {@code flush()}を実行し、このOutputStreamを閉じます。<br>
		 * 出力先が {@code System.out} の場合、閉じません。
		 *
		 * @throws IOException 出力時のエラーが発生した場合
		 */
		@Override
		public void close() throws IOException {
			flush();
			if (out != System.out)
				out.close();
		}

		/**
		 * 現在のバッファに保持しているすべてのデータを出力し、バッファをクリアします。
		 */
		public void flush() {
			try {
				if (pos > 0)
					out.write(buffer, 0, pos);
				out.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			pos = 0;
		}

		//  -------------------- println() 系メソッド --------------------

		/**
		 * 改行のみ出力します。
		 */
		public final void println() {
			ensureBufferSpace(1);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		/**
		 * int 値を出力します。（改行付き）
		 *
		 * @param i 出力する int 値
		 */
		public final void println(final int i) {
			ensureBufferSpace(MAX_INT_DIGITS + 1);
			fillBuffer(i);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		/**
		 * long 値を出力します。（改行付き）
		 *
		 * @param l 出力する long 値
		 */
		public final void println(final long l) {
			ensureBufferSpace(MAX_LONG_DIGITS + 1);
			fillBuffer(l);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		/**
		 * double 値を {@code Double.toString(d)} で文字列化し出力します。（改行付き）
		 *
		 * @param d 出力する double 値
		 */
		public final void println(final double d) {
			print(Double.toString(d), true);
		}

		/**
		 * char 値を出力します。（改行付き）
		 *
		 * @param c 出力する char 値
		 */
		public final void println(final char c) {
			ensureBufferSpace(2);
			buffer[pos++] = (byte) c;
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		/**
		 * boolean 値を出力します。（true は "Yes"、 false は "No", 改行付き）
		 *
		 * @param b 出力する boolean 値
		 */
		public final void println(final boolean b) {
			ensureBufferSpace(4);
			fillBuffer(b);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		/**
		 * String を出力します。（改行付き）
		 *
		 * @param s 出力する String 値
		 */
		public final void println(final String s) {
			print(s, true);
		}

		/**
		 * Object を {@code toString()} で文字列化し出力します。（改行付き）
		 *
		 * @param o 出力するオブジェクト
		 */
		public final void println(final Object o) {
			if (o == null) return;
			if (o instanceof String s) {
				print(s, true);
			} else if (o instanceof Long l) {
				println(l.longValue());
			} else if (o instanceof Integer i) {
				println(i.intValue());
			} else if (o instanceof Double d) {
				print(d.toString(), true);
			} else if (o instanceof Boolean b) {
				println(b.booleanValue());
			} else if (o instanceof Character c) {
				println(c.charValue());
			} else {
				print(o.toString(), true);
			}
		}

		/**
		 * BigInteger を {@code toString()} で文字列化し出力します。（改行付き）
		 *
		 * @param bi 出力するオブジェクト
		 */
		public final void println(final BigInteger bi) {
			print(bi.toString(), true);
		}

		/**
		 * BigDecimal を {@code toString()} で文字列化し出力します。（改行付き）
		 *
		 * @param bd 出力するオブジェクト
		 */
		public final void println(final BigDecimal bd) {
			print(bd.toString(), true);
		}

		//  --------------------- print() 系メソッド ---------------------

		/**
		 * int 値を出力します。（改行無し）
		 *
		 * @param i 出力する int 値
		 */
		public final void print(final int i) {
			ensureBufferSpace(MAX_INT_DIGITS);
			fillBuffer(i);
			if (autoFlush) flush();
		}

		/**
		 * long 値を出力します。（改行無し）
		 *
		 * @param l 出力する long 値
		 */
		public final void print(final long l) {
			ensureBufferSpace(MAX_LONG_DIGITS);
			fillBuffer(l);
			if (autoFlush) flush();
		}

		/**
		 * double 値を {@code Double.toString(d)} で文字列化し出力します。（改行無し）
		 *
		 * @param d 出力する double 値
		 */
		public final void print(final double d) {
			print(Double.toString(d), false);
		}

		/**
		 * char 値を出力します。（改行無し）
		 *
		 * @param c 出力する char 値
		 */
		public final void print(final char c) {
			ensureBufferSpace(1);
			buffer[pos++] = (byte) c;
			if (autoFlush) flush();
		}

		/**
		 * boolean 値を出力します。（true は "Yes"、 false は "No", 改行無し）
		 *
		 * @param b 出力する boolean 値
		 */
		public final void print(final boolean b) {
			ensureBufferSpace(3);
			fillBuffer(b);
			if (autoFlush) flush();
		}

		/**
		 * String を出力します。（改行無し）
		 *
		 * @param s 出力する String 値
		 */
		public final void print(final String s) {
			print(s, false);
		}

		/**
		 * Object を {@code toString()} で文字列化し出力します。（改行無し）
		 *
		 * @param o 出力するオブジェクト
		 */
		public final void print(final Object o) {
			if (o == null) return;
			if (o instanceof String s) {
				print(s, false);
			} else if (o instanceof Long l) {
				print(l.longValue());
			} else if (o instanceof Integer i) {
				print(i.intValue());
			} else if (o instanceof Double d) {
				print(d.toString(), false);
			} else if (o instanceof Boolean b) {
				print(b.booleanValue());
			} else if (o instanceof Character c) {
				print(c.charValue());
			} else {
				print(o.toString(), false);
			}
		}

		/**
		 * BigInteger を {@code toString()} で文字列化し出力します。（改行無し）
		 *
		 * @param bi 出力するオブジェクト
		 */
		public final void print(final BigInteger bi) {
			print(bi.toString(), false);
		}

		/**
		 * BigDecimal を {@code toString()} で文字列化し出力します。（改行無し）
		 *
		 * @param bd 出力するオブジェクト
		 */
		public final void print(final BigDecimal bd) {
			print(bd.toString(), false);
		}

		//  --------------------- printf() 系メソッド ---------------------

		/**
		 * 指定されたフォーマットに従い文字列を生成して出力します。（改行無し）
		 *
		 * @param format 書式文字列
		 * @param args   書式引数
		 */
		public final void printf(final String format, final Object... args) {
			print(String.format(format, args), false);
		}

		/**
		 * 指定された言語環境で整形し、フォーマットに従い文字列を生成して出力します。（改行無し）
		 *
		 * @param locale 言語環境
		 * @param format 書式文字列
		 * @param args   書式引数
		 */
		public final void printf(final Locale locale, final String format, final Object... args) {
			print(String.format(locale, format, args), false);
		}

		//  --------------------- 内部メソッド ---------------------

		/**
		 * 指定されたバイト数のデータを出力するために必要な領域を保証します。<br>
		 * バッファの残り容量が不足している場合、flush() を呼び出してバッファをクリアします。
		 *
		 * @param size 出力予定のデータのバイト数
		 */
		protected final void ensureBufferSpace(final int size) {
			if (pos + size > buffer.length) {
				try {
					out.write(buffer, 0, pos);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				pos = 0;
			}
		}

		/**
		 * 指定された文字列を出力し、オプションで改行を追加します。<br>
		 * 内部的にバッファへ書き込みを行い、autoFlush が有効な場合は自動で flush されます。
		 *
		 * @param s       出力する文字列
		 * @param newline true の場合、出力後に改行を追加
		 */
		protected final void print(final String s, final boolean newline) {
			fillBuffer(s);
			if (newline) {
				ensureBufferSpace(1);
				buffer[pos++] = '\n';
			}
			if (autoFlush) flush();
		}

		/**
		 * 指定された文字列をバッファに格納します。
		 *
		 * @param s 出力する文字列
		 */
		protected final void fillBuffer(final String s) {
			if (s == null) return;
			final int len = s.length();
			for (int i = 0; i < len; ) {
				ensureBufferSpace(1);
				int limit = min(buffer.length - pos, len - i);
				while (limit-- > 0) {
					buffer[pos++] = (byte) s.charAt(i++);
				}
			}
		}

		/**
		 * 指定された boolean 値を文字列 ("Yes" または "No") に変換してバッファに格納します。
		 *
		 * @param b 出力する boolean 値
		 */
		protected final void fillBuffer(final boolean b) {
			if (b) {
				buffer[pos++] = 'Y';
				buffer[pos++] = 'e';
				buffer[pos++] = 's';
			} else {
				buffer[pos++] = 'N';
				buffer[pos++] = 'o';
			}
		}

		/**
		 * 指定された int 値を文字列に変換してバッファに格納します。<br>
		 * 負の値の場合は先頭に '-' を付加します。
		 *
		 * @param i 出力する int 値
		 */
		protected final void fillBuffer(int i) {
			if (i == Integer.MIN_VALUE) {
				buffer[pos++] = '-';
				buffer[pos++] = '2';
				buffer[pos++] = '1';
				buffer[pos++] = '4';
				buffer[pos++] = '7';
				buffer[pos++] = '4';
				buffer[pos++] = '8';
				buffer[pos++] = '3';
				buffer[pos++] = '6';
				buffer[pos++] = '4';
				buffer[pos++] = '8';
				return;
			}

			boolean negative = (i < 0);
			if (negative) {
				i = -i;
				buffer[pos++] = '-';
			}

			int numOfDigits = countDigits(i);
			int writePos = pos + numOfDigits;
			while (i >= 100) {
				int quotient = i / 100;
				int remainder = (i - quotient * 100) << 1;
				buffer[--writePos] = TWO_DIGIT_NUMBERS[remainder + 1];
				buffer[--writePos] = TWO_DIGIT_NUMBERS[remainder];
				i = quotient;
			}

			if (i < 10) {
				buffer[--writePos] = (byte) ('0' + i);
			} else {
				buffer[--writePos] = TWO_DIGIT_NUMBERS[(i << 1) + 1];
				buffer[--writePos] = TWO_DIGIT_NUMBERS[i << 1];
			}

			pos += numOfDigits;
		}

		/**
		 * 指定された long 値を文字列に変換してバッファに格納します。<br>
		 * 負の値の場合は先頭に '-' を付加します。
		 *
		 * @param l 出力する long 値
		 */
		protected final void fillBuffer(long l) {
			if ((int) l == l) {
				fillBuffer((int) l);
				return;
			}
			if (l == Long.MIN_VALUE) {
				buffer[pos++] = '-';
				buffer[pos++] = '9';
				buffer[pos++] = '2';
				buffer[pos++] = '2';
				buffer[pos++] = '3';
				buffer[pos++] = '3';
				buffer[pos++] = '7';
				buffer[pos++] = '2';
				buffer[pos++] = '0';
				buffer[pos++] = '3';
				buffer[pos++] = '6';
				buffer[pos++] = '8';
				buffer[pos++] = '5';
				buffer[pos++] = '4';
				buffer[pos++] = '7';
				buffer[pos++] = '7';
				buffer[pos++] = '5';
				buffer[pos++] = '8';
				buffer[pos++] = '0';
				buffer[pos++] = '8';
				return;
			}

			boolean negative = (l < 0);
			if (negative) {
				l = -l;
				buffer[pos++] = '-';
			}

			int numOfDigits = countDigits(l);
			int writePos = pos + numOfDigits;
			while (l >= 100) {
				long quotient = l / 100;
				int remainder = (int) (l - quotient * 100) << 1;
				buffer[--writePos] = TWO_DIGIT_NUMBERS[remainder + 1];
				buffer[--writePos] = TWO_DIGIT_NUMBERS[remainder];
				l = quotient;
			}

			if (l < 10) {
				buffer[--writePos] = (byte) ('0' + l);
			} else {
				buffer[--writePos] = TWO_DIGIT_NUMBERS[(int) (l << 1) + 1];
				buffer[--writePos] = TWO_DIGIT_NUMBERS[(int) l << 1];
			}

			pos += numOfDigits;
		}

		/**
		 * 指定された int 値の桁数を数えます。
		 * 与えられる数値は正の整数であることを前提とした実装です。
		 *
		 * @param i 数値
		 * @return 桁数
		 */
		private int countDigits(final int i) {
			if (i < 10) return 1;
			if (i < 100) return 2;
			if (i < 1000) return 3;
			if (i < 10000) return 4;
			if (i < 100000) return 5;
			if (i < 1000000) return 6;
			if (i < 10000000) return 7;
			if (i < 100000000) return 8;
			if (i < 1000000000) return 9;
			return 10;
		}

		/**
		 * 指定された long 値の桁数を数えます。
		 * 与えられる数値は正の整数であり、int の範囲外が保証されることを前提とした実装です。
		 *
		 * @param l 数値
		 * @return 桁数
		 */
		private int countDigits(final long l) {
			if (l < 10000000000L) return 10;
			if (l < 100000000000L) return 11;
			if (l < 1000000000000L) return 12;
			if (l < 10000000000000L) return 13;
			if (l < 100000000000000L) return 14;
			if (l < 1000000000000000L) return 15;
			if (l < 10000000000000000L) return 16;
			if (l < 100000000000000000L) return 17;
			if (l < 1000000000000000000L) return 18;
			return 19;
		}

	}

	/**
	 * {@code ContestPrinter} は、競技プログラミング向けの高速出力ユーティリティです。<br>
	 * {@link FastPrinter} を拡張し、配列の改行・空白区切り出力や、関数を利用した変換出力をサポートします。<br>
	 * null チェックを導入し、意図しない {@code NullPointerException} を防ぎます。<br>
	 */
	@SuppressWarnings("unused")
	private static final class ContestPrinter extends FastPrinter {

		// ------------------------ コンストラクタ ------------------------

		/**
		 * デフォルトの設定で ContestPrinter を初期化します。<br>
		 * バッファ容量: 65536 バイト <br>
		 * OutputStream: System.out <br>
		 * autoFlush: false
		 */
		public ContestPrinter() {
			super();
		}

		/**
		 * 指定された OutputStream を用いて ContestPrinter を初期化します。<br>
		 * バッファ容量: 65536 バイト <br>
		 * autoFlush: false
		 *
		 * @param out 出力先の OutputStream
		 */
		public ContestPrinter(final OutputStream out) {
			super(out);
		}

		/**
		 * 指定されたバッファ容量で ContestPrinter を初期化します。<br>
		 * OutputStream: System.out <br>
		 * autoFlush: false
		 *
		 * @param bufferSize 内部バッファの容量（バイト単位）。64 バイト未満の場合、内部的に 64 バイトに調整されます。
		 */
		public ContestPrinter(final int bufferSize) {
			super(bufferSize);
		}

		/**
		 * autoFlush を指定して ContestPrinter を初期化します。<br>
		 * バッファ容量: 65536 バイト <br>
		 * OutputStream: System.out
		 *
		 * @param autoFlush true の場合、各出力操作後に自動的に {@link #flush()} が呼ばれます。
		 */
		public ContestPrinter(final boolean autoFlush) {
			super(autoFlush);
		}

		/**
		 * 指定された OutputStream とバッファ容量で ContestPrinter を初期化します。<br>
		 * autoFlush: false
		 *
		 * @param out        出力先の OutputStream
		 * @param bufferSize 内部バッファの容量（バイト単位）。64 バイト未満の場合、内部的に 64 バイトに調整されます。
		 */
		public ContestPrinter(final OutputStream out, final int bufferSize) {
			super(out, bufferSize);
		}

		/**
		 * 指定された OutputStream と autoFlush 設定で ContestPrinter を初期化します。<br>
		 * バッファ容量: 65536 バイト
		 *
		 * @param out       出力先の OutputStream
		 * @param autoFlush true を指定すると、各出力操作後に自動的に {@link #flush()} が呼ばれ、出力結果が即座に反映されます。
		 */
		public ContestPrinter(final OutputStream out, final boolean autoFlush) {
			super(out, autoFlush);
		}

		/**
		 * 指定されたバッファ容量と autoFlush 設定で ContestPrinter を初期化します。<br>
		 * OutputStream: System.out
		 *
		 * @param bufferSize 内部バッファの初期容量（バイト単位）。64 バイト未満の場合、内部的に 64 バイトに調整されます。
		 * @param autoFlush  true を指定すると、各出力操作後に自動的に {@link #flush()} が呼ばれ、出力結果が即座に反映されます。
		 */
		public ContestPrinter(final int bufferSize, final boolean autoFlush) {
			super(bufferSize, autoFlush);
		}

		/**
		 * 指定されたバッファ容量、OutputStream、autoFlush 設定で ContestPrinter を初期化します。
		 *
		 * @param out        出力先の OutputStream
		 * @param bufferSize 内部バッファの初期容量（バイト単位）。64 バイト未満の場合、内部的に 64 バイトに調整されます。
		 * @param autoFlush  true を指定すると、各出力操作後に自動的に {@link #flush()} が呼ばれ、出力結果が即座に反映されます。
		 */
		public ContestPrinter(final OutputStream out, final int bufferSize, final boolean autoFlush) {
			super(out, bufferSize, autoFlush);
		}

		// ------------------------ ペア出力メソッド（改行付き） ------------------------

		/**
		 * 2 つの整数値（int, int）をそれぞれ改行して出力します。
		 *
		 * @param a 出力する int 値
		 * @param b 出力する int 値
		 */
		public void println(final int a, final int b) {
			println(a, b, '\n');
		}

		/**
		 * 2 つの整数値（int, long）をそれぞれ改行して出力します。
		 *
		 * @param a 出力する int 値
		 * @param b 出力する long 値
		 */
		public void println(final int a, final long b) {
			println(a, b, '\n');
		}

		/**
		 * 2 つの整数値（long, int）をそれぞれ改行して出力します。
		 *
		 * @param a 出力する long 値
		 * @param b 出力する int 値
		 */
		public void println(final long a, final int b) {
			println(a, b, '\n');
		}

		/**
		 * 2 つの整数値（int または long）をそれぞれ改行して出力します。
		 *
		 * @param a 出力する long 値
		 * @param b 出力する long 値
		 */
		public void println(final long a, final long b) {
			println(a, b, '\n');
		}

		/**
		 * 2 つの整数値（int または long）を指定した区切り文字で出力します。（改行付き）
		 *
		 * @param a         出力する整数値（int または long）
		 * @param b         出力する整数値（int または long）
		 * @param delimiter 区切り文字
		 */
		public void println(final long a, final long b, final char delimiter) {
			ensureBufferSpace((MAX_LONG_DIGITS << 1) + 2);
			fillBuffer(a);
			buffer[pos++] = (byte) delimiter;
			fillBuffer(b);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		// ------------------------ ペア出力メソッド（改行無し） ------------------------

		/**
		 * 2 つの整数値（int, int）を半角スペース区切りで出力します。
		 *
		 * @param a 出力する int 値
		 * @param b 出力する int 値
		 */
		public void print(final int a, final int b) {
			print(a, b, ' ');
		}

		/**
		 * 2 つの整数値（int, long）を半角スペース区切りで出力します。
		 *
		 * @param a 出力する int 値
		 * @param b 出力する long 値
		 */
		public void print(final int a, final long b) {
			print(a, b, ' ');
		}

		/**
		 * 2 つの整数値（long, int）を半角スペース区切りで出力します。
		 *
		 * @param a 出力する long 値
		 * @param b 出力する int 値
		 */
		public void print(final long a, final int b) {
			print(a, b, ' ');
		}

		/**
		 * 2 つの整数値（long, long）を半角スペース区切りで出力します。
		 *
		 * @param a 出力する long 値
		 * @param b 出力する long 値
		 */
		public void print(final long a, final long b) {
			print(a, b, ' ');
		}

		/**
		 * 2 つの整数値（int または long）を指定した区切り文字で出力します。（改行無し）
		 *
		 * @param a         出力する整数値（int または long）
		 * @param b         出力する整数値（int または long）
		 * @param delimiter 区切り文字
		 */
		public void print(final long a, final long b, final char delimiter) {
			ensureBufferSpace((MAX_LONG_DIGITS << 1) + 1);
			fillBuffer(a);
			buffer[pos++] = (byte) delimiter;
			fillBuffer(b);
			if (autoFlush) flush();
		}

		// ------------------------ 小数系メソッド ------------------------

		/**
		 * double 値を指定された小数点以下桁数で出力します（四捨五入）。（改行付き）
		 *
		 * @param d 出力する double 値
		 * @param n 小数点以下の桁数
		 */
		public void println(final double d, final int n) {
			print(d, n);
			println();
		}

		/**
		 * double 値を指定された小数点以下桁数で出力します（四捨五入）。（改行無し）
		 *
		 * @param d 出力する double 値
		 * @param n 小数点以下の桁数
		 */
		public void print(double d, int n) {
			if (n == 0) {
				print(round(d));
				return;
			}
			if (d < 0) {
				ensureBufferSpace(1);
				buffer[pos++] = '-';
				d = -d;
			}
			d += pow(10, -n) / 2;
			print((long) d);
			ensureBufferSpace(n + 1);
			buffer[pos++] = '.';
			d -= (long) d;
			while (n-- > 0) {
				d *= 10;
				buffer[pos++] = (byte) ((int) d + '0');
				d -= (int) d;
			}
			if (autoFlush) flush();
		}

		//  --------------------- 1次元配列系メソッド（改行付き） ---------------------

		/**
		 * int 配列の各要素を改行区切りで出力します。
		 *
		 * @param arr 出力する int 配列（null の場合何も出力を行いません）
		 */
		public void println(final int[] arr) {
			println(arr, '\n');
		}

		/**
		 * long 配列の各要素を改行区切りで出力します。
		 *
		 * @param arr 出力する long 配列（null の場合何も出力を行いません）
		 */
		public void println(final long[] arr) {
			println(arr, '\n');
		}

		/**
		 * char 配列の各要素を改行区切りで出力します。
		 *
		 * @param arr 出力する char 配列（null の場合何も出力を行いません）
		 */
		public void println(final char[] arr) {
			println(arr, '\n');
		}

		/**
		 * boolean 配列の各要素を改行区切りで出力します。
		 *
		 * @param arr 出力する boolean 配列（null の場合何も出力を行いません）
		 */
		public void println(final boolean[] arr) {
			println(arr, '\n');
		}

		/**
		 * String 配列の各要素を改行区切りで出力します。
		 *
		 * @param arr 出力する String 配列（null の場合何も出力を行いません）
		 */
		public void println(final String[] arr) {
			println(arr, '\n');
		}

		/**
		 * 可変長の Object 配列の各要素を改行区切りで出力します。
		 *
		 * @param arr 出力する Object 配列（null の場合何も出力を行いません）
		 */
		public void println(final Object... arr) {
			if (arr == null) return;
			for (final Object o : arr)
				println(o);
		}

		/**
		 * int 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する int 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final int[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * long 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する long 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final long[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * char 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する char 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final char[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * boolean 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する boolean 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final boolean[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * String 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する String 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final String[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		//  --------------------- 1次元配列系メソッド（改行無し） ---------------------

		/**
		 * int 配列の各要素を半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する int 配列（null の場合何も出力を行いません）
		 */
		public void print(final int[] arr) {
			print(arr, ' ');
		}

		/**
		 * long 配列の各要素を半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する long 配列（null の場合何も出力を行いません）
		 */
		public void print(final long[] arr) {
			print(arr, ' ');
		}

		/**
		 * char 配列の各要素を半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する char 配列（null の場合何も出力を行いません）
		 */
		public void print(final char[] arr) {
			print(arr, ' ');
		}

		/**
		 * boolean 配列の各要素を半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する boolean 配列（null の場合何も出力を行いません）
		 */
		public void print(final boolean[] arr) {
			print(arr, ' ');
		}

		/**
		 * String 配列の各要素を半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する String 配列（null の場合何も出力を行いません）
		 */
		public void print(final String[] arr) {
			print(arr, ' ');
		}

		/**
		 * 可変長の Object 配列の各要素を半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する Object 配列（null の場合何も出力を行いません）
		 */
		public void print(final Object... arr) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0]);
			for (int i = 1; i < len; i++) {
				print(' ');
				print(arr[i]);
			}
		}

		/**
		 * int 配列の各要素を指定の区切り文字で出力します。（改行無し）
		 *
		 * @param arr       出力する int 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void print(final int[] arr, final char delimiter) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0]);
			for (int i = 1; i < len; i++) {
				ensureBufferSpace(MAX_INT_DIGITS + 1);
				buffer[pos++] = (byte) delimiter;
				fillBuffer(arr[i]);
			}
			if (autoFlush) flush();
		}

		/**
		 * long 配列の各要素を指定の区切り文字で出力します。（改行無し）
		 *
		 * @param arr       出力する long 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void print(final long[] arr, final char delimiter) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0]);
			for (int i = 1; i < len; i++) {
				ensureBufferSpace(MAX_LONG_DIGITS + 1);
				buffer[pos++] = (byte) delimiter;
				fillBuffer(arr[i]);
			}
			if (autoFlush) flush();
		}

		/**
		 * char 配列の各要素を指定の区切り文字で出力します。（改行無し）
		 *
		 * @param arr       出力する char 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void print(final char[] arr, final char delimiter) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0]);
			int i = 1;
			while (i < len) {
				ensureBufferSpace(2);
				int limit = min(buffer.length - pos, len - i);
				while (limit-- > 0) {
					buffer[pos++] = (byte) delimiter;
					buffer[pos++] = (byte) arr[i++];
				}
			}
			if (autoFlush) flush();
		}

		/**
		 * boolean 配列の各要素を指定の区切り文字で出力します。（改行無し）
		 *
		 * @param arr       出力する boolean 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void print(final boolean[] arr, final char delimiter) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0]);
			for (int i = 1; i < len; i++) {
				ensureBufferSpace(4);
				buffer[pos++] = (byte) delimiter;
				fillBuffer(arr[i]);
			}
			if (autoFlush) flush();
		}

		/**
		 * String 配列の各要素を指定の区切り文字で出力します。（改行無し）
		 *
		 * @param arr       出力する String 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void print(final String[] arr, final char delimiter) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0], false);
			for (int i = 1; i < len; i++) {
				print(delimiter);
				print(arr[i], false);
			}
		}

		//  ----------------------- 1次元配列の関数変換系メソッド（改行付き） -----------------------

		/**
		 * int 配列の各要素を指定された関数で変換し、改行区切りで出力します。
		 *
		 * @param arr      出力する int 配列（null の場合何も出力を行いません）
		 * @param function int を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final int[] arr, final IntFunction<T> function) {
			if (arr == null) return;
			for (final int i : arr)
				println(function.apply(i));
		}

		/**
		 * long 配列の各要素を指定された関数で変換し、改行区切りで出力します。
		 *
		 * @param arr      出力する long 配列（null の場合何も出力を行いません）
		 * @param function long を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final long[] arr, final LongFunction<T> function) {
			if (arr == null) return;
			for (final long l : arr)
				println(function.apply(l));
		}

		/**
		 * char 配列の各要素を指定された関数で変換し、改行区切りで出力します。
		 *
		 * @param arr      出力する char 配列（null の場合何も出力を行いません）
		 * @param function char を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final char[] arr, final Function<Character, T> function) {
			if (arr == null) return;
			for (final char c : arr)
				println(function.apply(c));
		}

		/**
		 * boolean 配列の各要素を指定された関数で変換し、改行区切りで出力します。
		 *
		 * @param arr      出力する boolean 配列（null の場合何も出力を行いません）
		 * @param function boolean を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final boolean[] arr, final Function<Boolean, T> function) {
			if (arr == null) return;
			for (final boolean b : arr)
				println(function.apply(b));
		}

		/**
		 * String 配列の各要素を指定された関数で変換し、改行区切りで出力します。
		 *
		 * @param arr      出力する String 配列（null の場合何も出力を行いません）
		 * @param function String を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final String[] arr, final Function<String, T> function) {
			if (arr == null) return;
			for (final String s : arr)
				println(function.apply(s));
		}


		//  ----------------------- 1次元配列の関数変換系メソッド（改行無し） -----------------------

		/**
		 * int 配列の各要素を指定された関数で変換し、半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr      出力する int 配列（null の場合何も出力を行いません）
		 * @param function int を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void print(final int[] arr, final IntFunction<T> function) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		/**
		 * long 配列の各要素を指定された関数で変換し、半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr      出力する long 配列（null の場合何も出力を行いません）
		 * @param function long を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void print(final long[] arr, final LongFunction<T> function) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		/**
		 * char 配列の各要素を指定された関数で変換し、半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr      出力する char 配列（null の場合何も出力を行いません）
		 * @param function char を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void print(final char[] arr, final Function<Character, T> function) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		/**
		 * boolean 配列の各要素を指定された関数で変換し、半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr      出力する boolean 配列（null の場合何も出力を行いません）
		 * @param function boolean を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void print(final boolean[] arr, final Function<Boolean, T> function) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		/**
		 * String 配列の各要素を指定された関数で変換し、半角スペース区切りで出力します。（改行無し）
		 *
		 * @param arr      出力する String 配列（null の場合何も出力を行いません）
		 * @param function String を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void print(final String[] arr, final Function<String, T> function) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		//  ----------------------- 2次元配列系メソッド -----------------------

		/**
		 * 二次元の int 配列を、各行を半角スペース区切りで出力します。（各行末に改行）
		 *
		 * @param arr2d 出力する二次元の int 配列（null の場合何も出力を行いません）
		 */
		public void println(final int[][] arr2d) {
			println(arr2d, ' ');
		}

		/**
		 * 二次元の long 配列を、各行を半角スペース区切りで出力します。（各行末に改行）
		 *
		 * @param arr2d 出力する二次元の long 配列（null の場合何も出力を行いません）
		 */
		public void println(final long[][] arr2d) {
			println(arr2d, ' ');
		}

		/**
		 * 二次元の char 配列を、各行を半角スペース区切りで出力します。（各行末に改行）
		 *
		 * @param arr2d 出力する二次元の char 配列（null の場合何も出力を行いません）
		 */
		public void println(final char[][] arr2d) {
			println(arr2d, ' ');
		}

		/**
		 * 二次元の boolean 配列を、各行を半角スペース区切りで出力します。（各行末に改行）
		 *
		 * @param arr2d 出力する二次元の boolean 配列（null の場合何も出力を行いません）
		 */
		public void println(final boolean[][] arr2d) {
			println(arr2d, ' ');
		}

		/**
		 * 二次元の String 配列を、各行を半角スペース区切りで出力します。（各行末に改行）
		 *
		 * @param arr2d 出力する二次元の String 配列（null の場合何も出力を行いません）
		 */
		public void println(final String[][] arr2d) {
			println(arr2d, ' ');
		}

		/**
		 * 二次元の Object 配列を、各行を半角スペース区切りで出力します。（各行末に改行）
		 *
		 * @param arr2d 出力する二次元の Object 配列（null の場合何も出力を行いません）
		 */
		public void println(final Object[][] arr2d) {
			println(arr2d, ' ');
		}


		/**
		 * 二次元の int 配列を、各行を指定した区切り文字で出力します。（各行末に改行）
		 *
		 * @param arr2d     出力する二次元の int 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final int[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final int[] arr : arr2d)
				println(arr, delimiter);
		}

		/**
		 * 二次元の long 配列を、各行を指定した区切り文字で出力します。（各行末に改行）
		 *
		 * @param arr2d     出力する二次元の long 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final long[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final long[] arr : arr2d)
				println(arr, delimiter);
		}

		/**
		 * 二次元の char 配列を、各行を指定した区切り文字で出力します。（各行末に改行）
		 *
		 * @param arr2d     出力する二次元の char 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final char[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final char[] arr : arr2d)
				println(arr, delimiter);
		}

		/**
		 * 二次元の boolean 配列を、各行を指定した区切り文字で出力します。（各行末に改行）
		 *
		 * @param arr2d     出力する二次元の boolean 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final boolean[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final boolean[] arr : arr2d)
				println(arr, delimiter);
		}

		/**
		 * 二次元の String 配列を、各行を指定した区切り文字で出力します。（各行末に改行）
		 *
		 * @param arr2d     出力する二次元の String 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final String[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final String[] arr : arr2d)
				println(arr, delimiter);
		}

		/**
		 * 二次元の Object 配列を、各行を指定した区切り文字で出力します。（各行末に改行）
		 *
		 * @param arr2d     出力する二次元の Object 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final Object[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final Object[] arr : arr2d) {
				print(arr[0]);
				for (int i = 1; i < arr.length; i++) {
					print(delimiter);
					print(arr[i]);
				}
				println();
			}
		}

		//  ----------------------- 2次元配列関数変換系メソッド -----------------------

		/**
		 * 二次元の int 配列の各要素を指定された関数で変換し、各行を半角スペース区切りで出力（各行末に改行）
		 *
		 * @param arr2d    出力する二次元の int 配列（null の場合何も出力を行いません）
		 * @param function int を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final int[][] arr2d, final IntFunction<T> function) {
			if (arr2d == null) return;
			for (final int[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		/**
		 * 二次元の long 配列の各要素を指定された関数で変換し、各行を半角スペース区切りで出力（各行末に改行）
		 *
		 * @param arr2d    出力する二次元の long 配列（null の場合何も出力を行いません）
		 * @param function long を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final long[][] arr2d, final LongFunction<T> function) {
			if (arr2d == null) return;
			for (final long[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		/**
		 * 二次元の char 配列の各要素を指定された関数で変換し、各行を半角スペース区切りで出力（各行末に改行）
		 *
		 * @param arr2d    出力する二次元の char 配列（null の場合何も出力を行いません）
		 * @param function char を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final char[][] arr2d, final LongFunction<T> function) {
			if (arr2d == null) return;
			for (final char[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		/**
		 * 二次元の boolean 配列の各要素を指定された関数で変換し、各行を半角スペース区切りで出力（各行末に改行）
		 *
		 * @param arr2d    出力する二次元の boolean 配列（null の場合何も出力を行いません）
		 * @param function boolean を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final boolean[][] arr2d, final Function<Boolean, T> function) {
			if (arr2d == null) return;
			for (final boolean[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		/**
		 * 二次元の String 配列の各要素を指定された関数で変換し、各行を半角スペース区切りで出力（各行末に改行）
		 *
		 * @param arr2d    出力する二次元の String 配列（null の場合何も出力を行いません）
		 * @param function String を変換する関数
		 * @param <T>      変換後の型
		 */
		public <T> void println(final String[][] arr2d, final Function<String, T> function) {
			if (arr2d == null) return;
			for (final String[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		//  ----------------------- char配列系メソッド -----------------------

		/**
		 * char 配列の各要素を区切り文字無しで出力します。（改行無し）
		 *
		 * @param arr 出力する char 配列（null の場合何も出力を行いません）
		 */
		public void printChars(final char[] arr) {
			if (arr == null) return;
			int i = 0;
			final int len = arr.length;
			while (i < len) {
				ensureBufferSpace(1);
				int limit = min(buffer.length - pos, len - i);
				while (limit-- > 0) {
					buffer[pos++] = (byte) arr[i++];
				}
			}
			if (autoFlush) flush();
		}

		/**
		 * char 配列の各要素を指定された関数で変換し、区切り文字無しで出力します。
		 *
		 * @param arr      出力する char 配列（null の場合何も出力を行いません）
		 * @param function char を変換する関数
		 */
		public void printChars(final char[] arr, final Function<Character, Character> function) {
			if (arr == null) return;
			int i = 0;
			final int len = arr.length;
			while (i < len) {
				ensureBufferSpace(1);
				int limit = min(buffer.length - pos, len - i);
				while (limit-- > 0) {
					buffer[pos++] = (byte) function.apply(arr[i++]).charValue();
				}
			}
			if (autoFlush) flush();
		}

		/**
		 * 二次元の char 配列を、各行を区切り文字無しで出力（各行末に改行）
		 *
		 * @param arr2d 出力する二次元の char 配列（null の場合何も出力を行いません）
		 */
		public void printChars(final char[][] arr2d) {
			if (arr2d == null) return;
			for (final char[] arr : arr2d) {
				printChars(arr);
				println();
			}
		}

		/**
		 * 二次元の char 配列の各要素を指定された関数で変換し、各行を区切り文字無しで出力（各行末に改行）
		 *
		 * @param arr2d    出力する二次元の char 配列（null の場合何も出力を行いません）
		 * @param function char を変換する関数
		 */
		public void printChars(final char[][] arr2d, final Function<Character, Character> function) {
			if (arr2d == null) return;
			for (final char[] arr : arr2d) {
				printChars(arr, function);
				println();
			}
		}

	}
}
