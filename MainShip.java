import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
/**
 * Handles graphical aspect of the main ship of the game
 * @author CaDaMac
 * @version 1.0
 *
 */

public class MainShip{
	private int shipY = 760;
	private int shipX = 50;
	private int shipAngle = 90;

	//How many px per second the ship should move
	private int PXPERSEC = 580;
	private int SHIPSPEED = 10;
	private boolean isHit = false;
	private final int shipSize = 20;
	private int frameCount = 0;
	private double mouseX, mouseY;
	private boolean shield = false;
	private static soundPlayer deathSound;
	private boolean isDrone = false;

	/**
	 * Makes a new MainShip at (760 y, 50 x)
	 */
	public MainShip(){
		shipY = 760;
		//shipX = 50;
		isHit = false;
		frameCount = 0;	
		deathSound = new soundPlayer();
		System.out.println("Main ship made");
	}
	/**
	 * Displays and runs the ship
	 * @param Graphics
	 */
	public void paint(Graphics g){
		if(isHit == false){
			if(shield){
				g.setColor(Color.YELLOW);
				g.fillOval(shipX - 10, shipY - 10, shipSize + 20, shipSize + 20);
			}
			g.setColor(Color.white);
			g.fillPolygon(getShape());
		}
		else{
			crashAnimation(g);

		}
	}

	/**
	 * Returns the shape of the ship with respect to its current looking angle
	 * @return Polygon
	 */
	public Polygon getShape(){
		if(!isDrone)
			setAngle(mouseX, mouseY);
		Polygon ship = new Polygon();
		int[] tmp;

		//Bottom left
		tmp = defaultToAngled(shipX, shipY + shipSize, (shipAngle - 180) - 132);
		ship.addPoint(tmp[0],tmp[1]);

		//Bottom right
		tmp = defaultToAngled(shipX + shipSize, shipY + shipSize, (shipAngle - 180) + 132);
		ship.addPoint(tmp[0],tmp[1]);

		//Top
		tmp = defaultToAngled(shipX + (shipSize / 2), shipY, shipAngle - 180);
		ship.addPoint(tmp[0], tmp[1]);
		return ship;
	}
	/**
	 * Rotates a point about the center axis of the ship based on angle.
	 * @param Origonal x location
	 * @param Origonal y location
	 * @param angle amount to rotate by
	 * @return x and y ints in an array
	 */
	public int[] defaultToAngled(int x, int y, int angle){

		double nx;
		double ny;
		double length = Math.sqrt((x - (shipX + (shipSize / 2))) * (x - (shipX + (shipSize / 2))) + (y - (shipY + (shipSize / 2))) * (y - (shipY + (shipSize / 2))));
		nx = (shipX + (shipSize / 2)) + (length * Math.cos(angle * Math.PI / 180));
		ny = (shipY + (shipSize / 2)) + (length * Math.sin(angle * Math.PI / 180));

		int[] ret = {(int)nx,(int)ny};
		return ret;
	}
	/**
	 * Moves the ship in a specified direction.
	 * @param move left
	 * @param move right
	 * @param move up
	 * @param move down
	 */
	public void move(boolean left, boolean right, boolean up, boolean down){
		
		//Bounds at galagaPanel.fieldX - 240
		SHIPSPEED = (int) ((double) PXPERSEC / (double) galagaPanel.getFrameRate());
		int x = mainFrame.getGalagaPanel().getfieldX();
		int y = mainFrame.getGalagaPanel().getfieldY();
		if(!isHit){
			if(left)
				shipX -= SHIPSPEED;
			if(right)
				shipX += SHIPSPEED;
			if(shipX < 0)
				shipX = 0;
			if(shipX > x - 220 - shipSize)
				shipX = x - 221 - shipSize;
			if(up)
				shipY -= SHIPSPEED;
			if(down)
				shipY += SHIPSPEED;
			if(shipY < 0)
				shipY = 0;
			if(shipY > y - shipSize)
				shipY = y - shipSize;
		}
	}
	/**
	 * Sets the location of the ship.<br>
	 * Only used if this MainShip is a 2nd player
	 * @param x location
	 * @param y location
	 */
	public void move(int nx, int ny){
		int x = mainFrame.getGalagaPanel().getfieldX();
		int y = mainFrame.getGalagaPanel().getfieldY();
		//Set coor
		shipX = nx;
		shipY = ny;
		//Check bounds (shouldn't be needed)
		if(shipX < 0)
			shipX = 0;
		if(shipX > x - 220 - shipSize)
			shipX = x - 221 - shipSize;
		if(shipY < 0)
			shipY = 0;
		if(shipY > y - shipSize)
			shipY = y - shipSize;
		//System.out.println("Player 2 loc: " + shipX + " , " + shipY);
	}
	/**
	 * Returns the current x location
	 * @return x location
	 */
	public int getX(){
		return shipX;
	}
	/**
	 * Return the current y location
	 * @return y location
	 */
	public int getY(){
		return shipY;
	}
	/**
	 * Returns the current size of the ship
	 * @return size
	 */
	public int getShipSize(){
		return shipSize;
	}
	/**
	 * Tells the ship it was hit by something
	 */
	public void shipWasHit(){
		
		isHit = true;
		//deathSound.add("Shipdeath");
	}
	/**
	 * Returns if the ship was hit
	 * @return is hit
	 */
	public boolean isHit(){
		return isHit;
	}
	/**
	 * Returns the current looking direction of the ship
	 * @return angle
	 */
	public int getAngle(){
		return shipAngle;
	}
	/**
	 * Animates the crash of the ship
	 * @param galagaPanel Graphics
	 */
	private void crashAnimation(Graphics g){
		if(frameCount <= 30){
			g.setColor(Color.WHITE);
			g.fillOval(shipX - frameCount,shipY - frameCount,frameCount * 2 + 20,frameCount * 2 + 20);
			frameCount+= 1 * (58 / galagaPanel.getFrameRate());
		}
		else
			mainFrame.getGalagaPanel().resetShip();
	}
	/**
	 * Sets the looking angle based on the mouse location
	 * @param mouse x location
	 * @param mouse y location
	 */
	public void setAngle(double x, double y){
		
		mouseX = x;
		mouseY = y;
		double tmpY = shipY + 37;
		double tmpX = shipX + 12;
		if(mouseX - tmpX == 0 && tmpY-mouseY > 0){
			shipAngle = 90;
			return;
		}
		if(mouseX - tmpX == 0 && tmpY-mouseY < 0){
			shipAngle = 270;
			return;
		}
		if(tmpY-mouseY == 0 && mouseX-tmpX > 0){
			shipAngle = 180;
			return;
		}
		if(tmpY-mouseY == 0 && mouseX-tmpX < 0){
			shipAngle = 0;//Same as 360
			return;
		}



		//North West
		if(tmpX > mouseX && tmpY > mouseY)
			shipAngle = (int) Math.toDegrees(Math.atan((tmpY - mouseY)/(tmpX - mouseX)));

		//North East
		if(tmpX < mouseX && tmpY > mouseY)
			shipAngle = 180 + (int) Math.toDegrees(Math.atan((tmpY - mouseY)/(tmpX - mouseX)));

		//South East
		if(tmpX < mouseX && tmpY < mouseY)
			shipAngle = 180 + (int) Math.toDegrees(Math.atan((tmpY - mouseY)/(tmpX - mouseX)));

		//South West
		if(tmpX > mouseX && tmpY < mouseY)
			shipAngle = 360 + (int) Math.toDegrees(Math.atan((tmpY - mouseY)/(tmpX - mouseX)));

		//Print out angle
		//System.out.println("shipAngle " + shipAngle);
	}
	/**
	 * Sets the speed to be "Fast", "Normal", or "Slow"
	 * @param String speed
	 */
	public void setSpeed(String speed) {
		if(speed.equals("Fast")){
			PXPERSEC = 20 * 49;
			SHIPSPEED = 20;
		}
		if(speed.equals("Normal")){
			PXPERSEC = 10 * 49;
			SHIPSPEED = 10;
		}
		if(speed.equals("Slow")){
			PXPERSEC  = 49;
			SHIPSPEED = 1;
		}
	}
	/**
	 * Sets the angle based on absolute angle values
	 * @param angle
	 */
	public void setAngle(int a){
		shipAngle = a;
	}
	/**
	 * Turn shield on or off.
	 * @param boolean turn on
	 */
	public void setShield(boolean b) {
		shield = b;

	}
	/**
	 * Returns is the shield on
	 * @return boolean is on
	 */
	public boolean getShieldOn() {
		return shield;
	}
	/**
	 * Sets if this is an external player (Muliplayer)<br>
	 * Only displays an image
	 * @param Is a drone
	 */
	public void setIsDrone(boolean b){
		isDrone = b;
	}
	/**
	 * Returns a String packaged with the data needed for multiplayer communication
	 */
	public String toString(){
		return " " + shipX + " " + shipY + " " + shipAngle + " ";
	}
}












