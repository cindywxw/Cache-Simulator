import java.util.Map;

/**
 * Data structure to represent the cache row, consisting of a cache blocks
 * (number based on associativity) and LRU bits
 * 
 * @author Shekhar
 *
 */
public class CacheSet {

	private int ways;
	private LRUCache<Integer, CacheBlock> blocks;

	public CacheSet(int associativity) {
		blocks = new LRUCache<Integer, CacheBlock>(associativity);

		ways = associativity;
	}

	public CacheBlock getBlockForTag(int tag) {
		CacheBlock found = blocks.get(tag);
		if (found == null) {
			return null;
		} else {
			return found;
		}
	}
	
	public void remove(CacheBlock block) {
		blocks.remove(block.getTag());
	}

	public void installBlock(CacheBlock newBlock) {
		blocks.put(newBlock.getTag(), newBlock);
	}

	public String toString() {
		String out = "{";

		for (Map.Entry<Integer, CacheBlock> e : blocks.getAll()) {
			out += e.getValue().toString();
		}

		out += " }";

		return out;
	}
}
