package processor.interlocks;

import processor.pipeline.IF_OF_LatchType;
import processor.pipeline.EX_IF_LatchType;
public class ControlInterlock {
    IF_OF_LatchType IF_OF_Latch;
    EX_IF_LatchType EX_IF_Latch;
    public ControlInterlock(IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch) {
        this.IF_OF_Latch = iF_OF_Latch;
        this.EX_IF_Latch = eX_IF_Latch;
    }
    public void validate() {
        if (EX_IF_Latch.getIsBranchTaken()) {
            IF_OF_Latch.setNop(true); // Setting Nop to True indicating an invalid instruction
        }
    }
}
