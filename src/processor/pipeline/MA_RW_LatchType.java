package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {

	boolean RW_enable,isValidInstruction, enableAdditionalNop=false;
	int loadResult, aluResult, excess;
	Instruction instruction;

	public void setIsRWstageEnabled(boolean rW_enable) {
		RW_enable = rW_enable;
	}



	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction newInst) {
		instruction = newInst;
		setEnableAdditionalNop(false);
	}

	public MA_RW_LatchType() {
		RW_enable = false;
		loadResult = 0;
		aluResult = 0;
		excess = 0;
		isValidInstruction = false;
		instruction = null;
	}

	public int getldres() {
		return loadResult;
	}

	public boolean getEnableAdditionalNop() {
		return enableAdditionalNop;
	}

	public void setEnableAdditionalNop(boolean bool) {
		enableAdditionalNop = getEnableAdditionalNop();
		this.enableAdditionalNop = bool;
	}


	public boolean isValidInstruction() {
		return isValidInstruction;
	}
	public int getalures() {
		return aluResult;
	}

	public void setalures(int aluResult) {
		this.aluResult = aluResult;
	}


	public void setValidInstruction(boolean bool) {
		this.isValidInstruction = bool;
	}

	public void setldres(int loadResult) {
		this.loadResult = loadResult;
	}

	public int getExcess() {
		return excess;
	}

	public void setExcess(int excess) {
		this.excess = excess;
	}


	public boolean getIsRWstageEnabled() {
		return RW_enable;
	}

}
