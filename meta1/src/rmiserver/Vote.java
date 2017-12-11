package rmiserver;

import java.io.Serializable;
import java.util.Date;

public class Vote implements Serializable{
    User user;
    Election election;
    VotingTable table;
    Date date;

    public Vote(User user, Election election, VotingTable table, Date date){
        this.user = user;
        this.election = election;
        this.table = table;
        this.date = date;
    }
}