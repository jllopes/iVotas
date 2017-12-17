package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Map;

public class ChoosePastElectionAction extends ActionSupport implements SessionAware {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3526857532181701532L;
	/**
	 * 
	 */
	private int election = 0;
	private Map<String,Object> session;
	@Override
	public String execute() {
		if(this.getSessionBean() != null) {
			if(election != 0) {
				this.getSessionBean().setPastElection(election);
				return SUCCESS;
			}
		}
		return ERROR;
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

	/**
	 * @return the election
	 */
	public int getElection() {
		return election;
	}

	/**
	 * @param election the election to set
	 */
	public void setElection(int election) {
		this.election = election;
	}
	
	
}
