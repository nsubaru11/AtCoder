import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.math.BigInteger;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public class C {

	private static void solve(final FastScanner sc, final FastPrinter out) {

	}

	public static void main(String[] args) {
		try (final FastScanner sc = new FastScanner();
			 final FastPrinter out = new FastPrinter()) {
			solve(sc, out);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * int型RingBufferクラス:
	 * このクラスは固定長の整数型リングバッファを実装しており、先頭・末尾の追加／削除、ランダムアクセス、ストリーム変換などを提供します。
	 */
	private static final class RingBuffer implements Iterable<Integer> {
		private final int capacity; // バッファの最大サイズ
		private final int[] buf; // データを格納する配列
		private int head, size; // データの先頭を表すインデックスとデータの要素数

		/**
		 * 指定されたサイズで新しいRingBufferを初期化します。
		 *
		 * @param capacity バッファの最大サイズ。(int)
		 * @throws IllegalArgumentException サイズが0以下の場合
		 */
		public RingBuffer(final int capacity) {
			if (capacity <= 0)
				throw new IllegalArgumentException();
			this.capacity = capacity;
			buf = new int[capacity];
			head = size = 0;
		}

		/**
		 * バッファの末尾に要素を追加します。
		 *
		 * @param e 追加する要素
		 * @return バッファに正常に追加された場合はtrueを返します。 すでに満杯の場合はfalseを返します。
		 */
		public boolean addLast(final int e) {
			if (isFull())
				return false;
			buf[physicalIndex(size++)] = e;
			return true;
		}

		/**
		 * バッファの先頭に要素を追加
		 *
		 * @param e 追加する要素
		 * @return バッファに正常に追加された場合はtrueを返します。 すでに満杯の場合はfalseを返します。
		 */
		public boolean addFirst(final int e) {
			if (isFull())
				return false;
			head = physicalIndex(capacity - 1);
			buf[head] = e;
			size++;
			return true;
		}

		/**
		 * 指定したインデックスの要素を取得します。 負のインデックスは末尾からの相対位置を表す。
		 *
		 * @param index 取得する要素のインデックス。-size <= index < sizeを満たす必要があります。
		 * @return 指定されたインデックスの要素
		 * @throws RingBufferIndexException バッファが空の場合、またはインデックスが範囲外の場合
		 */
		public int get(int index) {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			if (index >= size || index < -size)
				throw new RingBufferIndexException(index, -size, size - 1);
			if (index < 0)
				index += size;
			return buf[physicalIndex(index)];
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
			return buf[physicalIndex(--size)];
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
			size--;
			head = physicalIndex(1);
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
			return buf[physicalIndex(size - 1)];
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
		 * 指定されたインデックスの要素を上書きします。
		 *
		 * @param index 上書きする要素のインデックス。-size <= index < sizeを満たす必要があります。
		 * @param e     新しい要素
		 * @return このインスタンス自体を返します。
		 * @throws RingBufferIndexException バッファが空の場合、またはインデックスが範囲外の場合
		 */
		public RingBuffer set(int index, final int e) {
			if (isEmpty())
				throw new RingBufferIndexException("Buffer is empty.");
			if (index < -size || size <= index)
				throw new RingBufferIndexException(index, -size, size - 1);
			if (index < 0)
				index += size;
			buf[physicalIndex(index)] = e;
			return this;
		}

		/**
		 * 現在のバッファの長さを変更します。 長さが増加する場合、新しい位置には0が挿入されます。
		 *
		 * @param newLen 新しい長さ（0 <= newLen <= sizeを満たす必要があります）
		 * @return このインスタンス自体を返します。
		 * @throws RingBufferIndexException 指定された長さが範囲外の場合
		 */
		public RingBuffer setLength(int newLen) {
			if (newLen > capacity || newLen < 0)
				throw new RingBufferIndexException(newLen, capacity);
			if (size < newLen) {
				while (size < newLen) {
					addLast(0);
				}
			} else {
				size = newLen;
			}
			return this;
		}

		/**
		 * 指定した要素がバッファに含まれているかを調べます。
		 *
		 * @param e 検索対象の要素
		 * @return 指定した要素が含まれている場合はtrue、含まれていない場合はfalse
		 */
		public boolean contains(final int e) {
			return stream().anyMatch(i -> i == e);
		}

		/**
		 * バッファが空かどうかを返します。
		 *
		 * @return 現在の要素数が0の場合はtrue、それ以外の場合はfalse
		 */
		public boolean isEmpty() {
			return size == 0;
		}

		/**
		 * バッファが満杯かどうかを返します。
		 *
		 * @return 現在の要素数がバッファの最大サイズに達している場合はtrue、それ以外の場合はfalse
		 */
		public boolean isFull() {
			return size == capacity;
		}

		/**
		 * 現在のバッファ内の要素数を返します。
		 *
		 * @return 現在のバッファ内の要素数（0以上size以下）
		 */
		public int size() {
			return size;
		}

		/**
		 * バッファの最大容量を返します。
		 *
		 * @return バッファの最大容量
		 */
		public int capacity() {
			return capacity;
		}

		/**
		 * バッファにこれ以上追加できる残りの容量を返します。
		 *
		 * @return 現在の要素数を差し引いた、バッファに追加可能な空き容量
		 */
		public int remainingCapacity() {
			return capacity - size;
		}

		/**
		 * 指定された関数を使用してバッファ内の全要素を初期化します。
		 *
		 * @param generator 要素を生成する関数。インデックス（0からsize-1まで）を引数として受け取り、初期化する値を返します。
		 * @return このインスタンス自体を返します。
		 */
		public RingBuffer setAll(final IntFunction<Integer> generator) {
			head = 0;
			size = capacity;
			Arrays.setAll(buf, generator::apply);
			return this;
		}

		/**
		 * 指定された値でバッファ全体を初期化します。
		 *
		 * @param e バッファの全ての要素を埋める値
		 * @return このインスタンス自体を返します。
		 */
		public RingBuffer fill(final int e) {
			head = 0;
			size = capacity;
			Arrays.fill(buf, e);
			return this;
		}

		/**
		 * バッファ内の全ての要素を削除し、空の状態にします。
		 *
		 * @return このインスタンス
		 */
		public RingBuffer clear() {
			head = size = 0;
			return this;
		}

		/**
		 * このRingBufferインスタンスのクローンを作成します。 クローンされたインスタンスは元のインスタンスの独立したコピーです。
		 *
		 * @return クローンされたRingBufferインスタンス
		 **/
		public RingBuffer clone() {
			RingBuffer rb = new RingBuffer(capacity);
			System.arraycopy(buf, 0, rb.buf, 0, capacity);
			rb.head = head;
			rb.size = size;
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
			if (this == o)
				return true;
			if (!(o instanceof RingBuffer other))
				return false;
			return size == other.size && IntStream.range(0, size)
					.allMatch(i -> buf[physicalIndex(i)] == other.buf[other.physicalIndex(i)]);
		}

		/**
		 * バッファ内の要素をスペースで区切った文字列として返します。
		 *
		 * @return バッファ内の要素を表す文字列。バッファが空の場合は空文字列
		 */
		public String toString() {
			return stream().mapToObj(Integer::toString).collect(Collectors.joining(" "));
		}

		/**
		 * このRingBufferの論理状態に基づくハッシュコードを返します。 論理状態とは、バッファ内の現在有効な要素（headからsize分）の順序を指します。
		 *
		 * @return 論理状態に基づいたハッシュコード
		 */
		public int hashCode() {
			if (isEmpty())
				return 0;
			int result = 1;
			for (int element : this)
				result = 31 * result + element;
			return result;
		}

		/**
		 * バッファの論理状態（headからsize個の要素）を含む新しいIntStreamを返します。
		 *
		 * @return 現在のバッファ内容を含むIntStream
		 */
		public IntStream stream() {
			return IntStream.range(0, size).map(i -> buf[physicalIndex(i)]);
		}

		/**
		 * バッファの論理状態（headからsize個の要素）を含む新しい並列IntStreamを返します。
		 *
		 * @return 現在のバッファ内容を含む並列IntStream
		 */
		public IntStream parallelStream() {
			return stream().parallel();
		}

		/**
		 * バッファの論理状態（headからsize個の要素）を含む新しいint配列を返します。
		 *
		 * @return 現在のバッファ内容を含む配列
		 */
		public int[] toArray() {
			return stream().toArray();
		}

		/**
		 * 指定された配列にバッファの論理状態をコピーして返します。 渡された配列のサイズが足りない場合は、新しい配列が生成されます。
		 *
		 * @param data コピー先の配列
		 * @return バッファ内容がコピーされた配列
		 */
		public int[] toArray(final int[] data) {
			if (data.length < size)
				return toArray();
			for (int i = 0; i < size; i++)
				data[i] = buf[physicalIndex(i)];
			return data;
		}

		/**
		 * バッファの論理状態（headからsize個の要素）を含むリストを返します。
		 *
		 * @return バッファ内の要素のリスト
		 */
		public List<Integer> toList() {
			return IntStream.range(0, size).mapToObj(i -> buf[physicalIndex(i)]).toList();
		}

		/**
		 * このRingBufferの論理状態（headからsize個の要素）を走査するIteratorを返します。
		 *
		 * @return バッファ内の要素を順に返すIterator
		 */
		public Iterator<Integer> iterator() {
			return new Iterator<>() {
				private int index = 0;

				public boolean hasNext() {
					return index < size;
				}

				public Integer next() {
					if (!hasNext())
						throw new NoSuchElementException();
					return buf[physicalIndex(index++)];
				}
			};
		}

		/**
		 * 論理インデックスから実インデックスを取得します。
		 *
		 * @param logicalIndex 論理インデックス
		 * @return 実インデックス
		 */
		private int physicalIndex(int logicalIndex) {
			return (head + logicalIndex) % capacity;
		}

		/**
		 * RingBufferに関連する例外クラス。 主にインデックスや長さの不正使用時にスローされます。
		 */
		private static final class RingBufferIndexException extends RuntimeException {

			@Serial
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

	/**
	 * 汎用数学クラス
	 */
	private static final class MathFn {

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
		 * a / b以上で最小の長整数を返します。
		 *
		 * @param a 割られる値(long)
		 * @param b 割る値(long)
		 * @return ⌈a / b⌉
		 */
		public static long ceilLong(long a, long b) {
			return a < 0 ? a / b : (a + b - 1) / b;
		}

		/**
		 * a / b以上で最小の整数を返します。
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
				if ((b & 1) == 1) ans = ans * a % mod;
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
				if ((b & 1) == 1) ans *= a;
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
			long[] SmallFactorials = {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};
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
			long numer = 1;
			long denom = 1;
			r = min(n - r, r);
			for (int i = 1; i <= r; i++) {
				numer *= n - i;
				denom *= i + 1;
			}
			return numer / denom;
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
			return comb(n, r) % mod;
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
		 * @param a  long
		 * @param b  long
		 * @param xy long[]
		 * @return |x| + |y|の最小値
		 */
		public static long exGCD(long a, long b, long[] xy) {
			if (b == 0) {
				xy[0] = 1;
				xy[1] = 0;
				return a;
			}
			long[] tmp = new long[2];
			long d = exGCD(b, a % b, tmp);
			xy[0] = tmp[0];
			xy[1] = tmp[1] - (a / b) * tmp[0];
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
	private static final class UnionFind {
		private final int[] root, rank, size, path;
		private int cnt;

		public UnionFind(int n) {
			cnt = n;
			root = new int[n];
			rank = new int[n];
			size = new int[n];
			path = new int[n];
			for (int i = 0; i < n; i++) {
				size[i] = 1;
				root[i] = i;
			}
		}

		/**
		 * 引数の頂点の代表元を取得します。
		 *
		 * @param n 頂点
		 * @return 頂点xの代表元
		 */
		public int find(int n) {
			return n != root[n] ? root[n] = find(root[n]) : root[n];
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
		public Map<Integer, List<Integer>> groups() {
			Map<Integer, List<Integer>> groups = new HashMap<>(cnt);
			for (int i = 0; i < root.length; i++) {
				groups.computeIfAbsent(find(i), k -> new ArrayList<>()).add(i);
			}
			return groups;
		}
	}

	/**
	 * 整数と長整数に対して通常の二分探索、上限探索(Upper Bound)、下限探索(Lower Bound)を行うための抽象クラスです。
	 * 探索に失敗した際の戻り値は-(挿入位置(境界値) - 1)となっています。
	 */
	private static abstract class AbstractBinarySearch {

		/**
		 * 整数範囲での通常の二分探索を行います。comparatorが0を返した時点で探索を終了します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含まない)
		 * @return int
		 */
		public final int normalSearch(int l, int r) {
			return binarySearch(l, r - 1, SearchType.NORMAL);
		}

		/**
		 * 長整数範囲での通常の二分探索を行います。comparatorが0を返した時点で探索を終了します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含まない)
		 * @return long
		 */
		public final long normalSearch(long l, long r) {
			return binarySearch(l, r - 1, SearchType.NORMAL);
		}

		/**
		 * 整数範囲での上限探索(Upper Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含まない)
		 * @return 条件にちょうど当てはまる整数。もしくは上限値+1(挿入位置)。
		 */
		public final int upperBoundSearch(int l, int r) {
			return binarySearch(l, r - 1, SearchType.UPPER_BOUND);
		}

		/**
		 * 長整数範囲での上限探索(Upper Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含まない)
		 * @return 条件にちょうど当てはまる長整数。もしくは上限値+1(挿入位置)。
		 */
		public final long upperBoundSearch(long l, long r) {
			return binarySearch(l, r - 1, SearchType.UPPER_BOUND);
		}

		/**
		 * 整数範囲での下限探索(Lower Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含まない)
		 * @return 条件にちょうど当てはまる整数。もしくは下限値-1(挿入位置)。
		 */
		public final int lowerBoundSearch(int l, int r) {
			return binarySearch(l, r - 1, SearchType.LOWER_BOUND);
		}

		/**
		 * 長整数範囲での下限探索(Lower Bound)を行います。 目的の条件にちょうど当てはまる際にcomparatorが0を返すことが好ましい。
		 * comparatorが0を返した際、その値を記憶します。
		 *
		 * @param l 下限値 (この数を含む)
		 * @param r 上限値 (この数を含まない)
		 * @return 条件にちょうど当てはまる長整数。もしくは下限値-1(挿入位置)。
		 */
		public final long lowerBoundSearch(long l, long r) {
			return binarySearch(l, r - 1, SearchType.LOWER_BOUND);
		}

		/**
		 * 整数範囲での汎用二分探索メソッド
		 */
		private int binarySearch(int l, int r, SearchType type) {
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
		private long binarySearch(long l, long r, SearchType type) {
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
	 * 順列列挙に関するクラスです。
	 */
	private static class Permutation {

		/**
		 * 辞書順で次の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(int[] arr) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i] < arr[i + 1]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i] < arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で次の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(int[] arr, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i] < arr[i + 1]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i] < arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で前の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(int[] arr) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i] > arr[i + 1]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i] > arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で前の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(int[] arr, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i] > arr[i + 1]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i] > arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で次の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(long[] arr) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i] < arr[i + 1]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i] < arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で次の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(long[] arr, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i] < arr[i + 1]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i] < arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で前の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(long[] arr) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i] > arr[i + 1]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i] > arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で前の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(long[] arr, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i] > arr[i + 1]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i] > arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で次の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(char[] arr) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i] < arr[i + 1]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i] < arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で次の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(char[] arr, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i] < arr[i + 1]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i] < arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で前の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(char[] arr) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i] > arr[i + 1]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i] > arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で前の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(char[] arr, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i] > arr[i + 1]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i] > arr[j]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		private static void swap(final int[] a, int i, int j) {
			int swap = a[i];
			a[i] = a[j];
			a[j] = swap;
		}

		private static void swap(final long[] a, int i, int j) {
			long swap = a[i];
			a[i] = a[j];
			a[j] = swap;
		}

		private static void swap(final char[] a, int i, int j) {
			char swap = a[i];
			a[i] = a[j];
			a[j] = swap;
		}

		private static void reverseRange(final int[] a, int i, int j) {
			while (i < j) swap(a, i++, --j);
		}

		private static void reverseRange(final long[] a, int i, int j) {
			while (i < j) swap(a, i++, --j);
		}

		private static void reverseRange(final char[] a, int i, int j) {
			while (i < j) swap(a, i++, --j);
		}

		/**
		 * 辞書順で次の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(int[][] arr, int idx) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i][idx] < arr[i + 1][idx]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i][idx] < arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で次の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(int[][] arr, int idx, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i][idx] < arr[i + 1][idx]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i][idx] < arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で前の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(int[][] arr, int idx) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i][idx] > arr[i + 1][idx]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i][idx] > arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で前の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(int[][] arr, int idx, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i][idx] > arr[i + 1][idx]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i][idx] > arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で次の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(long[][] arr, int idx) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i][idx] < arr[i + 1][idx]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i][idx] < arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で次の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(long[][] arr, int idx, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i][idx] < arr[i + 1][idx]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i][idx] < arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で前の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(long[][] arr, int idx) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i][idx] > arr[i + 1][idx]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i][idx] > arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で前の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(long[][] arr, int idx, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i][idx] > arr[i + 1][idx]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i][idx] > arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で次の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(char[][] arr, int idx) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i][idx] < arr[i + 1][idx]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i][idx] < arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で次の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で次に当たる配列がある場合はtrue、arrが降順に並んでいるならfalse
		 */
		public static boolean next(char[][] arr, int idx, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i][idx] < arr[i + 1][idx]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i][idx] < arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 辞書順で前の順列に並び替えます。
		 *
		 * @param arr 並び替え対象の配列
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(char[][] arr, int idx) {
			int len = arr.length;
			for (int i = len - 2; i >= 0; --i) {
				if (arr[i][idx] > arr[i + 1][idx]) {
					for (int j = len - 1; i < j; --j) {
						if (arr[i][idx] > arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, len);
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * 指定した範囲内の要素を、辞書順で前の順列に並び替えます。
		 *
		 * @param arr     並び替え対象の配列
		 * @param fromIdx 始点_include
		 * @param toIdx   終点_exclude
		 * @return 辞書順で前に当たる配列がある場合はtrue、arrが昇順に並んでいるならfalse
		 */
		public static boolean prev(char[][] arr, int idx, int fromIdx, int toIdx) {
			for (int i = toIdx - 2; i >= fromIdx; --i) {
				if (arr[i][idx] > arr[i + 1][idx]) {
					for (int j = toIdx - 1; i < j; --j) {
						if (arr[i][idx] > arr[j][idx]) {
							swap(arr, i, j);
							reverseRange(arr, i + 1, toIdx);
							return true;
						}
					}
				}
			}
			return false;
		}

		private static void swap(int[][] a, int i, int j) {
			int[] swap = a[i];
			a[i] = a[j];
			a[j] = swap;
		}

		private static void swap(long[][] a, int i, int j) {
			long[] swap = a[i];
			a[i] = a[j];
			a[j] = swap;
		}

		private static void swap(char[][] a, int i, int j) {
			char[] swap = a[i];
			a[i] = a[j];
			a[j] = swap;
		}

		private static void reverseRange(final int[][] a, int i, int j) {
			while (i < j) swap(a, i++, --j);
		}

		private static void reverseRange(final long[][] a, int i, int j) {
			while (i < j) swap(a, i++, --j);
		}

		private static void reverseRange(final char[][] a, int i, int j) {
			while (i < j) swap(a, i++, --j);
		}

	}

	/**
	 * ダイクストラ法
	 */
	private static final class Dijkstra {
		private static final long INF = Long.MAX_VALUE;
		private final int v;
		private final long[] ans;
		private final List<List<Edge>> edges;
		private int used = -1;

		public Dijkstra(int v) {
			this.v = v;
			edges = new ArrayList<>(v);
			ans = new long[v];
			for (int i = 0; i < v; i++) {
				edges.add(new ArrayList<>());
			}
		}

		public void addEdge(int i, int j, long cost) {
			edges.get(i).add(new Edge(i, j, cost));
			used = -1;
		}

		public long getShortestPathWeight(int i, int j) {
			if (used != i) {
				used = i;
				fill(ans, INF);
				ans[i] = 0;
				PriorityQueue<Vertex> pq = new PriorityQueue<>();
				pq.add(new Vertex(i, ans[i]));
				while (!pq.isEmpty()) {
					Vertex vertex = pq.poll();
					int from = vertex.index;
					if (ans[from] < vertex.cost)
						continue;
					for (Edge e : edges.get(from)) {
						long cost = e.cost;
						int to = e.to;
						if (ans[from] != INF && ans[to] > ans[from] + cost) {
							ans[to] = ans[from] + cost;
							pq.add(new Vertex(to, ans[to]));
						}
					}
				}
			}
			return ans[j];
		}

		private static final class Edge {
			int from, to;
			long cost;

			Edge(int i, int j, long c) {
				from = i;
				to = j;
				cost = c;
			}
		}

		private static final class Vertex implements Comparable<Vertex> {
			int index;
			long cost;

			Vertex(int index, long cost) {
				this.index = index;
				this.cost = cost;
			}

			@Override
			public int compareTo(Vertex o) {
				return Long.compare(cost, o.cost);
			}
		}

	}

	/**
	 * ベルマンフォード法
	 */
	private static final class BellmanFord {
		private static final long INF = Long.MAX_VALUE;
		private final ArrayList<Edge> edges;
		private final int v;
		private final long[] ans;
		private int used = -1;

		public BellmanFord(int v, int e) {
			this.v = v;
			edges = new ArrayList<>(e);
			ans = new long[v];
		}

		public void addEdge(int i, int j, long cost) {
			edges.add(new Edge(i, j, cost));
			used = -1;
		}

		public long getShortestPathWeight(int i, int j) {
			if (used != i) {
				used = i;
				fill(ans, INF);
				ans[i] = 0;
				boolean update = false;
				for (int vtx = 0; vtx < v; vtx++) {
					update = false;
					for (Edge e : edges) {
						int from = e.from;
						int to = e.to;
						long cost = e.cost;
						if (ans[from] != INF && ans[to] > ans[from] + cost) {
							ans[to] = ans[from] + cost;
							update = true;
						}
					}
					if (!update)
						break;
				}
				if (update)
					return -INF;
			}
			return ans[j];
		}

		private static final class Edge {
			int from, to;
			long cost;

			Edge(int i, int j, long c) {
				from = i;
				to = j;
				cost = c;
			}
		}

	}

	/**
	 * ワーシャルフロイド法
	 */
	private static final class Warshallfroyd {
		private static final long INF = Long.MAX_VALUE;
		private final int v;
		private final long[][] dist;

		public Warshallfroyd(int v) {
			this.v = v;
			dist = new long[v][v];
			for (int i = 0; i < v; i++) {
				fill(dist[i], INF);
				dist[i][i] = 0;
			}
		}

		public void addEdge(int from, int to, long cost) {
			dist[from][to] = cost;
		}

		public long[][] dist() {
			for (int via = 0; via < v; via++) {
				for (int from = 0; from < v; from++) {
					if (dist[from][via] == INF)
						continue;
					for (int to = 0; to < v; to++) {
						if (dist[via][to] == INF)
							continue;
						dist[from][to] = min(dist[from][to], dist[from][via] + dist[via][to]);
						if (from == to && dist[from][to] != 0)
							return null;
					}
				}
			}
			return dist;
		}

	}

	/**
	 * 素数に関するクラス
	 */
	private static final class PrimeNumber {
		private static final int INF = Integer.MAX_VALUE;

		/**
		 * 素数判定
		 *
		 * @param n long
		 * @return {@code true}なら素数、{@code false}なら合成数
		 */
		public static boolean isPrimeNum(long n) {
			if (n <= 1) return false;
			if (n == 2 || n == 3) return true;
			if (n % 2L == 0 || n % 3L == 0) return false;
			for (long i = 5L; i * i <= n; i += 6L) {
				if (n % i == 0 || n % (i + 2L) == 0) return false;
			}
			return true;
		}

		/**
		 * 素数の可能性が高いかを判定
		 * {@code k}の値が大きいほど精度が上がります。
		 * 合成数であることが確定している場合は{@code false}を返します。
		 *
		 * @param n 判定する数
		 * @param k 精度を決めるパラメータ
		 * @return {@code true}なら素数の可能性が高く、{@code false}なら合成数
		 */
		public static boolean isProbablePrime(long n, int k) {
			BigInteger a = BigInteger.valueOf(n);
			return (a.isProbablePrime(k));
		}

		/**
		 * 2以上n以下の素数の個数を返します。
		 *
		 * @param n 上限値
		 * @return 素数の個数
		 */
		public static int elements(int n) {
			return elements(2, n);
		}

		/**
		 * n以上k以下の素数の個数を返します。
		 *
		 * @param min 下限値
		 * @param max 上限値
		 * @return 素数の個数
		 */
		public static int elements(int min, int max) {
			BitSet table = new BitSet(max + 1);
			int count = 0;
			if (2 > min) count++;
			if (3 > min) count++;
			for (int i = 4; i <= max; i += 2) table.set(i);
			for (int i = 9; i <= max; i += 6) table.set(i);
			for (int i = 5; i <= max; i += 6) {
				for (int j = i; j <= i + 2 && j <= max; j += 2) {
					if (!table.get(j)) {
						if (min <= j) count++;
						for (long k = (long) j * j; k <= max; k += (long) j + j) {
							table.set((int) k);
						}
					}
				}
			}
			return count;
		}

		/**
		 * 2以上n以下の素数をセットで返します。
		 *
		 * @param n 上限値
		 * @return 素数のセット
		 */
		public static Set<Integer> Set(int n) {
			return Eratosthenes(2, n, HashSet::new);
		}

		/**
		 * n以上k以下の素数をセットで返します。
		 *
		 * @param min 下限値
		 * @param max 上限値
		 * @return 素数のセット
		 */
		public static Set<Integer> Set(int min, int max) {
			return Eratosthenes(min, max, HashSet::new);
		}

		/**
		 * 2以上n以下の素数をリストで返します。
		 *
		 * @param n 上限値
		 * @return 素数のリスト
		 */
		public static List<Integer> List(int n) {
			return Eratosthenes(2, n, ArrayList::new);
		}

		/**
		 * n以上k以下の素数をリストで返します。
		 *
		 * @param min 下限値
		 * @param max 上限値
		 * @return 素数のリスト
		 */
		public static List<Integer> List(int min, int max) {
			return Eratosthenes(min, max, ArrayList::new);
		}

		private static <T extends Collection<Integer>> T Eratosthenes(int min, int max, Supplier<T> supplier) {
			T primeNum = supplier.get();
			BitSet table = new BitSet(max + 1);
			if (min <= 2) primeNum.add(2);
			if (min <= 3) primeNum.add(3);
			for (int i = 4; i <= max; i += 2) table.set(i);
			for (int i = 9; i <= max; i += 6) table.set(i);
			for (int i = 5; i <= max; i += 6) {
				for (int j = i; j <= i + 2 && j <= max; j += 2) {
					if (!table.get(j)) {
						if (min <= j) primeNum.add(j);
						for (long k = (long) j * j; k <= max; k += (long) j + j) {
							table.set((int) k);
						}
					}
				}
			}
			return primeNum;
		}
	}

	/**
	 * 標準入力を高速に処理するためのクラスです。
	 */
	@SuppressWarnings("unused")
	private static final class FastScanner implements AutoCloseable {
		private static final int DEFAULT_BUFFER_SIZE = 65536;
		private static final InputStream in = System.in;
		private final byte[] buffer;
		private int pos = 0, cnt = 0;

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

		public void close() throws Exception {
			in.close();
		}
	}

	/**
	 * 標準出力を高速に処理するクラスです。
	 * ※注意：このクラスは内部バッファが満杯になると自動的に出力されますが、
	 * 処理の途中で結果をすぐに反映させる必要がある場合は、明示的に flush() を呼び出す必要があります。
	 */
	@SuppressWarnings("unused")
	private static final class FastPrinter implements AutoCloseable {
		private static final int DEFAULT_BUFFER_SIZE = 65536;
		private static final OutputStream out = System.out;
		private final byte[] buffer;
		private int pos = 0;

		// ------------------ コンストラクタ ------------------

		/**
		 * デフォルトの設定でFastPrinterを初期化します。<p>
		 * バッファ容量: 65536 <p>
		 * autoFlush: false（バッファが満杯になるとflushされます。）
		 */
		public FastPrinter() {
			this(DEFAULT_BUFFER_SIZE);
		}

		/**
		 * 指定されたバッファ容量でFastPrinterを初期化します。<p>
		 * autoFlush: false（バッファが満杯になるとflushされます。）
		 *
		 * @param size バッファの初期容量（文字単位）
		 */
		public FastPrinter(int size) {
			buffer = new byte[max(size, 64)];
		}

		// --------------- 出力メソッド（改行付き） ---------------

		/**
		 * 改行のみ出力
		 */
		public void println() {
			if (pos >= buffer.length)
				flushBuffer();
			buffer[pos++] = '\n';
		}

		/**
		 * Objectを出力（改行付き）
		 */
		public void println(Object o) {
			println(o.toString());
		}

		/**
		 * charを出力（改行付き）
		 */
		public void println(char c) {
			if (pos + 1 >= buffer.length)
				flushBuffer();
			buffer[pos++] = (byte) c;
			buffer[pos++] = '\n';
		}

		/**
		 * Stringを出力（改行付き）
		 */
		public void println(String s) {
			print(s);
			println();
		}

		/**
		 * boolean を出力（true→"Yes", false→"No", 改行付き）
		 */
		public void println(boolean b) {
			if (pos + 3 >= buffer.length)
				flushBuffer();
			if (b) {
				buffer[pos++] = 'Y';
				buffer[pos++] = 'e';
				buffer[pos++] = 's';
			} else {
				buffer[pos++] = 'N';
				buffer[pos++] = 'o';
			}
			buffer[pos++] = '\n';
		}

		/**
		 * intを出力（改行付き）
		 */
		public void println(int i) {
			if (pos + 11 >= buffer.length)
				flushBuffer();
			fillBuffer(i);
			buffer[pos++] = '\n';
		}

		/**
		 * longを出力（改行付き）
		 */
		public void println(long l) {
			if (pos + 20 >= buffer.length)
				flushBuffer();
			fillBuffer(l);
			buffer[pos++] = '\n';
		}

		/**
		 * doubleを出力（改行付き）
		 */
		public void println(double d) {
			println(Double.toString(d));
		}

		/**
		 * 指定された桁数で double を出力（改行付き）
		 * 小数点以下 n 桁に丸め（四捨五入）
		 */
		public void println(double d, int n) {
			print(d, n);
			println();
		}

		/**
		 * 2 つの整数（int, int）をそれぞれ改行して出力
		 */
		public void println(int a, int b) {
			println(a);
			println(b);
		}

		/**
		 * 2 つの整数（int, long）をそれぞれ改行して出力
		 */
		public void println(int a, long b) {
			println(a);
			println(b);
		}

		/**
		 * 2 つの整数（long, int）をそれぞれ改行して出力
		 */
		public void println(long a, int b) {
			println(a);
			println(b);
		}

		/**
		 * 2 つの整数（long, long）をそれぞれ改行して出力
		 */
		public void println(long a, long b) {
			println(a);
			println(b);
		}

		/**
		 * 可変長の Object 配列の各要素を改行区切りで出力
		 */
		public void println(Object... o) {
			for (Object x : o)
				println(x.toString());
		}

		/**
		 * String 配列の各要素を改行区切りで出力
		 */
		public void println(String[] s) {
			for (String x : s)
				println(x);
		}

		/**
		 * char 配列の各要素を改行区切りで出力
		 */
		public void println(char[] c) {
			int i = 0;
			while (i < c.length) {
				if (pos >= buffer.length)
					flushBuffer();
				int limit = min((buffer.length - pos) >> 1, c.length - i);
				while (limit-- > 0) {
					buffer[pos++] = (byte) c[i++];
					buffer[pos++] = '\n';
				}
			}
		}

		/**
		 * int 配列の各要素を改行区切りで出力
		 */
		public void println(int[] a) {
			for (int x : a) {
				println(x);
			}
		}

		/**
		 * long 配列の各要素を改行区切りで出力
		 */
		public void println(long[] a) {
			for (long x : a) {
				println(x);
			}
		}

		/**
		 * 二次元 Object 配列を、各行を半角スペース区切りで出力（行末は改行）
		 */
		public void println(Object[][] o) {
			for (Object[] x : o) {
				print(x);
				println();
			}
		}

		/**
		 * 二次元 char 配列を、各行を半角スペース区切りで出力（行末は改行）
		 */
		public void println(char[][] c) {
			for (char[] x : c) {
				print(x);
				println();
			}
		}

		/**
		 * 二次元 int 配列を、各行を半角スペース区切りで出力（行末は改行）
		 */
		public void println(int[][] a) {
			for (int[] x : a) {
				print(x);
				println();
			}
		}


		/**
		 * 二次元 long 配列を、各行を半角スペース区切りで出力（行末は改行）
		 */
		public void println(long[][] a) {
			for (long[] x : a) {
				print(x);
				println();
			}
		}

		/**
		 * 二次元 char 配列を、各行を区切り文字無しで出力（各行末に改行）
		 */
		public void printChars(char[][] c) {
			for (char[] x : c) {
				printChars(x);
				println();
			}
		}

		//---------------- 出力メソッド（改行無し） ----------------

		/**
		 * Object を出力（改行無し）
		 */
		public void print(Object o) {
			print(o.toString());
		}

		/**
		 * char を出力（改行無し）
		 */
		public void print(char c) {
			if (pos >= buffer.length)
				flushBuffer();
			buffer[pos++] = (byte) c;
		}

		/**
		 * String を出力（改行無し）
		 */
		public void print(String s) {
			int i = 0;
			while (i < s.length()) {
				if (pos >= buffer.length)
					flushBuffer();
				int limit = min(buffer.length - pos, s.length() - i);
				while (limit-- > 0) {
					buffer[pos++] = (byte) s.charAt(i++);
				}
			}
		}

		/**
		 * int を出力（改行無し）
		 */
		public void print(int i) {
			if (pos + 10 >= buffer.length)
				flushBuffer();
			fillBuffer(i);
		}

		/**
		 * long を出力（改行無し）
		 */
		public void print(long l) {
			if (pos + 19 >= buffer.length)
				flushBuffer();
			fillBuffer(l);
		}

		/**
		 * double を出力（改行無し）
		 */
		public void print(double d) {
			print(Double.toString(d));
		}

		/**
		 * boolean を出力（true→"Yes", false→"No", 改行無し）
		 */
		public void print(boolean b) {
			if (pos + 2 >= buffer.length)
				flushBuffer();
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
		 * 指定された桁数で double を出力（改行無し）
		 * 小数点以下 n 桁に丸め（四捨五入）
		 */
		public void print(double a, int n) {
			if (n == 0) {
				print(round(a));
				return;
			}
			if (a < 0) {
				print('-');
				a = -a;
			}
			a += pow(10, -n) / 2;
			print((long) a);
			if (pos + n >= buffer.length)
				flushBuffer();
			buffer[pos++] = '.';
			a -= (long) a;
			while (n-- > 0) {
				a *= 10;
				buffer[pos++] = (byte) ((int) a + '0');
				a -= (int) a;
			}
		}

		/**
		 * 2 つの整数（int, int）を半角スペース区切りで出力（改行無し）
		 */
		public void print(int a, int b) {
			print(a);
			print(' ');
			print(b);
		}

		/**
		 * 2 つの整数（int, long）を半角スペース区切りで出力（改行無し）
		 */
		public void print(int a, long b) {
			print(a);
			print(' ');
			print(b);
		}

		/**
		 * 2 つの整数（long, int）を半角スペース区切りで出力（改行無し）
		 */
		public void print(long a, int b) {
			print(a);
			print(' ');
			print(b);
		}

		/**
		 * 2 つの整数（long, long）を半角スペース区切りで出力（改行無し）
		 */
		public void print(long a, long b) {
			print(a);
			print(' ');
			print(b);
		}

		/**
		 * 可変長の Object 配列の各要素を半角スペース区切りで出力（改行無し）
		 */
		public void print(Object... o) {
			if (o.length == 0) return;
			print(o[0]);
			for (int i = 1; i < o.length; i++) {
				print(' ');
				print(o[i]);
			}
		}

		/**
		 * String 配列の各要素を半角スペース区切りで出力（改行無し）
		 */
		public void print(String[] s) {
			print(s[0]);
			for (int i = 1; i < s.length; i++) {
				print(' ');
				print(s[i]);
			}
		}

		/**
		 * char 配列の各要素を半角スペース区切りで出力（改行無し）
		 */
		public void print(char[] c) {
			print(c[0]);
			for (int i = 1; i < c.length; i++) {
				if (pos + 1 >= buffer.length)
					flushBuffer();
				buffer[pos++] = ' ';
				buffer[pos++] = (byte) c[i];
			}
		}

		/**
		 * int 配列の各要素を半角スペース区切りで出力（改行無し）
		 */
		public void print(int[] a) {
			print(a[0]);
			for (int i = 1; i < a.length; i++) {
				if (pos + 11 >= buffer.length)
					flushBuffer();
				buffer[pos++] = ' ';
				fillBuffer(a[i]);
			}
		}

		/**
		 * long 配列の各要素を半角スペース区切りで出力（改行無し）
		 */
		public void print(long[] a) {
			print(a[0]);
			for (int i = 1; i < a.length; i++) {
				if (pos + 20 >= buffer.length)
					flushBuffer();
				buffer[pos++] = ' ';
				fillBuffer(a[i]);
			}
		}

		/**
		 * char 配列の各要素を区切り文字無しで出力（改行無し）
		 */
		public void printChars(char[] c) {
			int i = 0;
			while (i < c.length) {
				if (pos >= buffer.length)
					flushBuffer();
				int limit = min(buffer.length - pos, c.length - i);
				while (limit-- > 0) {
					buffer[pos++] = (byte) c[i++];
				}
			}
		}

		/**
		 * フォーマットを指定して出力（改行無し）
		 */
		public void printf(String format, Object... args) {
			print(String.format(format, args));
		}

		/**
		 * フォーマットを指定して出力します。指定された言語環境での整形を行います。（改行無し）
		 */
		public void printf(Locale locale, String format, Object... args) {
			print(String.format(locale, format, args));
		}

		// ------------------ バッファの管理 ------------------

		/**
		 * 現在のバッファに保持している全ての出力内容を標準出力に書き出し、バッファをクリアします。
		 */
		public void flush() {
			try {
				if (pos > 0) {
					out.write(buffer, 0, pos);
				}
				out.flush();
			} catch (IOException ignored) {
			}
			pos = 0;
		}

		/**
		 * 指定された int 値を文字列に変換し、buffer に格納します。
		 * 10進数表現により、負の値の場合は先頭に '-' を付けます。
		 *
		 * @param i int
		 */
		private void fillBuffer(int i) {
			int p = 0;
			byte[] tmp;
			if (i == Integer.MIN_VALUE) {
				buffer[pos++] = '-';
				tmp = new byte[]{'8', '4', '6', '3', '8', '4', '7', '4', '1', '2'};
				p = 10;
			} else {
				if (i < 0) {
					buffer[pos++] = '-';
					i = -i;
				}
				tmp = new byte[10];
				do {
					tmp[p++] = (byte) ((i % 10) + '0');
				} while ((i /= 10) > 0);
			}
			while (p-- > 0) {
				buffer[pos++] = tmp[p];
			}
		}

		/**
		 * 指定された long 値を文字列に変換し、buffer に格納します。
		 * 10進数表現により、負の値の場合は先頭に '-' を付けます。
		 *
		 * @param l long
		 */
		private void fillBuffer(long l) {
			int p = 0;
			byte[] tmp;
			if (l == Long.MIN_VALUE) {
				buffer[pos++] = '-';
				tmp = new byte[]{'8', '0', '8', '5', '7', '7', '4', '5', '8', '6', '3', '0', '2', '7', '3', '3', '2', '2', '9'};
				p = 19;
			} else {
				if (l < 0) {
					buffer[pos++] = '-';
					l = -l;
				}
				tmp = new byte[19];
				do {
					tmp[p++] = (byte) ((l % 10) + '0');
				} while ((l /= 10) > 0);
			}
			while (p-- > 0) {
				buffer[pos++] = tmp[p];
			}
		}

		/**
		 * 現在の buffer の内容を標準出力に書き出し、バッファをクリアします。
		 */
		private void flushBuffer() {
			try {
				out.write(buffer, 0, pos);
				pos = 0;
			} catch (IOException ignore) {
			}
		}

		public void close() throws Exception {
			flush();
			out.close();
		}
	}
}
