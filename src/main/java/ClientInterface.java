import data.models.Metrics;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.UUID;

public interface ClientInterface extends Remote {
    void onResultOverview(UUID queryID, Metrics result) throws RemoteException;
    void onResultDate(UUID queryID, Date result) throws RemoteException;
    void onFailedResultDate(UUID queryID) throws RemoteException;
}
