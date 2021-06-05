package models;

import java.util.ArrayList;
import java.util.List;

import objectives.EdgeServer;
import objectives.User;

public class HGAModel {
	private double mBenefitPerReplica;
	private double mCost;
	private double mAllBenefits;

//	private int mServersNumber;
//	private int[][] mAdjacencyMatrix;
//	private int[][] mUserBenefits;
	private List<User> mUsers;
	List<EdgeServer> mServers;

	private List<Integer> mValidUserList;
//	private Map<Integer, Integer> mBenefitsMap;
	private List<Integer> mSelectedServerList;
	private List<Integer> mBenefitsList;
//	private List<List<Integer>> mNewUsersCoveredByServerList;

	public HGAModel(int serversNumber, int[][] adjacencyMatrix, int[][] userBenefits, List<User> users,
			List<EdgeServer> servers) {
//		mServersNumber = serversNumber;
//		mAdjacencyMatrix = adjacencyMatrix;
//		mUserBenefits = userBenefits;
		mUsers = users;
		mServers = servers;
		mValidUserList = new ArrayList<>();
		mSelectedServerList = new ArrayList<>();
		mBenefitsList = new ArrayList<>();
//		mBenefitsMap = new HashMap<>();
//		mNewUsersCoveredByServerList = new ArrayList<>();
	}

//	public void runGA() {
//		updateNewUsersCoveredByServerList();
//		for (User user : mUsers) {
//			mBenefitsMap.put(user.id, 0);
//		}
//		while (mValidUserList.size() < mUsers.size()) {
//			selectServerWithMaximumIncreasingBenefits();
//		}
//
//		mAllBenefits = calculateBenefitsMap(mBenefitsMap);
//		mCost = mSelectedServerList.size();
//		mBenefitPerReplica = mAllBenefits / mCost;
//
//		// System.out.println("Total Cost = " + mCost);
//		//
//		// System.out.println("Benefit/Cost = " + mBenefitPerReplica);
//		//
//		// System.out.println("Selected servers are:");
//		// for (int server : mSelectedServerList) System.out.print(server + " ");
//		//
//		// System.out.println();
//	}
	
	public void runHGA() {
//		for (User user : mUsers) {
//			mBenefitsMap.put(user.id, 0);
//			mBenefitsList.add(0);
//		}
		
		for (int i = 0; i < mUsers.size(); i++) mBenefitsList.add(0);
		
		mAllBenefits = 0;
		
		while (mValidUserList.size() < mUsers.size()) {
//			updateNewUsersCoveredByServerList();
			int selected = bubbleServerWithMaximumIncreasingUncoveredUsers();
			mSelectedServerList.add(selected);
			mValidUserList.addAll(mServers.get(selected).getCoveredUsersWithinOneHopNotInTheList(mValidUserList));
		}
		
		for (int serverId : mSelectedServerList) {
			for (int userId : mServers.get(serverId).servedUsers) {
				mBenefitsList.set(userId, 2);
			}
			for (int userId : mServers.get(serverId).usersInOneHop) {
				if (mBenefitsList.get(userId) < 1) mBenefitsList.set(userId, 1);
			}
		}
		
		//System.out.print("Benefits list: ");
		for (int benefits : mBenefitsList) {
			mAllBenefits = mAllBenefits + benefits;
			//System.out.print(benefits + " ");
		}
		//System.out.println();

		mCost = mSelectedServerList.size();
		mBenefitPerReplica = mAllBenefits / mCost;
		
//		System.out.println("Total Cost = " + mCost);
//		System.out.println("Total Benefits = " + mAllBenefits);
//		System.out.println("Benefit/Cost = " + mBenefitPerReplica);
//
//		System.out.println("Selected servers are:");
//		for (int server : mSelectedServerList) System.out.print(server + " ");
//
//		System.out.println();
	}

	private int bubbleServerWithMaximumIncreasingUncoveredUsers() {
		int id = -1;
		int cu = 0;
		int dcu = 0;
		for (EdgeServer server : mServers) {
			if (server.getCoveredUsersWithinOneHopNotInTheList(mValidUserList).size() > cu ||
					(server.getCoveredUsersWithinOneHopNotInTheList(mValidUserList).size() == cu
							&& server.getDirectlyCoveredUsersNotInTheList(mValidUserList).size() > dcu)
					) {
				cu = server.getCoveredUsersWithinOneHopNotInTheList(mValidUserList).size();
				dcu = server.getDirectlyCoveredUsersNotInTheList(mValidUserList).size();
				id = server.id;
			}
		}
		
		return id;
	}

//	private void updateNewUsersCoveredByServerList() {
//		for (EdgeServer server : mServers) {
//			mNewUsersCoveredByServerList.add(server.getCoveredUsersNotInTheList(mValidUserList));
//		}
//	}

//	private void selectServerWithMaximumIncreasingBenefits() {
//		//Map<Integer, Integer> tmpMap = new HashMap<>();
//		//tmpMap.forEach(mBenefitsMap::putIfAbsent);
//		// int maxBenefits = calculateBenefitsMap(tmpMap);
//		int selectServerId = -1;
//		int newUserNumber = 0;
//
//		for (EdgeServer server1 : mServers) {
//			int tmpNewUserNumber = 0;
//			if (mSelectedServerList.contains(server1.id))
//				continue;
//
//			for (int userId : mServers.get(server1.id).servedUsers) {
//				//tmpMap.put(userId, 2);
//				if (!mValidUserList.contains(userId))
//					tmpNewUserNumber++;
//			}
//
//			for (EdgeServer server2 : mServers) {
//				if (mAdjacencyMatrix[server1.id][server2.id] == 1 || mAdjacencyMatrix[server2.id][server1.id] == 1) {
//					for (User user : mUsers) {
//						if (user.nearEdgeServers.contains(server2.id) && mBenefitsMap.get(user.id) < 1) {
//							//tmpMap.put(user.id, 1);
//							if (!mValidUserList.contains(user.id))
//								tmpNewUserNumber++;
//						}
//					}
//				}
//			}
//			// int currentBenefits = calculateBenefitsMap(tmpMap);
//			// if (currentBenefits > maxBenefits) {
//			// maxBenefits = currentBenefits;
//			// selectServerId = server1.id;
//			// }
//			if (tmpNewUserNumber > newUserNumber) {
//				newUserNumber = tmpNewUserNumber;
//				selectServerId = server1.id;
//			}
//		}
//
//		// mAllBenefits = maxBenefits;
//		mSelectedServerList.add(selectServerId);
//		updateBenefitsMapAndValidUserList();
//	}

//	private int calculateBenefitsMap(Map<Integer, Integer> map) {
//		int totalBenefits = 0;
//
//		for (int benefits : map.values()) {
//			totalBenefits = totalBenefits + benefits;
//		}
//
//		return totalBenefits;
//	}
//
//	private void updateBenefitsMapAndValidUserList() {
//		for (int serverId : mSelectedServerList) {
//			for (int userId : mServers.get(serverId).servedUsers) {
//				mBenefitsMap.put(userId, 2);
//				if (!mValidUserList.contains(userId)) {
//					mValidUserList.add(userId);
//				}
//			}
//
//			for (EdgeServer server : mServers) {
//				if (mAdjacencyMatrix[server.id][serverId] == 1 || mAdjacencyMatrix[serverId][server.id] == 1) {
//					for (User user : mUsers) {
//						if (user.nearEdgeServers.contains(server.id)) {
//							if (mBenefitsMap.get(user.id) < 1) {
//								mBenefitsMap.put(user.id, 1);
//							}
//
//							if (!mValidUserList.contains(user.id)) {
//								mValidUserList.add(user.id);
//							}
//						}
//					}
//				}
//			}
//		}
//	}

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
