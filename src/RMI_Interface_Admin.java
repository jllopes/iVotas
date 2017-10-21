public interface RMI_Interface_Admin {
    public boolean register(String name, String username, String password, int type, String address, int num_id, int month_id, int year_id);
    public void addDeparment(String name, int id);
    public void addFaculty(String name);
    public void changeDepartment(String newName, int id);
    public void changeFaculty(String newName, int id);
    public void deleteDepartment(int id);
    public void deleteFaculty(int id);
    public void createElection(String startDate, String endDate, String name, String desc, int type);
    public void changeElectionLists(ElectionList list);
    public void changeElectionProperties(String startDate, String endDate, String name, String desc, int id);
    public void addVotingTable(int ip, int port, int depId);
    public String whereUserVoted(int electionId, int userId);
    //CALLBACK MESAS DE VOTO -- estado mesas de voto
    //CALLBACK MESAS DE VOTO -- número eleitores que votaram em cada mesa de voto até ao momento
    public void endElection();




}
