package processor.pipeline;

import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;

public class Execute implements Element {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;

	int aluResult, op, excess;

	boolean isBranchTaken;

	int branchPC;

	private void scheduleEvent(Instruction inst) {

		switch (inst.getOperationType()) {
			case mul:
			case muli: {
				// System.out.println("Scheduling Event...\n"); // TEST

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.multiplier_latency, (Element) this, (Element) this, inst, this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEX_busy(true);
				break;
			}
			case div:
			case divi: {
				// System.out.println("Scheduling Event...\n"); // TEST

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.divider_latency, (Element) this, (Element) this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEX_busy(true);
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
				// System.out.println("Scheduling Event...\n"); // TEST

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.ALU_latency, (Element) this, (Element) this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEX_busy(true);
				break;
			}

			case jmp:
			case end: {
				// Performing Control-Interlock validation for branch instructions
				containingProcessor.getControlInterlockUnit().validate();

				OF_EX_Latch.setEX_busy(false);
				OF_EX_Latch.setEX_enable(false);


				EX_MA_Latch.setInstruction(inst);
				EX_MA_Latch.setMA_enable(true);
				break;
			}

			default:
				Misc.printErrorAndExit("Unknown Instruction!!");
		}
	}
	
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

			if (!EX_MA_Latch.getIsMA_busy()) {
				OF_EX_Latch.setEX_busy(true);
				Instruction inst = OF_EX_Latch.getInstruction();
				EX_MA_Latch.setInstruction(inst);

				if (inst != null) {
					compute(inst);
					scheduleEvent(inst);
					containingProcessor.getControlInterlockUnit().validate();
				} else {
					EX_MA_Latch.setInstruction(null);
				}
			}
			else{
				EX_MA_Latch.setIsMA_busy(true);
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
				this.aluResult = (getResult(op1+second));
				this.isBranchTaken = (false);
				break;
			}

			case mul:
			case muli:{
				this.aluResult = (getResult(op1*second));
				this.isBranchTaken = (false);
				break;}

			case div:
			case divi:{
				this.aluResult = (getResult(op1/second));
				this.excess = ((int)(op1%second));
				this.isBranchTaken = (false);
				break;
			}

			case and:
			case andi:{
				this.aluResult = (getResult(op1 & second));
				this.isBranchTaken = (false);
				break;
			}

			case sub:
			case subi:{
				this.aluResult = (getResult(op1-second));
				this.isBranchTaken = (false);
				break;
			}



			case or:
			case ori:{
				this.aluResult = (getResult(op1 | second));
				this.isBranchTaken = (false);
				break;
			}

			case xor:
			case xori:{
				this.aluResult = (getResult(op1 ^ second));
				this.isBranchTaken = (false);
				break;
			}



			case sra:
			case srai:{
				this.aluResult = (getResult(op1 >> second));
				this.isBranchTaken = (false);
				break;
			}

			case load:{
				this.aluResult = (getResult(op1 + imm));
				this.isBranchTaken = (false);
				break;
			}

			case slt:
			case slti:{
				this.aluResult = ((op1 < second) ? 1 : 0);
				this.isBranchTaken = (false);
				break;
			}

			case sll:
			case slli:{
				this.aluResult = (getResult(op1 << second));
				this.isBranchTaken = (false);
				break;
			}

			case srl:
			case srli:{
				this.aluResult = (getResult(op1 >>> second));
				this.isBranchTaken = (false);
				break;
			}

			case store:{
				this.aluResult = (getResult(op2 + imm));
				this.op = ((int)op1);
				this.isBranchTaken = (false);
				break;
			}

			case beq:{
				if (op1 == op2){
					this.isBranchTaken = (true);
					this.branchPC =  (OF_EX_Latch.getBranchTarget());
				}else{
					this.isBranchTaken = (false);
				}
				break;
			}

			case bne:{
				if (op1 != op2){
					this.isBranchTaken = (true);
					this.branchPC =  (OF_EX_Latch.getBranchTarget());
				}else{
					this.isBranchTaken = (false);
				}
				break;
			}



			case jmp:{
				this.isBranchTaken = (true);
				this.branchPC =  (OF_EX_Latch.getBranchTarget());
				break;
			}

			case blt:{
				if (op1 < op2){
					this.isBranchTaken = (true);
					this.branchPC =  (OF_EX_Latch.getBranchTarget());
				}else{
					this.isBranchTaken = (false);
				}
				break;
			}

			case bgt:{
				if (op1 > op2){
					this.isBranchTaken = (true);
					this.branchPC =  (OF_EX_Latch.getBranchTarget());
				}else{
					this.isBranchTaken = (false);
				}
				break;
			}

			case end:{
				this.isBranchTaken = (false);
				break;
			}

			default:
				Misc.printErrorAndExit("Instruction not present in the switch case of Execute.java");
		}
	}


	@Override
	public void handleEvent(Event event) {
		if (EX_MA_Latch.isMA_enable()) { // If MA stage is busy
			// System.out.println("Event postponed | MA Busy"); // TEST

			event.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(event);
		} else {
			ExecutionCompleteEvent e = (ExecutionCompleteEvent) event;

			// System.out.println("Event Triggered in EX: \n" + event); // TEST

			OF_EX_Latch.setEX_busy(false);

			OF_EX_Latch.setEX_enable(false);

			EX_IF_Latch.setIsBranchTaken(e.isBranchTaken());
			EX_IF_Latch.setBranchPC(e.getBranchPC());

			EX_MA_Latch.setMA_enable(true);
			EX_MA_Latch.setInstruction(e.getInst());
			EX_MA_Latch.setAluResult(e.getAluResult());
			EX_MA_Latch.setExcess(e.getExcess());
			EX_MA_Latch.setOperand(e.getOp());

			// Performing Control-Interlock validation for branch instructions
			containingProcessor.getControlInterlockUnit().validate();
		}
	}
	}


