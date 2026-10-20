// ====================================================================================================
// Author: nsubaru
// Runtime: Java 24
// Library: https://github.com/nsubaru11/competitive-programming-java-library
// ====================================================================================================

import static java.util.Arrays.*;

import java.util.*;
import java.util.stream.*;

import lib.io.*;

public final class D {

	// region < Constants & Globals >
	private static final boolean DEBUG = true;
	private static final int MOD = 998244353;
	// private static final int MOD = 1_000_000_007;
	private static final char[] op = new char[]{'L', 'U', 'R', 'D'};
	private static final int[] di = new int[]{0, -1, 0, 1, -1, -1, 1, 1};
	private static final int[] dj = new int[]{-1, 0, 1, 0, -1, 1, 1, -1};
	private static final FastScanner sc = new FastScanner();
	private static final FastPrinter out = new FastPrinter();
	// endregion

	private static void solve() {
		int n = sc.nextInt();
		boolean[] s = sc.nextBoolean(n, 'o');
		char[] ans = new char[n];
		if (check(0, s, ans, n)) {
			out.printChars(ans).println();
		} else {
			out.println(-1);
		}
	}

	private static boolean check(int i, boolean[] s, char[] ans, int n) {
		if (i == 0) {
			if (s[i]) {
				ans[i] = 'S';
				ans[n - 1] = ans[1] = 'S';
				if (check(i + 1, s, ans, n)) return true;
				ans[n - 1] = ans[1] = 'W';
				if (check(i + 1, s, ans, n)) return true;

				ans[i] = 'W';
				ans[n - 1] = 'S';
				ans[1] = 'W';
				if (check(i + 1, s, ans, n)) return true;
				ans[n - 1] = 'W';
				ans[1] = 'S';
				if (check(i + 1, s, ans, n)) return true;
			} else {
				ans[i] = 'S';
				ans[n - 1] = 'S';
				ans[1] = 'W';
				if (check(i + 1, s, ans, n)) return true;
				ans[n - 1] = 'W';
				ans[1] = 'S';
				if (check(i + 1, s, ans, n)) return true;

				ans[i] = 'W';
				ans[n - 1] = ans[1] = 'S';
				if (check(i + 1, s, ans, n)) return true;
				ans[n - 1] = ans[1] = 'W';
				if (check(i + 1, s, ans, n)) return true;
			}
			return false;
		}
		if (i == n - 2) {
			if (s[i]) {
				if (ans[i] == 'S') {
					if (ans[i - 1] != ans[i + 1]) return false;
				} else {
					if (ans[i - 1] == ans[i + 1]) return false;
				}
			} else {
				if (ans[i] == 'S') {
					if (ans[i - 1] == ans[i + 1]) return false;
				} else {
					if (ans[i - 1] != ans[i + 1]) return false;
				}
			}
			return ((((s[n - 1] && ans[n - 1] == 'S') || (!s[n - 1] && ans[n - 1] == 'W')) && ans[n - 2] == ans[0]) || (((!s[n - 1] && ans[n - 1] == 'S') || (s[n - 1] && ans[n - 1] == 'W')) && ans[n - 2] != ans[0]));
		}
		if (s[i]) {
			if (ans[i] == 'S') {
				ans[i + 1] = ans[i - 1];
			} else {
				ans[i + 1] = ans[i - 1] == 'S' ? 'W' : 'S';
			}
		} else {
			if (ans[i] == 'S') {
				ans[i + 1] = ans[i - 1] == 'S' ? 'W' : 'S';
			} else {
				ans[i + 1] = ans[i - 1];
			}
		}
		return check(i + 1, s, ans, n);
	}

	// region < Utility Methods >
	private static boolean isValidRange(final int i, final int from, final int to) {
		return ((i - from) | (to - 1 - i)) >= 0;
	}

	private static boolean isValidRange(final int i, final int j, final int h, final int w) {
		return ((i | j | (h - 1 - i) | (w - 1 - j)) >>> 31) == 0;
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

	private static int min(final int a, final int b) {
		return Math.min(a, b);
	}

	private static int min(final int a, final int b, final int c) {
		return Math.min(a, Math.min(b, c));
	}

	private static int min(int... a) {
		int len = a.length;
		int min = a[0];
		for (int i = 1; i < len; i++) if (min > a[i]) min = a[i];
		return min;
	}

	private static int max(final int a, final int b) {
		return Math.max(a, b);
	}

	private static int max(final int a, final int b, final int c) {
		return Math.max(a, Math.max(b, c));
	}

	private static int max(int... a) {
		int len = a.length;
		int max = a[0];
		for (int i = 1; i < len; i++) if (max < a[i]) max = a[i];
		return max;
	}

	private static long min(final long a, final long b) {
		return Math.min(a, b);
	}

	private static long min(final long a, final long b, final long c) {
		return Math.min(a, Math.min(b, c));
	}

	private static long min(long... a) {
		int len = a.length;
		long min = a[0];
		for (int i = 1; i < len; i++) if (min > a[i]) min = a[i];
		return min;
	}

	private static long max(final long a, final long b) {
		return Math.max(a, b);
	}

	private static long max(final long a, final long b, final long c) {
		return Math.max(a, Math.max(b, c));
	}

	private static long max(long... a) {
		int len = a.length;
		long max = a[0];
		for (int i = 1; i < len; i++) if (max < a[i]) max = a[i];
		return max;
	}

	private static double min(final double a, final double b) {
		return Math.min(a, b);
	}

	private static double min(final double a, final double b, final double c) {
		return Math.min(a, Math.min(b, c));
	}

	private static double min(double... a) {
		int len = a.length;
		double min = a[0];
		for (int i = 1; i < len; i++) if (min > a[i]) min = a[i];
		return min;
	}

	private static double max(final double a, final double b) {
		return Math.max(a, b);
	}

	private static double max(final double a, final double b, final double c) {
		return Math.max(a, Math.max(b, c));
	}

	private static double max(double... a) {
		int len = a.length;
		double max = a[0];
		for (int i = 1; i < len; i++) if (max < a[i]) max = a[i];
		return max;
	}

	private static int diff(final int a, final int b) {
		return a > b ? a - b : b - a;
	}

	private static long diff(final long a, final long b) {
		return a > b ? a - b : b - a;
	}

	private static double diff(final double a, final double b) {
		return a > b ? a - b : b - a;
	}

	private static void fill2D(final char[][] a, final char v) {
		for (final char[] ai : a) fill(ai, v);
	}

	private static void fill2D(final int[][] a, final int v) {
		for (final int[] ai : a) fill(ai, v);
	}

	private static void fill2D(final long[][] a, final long v) {
		for (final long[] ai : a) fill(ai, v);
	}

	private static void fill2D(final double[][] a, final double v) {
		for (final double[] ai : a) fill(ai, v);
	}

	private static void fill3D(final char[][][] a, final char v) {
		for (final char[][] ai : a) fill2D(ai, v);
	}

	private static void fill3D(final int[][][] a, final int v) {
		for (final int[][] ai : a) fill2D(ai, v);
	}

	private static void fill3D(final long[][][] a, final long v) {
		for (final long[][] ai : a) fill2D(ai, v);
	}

	private static void fill3D(final double[][][] a, final double v) {
		for (final double[][] ai : a) fill2D(ai, v);
	}
	// endregion

	// region < main & debug >
	public static void main(final String[] args) {
		try {
			solve();
		} finally {
			out.close();
		}
	}

	private static void debugln(final Object... args) {
		if (DEBUG) {
			out.flush();
			if (args == null) System.err.println("null");
			else if (args.getClass().getComponentType().isArray()) System.err.println(stringify(args));
			else System.err.println(stream(args).map(D::stringify).collect(Collectors.joining("\n", "\n", "")));
		}
	}

	private static void debug(final Object... args) {
		if (DEBUG) {
			out.flush();
			if (args == null) System.err.println("null");
			else if (args.getClass().getComponentType().isArray()) System.err.println(stringify(args));
			else System.err.println(stream(args).map(D::stringify).collect(Collectors.joining(", ", "", "")));
		}
	}

	private static String stringify(final Object obj) {
		return switch (obj) {
			case null -> "null";
			case int[][] arr -> "\n" + stream(arr).map(Arrays::toString).collect(Collectors.joining("\n"));
			case long[][] arr -> "\n" + stream(arr).map(Arrays::toString).collect(Collectors.joining("\n"));
			case char[][] arr -> "\n" + stream(arr).map(String::valueOf).collect(Collectors.joining("\n"));
			case Object[][] arr -> "\n" + stream(arr).map(Arrays::deepToString).collect(Collectors.joining("\n"));
			case int[] arr -> Arrays.toString(arr);
			case long[] arr -> Arrays.toString(arr);
			case double[] arr -> Arrays.toString(arr);
			case char[] arr -> Arrays.toString(arr);
			case boolean[] arr -> Arrays.toString(arr);
			case Object[] arr -> deepToString(arr);
			case Iterable<?> it -> {
				final StringJoiner sj = new StringJoiner(", ", "[", "]");
				for (final Object e : it) sj.add(stringify(e));
				yield sj.toString();
			}
			default -> obj.toString();
		};
	}
	// endregion
}
