package rmiserver;
import java.io.Serializable;

public class Election implements Serializable{
    String name;
    int id;
    Department department;

    public Election(String name, int id, Department department){
        this.name = name;
        this.id = id;
        this.department = department;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
    
    
}