//TODO copy Client Interface to Client

import data.models.Metrics;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

public class Client implements ClientInterface{

    String clientName;
    int ID;
    Dictionary dict;

    public Client(String clientName) {
        this.clientName = clientName;
        this.ID = 0;
        this.dict = new Hashtable();
    }

    private int generateID(){
        return this.ID++;
    }

    public void addToDictionary(Number id, String[] query){
        dict.put(id, query);
    }

    public void onResultOverview(Number id, Metrics result){
        String[] query = (String[]) dict.get(id);
        Attribute att = Attribute.valueOf(query[1]);
        long requested = 0;
        switch (att) {
            case labConfirmed: requested = result.labConfirmed; break;
            case female: requested = result.female; break;
            case male: requested = result.male; break;
            case aged: requested = result.aged; break;
            case hospitalised: requested = result.hospitalised; break;
            case intensiveCare: requested = result.intensiveCare; break;
            case deceased: requested = result.deceased; break;
            case comorbidities: requested = result.comorbidities; break;
        }
        String queryString = query[0] + " " + query[1];
        System.out.println("Query " + queryString +  " resolved: " + requested);
        dict.remove(id);
    }

    public static void main (String[] args) {
        try {
            String request = args[0];
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            Client client = new Client("Clarisse");
            //register client in the registry
            ClientInterface clientStub = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);
            registry.rebind(client.clientName, clientStub);
            System.err.println("Client Connected: " + client.clientName);
            //get server stub
            ServerInterface stub = (ServerInterface) registry.lookup("CDC");
            Attribute attribute;
            switch (request) {
                case "overview":
                    Number id = client.generateID();
                    String query = args[0] + " on " + args[1];
                    System.out.println("Sent query: " + query);
                    client.addToDictionary(id, args);
                    stub.totals(client.clientName, id);
                    break;
                case "date":
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = args[1];
                    Date date = sdf.parse(dateString);
                    attribute = Attribute.valueOf(args[2]);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
