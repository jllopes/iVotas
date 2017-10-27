import java.io.*;
import java.net.*;
import java.rmi.*;
import java.util.*;
import java.util.List;
import iVotas.Parser;
public class TCP_Server {

	int dc;
	RMI_Interface_TCP rmi;
	ArrayList<String> pedidos_espera;
	ArrayList<Socket> list_socket;
	List<Connection> conns ;
	String rmi_name;
	String rmi_ip;
	int rmi_port;
	int tcp_port;
	int id_table;
	int nTerminais;
	
	public TCP_Server() {
		this.dc = 0;
		pedidos_espera = new ArrayList<>();
		list_socket = new ArrayList<>();
		conns = Collections.synchronizedList(new ArrayList());
		nTerminais = 0;
		//Properties https://www.mkyong.com/java/java-properties-file-examples/
				Properties prop = new Properties();
				InputStream input = null;

				try {
					if(new File("../rmiconfig.properties").exists()){
						input = new FileInputStream("../tcpserverconfig.properties");
					}else
						input = new FileInputStream("tcpserverconfig.properties");

					// load a properties file
					prop.load(input);

					// get the property value and print it out
					rmi_port = Integer.parseInt(prop.getProperty("rmi_port"));
					rmi_ip = prop.getProperty("rmi_ip");
					tcp_port = Integer.parseInt(prop.getProperty("tcp_port"));
					rmi_name = prop.getProperty("rmi_name");
					id_table = Integer.parseInt(prop.getProperty("id_table"));
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

    public boolean unlockTable(String username){
    	try{
			return rmi.checkUser(username);
    			
    	} catch(RemoteException e){
    		if(rmiReconnection(6)){
    			return unlockTable(username);
    		}else 
    			return false;
		} 
    }
	
    public boolean rmiReconnection(int try_attempts){ //try till it finds or runs out of attempts
        if (try_attempts==0){
            return false;
        }
        try {
            this.rmi = (RMI_Interface_TCP) Naming.lookup("rmi://" + this.rmi_ip+ ":" + this.rmi_port+ "/" + this.rmi_name);
            //this.RMI.addTCPServer((RMI_Interface_TCP)this,this.host_port);
            return true;
        } catch (RemoteException | NotBoundException | MalformedURLException e1) {
        	System.out.println("RMI down, attemp to reconnect (" + (6-try_attempts) +")");
            try {
                try_attempts--;
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Error sleep");
            }
            return rmiReconnection(try_attempts);
        }
    }
 
	public static void main(String args[])  {
		TCP_Server tcp = new TCP_Server();
		int count = 0;
		try {

			/* tirar antes de entregar */
			@SuppressWarnings("resource")
			ServerSocket listenSocket = new ServerSocket(tcp.tcp_port);
			System.out.println("Listening to port: " + tcp.tcp_port);
			
			/*rmi*/
			try {
				tcp.rmi = (RMI_Interface_TCP) Naming.lookup("rmi://" + tcp.rmi_ip+ ":" + tcp.rmi_port+ "/" + tcp.rmi_name);
				System.out.println("RMIFound");
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			new Thread(){ //thread in charge to put clients on valid tables
				public void run(){
					@SuppressWarnings("resource")
					Scanner sc = new Scanner(System.in);
					while(true){
						//sc.nextLine();
						String msg = sc.nextLine();
						LinkedHashMap<String, String> input = Parser.parseInput(msg);
						if(!input.containsKey("username"))
							System.out.println("Follow the Protocol, missing username !");
						else if (tcp.unlockTable(input.get("username"))) {
							synchronized(tcp.conns){							
								if(tcp.nTerminais == 0 ) {
									System.out.println("TCP_Client: No TCP_Clients connected at the moment");
								}else {
									int i = 1;
									for(Connection c : tcp.conns){
										i++;
										if(c.getLocked()){ //locked table
											c.unlockTable(input.get("username"));
											System.out.println("TCP_Client: " +c.id + " unlocked");
											break;
										}
									}
									if(i  == tcp.nTerminais + 1){
										System.out.println("TCP_Client: No TCP_Clients available at the moment");
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											System.out.println("Tables full, consider waiting");
										}
									}
								}
							}
						}else {
							System.out.println("Unknown username !");
						}

					}//while(true);
				}
				
			}.start();
			
			
			while (true) {
				Socket clientSocket = listenSocket.accept(); 
				System.out.println("NEW_TABLE (created at accept())=" + clientSocket);
				
				synchronized (listenSocket) {
					tcp.conns.add(new Connection(clientSocket, ++count, tcp));
				}
				tcp.nTerminais ++;
			}
		} catch (IOException e) {
			System.out.println("Listen:" + e.getMessage());
		} 

	}


}

class Connection extends Thread {
    BufferedReader in;
    PrintWriter out;
    Socket clientSocket;
    TCP_Server tcp;
    //userdata
    int id;
    String currentUser = new String();
    int userType = 0;
    int userDep = 0;
    int userId = 0;
    boolean status = false;
    boolean block = true;
    long  time;
    
    public Connection (Socket aClientSocket, int numero, TCP_Server tcp) {
        id = numero;
        this.tcp = tcp;
        try{
            clientSocket = aClientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.start();

        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    
    //=============================
    public void run(){
    	time = System.currentTimeMillis();
        new Thread(){
        	public void run(){
        		while(true){
	            	while(!block){
	            		if(( System.currentTimeMillis()- time)/1000 < 120){ //notlocked
	            			try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								System.out.println("lock check-thread error ");
							}
	            		}else{
	            			logout();
	            			System.out.println("Table locked without action");
	            		}
	            	}
	            	try {
	            		//System.out.println("Table locked, going to sleep 10s ...");
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						System.out.println("Activity thread blocked ");
					}
        		}
        	}
        }.start();
    	
		try {
			while (true){
				String data = in.readLine();
				chooseAction(Parser.parseInput(data));
				time = System.currentTimeMillis();
			}
		} catch(IOException e){ //cliente exit
			System.out.println("Client force left ");
			block = true;
			tcp.nTerminais--;
			tcp.conns.remove(this);
			return;
		}
    }

	private void write(String msg) {
    	this.out.println(msg);
	}

    public void unlockTerminal(String usr) {
        this.out.write("type | unlock; username | " + usr);
    }

    public boolean getStatus() {
        return this.status;
    }

	private void chooseAction(LinkedHashMap<String, String> input) {
		try{
			String type = input.get("type");
			if(!input.containsKey("type")){
				write("type | error ; msg | login missing! ");
				return;
			}
			
			if(!status){
				switch(type){
					case "login":
						if(input.containsKey("username") &&  input.containsKey("password")){
							login(input);
						}else
							write("type | error ; msg | parameters missing! ");
						break;
					case "logout":
						logout();
						break;
					default:
						write("type | " +type +"; msg | login missing! ");
						break;
				}
			}else {
				switch(type){
					case "login":
						login(input); //should be reviewed
						break;
					case "lists":
						
						if(input.containsKey("election")){
							getLists(input);
						}else
							write("type | error ; msg | parameters missing! ");
						break;
					case "election":
						getElections();
						break;
					case "vote":
						if(input.containsKey("election") && input.containsKey("vote")){
							vote(input);
						}else
							write("type | error ; msg | parameters missing! ");
						break;
					case "logout":
						logout();
						break;
					default:
						write("type | error ; msg | unkown type! ");
						break;
				}
			}					
		} catch (NullPointerException e ){
			write("type | error ; msg | missing arguments! ");
		}
		return ;			
	}
	
	private void logout(){
	    this.currentUser = new String();
	    this.userType = 0;
	    this.userDep = 0;
	    this.userId = 0;
	    this.status = false;
	    this.block = true;
	}
	
	private void login(LinkedHashMap<String, String> input){
		String username = input.get("username");
		String password = input.get("password");

		int type;
		try {
			type = tcp.rmi.login(username, password);
			if(type != 0 && this.currentUser.equals(username)){
				write("type | status ; logged | on ; mswg | Welcome to iVotas " + username+ " !");
				this.currentUser = username;
				this.userType = type;
				HashMap<String, Integer> userInfo = tcp.rmi.getUserId(username);
			    this.userDep = userInfo.get("id_department");
			    this.userId = userInfo.get("id");
				this.status = true;
				if(userId == 0){ //should not happen{
					write("type | status ; logged | off ; msg | Incorrect identification!");
					return;
				}
				HashMap<Integer, String> elections = tcp.rmi.getElections(type, userDep);
				if(elections != null){
					write(Parser.HashmapToStringProtocol("election", elections));
				}else
					write("type | election ; msg | No elections occuring at the moment!");
					
			}else{
				write("type | status ; logged | off ; msg | Incorrect identification!");
			}
			return;
		} catch (RemoteException e) {
			if( tcp.rmiReconnection(6)){
				login(input);
			} else {
				write("type | Internal Error; msg | RMI down");
			}
		}
	}

	private void getElections() {
		HashMap<Integer, String> elections;
		try {
			elections = tcp.rmi.getElections(this.userType, this.userDep);
			if(elections != null){
				write(Parser.HashmapToStringProtocol("election", elections));
			}else
				write("type | election ; msg | No elections occuring at the moment!");
		
		} catch (RemoteException e) {
			if( tcp.rmiReconnection(6)){
				getElections();
			} else {
				write("type | Internal Error; msg | RMI down");
			}
		}
}

	private void getLists(LinkedHashMap<String, String> input) {
		try{
			int id_election = Integer.parseInt(input.get("election"));
			HashMap<Integer, String> lists = tcp.rmi.getListsElections(this.userType, this.userDep, id_election);
			write(Parser.HashmapToStringProtocol("ElectionLists", lists));
		}catch(NumberFormatException e1) {
			write("type | lists; msg | invalid election id");
		}catch(RemoteException e ){
			if( tcp.rmiReconnection(6)){
				getLists(input);
			} else {
				write("type | Internal Error; msg | RMI down");
			}
		}		
	}	

	private void vote(LinkedHashMap<String, String> input){
		try{
			int id_election = Integer.parseInt(input.get("election"));
			int vote = Integer.parseInt(input.get("vote")); 
			// vote = 0 blank , lista invalida -> nulo, lista valida -> new vote
			
			HashMap<Integer, String> lists = tcp.rmi.getListsElections(this.userType, this.userDep, id_election);
			if(vote == 0){
				//insert vote blank
				boolean answer = tcp.rmi.vote_blank(this.userId , this.userType, userDep, id_election) ;
				if(answer){
					write("type | vote ; msg: Success! (blank); "); //just for testing
				}else{
					write("type | vote ; msg: Vote invalid or already voted; (blank)"); //just for testing
				}
			}else {
				boolean validation = tcp.rmi.vote(this.userId ,this.userType, userDep, id_election, vote/* , tcp.id_table*/ );
				if(validation){
					write("type | vote ; msg: Success!; ");
				}else{
					write("type | vote ; msg: Vote invalid or already voted; ");
				}
				
			}
			
			write(Parser.HashmapToStringProtocol("ElectionLists", lists));
			
			
		}catch(NumberFormatException e1) {
			write("type | lists; msg | invalid election id");
		}catch(RemoteException e ){
			if( tcp.rmiReconnection(6)){
				vote(input);
			} else {
				write("type | Internal Error; msg | RMI down");
			}
		}		
		
	}

	public boolean getLocked(){
		return this.block;
	}
	
	public void unlockTable(String username){
		this.block = false;
		this.currentUser = username;
		write("type | unlock ; msg | Table unlocked for " + username );
	}
}