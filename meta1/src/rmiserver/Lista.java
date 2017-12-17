package rmiserver;

import java.io.Serializable;

public class Lista implements Serializable{
    String name;
    int votes;
    int id;
    Election election;

    public Lista(String name, Election election, int votes, int id){
        this.name = name;
        this.id = id;
        this.election = election;
        this.votes = votes;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the votes
	 */
	public int getVotes() {
		return votes;
	}

	/**
	 * @param votes the votes to set
	 */
	public void setVotes(int votes) {
		this.votes = votes;
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
    
    
}
