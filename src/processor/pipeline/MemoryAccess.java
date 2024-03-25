package processor.pipeline;

import processor.Clock;
import processor.Processor;
import generic.Element;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Misc;
import generic.Simulator;
import generic.Event;
import generic.Instruction;
import generic.MemoryReadEvent;


public class MemoryAccess implements Element {
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	Integer excess = 0;
	Instruction instruction;
	Processor containingProcessor;

	IF_EnableLatchType IF_EnableLatch;

	boolean isBlocked = false;

	public void performMA() {
		if (EX_MA_Latch.isMAstageEnabled()) {

			if (!EX_MA_Latch.isMAStageBusy()) {

				if (EX_MA_Latch.isValidInst()) {
					Instruction instruction = EX_MA_Latch.getInstruction();
					this.instruction = instruction;

					if (instruction != null) {
						int aluResult = EX_MA_Latch.getalures();
						this.excess = EX_MA_Latch.getExcess();
						int operand = EX_MA_Latch.getOperand();
						Instruction.OperationType operationType = instruction.getOperationType();
						if (operationType == Instruction.OperationType.store) {
							EX_MA_Latch.setMAStageBusy(true);
							Simulator.getEventQueue()
									.addEvent(new MemoryWriteEvent(Clock.getCurrentTime(), this,
											containingProcessor.getMainMemory(), aluResult,
											operand));
						} else if (operationType == Instruction.OperationType.load) {
							EX_MA_Latch.setMAStageBusy(true);
							Simulator.getEventQueue()
									.addEvent(new MemoryReadEvent(Clock.getCurrentTime(), this,
											containingProcessor.getMainMemory(), aluResult));
						} else if (operationType == Instruction.OperationType.add || operationType == Instruction.OperationType.sub ||
								operationType == Instruction.OperationType.mul || operationType == Instruction.OperationType.div ||
								operationType == Instruction.OperationType.and || operationType == Instruction.OperationType.or ||
								operationType == Instruction.OperationType.xor || operationType == Instruction.OperationType.slt ||
								operationType == Instruction.OperationType.sll || operationType == Instruction.OperationType.srl ||
								operationType == Instruction.OperationType.sra || operationType == Instruction.OperationType.addi ||
								operationType == Instruction.OperationType.andi || operationType == Instruction.OperationType.ori ||
								operationType == Instruction.OperationType.xori || operationType == Instruction.OperationType.slti ||
								operationType == Instruction.OperationType.bne || operationType == Instruction.OperationType.blt ||
								operationType == Instruction.OperationType.bgt || operationType == Instruction.OperationType.slli ||
								operationType == Instruction.OperationType.srli || operationType == Instruction.OperationType.srai ||
								operationType == Instruction.OperationType.beq || operationType == Instruction.OperationType.subi ||
								operationType == Instruction.OperationType.muli || operationType == Instruction.OperationType.divi ||
								operationType == Instruction.OperationType.jmp || operationType == Instruction.OperationType.end) {
							MA_RW_Latch.setalures(aluResult);
							MA_RW_Latch.setIsRWstageEnabled(true);
							MA_RW_Latch.setValidInstruction(true);
							MA_RW_Latch.setInstruction(this.instruction);
							MA_RW_Latch.setExcess(this.excess);
							EX_MA_Latch.setMAstageEnabled(false);
							EX_MA_Latch.setValidInstruction(false);
							EX_MA_Latch.setMAStageBusy(false);
						} else {
							Misc.printErrorAndExit("Instruction type not supported for Memory Access Stage: " + operationType);
						}
					} else {
						EX_MA_Latch.setValidInstruction(false);
						MA_RW_Latch.setInstruction(instruction);
						MA_RW_Latch.setValidInstruction(true);

						boolean b = IF_EnableLatch.getAdditionalNop();
						isBlocked = b;
					}
				}
			}
			EX_MA_Latch.setMAstageEnabled(false);
			MA_RW_Latch.setIsRWstageEnabled(true);
		}
	}

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType EX_MA_Latch,
						MA_RW_LatchType MA_RW_Latch,IF_EnableLatchType IF_EnableLatch) {
		this.MA_RW_Latch = MA_RW_Latch;
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = EX_MA_Latch;
		this.IF_EnableLatch = IF_EnableLatch;
	}


	@Override
	public void handleEvent(Event e) {
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			MA_RW_Latch.setValidInstruction(true);
			MA_RW_Latch.setExcess(this.excess);
			MA_RW_Latch.setInstruction(this.instruction);
			EX_MA_Latch.setValidInstruction(false);
			MA_RW_Latch.setldres(event.getValue());
			EX_MA_Latch.setMAStageBusy(false);
			IF_EnableLatch.setAdditionalNop(false);
		}



}
