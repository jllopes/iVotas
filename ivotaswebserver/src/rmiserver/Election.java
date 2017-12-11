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
}