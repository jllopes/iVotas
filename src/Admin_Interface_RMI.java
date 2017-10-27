import java.rmi.*;

public interface Admin_Interface_RMI extends Remote{
    public void receiveNotification(String notification) throws RemoteException;
}
