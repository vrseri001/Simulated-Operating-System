
/**
 * A timeout event is used to flag the end of the current execution time slice.
 * 
 * @author Stephan Jamieson
 * @version 8/3/15
 */
public class TimeOutEvent extends Event {

    protected long startTime;
    protected int processID;
    /**
     * Create a TimeOut event to mark the end of the execution timeslice for the given process.
     */
    public TimeOutEvent(long systemTime, int processID) {
        super(systemTime);
        startTime = systemTime;
        this.processID = processID;
    }
    
    /**
     * Obtain the process to switched out as a result of this execution timeout.
     */
    public int getProcessID() { return processID; }

    public long getStartTime() {
        return startTime;
    }
        
}
