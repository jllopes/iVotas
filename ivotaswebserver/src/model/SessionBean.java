package model;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashMap;

import rmiserver.*;


public class SessionBean {
	private RMI_Interface_TCP server;
	private String username;
	private String password;
	private int userType = 0;
	private int userDep = 0;
	private int userId = 0;
	
	
	
	public SessionBean(){
		try{
			/*
	 		System.getProperties().put("java.security.policy", "policy.all");
			System.setSecurityManager(new SecurityManager()); 
			*/
			//server = (RMI_Interface_TCP) LocateRegistry.getRegistry("127.0.0.1",1099).lookup("IVotas");
			server = (RMI_Interface_TCP) Naming.lookup("rmi://127.0.0.1:1099/IVotas");
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
 	
	public HashMap<Integer, String> getElections() throws RemoteException{
		return server.getElections(this.userType, this.userDep);
	}
	
	public HashMap<Integer, String> getListsElections() throws RemoteException {
		return 	server.getListsElections( this.userType, this.userDep, idElection); //arranjar este parametro
	}
	
	public HashMap<Integer, String> getPeopleFromList throws RemoteException {
		
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
	
	
	
}
