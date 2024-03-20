package processor.pipeline;

import processor.Processor;

public class InstructionFetch {
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;

	public static boolean additionalNop = false;

	boolean firstInstruction = true;
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}

	public void performIF() {
		if (IF_EnableLatch.isIF_enable()) {

			if (!IF_EnableLatch.isStall()) {
				if (EX_IF_Latch.getIsBranchTaken() && additionalNop==false) {

					containingProcessor.getRegisterFile()
							.setProgramCounter(EX_IF_Latch.getBranchPC());

					EX_IF_Latch.setIsBranchTaken(false);

					int currentPC = containingProcessor.getRegisterFile().getProgramCounter();

					int newInstruction = containingProcessor.getMainMemory().getWord(currentPC);

					IF_OF_Latch.setInstruction(newInstruction);

					IF_OF_Latch.setNop(false);


					IF_OF_Latch.setCurrentPC(currentPC);


					containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);

				} else if (EX_IF_Latch.getIsBranchTaken() && additionalNop==true) {
					IF_OF_Latch.setNop(true);
				}
				else {
					int currentPC = containingProcessor.getRegisterFile().getProgramCounter();

					int newInstruction = containingProcessor.getMainMemory().getWord(currentPC);

					IF_OF_Latch.setInstruction(newInstruction);

					IF_OF_Latch.setNop(false);


					IF_OF_Latch.setCurrentPC(currentPC);


					containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);

				}



			}


			IF_OF_Latch.setOF_enable(true);
		}
	}

}
