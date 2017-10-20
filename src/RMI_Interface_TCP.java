import java.rmi.*;

public interface RMI_Interface_TCP extends Remote {
	public String message() throws java.rmi.RemoteException;
}