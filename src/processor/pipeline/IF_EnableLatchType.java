package processor.pipeline;

public class IF_EnableLatchType {
	
	boolean IF_enable;
	boolean isStall; // whether IF stage is stalling or not

	boolean isIFBusy;

	public IF_EnableLatchType()
	{
		isStall = false;
		isIFBusy = false;
		IF_enable = true;

	}

	public boolean isIF_enable() {
		return IF_enable;
	}

	public void setIF_busy(boolean arg){
		 isIFBusy = arg;
	}

	public void setIF_enable(boolean iF_enable) {
		IF_enable = iF_enable;
	}

	public void setStall(boolean isStall) {
		this.isStall = isStall;
	}

	public boolean isStall() {
		return this.isStall;
	}


	public void setIF_Busy(boolean b) {
		isIFBusy = b;
	}

	public boolean getisIFBusy() {
		return isIFBusy;
	}
}
