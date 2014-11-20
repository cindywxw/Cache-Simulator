import java.util.Map;

public class CacheTest {

	public static void main(String[] args) {
		// Test cache
		Cache dataCache = new Cache("MESI", 1024, 16, 2);
		
		System.out.println("HIT:" + dataCache.isHit(0x000000C0));
		System.out.println("NEXT BUS 1:" + dataCache.getNextBusState(0x000000C0, 2));
		dataCache.updateToNextState(0x000000C0, 2);
		
//		System.out.println("NEXT BUS 2:" + dataCache.getNextBusState(0x000000C0, 1));
//		dataCache.updateToNextState(0x000000C0, 1);
//		
//		System.out.println("NEXT BUS 3:" + dataCache.getNextBusState(0x00000080, 1));
//		dataCache.updateToNextState(0x00000080, 1);
		
		System.out.println(dataCache);

	}
		

}