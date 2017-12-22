package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class RegisterAction extends ActionSupport implements SessionAware {


	/**
	 * 
	 */
	private String username = null;
	private String password = null;
	private String type = null;
	private String name = null;
	private int faculty = 0;
	private int department = 0;
	private int id = 0;
	private int month = 0;
	private int year = 0;
	private String phone = null;
	private String address = null;
	
	private Map<String,Object> session;
	
	@Override
	public String execute() {
		try{
			System.out.println("asodjfaosidjfoaisjd");
			if(this.getSessionBean() != null && this.getSessionBean().getUserType() == 4) {
				System.out.println("Logged in as admin");
				if(username != null && !username.equals("") && password != null && !password.equals("") && type != null && !type.equals("") && name != null && !name.equals("") && phone != null && !phone.equals("") && name != null && !name.equals("") && address != null && !address.equals("") && faculty != 0 && department != 0 && id != 0 && month != 0 && year != 0) {
					System.out.println("Valid input");
					int userType = returnUserType(type);
					if(this.getSessionBean().unusedUsername(username)) {
						System.out.println("valid username");
						if(this.getSessionBean().newUser(username,password,userType,name,faculty,department,id,month,year,phone,address)) {
							//alert worked, go to home
							System.out.println("register ok");
							return SUCCESS;
						}
					}
				}
			}
			return LOGIN;
		}catch(RemoteException e){
			return "rmi";
		}
	}
	
	public int returnUserType(String string) {
		if(string == "Student") {
			return 1;
		} else if(string == "Professor") {
			return 2;
		} else {
			return 3;
		}
	}
	
	public SessionBean getSessionBean(){
		if(!session.containsKey("sessionBean"))
			return null;
		return (SessionBean) session.get("sessionBean");
	}
	
	public void setSessionBean(SessionBean sessionBean){
		this.session.put("sessionBean", sessionBean);
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the faculty
	 */
	public int getFaculty() {
		return faculty;
	}

	/**
	 * @param faculty the faculty to set
	 */
	public void setFaculty(int faculty) {
		this.faculty = faculty;
	}

	/**
	 * @return the department
	 */
	public int getDepartment() {
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(int department) {
		this.department = department;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
