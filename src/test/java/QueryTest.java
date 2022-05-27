import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//netid: avargasg

public class QueryTest {
    public static void main(){
        try {
            //step one: create CDC server
            CDC cdc = new CDC();
            //step two: create chain of data servers (3 in this example)
            Date begin1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
            Date end1 = new GregorianCalendar(2020, Calendar.AUGUST, 1).getTime();
            DataServer first = new DataServer("firstTest", begin1, end1);
            Date begin2 = new GregorianCalendar(2020, Calendar.AUGUST, 2).getTime();
            Date end2 = new GregorianCalendar(2021, Calendar.APRIL, 1).getTime();
            DataServer second = new DataServer("secondTest", begin2, end2);
            //link servers to each other
            cdc.link("firstTest");
            first.link("secondTest");
            System.out.println("Data Servers Ready");
        }
        catch (Exception e){
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}
