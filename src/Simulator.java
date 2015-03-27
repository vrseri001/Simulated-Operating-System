import java.io.File;
import java.util.*;

/**
 * Created by ErinV on 2015-03-23.
 */
public class Simulator {

    //list of all the devices from the configuration file
    private static LinkedList<String> devices = new LinkedList<String>();

    //list of all the programs from the configuration file
    private static LinkedList<String> programs = new LinkedList<String>();

    //events queue
    private static EventQueue eventQueue = new EventQueue();

    //ready queue; queue to which PCBs are added when a program is ready to be executed according to schedule
    private LinkedList<SimulatedProcessControlBlock> readyQueue = new LinkedList<SimulatedProcessControlBlock>();

    //kernel to simulate process scheduling
    private static SimulatedKernel kernel;

    //parameters for the Round Robin simulation
    private static int sliceLength;
    private static int dispatchOverhead;

    //time tracker
    private static SimulatedSystemTimer systemTimer;

    //for processing events
    private static SimulatedCPU cpu = new SimulatedCPU();


    public static void main(String[] args) {
        loadConfig(args[0]);
        sliceLength = Integer.parseInt(args[1]);
        dispatchOverhead = Integer.parseInt(args[2]);

        kernel = new SimulatedKernel(dispatchOverhead);

        setUpSimulation();
        beginMainSimulationLoop();

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

                    //add device information to the LinkedList
                    //device ID
                    devices.add(info[0]);
                    //device name
                    devices.add(info[1]);
                }

                //case: line describes program information
                else if(line.startsWith("PROGRAM")){
                    String temp = line.substring(8);

                    //split string into integers and other chars
                    String[] info = temp.split(" ");

                    //add device information to the LinkedList
                    //device ID
                    programs.add(info[0]);
                    //device name
                    programs.add(info[1]);

                }

                //case: no other case condition met. provide feedback to that effect
                else {
                    print("Invalid line found: "+line);
                }

            }
        } catch (Exception e){
            print("Simulator: main(): error: "+e);
            e.printStackTrace();
        }
    }
    /*for each program loaded from the config file, create a ExecveEvent and add it to the eventQueue
    * load program event is used to indicate that an EXECVE event should be called
    */
    private static void addExecveEvents(){
        for(int i = 0; i<programs.size(); i+=2){
            ExecveEvent execveEvent = new ExecveEvent(Long.parseLong(programs.get(i)), programs.get(i+1));
            eventQueue.add(execveEvent);
        }
    }

    /*for each device loaded from the config file, make a MAKE_DEVICE system call to the kernel
    */
    private static void createDevices(){
        for(int i = 0; i<devices.size();i+=2){
            int success = kernel.syscall(1, /*device id*/devices.get(i), /*device name or type*/devices.get(i+1));
            print("Simulator: beginMainSimulationLoop: createDevices: successful device creation");
        }
    }

    private static void setUpSimulation(){
        addExecveEvents();
        createDevices();
        systemTimer = new SimulatedSystemTimer();
    }

    private static void beginMainSimulationLoop(){

        while( (!eventQueue.isEmpty()) && (!cpu.isIdle()) ){

            //if the event queue contains
            while( (!eventQueue.isEmpty()) && (eventQueue.peek().getTime()<=systemTimer.getSystemTime())){

                //process the event
                //get the current event off the top of the eventQueue
                Event currentEvent = eventQueue.poll();

                //case: current event is a load program event, otherwise called an Execve event
                if(currentEvent instanceof ExecveEvent){

                    //create an execve system call, which returns the time taken to read in the program instructions
                    int timeoutPID = kernel.syscall(2, ((ExecveEvent) currentEvent).getProgramName());
                    TimeOutEvent timeOutEvent = new TimeOutEvent((currentEvent.getTime()+sliceLength), timeoutPID);
                    eventQueue.add(timeOutEvent);
                }

                else if(currentEvent instanceof TimeOutEvent){

                    //remove currently executing process from CPU
                    ProcessControlBlock currentProcess = cpu.getCurrentProcess();
                    //cancel timeout for current process
                    //do this immediately after removing the currentProcess from the CPU so that...


                    //get the deviceID
                    int deviceID = ((IOInstruction)currentProcess.getInstruction()).getDeviceID();

                    //get the request duration
                    long duration = currentProcess.getInstruction().getDuration();

                    //schedule a wake up event
                    long start = System.currentTimeMillis()+duration;
                    IODevice ioDevice = kernel.devices.get(deviceID);
                    WakeUpEvent wakeUpEvent = new WakeUpEvent(start, ioDevice,currentProcess);
                    eventQueue.add(wakeUpEvent);

                    //place process on device queue in kernel
                    int endIORequest = kernel.syscall(3, deviceID, duration);

                    //next available process switched from the ready queue onto the CPU?
                    //is this how I do it?
                    cpu.contextSwitch(kernel.readyQueue.poll());


                }

                else if(currentEvent instanceof WakeUpEvent){

                }

            }

            long currentTime = System.currentTimeMillis();
            long nextEventDue = eventQueue.peek().getTime();

            //execute CPU
            //by only calling execute on 1, events generated by an executed instruction are
            //not missed as the while loop checks for them after the execution of each instruction
            cpu.execute(nextEventDue-currentTime);
        }
    }

    /**
     * Round robin scheduling of processes on the CPU
     * @param slice time slice assigned to each of the processes when running on the CPU
     * @param dO dispatch overhead. time taken for the dispatcher to stop a process and start another
     */
    private static void schedule(int slice, int dO){
    }
}
