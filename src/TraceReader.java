import java.io.IOException;

public class TraceReader {

	public int hitFlag = 0; // other Cache has hit
	public int myHitFlag = 0; // current cache has hit
	public int busAction;

	public static void main(String[] args) throws IOException {
		int coreCount = Integer.parseInt(args[2]); // number of cores
		int done = 0; // number of traces that have finished
		Processor[] processorArray = new Processor[coreCount];
		// Initialize cores
		String coreString;
		switch(coreCount){
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
		for (int i = 0; i < coreCount; i++) {
			processorArray[i] = new Processor(coreString + args[1] + (i + 1) + ".prg", i);
		}
		int active = 0;
		String s;
		while (done < coreCount) {
			if (processorArray[active] != null
					&& processorArray[active].getTrace().ready()) {
				s = processorArray[active].getTrace().readLine();
				System.out.println(s);
				for (int i = 0; i < coreCount - done - 1; i++) {
					
				}

				active = (active + 1) % coreCount;
			} else {
				if (processorArray[active] == null)
					active = (active + 1) % coreCount;
				else {
					processorArray[active] = null;
					done++;
					System.out.println("Trace " + active+1 + " has finished!");
					active = (active + 1) % coreCount;
				}

			}

		}

	}
}
