import java.io.IOException;
import java.util.Queue;

public class TraceReader {
	private static Queue<Quadrupel> busList;
	private static int blockTime;
	private static int coreCount;

	public static void main(String[] args) throws IOException {
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
		for (int i = 0; i < coreCount; i++) {
			processorArray[i] = new Processor(coreString + args[1] + (i + 1)
					+ ".prg", i, args[0], Integer.parseInt(args[3]),
					Integer.parseInt(args[5]), Integer.parseInt(args[4]));
		}

		String s;
		String[] split = new String[2];
		long address;
		long cycles = 0;
		int action;
		int busAction;
		Processor p;
		Quadrupel q;

		while (true) {
			cycles++;

			for (int i = 0; i < coreCount; i++) {// Process cycle operations for
													// all cores
				p = getNextProcessor(processorArray, i);
				if (p == null)
					return;// All traces have been processed
				if (!p.inQueue) { // Processor not blocked by bing in BusQueue
					s = p.getCycle();
					split = s.split(" ");
					action = Integer.parseInt(split[0]);
					if (action != 0) { // Action is read or write
						address = Long.parseLong(split[1]);
						busAction = p.cache.needsBus(address, action);
						if (busAction == 0) { // Read or Write doesn't require
												// bus
							p.cache.nextState(address, action);
						} else {// Read or write requires bus
							busList.add(new Quadrupel(p, address, busAction, action));
							p.inQueue = true;
						}
					}
				}
			}
			// Process bus
			if (blockTime > 0) { //Bus blocked by data transfer
				blockTime--;
			} else {
				q = busList.poll();
				if (q != null) {
					for (int i = 0; i < coreCount; i++) {
						if (i != q.p.id) {
							if(processorArray[i].cache.isHit(q.address)){ // Other cache has needed data
								blockTime = 1;
							}
							processorArray[i].cache.nextState(q.address,q.busAction);
						}
					}
					if(q.busAction == 2){
						blockTime = 0; //Bus only gets blocked for 10 cycles if no cache has valid data for address
					}else if(q.busAction == 1){ 
						if(blockTime == 0)blockTime = 10; //No cache has needed data
					}else{
						System.out.println("Bus action invalid, aborting!");
						return;
					}
					q.p.cache.nextState(q.address, q.action); 
				}
			}
		}
	}

	public static Processor getNextProcessor(Processor[] p, int id) {
		int j = id;
		for (int i = 0; i < coreCount; i++) {
			j = (j + 1) % coreCount;
			if (p[j].done == false)
				return p[j];
		}
		return null;
	}
}
