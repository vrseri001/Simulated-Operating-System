import java.io.File;
import java.lang.System;
import java.util.*;

/**
 * Created by ErinV on 2015-03-23.
 */
public class Simulator {

    //events queue
    private static EventQueue eventQueue = new EventQueue();

    //kernel to simulate process scheduling
    private static SimulatedKernel kernel;
    private static SimulatedCPU cpu;

    private static int sliceLength;
    private static String programFile;
    //time tracker
    private static SimulatedSystemTimer systemTimer;


    public static void main(String[] args) {

        loadConfig(args[0]);

        sliceLength = Integer.parseInt(args[1]);
        int dispatchOverhead = Integer.parseInt(args[2]);
        systemTimer = new SimulatedSystemTimer(eventQueue);

        cpu = new SimulatedCPU(systemTimer, dispatchOverhead);
        kernel = new SimulatedKernel(sliceLength, dispatchOverhead, eventQueue, systemTimer, cpu);

        beginMainSimulationLoop();

        kernel.output(args[0]);

    }
    
    private static void print(String string){
        System.out.println(string);
    }

    private static void loadConfig(String configName){
        try {
            //open the configuration file and read data in
            File file = new File(configName);
            Scanner scanner = new Scanner(file);

            //read program lines and device lines in
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                //case: line is a comment
                if(line.startsWith("#")){
                    //ignore this line
                }

                else if(line.startsWith("I/O") || line.startsWith("DEVICE")){
                    String temp = line.substring(7);

                    //split string into integers and other chars
                    String[] info = temp.split(" ");

                    //make a system call to the kernel to create a device
                    int success = kernel.syscall(1, Integer.parseInt(info[0]), info[1]);
                    print("Simulator: loadConfig: successful device creation");

                }

                //case: line describes program information
                else if(line.startsWith("PROGRAM")){
                    String temp = line.substring(8);

                    //split string into integers and other chars
                    String[] info = temp.split(" ");

                    //create an event to load the program and add it to the queue
                    ExecveEvent execveEvent = new ExecveEvent(Long.parseLong(info[0]), info[1]);
                    eventQueue.add(execveEvent);                }

                //case: no other case condition met. provide feedback to that effect
                else {

                    print("Simulator: set up: try/ while/ if error");
                    print("Invalid line found: "+line);

                }

            }
        } catch (Exception e) {
            print("Simulator: set up: error: " + e);
            e.printStackTrace();
        }
    }


    private static void beginMainSimulationLoop(){

        int count =0;

        //until eventQueue.isEmpty() return true AND AT THE SAME TIME cpu.isIdle() returns true
        while( /*!(eventQueue.isEmpty() && kernel.cpu.isIdle()) */ count<4 ){
            count++;
            print("run through outer while "+count+" times.");
            //on first run, cpu has not yet been initialised, and but events have already been added to the event queue
            //the following print statements should therefore be false and true respectively
            print("eventQueue empty state is "+eventQueue.isEmpty());
            print("cpu idle state is "+kernel.cpu.isIdle());
            print("current system time is "+systemTimer.getSystemTime());
            //if the event queue contains
            while( (!eventQueue.isEmpty()) && (eventQueue.peek().getTime()<=systemTimer.getSystemTime())) {
                print("     Inner while loop executing");
                print("     eventQueue is empty state is "+eventQueue.isEmpty());
                print("     current process is "+eventQueue.peek().toString()+" andtime at which to execute is "+eventQueue.peek().getTime());
                print("     current system time is "+systemTimer.getSystemTime());

                //process the event
                //get the current event off the top of the eventQueue
                Event currentEvent = eventQueue.poll();

                //case: current event is a load program event, otherwise called an Execve event
                if (currentEvent instanceof ExecveEvent) {
                    print("     Execve event loaded off. Initiating system call.");
                    //create an execve system call, which returns the time taken to read in the program instructions
                    int exitStatus = kernel.syscall(2, ((ExecveEvent) currentEvent).getProgramName());

                } else if (currentEvent instanceof TimeOutEvent) {

                    print("     Time out event loaded off of queue");
                    print("     Timing out process number "+((TimeOutEvent) currentEvent).getProcessID());
                    kernel.interrupt(0, kernel.readyQueue.peek().getPID());
                    print("     Time out complete");

                } else if (currentEvent instanceof WakeUpEvent) {
                    print("     Wake up event loaded off of queue");
                    kernel.interrupt(1, /**time at which event is due to occur*/currentEvent.getTime(),
                            /*id of device handling the request*/((WakeUpEvent) currentEvent).getDevice().getID(),
                            /*id of process making request*/((WakeUpEvent) currentEvent).getProcess().getPID());
                } else {
                    print("     Simulator: mainLoop: handling events: unknown event " + currentEvent);
                }
            }

            if(count==1){
                print("CPU uninitialised. Initialise CPU.");
                kernel.initialiseCPU();
            }

            //check that the program contains an instruction
            if(((SimulatedProcessControlBlock)cpu.getCurrentProcess()).getNumberOfInstructions()>1){

                //case: the instruction to be executed is a CPUInstruction
                if(((SimulatedProcessControlBlock)cpu.getCurrentProcess()).getNextInstruction() instanceof CPUInstruction){
                    cpu.execute(sliceLength);
                }

                //case: the instruction to be executed is an IOInstruction
                else if(((SimulatedProcessControlBlock)cpu.getCurrentProcess()).getNextInstruction() instanceof IOInstruction){
                    // make an IO_REQUEST
                    IOInstruction instruction = (IOInstruction)cpu.getCurrentProcess().getNextInstruction();
                    kernel.syscall(3, instruction.getDeviceID(), instruction.getDuration());
                }

                //case: unidentifies instruction type
                else{
                    print("Unsupported instruction");
//                    print("Instruction type: "+cpu.getCurrentProcess().getInstruction().toString());
                }
            }

            //if there is only one instruction left to execute, the program should terminate after execution
            if(((SimulatedProcessControlBlock)cpu.getCurrentProcess()).getNumberOfInstructions()==1){

                //case: the instruction to be executed is a CPUInstruction
                if(((SimulatedProcessControlBlock)cpu.getCurrentProcess()).getInstruction() instanceof CPUInstruction){

                    cpu.execute(sliceLength);
                    print(cpu.getCurrentProcess().getInstruction().getDuration()+"");

                }

                //case: the instruction to be executed is an IOInstruction
                else if(((SimulatedProcessControlBlock)cpu.getCurrentProcess()).getInstruction() instanceof IOInstruction){
                    // make an IO_REQUEST
                    IOInstruction instruction = (IOInstruction)cpu.getCurrentProcess().getInstruction();
                    kernel.syscall(3, instruction.getDeviceID(), instruction.getDuration());
                }

                //case: unidentifies instruction type
                else{
                    print("Only one instruction left. getNextInstruction() will return null.");
                }
            }

            //cpu MUST be empty/idle
            else{
                print("CPU state should be idle and is "+cpu.isIdle());
            }
            print("");
        }
    }

}
