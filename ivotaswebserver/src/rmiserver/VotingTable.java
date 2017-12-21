package rmiserver;

import java.io.Serializable;

public class VotingTable implements Serializable{
    Department department;
    int id;

    public VotingTable(Department department, int id){
        this.department = department;
        this.id = id;
    }

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}