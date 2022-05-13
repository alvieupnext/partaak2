import data.models.Metrics;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CDC implements ServerInterface {

    DataServerInterface dataServer;

    public CDC () {}

    public void link(String dataSID){
        try {
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            //get the first data server from the query
            DataServerInterface dataServer = (DataServerInterface) registry.lookup(dataSID);
            this.dataServer = dataServer;
        }
        catch (Exception e){
            System.err.println("Link to First Dataserver Failed");
        }
    }

    public void onResultTotals(String clientID, Number queryID, Metrics result){
        try {
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            //get the client from the registry
            ClientInterface client = (ClientInterface) registry.lookup(clientID);
            client.onResultOverview(queryID, result);
        }
        catch (Exception e){
            System.err.println("error on result" + e);
        }
    }

    public void totals(String clientID, Number queryID)  {
        try {
            dataServer.totals(clientID, queryID, new Metrics(0,0,0,0,0,0,0,0));
        }
        catch (Exception e){
            System.err.println("Error totals CDC"  + e);
        }
    }

    public void date(Date date, Attribute att){
        System.out.println(12);
    }

    public static void main(String[] args){
        try {
            //step one: create CDC server
            CDC cdc = new CDC();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(cdc, 0);

            //register CDC server
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("CDC", stub);
            System.err.println("Server ready");
            //step two: create chain of data servers (3 in this example)
            Date begin1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
            Date end1 = new GregorianCalendar(2020, Calendar.AUGUST, 1).getTime();
            Dataserver first = new Dataserver("first", begin1, end1);
            Date begin2 = new GregorianCalendar(2020, Calendar.AUGUST, 2).getTime();
            Date end2 = new GregorianCalendar(2021, Calendar.APRIL, 1).getTime();
            Dataserver second = new Dataserver("second", begin2, end2);
            Date begin3 = new GregorianCalendar(2021, Calendar.APRIL, 2).getTime();
            Date end3 = new GregorianCalendar(2022, Calendar.MARCH, 13).getTime();
            Dataserver third = new Dataserver("third", begin3, end3);
            //set up all the dataservers
            first.setup();
            second.setup();
            third.setup();
            //link servers to each other
            cdc.link("first");
            first.link("second");
            second.link("third");
            System.err.println("Data Servers Ready");
        }
        catch (Exception e){
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}
