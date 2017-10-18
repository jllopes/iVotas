import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


public class RMI_Server extends UnicastRemoteObject implements RMI_Interface_TCP{
	private int port;
	private String ip;
	private static final long serialVersionUID = 1L;

	RMI_Server() throws RemoteException{
		super();
		//Properties https://www.mkyong.com/java/java-properties-file-examples/
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("rmiconfig.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			port = Integer.parseInt(prop.getProperty("rmi_port"));
			ip = prop.getProperty("rmi_ip");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public String message() throws java.rmi.RemoteException {
		return "ACK";
	}
	
	public static void start(){
		//second server calls 
			try {
				RMI_Server server = new RMI_Server();
				RMI_Interface_TCP h = (RMI_Interface_TCP) Naming.lookup("rmi://"+server.ip+":"+server.port+"/IVotas");
				String message = h.message();
				System.out.println(message);
				try {
					Thread.sleep(2000);
					start(); //recursive call till it became primary server
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (MalformedURLException | NotBoundException e1) {

				e1.printStackTrace();

				
			} catch ( RemoteException e2){
				//main rmi didnt answer
				int i = 1;
				while(i<5){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						RMI_Server server = new RMI_Server();

						RMI_Interface_TCP h = (RMI_Interface_TCP) Naming.lookup("rmi://"+server.ip+":"+server.port+"/IVotas");
						String message = h.message();
						System.out.println(message);
						start();
					} catch (MalformedURLException | RemoteException | NotBoundException e) {
						System.out.println("Main server down " +i+ " already ...");
						i++;
					}
				}
				
				System.out.println("Primary RMI down, secundary server taking over...");
				try {
					RMI_Server h = new RMI_Server();
					LocateRegistry.createRegistry(1099).rebind("IVotas", h);
					System.out.println("IVotas ready.");
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				
			}
			


		return;
	}
	

	
	public static void main(String args[]) {
		
		/*
		 		System.getProperties().put("java.security.policy", "policy.all");
				System.setSecurityManager(new RMISecurityManager()); 
		 */
		
		try {
			RMI_Server h = new RMI_Server();
			LocateRegistry.createRegistry(h.port).rebind("IVotas", h);

			System.out.println("IVotas ready.");
			// main server
		} catch (RemoteException re) {
			System.out.println("RMI could not be created, lauching secundary");
			start();
			return;
		}
	}


}
