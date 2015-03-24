/**
 * Created by ErinV on 2015-03-23.
 */
public class Device {
    private int ID;
    private String type;

    public Device(){
        ID = 0;
        type = "no type";
    }

    public Device(int ID, String type){
        this.ID = ID;
        this.type = type;
    }
}
