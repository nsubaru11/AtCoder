import java.io.*;
import java.lang.invoke.*;
import java.math.*;
import java.util.*;
import java.util.ArrayList;
import java.util.function.*;

import static java.lang.Math.*;
import static java.util.Arrays.*;

public final class Solve7 {
	// ------------------------ 定数 ------------------------
	private static final boolean DEBUG = true;
	private static final int MOD = 998244353;
	// private static final int MOD = 1_000_000_007;
	private static final int[][] dij = {{1, 0}, {0, -1}, {0, 1}, {-1, 0}}; // 下、左、右、上
	// private static final int[][] dij = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
	private static final FastScanner sc;
	private static final FastPrinter out;

	private static final int L = 1, U = 3, R = 2, D = 0;

	private static final int n, k, t;
	private static final int[] wall, point;
	private static final long startTime, timeLimit;

	static {
		// init
		startTime = System.currentTimeMillis();
		timeLimit = 1900;
		sc = new FastScanner();
		out = new FastPrinter();
		n = sc.nextInt();
		k = sc.nextInt();
		t = sc.nextInt();
		wall = new int[n * n];
		point = new int[k];
		for (int i = 0; i < n; i++) {
			wall[i] |= 1 << U;
			wall[n * (n - 1) + i] |= 1 << D;
			wall[i * n] |= 1 << L;
			wall[(i + 1) * n - 1] |= 1 << R;
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n - 1; j++) {
				char v = sc.nextChar();
				if (v == '0') continue;
				wall[i * n + j] |= 1 << R;
				wall[i * n + j + 1] |= 1 << L;
			}
		}
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n; j++) {
				char h = sc.nextChar();
				if (h == '0') continue;
				wall[i * n + j] |= 1 << D;
				wall[(i + 1) * n + j] |= 1 << U;
			}
		}
		for (int i = 0; i < k; i++) {
			int x = sc.nextInt();
			int y = sc.nextInt();
			point[i] = x * n + y;
		}
	}

	// ------------------------ メインロジック ------------------------
	private static void solve() {
		Solution sol1 = solveMethod1();
		Solution sol2 = solveMethod2Greedy();
		Solution sol3 = solveMethod3PenaltyDijkstra();

		Solution best = sol1;
		if (sol2.score < best.score) best = sol2;
		if (sol3.score < best.score) best = sol3;

		best.output();
	}

	// 解法1: 色1個 + ステップ数を状態に
	private static Solution solveMethod1() {
		ArrayList<Integer> path = new ArrayList<>(t);
		path.add(point[0]);

		int[] dest = new int[n * n];
		for (int k2 = 0; k2 < k - 1; k2++) {
			ArrayDeque<Integer> dq = new ArrayDeque<>();
			int from = point[k2], to = point[k2 + 1];
			Arrays.fill(dest, -1);
			dest[from] = from;
			dq.add(from);

			while (!dq.isEmpty()) {
				from = dq.poll();
				if (from == to) break;

				for (int d = 0; d < 4; d++) {
					if ((wall[from] >> d & 1) == 1) continue;
					int next = from + dij[d][0] * n + dij[d][1];
					if (dest[next] >= 0) continue;
					dest[next] = from;
					dq.add(next);
				}
			}

			ArrayList<Integer> segment = new ArrayList<>();
			int p = to;
			while (dest[p] != p) {
				segment.add(p);
				p = dest[p];
			}
			for (int i = segment.size() - 1; i >= 0; i--) {
				path.add(segment.get(i));
			}
		}

		return new Solution(1, path.size(), path, null);
	}

	// 解法2: 貪欲に最短経路を選択
	private static Solution solveMethod2Greedy() {
		ArrayList<Integer> path = new ArrayList<>(t);
		HashMap<Integer, Integer> color = new HashMap<>();
		path.add(point[0]);
		color.put(point[0], 0);

		int[] dest = new int[n * n];
		for (int k2 = 0; k2 < k - 1; k2++) {
			ArrayDeque<Integer> dq = new ArrayDeque<>();
			int from = point[k2], to = point[k2 + 1];
			Arrays.fill(dest, -1);
			dest[from] = from;
			dq.add(from);

			while (!dq.isEmpty()) {
				from = dq.poll();
				if (from == to) break;

				for (int d = 0; d < 4; d++) {
					if ((wall[from] >> d & 1) == 1) continue;
					int next = from + dij[d][0] * n + dij[d][1];
					if (dest[next] >= 0) continue;
					dest[next] = from;
					dq.add(next);
				}
			}

			ArrayList<Integer> segment = new ArrayList<>();
			int p = to;
			while (dest[p] != p) {
				segment.add(p);
				p = dest[p];
			}
			for (int i = segment.size() - 1; i >= 0; i--) {
				int pos = segment.get(i);
				path.add(pos);
				color.putIfAbsent(pos, color.size());
			}
		}

		return new Solution(color.size(), k, path, color);
	}

	// 解法3: ペナルティ付きダイクストラ
	private static Solution solveMethod3PenaltyDijkstra() {
		Dijkstra dijkstra = new Dijkstra(n * n, n * n * 4);
		for (int ij = 0; ij < n * n; ij++) {
			for (int d = 0; d < 4; d++) {
				if ((wall[ij] >> d & 1) == 1) continue;
				dijkstra.addEdge(ij, ij + dij[d][0] * n + dij[d][1]);
			}
		}

		// スタート地点を中心からの距離でソート
		int centerI = n / 2, centerJ = n / 2;
		Integer[] sortedStarts = new Integer[k];
		for (int i = 0; i < k; i++) sortedStarts[i] = i;
		Arrays.sort(sortedStarts, (a, b) -> {
			int distA = Math.abs(point[a] / n - centerI) + Math.abs(point[a] % n - centerJ);
			int distB = Math.abs(point[b] / n - centerI) + Math.abs(point[b] % n - centerJ);
			return Integer.compare(distA, distB);
		});

		ArrayDeque<Integer> bestRoot = null;
		HashMap<Integer, Integer> bestColor = null;
		int bestScore = Integer.MAX_VALUE;

		// フェーズ1: 粗い探索（0-30%）
		long phase1End = startTime + (long) (timeLimit * 0.3);
		int[] coarsePenalties = {n / 2, n, 2 * n, 3 * n, 5 * n, 10 * n, 20 * n};
		int bestPenalty = n;

		for (int penalty : coarsePenalties) {
			if (System.currentTimeMillis() > phase1End) break;

			dijkstra.setPenalty(penalty);

			for (int idx = 0; idx < Math.min(3, k); idx++) {
				ArrayDeque<Integer> rootI = buildPath(dijkstra, sortedStarts[idx]);
				if (rootI == null || rootI.size() - 1 > t) continue;

				HashMap<Integer, Integer> colorI = new HashMap<>();
				for (int cell : rootI) {
					colorI.putIfAbsent(cell, colorI.size());
				}

				int score = colorI.size() + k;
				if (score < bestScore) {
					bestScore = score;
					bestRoot = new ArrayDeque<>(rootI);
					bestColor = new HashMap<>(colorI);
					bestPenalty = penalty;
				}
			}
		}

		// フェーズ2: 細かい探索（30-80%）
		long phase2End = startTime + (long) (timeLimit * 0.8);
		int searchRadius = Math.max(bestPenalty / 2, n);
		int minP = Math.max(1, bestPenalty - searchRadius);
		int maxP = bestPenalty + searchRadius;
		int step = Math.max(1, searchRadius / 20);

		for (int p = minP; p <= maxP; p += step) {
			if (System.currentTimeMillis() > phase2End) break;

			dijkstra.setPenalty(p);

			int numStarts = (System.currentTimeMillis() - startTime < timeLimit * 0.5) ? k : Math.max(k / 2, 1);

			for (int idx = 0; idx < numStarts; idx++) {
				if (System.currentTimeMillis() > phase2End) break;

				ArrayDeque<Integer> rootI = buildPath(dijkstra, sortedStarts[idx]);
				if (rootI == null || rootI.size() - 1 > t) continue;

				HashMap<Integer, Integer> colorI = new HashMap<>();
				for (int cell : rootI) {
					colorI.putIfAbsent(cell, colorI.size());
				}

				int score = colorI.size() + k;
				if (score < bestScore) {
					bestScore = score;
					bestRoot = new ArrayDeque<>(rootI);
					bestColor = new HashMap<>(colorI);
				}
			}
		}

		// フェーズ3: ファインチューニング（80-100%）
		Random rand = new Random(System.currentTimeMillis());
		int fineRadius = Math.max(step, n / 4);

		while (System.currentTimeMillis() - startTime < timeLimit) {
			int p;
			if (rand.nextDouble() < 0.8) {
				p = bestPenalty + rand.nextInt(2 * fineRadius + 1) - fineRadius;
				p = Math.max(1, p);
			} else {
				p = rand.nextInt(20 * n) + 1;
			}

			dijkstra.setPenalty(p);
			int startIdx = rand.nextInt(k);

			ArrayDeque<Integer> rootI = buildPath(dijkstra, sortedStarts[startIdx]);
			if (rootI == null || rootI.size() - 1 > t) continue;

			HashMap<Integer, Integer> colorI = new HashMap<>();
			for (int cell : rootI) {
				colorI.putIfAbsent(cell, colorI.size());
			}

			int score = colorI.size() + k;
			if (score < bestScore) {
				bestScore = score;
				bestRoot = new ArrayDeque<>(rootI);
				bestColor = new HashMap<>(colorI);
			}
		}

		if (bestRoot == null) return solveMethod2Greedy();

		return new Solution(bestColor.size(), k, new ArrayList<>(bestRoot), bestColor);
	}

	// 指定したスタート地点から経路を構築
	private static ArrayDeque<Integer> buildPath(Dijkstra dijkstra, int start) {
		ArrayDeque<Integer> rootI = new ArrayDeque<>();
		rootI.add(point[start]);
		boolean[] visited = new boolean[n * n];
		visited[point[start]] = true;

		// Forward
		for (int j = start; j < k - 1; j++) {
			int[] path = dijkstra.solve(point[j], point[j + 1], visited);
			if (path[point[j + 1]] == -1) return null;

			ArrayList<Integer> reversePath = new ArrayList<>();
			int p = point[j + 1];
			while (path[p] != p) {
				reversePath.add(p);
				p = path[p];
			}
			for (int r = reversePath.size() - 1; r >= 0; r--) {
				int pos = reversePath.get(r);
				rootI.add(pos);
				visited[pos] = true;
			}
		}

		// Backward
		for (int j = start - 1; j >= 0; j--) {
			int[] path = dijkstra.solve(point[j], point[j + 1], visited);
			if (path[point[j + 1]] == -1) return null;

			int p = point[j + 1];
			while (path[p] != p) {
				p = path[p];
				rootI.addFirst(p);
				visited[p] = true;
			}
		}

		return rootI;
	}

	// ------------------------ main() 関数 ------------------------
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

	// ------------------------ デバッグ用 ------------------------
	@SuppressWarnings("unused")
	private static void debug(final Object... args) {
		if (DEBUG) {
			out.flush();
			System.err.println(deepToString(args));
		}
	}

	// Solutionクラス
	private static class Solution {
		int colorCount;
		int stateCount;
		ArrayList<Integer> path;
		HashMap<Integer, Integer> color;
		int score;

		Solution(int colorCount, int stateCount, ArrayList<Integer> path, HashMap<Integer, Integer> color) {
			this.colorCount = colorCount;
			this.stateCount = stateCount;
			this.path = path;
			this.color = color;
			this.score = colorCount + stateCount;
		}

		void output() {
			if (color == null) {
				// 解法1
				out.print(1, path.size(), path.size() - 1).println();
				for (int i = 0; i < n; i++) {
					out.print('0').printRepeat(" 0", n - 1).println();
				}
				for (int p = 0; p < path.size() - 1; p++) {
					int cur = path.get(p);
					int nij = path.get(p + 1);
					char d = getDirection(cur, nij);
					out.print(0, p, 0, p + 1, d).println();
				}
			} else {
				// 解法2, 3
				out.print(colorCount, stateCount, path.size() - 1).println();
				for (int i = 0, ij = 0; i < n; i++) {
					out.print(color.getOrDefault(ij++, 0));
					for (int j = 1; j < n; j++) {
						out.print("", color.getOrDefault(ij++, 0));
					}
					out.println();
				}
				for (int p = 0, state = 0; p < path.size() - 1; p++) {
					int cur = path.get(p);
					int nij = path.get(p + 1);
					int curColor = color.get(cur);
					char d = getDirection(cur, nij);
					int nextState = (state + 1 < k && nij == point[state + 1]) ? state + 1 : state;
					out.print(curColor, state, curColor, nextState, d).println();
					state = nextState;
				}
			}
		}

		char getDirection(int cur, int next) {
			if (cur - 1 == next) return 'L';
			if (cur - n == next) return 'U';
			if (cur + 1 == next) return 'R';
			return 'D';
		}
	}

	@SuppressWarnings("unused")
	private static final class Dijkstra {
		// -------------- フィールド --------------
		private static final long INF = Long.MAX_VALUE;
		private final IndexedPriorityQueue ans;
		private final int[] dest, next, first;
		private final int v;
		private int e;
		private int penalty = 100;

		// -------------- コンストラクタ --------------

		/**
		 * コンストラクタ
		 *
		 * @param v 頂点数
		 * @param e 辺数の最大値
		 */
		public Dijkstra(final int v, final int e) {
			this(v, e, false);
		}

		/**
		 * コンストラクタ
		 *
		 * @param v     頂点数
		 * @param e     辺数の最大値
		 * @param isMax true の場合は最長経路を求める（最大値優先）、false の場合は最短経路を求める（最小値優先）
		 */
		public Dijkstra(final int v, final int e, final boolean isMax) {
			dest = new int[e];
			next = new int[e];
			first = new int[v];
			fill(first, -1);
			this.e = 0;
			this.v = v;
			ans = new IndexedPriorityQueue(v, isMax);
		}

		// -------------- グラフ構築 --------------

		/**
		 * 有向辺を追加する
		 *
		 * @param i 辺の始点（0-indexed）
		 * @param j 辺の終点（0-indexed）
		 */
		public void addEdge(final int i, final int j) {
			dest[e] = j;
			next[e] = first[i];
			first[i] = e;
			e++;
		}

		public void setPenalty(final int penalty) {
			this.penalty = penalty;
		}

		// -------------- 最短経路探索 --------------

		/**
		 * 始点 i から終点 j への最短経路の重みを返す
		 *
		 * @param i 始点
		 * @param j 終点
		 * @return 始点から終点への最短経路の重み（経路が存在しない場合は INF）
		 */
		public int[] solve(final int i, final int j, final boolean[] visited) {
			int[] path = new int[v];
			fill(path, -1);
			path[i] = i;
			ans.clear();
			ans.push(i, 0);
			while (!ans.isEmpty()) {
				long c = ans.peek();
				int from = ans.pollNode();
				for (int e = first[from]; e != -1; e = next[e]) {
					int to = dest[e];
					long cost = 1;
					if (!visited[to]) cost += penalty;
					if (ans.relax(to, c + cost)) {
						path[to] = from;
					}
				}
			}
			return path;
		}

		// -------------- 内部クラス：IndexedPriorityQueue --------------

		/**
		 * Dijkstra法特化インデックス付き優先度キュー
		 * <p>
		 * 遅延ヒープ構築により効率的な探索を実現する内部クラス。
		 */
		private static final class IndexedPriorityQueue {
			// -------------- フィールド --------------
			private final boolean isDescendingOrder;
			private final long[] cost;
			private final int[] heap, position;
			private int size, unsortedCount;

			// -------------- コンストラクタ --------------

			/**
			 * コンストラクタ
			 *
			 * @param n                 頂点数
			 * @param isDescendingOrder true の場合は最大値優先（降順）、false の場合は最小値優先（昇順）
			 */
			public IndexedPriorityQueue(final int n, final boolean isDescendingOrder) {
				this.isDescendingOrder = isDescendingOrder;
				cost = new long[n];
				heap = new int[n];
				position = new int[n];
				fill(position, -2);
				size = 0;
				unsortedCount = 0;
			}

			// -------------- 公開メソッド --------------

			/**
			 * 要素を追加する
			 *
			 * @param node 追加するノード
			 * @param c    追加するノードのコスト
			 */
			public void push(final int node, long c) {
				if (position[node] != -2) throw new IllegalArgumentException();
				if (isDescendingOrder) c = -c;
				cost[node] = c;
				heap[size] = node;
				position[node] = size;
				size++;
				unsortedCount++;
			}

			/**
			 * relax操作（Dijkstra法などで使用）
			 *
			 * @param node ノード
			 * @param cost 新しいコスト
			 * @return 更新が行われた場合はtrue
			 */
			public boolean relax(final int node, long cost) {
				if (position[node] == -1) return false;
				if (position[node] == -2) {
					push(node, cost);
					return true;
				}
				if (isDescendingOrder) cost = -cost;
				if (this.cost[node] > cost) {
					this.cost[node] = cost;
					siftUp(node, position[node]);
					return true;
				}
				return false;
			}

			/**
			 * ヒープの先頭要素のコストを取得する
			 *
			 * @return ヒープの先頭要素のコスト（昇順時は最小、降順時は最大）
			 */
			public long peek() {
				if (isEmpty()) throw new NoSuchElementException();
				if (unsortedCount > 0) ensureHeapProperty();
				return isDescendingOrder ? -cost[heap[0]] : cost[heap[0]];
			}

			/**
			 * ヒープの先頭ノードを削除し、そのノードを返す
			 *
			 * @return 削除されたノード（昇順時は最小コスト、降順時は最大コストのノード）
			 */
			public int pollNode() {
				if (isEmpty()) throw new NoSuchElementException();
				if (unsortedCount > 0) ensureHeapProperty();
				int node = heap[0];
				position[node] = -1;
				size--;
				if (size > 0) {
					int lastNode = heap[size];
					siftDown(lastNode, 0);
				}
				return node;
			}

			/**
			 * 指定したノードのコストを取得する（存在しない場合はデフォルト値を返す）
			 *
			 * @param node         対象ノード
			 * @param defaultValue デフォルト値
			 * @return コストまたはデフォルト値
			 */
			public long getCostOrDefault(final int node, final long defaultValue) {
				return position[node] == -2 ? defaultValue : isDescendingOrder ? -cost[node] : cost[node];
			}

			/**
			 * ヒープをクリアする
			 */
			public void clear() {
				size = 0;
				unsortedCount = 0;
				fill(position, -2);
			}

			/**
			 * ヒープが空かどうかを判定する
			 *
			 * @return 空の場合はtrue
			 */
			public boolean isEmpty() {
				return size == 0;
			}

			// -------------- ヒープ構築（遅延評価） --------------

			/**
			 * 遅延評価された未ソート要素をヒープ化し、ヒーププロパティを復元する。
			 * <p>
			 * このメソッドは、未ソート要素が存在する場合に最適なアルゴリズムを自動選択して実行します。
			 * <p><b>分岐点の決定：</b>
			 * 両アルゴリズムの最大比較回数を計算し、コストが小さい方を実行する。
			 * (heapifyCost < incrementalCost なら heapify を選択)
			 */
			private void ensureHeapProperty() {
				int log2N = 31 - Integer.numberOfLeadingZeros(size);
				int heapifyCost = size * 2 - 2 * log2N;
				int incrementalCost = unsortedCount <= 100 ? getIncrementalCostStrict() : getIncrementalCostApprox();
				if (heapifyCost < incrementalCost) {
					heapify();
				} else {
					for (int i = size - unsortedCount; i < size; i++) siftUp(heap[i], i);
				}
				unsortedCount = 0;
			}

			/**
			 * インクリメンタル構築の最大比較回数を厳密に計算する。
			 *
			 * @return 最大比較回数の合計
			 */
			private int getIncrementalCostStrict() {
				int totalCost = 0;
				int sortedSize = size - unsortedCount;
				for (int i = 1; i <= unsortedCount; i++) {
					int currentHeapSize = sortedSize + i;
					int depth = 31 - Integer.numberOfLeadingZeros(currentHeapSize);
					totalCost += depth;
				}
				return totalCost;
			}

			/**
			 * インクリメンタル構築の最大比較回数を高速に近似計算する。
			 * <p>コスト ≈ k * floor(log₂(平均ヒープサイズ))
			 *
			 * @return 最大比較回数の近似値
			 */
			private int getIncrementalCostApprox() {
				int sortedSize = size - unsortedCount;
				int avgHeapSize = sortedSize + (unsortedCount >> 1);
				if (avgHeapSize == 0) return 0;
				int depthOfAvgSize = 31 - Integer.numberOfLeadingZeros(avgHeapSize);
				return unsortedCount * depthOfAvgSize;
			}

			/**
			 * Bottom-up heapify (Floyd's algorithm)
			 */
			private void heapify() {
				for (int i = (size >> 1) - 1; i >= 0; i--) siftDown(heap[i], i);
			}

			// -------------- ヒープ操作（基本） --------------

			/**
			 * siftUp操作
			 *
			 * @param node 移動させるノード
			 * @param i    ノードの現在位置
			 */
			private void siftUp(final int node, int i) {
				long c = cost[node];
				while (i > 0) {
					int j = (i - 1) >> 1;
					int parent = heap[j];
					if (c >= cost[parent]) break;
					heap[i] = parent;
					position[parent] = i;
					i = j;
				}
				heap[i] = node;
				position[node] = i;
			}

			/**
			 * siftDown操作
			 *
			 * @param node 移動させるノード
			 * @param i    ノードの現在位置
			 */
			private void siftDown(final int node, int i) {
				long c = cost[node];
				int half = size >> 1;
				while (i < half) {
					int child = (i << 1) + 1;
					child += child + 1 < size && cost[heap[child]] > cost[heap[child + 1]] ? 1 : 0;
					int childNode = heap[child];
					if (c <= cost[childNode]) break;
					heap[i] = childNode;
					position[childNode] = i;
					i = child;
				}
				heap[i] = node;
				position[node] = i;
			}
		}
	}

	// ------------------------ 高速入出力クラス ------------------------
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

		private int skipSpaces() {
			int b = read();
			while (b <= 32) b = read();
			return b;
		}

		@Override
		public void close() {
			try {
				if (in != System.in) in.close();
				pos = 0;
				bufferLength = 0;
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		private int read() {
			if (pos >= bufferLength) {
				try {
					bufferLength = in.read(buffer, pos = 0, buffer.length);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
				if (bufferLength <= 0) throw new RuntimeException(new EOFException());
			}
			return buffer[pos++] & 0xFF;
		}

		public int peek() {
			try {
				int b = skipSpaces();
				pos--;
				return b;
			} catch (final RuntimeException e) {
				return 0;
			}
		}

		public boolean hasNext() {
			return peek() != 0;
		}

		public int nextInt() {
			int b = skipSpaces();
			boolean negative = false;
			if (b == '-') {
				negative = true;
				b = read();
			}
			int result = 0;
			do {
				result = (result << 3) + (result << 1) + (b & 15);
				b = read();
			} while (b >= '0' && b <= '9');
			return negative ? -result : result;
		}

		public long nextLong() {
			int b = skipSpaces();
			boolean negative = false;
			if (b == '-') {
				negative = true;
				b = read();
			}
			long result = 0;
			do {
				result = (result << 3) + (result << 1) + (b & 15);
				b = read();
			} while (b >= '0' && b <= '9');
			return negative ? -result : result;
		}

		public double nextDouble() {
			int b = skipSpaces();
			boolean negative = false;
			if (b == '-') {
				negative = true;
				b = read();
			}
			long intPart = 0;
			do {
				intPart = (intPart << 3) + (intPart << 1) + (b & 15);
				b = read();
			} while (b >= '0' && b <= '9');
			double result = intPart;
			if (b == '.') {
				b = read();
				double scale = 0.1;
				do {
					result += (b & 15) * scale;
					scale *= 0.1;
					b = read();
				} while (b >= '0' && b <= '9');
			}
			return negative ? -result : result;
		}

		public char nextChar() {
			int b = skipSpaces();
			return (char) b;
		}

		public String next() {
			return nextStringBuilder().toString();
		}

		public StringBuilder nextStringBuilder() {
			final StringBuilder sb = new StringBuilder();
			int b = skipSpaces();
			do {
				sb.append((char) b);
				b = read();
			} while (b > 32);
			return sb;
		}

		public String nextLine() {
			final StringBuilder sb = new StringBuilder();
			int b = read();
			while (b != 0 && b != '\n' && b != '\r') {
				sb.append((char) b);
				b = read();
			}
			if (b == '\r') {
				int c = read();
				if (c != '\n') pos--;
			}
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
		private static final int MAX_INT_DIGITS = 11;
		private static final int MAX_LONG_DIGITS = 20;
		private static final int DEFAULT_BUFFER_SIZE = 65536;
		private static final byte LINE = '\n';
		private static final byte SPACE = ' ';
		private static final byte HYPHEN = '-';
		private static final byte PERIOD = '.';
		private static final byte ZERO = '0';
		private static final byte[] TRUE_BYTES = {'Y', 'e', 's'};
		private static final byte[] FALSE_BYTES = {'N', 'o'};
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
		private static final long[] POW10 = {
				1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000,
				1_000_000_000, 10_000_000_000L, 100_000_000_000L, 1_000_000_000_000L,
				10_000_000_000_000L, 100_000_000_000_000L, 1_000_000_000_000_000L,
				10_000_000_000_000_000L, 100_000_000_000_000_000L, 1_000_000_000_000_000_000L
		};
		private final OutputStream out;
		private final boolean autoFlush;
		private byte[] buffer;
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
				if (out != System.out) out.close();
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
			write(b);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final byte b) {
			ensureCapacity(2);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, b);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final char c) {
			ensureCapacity(2);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) c);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final int i) {
			ensureCapacity(MAX_INT_DIGITS + 1);
			write(i);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final long l) {
			ensureCapacity(MAX_LONG_DIGITS + 1);
			write(l);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
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
			write(s);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final StringBuilder s) {
			ensureCapacity(s.length() + 1);
			write(s.toString());
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
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
			write(b);
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
			write(i);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final long l) {
			ensureCapacity(MAX_LONG_DIGITS);
			write(l);
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
			write(s);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final StringBuilder s) {
			ensureCapacity(s.length());
			write(s.toString());
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
			if (required <= buffer.length) return;
			if (required <= 1_000_000_000) {
				buffer = copyOf(buffer, roundUpToPowerOfTwo(required));
			} else {
				flush();
			}
		}

		private void write(final boolean b) {
			final byte[] src = b ? TRUE_BYTES : FALSE_BYTES;
			final int len = src.length;
			ensureCapacity(len);
			System.arraycopy(src, 0, buffer, pos, len);
			pos += len;
		}

		private void write(int i) {
			final byte[] buf = buffer;
			int p = pos;
			if (i >= 0) i = -i;
			else BYTE_ARRAY_HANDLE.set(buf, p++, HYPHEN);
			final int digits = countDigits(i);
			int writePos = p + digits;
			while (i <= -100) {
				final int q = i / 100;
				final int r = (q << 6) + (q << 5) + (q << 2) - i;
				BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitOnes[r]);
				BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitTens[r]);
				i = q;
			}
			final int r = -i;
			BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitOnes[r]);
			if (r >= 10) BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitTens[r]);
			pos = p + digits;
		}

		private void write(long l) {
			final byte[] buf = buffer;
			int p = pos;
			if (l >= 0) l = -l;
			else BYTE_ARRAY_HANDLE.set(buf, p++, HYPHEN);
			final int digits = countDigits(l);
			int writePos = p + digits;
			while (l <= -100) {
				final long q = l / 100;
				final int r = (int) ((q << 6) + (q << 5) + (q << 2) - l);
				BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitOnes[r]);
				BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitTens[r]);
				l = q;
			}
			final int r = (int) -l;
			BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitOnes[r]);
			if (r >= 10) BYTE_ARRAY_HANDLE.set(buf, --writePos, DigitTens[r]);
			pos = p + digits;
		}

		private void write(final String s) {
			final int len = s.length();
			final byte[] buf = buffer;
			int p = pos, i = 0;
			final int limit = len & ~7;
			while (i < limit) {
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
			}
			while (i < len) BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
			pos = p;
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
			write(a);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
			write(b);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, LINE);
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
			write(a);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
			write(b);
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter println(final double d, final int n) {
			return print(d, n).println();
		}

		public FastPrinter print(double d, int n) {
			if (n <= 0) return print(round(d));
			if (d < 0) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, HYPHEN);
				d = -d;
			}
			if (n > 18) n = 18;
			final long intPart = (long) d;
			final long fracPart = (long) ((d - intPart) * POW10[n]);
			print(intPart);
			int leadingZeros = n - countDigits(-fracPart);
			ensureCapacity(leadingZeros + 1);
			BYTE_ARRAY_HANDLE.set(buffer, pos++, PERIOD);
			while (leadingZeros-- > 0) BYTE_ARRAY_HANDLE.set(buffer, pos++, ZERO);
			print(fracPart);
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
			print(arr[from]);
			for (int i = from + 1; i < to; i++) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
				write(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final char[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			ensureCapacity(((to - from) << 1) - 1);
			byte[] buf = buffer;
			int p = pos;
			BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[from]);
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) delimiter);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i]);
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final int[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			final int len = to - from;
			ensureCapacity(len * (MAX_INT_DIGITS + 1));
			write(arr[from]);
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
				write(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final long[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			final int len = to - from;
			ensureCapacity(len * (MAX_LONG_DIGITS + 1));
			write(arr[from]);
			for (int i = from + 1; i < to; i++) {
				BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
				write(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final double[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			print(arr[from]);
			for (int i = from + 1; i < to; i++) {
				String s = Double.toString(arr[i]);
				ensureCapacity(s.length() + 1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
				write(s);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter print(final String[] arr, final int from, final int to, final char delimiter) {
			if (from >= to) return this;
			print(arr[from]);
			for (int i = from + 1; i < to; i++) {
				ensureCapacity(arr[i].length() + 1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
				write(arr[i]);
			}
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
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final char[] arr, final IntFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final int[] arr, final IntFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final long[] arr, final LongFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final double[] arr, final DoubleFunction<T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
				print(function.apply(arr[i]));
			}
			return this;
		}

		public <T> FastPrinter print(final String[] arr, final Function<String, T> function) {
			final int len = arr.length;
			if (len > 0) print(function.apply(arr[0]));
			for (int i = 1; i < len; i++) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, SPACE);
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
					ensureCapacity(1);
					BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
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
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i++]);
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
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) function.applyAsInt(arr[i++]));
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
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
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
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buffer, pos++, (byte) delimiter);
				print(function.apply(it.next()));
			}
			return this;
		}

		public FastPrinter printRepeat(final char c, final int cnt) {
			if (cnt <= 0) return this;
			ensureCapacity(cnt);
			final byte[] buf = buffer;
			final byte b = (byte) c;
			int p = pos;
			BYTE_ARRAY_HANDLE.set(buf, p++, b);
			int copied = 1;
			while (copied << 1 <= cnt) {
				System.arraycopy(buf, pos, buf, p, copied);
				p += copied;
				copied <<= 1;
			}
			final int remain = cnt - copied;
			if (remain > 0) {
				System.arraycopy(buf, pos, buf, p, remain);
				p += remain;
			}
			pos = p;
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
			int p = pos, i = 0;
			final int limit8 = len & ~7;
			while (i < limit8) {
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
			}
			while (i < len) BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
			int copied = 1;
			while (copied << 1 <= cnt) {
				System.arraycopy(buf, pos, buf, p, copied * len);
				p += copied * len;
				copied <<= 1;
			}
			final int remain = cnt - copied;
			if (remain > 0) {
				System.arraycopy(buf, pos, buf, p, remain * len);
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
			int p = pos;
			BYTE_ARRAY_HANDLE.set(buf, p++, b);
			BYTE_ARRAY_HANDLE.set(buf, p++, LINE);
			int copied = 2;
			while (copied << 1 <= total) {
				System.arraycopy(buf, pos, buf, p, copied);
				p += copied;
				copied <<= 1;
			}
			final int remain = total - copied;
			if (remain > 0) {
				System.arraycopy(buf, pos, buf, p, remain);
				p += remain;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnRepeat(final String s, final int cnt) {
			if (cnt <= 0) return this;
			final int sLen = s.length();
			if (sLen == 0) return this;
			final int unit = sLen + 1;
			final int total = unit * cnt;
			ensureCapacity(total);
			final byte[] buf = buffer;
			int p = pos;
			int i = 0;
			final int limit8 = sLen & ~7;
			while (i < limit8) {
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
			}
			while (i < sLen) BYTE_ARRAY_HANDLE.set(buf, p++, (byte) s.charAt(i++));
			BYTE_ARRAY_HANDLE.set(buf, p++, LINE);
			int copied = 1;
			while (copied << 1 <= cnt) {
				System.arraycopy(buf, pos, buf, p, copied * unit);
				p += copied * unit;
				copied <<= 1;
			}
			final int remain = cnt - copied;
			if (remain > 0) {
				System.arraycopy(buf, pos, buf, p, remain * unit);
				p += remain * unit;
			}
			pos = p;
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final boolean[] arr) {
			final int len = arr.length;
			final byte[] buf = buffer;
			for (int i = len - 1; i >= 0; i--) {
				write(arr[i]);
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buf, pos++, LINE);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final char[] arr) {
			final int len = arr.length;
			ensureCapacity(len << 1);
			final byte[] buf = buffer;
			int p = pos;
			for (int i = len - 1; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i]);
				BYTE_ARRAY_HANDLE.set(buf, p++, LINE);
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
			for (int i = len - 1; i >= 0; i--) {
				write(arr[i]);
				BYTE_ARRAY_HANDLE.set(buf, pos++, LINE);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final long[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_LONG_DIGITS + 1));
			final byte[] buf = buffer;
			for (int i = len - 1; i >= 0; i--) {
				write(arr[i]);
				BYTE_ARRAY_HANDLE.set(buf, pos++, LINE);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final double[] arr) {
			final int len = arr.length;
			final byte[] buf = buffer;
			for (int i = len - 1; i >= 0; i--) {
				String s = Double.toString(arr[i]);
				ensureCapacity(s.length() + 1);
				write(s);
				BYTE_ARRAY_HANDLE.set(buf, pos++, LINE);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printlnReverse(final String[] arr) {
			final int len = arr.length;
			final byte[] buf = buffer;
			for (int i = len - 1; i >= 0; i--) {
				String s = arr[i];
				ensureCapacity(s.length() + 1);
				write(s);
				BYTE_ARRAY_HANDLE.set(buf, pos++, LINE);
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
			final byte[] buf = buffer;
			write(arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buf, pos++, SPACE);
				write(arr[i]);
			}
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
				BYTE_ARRAY_HANDLE.set(buf, p++, SPACE);
				BYTE_ARRAY_HANDLE.set(buf, p++, (byte) arr[i]);
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
			write(arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, pos++, SPACE);
				write(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final long[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			ensureCapacity(len * (MAX_LONG_DIGITS + 1) - 1);
			final byte[] buf = buffer;
			write(arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				BYTE_ARRAY_HANDLE.set(buf, pos++, SPACE);
				write(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final double[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			final byte[] buf = buffer;
			print(arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				String s = Double.toString(arr[i]);
				ensureCapacity(s.length() + 1);
				BYTE_ARRAY_HANDLE.set(buf, pos++, SPACE);
				write(s);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final String[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			final byte[] buf = buffer;
			ensureCapacity(arr[len - 1].length());
			write(arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				ensureCapacity(arr[i].length() + 1);
				BYTE_ARRAY_HANDLE.set(buf, pos++, SPACE);
				write(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}

		public FastPrinter printReverse(final Object[] arr) {
			final int len = arr.length;
			if (len == 0) return this;
			final byte[] buf = buffer;
			print(arr[len - 1]);
			for (int i = len - 2; i >= 0; i--) {
				ensureCapacity(1);
				BYTE_ARRAY_HANDLE.set(buf, pos++, SPACE);
				print(arr[i]);
			}
			if (autoFlush) flush();
			return this;
		}
	}
}
