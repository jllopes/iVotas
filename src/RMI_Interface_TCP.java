import java.rmi.*;
import java.util.HashMap;

public interface RMI_Interface_TCP extends Remote {
	public String message() throws java.rmi.RemoteException;
	public int login(String username, String password )throws RemoteException;
	public HashMap<Integer, String> getElections(int usertype) throws RemoteException;
	public HashMap<Integer, String> getListsElections(int usertype, int idElection) throws RemoteException;
	public boolean vote(int userid ,int usertype, int idElection, int idList) throws RemoteException; //false se ja existir voto
	//public void unlockTable() throws; <-- so do lado do tcp :)
	public int getUserId(String username) throws RemoteException;
}