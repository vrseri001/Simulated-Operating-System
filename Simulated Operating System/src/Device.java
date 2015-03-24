/**
 * Created by ErinV on 2015-03-23.
 */
public class Device implements IODevice {

    private int ID;
    private String name;
    private long freeTime;

    public Device(int ID, String name){
        this.ID = ID;
        this.name = name;
    }

    /**
     * Obtain the device ID.
     */
    public int getID(){
        return ID;
    }


    /**
     * Obtain the device type name.
     */
    public String getName(){
        return name;
    }

    /**
     * Obtain the time at which the device will have completed all its current requests.
     */
    public long getFreeTime(){
        return freeTime;
    }

    /**
     * Place the given process on the device queue.
     */
    public long requestIO(int duration, ProcessControlBlock process){
        return 1;
    }

}
