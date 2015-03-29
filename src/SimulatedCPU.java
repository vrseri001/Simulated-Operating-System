/**
 * Created by ErinV on 2015-03-25.
 */
public class SimulatedCPU implements CPU {

    //advance systemTimer by product of these at end of execution
    private int contextSwitches = 0;
    private int dispatchOverhead;

    private ProcessControlBlock currentPCB = null;
    private Instruction currInstruc = null;

    private SimulatedSystemTimer systemTimer;

    public SimulatedCPU(SimulatedSystemTimer systemTimer, int dispatchOverhead){
        this.systemTimer = systemTimer;
        this.dispatchOverhead = dispatchOverhead;
    }

    @Override
    public ProcessControlBlock getCurrentProcess() {
        return currentPCB;
    }

    /**
     * Gets called from the Simulator
     * IOInstruction: requestIO from device
     * @param timeUnits amount of time allocated to execution
     * @return
     */
    @Override
    public int execute(int  timeUnits) {
        int timeRemaining = timeUnits;
        currInstruc = currentPCB.getInstruction();

        if(currInstruc instanceof CPUInstruction){
            //returns the timeRemaining to execute on this instruction
            timeRemaining = ((CPUInstruction) currInstruc).execute(timeUnits);

            while(timeRemaining>0){
                //execute the the next instruction
                currentPCB.nextInstruction();
                currInstruc = currentPCB.getInstruction();

                if(currInstruc instanceof CPUInstruction){
                    timeRemaining = ((CPUInstruction)currInstruc).execute(timeRemaining);
                }

                else if(currInstruc instanceof IOInstruction){
                    systemTimer.scheduleInterrupt((timeRemaining)*(systemTimer.eventQueue.size()),currentPCB.getPID() );
                }
            }
        }

        else if(currInstruc instanceof IOInstruction){
            systemTimer.scheduleInterrupt((timeRemaining)*(systemTimer.eventQueue.size()), currentPCB.getPID());
        }

        return timeRemaining;
    }

    @Override
    public ProcessControlBlock contextSwitch(ProcessControlBlock process) {
        contextSwitches++;
        ProcessControlBlock pcb = currentPCB;
        currentPCB = process;
        return pcb;
    }

    public void removeCurrentProcess(){
        currentPCB = null;
    }


    public Instruction getNextInstruction(){
        Instruction nextInstruc = currentPCB.getNextInstruction();
        return nextInstruc;
    }


    @Override
    public boolean isIdle() {

        if(getCurrentProcess() == null){
            return true;
        }
        return false;
    }

    @Override
    public int getNumberOfContextSwitches(){
        return contextSwitches;
    }
}