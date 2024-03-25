package processor.pipeline;

public class IF_OF_LatchType {

	boolean OF_stage_enable, isNopInstruction, isStall, isOFStageBusy, isValidInstruction;; ;
	int instruction, currentPC;


	public void setCurrentPC(int pc) {
		currentPC = pc;
	}

	public void setNopInstruction(boolean bool) {
		this.isNopInstruction = bool;
	}

	public void setOF_stage_enable(boolean bool) {
		OF_stage_enable = bool;
	}

	public int getInstruction() {
		return instruction;
	}


	public IF_OF_LatchType() {
		isStall = false;
		isOFStageBusy = false;
		isValidInstruction = false;
		OF_stage_enable = false;
		isNopInstruction = false;
	}

	public boolean isOF_stage_enable() {
		return OF_stage_enable;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

	
	
	public int getCurrentPC() {
		return currentPC;
	}


	public void setOFStageBusy(boolean bool) {
		this.isOFStageBusy = bool;
	}

	public boolean isOFStageBusy() {
		return isOFStageBusy;
	}


	public boolean getNopInstruction() {
		return this.isNopInstruction;
	}

	public void setStall(boolean bool) {
		this.isStall = bool;
	}

	public boolean getStall() {
		return this.isStall;
	}

	public void setValidInstruction(boolean bool) {
		this.isValidInstruction = bool;
	}

	public boolean isValidInstruction() {
		return isValidInstruction;
	}

}
