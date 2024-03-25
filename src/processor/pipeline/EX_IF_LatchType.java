package processor.pipeline;

public class EX_IF_LatchType {

	Integer branchPC = 0;


	public EX_IF_LatchType() {
		branchPC = 0;
		isBranchTaken = false;
	}
	public void setIsBranchTaken(boolean isBranchTaken) {
		this.isBranchTaken = isBranchTaken;
	}

	Boolean isBranchTaken = false;
	public int getBranchPC() {
		return branchPC;

	}

	public boolean getIsBranchTaken() {
		return isBranchTaken;

	}



	public void setBranchPC(int branchPC) {
		this.branchPC = branchPC;

	}

}
