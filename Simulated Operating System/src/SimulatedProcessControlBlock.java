import java.util.LinkedList;

/**
 * Created by ErinV on 2015-03-24.
 */
public class SimulatedProcessControlBlock implements ProcessControlBlock{

    private int PID;
    private String programName;
    private LinkedList<Instruction> instructions = new LinkedList<Instruction>();
    //private enum  state;

    /**
     * Possible process states.
     */
    enum State { WAITING, READY, RUNNING, TERMINATED };

    /**
     * Obtain process ID.
     */
    public int getPID(){
        return PID;
    }

    /**
     * Obtain program name.
     *
     */
    public String getProgramName(){
        return programName;
    }


    /**
     * Obtain current program 'instruction'.
     */
    public Instruction getInstruction(){
        return instructions.peek();
    }


    /**
     * Advance to next instruction by getting rid of current one.
     */
    public void nextInstruction(){
        instructions.peek();
    }

    /**
     * Obtain process state.
     */
    public State getState(){
      //  return state;
    }

    /**
     * Set process state.
     * Requires <code>getState()!=State.TERMINATED</code>.
     */
    public void setState(State state){
       // this.state = state;
    }
}
