import data.Reader;
import data.models.*;
import solutions.CovidAnalyser;
import solutions.ParallelAnalyser;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

//netid: avargasg

public class DataServer implements DataServerInterface{

    String name;

    //ArrayList important for adding new patients
    ArrayList<Patient> data;

    Date startDate;

    Date endDate;

    DataServerInterface next;

    //cdc server
    ServerInterface server;

    public DataServer(String name, Date startDate, Date endDate){
        this.name = name;
        this.next = null;
        this.startDate = startDate;
        this.endDate = endDate;
        this.setup();
    }

    //SETUP

    private void register(){
        try {
            //register data server to be found in the registry
            DataServerInterface stub = (DataServerInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            //get stub of cdc server
            this.server = (ServerInterface) registry.lookup("CDC");
        }
        catch (Exception e){
            System.err.println("Failed to Register Data Server" + e);
        }
    }

    private void generateData(){
        //generate data
        Patient[] generated = Reader.generateDataInDates(100000, this.startDate, this.endDate);
        //conversion to data
        this.data = new ArrayList<Patient>(Arrays.asList(generated));
    }

    private void setup(){
        this.generateData();
        this.register();
    }

    public void link(String serverName){
        try {
            Registry registry = LocateRegistry.getRegistry();
            this.next = (DataServerInterface) registry.lookup(serverName);
            System.out.println("Linked "+ name + " to " + serverName);
        }
        catch (Exception e){
            System.err.println("Failed to Connect to " + serverName + " from " + name);
        }
    }

    public void totals(UUID queryID, Metrics result){
        try {
            //create new parallel analyser
            CovidAnalyser parallel = new ParallelAnalyser(1, 50000);
            //convert arraylist into array
            Patient[] arrayData = this.data.toArray(new Patient[data.size()]);
            //execute phase one to get the metrics
            Metrics metric = parallel.phaseOne(arrayData);
            //merge this metric with our result
            Metrics intermediate = result.merge(metric);
            if (this.next != null){ //not the last dataserver
                this.next.totals(queryID, intermediate);
            }
            else { //communicate to CDC server
                server.onResultTotals(queryID, intermediate);
            }
        }
        catch (Exception e){
            System.err.println("Totals failed on dataserver " + name);
        }
    }

    //used by property to get the right test
    private boolean isTrue(Patient patient, Attribute att){
        return switch (att) {
            case labConfirmed -> patient.status == Status.LAB_CONFIRMED;
            case female -> patient.sex == Sex.FEMALE;
            case male -> patient.sex == Sex.MALE;
            case aged -> patient.age == AgeGroup.SIXTIES || patient.age == AgeGroup.SEVENTIES || patient.age == AgeGroup.OVER_EIGHTY;
            case hospitalised -> patient.hospitalised;
            case intensiveCare -> patient.icu;
            case deceased -> patient.deceased;
            case comorbidities -> patient.comorbidities;
        };
    }

    public void property(UUID queryID, int amount, Attribute att){
        try{
            int intermed = amount; //intermediate value
            for (Patient patient: data){
                if (isTrue(patient, att)){ //is our condition true
                    intermed -= 1; //decrease intermediate
                    if (intermed == 0){ //we found the last needed patient
                        Date result = patient.date;
                        server.onResultProperty(queryID, result);
                        return; //stop the procedure
                    }
                }
            }
            //if we reach this code, we have not found the patient in our data
            if (this.next != null){ //there are more servers to explore
                next.property(queryID, intermed, att);
            }
            else { //no more servers, report failure
                server.onFailedResultProperty(queryID);
            }
        }
        catch (Exception e){
            System.err.println("Property failed on dataserver " + name + " " + e);
        }

    }

    public void add(Patient patient){
        try{
            //date between startDate and endDate => not after endDate and not before beginDate
            Date targetDate = patient.date;
            if (!targetDate.before(startDate) && !targetDate.after(endDate)){
                for (int i = 0; i < data.size(); i++){
                    Patient dataPatient = data.get(i);
                    if (dataPatient.date.equals(targetDate)){ //found the correct date
                        data.add(i, patient); //add patient to data on this index
                        System.out.println("Added " + patient + "to Dataserver "+ name);
                        return;
                    }
                }
                //Error here if patient could not be added
                System.err.println("Failed to Add Patient in Dataserver " + name);
            }
            else //Date not in this dataserver
                if (this.next != null){ //still servers available
                this.next.add(patient);
            } //if no further server, ignore the patient
        }
        catch (Exception e){
            System.err.println("DataServer: Add Patient failed from " + name + e);
        }

    }


}
