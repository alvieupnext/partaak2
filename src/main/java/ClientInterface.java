import data.models.Metrics;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void onResultOverview(Number queryID, Metrics result) throws RemoteException;
}
