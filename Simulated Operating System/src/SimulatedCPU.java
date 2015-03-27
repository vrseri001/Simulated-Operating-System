/**
 * Created by ErinV on 2015-03-25.
 */
public class SimulatedCPU implements CPU {

    private int contextSwitches = 0;

    @Override
    public ProcessControlBlock getCurrentProcess() {
        return null;
    }

    @Override
    public int execute(long  timeUnits) {
        return 0;
    }

    @Override
    public ProcessControlBlock contextSwitch(ProcessControlBlock process) {
        contextSwitches++;
        return null;
    }

    @Override
    public boolean isIdle() {
        return false;
    }

    @Override
    public int getNumberOfContextSwitches(){
        return contextSwitches;
    }
}