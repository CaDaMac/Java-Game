import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class clientComunicator {
	Socket clientSocket;
	BufferedReader in;
	PrintWriter out;
	
	public clientComunicator(Socket cl){
		clientSocket = cl;
		
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getData(){
		String ret;
		try {
			ret = in.readLine();
			return ret;
		} catch (IOException e) {
			return null;
		}
	}
	
	public void sendData(String outPut){
		out.println(outPut);
	}
	
}
