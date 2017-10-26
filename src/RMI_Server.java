import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.sql.*;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.sql.DriverManager;
import java.util.Date;

public class RMI_Server extends UnicastRemoteObject implements RMI_Interface_TCP{
	private int port;
	private String ip;
	private int databasePort;
	private String databaseIP;
	private String databasePass;
	private String databaseUser;
	Connection connection = null;
	
	private static final long serialVersionUID = 1L;

	RMI_Server() throws RemoteException{
		super();
		//Properties https://www.mkyong.com/java/java-properties-file-examples/
		Properties prop = new Properties();
		InputStream input = null;

		try {
			//FileOutputStream file = new FileOutputStream("caralho_onde_está_esta_merda");
			input = new FileInputStream("../rmiconfig.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			port = Integer.parseInt(prop.getProperty("rmi_port"));
			ip = prop.getProperty("rmi_ip");
			databasePort = Integer.parseInt(prop.getProperty("database_port"));
			databaseIP = prop.getProperty("database_IP");
			databasePass = prop.getProperty("database_Pass");
			databaseUser = prop.getProperty("database_User");

			connection = DriverManager.getConnection("jdbc:mysql://"+this.databaseIP+":"+this.databasePort +"/ivotas",this.databaseUser, this.databasePass);
		}catch (SQLException e){
			System.out.println("Database: Cannot connect to database");
			System.exit(0);
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
	
    public boolean register(String name, String username, String password, int type,int id_faculty, int id_departement, String address, int num_id, int month_id, int year_id, String phoneNumber) throws RemoteException{
    	//to be testes
    	try {
			connection.setAutoCommit(false);
			
	    	String sql = "SELECT * FROM person WHERE username = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setString(1,username);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(!rs.next()){
	    		//theres someone with the same username;
	    		return false;
	    	}
	    	rs.close();
	    	
	    	String sql1 = "insert into person(username, password, type, id_faculty, id_departement) values (?,?,?,?,?)";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	int i = 1;
	    	prepStatement1.setString(i++,username);
	    	prepStatement1.setString(i++,password);
	    	prepStatement1.setInt(i++,type);
	    	prepStatement1.setInt(i++,id_faculty);
	    	prepStatement1.setInt(i++,id_departement);
		    prepStatement1.executeUpdate();
		    prepStatement1.close();
		    
		    //retrive id of the created person
		    int id_person = 0;
		    prepStatement.executeQuery();
		    ResultSet rs1 = prepStatement.executeQuery();
		    while(rs1.next()){
		    	id_person = rs1.getInt("id");
		    }
		    rs1.close();
		    
		    String sql2 = "insert into data_person(name, address, cc_number, cc_month, cc_year, phoneNumber,id_person) values (?,?,?,?,?,?)";
		    PreparedStatement prepStatement2 = connection.prepareStatement(sql2);
		    i = 1;
		    prepStatement2.setString(i++, name);
		    prepStatement2.setString(i++, address);
		    prepStatement2.setInt(i++, num_id);
		    prepStatement2.setInt(i++, month_id);
		    prepStatement2.setInt(i++, year_id);
		    prepStatement2.setString(i++, phoneNumber);
		    prepStatement2.setInt(i++,id_person );
		    prepStatement2.executeUpdate();
		    prepStatement2.close();
	    	
		    
	     	return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
			return false;
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	   
    }

    public void addDepartment(String name, int id_faculty) throws RemoteException{

	    try {
	    	connection.setAutoCommit(false);
	    	
		    String sql = "insert into departement(id_faculty, name) values (?,?)";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setInt(1,id_faculty );
		    prepStatement.setString(2, name);
		    prepStatement.executeUpdate();
			prepStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	
    }

    public void addFaculty(String name) throws RemoteException{
	    try {
	    	connection.setAutoCommit(false);
	    	
		    String sql = "insert into faculty(name) values (?)";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setString(1, name);
		    prepStatement.executeUpdate();
			prepStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	
    }
    
    public void changeDeparment(String name, int id) throws RemoteException{

	    try {
	    	connection.setAutoCommit(false);

		    String sql = "update departement set departement.name = ? where departement.id = ?";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setString(1, name);
		    prepStatement.setInt(2,id);
		    prepStatement.executeUpdate();
			prepStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	
    }
    
    public void changeFaculty(String name, int id) throws RemoteException{
	    try {
	    	connection.setAutoCommit(false);
	    	
		    String sql = "update faculty set faculty.name = ? where faculty.id = ?";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setString(1, name);
		    prepStatement.setInt(2, id);
		    prepStatement.executeUpdate();
			prepStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	
    }
  
    public boolean deleteDepartement(int id) throws RemoteException{
    	try {
			connection.setAutoCommit(false);
			
	    	String sql = "select * from election where department_number = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setInt(1,id);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(!rs.next()){
	    		//theres an election on the department
	    		return false;
	    	}
	    	
	    	String sql1 = "select * from person where id_department = ? limit 1";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	prepStatement1.setInt(1,id);
	    	ResultSet rs1 = prepStatement1.executeQuery();
	    	if(!rs1.next()){
	    		//theres an person on the department
	    		return false;
	    	}
	    	
	    	String sql2 = "select * from vote_department where id_department = ? limit 1";
	    	PreparedStatement prepStatement2 = connection.prepareStatement(sql2);
	    	prepStatement2.setInt(1,id);
	    	ResultSet rs2 = prepStatement2.executeQuery();
	    	if(!rs2.next()){
	    		//theres an table_vote on the department
	    		return false;
	    	}
	    	
	    	String sql3 = "select * from vote where id_department = ? limit 1";
	    	PreparedStatement prepStatement3 = connection.prepareStatement(sql3);
	    	prepStatement3.setInt(1,id);
	    	ResultSet rs3 = prepStatement3.executeQuery();
	    	if(!rs3.next()){
	    		//theres an person on the department
	    		return false;
	    	}
	    	
	    	rs.close();
	    	rs1.close();
	    	rs2.close();
	    	rs3.close();
	    	prepStatement.close();
	    	prepStatement1.close();
	    	prepStatement2.close();
	    	prepStatement3.close();
	    	
		    String sql4 = "delete from department where department.id = ?";
		    PreparedStatement prepStatement4 = connection.prepareStatement(sql4);
		    prepStatement4.setInt(1, id);
		    prepStatement4.executeUpdate();
			prepStatement4.close();
	    	
	    	
	     	return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
			return false;
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	
    }

    public boolean deleteFaculty(int id) throws RemoteException{
    	try {
			connection.setAutoCommit(false);
			
	    	String sql = "select * from department where id_faculty = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setInt(1,id);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(!rs.next()){
	    		//theres an election on the faculty
	    		return false;
	    	}
	    	
	    	String sql1 = "select * from person where id_department = ? limit 1";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	prepStatement1.setInt(1,id);
	    	ResultSet rs1 = prepStatement1.executeQuery();
	    	if(!rs1.next()){
	    		//theres an person on the faculty
	    		return false;
	    	}
	    	
	    	rs.close();
	    	rs1.close();
	    	prepStatement.close();
	    	prepStatement1.close();
	    	
		    String sql4 = "delete from department where department.id = ?";
		    PreparedStatement prepStatement4 = connection.prepareStatement(sql4);
		    prepStatement4.setInt(1, id);
		    prepStatement4.executeUpdate();
			prepStatement4.close();
	    	
	    	
	     	return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
			return false;
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}	
    }

    public void createElection(Date startDate, Date endDate, String name, String desc, int id_department) throws RemoteException{
	    //date parser admin side
    	try {
	    	connection.setAutoCommit(false);
	    	/*
				java.text.DateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
				java.util.Date date = format.parse("20110210120534");
				java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				System.out.println(timestamp); // prints "2011-02-10 12:05:34.0"
	    	 * 
	    	 * 
	    	 * */
	    	Timestamp start = new Timestamp(startDate.getTime());
			Timestamp end = new Timestamp(endDate.getTime());
	    	
		    String sql = "insert into election(name,description,start_date, end_date, department_number) values (?,?,?,?,?)";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setString(1,name);
		    prepStatement.setString(2,desc);
		    prepStatement.setTimestamp(3, start); /**/
		    prepStatement.setTimestamp(4, end); /**/
		    prepStatement.setInt(5, id_department);
		    prepStatement.executeUpdate();
			prepStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	
    	
    }

    public void changeElectionProperties(Date startDate, Date endDate, String name, String desc, int id) throws RemoteException{
	    //date parser admin side
    	try {
	    	connection.setAutoCommit(false);
	    	/*
				java.text.DateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
				java.util.Date date = format.parse("20110210120534");
				java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
				System.out.println(timestamp); // prints "2011-02-10 12:05:34.0"
	    	 * 
	    	 * 
	    	 * */
	    	
		    String sql = "update election where election.name = ? and election.description = ? and election.start_date = ? and election.end_date = ? where election.id = ?";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setString(1,name);
		    prepStatement.setString(2,desc);
		    prepStatement.setTimestamp(3, (java.sql.Timestamp) startDate); /**/
		    prepStatement.setTimestamp(4, (java.sql.Timestamp) endDate); /**/
		    prepStatement.setInt(5, id);
		    prepStatement.executeUpdate();
			prepStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
    	
    }

	public boolean addVotingTable(int depId , int electionId) throws RemoteException {
    	try {
	    	connection.setAutoCommit(false);
	    	/* limit 1 vote_table per election ??
	    	String sql1 = "Select 1 from vote_table where id_department = ? limit 1";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	prepStatement1.setInt(1,depId);
	    	
	    	ResultSet rs = prepStatement1.executeQuery();
	    	if(!rs.next()){
	    		//theres an election on the department
	    		return false;
	    	}
	    	rs.close();
	    	*/

			String sql = "insert into vote_table(id,id_election) values (?,?)";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setInt(1,depId );
		    prepStatement.executeUpdate();
			prepStatement.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
				return false;
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return true;

    }

   /* public ArrayList<Vote> getUserVotes(int id){
		ArrayList<Vote> votes = new ArrayList<>();
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from vote where id_person = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			while(rs.next()){
				votes.add();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return false;
	}
	*/

    public String whereUserVoted(int electionId, int userId) throws RemoteException{
    	try {
	    	connection.setAutoCommit(false);
	    	
	    	String sql1 = "Select vote.time_vote, person.username, election.name, department.name from vote, person,election, vote_table, department where vote.id_person = ? and vote.id_election = ? and vote.id_person = person.id and vote.id_election = election.id  and vote.id_table=vote_table.id and vote_table.id_department = department.id;";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	prepStatement1.setInt(1,userId);
	    	prepStatement1.setInt(2,electionId);
	    	ResultSet rs = prepStatement1.executeQuery();
	    	if(rs.next()){
	    		String whereVote = "[" + rs.getTimestamp(1) + "] " + rs.getInt(2) + " vote on election: " + rs.getString(3) + ", in department "+  rs.getString(4);
	    		rs.close();
	    		return whereVote;
	    	}else{
	    		rs.close();
	    		return null;
	    	}
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
    	
    }

	public boolean checkDepartment(int id)throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from departement where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				return true;
			}else{
				return false;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return false;
	}

	public boolean checkFaculty(int id)throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from faculty where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				return true;
			}else{
				return false;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return false;

	}

	public int checkUserType(String username)throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql1 = "select type from person where username = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setString(1,username);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				return rs.getInt("type");
			}else{
				return 0;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return 0;

	}

	public boolean checkElection(int id)throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from election where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				return true;
			}else{
				return false;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return false;
	}

	public HashMap<String, Integer> getAllDeparments() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer>  departments = new HashMap<>();
			String sql1 = "select * from department";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				while(rs.next()) {
					departments.put(rs.getString("name"), rs.getInt("id"));
				}
				return departments;
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}

	public int getDepartment(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select department_number from election where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				return rs.getInt("department_number");
			}else{
				return 0;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return 0;

	}

	public void printUsers(){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from person";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()) {
				while(rs.next()) {
					System.out.println("Name: " + rs.getString("name") + ", Username: " + rs.getString("username") + ", Id: " + rs.getInt("id"));
				}
			}else {
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
	}

	public User getUser(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from person where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				String username = rs.getString("username");
				return new User(username, id);
			}else{
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}

	public Election getElection(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from election where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				String name = rs.getString("name");
				int department = rs.getInt("department_number");
				return new Election(name, id, department);
			}else{
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}

	public VotingTable getVotingTable(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from vote_table where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				int department = rs.getInt("department_number");
				return new VotingTable(department, id);
			}else{
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}

	public ArrayList<Vote> getUserVotes(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from vote where id_person = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			ArrayList<Vote> votes = new ArrayList<>();
			if(rs.next()){
				while(rs.next()) {
					User user = getUser(rs.getInt("id_person"));
					Election election = getElection(rs.getInt("id_election"));
					VotingTable table = getVotingTable(rs.getInt("id_table"));
					Date date = new Date(rs.getTimestamp("time_vote"));
					votes.add(new Vote(user, election, table, date));
				}
				return votes;
			}else{
				return null;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}

	public HashMap<String, Integer> getElectionResults(int id){
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer>  results = new HashMap<>();
			String sql1 = "select * from list_election where id_election = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			String sql2 = "select * from election where id = ? limit 1";
			PreparedStatement prepStatement2 = connection.prepareStatement(sql2);
			prepStatement2.setInt(1,id);
			ResultSet rs2 = prepStatement2.executeQuery();
			if(rs2.next()){
				while(rs.next()){
					String name = rs.getString("name");
					int vote = rs.getInt("vote");
					results.put(name, vote);
				}
				String blank_votes = "blank";
				String null_votes = "null";
				int blank_total = rs2.getInt("vote_blank");
				int null_total = rs2.getInt("vote_null");
				results.put(blank_votes, blank_total);
				results.put(null_votes, null_total);
				return results;
			} else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}

	public int login(String username, String password )throws RemoteException {
    	try {
	    	connection.setAutoCommit(false);

			String sql1 = "Select type from person where username = ? and password = ? limit 1";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	prepStatement1.setString(1,username);
	    	prepStatement1.setString(2,password);
	    	ResultSet rs = prepStatement1.executeQuery();
	    	if(rs.next()){
	    		return rs.getInt("type");
	    	}else{
	    		rs.close();
	    		return 0;
	    	}
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return 0;
		
	}
    
	public HashMap<Integer, String> getElections(int usertype, int userDep) throws RemoteException{
	    try {
	    	connection.setAutoCommit(false);

			String sql1 = "select election.id , election.name from election, list_election  where (election.start_date < current_timestamp() and election.end_date > current_timestamp() and (election.department_number = ? or election.department_number = 0) and  list_election.type= ? and list_election.id_election = election.id ) group by list_election.id_election";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,userDep);
			prepStatement1.setInt(2,usertype);
	    	ResultSet rs = prepStatement1.executeQuery();
	    	
	    	if(rs.next()){ //theres is at least one election
	    		
	    		HashMap<Integer, String> elections = new HashMap<>(); 
	    		elections.put(rs.getInt("election.id"),rs.getString("election.name"));
	    		while(rs.next()){
	        		elections.put(rs.getInt("election.id"),rs.getString("election.name"));
	    		}
	    		rs.close();
	    		return elections;
	    		
	    	}else{
	    		rs.close();
	    		return null;
	    		//return 0;
	    		
	    	}
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}
	
	public HashMap<Integer, String> getListsElections(int usertype, int userDep, int idElection) throws RemoteException {
	    try {
	    	connection.setAutoCommit(false);
			HashMap<Integer, String> elections = getElections(usertype, userDep);
	    	if( elections != null && elections.get(idElection) != null){ //valid election
	    		
		    	String sql1 = "select id , name from  list_election  where id_election=? and type=?";
		    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
		    	prepStatement1.setInt(1,idElection);
		    	prepStatement1.setInt(2,usertype);
		    	ResultSet rs = prepStatement1.executeQuery();
	    		
		    	HashMap<Integer, String> electionlists = new HashMap<>(); 
	    		while(rs.next()){ //no need to verify if theres is lists, getElection does it 
	        		electionlists.put(rs.getInt("id"),rs.getString("name"));
	    		}
	    		rs.close();
	    		return electionlists;

	    	} else
	    		return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
	}

	public boolean vote(int userId, int usertype, int userDep,int idElection, int idList) throws RemoteException{ //false se ja existir voto
		 try {
		    	connection.setAutoCommit(false);
			 HashMap<Integer, String> lists = getListsElections(usertype,userDep,idElection);
		    	if( lists != null && lists.get(idList) != null){ //valid election
		    		//search if theres is a vote already
		    		String getvotes = "Select 1 from votes where id_election = ? and id_person = ?";
		    		PreparedStatement prepStatement2 = connection.prepareStatement(getvotes);
				    prepStatement2.setInt(1, idElection);
				    prepStatement2.setInt(2, userId);
		    		ResultSet rs = prepStatement2.executeQuery();
		    		prepStatement2.close();
		    		if(rs.next()){//already voted
		    			rs.close();
		    			return false;
		    		}
		    		
				    String sql = "insert into vote(id_election, id_person, id_table) values (?,?,?)";
				    PreparedStatement prepStatement = connection.prepareStatement(sql);
				    prepStatement.setInt(1, idElection);
				    prepStatement.setInt(2, userId);
				    prepStatement.setInt(3, idList);

				    prepStatement.executeUpdate();
					prepStatement.close();
					
				    String sql1 = "update list_election set list_election.vote = list_election.vote +1 where list_election.id = ?";
				    PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
				    prepStatement1.setInt(1, idList);
				    prepStatement1.executeUpdate();
					prepStatement1.close();

					//something to tell theres a new vote ¯\_(¨)_/¯
		    		return true;
		    	} else
		    		return false;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					connection.rollback();
				} catch (SQLException e1) {
					System.out.println("DB: Connection lost...");
				}
			} finally {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					System.out.println("DB: Connection lost...");
				}
			}
			return false;

		
		
	}

	public HashMap<String, Integer> getUserId(String username) throws RemoteException {
		try{
			String sql = "select id, id_department from person where username = ? limit 1";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1,username);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()){
				HashMap<String, Integer> userInfo = new HashMap<>();
				userInfo.put("id", rs.getInt("id"));
				userInfo.put("id_department", rs.getInt("id_department"));
				prepStatement.close();
				rs.close();
				return userInfo;
			}
			
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
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

class Vote{
	User user;
	Election election;
	VotingTable table;
	Date date;

	public Vote(User user, Election election, VotingTable table, Date date){
		super();
	}
}

class User{
	String username;
	int id;

	public User(String username, int id){
		super();
	}
}

class Election{
	String name;
	int id;
	Department department;

	public Election(String name, int id, Department department){
		super();
	}
}

class VotingTable{
	Department department;
	int id;

	public VotingTable(Department department, int id){
		super();
	}
}

class Department{
	String name;
	int id;

	public Department(String name, int id){
		super();
	}
}