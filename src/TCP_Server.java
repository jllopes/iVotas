import java.io.*;
import java.net.*;
import java.rmi.*;
import java.util.*;

public class TCP_Server {

	int dc;
	RMI_Interface_TCP rmi;
	ArrayList<String> pedidos_espera;
	ArrayList<Socket> list_socket;
	ArrayList<Connection> conns;
	String rmi_name;
	String rmi_ip;
	int rmi_port;
	int tcp_port;
	int id_table;
	
	public TCP_Server() {
		this.dc = 0;
		pedidos_espera = new ArrayList<>();
		list_socket = new ArrayList<>();
		conns = new ArrayList<>();

		//Properties https://www.mkyong.com/java/java-properties-file-examples/
				Properties prop = new Properties();
				InputStream input = null;

				try {

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
			
			//tcp.id_table = handShake();
			
			
			while (true) {
				Socket clientSocket = listenSocket.accept(); 
				System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
				
				synchronized (listenSocket) {
					tcp.conns.add(new Connection(clientSocket, ++count, tcp));
				}
				
			}
		} catch (IOException e) {
			System.out.println("Listen:" + e.getMessage());
		} 

	}

	public int handShake(int id_election, int id_table){
		try {
			return this.rmi.assignTable();
		} catch (RemoteException e) {
			System.out.println("RMI: Cannot register table");
			System.exit(0);
			return 0;
		}
	}

	//timeout
    boolean rmiReconnection(int try_attempts){ //try till it finds or runs out of attempts
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

	
	public void updateTime(){ //estas aqui mano
		
		
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
    long  time = System.currentTimeMillis();
    
    public Connection (Socket aClientSocket, int numero, TCP_Server tcp) {
        id = numero;
        this.tcp = tcp;
        try{
            clientSocket = aClientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            new Thread(){
            	public void run(){
                	boolean unlock = true;
                	while(unlock){
                		if(( System.currentTimeMillis()- time)/1000 < 120){ //notlocked
                			try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								System.out.println("lock check-thread error ");
							}
                		}else{
                			unlock = false;
                			System.out.println("mesa sem a�ao, dar lock");
                		}
                	}
            	}
            	
            }.start();
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    
    //=============================
    public void run(){
        while(true){
			try {
				String data = in.readLine();
				chooseAction(parseInput(data));
				time = System.currentTimeMillis();
			} catch(IOException e){ //cliente exit
				System.out.println("Client force left ");
				return;
			}
		}
    }

	private LinkedHashMap<String, String> parseInput(String input) throws RemoteException{

		String[] aux;
		LinkedHashMap<String, String> hashmap = new LinkedHashMap<String, String>();
		aux = input.split(";");
		for (String field : aux) {
			try {
				String[] split = field.split("\\|"); // | representa a função OR por isso tem de ser \\|
				String firstString = split[0].trim();
				String secondString = split[1].trim();
				hashmap.put(firstString, secondString);
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("Message doesnt follow the protocol");
				return null;
			}
		}
		return hashmap;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public String HashmapToStringProtocol(String name, HashMap<Integer,String> hashmap){
	    Iterator it = hashmap.entrySet().iterator();
	    int i = 0;
    	String str = "type | "+name+"_list; "+name+"_count: "+hashmap.size()+"; ";
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
			str += name+"_" + i + "_id | " + pair.getKey() + "; " + name+"_" + i  + "_name | " + pair.getValue() + "; ";
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    System.out.println(str);
		return str;
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
					default:
						//run(); ?? lul
						write("type | error ; msg | unkown type! ");
						break;
				}
			}					
		} catch (NullPointerException e ){
			write("type | error ; msg | missing arguments! ");
		}
		return ;			
	}

	private void login(LinkedHashMap<String, String> input){
		String username = input.get("username");
		String password = input.get("password");

		int type;
		try {
			type = tcp.rmi.login(username, password);
			if(type != 0) {
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
					write(HashmapToStringProtocol("election", elections));
				}else
					write("type | election ; msg | No elections occuring at the moment!");
					
			}
			else{
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
				write(HashmapToStringProtocol("election", elections));
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
			write(HashmapToStringProtocol("ElectionLists", lists));
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
				boolean validation = tcp.rmi.vote(this.userId ,this.userType, userDep, id_election, vote , tcp.id_table );
				if(validation){
					write("type | vote ; msg: Success!; ");
				}else{
					write("type | vote ; msg: Vote invalid or already voted; ");
				}
				
			}
			
			write(HashmapToStringProtocol("ElectionLists", lists));
			
			
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
}