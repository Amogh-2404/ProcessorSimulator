package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {
	
	boolean RW_enable;

	Instruction inst ;

	int ldResult, aluResult, excess;
	public MA_RW_LatchType()
	{
		RW_enable = false;
		excess = 0;
	}

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}

	public Instruction getInstruction(){return inst;}

	public void setInstruction(Instruction newInst){inst = newInst;}

	public int getLdResult(){return ldResult;}

	public void setLdResult(int ldResult){this.ldResult = ldResult;}

	public int getAluResult(){return aluResult;}

	public void setAluResult(int aluResult){this.aluResult = aluResult;}

	public int getExcess(){return excess;}

	public void setExcess(int excess){this.excess = excess;}
}
