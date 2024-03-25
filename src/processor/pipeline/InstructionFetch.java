package processor.pipeline;

import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.Simulator;
import processor.Clock;
import processor.Processor;


public class InstructionFetch implements Element {
	// Declarations
	EX_IF_LatchType EX_IF_Latch;
	IF_EnableLatchType IF_EnableLatch;
	int previousProgramCounter;
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;

	MA_RW_LatchType MA_RW_Latch;

	OF_EX_LatchType OF_EX_Latch;

	boolean additionalNop = false; // To check if additional nop is required in case of control hazards

	int additionalNopCounter = 0;

	public void performIF() {
		
if (!additionalNop) {
	if (IF_EnableLatch.isIF_Stage_enable()) {

		if (!IF_EnableLatch.isIFstageBusy()) {

			if (!IF_EnableLatch.getIsIFStageStall()) {
				if (EX_IF_Latch.getIsBranchTaken()) {
					containingProcessor.getRegisterFile()
							.setProgramCounter(EX_IF_Latch.getBranchPC());
					EX_IF_Latch.setIsBranchTaken(false);
				}

				int currentPC = containingProcessor.getRegisterFile().getProgramCounter();

				IF_EnableLatch.setIFstageBusy(true);
				Simulator.getEventQueue().addEvent(new MemoryReadEvent(Clock.getCurrentTime(),
						this, containingProcessor.getMainMemory(), currentPC));

				previousProgramCounter = currentPC;

				containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);

//					Simulator.incNumInst();
			}
			Simulator.incNumInst();
		}

		IF_OF_Latch.setOF_stage_enable(true);
	}
}
additionalNopCounter = 0;  //Reduntant ?
	}
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType IF_EnableLatch,
							IF_OF_LatchType IF_OF_Latch, EX_IF_LatchType EX_IF_Latch) {
		previousProgramCounter = 0;
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = IF_OF_Latch;
		this.EX_IF_Latch = EX_IF_Latch;
		this.IF_EnableLatch = IF_EnableLatch;

	}
	
	@Override
	public void handleEvent(Event e) {

		if (IF_OF_Latch.isOFStageBusy()) {
			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
//			Simulator.incNumInst();
		} else if (IF_OF_Latch.getNopInstruction()) {
			
			containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getBranchPC());
			IF_OF_Latch.setNopInstruction(false);
			IF_OF_Latch.setOF_stage_enable(true);
			IF_EnableLatch.setIFstageBusy(false);
			EX_IF_Latch.setIsBranchTaken(false);
			IF_OF_Latch.setValidInstruction(false);
//			Simulator.incNumInst();
		}
		else if(additionalNop){
			additionalNopCounter++;
		}
		else {
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			IF_EnableLatch.setIFstageBusy(false);
			IF_OF_Latch.setNopInstruction(false);
			IF_OF_Latch.setCurrentPC(previousProgramCounter);
			IF_OF_Latch.setOF_stage_enable(true);
			IF_OF_Latch.setValidInstruction(true);
			IF_OF_Latch.setInstruction(event.getValue());

//			Simulator.incNumInst();
		}
	}

}
