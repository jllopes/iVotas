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
	private String username;
	private String password;
	private int type = 0;
	private String name;
	private int faculty = 0;
	private int department = 0;
	private int id = 0;
	private int month = 0;
	private int year = 0;
	private String phoneNumber;
	
	private Map<String,Object> session;
	
	@Override
	public String execute() {
		try{
			
			//if(this.getSessionBean().checkRegister(username, type, faculty, department,))
			return "register";
		}catch(RemoteException e){
			return ERROR;
		}
	}
	
	/*public void checkType() {
		if(type)
	}*/
	
	public void setFaculties(ArrayList<String> faculties) {
		this.facultyList = faculties;
	}
	
	public void setDepartments(ArrayList<String> departments) {
		this.departmentList = departments;
	}
	
	public SessionBean getSessionBean(){
		if(!session.containsKey("sessionBean"))
			this.setSessionBean(new SessionBean());
		return (SessionBean) session.get("sessionBean");
	}
	
	public void setSessionBean(SessionBean sessionBean){
		this.session.put("sessionBean", sessionBean);
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
