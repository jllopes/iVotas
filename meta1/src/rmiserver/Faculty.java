import java.io.Serializable;

public class Faculty implements Serializable{
    String name;
    int id;

    public Faculty(String name, int id){
        this.name = name;
        this.id = id;
    }
}