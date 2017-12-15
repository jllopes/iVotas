package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;
import rmiserver.Election;

import org.apache.struts2.interceptor.SessionAware;

import java.awt.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class VoteAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1921101606516661655L;
	private Map<String,Object> session;
	private int electionId;
	private ArrayList<Integer> listId = new ArrayList<>();
	//private int listId;

	public String execute() {
		System.out.println("FUNFA2" + listId);/*
		if(listId.size() == 0){
			System.out.println("VAZIO");
		}else if(listId.size() == 1)
			System.out.println("SO 1");
		else
			System.out.println("VARIOS");*/
		return SUCCESS;
			/*
		try{
			if(this.getSessionBean() != null){
				if(this.getSessionBean().vote(this.electionId, this.listId)){
					return SUCCESS;
				}else
					return INPUT; //something to popup already vote or error
			}else
				return LOGIN;
		}catch (RemoteException e){
			return ERROR;
		}	*/
	}
	
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

	public ArrayList<Integer> getListId() {
		return listId;
	}

	public void setListId(ArrayList<Integer> listId) {
		this.listId =listId;
	}



	
	
}

