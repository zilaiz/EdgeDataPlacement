package objectives;

import java.util.ArrayList;
import java.util.List;

public class EdgeServer {
	public double lat;
	public double lng;
	public double radius;
	public int id;
	public List<Integer> servedUsers = new ArrayList<>();
	public double fromArea;
	public double toArea;
	public List<Integer> usersInOneHop = new ArrayList<>();
	
	public List<Integer> getCoveredUsersWithinOneHopNotInTheList(List<Integer> list) {
		List<Integer> users = new ArrayList<>();
		
		for (int id : usersInOneHop) {
			if (!list.contains(id)) users.add(id);
		}
		
		return users;
	}
	
	public List<Integer> getDirectlyCoveredUsersNotInTheList(List<Integer> list) {
		List<Integer> users = new ArrayList<>();
		
		for (int id : servedUsers) {
			if (!list.contains(id)) users.add(id);
		}
		
		return users;
	}
}
