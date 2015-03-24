import java.io.File;
import java.util.*;

/**
 * Created by ErinV on 2015-03-23.
 */
public class Simulator {

    //list of all the devices from the configuration file
    private static LinkedList<Device> devices = new LinkedList<Device>();

    //list of all the programs from the configuration file
    private static LinkedList<Program> programs = new LinkedList<Program>();

    //list of the times at which a new process should begin executing
    private static LinkedList<Long> systemTimes;

    //event queue
    private static EventQueue eventQueue = new EventQueue();

    //kernel
    private static SimulatedKernel kernel = new SimulatedKernel();

    //system time
    private static long systemTime = 0;

    //parameters for the Round Robin simulation
    private static int sliceLength;
    private static int dispatchOverhead;

    public static void main(String[] args) {
        try {
            //open the configuration file and read data in
            File file = new File(args[0]);
            Scanner scanner = new Scanner(file);

            //read program lines and device lines in
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                //case: line is a comment
                if(line.startsWith("#")){
                    //ignore this line
                }

                //case: line describes I/O device attached to the system
                else if(line.startsWith("I/O")){
                    //only store relevant information, not line marker
                    String temp = line.substring(4);

                    //split string into integers and other chars
                    String[] info = temp.split(" ");

                    //create a new device
                    Device newDevice = new Device(Integer.parseInt(info[0]), info[1]);

                    //add device to devices LinkedList
                    devices.add(newDevice);
                }

                else if(line.startsWith("DEVICE")){
                    //only store relevant information, not line marker
                    String temp = line.substring(7);

                    //split string into integers and other chars
                    //split string into integers and other chars
                    String[] info = temp.split(" ");

                    //create a new device
                    Device newDevice = new Device(Integer.parseInt(info[0]), info[1]);

                    //add device to devices LinkedList
                    devices.add(newDevice);
                }

                //case: line describes program information
                else if(line.startsWith("PROGRAM")){
                    //only store relevant information, not line marker
                    String temp = line.substring(8);

                    //split string into integers and other chars
                    String[] info = temp.split(" ");

                    //create a new program
                    Program newProgram = new Program(Integer.parseInt(info[0]), info[1]);

                    //add program to programs LinkedList
                    programs.add(newProgram);

                }

                //case: no other case condition met. provide feedback to that effect
                else {
                    print("Simulator: main(): try/ while/ if error");
                }
            }
        } catch (Exception e){
            print("Simulator: main(): error: "+e);
        }

        eventSimulationDriver();
    }

    private static void print(String string){
        System.out.println(string);
    }

    private static void eventSimulationDriver(){

        //for each program identified in the configuration file,
        // create a load program event and insert it into the event queue
        for (int i = 0; i<programs.size(); i++){
            LoadProgramEvent lpe = new LoadProgramEvent(systemTime, dispatchOverhead, programs.peek().getFileName());
            eventQueue.add(lpe);
        }

        //for each device in the config file, make a MAKE_DEVICE sys call to kernel
        for(int i = 0; i<devices.size(); i++){
            kernel.syscall(1, devices.peek());
        }

        mainEventLoop();
    }

    /**
     * Round robin scheduling of processes on the CPU
     * @param slice time slice assigned to each of the processes when running on the CPU
     * @param dO dispatch overhead. time taken for the dispatcher to stop a process and start another
     */
    private static void schedule(int slice, int dO){
    }

    private static void mainEventLoop(){

        boolean eventQueueEmpty = false;
        boolean cpuIdle = false;

        while (!eventQueueEmpty&&!cpuIdle){

            //process the event up until TF
            cpu.execute(eventQueue.peek());

            //if the eventQueuse is empty
            if (eventQueue.isEmpty()){
                eventQueueEmpty = true;
            }
            else{
                eventQueueEmpty = false;
            }

            //if the CPU is idle
            if(/**/){
                cpuIdle = true;
            }
            else{
                cpuIdle = false;
            }
        }
    }
}
