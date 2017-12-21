package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Map;

public class CreateTableAction extends ActionSupport implements SessionAware {
	private int depId;
	private Map<String,Object> session;
	@Override
	public String execute() {
		try{
			if(this.getSessionBean()!= null ){
				this.getSessionBean().CreateVotingTable(depId);
				return SUCCESS;
			}
			else
				return LOGIN;
		}catch(RemoteException e){
			return ERROR;
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

	public int getDepId() {
		return depId;
	}

	public void setDepId(int depId) {
		this.depId = depId;
	}

	
}
