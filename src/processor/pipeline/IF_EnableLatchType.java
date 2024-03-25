package processor.pipeline;

public class IF_EnableLatchType {

	boolean IF_Stage_enable, isIFStall, isIFstageBusy;


	public boolean getIsIFStageStall() {
		return this.isIFStall;
	}

	public void setIFstageBusy(boolean bool) {
		this.isIFstageBusy = bool;
	}

	public boolean isIFstageBusy() {
		return isIFstageBusy;
	}
	public IF_EnableLatchType() {
		isIFStall = false;
		isIFstageBusy = false;
		IF_Stage_enable = true;
	}


	public void setIsIFStagestall(boolean bool) {
		this.isIFStall = bool;
	}


	public boolean isIF_Stage_enable() {
		return IF_Stage_enable;
	}

	public void setIF_Stage_enable(boolean bool) {
		IF_Stage_enable = bool;
	}


}
