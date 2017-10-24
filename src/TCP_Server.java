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


			new Thread(){
				public void run() {
					try {
						while (true) {
							Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
							tcp.conns.add(new Connection(clientSocket, ++count, tcp.rmi));

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
			while (true) {
				Socket clientSocket = listenSocket.accept(); 
				System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
				
				synchronized (listenSocket) {
					tcp.conns.add(new Connection(clientSocket, ++count,))
				}
				
			}

		} catch (IOException e) {
			System.out.println("Listen:" + e.getMessage());
		}

	}
}

	private static void parseInput(String input){

		String[] aux;
		LinkedHashMap<String, String> hashmap = new LinkedHashMap<String, String>();
		aux = input.trim().split(";");
		for (String field : aux) {
			try {
				String[] split = field.split("\\|"); // | representa a função OR por isso tem de ser \\|
				String firstString = split[0].trim();
				String secondString = split[1].trim();
				hashmap.put(firstString, secondString);
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}
		//chosenType(parsedInput);
	}

	private void chooseAction(LinkedHashMap<String, String> input){
		String type = input.get("type");

		switch(type){
			case "login":
				login(input);
				break;
			case "candidate_list":
				candidateList();
				break;
			default:
				run();
				break;
		}
	}

	private void login(LinkedHashMap<String, String> input, Connection client) throws RemoteException{
		String username = input.get("username");
		String password = input.get("password");
		if(rmi.login(username, password)) {
			client.write("type | status ; logged | on ; msg | Welcome to iVotas!");
			int type = rmi.getUserType(username);
			rmi.getElections()
		}
		else{
			client.write("type | status ; logged | off ; msg | Incorrect identification!");
		}
	}

	private void getElections(String username) throws RemoteException{
		int type = rmi.getUserType(username);
		LinkedHashMap<Integer, String> elections =
	}

	private void candidateList(LinkedHashMap<String, String> input){
		int count = Integer.parseInt(input.get("item_count"));
		ArrayList<String> names = new ArrayList<String>();
		String name;
		for(int i=0; i<count; i++){
			name = input.get("candidate_" + i + "_name");
			names.add(name);
		}
	}

	private void hide() {
/*

    public static boolean checkUserByName(String str){
        //for(voter:voters)
        //if(voter.name = str)
        //return true
        //return false
    }

    public static boolean checkUserById(int num){
        //for(voter:voters)
        //if(voter.id = num)
        //return true
        //return false
    }

    public static boolean checkUserByUsername(String str){
        //for(voter:voters)
        //if(voter.username = str)
        //return true
        //return false
    }

    public static User getUser(String usr) {
        User aux;
        for(User user: users) {
            if(user.username == usr) {
                aux = user;
            }
        }
        return aux;
    }

    public static void login(String usr, Scanner keyboardScanner) {
        System.out.println("Please insert your password");
        String pw = keyboardScanner.nextLine();
        User aux = getUser(usr);
        while(!verifyPassword(aux, pw)) {
            System.out.println("Password is incorrect, please try again.");
        }
    }

    public boolean void verifyPassword(User usr, String pw) {
        if (usr.password == pw)
            return true;
    }
    return false;
    }

    public static void printInfoOptions() {
        System.out.println("Please choose what information you want to identify the voter by:");
        System.out.println("<1> Name");
        System.out.println("<2> Id Number");
        System.out.println("<3> Username");
    }
    public static void unlockTerminal(Socket socket, PrintWriter outToClient, String usr){
        outToClient.println("type | unlock; username |" + usr)
    }

    public static void checkNumber(int number) {
        //Verificar número de eleitor e retornar detalhes
        //for ... eleitores
        // if eleitor.num = number
        // eleitor.toString()
    }

    public static void printElections() {
        //getElections()
        //System.out.println("Choose an election")
        //read choice
    }

    public static void getElections() {
    }

    //type | login; username | pierre ; password | omidyar
    //type | status; logged | on; msg | Welcome to iVotas
    //type | unlock --  //type | lock
    //type | vote; option | x; election | y
    //type | voter;

}
*/

	}

class Connection extends Thread {
    BufferedReader in;
    PrintWriter out;
    Socket clientSocket;
    RMI_Interface_TCP rmi;
    int id;

    public Connection (Socket aClientSocket, int numero, RMI_Interface_TCP rmi) {
        id = numero;
        this.rmi = rmi;
        try{
            clientSocket = aClientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    //=============================
    public void run(){
        try{
            while(true){
            	try {
					String data = in.readLine();
					parseInput(data);
				} catch(IOException e){
            		e.printStackTrace();
				}
            }
        }catch(EOFException e){System.out.println("EOF:" + e);
        }catch(IOException e){System.out.println("IO:" + e);}
    }

	private void parseInput(String input) throws RemoteException{

		String[] aux;
		LinkedHashMap<String, String> hashmap = new LinkedHashMap<String, String>();
		aux = input.trim().split(";");
		for (String field : aux) {
			try {
				String[] split = field.split("\\|"); // | representa a função OR por isso tem de ser \\|
				String firstString = split[0].trim();
				String secondString = split[1].trim();
				hashmap.put(firstString, secondString);
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}
		chooseAction(input);
	}

	private void chooseAction(LinkedHashMap<String, String> input) throws RemoteException {
		String type = input.get("type");

		switch(type){
			case "login":
				login(input);
				break;
			default:
				run();
				break;
		}
	}

	private void login(LinkedHashMap<String, String> input) throws RemoteException{
		String username = input.get("username");
		String password = input.get("password");
		if(rmi.login(username, password)) {
			write("type | status ; logged | on ; msg | Welcome to iVotas!");
			int type = rmi.getUserType(username);
			rmi.getElections();
		}
		else{
			write("type | status ; logged | off ; msg | Incorrect identification!");
		}
	}

	private void getElections(String username) throws RemoteException{
		int type = rmi.getUserType(username);
		LinkedHashMap<Integer, String> elections = rmi.getElections(type);
		write
	}

	private String createElectionString(LinkedHashMap<Integer, String> elections) throws RemoteException{
    	String str = "type | election_list ; ";
    	int i = 0;
		elections.forEach((Integer key, String value) -> {
			str += "election_" + i + "_id | " + key + " ; " + "election_" + i + "_name | " + value + " ; ";
			i++;
		});
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
}