import data.models.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

//netid: avargasg

public class Client implements ClientInterface{

    String clientName;
    HashMap<UUID, String[]> dict;
    ServerInterface server;
    HospitalInterface hospitalServer = null;

    public Client(String clientName) {
        this.clientName = clientName;
        this.dict = new HashMap<UUID, String[]>();
        this.setup();
    }

    public void setup(){
        try{
            //put this object in the rmiregistry
            Registry registry = LocateRegistry.getRegistry();
            ClientInterface clientStub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
            registry.rebind(this.clientName, clientStub);
            System.out.println("Client Connected: " + this.clientName);
            //get the CDC server stub
            this.server = (ServerInterface) registry.lookup("CDC");
        }
        catch (Exception e){
            System.err.println("Client Setup failed for " + clientName);
        }
    }

    public void overview(String attribute){
        try {
            //generate id for query
            UUID id = UUID.randomUUID();
            String[] query = new String[]{"overview", attribute};
            //save query in dictionary
            this.addToDictionary(id, query);
            String queryString = "overview on " + attribute;
            System.out.println(this.clientName + " sent query: " + queryString);
            server.totals(this.clientName, id);
        }
        catch (Exception e){
            System.err.println("Failed to send overview");
        }
    }

    public void date(int amount, String attribute){
        try {
            //generate id for query
            UUID id = UUID.randomUUID();
            String[] query = new String[]{"date", Integer.toString(amount), attribute};
            //save query in dictionary
            this.addToDictionary(id, query);
            String queryString = "Date where " + amount + " are " + attribute;
            System.out.println(this.clientName + " sent query: " + queryString);
            server.property(this.clientName, id, amount, attribute);
        }
        catch (Exception e){
            System.err.println("Failed to send overview");
        }
    }

    //connect to a hospital server
    public void connect(String name){
        try{
            Registry registry = LocateRegistry.getRegistry();
            this.hospitalServer = (HospitalInterface) registry.lookup(name);
        }
        catch (Exception e){
            System.err.println("Failed to Connect to Hospital" + name + e);
        }
    }

    public void addPatient(Patient patient){
        try{
            if (hospitalServer != null){ //hospital Server connected
                hospitalServer.add(patient);
            }
            else{ //no Hospital server connected
                throw new Exception("Hospital Server Not Connected");
            }
        }
        catch (Exception e){
            System.err.println("Client: Failed to Create Patient in " + clientName + e);
        }
    }

    public void addToDictionary(UUID id, String[] query){
        dict.put(id, query);
    }

    public void onResultOverview(UUID id, Metrics result){
        String[] query = dict.get(id);
        Attribute att = Attribute.valueOf(query[1]);
        long requested = switch (att) {
            case labConfirmed -> result.labConfirmed;
            case female -> result.female;
            case male -> result.male;
            case aged -> result.aged;
            case hospitalised -> result.hospitalised;
            case intensiveCare -> result.intensiveCare;
            case deceased -> result.deceased;
            case comorbidities -> result.comorbidities;
        };
        //query = overview + attribute
        String queryString = query[0] + " on " + query[1];
        System.out.println("Query " + queryString + " from " + clientName + " resolved: " + requested);
        //remove from dictionary afterwards
        dict.remove(id);
    }

    public void onResultDate(UUID id, Date result) {
        String[] query = dict.get(id);
        //query = date + amount + attribute
        String queryString = query[0] + " on which " + query[1] + " amount of " + query[2];
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(result);
        System.out.println("Query " + queryString + " resolved: " + date);
        //remove from dictionary afterwards
        dict.remove(id);
    }

    public void onFailedResultDate(UUID id)  {
        String[] query = dict.get(id);
        //query = date + amount + attribute
        String queryString = query[0] + " on which " + query[1] + " amount of " + query[2];
        System.out.println("Query " + queryString + " not resolved: Patient not found");
        //remove from dictionary afterwards
        dict.remove(id);
    }

    public static void main (String[] args) {
        try {
            String request = args[0];
            Client client = new Client("Theresa");
            String att;
            switch (request) {
                case "overview" -> {
                    att = args[1];
                    client.overview(att);
                }
                case "date" -> {
                    int amount = Integer.parseInt(args[1]);
                    att = args[2];
                    client.date(amount, att);
                }
                case "connect" -> {
                    String hospitalName = args[1];
                    client.connect(hospitalName);
                }
                case "add" -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = sdf.parse(args[1]);
                    Status status = Status.valueOf(args[2]);
                    Sex sex = Sex.valueOf(args[3]);
                    AgeGroup age = AgeGroup.valueOf(args[4]);
                    boolean hospitalised = (args[5].equals("hospitalised"));
                    boolean icu = (args[6].equals("intensiveCare"));
                    boolean deceased = (args[7].equals("deceased"));
                    boolean comorbidities = (args[8].equals("comorbidities"));
                    Patient patient = new Patient(date, status, sex, age, hospitalised, icu, deceased, comorbidities);
                    client.addPatient(patient);
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}
