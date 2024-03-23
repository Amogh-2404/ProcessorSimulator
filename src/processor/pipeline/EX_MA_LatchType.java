package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {
	
	boolean MA_enable, isMA_busy, setIsValidInstruction;

	Instruction inst;

	int aluResult, excess, op;
	// Excess bits to store in x31 register

	public EX_MA_LatchType()
	{
		MA_enable = false;
		excess = 0 ;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public boolean getIsValidInstruction(){
		return setIsValidInstruction;
	}

	public void setIsValidInstruction(boolean isValidInstruction){
		this.setIsValidInstruction = isValidInstruction;
	}

	public boolean getIsMA_busy(){
		return isMA_busy;
	}
	public void setIsMA_busy(boolean isMA_busy){
		this.isMA_busy = isMA_busy;
	}

	public void setInstruction(Instruction newIns){inst = newIns;}

	public int getAluResult() {
		return aluResult;
	}

	public void setAluResult(int result) {
		aluResult = result;
	}

	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}

	public Instruction getInstruction(){return inst;}


	public void setExcess(int exc) {
		excess = exc;
	}

	public int getOperand() {
		return op;
	}

	public void setOperand(int operand) {
		op = operand;
	}



	public int getExcess() {
		return excess;
	}


}
