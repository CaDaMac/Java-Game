import java.awt.Color;
import java.awt.Graphics;
import java.util.Scanner;
/**
 * This class displays a power up drop down that falls down continuously.<br>
 * Gives a power up to player if this PowerDropDown is caught
 * 
 * @author CaDaMac
 * @version 1.0
 */

public class PowerDropDown {
	private int shipX, shipY;
	private static final int SHIPSIZE = 20;
	private static  int YACC = 2;
	private static final int PXPERSEC = 116;
	private boolean isHit;
	
	/**
	 * Makes a new PowerDropDown at a random y location and 0 x location
	 */
	public PowerDropDown(){
		shipX = (int)(Math.random() * (int)(Math.random() * (900 - (SHIPSIZE - 2)) + 1));
		shipY = 0;
		isHit = false;
	}
	/**
	 * Makes a new PowerDropDown at a specified location
	 * @param x location
	 * @param y location
	 */
	public PowerDropDown(int x, int y){
		shipX = x;
		shipY = y;
		isHit = false;
	}
	/**
	 * Displays and runs the drop down
	 * @param galagaPanel Graphics
	 */
	public void paint(Graphics g){
		if(isHit){
			return;
		}
		else
			checkHit();
		
		YACC = (int)((double) PXPERSEC / (double) galagaPanel.getFrameRate());
		shipY += YACC;
		
		g.setColor(Color.ORANGE);
		g.drawOval(shipX, shipY, 35, 35);
		
		
		
	}
	/**
	 * Checks if this drop down is touching the main ship
	 * @return boolean is touching
	 */
	public boolean checkHit(){
		int nx = mainFrame.getGalagaPanel().getShip().getX();
		int ny = mainFrame.getGalagaPanel().getShip().getY();
		int size = mainFrame.getGalagaPanel().getShip().getShipSize() + 5;
		
		if(nx >= (shipX - size) && nx <= (shipX + SHIPSIZE + 5) && ny >= (shipY - size) && ny <= (shipY + SHIPSIZE + 5)){
			isHit = true;
			mainFrame.getGalagaPanel().getPowerUpDriver().randomPowerUp();
			return true;
		}
		return false;
		
	}
	/**
	 * Returns if this drop down was touched by the main ship
	 * @return boolean was touched
	 */
	public boolean isHit(){
		return isHit;
	}
	/**
	 * Distroys this drop down
	 */
	public void kill(){
		isHit = true;
	}
}
