import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class WebServer {
	public static void main(String args[]) {
		WebServer server = new WebServer();
		server.run();
	}
	
	public void run( ) {
		int portNum = 5520;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNum);
		} catch(IOException e) {
			e.printStackTrace();
		}
		while(true) {
			Socket sock = null;
			try {
				sock = serverSocket.accept();
			} catch(IOException e) {
				e.printStackTrace();
			}
			WebRequest servThread = new WebRequest(sock);
			servThread.start();
		}
		
	}
	
}
