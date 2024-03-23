package processor.pipeline;

import generic.Instruction;
import generic.Misc;
import generic.Operand;
import generic.Simulator;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;


	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}
	
	public void performOF()
	{
		if (IF_OF_Latch.isOF_enable()) {

			if (!OF_EX_Latch.isEX_busy){ // TODO:- Check if one more condition is needed or not
				IF_OF_Latch.setOF_busy(false);

				if (IF_OF_Latch.getIsNop()) {
					OF_EX_Latch.setInstruction(null);
					OF_EX_Latch.setIsValidInstruction(true);
					IF_OF_Latch.setIsValidInstruction(false);
					Simulator.incrementNop();
					InstructionFetch.additionalNop = !InstructionFetch.additionalNop;
				} else {
				if (IF_OF_Latch.getIsValidInstruction()) {
					containingProcessor.getDataInterlockUnit().checkConflict();

					if (IF_OF_Latch.getStall()) {
						OF_EX_Latch.setInstruction(null);
						Simulator.incNumDataHazards();

					} else {
						decodeTheInstruction();
						IF_OF_Latch.setOF_enable(false);
						OF_EX_Latch.setEX_enable(true);

					}
				}
				}


		}
		else{
			IF_OF_Latch.setOF_busy(true);
			}

			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);

		}
	}

	private int convertBinaryToDecimal(String binaryString, boolean isSigned) {
		if (!isSigned) {
			return Integer.parseInt(binaryString, 2);
		} else {
			String copyString = '0' + binaryString.substring(1);
			int ans = Integer.parseInt(copyString, 2);
			if (binaryString.length() == 32) {
				if (binaryString.charAt(0) == '1') {

					int power = (1 << 30);
					ans -= power;
					ans -= power;
				}
			} else {
				if (binaryString.charAt(0) == '1') {

					int power = (1 << (binaryString.length() - 1));
					ans -= power;
				}
			}

			return ans;
		}
	}

	private Operand getRegisterOperand(String val) {
		Operand operand = new Operand();
		operand.setOperandType(OperandType.Register);
		operand.setValue(convertBinaryToDecimal(val, false));
		return operand;
	}

	private void decodeTheInstruction(){

		String inst = addPaddingToStart(Integer.toBinaryString(IF_OF_Latch.getInstruction()), 32);

		Instruction newIns = new Instruction();
		newIns.setProgramCounter(IF_OF_Latch.getCurrentPC());


		newIns.setOperationType(
				OperationType.values()[convertBinaryToDecimal(inst.substring(0, 5), false)]);

		switch (newIns.getOperationType()) {
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
			case load:
			case store: {

				newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));

				newIns.setDestinationOperand(getRegisterOperand(inst.substring(10, 15)));

				newIns.setSourceOperand2(getImmediateOperand(inst.substring(15, 32)));


				OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand1().getValue()));

				OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile()
						.getValue(newIns.getDestinationOperand().getValue()));

				OF_EX_Latch.setImmediate(newIns.getSourceOperand2().getValue());

				OF_EX_Latch.setIsImmediate(true);
				break;
			}


			case beq:
			case bne:
			case blt:
			case bgt: {

				newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));

				newIns.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));

				newIns.setDestinationOperand(getImmediateOperand(inst.substring(15, 32)));


				OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand1().getValue()));

				OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand2().getValue()));

				OF_EX_Latch.setBranchTarget(
						IF_OF_Latch.getCurrentPC() + newIns.getDestinationOperand().getValue());
				break;
			}


			case jmp: {
				if (convertBinaryToDecimal(inst.substring(5, 10), false) != 0) { // if rd is used

					newIns.setDestinationOperand(getRegisterOperand(inst.substring(5, 10)));
				} else {

					newIns.setDestinationOperand(getImmediateOperand(inst.substring(10, 32)));
				}


				OF_EX_Latch.setBranchTarget(
						IF_OF_Latch.getCurrentPC() + newIns.getDestinationOperand().getValue());
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
			case sra: {
				// rs1
				newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
				// rs2
				newIns.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
				// rd
				newIns.setDestinationOperand(getRegisterOperand(inst.substring(15, 20)));

				// operand1
				OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand1().getValue()));
				// operand2
				OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand2().getValue()));
				// isImmediate control signal
				OF_EX_Latch.setIsImmediate(false);
				break;
			}

			case end:
				containingProcessor.getRegisterFile().setProgramCounter(IF_OF_Latch.getCurrentPC());
				break;

			default:
				Misc.printErrorAndExit("Unknown Instruction!!");
		}


		OF_EX_Latch.setInstruction(newIns);
	}


	private String addPaddingToStart(String str, int totalLength) {
		if (str.length() >= totalLength) {
			return str;
		}
		int count = 0;
		String ans = "";
		while (count < totalLength - str.length()) {
			ans += '0';
			++count;
		}
		ans += str;
		return ans;
	}


	private Operand getImmediateOperand(String val) {
		Operand operand = new Operand(); // Making a new operand
		operand.setOperandType(OperandType.Immediate); // setting operand type as Register
		operand.setValue(convertBinaryToDecimal(val, true)); // setting its value
		return operand;
	}
}




