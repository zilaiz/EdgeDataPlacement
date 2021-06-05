package models;

import java.util.ArrayList;
import java.util.List;

import objectives.User;
import tool.RandomNodeGenerator;

public class RandomModel {
	
	private double mBenefitPerReplica;
	private double mCost;
	private double mAllBenefits;

	private int mServersNumber;
	private int[][] mAdjacencyMatrix;
	private int[][] mUserBenefits;
	private List<User> mUsers;
	
	private List<Integer> mValidUserList;
	
	private List<Integer> mSelectedServerList;
	private List<Integer> mBenefitList;
	
	public RandomModel(int serversNumber, int[][] adjacencyMatrix, int[][] userBenefits, List<User> users) {
		mServersNumber = serversNumber;
		mAdjacencyMatrix = adjacencyMatrix;
		mUserBenefits = userBenefits;
		mUsers = users;
		

		mValidUserList = new ArrayList<>();
		mSelectedServerList = new ArrayList<>();
		mBenefitList = new ArrayList<>();
		
	}
	
	private boolean canServerAccessSelectedServers(int server) {
		for (int ss : mSelectedServerList) {
			if (ss == server) return true;
			//if (mAdjacencyMatrix[ss][server] == 1 || mAdjacencyMatrix[server][ss] == 1) return true;
		}
		
		return false;
	}
	
	public void runRandom() {
		// randomly select first server
		RandomNodeGenerator randomGenerator = new RandomNodeGenerator();
		int randomServer;
		//randomServer = randomGenerator.getRandomNode(mServersNumber);
		
		while (mValidUserList.size() < mUsers.size()) {
			randomServer = randomGenerator.getRandomNode(mServersNumber);
			//System.out.println(randomServer);
			if (canServerAccessSelectedServers(randomServer)) continue;
			mSelectedServerList.add(randomServer);
			for (User user : mUsers) {
				if (user.nearEdgeServers.contains(randomServer) && !mValidUserList.contains(user.id)) {
					mValidUserList.add(user.id);
				}
			}

			for (int i = 0; i < mServersNumber; i++) {
				if (mAdjacencyMatrix[i][randomServer] == 1 || mAdjacencyMatrix[randomServer][i] == 1) {
					// mConnectionsMap.remove(i);
					for (User user : mUsers) {
						if (user.nearEdgeServers.contains(i) && !mValidUserList.contains(user.id)) {
							mValidUserList.add(user.id);
						}
					}
				}
			}
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
