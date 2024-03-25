package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	int operand1, operand2, immediate, branchTarget;
	boolean EX_enable,isImmediate, isEXstageBusy, isEXstageBusyDueToMA, isValidInstruction,additionalNop;
	Instruction instruction;


	public OF_EX_LatchType() {
		EX_enable = false;
		operand1 = operand2 = immediate = branchTarget = 0;
		isEXstageBusy = isEXstageBusyDueToMA = isValidInstruction = false;
		instruction = null;
		isImmediate = false;
	}

	public boolean isEXstageEnabled() {
		return EX_enable;
	}

	public void setEXstageEnabled(boolean eX_enable) {
		EX_enable = eX_enable;
	}

	 public void setisAdditionalNop(boolean bool) {
		additionalNop = bool;
	 }
	
	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public int getOperand1() {
		return operand1;
	}

	public void setOperand1(int value) {
		operand1 = value;
	}

	public int getOperand2() {
		return operand2;
	}

	public void setOperand2(int value) {
		operand2 = value;
	}

	public int getImmediate() {
		return immediate;
	}

	public void setImmediate(int value) {
		immediate = value;
	}

	public int getBranchTarget() {
		return branchTarget;
	}

	public boolean isEXstageBusy() {
		return isEXstageBusy;
	}
	public void setValidInstruction(boolean bool) {
		this.isValidInstruction = bool;
	}

	public void setEXisBusyDueToMA(boolean bool) {
		this.isEXstageBusyDueToMA = bool;
	}
	public boolean isValidInstruction() {
		return isValidInstruction;
	}

	public boolean isEXstageBusyDueToMA() {
		return isEXstageBusyDueToMA;
	}



	public void setIsImmediate(boolean bool) {
		isImmediate = bool;
	}

	public void setEXstageBusy(boolean bool) {
		this.isEXstageBusy = bool;
	}

	public void setBranchTarget(int value) {
		branchTarget = value;
	}

	public boolean getIsImmediate() {
		return isImmediate;
	}


}
