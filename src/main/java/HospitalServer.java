import data.models.Patient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

//netid: avargasg

public class HospitalServer implements HospitalInterface{

    Patient[] batch;

    String name;

    int amountOfPatients = 0;

    int MAX_BATCH_SIZE = 2;

    //cdc server
    ServerInterface server;

    public HospitalServer(String name) {
        this.batch = new Patient[MAX_BATCH_SIZE];
        this.name = name;
        this.setup();
    }

    private void setup(){
        try{
            //get registry
            Registry registry = LocateRegistry.getRegistry();
            //Step 1: register this server to be found
            HospitalInterface stub = (HospitalInterface) UnicastRemoteObject.exportObject(this, 0);
            registry.rebind(this.name, stub);
            //Step 2: establish connection with CDC
            this.server = (ServerInterface) registry.lookup("CDC");

        }
        catch (Exception e){
            System.err.println("Hospital Server Setup Failed" + e);
        }
    }

    public void add(Patient patient){
        //add patient to batch and increase amount of patients
        batch[amountOfPatients++] = patient;
        //if we reached our max batch size
        if (amountOfPatients == MAX_BATCH_SIZE){
            try{
                //send batch to CDC server
                this.server.patients(batch);
                //clear batch
                this.batch = new Patient[MAX_BATCH_SIZE];
                //reset amount of patients
                amountOfPatients = 0;
                System.out.println("Sent New Patients to CDC");
            }
            catch (Exception e){
                System.err.println("Hospital: Failed to add patients" + e);
            }
        }
    }

    public static void main(String args[]){
        try{
            HospitalServer hos = new HospitalServer("hospital");
        }
        catch (Exception e){
            System.err.println("Failed to run main on HospitalServer");
        }
    }
}
