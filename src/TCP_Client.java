import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class establishes a TCP connection to a specified server, and loops
 * sending/receiving strings to/from the server.
 * <p>
 * The main() method receives two arguments specifying the server address and
 * the listening port.
 * <p>
 * The usage is similar to the 'telnet <address> <port>' command found in most
 * operating systems, to the 'netcat <host> <port>' command found in Linux, and
 * to the 'nc <hostname> <port>' found in macOS.
 *
 * @author Raul Barbosa
 * @author Alcides Fonseca
 * @version 1.1
 */
class TCPClient {
	public static void main(String[] args) {
		Socket socket;
		PrintWriter outToServer;
		BufferedReader inFromServer = null;
		try {
			// connect to the specified address:port (default is
			// localhost:12345)
			if (args.length == 2)
				socket = new Socket(args[0], Integer.parseInt(args[1]));
			else
				socket = new Socket("localhost", 12345);

			// create streams for writing to and reading from the socket
			inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToServer = new PrintWriter(socket.getOutputStream(), true);

			// create a thread for reading from the keyboard and writing to the
			// server
			new Thread() {
				public void run() {
					Scanner keyboardScanner = new Scanner(System.in);
					while (!socket.isClosed()) {
						String readKeyboard = keyboardScanner.nextLine();
						outToServer.println(readKeyboard);
					}
				}
			}.start();

			// the main thread loops reading from the server and writing to
			// System.out
			String messageFromServer;
			while ((messageFromServer = inFromServer.readLine()) != null)
				System.out.println(messageFromServer);
		} catch (IOException e) {
			if (inFromServer == null)
				System.out.println("\nUsage: java TCPClient <host> <port>\n");
			System.out.println(e.getMessage());
		} finally {
			try {
				inFromServer.close();
			} catch (Exception e) {
			}
		}
	}
}
