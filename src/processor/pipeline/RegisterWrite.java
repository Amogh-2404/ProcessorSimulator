package processor.pipeline;

import generic.Simulator;
import processor.Processor;


import generic.Instruction;
import generic.Misc;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;

	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch,
			IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	public void performRW() {
		

		if (MA_RW_Latch.getIsRWstageEnabled()) {
			if (MA_RW_Latch.isValidInstruction()) {
				Instruction instructionInRW = MA_RW_Latch.getInstruction();

				

				MA_RW_Latch.setValidInstruction(false);

				if (instructionInRW != null) {


					int ldResult = MA_RW_Latch.getldres();
					int aluResult = MA_RW_Latch.getalures();

					int excess = MA_RW_Latch.getExcess(); 
					containingProcessor.getRegisterFile().setValue(31, excess); 
																				
																				

					int rd = 0; 
					if (instructionInRW.getDestinationOperand() != null) {
						rd = instructionInRW.getDestinationOperand().getValue();
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
							
							containingProcessor.getRegisterFile().setValue(rd, aluResult);
							break;
						}

						case load: {
							
							containingProcessor.getRegisterFile().setValue(rd, ldResult);
							break;
						}

						case store:
						case beq:
						case bne:
						case blt:
						case bgt:
							
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
				containingProcessor.getDataInterlockUnit().setFinalInstruction(instructionInRW);
			}

			MA_RW_Latch.setIsRWstageEnabled(false);
			IF_EnableLatch.setIF_Stage_enable(true);
//			Simulator.incNumInst();

		}
	}

}
