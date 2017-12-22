package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogoutAction extends ActionSupport implements SessionAware {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2059576564105439712L;
	private Map<String,Object> session;
	private String username = null, password = null;
	private int type = 0;
	private List<Integer> option = new ArrayList<>();
	@Override
	public String execute() {
		this.getSessionBean().logout();
		session.put("sessionBean",null);
		return SUCCESS;
	}
	
	public int addOption(int idList){
		System.out.println("FUNCIONA");
		option.add(idList);
		return idList;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public void setUsername(String username) {
		this.username = username; // will you sanitize this input? maybe use a prepared statement?
	}

	public void setPassword(String password) {
		this.password = password; // what about this input? 
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
}
