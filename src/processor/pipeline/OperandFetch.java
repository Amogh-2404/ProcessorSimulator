package processor.pipeline;

import processor.Processor;


import generic.Simulator;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Misc;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;

	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch,
			OF_EX_LatchType oF_EX_Latch) {
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}

	public void performOF() {
		

		if (IF_OF_Latch.isOF_stage_enable()) {

			
			if (!OF_EX_Latch.isEXstageBusy() && !OF_EX_Latch.isEXstageBusyDueToMA()) {
				IF_OF_Latch.setOFStageBusy(false);

				if (IF_OF_Latch.getNopInstruction()) {
					OF_EX_Latch.setInstruction(null);
					
					OF_EX_Latch.setValidInstruction(true);
					IF_OF_Latch.setValidInstruction(false);
					Simulator.incNop(); 

				} else {
					if (IF_OF_Latch.isValidInstruction()) {
						

						
						
						containingProcessor.getDataInterlockUnit().checkConflict();

						if (IF_OF_Latch.getStall()) { 
							

							OF_EX_Latch.setInstruction(null); 
																
							OF_EX_Latch.setValidInstruction(true);
							Simulator.incNumDataHazards(); 
															

						} else {
							

							decode(); 

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

	
	
	
	
	
	private void decode() {
		
		String inst = padStart(Integer.toBinaryString(IF_OF_Latch.getInstruction()), 32);

		Instruction newIns = new Instruction(); 
		newIns.setProgramCounter(IF_OF_Latch.getCurrentPC()); 

		
		newIns.setOperationType(
				OperationType.values()[binaryToDecimal(inst.substring(0, 5), false)]);

		
		switch (newIns.getOperationType()) {
			
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
				
				newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
				
				newIns.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
				
				newIns.setDestinationOperand(getRegisterOperand(inst.substring(15, 20)));

				
				OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand1().getValue()));
				
				OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand2().getValue()));
				
				OF_EX_Latch.setIsImmediate(false);
				break;
			}

			
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
				if (binaryToDecimal(inst.substring(5, 10), false) != 0) { 
					
					newIns.setDestinationOperand(getRegisterOperand(inst.substring(5, 10)));
				} else { 
					
					newIns.setDestinationOperand(getImmediateOperand(inst.substring(10, 32)));
				}

				
				OF_EX_Latch.setBranchTarget(
						IF_OF_Latch.getCurrentPC() + newIns.getDestinationOperand().getValue());
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

	
	private String padStart(String str, int totalLength) {
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

	
	private int binaryToDecimal(String binaryString, boolean isSigned) {
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
		operand.setValue(binaryToDecimal(val, false)); 
		return operand;
	}

	
	
	private Operand getImmediateOperand(String val) {
		Operand operand = new Operand(); 
		operand.setOperandType(OperandType.Immediate); 
		operand.setValue(binaryToDecimal(val, true)); 
		return operand;
	}
}
