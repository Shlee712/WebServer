import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

class WebRequest extends Thread {
	Socket clientSock;
	DataOutputStream sockWriter;
	BufferedReader reader;
	
	public WebRequest(Socket clientSock) {
		this.clientSock = clientSock;
	}
	
	public void run() {
		LocalDateTime date = LocalDateTime.now();
		System.out.println("Connection received. Date: " + date.toString() + " IP: " + clientSock.getInetAddress() + " Port: " +clientSock.getPort());
		
		try {
			reader = new BufferedReader(new InputStreamReader( clientSock.getInputStream()));
			sockWriter = new DataOutputStream(clientSock.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error: " +e.toString());
		}
		
		String msg = null;
		try {
			while(reader.ready()) {
				try {
					msg = reader.readLine();
					if(msg != null) {
						StringTokenizer st = new StringTokenizer(msg);
						while(st.hasMoreTokens()) {
							if(st.nextToken().equals("GET")) {
								System.out.println("Received GET request from IP: " + clientSock.getInetAddress() + " Port: " + clientSock.getPort());
								String temp;
								HTTP httpHandler = new HTTP(st.nextToken(),clientSock.getOutputStream());
								temp = httpHandler.ConstructStatusLine();
								System.out.println("Sending status line to IP: " + clientSock.getInetAddress() + " Port: " + clientSock.getPort());
								sockWriter.writeBytes(temp);
								sockWriter.flush();
								temp = httpHandler.ConstructResponseHeader();
								System.out.println("Sending Response Header to IP: " + clientSock.getInetAddress() + " Port: "+ clientSock.getPort());
								sockWriter.writeBytes(temp);
								sockWriter.flush();
								try {
									httpHandler.SendEntityBody();
								} catch (Exception e) {
									System.out.println("Something went wrong trying to read or send the file");
								}
							}
						}
					}
				
				} catch (IOException e) {
					System.out.println("Error: " + e.toString());
					break;
				}
			}
		} catch (IOException e1) {
			 System.out.println("Error: " + e1.toString());
		}
		try {
			sockWriter.close();
			System.out.println("Connection closed "  + clientSock.getPort() );
			clientSock.close();
			reader.close();
		} catch (IOException e) {
			System.out.println("Error: "+e.toString());
		}
		return;
	}
}
