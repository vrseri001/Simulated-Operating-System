import java.util.LinkedList;

/**
 * Created by ErinV on 2015-03-23.
 */
public class Device implements IODevice {
    private int ID;
    private String name;
    //processes waiting to execute on this IODevice
    protected LinkedList<ProcessControlBlock> processQ = new LinkedList<ProcessControlBlock>();

    public Device(){
        ID = 0;
        name = "no name";
    }

    public Device(int ID, String name){
        this.ID = ID;
        this.name = name;
    }

    public int getID(){
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getFreeTime() {
        return 0;
    }

    @Override
    public long requestIO(int duration, ProcessControlBlock process) {
        return 0;
    }

    public void addProcessControlBlockToQueue(ProcessControlBlock pcb){
        processQ.add(pcb);
    }
}
