import java.awt.Color;
import java.awt.Graphics;

/**
 * Handles displaying and running a star in the background
 * @author CaDaMac
 * @version 1.0
 *
 */
public class star {
	private int speed;
	private int PXPERSEC;
	private int size = 4;
	private Color color;
	
	private int X, Y;
	
	/**
	 * Makes a new star with a random color and location
	 */
	public star(){
		int rnd = (int)(Math.random() * 3);
		if(rnd == 0)
			speed = 1;
		if(rnd == 1)
			speed = 2;
		if(rnd == 2)
			speed = 3;
		
		PXPERSEC = speed * 58;
		
		rnd = (int)(Math.random() * 6);
		if(rnd == 0)
			color = Color.green;
		if(rnd == 1)
			color = Color.blue;
		if(rnd == 2)
			color = Color.yellow;
		if(rnd == 3)
			color = Color.red;
		if(rnd == 4)
			color = Color.orange;
		if(rnd == 5)
			color = Color.pink;
		
		X = (int)(Math.random() * mainFrame.getGalagaPanel().getfieldX());
		Y = (int)(Math.random() * mainFrame.getGalagaPanel().getfieldY());
	}
	/**
	 * Displays and runs the star
	 * @param galagaPanel Graphics
	 */
	public void paint(Graphics g){
		g.setColor(color);
		speed = (int) ((double) PXPERSEC / (double) galagaPanel.getFrameRate());
		Y += speed;
		g.fillOval(X, Y, size, size);
		
		if(Y > galagaPanel.getfieldY()){
			X = (int)(Math.random() * 900);
			Y = 0;
		}
	}
}
