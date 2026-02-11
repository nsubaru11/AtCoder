import java.io.*;
import java.math.*;
import java.util.*;
import java.util.ArrayList;
import java.util.function.*;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public class D {

	private static void solve(FastScanner sc, FastPrinter out) {
		int n = sc.nextInt();
		int a = sc.nextInt(), b = sc.nextInt();
		char[] s = sc.nextChars();
		int cnt = 0;
		int cntb = 0;
		for (int i = 0; i < n; i++) {
			if (cnt < a + b) {
				if (s[i] == 'a') {
					out.println("Yes");
					cnt++;
				} else if (cntb < b && s[i] == 'b') {
					out.println("Yes");
					cnt++;
					cntb++;
				} else {
					out.println("No");
				}
			} else {
				out.println("No");
			}
		}
	}

	public static void main(String[] args) {
		try (final FastPrinter out = new FastPrinter()) {
			final FastScanner sc = new FastScanner();
			solve(sc, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 標準入力を高速に処理するためのクラスです。
	 */
	private static final class FastScanner {
		private static final int DEFAULT_BUFFER_SIZE = 65536;
		private static final InputStream in = System.in;
		private static int pos = 0, cnt = 0;
		private final byte[] buffer;

		public FastScanner() {
			this(DEFAULT_BUFFER_SIZE);
		}

		public FastScanner(int bufferSize) {
			buffer = new byte[bufferSize];
		}

		public byte read() {
			if (pos == cnt) {
				try {
					cnt = in.read(buffer, pos = 0, buffer.length);
				} catch (IOException ignored) {
				}
			}
			if (cnt < 0) return 0;
			return buffer[pos++];
		}

		/**
		 * 次の一文字を読み込みます。
		 *
		 * @return 読み込んだ文字(char)
		 */
		public char nextChar() {
			int b = read();
			while (b < '!' || '~' < b) b = read();
			return (char) b;
		}

		/**
		 * 次のトークンを文字列(String)として読み込みます。
		 *
		 * @return 読み込んだ文字列(String)
		 */
		public String next() {
			return nextStringBuilder().toString();
		}

		/**
		 * 次のトークンを文字列(StringBuilder)として読み込みます。
		 *
		 * @return 読み込んだ文字列(StringBuilder)
		 */
		public StringBuilder nextStringBuilder() {
			StringBuilder sb = new StringBuilder();
			int b = nextChar();
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
			if (b == '\r') pos++;
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
			if (neg) b = read();
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
			if (neg) b = read();
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
		 * 次の文字列のi番目の文字を読み込みます。
		 *
		 * @param i 読み込む文字のindex
		 * @return 読み込んだ文字(char)
		 */
		public char nextCharAt(int i) {
			return next().charAt(i);
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
			setAll(a, i -> nextInt());
			return a;
		}

		public int[] nextInt(int n, IntFunction<Integer> generator) {
			int[] a = new int[n];
			setAll(a, i -> generator.apply(nextInt()));
			return a;
		}

		public int[] nextInt(int n, BiFunction<Integer, Integer, Integer> generator) {
			int[] a = new int[n];
			setAll(a, i -> generator.apply(i, nextInt()));
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
		 * 整数の累積和配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 累積和配列(int[])
		 */
		public int[] nextIntSum(int n) {
			int[] a = new int[n];
			setAll(a, i -> i > 0 ? nextInt() + a[i - 1] : nextInt());
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
			for (int i = 1; i <= h; i++) {
				int j = i;
				setAll(a[i], k -> k > 0 ? nextInt() + a[j - 1][k] + a[j][k - 1] - a[j - 1][k - 1] : 0);
			}
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
				for (int b = 1; b <= y; b++) {
					int A = a, B = b;
					setAll(e[A][B], c -> c > 0 ? nextInt() + e[A - 1][B][c] + e[A][B - 1][c] + e[A][B][c - 1]
							- e[A - 1][B - 1][c] - e[A - 1][B][c - 1] - e[A][B - 1][c - 1] + e[A - 1][B - 1][c - 1] : 0);
				}
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
			setAll(a, i -> nextLong());
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
		 * 長整数の累積和配列を読み込みます。
		 *
		 * @param n 配列の長さ
		 * @return 累積和配列(long[])
		 */
		public long[] nextLongSum(int n) {
			long[] a = new long[n];
			setAll(a, i -> i > 0 ? nextLong() + a[i - 1] : nextLong());
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
			for (int i = 1; i <= h; i++) {
				int j = i;
				setAll(a[i], k -> k > 0 ? nextLong() + a[j - 1][k] + a[j][k - 1] - a[j - 1][k - 1] : 0);
			}
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
				for (int b = 1; b <= y; b++) {
					int A = a, B = b;
					setAll(e[A][B], c -> c > 0 ? nextLong() + e[A - 1][B][c] + e[A][B - 1][c] + e[A][B][c - 1]
							- e[A - 1][B - 1][c] - e[A - 1][B][c - 1] - e[A][B - 1][c - 1] + e[A - 1][B - 1][c - 1] : 0);
				}
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
			while (n-- > 0) {
				c.add(nextInt());
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
			return nextIntCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの整数を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<Integer> nextIntHS(int n) {
			return nextIntCollection(n, () -> new HashSet<>(n));
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
			while (n-- > 0) {
				c.add(nextLong());
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
			return nextLongCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの長整数を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<Long> nextLongHS(int n) {
			return nextLongCollection(n, () -> new HashSet<>(n));
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
			return nextStringCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの文字列を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<String> nextStringHS(int n) {
			return nextStringCollection(n, () -> new HashSet<>(n));
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
		private <T extends Collection<Character>> T nextCharacterCollection(int n, Supplier<T> s) {
			T c = s.get();
			while (n-- > 0) {
				c.add(nextChar());
			}
			return c;
		}

		/**
		 * 指定された長さの文字のArrayListを読み込みます。
		 *
		 * @param n 要素数
		 * @return 読み込んだArrayList
		 */
		public ArrayList<Character> nextCharacterAL(int n) {
			return nextCharacterCollection(n, () -> new ArrayList<>(n));
		}

		/**
		 * 指定された長さの文字を読み込んだHashSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだHashSet
		 */
		public HashSet<Character> nextCharacterHS(int n) {
			return nextCharacterCollection(n, () -> new HashSet<>(n));
		}

		/**
		 * 指定された長さの文字を読み込んだTreeSetを返します。
		 *
		 * @param n 要素数
		 * @return 読み込んだTreeSet
		 */
		public TreeSet<Character> nextCharacterTS(int n) {
			return nextCharacterCollection(n, TreeSet::new);
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
			while (n-- > 0) {
				int a = nextInt();
				c.put(a, c.getOrDefault(a, 0) + 1);
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
			return nextIntMultiset(n, () -> new HashMap<>(n));
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
			while (n-- > 0) {
				long a = nextLong();
				c.put(a, c.getOrDefault(a, 0) + 1);
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
			return nextLongMultiset(n, () -> new HashMap<>(n));
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
			return nextStringMultiset(n, () -> new HashMap<>(n));
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
			while (n-- > 0) {
				char a = nextChar();
				c.put(a, c.getOrDefault(a, 0) + 1);
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
			return nextCharMultiset(n, () -> new HashMap<>(n));
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

	}

	/**
	 * 標準出力を高速に処理するクラスです。<br>
	 * ※注意: 内部バッファが満杯になると自動的に指定の OutputStream に書き出します。<br>
	 * 処理途中で結果をすぐに反映させる必要がある場合は、autoFlush を true にするか、明示的に {@link #flush()} を呼び出してください。
	 */
	private static final class FastPrinter implements AutoCloseable {

		//  ------------------------ 定数 ------------------------
		/**
		 * int 型の値を文字列に変換した際に必要となる最大桁数（符号込み）<br>
		 * 例: Integer.MIN_VALUE は "-2147483648"（11バイト）
		 */
		private static final int MAX_INT_DIGITS = 11;

		/**
		 * long 型の値を文字列に変換した際に必要となる最大桁数（符号込み）<br>
		 * 例: Long.MIN_VALUE は "-9223372036854775808"（20バイト）
		 */
		private static final int MAX_LONG_DIGITS = 20;

		/**
		 * 出力用内部バッファの初期サイズ（バイト単位）<br>
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
		private final byte[] buffer;

		/**
		 * 出力後に自動的にバッファを flush するかどうかを示すフラグです。<br>
		 * true の場合、各出力操作後に自動的に {@link #flush()} が呼ばれます。
		 */
		private final boolean autoFlush;

		/**
		 * 出力先の OutputStream です。デフォルトは {@code System.out} です。
		 */
		private final OutputStream out;

		/**
		 * 現在のバッファ内での書き込み位置
		 */
		private int pos = 0;

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
		public void println() {
			ensureBufferSpace(1);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		/**
		 * int 値を出力します。（改行付き）
		 *
		 * @param i 出力する int 値
		 */
		public void println(final int i) {
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
		public void println(final long l) {
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
		public void println(final double d) {
			print(Double.toString(d), true);
		}

		/**
		 * char 値を出力します。（改行付き）
		 *
		 * @param c 出力する char 値
		 */
		public void println(final char c) {
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
		public void println(final boolean b) {
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
		public void println(final String s) {
			print(s, true);
		}

		/**
		 * Object を {@code toString()} で文字列化し出力します。（改行付き）
		 *
		 * @param o 出力するオブジェクト
		 */
		public void println(final Object o) {
			if (o == null) return;
			if (o instanceof String s) {
				print(s, true);
			} else if (o instanceof Long l) {
				println(l);
			} else if (o instanceof Integer i) {
				println(i);
			} else if (o instanceof Double d) {
				println(d);
			} else if (o instanceof Boolean b) {
				println(b);
			} else if (o instanceof Character c) {
				println(c);
			} else {
				print(o.toString(), true);
			}
		}

		/**
		 * BigInteger を {@code toString()} で文字列化し出力します。（改行付き）
		 *
		 * @param bi 出力するオブジェクト
		 */
		public void println(final BigInteger bi) {
			print(bi.toString(), true);
		}

		/**
		 * BigDecimal を {@code toString()} で文字列化し出力します。（改行付き）
		 *
		 * @param bd 出力するオブジェクト
		 */
		public void println(final BigDecimal bd) {
			print(bd.toString(), true);
		}

		//  --------------------- print() 系メソッド ---------------------

		/**
		 * int 値を出力します。（改行無し）
		 *
		 * @param i 出力する int 値
		 */
		public void print(final int i) {
			ensureBufferSpace(MAX_INT_DIGITS);
			fillBuffer(i);
			if (autoFlush) flush();
		}

		/**
		 * long 値を出力します。（改行無し）
		 *
		 * @param l 出力する long 値
		 */
		public void print(final long l) {
			ensureBufferSpace(MAX_LONG_DIGITS);
			fillBuffer(l);
			if (autoFlush) flush();
		}

		/**
		 * double 値を {@code Double.toString(d)} で文字列化し出力します。（改行無し）
		 *
		 * @param d 出力する double 値
		 */
		public void print(final double d) {
			print(Double.toString(d), false);
		}

		/**
		 * char 値を出力します。（改行無し）
		 *
		 * @param c 出力する char 値
		 */
		public void print(final char c) {
			ensureBufferSpace(1);
			buffer[pos++] = (byte) c;
			if (autoFlush) flush();
		}

		/**
		 * boolean 値を出力します。（true は "Yes"、 false は "No", 改行無し）
		 *
		 * @param b 出力する boolean 値
		 */
		public void print(final boolean b) {
			ensureBufferSpace(3);
			fillBuffer(b);
			if (autoFlush) flush();
		}

		/**
		 * String を出力します。（改行無し）
		 *
		 * @param s 出力する String 値
		 */
		public void print(final String s) {
			print(s, false);
		}

		/**
		 * Object を {@code toString()} で文字列化し出力します。（改行無し）
		 *
		 * @param o 出力するオブジェクト
		 */
		public void print(final Object o) {
			if (o == null) return;
			if (o instanceof String s) {
				print(s, false);
			} else if (o instanceof Long l) {
				print(l);
			} else if (o instanceof Integer i) {
				print(i);
			} else if (o instanceof Double d) {
				print(d);
			} else if (o instanceof Boolean b) {
				print(b);
			} else if (o instanceof Character c) {
				print(c);
			} else {
				print(o.toString(), false);
			}
		}

		/**
		 * BigInteger を {@code toString()} で文字列化し出力します。（改行無し）
		 *
		 * @param bi 出力するオブジェクト
		 */
		public void print(final BigInteger bi) {
			print(bi.toString(), false);
		}

		/**
		 * BigDecimal を {@code toString()} で文字列化し出力します。（改行無し）
		 *
		 * @param bd 出力するオブジェクト
		 */
		public void print(final BigDecimal bd) {
			print(bd.toString(), false);
		}

		//  --------------------- printf() 系メソッド ---------------------

		/**
		 * 指定されたフォーマットに従い文字列を生成して出力します。（改行無し）
		 *
		 * @param format 書式文字列
		 * @param args   書式引数
		 */
		public void printf(String format, Object... args) {
			print(String.format(format, args), false);
		}

		/**
		 * 指定された言語環境で整形し、フォーマットに従い文字列を生成して出力します。（改行無し）
		 *
		 * @param locale 言語環境
		 * @param format 書式文字列
		 * @param args   書式引数
		 */
		public void printf(Locale locale, String format, Object... args) {
			print(String.format(locale, format, args), false);
		}

		// ------------------------ ペア出力メソッド（改行付き） ------------------------

		/**
		 * 2 つの整数値（int または long）をそれぞれ改行して出力します。
		 *
		 * @param a 出力する整数値（int または long）
		 * @param b 出力する整数値（int または long）
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
		 * 2 つの整数値（int または long）を半角スペース区切りで出力します。
		 *
		 * @param a 出力する整数値（int または long）
		 * @param b 出力する整数値（int または long）
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
		public void println(final int[] arr, char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * long 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する long 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final long[] arr, char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * char 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する char 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final char[] arr, char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * boolean 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する boolean 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final boolean[] arr, char delimiter) {
			print(arr, delimiter);
			println();
		}

		/**
		 * String 配列の各要素を指定の区切り文字で出力します。（改行付き）
		 *
		 * @param arr       出力する String 配列（null の場合何も出力を行いません）
		 * @param delimiter 区切り文字
		 */
		public void println(final String[] arr, char delimiter) {
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
		 * long 配列の各要素を半角スペース区切り区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する long 配列（null の場合何も出力を行いません）
		 */
		public void print(final long[] arr) {
			print(arr, ' ');
		}

		/**
		 * char 配列の各要素を半角スペース区切り区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する char 配列（null の場合何も出力を行いません）
		 */
		public void print(final char[] arr) {
			print(arr, ' ');
		}

		/**
		 * boolean 配列の各要素を半角スペース区切り区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する boolean 配列（null の場合何も出力を行いません）
		 */
		public void print(final boolean[] arr) {
			print(arr, ' ');
		}

		/**
		 * String 配列の各要素を半角スペース区切り区切りで出力します。（改行無し）
		 *
		 * @param arr 出力する String 配列（null の場合何も出力を行いません）
		 */
		public void print(final String[] arr) {
			print(arr, ' ');
		}

		/**
		 * 可変長の Object 配列の各要素を半角スペース区切り区切りで出力します。（改行無し）
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
		public void print(final int[] arr, char delimiter) {
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
		public void print(final long[] arr, char delimiter) {
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
		public void print(final char[] arr, char delimiter) {
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
		public void print(final boolean[] arr, char delimiter) {
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
		public void print(final String[] arr, char delimiter) {
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
		public void println(final int[][] arr2d, char delimiter) {
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
		public void println(final long[][] arr2d, char delimiter) {
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
		public void println(final char[][] arr2d, char delimiter) {
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
		public void println(final boolean[][] arr2d, char delimiter) {
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
		public void println(final String[][] arr2d, char delimiter) {
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
		public void println(final Object[][] arr2d, char delimiter) {
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

		//  --------------------- 内部メソッド ---------------------

		/**
		 * 指定されたバイト数のデータを出力するために必要な領域を保証します。<br>
		 * バッファの残り容量が不足している場合、flush() を呼び出してバッファをクリアします。
		 *
		 * @param size 出力予定のデータのバイト数
		 */
		private void ensureBufferSpace(int size) {
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
		 * @param s       出力する文字列（null の場合は "null" として出力）
		 * @param newline true の場合、出力後に改行を追加
		 */
		private void print(String s, boolean newline) {
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
		private void fillBuffer(String s) {
			int i = 0;
			final int len = s.length();
			while (i < len) {
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
		private void fillBuffer(boolean b) {
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
		private void fillBuffer(int i) {
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
			int p = pos + numOfDigits;
			while (i >= 100) {
				int q = i / 100;
				int r = i - q * 100;
				buffer[--p] = TWO_DIGIT_NUMBERS[(r << 1) + 1];
				buffer[--p] = TWO_DIGIT_NUMBERS[r << 1];
				i = q;
			}

			if (i < 10) {
				buffer[--p] = (byte) ('0' + i);
			} else {
				buffer[--p] = TWO_DIGIT_NUMBERS[(i << 1) + 1];
				buffer[--p] = TWO_DIGIT_NUMBERS[i << 1];
			}

			pos += numOfDigits;
		}

		/**
		 * 指定された long 値を文字列に変換してバッファに格納します。<br>
		 * 負の値の場合は先頭に '-' を付加します。
		 *
		 * @param l 出力する long 値
		 */
		private void fillBuffer(long l) {
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
				int remainder = (int) (l - quotient * 100);
				buffer[--writePos] = TWO_DIGIT_NUMBERS[(remainder << 1) + 1];
				buffer[--writePos] = TWO_DIGIT_NUMBERS[remainder << 1];
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
		 *
		 * @param i 数値
		 * @return 桁数
		 */
		private int countDigits(int i) {
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
		 *
		 * @param l 数値
		 * @return 桁数
		 */
		private int countDigits(long l) {
			if (l < 10) return 1;
			if (l < 100) return 2;
			if (l < 1000) return 3;
			if (l < 10000) return 4;
			if (l < 100000) return 5;
			if (l < 1000000) return 6;
			if (l < 10000000) return 7;
			if (l < 100000000) return 8;
			if (l < 1000000000) return 9;
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
}