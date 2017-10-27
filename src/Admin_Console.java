import java.io.*;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Admin_Console extends UnicastRemoteObject implements Admin_Interface_RMI {
    RMI_Interface_Admin rmi;
    String rmi_name;
    String rmi_ip;
    int rmi_port;

    public Admin_Console() throws RemoteException{
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

    public static void main(String args[]) throws RemoteException{
        Admin_Console admin = new Admin_Console();
        System.out.println(admin.rmi_ip);
        /*rmi*/
        try {

            admin.rmi = (RMI_Interface_Admin) Naming.lookup("rmi://" + admin.rmi_ip+ ":" + admin.rmi_port+ "/" + admin.rmi_name);
            System.out.println("RMIFound");
            admin.rmi.setAdmin((Admin_Interface_RMI) admin);
            admin.mainMenu();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void receiveNotification(String notification) throws RemoteException{
        System.out.println(notification);
    }

    /*--------Main Menu---------*/

    public void mainMenu() throws RemoteException{
        Scanner in = new Scanner(System.in);
        printMainMenu();
        int opt = Integer.parseInt(in.nextLine());
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
                    mainMenu();
                    break;
            case 2: manageDepartments();
                    mainMenu();
                    break;
            case 3: manageFaculties();
                    mainMenu();
                    break;
            case 4: newElection();
                    mainMenu();
                    break;
            case 5: manageElections();
                    mainMenu();
                    break;
            case 6: consultPastElections();
                    mainMenu();
                    break;
            case 7: whereVoted();
                    mainMenu();
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
        int user = Integer.parseInt(in.nextLine());
        HashMap<String, Integer> elections = rmi.getUserVotedElections(user);
        Iterator it = elections.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("Insert the id of the election:");
        int election = Integer.parseInt(in.nextLine());
        HashMap<Date, String> details = rmi.getUserVoteDetails(user, election);
        it = details.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date = formatter.format(pair.getKey());
            System.out.println("Date: " + date + ", Table: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void consultPastElections() throws RemoteException{
        Scanner in = new Scanner(System.in);
        HashMap<String, Integer> pastElections = listPastElections();
        System.out.println("Insert the id of which election you want to consult:");
        int id = Integer.parseInt(in.nextLine());
        while(checkPastElection(pastElections,id)){
            System.out.println("There is no election with that id, insert a valid id");
            id = Integer.parseInt(in.nextLine());
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
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return elections_copy;
    }

    public void printElectionResults(int id) throws RemoteException{
        HashMap<String, Integer> results = rmi.getElectionResults(id);
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
        department = Integer.parseInt(in.nextLine());
        while(!rmi.checkDepartment(department) && department != 0) {
            System.out.println("There is no department with that id, please insert a valid id");
            department = Integer.parseInt(in.nextLine());
        }
        System.out.println("Insert the start date of the election (Format: dd/mm/yyyy hh:mm):");
        Date startDate = new Date();
        try {
            startDate = getDate();
            while(startDate == null){
                System.out.println("Please insert a valid date! (Format: dd/mm/yyyy hh:mm");
                getDate();
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        System.out.println("Insert the end date of the election (Format: dd/mm/yyyy hh:mm):");
        Date endDate = new Date();
        try {
            endDate = getDate();
            while(endDate == null){
                System.out.println("Please insert a valid date! (Format: dd/mm/yyyy hh:mm");
                getDate();
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        System.out.println("Insert a title for the election:");
        String title = in.nextLine();
        System.out.println("Insert a description for the election:");
        String description = in.nextLine();
        rmi.createElection(startDate, endDate, title, description, department);
    }

    public void listDepartments() throws RemoteException {
        HashMap<String, Integer> departments = rmi.getAllDepartments();
        Iterator it = departments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
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
            return null;
        }
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
        int type = Integer.parseInt(in.nextLine());
        System.out.println("Choose the faculty of the user:");
        listFaculties();
        int facultyId = Integer.parseInt(in.nextLine());
        System.out.println("Choose the department of the user:");
        listDepartmentsFromFaculty(facultyId);
        int departmentId = Integer.parseInt(in.nextLine());
        System.out.println("Insert the ID number of the user:");
        int id = Integer.parseInt(in.nextLine());
        System.out.println("Insert the validity month of the ID:");
        int idMonth = Integer.parseInt(in.nextLine());
        System.out.println("Insert the validity year of the ID:");
        int idYear = Integer.parseInt(in.nextLine());
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

    public void manageDepartments() throws RemoteException {
        Scanner in = new Scanner(System.in);
        printDepartmentsMenu();
        int opt = Integer.parseInt(in.nextLine());
        chooseDepartmentsMenu(opt);
    }

    public void printDepartmentsMenu(){
        System.out.println("<1> Add Department");
        System.out.println("<2> Change Department");
        System.out.println("<3> Delete Department");
    }

    public void chooseDepartmentsMenu(int opt) throws RemoteException {
        switch(opt){
            case 1: newDepartment();
                    break;
            case 2: alterDepartment();
                    break;
            case 3: removeDepartment();
                    break;
        }
    }

    public void newDepartment() throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the name of the new department:");
        String name = in.nextLine();
        listFaculties();
        System.out.println("Insert the faculty id of the department:");
        int faculty = Integer.parseInt(in.nextLine());
        while(!rmi.checkFaculty(faculty)) {
            System.out.println("There is no faculty with that id, please insert a valid id");
            faculty = Integer.parseInt(in.nextLine());
        }
        rmi.addDepartment(name,faculty);
    }

    public void listFaculties() throws RemoteException {
        HashMap<String, Integer> faculties = rmi.getAllFaculties();
        Iterator it = faculties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void alterDepartment() throws RemoteException {
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the department:");
        listDepartments();
        int department = Integer.parseInt(in.nextLine());
        while(!rmi.checkDepartment(department)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            department = Integer.parseInt(in.nextLine());
        }
        System.out.println("Insert the new name of the department:");
        String newName = in.nextLine();
        rmi.changeDepartment(newName,department);

    }

    public void removeDepartment() throws RemoteException {
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the department:");
        listDepartments();
        int department = Integer.parseInt(in.nextLine());
        while(!rmi.checkDepartment(department)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            department = Integer.parseInt(in.nextLine());
        }
        boolean check = rmi.deleteDepartment(department);
        if(check){
            System.out.println("Operation completed successfully");
        } else {
            System.out.println("Operation is not valid, please confirm there is nothing linked to that department");
        }

    }

    /*--------Faculties Menu---------*/

    public void manageFaculties() throws RemoteException {
        Scanner in = new Scanner(System.in);
        printFacultiesMenu();
        int opt = Integer.parseInt(in.nextLine());
        chooseFacultiesMenu(opt);
    }

    public void printFacultiesMenu(){
        System.out.println("<1> Add Faculty");
        System.out.println("<2> Change Faculty");
        System.out.println("<3> Delete Faculty");
    }

    public void chooseFacultiesMenu(int opt) throws RemoteException {
        switch(opt){
            case 1: newFaculty();
                break;
            case 2: alterFaculty();
                break;
            case 3: removeFaculty();
                break;
        }
    }

    public void newFaculty() throws RemoteException {
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the name of the new faculty:");
        String name = in.nextLine();
        rmi.addFaculty(name);
    }

    public void alterFaculty() throws RemoteException {
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the faculty:");
        listFaculties();
        int faculty = Integer.parseInt(in.nextLine());
        while(!rmi.checkFaculty(faculty)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            faculty = Integer.parseInt(in.nextLine());
        }
        System.out.println("Insert the new name of the faculty:");
        String newName = in.nextLine();
        rmi.changeFaculty(newName,faculty);
    }

    public void removeFaculty() throws RemoteException{
        Scanner in = new Scanner(System.in);
        System.out.println("Choose the id of the faculty:");
        listFaculties();
        int faculty = Integer.parseInt(in.nextLine());
        while(!rmi.checkFaculty(faculty)){
            System.out.println("There is no faculty with that id, please insert a valid id:");
            faculty = Integer.parseInt(in.nextLine());
        }
        boolean check = rmi.deleteFaculty(faculty);
        if(check){
            System.out.println("Operation completed successfully");
        } else{
            System.out.println("Operation invalid, please confirm nothing is linked to the faculty");
        }
    }

    /*--------Election Menu---------*/

    public void manageElections() throws RemoteException{
        Scanner in = new Scanner(System.in);
        listElections();
        System.out.println("Insert the id of the election you want to manage:");
        int election_id = Integer.parseInt(in.nextLine());
        int department = rmi.getDepartmentNumber(election_id);
        printElectionsMenu();
        int opt = Integer.parseInt(in.nextLine());
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
                    break;
            case 2: removeCandidates(election_id);
                    break;
            case 3: newVotingTable(election_id);
                    break;
            case 4: removeVotingTable(election_id);
                    break;
            case 5: changeElection(election_id);
                    break;
            case 6:
                    addCandidatesToList(election_id, department);
                    break;
            default:
                    System.out.println("That is not a valid option");
                    break;
        }
    }

    public void changeElection(int id) throws RemoteException {
        Scanner in = new Scanner(System.in);
        printChangeElectionMenu();
        System.out.println("Insert the option:");
        int opt = Integer.parseInt(in.nextLine());
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
            type = Integer.parseInt(in.nextLine());
        }
        rmi.createList(name, type, election);
    }

    public void addCandidatesToList(int election, int type) throws RemoteException{
        Scanner in = new Scanner(System.in);
        listElectionLists(election);
        System.out.println("What list do you want to add candidates to?");
        int list = Integer.parseInt(in.nextLine());
        System.out.println("How many candidates are you going to insert?");
        int list_type = rmi.getListType(election);
        int number = Integer.parseInt(in.nextLine());
        in.nextLine();
        System.out.println("Insert the list of ids you want to be candidates of the list:");
        int id;
        ArrayList<Integer> users = new ArrayList<>();
        int check;
        int i = 0;
        while(i<number){
            id = Integer.parseInt(in.nextLine());
            check = rmi.checkUserType(id);
            i++;
            if(type == 2){
                if(check == 1 && list_type == 1){ // Student
                    users.add(id);
                }
                else if(check == 2 && list_type == 2){ // Professors
                    users.add(id);
                }
                else if(check == 3 && list_type == 3){ // Professors
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
        rmi.addCandidatesToList(election, users);
    }

    public void removeCandidates(int election) throws RemoteException{
        Scanner in = new Scanner(System.in);
        boolean check = listElectionLists(election);
        if(check) {
            System.out.println("Insert the id of the list you want to remove:");
            int id = Integer.parseInt(in.nextLine());
            rmi.removeList(id);
        }

    }

    public boolean listElectionLists(int election) throws RemoteException {
        HashMap<String, Integer> lists = rmi.getElectionLists(election);
        if(lists == null) {
            System.out.println("There are no lists for this election");
            return false;
        }
        Iterator it = lists.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Name: " + pair.getKey() + ", Id: " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return true;
    }

    public void newVotingTable(int election){
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the id of the voting table you want to associate with the election:");
        //listVotingTables();
        int votingTable = Integer.parseInt(in.nextLine());
        //addVotingTable(election, votingTable);
    }

    public void removeVotingTable(int election){
        Scanner in = new Scanner(System.in);
        System.out.println("Insert the id of the voting table you want to remove from the election:");
        //listElectionTables(election);
        int votingTable = Integer.parseInt(in.nextLine());
        //deleteVotingTable(election, votingTable);
    }
}