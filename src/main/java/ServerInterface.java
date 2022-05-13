import data.models.Metrics;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;


public interface ServerInterface extends Remote {
    void totals(String clientID, Number queryID) throws RemoteException;
    void date(Date date, Attribute att) throws RemoteException;
    void onResultTotals(String clientID, Number queryID, Metrics result) throws RemoteException;
}
