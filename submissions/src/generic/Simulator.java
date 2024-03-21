package generic;

import processor.Clock;
import processor.Processor;

import java.io.*;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;

	static int numInst; // Number of instructions executed
	static int numDataHazards; // Number of times the OF stage needed to stall because of a data
								// hazard
	static int numNop; // Number of times an instruction on a wrong branch path entered the pipeline

	static EventQueue eventQueue = new EventQueue();
	
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		
		simulationComplete = false;

		numInst = numDataHazards = numNop = 0; // Initializing them to all 0's
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
//		System.out.println(processor.getMainMemory().getContentsAsString(65530, 65535));
		while(simulationComplete==false)
		{
			processor.getRWUnit().performRW();
			processor.getMAUnit().performMA();
			processor.getEXUnit().performEX();
			eventQueue.processEvents();
			processor.getOFUnit().performOF();
			processor.getIFUnit().performIF();
			Clock.incrementClock();

		}
		

		// set statistics
		Statistics.setNumberOfCycles((int)Clock.getCurrentTime());
		Statistics.setNumberOfInstructions(numInst);
		Statistics.setNumberOfDataHazards(numDataHazards);
		Statistics.setNumberOfNop(numNop);

	}

	public static EventQueue getEventQueue()
	{
		return eventQueue;
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}

	public static void incNumInst(){
		++numInst;
	}

	public static void incNumDataHazards(){
		++numDataHazards;
	}

	public static void incrementNop(){
		++numNop;
	}
}
