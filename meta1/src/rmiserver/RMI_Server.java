package rmiserver;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.sql.*;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.Date;
public class RMI_Server extends UnicastRemoteObject implements RMI_Interface_TCP , RMI_Interface_Admin, RMI_Interface_Bean{
	private int port;
	private String ip;
	private int databasePort;
	private String databaseIP;
	private String databasePass;
	private String databaseUser;
	List<Admin_Interface_RMI> admins;
	List<TCP_Interface> tables;
	Connection connection = null;
	
	private static final long serialVersionUID = 1L;

	RMI_Server() throws RemoteException{
		super();
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
			System.out.println("IVotas ready.");
			new Thread() {
				public void run() {
					while(true) {

						try {			
							ArrayList<TCP_Interface> toBeRemoved = new ArrayList<>();
							synchronized(tables){
								for(TCP_Interface t : tables){
									try {
										t.ping("Ping");
									} catch (NullPointerException e) {
										System.out.println("Mesa offline");
										toBeRemoved.add(t);
									} catch (RemoteException e) {
										System.out.println("Mesa offline");
										toBeRemoved.add(t);
									}
								}
							}
							for(TCP_Interface t : toBeRemoved){
								removeTable(t);
							}

							Thread.sleep(5000);

							
						} catch (InterruptedException e) {
							System.out.println("Problem with end auctions thread!");
						}
						
					}
				}
			}.start();
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

	/**
	 * Simple method to verify if a user exists.
	 * The username is received and the method tries to get
	 * the user information from the database, if the result set
	 * is empty, it means there is no user with that username.
	 *
	 * @param  username Username to search for in the data base.
	 * @return      	Boolean identifying if the operation was successful.
	 */
	public boolean checkUser(String username) throws RemoteException {
		try {
			connection.setAutoCommit(false);
	    	String sql = "SELECT * FROM user WHERE username = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setString(1,username);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(rs.next()){
	    		//Success: There is a user with that username
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

	/**
	 * Adds the data about an user to the data base.
	 * The data is passed as parameter and a sql query is created
	 * so a new user can be added to the data base with the arguments provided.
	 *
	 * @param  username Chosen username to be identified by the user, must be unique.
	 * @param  password Chosen password used for authentication.
	 * @return      	Boolean identifying if the operation was successful or not.
	 */
    public boolean register(String name, String username, String password, int type,int faculty, int department, String address, int num_id, int month_id, int year_id, String phoneNumber) throws RemoteException{
    	try {
			connection.setAutoCommit(false);
	    	String sql = "SELECT * FROM user WHERE username = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setString(1,username);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(rs.next()){
	    		//Error: There is someone with that username
	    		return false;
	    	}
	    	rs.close();
	    	sql = "insert into user(username, password, type, faculty, department, name, address, ccNumber, ccMonth, ccYear, phoneNumber) values (?,?,?,?,?,?,?,?,?,?,?)";
	    	PreparedStatement prepStatement1 = connection.prepareStatement(sql);
	    	prepStatement1.setString(1,username);
	    	prepStatement1.setString(2,password);
	    	prepStatement1.setInt(3,type);
	    	prepStatement1.setInt(4,faculty);
	    	prepStatement1.setInt(5,department);
			prepStatement1.setString(6, name);
			prepStatement1.setString(7, address);
			prepStatement1.setInt(8, num_id);
			prepStatement1.setInt(9, month_id);
			prepStatement1.setInt(10, year_id);
			prepStatement1.setString(11, phoneNumber);
		    prepStatement1.executeUpdate();
		    prepStatement1.close();
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
  
    public boolean associateFacebook(String username ,String facebookId) throws RemoteException{
	    	PreparedStatement prepStatement;
	    	ResultSet rs;
	    	try {
	    			connection.setAutoCommit(false);
	    			prepStatement = connection.prepareStatement("SELECT facebookId FROM user u WHERE u.facebookId = ?");
	    			prepStatement.setString(1, facebookId);
	            rs = prepStatement.executeQuery();
	            if(rs.next()) {
	                System.out.println("Facebook account already associated with a user!");
	                return false;
	            } else {
	            	try {
	            		prepStatement = connection.prepareStatement("UPDATE user SET facebookId = ? WHERE username = ?");
	                    prepStatement.setString(1, facebookId);
	                    prepStatement.setString(2, username);
	                    prepStatement.execute();
	                    connection.commit();
	                    return true;
	                } catch (SQLException e) {
	                    try {
	                        connection.rollback();
	                    } catch (SQLException e1) {
	                        System.out.println("Error doing rollback!");
	                    }
	                    e.printStackTrace();
	                    return false;
	                }
	            }
	        } catch(SQLException e) {
	            try {
	                connection.rollback();
	            } catch (SQLException e1) {
	                System.out.println("Error doing rollback!");
	            }
	            e.printStackTrace();
	            return false;
	        }        
	}
    
    public String loginFacebook(String facebookId) throws RemoteException {
        ResultSet rs;
        PreparedStatement statement = null;
        try {
        		connection.setAutoCommit(false);
            statement = connection.prepareStatement("SELECT * FROM user WHERE facebookId = ?");
            statement.setString(1, facebookId);
            rs = statement.executeQuery();
            if(!rs.next()){
                System.out.println("Facebook account not associated!");
                return null;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Error doing rollback!");
            }
            e.printStackTrace();
            return null;
        }
        try {
            statement = connection.prepareStatement("SELECT username FROM user WHERE facebookId = ?");
            statement.setString(1, facebookId);
            rs = statement.executeQuery();
            if(rs.next()){
                String username = rs.getString("username");
                return username;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Error doing rollback!");
            }
            e.printStackTrace();
        }
        return null;
    }
    
	public boolean updateUser(int user,String name, int faculty, int department, String address, int num_id, int month_id, int year_id, String phoneNumber) throws RemoteException{
		try {
			connection.setAutoCommit(false);
			String sql = "update user set user.name = ?, user.faculty = ?, user.department = ?, user.address = ?, user.ccNumber = ?, user.ccMonth = ?, user.ccYear = ?, user.phoneNumber = ? where user.id = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, name);
			prepStatement.setInt(2,faculty);
			prepStatement.setInt(3,department);
			prepStatement.setString(4,address);
			prepStatement.setInt(5,num_id);
			prepStatement.setInt(6,month_id);
			prepStatement.setInt(7,year_id);
			prepStatement.setString(8,phoneNumber);
			prepStatement.setInt(9,user);
			prepStatement.executeUpdate();
			prepStatement.close();
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

	/**
	 * Adds the data about a department to the data base.
	 * The data is passed as parameter and a sql query is created
	 * so a new department can be added to the data base with the arguments provided.
	 *
	 * @return      	Boolean identifying if the operation was successful or not.
	 */
    public void addDepartment(String name, int faculty) throws RemoteException{

	    try {
	    	connection.setAutoCommit(false);

		    String sql = "insert into department(faculty, name) values (?,?)";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setInt(1,faculty );
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

	public boolean addVotingTable(int election, int table) throws RemoteException{

		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from tableElection where election = ? and voteTable = ?";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,election);
			prepStatement1.setInt(2, table);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				//Error: It is already associated
				rs.close();
				return false;
			}

			String sql = "insert into tableElection(election, voteTable) values (?,?)";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,election);
			prepStatement.setInt(2, table);
			prepStatement.executeUpdate();
			prepStatement.close();
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

	public void deleteVotingTable(int election, int table) throws RemoteException {
		try {
			connection.setAutoCommit(false);
			String sql = "delete from tableElection where election = ? and voteTable = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, election);
			prepStatement.setInt(2, table);
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

	/**
	 * Adds the data about a list to the data base.
	 * The data is passed as parameter and a sql query is created
	 * so a new list can be added to the data base with the arguments provided.
	 *
	 * @param  type 	Identifies what type of list it is (1: Student, 2: Professor, 3: Employee)
	 * @param  election Election to associate the new list to.
	 * @return      	Boolean identifying if the operation was successful or not.
	 */
	public void createList(String name, int type, int election) throws RemoteException{

		try {
			connection.setAutoCommit(false);

			String sql = "insert into electionList(name, election, type) values (?,?,?)";
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

	/**
	 * Adds the data about a faculty to the data base.
	 * The data is passed as parameter and a sql query is created
	 * so a new faculty can be added to the data base with the arguments provided.
	 *
	 * @return      Boolean identifying if the operation was successful or not.
	 */
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

	/**
	 * Method to change the name of a department.
	 * The new name is passed as a parameter and a sql query is created
	 * so an update can be executed to the data base with the arguments provided.
	 *
	 * @param  id 	Department's id which name is going to be changed.
	 * @return      Boolean identifying if the operation was successful or not.
	 */
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

	/**
	 * Method to change the name of a faculty.
	 * The new name is passed as a parameter and a sql query is created
	 * so an update can be executed to the data base with the arguments provided.
	 *
	 * @param  id 	Faculty's id which name is going to be changed.
	 * @return      Boolean identifying if the operation was successful or not.
	 */
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

	/**
	 * Method to remove a faculty from the data base.
	 * The faculty's id is passed as a parameter and a verification occurs where if
	 * there is a department associated with that faculty the department will not be deleted.
	 *
	 * @param id 	Faculty's id.
	 * @return      Boolean identifying if the operation was successful or not.
	 */
    public boolean deleteFaculty(int id) throws RemoteException{
    	try {
			connection.setAutoCommit(false);
			
	    	String sql = "select * from department where faculty = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setInt(1,id);
	    	ResultSet rs = prepStatement.executeQuery();
	    	if(rs.next()){
	    		//Error: There is a department within this faculty
	    		return false;
	    	}
	    	
	    	rs.close();
	    	prepStatement.close();
	    	
		    sql = "delete from faculty where faculty.id = ?";
		    prepStatement = connection.prepareStatement(sql);
		    prepStatement.setInt(1, id);
		    prepStatement.executeUpdate();
			prepStatement.close();
	    	
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

	/**
	 * Adds the data about an election to the data base.
	 * The data is passed as parameter and a sql query is created
	 * so a new election can be added to the data base with the arguments provided.
	 *
	 * @param  startDate 	Identifies the date when the election is supposed to start.
	 * @param  endDate 		Identifies the date when the election is supposed to end.
	 * @return      		Boolean identifying if the operation was successful or not.
	 */
    public boolean createElection(Date startDate, Date endDate, String name, String desc, int department) throws RemoteException{
	    //date parser admin side
    	try {
	    	connection.setAutoCommit(false);
	    	Timestamp start = new Timestamp(startDate.getTime());
			Timestamp end = new Timestamp(endDate.getTime());
	    	
		    String sql = "insert into election(name, description, startDate, endDate, department) values (?,?,?,?,?)";
		    PreparedStatement prepStatement = connection.prepareStatement(sql);
		    prepStatement.setString(1,name);
		    prepStatement.setString(2,desc);
		    prepStatement.setTimestamp(3, start);
		    prepStatement.setTimestamp(4, end);
		    prepStatement.setInt(5, department);
		    prepStatement.executeUpdate();
			prepStatement.close();
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

	/**
	 * Changes the election start date.
	 *
	 * @param  startDate 	Identifies the new date when the election is supposed to start.
	 * @param  id 			Identifies the election which is supposed to be updated.
	 * @return      		Boolean identifying if the operation was successful or not.
	 */
	public void changeElectionStartDate(int id, Date startDate) throws RemoteException{
		//date parser admin side
		try {
			connection.setAutoCommit(false);
			String sql = "update election set election.startDate = ? where election.id = ?";
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

	/**
	 * Changes the election end date.
	 *
	 * @param  endDate 		Identifies the new date when the election is supposed to start.
	 * @param  id 			Identifies the election which is supposed to be updated.
	 * @return      		Boolean identifying if the operation was successful or not.
	 */
	public void changeElectionEndDate(int id, Date endDate) throws RemoteException{
		//date parser admin side
		try {
			connection.setAutoCommit(false);
			String sql = "update election set election.endDate = ? where election.id = ?";
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

	/**
	 * Changes the election name.
	 *
	 * @param  name 		New name of the election.
	 * @param  id 			Identifies the election which is supposed to be updated.
	 * @return      		Boolean identifying if the operation was successful or not.
	 */
	public void changeElectionName(int id, String name) throws RemoteException{
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

	/**
	 * Changes the election description.
	 *
	 * @param  desc 		New description of the election.
	 * @param  id 			Identifies the election which is supposed to be updated.
	 * @return      		Boolean identifying if the operation was successful or not.
	 */
	public void changeElectionDescription(int id, String desc) throws RemoteException{
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

	/**
	 * Adds candidates to a list.
	 * The data is passed as parameter and a sql query is created
	 * so a list of users can be added as candidates from a certain list.
	 *
	 * @return      Boolean identifying if the operation was successful or not.
	 */
	public void addCandidatesToList(int list, ArrayList<Integer> users) throws RemoteException{
		for(int user : users) {
			try {
				connection.setAutoCommit(false);
				String sql = "insert into candidate(user, list) values (?,?)";
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
/*
	public boolean addVotingTable(int depId) throws RemoteException {
    	try {
	    	connection.setAutoCommit(false);
			String sql = "insert into vote_table(department) values (?)";
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
	*/

	public void addTable(TCP_Interface t) throws RemoteException{
		synchronized(tables){
			tables.add(t);
		}
	}
	public void removeTable(TCP_Interface t) {
		synchronized(tables){
			tables.remove(t);
		}
	}
	
	
  /* public ArrayList<Vote> getUserVotes(int id){
		ArrayList<Vote> votes = new ArrayList<>();
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from vote where id_user = ? limit 1";
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
    	ArrayList<Integer> onlineIds = new ArrayList<>();
    	synchronized(tables){
	    	for(TCP_Interface t: tables){
	    		onlineIds.add(t.ping("GetIds"));
	    	}	
    	}
		return onlineIds;
    }

    public ArrayList<Integer> getElectionTables(int election) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql = "select * from tableElection where election = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,election);
			ResultSet rs = prepStatement.executeQuery();
			ArrayList<Integer> tables = new ArrayList<>();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					tables.add(rs.getInt("voteTable"));
				}
				return tables;
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

	/**
	 * Method to return info about where a user voted.
	 * The election and user ids are received and a query is created
	 * to get the vote with that information.
	 *
	 * @return      String containing the data or null in case the access to the data base wasn't successful.
	 */
    public String whereUserVoted(int electionId, int userId) throws RemoteException{
    	try {
	    	connection.setAutoCommit(false);
	    	
	    	String sql = "Select vote.voteTime, user.username, election.name, department.name from vote, user, election, voteTable, department where vote.user = ? and vote.election = ? and vote.user = user.id and vote.election = election.id  and vote.voteTable = voteTable.id and voteTable.department = department.id;";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setInt(1,userId);
	    	prepStatement.setInt(2,electionId);
	    	ResultSet rs = prepStatement.executeQuery();
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

	/**
	 * Simple method to verify if a department exists.
	 * The id is received and the method tries to get
	 * the department information from the database, if the result set
	 * is empty, it means there is no department with that id.
	 *
	 * @param  id 		Department to search for in the data base.
	 * @return      	Boolean identifying if the operation was successful or not.
	 */
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

	/**
	 * Simple method to verify if a faculty exists.
	 * The id is received and the method tries to get
	 * the faculty information from the database, if the result set
	 * is empty, it means there is no faculty with that id.
	 *
	 * @param  id 		Faculty to search for in the data base.
	 * @return      	Boolean identifying if the operation was successful or not.
	 */
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
	
	public String getFacebookId(String username) throws RemoteException{
		try {
			connection.setAutoCommit(false);
			String sql1 = "select facebookId from user where username = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setString(1,username);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				if(rs.getString("facebookId") != null) {
					return rs.getString("facebookId");
				}
				return null;
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
	/**
	 * Simple method to verify the type of a user.
	 * The id is received and the method uses a query to get
	 * the user type from the data base.
	 *
	 * @param  id 		User to search for in the data base.
	 * @return      	User type or 0 if there is no user with that id.
	 */
	public int checkUserType(int id)throws RemoteException {
		try {
			connection.setAutoCommit(false);
			String sql = "select type from user where id = ? limit 1";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,id);
			ResultSet rs = prepStatement.executeQuery();
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

	/**
	 * Simple method to verify the type of a list.
	 * The id is received and the method uses a query to get
	 * the list type from the data base.
	 *
	 * @param  id 		List to search for in the data base.
	 * @return      	List type or 0 if there is no list with that id.
	 */
	public int getListType(int id)throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql = "select type from electionList where id = ? limit 1";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,id);
			ResultSet rs = prepStatement.executeQuery();
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

	/*
	/**
	 * Simple method to verify if a election exists.
	 * The id is received and the method tries to get
	 * the election information from the database, if the result set
	 * is empty, it means there is no election with that id.
	 *
	 * @param  id 		Election to search for in the data base.
	 * @return      	Boolean identifying if the operation was successful or not.
	 *
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
*/
	/**
	 * Method to return elections which have previously occurred.
	 * The method tries to get all elections which the end date has
	 * already passed and puts that information into an array.
	 *
	 * @return      	ArrayList with past elections or null if there have been none elections yet.
	 */
	public ArrayList<Election> getPastElections() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			ArrayList<Election> elections = new ArrayList<Election>();
			String sql = "select * from election where endDate < current_timestamp";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			ResultSet rs = prepStatement.executeQuery();
			Date current = new Date();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					String name = rs.getString("name");
					int id = rs.getInt("id");
					Department department = getDepartment(rs.getInt("department"));
					String description = rs.getString("description");
					Date startDate = new Date(rs.getTimestamp("startDate").getTime());
					Date endDate = new Date(rs.getTimestamp("endDate").getTime());
					int blankVotes = rs.getInt("blankVotes");
					int nullVotes = rs.getInt("nullVotes");

					if (department == null || department.id == 0) {
						elections.add(new Election(name, id,new Department("Conselho Geral",0),description, startDate, endDate,blankVotes, nullVotes));
					} else {
						elections.add(new Election(name, id, department,description, startDate, endDate,blankVotes, nullVotes));
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

/*	public HashMap<String,Integer> getPastElections() throws RemoteException{
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
					Date date = rs.getTimestamp("endDate");
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
*/

	/**
	 * Method to return elections.
	 * The method tries to get all the elections
	 * and puts that information into an array.
	 *
	 * @return      	ArrayList with all elections or null if there are none elections yet.
	 */
	public ArrayList<Election> getAllElections() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			ArrayList<Election> elections = new ArrayList<Election>();
			String sql = "select * from election";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			ResultSet rs = prepStatement.executeQuery();
			Date current = new Date();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					String name = rs.getString("name");
					int id = rs.getInt("id");
					Department department = getDepartment(rs.getInt("department"));
					String description = rs.getString("description");
					Date startDate = new Date(rs.getTimestamp("startDate").getTime());
					Date endDate = new Date(rs.getTimestamp("endDate").getTime());
					int blankVotes = rs.getInt("blankVotes");
					int nullVotes = rs.getInt("nullVotes");

					if (department== null || department.id == 0) {
						elections.add(new Election(name, id,new Department("Conselho Geral",0),description, startDate, endDate,blankVotes, nullVotes));
					} else {
						
						elections.add(new Election(name, id, department,description, startDate, endDate,blankVotes, nullVotes));
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

	/**
	 * Method to return departments.
	 * The method tries to get all the departments
	 * and puts that information into an array.
	 *
	 * @return      	ArrayList with all departments or null if there are none departments yet.
	 */
	public ArrayList<Department> getAllDepartments() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			ArrayList<Department>  departments = new ArrayList<>();
			String sql = "select * from department";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					departments.add(getDepartment(rs.getInt("id")));
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
	
	public ArrayList<Department> getDepartmentsFromFaculty(int faculty) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			ArrayList<Department> departments = new ArrayList<>();
			String sql = "select * from department where faculty = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,faculty);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					departments.add(getDepartment(rs.getInt("id")));
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
	
	public ArrayList<Lista> getElectionLists(int election) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			ArrayList<Lista> lists = new ArrayList<>();
			String sql = "select * from electionList where election = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,election);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()){
				rs.beforeFirst();
				while(rs.next()) {
					lists.add(getList(rs.getInt("id")));
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

			String sql1 = "select department from election where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				return rs.getInt("department");
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

	/**
	 * Method to get user info by his id.
	 * The method tries to get all the information
	 * and puts that information into a class.
	 *
	 * @return      	User information in a class (User) or null if there is not an user with that id.
	 */
	public User getUser(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select username from user where id = ? limit 1";
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

	/**
	 * Method to get election info by its id.
	 * The method tries to get all the information
	 * and puts that information into a class.
	 *
	 * @return      	Election information in a class (Election) or null if there is not an election with that id.
	 */
	public Election getElection(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from election where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				String name = rs.getString("name");
				Department department = getDepartment(rs.getInt("department"));
				String description = rs.getString("description");
				Date startDate = new Date(rs.getTimestamp("startDate").getTime());
				Date endDate = new Date(rs.getTimestamp("endDate").getTime());
				int blankVotes = rs.getInt("blankVotes");
				int nullVotes = rs.getInt("nullVotes");
				if (department == null || department.id == 0 ) {
					return new Election(name, id,new Department("Conselho Geral",0),description, startDate, endDate,blankVotes, nullVotes);
				} else {
					
					return new Election(name, id, department,description, startDate, endDate,blankVotes, nullVotes);
				}
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

	/**
	 * Method to get list info by its id.
	 * The method tries to get all the information
	 * and puts that information into a class.
	 *
	 * @return      	List information in a class (Lista) or null if there is not a list with that id.
	 */
	public Lista getList(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from electionList where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				String name = rs.getString("name");
				Election election = getElection(rs.getInt("election"));
				int votes = rs.getInt("vote");
				return new Lista(name, election, votes, id);
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

	/**
	 * Method to get department info by its id.
	 * The method tries to get all the information
	 * and puts that information into a class.
	 *
	 * @return      	Department information in a class (Department) or null if there is not a department with that id.
	 */
	public Department getDepartment(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from department where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				String name = rs.getString("name");
				Department department = new Department(name, id);
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

	/**
	 * Method to get voting table info by its id.
	 * The method tries to get all the information
	 * and puts that information into a class.
	 *
	 * @return      	Voting table information in a class (VotingTable) or null if there is not a voting table with that id.
	 */
	public VotingTable getVotingTable(int id){
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from voteTable where id = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			if(rs.next()){
				Department department = getDepartment(rs.getInt("department"));
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

	/**
	 * Method to get all user votes.
	 * The method tries to get all the votes from a user with a
	 * given id and puts that information into a list.
	 *
	 * @return      	List of user votes or null if there aren't any.
	 */
	
	public List<Vote> getUserVotes(int id) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql1 = "select * from vote where user = ?";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs = prepStatement1.executeQuery();
			List<Vote> votes;
			votes = Collections.synchronizedList(new ArrayList());
			if (rs.next()) {
				rs.beforeFirst();
				while (rs.next()) {
					User user = getUser(rs.getInt("user"));
					Election election = getElection(rs.getInt("election"));
					VotingTable table = getVotingTable(rs.getInt("voteTable"));
					Date date = new Date(rs.getTimestamp("voteTime").getTime());
					int voteId = rs.getInt("id");
					votes.add(new Vote(user, election, table, date, voteId));
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

	/**
	 * Method to get all elections a user has voted in.
	 * The method tries to get all the elections a user with a
	 * given id voted in and puts that information into a list.
	 *
	 * @return      	List of user voted elections or null if there aren't any.
	 */
	public ArrayList<Election> getUserVotedElections(int id) throws RemoteException{
		ArrayList<Election> elections = new ArrayList<>();
		List<Vote> votes = getUserVotes(id);
		if(votes == null)
			return null;
		for(Vote vote: votes){
			System.out.println(vote.election.name);
			elections.add(vote.election);
		}
		return elections;
	}

	/**
	 * Method to get information about a vote in a certain election.
	 * The method tries to get all the information from a vote on a
	 * given election  and puts that information into a class.
	 *
	 * @return      	Class with vote information (Vote) or null.
	 */
	public Vote getUserVoteDetails(int id, int election) throws RemoteException{
		List<Vote> votes = getUserVotes(id);
		for(Vote vote: votes){
			if(vote.election.id == election){
				return vote;
			}
		}
		return null;
	}

	/**
	 * Method to get the results from a certain election.
	 * The method tries to get all the results from an election
	 * and puts that information into an hashmap with the name of the
	 * list/type of vote and the sum of the votes.
	 *
	 * @return      	Hashmap with the results of an election or null.
	 */
	public HashMap<String, Integer> getElectionResults(int id)throws RemoteException{
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer>  results = new HashMap<>();
			String sql1 = "select * from electionList where election = ?";
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
				int blank_total = rs2.getInt("blankVotes");
				int null_total = rs2.getInt("nullVotes");
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

	/**
	 * Method to get information about a vote in a certain election.
	 * The method tries to get all the information from a vote on a
	 * given election  and puts that information into a class.
	 *
	 * @return      	Class with vote information (Vote) or null.
	 */
	public int login(String username, String password )throws RemoteException {
    	try {
	    	connection.setAutoCommit(false);

			String sql = "Select type from user where username = ? and password = ? limit 1";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
	    	prepStatement.setString(1,username);
	    	prepStatement.setString(2,password);
	    	ResultSet rs = prepStatement.executeQuery();
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


	/*
	FOI AQUI QUE FICOU TODO
	 */

	public ArrayList<User> getUsers() throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql = "select * from user";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			ResultSet rs = prepStatement.executeQuery();

			if(rs.next()){ //theres is at least one election
				ArrayList<User> users = new ArrayList<>();
				rs.beforeFirst();
				while(rs.next()){
					users.add(new User(rs.getString("username"),rs.getInt("id")));
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

	public HashMap<Integer, String> getEarlyElections(int usertype) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql = "select election.id , election.name from election, electionList  where (election.endDate > current_timestamp() and  electionList.type= ? and electionList.election = election.id ) group by electionList.election";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,usertype);
			ResultSet rs = prepStatement.executeQuery();

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

	public HashMap<Integer, String> getElections(int usertype, int userDep) throws RemoteException{
	    try {
	    	connection.setAutoCommit(false);

			String sql = "select election.id , election.name from election, electionList  where (election.startDate < current_timestamp() and election.endDate > current_timestamp() and (election.department = ? or election.department = 0) and  electionList.type= ? and electionList.election = election.id ) group by electionList.election";
	    	PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,userDep);
			prepStatement.setInt(2,usertype);
	    	ResultSet rs = prepStatement.executeQuery();

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

	public HashMap<Integer, String> getListsEarlyElections(int usertype, int idElection) throws RemoteException {
		try {
			connection.setAutoCommit(false);
			HashMap<Integer, String> elections = getEarlyElections(usertype);
			if( elections != null && elections.get(idElection) != null){ //valid election

				String sql1 = "select id , name from electionList  where election=? and type=?";
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

	public HashMap<Integer, String> getListsElections(int usertype, int userDep, int idElection) throws RemoteException {
	    try {
	    	connection.setAutoCommit(false);
			HashMap<Integer, String> elections = getElections(usertype, userDep);
	    	if( elections != null && elections.get(idElection) != null){ //valid election
	    		
		    	String sql1 = "select id , name from electionList  where election=? and type=?";
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

	public ArrayList<Lista> getListsElection(int idElection) throws RemoteException {
	    try {
	    		connection.setAutoCommit(false);
		    	String sql1 = "select id , name, vote from electionList  where election=?";
		    	PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
		    	prepStatement1.setInt(1,idElection);
		    	ResultSet rs = prepStatement1.executeQuery();
	    		
		    	ArrayList<Lista> electionlists = new ArrayList<>(); 
	    		while(rs.next()){ //no need to verify if theres is lists, getElection does it 
	        		electionlists.add(new Lista(rs.getString("name"), getElection(idElection), rs.getInt("vote"), rs.getInt("id")));
	    		}
	    		rs.close();
	    		return electionlists;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				System.out.println("DB: Connection lost...");
			}
		} finally{
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("DB: Connection lost...");
			}
		}
		return null;
		}

	public HashMap<String, Integer> getElectionVotesPerTable(int electionId) throws RemoteException {
		try {
			connection.setAutoCommit(false);

			HashMap<String, Integer>  votes = new HashMap<>();
			String sql1 = "select  voteTable, count(*) as total from vote where election = ? group by voteTable;";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
	    	prepStatement1.setInt(1,electionId);
			ResultSet rs = prepStatement1.executeQuery();
			while(rs.next()) {
				if(rs.getInt("voteTable") == 1)
					votes.put("Online", rs.getInt("total"));
				else
					votes.put("Mesa " + rs.getInt("voteTable"), rs.getInt("total"));
			}
			return votes;
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
	
	public boolean earlyVote(int userId, int userType, int userDep, int election, int vote) throws RemoteException{
		try { //if no vote or vote = 0 is veryfied on tcp
			connection.setAutoCommit(false);
			HashMap<Integer, String> lists = getListsEarlyElections(userType,election);
			if( lists != null){ //valid election
				Iterator it = lists.entrySet().iterator();
				while (it.hasNext()) {
					//search if theres is a vote already
					Map.Entry pair = (Map.Entry)it.next();
					if( (Integer)pair.getKey() == vote){
						String getvotes = "Select 1 from vote where election = ? and user = ?";
						PreparedStatement prepStatement2 = connection.prepareStatement(getvotes);
						prepStatement2.setInt(1, election);
						prepStatement2.setInt(2, userId);
						ResultSet rs = prepStatement2.executeQuery();
						if(rs.next()){//already voted
							prepStatement2.close();
							rs.close();
							return false;
						}
						Lista list = getList(vote);
						String str = "List " + list.name + " got one more vote, they now have " + list.votes + " votes.";
						sendNotification(str);

						String sql = "insert into vote(election, user) values (?,?)";
						PreparedStatement prepStatement = connection.prepareStatement(sql);
						prepStatement.setInt(1, vote);
						prepStatement.setInt(2, userId);

						prepStatement.executeUpdate();
						prepStatement.close();

						String sql1 = "update electionList set electionList.vote = electionList.vote +1 where electionList.id = ?";
						PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
						prepStatement1.setInt(1, election);
						prepStatement1.executeUpdate();
						prepStatement1.close();

						//something to tell theres a new vote
						return true;
					}else{
						it.remove(); // avoids a ConcurrentModificationException
					}
				}
				String blankVote = "update election set election.blankVotes =  election.blankVotes +1 where election.id = ?";
				PreparedStatement prepNullStatement = connection.prepareStatement(blankVote);
				prepNullStatement.setInt(1, election);
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
		return true;
	}

	@SuppressWarnings("rawtypes")
	public boolean vote(int userId, int userType, int userDep, int election, int vote, int table) throws RemoteException{
		 try { //if no vote or vote = 0 is veryfied on tcp
		    connection.setAutoCommit(false); 
			 HashMap<Integer, String> lists = getListsElections(userType,userDep,election);
		    		if( lists != null){ //valid election
		    		    Iterator it = lists.entrySet().iterator();
		    		    while (it.hasNext()) {
				    		//search if theres is a vote already
		    		        Map.Entry pair = (Map.Entry)it.next();
		    		    	if( (Integer)pair.getKey() == vote){
					    		String getvotes = "Select 1 from vote where election = ? and user = ?";
					    		PreparedStatement prepStatement2 = connection.prepareStatement(getvotes);
							    prepStatement2.setInt(1, election);
							    prepStatement2.setInt(2, userId);
					    		ResultSet rs = prepStatement2.executeQuery();
					    		if(rs.next()){//already voted
						    		prepStatement2.close();
					    			rs.close();
					    			return false;
					    		}
					    		Lista list = getList(vote);
					    		String str = "List " + list.name + " got one more vote, they now have " + list.votes + " votes.";
					    		sendNotification(str);
			
							    String sql = "insert into vote(election, user, voteTable) values (?,?,?)";
							    PreparedStatement prepStatement = connection.prepareStatement(sql);
							    prepStatement.setInt(1, election);
							    prepStatement.setInt(2, userId);
							    prepStatement.setInt(3, table);
			
							    prepStatement.executeUpdate();
								prepStatement.close();
								
							    String sql1 = "update electionList set electionList.vote = electionList.vote +1 where electionList.id = ?";
							    PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
							    prepStatement1.setInt(1, election);
							    prepStatement1.executeUpdate();
								prepStatement1.close();
		
								//something to tell theres a new vote �\_(�)_/�
					    		return true;
		    		    	}else{
		    			        it.remove(); // avoids a ConcurrentModificationException
		    		    	}
		    		    } 

			    		String getvotes = "Select 1 from vote where election = ? and user = ?";
			    		PreparedStatement prepStatement2 = connection.prepareStatement(getvotes);
					    prepStatement2.setInt(1, election);
					    prepStatement2.setInt(2, userId);
			    		ResultSet rs = prepStatement2.executeQuery();
			    		if(rs.next()){//already voted
				    		prepStatement2.close();
			    			rs.close();
			    			return false;
			    		}
		    		    
		    		    
		    		    String sql = "insert into vote(election, user, voteTable) values (?,?,?)";		
		    		    PreparedStatement prepStatement = connection.prepareStatement(sql);		
		    		    prepStatement.setInt(1, election);		
		    		    prepStatement.setInt(2, userId);		
		    		    prepStatement.setInt(3, table);		
		    		    
		    		    prepStatement.executeUpdate();		
		    		    prepStatement.close();
		    		    
		    		    String nullVote = "update election set election.nullVotes =  election.nullVotes +1 where election.id = ?";
					    PreparedStatement prepNullStatement = connection.prepareStatement(nullVote);
					    prepNullStatement.setInt(1, election);
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

	public boolean vote_blank(int userid, int usertype, int userDep, int idElection, int table) throws RemoteException {
		try {
	    	String getvotes = "Select * from vote where election = ? and user = ?";
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
	    	
    		String sql = "insert into vote(election, user, voteTable) values (?,?,?)";		
		    PreparedStatement prepStatement = connection.prepareStatement(sql);		
		    prepStatement.setInt(1, idElection);		
		    prepStatement.setInt(2, userid);		
		    prepStatement.setInt(3, table);		
		    
		    prepStatement.executeUpdate();		
		    prepStatement.close();
		    
		    String blankVote = "update election set election.blankVotes =  election.blankVotes +1 where election.id = ?";
		    PreparedStatement prepNullStatement = connection.prepareStatement(blankVote);
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
			String sql = "select id, department from user where username = ? limit 1";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1,username);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()){
				HashMap<String, Integer> userInfo = new HashMap<>();
				userInfo.put("id", rs.getInt("id"));
				userInfo.put("department", rs.getInt("department"));
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

	/**
	 * Method to remove a department from the data base.
	 * The department's id is passed as a parameter and a verification occurs where if
	 * there is anything associated with that department
	 * ( election, user, voteTable or vote) the department will not be deleted.
	 *
	 * @param id 	Department's id.
	 * @return      Boolean identifying if the operation was successful.
	 */
	public boolean deleteDepartment(int id) throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql = "select * from election where department = ? limit 1";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,id);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()){
				//theres an election on the faculty
				return false;
			}

			String sql1 = "select * from user where department = ? limit 1";
			PreparedStatement prepStatement1 = connection.prepareStatement(sql1);
			prepStatement1.setInt(1,id);
			ResultSet rs1 = prepStatement1.executeQuery();
			if(rs1.next()){
				//theres an user on the faculty
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

			String sql4 = "delete from electionList where electionList.id = ?";
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


	public int checkUserDep(int id) throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql4 = "Select type from user where id = ? limit 1";
			PreparedStatement prepStatement4 = connection.prepareStatement(sql4);
			prepStatement4.setInt(1, id);
			ResultSet rs = prepStatement4.executeQuery();
			if(rs.next()){
				int type = rs.getInt("type");
				prepStatement4.close();
				rs.close();
				return type;
			}
			else return 0;

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

	public void editListType(int listId,int userType) throws RemoteException {
		try {
			connection.setAutoCommit(false);

			String sql = "update electionlist set electionlist.type = ? where electionList.id = ?";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, listId);
			prepStatement.setInt(2, userType);
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
	
	public ArrayList<String> getPeopleList(int listId) throws RemoteException{
		try {
			connection.setAutoCommit(false);

			String sql = "select u.name from electionlist e, candidate c, user u where e.id =? and e.id=c.list and c.user=u.id;";
			PreparedStatement prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, listId);
			ResultSet rs = prepStatement.executeQuery();

			if(rs.next()){ //theres is at least one election
				ArrayList<String> candidates = new ArrayList<>();
				rs.beforeFirst();
				while(rs.next()){
					candidates.add(rs.getString("u.name"));
				}
				rs.close();
				return candidates;

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
	
	
	public static void main(String args[]) {
		
	 		System.getProperties().put("java.security.policy", "policy.all");
			System.setSecurityManager(new SecurityManager()); 
		 
		
		try {
			RMI_Server h = new RMI_Server();
			LocateRegistry.createRegistry(h.port).rebind("IVotas", h);

		} catch (RemoteException re) {
			System.out.println("RMI could not be created, lauching secundary");
			start();
			return;
		}
	}

}