package processor.pipeline;

public class EX_IF_LatchType {


	boolean isBranchTaken;

	int branchPC;
	
	public EX_IF_LatchType()
	{
		isBranchTaken = false;
	}

	public boolean getIsBranchTaken(){return isBranchTaken;}

	public void setIsBranchTaken(boolean isBranchTaken){this.isBranchTaken=isBranchTaken;}

	public void setIsBranch_enable(boolean isBranchTaken, int pC)
	{
		this.isBranchTaken = isBranchTaken;
		this.branchPC = pC;
	}

	public int getBranchPC(){return branchPC;}

	public void setBranchPC(int branchPC){this.branchPC = branchPC;}


}
