package tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

import objectives.EdgeServer;
import objectives.User;

public class RandomUserListGenerator {

	public List<User> generateUsers(int serversNumber, int totalUsersNumber, double networkArea, List<EdgeServer> servers) {
		List<User> users = new ArrayList<>();
		RandomUserGenerator ug = new RandomUserGenerator();

		// System.out.println("totalUsersNumber: " + totalUsersNumber);

		int id = 0;
		// make sure each server at least server one user, otherwise we have this edge
		// server?
		for (EdgeServer server : servers) {
			User user = ug.generateUser(servers, server.fromArea, server.toArea, id);
			users.add(user);
			id++;

		}

		for (int i = 0; i < totalUsersNumber - serversNumber; i++) {
			User user = ug.generateUser(servers, 0, networkArea, id);
			users.add(user);
			id++;
		}

		return users;
	}

	public List<User> generateUserListFromRealWorldData(int serversNumber, List<EdgeServer> servers) {
		List<User> users = new ArrayList<>();
		List<User> allUsers = readUsersFromCsv();

		int id = 0;
		
//		while (id < 100) {
//			int number = ThreadLocalRandom.current().nextInt(0, allUsers.size());
//			User user = allUsers.get(number);
//			
//			boolean inRange = false;
//			
//			for (EdgeServer server: servers)  {
//				if (distance(server.lat, server.lng, user.lat, user.lng) <= server.radius) {
//					inRange = true;
//					break;
//				}
//			}
//			
//			if (inRange) {
//				for (EdgeServer server: servers) {
//					if (distance(server.lat, server.lng, user.lat, user.lng) <= server.radius) {
//						// in the coverage
//						user.nearEdgeServers.add(server.id);
//						user.id = id;
//						server.servedUsers.add(id);
//					}
//				}
//
//				if (user.nearEdgeServers.size() > 0) {
//					users.add(user);
//					allUsers.remove(user);
//					id++;
//				}
//				
//			}
//		}
		
		for (User user : allUsers) {
			for (EdgeServer server: servers) {
				if (distance(server.lat, server.lng, user.lat, user.lng) <= server.radius) {
					// in the coverage
					user.nearEdgeServers.add(server.id);
					user.id = id;
					server.servedUsers.add(id);
				}
			}
			if (user.nearEdgeServers.size() > 0) {
				users.add(user);
				id++;
			}
		}

		return users;
	}

	private double distance(double lat1, double lng1, double lat2, double lng2) {
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		GlobalPosition serverPoint = new GlobalPosition(lat1, lng1, 0.0); // Point A

		GlobalPosition userPoint = new GlobalPosition(lat2, lng2, 0.0); // Point B

		double distance = geoCalc.calculateGeodeticCurve(reference, userPoint, serverPoint).getEllipsoidalDistance();
		
		return distance;
	}

	public List<User> readUsersFromCsv() {
		File file = new File("eua-dataset-master/users/users-melbcbd-generated.csv");

		List<User> users = new ArrayList<>();

		Scanner sc;
		try {
			sc = new Scanner(file);
			sc.nextLine();
			while (sc.hasNextLine()) {
				User user = new User();
				String[] location = sc.nextLine().replaceAll(" ", "").split(",");
				user.lat = Double.parseDouble(location[0]);
				user.lng = Double.parseDouble(location[1]);
				users.add(user);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return users;
	}

	// public int[] getRandomUserList(int serversNumber) {
	// int[] list = new int[serversNumber];
	//
	// for (int i = 0; i < serversNumber; i++) {
	// int number = ThreadLocalRandom.current().nextInt(1, 10);
	// list[i] = number;
	// }
	//
	//// System.out.println("User Number Set");
	//// for (int i : list) System.out.print(i + " ");
	//// System.out.println();
	//// System.out.println();
	//
	// return list;
	// }
	//
	// public int[] getRandomUserListWithExtremeCase(int serversNumber) {
	// int[] list = new int[serversNumber];
	//
	// for (int i = 0; i < serversNumber; i++) {
	// int number = ThreadLocalRandom.current().nextInt(1, 10);
	// list[i] = number;
	// }
	//
	// int random = ThreadLocalRandom.current().nextInt(0, serversNumber);
	// list[random] = 100;
	//
	//// System.out.println("User Number Set");
	//// for (int i : list) System.out.print(i + " ");
	//// System.out.println();
	//// System.out.println();
	//
	// return list;
	// }
}
