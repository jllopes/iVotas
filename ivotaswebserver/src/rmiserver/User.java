package rmiserver;
import java.io.Serializable;

public class User implements Serializable{
    String username;
    int id;

    public User(String username, int id){
        this.username = username;
        this.id = id;
    }

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
