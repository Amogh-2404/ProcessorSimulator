package processor.interlocks;

import processor.pipeline.*;

import processor.Processor;

import generic.Instruction;
import generic.Operand.OperandType;
import generic.Misc;
import generic.Instruction.OperationType;
import generic.Operand;



public class DataInterlock {

   
    Processor containingProcessor;
    IF_EnableLatchType IF_EnableLatch;
    IF_OF_LatchType IF_OF_Latch;
    EX_MA_LatchType EX_MA_Latch;
    MA_RW_LatchType MA_RW_Latch;

    OF_EX_LatchType OF_EX_Latch;

    Instruction finalInstruction;

    public Instruction getFinalInstruction() {
        return finalInstruction;
    }

    public void setFinalInstruction(Instruction finalInstruction) {
        this.finalInstruction = finalInstruction;
    }
    
    public DataInterlock(Processor containingProcessor, IF_EnableLatchType IF_EnableLatch,
            IF_OF_LatchType IF_OF_Latch, EX_MA_LatchType EX_MA_Latch, MA_RW_LatchType MA_RW_Latch,OF_EX_LatchType OF_EX_Latch) {
        this.containingProcessor = containingProcessor;
        this.IF_EnableLatch = IF_EnableLatch;
        this.IF_OF_Latch = IF_OF_Latch;
        this.EX_MA_Latch = EX_MA_Latch;
        this.MA_RW_Latch = MA_RW_Latch;
        this.OF_EX_Latch = OF_EX_Latch;
    }

   
    public void checkConflict() {

        Instruction currentInstruction = getInstruction();

        Instruction instructionInEX = OF_EX_Latch.getInstruction();
        Instruction instructionInMA = EX_MA_Latch.getInstruction();
        Instruction instructionInRW = MA_RW_Latch.getInstruction();

        
        if ((EX_MA_Latch.isValidInst() && instructionInMA != null && hasConflict(currentInstruction, instructionInMA))
                || (MA_RW_Latch.isValidInstruction() && instructionInRW != null && hasConflict(currentInstruction, instructionInRW))
                || (OF_EX_Latch.isValidInstruction() && instructionInEX != null && hasConflict(currentInstruction, instructionInEX))
        ||(hasConflict(currentInstruction, finalInstruction))){

            IF_EnableLatch.setIsIFStagestall(true);
            IF_OF_Latch.setStall(true);

        } else { 
            IF_EnableLatch.setIsIFStagestall(false);
            IF_OF_Latch.setStall(false);
        }
    }

    
    
    private boolean hasConflict(Instruction A, Instruction B) {
       
        if (A == null || B == null) {
            return false;
        }

       
        switch (A.getOperationType()) {
            case jmp:
            case end:
                return false;
            default:
        }

        Integer sourceRegister1A = A.getSourceOperand1().getValue();
        Integer sourceRegister2A = A.getSourceOperand2().getValue();
        Integer sourceRegister2A_copy = sourceRegister2A;
        Boolean isSecondImmediate =
                (A.getSourceOperand2().getOperandType() == OperandType.valueOf("Immediate"));
        Integer destinationRegisterA = A.getDestinationOperand().getValue();


        if (A.getOperationType() == OperationType.valueOf("store")) {
            sourceRegister2A_copy = destinationRegisterA;
            isSecondImmediate = false;
        }

        
        
        if (sourceRegister1A == 31 || sourceRegister2A_copy == 31) {
            return true;
        }

        
        switch (B.getOperationType()) {
            case jmp:
            case end:
                return false;
            default:
        }

        int sourceRegister2B = B.getSourceOperand2().getValue();
        int destinationRegisterB = B.getDestinationOperand().getValue();

        
        switch (B.getOperationType()) {
            case bne:
            case blt:
            case beq:
            case bgt:
                return false;
            default:
        }

        
        
        if (A.getOperationType() == OperationType.valueOf("load")
                && B.getOperationType() == OperationType.valueOf("store")) {
            int address1 = containingProcessor.getRegisterFile().getValue(sourceRegister1A) + sourceRegister2A;
            int address2 = containingProcessor.getRegisterFile().getValue(destinationRegisterB) + sourceRegister2B;
            if (address1 == address2) {
                return true;
            }
        }

        
        if (B.getOperationType() == OperationType.valueOf("store")) {
            return false;
        }

        
        if (sourceRegister1A == destinationRegisterB || (!isSecondImmediate && sourceRegister2A_copy == destinationRegisterB)) {
            return true;
        }

        return false;
    }

    private Instruction getInstruction() {
        
        String instruction = addPaddingToStart(Integer.toBinaryString(IF_OF_Latch.getInstruction()), 32);

        Instruction newInstruction = new Instruction();

        
        newInstruction.setOperationType(
                OperationType.values()[binaryToDecimal(instruction.substring(0, 5), false)]);

        
        switch (newInstruction.getOperationType()) {
            
            case add:
            case sub:
            case mul:
            case div:
            case and:
            case or:
            case xor:
            case slt:
            case sll:
            case srl:
            case sra: {
                
                newInstruction.setSourceOperand1(getRegisterOperand(instruction.substring(5, 10)));
                
                newInstruction.setSourceOperand2(getRegisterOperand(instruction.substring(10, 15)));
                
                newInstruction.setDestinationOperand(getRegisterOperand(instruction.substring(15, 20)));
                break;
            }

            
            case addi:
            case subi:
            case muli:
            case divi:
            case andi:
            case ori:
            case xori:
            case slti:
            case slli:
            case srli:
            case srai:
            case load:
            case store: {
                
                newInstruction.setSourceOperand1(getRegisterOperand(instruction.substring(5, 10)));
                
                newInstruction.setDestinationOperand(getRegisterOperand(instruction.substring(10, 15)));
                
                newInstruction.setSourceOperand2(getImmediateOperand(instruction.substring(15, 32)));
                break;
            }

            case beq:
            case bne:
            case blt:
            case bgt: {
                
                newInstruction.setSourceOperand1(getRegisterOperand(instruction.substring(5, 10)));
                
                newInstruction.setSourceOperand2(getRegisterOperand(instruction.substring(10, 15)));
                
                newInstruction.setDestinationOperand(getImmediateOperand(instruction.substring(15, 32)));
                break;
            }

            
            case jmp: {
                if (binaryToDecimal(instruction.substring(5, 10), false) != 0) {
                    
                    newInstruction.setDestinationOperand(getRegisterOperand(instruction.substring(5, 10)));
                } else { 
                    
                    newInstruction.setDestinationOperand(getImmediateOperand(instruction.substring(10, 32)));
                }
                break;
            }

            case end:
                break;

            default:
                Misc.printErrorAndExit("Unknown Instruction!!");
        }

        return newInstruction;
    }

    
    private String addPaddingToStart(String str, int totalLength) {
        if (str.length() >= totalLength) { 
            return str;
        }
        int count = 0;
        String ans = "";
        while (count < totalLength - str.length()) { 
            ans += '0';
            ++count;
        }
        ans += str; 
        return ans;
    }

    
    private int binaryToDecimal(String binaryString, boolean isSigned) {
        if (!isSigned) {
            return Integer.parseInt(binaryString, 2); 
        } else {
            String copyString = '0' + binaryString.substring(1); 
            int ans = Integer.parseInt(copyString, 2); 
                                                       

            
            if (binaryString.length() == 32) {
                if (binaryString.charAt(0) == '1') { 
                                                     

                    int power = (1 << 30); 
                    
                    ans -= power;
                    ans -= power;
                }
            } else {
                if (binaryString.charAt(0) == '1') { 
                                                     
                    int power = (1 << (binaryString.length() - 1));
                    ans -= power;
                }
            }

            return ans;
        }
    }

    
    
    private Operand getRegisterOperand(String val) {
        Operand operand = new Operand(); 
        operand.setOperandType(OperandType.Register); 
        operand.setValue(binaryToDecimal(val, false)); 
        return operand;
    }

    
    
    private Operand getImmediateOperand(String val) {
        Operand operand = new Operand(); 
        operand.setOperandType(OperandType.Immediate); 
        operand.setValue(binaryToDecimal(val, true)); 
        return operand;
    }

}
