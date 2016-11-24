import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
/**
 * This class handles displaying a JFrame to gather data to make a new MultiplayerDriver.<br>
 * Lets player choose a Server or Client and sets the IP address if its a client.
 * @author CaDaMac
 * @version 1.0
 */

public class MultiplayerScreen extends JPanel implements ActionListener{
	private JFrame connect;
	private JButton sOrC;
	private JButton start;
	private JTextArea ip;
	private MultiplayerDriver ret = null;
	
	/**
	 * Creates a new MultiplayerScreen and displays the JFrame
	 */
	public MultiplayerScreen(){
		connect = new JFrame();
		connect.setAlwaysOnTop(true);
		connect.setUndecorated(true);
		connect.setSize(200, 300);
		connect.setLayout(new java.awt.GridLayout(8,2));
		
		sOrC = new JButton();
			sOrC.setText("Server");
			sOrC.addActionListener(this);
			connect.add(sOrC);
			
		start = new JButton();
			start.setText("Run " + sOrC.getText());
			start.addActionListener(this);
			connect.add(start);
			
		ip = new JTextArea();
			ip.setText("Your IP Address: ");
			ip.setEditable(false);
			connect.add(ip);
		connect.setVisible(true);
		
	}
	/**
	 * Handles any actions done by JFrame components<br>
	
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sOrC){
			if(sOrC.getText().equals("Server")){
				sOrC.setText("Client");
				start.setText("Run " + sOrC.getText());
				ip.setEditable(true);
				ip.setText("Server IP Address");
				connect.add(ip);
			}
			else{
				sOrC.setText("Server");
				start.setText("Run " + sOrC.getText());
				ip.setEditable(false);
				ip.setText("Your IP Address: ");
				connect.setVisible(false);
				connect.setVisible(true);
			}
		}
		
		//Creat multiplayerDriver
		if(e.getSource() == start){
			System.out.println("start button clicked");
			if(sOrC.getText().equals("Server")){
				ret = new MultiplayerDriver();
			}
			if(sOrC.getText().equals("Client")){
				String[] tmp = ip.getText().split("");
				int count = 0;
				for(String a: tmp){
					if(a.equals("."))
						count++;
				}
				if(count != 3 || tmp.length < 7){
					System.out.println("Invalid IP");
					return;
				}
				System.out.println("New Client! Server IP = " + ip.getText());
				ret = new MultiplayerDriver(ip.getText());
			}
			mainFrame.getGalagaPanel().setMultiplayerDriver(ret);
			connect.dispose();
		}
		
	}
}
