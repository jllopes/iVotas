public class Lista {
    String name;
    int votes;
    Election election;

    public Lista(String name, Election election, int votes){
        this.name = name;
        this.election = election;
        this.votes = votes;
    }
}
