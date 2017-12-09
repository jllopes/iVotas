import java.rmi.*;

public interface TCP_Interface extends Remote{
	public int ping(String isAlive) throws RemoteException;
}
