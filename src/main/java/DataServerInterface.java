import data.models.Metrics;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface DataServerInterface extends Remote {
    void totals(String clientID, Number queryID, Metrics result) throws RemoteException;
}
