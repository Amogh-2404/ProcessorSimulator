package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {

	Boolean MA_enable;
	Instruction instruction;


	Boolean isMABusy, isValidInst;
	public Boolean isMAstageEnabled() {
		return MA_enable;
	}

	public void setMAstageEnabled(boolean bool) {
		MA_enable = bool;
	}

	Integer aluResult, excess, op;
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

	public int getAluRes() {
		return aluResult;
	}

	public void setAluRes(Integer result) {
		aluResult = result;
	}

	public int getExcess() {
		return excess;
	}

	public void setExcess(Integer value) {
		excess = value;
	}

	public Integer getOperand() {
		return op;
	}

	public void setOperand(Integer operand) {
		op = operand;
	}

	public Boolean isMAStageBusy() {
		return isMABusy;
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
