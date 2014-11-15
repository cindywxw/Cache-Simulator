
public class CacheTest {

	public static void main(String[] args) {
		// Test cache
		Cache dataCache = new Cache("MESI", 4096, 64, 2);
		
		dataCache.nextState(0x11114567, 0);
		
		System.out.println(dataCache);
	}

}
