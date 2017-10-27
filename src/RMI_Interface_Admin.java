import java.rmi.*;
import java.util.*;
public interface RMI_Interface_Admin extends Remote{
    public boolean register(String name, String username, String password, int type, int id_faculty, int id_departement, String address, int num_id, int month_id, int year_id, String phoneNumber)throws RemoteException;
    public void addDepartment(String name, int id_faculty)throws RemoteException;
    public void addFaculty(String name)throws RemoteException;
    public void changeDepartment(String newName, int id)throws RemoteException;
    public void changeFaculty(String newName, int id)throws RemoteException;
    public boolean deleteDepartment(int id)throws RemoteException;
    public boolean deleteFaculty(int id)throws RemoteException;
    public void createElection(Date startDate, Date endDate, String name, String desc, int id_department)throws RemoteException;
    public void removeList(int election, String name)throws RemoteException;
    public int getDepartmentNumber(int id)throws RemoteException;
    public int getListType(int id)throws RemoteException;
    public boolean checkDepartment(int id)throws RemoteException;
    public boolean checkFaculty(int id)throws RemoteException;
    public int checkUserType(int id)throws RemoteException;
    public boolean checkElection(int id)throws RemoteException;
    public void createList(String name, int type, int election) throws RemoteException;
    public void changeElectionName(int id, String name) throws RemoteException;
    public void changeElectionEndDate(int id, Date endDate) throws RemoteException;
    public void changeElectionStartDate(int id, Date startDate) throws RemoteException;
    public void changeElectionDescription(int id, String desc) throws RemoteException;
    public boolean addVotingTable(int depId)throws RemoteException;
    public String whereUserVoted(int electionId, int userId)throws RemoteException;
    public ArrayList<Vote> getUserVotes(int id)throws RemoteException;
    public HashMap<String, Integer> getElectionResults(int id)throws RemoteException;
    public HashMap<String, Integer> getAllDepartments() throws RemoteException;
    public HashMap<String, Integer> getAllFaculties() throws RemoteException;
    public HashMap<String,Integer> getPastElections() throws RemoteException;
    public HashMap<String,Integer> getAllElections() throws RemoteException;
    public HashMap<String,Integer> getElectionLists(int election) throws RemoteException;
    public void addCandidatesToList(int list, ArrayList<Integer> users) throws RemoteException;
    public HashMap<String, Integer> getDepartmentsFromFaculty(int faculty) throws RemoteException;
    public void setAdmin(Admin_Console admin)throws RemoteException;
    //CALLBACK MESAS DE VOTO -- estado mesas de voto
    //CALLBACK MESAS DE VOTO -- número eleitores que votaram em cada mesa de voto até ao momento
    public void endElection();
}
