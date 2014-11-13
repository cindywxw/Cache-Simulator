
public class Cache {
	/*@param Gets address to perform action on and the action as int 1=read 2=write
	Calculates nextState of cache entry and updates cache
	@return Returns Bus Action that is performed 0=none/flush 1=busRead 2= busReadEx */
	public int nextState(long address, int action){
		int busAction = 0;
		//super.setFlag(1); Please set Flag in case of cache hit
		return busAction;
	}
	
}
