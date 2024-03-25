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
	int excess;
	Instruction instruction;
	Processor containingProcessor;

	public void performMA() {
		if (EX_MA_Latch.isMAstageEnabled()) {

			if (!EX_MA_Latch.isMAStageBusy()) {

				if (EX_MA_Latch.isValidInst()) {
					Instruction instruction = EX_MA_Latch.getInstruction();
					this.instruction = instruction;

					if (instruction != null) {
						int aluResult = EX_MA_Latch.getAluRes();
						this.excess = EX_MA_Latch.getExcess();
						int operand = EX_MA_Latch.getOperand();
						switch (instruction.getOperationType()) {
							case store: {
								EX_MA_Latch.setMAStageBusy(true);
								Simulator.getEventQueue()
										.addEvent(new MemoryWriteEvent(Clock.getCurrentTime(), this,
												containingProcessor.getMainMemory(), aluResult,
												operand));
								break;
							}
							case load: {
								EX_MA_Latch.setMAStageBusy(true);
								Simulator.getEventQueue()
										.addEvent(new MemoryReadEvent(Clock.getCurrentTime(), this,
												containingProcessor.getMainMemory(), aluResult));
								break;
							}

							case add:
							case sub:
							case mul:
							case div:
							case and:
							case or:
							case xor:
							case slt:
							case sll:
							case srl:
							case sra:
							case addi:
							case andi:
							case ori:
							case xori:
							case slti:
							case bne:
							case blt:
							case bgt:
							case slli:
							case srli:
							case srai:
							case beq:
							case subi:
							case muli:
							case divi:

							case jmp:
							case end: {

								MA_RW_Latch.setalures(aluResult);
								MA_RW_Latch.setIsRWstageEnabled(true);
								MA_RW_Latch.setValidInstruction(true);

								MA_RW_Latch.setInstruction(this.instruction);

								MA_RW_Latch.setExcess(this.excess);

								EX_MA_Latch.setMAstageEnabled(false);
								EX_MA_Latch.setValidInstruction(false);

								EX_MA_Latch.setMAStageBusy(false);
								break;
							}

							default:
								Misc.printErrorAndExit("Instruction type not supported for Memory Access Stage: "
										+ instruction.getOperationType());
						}
					} else {
						EX_MA_Latch.setValidInstruction(false);
						MA_RW_Latch.setInstruction(instruction);
						MA_RW_Latch.setValidInstruction(true);

					}
				}
			}
			EX_MA_Latch.setMAstageEnabled(false);
			MA_RW_Latch.setIsRWstageEnabled(true);
		}
	}

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType EX_MA_Latch,
						MA_RW_LatchType MA_RW_Latch) {
		this.MA_RW_Latch = MA_RW_Latch;
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = EX_MA_Latch;
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
		}



}
