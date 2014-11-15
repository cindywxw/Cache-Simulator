import java.util.ArrayList;

enum CacheState {
	MODIFIED, EXCLUSIVE, SHARED, INVALID
}

enum BusState {
	NONE(0), BUSRD(1), BUSWR(2);
	
	private int type;
	
	BusState(int input) {
		type = input;
	}
	
	int getType() {
		return type;
	}
}

enum  Action {
	READ(0), WRITE(1), BUS_READ(2), BUS_RDX(3);
	
	private int type;
	
	Action(int input) {
		type = input;
	}
	
	int getType() {
		return type;
	}
	
}

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

		//Init
		BusState busAction = BusState.NONE;

		int index = getAddressIndexValue(address);
		int tag = getAddressTagValue(address);
		
		CacheState currentState = null;
		CacheState nextState = null;
		
		//Get cache block for given address
		CacheBlock matchingBlock = dataCache.get(index).getBlockForTag(tag);

		if (matchingBlock != null) {

			// TODO:SET FLAG IF HIT
			if (protocol.equals(PROTOCOL_MSI)) {
				currentState = getCurrentStateMSI(matchingBlock);
			} else if (protocol.equals(PROTOCOL_MESI)) {
				currentState = getCurrentStateMESI(matchingBlock);
			} else {
				System.out.println("Ooops, wrong protocol!");
			}

		} else {
			//Block does not exist in cache
			currentState = CacheState.INVALID;
		}
		//Get next state
		nextState = getNextStateMSI(currentState, Action.values()[action]);
		
		return busAction.getType();
	}
	
	/**
	 * Return next state for MSI
	 * @param current
	 * @param action
	 * @return
	 */
	private CacheState getNextStateMSI(CacheState current, Action action) {
		switch (current) {
		case MODIFIED:
			switch (action) {
			case READ:
				return CacheState.MODIFIED;
			case WRITE:
				return CacheState.MODIFIED;
			case BUS_READ:
				return CacheState.SHARED;// Need to flush
			case BUS_RDX:
				return CacheState.INVALID;// Need to flush
			}
		case SHARED:
			switch (action) {
			case READ:
				return CacheState.SHARED;
			case WRITE:
				return CacheState.MODIFIED;// Need to send BusRd_Ex
			case BUS_READ:
				return CacheState.SHARED;
			case BUS_RDX:
				return CacheState.INVALID;// Need to flush
			}
		case INVALID:
			switch (action) {
			case READ:
				return CacheState.SHARED;
			case WRITE:
				return CacheState.MODIFIED;
			case BUS_READ:
				return CacheState.INVALID;
			case BUS_RDX:
				return CacheState.INVALID;
			}
		}
		
		return null;
	}

	/**
	 * Return next state for MESI
	 * @param current
	 * @param action
	 * @return
	 */
	private CacheState getNextStateMESI(CacheState current, Action action) {
		switch (current) {
		case MODIFIED:
			switch (action) {
			case READ:
				return CacheState.MODIFIED;
			case WRITE:
				return CacheState.MODIFIED;
			case BUS_READ:
				return CacheState.SHARED;// Need to flush
			case BUS_RDX:
				return CacheState.INVALID;// Need to flush
			}
		case EXCLUSIVE:
			switch (action) {
			case READ:
				return CacheState.EXCLUSIVE;
			case WRITE:
				return CacheState.MODIFIED;
			case BUS_READ:
				return CacheState.SHARED;
			case BUS_RDX:
				return CacheState.INVALID;// Need to flush
			}
		case SHARED:
			switch (action) {
			case READ:
				return CacheState.SHARED;
			case WRITE:
				return CacheState.MODIFIED;// Need to send BusRd_Ex
			case BUS_READ:
				return CacheState.SHARED;
			case BUS_RDX:
				return CacheState.INVALID;// Need to flush
			}
		case INVALID:
			switch (action) {
			case READ:
				return CacheState.EXCLUSIVE;
			case WRITE:
				return CacheState.MODIFIED;
			case BUS_READ:
				return CacheState.INVALID;
			case BUS_RDX:
				return CacheState.INVALID;
			}
		}
		
		return null;
	}

	/**
	 * Current state based on MSI valid and dirty bit
	 * 
	 * @param matchingBlock
	 * @return current state
	 */
	private CacheState getCurrentStateMSI(CacheBlock matchingBlock) {
		CacheState current_state;

		if (matchingBlock.getValidBit() && matchingBlock.getDirtyBit()) {
			current_state = CacheState.MODIFIED;
		} else if (matchingBlock.getValidBit() && !matchingBlock.getDirtyBit()) {
			current_state = CacheState.SHARED;
		} else {
			current_state = CacheState.INVALID;
		}

		return current_state;
	}

	/**
	 * Current state based on MESI valid and dirty bit
	 * 
	 * @param matchingBlock
	 * @return current state
	 */
	private CacheState getCurrentStateMESI(CacheBlock matchingBlock) {
		CacheState current_state;

		if (matchingBlock.getValidBit() && !matchingBlock.getDirtyBit()
				&& matchingBlock.getExclusiveBit()) {
			current_state = CacheState.EXCLUSIVE;
		} else if (matchingBlock.getValidBit() && matchingBlock.getDirtyBit()
				&& !matchingBlock.getExclusiveBit()) {
			current_state = CacheState.MODIFIED;
		} else if (matchingBlock.getValidBit() && !matchingBlock.getDirtyBit()
				&& !matchingBlock.getExclusiveBit()) {
			current_state = CacheState.SHARED;
		} else {
			current_state = CacheState.INVALID;
		}

		return current_state;
	}

	/**
	 * Get tag value
	 * 
	 * @param address
	 * @return tag value
	 */
	private int getAddressTagValue(long address) {
		// Create mask for getting index bits
		long cacheTag = address >> (offsetBits + indexBits);
		return (int) cacheTag;
	}

	/**
	 * Returns the cache block index with the matching tag
	 *
	 * @param address
	 * @return matching Cache Block or nil if no match
	 */
	private int getAddressIndexValue(long address) {
		// Create mask for getting index bits
		int mask = (int) Math.pow(2, indexBits) - 1;
		long cacheIndex = (address >> offsetBits) & mask;
		return (int) cacheIndex;
	}

	/**
	 * Function that checks, depending on protocol and state of cache block if
	 * action requires a bus action and returns it
	 * 
	 * @param address
	 *            of memory location
	 * @param action
	 *            to be performed 2 = read 3 = write
	 * @return busAction that is performed 0=none/flush 1=busRead 2= busReadEx
	 */
	public int needsBus(long address, int action) { // TODO
		// Function that checks, depending on protocol and state of cache block
		// if action requires a bus action

		return 0;
	}

	/**
	 * Function that checks if there is a valid value in this cache for given
	 * address
	 * 
	 * @param address
	 *            of memory location
	 * @return true if hit, false if miss
	 */
	public boolean isHit(long address) { // TODO
		return false;
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
