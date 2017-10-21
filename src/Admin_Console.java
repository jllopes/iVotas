import java.util.*;

public class Admin_Console {
    public boolean register(String name, String username, String password, int type, String address, int num_id, int month_id, int year_id);
    public void addDeparment(String name, int id);
    public void addFaculty(String name);
    public void changeDepartment(String newName, int id);
    public void changeFaculty(String newName, int id);
    public void deleteDepartment(int id);
    public void deleteFaculty(int id);
    public void createElection(String startDate, String endDate, String name, String desc, int type);
    public void changeElectionLists(ElectionList list);
    public void changeElectionProperties(String startDate, String endDate, String name, String desc, int id);
    public void addVotingTable(int ip, int port, int depId);
    public String whereUserVoted(int electionId, int userId);
    //CALLBACK MESAS DE VOTO -- estado mesas de voto
    //CALLBACK MESAS DE VOTO -- número eleitores que votaram em cada mesa de voto até ao momento
    public void endElection();

    /*--------Main Menu---------*/
    public void mainMenu(){
        Scanner in = new Scanner(System.in);
        printMainMenu();
        int opt = in.nextInt();
        chooseMainMenu(opt);
    }
    public void printMainMenu(){
        System.out.println("<1> Register User"); //TODO
        System.out.println("<2> Manage Departments");
        System.out.println("<3> Manage Colleges");
        System.out.println("<4> Create Election");
        System.out.println("<5> Manage Election");
        System.out.println("<6> Consult Past Elections");
    }
    public void chooseMainMenu(int opt){
        switch(opt){
            case 1: newUser();
                    break;
            case 2: manageDepartments();
                    break;
            case 3: manageColleges();
                    break;
            case 4: newElection();
                    break;
            case 5: manageElections();
                    break;
            case 6: consultPastElections();
                    break;
        }
    }

    public void newUser(){
        Scanner in = new Scanner(System.in);

    }
    /*--------Departments Menu---------*/
    public void manageDepartments(){
        Scanner in = new Scanner(System.in);
        printDepartmentsMenu();
        int opt = in.nextInt();
        chooseDepartmentsMenu(opt);
    }
    public void printDepartmentsMenu(){
        System.out.println("<1> Add Department");
        System.out.println("<1> Change Department");
        System.out.println("<1> Delete Department");
    }
    public void chooseDepartmentsMenu(int opt){
        switch(opt){
            case 1: newDepartment();
                    break;
            case 2: alterDepartment();
                    break;
            case 3: removeDepartment();
                    break;
        }
    }
    /*--------Colleges Menu---------*/
    public void manageColleges(){
        Scanner in = new Scanner(System.in);
        printCollegesMenu();
        int opt = in.nextInt();
        chooseCollegesMenu(opt);
    }
    public void printCollegesMenu(){
        System.out.println("<1> Add College");
        System.out.println("<1> Change College");
        System.out.println("<1> Delete College");
    }
    public void chooseCollegesMenu(int opt){
        switch(opt){
            case 1: newCollege();
                break;
            case 2: alterCollege();
                break;
            case 3: removeCollege();
                break;
        }
    }
    /*--------Election Menu---------*/
    public void manageElections(){
        Scanner in = new Scanner(System.in);
        printElectionsMenu();
        int opt = in.nextInt();
        chooseElectionsMenu(opt);
    }
    public void printElectionsMenu(){
        System.out.println("<1> Add Candidates");
        System.out.println("<1> Remove Candidates");
        System.out.println("<1> Add Voting Table");
        System.out.println("<1> Remove Voting Table");
    }
    public void chooseElectionsMenu(int opt){
        switch(opt){
            case 1: addCandidates();
                    break;
            case 2: removeCandidates();
                    break;
            case 3: newVotingTable();
                    break;
            case 4: removeVotingTable();
                    break;
        }
    }



}

class Lista {
    String[] candidates;
    String name;
}