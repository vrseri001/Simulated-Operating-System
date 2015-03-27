import java.io.File;
import java.util.LinkedList;

/**
 * Created by ErinV on 2015-03-25.
 */
public class SimulatedKernel implements Kernel {

    //programs to be executed
    protected LinkedList<ProcessControlBlock> readyQueue = new LinkedList<ProcessControlBlock>();

    //device q
    protected LinkedList<Device> devices = new LinkedList<Device>();

    //dispatch overhead to be added to cost of call in context switching situations
    private int dispatchOverhead;

    private long timeSpentInKernel = 0;
    private int currentPID = 0;
    private int numberOfSystemCallsMade = 0;

    public SimulatedKernel(int dispatchOverhead){

        this.dispatchOverhead = dispatchOverhead;
    }

    /**
     * Create a device. Device ID and name are required arguments.
     */
    final static int MAKE_DEVICE = 1;

    /**
     * Execute a program. Program name is a required argument.
     * Process ID is returned when the call completes.
     */
    final static int EXECVE = 2;

    /**
     * Perform IO request. Device ID is a required argument.
     */
    final static int IO_REQUEST = 3;

    /**
     * Terminate current process.
     */
    final static int TERMINATE_PROCESS = 4;


    /**
     *  Invoke the system call with the given number, providing zero or more arguments.
     *  @return -1 for failure, 0 for completion
     */
    public int syscall(int number, Object... varargs){

        numberOfSystemCallsMade++;
        //MAKE_DEVICE system call
        if (number==1){
            Device device = new Device((Integer) varargs[0],(String) varargs[1]);
            //index them according to their IDs
            devices.add((Integer)varargs[0], device);
            return 0;
        }

        //EXECVE system call: load program
        else if(number == 2){

            //make a new PCB
            SimulatedProcessControlBlock processControlBlock = new SimulatedProcessControlBlock((String) varargs[0], currentPID);
            currentPID++;

            //put it onto the ready queue
            readyQueue.add(processControlBlock);

            //the id of the processControlBlock just added to the readyQueue
            return processControlBlock.getPID();

        }

        //IO_REQUEST system call: simulate an IO request on given device for given duration
        else if(number==3){
            //next available process switched from the ready queue onto the CPU
            return readyQueue.peek().getPID();
        }

        return -1;
    }

    /**
     * Identifies a time_out interrupt.
     * Optionally, a call to the <code>interrupt</code> with this code,  may provide
     * the ID of the process to be preempted - should correspond to that currently executing!
     */
    final static int TIME_OUT = 0;

    /**
     * Identifies completion of a device I/O request.
     * Device ID and Process ID are required parameters.
     */
    final static int WAKE_UP = 1;

    /**
     * Invoke the interrupt handler, providing the interrupt type and zero or more arguments.
     */
    public void interrupt(int interruptType, Object... varargs){

    }
}
