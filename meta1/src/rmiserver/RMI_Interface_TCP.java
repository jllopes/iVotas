package rmiserver;

import java.rmi.*;
import java.util.HashMap;

public interface RMI_Interface_TCP extends Remote {
	public String message() throws java.rmi.RemoteException;
	public int login(String username, String password )throws RemoteException;
	public HashMap<Integer, String> getElections(int usertype, int userDep) throws RemoteException;
	public HashMap<Integer, String> getListsElections(int usertype, int userDep, int idElection) throws RemoteException;
	public HashMap<String, Integer>  getUserId(String username) throws RemoteException;
	public boolean checkUser(String username) throws RemoteException;
	public boolean vote(int userId, int userType, int userDep, int id_election, int vote, int id_table) throws RemoteException;
	public boolean vote_blank(int userid ,int usertype, int userDep, int idElection, int table) throws RemoteException; //false se ja existir voto

	public void addTable(TCP_Interface t) throws RemoteException;
}