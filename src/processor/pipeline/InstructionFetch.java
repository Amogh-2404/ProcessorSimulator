package processor.pipeline;

import generic.*;
import processor.Clock;
import processor.Processor;

public class InstructionFetch implements Element {
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;

	public static boolean additionalNop = false;

	boolean firstInstruction = true;

	int previousPC;

	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		previousPC = 0;
	}

	public void performIF() {
		if (IF_EnableLatch.isIF_enable()) {

			if(!IF_EnableLatch.getisIFBusy()) {

				if (!IF_EnableLatch.isStall()) {
					if (EX_IF_Latch.getIsBranchTaken()) {

						containingProcessor.getRegisterFile()
								.setProgramCounter(EX_IF_Latch.getBranchPC());

						EX_IF_Latch.setIsBranchTaken(false);
					}

						int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
						Simulator.getEventQueue().addEvent(new MemoryReadEvent(Clock.getCurrentTime(), this, containingProcessor.getMainMemory(), currentPC));

						previousPC = currentPC;

						containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);

						IF_EnableLatch.setIF_Busy(true);
				}

			}
			IF_OF_Latch.setOF_enable(true);
		}
	}

	@Override
	public void handleEvent(Event event) {
		if (IF_OF_Latch.getOF_busy()){
			// Postpone the event if the next stage(OF) is busy
			event.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(event);
		}
		// TODO:- Potential spot to add the condition if the instruction in the next latch is a nop
		else if (IF_OF_Latch.getIsNop()) {
			containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getBranchPC());

			IF_EnableLatch.setIF_Busy(false);

			IF_OF_Latch.setIsValidInstruction(false);
			IF_OF_Latch.setNop(false);
			IF_OF_Latch.setOF_enable(true);

			EX_IF_Latch.setIsBranchTaken(false);
		}
		else{
			MemoryResponseEvent e = (MemoryResponseEvent) event;
			IF_OF_Latch.setInstruction(e.getValue());
			IF_OF_Latch.setOF_enable(true);
			IF_EnableLatch.setIF_Busy(false);
			IF_OF_Latch.setIsValidInstruction(true);
			IF_OF_Latch.setNop(false);
			IF_OF_Latch.setCurrentPC(previousPC);
		}

	}
}
