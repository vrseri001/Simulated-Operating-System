
/**
 * Abstract representation of program instruction. 
 * 
 * @author Stephan Jamieson
 * @version 8/3/15
 */
public abstract class Instruction {

    private int duration;
    
    /**
     * Create an instruction of the given duration for the given process.
     */
    public Instruction(int duration) {
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }
    
    
}
