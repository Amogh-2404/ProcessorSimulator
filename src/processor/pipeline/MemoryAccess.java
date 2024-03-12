package processor.pipeline;

import generic.Instruction;
import processor.Processor;
import generic.Misc;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	
	public void performMA()
	{
		if(EX_MA_Latch.isMA_enable())
		{
			// Fetching the instruction from EX_MA_Latch
			Instruction inst = EX_MA_Latch.getInstruction();
			// Passing the instruction forward in pipeline
			MA_RW_Latch.setInstruction(inst);
			// Fetching the aluResult from EX_MA_Latch
			int aluResult = EX_MA_Latch.getAluResult();
			// Fetching the operand from EX_MA_Latch
			int operand = EX_MA_Latch.getOperand();
			// Fetching the excess from EX_MA_Latch
			int excess = EX_MA_Latch.getExcess();
			// Passing the excess forward in pipeline
			MA_RW_Latch.setExcess(excess);

			switch(inst.getOperationType())
			{
				case load:
				{
					// Alu Result will be address from which we have to load in case of load instruction
					// Fetching the ldResult from Main Memory
					int ldResult = containingProcessor.getMainMemory().getWord(aluResult);
					// Passing the ldResult to store it to register Writeback stage
					MA_RW_Latch.setLdResult(ldResult);
					break;
				}

				case store:
				{
					// Alu Result will be address where we need to store in case of store instruction
					// Setting operand value at that address
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
				case end:
				{
					MA_RW_Latch.setAluResult(aluResult);
					break;
				}
				default:
				{
					Misc.printErrorAndExit("Unknown operation type in Memory Access Stage");
				}
			}

			// Disabling the MA stage
			EX_MA_Latch.setMA_enable(false);
			// Enabling the RW stage
			MA_RW_Latch.setRW_enable(true);
		}
	}

}
