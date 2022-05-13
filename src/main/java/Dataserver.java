import data.Reader;
import data.models.Metrics;
import data.models.Patient;
import solutions.CovidAnalyser;
import solutions.ParallelAnalyser;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class Dataserver implements DataServerInterface{

    String name;

    Patient[] data;

    Date startDate;

    Date endDate;

    DataServerInterface next;

    public Dataserver (String name, Date startDate, Date endDate){
        this.name = name;
        this.next = null;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //SETUP
    //register data server to be found in the registry
    private void register(){
        try {
            DataServerInterface stub = (DataServerInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
        }
        catch (Exception e){
            System.err.println("Failed to Register Data Server");
        }
    }

    private void generateData(){
        this.data = Reader.generateDataInDates(100000, this.startDate, this.endDate);
    }

    public void setup(){
        this.generateData();
        this.register();
    }

    public void link(String serverName){
        try {
            Registry registry = LocateRegistry.getRegistry();
            DataServerInterface server = (DataServerInterface) registry.lookup(serverName);
            this.next = server;
        }
        catch (Exception e){
            System.err.println("Failed to Connect to " + serverName + " from " + name);
        }
    }

    public void totals(String clientID, Number queryID, Metrics result){
        try {
            CovidAnalyser parallel = new ParallelAnalyser(1, 50000);
            Metrics metric = parallel.phaseOne(this.data);
            if (this.next != null){ //not the last dataserver
                this.next.totals(clientID, queryID, result.merge(metric));
            }
            else { //communicate to CDC server
                Registry registry = LocateRegistry.getRegistry();
                ServerInterface server = (ServerInterface) registry.lookup("CDC");
                server.onResultTotals(clientID, queryID, result);
            }
        }
        catch (Exception e){
            System.err.println("Totals failed on dataserver " + name);
        }
    }
}
