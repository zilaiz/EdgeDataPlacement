package tool;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import objectives.EdgeServer;
import objectives.User;

public class RandomUserGenerator {
		
	public User generateUser(List<EdgeServer> servers, double fromArea, double toArea, int id) {
		User user = new User();
		user.id = id;
		user.location = -1;
		
		while (!isLocationValid(user.location, servers)) {
			user.location = ThreadLocalRandom.current().nextDouble(fromArea, toArea);
		}
		
		for (int i = 0; i < servers.size(); i++) {
			if (user.location >= servers.get(i).fromArea && user.location <= servers.get(i).toArea) {
				user.nearEdgeServers.add(i);
				servers.get(i).servedUsers.add(id);
			}
		}
		
		return user;
	}
	
	private boolean isLocationValid(double location, List<EdgeServer> servers) {
		for (int i = 0; i < servers.size(); i++) {
			if (location >= servers.get(i).fromArea && location <= servers.get(i).toArea) {
				return true;
			}
		}
		return false;
	}
	
}
