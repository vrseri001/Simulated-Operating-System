import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by ErinV on 2015-03-25.
 */
public class SimulatedProcessControlBlock implements ProcessControlBlock {

    private LinkedList<Instruction> instructions = new LinkedList<Instruction>();
    private Instruction currInstr;
    private int numberOfInstructions = 0;
    protected int programCounter = 0;
    private int PID;
    private String programName;

    public SimulatedProcessControlBlock(String fileName, int PID){
        this.PID = PID;
        programName = fileName;

        try{
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            //read program lines and device lines in
            while (scanner.hasNextLine()){
                String str = scanner.nextLine();
                String[] temp = str.split(" ");
                try{
                    //check if it's a cpu or io instruction
                    if(temp[0].startsWith("#")){
                        continue;
                    }

                    //case: create a CPU instruction
                    else if(temp[0].equals("CPU")){
                        CPUInstruction cpuInstru = new CPUInstruction(Integer.parseInt(temp[1]));
                    }

                    //case: create a IO instruction
                    else if(temp[0].equals("IO")||temp[1].equals("DEVICE")){
                        IOInstruction ioInstruc = new IOInstruction(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
                    }
                    else {
                        System.out.print("SimulatedProcessControlBlock constructor: unidentified instruction");
                    }

                    SimulatedInstruction instr = new SimulatedInstruction(Integer.parseInt(temp[1]));
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
        return programName;
    }

    @Override
    public Instruction getInstruction() {
        Instruction instruction = instructions.peek();
        return instruction;
    }

    @Override
    public void nextInstruction() {
        programCounter++;
        numberOfInstructions--;
    }

    public Instruction getNextInstruction(){
        if(instructions.size()==1){
            return null;
        }
        Instruction nextInstruc = instructions.get(1);
        return nextInstruc;
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void setState(State state) {

    }
    public String toString() {
        return String.format("{%d, %s}", this.getPID(), this.getProgramName());
    }
}
