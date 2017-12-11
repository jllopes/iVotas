package rmiserver;
import java.io.Serializable;

public class VotingTable implements Serializable{
    Department department;
    int id;

    public VotingTable(Department department, int id){
        this.department = department;
        this.id = id;
    }
}