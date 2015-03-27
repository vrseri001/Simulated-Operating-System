/**
 * Created by ErinV on 2015-03-25.
 */
public class SimulatedSystemTimer implements SystemTimer {

    private long kernelTime;
    private long idleTime;
    private long userTime;
    private long systemTime;

    public SimulatedSystemTimer(){

        systemTime = 0;
    }

    /**
     * Time cost of a system call.
     */
    final static int SYSCALL_COST = 2;

    /**
     * Obtain the current system time.
     */
    public long getSystemTime(){
        return systemTime;
    }

    /**
     * Obtain the amount of time the CPU has been idle.
     */
    public long getIdleTime(){
        return idleTime;
    }

    /**
     * Obtain the amount of time the system has spent executing in user space.
     */
    public long getUserTime(){
        return userTime;
    }

    /**
     * Obtain the amount of time the system has spent executing in kernel space.
     */
    public long getKernelTime(){
        return kernelTime;
    }

    /**
     * Set the current system time.
     */
    public void setSystemTime(long systemTime){

    }

    /**
     * Advance system time by the given amount.
     */
    public void advanceSystemTime(long time){

    }

    /**
     * Advance user time and system time by the given amount.
     */
    public void advanceUserTime(long time){

    }

    /**
     * Advance kernel time and system time by the given amount.
     */
    public void advanceKernelTime(long time){
        kernelTime+=time;
        systemTime+=time;
    }

    /**
     * Schedule a timer interrupt for <code>timeUnits</code> time units in the future
     * for the given pricess.
     */
    public void scheduleInterrupt(int timeUnits, int processID){

    }

    /**
     * Cancel timer interrupt for the given process.
     */
    public void cancelInterrupt(int processID){
    }
}
