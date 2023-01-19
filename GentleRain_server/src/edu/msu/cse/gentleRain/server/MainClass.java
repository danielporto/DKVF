package edu.msu.cse.gentlerain.server;

import edu.msu.cse.dkvf.config.ConfigReader;

public class MainClass {
	public static void main(String args[]) throws IllegalAccessException {
		ConfigReader cnfReader = new ConfigReader(args[0]);
		GentleRainServer gServer = new GentleRainServer(cnfReader);
		gServer.runAll();
	}
}
