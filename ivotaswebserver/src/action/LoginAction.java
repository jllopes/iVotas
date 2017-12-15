package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware {


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
		try{
			if(this.username != null && !username.equals("") && this.password != null && !password.equals("")) {
				this.getSessionBean().setUsername(this.username);
				this.getSessionBean().setPassword(this.password);
				session.put("username", username);
				session.put("password", password);
				if(this.getSessionBean().login()){
					session.put("loggedin", true);
					if(this.getSessionBean().getUserType() == 4){
						return "admin";
					}else
						return "user";
				} else {
					session.put("loggedin", false);
					return LOGIN;
				}
			} else
				return LOGIN;
		}catch(RemoteException e){
			return ERROR;
		}
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
