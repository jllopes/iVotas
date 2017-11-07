import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.sql.*;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.Date;
public class RMI_Server extends UnicastRemoteObject implements RMI_Interface_TCP , RMI_Interface_Admin{
	private int port;
	private String ip;
	private int databasePort;
	private String databaseIP;
	private String databasePass;
	private String databaseUser;
	List<Admin_Interface_RMI> admins;
	List<Integer> tables;
	Connection connection = null;
	
	private static final long serialVersionUID = 1L;

	RMI_Server() throws RemoteException{
		super();
		//Properties https://www.mkyong.com/java/java-properties-file-examples/
		Properties prop = new Properties();
		InputStream input = null;
		this.admins = Collections.synchronizedList(new ArrayList<>());
		this.tables = Collections.synchronizedList(new ArrayList<>());
		try {
			if(new File("../rmiconfig.properties").exists()){
				input = new FileInputStream("../rmiconfig.properties");
			}else
				input = new FileInputStream("rmiconfig.properties");
			// load a properties file
			prop.load(input);

			// get the property value and print it out
			port = Integer.parseInt(prop.getProperty("rmi_port"));
			ip = prop.getProperty("rmi_ip");
			databasePort = Integer.parseInt(prop.getProperty("database_port"));
			databaseIP = prop.getProperty("database_IP");
			databasePass = prop.getProperty("database_Pass");
			databaseUser = prop.getProperty("database_User");
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://"+this.databaseIP+":"+this.databasePort +"/ivotas",this.databaseUser, this.databasePass);
		}catch (SQLException e){
			System.out.println("Database: Cannot connect to database");
			System.exit(0);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
	
	public boolean checkUser(String username) throws RemoteException {
		try {
			connection.setAutoCommit(false);
	    	String sql = "SELECT * FROM person WHERE username = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setString(1,username);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(rs.next()){
	    		//theres someone with the same username;
	    		return true;
	    	}
	    	rs.close();
	    	return false;
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
	
    public boolean register(String name, String username, String password, int type,int id_faculty, int id_departement, String address, int num_id, int month_id, int year_id, String phoneNumber) throws RemoteException{
    	//to be testes
    	try {
			connection.setAutoCommit(false);
			
	    	String sql = "SELECT * FROM person WHERE username = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setString(1,username);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(rs.next()){
	    		//theres someone with the same username;
	    		return false;
	    	}
	    	rs.close();
	    	
	    	String sql1 = "insert into person(username, password, type, id_faculty, id_department) values (?,?,?,?,?)";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	prepStatement1.setString(1,username);
	    	prepStatement1.setString(2,password);
	    	prepStatement1.setInt(3,type);
	    	prepStatement1.setInt(4,id_faculty);
	    	prepStatement1.setInt(5,id_departement);
	    	System.out.println(prepStatement1);
	    	System.out.println("here");
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
		    
		    String sql2 = "insert into data_person(name, address, cc_number, cc_month, cc_year, phoneNumber,id_person) values (?,?,?,?,?,?,?)";
		    PreparedStatement prepStatement2 = connection.prepareStatement(sql2);
		    prepStatement2.setString(1, name);
		    prepStatement2.setString(2, address);
		    prepStatement2.setInt(3, num_id);
		    prepStatement2.setInt(4, month_id);
		    prepStatement2.setInt(5, year_id);
		    prepStatement2.setString(6, phoneNumber);
		    prepStatement2.setInt(7,id_person );
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

		    String sql = "insert into department(id_faculty, name) values (?,?)";
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

	public void createList(String name, int type, int election) throws RemoteException{

		try {
			connection.setAutoCommit(false);

			String sql = "insert into list_election(name, id_election, type) values (?,?,?)";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1,name);
			prepStatement.setInt(2,election );
			prepStatement.setInt(3, type);
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

		    String sql = "update department set department.name = ? where department.id = ?";
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
	    	if(rs.next()){
	    		//theres a department on the faculty
	    		return false;
	    	}
	    	
	    	rs.close();
	    	prepStatement.close();
	    	
		    String sql4 = "delete from faculty where faculty.id = ?";
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

	public void changeElectionStartDate(int id, Date startDate) throws RemoteException{
		//date parser admin side
		try {
			connection.setAutoCommit(false);
			String sql = "update election set election.start_date = ? where election.id = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setTimestamp(1, new Timestamp(startDate.getTime()));
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

	public void changeElectionEndDate(int id, Date endDate) throws RemoteException{
		//date parser admin side
		try {
			connection.setAutoCommit(false);
			String sql = "update election set election.end_date = ? where election.id = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setTimestamp(1, new Timestamp(endDate.getTime()));
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

	public void changeElectionName(int id, String name) throws RemoteException{
		//date parser admin side
		try {
			connection.setAutoCommit(false);
			String sql = "update election set election.name = ? where election.id = ?";
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

	public void changeElectionDescription(int id, String desc) throws RemoteException{
		//date parser admin side
		try {
			connection.setAutoCommit(false);
			String sql = "update election set election.description = ? where election.id = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, desc);
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

	public void addCandidatesToList(int list, ArrayList<Integer> users) throws RemoteException{
		for(int user : users) {
			try {
				connection.setAutoCommit(false);

				String sql = "insert into person_list(id_person, id_list) values (?,?)";
				PreparedStatement prepStatement = connection.prepareStatement(sql);
				prepStatement.setInt(1, user);
				prepStatement.setInt(2, list);
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
	}

	public boolean addVotingTable(int depId) throws RemoteException {
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

			String sql = "insert into vote_table(id_department) values (?)";
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

	public void addTable(int idTable) throws RemoteException{
		synchronized(tables){
			for(Integer table : tables){
				if(table == idTable){
					return;
				}
			}
			tables.add(idTable);
		}
	}
	public void removeTable(int idTable) throws RemoteException{
		synchronized(tables){
			tables.remove((Object)idTable);
		}

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
    public List<Integer> getOnlineTables() throws RemoteException{    	
    	return this.tables;
    }
	
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

			String sql1 = "select * from department where id = ? limit 1";
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

	public int checkUserType(int id)throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql1 = "select type from person where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
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

	public int getListType(int id)throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql1 = "select type from list_election where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
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

	public HashMap<String,Integer> getPastElections() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer> elections = new HashMap<>();
			String sql1 = "select * from election";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			ResultSet rs = prepStatement1.executeQuery();
			Date current = new Date();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					Date date = rs.getTimestamp("end_date");
					if(date.before(current)){
						elections.put(rs.getString("name"), rs.getInt("id"));
					}
				}
				return elections;
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

	public HashMap<String,Integer> getAllElections() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer> elections = new HashMap<>();
			String sql1 = "select * from election";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			ResultSet rs = prepStatement1.executeQuery();
			Date current = new Date();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					elections.put(rs.getString("name"), rs.getInt("id"));
				}
				return elections;
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

	public HashMap<String, Integer> getAllDepartments() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer>  departments = new HashMap<>();
			String sql1 = "select * from department";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				rs.beforeFirst();
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

	public HashMap<String, Integer> getAllFaculties() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer>  departments = new HashMap<>();
			String sql1 = "select * from faculty";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				rs.beforeFirst();
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

	public HashMap<String, Integer> getDepartmentsFromFaculty(int faculty) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer> lists = new HashMap<>();
			String sql1 = "select * from department where id_faculty = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,faculty);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					lists.put(rs.getString("name"), rs.getInt("id"));
				}
				return lists;
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

	public HashMap<String,Integer> getElectionLists(int election) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer> lists = new HashMap<>();
			String sql1 = "select * from list_election where id_election = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,election);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					lists.put(rs.getString("name"), rs.getInt("id"));
				}
				return lists;
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

	public int getDepartmentNumber(int id){
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
				rs.beforeFirst();
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
				Department department = getDepartment(rs.getInt("department_number"));
				Election election = new Election(name, id, department);
				System.out.println("wtf" + election.name +  " " + election.id + " " + election.department.name);
				return election;
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

	public Lista getList(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from list_election where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				String name = rs.getString("name");
				Election election = getElection(rs.getInt("id_election"));
				int votes = rs.getInt("vote");
				return new Lista(name, election, votes);
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

	public Department getDepartment(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from department where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				System.out.println("id " + id);
				String name = rs.getString("name");
				System.out.println(", " + name);
				Department department = new Department(name, id);
				System.out.println(department.name);
				return department;
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
				Department department = getDepartment(rs.getInt("id_department"));
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

	public List<Vote> getUserVotes(int id) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from vote where id_person = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			List<Vote> votes;
			votes = Collections.synchronizedList(new ArrayList());
			if (rs.next()) {
				rs.beforeFirst();
				while (rs.next()) {
					User user = getUser(rs.getInt("id_person"));
					Election election = getElection(rs.getInt("id_election"));
					VotingTable table = getVotingTable(rs.getInt("id_table"));
					Date date = new Date(rs.getTimestamp("time_vote").getTime());
					votes.add(new Vote(user, election, table, date));
				}
				return votes;
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

	public HashMap<String, Integer> getUserVotedElections(int id) throws RemoteException{
		HashMap<String, Integer> elections = new HashMap<>();
		List<Vote> votes = getUserVotes(id);
		if(votes == null)
			return null;
		for(Vote vote: votes){
			System.out.println(vote.election.name);
			elections.put(vote.election.name, vote.election.id);
		}
		return elections;
	}

	public HashMap<Date, String> getUserVoteDetails(int id, int election) throws RemoteException{
		HashMap<Date, String> details = new HashMap<>();
		List<Vote> votes = getUserVotes(id);
		for(Vote vote: votes){
			if(vote.election.id == election){
				details.put(vote.date, vote.table.department.name);
			}
		}
		return details;
	}

	public HashMap<String, Integer> getElectionResults(int id)throws RemoteException{
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

	public HashMap<String, Integer> getUsers() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from person";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			ResultSet rs = prepStatement1.executeQuery();

			if(rs.next()){ //theres is at least one election
				HashMap<String, Integer> users = new HashMap<>();
				rs.beforeFirst();
				while(rs.next()){
					users.put(rs.getString("username"),rs.getInt("id"));
				}
				rs.close();
				return users;

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
	    		rs.beforeFirst();
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

	@SuppressWarnings("rawtypes")
	public boolean vote(int userId, int userType, int userDep, int id_election, int vote, int id_table) throws RemoteException{
		 try { //if no vote or vote = 0 is veryfied on tcp
		    connection.setAutoCommit(false); 
			 HashMap<Integer, String> lists = getListsElections(userType,userDep,id_election);
		    		if( lists != null){ //valid election
		    		    Iterator it = lists.entrySet().iterator();
		    		    while (it.hasNext()) {
				    		//search if theres is a vote already
		    		        Map.Entry pair = (Map.Entry)it.next();
		    		    	if( (Integer)pair.getKey() == vote){
					    		String getvotes = "Select 1 from vote where id_election = ? and id_person = ?";
					    		PreparedStatement prepStatement2 = connection.prepareStatement(getvotes);
							    prepStatement2.setInt(1, id_election);
							    prepStatement2.setInt(2, userId);
					    		ResultSet rs = prepStatement2.executeQuery();
					    		if(rs.next()){//already voted
					    			System.out.println("ja votou");
						    		prepStatement2.close();
					    			rs.close();
					    			return false;
					    		}
					    		Lista list = getList(vote);
					    		String str = "List " + list.name + " got one more vote, they now have " + list.votes + " votes.";
					    		sendNotification(str);
			
							    String sql = "insert into vote(id_election, id_person, id_table) values (?,?,?)";
							    PreparedStatement prepStatement = connection.prepareStatement(sql);
							    prepStatement.setInt(1, id_election);
							    prepStatement.setInt(2, userId);
							    prepStatement.setInt(3, id_table);
			
							    prepStatement.executeUpdate();
								prepStatement.close();
								
							    String sql1 = "update list_election set list_election.vote = list_election.vote +1 where list_election.id = ?";
							    PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
							    prepStatement1.setInt(1, id_election);
							    prepStatement1.executeUpdate();
								prepStatement1.close();
		
								//something to tell theres a new vote ¯\_(¨)_/¯
					    		return true;
		    		    	}else{
		    			        it.remove(); // avoids a ConcurrentModificationException
		    		    	}
		    		    } 
		    		    System.out.println("lista nao found");
					    String nullVote = "update election set election.vote_blank =  election.vote_blank +1 where election.id = ?";
					    PreparedStatement prepNullStatement = connection.prepareStatement(nullVote);
					    prepNullStatement.setInt(1, id_election);
					    prepNullStatement.executeUpdate();
					    prepNullStatement.close();
		    		    return true;
		    	} else{
		    		System.out.println("random vote false");

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

	public boolean vote_blank(int userid, int usertype, int userDep, int idElection) throws RemoteException {
	    try {
	    	String getvotes = "Select 1 from vote where id_election = ? and id_person = ?";
    		PreparedStatement prepStatement2 = connection.prepareStatement(getvotes);
		    prepStatement2.setInt(1, idElection);
		    prepStatement2.setInt(2, userid);
    		ResultSet rs = prepStatement2.executeQuery();
    		if(rs.next()){//already voted
	    		prepStatement2.close();
    			rs.close();
    			return false;
    		}
    		String str = "New blank vote on election " + idElection + " .";
    		sendNotification(str);
	    	
	    	
		    String nullVote = "update election set election.vote_null =  election.vote_blank +1 where election.id = ?";
		    PreparedStatement prepNullStatement = connection.prepareStatement(nullVote);
			prepNullStatement.setInt(1, idElection);
		    prepNullStatement.executeUpdate();
		    prepNullStatement.close();
		    return true;
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

	public void setAdmin(Admin_Interface_RMI admin)throws RemoteException{
		admins.add(admin);
	}

	public void sendNotification(String str) throws RemoteException{
		for(Admin_Interface_RMI admin : admins) {
			try {
				admin.receiveNotification(str);
			} catch (NullPointerException e){
				admins.remove(admin);
			}
		}
	}

	public void changeDepartment(String newName, int id) throws RemoteException {
		try {
				connection.setAutoCommit(false);

				String sql = "update department set department.name = ? where department.id = ?";
				PreparedStatement prepStatement = connection.prepareStatement(sql);
				prepStatement.setString(1, newName);
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
		
	public boolean deleteDepartment(int id) throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql = "select * from election where department_number = ? limit 1";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,id);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()){
				//theres an election on the faculty
				return false;
			}

			String sql1 = "select * from person where id_department = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs1 = prepStatement1.executeQuery();
			if(rs1.next()){
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

	public void removeList(int id) throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql4 = "delete from list_election where list_election.id = ?";
			PreparedStatement prepStatement4 = connection.prepareStatement(sql4);
			prepStatement4.setInt(1, id);
			prepStatement4.executeUpdate();
			prepStatement4.close();


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

	public static void main(String args[]) {
		
		/*
		 		System.getProperties().put("java.security.policy", "policy.all");
				System.setSecurityManager(new RMISecurityManager()); 
		 */
		
		try {
			RMI_Server h = new RMI_Server();
			LocateRegistry.createRegistry(h.port).rebind("IVotas", h);

			System.out.println("IVotas ready.");
			/*new Thread() {
				public void run() {
					while(true) {
						h.endElections();
						try {
							Thread.sleep(40000);
						} catch (InterruptedException e) {
							System.out.println("Problem with end auctions thread!");
						}
					}
				}
			}.start();*/
			// main server
		} catch (RemoteException re) {
			System.out.println("RMI could not be created, lauching secundary");
			start();
			return;
		}
	}

}