import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    public static void main(String[] args) {
        int numero=0;
        Scanner input;
        try {
            // connect to the specified address:port (default is localhost:12345)
            input = new Scanner(System.in);
            int serverPort = 12345;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            ArrayList<Socket> conns = new ArrayList<Socket>();

            new Thread(){
                public void run() {
                    try {
                        while (true) {
                            Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                            numero++;
                            conns.add(clientSocket);
                            new Connection(clientSocket, numero);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            try {
                int num;
                printInfoOptions(); // 1 - Nome, 2 - Id, 3 - Username
                while (true) { // Read From Keyboard
                    int num = input.nextInt();
                    switch (num) {
                        case 1: System.out.println("Insert name");
                                String name = input.nextLine();
                                //checkUserByName(name);
                                System.out.println("checkUserByUsername");
                                break;
                        case 2: System.out.println("Insert name");
                                int id = input.nextInt();
                                //checkUserById(id);
                                System.out.println("checkUserByUsername");
                                break;
                        case 2: System.out.println("Insert name");
                                String username = input.nextLine();
                                //checkUserByUsername(username);
                                System.out.println("checkUserByUsername");
                                break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            /*for (Connection conn : conns) {
                int status = conn.getStatus(); //0 -> unlocked/being used , 1 -> locked/free, 2 -> unlocked/not used (for blocking after 120s)
                if(!status){
                    //set current user to this conn ( terminal )
                    break;
                }
            }*/
            //TODO need to check what terminal is free
            //TODO switch to check what info is going to be read for verification

            //


            // the main thread loops reading from the client and writing to System.out
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public static boolean checkUserByName(String str){
        //for(voter:voters)
        //if(voter.name = str)
        //return true
        //return false
    }

    public static boolean checkUserById(int num){
        //for(voter:voters)
        //if(voter.id = num)
        //return true
        //return false
    }

    public static boolean checkUserByUsername(String str){
        //for(voter:voters)
        //if(voter.username = str)
        //return true
        //return false
    }

    public static User getUser(String usr) {
        User aux;
        for(User user: users) {
            if(user.username == usr) {
                aux = user;
            }
        }
        return aux;
    }

    public static void login(String usr, Scanner keyboardScanner) {
        System.out.println("Please insert your password");
        String pw = keyboardScanner.nextLine();
        User aux = getUser(usr);
        while(!verifyPassword(aux, pw)) {
            System.out.println("Password is incorrect, please try again.");
        }
    }

    public boolean void verifyPassword(User usr, String pw) {
        if (usr.password == pw)
            return true;
    }
    return false;
    }

    public static void printInfoOptions() {
        System.out.println("Please choose what information you want to identify the voter by:");
        System.out.println("<1> Name");
        System.out.println("<2> Id Number");
        System.out.println("<3> Username");
    }
    public static void unlockTerminal(Socket socket, PrintWriter outToClient, String usr){
        outToClient.println("type | unlock; username |" + usr)
    }

    public static void checkNumber(int number) {
        //Verificar número de eleitor e retornar detalhes
        //for ... eleitores
        // if eleitor.num = number
        // eleitor.toString()
    }

    public static void printElections() {
        //getElections()
        //System.out.println("Choose an election")
        //read choice
    }

    public static void getElections() {
    }

    //type | login; username | pierre ; password | omidyar
    //type | status; logged | on; msg | Welcome to iVotas
    //type | unlock --  //type | lock
    //type | vote; option | x; election | y
    //type | voter;

}

class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    int id;

    public Connection (Socket aClientSocket, int numero, ArrayList<Socket> connections) {
        id = numero;
        try{
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    //=============================
    public void run(){
        try{
            while(true){
                String data = in.readLine();
            }
            }
        }catch(EOFException e){System.out.println("EOF:" + e);
        }catch(IOException e){System.out.println("IO:" + e);}
    }

    public static boolean getStatus() {
        return this.status;
    }
}