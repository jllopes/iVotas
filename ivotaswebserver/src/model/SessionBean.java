package model;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import rmiserver.*;


public class SessionBean {
	private RMI_Interface_TCP server;
	private String username;
	private String password;
	
	public SessionBean(){
		try{
	 		System.getProperties().put("java.security.policy", "policy.all");
			System.setSecurityManager(new SecurityManager()); 
		
			//server = (RMI_Interface_TCP) LocateRegistry.getRegistry("127.0.0.1",1099).lookup("IVotas");
			//server = (RMI_Interface_TCP) Naming.lookup("rmi://127.0.0.1:1099/IVotas");
			server = (RMI_Interface_TCP) Naming.lookup("IVotas");

			System.out.println("encontrou rmi");
		}catch(NotBoundException|MalformedURLException|RemoteException e) {
			System.out.println("nao encontrou rmi");
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}

	public boolean login() throws RemoteException{
		if(server.login(this.username, this.password) != 0)
			return true;
		return false;
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
	
	
	
}
