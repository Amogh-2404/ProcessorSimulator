package processor.interlocks;

import processor.pipeline.*;
import processor.Processor;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Misc;

// DataInterlock class to implement the functionality of Data-Interlock
public class DataInterlock {

    Instruction instructionThrownOutOfRW;

    // Required variables
    Processor containingProcessor;
    IF_EnableLatchType IF_EnableLatch;
    IF_OF_LatchType IF_OF_Latch;
    EX_MA_LatchType EX_MA_Latch;
    MA_RW_LatchType MA_RW_Latch;

    OF_EX_LatchType OF_EX_Latch;

    // Constructor
    public DataInterlock(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch,
                         IF_OF_LatchType iF_OF_Latch, EX_MA_LatchType EXMAlatch, MA_RW_LatchType MARWlatch, OF_EX_LatchType OFEXlatch) {
        this.containingProcessor = containingProcessor;
        this.IF_EnableLatch = iF_EnableLatch;
        this.IF_OF_Latch = iF_OF_Latch;
        this.EX_MA_Latch = EXMAlatch;
        this.MA_RW_Latch = MARWlatch;
        this.OF_EX_Latch = OFEXlatch;
    }
    public Instruction getFinalInstruction(){
        return instructionThrownOutOfRW;
    }
    public void setFinalInstruction(Instruction ins){
        instructionThrownOutOfRW = ins;
    }
    private Instruction getInstruction() {

        String inst = addPaddingToStart(Integer.toBinaryString(IF_OF_Latch.getInstruction()), 32);

        Instruction newInstruction = new Instruction(); // Making an object of Instruction class


        newInstruction.setOperationType(
                OperationType.values()[convertDecimalToBinary(inst.substring(0, 5), false)]);


        switch (newInstruction.getOperationType()) {
            // R3I type
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
                // rs1
                newInstruction.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
                // rs2
                newInstruction.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
                // rd
                newInstruction.setDestinationOperand(getRegisterOperand(inst.substring(15, 20)));
                break;
            }

            // R2I type
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
                // rs1
                newInstruction.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
                // rd
                newInstruction.setDestinationOperand(getRegisterOperand(inst.substring(10, 15)));
                // imm
                newInstruction.setSourceOperand2(getImmediateOperand(inst.substring(15, 32)));
                break;
            }

            case beq:
            case bne:
            case blt:
            case bgt: {
                // rs1
                newInstruction.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
                // rd
                newInstruction.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
                // imm
                newInstruction.setDestinationOperand(getImmediateOperand(inst.substring(15, 32)));
                break;
            }

            // RI type :
            case jmp: {
                if (convertDecimalToBinary(inst.substring(5, 10), false) != 0) { // if rd is used
                    // rd
                    newInstruction.setDestinationOperand(getRegisterOperand(inst.substring(5, 10)));
                } else { // else imm is used
                    // imm
                    newInstruction.setDestinationOperand(getImmediateOperand(inst.substring(10, 32)));
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

    public void checkConflict() {
        // Checking

        Instruction currInst = getInstruction();
        Instruction instructionInMA = EX_MA_Latch.getInstruction();
        Instruction instructionInRW = MA_RW_Latch.getInstruction();
        Instruction instructionInEX = OF_EX_Latch.getInstruction();


        if (hasConflict(currInst, instructionInMA) || hasConflict(currInst, instructionInRW) || hasConflict(currInst, instructionInEX )|| hasConflict(currInst, instructionThrownOutOfRW)) {
            IF_EnableLatch.setStall(true);
            IF_OF_Latch.setStall(true);

        }
        else{
            IF_EnableLatch.setStall(false);
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

        int sourceRegisterOneA = A.getSourceOperand1().getValue();
        int sourceRegisterTwoA = A.getSourceOperand2().getValue();
        int destinationRegisterA = A.getDestinationOperand().getValue();
        boolean isSecondImmediate =
                (A.getSourceOperand2().getOperandType() == OperandType.valueOf("Immediate"));
        int second = sourceRegisterTwoA;

        if (A.getOperationType() == OperationType.valueOf("store")) {
            second = destinationRegisterA;
            isSecondImmediate = false;
        }


        if (sourceRegisterOneA == 31 || second == 31) {
            return true;
        }


        switch (B.getOperationType()) {
            case jmp:
            case end:
                return false;
            default:
        }

        int sourceRegisterTwoB = B.getSourceOperand2().getValue();
        int destinationRegisterB = B.getDestinationOperand().getValue();

        switch (B.getOperationType()) {
            case beq:
            case bne:
            case blt:
            case bgt:
                return false;
            default:
        }


        if (A.getOperationType() == OperationType.valueOf("load")
                && B.getOperationType() == OperationType.valueOf("store")) {
            int addressOne = containingProcessor.getRegisterFile().getValue(sourceRegisterOneA) + sourceRegisterTwoB;
            int addressTwo = containingProcessor.getRegisterFile().getValue(destinationRegisterB) + sourceRegisterTwoB;
            if (addressOne == addressTwo) {
                return true;
            }
        }


        if (B.getOperationType() == OperationType.valueOf("store")) {
            return false;
        }

        // If Following condition holds true, then there is a conflict
        if (sourceRegisterOneA == destinationRegisterB || (!isSecondImmediate && second == destinationRegisterB)) {
            return true;
        }

        return false;
    }

    private Operand getRegisterOperand(String val) {
        Operand operand = new Operand(); // Making a new operand
        operand.setOperandType(OperandType.Register); // setting operand type as Register
        operand.setValue(convertDecimalToBinary(val, false)); // setting its value
        return operand;
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


    private int convertDecimalToBinary(String binaryString, boolean isSigned) {
        if (!isSigned) {
            return Integer.parseInt(binaryString, 2);
        } else {
            String copyString = '0' + binaryString.substring(1);
            int answer = Integer.parseInt(copyString, 2);


            if (binaryString.length() == 32) {
                if (binaryString.charAt(0) == '1') {

                    int power = (1 << 30);
                    answer -= power;
                    answer -= power;
                }
            } else {
                if (binaryString.charAt(0) == '1') {
                    int power = (1 << (binaryString.length() - 1));
                    answer -= power;
                }
            }

            return answer;
        }
    }




    private Operand getImmediateOperand(String val) {
        Operand operand = new Operand(); // Making a new operand
        operand.setOperandType(OperandType.Immediate); // setting operand type as Register
        operand.setValue(convertDecimalToBinary(val, true)); // setting its value
        return operand;
    }

}
