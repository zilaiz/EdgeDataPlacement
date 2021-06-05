package tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import objectives.EdgeServer;

public class RandomServersGenerator {
	// set random server area between 1 to 3
	public List<EdgeServer> generateEdgeServers(int serversNumber, double networkArea) {
		List<EdgeServer> servers = new ArrayList<>();
		//double networkArea = serversNumber * 2;
		for (int i = 0; i < serversNumber; i++) {
			EdgeServer server = new EdgeServer();
			server.id = i;
			server.fromArea = ThreadLocalRandom.current().nextDouble(0, networkArea);
			server.toArea = server.fromArea + ThreadLocalRandom.current().nextDouble(1, 3);
			if (server.toArea > networkArea) server.toArea = networkArea;
			servers.add(server);
		}
		
		return servers;
	}
	
	public List<EdgeServer> generateEdgeServerListFromRealWorldData(int serversNumber) {
		List<EdgeServer> servers = new ArrayList<>();
		List<EdgeServer> allServers = readServersFromCsv();
		
		for (int i = 0; i < serversNumber; i++) {
			int random = ThreadLocalRandom.current().nextInt(0, allServers.size());
			EdgeServer server = allServers.get(random);
			server.id = i;
			servers.add(server);
			allServers.remove(server);
		}
		
		return servers;
	}
	
	public List<EdgeServer> readServersFromCsv() {
		File file = new File("eua-dataset-master/edge-servers/site.csv");

		List<EdgeServer> servers = new ArrayList<>();
		
		Scanner sc;
		try {
			sc = new Scanner(file);
			sc.nextLine();
			while (sc.hasNextLine()) {
				EdgeServer server = new EdgeServer();
				String[] info = sc.nextLine().replaceAll(" ", "").split(",");
				
				server.radius = Double.parseDouble(info[0]);
				server.lat = Double.parseDouble(info[1]);
				server.lng = Double.parseDouble(info[2]);
				servers.add(server);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return servers;
	}
}
