/**
 * Created by ErinV on 2015-03-23.
 */
public class Program {

    private int arrivalTime;
    private String fileName;

    public Program(){
        arrivalTime = 0;
        fileName = "no file name";
    }

    public Program(int arrivalTime, String fileName){
        this.arrivalTime = arrivalTime;
        this.fileName = fileName;
    }

    public int getArrivalTime(){
        return arrivalTime;
    }

    public String getFileName(){
        return fileName;
    }
}
