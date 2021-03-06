package action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

public class ChooseTypeAction extends ActionSupport implements SessionAware {

	private int listType = 0;
	private int electionId = 0;
	private Map<String,Object> session;
	
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
	 * @return the listType
	 */
	public int getlistType() {
		System.out.println("get list type" + listType);
		return listType;
	}

	/**
	 * @param listType the listType to set
	 */
	public void setlistType(int listType) {
		System.out.println("set list type" + listType);
		this.listType = listType;
	}

	public int getElectionId() {
		return electionId;
	}

	public void setElectionId(int electionId) {
		this.electionId = electionId;
	}
	
	
	
}
