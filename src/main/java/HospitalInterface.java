import data.models.Patient;

import java.rmi.Remote;
import java.rmi.RemoteException;

//netid: avargasg

public interface HospitalInterface extends Remote {
    void add(Patient patient) throws RemoteException;
}
