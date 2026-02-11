import java.io.*;
import java.math.*;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public class D {

	private static final FastScanner sc = new FastScanner();
	private static final FastPrinter out = new FastPrinter(true);

	public static void main(String[] args) {
		int n = sc.nextInt();
		long[] a = sc.nextLong(n);
		HashSet<Integer> hs = new HashSet<>();

	}

	private static long count() {
		return 0;
	}

	/**
	 * int型RingBufferクラス: 固定長の循環バッファを実装
	 */
	@SuppressWarnings("unused")
	private static final class RingBuffer {
		private final int size; // バッファの最大サイズ
		private final int[] buf; // データを格納する配列
		private int head, tail; // データの先頭と末尾を示すインデックス
		private int len; // バッファ内の現在のデータ数

		/**
		 * 指定されたサイズで新しいRingBufferを初期化します。
		 *
		 * @param n バッファの最大サイズ。(int)
		 * @throws IllegalArgumentException サイズが0以下の場合
		 */
		public RingBuffer(int n) {
			if (n <= 0)
				throw new IllegalArgumentException();
			size = n;
			buf = new int[size];
			len = head = tail = 0;
		}

		/**
		 * バッファの末尾に要素を追加します。
		 *
		 * @param e 追加する要素
		 * @return バッファに正常に追加された場合はtrueを返します。 すでに満杯の場合はfalseを返します。
		 */
		public boolean addLast(int e) {
			if (isFull())
				return false;
			buf[tail] = e;
			tail = mod(tail + 1, size);
			len++;
			return true;
		}

		/**
		 * バッファの先頭に要素を追加
		 *
		 * @param e 追加する要素
		 * @return バッファに正常に追加された場合はtrueを返します。 すでに満杯の場合はfalseを返します。
		 */
		public boolean addFirst(int e) {
			if (isFull())
				return false;
			head = mod(head - 1, size);
			buf[head] = e;
			len++;
			return true;
		}

		/**
		 * 指定したインデックスの要素を取得します。
		 * <p>
		 * 負のインデックスは末尾からの相対位置を表す。
		 *
		 * @param index 取得する要素のインデックス。-len <= index < lenを満たす必要があります。
		 * @return 指定されたインデックスの要素
		 * @throws RingBufferIndexException バッファが空の場合、またはインデックスが範囲外の場合
		 */
		public int get(int index) {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			if (index >= len || index < -len)
				throw new RingBufferIndexException(index, -len, len - 1);
			if (index < 0)
				index += len;
			index = mod(index + head, size);
			return buf[index];
		}

		/**
		 * 末尾の要素を取得して削除します。
		 *
		 * @return 末尾の要素
		 * @throws RingBufferIndexException バッファが空の場合
		 */
		public int pollLast() {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			int last = buf[mod(tail - 1, size)];
			len--;
			tail = mod(tail - 1, size);
			return last;
		}

		/**
		 * 先頭の要素を取得して削除します。
		 *
		 * @return 先頭の要素
		 * @throws RingBufferIndexException バッファが空の場合
		 */
		public int pollFirst() {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			int first = buf[head];
			len--;
			head = mod(head + 1, size);
			return first;
		}

		/**
		 * 末尾の要素を取得します。
		 *
		 * @return 末尾の要素
		 * @throws RingBufferIndexException バッファが空の場合
		 */
		public int peekLast() {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			return buf[mod(tail - 1, size)];
		}

		/**
		 * 先頭の要素を取得します。
		 *
		 * @return 先頭の要素
		 * @throws RingBufferIndexException バッファが空の場合
		 */
		public int peekFirst() {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			return buf[head];
		}

		/**
		 * 指定されたインデックスの要素を上書きします。 インデックスが現在の長さと等しい場合、新しい要素を末尾に追加します。
		 *
		 * @param index 上書きする要素のインデックス。-len <= index <= lenを満たす必要があります。
		 * @param e     新しい要素
		 * @return このインスタンス自体を返します。
		 * @throws RingBufferIndexException バッファが空の場合、またはインデックスが範囲外の場合
		 */
		public RingBuffer set(int index, int e) {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			if (index > len || index < -len)
				throw new RingBufferIndexException(index, -len, len);
			if (index == len) {
				addLast(e);
			} else {
				if (index < 0)
					index += len;
				index = mod(index + head, size);
				buf[index] = e;
			}
			return this;
		}

		/**
		 * 現在のバッファの長さを変更します。 長さが増加する場合、新しい位置には0が挿入されます。
		 *
		 * @param len 新しい長さ（0 <= len <= sizeを満たす必要があります）
		 * @return このインスタンス自体を返します。
		 * @throws RingBufferIndexException 指定された長さが範囲外の場合
		 */
		public RingBuffer setLength(int len) {
			if (len > size || len < 0)
				throw new RingBufferIndexException(len, size);
			if (this.len < len) {
				while (this.len < len) {
					this.addLast(0);
				}
			} else {
				this.len = len;
				tail = mod(head + len, size);
			}
			return this;
		}

		/**
		 * 現在のバッファの長さを開始インデックスと終了インデックスを指定して変更します。 長さが増加する場合、新しい位置には0が挿入されます。
		 *
		 * @param startIndex 開始インデックス（0 <= startIndex < sizeを満たす必要があります）（これを含む）
		 * @param endIndex   終了インデックス（0 <= endIndex <= sizeを満たす必要があります）（これを含まない）
		 * @return このインスタンス自体を返します。
		 * @throws RingBufferIndexException 指定されたインデックスが範囲外の場合 startIndex > endIndexの場合
		 */
		public RingBuffer setLength(int startIndex, int endIndex) {
			if (startIndex > endIndex)
				throw new RingBufferIndexException("Invalid range: [" + startIndex + ", " + endIndex + "]");
			if (startIndex < 0 || size <= startIndex)
				throw new RingBufferIndexException(startIndex, 0, size - 1);
			if (endIndex < 0 || size < endIndex)
				throw new RingBufferIndexException(endIndex, 0, size - 1);
			if (startIndex == endIndex) {
				clear();
				return this;
			}
			while (!isFull() && endIndex > mod(tail - head, size)) {
				addLast(0);
			}
			head = mod(startIndex + head, size);
			tail = mod(head + endIndex, size);
			len = endIndex - startIndex;
			return this;
		}

		/**
		 * 指定した要素がバッファに含まれているかを調べます。
		 *
		 * @param e 検索対象の要素
		 * @return 指定した要素が含まれている場合はtrue、含まれていない場合はfalse
		 */
		public boolean contains(int e) {
			for (int i = 0; i < len; i++) {
				if (get(i) == e)
					return true;
			}
			return false;
		}

		/**
		 * 指定した位置に要素を挿入します。 挿入により要素が移動するため、計算コストが発生します。
		 *
		 * @param index 挿入位置のインデックス。負の値は末尾からの相対位置を表します（-len <= index <= len）。
		 * @param e     挿入する要素
		 * @return このインスタンス自体を返します。
		 * @throws RingBufferIndexException バッファが満杯の場合、 またはインデックスが有効範囲外の場合
		 */
		public RingBuffer insert(int index, int e) {
			if (len == size)
				throw new RingBufferIndexException("Buffer is full");
			if (index > len || index < -len)
				throw new RingBufferIndexException(index, -len, len);
			if (index == 0) {
				addFirst(e);
			} else if (index == len) {
				addLast(e);
			} else {
				if (index < 0)
					index += len;
				if (index < len / 2) {
					addFirst(get(0));
					for (int i = 1; i < index; i++) {
						set(i, get(i + 1));
					}
					set(index, e);
				} else {
					for (int i = len; i > index; i--) {
						set(i, get(i - 1));
					}
					set(index, e);
				}
			}
			return this;
		}

		/**
		 * 指定した位置の要素を削除し、バッファ内の他の要素をシフトします。
		 *
		 * @param index 削除する要素のインデックス。負の値は末尾からの相対位置を表します（-len <= index < len）。
		 * @return このインスタンス自体を返します。
		 * @throws RingBufferIndexException バッファが空の場合 またはインデックスが有効範囲外の場合
		 */
		public RingBuffer remove(int index) {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty");
			if (index >= len || index < -len)
				throw new RingBufferIndexException(index, -len, len - 1);
			if (index < 0)
				index += len;
			for (int i = index; i < len - 1; i++) {
				set(i, get(i + 1));
			}
			pollLast();
			return this;
		}

		/**
		 * バッファが空かどうかを返します。
		 *
		 * @return 現在の要素数が0の場合はtrue、それ以外の場合はfalse
		 */
		public boolean isEmpty() {
			return len == 0;
		}

		/**
		 * バッファが満杯かどうかを返します。
		 *
		 * @return 現在の要素数がバッファの最大サイズに達している場合はtrue、それ以外の場合はfalse
		 */
		public boolean isFull() {
			return len == size;
		}

		/**
		 * 現在のバッファ内の要素数を返します。
		 *
		 * @return 現在のバッファ内の要素数（0以上size以下）
		 */
		public int length() {
			return len;
		}

		/**
		 * バッファの最大サイズを返します。
		 *
		 * @return バッファの最大サイズ
		 */
		public int size() {
			return size;
		}

		/**
		 * 指定された関数を使用してバッファ内の全要素を初期化します。
		 *
		 * @param generator 要素を生成する関数。インデックス（0からsize-1まで）を引数として受け取り、初期化する値を返します。
		 * @return このインスタンス自体を返します。
		 */
		public RingBuffer setAll(IntFunction<Integer> generator) {
			head = tail = 0;
			len = size;
			Arrays.setAll(buf, i -> generator.apply(i));
			return this;
		}

		/**
		 * 指定された値でバッファ全体を初期化します。
		 *
		 * @param e バッファの全ての要素を埋める値
		 * @return このインスタンス自体を返します。
		 */
		public RingBuffer fill(int e) {
			Arrays.fill(buf, e);
			return this;
		}

		/**
		 * バッファ内の全ての要素を削除し、空の状態にします。
		 *
		 * @return このインスタンス
		 */
		public RingBuffer clear() {
			head = tail = len = 0;
			return this;
		}

		/**
		 * aをbで割った余りを計算します。負の値にも対応しています。
		 *
		 * @param a 割られる数
		 * @param b 割る数（0ではない必要があります）
		 * @return aをbで割った余り。常に0以上b未満の値を返します。
		 */
		private int mod(int a, int b) {
			return (a % b + b) % b;
		}

		/**
		 * このRingBufferインスタンスのクローンを作成します。 クローンされたインスタンスは元のインスタンスの独立したコピーです。
		 *
		 * @return クローンされたRingBufferインスタンス
		 **/
		public RingBuffer clone() {
			RingBuffer rb = new RingBuffer(size);
			System.arraycopy(buf, 0, rb.buf, 0, size);
			rb.head = head;
			rb.tail = tail;
			rb.len = len;
			return rb;
		}

		/**
		 * 指定されたオブジェクトがこのRingBufferと等しいかを判定します。
		 *
		 * @param o 比較対象のオブジェクト
		 * @return trueの場合、指定されたオブジェクトがRingBufferのインスタンスであり、
		 * バッファ内の要素と順序が完全に一致していることを示します。
		 */
		public boolean equals(Object o) {
			if (!(o instanceof RingBuffer))
				return false;
			RingBuffer other = (RingBuffer) o;
			return len == other.len && IntStream.range(0, len).allMatch(i -> get(i) == other.get(i));
		}

		/**
		 * バッファ内の要素をスペースで区切った文字列として返します。
		 *
		 * @return バッファ内の要素を表す文字列。バッファが空の場合は空文字列
		 */
		public String toString() {
			if (len == 0)
				return "";
			StringBuilder sb = new StringBuilder();
			sb.append(get(0));
			for (int i = 1; i < len; i++) {
				sb.append(" ").append(get(i));
			}
			return sb.toString();
		}

		/**
		 * RingBufferに関連する例外クラス。 主にインデックスや長さの不正使用時にスローされます。
		 */
		private class RingBufferIndexException extends RuntimeException {

			private static final long serialVersionUID = -7627324668888567160L;

			/**
			 * 指定されたエラーメッセージを使用して例外を初期化します。
			 *
			 * @param message エラーメッセージ
			 */
			public RingBufferIndexException(String message) {
				super(message);
			}

			/**
			 * 指定されたインデックスが有効範囲外である場合の例外を初期化します。
			 *
			 * @param index 不正なインデックス
			 * @param from  有効範囲の下限（含む）
			 * @param to    有効範囲の上限（含む）
			 */
			public RingBufferIndexException(int index, int from, int to) {
				super(String.format("Invalid index %d. Valid length is [%d, %d].", index, from, to));
			}

			/**
			 * 指定のlenが0 <= len <= sizeを満たさないときのエラーです。
			 *
			 * @param len  不正な長さ
			 * @param size 有効なサイズの上限
			 */
			public RingBufferIndexException(int len, int size) {
				super(String.format("Invalid length %d. Max allowed: %d.", len, size));
			}
		}
	}

	@SuppressWarnings("unused")
	private static class MathFn {

		/**
		 * a / b以下で最大の長整数を返します。
		 *
		 * @param a 割られる値(long)
		 * @param b 割る値(long)
		 * @return ⌊a / b⌋
		 */
		public static long floorLong(long a, long b) {
			return a < 0 ? (a - b + 1) / b : a / b;
		}

		/**
		 * a / b以下で最大の整数を返します。
		 *
		 * @param a 割られる値(int)
		 * @param b 割る値(int)
		 * @return ⌊a / b⌋
		 */
		public static int floorInt(int a, int b) {
			return a < 0 ? (a - b + 1) / b : a / b;
		}

		/**
		 * a / b以上で最大の長整数を返します。
		 *
		 * @param a 割られる値(long)
		 * @param b 割る値(long)
		 * @return ⌈a / b⌉
		 */
		public static long ceilLong(long a, long b) {
			return a < 0 ? a / b : (a + b - 1) / b;
		}

		/**
		 * a / b以上で最大の整数を返します。
		 *
		 * @param a 割られる値(int)
		 * @param b 割る値(int)
		 * @return ⌈a / b⌉
		 */
		public static long ceilInt(int a, int b) {
			return a < 0 ? a / b : (a + b - 1) / b;
		}

		/**
		 * doubleの高速なフォーマット。小数点第n - 1位を四捨五入します。
		 *
		 * @param x フォーマットする対象
		 * @param n 少数点以下の桁数
		 * @return String
		 */
		public static String formatDouble(double x, int n) {
			StringBuilder sb = new StringBuilder();
			if (n == 0) return sb.append(round(x)).toString();
			if (x < 0) {
				sb.append("-");
				x = -x;
			}
			x += pow(10, -n) / 2;
			sb.append((long) x).append(".");
			x -= (long) x;
			while (n-- > 0) {
				x *= 10;
				sb.append((int) x);
				x -= (int) x;
			}
			return sb.toString();
		}

		/**
		 * a ^ b % mod を求めます。
		 *
		 * @param a   底
		 * @param b   指数
		 * @param mod 割る値(素数)
		 * @return a ^ b % mod
		 */
		public static double modPow(long a, long b, long mod) {
			long ans = 1;
			boolean f = b < 0;
			if (f) b = -b;
			b %= mod - 1;
			for (; b > 0; a = a * a % mod, b >>= 1) {
				if ((b & 1) == 1)
					ans = ans * a % mod;
			}
			return f ? (double) 1 / ans : ans;
		}

		/**
		 * aのb乗を求めます
		 *
		 * @param a 底
		 * @param b 指数
		 * @return a ^ b
		 */
		public static double pow(double a, long b) {
			double ans = 1;
			boolean f = b < 0;
			if (f) b = -b;
			for (; b > 0; a *= a, b >>= 1) {
				if ((b & 1) == 1)
					ans *= a;
			}
			return f ? (double) 1 / ans : ans;
		}

		/**
		 * nが20以下の階乗を返します。
		 *
		 * @param n int
		 * @return n!
		 */
		public static long factorial_max20(int n) {
			long[] SmallFactorials = {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800,
					39916800, 479001600, 6227020800L, 87178291200L, 1307674368000L,
					20922789888000L, 355687428096000L, 6402373705728000L,
					121645100408832000L, 2432902008176640000L};
			return SmallFactorials[n];
		}

		/**
		 * nの階乗をmodで割った余りを計算します。
		 *
		 * @param n   int
		 * @param mod long
		 * @return n! % mod
		 */
		public static long modFactorial(int n, long mod) {
			if (n <= 20) return factorial_max20(n) % mod;
			if (n >= mod) return 0;
			if (n == mod - 1) return n;
			int twoExponent = n - Integer.bitCount(n);
			long[] oddPart = new long[(n >> 1) + 1];
			oddPart[0] = 1;
			for (int i = 3; i <= n; i += 2) {
				int idx = i >> 1;
				oddPart[idx] = oddPart[idx - 1] * i % mod;
			}
			long ans = oddPart[(n - 1) >> 1];
			for (int i = 1; n >> i > 1; i++) {
				int k = n;
				if ((k >>= i) % 2 == 1) {
					ans = ans * oddPart[k >> 1] % mod;
				} else {
					ans = ans * oddPart[(k - 1) >> 1] % mod;
				}
			}
			long twoExp = (1L << 62) % mod;
			while (twoExponent > 62) {
				ans = ans * twoExp % mod;
				twoExponent -= 62;
			}
			return ans * ((1L << twoExponent) % mod) % mod;
		}

		/**
		 * 平方数かどうかの判定
		 *
		 * @param s 判定する対象
		 * @return boolean
		 */
		public static boolean isSquareNum(long s) {
			long sqrt = round(sqrt(s));
			return s == sqrt * sqrt;
		}

		/**
		 * 立方数かどうかの判定
		 *
		 * @param c 判定する対象
		 * @return boolean
		 */
		public static boolean isCubicNum(long c) {
			long cbrt = round(cbrt(c));
			return c == cbrt * cbrt * cbrt;
		}

		/**
		 * nCrを求めます。
		 *
		 * @param n 二項係数を求めるのに用いる値
		 * @param r 二項係数を求めるのに用いる値
		 * @return nCr
		 */
		public static long comb(long n, long r) {
			if (n < 0 || r < 0) return 0;
			long ans = 1;
			r = min(n - r, r);
			for (int i = 1; i <= r; i++) {
				ans *= n - i;
				ans /= i + 1;
			}
			return ans;
		}

		/**
		 * nCrをmodで割った余りを求めます。
		 *
		 * @param n   二項係数を求めるのに用いる値
		 * @param r   二項係数を求めるのに用いる値
		 * @param mod 法とする整数
		 * @return nCr % mod
		 */
		public static long modComb(long n, long r, long mod) {
			if (n < 0 || r < 0) return 0;
			long ans = 1;
			r = min(n - r, r);
			for (int i = 0; i < r; i++) {
				ans = ans * (n - i) % mod;
				ans = ans * (long) modPow(i + 1, mod - 2, mod) % mod;
			}
			return ans;
		}

		public static long isCrossed(int Ax1, int Ay1, int Ax2, int Ay2, int Bx1, int By1, int Bx2, int By2) {
			return 1;
		}

		/**
		 * 最小公倍数を求めます
		 *
		 * @param x long
		 * @param y long
		 * @return x * (y / GCD(x, y))
		 */
		public static long LCM(long x, long y) {
			return x == 0 || y == 0 ? 0 : x * (y / GCD(x, y));
		}

		/**
		 * 最大公約数を求めます
		 *
		 * @param x long
		 * @param y long
		 * @return GCD(x, y)
		 */
		public static long GCD(long x, long y) {
			return y > 0 ? GCD(y, x % y) : x;
		}

		/**
		 * ax + by = GCD(x, y)となるx, yの組を見つけます。
		 *
		 * @param a long
		 * @param b long
		 * @param x AtomicLong
		 * @param y AtomicLong
		 * @return |x| + |y|の最小値
		 */
		public static long exGCD(long a, long b, AtomicLong x, AtomicLong y) {
			if (b == 0) {
				x.set(1);
				y.set(0);
				return a;
			}
			AtomicLong xx = new AtomicLong(), yy = new AtomicLong();
			long d = exGCD(b, a % b, yy, xx);
			y.set(yy.get() - (a / b) * xx.get());
			x.set(xx.get());
			return d;
		}

		/**
		 * オイラーのトーシェント関数に基づき1からnまでのnと互いに素な数字の個数を調べます。
		 *
		 * @param n long
		 * @return long
		 */
		public static long EulerTotientFunction(long n) {
			long sum = 1;
			for (int i = 2; i <= 3; i++) {
				long temp = 1;
				while (n % i == 0) {
					n /= i;
					temp *= i;
				}
				sum *= temp - temp / i;
			}
			for (int i = 5; (long) i * i <= n; i += 6) {
				for (int j = i; j <= i + 2; j += 2) {
					if (n % j == 0) {
						long temp = 1;
						while (n % j == 0) {
							n /= j;
							temp *= j;
						}
						sum *= temp - temp / j;
					}
				}
			}
			return n == 1 ? sum : sum * (n - 1);
		}

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

	@SuppressWarnings("unused")
	private static final class FastScanner {
		private static final BufferedInputStream reader = new BufferedInputStream(System.in);
		private static final byte[] buf = new byte[1 << 17];
		private static int pos = 0, cnt = 0;
		private long sum, low, high;

		private byte read() {
			if (pos == cnt) {
				try {
					cnt = reader.read(buf, pos = 0, 1 << 17);
				} catch (IOException ignored) {
				}
			}
			if (cnt < 0) return 0;
			return buf[pos++];
		}

		public char nextChar() {
			byte b = read();
			while (b < '!' || '~' < b) b = read();
			return (char) b;
		}

		public String next() {
			return nextSb().toString();
		}

		public StringBuilder nextSb() {
			StringBuilder sb = new StringBuilder();
			int b = read();
			while (b < '!' || '~' < b) b = read();
			while ('!' <= b && b <= '~') {
				sb.appendCodePoint(b);
				b = read();
			}
			return sb;
		}

		public String nextLine() {
			StringBuilder sb = new StringBuilder();
			int b = read();
			while (b != 0 && b != '\r' && b != '\n') {
				sb.appendCodePoint(b);
				b = read();
			}
			if (b == '\r') read();
			return sb.toString();
		}

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

		public int nextIntRadix(int radix) {
			return Integer.parseInt(next(), radix);
		}

		public long nextLongRadix(int radix) {
			return Long.parseLong(next(), radix);
		}

		public BigInteger nextBigInteger() {
			return new BigInteger(next());
		}

		public BigInteger nextBigInteger(int radix) {
			return new BigInteger(next(), radix);
		}

		public double nextDouble() {
			return Double.parseDouble(next());
		}

		public String[] nextStrings(int n) {
			String[] s = new String[n];
			setAll(s, x -> next());
			return s;
		}

		public String[] nextSortedStrings(int n) {
			String[] s = nextStrings(n);
			sort(s);
			return s;
		}

		public String[][] nextStringMat(int n, int m) {
			String[][] s = new String[n][m];
			setAll(s, x -> nextStrings(m));
			return s;
		}

		public char nextCharAt(int i) {
			return next().charAt(i);
		}

		public char[] nextChars() {
			return next().toCharArray();
		}

		public char[] nextChars(int n) {
			char[] c = new char[n];
			for (int i = 0; i < n; i++) c[i] = nextChar();
			return c;
		}

		public char[] nextSortedChars() {
			char[] c = nextChars();
			sort(c);
			return c;
		}

		public char[] nextSortedChars(int n) {
			char[] c = nextChars(n);
			sort(c);
			return c;
		}

		public char[][] nextCharMat(int n) {
			char[][] c = new char[n][];
			setAll(c, x -> nextChars());
			return c;
		}

		public char[][] nextCharMat(int n, int m) {
			char[][] c = new char[n][m];
			setAll(c, x -> nextChars(m));
			return c;
		}

		public int[] nextInt(int n) {
			int[] a = new int[n];
			resetStats();
			for (int i = 0; i < n; i++) updateStats(a[i] = nextInt());
			return a;
		}

		public Integer[] nextInts(int n) {
			Integer[] a = new Integer[n];
			resetStats();
			for (int i = 0; i < n; i++) updateStats(a[i] = nextInt());
			return a;
		}

		public int[] nextSortedInt(int n) {
			int[] a = nextInt(n);
			sort(a);
			return a;
		}

		public Integer[] nextSortedInts(int n) {
			Integer[] a = nextInts(n);
			sort(a);
			return a;
		}

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

		public int[][] nextIntMat(int h, int w) {
			int[][] a = new int[h][w];
			setAll(a, x -> nextInt(w));
			return a;
		}

		public int[][] nextIntMatSum(int h, int w) {
			int[][] a = new int[h + 1][w + 1];
			for (int i = 1; i <= h; i++)
				for (int j = 1; j <= w; j++) a[i][j] = nextInt() + a[i - 1][j] + a[i][j - 1] - a[i - 1][j - 1];
			return a;
		}

		public int[][][] nextInt3D(int x, int y, int z) {
			int[][][] a = new int[x][y][z];
			setAll(a, b -> nextIntMat(y, z));
			return a;
		}

		public int[][][] nextIntSum3D(int x, int y, int z) {
			int[][][] e = new int[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++)
					for (int c = 1; c <= z; c++)
						e[a][b][c] = nextInt() + e[a - 1][b][c]
								+ e[a][b - 1][c] + e[a][b][c - 1] - e[a - 1][b - 1][c] - e[a - 1][b][c - 1] - e[a][b - 1][c - 1] + e[a - 1][b - 1][c - 1];
			return e;
		}

		public long[] nextLong(int n) {
			long[] a = new long[n];
			resetStats();
			for (int i = 0; i < n; i++) updateStats(a[i] = nextLong());
			return a;
		}

		public Long[] nextLongs(int n) {
			Long[] a = new Long[n];
			resetStats();
			for (int i = 0; i < n; i++) updateStats(a[i] = nextLong());
			return a;
		}

		public long[] nextSortedLong(int n) {
			long[] a = nextLong(n);
			sort(a);
			return a;
		}

		public Long[] nextSortedLongs(int n) {
			Long[] a = nextLongs(n);
			sort(a);
			return a;
		}

		public long[] nextLongSum(int n) {
			long[] a = new long[n];
			low = high = sum = a[0] = nextLong();
			for (int i = 1; i < n; i++) {
				updateStats(a[i] = nextLong());
				a[i] += a[i - 1];
			}
			return a;
		}

		public long[][] nextLongMat(int h, int w) {
			long[][] a = new long[h][w];
			setAll(a, x -> nextLong(w));
			return a;
		}

		public long[][] nextLongMatSum(int h, int w) {
			long[][] a = new long[h + 1][w + 1];
			for (int i = 1; i <= h; i++)
				for (int j = 1; j <= w; j++) a[i][j] = nextLong() + a[i - 1][j] + a[i][j - 1] - a[i - 1][j - 1];
			return a;
		}

		public long[][][] nextLong3D(int x, int y, int z) {
			long[][][] a = new long[x][y][z];
			setAll(a, b -> nextLongMat(y, z));
			return a;
		}

		public long[][][] nextLongSum3D(int x, int y, int z) {
			long[][][] e = new long[x + 1][y + 1][z + 1];
			for (int a = 1; a <= x; a++)
				for (int b = 1; b <= y; b++)
					for (int c = 1; c <= z; c++)
						e[a][b][c] = nextLong() + e[a - 1][b][c] + e[a][b - 1][c] + e[a][b][c - 1] - e[a - 1][b - 1][c] - e[a - 1][b][c - 1] - e[a][b - 1][c - 1] + e[a - 1][b - 1][c - 1];
			return e;
		}

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

		public ArrayList<Integer> nextIntAL(int n) {
			return nextIntCollection(n, ArrayList::new);
		}

		public LinkedList<Integer> nextIntLL(int n) {
			return nextIntCollection(n, LinkedList::new);
		}

		public HashSet<Integer> nextIntHS(int n) {
			return nextIntCollection(n, HashSet::new);
		}

		public LinkedHashSet<Integer> nextIntLHS(int n) {
			return nextIntCollection(n, LinkedHashSet::new);
		}

		public TreeSet<Integer> nextIntTS(int n) {
			return nextIntCollection(n, TreeSet::new);
		}

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

		public ArrayList<Long> nextLongAL(int n) {
			return nextLongCollection(n, ArrayList::new);
		}

		public LinkedList<Long> nextLongLL(int n) {
			return nextLongCollection(n, LinkedList::new);
		}

		public HashSet<Long> nextLongHS(int n) {
			return nextLongCollection(n, HashSet::new);
		}

		public LinkedHashSet<Long> nextLongLHS(int n) {
			return nextLongCollection(n, LinkedHashSet::new);
		}

		public TreeSet<Long> nextLongTS(int n) {
			return nextLongCollection(n, TreeSet::new);
		}

		private <T extends Collection<String>> T nextStringCollection(int n, Supplier<T> s) {
			T c = s.get();
			while (n-- > 0) {
				c.add(next());
			}
			return c;
		}

		public ArrayList<String> nextStringAL(int n) {
			return nextStringCollection(n, ArrayList::new);
		}

		public LinkedList<String> nextStringLL(int n) {
			return nextStringCollection(n, LinkedList::new);
		}

		public HashSet<String> nextStringHS(int n) {
			return nextStringCollection(n, HashSet::new);
		}

		public LinkedHashSet<String> nextStringLHS(int n) {
			return nextStringCollection(n, LinkedHashSet::new);
		}

		public TreeSet<String> nextStringTS(int n) {
			return nextStringCollection(n, TreeSet::new);
		}

		private <T extends Collection<Character>> T nextCharacterCollection(int n, Supplier<T> s) {
			T c = s.get();
			resetStats();
			while (n-- > 0) {
				char t = nextChar();
				c.add(t);
				updateStats(t);
			}
			return c;
		}

		public ArrayList<Character> nextCharacterAL(int n) {
			return nextCharacterCollection(n, ArrayList::new);
		}

		public LinkedList<Character> nextCharacterLL(int n) {
			return nextCharacterCollection(n, LinkedList::new);
		}

		public HashSet<Character> nextCharacterHS(int n) {
			return nextCharacterCollection(n, HashSet::new);
		}

		public LinkedHashSet<Character> nextCharacterLHS(int n) {
			return nextCharacterCollection(n, LinkedHashSet::new);
		}

		public TreeSet<Character> nextCharacterTS(int n) {
			return nextCharacterCollection(n, TreeSet::new);
		}

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

		public HashMap<Integer, Integer> nextIntMultisetHM(int n) {
			return nextIntMultiset(n, HashMap::new);
		}

		public LinkedHashMap<Integer, Integer> nextIntMultisetLHM(int n) {
			return nextIntMultiset(n, LinkedHashMap::new);
		}

		public TreeMap<Integer, Integer> nextIntMultisetTM(int n) {
			return nextIntMultiset(n, TreeMap::new);
		}

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

		public HashMap<Long, Integer> nextLongMultisetHM(int n) {
			return nextLongMultiset(n, HashMap::new);
		}

		public LinkedHashMap<Long, Integer> nextLongMultisetLHM(int n) {
			return nextLongMultiset(n, LinkedHashMap::new);
		}

		public TreeMap<Long, Integer> nextLongMultisetTM(int n) {
			return nextLongMultiset(n, TreeMap::new);
		}

		private <T extends Map<String, Integer>> T nextStringMultiset(int n, Supplier<T> s) {
			T c = s.get();
			while (n-- > 0) {
				String a = next();
				c.put(a, c.getOrDefault(a, 0) + 1);
			}
			return c;
		}

		public HashMap<String, Integer> nextStringMultisetHM(int n) {
			return nextStringMultiset(n, HashMap::new);
		}

		public LinkedHashMap<String, Integer> nextStringMultisetLHM(int n) {
			return nextStringMultiset(n, LinkedHashMap::new);
		}

		public TreeMap<String, Integer> nextStringMultisetTM(int n) {
			return nextStringMultiset(n, TreeMap::new);
		}

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

		public HashMap<Character, Integer> nextCharMultisetHM(int n) {
			return nextCharMultiset(n, HashMap::new);
		}

		public LinkedHashMap<Character, Integer> nextCharacterMultisetLHM(int n) {
			return nextCharMultiset(n, LinkedHashMap::new);
		}

		public TreeMap<Character, Integer> nextCharMultisetTM(int n) {
			return nextCharMultiset(n, TreeMap::new);
		}

		public long getLowestNum() {
			return low;
		}

		public long getHighestNum() {
			return high;
		}

		public long getSum() {
			return sum;
		}

		private void resetStats() {
			low = Long.MAX_VALUE;
			high = Long.MIN_VALUE;
			sum = 0;
		}

		private void updateStats(long a) {
			sum += a;
			low = min(low, a);
			high = max(high, a);
		}
	}

	/**
	 * 標準出力を管理するクラスです。<p>
	 * デフォルトではautoFlushはfalseであり、出力内容をStringBuilderに蓄積して一括出力します。<p>
	 * - autoFlushが有効な場合、出力は即座に標準出力に書き込まれ、StringBuilderは使用されません。<p>
	 * - 内部でStringBuilderを使用しているため、スレッドセーフではありません。<p>
	 * - 大量の出力が必要な場合は、autoFlushを無効にすることを推奨します。
	 */
	@SuppressWarnings("unused")
	private static final class FastPrinter {
		private final StringBuilder sb;
		private final boolean autoFlush;
		private int capacity;


		/**
		 * デフォルトの設定でFastPrinterを初期化します。<p>
		 * バッファ容量: 65536 <p>
		 * autoFlush: false
		 */
		public FastPrinter() {
			this(65536, false);
		}

		/**
		 * 指定されたバッファ容量でFastPrinterを初期化します。<p>
		 * autoFlushはfalseに設定されます。
		 *
		 * @param capacity バッファの初期容量（文字単位）
		 */
		public FastPrinter(int capacity) {
			this(capacity, false);
		}

		/**
		 * autoFlushの設定を指定してFastPrinterを初期化します。<p>
		 * バッファ容量: 65536
		 *
		 * @param autoFlush trueの場合、出力は即座に標準出力に書き込まれます。
		 *                  falseの場合、出力はStringBuilderに蓄積されます。
		 */
		public FastPrinter(boolean autoFlush) {
			this(65536, autoFlush);
		}

		/**
		 * 指定されたバッファ容量とautoFlushの設定でFastPrinterを初期化します。
		 *
		 * @param capacity  バッファの初期容量（文字単位）
		 * @param autoFlush trueの場合、出力は即座に標準出力に書き込まれます。
		 *                  falseの場合、出力はStringBuilderに蓄積されます。
		 */
		public FastPrinter(int capacity, boolean autoFlush) {
			this.autoFlush = autoFlush;
			this.capacity = capacity;
			sb = autoFlush ? new StringBuilder(capacity) : null;
		}

		/**
		 * 改行を一つ出力します。
		 */
		public FastPrinter println() {
			print("\n");
			return this;
		}

		/**
		 * 指定されたObjectを出力します（改行付き）。
		 *
		 * @param o Object
		 */
		public FastPrinter println(Object o) {
			print(o).println();
			return this;
		}

		/**
		 * 指定されたStringを出力します（改行付き）。
		 *
		 * @param s String
		 */
		public FastPrinter println(String s) {
			print(s).println();
			return this;
		}

		/**
		 * 指定されたbooleanを出力します（改行付き）。
		 *
		 * @param f boolean
		 */
		public FastPrinter println(boolean f) {
			print(f).println();
			return this;
		}

		/**
		 * 指定されたcharを出力します（改行付き）。
		 *
		 * @param c char
		 */
		public FastPrinter println(char c) {
			print(c).println();
			return this;
		}

		/**
		 * 指定されたintを出力します（改行付き）。
		 *
		 * @param a int
		 */
		public FastPrinter println(int a) {
			print(a).println();
			return this;
		}

		/**
		 * 指定されたlongを出力します（改行付き）。
		 *
		 * @param a long
		 */
		public FastPrinter println(long a) {
			print(a).println();
			return this;
		}

		/**
		 * 指定されたdoubleを出力します（改行付き）。
		 *
		 * @param a double
		 */
		public FastPrinter println(double a) {
			print(a).println();
			return this;
		}

		/**
		 * 指定された桁数でdoubleを出力します（改行付き）。<p>
		 * 出力は小数点以下n桁に丸められます。<p>
		 * 丸め処理には四捨五入を使用します。
		 *
		 * @param a double
		 * @param n int 小数点以下の桁数（0以上）
		 */
		public FastPrinter println(double a, int n) {
			print(a, n).println();
			return this;
		}

		/**
		 * 2つの整数をそれぞれ改行して出力します。
		 *
		 * @param a int
		 * @param b int
		 */
		public FastPrinter println(int a, int b) {
			println(a).println(b);
			return this;
		}

		/**
		 * 2つの整数をそれぞれ改行して出力します。
		 *
		 * @param a int
		 * @param b long
		 */
		public FastPrinter println(int a, long b) {
			println(a).println(b);
			return this;
		}

		/**
		 * 2つの整数をそれぞれ改行して出力します。
		 *
		 * @param a long
		 * @param b int
		 */
		public FastPrinter println(long a, int b) {
			println(a).println(b);
			return this;
		}

		/**
		 * 2つの整数をそれぞれ改行して出力します。
		 *
		 * @param a long
		 * @param b long
		 */
		public FastPrinter println(long a, long b) {
			println(a).println(b);
			return this;
		}

		/**
		 * 指定された配列の各要素を改行区切りで出力し、行末は改行します。
		 *
		 * @param o Object...
		 */
		public FastPrinter println(Object... o) {
			for (Object x : o)
				println(x);
			return this;
		}

		/**
		 * 指定された配列の各要素を改行区切りで出力し、行末は改行します。
		 *
		 * @param s String[]
		 */
		public FastPrinter println(String[] s) {
			for (String x : s)
				println(x);
			return this;
		}

		/**
		 * 指定された配列の各要素を改行区切りで出力し、行末は改行します。
		 *
		 * @param c char[]
		 */
		public FastPrinter println(char[] c) {
			for (char x : c)
				println(x);
			return this;
		}

		/**
		 * 指定された配列の各要素を改行区切りで出力し、行末は改行します。
		 *
		 * @param a int[]
		 */
		public FastPrinter println(int[] a) {
			for (int x : a)
				println(x);
			return this;
		}

		/**
		 * 指定された配列の各要素を改行区切りで出力し、行末は改行します。
		 *
		 * @param a long[]
		 */
		public FastPrinter println(long[] a) {
			for (long x : a)
				println(x);
			return this;
		}

		/**
		 * 指定された二次元配列を出力し、行末は改行します。<p>
		 * 各列を半角スペースで区切り、行ごとに改行します。
		 *
		 * @param o Object[][]
		 */
		public FastPrinter println(Object[][] o) {
			for (Object[] x : o)
				print(x).println();
			return this;
		}

		/**
		 * 指定された二次元配列を出力し、行末は改行します。<p>
		 * 各列を半角スペースで区切り、行ごとに改行します。
		 *
		 * @param c char[][]
		 */
		public FastPrinter println(char[][] c) {
			for (char[] x : c)
				print(x).println();
			return this;
		}

		/**
		 * 指定された二次元配列を出力し、行末は改行します。<p>
		 * 各列を半角スペースで区切り、行ごとに改行します。
		 *
		 * @param a int[][]
		 */
		public FastPrinter println(int[][] a) {
			for (int[] x : a)
				print(x).println();
			return this;
		}

		/**
		 * 指定された二次元配列を出力し、行末は改行します。<p>
		 * 各列を半角スペースで区切り、行ごとに改行します。
		 *
		 * @param a long[][]
		 */
		public FastPrinter println(long[][] a) {
			for (long[] x : a)
				print(x).println();
			return this;
		}

		/**
		 * 指定された二次元配列を出力し行末は改行します。
		 * 各列を区切り文字無しで出力し、行ごとに改行します。
		 *
		 * @param c char[][]
		 */
		public FastPrinter printChars(char[][] c) {
			for (char[] x : c)
				printChars(x).println();
			return this;
		}

		/**
		 * 指定されたObjectを出力します（改行無し）。
		 *
		 * @param o Object
		 */
		public FastPrinter print(Object o) {
			if (autoFlush) {
				System.out.println(o);
			} else {
				String str = o.toString();
				isFull(str.length());
				sb.append(str);
			}
			return this;
		}

		/**
		 * 指定されたStringを出力します（改行無し）。
		 *
		 * @param s String
		 */
		public FastPrinter print(String s) {
			if (autoFlush) {
				System.out.println(s);
			} else {
				isFull(s.length());
				sb.append(s);
			}
			return this;
		}

		/**
		 * 指定されたbooleanを出力します（改行無し）。
		 *
		 * @param f boolean
		 */
		public FastPrinter print(boolean f) {
			if (autoFlush) {
				System.out.println(f);
			} else {
				String str = f ? "true" : "false";
				isFull(str.length());
				sb.append(str);
			}
			return this;
		}

		/**
		 * 指定されたcharを出力します（改行無し）。
		 *
		 * @param c char
		 */
		public FastPrinter print(char c) {
			if (autoFlush) {
				System.out.println(c);
			} else {
				isFull(1);
				sb.append(c);
			}
			return this;
		}

		/**
		 * 指定されたintを出力します（改行無し）。
		 *
		 * @param a int
		 */
		public FastPrinter print(int a) {
			if (autoFlush) {
				System.out.println(a);
			} else {
				isFull(10);
				sb.append(a);
			}
			return this;
		}

		/**
		 * 指定されたlongを出力します（改行無し）。
		 *
		 * @param a long
		 */
		public FastPrinter print(long a) {
			if (autoFlush) {
				System.out.println(a);
			} else {
				isFull(20);
				sb.append(a);
			}
			return this;
		}

		/**
		 * 指定されたdoubleを出力します（改行無し）。
		 *
		 * @param a double
		 */
		public FastPrinter print(double a) {
			if (autoFlush) {
				System.out.println(a);
			} else {
				isFull(20);
				sb.append(a);
			}
			return this;
		}

		/**
		 * 指定された桁数でdoubleを出力します（改行無し）。<p>
		 * 出力は小数点以下n桁に丸められます。<p>
		 * 丸め処理には四捨五入を使用します。
		 *
		 * @param a double
		 * @param n int 小数点以下の桁数（0以上）
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
		 * 指定された配列の各要素を半角スペース区切りで出力し、行末は改行しません。
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
		 * 指定された配列の各要素を半角スペース区切りで出力し、行末は改行しません。
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
		 * 指定された配列の各要素を半角スペース区切りで出力し、行末は改行しません。
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
		 * 指定された配列の各要素を半角スペース区切りで出力し、行末は改行しません。
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
		 * 指定された配列の各要素を半角スペース区切りで出力し、行末は改行しません。
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
		 * 指定された配列の各要素を区切り文字無しで出力し、行末は改行しません。。
		 *
		 * @param c char[]
		 */
		public FastPrinter printChars(char[] c) {
			if (autoFlush) {
				System.out.println(c);
			} else {
				isFull(c.length);
				sb.append(c);
			}
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
		 * 現在のバッファに保持している出力内容を逆順に並べ替えます。
		 */
		public FastPrinter reverse() {
			sb.reverse();
			return this;
		}

		/**
		 * 現在のバッファに保持している全ての出力内容を標準出力に出力し、バッファをクリアします。
		 */
		public FastPrinter flush() {
			System.out.print(sb);
			clear();
			return this;
		}

		/**
		 * StringBuilderの初期化を行います。
		 */
		public FastPrinter clear() {
			sb.setLength(0);
			return this;
		}

		/**
		 * StringBuilderの容量を指定した値に確保します。
		 * 指定した容量が現在の容量より小さい場合、変更は行われません。
		 *
		 * @param capacity 確保する容量（文字単位）
		 */
		public FastPrinter ensureCapacity(int capacity) {
			if (this.capacity >= capacity) return this;
			this.capacity = capacity;
			sb.ensureCapacity(capacity);
			return this;
		}

		private void isFull(int length) {
			if (sb.length() + length > sb.capacity()) flush();
		}
	}

}
