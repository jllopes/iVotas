import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class establishes a TCP connection to a specified server, and loops
 * sending/receiving strings to/from the server.
 * <p>
 * The main() method receives two arguments specifying the server address and
 * the listening port.
 * <p>
 * The usage is similar to the 'telnet <address> <port>' command found in most
 * operating systems, to the 'netcat <host> <port>' command found in Linux,
 * and to the 'nc <hostname> <port>' found in macOS.
 *
 * @author Raul Barbosa
 * @author Alcides Fonseca
 * @version 1.1
 */
class TCPClient {
  int status; //0 -> unlocked/being used , 1 -> locked/free, 2 -> unlocked/not used (for blocking after 120s)
  PrintWriter outToServer;
  BufferedReader inFromServer = null;
  public static void main(String[] args) {
    Socket socket;
    try {
      // connect to the specified address:port (default is localhost:12345)
      if(args.length == 2)
        socket = new Socket(args[0], Integer.parseInt(args[1]));
      else
        socket = new Socket("localhost", 12345);
      // create streams for writing to and reading from the socket

      this.inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.outToServer = new PrintWriter(this.socket.getOutputStream(), true);
      this.status = 1;


      // create a thread for reading from the keyboard and writing to the server
      /*new Thread() {
        public void run() {
          Scanner keyboardScanner = new Scanner(System.in);
          while(!(socket.isClosed())) {
            String readKeyboard = keyboardScanner.nextLine();
            parseMessage(readKeyboard);
          }
        }
      }.start();*/



      // the main thread loops reading from the server and writing to System.out
      String messageFromServer;
      while((messageFromServer = this.inFromServer.readLine()) != null)
        //parseMessage();
        System.out.println(messageFromServer);
    } catch (IOException e) {
      if(this.inFromServer == null)
        System.out.println("\nUsage: java TCPClient <host> <port>\n");
      System.out.println(e.getMessage());
    } finally {
      try { this.inFromServer.close(); } catch (Exception e) {}
    }
  }


  /*public static void unlock(String usr){
    this.status = 2; // unlocked/not used
    System.out.println("This terminal has just been unlocked!");
    System.out.println("Hello " + usr + ", please insert your password:");
    Scanner in = new Scanner(System.in);
    String pw = in.nextLine();
    boolean res = verifyPassword();
    while(!res){
      System.out.println("Password is incorrect, please try again.")
      res = verifyPassword();
    }
    this.status = 0;
    vote();
  }*/
}