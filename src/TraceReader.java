package cacheSimulator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class TraceReader {
	
	public static String dir = "/home/user/git/Cache-Simulator/Project/CacheSimulator/src/cacheSimulator/Traces/Unicore/";

	public static void main(String[] args) {
		File file = new File(dir + "FFT1.prg");
		String line; 
		String[] split = new String[2];
		int action;
		long address;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			BufferedReader dis = new BufferedReader(new InputStreamReader(bis));

			// dis.available() returns 0 if the file does not have more lines.
//			while (dis.ready()) {
			for(int i = 0; i<5; i++){
				line = dis.readLine();
				split=line.split(" ");
				action = Integer.parseInt((split[0]));
				address = Long.parseLong((split[1]),16);
				
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
