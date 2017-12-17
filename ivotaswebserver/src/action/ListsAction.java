package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ListsAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1921101606516661655L;
	private Map<String,Object> session;
	private HashMap<Integer,String> electionLists;
	private int electionId;
	//private HashMap<Integer,Lista> electionLists;
	

	public SessionBean getSessionBean(){
		if(!session.containsKey("sessionBean"))
			return null;
		return (SessionBean) session.get("sessionBean");
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public int getElectionId() {
		return electionId;
	}

	public void setElectionId(int electionId) {
		this.electionId = electionId;
	}
	
	public HashMap<Integer,String> getElectionLists(){
		return this.electionLists;
	}

}

