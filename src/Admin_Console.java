import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.rmi.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Admin_Console implements Admin_Interface_RMI {
    RMI_Interface_Admin rmi;
    String rmi_name;
    String rmi_ip;
    int rmi_port;

    public Admin_Console(){
        //Properties https://www.mkyong.com/java/java-properties-file-examples/
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("../tcpserverconfig.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            rmi_port = Integer.parseInt(prop.getProperty("rmi_port"));
            rmi_ip = prop.getProperty("rmi_ip");
            rmi_name = prop.getProperty("rmi_name");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void main(String args[]) {
        Admin_Console admin = new Admin_Console();
        /*rmi*/
        try {
            admin.rmi = (RMI_Interface_Admin) Naming.lookup("rmi://" + admin.rmi_ip+ ":" + admin.rmi_port+ "/" + admin.rmi_name);
            System.out.println("RMIFound");
            rmi.setAdmin(admin);
            mainMenu();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void receiveNotification(String notification){
        System.out.println(notification);
    }

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
        System.out.println("<7> Where User Voted");
    }

    public void chooseMainMenu(int opt) throws RemoteException{
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
            case 7: whereVoted();
                    break;
            default:
                System.out.println("Please insert a valid option");
                mainMenu();
                break;
        }
    }

    public void whereVoted() throws RemoteException{
        Scanner in = new Scanner(System.in);
        //printUsers();
        System.out.println("Insert the id of the user:");
        int user = in.nextInt();
        ArrayList<Vote> votes = rmi.getUserVotes(user);
        for(Vote vote : votes){
            System.out.println("Election Name: " + vote.election.name + " , Election Id: " + vote.election.id);
        }
        System.out.println("Insert the id of the election:");
        int election = in.nextInt();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for(Vote vote : votes){
            if(vote.election.id == election){
                String date = formatter.format(vote.date);
                System.out.println("Date: " + date + " , Table:" + vote.table);
                break;
            }
        }
    }

    public void consultPastElections() throws RemoteException{
        Scanner in = new Scanner(System.in);
        HashMap<String, Integer> pastElections = listPastElections();
        System.out.println("Insert the id of which election you want to consult:");
        int id = in.nextInt();
        while(checkPastElection(pastElections,id)){
            System.out.println("There is no election with that id, insert a valid id");
            id = in.nextInt();
        }
        System.out.println("The results of the election were the following:");
        printElectionResults(id);
    }

    public boolean checkPastElection(HashMap<String, Integer> elections, int election){
        for(int id : elections.values()){
            if(election == id)
                return true;
        }
        return false;
    }

    public HashMap<String, Integer> listPastElections() throws RemoteException{
        HashMap<String, Integer> elections = rmi.getPastElections();
        HashMap<String, Integer> elections_copy = elections;
        Iterator it = elections.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: "pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return elections_copy;
    }

    public void printElectionResults(int id){
        HashMap<String, Integer> results = rmi.getElectionResults();
        Iterator it = results.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + ": " + pair.getValue() + " votes");
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void newElection() throws RemoteException {
        Scanner in = new Scanner(System.in);
        int department;
        listDepartments();
        System.out.println("Insert the department where the election is happening (0 if general council):");
        department = in.nextInt();
        while(!rmi.checkDepartment(department)) {
            System.out.println("There is no department with that id, please insert a valid id");
            department = in.nextInt();
        }
        System.out.println("Insert the start date of the election (Format: dd/mm/yy hh:mm):");
        Date startDate = new Date();
        try {
            startDate = getDate();
        } catch (ParseException e){
            e.printStackTrace();
        }
        System.out.println("Insert the end date of the election (Format: dd/mm/yy hh:mm):");
        Date endDate = new Date();
        try {
            endDate = getDate();
        } catch (ParseException e){
            e.printStackTrace();
        }
        System.out.println("Insert a title for the election:");
        String title = in.nextLine();
        System.out.println("Insert a description for the election:");
        String description = in.nextLine();
        rmi.createElection(startDate, endDate, title, description, department);
    }

    public void listDepartments(){
        HashMap<String, Integer> departments = rmi.getAllDepartments();
        Iterator it = departments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: "pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public Date getDate() throws ParseException {
        Scanner in = new Scanner(System.in);
        String dateString = in.nextLine();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            return date.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.parse(dateString);
    }

    public void newUser()throws RemoteException{
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
        listFaculties();
        int facultyId = in.nextInt();
        System.out.println("Choose the department of the user:");
        listDepartmentsFromFaculty(int facultyId);
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
        rmi.register(name, username,password,type,facultyId,departmentId,address,id,idMonth,idYear,phoneNumber);
    }

    public void listDepartmentsFromFaculty(int faculty) throws RemoteException{
        HashMap<String, Integer> departments = rmi.getDepartmentsFromFaculty(faculty);
        Iterator it = departments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    /*public void printElectionTypeMenu(){
        System.out.println("<1> Student Association");
        System.out.println("<2> General Council");
    }*/

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
        System.out.println("<2> Change Department");
        System.out.println("<3> Delete Department");
    }

    public void chooseDepartmentsMenu(int opt){
        switch(opt){
            case 1: newDepartment();
                    mainMenu();
                    break;
            case 2: alterDepartment();
                    mainMenu();
                    break;
            case 3: removeDepartment();
                    mainMenu();
                    break;
        }
    }

    public void newDepartment() throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the name of the new department:");
        String name = in.nextLine();
        listFaculties();
        System.out.println("Insert the faculty id of the department:");
        int faculty = in.nextInt();
        while(!rmi.checkFaculty(faculty)) {
            System.out.println("There is no faculty with that id, please insert a valid id");
            faculty = in.nextInt();
        }
        rmi.addDepartment(name,faculty);
    }

    public void listFaculties(){
        HashMap<String, Integer> faculties = rmi.getAllFaculties();
        Iterator it = faculties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void alterDepartment(){
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the department:");
        //printDepartments(0);
        int department = in.nextInt();
        while(!rmi.checkDepartment(department)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            department = in.nextInt();
        }
        System.out.println("Insert the new name of the department:");
        String newName = in.nextLine();
        rmi.changeDepartment(newName,department);
    }

    public void removeDepartment(){
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the department:");
        //printDepartments(0);
        int department = in.nextInt();
        while(!rmi.checkDepartment(department)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            department = in.nextInt();
        }
        rmi.deleteDepartment(department);
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
        System.out.println("<2> Change Faculty");
        System.out.println("<3> Delete Faculty");
    }

    public void chooseFacultiesMenu(int opt){
        switch(opt){
            case 1: newFaculty();
                    mainMenu();
                break;
            case 2: alterFaculty();
                    mainMenu();
                break;
            case 3: removeFaculty();
                    mainMenu();
                break;
        }
    }

    public void newFaculty(){
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the name of the new faculty:");
        String name = in.nextLine();
        rmi.addFaculty(name);
    }

    public void alterFaculty(){
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the faculty:");
        //printFaculties();
        int faculty = in.nextInt();
        while(!rmi.checkFaculty(faculty)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            faculty = in.nextInt();
        }
        System.out.println("Insert the new name of the faculty:");
        String newName = in.nextLine();
        rmi.changeFaculty(newName,faculty);
    }

    public void removeFaculty() throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the faculty:");
        //printFaculties();
        int faculty = in.nextInt();
        while(!rmi.checkFaculty(faculty)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            faculty = in.nextInt();
        }
        rmi.deleteFaculty(faculty);
    }

    /*--------Election Menu---------*/

    public void manageElections() throws RemoteException{
        Scanner in = new Scanner(System.in);
        listElections();
        System.out.println("Insert the id of the election you want to manage:");
        int election_id = in.nextInt();
        int department = rmi.getDepartmentNumber(election_id);
        printElectionsMenu();
        int opt = in.nextInt();
        chooseElectionsMenu(opt, election_id, department);
    }

    public void listElections() throws RemoteException{
        HashMap<String, Integer> elections = rmi.getAllElections();
        Iterator it = elections.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    public void printElectionsMenu(){
        System.out.println("<1> Add List");
        System.out.println("<2> Remove List");
        System.out.println("<3> Add Voting Table");
        System.out.println("<4> Remove Voting Table");
        System.out.println("<5> Change Election Properties");
        System.out.println("<6> Add Candidates to List");
    }

    public void chooseElectionsMenu(int opt, int election_id, int department) throws RemoteException{
        switch(opt){
            case 1: addCandidates(department, election_id);
                    mainMenu();
                    break;
            case 2: removeCandidates(election_id);
                    mainMenu();
                    break;
            case 3: newVotingTable(election_id);
                    mainMenu();
                    break;
            case 4: removeVotingTable(election_id);
                    mainMenu();
                    break;
            case 5: changeElection();
                    mainMenu();
                    break;
            case 6:
                    addCandidatesToList(election_id, department);
                    break;
            default:
                    System.out.println("That is not a valid option");
                    mainMenu();
                    break;
        }
    }

    public void changeElection(int id) throws RemoteException {
        Scanner in = new Scanner(System.in);
        printChangeElectionMenu();
        System.out.println("Insert the option:");
        int opt = in.nextInt();
        chooseChangeElectionMenu(opt, id);
    }

    public void printChangeElectionMenu(){
        System.out.println("<1> Change start date");
        System.out.println("<2> Change end date");
        System.out.println("<3> Change name");
        System.out.println("<4> Change description");
    }

    public void changeStartDate(int election)throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the new start date of the election (Format: dd/mm/yy hh:mm):");
        Date startDate = new Date();
        try {
            startDate = getDate();
        } catch (ParseException e){
            e.printStackTrace();
        }
        rmi.changeElectionStartDate(election, startDate);
    }

    public void changeEndDate(int election)throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the new start date of the election (Format: dd/mm/yy hh:mm):");
        Date endDate = new Date();
        try {
            endDate = getDate();
        } catch (ParseException e){
            e.printStackTrace();
        }
        rmi.changeElectionEndDate(election, endDate);
    }

    public void changeName(int election)throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the new name for the election");
        String name = in.nextLine();
        rmi.changeElectionName(election, name);
    }

    public void changeDescription(int election)throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the new description for the election");
        String description = in.nextLine();
        rmi.changeElectionDescription(election, description);
    }

    public void chooseChangeElectionMenu(int opt, int election)throws RemoteException{
        switch (opt){
            case 1:
                changeStartDate(election);
                break;
            case 2:
                changeEndDate(election);
                break;
            case 3:
                changeName(election);
                break;
            case 4:
                changeDescription(election);
                break;
        }
    }

    public void addCandidates(int department, int election) throws RemoteException {
        Scanner in = new Scanner(System.in);
        System.out.println("Insert a name for the candidates list:");
        String name = in.nextLine();
        int type = 0;
        if (department != 0) {
            System.out.println("Choose the type of list you want to create:");
            printUserTypeMenu();
            type = in.nextInt();
        }
        rmi.createList(name, type, election);
    }

    public void addCandidatesToList(int election, int type) throws RemoteException{
        Scanner in = new Scanner(System.in);
        listElectionLists(election);
        System.out.println("What list do you want to add candidates to?");
        int list = in.nextInt();
        System.out.println("How many candidates are you going to insert?");
        int type = rmi.getListType(election);
        int number = in.nextInt();
        System.out.println("Insert the list of ids you want to be candidates of the list:");
        int id;
        ArrayList<Integer> users = new ArrayList<>();
        int check;
        int i = 0;
        while(i<number){
            id = in.nextInt();
            check = rmi.checkUserType(id);
            i++;
            if(type == 2){
                if(check == 1 && type == 1){ // Student
                    users.add(id);
                }
                else if(check == 2 && type == 2){ // Professors
                    users.add(id);
                }
                else if(check == 3 && type == 3){ // Professors
                    users.add(id);
                }
                else{
                    System.out.println("There is no user with such a username, or the user does not match the type you chose, please insert a valid username:");
                    i--;
                }
            }
            else {
                if (check == 1) { // Student
                    users.add(id);
                }
                else{
                    System.out.println("That username does not exist or the user is not a student, please insert a valid username:");
                    i--;
                }
            }
        }
        rmi.addCandidatesToList(users);
    }

    public void removeCandidates(int election) throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the name of the list you want to remove:");
        listElectionLists(election);
        String name = in.nextLine();
        rmi.removeList(election, name);
    }

    public void listElectionLists(int election){
        HashMap<String, Integer> lists = rmi.getElectionLists(election);
        Iterator it = lists.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: "pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void newVotingTable(int election){
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the id of the voting table you want to associate with the election:");
        //listVotingTables();
        int votingTable = in.nextInt();
        //addVotingTable(election, votingTable);
    }

    public void removeVotingTable(int election){
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the id of the voting table you want to remove from the election:");
        //listElectionTables(election);
        int votingTable = in.nextInt();
        //deleteVotingTable(election, votingTable);
    }
}