import data.models.Metrics;
import data.models.Patient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

//netid: avargasg

public class CDC implements ServerInterface {

    DataServerInterface dataServer;

    HashMap<UUID, String> dictionary;

    public CDC () {
        this.dictionary = new HashMap<UUID, String>();
        this.setup();
    }

    public void link(String dataSID){
        try {
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            //get the first data server from the query
            this.dataServer = (DataServerInterface) registry.lookup(dataSID);
        }
        catch (Exception e){
            System.err.println("Link to First Dataserver Failed");
        }
    }

    private void setup(){
        try{
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);
            //register CDC server
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("CDC", stub);
            System.out.println("CDC Server ready");
        }
        catch (Exception e){
            System.err.println("Failed to Setup CDC Server");
        }

    }

    public void onResultTotals(UUID queryID, Metrics result){
        try {
            //get client name corresponding to the queryID
            String clientID = this.dictionary.get(queryID);
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            //get the client from the registry
            ClientInterface client = (ClientInterface) registry.lookup(clientID);
            client.onResultOverview(queryID, result);
        }
        catch (Exception e){
            System.err.println("Error on result CDC" + e);
        }
    }

    public void onResultProperty(UUID queryID, Date result){
        try {
            //get client name corresponding to the queryID
            String clientID = this.dictionary.get(queryID);
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            //get the client from the registry
            ClientInterface client = (ClientInterface) registry.lookup(clientID);
            client.onResultDate(queryID, result);
        }
        catch (Exception e){
            System.err.println("Error on property CDC " + e);
        }
    }

    public void onFailedResultProperty(UUID queryID){
        try {
            //get client name corresponding to the queryID
            String clientID = this.dictionary.get(queryID);
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            //get the client from the registry
            ClientInterface client = (ClientInterface) registry.lookup(clientID);
            client.onFailedResultDate(queryID);
        }
        catch (Exception e){
            System.err.println("Error on property CDC " + e);
        }
    }

    public void totals(String clientID, UUID queryID)  {
        try {
            //link the query id to the client name in the dictionary
            this.dictionary.put(queryID, clientID);
            //send query to first dataserver
            dataServer.totals(queryID, new Metrics(0,0,0,0,0,0,0,0));
        }
        catch (Exception e){
            System.err.println("Error totals CDC"  + e);
        }
    }

    public void property(String clientID, UUID queryID, int amount, String att){
        try{
            //link the query id to the client name in the dictionary
            this.dictionary.put(queryID, clientID);
            Attribute attribute = Attribute.valueOf(att);
            //send query to first dataserver
            dataServer.property(queryID, amount, attribute);
        }
        catch(Exception e){
            System.err.println("Error property CDC "  + e);
        }
    }

    public void patients(Patient[] patients){
        try{
            for (Patient patient: patients){
                dataServer.add(patient);
            }
        }
        catch (Exception e){
            System.err.println("CDC: failed to add patients");
        }
    }

    public static void main(String[] args){
        try {
            //step one: create CDC server
            CDC cdc = new CDC();
            //step two: create chain of data servers (3 in this example)
            Date begin1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
            Date end1 = new GregorianCalendar(2020, Calendar.AUGUST, 1).getTime();
            DataServer first = new DataServer("first", begin1, end1);
            Date begin2 = new GregorianCalendar(2020, Calendar.AUGUST, 2).getTime();
            Date end2 = new GregorianCalendar(2021, Calendar.APRIL, 1).getTime();
            DataServer second = new DataServer("second", begin2, end2);
            Date begin3 = new GregorianCalendar(2021, Calendar.APRIL, 2).getTime();
            Date end3 = new GregorianCalendar(2022, Calendar.MARCH, 13).getTime();
            DataServer third = new DataServer("third", begin3, end3);
            //link servers to each other
            cdc.link("first");
            first.link("second");
            second.link("third");
            System.out.println("Data Servers Ready");
        }
        catch (Exception e){
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}
