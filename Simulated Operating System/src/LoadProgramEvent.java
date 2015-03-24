/**
 * Class used to indicate that an EXECVE system call
 * must be made to the kernel to load a program
 */
public class LoadProgramEvent extends Event {

    private long startTime;
    private String fileName;

    /**
     * Constructor function
     * @param systemTime system time upon creation of the object
     * @param dispatchOverhead time taken to load event onto CPU
     * @param fileName name of file to be loaded onto the CPU
     */
    public LoadProgramEvent(long systemTime, int dispatchOverhead, String fileName){
        super(systemTime);
        startTime = systemTime + dispatchOverhead;
        this.fileName = fileName;
    }
}
