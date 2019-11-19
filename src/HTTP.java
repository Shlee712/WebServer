import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

class HTTP {
	boolean FileNotFound = false;
	FileInputStream input;
	String fileName;
	OutputStream sockOutput;
	
	public HTTP(String file, OutputStream sockOutput) {
		fileName = "." + file;
		System.out.println("File name requested is:" + fileName);
		try {
			input = new FileInputStream(fileName);
			
		} catch(Exception e) {
			System.out.println("Requested file not found");
			FileNotFound = true;
		}
		this.sockOutput = sockOutput;
	}
	
	String ConstructStatusLine() {
		String version = "HTTP/1.0";
		String status;
		String phrase = "Status Line";
		String termination = "\r\n";
		if(FileNotFound) {
			status = "404 Not Found";
		}
		else {
			status = "200 OK";
		}
		return version + " " + status + " " + phrase + termination;
	}
	
	String ConstructResponseHeader() {
		String HeaderFieldName = "Content-type:";
		String value = contentType(fileName);
		String termination = "\r\n\r\n";
		return HeaderFieldName + " " + value + termination;
	}
	
	void SendEntityBody() {
		byte[] buffer = new byte[1024];
		int bRead = 0;
		String notFound = "<HTML>\r\n" + 
				"<HEAD><TITLE>Not Found</TITLE></HEAD>\r\n" + 
				"<BODY>Not Found</BODY>\r\n" + 
				"</HTML>";
		if(FileNotFound) {
			byte[] b = notFound.getBytes();
			try {
				sockOutput.write(b,0,b.length);
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println(e);
			}
			return;
		}
		try {
			while(  (bRead = input.read(buffer) )> 0) {
				sockOutput.write(buffer,0,bRead);
			}
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		try {
			sockOutput.close();

		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	private String contentType(String filename) {
		if(filename.endsWith(".htm") || filename.endsWith(".html")) {
			return "text/html";
		}
		else if(filename.endsWith(".gif")) {
			return "image/gif";
		}
		else if(filename.endsWith(".bmp")) {
			return "image/bmp";
		}
		else if(filename.endsWith(".jpeg") || filename.endsWith(".jpg")) {
			return "image/jpeg";
		}
		return "application/octet-stream";
		
	}
	

}
