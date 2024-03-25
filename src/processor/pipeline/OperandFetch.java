package processor.pipeline;

import processor.Processor;


import generic.Simulator;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Misc;

public class OperandFetch {
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	Processor containingProcessor;

	IF_EnableLatchType IF_EnableLatch;

	EX_IF_LatchType EX_IF_Latch;

	MA_RW_LatchType MA_RW_Latch;

	private int convertBinaryToDecimal(String binaryString, boolean isSigned) {
		if (!isSigned) {
			return Integer.parseInt(binaryString, 2);
		} else {
			String copyString = '0' + binaryString.substring(1);
			int answer = Integer.parseInt(copyString, 2);

			if (binaryString.length() == 32) {
				if (binaryString.charAt(0) == '1') {
					int power = (1 << 30);

					answer -= power;
					answer -= power;
				}
			} else {
				if (binaryString.charAt(0) == '1') {

					int power = (1 << (binaryString.length() - 1));
					answer -= power;
				}
			}
			return answer;
		}
	}

	private String addPaddingToStart(String str, int totalLength) {
		if (str.length() >= totalLength) {
			return str;
		}
		String answer = "";
		int count = 1;
		while (count < (totalLength - str.length())+1) {
			answer += '0';
			++count;
		}
		answer += str;
		return answer;
	}


	public void performOF() {

		if (IF_OF_Latch.isOF_stage_enable()) {
			
			if (!OF_EX_Latch.isEXstageBusy() && !OF_EX_Latch.isEXstageBusyDueToMA()) {
				IF_OF_Latch.setOFStageBusy(false);

				if (IF_OF_Latch.getNopInstruction()) {
					OF_EX_Latch.setInstruction(null);
					
					OF_EX_Latch.setValidInstruction(true);
					IF_OF_Latch.setValidInstruction(false);

					IF_EnableLatch.additionalNop = false;
					Simulator.incNop(); 

				} else {
					if (IF_OF_Latch.isValidInstruction()) {

						containingProcessor.getDataInterlockUnit().checkConflict();

						if (IF_OF_Latch.getStall()) { 
							

							OF_EX_Latch.setInstruction(null); 
																
							OF_EX_Latch.setValidInstruction(true);
							Simulator.incNumDataHazards(); 
															

						} else {
							

							decodeTheInstruction();

							OF_EX_Latch.setValidInstruction(true);
							IF_OF_Latch.setValidInstruction(false);
						}
					} else {
						
					}
				}
			} else {
				

				IF_OF_Latch.setOFStageBusy(true);
			}

			IF_OF_Latch.setOF_stage_enable(false);
			OF_EX_Latch.setEXstageEnabled(true);
		} else {
			
		}
	}

	private Operand getRegisterOperand(String val) {
		Operand operand = new Operand();
		operand.setOperandType(OperandType.Register);
		operand.setValue(convertBinaryToDecimal(val, false));
		return operand;
	}
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType IF_OF_Latch,
						OF_EX_LatchType OF_EX_Latch,IF_EnableLatchType IF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = IF_OF_Latch;
		this.OF_EX_Latch = OF_EX_Latch;
		this.IF_EnableLatch = IF_EnableLatch;
	}
	
	
	private void decodeTheInstruction() {
		
		String instruction = addPaddingToStart(Integer.toBinaryString(IF_OF_Latch.getInstruction()), 32);

		Instruction currentInstruction = new Instruction();
		currentInstruction.setProgramCounter(IF_OF_Latch.getCurrentPC());

		
		currentInstruction.setOperationType(
				OperationType.values()[convertBinaryToDecimal(instruction.substring(0, 5), false)]);

		OperationType operationType = currentInstruction.getOperationType();

		if (operationType == OperationType.add || operationType == OperationType.sub ||
				operationType == OperationType.div || operationType == OperationType.xor ||
				operationType == OperationType.slt || operationType == OperationType.mul ||
				operationType == OperationType.sll || operationType == OperationType.srl ||
				operationType == OperationType.and || operationType == OperationType.or ||
				operationType == OperationType.sra) {

			OF_EX_Latch.setIsImmediate(false);
			currentInstruction.setSourceOperand1(getRegisterOperand(instruction.substring(5, 10)));
			OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile().getValue(currentInstruction.getSourceOperand1().getValue()));
			currentInstruction.setSourceOperand2(getRegisterOperand(instruction.substring(10, 15)));
			OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile().getValue(currentInstruction.getSourceOperand2().getValue()));
			currentInstruction.setDestinationOperand(getRegisterOperand(instruction.substring(15, 20)));

		} else if (operationType == OperationType.addi || operationType == OperationType.subi ||
				operationType == OperationType.muli || operationType == OperationType.xori ||
				operationType == OperationType.slti || operationType == OperationType.slli ||
				operationType == OperationType.srli || operationType == OperationType.srai ||
				operationType == OperationType.divi || operationType == OperationType.andi ||
				operationType == OperationType.ori || operationType == OperationType.load ||
				operationType == OperationType.store) {

			OF_EX_Latch.setIsImmediate(true);
			currentInstruction.setSourceOperand2(getImmediateOperand(instruction.substring(15, 32)));
			OF_EX_Latch.setImmediate(currentInstruction.getSourceOperand2().getValue());
			currentInstruction.setSourceOperand1(getRegisterOperand(instruction.substring(5, 10)));
			currentInstruction.setDestinationOperand(getRegisterOperand(instruction.substring(10, 15)));
			OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile().getValue(currentInstruction.getDestinationOperand().getValue()));
			OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile().getValue(currentInstruction.getSourceOperand1().getValue()));

		} else if (operationType == OperationType.jmp) {
			if (convertBinaryToDecimal(instruction.substring(5, 10), false) != 0) {
				currentInstruction.setDestinationOperand(getRegisterOperand(instruction.substring(5, 10)));
			} else {
				currentInstruction.setDestinationOperand(getImmediateOperand(instruction.substring(10, 32)));
			}
			OF_EX_Latch.setBranchTarget(IF_OF_Latch.getCurrentPC() + currentInstruction.getDestinationOperand().getValue());

		} else if (operationType == OperationType.beq || operationType == OperationType.bne ||
				operationType == OperationType.blt || operationType == OperationType.bgt) {

			currentInstruction.setSourceOperand1(getRegisterOperand(instruction.substring(5, 10)));
			currentInstruction.setSourceOperand2(getRegisterOperand(instruction.substring(10, 15)));
			OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile().getValue(currentInstruction.getSourceOperand2().getValue()));
			currentInstruction.setDestinationOperand(getImmediateOperand(instruction.substring(15, 32)));
			OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile().getValue(currentInstruction.getSourceOperand1().getValue()));
			OF_EX_Latch.setBranchTarget(IF_OF_Latch.getCurrentPC() + currentInstruction.getDestinationOperand().getValue());

		} else if (operationType == OperationType.end) {
			containingProcessor.getRegisterFile().setProgramCounter(IF_OF_Latch.getCurrentPC());

		} else {
			Misc.printErrorAndExit("Instruction not supported in Operand Fetch Stage: " + currentInstruction.getOperationType());
		}




		OF_EX_Latch.setInstruction(currentInstruction);
	}

	

	private Operand getImmediateOperand(String val) {
		Operand operand = new Operand();
		operand.setOperandType(OperandType.Immediate);
		operand.setValue(convertBinaryToDecimal(val, true));
		return operand;
	}


}
