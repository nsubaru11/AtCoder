import java.io.*;
import java.math.*;
import java.util.*;
import java.util.ArrayList;
import java.util.function.*;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public class C2 {

	private static void solve(final FastScanner sc, final FastPrinter out) {
		long[][] ans = {
				{0L, 1L, 3L, 5L, 7L, 9L, 33L, 99L, 313L, 585L, 717L, 7447L, 9009L, 15351L, 32223L, 39993L, 53235L, 53835L, 73737L, 585585L, 1758571L, 1934391L, 1979791L, 3129213L, 5071705L, 5259525L, 5841485L, 13500531L, 719848917L, 910373019L, 939474939L, 1290880921L, 7451111547L, 10050905001L, 18462126481L, 32479297423L, 75015151057L, 110948849011L, 136525525631L},
				{0L, 1L, 2L, 4L, 8L, 121L, 151L, 212L, 242L, 484L, 656L, 757L, 29092L, 48884L, 74647L, 75457L, 76267L, 92929L, 93739L, 848848L, 1521251L, 2985892L, 4022204L, 4219124L, 4251524L, 4287824L, 5737375L, 7875787L, 7949497L, 27711772L, 83155138L, 112969211L, 123464321L, 211131112L, 239060932L, 387505783L, 520080025L, 885626588L, 2518338152L, 58049094085L, 81234543218L},
				{0L, 1L, 2L, 3L, 5L, 55L, 373L, 393L, 666L, 787L, 939L, 7997L, 53235L, 55255L, 55655L, 57675L, 506605L, 1801081L, 2215122L, 3826283L, 3866683L, 5051505L, 5226225L, 5259525L, 5297925L, 5614165L, 5679765L, 53822835L, 623010326L, 954656459L, 51717171715L, 53406060435L, 59201610295L, 73979697937L, 506802208605L, 508152251805L},
				{0L, 1L, 2L, 3L, 4L, 6L, 88L, 252L, 282L, 626L, 676L, 1221L, 15751L, 18881L, 10088001L, 10400401L, 27711772L, 30322303L, 47633674L, 65977956L, 808656808L, 831333138L, 831868138L, 836131638L, 836181638L, 2512882152L, 2596886952L, 2893553982L, 6761551676L, 12114741121L, 12185058121L},
				{0L, 1L, 2L, 3L, 4L, 5L, 7L, 55L, 111L, 141L, 191L, 343L, 434L, 777L, 868L, 1441L, 7667L, 7777L, 22022L, 39893L, 74647L, 168861L, 808808L, 909909L, 1867681L, 3097903L, 4232324L, 4265624L, 4298924L, 4516154L, 4565654L, 4598954L, 4849484L, 5100015L, 5182815L, 5400045L, 5433345L, 5482845L, 5733375L, 5766675L, 5799975L, 6901096L, 6934396L, 6983896L, 8164618L, 9081809L, 15266251L, 24466442L, 103656301L, 104888401L, 108151801L, 290222092L, 310393013L, 342050243L, 3733113373L, 4368778634L, 7111881117L, 7786556877L, 8801331088L, 11271517211L, 12482428421L, 18013531081L, 61662426616L, 71771717717L, 75535653557L},
				{0L, 1L, 2L, 3L, 4L, 5L, 6L, 8L, 121L, 171L, 242L, 292L, 16561L, 65656L, 2137312L, 4602064L, 6597956L, 6958596L, 9470749L, 61255216L, 230474032L, 466828664L, 485494584L, 638828836L, 657494756L, 858474858L, 25699499652L, 40130703104L, 45862226854L, 61454945416L, 64454545446L, 65796069756L, 75016161057L, 75431213457L, 90750705709L, 91023932019L, 95365056359L, 426970079624L, 775350053577L},
				{0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 9L, 121L, 292L, 333L, 373L, 414L, 585L, 3663L, 8778L, 13131L, 13331L, 26462L, 26662L, 30103L, 30303L, 207702L, 628826L, 660066L, 1496941L, 1935391L, 1970791L, 4198914L, 55366355L, 130535031L, 532898235L, 719848917L, 799535997L, 1820330281L, 2464554642L, 4424994244L, 4480880844L, 4637337364L, 20855555802L, 94029892049L, 94466666449L, 294378873492L, 390894498093L},
				{0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 191L, 282L, 373L, 464L, 555L, 646L, 656L, 6886L, 25752L, 27472L, 42324L, 50605L, 626626L, 1540451L, 1713171L, 1721271L, 1828281L, 1877781L, 1885881L, 2401042L, 2434342L, 2442442L, 2450542L, 3106013L, 3114113L, 3122213L, 3163613L, 3171713L, 3303033L, 3360633L, 65666656L, 167191761L, 181434181L, 232000232L, 382000283L, 5435665345L, 8901111098L, 9565335659L, 827362263728L}
		};
		int a = sc.nextInt() - 2;
		long n = sc.nextLong();
		int index = Arrays.binarySearch(ans[a], n);
		if (index < 0) {
			index = ~index;
		} else {
			index++;
		}
		long sum = 0;
		for (int i = 0; i < index; i++) {
			sum += ans[a][i];
		}
		out.println(sum);
	}

	public static void main(String[] args) {
		try (final FastScanner sc = new FastScanner();
		     final FastPrinter out = new FastPrinter()) {
			solve(sc, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unused")
	private static final class FastScanner implements AutoCloseable {
		private static final int DEFAULT_BUFFER_SIZE = 65536;
		private final InputStream in;
		private final byte[] buffer;
		private int pos = 0, bufferLength = 0;

		public FastScanner() {
			this(System.in, DEFAULT_BUFFER_SIZE);
		}

		public FastScanner(final InputStream in) {
			this(in, DEFAULT_BUFFER_SIZE);
		}

		public FastScanner(final int bufferSize) {
			this(System.in, bufferSize);
		}

		public FastScanner(final InputStream in, final int bufferSize) {
			this.in = in;
			this.buffer = new byte[bufferSize];
		}

		private static boolean isWhitespace(final int c) {
			return c == ' ' || c == '\n' || c == '\r' || c == '\t';
		}

		@Override
		public void close() throws IOException {
			if (in != System.in)
				in.close();
		}

		public byte read() {
			if (pos >= bufferLength) {
				try {
					bufferLength = in.read(buffer, pos = 0, buffer.length);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (bufferLength < 0)
					throw new RuntimeException(new IOException("End of input reached"));
			}
			return buffer[pos++];
		}

		public int nextInt() {
			int b = read();
			while (isWhitespace(b)) b = read();
			boolean negative = b == '-';
			if (negative) b = read();
			int result = 0;
			while ('0' <= b && b <= '9') {
				result = result * 10 + b - '0';
				b = read();
			}
			return negative ? -result : result;
		}

		public long nextLong() {
			int b = read();
			while (isWhitespace(b)) b = read();
			boolean negative = b == '-';
			if (negative) b = read();
			long result = 0;
			while ('0' <= b && b <= '9') {
				result = result * 10 + b - '0';
				b = read();
			}
			return negative ? -result : result;
		}

		public double nextDouble() {
			int b = read();
			while (isWhitespace(b)) b = read();
			boolean negative = b == '-';
			if (negative) b = read();
			double result = 0;
			while ('0' <= b && b <= '9') {
				result = result * 10 + b - '0';
				b = read();
			}
			if (b == '.') {
				b = read();
				long f = 0, d = 1;
				while ('0' <= b && b <= '9') {
					f = f * 10 + b - '0';
					d *= 10;
					b = read();
				}
				result += (double) f / d;
			}
			return negative ? -result : result;
		}

		public char nextChar() {
			byte b = read();
			while (isWhitespace(b)) b = read();
			return (char) b;
		}

		public String next() {
			return nextStringBuilder().toString();
		}

		public StringBuilder nextStringBuilder() {
			final StringBuilder sb = new StringBuilder();
			byte b = read();
			while (isWhitespace(b)) b = read();
			while (!isWhitespace(b)) {
				sb.appendCodePoint(b);
				b = read();
			}
			return sb;
		}

		public String nextLine() {
			final StringBuilder sb = new StringBuilder();
			int b = read();
			while (b != 0 && b != '\r' && b != '\n') {
				sb.appendCodePoint(b);
				b = read();
			}
			read();
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
			setAll(a, i -> nextInt());
			return a;
		}

		public long[] nextLong(final int n) {
			final long[] a = new long[n];
			setAll(a, i -> nextLong());
			return a;
		}

		public double[] nextDouble(final int n) {
			final double[] a = new double[n];
			setAll(a, i -> nextDouble());
			return a;
		}

		public char[] nextChars() {
			return next().toCharArray();
		}

		public char[] nextChars(final int n) {
			final char[] c = new char[n];
			for (int i = 0; i < n; i++)
				c[i] = nextChar();
			return c;
		}

		public String[] nextStrings(final int n) {
			final String[] s = new String[n];
			setAll(s, i -> next());
			return s;
		}

		public int[][] nextIntMat(final int h, final int w) {
			final int[][] a = new int[h][w];
			for (int i = 0; i < h; i++)
				setAll(a[i], j -> nextInt());
			return a;
		}

		public long[][] nextLongMat(final int h, final int w) {
			final long[][] a = new long[h][w];
			for (int i = 0; i < h; i++)
				setAll(a[i], j -> nextLong());
			return a;
		}

		public double[][] nextDoubleMat(final int h, final int w) {
			final double[][] a = new double[h][w];
			for (int i = 0; i < h; i++)
				setAll(a[i], j -> nextDouble());
			return a;
		}

		public char[][] nextCharMat(final int n) {
			final char[][] c = new char[n][];
			setAll(c, j -> nextChars());
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
				setAll(s[i], j -> next());
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
			setAll(ps, i -> i > 0 ? nextInt() + ps[i - 1] : nextInt());
			return ps;
		}

		public long[] nextLongPrefixSum(final int n) {
			final long[] ps = new long[n];
			setAll(ps, i -> i > 0 ? nextLong() + ps[i - 1] : nextLong());
			return ps;
		}

		public int[][] nextIntPrefixSum(final int h, final int w) {
			final int[][] ps = new int[h + 1][w + 1];
			for (int i = 1; i <= h; i++) {
				final int j = i;
				setAll(ps[i], k -> k > 0 ? nextInt() + ps[j - 1][k] + ps[j][k - 1] - ps[j - 1][k - 1] : 0);
			}
			return ps;
		}

		public long[][] nextLongPrefixSum(final int h, final int w) {
			final long[][] ps = new long[h + 1][w + 1];
			for (int i = 1; i <= h; i++) {
				final int j = i;
				setAll(ps[i], k -> k > 0 ? nextLong() + ps[j - 1][k] + ps[j][k - 1] - ps[j - 1][k - 1] : 0);
			}
			return ps;
		}

		public int[][][] nextIntPrefixSum(final int x, final int y, final int z) {
			final int[][][] ps = new int[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++) {
					final int A = a, B = b;
					setAll(ps[A][B], c -> c > 0 ? nextInt() + ps[A - 1][B][c] + ps[A][B - 1][c] + ps[A][B][c - 1]
							- ps[A - 1][B - 1][c] - ps[A - 1][B][c - 1] - ps[A][B - 1][c - 1] + ps[A - 1][B - 1][c - 1] : 0);
				}
			return ps;
		}

		public long[][][] nextLongPrefixSum(final int x, final int y, final int z) {
			final long[][][] ps = new long[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1, A = a, B = b; b <= y; b++)
					setAll(ps[A][B], c -> c > 0 ? nextLong() + ps[A - 1][B][c] + ps[A][B - 1][c] + ps[A][B][c - 1]
							- ps[A - 1][B - 1][c] - ps[A - 1][B][c - 1] - ps[A][B - 1][c - 1] + ps[A - 1][B - 1][c - 1] : 0);
			return ps;
		}

		public int[] nextIntInverseMapping(final int n) {
			final int[] inv = new int[n];
			for (int i = 0; i < n; i++)
				inv[nextInt() - 1] = i;
			return inv;
		}

		private <T extends Collection<Integer>> T nextIntCollection(int n, final Supplier<T> supplier) {
			final T collection = supplier.get();
			while (n-- > 0) {
				collection.add(nextInt());
			}
			return collection;
		}

		public ArrayList<Integer> nextIntAL(final int n) {
			return nextIntCollection(n, () -> new ArrayList<>(n));
		}

		public HashSet<Integer> nextIntHS(final int n) {
			return nextIntCollection(n, () -> new HashSet<>(n));
		}

		public TreeSet<Integer> nextIntTS(final int n) {
			return nextIntCollection(n, TreeSet::new);
		}

		private <T extends Collection<Long>> T nextLongCollection(int n, final Supplier<T> supplier) {
			final T collection = supplier.get();
			while (n-- > 0) {
				collection.add(nextLong());
			}
			return collection;
		}

		public ArrayList<Long> nextLongAL(final int n) {
			return nextLongCollection(n, () -> new ArrayList<>(n));
		}

		public HashSet<Long> nextLongHS(final int n) {
			return nextLongCollection(n, () -> new HashSet<>(n));
		}

		public TreeSet<Long> nextLongTS(final int n) {
			return nextLongCollection(n, TreeSet::new);
		}

		private <T extends Collection<Character>> T nextCharacterCollection(int n, final Supplier<T> supplier) {
			final T collection = supplier.get();
			while (n-- > 0) {
				collection.add(nextChar());
			}
			return collection;
		}

		public ArrayList<Character> nextCharacterAL(final int n) {
			return nextCharacterCollection(n, () -> new ArrayList<>(n));
		}

		public HashSet<Character> nextCharacterHS(final int n) {
			return nextCharacterCollection(n, () -> new HashSet<>(n));
		}

		public TreeSet<Character> nextCharacterTS(final int n) {
			return nextCharacterCollection(n, TreeSet::new);
		}

		private <T extends Collection<String>> T nextStringCollection(int n, final Supplier<T> supplier) {
			final T collection = supplier.get();
			while (n-- > 0) {
				collection.add(next());
			}
			return collection;
		}

		public ArrayList<String> nextStringAL(final int n) {
			return nextStringCollection(n, () -> new ArrayList<>(n));
		}

		public HashSet<String> nextStringHS(final int n) {
			return nextStringCollection(n, () -> new HashSet<>(n));
		}

		public TreeSet<String> nextStringTS(final int n) {
			return nextStringCollection(n, TreeSet::new);
		}

		private <T extends Map<Integer, Integer>> T nextIntMultiset(int n, final Supplier<T> supplier) {
			final T multiSet = supplier.get();
			while (n-- > 0) {
				final int i = nextInt();
				multiSet.put(i, multiSet.getOrDefault(i, 0) + 1);
			}
			return multiSet;
		}

		public HashMap<Integer, Integer> nextIntMultisetHM(final int n) {
			return nextIntMultiset(n, () -> new HashMap<>(n));
		}

		public TreeMap<Integer, Integer> nextIntMultisetTM(final int n) {
			return nextIntMultiset(n, TreeMap::new);
		}

		private <T extends Map<Long, Integer>> T nextLongMultiset(int n, final Supplier<T> supplier) {
			final T multiSet = supplier.get();
			while (n-- > 0) {
				final long l = nextLong();
				multiSet.put(l, multiSet.getOrDefault(l, 0) + 1);
			}
			return multiSet;
		}

		public HashMap<Long, Integer> nextLongMultisetHM(final int n) {
			return nextLongMultiset(n, () -> new HashMap<>(n));
		}

		public TreeMap<Long, Integer> nextLongMultisetTM(final int n) {
			return nextLongMultiset(n, TreeMap::new);
		}

		private <T extends Map<Character, Integer>> T nextCharMultiset(int n, final Supplier<T> supplier) {
			final T multiSet = supplier.get();
			while (n-- > 0) {
				final char c = nextChar();
				multiSet.put(c, multiSet.getOrDefault(c, 0) + 1);
			}
			return multiSet;
		}

		public HashMap<Character, Integer> nextCharMultisetHM(final int n) {
			return nextCharMultiset(n, () -> new HashMap<>(n));
		}

		public TreeMap<Character, Integer> nextCharMultisetTM(final int n) {
			return nextCharMultiset(n, TreeMap::new);
		}

		private <T extends Map<String, Integer>> T nextStringMultiset(int n, final Supplier<T> supplier) {
			final T multiSet = supplier.get();
			while (n-- > 0) {
				final String s = next();
				multiSet.put(s, multiSet.getOrDefault(s, 0) + 1);
			}
			return multiSet;
		}

		public HashMap<String, Integer> nextStringMultisetHM(final int n) {
			return nextStringMultiset(n, () -> new HashMap<>(n));
		}

		public TreeMap<String, Integer> nextStringMultisetTM(final int n) {
			return nextStringMultiset(n, TreeMap::new);
		}

		public int[] nextIntMultiset(final int n, final int m) {
			final int[] multiset = new int[m];
			for (int i = 0; i < n; i++) {
				multiset[nextInt() - 1]++;
			}
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
		private static final int MAX_INT_DIGITS = 11;
		private static final int MAX_LONG_DIGITS = 20;
		private static final int DEFAULT_BUFFER_SIZE = 65536;
		private static final byte[] DigitTens = {
				'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
				'1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
				'2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
				'3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
				'4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
				'5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
				'6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
				'7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
				'8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
				'9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
		};
		private static final byte[] DigitOnes = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		};
		private final byte[] buffer;
		private final boolean autoFlush;
		private final OutputStream out;
		private int pos = 0;

		public FastPrinter() {
			this(System.out, DEFAULT_BUFFER_SIZE, false);
		}

		public FastPrinter(final OutputStream out) {
			this(out, DEFAULT_BUFFER_SIZE, false);
		}

		public FastPrinter(final int bufferSize) {
			this(System.out, bufferSize, false);
		}

		public FastPrinter(final boolean autoFlush) {
			this(System.out, DEFAULT_BUFFER_SIZE, autoFlush);
		}

		public FastPrinter(final OutputStream out, final boolean autoFlush) {
			this(out, DEFAULT_BUFFER_SIZE, autoFlush);
		}

		public FastPrinter(final int bufferSize, final boolean autoFlush) {
			this(System.out, bufferSize, autoFlush);
		}

		public FastPrinter(final OutputStream out, final int bufferSize) {
			this(out, bufferSize, false);
		}

		public FastPrinter(final OutputStream out, final int bufferSize, final boolean autoFlush) {
			this.out = out;
			this.buffer = new byte[max(bufferSize, 64)];
			this.autoFlush = autoFlush;
		}

		@Override
		public void close() throws IOException {
			flush();
			if (out != System.out)
				out.close();
		}

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

		public void println() {
			ensureBufferSpace(1);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void println(final int i) {
			ensureBufferSpace(MAX_INT_DIGITS + 1);
			fillBuffer(i);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void println(final long l) {
			ensureBufferSpace(MAX_LONG_DIGITS + 1);
			fillBuffer(l);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void println(final double d) {
			fillBuffer(Double.toString(d));
			ensureBufferSpace(1);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void println(final char c) {
			ensureBufferSpace(2);
			buffer[pos++] = (byte) c;
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void println(final boolean b) {
			ensureBufferSpace(4);
			fillBuffer(b);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void println(final String s) {
			fillBuffer(s);
			ensureBufferSpace(1);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void println(final Object o) {
			if (o == null) return;
			if (o instanceof String s) {
				println(s);
			} else if (o instanceof Long l) {
				println(l.longValue());
			} else if (o instanceof Integer i) {
				println(i.intValue());
			} else if (o instanceof Double d) {
				println(d.toString());
			} else if (o instanceof Boolean b) {
				println(b.booleanValue());
			} else if (o instanceof Character c) {
				println(c.charValue());
			} else if (o instanceof int[] arr) {
				println(arr);
			} else if (o instanceof long[] arr) {
				println(arr);
			} else if (o instanceof double[] arr) {
				println(arr);
			} else if (o instanceof boolean[] arr) {
				println(arr);
			} else if (o instanceof char[] arr) {
				println(arr);
			} else if (o instanceof String[] arr) {
				println(arr);
			} else if (o instanceof Object[] arr) {
				println(arr);
			} else {
				println(o.toString());
			}
		}

		public void println(final BigInteger bi) {
			println(bi.toString());
		}

		public void println(final BigDecimal bd) {
			println(bd.toString());
		}

		public void print(final int i) {
			ensureBufferSpace(MAX_INT_DIGITS);
			fillBuffer(i);
			if (autoFlush) flush();
		}

		public void print(final long l) {
			ensureBufferSpace(MAX_LONG_DIGITS);
			fillBuffer(l);
			if (autoFlush) flush();
		}

		public void print(final double d) {
			print(Double.toString(d));
		}

		public void print(final char c) {
			ensureBufferSpace(1);
			buffer[pos++] = (byte) c;
			if (autoFlush) flush();
		}

		public void print(final boolean b) {
			ensureBufferSpace(3);
			fillBuffer(b);
			if (autoFlush) flush();
		}

		public void print(final String s) {
			fillBuffer(s);
			if (autoFlush) flush();
		}

		public void print(final Object o) {
			if (o == null) return;
			if (o instanceof String s) {
				print(s);
			} else if (o instanceof Long l) {
				print(l.longValue());
			} else if (o instanceof Integer i) {
				print(i.intValue());
			} else if (o instanceof Double d) {
				print(d.toString());
			} else if (o instanceof Boolean b) {
				print(b.booleanValue());
			} else if (o instanceof Character c) {
				print(c.charValue());
			} else if (o instanceof int[] arr) {
				print(arr);
			} else if (o instanceof long[] arr) {
				print(arr);
			} else if (o instanceof double[] arr) {
				print(arr);
			} else if (o instanceof boolean[] arr) {
				print(arr);
			} else if (o instanceof char[] arr) {
				print(arr);
			} else if (o instanceof String[] arr) {
				print(arr);
			} else if (o instanceof Object[] arr) {
				print(arr);
			} else {
				print(o.toString());
			}
		}

		public void print(final BigInteger bi) {
			print(bi.toString());
		}

		public void print(final BigDecimal bd) {
			print(bd.toString());
		}

		public void printf(final String format, final Object... args) {
			print(String.format(format, args));
		}

		public void printf(final Locale locale, final String format, final Object... args) {
			print(String.format(locale, format, args));
		}

		private void ensureBufferSpace(final int size) {
			if (pos + size > buffer.length) {
				try {
					out.write(buffer, 0, pos);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				pos = 0;
			}
		}

		private void fillBuffer(final String s) {
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

		private void fillBuffer(final boolean b) {
			if (b) {
				buffer[pos++] = 'Y';
				buffer[pos++] = 'e';
				buffer[pos++] = 's';
			} else {
				buffer[pos++] = 'N';
				buffer[pos++] = 'o';
			}
		}

		private void fillBuffer(long l) {
			if (l < 0) {
				buffer[pos++] = '-';
			} else {
				l = -l;
			}
			long quotient;
			int remainder;
			final int numOfDigits = countDigits(l);
			int writePos = pos + numOfDigits;
			while (l <= -100) {
				quotient = l / 100;
				remainder = (int) (quotient * 100 - l);
				buffer[--writePos] = DigitOnes[remainder];
				buffer[--writePos] = DigitTens[remainder];
				l = quotient;
			}
			quotient = l / 10;
			remainder = (int) (quotient * 10 - l);
			buffer[--writePos] = (byte) ('0' + remainder);
			if (quotient < 0) {
				buffer[--writePos] = (byte) ('0' - quotient);
			}
			pos += numOfDigits;
		}

		private int countDigits(final long l) {
			if (l > -10) return 1;
			if (l > -100) return 2;
			if (l > -1000) return 3;
			if (l > -10000) return 4;
			if (l > -100000) return 5;
			if (l > -1000000) return 6;
			if (l > -10000000) return 7;
			if (l > -100000000) return 8;
			if (l > -1000000000) return 9;
			if (l > -10000000000L) return 10;
			if (l > -100000000000L) return 11;
			if (l > -1000000000000L) return 12;
			if (l > -10000000000000L) return 13;
			if (l > -100000000000000L) return 14;
			if (l > -1000000000000000L) return 15;
			if (l > -10000000000000000L) return 16;
			if (l > -100000000000000000L) return 17;
			if (l > -1000000000000000000L) return 18;
			return 19;
		}

		public void println(final int a, final int b) {
			println(a, b, '\n');
		}

		public void println(final int a, final long b) {
			println(a, b, '\n');
		}

		public void println(final long a, final int b) {
			println(a, b, '\n');
		}

		public void println(final long a, final long b) {
			println(a, b, '\n');
		}

		public void println(final long a, final long b, final char delimiter) {
			ensureBufferSpace((MAX_LONG_DIGITS << 1) + 2);
			fillBuffer(a);
			buffer[pos++] = (byte) delimiter;
			fillBuffer(b);
			buffer[pos++] = '\n';
			if (autoFlush) flush();
		}

		public void print(final int a, final int b) {
			print(a, b, ' ');
		}

		public void print(final int a, final long b) {
			print(a, b, ' ');
		}

		public void print(final long a, final int b) {
			print(a, b, ' ');
		}

		public void print(final long a, final long b) {
			print(a, b, ' ');
		}

		public void print(final long a, final long b, final char delimiter) {
			ensureBufferSpace((MAX_LONG_DIGITS << 1) + 1);
			fillBuffer(a);
			buffer[pos++] = (byte) delimiter;
			fillBuffer(b);
			if (autoFlush) flush();
		}

		public void println(final double d, final int n) {
			print(d, n);
			println();
		}

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

		public void println(final int[] arr) {
			println(arr, '\n');
		}

		public void println(final long[] arr) {
			println(arr, '\n');
		}

		public void println(final double[] arr) {
			println(arr, '\n');
		}

		public void println(final char[] arr) {
			println(arr, '\n');
		}

		public void println(final boolean[] arr) {
			println(arr, '\n');
		}

		public void println(final String[] arr) {
			println(arr, '\n');
		}

		public void println(final Object... arr) {
			if (arr == null) return;
			for (final Object o : arr)
				println(o);
		}

		public void println(final int[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		public void println(final long[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		public void println(final double[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		public void println(final char[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		public void println(final boolean[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		public void println(final String[] arr, final char delimiter) {
			print(arr, delimiter);
			println();
		}

		public void print(final int[] arr) {
			print(arr, ' ');
		}

		public void print(final long[] arr) {
			print(arr, ' ');
		}

		public void print(final double[] arr) {
			print(arr, ' ');
		}

		public void print(final char[] arr) {
			print(arr, ' ');
		}

		public void print(final boolean[] arr) {
			print(arr, ' ');
		}

		public void print(final String[] arr) {
			print(arr, ' ');
		}

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

		public void print(final double[] arr, final char delimiter) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0], 16);
			for (int i = 1; i < len; i++) {
				ensureBufferSpace(1);
				buffer[pos++] = (byte) delimiter;
				print(arr[i], 16);
			}
			if (autoFlush) flush();
		}

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

		public void print(final String[] arr, final char delimiter) {
			if (arr == null) return;
			final int len = arr.length;
			if (len == 0) return;
			print(arr[0]);
			for (int i = 1; i < len; i++) {
				print(delimiter);
				print(arr[i]);
			}
		}

		public <T> void println(final int[] arr, final IntFunction<T> function) {
			if (arr == null) return;
			if (function == null) {
				println(arr);
				return;
			}
			for (final int i : arr)
				println(function.apply(i));
		}

		public <T> void println(final long[] arr, final LongFunction<T> function) {
			if (arr == null) return;
			if (function == null) {
				println(arr);
				return;
			}
			for (final long l : arr)
				println(function.apply(l));
		}

		public <T> void println(final double[] arr, final DoubleFunction<T> function) {
			if (arr == null) return;
			if (function == null) {
				println(arr);
				return;
			}
			for (final double l : arr)
				println(function.apply(l));
		}

		public <T> void println(final char[] arr, final Function<Character, T> function) {
			if (arr == null) return;
			if (function == null) {
				println(arr);
				return;
			}
			for (final char c : arr)
				println(function.apply(c));
		}

		public <T> void println(final boolean[] arr, final Function<Boolean, T> function) {
			if (arr == null) return;
			if (function == null) {
				println(arr);
				return;
			}
			for (final boolean b : arr)
				println(function.apply(b));
		}

		public <T> void println(final String[] arr, final Function<String, T> function) {
			if (arr == null) return;
			if (function == null) {
				println(arr);
				return;
			}
			for (final String s : arr)
				println(function.apply(s));
		}

		public <T> void print(final int[] arr, final IntFunction<T> function) {
			if (arr == null) return;
			if (function == null) {
				print(arr, ' ');
				return;
			}
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		public <T> void print(final long[] arr, final LongFunction<T> function) {
			if (arr == null) return;
			if (function == null) {
				print(arr, ' ');
				return;
			}
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		public <T> void print(final double[] arr, final DoubleFunction<T> function) {
			if (arr == null) return;
			if (function == null) {
				print(arr, ' ');
				return;
			}
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		public <T> void print(final char[] arr, final Function<Character, T> function) {
			if (arr == null) return;
			if (function == null) {
				print(arr, ' ');
				return;
			}
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		public <T> void print(final boolean[] arr, final Function<Boolean, T> function) {
			if (arr == null) return;
			if (function == null) {
				print(arr, ' ');
				return;
			}
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		public <T> void print(final String[] arr, final Function<String, T> function) {
			if (arr == null) return;
			if (function == null) {
				print(arr, ' ');
				return;
			}
			final int len = arr.length;
			if (len == 0) return;
			print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				print(' ');
				print(function.apply(arr[i]));
			}
		}

		public void println(final int[][] arr2d) {
			println(arr2d, ' ');
		}

		public void println(final long[][] arr2d) {
			println(arr2d, ' ');
		}

		public void println(final double[][] arr2d) {
			println(arr2d, ' ');
		}

		public void println(final char[][] arr2d) {
			println(arr2d, ' ');
		}

		public void println(final boolean[][] arr2d) {
			println(arr2d, ' ');
		}

		public void println(final String[][] arr2d) {
			println(arr2d, ' ');
		}

		public void println(final Object[][] arr2d) {
			println(arr2d, ' ');
		}

		public void println(final int[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final int[] arr : arr2d)
				println(arr, delimiter);
		}

		public void println(final long[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final long[] arr : arr2d)
				println(arr, delimiter);
		}

		public void println(final double[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final double[] arr : arr2d)
				println(arr, delimiter);
		}

		public void println(final char[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final char[] arr : arr2d)
				println(arr, delimiter);
		}

		public void println(final boolean[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final boolean[] arr : arr2d)
				println(arr, delimiter);
		}

		public void println(final String[][] arr2d, final char delimiter) {
			if (arr2d == null) return;
			for (final String[] arr : arr2d)
				println(arr, delimiter);
		}

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

		public <T> void println(final int[][] arr2d, final IntFunction<T> function) {
			if (arr2d == null) return;
			if (function == null) {
				println(arr2d);
				return;
			}
			for (final int[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		public <T> void println(final long[][] arr2d, final LongFunction<T> function) {
			if (arr2d == null) return;
			if (function == null) {
				println(arr2d);
				return;
			}
			for (final long[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		public <T> void println(final double[][] arr2d, final DoubleFunction<T> function) {
			if (arr2d == null) return;
			if (function == null) {
				println(arr2d);
				return;
			}
			for (final double[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		public <T> void println(final char[][] arr2d, final Function<Character, T> function) {
			if (arr2d == null) return;
			if (function == null) {
				println(arr2d);
				return;
			}
			for (final char[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		public <T> void println(final boolean[][] arr2d, final Function<Boolean, T> function) {
			if (arr2d == null) return;
			if (function == null) {
				println(arr2d);
				return;
			}
			for (final boolean[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

		public <T> void println(final String[][] arr2d, final Function<String, T> function) {
			if (arr2d == null) return;
			if (function == null) {
				println(arr2d);
				return;
			}
			for (final String[] arr : arr2d) {
				print(arr, function);
				println();
			}
		}

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

		public void printChars(final char[] arr, final Function<Character, Character> function) {
			if (arr == null) return;
			if (function == null) {
				printChars(arr);
				return;
			}
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

		public void printChars(final char[][] arr2d) {
			if (arr2d == null) return;
			for (final char[] arr : arr2d) {
				printChars(arr);
				println();
			}
		}

		public void printChars(final char[][] arr2d, final Function<Character, Character> function) {
			if (arr2d == null) return;
			if (function == null) {
				printChars(arr2d);
				return;
			}
			for (final char[] arr : arr2d) {
				printChars(arr, function);
				println();
			}
		}

		public <T> void println(final Iterable<T> iter) {
			print(iter, '\n');
			println();
		}

		public <T> void println(final Iterable<T> iter, final char delimiter) {
			print(iter, delimiter);
			println();
		}

		public <T, U> void println(final Iterable<T> iter, final Function<T, U> function) {
			print(iter, function, '\n');
			println();
		}

		public <T> void print(final Iterable<T> iter) {
			print(iter, ' ');
		}

		public <T> void print(final Iterable<T> iter, final char delimiter) {
			if (iter == null) return;
			boolean first = true;
			for (final T t : iter) {
				if (first) {
					first = false;
				} else {
					ensureBufferSpace(1);
					buffer[pos++] = (byte) delimiter;
				}
				print(t);
			}
		}

		public <T, U> void print(final Iterable<T> iter, final Function<T, U> function) {
			print(iter, function, ' ');
		}

		public <T, U> void print(final Iterable<T> iter, final Function<T, U> function, final char delimiter) {
			if (iter == null) return;
			if (function == null) {
				print(iter, delimiter);
				return;
			}
			boolean first = true;
			for (final T t : iter) {
				if (first) {
					first = false;
				} else {
					ensureBufferSpace(1);
					buffer[pos++] = (byte) delimiter;
				}
				print(function.apply(t));
			}
		}
	}
}
