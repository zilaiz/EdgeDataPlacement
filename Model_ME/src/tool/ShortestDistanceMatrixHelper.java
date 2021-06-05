package tool;

class ShortestDistanceMatrixHelper {
	private int mNodesNumber;
	private int[][] mMatrix;
	private int[][] mDistanceMatrix;

	public ShortestDistanceMatrixHelper(int nodesNumber, int adjacencyMatrix[][]) {
		mNodesNumber = nodesNumber;
		mMatrix = new int [mNodesNumber][mNodesNumber];
		mDistanceMatrix = new int [mNodesNumber][mNodesNumber];
		for (int i = 0; i < mNodesNumber; i++) {
			for (int j = 0; j < mNodesNumber; j++) {
				if (adjacencyMatrix[i][j] == 0) mMatrix[i][j] = Integer.MAX_VALUE;
				else mMatrix[i][j] = adjacencyMatrix[i][j];
			}
		}
	}

	public int[][] getShortestDistanceMatrix() {
		for (int i = 0; i < mNodesNumber; i++) {
			mDistanceMatrix[i] = getMinDistancesByDijkstra(i);
		}
		
		return mDistanceMatrix;
	}
	
	private int getMinimumVertex(boolean[] mst, int[] key) {
		int minKey = Integer.MAX_VALUE;
		int vertex = -1;
		for (int i = 0; i < mNodesNumber; i++) {
			if (mst[i] == false && minKey > key[i]) {
				minKey = key[i];
				vertex = i;
			}
		}
		return vertex;
	}

	private int[] getMinDistancesByDijkstra(int sourceVertex) {
		boolean[] spt = new boolean[mNodesNumber];
		int[] distance = new int[mNodesNumber];
		int INFINITY = Integer.MAX_VALUE;

		// Initialize all the distance to infinity
		for (int i = 0; i < mNodesNumber; i++) {
			distance[i] = INFINITY;
		}

		// start from the vertex 0
		distance[sourceVertex] = 0;

		// create SPT
		for (int i = 0; i < mNodesNumber; i++) {

			// get the vertex with the minimum distance
			int vertex_U = getMinimumVertex(spt, distance);

			//System.out.println(vertex_U);
			// include this vertex in SPT
			spt[vertex_U] = true;

			// iterate through all the adjacent vertices of above vertex and update the keys
			for (int vertex_V = 0; vertex_V < mNodesNumber; vertex_V++) {
				// check of the edge between vertex_U and vertex_V
				if (mMatrix[vertex_U][vertex_V] > 0) {
					// check if this vertex 'vertex_V' already in spt and
					// if distance[vertex_V]!=Infinity

					if (spt[vertex_V] == false && mMatrix[vertex_U][vertex_V] != INFINITY) {
						// check if distance needs an update or not
						// means check total weight from source to vertex_V is less than
						// the current distance value, if yes then update the distance

						int newKey = mMatrix[vertex_U][vertex_V] + distance[vertex_U];
						if (newKey < distance[vertex_V])
							distance[vertex_V] = newKey;
					}
				}
			}
		}
		
		return distance;
	}
}
