package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	int operand1, operand2, immediate, branchTarget;
	boolean EX_enable,isImmediate, isEXstageBusy, isEXstageBusyDueToMA, isValidInstruction;
	Instruction inst; 


	public OF_EX_LatchType() {
		EX_enable = false;
		operand1 = operand2 = immediate = branchTarget = 0;
		isEXstageBusy = isEXstageBusyDueToMA = isValidInstruction = false;
		inst = null;
		isImmediate = false;

	}

	public boolean isEXstageEnabled() {
		return EX_enable;
	}

	public void setEXstageEnabled(boolean eX_enable) {
		EX_enable = eX_enable;
	}

	
	
	public Instruction getInstruction() {
		return inst;
	}

	public void setInstruction(Instruction newInst) {
		inst = newInst;
	}

	public int getOperand1() {
		return operand1;
	}

	public void setOperand1(int op1) {
		operand1 = op1;
	}

	public int getOperand2() {
		return operand2;
	}

	public void setOperand2(int op2) {
		operand2 = op2;
	}

	public int getImmediate() {
		return immediate;
	}

	public void setImmediate(int imm) {
		immediate = imm;
	}

	public int getBranchTarget() {
		return branchTarget;
	}

	public void setBranchTarget(int target) {
		branchTarget = target;
	}

	public boolean getIsImmediate() {
		return isImmediate;
	}

	public void setIsImmediate(boolean isImm) {
		isImmediate = isImm;
	}

	public void setEXstageBusy(boolean isEXBusy) {
		this.isEXstageBusy = isEXBusy;
	}

	public boolean isEXstageBusy() {
		return isEXstageBusy;
	}

	public void setEXisBusyDueToMA(boolean isEXMABusy) {
		this.isEXstageBusyDueToMA = isEXMABusy;
	}

	public boolean isEXstageBusyDueToMA() {
		return isEXstageBusyDueToMA;
	}

	public boolean isValidInstruction() {
		return isValidInstruction;
	}

	public void setValidInstruction(boolean isValidInst) {
		this.isValidInstruction = isValidInst;
	}

}
