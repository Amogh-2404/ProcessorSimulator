package processor.pipeline;

import generic.Instruction;
import generic.Misc;
import processor.Processor;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performEX()
	{
		if (OF_EX_Latch.isEX_enable()) {
			Instruction inst = OF_EX_Latch.getInstruction();
			EX_MA_Latch.setInstruction(inst);

			if (inst != null) {
				compute(inst);

				containingProcessor.getControlInterlockUnit().validate();
			}
			// TODO:- DEBUG
			else{
				EX_MA_Latch.setInstruction(null);
			}

			OF_EX_Latch.setEX_enable(false);
			EX_MA_Latch.setMA_enable(true);
		}
	}

	private int convertDecimalToBinary(String binaryString, boolean isSigned) {
		if (!isSigned) {
			return Integer.parseInt(binaryString, 2);

		} else {
			String copyString = '0' + binaryString.substring(1);
			int answer = Integer.parseInt(copyString, 2);
			// bits

			if (binaryString.length() == 32) {
				int power = (1 << 30);
				if (binaryString.charAt(0) == '1') {
					answer -= power;
					answer -= power;
				}
			} else {
				int power = (1 << (binaryString.length() - 1));
				if (binaryString.charAt(0) == '1') {
					// number
					answer -= power;
				}
			}

			return answer;
		}
	}

	private int getResult(long res) {
		String binaryString = Long.toBinaryString(res);
		if (binaryString.length() <= 32) {
			return (int) res;

		} else {
			EX_MA_Latch.setExcess(convertDecimalToBinary( // Setting excess
					binaryString.substring(0, binaryString.length() - 32), (res < 0)));

			return convertDecimalToBinary(binaryString.substring(binaryString.length() - 32), (res < 0));
		}
	}

	private void compute(Instruction inst){

		long op1 = OF_EX_Latch.getOperand1();
		long op2 = OF_EX_Latch.getOperand2();
		long imm = OF_EX_Latch.getImmediate();
		long second = (OF_EX_Latch.getIsImmediate()) ? imm : op2;


		switch(inst.getOperationType()){
			case add:
			case addi:{
				EX_MA_Latch.setAluResult(getResult(op1+second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case mul:
			case muli:{
				EX_MA_Latch.setAluResult(getResult(op1*second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;}

			case div:
			case divi:{
				EX_MA_Latch.setAluResult(getResult(op1/second));
				EX_MA_Latch.setExcess((int)(op1%second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case and:
			case andi:{
				EX_MA_Latch.setAluResult(getResult(op1 & second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case sub:
			case subi:{
				EX_MA_Latch.setAluResult(getResult(op1-second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}



			case or:
			case ori:{
				EX_MA_Latch.setAluResult(getResult(op1 | second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case xor:
			case xori:{
				EX_MA_Latch.setAluResult(getResult(op1 ^ second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}



			case sra:
			case srai:{
				EX_MA_Latch.setAluResult(getResult(op1 >> second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case load:{
				EX_MA_Latch.setAluResult(getResult(op1 + imm));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case slt:
			case slti:{
				EX_MA_Latch.setAluResult((op1 < second) ? 1 : 0);
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case sll:
			case slli:{
				EX_MA_Latch.setAluResult(getResult(op1 << second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case srl:
			case srli:{
				EX_MA_Latch.setAluResult(getResult(op1 >>> second));
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case store:{
				EX_MA_Latch.setAluResult(getResult(op2 + imm));
				EX_MA_Latch.setOperand((int)op1);
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			case beq:{
				if (op1 == op2){
					EX_IF_Latch.setIsBranchTaken(true);
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				}else{
					EX_IF_Latch.setIsBranchTaken(false);
				}
				break;
			}

			case bne:{
				if (op1 != op2){
					EX_IF_Latch.setIsBranchTaken(true);
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				}else{
					EX_IF_Latch.setIsBranchTaken(false);
				}
				break;
			}



			case jmp:{
				EX_IF_Latch.setIsBranchTaken(true);
				EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				break;
			}

			case blt:{
				if (op1 < op2){
					EX_IF_Latch.setIsBranchTaken(true);
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				}else{
					EX_IF_Latch.setIsBranchTaken(false);
				}
				break;
			}

			case bgt:{
				if (op1 > op2){
					EX_IF_Latch.setIsBranchTaken(true);
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				}else{
					EX_IF_Latch.setIsBranchTaken(false);
				}
				break;
			}

			case end:{
				EX_IF_Latch.setIsBranchTaken(false);
				break;
			}

			default:
				Misc.printErrorAndExit("Instruction not present in the switch case of Execute.java");
		}
	}




}

