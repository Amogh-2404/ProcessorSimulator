package processor.interlocks;

import processor.pipeline.EX_IF_LatchType;

import processor.pipeline.IF_OF_LatchType;
import processor.pipeline.OF_EX_LatchType;


public class ControlInterlock {

    OF_EX_LatchType OF_EX_Latch;
    
    IF_OF_LatchType IF_OF_Latch;
    EX_IF_LatchType EX_IF_Latch;


    public void validate() {
        if (EX_IF_Latch.getIsBranchTaken()) {
            IF_OF_Latch.setNopInstruction(true);
            OF_EX_Latch.setisAdditionalNop(false);
        }
    }
    
    public ControlInterlock(IF_OF_LatchType IF_OF_Latch, EX_IF_LatchType EX_IF_Latch, OF_EX_LatchType OF_EX_Latch ) {
        this.IF_OF_Latch = IF_OF_Latch;
        this.OF_EX_Latch = OF_EX_Latch;
        this.EX_IF_Latch = EX_IF_Latch;

    }


}
