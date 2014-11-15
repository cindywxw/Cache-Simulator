import java.util.ArrayList;

public class Cache {

	// Protocol constants
	private static final String PROTOCOL_MESI = "MESI";
	private static final String PROTOCOL_MSI = "MSI";
	private static final int WORD_SIZE = 16;

	private ArrayList<CacheSet> dataCache;
	private int offsetBits;
	private int indexBits;
	private int tagBits;

	private String protocol;

	private boolean blocked;

	/**
	 * Initializes the cache
	 * 
	 * @param protocol
	 * @param cache_size
	 * @param block_size
	 * @param associativity
	 */
	public Cache(String protocol, int cacheSize, int blockSize,
			int associativity) {
		this.dataCache = new ArrayList<CacheSet>();

		// Calculate number of sets
		int numSets = cacheSize / (associativity * blockSize);

		// Create the cache structure
		for (int i = 0; i < numSets; i++) {
			dataCache.add(new CacheSet(associativity));
		}

		calculateBitLengths(blockSize, numSets);

		this.protocol = protocol;
		this.blocked = false;
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
		this.offsetBits = (int) (Math.log(blockSize / WORD_SIZE) / Math.log(2));
		this.tagBits = 32 - this.indexBits - this.offsetBits;

		System.out.println("Bits Index:" + indexBits + " Offset:" + offsetBits
				+ " Tag:" + tagBits);
	}

	/**
	 * Calculates nextState of cache entry and updates cache given actions
	 * 1.read 2.write super.setFlag(1); Please set Flag in case of cache hit
	 * 
	 * @param address
	 * @param action
	 * @return bus action that is performed 0=none/flush 1=busRead 2= busReadEx
	 */
	public int nextState(long address, int action) {
		int busAction = 0;

		CacheBlock found = findCacheBlock(address);

		int cacheIndex = getFoundIndexPosition(address);
		System.out.println("Index:" + cacheIndex);
		
		return busAction;
	}
	
	private CacheBlock findCacheBlock(long address) {
		
	}

	/**
	 * Returns the cache block index with the matching tag
	 * 
	 * @param address
	 * @return matching Cache Block or nil if no match
	 */
	private int getFoundIndexPosition(long address) {
		//Create mask for getting index bits
		int mask = (int) Math.pow(2, indexBits) - 1;
		long cacheIndex = (address >> offsetBits) & mask;
		return (int)cacheIndex;
	}

	public String toString() {
		String out = "------\n";

		for (CacheSet set : dataCache) {
			out += set.toString() + "\n";
		}

		out += "------";

		return out;
	}

}
