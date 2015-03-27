/**
 * Created by ErinV on 2015-03-26.
 */
public class SimulatedInstruction extends Instruction {

    private int duration;

    /**
     * Create an instruction of the given duration for the given process.
     */
    public SimulatedInstruction(int duration) {
        super(duration);
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }
}
