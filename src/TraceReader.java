import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class TraceReader {
	private static String path = "/home/user/git/Cache-Simulator/";
	private static int blockTime;
	private static int coreCount;
	static int done = 0;
/*
 * Actions: 0: Fetch Instruction
 * 			1: Nothing
 * 			2: PrRead
 * 			3: PrWrite
 * 			5: PrRead(Shared) (Just for special case PrRead from invalid)
 * 			6: BusRead
 * 			7: BusWrite
 * 			
 */
	public static void main(String[] args) throws IOException {
		System.out.println("Protocol: " + args[0]);
		System.out.println("Benchmark: " + args[1]);
		System.out.println("Cores: " + args[2]);
		System.out.println("Cache Size: " + args[3]);
		System.out.println("Associativity: " + args[4]);
		System.out.println("Block Size: " + args[5]);
		coreCount = Integer.parseInt(args[2]); // number of cores
		Processor[] processorArray = new Processor[coreCount];
		// Initialize cores
		String coreString;
		switch (coreCount) {
		case 1:
			coreString = "Unicore/";
			break;
		case 2:
			coreString = "Dualcore/";
			break;
		case 4:
			coreString = "Quadcore/";
			break;
		case 8:
			coreString = "OctaCore/";
			break;
		default:
			System.out.println("Invalid number of cores!");
			return;
		}
		// Array of Cores; A core becomes null when its trace is finished
		//arguments: protocol(0), benchmark(1), cores(2), cache size(3), associativity(4), block size(5)
		for (int i = 0; i < coreCount; i++) {
			processorArray[i] = new Processor(path + coreString + args[1] + (i + 1)
					+ ".prg", i, args[0], Integer.parseInt(args[3]),
					Integer.parseInt(args[5]), Integer.parseInt(args[4]));
		}

		String s;
		String[] split = new String[2];
		long address;
		long cycles = 0;
		long busCount = 0;
		int busNotUsed = 0;
		int action;
		int busAction;
		Processor p;
		Quadrupel q = null;
		LinkedList<Quadrupel> busList = new LinkedList<Quadrupel>();
		while (true) {
			cycles++;

			for (int i = 0; i < coreCount; i++) {// Process cycle operations for
													// all cores
				p = getNextProcessor(processorArray, i);
				i = i + done;
				done = 0;
				if (p == null){
					printResults(cycles, processorArray, busCount, busNotUsed);
					return;// All traces have been processed
				}
				if (!p.inQueue) { // Processor not blocked by being in BusQueue
					p.cycles++;
					s = p.getCycle();
					split = s.split(" ");
					action = Integer.parseInt(split[0]);
					if (action != 0) { // Action is read or write
						address = Long.parseLong(split[1],16);
						busAction = p.cache.needsBus(address, action);
						if (busAction == 0) { // Read or Write doesn't require
												// bus
							p.hits++;
							p.cache.nextState(address, action);
						} else {// Read or write requires bus
							busList.add(new Quadrupel(p, address, busAction, action));
							p.misses++;
							p.inQueue = true;
						}
					}
				}
			}
			// Process bus
			boolean hitFlag = false;
			if (blockTime > 0) { //Bus blocked by data transfer
				blockTime--;
			} else {
				if(q != null){
					//Processor Action is executed when data transmit is done and bus is free
					q.p.cache.nextState(q.address, q.action);
					q.p.inQueue = false;
				}
				q = busList.pollFirst();
				if (q != null) {
					busCount = busCount + 16;
					for (int i = 0; i < coreCount; i++) {
						if (i != q.p.id) {
							if(processorArray[i].cache.isHit(q.address)){ // Other cache has needed data
								blockTime = 1;
								hitFlag = true; //Accessing shared data
								processorArray[i].cache.nextState(q.address,q.busAction);
							}
						}
					}
					if(q.busAction == 5){ //BusReadEX
						blockTime = 10; 
					}else if(q.busAction == 4){ //BusRead
						if(!hitFlag){
							blockTime = 10; //No cache has needed data
						}else{
							blockTime = 1;
							q.action = 5; // Read of shared data
						}
					}else{
						System.out.println("Bus action invalid, aborting!");
						return;
					}
					hitFlag = false;
				}else{
					busNotUsed++;
				}
				
			}
		}
	}

	public static Processor getNextProcessor(Processor[] p, int id) {
		for (int i = id; i < coreCount; i++) {
			if (p[i].done == false){
				return p[i];
			}else {
				done++;
			}
		}
		return null;
	}
	
	public static void printResults(long cycles, Processor[] processors, long busCount, int busNotUsed){
		int length = processors.length;
		System.out.println("Cycles taken: " + cycles);
		System.out.println("Bytes transfered on bus: " + busCount);
		System.out.println("Cycles in which bus was not used: " + busNotUsed);
		for(int i = 1; i<= length; i++){
			System.out.println("Number of execution Cycles Processor " + i + ": " + processors[i-1].cycles);
			System.out.println("Number of cache hits Processor " + i + ": " + processors[i-1].hits);
			System.out.println("Number of cache misses Cycles Processor " + i + ": " + processors[i-1].misses);
		}
		
	}
}
//Adjust action/busAction numbers to the ones in cache
//Rewrite getNextProcessor();