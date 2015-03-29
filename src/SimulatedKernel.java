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

    //parameters for RR scheduling
    //dispatch overhead to be added to cost of call in context switching situations
    private int dispatchOverhead;
    protected int sliceLength;

    private int contextSwitches = 0;

    private int currentPID = 0;
    protected SimulatedCPU cpu;
    protected SimulatedSystemTimer systemTimer;
    protected EventQueue eventQueue;
    private int count = 0;

    public SimulatedKernel(int slice, int dispatchOverhead, EventQueue eventQueue, SimulatedSystemTimer systemTimer, SimulatedCPU cpu){

        this.eventQueue = eventQueue;
        this.systemTimer = systemTimer;
        this.cpu = cpu;
        this.dispatchOverhead = dispatchOverhead;
        this.sliceLength = slice;
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

        //MAKE_DEVICE system call
        if (number==1){
            Device device = new Device(/*device id*/(Integer) varargs[0],/*device type*/(String) varargs[1]);
            //index them according to their IDs
            devices.add((Integer) varargs[0], device);
            sysCallTrace(1, varargs[0], varargs[1]);
            systemTimer.advanceKernelTime(2);
            return 0;
        }

        //EXECVE system call: load program
        else if(number == 2){

            print("");
            print("         Execve system call running");

            //make a new PCB
            SimulatedProcessControlBlock processControlBlock = new SimulatedProcessControlBlock((String) varargs[0], currentPID);
            processControlBlock.getProgramName();
            currentPID++;

            readyQueue.add(processControlBlock);
            print("         ready queue isEmpty() is: "+readyQueue.isEmpty());
            print("         cpu isIdle() is:"+cpu.isIdle()+"");
            print("         "+readyQueue.peek().toString());


            systemTimer.advanceKernelTime(2);
            TimeOutEvent timeOutEvent = new TimeOutEvent(systemTimer.getSystemTime()+sliceLength, processControlBlock.getPID());
            eventQueue.add(timeOutEvent);
            print("program loaded and timeout event added to readyQ. eventQ size should be 2 and is: "+eventQueue.size());
            print("Current system time: "+systemTimer.getSystemTime());
            print("Timeout details: scheduled to occur at: "+systemTimer.getSystemTime()+sliceLength+"; scheduled to timeout event number: "+processControlBlock.getPID());
            sysCallTrace(2, varargs[0]);

            print("         Execve system call ended");
            print("");

            return 0;

        }

        //IO_REQUEST system call: simulate an IO request on given device for given duration
        else if(number==3){

            int deviceID = (Integer)varargs[0];
            int duration = (Integer)varargs[1];
            String program = cpu.getCurrentProcess().getProgramName();

            //remove currently executing process from CPU and place on relevant device queue
            ProcessControlBlock pcb = cpu.getCurrentProcess();
            Device requestedDevice = devices.get(deviceID);
            requestedDevice.addProcessControlBlockToQueue(pcb);

            //timeout scheduled to mark end of current time slice cancelled
            eventQueue.cancelEvent(pcb.getPID());

            long endTime = systemTimer.getSystemTime()+duration;
            //schedule wake up
            WakeUpEvent wakeUp = new WakeUpEvent(endTime, requestedDevice, pcb);

            cpu.contextSwitch(readyQueue.poll());
            TimeOutEvent timeOutEvent = new TimeOutEvent(systemTimer.getSystemTime()+sliceLength, pcb.getPID());
            eventQueue.add(timeOutEvent);

            sysCallTrace(3, program, deviceID);

            systemTimer.advanceKernelTime(2);
            return 0;
        }

        //case: TERMINATE current process
        else if(number == 4){

            int pid = cpu.getCurrentProcess().getPID();
            String name = cpu.getCurrentProcess().toString();
            //currently executing process is removed from the CPU and discarded
            cpu.removeCurrentProcess();
            //cancel the timeout event for this time slice
            eventQueue.cancelEvent(pid);

            ProcessControlBlock pcb = readyQueue.poll();
            cpu.contextSwitch(pcb);

            TimeOutEvent timeOutEvent = new TimeOutEvent(systemTimer.getSystemTime()+sliceLength, pcb.getPID());

            sysCallTrace(4);

            systemTimer.advanceKernelTime(2);
            return 0;
        }

        return -1;
    }

    public void initialiseCPU(){

        //include tracking of switches in ReadMe
        cpu.contextSwitch(readyQueue.poll());

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

        //case: TimeOut interrupt
        if(interruptType==0){

            //currently executed process is removed from the CPU and placed at the back of the read queue
            ProcessControlBlock pcb = cpu.getCurrentProcess();
            readyQueue.add(cpu.getCurrentProcess());
            //increment context switch. Note in ReadMe that context switches are measured both ways, so
            //multiple occur for some events
            contextSwitches++;

            //next available process switched onto CPU
            cpu.contextSwitch(readyQueue.poll());

            TimeOutEvent timeOutEvent = new TimeOutEvent(systemTimer.getSystemTime()+sliceLength, cpu.getCurrentProcess().getPID());
            eventQueue.add(timeOutEvent);

            interruptTrace(0, pcb);
        }

        //case: wake up interrupt
        else if(interruptType == 1){

            Device wokenDevice = devices.get(/*retrieve current device using id*/(Integer)varargs[1]);

            //remove the specific process from the queue on the device
            SimulatedProcessControlBlock pcb = (SimulatedProcessControlBlock) wokenDevice.removePCB((Integer)varargs[2]);

            pcb.nextInstruction();
            readyQueue.add(pcb);

            interruptTrace(1, varargs[1], varargs[2]);

        }
    }

    public void sysCallTrace(int number, Object... varargs) {
        String details=null;
        switch (number) {
            case MAKE_DEVICE:
                details=String.format("MAKE_DEVICE, %s,\"%s\"", varargs[0], varargs[1]);
                break;
            case EXECVE:
                details=String.format("EXECVE, \"%s\"", varargs[0]);
                break;
            case IO_REQUEST:
                details=String.format("IO_REQUEST, %s, %s", varargs[0], varargs[1]);
                break;
            case TERMINATE_PROCESS:
                details="TERMINATE_PROCESS";
                break;
            default:
                details="ERROR_UNKNOWN_NUMBER";
        }
        System.out.printf("Time: %010d SysCall(%s)\n", systemTimer.getSystemTime(), details);
    }


    public void interruptTrace(int interruptType, Object... varargs) {
        String details = null;
        switch (interruptType) {
            case TIME_OUT:
                details=String.format("TIME_OUT, %s", varargs[0]);
                break;
            case WAKE_UP:
                details=String.format("WAKE_UP, %s, %s", varargs[0], varargs[1]);
                break;
            default:
                details="ERROR_UNKNOWN_NUMBER";
        }
        System.out.printf("Time: %010d Interrupt(%s)\n", systemTimer.getSystemTime(), details);
    }

    private void print(String string){
        System.out.println(string);
    }

    public void output(String configFile){
        print("System time: "+systemTimer.getSystemTime());
        print("Kernel time: "+systemTimer.getKernelTime());
        print("User time: "+systemTimer.getUserTime());
        print("Idle time: "+systemTimer.getIdleTime());
        print("Context switches: "+cpu.getNumberOfContextSwitches());
        print("CPU Utilisation: "+(systemTimer.getUserTime())/(systemTimer.getIdleTime()+systemTimer.getUserTime()+dispatchOverhead*cpu.getNumberOfContextSwitches()));
    }
}
