package model;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.util.Pair;
import rmiserver.*;


public class SessionBean {
	private RMI_Interface_Bean server;
	private String username;
	private String password;
	private int userType = 0;
	private int userDep = 0;
	private int userId = 0;
	private int faculty = 0;
	private int userElection = 0;
	private int userVote = 0;
	private int pastElection = 0;
	
	public SessionBean(){
		try{
			/*
	 		System.getProperties().put("java.security.policy", "policy.all");
			System.setSecurityManager(new SecurityManager()); 
			*/
			//server = (RMI_Interface_TCP) LocateRegistry.getRegistry("127.0.0.1",1099).lookup("IVotas");
			server = (RMI_Interface_Bean) Naming.lookup("rmi://127.0.0.1:1099/IVotas");
			//server = (RMI_Interface_TCP) Naming.lookup("IVotas");

			System.out.println("encontrou rmi");
		}catch(NotBoundException|MalformedURLException|RemoteException e) {
			System.out.println("nao encontrou rmi");
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}

	public boolean login() throws RemoteException{
		if( (userType = server.login(this.username, this.password)) != 0){
			HashMap<String, Integer> userInfo = server.getUserId(username);
		    this.userDep = userInfo.get("department");
		    this.userId = userInfo.get("id");
			return true;
		}
		return false;
	}
 	
	public boolean unusedUsername(String username) throws RemoteException {
		return !server.checkUser(username);
	}
	
	public boolean newUser(String username, String password, int userType, String name, int faculty, int department, int id, int month, int year, String phoneNumber, String address) throws RemoteException{
		return server.register(name, username, password, userType, faculty, department, address, id, month, year, phoneNumber);
	}
	
	public HashMap<Pair<String, Integer>, HashMap<String, Integer>> getFaculties() throws RemoteException{
		HashMap<String, Integer> faculties = server.getAllFaculties();
		HashMap<Pair<String, Integer>, HashMap<String, Integer>> facultyInfo = new HashMap<Pair<String, Integer>, HashMap<String, Integer>>();
		Iterator it = faculties.entrySet().iterator();
    		while (it.hasNext()) {
    			Map.Entry<String, Integer> f = (Map.Entry) it.next();
    		    facultyInfo.put(new Pair<String, Integer>(f.getKey(), f.getValue()), this.getDepartmentsFaculty(f.getValue()));
    		}
    		return facultyInfo;
	}
	
	public HashMap<String, Integer> getDepartmentsFaculty(int faculty) throws RemoteException{
		if(faculty != 0) {
		ArrayList<Department> departments = server.getDepartmentsFromFaculty(faculty);
		HashMap<String, Integer> departmentMap = new HashMap<>();
		if(departments != null) {
			for(Department dep : departments) {
				departmentMap.put(dep.name, dep.id);
			}	
		}
		return departmentMap;
		} else {
			HashMap<String, Integer> departmentMap = new HashMap<>();
			departmentMap.put("Select Faculty",0);
			return departmentMap;
		}
	}
	
	public ArrayList<Vote> getUserVotes(int user) throws RemoteException{
		return new ArrayList<Vote>(server.getUserVotes(user));
	}
	
	public ArrayList<Election> getPastElections() throws RemoteException{
		System.out.println(server.getPastElections());
		return server.getPastElections();
	}
	
	public HashMap<String, Integer> getElectionResults() throws RemoteException{
		return server.getElectionResults(this.pastElection);
	}
	
	public HashMap<String, Integer> getDepartments() throws RemoteException{
		ArrayList<Department> departments = server.getAllDepartments();
		HashMap<String, Integer> departmentMap = new HashMap<>();
		if(departments != null) {
			for(Department dep : departments) {
				departmentMap.put(dep.name, dep.id);
			}	
		}
		return departmentMap;
	}
	
	public boolean createElection(String name, String description, String startDate, String endDate, String startTime, String endTime, int department) throws RemoteException, ParseException{
		String start = startDate + " " + startTime;
		String end = endDate + " " + endTime;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dateStart = dateFormat.parse(start);
		Date dateEnd = dateFormat.parse(end);
		return server.createElection(dateStart, dateEnd, name, description, department);
	}
	
	
	public HashMap<Integer, String> getElections() throws RemoteException{
		return server.getElections(this.userType, this.userDep);
	}
	
	public HashMap<Integer, String> getListsElections(int idElection) throws RemoteException {
		return 	server.getListsElections( this.userType, this.userDep, idElection); //arranjar este parametro
	}
	
	public boolean vote(int idElection, int vote) throws RemoteException{		
		if(vote == 0){		
			return  server.vote(this.userId,this.userType, this.userDep, idElection,0,1);		
		}else{		
			return server.vote(this.userId, this.userType, this.userDep, idElection, vote,1);		
		}		
	}
	
	public boolean vote_blank( int idElection) throws RemoteException{		
		return server.vote_blank( this.userId, this.userType, this.userDep, idElection,1);		
	}
	
	/*public HashMap<Integer, String> getPeopleFromList throws RemoteException {
		
	}*/

	public ArrayList<Election> getAllElections() throws RemoteException {
		return server.getAllElections();
	}
	
	public Election getElectionInfo(int idElection) throws RemoteException {
		return server.getElection(idElection);
	}
	
	public ArrayList<String> getPeopleList(int idList) throws RemoteException {
		System.out.println("LISTA " + idList);
		ArrayList<String> candidates = server.getPeopleList(idList);
		for(String s : candidates){
			System.out.println(s);
		}
		return candidates;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUserType() {
		return this.userType;
	}

	public void setUserType(int type) {
		this.userType = type;
	}

	
	public int getUserDep() {
		return userDep;
	}

	
	public void setUserDep(int userDep) {
		this.userDep = userDep;
	}

	
	public int getUserId() {
		return userId;
	}
	

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public ArrayList<User> getAllUsers() throws RemoteException {
		return server.getUsers();
	}
	
	public void changeElection(int id, String name, String description, String startDate, String endDate, String startTime, String endTime) throws ParseException, RemoteException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if(name != null) {
			server.changeElectionName(id, name);
		}
		if(description != null) {
			server.changeElectionDescription(id, description);
		}
		if(startDate != null && startTime != null) {
			String start = startDate + " " + startTime;
			Date dateStart = dateFormat.parse(start);
			server.changeElectionStartDate(id, dateStart);
		}
		if(endDate != null && endTime != null) {
			String end = endDate + " " + endTime;
			Date dateEnd = dateFormat.parse(end);
			server.changeElectionEndDate(id, dateEnd);
		}
	}

	/**
	 * @return the userElection
	 */
	public int getUserElection() {
		return userElection;
	}

	/**
	 * @param userElection the userElection to set
	 */
	public void setUserElection(int userElection) {
		this.userElection = userElection;
	}

	/**
	 * @return the userVote
	 */
	public int getUserVote() {
		return userVote;
	}

	/**
	 * @param userVote the userVote to set
	 */
	public void setUserVote(int userVote) {
		this.userVote = userVote;
	}

	/**
	 * @return the pastElection
	 */
	public int getPastElection() {
		return pastElection;
	}

	/**
	 * @param pastElection the pastElection to set
	 */
	public void setPastElection(int pastElection) {
		this.pastElection = pastElection;
	}
	public HashMap<String, Integer> getElectionVotes(int electionId) throws RemoteException{
		return server.getElectionVotesPerTable( electionId);
	}
	
	
}
