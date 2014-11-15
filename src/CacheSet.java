import java.util.ArrayList;

/**
 * Data structure to represent the cache row, consisting of a cache blocks
 * (number based on associativity) and LRU bits
 * 
 * @author Shekhar
 *
 */
public class CacheSet {

	private int lruBits;
	private int ways;
	private ArrayList<CacheBlock> blocks;

	public CacheSet(int associativity) {
		blocks = new ArrayList<CacheBlock>();

		// Add the cache blocks based on number of ways
		for (int i = 0; i < associativity; i++) {
			blocks.add(new CacheBlock());
		}

		ways = associativity;
	}
	
	public CacheBlock getBlockForTag(int tag) {
		for(CacheBlock block: blocks) {
			if(block.getTag() == tag) {
				return block;
			} 
		}
		return null;
	}

	public String toString() {
		String out =  "{" + "[" + lruBits + "]";
		
		for(CacheBlock blk: blocks) {
			out += " " + blk;
		}
		
		out += "}";
		
		return out;
	}
}
