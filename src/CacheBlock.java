/**
 * Contains the individual cache block with its valid and dirty bit
 * @author Shekhar
 *
 */
public class CacheBlock {
	
	private boolean valid;
	private boolean dirty;
	private int tag;	
	
	public CacheBlock() {
		valid = false;
		dirty = false;
		tag = 0;
	}
	
	public boolean getValidBit() {
		return valid;
	}
	
	public boolean getDirtyBit() {
		return dirty;
	}
	
	public int getTag() {
		return tag;
	}
	
	public String toString() {
		return "[" + valid + " " + dirty + " " + tag + "]";
	}
	
}
