package rmiserver;

import java.io.Serializable;

public class Department implements Serializable{
    public String name;
    public int id;

    public Department(String name, int id){
        this.name = name;
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
    
    
}