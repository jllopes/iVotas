import java.rmi.*;
import java.util.HashMap;

public interface RMI_Interface_TCP extends Remote {
	public String message() throws java.rmi.RemoteException;
	public int login(String username, String password )throws RemoteException;
	public HashMap<Integer, String> getElections(int usertype, int userDep) throws RemoteException;
	public HashMap<Integer, String> getListsElections(int usertype, int userDep, int idElection) throws RemoteException;
	public boolean vote(int userid ,int usertype, int userDep, int idElection, int idList, int idTable) throws RemoteException; //false se ja existir voto
	public boolean vote_blank(int userid ,int usertype, int userDep, int idElection) throws RemoteException; //false se ja existir voto

	//public void unlockTable() throws; <-- so do lado do tcp :)
	public int assignTable() throws RemoteException;
	public HashMap<String, Integer>  getUserId(String username) throws RemoteException;
}