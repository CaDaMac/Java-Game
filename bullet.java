import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
/**
 * Is the bullet used on screen
 * @author CaDaMac
 * @version 1.0
 */

public class bullet {
	private double bulletX, bulletY;
	private double bulletAngle;
	private int bulletSpeed;
	private int PXPERSEC;
	private double YAcc;
	private boolean checkSelf;//Kill mainShip
	private static final int bulletWidth = 9;
	private static final int bulletHeight = 15;
	//Compensates for invalid slopes
	private boolean up = false, down = false, left = false, right = false;
	//is it out of bounds
	private boolean isUseless = false;
	private static soundPlayer sound = new soundPlayer();
	private static boolean addedSound = false;
	/**
	 * Makes a bullet that will hit and kill ships that extend ShipInterface<br>
	 * Won't hurt mainShips
	 * @param bullet x location
	 * @param bullet y location
	 * @param bullet move angle
	 */
	//Main Ship makes this one
	public bullet(int x, int y, int angle){
		//Determins if impact detection is handled internally or not
		checkSelf = false;
		YAcc = -1;
		bulletX = x + ((mainFrame.getGalagaPanel().getShip().getShipSize() / 2) - (bulletWidth / 2));
		bulletY = y;
		bulletAngle = angle - 180;
		bulletSpeed = 20;
		PXPERSEC = 1160;
		setSlope();
		if(!addedSound){
			sound.add("gun");
			addedSound = true;
		}
		sound.justPlay();
	}

	/**
	 * Made by the EnemyShip<br>
	 * Will only harm mainShip<br>
	 * Only travels strait down
	 * @param bullet x location
	 * @param bullet y location
	 */
	//Enemy made this one
	//Assumed to be going strait down
	public bullet(int x, int y){
		checkSelf = true;
		YAcc = 0.5;		
		bulletX = x;
		bulletY = y;
		bulletAngle = 90;
		bulletSpeed = 12;
		PXPERSEC = 696;
		setSlope();
	}


	/**
	 * Displays and runs bullet
	 * @param galagaPanel Graphics
	 */
	public void paint(Graphics g){
		//System.out.println("Angle= " + bulletAngle);
		int rnd = (int)(Math.random() * 6);
		if(rnd == 0)
			g.setColor(Color.green);
		if(rnd == 1)
			g.setColor(Color.blue);
		if(rnd == 2)
			g.setColor(Color.yellow);
		if(rnd == 3)
			g.setColor(Color.red);
		if(rnd == 4)
			g.setColor(Color.orange);
		if(rnd == 5)
			g.setColor(Color.pink);

		if(checkSelf && hitMainShip()){
			//System.out.println("Main ship shot");
			mainFrame.getGalagaPanel().shipHit("bullet");
		}


		bulletSpeed = (int)((double) PXPERSEC / (double) galagaPanel.getFrameRate());
		g.fillPolygon(getShape());
		moveWithSlope();

	}
	/**
	 * Prevents problems with Math.sin directions<br>
	 * 0, pi, and 2*pi return 0 and crashes program elsewhere<br>
	 * Specifies if the angle is any of these special cases
	 */
	private void setSlope(){
		if(bulletAngle == 0){
			//System.out.println("right");
			right = true;
			return;
		}
		if(bulletAngle == 90){
			//System.out.println("down");
			down = true;
			return;
		}
		if(bulletAngle == 180){
			//System.out.println("left");
			left = true;
			return;
		}
		if(bulletAngle == -90){
			//System.out.println("up");
			up = true;
			return;
		}
	}
	/**
	 * Changes the bullet's x and y locations bases on the angle<br>
	 * No Parameters. Uses pre-set global variables
	 */
	private void moveWithSlope(){
		if(up){
			bulletY -= bulletSpeed;
			return;
		}
		if(down){
			bulletY += bulletSpeed;
			return;
		}
		if(left){
			bulletX -= bulletSpeed;
			return;
		}
		if(right){
			bulletX += bulletSpeed;
			return;
		}


		double scale_X = Math.sin(Math.toRadians(bulletAngle));
		double scale_Y = Math.cos(Math.toRadians(bulletAngle));
		double velosity_X = (bulletSpeed*scale_X);
		double velosity_Y = (bulletSpeed*scale_Y);

		bulletY += (int)velosity_X;
		bulletX += (int)velosity_Y;
	}
	/**
	 * Returns the shape of the bullet with regards to its angle
	 * @return Polygon of bullet
	 */
	public Polygon getShape(){
		Polygon ret = new Polygon();

		int[] p1 = defaultToAngled((int)bulletX, (int)bulletY, (int) bulletAngle + 31);
		ret.addPoint(p1[0], p1[1]);
		p1 = defaultToAngled((int)bulletX + bulletWidth, (int)bulletY, (int)bulletAngle - 31);
		ret.addPoint(p1[0], p1[1]);
		p1 = defaultToAngled((int)bulletX, (int)bulletY + bulletHeight, (int)bulletAngle + 31 + 180);
		ret.addPoint(p1[0], p1[1]);
		p1 = defaultToAngled((int)bulletX + bulletWidth, (int)bulletY + bulletWidth, (int) bulletAngle - 31 + 180);
		ret.addPoint(p1[0], p1[1]);

		return ret;		
	}
	/**
	 * Rotates a point about the center axis of the bullet based on angle.
	 * @param Origonal x location
	 * @param Origonal y location
	 * @param angle amount to rotate by
	 * @return x and y ints in an array
	 */
	public int[] defaultToAngled(int x, int y, int angle){

		double nx;
		double ny;
		double length = 8.746427;
		nx = (bulletX + (bulletHeight / 2)) + (length * Math.cos(angle * Math.PI / 180));
		ny = (bulletY + (bulletHeight / 2)) + (length * Math.sin(angle * Math.PI / 180));

		int[] ret = {(int)nx,(int)ny};
		return ret;
	}
	/**
	 * Tests if this bullet hit the mainShip.<br>
	 * Only tests if bullet was made by enemy
	 * @return boolean did it hit the mainShip
	 */
	//Is going in down, testing if MainShip hit
	private boolean hitMainShip(){
		//if(true)
		//	return true;
		int x = mainFrame.getGalagaPanel().getShip().getX();
		int y = mainFrame.getGalagaPanel().getShip().getY();
		int w = mainFrame.getGalagaPanel().getShip().getShipSize();
		int h = w;

		if(x >= (bulletX - w) && x <= (bulletX + bulletWidth) && y >= (bulletY - h) && y <= (bulletY + bulletHeight)){

			System.out.println("{Enemy}bullet hit ship");
			return true;
		}


		return false;
	}
	/**
	 * @return boolean if bullet is out of bounds and useless
	 */
	public boolean isFinished(){
		if(bulletY < -15 || bulletY > mainFrame.getGalagaPanel().getfieldY() || bulletX < -15 || bulletX > mainFrame.getGalagaPanel().getfieldX()  - 220) {
			isUseless = true;
			return true;
		}
		else
			return false;
	}
	/**
	 * Moves bullet out of bounds to make it useless
	 */
	public void makeFinished(){
		//if(YAcc == 0.5)
		//System.out.println("Enemy bullet makeFinished");
		if(YAcc == -1)
			bulletY = -15;
		else
			bulletY = -15;
	}
	/**
	 * 
	 * @return bullet x location
	 */
	public int getX(){
		if(isUseless)
			return -100;
		return (int) bulletX;
	}
	/**
	 * 
	 * @return bullet y location
	 */
	public int getY(){
		if(isUseless)
			return -100;
		return (int) bulletY;
	}
	/**
	 * 
	 * @return bullet width
	 */
	public static int getWidth(){
		return bulletWidth;
	}
	public static int getHeight(){
		return bulletHeight;
	}
	public void setDeadly(boolean d){
		checkSelf = d;
	}
	public String toString(){
		return " " + bulletX + "  " + bulletY + " " + bulletAngle + " " + (checkSelf? "1":"0") + " ";
	}
}
