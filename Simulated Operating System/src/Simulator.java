import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by ErinV on 2015-03-23.
 */
public class Simulator {

    //list of all the devices from the configuration file
    private static LinkedList<Device> devices = new LinkedList<Device>();

    //list of all the programs from the configuration file
    private static LinkedList<Program> programs = new LinkedList<Program>();

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
                    print("Comment case executing");
                    //ignore this line
                }

                //case: line describes I/O device attached to the system
                else if(line.startsWith("I/O")){
                    String temp = line.substring(4);
                    print("I/O attachment case");
                    print(temp);

                    //split string into integers and other chars

                    //create a new device

                    //add device to devices LinkedList
                }

                else if(line.startsWith("DEVICE")){
                    String temp = line.substring(7);
                    print("DEVICE attachment case");
                    print(temp);

                    //split string into integers and other chars

                    //create a new device

                    //add device to devices LinkedList
                }

                //case: line describes program information
                else if(line.startsWith("PROGRAM")){
                    String temp = line.substring(8);
                    print("Program case");
                    print(temp);

                    //split string into integers and other chars

                    //create a new program

                    //add device to programs LinkedList

                }

                //case: no other case condition met. provide feedback to that effect
                else {
                    print("Simulator: main(): try/ while/ if error");
                }

            }
        } catch (Exception e){
            print("Simulator: main(): error: "+e);
        }
    }

    private static void print(String string){
        System.out.println(string);
    }

    private static void schedule(int slice, int do){
        
    }
}
