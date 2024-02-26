package generic;

import processor.Clock;
import processor.Processor;

import java.io.*;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile)
	{
		/*
		 *
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		try{
			FileInputStream fileInputStream = new FileInputStream(assemblyProgramFile);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			try{
				try{
					int pc = -1, address = 0;
					while (dataInputStream.available()>0){
						int number = dataInputStream.readInt();
						if (pc==-1){
							pc = number;
							Simulator.processor.getRegisterFile().setProgramCounter(pc);
						}
						else{
							Simulator.processor.getMainMemory().setWord(address, number);
							++address;
						}
					}
				}
				catch (EOFException ignored){}
				dataInputStream.close();
			}
			catch (IOException e){
				Misc.printErrorAndExit(e.toString());
			}
		}
		catch (FileNotFoundException e){
			Misc.printErrorAndExit(e.toString());
		}

		Simulator.processor.getRegisterFile().setValue(0,0);
		Simulator.processor.getRegisterFile().setValue(1,65535);
		Simulator.processor.getRegisterFile().setValue(2,65535);

	}
	
	public static void simulate()
	{
		int numberOfInstructions = 0;
		while(!simulationComplete)
		{
			processor.getIFUnit().performIF();
			processor.getOFUnit().performOF();
			processor.getEXUnit().performEX();
			processor.getMAUnit().performMA();
			processor.getRWUnit().performRW();
			Clock.incrementClock();

			++numberOfInstructions;
		}
		

		// set statistics
		Statistics.setNumberOfCycles((int)Clock.getCurrentTime());
		Statistics.setNumberOfInstructions(numberOfInstructions);
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
}
