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
}
