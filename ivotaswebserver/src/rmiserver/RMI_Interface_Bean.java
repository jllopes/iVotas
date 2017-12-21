package rmiserver;

import java.rmi.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public interface RMI_Interface_Bean extends Remote{
    /* ADMIN */
    public boolean register(String name, String username, String password, int type, int id_faculty, int id_departement, String address, int num_id, int month_id, int year_id, String phoneNumber)throws RemoteException;
    public void addDepartment(String name, int id_faculty)throws RemoteException;
    public void addFaculty(String name)throws RemoteException;
    public void changeDepartment(String newName, int id)throws RemoteException;
    public void changeFaculty(String newName, int id)throws RemoteException;
    public boolean deleteDepartment(int id)throws RemoteException;
    public boolean deleteFaculty(int id)throws RemoteException;
    public boolean createElection(Date startDate, Date endDate, String name, String desc, int id_department)throws RemoteException;
    public void removeList(int id) throws RemoteException;
    public int getDepartmentNumber(int id)throws RemoteException;
    public int getListType(int id)throws RemoteException;
    public boolean checkDepartment(int id)throws RemoteException;
    public boolean checkFaculty(int id)throws RemoteException;
    public int checkUserType(int id)throws RemoteException;
    public void createList(String name, int type, int election) throws RemoteException;
    public void changeElectionName(int id, String name) throws RemoteException;
    public void changeElectionEndDate(int id, Date endDate) throws RemoteException;
    public void changeElectionStartDate(int id, Date startDate) throws RemoteException;
    public void changeElectionDescription(int id, String desc) throws RemoteException;
    public ArrayList<Department> getDepartmentsFromFaculty(int faculty) throws RemoteException;
    public HashMap<String, Integer> getAllFaculties() throws RemoteException;
    public ArrayList<Election> getAllElections() throws RemoteException;
    public Election getElection(int id) throws RemoteException;
    public ArrayList<String> getPeopleList(int listId) throws RemoteException;
    public ArrayList<User> getUsers() throws RemoteException;
    public List<Vote> getUserVotes(int id) throws RemoteException;
    public ArrayList<Election> getPastElections() throws RemoteException;
    public HashMap<String, Integer> getElectionResults(int id)throws RemoteException;
    public ArrayList<Lista> getListsElection(int idElection) throws RemoteException;
    public boolean associateFacebook(String username ,String facebookId) throws RemoteException;
    public String loginFacebook(String facebookId) throws RemoteException;
    public String getFacebookId(String username) throws RemoteException;
    
    
    public HashMap<String, Integer> getElectionVotesPerTable(int electionId) throws RemoteException;
    
    /* TCP */
    
    public String message() throws java.rmi.RemoteException;
    public int login(String username, String password )throws RemoteException;
    public HashMap<Integer, String> getElections(int usertype, int userDep) throws RemoteException;
    public HashMap<Integer, String> getListsElections(int usertype, int userDep, int idElection) throws RemoteException;
    public HashMap<String, Integer>  getUserId(String username) throws RemoteException;
    public boolean checkUser(String username) throws RemoteException;
    public boolean vote(int userId, int userType, int userDep, int id_election, int vote, int id_table) throws RemoteException;
    public boolean vote_blank(int userid ,int usertype, int userDep, int idElection, int table) throws RemoteException; //false se ja existir voto
    public Vote getVote(int id) throws RemoteException;
    public void addTable(TCP_Interface t) throws RemoteException;
    public ArrayList<Department> getAllDepartments() throws RemoteException;
	public ArrayList<Department> getFreeDepartments() throws RemoteException;
	public boolean createTable(int depId) throws RemoteException;
	public boolean addVotingTable(int election, int table) throws RemoteException;
	public ArrayList<Election> getFutureElections() throws RemoteException;
	public ArrayList<VotingTable> getTables() throws RemoteException;


}
