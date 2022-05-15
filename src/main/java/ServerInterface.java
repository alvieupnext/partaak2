import data.models.Metrics;
import data.models.Patient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.UUID;


public interface ServerInterface extends Remote {
    void totals(String clientID, UUID queryID) throws RemoteException;
    void property(String clientID, UUID queryID, int amount, String att) throws RemoteException;
    void onResultTotals(UUID queryID, Metrics result) throws RemoteException;
    void onResultProperty(UUID queryID, Date result) throws RemoteException;
    void onFailedResultProperty(UUID queryID) throws RemoteException;
    void patients(Patient[] patients) throws RemoteException;
}
