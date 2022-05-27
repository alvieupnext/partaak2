import data.models.Metrics;
import data.models.Patient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.UUID;

//netid: avargasg

public interface DataServerInterface extends Remote {
    void totals(UUID queryID, Metrics result) throws RemoteException;
    void property(UUID queryID, int amount, Attribute att) throws RemoteException;
    void link(String serverName) throws RemoteException;
    void add(Patient patient) throws RemoteException;
}
