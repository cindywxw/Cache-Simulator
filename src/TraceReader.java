import java.io.IOException;

public class TraceReader {

	public static void main(String[] args) throws IOException {
		int coreCount = Integer.parseInt(args[2]); // number of cores
		int done = 0; // number of traces that have finished
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
		int active = 0;
		String s;
		String[] split = new String[2];
		long address;
		long cycles = 0;
		int action;
		int busAction;
		int k;
		int hitFlag = 0; // other Cache has hit
		int myHitFlag = 0; // current cache has hit

		// While there is still a trace not finished
		while (done < coreCount) {
			if (processorArray[active] != null
					&& processorArray[active].getTrace().ready()) {
				// Read trace line of active processor
				s = processorArray[active].getTrace().readLine();
				// System.out.println(s);
				split = s.split(" ");
				address = Long.parseLong(split[1]);
				action = Integer.parseInt(split[0]);
				// Process processor action
				if (Integer.parseInt(split[0]) != 0) {
					busAction = processorArray[active].cache.nextState(address,
							action);
					myHitFlag = processorArray[active].getFlag();
					// Process snooping actions
					if (busAction != 0) {
						k = active;
						active = (active + 1) % coreCount;
						while (active != k) {
							processorArray[active].cache.nextState(address,
									busAction);
							hitFlag = processorArray[active].getFlag();
							active = (active + 1) % coreCount;
						}

					}
					//Increase cycle number depending on cache hits/misses
					if (myHitFlag == 0) {
						if (hitFlag == 0) {
							cycles = cycles + 10;
						} else{
							cycles = cycles + 1;
							hitFlag = 0;
						}
					}else{
						myHitFlag = 0;
					}
				}
				cycles++;
				active = (active + 1) % coreCount;
				
			} else {
				if (processorArray[active] == null)
					active = (active + 1) % coreCount;
				else {
					processorArray[active] = null;
					done++;
					System.out
							.println("Trace " + active + 1 + " has finished!");
					active = (active + 1) % coreCount;
				}

			}
		}
	}
}
