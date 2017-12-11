package rmiserver;
import java.io.Serializable;

public class Department implements Serializable{
    String name;
    int id;

    public Department(String name, int id){
        this.name = name;
        this.id = id;
    }
}