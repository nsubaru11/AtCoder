import java.io.*;
import java.util.concurrent.*;

import static java.util.Arrays.*;

public final class Main3 {

	// region < Constants & Globals >
	private static final int MAX_TURN = 100000;
	private static final long startTime = System.currentTimeMillis();
	private static final byte[] di = new byte[]{0, -1, 0, 1};
	private static final byte[] dj = new byte[]{-1, 0, 1, 0};
	private static final FastScanner sc = new FastScanner();
	private static final FastPrinter out = new FastPrinter();
	private static byte[] init, target, moveMask;
	private static byte[][] nextIdx;
	private static int[][] distToColor;
	private static int n, nn, m;
	private static int nextScore, nextRank;
	private static long nextHash;
	private static ZobristHash zobristHash;
	// endregion

	private static Ouroboros beamSearch(int mnW, int mxW) {
		BeamFrontier cur = new BeamFrontier(65536);
		BeamFrontier next = new BeamFrontier(65536);
		Ouroboros best = new Ouroboros(init);
		cur.push(best);
		boolean solved = false;
		for (int turn = 0; turn < MAX_TURN; turn++) {
			long elapsed = System.currentTimeMillis() - startTime;
			if (elapsed > 1900) break;
			int curW = Math.max(mnW, (int) (mxW * (2000 - elapsed) / 2000));
			for (int s = 0, sz = cur.size(); s < sz; s++) {
				Ouroboros state = cur.get(s);
				if (best.score > state.score) best = state;
				for (byte d = 0; d < 4; d++) {
					if (!state.canMove(d)) continue;
					state.evalNextUnchecked(d);
					if (next.size() == curW && !next.containsHash(nextHash) && next.isWorseOrEqualThanWorst(nextScore, nextRank)) continue;
					Ouroboros nextState = state.copy();
					nextState.moveUnchecked(d);
					if (best.score > nextState.score) best = nextState;
					if (nextState.len == m && nextState.e == 0) {
						solved = true;
						continue;
					}
					next.upsert(nextState, curW);
				}
			}
			if (solved || next.isEmpty()) break;
			BeamFrontier temp = cur;
			cur = next;
			next = temp;
			next.clear();
		}
		best.print();
		// System.out.println(best.score);
		return best;
	}

	private static void init() {
		n = sc.nextInt();
		nn = n * n;
		m = sc.nextInt();
		int c = sc.nextInt();
		target = new byte[m];
		for (int i = 0; i < m; i++) target[i] = (byte) (sc.nextChar() - '0');
		init = new byte[nn];
		for (int i = 0; i < nn; i++) init[i] = (byte) (sc.nextChar() - '0');

		zobristHash = new ZobristHash(c, nn);
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
		buildDistToColor(c);
	}

	private static void buildDistToColor(final int c) {
		distToColor = new int[c + 1][nn];
		int[] queue = new int[nn];
		for (int col = 1; col <= c; col++) {
			int[] dist = distToColor[col];
			fill(dist, 1 << 29);
			int head = 0, tail = 0;
			for (int i = 0; i < nn; i++) {
				if (init[i] == col) {
					dist[i] = 0;
					queue[tail++] = i;
				}
			}
			while (head < tail) {
				int cur = queue[head++];
				int cd = dist[cur] + 1;
				for (byte d = 0; d < 4; d++) {
					if (((moveMask[cur] >>> d) & 1) == 0) continue;
					int nxt = nextIdx[cur][d] & 0xFF;
					if (dist[nxt] <= cd) continue;
					dist[nxt] = cd;
					queue[tail++] = nxt;
				}
			}
		}
	}

	private static boolean isValidRange(final byte i, final byte j, final int h, final int w) {
		return ((i | j | (h - 1 - i) | (w - 1 - j)) >>> 31) == 0;
	}

	// region < I/O & Core >
	public static void main(final String[] args) {
		try {
			init();
			if (args.length == 2) {
				beamSearch(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			} else {
				beamSearch(100, 10000);
			}
		} finally {
			sc.close();
			out.close();
		}
	}

	private static final class Ouroboros implements Comparable<Ouroboros> {
		private static final byte[] opc = {'L', 'U', 'R', 'D'};
		private final byte[] index, color, grid;
		private final byte[] snakeCount;
		private SearchNode cur;
		private int head, len, e, t, score, rank;
		private long hash;

		public Ouroboros(final byte[] init) {
			index = new byte[m];
			color = new byte[m];
			grid = new byte[nn];
			snakeCount = new byte[nn];
			System.arraycopy(init, 0, grid, 0, nn);
			head = 4;
			len = 5;
			for (int i = 0; i < 5; i++) {
				color[i] = 1;
				byte idx = (byte) (i * n);
				index[i] = idx;
				setSnake(idx & 0xFF);
			}
			score = calcScore(0, 0, len);
			rank = heuristic(index[head] & 0xFF, len);
			cur = null;
			long h = 0;
			h ^= zobristHash.head(index[head] & 0xFF);
			for (int i = 0; i < nn; i++) {
				h ^= zobristHash.grid(grid[i], i);
				if (isSnake(i)) h ^= zobristHash.snake(i);
			}
			this.hash = h;
		}

		private Ouroboros(SearchNode cur) {
			index = new byte[m];
			color = new byte[m];
			grid = new byte[nn];
			snakeCount = new byte[nn];
			this.cur = cur;
		}

		public Ouroboros copy() {
			Ouroboros o = new Ouroboros(cur);
			System.arraycopy(index, 0, o.index, 0, m);
			System.arraycopy(color, 0, o.color, 0, len);
			System.arraycopy(grid, 0, o.grid, 0, nn);
			System.arraycopy(snakeCount, 0, o.snakeCount, 0, nn);
			o.e = e;
			o.t = t;
			o.head = head;
			o.len = len;
			o.score = score;
			o.rank = rank;
			o.hash = hash;
			return o;
		}

		private int heuristic(final int headIdx, final int nextLen) {
			if (nextLen >= m) return 0;
			int need = target[nextLen];
			int d1 = distToColor[need][headIdx];
			if (d1 >= (1 << 28)) return 30000;
			int score = d1 * 15;

			if (nextLen + 1 < m) {
				int need2 = target[nextLen + 1];
				int d2 = distToColor[need2][headIdx];
				if (d2 < (1 << 28)) score += d2 * 7;
			}
			return score;
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

			nextHash = hash ^ zobristHash.head(old) ^ zobristHash.head(idx);
			nextScore = score + 1;
			int nextLen = len, nextE = e;
			int h = head == m - 1 ? 0 : head + 1;
			if (grid[idx] != 0) {
				byte eat = grid[idx];
				nextHash ^= zobristHash.grid(eat, idx) ^ zobristHash.grid(0, idx);
				if (target[nextLen] != eat) {
					nextE++;
					nextScore -= 10000;
				} else {
					nextScore -= 20000;
				}
				nextLen++;
				if (!isSnake(idx)) nextHash ^= zobristHash.snake(idx);
			} else {
				int oldTailPos = h - nextLen;
				if (oldTailPos < 0) oldTailPos += m;
				int oldTail = index[oldTailPos] & 0xFF;
				if (snakeCount[oldTail] == 1) nextHash ^= zobristHash.snake(oldTail);

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
						nextHash ^= zobristHash.grid(0, i) ^ zobristHash.grid(ci, i);
						if (snakeCount[i] == 1) nextHash ^= zobristHash.snake(i);
						if (++tail == m) tail = 0;
					}
				}
				if (!hitBody && !isSnake(idx)) nextHash ^= zobristHash.snake(idx);
			}
			nextHash ^= ((long) nextLen << 32) ^ (nextE * 0x9E3779B97F4A7C15L) ^ idx;
			nextRank = heuristic(idx, nextLen);
		}

		public void moveUnchecked(final byte d) {
			int old = index[head] & 0xFF;
			int idx = nextIdx[old][d] & 0xFF;
			hash ^= zobristHash.head(old) ^ zobristHash.head(idx);
			cur = new SearchNode(cur, opc[d]);
			t++;
			if (++head == m) head = 0;
			if (grid[idx] != 0) {
				byte eat = grid[idx];
				hash ^= zobristHash.grid(eat, idx) ^ zobristHash.grid(0, idx);
				if (target[len] != (color[len++] = eat)) e++;
				grid[idx] = 0;
			} else {
				int oldTailPos = head - len;
				if (oldTailPos < 0) oldTailPos += m;
				int oldTail = index[oldTailPos] & 0xFF;
				clearSnake(oldTail);
				if (!isSnake(oldTail)) hash ^= zobristHash.snake(oldTail);
				if (isSnake(idx)) {
					int tail = head - len + 1;
					if (tail < 0) tail += m;
					while ((index[tail] & 0xFF) != idx) {
						int i = index[tail] & 0xFF;
						byte ci = color[--len];
						if (target[len] != (grid[i] = ci)) e--;
						clearSnake(i);
						hash ^= zobristHash.grid(0, i) ^ zobristHash.grid(ci, i);
						if (!isSnake(i)) hash ^= zobristHash.snake(i);
						if (++tail == m) tail = 0;
					}
				}
			}
			if (!isSnake(idx)) hash ^= zobristHash.snake(idx);
			index[head] = (byte) idx;
			setSnake(idx);

			score = calcScore(t, e, len);
			rank = heuristic(idx, len);
			hash ^= ((long) len << 32) ^ (e * 0x9E3779B97F4A7C15L) ^ idx;
		}

		private int calcScore(final int turn, final int err, final int length) {
			return turn + 10000 * (err + 2 * (m - length));
		}

		private boolean isSnake(final int idx) {
			return snakeCount[idx] > 0;
		}

		private void setSnake(final int idx) {
			snakeCount[idx]++;
		}

		private void clearSnake(final int idx) {
			snakeCount[idx]--;
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
			int c = Integer.compare(score, o.score);
			if (c != 0) return c;
			return Integer.compare(rank, o.rank);
		}

		private record SearchNode(SearchNode prev, byte op) {
		}
	}

	private static final class ZobristHash {
		private final long[] head, snake;
		private final long[][] grid;

		public ZobristHash(final int c, final int n) {
			ThreadLocalRandom rnd = ThreadLocalRandom.current();
			head = new long[n];
			snake = new long[n];
			grid = new long[c + 1][n];
			for (int i = 0; i < n; i++) {
				head[i] = rnd.nextLong();
				snake[i] = rnd.nextLong();
			}
			for (int i = 0; i <= c; i++) setAll(grid[i], _ -> rnd.nextLong());
		}

		private long head(final int idx) {
			return head[idx];
		}

		private long snake(final int idx) {
			return snake[idx];
		}

		private long grid(final int color, final int idx) {
			return grid[color][idx];
		}
	}

	private static final class BeamFrontier {
		private final Ouroboros[] buf;
		private final int[] stamp, position;
		private final long[] key;
		private final int mask;
		private int curStamp, size, unsortedCount;

		public BeamFrontier(int capacity) {
			buf = new Ouroboros[capacity];
			stamp = new int[capacity];
			position = new int[capacity];
			key = new long[capacity];
			mask = capacity - 1;
			curStamp = 1;
			size = 0;
			unsortedCount = 0;
		}

		private void push(Ouroboros v) {
			buf[size++] = v;
			setPosition(v.hash, size - 1);
			unsortedCount++;
		}

		public int size() {
			return size;
		}

		public boolean containsHash(final long hash) {
			return getPosition(hash) >= 0;
		}

		public Ouroboros get(final int index) {
			return buf[index];
		}

		public void upsert(final Ouroboros v, final int capacity) {
			int pos = getPosition(v.hash);
			if (pos >= 0) {
				Ouroboros old = buf[pos];
				if (v.score >= old.score) return;
				if (unsortedCount > 0) ensureHeapProperty();
				buf[pos] = v;
				siftDown(buf[pos], pos);
				return;
			}
			if (size < capacity) {
				push(v);
			} else {
				if (unsortedCount > 0) ensureHeapProperty();
				if (v.compareTo(buf[0]) >= 0) return;
				setPosition(buf[0].hash, -1);
				buf[0] = v;
				setPosition(v.hash, 0);
				siftDown(buf[0], 0);
			}
		}

		public boolean isWorseOrEqualThanWorst(final int score, final int rank) {
			if (unsortedCount > 0) ensureHeapProperty();
			Ouroboros worst = buf[0];
			return score > worst.score || (score == worst.score && rank >= worst.rank);
		}

		public void clear() {
			size = 0;
			unsortedCount = 0;
			curStamp++;
		}

		public boolean isEmpty() {
			return size == 0;
		}

		private void ensureHeapProperty() {
			if (size == 0) {
				unsortedCount = 0;
				return;
			}
			if (unsortedCount > size) unsortedCount = size;
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
				setPosition(b[i].hash, i);
				i = j;
			}
			b[i] = v;
			setPosition(v.hash, i);
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
				setPosition(b[i].hash, i);
				i = child;
			}
			b[i] = v;
			setPosition(v.hash, i);
		}

		private int getPosition(final long hash) {
			int idx = Long.hashCode(hash) & mask;
			while (stamp[idx] == curStamp) {
				if (key[idx] == hash) return position[idx];
				idx = (idx + 1) & mask;
			}
			return -1;
		}

		private void setPosition(final long hash, final int pos) {
			int idx = Long.hashCode(hash) & mask;
			while (stamp[idx] == curStamp) {
				if (key[idx] == hash) {
					position[idx] = pos;
					return;
				}
				idx = (idx + 1) & mask;
			}
			stamp[idx] = curStamp;
			key[idx] = hash;
			position[idx] = pos;
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
