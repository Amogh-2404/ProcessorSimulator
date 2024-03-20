package processor.pipeline;

import generic.Instruction;
import processor.Processor;
import generic.Misc;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType EX_MA_Latch, MA_RW_LatchType MA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = EX_MA_Latch;
		this.MA_RW_Latch = MA_RW_Latch;
	}

	public void performMA() {
		if (EX_MA_Latch.isMA_enable()) {
			Instruction inst = EX_MA_Latch.getInstruction();
			MA_RW_Latch.setInstruction(inst);

			if (inst != null) {
				int aluResult = EX_MA_Latch.getAluResult();
				int operand = EX_MA_Latch.getOperand();
				int excess = EX_MA_Latch.getExcess();
				MA_RW_Latch.setExcess(excess);


				switch (inst.getOperationType()) {
					case load: {

						int ldResult = containingProcessor.getMainMemory().getWord(aluResult);
						MA_RW_Latch.setLdResult(ldResult);
						break;
					}

					case store: {
						containingProcessor.getMainMemory().setWord(aluResult, operand);
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
						// Passing aluResult to Register Writeback stage to store it register
						MA_RW_Latch.setAluResult(aluResult);
						break;
					}

					default:
						Misc.printErrorAndExit("Unknown Instruction!!");
				}
			}
			// TODO:- DEBUG
			else{
				MA_RW_Latch.setInstruction(null);
			}


			EX_MA_Latch.setMA_enable(false);
			MA_RW_Latch.setRW_enable(true);
		}
	}

}
