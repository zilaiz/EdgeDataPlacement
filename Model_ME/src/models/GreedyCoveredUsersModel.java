package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objectives.EdgeServer;
import objectives.User;

public class GreedyCoveredUsersModel {
	
	private double mBenefitPerReplica;
	private double mCost;
	private double mAllBenefits;

	private int mServersNumber;
	private int[][] mAdjacencyMatrix;
	private int[][] mUserBenefits;
	private List<User> mUsers;
	List<EdgeServer> mServers;
	
	private Map<Integer, Integer> mCoveredUsersMap;
	private List<Integer> mValidUserList;
	private List<Integer> mSelectedServerList;
	private List<Integer> mBenefitList;
	
	public GreedyCoveredUsersModel(int serversNumber, int[][] adjacencyMatrix, int[][] userBenefits, List<User> users, List<EdgeServer> servers) {
		mServersNumber = serversNumber;
		mAdjacencyMatrix = adjacencyMatrix;
		mUserBenefits = userBenefits;
		mUsers = users;
		mServers = servers;
		mValidUserList = new ArrayList<>();
		mSelectedServerList = new ArrayList<>();
		mBenefitList = new ArrayList<>();
		mCoveredUsersMap = new HashMap<>();
	}
	
	public void runGreedyCoveredUsers() {
		
		for (int i = 0; i < mServersNumber; i++) {
			mCoveredUsersMap.put(i, mServers.get(i).servedUsers.size());
		}
		
		while (mValidUserList.size() < mUsers.size()) {
			selectServerWithMaximumCoveredUsers();
		}
		
		mCost = mSelectedServerList.size();
		mAllBenefits = calculateBenefits();
		mBenefitPerReplica = mAllBenefits / mCost;
		
//		System.out.println("Total Cost = " + mCost);
//
//		System.out.println("Benefit/Cost = " + mBenefitPerReplica);
//		
//		System.out.println("Selected servers are:");
//		for (int server : mSelectedServerList) System.out.print(server + " ");
//		
//		System.out.println();
	}
	
	private double calculateBenefits() {
		mBenefitList.clear();
		for (int i = 0; i < mUsers.size(); i++) {
			mBenefitList.add(0);
		}

		double benefits = 0;
		
		for (User user : mUsers) {
			for (int server : user.nearEdgeServers) {
				int benefit = 1;
				if (mSelectedServerList.contains(server)) benefit = mUserBenefits[server][user.id];
				if (mBenefitList.get(user.id) < benefit) mBenefitList.set(user.id, benefit);
			}
		}
		
		for (int benefit : mBenefitList) benefits = benefits + benefit;
		
		return benefits;
	}
	
	private int selectServerWithMaximumCoveredUsers() {
		int maximumCoveredUsers = -1;
		int serverWithMaximumCoveredUsers = -1;
		for (int server : mCoveredUsersMap.keySet()) {
			int coveredUsers = mCoveredUsersMap.get(server);
			if (coveredUsers > maximumCoveredUsers) {
				maximumCoveredUsers = coveredUsers;
				serverWithMaximumCoveredUsers = server;
			}
		}
		
		mCoveredUsersMap.remove(serverWithMaximumCoveredUsers);
		
		mSelectedServerList.add(serverWithMaximumCoveredUsers);
		
		for (User user : mUsers) {
			if (user.nearEdgeServers.contains(serverWithMaximumCoveredUsers) && !mValidUserList.contains(user.id)) {
				mValidUserList.add(user.id);
			}
		}

		for (int i = 0; i < mServersNumber; i++) {
			if (mAdjacencyMatrix[i][serverWithMaximumCoveredUsers] == 1 || mAdjacencyMatrix[serverWithMaximumCoveredUsers][i] == 1) {
				// mConnectionsMap.remove(i);
				for (User user : mUsers) {
					if (user.nearEdgeServers.contains(i) && !mValidUserList.contains(user.id)) {
						mValidUserList.add(user.id);
					}
				}
			}
		}
		
		return serverWithMaximumCoveredUsers;
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
