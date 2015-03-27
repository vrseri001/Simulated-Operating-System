import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by ErinV on 2015-03-25.
 */
public class SimulatedProcessControlBlock implements ProcessControlBlock {

    private LinkedList<Instruction> instructions;
    private int numberOfInstructions = 0;
    private int programCounter = 0;
    private int PID;

    public SimulatedProcessControlBlock(String fileName, int PID){
        this.PID = PID;

        try{
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            //read program lines and device lines in
            while (scanner.hasNextLine()){
                String str = scanner.nextLine();
                String[] temp = str.split(" ");
                try{
                    SimulatedInstruction instr = new SimulatedInstruction(Integer.parseInt(temp[1]), this);
                    instructions.add(instr);
                    numberOfInstructions++;
                }
                catch (Exception e){
                    System.out.print("SimulatedProcessControlBlock: constructor: reading in instructions: "+e);
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            System.out.print("SimulatedProcessControlBlock: constructor: "+e);
            e.printStackTrace();
        }

    }

    @Override
    public int getPID() {
        return PID;
    }

    public int getNumberOfInstructions(){
        return numberOfInstructions;
    }
    @Override
    public String getProgramName() {
        return null;
    }

    @Override
    public Instruction getInstruction() {
        Instruction instruction = instructions.poll();
        Instruction result = instruction;
        instructions.add(instruction);
        return result;
    }

    @Override
    public void nextInstruction() {
        programCounter++;
        numberOfInstructions--;
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void setState(State state) {

    }
}
