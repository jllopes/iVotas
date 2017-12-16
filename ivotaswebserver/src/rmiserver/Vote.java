package rmiserver;
import java.io.Serializable;
import java.util.Date;

public class Vote implements Serializable{
    User user;
    Election election;
    VotingTable table;
    Date date;
    int id;

    public Vote(User user, Election election, VotingTable table, Date date, int id){
        this.user = user;
        this.election = election;
        this.table = table;
        this.date = date;
        this.id = id;
    }

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the election
	 */
	public Election getElection() {
		return election;
	}

	/**
	 * @param election the election to set
	 */
	public void setElection(Election election) {
		this.election = election;
	}

	/**
	 * @return the table
	 */
	public VotingTable getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(VotingTable table) {
		this.table = table;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
    
    
}