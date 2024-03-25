package processor.pipeline;

public class EX_IF_LatchType {

	int branchPC;


	public EX_IF_LatchType() {
		isBranchTaken = false;
		branchPC = 0;
	}

	public boolean getIsBranchTaken() {
		return isBranchTaken;
	}

	public void setIsBranchTaken(boolean isBranchTaken) {
		this.isBranchTaken = isBranchTaken;
	}

	boolean isBranchTaken;
	public int getBranchPC() {
		return branchPC;
	}

	public void setBranchPC(int branchPC) {
		this.branchPC = branchPC;
	}

}
