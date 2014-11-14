
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class Processor {
	BufferedReader trace;
	Cache cache;
	int id;
	int hitFlag;
	public Processor(String dir, int id, String protocol, int cacheSize, int blockSize, int associativity){
		File file = new File(dir);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		
		BufferedInputStream bis = new BufferedInputStream(fis);
		this.trace = new BufferedReader(new InputStreamReader(bis));
		this.hitFlag = 0;
		this.id = id;
		this.cache = new Cache(protocol, cacheSize, blockSize, associativity);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void setFlag(int i){
		hitFlag = i;
	}
	public int getFlag(){
		return hitFlag;
	}
	public BufferedReader getTrace(){
		return this.trace;
	}
}