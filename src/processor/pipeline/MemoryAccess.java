package processor.pipeline;

import generic.*;
import processor.Clock;
import processor.Processor;

public class MemoryAccess implements Element {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;

	Instruction instruction;
	int excess;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType EX_MA_Latch, MA_RW_LatchType MA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = EX_MA_Latch;
		this.MA_RW_Latch = MA_RW_Latch;
	}

	public void performMA() {
		if (EX_MA_Latch.isMA_enable()) {
			Instruction inst = EX_MA_Latch.getInstruction();
//			MA_RW_Latch.setInstruction(inst);
			this.instruction = inst;

			if (!EX_MA_Latch.getIsMA_busy()) {

				if (inst != null) {
					int aluResult = EX_MA_Latch.getAluResult();
					int operand = EX_MA_Latch.getOperand();
					this.excess = EX_MA_Latch.getExcess();
//				MA_RW_Latch.setExcess(excess);


					switch (inst.getOperationType()) {
						case load: {
							Simulator.getEventQueue()
									.addEvent(new MemoryReadEvent(Clock.getCurrentTime(), this,
											containingProcessor.getMainMemory(), aluResult));
							EX_MA_Latch.setIsMA_busy(true);
							break;
						}

						case store: {
							Simulator.getEventQueue()
									.addEvent(new MemoryWriteEvent(Clock.getCurrentTime(), this,
											containingProcessor.getMainMemory(), aluResult,
											operand));
							EX_MA_Latch.setIsMA_busy(true);
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
						case subi:
						case muli:
						case divi:
						case andi:
						case ori:
						case xori:
						case slti:
						case slli:
						case srli:
						case srai:
						case beq:
						case bne:
						case blt:
						case bgt:
						case jmp:
						case end: {
							EX_MA_Latch.setIsMA_busy(false);
							EX_MA_Latch.setMA_enable(false);
							EX_MA_Latch.setIsValidInstruction(false);
							MA_RW_Latch.setInstruction(this.instruction);
							MA_RW_Latch.setExcess(this.excess);
							MA_RW_Latch.setAluResult(aluResult);
							MA_RW_Latch.setRW_enable(true);
							MA_RW_Latch.setIsValidInstruction(true);

							break;
						}

						default:
							Misc.printErrorAndExit("Unknown Instruction!!");
					}
				}
				// TODO:- DEBUG
				else {
					MA_RW_Latch.setInstruction(null);
					MA_RW_Latch.setIsValidInstruction(true);
					EX_MA_Latch.setIsValidInstruction(false);
				}


				EX_MA_Latch.setMA_enable(false);
				MA_RW_Latch.setRW_enable(true);
			}
			else{
				return;
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		MemoryResponseEvent e = (MemoryResponseEvent) event;

		EX_MA_Latch.setIsMA_busy(false);
		EX_MA_Latch.setIsValidInstruction(false);

		MA_RW_Latch.setInstruction(this.instruction);
		MA_RW_Latch.setExcess(this.excess);
		MA_RW_Latch.setLdResult(e.getValue());
		MA_RW_Latch.setIsValidInstruction(true);

	}
}
