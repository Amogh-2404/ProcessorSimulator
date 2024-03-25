package processor.interlocks;

import processor.pipeline.EX_IF_LatchType;

import processor.pipeline.IF_OF_LatchType;



public class ControlInterlock {

    
    IF_OF_LatchType IF_OF_Latch;
    EX_IF_LatchType EX_IF_Latch;


    public void validate() {
        if (EX_IF_Latch.getIsBranchTaken()) {
            IF_OF_Latch.setNopInstruction(true);
        }
    }
    
    public ControlInterlock(IF_OF_LatchType IF_OF_Latch, EX_IF_LatchType EX_IF_Latch) {
        this.IF_OF_Latch = IF_OF_Latch;
        this.EX_IF_Latch = EX_IF_Latch;
    }


}
