import java.util.*;
public interface RMI_Interface_Admin {
    public boolean register(String name, String username, String password, int type, int id_faculty, int id_departement, String address, int num_id, int month_id, int year_id, String phoneNumber);
    public void addDeparment(String name, int id_faculty);
    public void addFaculty(String name);
    public void changeDepartment(String newName, int id);
    public void changeFaculty(String newName, int id);
    public boolean deleteDepartment(int id);
    public boolean deleteFaculty(int id);
    public void createElection(Date startDate, Date endDate, String name, String desc, int id_department);
    public void changeElectionLists(ElectionList list);
    public void changeElectionProperties(Date startDate, Date endDate, String name, String desc, int id);
    public boolean addVotingTable( int depId);
    public String whereUserVoted(int electionId, int userId);
    //CALLBACK MESAS DE VOTO -- estado mesas de voto
    //CALLBACK MESAS DE VOTO -- número eleitores que votaram em cada mesa de voto até ao momento
    public void endElection();
}
