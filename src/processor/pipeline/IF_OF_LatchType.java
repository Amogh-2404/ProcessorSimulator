package processor.pipeline;

public class IF_OF_LatchType {
	
	boolean OF_enable;
	int instruction ,currentPC;


	boolean isNop, isStall, isOF_busy, isValidInstruction;
	
	public IF_OF_LatchType()
	{
		isOF_busy = false;
		isValidInstruction = false;
		OF_enable = false;
		isNop = false;
	}

	public boolean isOF_enable() {
		return OF_enable;
	}

	public boolean getOF_busy() {
		return isOF_busy;
	}

	public boolean getIsValidInstruction() {
		return isValidInstruction;
	}
	public void setIsValidInstruction(boolean isValidInstruction) {
		this.isValidInstruction = isValidInstruction;
	}

	public void setOF_busy(boolean isOF_busy) {
		this.isOF_busy = isOF_busy;
	}


	public void setOF_enable(boolean oF_enable) {
		OF_enable = oF_enable;
	}

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

	public int getCurrentPC(){return currentPC;}

	public void setCurrentPC(int currPC){ currentPC = currPC;}

	public void setNop(boolean isNop) {
		this.isNop = isNop;
	}

	public boolean getIsNop() {
		return this.isNop;
	}

	public void setStall(boolean isStall) {
		this.isStall = isStall;
	}

	public boolean getStall() {
		return this.isStall;
	}

}
