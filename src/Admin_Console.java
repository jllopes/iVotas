import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Admin_Console {
    /*--------Main Menu---------*/
    public void mainMenu(){
        Scanner in = new Scanner(System.in);
        printMainMenu();
        int opt = in.nextInt();
        chooseMainMenu(opt);
    }
    public void printMainMenu(){
        System.out.println("<1> Register User");
        System.out.println("<2> Manage Departments");
        System.out.println("<3> Manage Faculties");
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
            case 3: manageFaculties();
                    break;
            case 4: newElection();
                    break;
            case 5: manageElections();
                    break;
            case 6: consultPastElections();
                    break;
        }
    }

    public void consultPastElections(){
        Scanner in = new Scanner(System.in);
        //listPastElections();
        System.out.println("Insert the id of which election you want to consult:");
        int id = in.nextInt();
        System.out.println("The results of the election were the following:");
        //printElectionResults(id);
    }

    public void newElection(){
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the type of election you want to create:"); //1-student, 2-general
        printElectionTypeMenu();
        int type = in.nextInt();
        int department;
        if(type == 1){
            System.out.println("Insert the department where the election is happening:");
            //listDepartments();
            department = in.nextInt();
        }
        else
            department = 0;
        System.out.println("Insert the start date of the election (Format: dd/mm/yy hh:mm):");
        Date startDate = getDate();
        System.out.println("Insert the end date of the election (Format: dd/mm/yy hh:mm):");
        Date endDate = getDate();
        System.out.println("Insert a title for the election:");
        String title = in.nextLine();
        System.out.println("Insert a description for the election:");
        String description = in.nextLine();
        createElection(startDate, endDate, title, description, type, department);
    }

    public Date getDate() {
        Scanner in = new Scanner(System.in);
        String dateString = in.nextLine();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            return date.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void newUser(){
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the user's name");
        String name = in.nextLine();
        System.out.println("Insert an username up to 16 characters:");
        String username = in.nextLine();
        System.out.println("Insert a password up to 16 characters:");
        String password = in.nextLine();
        System.out.println("Choose the type of user:");
        printUserTypeMenu();
        int type = in.nextInt();
        System.out.println("Choose the faculty of the user:");
        //printFacultyList();
        int facultyId = in.nextInt();
        System.out.println("Choose the department of the user:");
        //printDepartmentList(int facultyId);
        int departmentId = in.nextInt();
        System.out.println("Insert the ID number of the user:");
        int id = in.nextInt();
        System.out.println("Insert the validity month of the ID:");
        int idMonth = in.nextInt();
        System.out.println("Insert the validity year of the ID:");
        int idYear = in.nextInt();
        System.out.println("Insert the address of the user:");
        String address = in.nextLine();
        System.out.println("Insert the contact number of the user:");
        String phoneNumber = in.nextLine();
        register(name,username,password,type,facultyId,departmentId,address,id,idMonth,idYear,phoneNumber);
    }

    public void printElectionTypeMenu(){
        System.out.println("<1> Student Association");
        System.out.println("<1> General Council");
    }

    public void printUserTypeMenu(){
        System.out.println("<1> Student");
        System.out.println("<2> Professor");
        System.out.println("<3> Employee");
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

    public void newDepartment(){
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the name of the new department:");
        String name = in.nextLine();
        //printFaculties();
        System.out.println("Insert the faculty id of the department:");
        int faculty = in.nextInt();
        addDepartment(name,faculty);
    }

    public void alterDepartment(){
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the department:");
        //printDepartments();
        int department = in.nextInt();
        System.out.println("Insert the new name of the department:");
        String newName = in.nextLine();
    }
    
    /*--------Faculties Menu---------*/
    public void manageFaculties(){
        Scanner in = new Scanner(System.in);
        printFacultiesMenu();
        int opt = in.nextInt();
        chooseFacultiesMenu(opt);
    }
    public void printFacultiesMenu(){
        System.out.println("<1> Add Faculty");
        System.out.println("<1> Change Faculty");
        System.out.println("<1> Delete Faculty");
    }
    public void chooseFacultiesMenu(int opt){
        switch(opt){
            case 1: newFaculty();
                break;
            case 2: alterFaculty();
                break;
            case 3: removeFaculty();
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