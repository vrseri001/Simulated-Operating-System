/**
 * Created by ErinV on 2015-03-24.
 */
public class SimulatedKernel implements Kernel {

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

    public SimulatedKernel() {
    }

    /**
     * Invoke the system call with the given number, providing zero or more arguments.
     */
    public int syscall(int number, Object... varargs) {
        //case: MAKE_DEVICE
        if (number == 1) {
            print("Make device case running");
            //create an object to represent the device

        }

        //case: EXECVE
        //create a device object and IO queue
        else if (number == 2) {
            print("Execve case running");
        }

        //case: IO_REQUEST
        //simulate a IO request on the given device for the given duration
        else if (number == 3) {
            print("IO request case running");

        }

        //case: TERMINATE_PROCESS
        //terminate execution of the current process
        else if (number == 4) {
            print("Terminate process case running");

        }

        //change this value later
        return 1;
    }

    private void print(String str) {
        System.out.println(str);
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
    public void interrupt(int interruptType, Object... varargs) {

        //case: TIME_OUT
        //used to signal the end of a scheduled time slice
        if(interruptType==0){
            print("Time out case running");
        }

        //case: WAKE_UP
        //used to signal completion of the end of an IO request
        else if(interruptType==1){
            print("Wake up case running");
        }
    }
}
