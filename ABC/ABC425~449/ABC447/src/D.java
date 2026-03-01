import java.io.*;
import java.lang.invoke.*;
import java.math.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public final class D {

	// region < Constants & Globals >
	private static final boolean DEBUG;
	private static final int MOD;
	private static final int[] di;
	private static final int[] dj;
	private static final FastScanner sc;
	private static final FastPrinter out;

	static {
		DEBUG = true;
		MOD = 998244353;
		// MOD = 1_000_000_007;
		di = new int[]{0, -1, 0, 1, -1, -1, 1, 1};
		dj = new int[]{-1, 0, 1, 0, -1, 1, 1, -1};
		sc = new FastScanner();
		out = new FastPrinter(64);
	}
	// endregion

	private static void solve() {
		int cnt = 0;
		int ca = 0, cab = 0;
		for (char si : sc.nextChars()) {
			if (si == 'A') {
				ca++;
			} else if (si == 'B') {
				if (ca > 0) {
					ca--;
					cab++;
				}
			} else {
				if (cab == 0) continue;
				cab--;
				cnt++;
			}
		}
		out.println(cnt);
	}

	// region < Utility Methods >
	private static boolean isValidRange(final int i, final int j, final int h, final int w) {
		return 0 <= i && i < h && 0 <= j && j < w;
	}

	private static void swap(final char[] a, final int i, final int j) {
		final char tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}

	private static void swap(final int[] a, final int i, final int j) {
		final int tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}

	private static void swap(final long[] a, final int i, final int j) {
		final long tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}

	private static boolean chmin(final char[] a, final int i, final char v) {
		if (a[i] <= v) return false;
		a[i] = v;
		return true;
	}

	private static boolean chmin(final int[] a, final int i, final int v) {
		if (a[i] <= v) return false;
		a[i] = v;
		return true;
	}

	private static boolean chmin(final long[] a, final int i, final long v) {
		if (a[i] <= v) return false;
		a[i] = v;
		return true;
	}

	private static boolean chmax(final char[] a, final int i, final char v) {
		if (a[i] >= v) return false;
		a[i] = v;
		return true;
	}

	private static boolean chmax(final int[] a, final int i, final int v) {
		if (a[i] >= v) return false;
		a[i] = v;
		return true;
	}

	private static boolean chmax(final long[] a, final int i, final long v) {
		if (a[i] >= v) return false;
		a[i] = v;
		return true;
	}

	private static int min(int... a) {
		int len = a.length;
		int min = a[0];
		for (int i = 1; i < len; i++) if (min > a[i]) min = a[i];
		return min;
	}

	private static int max(int... a) {
		int len = a.length;
		int max = a[0];
		for (int i = 1; i < len; i++) if (max < a[i]) max = a[i];
		return max;
	}

	private static long min(long... a) {
		int len = a.length;
		long min = a[0];
		for (int i = 1; i < len; i++) if (min > a[i]) min = a[i];
		return min;
	}

	private static long max(long... a) {
		int len = a.length;
		long max = a[0];
		for (int i = 1; i < len; i++) if (max < a[i]) max = a[i];
		return max;
	}

	private static double min(double... a) {
		int len = a.length;
		double min = a[0];
		for (int i = 1; i < len; i++) if (min > a[i]) min = a[i];
		return min;
	}

	private static double max(double... a) {
		int len = a.length;
		double max = a[0];
		for (int i = 1; i < len; i++) if (max < a[i]) max = a[i];
		return max;
	}

	private static long lModPow(long a, long b, final long mod) {
		long ans = 1;
		for (a %= mod; b > 0; a = a * a % mod, b >>= 1) {
			if ((b & 1) == 1) ans = ans * a % mod;
		}
		return ans;
	}

	private static int iModPow(int a, int b, final int mod) {
		int ans = 1;
		for (a %= mod; b > 0; a = (int) ((long) a * a % mod), b >>= 1) {
			if ((b & 1) == 1) ans = (int) ((long) ans * a % mod);
		}
		return ans;
	}

	private static long floorLong(final long a, final long b) {
		return a < 0 ? (a - b + 1) / b : a / b;
	}

	private static int floorInt(final int a, final int b) {
		return a < 0 ? (a - b + 1) / b : a / b;
	}

	private static long ceilLong(final long a, final long b) {
		return a < 0 ? a / b : (a + b - 1) / b;
	}

	private static int ceilInt(final int a, final int b) {
		return a < 0 ? a / b : (a + b - 1) / b;
	}

	private static long lcmLong(final long x, final long y) {
		return x == 0 || y == 0 ? 0 : x / gcdLong(x, y) * y;
	}

	private static int lcmInt(final int x, final int y) {
		return x == 0 || y == 0 ? 0 : x / gcdInt(x, y) * y;
	}

	private static long gcdLong(long a, long b) {
		a = abs(a);
		b = abs(b);
		if (a == 0) return b;
		if (b == 0) return a;
		int commonShift = Long.numberOfTrailingZeros(a | b);
		a >>= Long.numberOfTrailingZeros(a);
		while (b != 0) {
			b >>= Long.numberOfTrailingZeros(b);
			if (a > b) {
				long tmp = a;
				a = b;
				b = tmp;
			}
			b -= a;
		}
		return a << commonShift;
	}

	private static int gcdInt(int a, int b) {
		a = abs(a);
		b = abs(b);
		if (a == 0) return b;
		if (b == 0) return a;
		int commonShift = Integer.numberOfTrailingZeros(a | b);
		a >>= Integer.numberOfTrailingZeros(a);
		while (b != 0) {
			b >>= Integer.numberOfTrailingZeros(b);
			if (a > b) {
				int tmp = a;
				a = b;
				b = tmp;
			}
			b -= a;
		}
		return a << commonShift;
	}
	// endregion

	// region < I/O & Debug >
	public static void main(final String[] args) {
		try {
			solve();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
			out.close();
		}
	}

	private static void debug(final Object... args) {
		if (DEBUG) {
			out.flush();
			System.err.println(deepToString(args));
		}
	}

	@SuppressWarnings("unused")
	private static final class FastScanner implements AutoCloseable {
		private static final int DEFAULT_BUFFER_SIZE = 1 << 20;
		private final InputStream in;
		private final byte[] buffer;
		private int pos = 0, bufferLength = 0;

		public FastScanner() {
			this(new FileInputStream(FileDescriptor.in), DEFAULT_BUFFER_SIZE);
		}

		public FastScanner(final InputStream in) {
			this(in, DEFAULT_BUFFER_SIZE);
		}

		public FastScanner(final int bufferSize) {
			this(new FileInputStream(FileDescriptor.in), bufferSize);
		}

		public FastScanner(final InputStream in, final int bufferSize) {
			this.in = in;
			this.buffer = new byte[bufferSize];
		}

		private int skipSpaces() {
			final byte[] buf = buffer;
			int p = pos, len = bufferLength, b;
			do {
				if (p >= len) {
					try {
						len = in.read(buf);
						p = 0;
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
					if (len <= 0) throw new NoSuchElementException();
				}
				b = buf[p++];
			} while (b <= 32);
			pos = p;
			bufferLength = len;
			return b;
		}

		@Override
		public void close() {
			try {
				in.close();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		private boolean hasNextByte() {
			if (pos < bufferLength) return true;
			pos = 0;
			try {
				bufferLength = in.read(buffer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return bufferLength > 0;
		}

		public boolean hasNext() {
			while (hasNextByte()) {
				if (buffer[pos] > 32) return true;
				pos++;
			}
			return false;
		}

		public char nextChar() {
			if (!hasNext()) throw new NoSuchElementException();
			return (char) buffer[pos++];
		}

		public int nextInt() {
			int b = skipSpaces();
			int n = 0;
			boolean negative = false;
			if (b == '-') {
				negative = true;
				if (pos == bufferLength) hasNextByte();
				b = buffer[pos++];
			}
			final byte[] buf = buffer;
			int p = pos, len = bufferLength;
			if (p + 11 <= len) {
				do {
					n = (n << 3) + (n << 1) + (b & 15);
					b = buf[p++];
				} while (b > 32);
			} else {
				do {
					n = (n << 3) + (n << 1) + (b & 15);
					if (p == len) {
						pos = p;
						if (!hasNextByte()) {
							p = pos;
							break;
						}
						p = pos;
						len = bufferLength;
					}
					b = buf[p++];
				} while (b > 32);
			}
			pos = p;
			return negative ? -n : n;
		}

		public long nextLong() {
			int b = skipSpaces();
			long n = 0;
			boolean negative = false;
			if (b == '-') {
				negative = true;
				if (pos == bufferLength) hasNextByte();
				b = buffer[pos++];
			}
			final byte[] buf = buffer;
			int p = pos, len = bufferLength;
			if (p + 20 <= len) {
				do {
					n = (n << 3) + (n << 1) + (b & 15);
					b = buf[p++];
				} while (b > 32);
			} else {
				do {
					n = (n << 3) + (n << 1) + (b & 15);
					if (p == len) {
						pos = p;
						if (!hasNextByte()) {
							p = pos;
							break;
						}
						p = pos;
						len = bufferLength;
					}
					b = buf[p++];
				} while (b > 32);
			}
			pos = p;
			return negative ? -n : n;
		}

		public double nextDouble() {
			int b = skipSpaces();
			boolean negative = false;
			if (b == '-') {
				negative = true;
				if (pos == bufferLength) hasNextByte();
				b = buffer[pos++];
			}
			long intPart = 0;
			final byte[] buf = buffer;
			int p = pos, len = bufferLength;
			if (p + 20 <= len) {
				do {
					intPart = (intPart << 3) + (intPart << 1) + (b & 15);
					b = buf[p++];
				} while ('0' <= b && b <= '9');
			} else {
				do {
					intPart = (intPart << 3) + (intPart << 1) + (b & 15);
					if (p == len) {
						pos = p;
						if (!hasNextByte()) {
							p = pos;
							b = -1;
							break;
						}
						p = pos;
						len = bufferLength;
					}
					b = buf[p++];
				} while ('0' <= b && b <= '9');
			}
			double result = intPart;
			if (b == '.') {
				if (p == len) {
					pos = p;
					hasNextByte();
					p = pos;
					len = bufferLength;
				}
				b = buf[p++];
				long fracPart = 0;
				long divisor = 1;
				if (p + 20 <= len) {
					do {
						fracPart = fracPart * 10 + (b & 15);
						divisor *= 10;
						b = buf[p++];
					} while ('0' <= b && b <= '9');
				} else {
					do {
						fracPart = fracPart * 10 + (b & 15);
						divisor *= 10;
						if (p == len) {
							pos = p;
							if (!hasNextByte()) {
								p = pos;
								break;
							}
							p = pos;
							len = bufferLength;
						}
						b = buf[p++];
					} while ('0' <= b && b <= '9');
				}
				result += (double) fracPart / divisor;
			}
			pos = p;
			return negative ? -result : result;
		}

		public String next() {
			return nextStringBuilder().toString();
		}

		public StringBuilder nextStringBuilder() {
			final StringBuilder sb = new StringBuilder();
			int b = skipSpaces(), p = pos, len = bufferLength;
			do {
				sb.append((char) b);
				if (p == len) {
					pos = p;
					if (!hasNextByte()) {
						p = pos;
						break;
					}
					p = pos;
					len = bufferLength;
				}
				b = buffer[p++];
			} while (b > 32);
			pos = p;
			return sb;
		}

		public String nextLine() {
			final StringBuilder sb = new StringBuilder();
			if (pos == bufferLength && !hasNextByte()) return "";
			final byte[] buf = buffer;
			int p = pos, len = bufferLength, b = buf[p];
			while (b != '\n' && b != '\r') {
				sb.append((char) b);
				p++;
				if (p == len) {
					pos = p;
					if (!hasNextByte()) {
						p = pos;
						b = -1;
						break;
					}
					p = pos;
					len = bufferLength;
				}
				b = buf[p];
			}
			if (b == '\n' || b == '\r') {
				p++;
				if (b == '\r') {
					if (p == len) {
						pos = p;
						hasNextByte();
						p = pos;
						len = bufferLength;
					}
					if (p < len && buf[p] == '\n') p++;
				}
			}
			pos = p;
			return sb.toString();
		}

		public BigInteger nextBigInteger() {
			return new BigInteger(next());
		}

		public BigDecimal nextBigDecimal() {
			return new BigDecimal(next());
		}

		public int[] nextInt(final int n) {
			final int[] a = new int[n];
			for (int i = 0; i < n; i++) a[i] = nextInt();
			return a;
		}

		public long[] nextLong(final int n) {
			final long[] a = new long[n];
			for (int i = 0; i < n; i++) a[i] = nextLong();
			return a;
		}

		public double[] nextDouble(final int n) {
			final double[] a = new double[n];
			for (int i = 0; i < n; i++) a[i] = nextDouble();
			return a;
		}

		public int nextInt0() {
			return nextInt() - 1;
		}

		public long nextLong0() {
			return nextLong() - 1;
		}

		public int[] nextInt0(final int n) {
			final int[] a = new int[n];
			for (int i = 0; i < n; i++) a[i] = nextInt() - 1;
			return a;
		}

		public long[] nextLong0(final int n) {
			final long[] a = new long[n];
			for (int i = 0; i < n; i++) a[i] = nextLong() - 1;
			return a;
		}

		public char[] nextChars() {
			return next().toCharArray();
		}

		public char[] nextChars(final int n) {
			final char[] c = new char[n];
			for (int i = 0; i < n; i++) c[i] = nextChar();
			return c;
		}

		public String[] nextStrings(final int n) {
			final String[] s = new String[n];
			for (int i = 0; i < n; i++) s[i] = next();
			return s;
		}

		public int[][] nextIntMat(final int h, final int w) {
			final int[][] a = new int[h][w];
			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++)
					a[i][j] = nextInt();
			return a;
		}

		public long[][] nextLongMat(final int h, final int w) {
			final long[][] a = new long[h][w];
			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++)
					a[i][j] = nextLong();
			return a;
		}

		public double[][] nextDoubleMat(final int h, final int w) {
			final double[][] a = new double[h][w];
			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++)
					a[i][j] = nextDouble();
			return a;
		}

		public char[][] nextCharMat(final int n) {
			final char[][] c = new char[n][];
			for (int i = 0; i < n; i++) c[i] = nextChars();
			return c;
		}

		public char[][] nextCharMat(final int h, final int w) {
			final char[][] c = new char[h][w];
			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++)
					c[i][j] = nextChar();
			return c;
		}

		public String[][] nextStringMat(final int h, final int w) {
			final String[][] s = new String[h][w];
			for (int i = 0; i < h; i++)
				for (int j = 0; j < w; j++)
					s[i][j] = next();
			return s;
		}

		public int[][][] nextInt3D(final int x, final int y, final int z) {
			final int[][][] a = new int[x][y][z];
			for (int i = 0; i < x; i++)
				for (int j = 0; j < y; j++)
					for (int k = 0; k < z; k++)
						a[i][j][k] = nextInt();
			return a;
		}

		public long[][][] nextLong3D(final int x, final int y, final int z) {
			final long[][][] a = new long[x][y][z];
			for (int i = 0; i < x; i++)
				for (int j = 0; j < y; j++)
					for (int k = 0; k < z; k++)
						a[i][j][k] = nextLong();
			return a;
		}

		public int[] nextSortedInt(final int n) {
			final int[] a = nextInt(n);
			sort(a);
			return a;
		}

		public long[] nextSortedLong(final int n) {
			final long[] a = nextLong(n);
			sort(a);
			return a;
		}

		public double[] nextSortedDouble(final int n) {
			final double[] a = nextDouble(n);
			sort(a);
			return a;
		}

		public char[] nextSortedChars() {
			final char[] c = nextChars();
			sort(c);
			return c;
		}

		public char[] nextSortedChars(final int n) {
			final char[] c = nextChars(n);
			sort(c);
			return c;
		}

		public String[] nextSortedStrings(final int n) {
			final String[] s = nextStrings(n);
			sort(s);
			return s;
		}

		public int[] nextIntPrefixSum(final int n) {
			final int[] ps = new int[n];
			ps[0] = nextInt();
			for (int i = 1; i < n; i++) ps[i] = nextInt() + ps[i - 1];
			return ps;
		}

		public long[] nextLongPrefixSum(final int n) {
			final long[] ps = new long[n];
			ps[0] = nextLong();
			for (int i = 1; i < n; i++) ps[i] = nextLong() + ps[i - 1];
			return ps;
		}

		public int[][] nextIntPrefixSum(final int h, final int w) {
			final int[][] ps = new int[h + 1][w + 1];
			for (int i = 1; i <= h; i++)
				for (int j = 1; j <= w; j++)
					ps[i][j] = nextInt() + ps[i - 1][j] + ps[i][j - 1] - ps[i - 1][j - 1];
			return ps;
		}

		public long[][] nextLongPrefixSum(final int h, final int w) {
			final long[][] ps = new long[h + 1][w + 1];
			for (int i = 1; i <= h; i++)
				for (int j = 1; j <= w; j++)
					ps[i][j] = nextLong() + ps[i - 1][j] + ps[i][j - 1] - ps[i - 1][j - 1];
			return ps;
		}

		public int[][][] nextIntPrefixSum(final int x, final int y, final int z) {
			final int[][][] ps = new int[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++)
					for (int c = 1; c <= z; c++)
						ps[a][b][c] = nextInt() + ps[a - 1][b][c] + ps[a][b - 1][c] + ps[a][b][c - 1] - ps[a - 1][b - 1][c]
								- ps[a - 1][b][c - 1] - ps[a][b - 1][c - 1] + ps[a - 1][b - 1][c - 1];
			return ps;
		}

		public long[][][] nextLongPrefixSum(final int x, final int y, final int z) {
			final long[][][] ps = new long[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++)
					for (int c = 1; c <= z; c++)
						ps[a][b][c] = nextLong() + ps[a - 1][b][c] + ps[a][b - 1][c] + ps[a][b][c - 1] - ps[a - 1][b - 1][c]
								- ps[a - 1][b][c - 1] - ps[a][b - 1][c - 1] + ps[a - 1][b - 1][c - 1];
			return ps;
		}

		public int[] nextIntInverseMapping(final int n) {
			final int[] inv = new int[n];
			for (int i = 0; i < n; i++) inv[nextInt() - 1] = i;
			return inv;
		}

		public ArrayList<Integer> nextIntAL(final int n) {
			return nextCollection(n, this::nextInt, () -> new ArrayList<>(n));
		}

		public HashSet<Integer> nextIntHS(final int n) {
			return nextCollection(n, this::nextInt, () -> new HashSet<>(n));
		}

		public TreeSet<Integer> nextIntTS(final int n) {
			return nextCollection(n, this::nextInt, TreeSet::new);
		}

		public ArrayList<Long> nextLongAL(final int n) {
			return nextCollection(n, this::nextLong, () -> new ArrayList<>(n));
		}

		public HashSet<Long> nextLongHS(final int n) {
			return nextCollection(n, this::nextLong, () -> new HashSet<>(n));
		}

		public TreeSet<Long> nextLongTS(final int n) {
			return nextCollection(n, this::nextLong, TreeSet::new);
		}

		public ArrayList<Character> nextCharacterAL(final int n) {
			return nextCollection(n, this::nextChar, () -> new ArrayList<>(n));
		}

		public HashSet<Character> nextCharacterHS(final int n) {
			return nextCollection(n, this::nextChar, () -> new HashSet<>(n));
		}

		public TreeSet<Character> nextCharacterTS(final int n) {
			return nextCollection(n, this::nextChar, TreeSet::new);
		}

		public ArrayList<String> nextStringAL(final int n) {
			return nextCollection(n, this::next, () -> new ArrayList<>(n));
		}

		public HashSet<String> nextStringHS(final int n) {
			return nextCollection(n, this::next, () -> new HashSet<>(n));
		}

		public TreeSet<String> nextStringTS(final int n) {
			return nextCollection(n, this::next, TreeSet::new);
		}

		private <S, T extends Collection<S>> T nextCollection(int n, final Supplier<S> input, final Supplier<T> collection) {
			final T t = collection.get();
			while (n-- > 0) t.add(input.get());
			return t;
		}

		public HashMap<Integer, Integer> nextIntMultisetHM(final int n) {
			return nextMultiset(n, this::nextInt, () -> new HashMap<>(n));
		}

		public TreeMap<Integer, Integer> nextIntMultisetTM(final int n) {
			return nextMultiset(n, this::nextInt, TreeMap::new);
		}

		public HashMap<Long, Integer> nextLongMultisetHM(final int n) {
			return nextMultiset(n, this::nextLong, () -> new HashMap<>(n));
		}

		public TreeMap<Long, Integer> nextLongMultisetTM(final int n) {
			return nextMultiset(n, this::nextLong, TreeMap::new);
		}

		public HashMap<Character, Integer> nextCharMultisetHM(final int n) {
			return nextMultiset(n, this::nextChar, () -> new HashMap<>(n));
		}

		public TreeMap<Character, Integer> nextCharMultisetTM(final int n) {
			return nextMultiset(n, this::nextChar, TreeMap::new);
		}

		public HashMap<String, Integer> nextStringMultisetHM(final int n) {
			return nextMultiset(n, this::next, () -> new HashMap<>(n));
		}

		public TreeMap<String, Integer> nextStringMultisetTM(final int n) {
			return nextMultiset(n, this::next, TreeMap::new);
		}

		private <S, T extends Map<S, Integer>> T nextMultiset(int n, final Supplier<S> input, final Supplier<T> map) {
			final T multiSet = map.get();
			while (n-- > 0) {
				final S i = input.get();
				multiSet.put(i, multiSet.getOrDefault(i, 0) + 1);
			}
			return multiSet;
		}

		public int[] nextIntMultiset(final int n, final int m) {
			final int[] multiset = new int[m];
			for (int i = 0; i < n; i++) multiset[nextInt() - 1]++;
			return multiset;
		}

		public int[] nextUpperCharMultiset(final int n) {
			return nextCharMultiset(n, 'A', 'Z');
		}

		public int[] nextLowerCharMultiset(final int n) {
			return nextCharMultiset(n, 'a', 'z');
		}

		public int[] nextCharMultiset(int n, final char l, final char r) {
			final int[] multiset = new int[r - l + 1];
			while (n-- > 0) {
				final int c = nextChar() - l;
				multiset[c]++;
			}
			return multiset;
		}
	}

	@SuppressWarnings("unused")
	private static final class FastPrinter implements AutoCloseable {
		private static final VarHandle BYTE_ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(byte[].class);
		private static final VarHandle SHORT_HANDLE = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN);
		private static final int MAX_INT_DIGITS = 11;
		private static final int MAX_LONG_DIGITS = 20;
		private static final int MAX_BOOL_DIGITS = 3;
		private static final int DEFAULT_BUFFER_SIZE = 1 << 20;
		private static final byte LINE = '\n';
		private static final byte SPACE = ' ';
		private static final byte HYPHEN = '-';
		private static final byte PERIOD = '.';
		private static final byte ZERO = '0';
		private static final byte[] TRUE_BYTES = {'Y', 'e', 's'};
		private static final byte[] FALSE_BYTES = {'N', 'o'};
		private static final short[] DIGIT_PAIRS = {
				12336, 12592, 12848, 13104, 13360, 13616, 13872, 14128, 14384, 14640,
				12337, 12593, 12849, 13105, 13361, 13617, 13873, 14129, 14385, 14641,
				12338, 12594, 12850, 13106, 13362, 13618, 13874, 14130, 14386, 14642,
				12339, 12595, 12851, 13107, 13363, 13619, 13875, 14131, 14387, 14643,
				12340, 12596, 12852, 13108, 13364, 13620, 13876, 14132, 14388, 14644,
				12341, 12597, 12853, 13109, 13365, 13621, 13877, 14133, 14389, 14645,
				12342, 12598, 12854, 13110, 13366, 13622, 13878, 14134, 14390, 14646,
				12343, 12599, 12855, 13111, 13367, 13623, 13879, 14135, 14391, 14647,
				12344, 12600, 12856, 13112, 13368, 13624, 13880, 14136, 14392, 14648,
				12345, 12601, 12857, 13113, 13369, 13625, 13881, 14137, 14393, 14649,
		};

		private static final long[] POW10 = {
				1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000,
				1_000_000_000, 10_000_000_000L, 100_000_000_000L, 1_000_000_000_000L,
				10_000_000_000_000L, 100_000_000_000_000L, 1_000_000_000_000_000L,
				10_000_000_000_000_000L, 100_000_000_000_000_000L, 1_000_000_000_000_000_000L
		};
		private final OutputStream out;
		private final boolean autoFlush;
		private byte[] buffer;
		private int pos;

		public FastPrinter() {
			this(new FileOutputStream(FileDescriptor.out), DEFAULT_BUFFER_SIZE, false);
		}

		public FastPrinter(final OutputStream out) {
			this(out, DEFAULT_BUFFER_SIZE, false);
		}

		public FastPrinter(final int bufferSize) {
			this(new FileOutputStream(FileDescriptor.out), bufferSize, false);
		}

		public FastPrinter(final boolean autoFlush) {
			this(new FileOutputStream(FileDescriptor.out), DEFAULT_BUFFER_SIZE, autoFlush);
		}

		public FastPrinter(final OutputStream out, final boolean autoFlush) {
			this(out, DEFAULT_BUFFER_SIZE, autoFlush);
		}

		public FastPrinter(final int bufferSize, final boolean autoFlush) {
			this(new FileOutputStream(FileDescriptor.out), bufferSize, autoFlush);
		}

		public FastPrinter(final OutputStream out, final int bufferSize) {
			this(out, bufferSize, false);
		}

		public FastPrinter(final OutputStream out, final int bufferSize, final boolean autoFlush) {
			this.out = out;
			this.buffer = new byte[max(64, roundUpToPowerOfTwo(bufferSize))];
			this.autoFlush = autoFlush;
		}

		private static int countDigits(final int i) {
			if (i > -100000) {
				if (i > -100) {
					return i > -10 ? 1 : 2;
				} else {
					if (i > -10000) return i > -1000 ? 3 : 4;
					else return 5;
				}
			} else {
				if (i > -10000000) {
					return i > -1000000 ? 6 : 7;
				} else {
					if (i > -1000000000) return i > -100000000 ? 8 : 9;
					else return 10;
				}
			}
		}

		private static int countDigits(final long l) {
			if (l > -1000000000) {
				if (l > -10000) {
					if (l > -100) {
						return l > -10 ? 1 : 2;
					} else {
						return l > -1000 ? 3 : 4;
					}
				} else {
					if (l > -1000000) {
						return l > -100000 ? 5 : 6;
					} else {
						if (l > -100000000) return l > -10000000 ? 7 : 8;
						else return 9;
					}
				}
			} else {
				if (l > -10000000000000L) {
					if (l > -100000000000L) {
						return l > -10000000000L ? 10 : 11;
					} else {
						return l > -1000000000000L ? 12 : 13;
					}
				} else {
					if (l > -10000000000000000L) {
						if (l > -1000000000000000L) return l > -100000000000000L ? 14 : 15;
						else return 16;
					} else {
						if (l > -1000000000000000000L) return l > -100000000000000000L ? 17 : 18;
						else return 19;
					}
				}
			}
		}

		private static int roundUpToPowerOfTwo(int x) {
			if (x <= 1) return 1;
			x--;
			x |= x >>> 1;
			x |= x >>> 2;
			x |= x >>> 4;
			x |= x >>> 8;
			x |= x >>> 16;
			return x + 1;
		}

		@Override
		public void close() {
			try {
				flush();
				out.close();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void flush() {
			if (pos == 0) return;
			try {
				out.write(buffer, 0, pos);
				pos = 0;
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		public FastPrinter println() {
			ensureCapacity(1);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final boolean b) {
			ensureCapacity(MAX_BOOL_DIGITS + 1);
			final int p = write(b, pos);
			BYTE_ARRAY_HANDLE.set(buffer, p, LINE);
			pos = p + 1;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final byte b) {
			ensureCapacity(2);
			final byte[] buf = buffer;
			final int p = pos;
			BYTE_ARRAY_HANDLE.set(buf, p, b);
			BYTE_ARRAY_HANDLE.set(buf, p + 1, LINE);
			pos = p + 2;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final char c) {
			ensureCapacity(2);
			final byte[] buf = buffer;
			final int p = pos;
			BYTE_ARRAY_HANDLE.set(buf, p, (byte) c);
			BYTE_ARRAY_HANDLE.set(buf, p + 1, LINE);
			pos = p + 2;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final int i) {
			ensureCapacity(MAX_INT_DIGITS + 1);
			final int p = write(i, pos);
			BYTE_ARRAY_HANDLE.set(buffer, p, LINE);
			pos = p + 1;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final long l) {
			ensureCapacity(MAX_LONG_DIGITS + 1);
			final int p = write(l, pos);
			BYTE_ARRAY_HANDLE.set(buffer, p, LINE);
			pos = p + 1;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final double d) {
			return println(Double.toString(d));
		}

		public FastPrinter println(final BigInteger bi) {
			return println(bi.toString());
		}

		public FastPrinter println(final BigDecimal bd) {
			return println(bd.toString());
		}

		public FastPrinter println(final String s) {
			ensureCapacity(s.length() + 1);
			final int p = write(s, pos);
			BYTE_ARRAY_HANDLE.set(buffer, p, LINE);
			pos = p + 1;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final StringBuilder s) {
			ensureCapacity(s.length() + 1);
			final int p = write(s.toString(), pos);
			BYTE_ARRAY_HANDLE.set(buffer, p, LINE);
			pos = p + 1;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final Object o) {
			return switch (o) {
				case null -> println();
				case Boolean b -> println(b.booleanValue());
				case Byte b -> println(b.byteValue());
				case Character c -> println(c.charValue());
				case Integer i -> println(i.intValue());
				case Long l -> println(l.longValue());
				case Double d -> println(d.toString());
				case BigInteger bi -> println(bi.toString());
				case BigDecimal bd -> println(bd.toString());
				case String s -> println(s);
				case boolean[] arr -> println(arr);
				case char[] arr -> println(arr);
				case int[] arr -> println(arr);
				case long[] arr -> println(arr);
				case double[] arr -> println(arr);
				case String[] arr -> println(arr);
				case Object[] arr -> println(arr);
				default -> println(o.toString());
			};
		}

		public FastPrinter print(final boolean b) {
			ensureCapacity(MAX_BOOL_DIGITS);
			pos = write(b, pos);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final byte b) {
			ensureCapacity(1);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, b);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final char c) {
			ensureCapacity(1);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) c);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final int i) {
			ensureCapacity(MAX_INT_DIGITS);
			pos = write(i, pos);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final long l) {
			ensureCapacity(MAX_LONG_DIGITS);
			pos = write(l, pos);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final double d) {
			return print(Double.toString(d));
		}

		public FastPrinter print(final BigInteger bi) {
			return print(bi.toString());
		}

		public FastPrinter print(final BigDecimal bd) {
			return print(bd.toString());
		}

		public FastPrinter print(final String s) {
			ensureCapacity(s.length());
			pos = write(s, pos);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final StringBuilder s) {
			ensureCapacity(s.length());
			pos = write(s.toString(), pos);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final Object o) {
			return switch (o) {
				case null -> this;
				case Boolean b -> print(b.booleanValue());
				case Byte b -> print(b.byteValue());
				case Character c -> print(c.charValue());
				case Integer i -> print(i.intValue());
				case Long l -> print(l.longValue());
				case Double d -> print(d.toString());
				case BigInteger bi -> print(bi.toString());
				case BigDecimal bd -> print(bd.toString());
				case String s -> print(s);
				case boolean[] arr -> print(arr);
				case char[] arr -> print(arr);
				case int[] arr -> print(arr);
				case long[] arr -> print(arr);
				case double[] arr -> print(arr);
				case String[] arr -> print(arr);
				case Object[] arr -> print(arr);
				default -> print(o.toString());
			};
		}

		public FastPrinter printf(final String format, final Object... args) {
			return print(String.format(format, args));
		}

		public FastPrinter printf(final Locale locale, final String format, final Object... args) {
			return print(String.format(locale, format, args));
		}

		private void ensureCapacity(final int additional) {
			final int required = pos + additional;
			final int bufferLength = buffer.length;
			if (required <= bufferLength) return;
			flush();
			if (additional > bufferLength) buffer = new byte[roundUpToPowerOfTwo(additional)];
		}

		private int write(final boolean b, int p) {
			final byte[] src = b ? TRUE_BYTES : FALSE_BYTES;
			final int len = src.length;
			System.arraycopy(src, 0, buffer, p, len);
			return p + len;
		}

		private int write(int i, int p) {
			final byte[] buf = buffer;
			if (i >= 0) i = -i;
			else BYTE_ARRAY_HANDLE.set(buf, p++, HYPHEN);
			final int digits = countDigits(i);
			int writePos = p + digits;
			while (i <= -100) {
				final int q = i / 100;
				final int r = (q << 6) + (q << 5) + (q << 2) - i;
				SHORT_HANDLE.set(buf, writePos - 2, DIGIT_PAIRS[r]);
				writePos -= 2;
				i = q;
			}
			final int r = -i;
			if (r >= 10) SHORT_HANDLE.set(buf, writePos - 2, DIGIT_PAIRS[r]);
			else BYTE_ARRAY_HANDLE.set(buf, writePos - 1, (byte) (r + ZERO));
			return p + digits;
		}

		private int write(long l, int p) {
			final byte[] buf = buffer;
			if (l >= 0) l = -l;
			else BYTE_ARRAY_HANDLE.set(buf, p++, HYPHEN);
			final int digits = countDigits(l);
			int writePos = p + digits;
			while (l <= -100) {
				final long q = l / 100;
				final int r = (int) ((q << 6) + (q << 5) + (q << 2) - l);
				SHORT_HANDLE.set(buf, writePos - 2, DIGIT_PAIRS[r]);
				writePos -= 2;
				l = q;
			}
			final int r = (int) -l;
			if (r >= 10) SHORT_HANDLE.set(buf, writePos - 2, DIGIT_PAIRS[r]);
			else BYTE_ARRAY_HANDLE.set(buf, writePos - 1, (byte) (r + ZERO));
			return p + digits;
		}

		private int write(final String s, int p) {
			final int len = s.length();
			final byte[] buf = buffer;
			int i = 0;
			final int limit = len & ~7;
			while (i < limit) {
				BYTE_ARRAY_HANDLE.set(buf, p, (byte) s.charAt(i));
				BYTE_ARRAY_HANDLE.set(buf, p + 1, (byte) s.charAt(i + 1));
				BYTE_ARRAY_HANDLE.set(buf, p + 2, (byte) s.charAt(i + 2));
				BYTE_ARRAY_HANDLE.set(buf, p + 3, (byte) s.charAt(i + 3));
				BYTE_ARRAY_HANDLE.set(buf, p + 4, (byte) s.charAt(i + 4));
				BYTE_ARRAY_HANDLE.set(buf, p + 5, (byte) s.charAt(i + 5));
				BYTE_ARRAY_HANDLE.set(buf, p + 6, (byte) s.charAt(i + 6));
				BYTE_ARRAY_HANDLE.set(buf, p + 7, (byte) s.charAt(i + 7));
				p += 8;
				i += 8;
			}
			while (i < len) BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
			return p;
		}

		public FastPrinter println(final int a, final int b) {
			return println(a, b, '\n');
		}

		public FastPrinter println(final int a, final long b) {
			return println(a, b, '\n');
		}

		public FastPrinter println(final long a, final int b) {
			return println(a, b, '\n');
		}

		public FastPrinter println(final long a, final long b) {
			return println(a, b, '\n');
		}

		public FastPrinter println(final long a, final long b, final char delimiter) {
			ensureCapacity((MAX_LONG_DIGITS << 1) + 2);
			final byte[] buf = buffer;
			int p = pos;
			p = write(a, p);
			BYTE_ARRAY_HANDLE.set(buf, p, (byte) delimiter);
			p = write(b, p + 1);
			BYTE_ARRAY_HANDLE.set(buf, p, LINE);
			pos = p + 1;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final int a, final int b) {
			return print(a, b, ' ');
		}

		public FastPrinter print(final int a, final long b) {
			return print(a, b, ' ');
		}

		public FastPrinter print(final long a, final int b) {
			return print(a, b, ' ');
		}

		public FastPrinter print(final long a, final long b) {
			return print(a, b, ' ');
		}

		public FastPrinter print(final long a, final long b, final char delimiter) {
			ensureCapacity((MAX_LONG_DIGITS << 1) + 1);
			final byte[] buf = buffer;
			int p = pos;
			p = write(a, p);
			BYTE_ARRAY_HANDLE.set(buf, p, (byte) delimiter);
			pos = write(b, p + 1);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final double d, final int n) {
			return print(d, n).println();
		}

		public FastPrinter print(double d, int n) {
			if (n <= 0) return print(round(d));
			ensureCapacity(MAX_LONG_DIGITS + n + 2);
			final byte[] buf = buffer;
			int p = pos;
			if (d < 0) {
				BYTE_ARRAY_HANDLE.set(buf, p++, HYPHEN);
				d = -d;
			}
			if (n > 18) n = 18;
			final long intPart = (long) d;
			final long fracPart = (long) ((d - intPart) * POW10[n]);
			p = write(intPart, p);
			BYTE_ARRAY_HANDLE.set(buf, p++, PERIOD);
			int leadingZeros = n - countDigits(-fracPart);
			while (leadingZeros-- > 0) BYTE_ARRAY_HANDLE.set(buf, p++, ZERO);
			pos = write(fracPart, p);
			return this;
		}

		public FastPrinter println(final boolean[] arr) {
			return print(arr, 0, arr.length, '\n').println();
		}

		public FastPrinter println(final char[] arr) {
			return print(arr, 0, arr.length, '\n').println();
		}

		public FastPrinter println(final int[] arr) {
			return print(arr, 0, arr.length, '\n').println();
		}

		public FastPrinter println(final long[] arr) {
			return print(arr, 0, arr.length, '\n').println();
		}

		public FastPrinter println(final double[] arr) {
			return print(arr, 0, arr.length, '\n').println();
		}

		public FastPrinter println(final String[] arr) {
			return print(arr, 0, arr.length, '\n').println();
		}

		public FastPrinter println(final Object... arr) {
			for (final Object o : arr) println(o);
			return this;
		}

		public FastPrinter println(final boolean[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter).println();
		}

		public FastPrinter println(final char[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter).println();
		}

		public FastPrinter println(final int[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter).println();
		}

		public FastPrinter println(final long[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter).println();
		}

		public FastPrinter println(final double[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter).println();
		}

		public FastPrinter println(final String[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter).println();
		}

		public FastPrinter println(final boolean[] arr, final int from, final int to) {
			return print(arr, from, to, '\n').println();
		}

		public FastPrinter println(final char[] arr, final int from, final int to) {
			return print(arr, from, to, '\n').println();
		}

		public FastPrinter println(final int[] arr, final int from, final int to) {
			return print(arr, from, to, '\n').println();
		}

		public FastPrinter println(final long[] arr, final int from, final int to) {
			return print(arr, from, to, '\n').println();
		}

		public FastPrinter println(final double[] arr, final int from, final int to) {
			return print(arr, from, to, '\n').println();
		}

		public FastPrinter println(final String[] arr, final int from, final int to) {
			return print(arr, from, to, '\n').println();
		}

		public FastPrinter println(final boolean[] arr, final int from, final int to, final char delimiter) {
			return print(arr, from, to, delimiter).println();
		}

		public FastPrinter println(final char[] arr, final int from, final int to, final char delimiter) {
			return print(arr, from, to, delimiter).println();
		}

		public FastPrinter println(final int[] arr, final int from, final int to, final char delimiter) {
			return print(arr, from, to, delimiter).println();
		}

		public FastPrinter println(final long[] arr, final int from, final int to, final char delimiter) {
			return print(arr, from, to, delimiter).println();
		}

		public FastPrinter println(final double[] arr, final int from, final int to, final char delimiter) {
			return print(arr, from, to, delimiter).println();
		}

		public FastPrinter println(final String[] arr, final int from, final int to, final char delimiter) {
			return print(arr, from, to, delimiter).println();
		}

		public FastPrinter print(final boolean[] arr) {
			return print(arr, 0, arr.length, ' ');
		}

		public FastPrinter print(final char[] arr) {
			return print(arr, 0, arr.length, ' ');
		}

		public FastPrinter print(final int[] arr) {
			return print(arr, 0, arr.length, ' ');
		}

		public FastPrinter print(final long[] arr) {
			return print(arr, 0, arr.length, ' ');
		}

		public FastPrinter print(final double[] arr) {
			return print(arr, 0, arr.length, ' ');
		}

		public FastPrinter print(final String[] arr) {
			return print(arr, 0, arr.length, ' ');
		}

		public FastPrinter print(final Object... arr) {
			final int len = arr.length;
			if (len > 0) print(arr[0]);
			for (int i = 1; i < len; i++) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
				print(arr[i]);
			}
			return this;
		}

		public FastPrinter print(final boolean[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter);
		}

		public FastPrinter print(final char[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter);
		}

		public FastPrinter print(final int[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter);
		}

		public FastPrinter print(final long[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter);
		}

		public FastPrinter print(final double[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter);
		}

		public FastPrinter print(final String[] arr, final char delimiter) {
			return print(arr, 0, arr.length, delimiter);
		}

		public FastPrinter print(final boolean[] arr, final int from, final int to) {
			return print(arr, from, to, ' ');
		}

		public FastPrinter print(final char[] arr, final int from, final int to) {
			return print(arr, from, to, ' ');
		}

		public FastPrinter print(final int[] arr, final int from, final int to) {
			return print(arr, from, to, ' ');
		}

		public FastPrinter print(final long[] arr, final int from, final int to) {
			return print(arr, from, to, ' ');
		}

		public FastPrinter print(final double[] arr, final int from, final int to) {
			return print(arr, from, to, ' ');
		}

		public FastPrinter print(final String[] arr, final int from, final int to) {
			return print(arr, from, to, ' ');
		}

		public FastPrinter print(final boolean[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			ensureCapacity((to - from) * (MAX_BOOL_DIGITS + 1));
			final byte[] buf = buffer;
			int p = pos;
			p = write(arr[from], p);
			final byte d = (byte) delimiter;
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buf, p, d);
				p = write(arr[i], p + 1);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final char[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			ensureCapacity(((to - from) << 1) - 1);
			final byte[] buf = buffer;
			int p = pos;
			BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[from]);
			final byte d = (byte) delimiter;
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buf, p, d);
				BYTE_ARRAY_HANDLE.set(buf, p + 1, (byte) arr[i]);
				p += 2;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final int[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			ensureCapacity((to - from) * (MAX_INT_DIGITS + 1));
			final byte[] buf = buffer;
			int p = pos;
			p = write(arr[from], p);
			final byte d = (byte) delimiter;
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buf, p, d);
				p = write(arr[i], p + 1);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final long[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			ensureCapacity((to - from) * (MAX_LONG_DIGITS + 1));
			final byte[] buf = buffer;
			int p = pos;
			p = write(arr[from], p);
			final byte d = (byte) delimiter;
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buf, p, d);
				p = write(arr[i], p + 1);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final double[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			print(arr[from]);
			final byte d = (byte) delimiter;
			for (int i = from + 1; i < to; i++) {
				print(d);
				print(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final String[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			int totalLen = 0;
			for (int i = from; i < to; i++) totalLen += arr[i].length();
			ensureCapacity(totalLen + (to - from - 1));

			final byte[] buf = buffer;
			int p = pos;
			p = write(arr[from], p);
			final byte d = (byte) delimiter;
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buf, p, d);
				p = write(arr[i], p + 1);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public <T> FastPrinter println(final boolean[] arr, final Function<Boolean, T> function) {
			for (final boolean b : arr) println(function.apply(b));
			return this;
		}

		public <T> FastPrinter println(final char[] arr, final IntFunction<T> function) {
			for (final char c : arr) println(function.apply(c));
			return this;
		}

		public <T> FastPrinter println(final int[] arr, final IntFunction<T> function) {
			for (final int i : arr) println(function.apply(i));
			return this;
		}

		public <T> FastPrinter println(final long[] arr, final LongFunction<T> function) {
			for (final long l : arr) println(function.apply(l));
			return this;
		}

		public <T> FastPrinter println(final double[] arr, final DoubleFunction<T> function) {
			for (final double l : arr) println(function.apply(l));
			return this;
		}

		public <T> FastPrinter println(final String[] arr, final Function<String, T> function) {
			for (final String s : arr) println(function.apply(s));
			return this;
		}

		public <T> FastPrinter print(final boolean[] arr, final Function<Boolean, T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final char[] arr, final IntFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final int[] arr, final IntFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final long[] arr, final LongFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final double[] arr, final DoubleFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final String[] arr, final Function<String, T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public FastPrinter println(final boolean[][] arr2d) {
			return println(arr2d, ' ');
		}

		public FastPrinter println(final char[][] arr2d) {
			return println(arr2d, ' ');
		}

		public FastPrinter println(final int[][] arr2d) {
			return println(arr2d, ' ');
		}

		public FastPrinter println(final long[][] arr2d) {
			return println(arr2d, ' ');
		}

		public FastPrinter println(final double[][] arr2d) {
			return println(arr2d, ' ');
		}

		public FastPrinter println(final String[][] arr2d) {
			return println(arr2d, ' ');
		}

		public FastPrinter println(final Object[][] arr2d) {
			return println(arr2d, ' ');
		}

		public FastPrinter println(final boolean[][] arr2d, final char delimiter) {
			for (final boolean[] arr : arr2d) println(arr, delimiter);
			return this;
		}

		public FastPrinter println(final char[][] arr2d, final char delimiter) {
			for (final char[] arr : arr2d) println(arr, delimiter);
			return this;
		}

		public FastPrinter println(final int[][] arr2d, final char delimiter) {
			for (final int[] arr : arr2d) println(arr, delimiter);
			return this;
		}

		public FastPrinter println(final long[][] arr2d, final char delimiter) {
			for (final long[] arr : arr2d) println(arr, delimiter);
			return this;
		}

		public FastPrinter println(final double[][] arr2d, final char delimiter) {
			for (final double[] arr : arr2d) println(arr, delimiter);
			return this;
		}

		public FastPrinter println(final String[][] arr2d, final char delimiter) {
			for (final String[] arr : arr2d) println(arr, delimiter);
			return this;
		}

		public FastPrinter println(final Object[][] arr2d, final char delimiter) {
			for (final Object[] arr : arr2d) {
				final int len = arr.length;
				if (len > 0) print(arr[0]);
				for (int i = 1; i < len; i++) {
					print(delimiter);
					print(arr[i]);
				}
				println();
			}
			return this;
		}

		public <T> FastPrinter println(final boolean[][] arr2d, final Function<Boolean, T> function) {
			for (final boolean[] arr : arr2d) print(arr, function).println();
			return this;
		}

		public <T> FastPrinter println(final char[][] arr2d, final IntFunction<T> function) {
			for (final char[] arr : arr2d) print(arr, function).println();
			return this;
		}

		public <T> FastPrinter println(final int[][] arr2d, final IntFunction<T> function) {
			for (final int[] arr : arr2d) print(arr, function).println();
			return this;
		}

		public <T> FastPrinter println(final long[][] arr2d, final LongFunction<T> function) {
			for (final long[] arr : arr2d) print(arr, function).println();
			return this;
		}

		public <T> FastPrinter println(final double[][] arr2d, final DoubleFunction<T> function) {
			for (final double[] arr : arr2d) print(arr, function).println();
			return this;
		}

		public <T> FastPrinter println(final String[][] arr2d, final Function<String, T> function) {
			for (final String[] arr : arr2d) print(arr, function).println();
			return this;
		}

		public FastPrinter printChars(final char[] arr) {
			return printChars(arr, 0, arr.length);
		}

		public FastPrinter printChars(final char[] arr, final int from, final int to) {
			final int len = to - from;
			ensureCapacity(len);
			final byte[] buf = buffer;
			int p = pos, i = from;
			final int limit8 = from + (len & ~7);
			while (i < limit8) {
				BYTE_ARRAY_HANDLE.set(buf, p, (byte) arr[i]);
				BYTE_ARRAY_HANDLE.set(buf, p + 1, (byte) arr[i + 1]);
				BYTE_ARRAY_HANDLE.set(buf, p + 2, (byte) arr[i + 2]);
				BYTE_ARRAY_HANDLE.set(buf, p + 3, (byte) arr[i + 3]);
				BYTE_ARRAY_HANDLE.set(buf, p + 4, (byte) arr[i + 4]);
				BYTE_ARRAY_HANDLE.set(buf, p + 5, (byte) arr[i + 5]);
				BYTE_ARRAY_HANDLE.set(buf, p + 6, (byte) arr[i + 6]);
				BYTE_ARRAY_HANDLE.set(buf, p + 7, (byte) arr[i + 7]);
				p += 8;
				i += 8;
			}
			while (i < to) BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printChars(final char[] arr, final IntUnaryOperator function) {
			final int len = arr.length;
			ensureCapacity(len);
			final byte[] buf = buffer;
			int p = pos, i = 0;
			final int limit = len & ~7;
			while (i < limit) {
				BYTE_ARRAY_HANDLE.set(buf, p, (byte) function.applyAsInt(arr[i]));
				BYTE_ARRAY_HANDLE.set(buf, p + 1, (byte) function.applyAsInt(arr[i + 1]));
				BYTE_ARRAY_HANDLE.set(buf, p + 2, (byte) function.applyAsInt(arr[i + 2]));
				BYTE_ARRAY_HANDLE.set(buf, p + 3, (byte) function.applyAsInt(arr[i + 3]));
				BYTE_ARRAY_HANDLE.set(buf, p + 4, (byte) function.applyAsInt(arr[i + 4]));
				BYTE_ARRAY_HANDLE.set(buf, p + 5, (byte) function.applyAsInt(arr[i + 5]));
				BYTE_ARRAY_HANDLE.set(buf, p + 6, (byte) function.applyAsInt(arr[i + 6]));
				BYTE_ARRAY_HANDLE.set(buf, p + 7, (byte) function.applyAsInt(arr[i + 7]));
				p += 8;
				i += 8;
			}
			while (i < len) BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printChars(final char[][] arr2d) {
			for (final char[] arr : arr2d) printChars(arr).println();
			return this;
		}

		public FastPrinter printChars(final char[][] arr2d, final IntUnaryOperator function) {
			for (final char[] arr : arr2d) printChars(arr, function).println();
			return this;
		}

		public <T> FastPrinter println(final Iterable<T> iter) {
			return print(iter, '\n').println();
		}

		public <T> FastPrinter println(final Iterable<T> iter, final char delimiter) {
			return print(iter, delimiter).println();
		}

		public <T> FastPrinter print(final Iterable<T> iter) {
			return print(iter, ' ');
		}

		public <T> FastPrinter print(final Iterable<T> iter, final char delimiter) {
			final Iterator<T> it = iter.iterator();
			if (it.hasNext()) print(it.next());
			while (it.hasNext()) {
				print(delimiter);
				print(it.next());
			}
			return this;
		}

		public <T, U> FastPrinter println(final Iterable<T> iter, final Function<T, U> function) {
			return print(iter, function, '\n').println();
		}

		public <T, U> FastPrinter println(final Iterable<T> iter, final Function<T, U> function, final char delimiter) {
			return print(iter, function, delimiter).println();
		}

		public <T, U> FastPrinter print(final Iterable<T> iter, final Function<T, U> function) {
			return print(iter, function, ' ');
		}

		public <T, U> FastPrinter print(final Iterable<T> iter, final Function<T, U> function, final char delimiter) {
			final Iterator<T> it = iter.iterator();
			if (it.hasNext()) print(function.apply(it.next()));
			while (it.hasNext()) {
				print(delimiter);
				print(function.apply(it.next()));
			}
			return this;
		}

		public FastPrinter printRepeat(final char c, final int cnt) {
			if (cnt <= 0) return this;
			ensureCapacity(cnt);
			final byte[] buf = buffer;
			final int p = pos;
			fill(buf, p, p + cnt, (byte) c);
			pos = p + cnt;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printRepeat(final String s, final int cnt) {
			if (cnt <= 0) return this;
			final int len = s.length();
			if (len == 0) return this;
			final int total = len * cnt;
			ensureCapacity(total);
			final byte[] buf = buffer;
			final int origPos = pos;
			int p = write(s, origPos);
			int copied = 1;
			while (copied << 1 <= cnt) {
				System.arraycopy(buf, origPos, buf, p, copied * len);
				p += copied * len;
				copied <<= 1;
			}
			final int remain = cnt - copied;
			if (remain > 0) {
				System.arraycopy(buf, origPos, buf, p, remain * len);
				p += remain * len;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnRepeat(final char c, final int cnt) {
			if (cnt <= 0) return this;
			final int total = cnt << 1;
			ensureCapacity(total);
			final byte[] buf = buffer;
			final byte b = (byte) c;
			final int origPos = pos;
			BYTE_ARRAY_HANDLE.set(buf, origPos, b);
			BYTE_ARRAY_HANDLE.set(buf, origPos + 1, LINE);
			int p = origPos + 2;
			int copied = 2;
			while (copied << 1 <= total) {
				System.arraycopy(buf, origPos, buf, p, copied);
				p += copied;
				copied <<= 1;
			}
			final int remain = total - copied;
			if (remain > 0) {
				System.arraycopy(buf, origPos, buf, p, remain);
				p += remain;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnRepeat(final String s, final int cnt) {
			if (cnt <= 0) return this;
			final int len = s.length();
			if (len == 0) return this;
			final int unitLen = len + 1;
			ensureCapacity(unitLen * cnt);
			final byte[] buf = buffer;
			final int origPos = pos;
			int p = write(s, origPos);
			BYTE_ARRAY_HANDLE.set(buf, p++, LINE);
			int copied = 1;
			while (copied << 1 <= cnt) {
				System.arraycopy(buf, origPos, buf, p, copied * unitLen);
				p += copied * unitLen;
				copied <<= 1;
			}
			final int remain = cnt - copied;
			if (remain > 0) {
				System.arraycopy(buf, origPos, buf, p, remain * unitLen);
				p += remain * unitLen;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final boolean[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_BOOL_DIGITS + 1));
			final byte[] buf = buffer;
			int p = pos;
			for (int i = len - 1; i >= 0; i--) {
				p = write(arr[i], p);
				BYTE_ARRAY_HANDLE.set(buf, p++, LINE);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final char[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len << 1);
			final byte[] buf = buffer;
			int p = pos;
			for (int i = len - 1; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, p, (byte) arr[i]);
				BYTE_ARRAY_HANDLE.set(buf, p + 1, LINE);
				p += 2;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final int[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_INT_DIGITS + 1));
			final byte[] buf = buffer;
			int p = pos;
			for (int i = len - 1; i >= 0; i--) {
				p = write(arr[i], p);
				BYTE_ARRAY_HANDLE.set(buf, p++, LINE);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final long[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_LONG_DIGITS + 1));
			final byte[] buf = buffer;
			int p = pos;
			for (int i = len - 1; i >= 0; i--) {
				p = write(arr[i], p);
				BYTE_ARRAY_HANDLE.set(buf, p++, LINE);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final double[] arr) {
			final int len = arr.length;
			for (int i = len - 1; i >= 0; i--) {
				final String s = Double.toString(arr[i]);
				ensureCapacity(s.length() + 1);
				pos = write(s, pos);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final String[] arr) {
			final int len = arr.length;
			for (int i = len - 1; i >= 0; i--) {
				final String s = arr[i];
				ensureCapacity(s.length() + 1);
				pos = write(s, pos);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final Object[] arr) {
			final int len = arr.length;
			for (int i = len - 1; i >= 0; i--) println(arr[i]);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final boolean[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_BOOL_DIGITS + 1) - 1);
			final byte[] buf = buffer;
			int p = pos;
			p = write(arr[len - 1], p);
			for (int i = len - 2; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, p, SPACE);
				p = write(arr[i], p + 1);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final char[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity((len << 1) - 1);
			final byte[] buf = buffer;
			int p = pos;
			BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, p, SPACE);
				BYTE_ARRAY_HANDLE.set(buf, p + 1, (byte) arr[i]);
				p += 2;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final int[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_INT_DIGITS + 1) - 1);
			final byte[] buf = buffer;
			int p = pos;
			p = write(arr[len - 1], p);
			for (int i = len - 2; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, p, SPACE);
				p = write(arr[i], p + 1);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final long[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_LONG_DIGITS + 1) - 1);
			final byte[] buf = buffer;
			int p = pos;
			p = write(arr[len - 1], p);
			for (int i = len - 2; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, p, SPACE);
				p = write(arr[i], p + 1);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final double[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			final String s0 = Double.toString(arr[len - 1]);
			ensureCapacity(s0.length());
			pos = write(s0, pos);
			for (int i = len - 2; i >= 0; i--) {
				final String s = Double.toString(arr[i]);
				ensureCapacity(s.length() + 1);
				BYTE_ARRAY_HANDLE.set(buffer, pos, SPACE);
				pos = write(s, pos + 1);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final String[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(arr[len - 1].length());
			pos = write(arr[len - 1], pos);
			for (int i = len - 2; i >= 0; i--) {
				ensureCapacity(arr[i].length() + 1);
				BYTE_ARRAY_HANDLE.set(buffer, pos, SPACE);
				pos = write(arr[i], pos + 1);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final Object[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			print(arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
				print(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}
	}
	// endregion
}
