import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
/**
 * Handles communication back and forth between 2 computers
 * @author CaDaMac
 * @version 0.5 alpha
 *
 */

public class MultiplayerDriver {
	//Server Data
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static PrintWriter toClient;
	private static BufferedReader fromClient;
	private static String CData;//shipX shipY bullet1X bullet1Y b2X b2Y...
	//Server graphics data
	private static MainShip player2;

	//Client Data
	private static InetAddress ip;
	private static Socket server;
	private static PrintWriter CtoServer;
	private static BufferedReader StoClient;
	private String SData;

	private static boolean isServer;

	private static Thread runnerThread;
	/**
	 * Make a new MultiplayerDriver as a server. Waits for connection
	 */
	//Server MultiplayerDriver
	public MultiplayerDriver(){
		isServer = true;
		serverSocket = null;
		try{
			serverSocket = new ServerSocket(6945);
			System.out.println("Successfully listened to port 6945");
		}
		catch(IOException e){
			System.err.println("Could not listen to port: 6945");
			return;
		}	System.out.println("Server IP: "+ serverSocket.getInetAddress().getHostAddress());
		clientSocket = null;

		//Waiting for client thread
		Thread t = new Thread(){
			@Override
			public void run(){ 
				try {
					clientSocket = serverSocket.accept();
					//Client accepted
					System.out.println("Client accepted: " + clientSocket.getInetAddress());

					//Setting up input and output streamers
					try {
						toClient = new PrintWriter(clientSocket.getOutputStream(), true);
						fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					}
					//Setting up continuously running reader
					Thread SThread = new Thread(){
						@Override
						public void run(){ 
							while(true){
								inFromClient();		
							}
						}
					};
					SThread.start();
					//Setting up graphical data
					player2 = new MainShip();
					player2.setIsDrone(true);
				} catch (IOException e) {
					System.err.println("Accept failed: 6945");
					e.printStackTrace();
					return;
				}
			}
		};
		t.start();

	}
	/**
	 * Makes a new MultiplayerDriver as a client.<br>
	 * Needs a IP to connect to.
	 * @param IP Address (ipv4)
	 */
	//Client MultiplayerDriver
	public MultiplayerDriver(String ipAddr){
		isServer = false;
		try {
			ip = InetAddress.getByName(ipAddr);
			server = new Socket(ip, 6945);
			System.out.println("Client connected to: " + server.getInetAddress().getHostAddress());
			CtoServer = new PrintWriter(server.getOutputStream(),true);
			StoClient = new BufferedReader(new InputStreamReader(server.getInputStream()));
		} catch (Exception e) {
			System.err.println("SERVER NOT FOUND");
		}
		runnerThread = new Thread(){
			@Override
			public void run(){ 
				while(true){
					fromServer();		
				}
			}
		};
		runnerThread.start();
		player2 = new MainShip();
		player2.setIsDrone(true);
	}
	/**
	 * Runs all client and server data and sends out data to others.<br>
	 * Displays other players and enemies (if client)
	 * @param galagaPanel Graphics
	 */
	public void run(Graphics g){
		if(clientSocket == null && server == null)
			return;
		//System.out.println(clientSocket.isConnected());
		//System.out.println(clientSocket.isBound());
		if(isServer){//SERVER!!!!!!!!!!!!!!!!!!!
			//System.err.println("Start Sending data!!!");
			//SENDING DATA VVVVV====================================================VVVVVV
			//Send order: main x,y,angle,hasShield,level,numEnemyShips,[x,y,angle,hit],numRocks,[x,y,angle,numHits,hit]
			String sendData = "";
			
			//Player 1 location data
			sendData += mainFrame.getGalagaPanel().getShip().toString();//x,y,angle

			//Checking for shield
			boolean hasShield = false;
			for(PowerUpInterface p: mainFrame.getGalagaPanel().getPowerUpDriver().getPowerUps()){
				if(p instanceof ShieldPower)
					hasShield = true;
			}
			if(hasShield)
				sendData += " " + 1 + " ";
			else
				sendData += " " + 0 + " ";
			
			//Getting enemy ships location data and level
			sendData += mainFrame.getGalagaPanel().getLevelRunner().toString();
			
			outToClient(sendData);
			
			//USING RECEIVED DATA VVVVV===============================================VVVVV
			//R Order: mainShipX, mainShipY, mainShipAngle, hasShield, 
			//#bullets, bullet x, y, angle, deadly
			
			if(CData == null){//No data recived
				player2.paint(g);
				return;
			}
	
			Scanner getData = new Scanner(CData);
			
			//Affect mainShip
			player2.move(getData.nextInt(), getData.nextInt());
			player2.setAngle(getData.nextInt());
			if(getData.nextInt() == 1)
				player2.setShield(true);
			else
				player2.setShield(false);
			player2.paint(g);
			
			//Making bullets
			bullet[] dispB = new bullet[getData.nextInt()];
			for(int a = 0; a < dispB.length; a++){
				double x = getData.nextDouble();
				double y = getData.nextDouble();
				double ang = getData.nextDouble();
				System.err.println("!!!!bullet x, y, angle: " + x + " " + y + " " + ang);
				dispB[a] = new bullet((int) x, (int) y, (int) ang);
				if(getData.nextInt() == 1)
					dispB[a].setDeadly(true);
				else
					dispB[a].setDeadly(false);
			}
			mainFrame.getGalagaPanel().setMultiplayerBullets(dispB);
			//System.err.println("End Sending data!!!");
		}
		if(!isServer){//CLIENT!!!!!!!!!!!!!!
			
			//SENDING DATA VVVVV====================================================VVVVVV
			//Send Order: mainShipX, mainShipY, mainShipAngle, hasShield, 
			//# bullets, bullet x, y, angle, deadly
			String sendData = "";
			sendData += mainFrame.getGalagaPanel().getShip().toString();//x,y,angle

			//Checking for shield
			boolean hasShield = false;
			for(PowerUpInterface p: mainFrame.getGalagaPanel().getPowerUpDriver().getPowerUps()){
				if(p instanceof ShieldPower)
					hasShield = true;
			}
			if(hasShield)
				sendData += " " + 1 + " ";
			else
				sendData += " " + 0 + " ";
			
			//Gathering bullets
			int bulletCount = 0;
			for(bullet b: mainFrame.getGalagaPanel().getBullets()) {
				if(b != null)
					bulletCount ++;
			}
			for(bullet b: mainFrame.getGalagaPanel().getLevelRunner().getBullets()){
				if(b != null)
					bulletCount ++;
			}
			sendData += " " + bulletCount + " ";
			for(bullet b: mainFrame.getGalagaPanel().getBullets()){
				if(b != null)
					sendData += " " + b.toString() + " ";
			}
			for(bullet b: mainFrame.getGalagaPanel().getLevelRunner().getBullets()){
				if(b != null)
					sendData += " " + b.toString() + " ";
			}
			
			
			toServer(sendData);
			
			//USING RECEIVED DATA VVVVV===============================================VVVVV
			//From server: mainShipX, mainShipY, mainShipAngle, hasShield
			//Return:level#,numShips,[x,y,angle,crashFrames],...,numRocks,[x,y,numHits,crashFrames],...
			if(SData == null){
				player2.paint(g);
				return;
			}
			//Using received data
			Scanner getData = new Scanner(SData);
			
			//Affect mainShip
			player2.move(getData.nextInt(), getData.nextInt());
			player2.setAngle(getData.nextInt());
			if(getData.nextInt() == 1)
				player2.setShield(true);
			else
				player2.setShield(false);
			player2.paint(g);
			
			//Create and use enemy ships (ShipInterface)
			//Return:level#,numShips,[x,y,angle,crashFrames],numRocks,[x,y,numHits,crashFrames]
			mainFrame.getGalagaPanel().setLevel(getData.nextInt());//level#
			EnemyShip[] tmp = new EnemyShip[getData.nextInt()];
			for(int a = 0; a < tmp.length; a++){
				tmp[a] = new EnemyShip();
				tmp[a].setIsDrone(true);
				
				tmp[a].setPosition(getData.nextInt(), getData.nextDouble(), getData.nextDouble(), getData.nextInt());
				tmp[a].paint(g);
			}
			astroidShip[] tmp2 = new astroidShip[getData.nextInt()];
			for(int a = 0; a < tmp2.length; a++){
				tmp2[a] = new astroidShip();
				tmp2[a].setIsDrone(true);
				double x = getData.nextDouble();
				double y = getData.nextDouble();
				int nH = getData.nextInt();
				int cF = getData.nextInt();
				tmp2[a].setPosition(x,y,0,cF);
				tmp2[a].setNumHits(nH);
				tmp2[a].paint(g);
			}
		}
	}
	/**
	 * Sends data to the client if this MultiplayerDriver is a server.
	 * @param String to send
	 */
	private void outToClient(String input){
		System.out.println("Server to Client: " + input);
		toClient.println(input.toString());
	}
	/**
	 * Checks for data sent from the client and stores it. <br>
	 * @return String data recieved. null if none
	 */
	private String inFromClient(){
		try {
			CData = fromClient.readLine();
			System.out.println("Client said: " + CData);
			return CData;
		} catch (IOException e){

			return null;
		}
	}
	/**
	 * Checks for data sent from the server and stores it. <br>
	 * @return String data recieved. null if none
	 */
	private String fromServer(){
		try {
			SData = StoClient.readLine();
			System.out.println("Server said: " + SData);
			return SData;
		} catch (IOException e) {
			return null;
		}
	}
	/**
	 * Sends data to the server if this MultiplayerDriver is a client.
	 * @param String to send
	 */
	private void toServer(String output){
		CtoServer.println(output);
	}
	/*
	public static void main(String[] args) throws UnknownHostException, IOException{
		//new MultiplayerDriver();
		MultiplayerDriver cl;
		System.out.print("Client or Server [C || S]?:: ");

		Scanner scan = new Scanner(System.in);
		System.out.println("\n");
		if(scan.next().equals("S")){
			cl = new MultiplayerDriver();
			while(1==1){
				cl.outToClient(scan.nextLine());
			}
		}
		else{
			System.out.print("Ip addr?:: ");
			cl = new MultiplayerDriver(scan.nextLine());
			while(1==1){
				cl.toServer("50 760 85  0  1 5   614 63.02666048367873 -118.5 0    129 95.63199258041448 -97.5 0    583 84.22012634655694 -67.5 0    435 -0.553737104955975 -60.0 0    235 100.52279239492488 -99.0 0   5  112.10996584051645 -9.090582099364099 0 0   701.1697925134246 66.0465239426014 0 0   176.04752491018118 65.53445736705713 0 0   825.1665788531235 21.958933513230583 0 0   218.4471605863165 137.08745555008747 0 0");
			}
		}
		}
	*/
	public boolean isServer(){
		return isServer;
	}
}



//From server: mainShipX, mainShipY, mainShipAngle, hasShield
//Return:level#,numShips,[x,y,angle,crashFrames],...,numRocks,[x,y,numHits,crashFrames],...

//Server to Client:  50 760 134  0  
//1 5   
//705 100.01969270159337 -70.5 0    
//746 215.8205674235686 -93.0 0    
//103 310.5667376506391 -60.0 0    
//101 213.1887293617055 -60.0 0    
//743 260.5618144752408 -60.0 0   
//5  
//217.62406653764637 331.8004097220117 0 0   
//745.9715012082221 176.87842967267727 0 0   
//373.3275676634467 51.563161272510065 0 0   
//920.3378634241063 168.81756057824802 0 31   
//581.0184808633704 91.2351249365926 0 0  



















