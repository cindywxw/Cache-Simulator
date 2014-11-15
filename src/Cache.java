import java.util.ArrayList;

public class Cache {

	// Protocol constants
	private static final String PROTOCOL_MESI = "MESI";
	private static final String PROTOCOL_MSI = "MSI";

	private ArrayList<CacheSet> dataCache;
	private int offsetBits;
	private int indexBits;
	private int tagBits;
	private String protocol;

	/**
	 * Initializes the cache
	 * 
	 * @param protocol
	 * @param cache_size
	 * @param block_size
	 * @param associativity
	 */
	public Cache(String protocol, int cacheSize, int blockSize, int associativity) {
		this.dataCache = new ArrayList<CacheSet>();

		// Calculate number of sets
		int numSets = cacheSize / (associativity * blockSize);

		// Create the cache structure
		for (int i = 0; i < numSets; i++) {
			dataCache.add(new CacheSet(associativity));
		}

		calculateBitLengths(blockSize, numSets);
		
		this.protocol = protocol;  
	}

	/**
	 * Calculate bit lengths of tag, index and offset
	 * 
	 * @param blockSize
	 * @param numSets
	 */
	private void calculateBitLengths(int blockSize, int numSets) {
		// Set bit lengths
		this.indexBits = (int) (Math.log(numSets) / Math.log(2));
		this.offsetBits = (int) (Math.log(blockSize / 2) / Math.log(2));
		this.tagBits = 32 - this.indexBits - this.offsetBits;
	}

	/**
	 * Calculates nextState of cache entry and updates cache
	 * 
	 * @param address
	 *            of memory location
	 * @param action
	 *            to be performed 1.read 2.write
	 * @return bus action that is performed 0=none/flush 1=busRead 2= busReadEx
	 */
	public void nextState(long address, int action) { //TODO
		int busAction = 0;
		return;
	}
	/**
	 * Function that checks, depending on protocol and state of cache block if action requires a bus action and returns it
	 * 
	 * @param address
	 *            of memory location
	 * @param action
	 *            to be performed 2 = read 3 = write
	 * @return busAction that is performed 0=none/flush 1=busRead 2= busReadEx
	 */
	public int needsBus(long address, int action){ //TODO
		//Function that checks, depending on protocol and state of cache block if action requires a bus action
		
		return 0;
	}
	/**
	 * Function that checks if there is a valid value in this cache for given address
	 * @param address of memory location
	 * @return true if hit, false if miss
	 */
	public boolean isHit(long address){ // TODO
		return false;
	}
	
	public String toString() {
		String out = "------\n";
		
		for(CacheSet set: dataCache) {
			out += set.toString() + "\n";
		}
		
		out += "------";
		
		return out;
	}

}
