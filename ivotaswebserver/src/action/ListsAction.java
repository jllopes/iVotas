package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;

import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class ListsAction extends ActionSupport implements SessionAware {
	private Map<String,Object> session;
	private HashMap<Integer,String> electionLists;
	private int idElection;
	//private HashMap<Integer,Lista> electionLists;
	
	public String execute() throws RemoteException{
		
		
		
		
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}

