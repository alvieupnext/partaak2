import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//netid: avargasg

public class LinkTest {
    public static void main(){
        try{
            //create new data server and link to the second dataserver
            Date begin3 = new GregorianCalendar(2021, Calendar.APRIL, 2).getTime();
            Date end3 = new GregorianCalendar(2022, Calendar.MARCH, 13).getTime();
            DataServer third = new DataServer("thirdTest", begin3, end3);
            Registry registry = LocateRegistry.getRegistry();
            DataServerInterface second = (DataServerInterface) registry.lookup("secondTest");
            second.link("thirdTest");
        }
        catch (Exception e){
            System.err.println("Server test failed");
        }
    }
}
