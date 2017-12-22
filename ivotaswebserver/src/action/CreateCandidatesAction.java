package action;

import com.opensymphony.xwork2.ActionSupport;

import model.SessionBean;
import rmiserver.Election;

import org.apache.struts2.interceptor.SessionAware;

import java.awt.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class CreateCandidatesAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1921101606516661655L;
	private Map<String,Object> session;
	private int electionId;
	private int listType;
	private String[] userIds;
	private int counter = 0;
	private String listName;
		
	public String execute() {

		try{
			if(this.getSessionBean() != null){
				boolean a;
				if(counter== 0){
					return INPUT;
				}else{
					int listId = 0;
					if((listId = this.getSessionBean().createList(listName, electionId, listType))!= 0){
						ArrayList<Integer> users = new ArrayList<>();
						for(String s : userIds)
							users.add(Integer.parseInt(s));
						
						this.getSessionBean().addCandidatesToList(listId, users);
						return SUCCESS;
					}
					return INPUT;
				}
			}else
				return LOGIN;
			
		} catch(NumberFormatException e1) {
			return ERROR;
		}catch (RemoteException e){
			return "rmi";
		}	
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


	public int getListType() {
		return listType;
	}


	public void setListType(int listType) {
		this.listType = listType;
	}


	public String[] getUserIds() {
		if(userIds != null)
			return userIds;
		else
			return null;
	}


	public void setUserIds(String[] userIds) {
		this.userIds = userIds;
		this.counter = userIds.length;
	}


	public String getListName() {
		return listName;
	}


	public void setListName(String listName) {
		this.listName = listName;
	}

	

}
