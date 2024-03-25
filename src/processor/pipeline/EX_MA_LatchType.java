package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {

	Boolean MA_enable = false;
	Instruction instruction;


	Boolean isMABusy = false, isValidInst = false;
	public Boolean isMAstageEnabled() {
		return MA_enable;
	}

	public void setMAstageEnabled(boolean bool) {
		MA_enable = bool;
	}

	Integer aluResult = 0, excess =0, op =0;
	public Instruction getInstruction() {
		return instruction;
	}

	public EX_MA_LatchType() {
		MA_enable = false;
		instruction = null;
		aluResult = op = excess = 0;
		isMABusy = isValidInst = false;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public int getalures() {
		return aluResult;
	}

	public void setalures(Integer result) {
		aluResult = result;
	}

	public int getExcess() {
		return excess;
	}

	public void setExcess(Integer value) {
		excess = value;
	}

	public void setOperand(Integer operand) {
		op = operand;
	}

	public boolean isMAStageBusy() {
		return isMABusy;
	}

	public int getOperand() {
		return op;
	}


	public void setMAStageBusy(Boolean isMABusy) {
		this.isMABusy = isMABusy;
	}

	public void setValidInstruction(Boolean bool) {
		this.isValidInst = bool;
	}

	public Boolean isValidInst() {
		return isValidInst;
	}

}
