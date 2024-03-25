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

	IF_EnableLatchType IF_EnableLatch;

	IF_OF_LatchType IF_OF_Latch;

	
	Integer aluResult = 0;
	int excess = 0;
	int op = 0;
	Integer branchPC = 0;
	Boolean isBranchTaken = false;

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


		Instruction.OperationType operationType = instruction.getOperationType();

		if (operationType == Instruction.OperationType.sub || operationType == Instruction.OperationType.subi) {
			this.aluResult = getSolutionAdjusted(operand1 - second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.mul || operationType == Instruction.OperationType.muli) {
			this.aluResult = getSolutionAdjusted(operand1 * second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.add || operationType == Instruction.OperationType.addi) {
			this.aluResult = getSolutionAdjusted(operand1 + second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.or || operationType == Instruction.OperationType.ori) {
			this.aluResult = getSolutionAdjusted(operand1 | second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.xor || operationType == Instruction.OperationType.xori) {
			this.aluResult = getSolutionAdjusted(operand1 ^ second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.div || operationType == Instruction.OperationType.divi) {
			this.aluResult = getSolutionAdjusted(operand1 / second);
			this.excess = (int) (operand1 % second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.slt || operationType == Instruction.OperationType.slti) {
			this.aluResult = (operand1 < second) ? 1 : 0;
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.sll || operationType == Instruction.OperationType.slli) {
			this.aluResult = getSolutionAdjusted(operand1 << second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.sra || operationType == Instruction.OperationType.srai) {
			this.aluResult = getSolutionAdjusted(operand1 >> second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.load) {
			this.aluResult = getSolutionAdjusted(operand1 + immediate);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.bne) {
			if (operand1 != operand2) {
				this.isBranchTaken = true;
				this.branchPC = OF_EX_Latch.getBranchTarget();
			} else {
				this.isBranchTaken = false;
			}
		} else if (operationType == Instruction.OperationType.and || operationType == Instruction.OperationType.andi) {
			this.aluResult = getSolutionAdjusted(operand1 & second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.beq) {
			if (operand1 == operand2) {
				this.isBranchTaken = true;
				this.branchPC = OF_EX_Latch.getBranchTarget();
			} else {
				this.isBranchTaken = false;
			}
		} else if (operationType == Instruction.OperationType.srl || operationType == Instruction.OperationType.srli) {
			this.aluResult = getSolutionAdjusted(operand1 >>> second);
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.store) {
			this.aluResult = getSolutionAdjusted(operand2 + immediate);
			this.op = (int) operand1;
			this.isBranchTaken = false;
		} else if (operationType == Instruction.OperationType.blt) {
			if (operand1 < operand2) {
				this.isBranchTaken = true;
				this.branchPC = OF_EX_Latch.getBranchTarget();
			} else {
				this.isBranchTaken = false;
			}
		} else if (operationType == Instruction.OperationType.bgt) {
			if (operand1 > operand2) {
				this.isBranchTaken = true;
				this.branchPC = OF_EX_Latch.getBranchTarget();
			} else {
				this.isBranchTaken = false;
			}
		} else if (operationType == Instruction.OperationType.jmp) {
			EX_IF_Latch.setIsBranchTaken(true);
			EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
			this.isBranchTaken = true;
			this.branchPC = OF_EX_Latch.getBranchTarget();
		} else if (operationType == Instruction.OperationType.end) {
			EX_IF_Latch.setIsBranchTaken(false);
			this.isBranchTaken = false;
		} else {
			Misc.printErrorAndExit("Instruction not supported in Execute Stage");
		}


	}

	
	private void scheduleEvent(Instruction inst) {

		Instruction.OperationType operationType = inst.getOperationType();

		if (operationType == Instruction.OperationType.mul || operationType == Instruction.OperationType.muli) {
			Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
					Clock.getCurrentTime() + Configuration.multiplier_latency, this, this, inst,
					this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
			OF_EX_Latch.setEXstageBusy(true);
		} else if (operationType == Instruction.OperationType.div || operationType == Instruction.OperationType.divi) {
			Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
					Clock.getCurrentTime() + Configuration.divider_latency, this, this, inst,
					this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
			OF_EX_Latch.setEXstageBusy(true);
		} else if (operationType == Instruction.OperationType.add || operationType == Instruction.OperationType.sub ||
				operationType == Instruction.OperationType.and || operationType == Instruction.OperationType.or ||
				operationType == Instruction.OperationType.xor || operationType == Instruction.OperationType.slt ||
				operationType == Instruction.OperationType.sll || operationType == Instruction.OperationType.srl ||
				operationType == Instruction.OperationType.sra || operationType == Instruction.OperationType.addi ||
				operationType == Instruction.OperationType.subi || operationType == Instruction.OperationType.andi ||
				operationType == Instruction.OperationType.ori || operationType == Instruction.OperationType.xori ||
				operationType == Instruction.OperationType.slti || operationType == Instruction.OperationType.slli ||
				operationType == Instruction.OperationType.srli || operationType == Instruction.OperationType.srai ||
				operationType == Instruction.OperationType.load || operationType == Instruction.OperationType.store ||
				operationType == Instruction.OperationType.beq || operationType == Instruction.OperationType.bne ||
				operationType == Instruction.OperationType.blt || operationType == Instruction.OperationType.bgt) {
			Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
					Clock.getCurrentTime() + Configuration.ALU_latency, this, this, inst,
					this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
			OF_EX_Latch.setEXstageBusy(true);
		} else if (operationType == Instruction.OperationType.jmp || operationType == Instruction.OperationType.end) {
			containingProcessor.getControlInterlockUnit().validate();
			OF_EX_Latch.setEXstageBusy(false);
			OF_EX_Latch.setValidInstruction(false);
			OF_EX_Latch.setEXstageEnabled(false);
			EX_MA_Latch.setValidInstruction(true);
			EX_MA_Latch.setInstruction(inst);
			EX_MA_Latch.setMAstageEnabled(true);
		} else {
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
			EX_MA_Latch.setalures(event.getAluResult());
			EX_MA_Latch.setExcess(event.getExcess());
			EX_MA_Latch.setOperand(event.getOp());
			EX_MA_Latch.setInstruction(event.getInst());


			containingProcessor.getControlInterlockUnit().validate();
		}
	}

}
