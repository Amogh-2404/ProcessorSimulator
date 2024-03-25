package processor.pipeline;

import processor.Clock;
import processor.Processor;
import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.ExecutionCompleteEvent;

import generic.Instruction;
import generic.Simulator;
import generic.Misc;


public class Execute implements Element {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;

	
	int aluResult, excess, op;
	Boolean isBranchTaken;
	int branchPC;

	public Execute(Processor containingProcessor, OF_EX_LatchType OF_EX_Latch,
			EX_MA_LatchType EX_MA_Latch, EX_IF_LatchType EX_IF_Latch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = OF_EX_Latch;
		this.EX_MA_Latch = EX_MA_Latch;
		this.EX_IF_Latch = EX_IF_Latch;
	}

	private int convertDecimalToBinary(String binaryString, boolean isSigned) {
		if (!isSigned) {
			return Integer.parseInt(binaryString, 2);

		} else {
			String copyString = '0' + binaryString.substring(1);
			int answer = Integer.parseInt(copyString, 2);


			if (binaryString.length() == 32) {
				int power = (1 << 30);
				if (binaryString.charAt(0) == '1') {


					answer -= power;
					answer -= power;
				}
			} else {
				int power = (1 << (binaryString.length() - 1));
				if (binaryString.charAt(0) == '1') {

					answer -= power;
				}
			}

			return answer;
		}
	}

	public void performEX() {

		if (OF_EX_Latch.isEXstageEnabled()) {

			if (!OF_EX_Latch.isEXstageBusy()) {
				if (!EX_MA_Latch.isMAStageBusy()) {
					OF_EX_Latch.setEXisBusyDueToMA(false);

					if (OF_EX_Latch.isValidInstruction()) {
						Instruction instruction = OF_EX_Latch.getInstruction();

						if (instruction != null) {
							execute_(instruction);
							scheduleEvent(instruction);
							OF_EX_Latch.setValidInstruction(false);

						} else {

							OF_EX_Latch.setValidInstruction(false);

							EX_MA_Latch.setInstruction(instruction);
																
							EX_MA_Latch.setValidInstruction(true);

							EX_IF_Latch.setIsBranchTaken(false);

						}
					}
				} else {
					OF_EX_Latch.setEXisBusyDueToMA(true);
				}
			}

			EX_MA_Latch.setMAstageEnabled(true);
			OF_EX_Latch.setEXstageEnabled(false);

		}
	}

	private int getSolutionAdjusted(long result) {
		String binaryString = Long.toBinaryString(result);
		if (binaryString.length() <= 32) { 
			return (int) result;

		} else {
			
			
			this.excess = convertDecimalToBinary(
					binaryString.substring(0, binaryString.length() - 32), (result < 0));

			return convertDecimalToBinary(binaryString.substring(binaryString.length() - 32), (result < 0));
		}
	}

	private void execute_(Instruction instruction) {
		long operand2 = OF_EX_Latch.getOperand2();
		long immediate = OF_EX_Latch.getImmediate();
		long second = (OF_EX_Latch.getIsImmediate()) ? immediate : operand2;
		long operand1 = OF_EX_Latch.getOperand1();


		switch (instruction.getOperationType()) {

			case sub:
			case subi: {
				this.aluResult = getSolutionAdjusted(operand1 - second);
				this.isBranchTaken = false;
				break;
			}

			case mul:
			case muli: {
				this.aluResult = getSolutionAdjusted(operand1 * second);
				this.isBranchTaken = false;
				break;
			}
			case add:
			case addi: {
				this.aluResult = getSolutionAdjusted(operand1 + second);
				this.isBranchTaken = false;
				break;
			}
			case or:
			case ori: {


				this.aluResult = getSolutionAdjusted(operand1 | second);
				this.isBranchTaken = false;
				break;
			}

			case xor:
			case xori: {


				this.aluResult = getSolutionAdjusted(operand1 ^ second);
				this.isBranchTaken = false;
				break;
			}

			case div:
			case divi: {

				this.aluResult = getSolutionAdjusted(operand1 / second);
				this.excess = (int) (operand1 % second);
				this.isBranchTaken = false;
				break;
			}

			case slt:
			case slti: {


				this.aluResult = (operand1 < second) ? 1 : 0;
				this.isBranchTaken = false;
				break;
			}

			case sll:
			case slli: {


				this.aluResult = getSolutionAdjusted(operand1 << second);
				this.isBranchTaken = false;
				break;
			}
			case sra:
			case srai: {


				this.aluResult = getSolutionAdjusted(operand1 >> second);
				this.isBranchTaken = false;
				break;
			}

			case load: {


				this.aluResult = getSolutionAdjusted(operand1 + immediate);
				this.isBranchTaken = false;
				break;
			}

			case bne: {
				if (operand1 != operand2) {

					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {

					this.isBranchTaken = false;
				}
				break;
			}

			case and:
			case andi: {

				this.aluResult = getSolutionAdjusted(operand1 & second);
				this.isBranchTaken = false;
				break;
			}
			case beq: {
				if (operand1 == operand2) {
					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {

					this.isBranchTaken = false;
				}
				break;
			}



			case srl:
			case srli: {


				this.aluResult = getSolutionAdjusted(operand1 >>> second);
				this.isBranchTaken = false;
				break;
			}



			case store: {
				this.aluResult = getSolutionAdjusted(operand2 + immediate);
				this.op = (int) operand1;
				this.isBranchTaken = false;
				break;
			}



			case blt: {
				if (operand1 < operand2) {

					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {

					this.isBranchTaken = false;
				}
				break;
			}

			case bgt: {
				if (operand1 > operand2) {
					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {

					this.isBranchTaken = false;
				}
				break;
			}

			case jmp: {
				EX_IF_Latch.setIsBranchTaken(true);
				EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				this.isBranchTaken = true;
				this.branchPC = OF_EX_Latch.getBranchTarget();
				break;
			}


			case end: {
				EX_IF_Latch.setIsBranchTaken(false);
				this.isBranchTaken = false;
				break;
			}

			default:
				Misc.printErrorAndExit("Instruction not supported in Execute Stage");
		}


	}

	
	private void scheduleEvent(Instruction inst) {

		switch (inst.getOperationType()) {
			case mul:
			case muli: {
				

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.multiplier_latency, this, this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEXstageBusy(true);
				break;
			}
			case div:
			case divi: {
				

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.divider_latency, this, this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEXstageBusy(true);
				break;
			}

			case add:
			case sub:
			case and:
			case or:
			case xor:
			case slt:
			case sll:
			case srl:
			case sra:
			case addi:
			case subi:
			case andi:
			case ori:
			case xori:
			case slti:
			case slli:
			case srli:
			case srai:
			case load:
			case store:
			case beq:
			case bne:
			case blt:
			case bgt: {
				

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.ALU_latency, this, this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEXstageBusy(true);
				break;
			}

			case jmp:
			case end: {
				
				containingProcessor.getControlInterlockUnit().validate();

				OF_EX_Latch.setEXstageBusy(false);
				OF_EX_Latch.setValidInstruction(false);
				OF_EX_Latch.setEXstageEnabled(false);

				EX_MA_Latch.setValidInstruction(true);
				EX_MA_Latch.setInstruction(inst);
				EX_MA_Latch.setMAstageEnabled(true);
				break;
			}

			default:
				Misc.printErrorAndExit("Unknown Instruction!!");
		}
	}

	
	@Override
	public void handleEvent(Event e) {
		

		if (EX_MA_Latch.isMAStageBusy()) {
			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
		} else {
			ExecutionCompleteEvent event = (ExecutionCompleteEvent) e;

			OF_EX_Latch.setEXstageBusy(false);
			OF_EX_Latch.setValidInstruction(false);
			OF_EX_Latch.setEXstageEnabled(false);

			EX_IF_Latch.setIsBranchTaken(event.isBranchTaken());
			EX_IF_Latch.setBranchPC(event.getBranchPC());

			EX_MA_Latch.setMAstageEnabled(true);
			EX_MA_Latch.setValidInstruction(true);
			EX_MA_Latch.setAluRes(event.getAluResult());
			EX_MA_Latch.setExcess(event.getExcess());
			EX_MA_Latch.setOperand(event.getOp());
			EX_MA_Latch.setInstruction(event.getInst());


			containingProcessor.getControlInterlockUnit().validate();
		}
	}

}
