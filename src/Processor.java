package cacheSimulator;

import java.io.File;

public class Processor {
	File trace;
	Cache cache;
	int id;
	public Processor(String dir, Cache c, int id){
		this.trace = new File(dir);
	}
}