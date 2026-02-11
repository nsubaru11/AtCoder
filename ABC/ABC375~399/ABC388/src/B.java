import java.io.*;
import java.math.*;
import java.util.*;
import java.util.ArrayList;
import java.util.function.*;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public class B {

	private static final FastScanner sc = new FastScanner();
	private static final FastPrinter out = new FastPrinter();

	public static void main(String[] args) {
		int n = sc.nextInt();
		int d = sc.nextInt();
		int[][] tl = sc.nextIntMat(n, 2);
		for (int i = 1; i <= d; i++) {
			int max = 0;
			for (int j = 0; j < n; j++) {
				max = max(max, tl[j][0] * (tl[j][1] + i));
			}
			out.println(max);
		}
		out.flush();
	}

	/**
	 * UnionFind 総グループ数、各グループの総辺数、頂点数を保持します。
	 */
	@SuppressWarnings("unused")
	private static class UnionFind {
		private final List<List<Integer>> groups;
		private final int[] root, rank, size, path;
		private int cnt;

		public UnionFind(int n) {
			cnt = n;
			root = new int[n];
			rank = new int[n];
			size = new int[n];
			path = new int[n];
			groups = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				size[i] = 1;
				root[i] = i;
				groups.add(new ArrayList<>());
			}
		}

		/**
		 * 引数の頂点の代表元を取得します。
		 *
		 * @param x 頂点
		 * @return 頂点xの代表元
		 */
		public int find(int x) {
			return x != root[x] ? root[x] = find(root[x]) : root[x];
		}

		/**
		 * 引数の二つの頂点が同じグループに属するかどうか。
		 *
		 * @param x 頂点1
		 * @param y 頂点2
		 * @return 同じグループに属するならtrue、そうでなければfalse
		 */
		public boolean isConnected(int x, int y) {
			return find(x) == find(y);
		}

		/**
		 * 引数の二つの頂点を連結する。
		 *
		 * @param x 頂点1
		 * @param y 頂点2
		 * @return すでに連結済みならfalse、そうでなければfalse
		 */
		public boolean union(int x, int y) {
			x = find(x);
			y = find(y);
			path[x]++;
			if (x == y) return false;
			if (rank[x] < rank[y]) {
				int temp = x;
				x = y;
				y = temp;
			}
			if (rank[x] == rank[y]) rank[x]++;
			root[y] = x;
			path[x] += path[y];
			size[x] += size[y];
			cnt--;
			return true;
		}

		/**
		 * このUnionFindのグループ数を返します。
		 *
		 * @return グループ数
		 */
		public int groupCount() {
			return cnt;
		}

		/**
		 * 引数の頂点が属するグループの総辺数を返します。
		 *
		 * @param x 頂点
		 * @return グループの総辺数
		 */
		public int pathCount(int x) {
			return path[find(x)];
		}

		/**
		 * 引数の頂点の属するグループの頂点数を返します。
		 *
		 * @param x 頂点数
		 * @return グループの頂点数
		 */
		public int size(int x) {
			return size[find(x)];
		}

		/**
		 * 頂点i(0 <= i < n)を代表元とする、グループのリストを返します。 iが代表元でないとき、要素を含みません
		 *
		 * @return 全てのグループ
		 */
		public List<List<Integer>> groups() {
			for (int i = 0; i < root.length; i++) {
				groups.get(find(i)).add(i);
			}
			return groups;
		}
	}

	/**
	 * 整数と長整数に対して通常の二分探索、上限探索(Upper Bound)、下限探索(Lower Bound)を行うための抽象クラスです。
	 * 探索に失敗した際の戻り値は-(挿入位置(境界値) - 1)となっています。
	 */
	@SuppressWarnings("unused")
	private static abstract class AbstractBinarySearch {

		/**
		 * 整数範囲での通常の二分探索を行います。comparatorが0を返した時点で探索を終了します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含む)
		 * @return int
		 */
		public final int normalSearch(int l, int r) {
			return binarySearch(l, r, SearchType.NORMAL);
		}

		/**
		 * 長整数範囲での通常の二分探索を行います。comparatorが0を返した時点で探索を終了します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含む)
		 * @return long
		 */
		public final long normalSearch(long l, long r) {
			return binarySearch(l, r, SearchType.NORMAL);
		}

		/**
		 * 整数範囲での上限探索(Upper Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含む)
		 * @return 条件にちょうど当てはまる整数。もしくは上限値+1(挿入位置)。
		 */
		public final int upperBoundSearch(int l, int r) {
			return binarySearch(l, r, SearchType.UPPER_BOUND);
		}

		/**
		 * 長整数範囲での上限探索(Upper Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含む)
		 * @return 条件にちょうど当てはまる長整数。もしくは上限値+1(挿入位置)。
		 */
		public final long upperBoundSearch(long l, long r) {
			return binarySearch(l, r, SearchType.UPPER_BOUND);
		}

		/**
		 * 整数範囲での下限探索(Lower Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含む)
		 * @return 条件にちょうど当てはまる整数。もしくは下限値-1(挿入位置)。
		 */
		public final int lowerBoundSearch(int l, int r) {
			return binarySearch(l, r, SearchType.LOWER_BOUND);
		}

		/**
		 * 長整数範囲での下限探索(Lower Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含む)
		 * @return 条件にちょうど当てはまる長整数。もしくは下限値-1(挿入位置)。
		 */
		public final long lowerBoundSearch(long l, long r) {
			return binarySearch(l, r, SearchType.LOWER_BOUND);
		}

		/**
		 * 整数範囲での汎用二分探索メソッド
		 */
		private final int binarySearch(int l, int r, SearchType type) {
			Integer k = null;
			while (l <= r) {
				int m = (l + r) >> 1;
				switch (comparator(m)) {
					case 1:
						r = m - 1;
						break;
					case 0:
						switch (type) {
							case UPPER_BOUND:
								l = m + 1;
								break;
							case LOWER_BOUND:
								r = m - 1;
								break;
							case NORMAL:
								return m;
						}
						k = m;
						break;
					case -1:
						l = m + 1;
						break;
				}
			}
			return k != null ? k : ~l;
		}

		/**
		 * 長整数範囲での汎用二分探索メソッド
		 */
		private final long binarySearch(long l, long r, SearchType type) {
			Long k = null;
			while (l <= r) {
				long m = (l + r) >> 1L;
				switch (comparator(m)) {
					case 1:
						r = m - 1;
						break;
					case 0:
						switch (type) {
							case UPPER_BOUND:
								l = m + 1;
								break;
							case LOWER_BOUND:
								r = m - 1;
								break;
							case NORMAL:
								return m;
						}
						k = m;
						break;
					case -1:
						l = m + 1;
						break;
				}
			}
			return k != null ? k : ~l;
		}

		/**
		 * 問題に応じた実装を必要とします。条件を超過する際は1, ちょうど合致する際は0、そうでない場合は-1を返すことが望ましい。
		 */
		abstract protected int comparator(long n);

		/**
		 * 内部的に利用される探索種別を示す列挙型
		 */
		private enum SearchType {
			NORMAL, UPPER_BOUND, LOWER_BOUND
		}
	}

	/**
	 * 標準入力を高速に処理するためのクラスです。配列の入力時に総和、最大値、最小値を取得できます。
	 */
	@SuppressWarnings("unused")
	private static class FastScanner {
		private static final BufferedInputStream reader = new BufferedInputStream(System.in);
		private static final byte[] buf = new byte[1 << 17];
		private static int pos = 0, cnt = 0;
		private long sum, low, high;

		/**
		 * バッファから1バイトを読み込みます。
		 *
		 * @return 読み込んだバイト（byte）
		 */
		private byte read() {
			if (pos == cnt) {
				try {
					cnt = reader.read(buf, pos = 0, 1 << 17);
				} catch (IOException ignored) {
				}
			}
			if (cnt < 0)
				return 0;
			return buf[pos++];
		}

		/**
		 * 次の一文字を読み込みます。
		 *
		 * @return 読み込んだ文字(char)
		 */
		public char nextChar() {
			byte b = read();
			while (b < '!' || '~' < b)
				b = read();
			return (char) b;
		}

		/**
		 * 次の文字列のi番目の文字を読み込みます。
		 *
		 * @param i 読み込む文字のindex
		 * @return 読み込んだ文字(char)
		 */
		public char nextCharAt(int i) {
			return next().charAt(i);
		}

		/**
		 * 次のトークンを文字列(String)として読み込みます。
		 *
		 * @return 読み込んだ文字列(String)
		 */
		public String next() {
			return nextSb().toString();
		}

		/**
		 * 次のトークンを文字列(StringBuilder)として読み込みます。
		 *
		 * @return 読み込んだ文字列(StringBuilder)
		 */
		public StringBuilder nextSb() {
			StringBuilder sb = new StringBuilder();
			int b = read();
			while (b < '!' || '~' < b)
				b = read();
			while ('!' <= b && b <= '~') {
				sb.appendCodePoint(b);
				b = read();
			}
			return sb;
		}

		/**
		 * 改行までの一行を読み込みます。
		 *
		 * @return 読み込んだ行(String)
		 */
		public String nextLine() {
			StringBuilder sb = new StringBuilder();
			int b = read();
			while (b != 0 && b != '\r' && b != '\n') {
				sb.appendCodePoint(b);
				b = read();
			}
			if (b == '\r')
				read();
			return sb.toString();
		}

		/**
		 * 次の整数を読み込みます。
		 *
		 * @return 読み込んだ整数(int)
		 */
		public int nextInt() {
			int b = nextChar();
			boolean neg = b == '-';
			if (neg)
				b = read();
			int n = 0;
			while ('0' <= b && b <= '9') {
				n = n * 10 + b - '0';
				b = read();
			}
			return neg ? -n : n;
		}

		/**
		 * 次の長整数を読み込みます。
		 *
		 * @return 読み込んだ長整数(long)
		 */
		public long nextLong() {
			int b = nextChar();
			boolean neg = b == '-';
			if (neg)
				b = read();
			long n = 0;
			while ('0' <= b && b <= '9') {
				n = n * 10 + b - '0';
				b = read();
			}
			return neg ? -n : n;
		}

		/**
		 * 基数を指定して整数を読み込みます。
		 *
		 * @param radix 基数
		 * @return 読み込んだ整数(int)
		 */
		public int nextIntRadix(int radix) {
			return Integer.parseInt(next(), radix);
		}

		/**
		 * 基数を指定して長整数を読み込みます。
		 *
		 * @param radix 基数
		 * @return 読み込んだ長整数(long)
		 */
		public long nextLongRadix(int radix) {
			return Long.parseLong(next(), radix);
		}

		/**
		 * 次のトークンをBigIntegerとして読み込みます。
		 *
		 * @return 読み込んだBigInteger
		 */
		public BigInteger nextBigInteger() {
			return new BigInteger(next());
		}

		/**
		 * 基数を指定してBigIntegerを読み込みます。
		 *
		 * @param radix 基数
		 * @return 読み込んだBigInteger
		 */
		public BigInteger nextBigInteger(int radix) {
			return new BigInteger(next(), radix);
		}

		/**
		 * 次のトークンを浮動小数点数として読み込みます。
		 *
		 * @return 読み込んだ浮動小数点数(double)
		 */
		public double nextDouble() {
			return Double.parseDouble(next());
		}

		/**
		 * 指定された長さの文字列配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ文字列配列(String[])
		 */
		public String[] nextStrings(int n) {
			String[] s = new String[n];
			setAll(s, x -> next());
			return s;
		}

		/**
		 * 指定された長さの文字列配列を読み込み、ソートされた文字列配列として返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた文字列配列(String[])
		 */
		public String[] nextSortedStrings(int n) {
			String[] s = nextStrings(n);
			sort(s);
			return s;
		}

		/**
		 * 指定された行数・列数の二次元文字列配列を読み込みます。
		 *
		 * @param n 行数
		 * @param m 列数
		 * @return 読み込んだ二次元文字列配列(String[][])
		 */
		public String[][] nextStringMat(int n, int m) {
			String[][] s = new String[n][m];
			setAll(s, x -> nextStrings(m));
			return s;
		}


		/**
		 * 文字列を文字配列として読み込みます。
		 *
		 * @return 読み込んだ文字配列(char[])
		 */
		public char[] nextChars() {
			return next().toCharArray();
		}

		/**
		 * 指定された長さの文字配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ文字配列(char[])
		 */
		public char[] nextChars(int n) {
			char[] c = new char[n];
			for (int i = 0; i < n; i++)
				c[i] = nextChar();
			return c;
		}

		/**
		 * 文字列を読み込み、ソートされた文字配列として返します。
		 *
		 * @return ソートされた文字配列(char[])
		 */
		public char[] nextSortedChars() {
			char[] c = nextChars();
			sort(c);
			return c;
		}

		/**
		 * 指定された長さの文字配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた文字配列(char[])
		 */
		public char[] nextSortedChars(int n) {
			char[] c = nextChars(n);
			sort(c);
			return c;
		}

		/**
		 * 複数の文字列を二次元の文字配列として読み込みます。
		 *
		 * @param n 行数（文字列の個数）
		 * @return 読み込んだ二次元文字配列(char[][])
		 */
		public char[][] nextCharMat(int n) {
			char[][] c = new char[n][];
			setAll(c, x -> nextChars());
			return c;
		}

		/**
		 * 指定された行数・列数の二次元文字配列を読み込みます。
		 *
		 * @param n 行数
		 * @param m 列数
		 * @return 読み込んだ二次元文字配列(char[][])
		 */
		public char[][] nextCharMat(int n, int m) {
			char[][] c = new char[n][m];
			setAll(c, x -> nextChars(m));
			return c;
		}

		/**
		 * 指定された長さの整数(int)配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ整数配列(int[])
		 */
		public int[] nextInt(int n) {
			int[] a = new int[n];
			resetStats();
			for (int i = 0; i < n; i++)
				updateStats(a[i] = nextInt());
			return a;
		}

		/**
		 * 指定された長さの整数(Integer)配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ整数配列(Integer[])
		 */
		public Integer[] nextInts(int n) {
			Integer[] a = new Integer[n];
			resetStats();
			for (int i = 0; i < n; i++)
				updateStats(a[i] = nextInt());
			return a;
		}

		/**
		 * 指定された長さの整数(int)配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた整数配列(int[])
		 */
		public int[] nextSortedInt(int n) {
			int[] a = nextInt(n);
			sort(a);
			return a;
		}

		/**
		 * 指定された長さの整数(Integer)配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた整数配列(Integer[])
		 */
		public Integer[] nextSortedInts(int n) {
			Integer[] a = nextInts(n);
			sort(a);
			return a;
		}

		/**
		 * 整数の累積和配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 累積和配列(int[])
		 */
		public int[] nextIntSum(int n) {
			int[] a = new int[n];
			resetStats();
			updateStats(a[0] = nextInt());
			for (int i = 1; i < n; i++) {
				updateStats(a[i] = nextInt());
				a[i] += a[i - 1];
			}
			return a;
		}

		/**
		 * 二次元整数配列を読み込みます。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 読み込んだ二次元整数配列(int[][])
		 */
		public int[][] nextIntMat(int h, int w) {
			int[][] a = new int[h][w];
			setAll(a, x -> nextInt(w));
			return a;
		}

		/**
		 * 二次元整数配列を読み込み、累積和配列として返します。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 二次元累積和配列(int[][])
		 */
		public int[][] nextIntMatSum(int h, int w) {
			int[][] a = new int[h + 1][w + 1];
			for (int i = 1; i <= h; i++)
				for (int j = 1; j <= w; j++)
					a[i][j] = nextInt() + a[i - 1][j] + a[i][j - 1] - a[i - 1][j - 1];
			return a;
		}

		/**
		 * 三次元整数配列を読み込みます。
		 *
		 * @param x サイズX
		 * @param y サイズY
		 * @param z サイズZ
		 * @return 読み込んだ三次元整数配列(int[][][])
		 */
		public int[][][] nextInt3D(int x, int y, int z) {
			int[][][] a = new int[x][y][z];
			setAll(a, b -> nextIntMat(y, z));
			return a;
		}

		/**
		 * 三次元整数配列を読み込み、累積和配列として返します。
		 *
		 * @param x サイズX
		 * @param y サイズY
		 * @param z サイズZ
		 * @return 三次元累積和配列(int[][][])
		 */
		public int[][][] nextIntSum3D(int x, int y, int z) {
			int[][][] e = new int[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++)
					for (int c = 1; c <= z; c++)
						e[a][b][c] = nextInt() + e[a - 1][b][c] + e[a][b - 1][c] + e[a][b][c - 1] - e[a - 1][b - 1][c]
								- e[a - 1][b][c - 1] - e[a][b - 1][c - 1] + e[a - 1][b - 1][c - 1];
			return e;
		}

		/**
		 * 指定された長さの長整数(long)配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ長整数配列(long[])
		 */
		public long[] nextLong(int n) {
			long[] a = new long[n];
			resetStats();
			for (int i = 0; i < n; i++)
				updateStats(a[i] = nextLong());
			return a;
		}

		/**
		 * 指定された長さの長整数(Long)配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 読み込んだ長整数配列(Long[])
		 */
		public Long[] nextLongs(int n) {
			Long[] a = new Long[n];
			resetStats();
			for (int i = 0; i < n; i++)
				updateStats(a[i] = nextLong());
			return a;
		}

		/**
		 * 指定された長さの長整数(long)配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた長整数配列(long[])
		 */
		public long[] nextSortedLong(int n) {
			long[] a = nextLong(n);
			sort(a);
			return a;
		}

		/**
		 * 指定された長さの長整数(Long)配列を読み込み、ソートして返します。
		 *
		 * @param n 配列の長さ
		 * @return ソートされた長整数配列(Long[])
		 */
		public Long[] nextSortedLongs(int n) {
			Long[] a = nextLongs(n);
			sort(a);
			return a;
		}

		/**
		 * 長整数の累積和配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 累積和配列(long[])
		 */
		public long[] nextLongSum(int n) {
			long[] a = new long[n];
			low = high = sum = a[0] = nextLong();
			for (int i = 1; i < n; i++) {
				updateStats(a[i] = nextLong());
				a[i] += a[i - 1];
			}
			return a;
		}

		/**
		 * 二次元長整数配列を読み込みます。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 読み込んだ二次元長整数配列(long[])
		 */
		public long[][] nextLongMat(int h, int w) {
			long[][] a = new long[h][w];
			setAll(a, x -> nextLong(w));
			return a;
		}

		/**
		 * 二次元長整数配列を読み込み、累積和配列として返します。
		 *
		 * @param h 行数
		 * @param w 列数
		 * @return 二次元累積和配列(long[][])
		 */
		public long[][] nextLongMatSum(int h, int w) {
			long[][] a = new long[h + 1][w + 1];
			for (int i = 1; i <= h; i++)
				for (int j = 1; j <= w; j++)
					a[i][j] = nextLong() + a[i - 1][j] + a[i][j - 1] - a[i - 1][j - 1];
			return a;
		}

		/**
		 * 三次元長整数配列を読み込みます。
		 *
		 * @param x サイズX
		 * @param y サイズY
		 * @param z サイズZ
		 * @return 読み込んだ三次元長整数配列(long[][])
		 */
		public long[][][] nextLong3D(int x, int y, int z) {
			long[][][] a = new long[x][y][z];
			setAll(a, b -> nextLongMat(y, z));
			return a;
		}

		/**
		 * 三次元長整数配列を読み込み、累積和配列として返します。
		 *
		 * @param x サイズX
		 * @param y サイズY
		 * @param z サイズZ
		 * @return 三次元累積和配列(long[][][])
		 */
		public long[][][] nextLongSum3D(int x, int y, int z) {
			long[][][] e = new long[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++)
					for (int c = 1; c <= z; c++)
						e[a][b][c] = nextLong() + e[a - 1][b][c] + e[a][b - 1][c] + e[a][b][c - 1] - e[a - 1][b - 1][c]
								- e[a - 1][b][c - 1] - e[a][b - 1][c - 1] + e[a - 1][b - 1][c - 1];
			return e;
		}

		/**
		 * 整数を含むコレクションを読み込みます。
		 *
		 * @param <T> コレクションの型
		 * @param n   要素数
		 * @param s   コレクションのサプライヤー
		 * @return 読み込んだコレクション(Collection)
		 */
		private <T extends Collection<Integer>> T nextIntCollection(int n, Supplier<T> s) {
			T c = s.get();
			resetStats();
			while (n-- > 0) {
				int a = nextInt();
				c.add(a);
				updateStats(a);
			}
			return c;
		}

		/**
		 * 指定された長さの整数のArrayListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだArrayList
		 */
		public ArrayList<Integer> nextIntAL(int n) {
			return nextIntCollection(n, ArrayList::new);
		}

		/**
		 * 指定された長さの整数のLinkedListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedList
		 */
		public LinkedList<Integer> nextIntLL(int n) {
			return nextIntCollection(n, LinkedList::new);
		}

		/**
		 * 指定された長さの整数を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<Integer> nextIntHS(int n) {
			return nextIntCollection(n, HashSet::new);
		}

		/**
		 * 指定された長さの整数を読み込んだLinkedHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashSet
		 */
		public LinkedHashSet<Integer> nextIntLHS(int n) {
			return nextIntCollection(n, LinkedHashSet::new);
		}

		/**
		 * 指定された長さの整数を読み込んだTreeSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだTreeSet
		 */
		public TreeSet<Integer> nextIntTS(int n) {
			return nextIntCollection(n, TreeSet::new);
		}

		/**
		 * 長整数を含むコレクションを読み込みます。
		 *
		 * @param <T> コレクションの型
		 * @param n   要素数
		 * @param s   コレクションのサプライヤー
		 * @return 読み込んだコレクション(Collection)
		 */
		private <T extends Collection<Long>> T nextLongCollection(int n, Supplier<T> s) {
			T c = s.get();
			resetStats();
			while (n-- > 0) {
				long a = nextLong();
				c.add(a);
				updateStats(a);
			}
			return c;
		}

		/**
		 * 指定された長さの整数のArrayListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだArrayList
		 */
		public ArrayList<Long> nextLongAL(int n) {
			return nextLongCollection(n, ArrayList::new);
		}

		/**
		 * 指定された長さの整数のLinkedListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedList
		 */
		public LinkedList<Long> nextLongLL(int n) {
			return nextLongCollection(n, LinkedList::new);
		}

		/**
		 * 指定された長さの長整数を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<Long> nextLongHS(int n) {
			return nextLongCollection(n, HashSet::new);
		}

		/**
		 * 指定された長さの長整数を読み込んだLinkedHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashSet
		 */
		public LinkedHashSet<Long> nextLongLHS(int n) {
			return nextLongCollection(n, LinkedHashSet::new);
		}

		/**
		 * 指定された長さの長整数を読み込んだTreeSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだTreeSet
		 */
		public TreeSet<Long> nextLongTS(int n) {
			return nextLongCollection(n, TreeSet::new);
		}

		/**
		 * 文字列を含むコレクションを読み込みます。
		 *
		 * @param <T> コレクションの型
		 * @param n   要素数
		 * @param s   コレクションのサプライヤー
		 * @return 読み込んだコレクション(Collection)
		 */
		private <T extends Collection<String>> T nextStringCollection(int n, Supplier<T> s) {
			T c = s.get();
			while (n-- > 0) {
				c.add(next());
			}
			return c;
		}

		/**
		 * 指定された長さの文字列のArrayListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだArrayList
		 */
		public ArrayList<String> nextStringAL(int n) {
			return nextStringCollection(n, ArrayList::new);
		}

		/**
		 * 指定された長さの文字列のLinkedListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedList
		 */
		public LinkedList<String> nextStringLL(int n) {
			return nextStringCollection(n, LinkedList::new);
		}

		/**
		 * 指定された長さの文字列を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<String> nextStringHS(int n) {
			return nextStringCollection(n, HashSet::new);
		}

		/**
		 * 指定された長さの文字列を読み込んだLinkedHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashSet
		 */
		public LinkedHashSet<String> nextStringLHS(int n) {
			return nextStringCollection(n, LinkedHashSet::new);
		}

		/**
		 * 指定された長さの文字列を読み込んだTreeSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだTreeSet
		 */
		public TreeSet<String> nextStringTS(int n) {
			return nextStringCollection(n, TreeSet::new);
		}

		/**
		 * 文字を含むコレクションを読み込みます。
		 *
		 * @param <T> コレクションの型
		 * @param n   要素数
		 * @param s   コレクションのサプライヤー
		 * @return 読み込んだコレクション(Collection)
		 */
		private <T extends Collection<Character>> T nextCharCollection(int n, Supplier<T> s) {
			T c = s.get();
			resetStats();
			while (n-- > 0) {
				char t = nextChar();
				c.add(t);
				updateStats(t);
			}
			return c;
		}

		/**
		 * 指定された長さの文字のArrayListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだArrayList
		 */
		public ArrayList<Character> nextCharAL(int n) {
			return nextCharCollection(n, ArrayList::new);
		}

		/**
		 * 指定された長さの文字のLinkedListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedList
		 */
		public LinkedList<Character> nextCharLL(int n) {
			return nextCharCollection(n, LinkedList::new);
		}

		/**
		 * 指定された長さの文字を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<Character> nextCharHS(int n) {
			return nextCharCollection(n, HashSet::new);
		}

		/**
		 * 指定された長さの文字を読み込んだLinkedHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashSet
		 */
		public LinkedHashSet<Character> nextCharLHS(int n) {
			return nextCharCollection(n, LinkedHashSet::new);
		}

		/**
		 * 指定された長さの文字を読み込んだTreeSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだTreeSet
		 */
		public TreeSet<Character> nextCharTS(int n) {
			return nextCharCollection(n, TreeSet::new);
		}

		/**
		 * 整数をKey、出現回数をValueとするMapを読み込みます。
		 *
		 * @param <T> Mapの型
		 * @param n   要素数
		 * @param s   Mapのサプライヤー
		 * @return 読み込んだMap
		 */
		private <T extends Map<Integer, Integer>> T nextIntMultiset(int n, Supplier<T> s) {
			T c = s.get();
			resetStats();
			while (n-- > 0) {
				int a = nextInt();
				c.put(a, c.getOrDefault(a, 0) + 1);
				updateStats(a);
			}
			return c;
		}

		/**
		 * Key(Integer)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashMap
		 */
		public HashMap<Integer, Integer> nextIntMultisetHM(int n) {
			return nextIntMultiset(n, HashMap::new);
		}

		/**
		 * Key(Integer)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashMap
		 */
		public LinkedHashMap<Integer, Integer> nextIntMultisetLHM(int n) {
			return nextIntMultiset(n, LinkedHashMap::new);
		}

		/**
		 * Key(Integer)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 読み込む配列の長さ
		 * @return 読み込んだTreeMap
		 */
		public TreeMap<Integer, Integer> nextIntMultisetTM(int n) {
			return nextIntMultiset(n, TreeMap::new);
		}

		/**
		 * 長整数をKey、出現回数をValueとするMapを読み込みます。
		 *
		 * @param <T> Mapの型
		 * @param n   要素数
		 * @param s   Mapのサプライヤー
		 * @return 読み込んだMap
		 */
		private <T extends Map<Long, Integer>> T nextLongMultiset(int n, Supplier<T> s) {
			T c = s.get();
			resetStats();
			while (n-- > 0) {
				long a = nextLong();
				c.put(a, c.getOrDefault(a, 0) + 1);
				updateStats(a);
			}
			return c;
		}

		/**
		 * Key(Long)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashMap
		 */
		public HashMap<Long, Integer> nextLongMultisetHM(int n) {
			return nextLongMultiset(n, HashMap::new);
		}

		/**
		 * Key(Long)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashMap
		 */
		public LinkedHashMap<Long, Integer> nextLongMultisetLHM(int n) {
			return nextLongMultiset(n, LinkedHashMap::new);
		}

		/**
		 * Key(Long)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 読み込む配列の長さ
		 * @return 読み込んだTreeMap
		 */
		public TreeMap<Long, Integer> nextLongMultisetTM(int n) {
			return nextLongMultiset(n, TreeMap::new);
		}

		/**
		 * 文字列をKey、出現回数をValueとするMapを読み込みます。
		 *
		 * @param <T> Mapの型
		 * @param n   要素数
		 * @param s   Mapのサプライヤー
		 * @return 読み込んだMap
		 */
		private <T extends Map<String, Integer>> T nextStringMultiset(int n, Supplier<T> s) {
			T c = s.get();
			while (n-- > 0) {
				String a = next();
				c.put(a, c.getOrDefault(a, 0) + 1);
			}
			return c;
		}

		/**
		 * Key(String)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashMap
		 */
		public HashMap<String, Integer> nextStringMultisetHM(int n) {
			return nextStringMultiset(n, HashMap::new);
		}

		/**
		 * Key(String)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashMap
		 */
		public LinkedHashMap<String, Integer> nextStringMultisetLHM(int n) {
			return nextStringMultiset(n, LinkedHashMap::new);
		}

		/**
		 * Key(String)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 読み込む配列の長さ
		 * @return 読み込んだTreeMap
		 */
		public TreeMap<String, Integer> nextStringMultisetTM(int n) {
			return nextStringMultiset(n, TreeMap::new);
		}

		/**
		 * 文字をKey、出現回数をValueとするMapを読み込みます。
		 *
		 * @param <T> Mapの型
		 * @param n   要素数
		 * @param s   Mapのサプライヤー
		 * @return 読み込んだMap
		 */
		private <T extends Map<Character, Integer>> T nextCharMultiset(int n, Supplier<T> s) {
			T c = s.get();
			resetStats();
			while (n-- > 0) {
				char a = nextChar();
				c.put(a, c.getOrDefault(a, 0) + 1);
				updateStats(a);
			}
			return c;
		}

		/**
		 * Key(Character)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashMap
		 */
		public HashMap<Character, Integer> nextCharMultisetHM(int n) {
			return nextCharMultiset(n, HashMap::new);
		}

		/**
		 * Key(Character)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだLinkedHashMap
		 */
		public LinkedHashMap<Character, Integer> nextCharMultisetLHM(int n) {
			return nextCharMultiset(n, LinkedHashMap::new);
		}

		/**
		 * Key(Character)に対しValueをその出現回数とする写像を返します。
		 *
		 * @param n 読み込む配列の長さ
		 * @return 読み込んだTreeMap
		 */
		public TreeMap<Character, Integer> nextCharMultisetTM(int n) {
			return nextCharMultiset(n, TreeMap::new);
		}

		/**
		 * 最後に読み込んだ配列の最小値を返します。
		 *
		 * @return 最小値
		 */
		public long getLowestNum() {
			return low;
		}

		/**
		 * 最後に読み込んだ配列の最大値を返します。
		 *
		 * @return 最大値
		 */
		public long getHighestNum() {
			return high;
		}

		/**
		 * 最後に読み込んだ配列の総和を返します。
		 *
		 * @return 総和
		 */
		public long getSum() {
			return sum;
		}

		/**
		 * 統計情報をリセットします（最小値、最大値、総和）。
		 */
		private void resetStats() {
			low = Long.MAX_VALUE;
			high = Long.MIN_VALUE;
			sum = 0;
		}

		/**
		 * 読み込んだ値で統計情報（最小値、最大値、総和）を更新します。
		 *
		 * @param a 更新する値
		 */
		private void updateStats(long a) {
			sum += a;
			low = min(low, a);
			high = max(high, a);
		}
	}

	/**
	 * 標準出力を管理するクラスです。 複数回の出力を高速に行うことに適しており、全ての出力をStringBuilderで結合して最後に出力します。
	 * 一度flushしたあと再度出力を行う必要がある場合、clearメソッドを呼び出しStringBuilderを一度初期化する必要があります。
	 * 逐次flushを行う必要がある場合や出力が一度のみでよい場合などは推奨されません。
	 * 内部でStringBuilderを用いるためスレッドセーフではありません。
	 */
	@SuppressWarnings("unused")
	private static final class FastPrinter {
		private StringBuilder sb;

		public FastPrinter() {
			sb = new StringBuilder(65536);
		}

		/**
		 * 改行を一つ出力します。
		 */
		public FastPrinter println() {
			print("\n");
			return this;
		}

		/**
		 * Objectを出力し、行末は改行します。
		 *
		 * @param o Object
		 */
		public FastPrinter println(Object o) {
			print(o).println();
			return this;
		}

		/**
		 * Stringを出力し、行末は改行します。
		 *
		 * @param s String
		 */
		public FastPrinter println(String s) {
			print(s).println();
			return this;
		}

		/**
		 * booleanを出力し、行末は改行します。
		 *
		 * @param f boolean
		 */
		public FastPrinter println(boolean f) {
			print(f).println();
			return this;
		}

		/**
		 * charを出力し、行末は改行します。
		 *
		 * @param c char
		 */
		public FastPrinter println(char c) {
			print(c).println();
			return this;
		}

		/**
		 * intを出力し、行末は改行します。
		 *
		 * @param a int
		 */
		public FastPrinter println(int a) {
			print(a).println();
			return this;
		}

		/**
		 * longを出力し、行末は改行します。
		 *
		 * @param a long
		 */
		public FastPrinter println(long a) {
			print(a).println();
			return this;
		}

		/**
		 * doubleを出力し、行末は改行します。
		 *
		 * @param a double
		 */
		public FastPrinter println(double a) {
			print(a).println();
			return this;
		}

		/**
		 * doubleを桁数を指定して出力し、行末を改行します。
		 *
		 * @param a double
		 * @param n int
		 */
		public FastPrinter println(double a, int n) {
			print(a, n).println();
			return this;
		}

		/**
		 * 2つの整数を改行区切りで出力し、行末を改行します。
		 *
		 * @param a int
		 * @param b int
		 */
		public FastPrinter println(int a, int b) {
			println(a).println(b);
			return this;
		}

		/**
		 * 2つの整数を改行区切りで出力し、行末を改行します。
		 *
		 * @param a int
		 * @param b long
		 */
		public FastPrinter println(int a, long b) {
			println(a).println(b);
			return this;
		}

		/**
		 * 2つの整数を改行区切りで出力し、行末を改行します。
		 *
		 * @param a long
		 * @param b int
		 */
		public FastPrinter println(long a, int b) {
			println(a).println(b);
			return this;
		}

		/**
		 * 2つの整数を改行区切りで出力し、行末を改行します。
		 *
		 * @param a long
		 * @param b long
		 */
		public FastPrinter println(long a, long b) {
			println(a).println(b);
			return this;
		}

		/**
		 * Object配列を改行区切りで出力し、行末も改行します。
		 *
		 * @param o Object...
		 */
		public FastPrinter println(Object... o) {
			for (Object x : o)
				println(x);
			return this;
		}

		/**
		 * String配列を改行区切りで出力し、行末も改行します。
		 *
		 * @param s String[]
		 */
		public FastPrinter println(String[] s) {
			for (String x : s)
				println(x);
			return this;
		}

		/**
		 * char配列を改行区切りで出力し、行末も改行します。
		 *
		 * @param c char[]
		 */
		public FastPrinter println(char[] c) {
			for (char x : c)
				println(x);
			return this;
		}

		/**
		 * int配列を改行区切りで出力し、行末も改行します。
		 *
		 * @param a int[]
		 */
		public FastPrinter println(int[] a) {
			for (int x : a)
				println(x);
			return this;
		}

		/**
		 * long配列を改行区切りで出力し、行末も改行します。
		 *
		 * @param a long[]
		 */
		public FastPrinter println(long[] a) {
			for (long x : a)
				println(x);
			return this;
		}

		/**
		 * Objectの二次元配列を出力し、行末は改行します。 行ごとに改行し、区切り文字は半角スペース。
		 *
		 * @param o Object[][]
		 */
		public FastPrinter println(Object[][] o) {
			for (Object[] x : o)
				print(x).println();
			return this;
		}

		/**
		 * charの二次元配列を出力し、行末は改行します。 行ごとに改行し、区切り文字は半角スペース。
		 *
		 * @param c char[][]
		 */
		public FastPrinter println(char[][] c) {
			for (char[] x : c)
				print(x).println();
			return this;
		}

		/**
		 * intの二次元配列を出力し、行末は改行します。 行ごとに改行し、区切り文字は半角スペース。
		 *
		 * @param a int[][]
		 */
		public FastPrinter println(int[][] a) {
			for (int[] x : a)
				print(x).println();
			return this;
		}

		/**
		 * longの二次元配列を出力し、行末は改行します。 行ごとに改行し、区切り文字は半角スペース。
		 *
		 * @param a long[][]
		 */
		public FastPrinter println(long[][] a) {
			for (long[] x : a)
				print(x).println();
			return this;
		}

		/**
		 * charの二次元配列を区切り文字なしで出力します。行末は改行します。
		 *
		 * @param c char[][]
		 */
		public FastPrinter printChars(char[][] c) {
			for (char[] x : c)
				printChars(x).println();
			return this;
		}

		/**
		 * Objectを出力し、行末は改行しません。
		 *
		 * @param o Object
		 */
		public FastPrinter print(Object o) {
			sb.append(o);
			return this;
		}

		/**
		 * Stringを出力し、行末は改行しません。
		 *
		 * @param s String
		 */
		public FastPrinter print(String s) {
			sb.append(s);
			return this;
		}

		/**
		 * booleanを出力し、行末は改行しません。
		 *
		 * @param f boolean
		 */
		public FastPrinter print(boolean f) {
			sb.append(f);
			return this;
		}

		/**
		 * charを出力し、行末は改行しません。
		 *
		 * @param c char
		 */
		public FastPrinter print(char c) {
			sb.append(c);
			return this;
		}

		/**
		 * intを出力し、行末は改行しません。
		 *
		 * @param a int
		 */
		public FastPrinter print(int a) {
			sb.append(a);
			return this;
		}

		/**
		 * longを出力し、行末は改行しません。
		 *
		 * @param a long
		 */
		public FastPrinter print(long a) {
			sb.append(a);
			return this;
		}

		/**
		 * doubleを出力し、行末は改行しません。
		 *
		 * @param a double
		 */
		public FastPrinter print(double a) {
			sb.append(a);
			return this;
		}

		/**
		 * doubleを桁数を指定して出力し、行末は改行しません。
		 *
		 * @param a double
		 * @param n int
		 */
		public FastPrinter print(double a, int n) {
			if (n == 0) {
				print(Math.round(a));
				return this;
			}
			if (a < 0) {
				print("-");
				a = -a;
			}
			a += Math.pow(10, -n) / 2;
			print((long) a).print(".");
			a -= (long) a;
			while (n-- > 0) {
				a *= 10;
				print((int) a);
				a -= (int) a;
			}
			return this;
		}

		/**
		 * 2つの整数を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param a int
		 * @param b int
		 */
		public FastPrinter print(int a, int b) {
			print(a).print(" ").print(b);
			return this;
		}

		/**
		 * 2つの整数を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param a int
		 * @param b long
		 */
		public FastPrinter print(int a, long b) {
			print(a).print(" ").print(b);
			return this;
		}

		/**
		 * 2つの整数を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param a long
		 * @param b int
		 */
		public FastPrinter print(long a, int b) {
			print(a).print(" ").print(b);
			return this;
		}

		/**
		 * 2つの整数を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param a long
		 * @param b long
		 */
		public FastPrinter print(long a, long b) {
			print(a).print(" ").print(b);
			return this;
		}

		/**
		 * Object配列を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param o Object...
		 */
		public FastPrinter print(Object... o) {
			print(o[0]);
			for (int i = 1; i < o.length; i++) {
				print(" ").print(o[i]);
			}
			return this;
		}

		/**
		 * String配列を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param s String[]
		 */
		public FastPrinter print(String[] s) {
			print(s[0]);
			for (int i = 1; i < s.length; i++) {
				print(" ").print(s[i]);
			}
			return this;
		}

		/**
		 * char配列を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param c char[]
		 */
		public FastPrinter print(char[] c) {
			print(c[0]);
			for (int i = 1; i < c.length; i++) {
				print(" ").print(c[i]);
			}
			return this;
		}

		/**
		 * int配列を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param a int[]
		 */
		public FastPrinter print(int[] a) {
			print(a[0]);
			for (int i = 1; i < a.length; i++) {
				print(" ").print(a[i]);
			}
			return this;
		}

		/**
		 * long配列を半角スペース区切りで出力し、行末は改行しません。
		 *
		 * @param a long[]
		 */
		public FastPrinter print(long[] a) {
			print(a[0]);
			for (int i = 1; i < a.length; i++) {
				print(" ").print(a[i]);
			}
			return this;
		}

		/**
		 * charの配列を区切り文字無しで出力します。
		 *
		 * @param c char[]
		 */
		public FastPrinter printChars(char[] c) {
			sb.append(c);
			return this;
		}

		/**
		 * フォーマットを指定して出力します。
		 *
		 * @param format String 書式文字列
		 * @param args   Object... 書式に割り当てる値
		 */
		public FastPrinter printf(String format, Object... args) {
			print(String.format(format, args));
			return this;
		}

		/**
		 * フォーマットを指定して出力します。 このとき指定した言語環境での整形を行います。
		 *
		 * @param locale Locale 言語環境
		 * @param format String 書式文字列
		 * @param args   Object... 書式に割り当てる値
		 */
		public FastPrinter printf(Locale locale, String format, Object... args) {
			print(String.format(locale, format, args));
			return this;
		}

		/**
		 * StringBuilderにためた出力内容を逆順にします。
		 */
		public FastPrinter reverse() {
			sb.reverse();
			return this;
		}

		/**
		 * StringBuilderにためた出力内容を表示します。
		 */
		public FastPrinter flush() {
			System.out.print(sb);
			return this;
		}

		/**
		 * StringBuilderの初期化を行います。
		 */
		public FastPrinter clear() {
			sb.setLength(0);
			return this;
		}
	}

}