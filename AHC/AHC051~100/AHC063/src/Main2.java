import java.io.*;
import java.util.concurrent.*;

import static java.util.Arrays.*;

public final class Main2 {

	// region < Constants & Globals >
	private static final long startTime = System.currentTimeMillis();
	private static final byte[] di = new byte[]{0, -1, 0, 1};
	private static final byte[] dj = new byte[]{-1, 0, 1, 0};
	private static final FastScanner sc = new FastScanner();
	private static final FastPrinter out = new FastPrinter();
	private static byte[] init, target, moveMask;
	private static byte[][] nextIdx;
	private static long[] ZOBRIST_SNAKE, ZOBRIST_HEAD;
	private static long[][] ZOBRIST_GRID;
	private static int n, nn, m, bitWords;
	private static int nextScore;
	private static long nextHash;
	// endregion

	private static void solve() {
		init();
		beamSearch();
	}

	private static int beamSearch() {
		int w = 10000;
		BeamQueue cur = new BeamQueue(w);
		BeamQueue next = new BeamQueue(w);
		Ouroboros best = new Ouroboros(init);
		cur.push(best);
		LongHashSet visited = new LongHashSet();
		for (int turn = 0; turn < 100000; turn++) {
			long elapsed = System.currentTimeMillis() - startTime;
			if (elapsed > 1900) break;
			int curW = Math.max(50, (int) (w * (2000 - elapsed) / 2000));
			for (int s = 0, sz = cur.size(); s < sz; s++) {
				Ouroboros state = cur.get(s);
				for (byte d = 0; d < 4; d++) {
					if (!state.canMove(d)) continue;
					state.evalNextUnchecked(d);
					boolean isFull = next.size() == curW;
					if (isFull && nextScore >= next.getMaxScore()) continue;
					if (visited.add(nextHash)) {
						Ouroboros nextState = state.copy();
						nextState.moveUnchecked(d);
						if (best.score > nextState.score) best = nextState;
						if (nextState.len == m && nextState.e == 0) continue;
						next.pushOrReplace(nextState, isFull);
					}
				}
			}
			if (next.isEmpty()) break;
			BeamQueue temp = cur;
			cur = next;
			next = temp;
			next.clear();
			visited.clear();
		}
		best.print();
		return best.score;
	}

	private static void init() {
		n = sc.nextInt();
		nn = n * n;
		bitWords = (nn + 63) >>> 6;
		m = sc.nextInt();
		int c = sc.nextInt();
		target = new byte[m];
		for (int i = 0; i < m; i++) target[i] = (byte) (sc.nextChar() - '0');
		init = new byte[nn];
		for (int i = 0; i < nn; i++) init[i] = (byte) (sc.nextChar() - '0');

		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		ZOBRIST_GRID = new long[c + 1][nn];
		ZOBRIST_SNAKE = new long[nn];
		ZOBRIST_HEAD = new long[nn];
		for (int i = 0; i < nn; i++) {
			ZOBRIST_SNAKE[i] = rnd.nextLong();
			ZOBRIST_HEAD[i] = rnd.nextLong();
		}
		for (int i = 0; i <= c; i++) setAll(ZOBRIST_GRID[i], _ -> rnd.nextLong());
		nextIdx = new byte[nn][4];
		moveMask = new byte[nn];
		for (int i = 0, ij = 0; i < n; i++) {
			for (int j = 0; j < n; j++, ij++) {
				byte mask = 0;
				for (byte d = 0; d < 4; d++) {
					int ni = i + di[d], nj = j + dj[d];
					if (isValidRange((byte) ni, (byte) nj, n, n)) {
						nextIdx[ij][d] = (byte) (ni * n + nj);
						mask |= (byte) (1 << d);
					}
				}
				moveMask[ij] = mask;
			}
		}
	}

	private static boolean isValidRange(final byte i, final byte j, final int h, final int w) {
		return ((i | j | (h - 1 - i) | (w - 1 - j)) >>> 31) == 0;
	}

	// region < I/O & Core >
	public static void main(final String[] args) {
		try {
			solve();
		} finally {
			sc.close();
			out.close();
		}
	}

	private static final class Ouroboros implements Comparable<Ouroboros> {
		private static final byte[] opc = {'L', 'U', 'R', 'D'};
		private final byte[] index, color, grid;
		private final long[] snakeBits;
		private SearchNode cur;
		private int head, len, e, t, score;
		private long hash;

		public Ouroboros(final byte[] init) {
			index = new byte[m];
			color = new byte[m];
			grid = new byte[nn];
			snakeBits = new long[bitWords];
			System.arraycopy(init, 0, grid, 0, nn);
			head = 4;
			len = 5;
			for (int i = 0; i < 5; i++) {
				color[i] = 1;
				byte idx = (byte) (i * n);
				index[i] = idx;
				setSnake(idx & 0xFF);
			}
			score = 10000 * (2 * (m - len));
			cur = null;
			long h = 0;
			h ^= ZOBRIST_HEAD[index[head] & 0xFF];
			for (int i = 0; i < nn; i++) {
				h ^= ZOBRIST_GRID[grid[i]][i];
				if (isSnake(i)) h ^= ZOBRIST_SNAKE[i];
			}
			this.hash = h;
		}

		private Ouroboros(SearchNode cur) {
			index = new byte[m];
			color = new byte[m];
			grid = new byte[nn];
			snakeBits = new long[bitWords];
			this.cur = cur;
		}

		public Ouroboros copy() {
			Ouroboros o = new Ouroboros(cur);
			System.arraycopy(index, 0, o.index, 0, m);
			System.arraycopy(color, 0, o.color, 0, len);
			System.arraycopy(grid, 0, o.grid, 0, nn);
			System.arraycopy(snakeBits, 0, o.snakeBits, 0, bitWords);
			o.e = e;
			o.t = t;
			o.head = head;
			o.len = len;
			o.score = score;
			o.hash = hash;
			return o;
		}

		public boolean canMove(final byte d) {
			int oldHead = index[head] & 0xFF;
			if (((moveMask[oldHead] >>> d) & 1) == 0) return false;
			int idx = nextIdx[oldHead][d] & 0xFF;
			int neck = head == 0 ? m - 1 : head - 1;
			return (index[neck] & 0xFF) != idx;
		}

		public void evalNextUnchecked(final byte d) {
			int old = index[head] & 0xFF;
			int idx = nextIdx[old][d] & 0xFF;

			nextHash = hash ^ ZOBRIST_HEAD[old] ^ ZOBRIST_HEAD[idx];
			nextScore = score + 1;
			int nextLen = len, nextE = e;
			int h = head == m - 1 ? 0 : head + 1;
			if (grid[idx] != 0) {
				byte eat = grid[idx];
				nextHash ^= ZOBRIST_GRID[eat][idx] ^ ZOBRIST_GRID[0][idx];
				if (target[nextLen] != eat) {
					nextE++;
					nextScore -= 10000;
				} else {
					nextScore -= 20000;
				}
				nextLen++;
				nextHash ^= ZOBRIST_SNAKE[idx];
			} else {
				int oldTailPos = h - nextLen;
				if (oldTailPos < 0) oldTailPos += m;
				int oldTail = index[oldTailPos] & 0xFF;
				nextHash ^= ZOBRIST_SNAKE[oldTail];

				boolean hitBody = idx != oldTail && isSnake(idx);
				if (hitBody) {
					int tail = h - nextLen + 1;
					if (tail < 0) tail += m;
					while ((index[tail] & 0xFF) != idx) {
						int i = index[tail] & 0xFF;
						byte ci = color[--nextLen];
						if (target[nextLen] != ci) {
							nextE--;
							nextScore -= 10000;
						}
						nextScore += 20000;
						nextHash ^= ZOBRIST_SNAKE[i] ^ ZOBRIST_GRID[0][i] ^ ZOBRIST_GRID[ci][i];
						if (++tail == m) tail = 0;
					}
				}
				if (!hitBody) nextHash ^= ZOBRIST_SNAKE[idx];
			}
			nextHash ^= ((long) nextLen << 32) ^ (nextE * 0x9E3779B97F4A7C15L) ^ idx;
		}

		public void moveUnchecked(final byte d) {
			int old = index[head] & 0xFF;
			int idx = nextIdx[old][d] & 0xFF;
			hash ^= ZOBRIST_HEAD[old] ^ ZOBRIST_HEAD[idx];
			cur = new SearchNode(cur, opc[d]);
			t++;
			if (++head == m) head = 0;
			if (grid[idx] != 0) {
				byte eat = grid[idx];
				hash ^= ZOBRIST_GRID[eat][idx] ^ ZOBRIST_GRID[0][idx];
				if (target[len] != (color[len++] = eat)) e++;
				grid[idx] = 0;
			} else {
				int oldTailPos = head - len;
				if (oldTailPos < 0) oldTailPos += m;
				int oldTail = index[oldTailPos] & 0xFF;
				clearSnake(oldTail);
				hash ^= ZOBRIST_SNAKE[oldTail];
				if (isSnake(idx)) {
					int tail = head - len + 1;
					if (tail < 0) tail += m;
					while ((index[tail] & 0xFF) != idx) {
						int i = index[tail] & 0xFF;
						byte ci = color[--len];
						if (target[len] != (grid[i] = ci)) e--;
						clearSnake(i);
						hash ^= ZOBRIST_SNAKE[i] ^ ZOBRIST_GRID[0][i] ^ ZOBRIST_GRID[ci][i];
						if (++tail == m) tail = 0;
					}
				}
			}
			if (!isSnake(idx)) hash ^= ZOBRIST_SNAKE[idx];
			index[head] = (byte) idx;
			setSnake(idx);

			score = t + 10000 * (e + 2 * (m - len));
		}

		private boolean isSnake(final int idx) {
			return (snakeBits[idx >>> 6] & (1L << (idx & 63))) != 0;
		}

		private void setSnake(final int idx) {
			snakeBits[idx >>> 6] |= 1L << (idx & 63);
		}

		private void clearSnake(final int idx) {
			snakeBits[idx >>> 6] &= ~(1L << (idx & 63));
		}

		public void print() {
			SearchNode p = cur;
			byte[] ans = new byte[t];
			int loop = t;
			while (p != null) {
				ans[--loop] = p.op;
				p = p.prev;
			}
			out.println(ans);
		}

		public int compareTo(final Ouroboros o) {
			return Integer.compare(score, o.score);
		}

		private record SearchNode(SearchNode prev, byte op) {
		}
	}

	private static final class BeamQueue {
		private final Ouroboros[] buf;
		private int size, unsortedCount;

		public BeamQueue(int capacity) {
			buf = new Ouroboros[capacity];
			size = 0;
			unsortedCount = 0;
		}

		private void push(Ouroboros v) {
			buf[size++] = v;
			unsortedCount++;
		}

		public int size() {
			return size;
		}

		public Ouroboros get(final int index) {
			return buf[index];
		}

		public void pushOrReplace(final Ouroboros v, final boolean isFull) {
			if (!isFull) {
				push(v);
			} else {
				if (unsortedCount > 0) ensureHeapProperty();
				buf[0] = v;
				siftDown(buf[0], 0);
			}
		}

		public int getMaxScore() {
			if (unsortedCount > 0) ensureHeapProperty();
			return buf[0].score;
		}

		public void clear() {
			size = 0;
			unsortedCount = 0;
		}

		public boolean isEmpty() {
			return size == 0;
		}

		private void ensureHeapProperty() {
			final int log2N = 31 - Integer.numberOfLeadingZeros(size);
			final int heapifyCost = size * 2 - 2 * log2N;
			final int incrementalCost = unsortedCount <= 100 ? getIncrementalCostStrict() : getIncrementalCostApprox();
			if (heapifyCost < incrementalCost) {
				heapify();
			} else {
				for (int i = size - unsortedCount; i < size; i++) siftUp(buf[i], i);
			}
			unsortedCount = 0;
		}

		private int getIncrementalCostStrict() {
			int totalCost = 0;
			final int sortedSize = size - unsortedCount;
			for (int i = 1; i <= unsortedCount; i++) {
				final int currentHeapSize = sortedSize + i;
				final int depth = 31 - Integer.numberOfLeadingZeros(currentHeapSize);
				totalCost += depth;
			}
			return totalCost;
		}

		private int getIncrementalCostApprox() {
			final int sortedSize = size - unsortedCount;
			final int avgHeapSize = sortedSize + (unsortedCount >> 1);
			if (avgHeapSize == 0) return 0;
			final int depthOfAvgSize = 31 - Integer.numberOfLeadingZeros(avgHeapSize);
			return unsortedCount * depthOfAvgSize;
		}

		private void heapify() {
			for (int i = (size >> 1) - 1; i >= 0; i--) siftDown(buf[i], i);
		}

		private void siftUp(final Ouroboros v, int i) {
			final Ouroboros[] b = buf;
			while (i > 0) {
				final int j = (i - 1) >> 1;
				if (v.compareTo(b[j]) <= 0) break;
				b[i] = b[j];
				i = j;
			}
			b[i] = v;
		}

		private void siftDown(final Ouroboros v, int i) {
			final Ouroboros[] b = buf;
			final int n = size;
			final int half = n >> 1;
			while (i < half) {
				int child = (i << 1) + 1;
				if (child + 1 < n && b[child].compareTo(b[child + 1]) < 0) child++;
				if (v.compareTo(b[child]) >= 0) break;
				b[i] = b[child];
				i = child;
			}
			b[i] = v;
		}
	}

	private static final class LongHashSet {
		private final int[] stamp;
		private final long[] keys;
		private final int mask;
		private int curStamp;

		public LongHashSet() {
			int capacity = 1 << 20;
			stamp = new int[capacity];
			keys = new long[capacity];
			mask = capacity - 1;
			curStamp = 1;
		}

		public boolean add(long key) {
			int idx = Long.hashCode(key) & mask;
			while (stamp[idx] == curStamp) {
				if (keys[idx] == key) return false;
				idx = (idx + 1) & mask;
			}
			stamp[idx] = curStamp;
			keys[idx] = key;
			return true;
		}

		public void clear() {
			curStamp++;
		}
	}

	private static final class FastScanner implements AutoCloseable {
		private final InputStream in;
		private final byte[] buffer;
		private int pos = 0;

		public FastScanner() {
			this.in = System.in;
			try {
				buffer = in.readAllBytes();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		private int skipSpaces() {
			int p = pos, b;
			do {
				b = buffer[p++];
			} while (b <= 32);
			pos = p;
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

		public char nextChar() {
			return (char) skipSpaces();
		}

		public int nextInt() {
			int b = skipSpaces();
			boolean negative = false;
			if (b == '-') {
				negative = true;
				b = buffer[pos++];
			}
			int p = pos;
			int n = 0;
			do {
				n = (n << 3) + (n << 1) + (b & 15);
				b = buffer[p++];
			} while (b > 32);
			pos = p;
			return negative ? -n : n;
		}
	}

	private static final class FastPrinter implements AutoCloseable {
		private final OutputStream out;
		private final byte[] buffer;
		private int pos = 0;

		public FastPrinter() {
			this.out = System.out;
			this.buffer = new byte[200000];
		}

		public void println(final byte[] arr) {
			for (byte b : arr) {
				buffer[pos++] = b;
				buffer[pos++] = '\n';
			}
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

	}
	// endregion
}
