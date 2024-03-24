package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	
	boolean EX_enable, isEX_busy = false, isEX_MA_busy = false, isValidInstruction=false;

	Instruction inst;

	int operand1 = 0, operand2 = 0, immediate=0, branchTarget;

	boolean isImmediate ;
	
	public OF_EX_LatchType()
	{
		EX_enable = false;
		inst = null;
		branchTarget = 0;
		isImmediate = false;

	}

	public boolean getEX_busy(){
		return isEX_busy;
	}

	public void setEX_busy(boolean isEX_busy){
		this.isEX_busy = isEX_busy;
	}

	public boolean isEX_enable() {
		return EX_enable;
	}

	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}

	public Instruction getInstruction(){return inst;}
	public void setInstruction(Instruction newInst){inst = newInst;}

	public int getOperand1(){return operand1;}

	public void setOperand1(int op1){operand1 = op1;}

	public int getOperand2(){return operand2;}

	public void setOperand2(int op2){operand2 = op2;}

	public int getImmediate(){return immediate;}

	public void setImmediate(int imm){immediate = imm;}

	public boolean getIsValidInstruction(){return isValidInstruction;}

	public void setIsValidInstruction(boolean isValidInstruction){this.isValidInstruction = isValidInstruction;}

	public int getBranchTarget(){return branchTarget;}

	public void setBranchTarget(int target){branchTarget=target;}

	public boolean getIsImmediate(){return isImmediate;}

	public void setIsImmediate(boolean isImm){isImmediate = isImm;}

    public void setEX_MA_busy(boolean b) {
		isEX_MA_busy = b;
    }

	public boolean getEX_MA_busy() {
		return isEX_MA_busy;
	}
}
