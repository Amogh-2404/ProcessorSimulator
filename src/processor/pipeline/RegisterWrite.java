package processor.pipeline;

import generic.Simulator;
import processor.Processor;

import generic.Instruction;
import generic.Misc;
import processor.interlocks.DataInterlock;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	public void performRW() {
		if (MA_RW_Latch.isRW_enable()) {
			if (MA_RW_Latch.getIsValidInstruction()){
				Instruction instructionInRW = MA_RW_Latch.getInstruction();

			if (instructionInRW != null) {

				int ldResult = MA_RW_Latch.getLdResult(); // Load result
				int aluResult = MA_RW_Latch.getAluResult(); // ALU result

				int excess = MA_RW_Latch.getExcess(); // excess bits
				containingProcessor.getRegisterFile().setValue(31, excess);

				int destinationRegister = 0; // Destination register where we need to store result
				if (instructionInRW.getDestinationOperand() != null) { // If it is not an end instruction
					destinationRegister = instructionInRW.getDestinationOperand().getValue();
				}

				switch (instructionInRW.getOperationType()) {

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
						// R2I type
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
					case srai: {
						// Storing ALU result at destination register
						containingProcessor.getRegisterFile().setValue(destinationRegister, aluResult);
						break;
					}

					case load: {
						// Storing Load register at destination register
						containingProcessor.getRegisterFile().setValue(destinationRegister, ldResult);
						break;
					}

					case store:
					case beq:
					case bne:
					case blt:
					case bgt:
						// RI type :
					case jmp:
						break;

					case end: {
						Simulator.setSimulationComplete(true);
						break;
					}

					default:
						Misc.printErrorAndExit("Unknown Instruction!!");
				}
			}
				Simulator.incNumInst();
				containingProcessor.getDataInterlockUnit().setFinalInstruction(instructionInRW);
			}

			MA_RW_Latch.setRW_enable(false);
			IF_EnableLatch.setIF_enable(true);

		}
	}

}
