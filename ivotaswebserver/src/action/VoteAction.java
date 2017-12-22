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


public class VoteAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1921101606516661655L;
	private Map<String,Object> session;
	private int electionId;
	//private ArrayList<Integer> listId = new ArrayList<>();
	private String[] listId = null;
	private int counter = 0;
		
	public String execute() {

		try{
			if(this.getSessionBean() != null){
				boolean a;
				if(counter== 0){
					a = this.getSessionBean().vote_blank(electionId);
				}else if(counter == 1)
					a = this.getSessionBean().vote(electionId, Integer.parseInt(listId[0]));
				else{
					a = this.getSessionBean().vote(electionId,0 ); //invalid list
				}
				if(a){
					if(this.getSessionBean().getFacebookId() != null) {
						String msg = "I just voted on election " + this.getSessionBean().getElectionInfo(electionId).getName() + " on iVotas!";
						msg.replaceAll(" ", "%20");
						try {
							msg = URLEncoder.encode(msg, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						this.getSessionBean().postToFacebook(msg);
						System.out.println("postou");
						System.out.println("posted");
					}
					addActionMessage("Vote with success");
					return SUCCESS;
				
				}else{
					addActionError("User already voted");
					return SUCCESS; //already vote	
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
	
	
	public String[] getListId() {
		if(listId != null)
			return listId;
		else
			return null;
	}

	public void setListId(String[] listId) {
		this.listId = listId;
		this.counter = listId.length;
	}
	

}
