import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.applet.*;
import java.io.File;
import java.net.*;

/**
 * This is the starting class that starts the whole game.<br>
 * Makes the JFrame and launches galagaPanel
 * @author CaDaMac
 * @version 1.0
 */
public class mainFrame extends JFrame implements KeyListener, ActionListener, MouseListener, MouseMotionListener{
	//							Default is 24
	private static long DELAY_MILIS = 17;
	//private static StartUpScreen start;
	private static galagaPanel panel;
	private boolean canPause = true;

	//Handles full screen
	private GraphicsEnvironment ge;
	private GraphicsDevice gs;
	private boolean isFullScreen = false;
	
	//Handles making cursor blank
	private BufferedImage cursorImg;
	private Cursor blankCursor;
	
	/**
	 * Makes a new mainFrame
	 */
	public mainFrame(){
		//Adding hardware listeners
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		
		
		
		//Adjusting JFrame
		setSize(new Dimension(1120,822));
		panel = new galagaPanel(getWidth(), getHeight());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Galaga");
		setResizable(true);
		
		//Make start up screen
		//start = new StartUpScreen();
		//add(start);
		
		//while(!start.isDone()){
		//	start.repaint();
		//}
		
		
		//Add galaga game panel
		add(panel);
		
		//Make blank cursor
		cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		this.getContentPane().setCursor(blankCursor);
		System.setProperty("apple.awt.fullscreenhidecursor","true");
		
		
		Thread t = new Thread(){
			@Override
			public void run(){ 
				while(true){
					panel.repaint();
					try{
						Thread.sleep(DELAY_MILIS);
					}
					catch(Exception e){
						
					}
				
					if(!isFullScreen)
						((galagaPanel) panel).setDimensions(getWidth(), getHeight() - 32);
					else
						((galagaPanel) panel).setDimensions(getWidth(), getHeight());					
				}
			}
		};
		t.start();
	}
	/**
	 * Start game
	 * @param args
	 */
	public static void main(String[] args) {
		new mainFrame().setVisible(true);
	}
	/**
	 * Handles pressing of keys on the keyboard.<br>
	 * Called on by super
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("Key Pressed " + e.getKeyCode());
		if(e.getKeyChar() == 'r' || e.getKeyChar() == 'R')
			panel.resetALL();
		if(e.getKeyChar() == 'a' ||e.getKeyChar() == 'A')
			panel.moveL(true);
		if(e.getKeyChar() == 'd' ||e.getKeyChar() == 'D')
			panel.moveR(true);
		if(e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
			panel.moveU(true);
		if(e.getKeyChar() == 's' || e.getKeyChar() == 'S')
			panel.moveD(true);
		if(e.getKeyCode() == 27)
			System.exit(0);
		if(e.getKeyCode() == 17 || e.getKeyChar() == 'p' || e.getKeyChar() == 'P'){
			if(canPause){
				canPause = false;
				System.out.println("Paused");
				panel.pause();
			}
		}
		
		if(e.getKeyChar() == 'f' || e.getKeyChar() == 'F'){
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gs = ge.getDefaultScreenDevice();			
			
			setVisible(false);
			if(!isFullScreen){
				dispose();
				setUndecorated(true);
				gs.setFullScreenWindow(this);
				isFullScreen = true;
				panel.resetStars();
				System.out.println("Full Screen Mode");
				panel.setNotification("Full Screen Entered");
				//Make Cursor blank again
				
			}
			else{
				dispose();
				setUndecorated(false);
				gs.setFullScreenWindow(null);
				isFullScreen = false;
				panel.resetStars();
				System.out.println("Windowed Mode");
				panel.setNotification("Full Screen Exited");
			}
			setVisible(true);
		}
		if(e.getKeyChar() == '0'){
			panel.setNotification("100% odds!");
			panel.getPowerUpDriver().setOdds(1);
		}
		if(e.getKeyChar() == '9'){
			panel.setNotification("Default odds");
			panel.getPowerUpDriver().setOdds(PowerUpDriver.DEFAULT_RELEASE);
		}
		if(e.getKeyChar() == 'm' || e.getKeyChar() == 'M'){
			panel.startMultiplayer();
		}
		//panel.setNotification(e.getKeyChar() + "");
	}
	/**
	 * Handles releasing of keys on the keyboard.<br>
	 * Called on by super
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyChar() == 'a' || e.getKeyChar() == 'A')
			panel.moveL(false);
		if(e.getKeyChar() == 'd' || e.getKeyChar() == 'D')
			panel.moveR(false);
		if(e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
			panel.moveU(false);
		if(e.getKeyChar() == 's' || e.getKeyChar() == 'S')
			panel.moveD(false);
		if(e.getKeyCode() == 17 || e.getKeyChar() == 'p' || e.getKeyChar() == 'P'){
			canPause = true;
		}

	}
	/**
	 * Handles typing of keys on the keyboard.<br>
	 * Called on by super
	 */
	@Override
	public void keyTyped(KeyEvent e) {


	}
	/**
	 * Sets the delay on starting each frame.<br>
	 * Controls maximum frame rate of game
	 * @param speed
	 */
	public static void setDelay(String speed){
		System.out.println("setDelay(" + speed + ")");
		if(speed.equals("Slow")){
			DELAY_MILIS = 44;
		}
		if(speed.equals("Normal")){
			DELAY_MILIS = 17;
		}
	}
	/**
	 * Returns the galagaPanel
	 * @return galagaPanel
	 */
	public static galagaPanel getGalagaPanel(){
		return panel;
	}
	/**
	 * Handles any actions done by JFrame components
	 */
	@Override
	public void actionPerformed(ActionEvent e) {


	}
	/**
	 * Handles clicks on the mouse.<br>
	 * Called on by super
	 */
	@Override
	public void mouseClicked(MouseEvent e) {


	}
	/**
	 * Handles when mouse enters the JFrame.<br>
	 * Called on by super
	 */
	@Override
	public void mouseEntered(MouseEvent e) {


	}
	/**
	 * Handles when mouse exits the JFrame.<br>
	 * Called on by super
	 */
	@Override
	public void mouseExited(MouseEvent e) {


	}
	/**
	 * Handles presses on the mouse.<br>
	 * Called on by super
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1){
			if(!panel.isFiring())
				panel.fire(true);
		}

	}
	/**
	 * Handles releases on the mouse.<br>
	 * Called on by super
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1)
			panel.fire(false);

	}
	/**
	 * Handles dragging on the mouse.<br>
	 * Called on by super
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		panel.getShip().setAngle(x,y);
		panel.setCursorPosition(x,y);
	}
	/**
	 * Handles movement of the mouse.<br>
	 * Called on by super
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		panel.getShip().setAngle(x,y);
		panel.setCursorPosition(x,y);
		//System.out.println("mouseMoved Line 216");
	}
	/**
	 * Returns the current delay for each frame in milliseconds
	 * @return int delay
	 */
	public static long getDelayMilis() {
		return DELAY_MILIS;
	}
}





