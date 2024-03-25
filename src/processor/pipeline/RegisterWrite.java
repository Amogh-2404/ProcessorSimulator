package processor.pipeline;

import generic.Simulator;
import processor.Processor;
import generic.Instruction.OperationType;

import generic.Instruction;
import generic.Misc;

public class RegisterWrite {
	Processor containingProcessor;

	OF_EX_LatchType OF_EX_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;



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

					OperationType operationType = instructionInRW.getOperationType();

					if (operationType == OperationType.add || operationType == OperationType.sub || operationType == OperationType.mul ||
							operationType == OperationType.div || operationType == OperationType.and || operationType == OperationType.or ||
							operationType == OperationType.xor || operationType == OperationType.slt || operationType == OperationType.sll ||
							operationType == OperationType.srl || operationType == OperationType.sra || operationType == OperationType.addi ||
							operationType == OperationType.subi || operationType == OperationType.muli || operationType == OperationType.divi ||
							operationType == OperationType.andi || operationType == OperationType.ori || operationType == OperationType.xori ||
							operationType == OperationType.slti || operationType == OperationType.slli || operationType == OperationType.srli ||
							operationType == OperationType.srai) {

						containingProcessor.getRegisterFile().setValue(rd, aluResult);

					} else if (operationType == OperationType.load) {

						containingProcessor.getRegisterFile().setValue(rd, ldResult);

					} else if (operationType == OperationType.store || operationType == OperationType.beq ||
							operationType == OperationType.bne || operationType == OperationType.blt ||
							operationType == OperationType.bgt || operationType == OperationType.jmp) {
						// No operation for these cases in the original switch.

					} else if (operationType == OperationType.end) {

						Simulator.setSimulationComplete(true);

					} else {
						Misc.printErrorAndExit("Instruction type not supported for RW stage: " + instructionInRW.getOperationType());
					}

				}
				containingProcessor.getDataInterlockUnit().setFinalInstruction(instructionInRW);
			}

			MA_RW_Latch.setIsRWstageEnabled(false);
			IF_EnableLatch.setIF_Stage_enable(true);
//			Simulator.incNumInst();

		}
	}
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType MA_RW_Latch,
						 IF_EnableLatchType IF_EnableLatch, OF_EX_LatchType OF_EX_Latch) {
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = MA_RW_Latch;
		this.IF_EnableLatch = IF_EnableLatch;
		this.OF_EX_Latch = OF_EX_Latch;
	}

}
