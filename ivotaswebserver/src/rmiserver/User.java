package rmiserver;
import java.io.Serializable;

public class User implements Serializable{
    String username;
    int id;

    public User(String username, int id){
        this.username = username;
        this.id = id;
    }
}
