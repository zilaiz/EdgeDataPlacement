package tool;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

// in this class, the generator will create a connected graph randomly, so density >= 1 and nodes >= 2
public class RandomGraphGenerator {

	private int[][] mAdjacencyMatrix;
	private int[][] mDistanceMatrix;
	private int mNodesNumber;
	private double mDensity;

	public RandomGraphGenerator(int nodesNumber, double density) {
		mNodesNumber = nodesNumber;
		mDensity = density;
		mAdjacencyMatrix = new int[mNodesNumber][mNodesNumber];
		mDistanceMatrix = new int[mNodesNumber][mNodesNumber];
	}

	public void createRandomGraph() {
		for (int i = 0; i < mNodesNumber; i++) {
			for (int j = 0; j < mNodesNumber; j++) {
				mAdjacencyMatrix[i][j] = 0;
			}
		}

		// create a connect graph
		for (int i = 0; i < mNodesNumber; i++) {
			int randomNode = getRandomNodeExceptItself(i);
			
			if (i == randomNode) {
				i--;
				continue;
			}
			
			mAdjacencyMatrix[i][randomNode] = 1;
			mAdjacencyMatrix[randomNode][i] = 1;

		}

		// if density is more than 1, then create more edges
		int otherEdges = (int) ((mDensity - 1) * mNodesNumber);

		for (int i = 0; i < otherEdges; i++) {
			int randomNode1 = getRandomNode();
			int randomNode2 = getRandomNodeExceptItself(randomNode1);
			
			if (randomNode1 == randomNode2) {
				i--;
				continue;
			}
			
			mAdjacencyMatrix[randomNode1][randomNode2] = 1;
			mAdjacencyMatrix[randomNode2][randomNode1] = 1;

			// System.out.println(randomNode1 + " " + randomNode2);
		}

		//test();

		if (isGraphConnected()) {
			//System.out.println("The random graph is a connected graph.");
			ShortestDistanceMatrixHelper path = new ShortestDistanceMatrixHelper(mNodesNumber, mAdjacencyMatrix);
			mDistanceMatrix = path.getShortestDistanceMatrix();
			//printMatrix(mAdjacencyMatrix, "adjacencyMatrix");
			//printMatrix(mDistanceMatrix, "shortestDistanceMatrix");
		} else {
			//System.out.println("The random graph is not a connected graph, re-creating.");
			//printMatrix(mAdjacencyMatrix, "adjacencyMatrix");
			createRandomGraph();
		}

//		System.out.println();
//		System.out.println();
	}

	@SuppressWarnings("unused")
	 private void test() {
	 mAdjacencyMatrix = new int[][] { 
		 {0,0,1,0,0,0,0,0,0,1},
		 {0,0,0,0,0,0,0,0,0,1},
		 {1,0,0,1,0,0,0,1,0,0},
		 {0,0,1,0,1,0,0,0,0,0},
		 {0,0,0,1,0,0,0,0,1,0},
		 {0,0,0,0,0,0,0,1,1,0},
		 {0,0,0,0,0,0,0,0,0,1},
		 {0,0,1,0,0,1,0,0,0,0},
		 {0,0,0,0,1,1,0,0,0,0},
		 {1,1,0,0,0,0,1,0,0,0}
		 };
	 }

	public int[][] getRandomGraphAdjacencyMatrix() {
		// printMatrix(mAdjacencyMatrix, "adjacencyMatrix");
		return mAdjacencyMatrix;
	}

	public int[][] getRandomGraphDistanceMatrix() {
		// printMatrix(mDistanceMatrix, "shortestDistanceMatrix");
		return mDistanceMatrix;
	}

	private int getRandomNodeExceptItself(int nodeNumber) {
		int number = nodeNumber;
		int round = 0;
		while (number == nodeNumber || mAdjacencyMatrix[number][nodeNumber] == 1
				|| mAdjacencyMatrix[nodeNumber][number] == 1) {
			// nextInt is normally exclusive of the top value,
			// so use mNodesNumber directly, and the upper limit is mNodesNumber - 1
			number = ThreadLocalRandom.current().nextInt(0, mNodesNumber);
			round++;
			if (round == mNodesNumber) break;
		}
		return number;
	}

	private int getRandomNode() {
		return ThreadLocalRandom.current().nextInt(0, mNodesNumber);
	}

	@SuppressWarnings("unused")
	private void printMatrix(int[][] matrix, String name) {
		System.out.println(name + ": ");
		for (int i = 0; i < mNodesNumber; i++) {
			for (int j = 0; j < mNodesNumber; j++) {
				System.out.print(matrix[i][j] + ",");
			}
			System.out.println();
		}

		System.out.println();
	}

	private boolean isGraphConnected() {
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(0);

		int visited = 1;
		int round = 0;
		
		while (visited < mNodesNumber && round < mNodesNumber) {
			for (int i = 0; i < mNodesNumber; i++) {
				for (int j = 0; j < mNodesNumber; j++) {
					if (queue.contains(i) && !queue.contains(j) && mAdjacencyMatrix[i][j] == 1) {
						queue.add(j);
						visited++;
					} else if (!queue.contains(i) && queue.contains(j) && mAdjacencyMatrix[i][j] == 1) {
						queue.add(i);
						visited++;
					}
				}
			}
			round++;
		}

		//System.out.println(queue);
		return queue.size() == mNodesNumber;
	}
}
