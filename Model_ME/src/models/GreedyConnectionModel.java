package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objectives.User;

public class GreedyConnectionModel {

	private double mBenefitPerReplica;
	private double mCost;
	private double mAllBenefits;

	private int mServersNumber;
	private int[][] mAdjacencyMatrix;
	private int[][] mUserBenefits;
	private List<User> mUsers;

	private List<Integer> mValidUserList;

	private Map<Integer, Integer> mConnectionsMap;
	private List<Integer> mSelectedServerList;
	private List<Integer> mBenefitList;

	public GreedyConnectionModel(int serversNumber, int[][] adjacencyMatrix, int[][] userBenefits, List<User> users) {
		mServersNumber = serversNumber;
		mAdjacencyMatrix = adjacencyMatrix;
		mUserBenefits = userBenefits;
		mUsers = users;
		mValidUserList = new ArrayList<>();
		mSelectedServerList = new ArrayList<>();
		mBenefitList = new ArrayList<>();
		mConnectionsMap = new HashMap<>();
	}

	public void runGreedyConnection() {
		for (int i = 0; i < mServersNumber; i++) {
			int connection = 0;
			for (int c : mAdjacencyMatrix[i])
				connection = connection + c;
			mConnectionsMap.put(i, connection);
		}

		while (mValidUserList.size() < mUsers.size()) {
			selectServerWithMaximumConnections();
		}

		mCost = mSelectedServerList.size();
		mAllBenefits = calculateBenefits();
		mBenefitPerReplica = mAllBenefits / mCost;

//		System.out.println("Total Cost = " + mCost);
//
//		System.out.println("Benefit/Cost = " + mBenefitPerReplica);
//
//		System.out.println("Selected servers are:");
//		for (int server : mSelectedServerList)
//			System.out.print(server + " ");
//
//		System.out.println();
	}

	private double calculateBenefits() {
		// mBenefitList.clear();
		// for (int i = 0; i < mServersNumber; i++) {
		// mBenefitList.add(0);
		// }
		//
		// double benefits = 0;
		//
		// for (int server : mSelectedServerList) {
		// for (int i = 0; i < mServersNumber; i++) {
		// int savedHops = 2 - mDistanceMatrics[server][i];
		// if (savedHops < 0) savedHops = 0;
		// int benefit = savedHops * mUserNumberList[i];
		// if (mBenefitList.get(i) < benefit) mBenefitList.set(i, benefit);
		// }
		// }

		mBenefitList.clear();
		for (int i = 0; i < mUsers.size(); i++) {
			mBenefitList.add(0);
		}

		double benefits = 0;

		for (User user : mUsers) {
			for (int server : user.nearEdgeServers) {
				int benefit = 1;
				if (mSelectedServerList.contains(server))
					benefit = mUserBenefits[server][user.id];
				if (mBenefitList.get(user.id) < benefit)
					mBenefitList.set(user.id, benefit);
			}
		}

		for (int benefit : mBenefitList)
			benefits = benefits + benefit;

		return benefits;
	}

	private int selectServerWithMaximumConnections() {
		int maximumConnections = 0;
		int serverWithMaximumConnections = -1;
		for (int server : mConnectionsMap.keySet()) {
			int connections = mConnectionsMap.get(server);
			if (connections > maximumConnections) {
				maximumConnections = connections;
				serverWithMaximumConnections = server;
			}
		}
		mConnectionsMap.remove(serverWithMaximumConnections);

		mSelectedServerList.add(serverWithMaximumConnections);
		
		for (User user : mUsers) {
			if (user.nearEdgeServers.contains(serverWithMaximumConnections) && !mValidUserList.contains(user.id)) {
				mValidUserList.add(user.id);
			}
		}

		for (int i = 0; i < mServersNumber; i++) {
			if (mAdjacencyMatrix[i][serverWithMaximumConnections] == 1 || mAdjacencyMatrix[serverWithMaximumConnections][i] == 1) {
				// mConnectionsMap.remove(i);
				for (User user : mUsers) {
					if (user.nearEdgeServers.contains(i) && !mValidUserList.contains(user.id)) {
						mValidUserList.add(user.id);
					}
				}
			}
		}

		// if (!mServerValidList.contains(serverWithMaximumConnections))
		// mServerValidList.add(serverWithMaximumConnections);
		//
		// for (int i = 0; i < mServersNumber; i++) {
		// if (mAdjacencyMatrix[i][serverWithMaximumConnections] == 1 ||
		// mAdjacencyMatrix[serverWithMaximumConnections][i] == 1) {
		// //mConnectionsMap.remove(i);
		// if (!mServerValidList.contains(i)) {
		// mServerValidList.add(i);
		// }
		// }
		// }

		return serverWithMaximumConnections;
	}

	public double getCost() {
		return mCost;
	}

	public double getBenefitPerReplica() {
		return mBenefitPerReplica;
	}

	public double getAllBenefits() {
		return mAllBenefits;
	}
}
