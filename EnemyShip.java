import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
/**
 * This displays and run the red enemy ships on screen
 * @author CaDaMac
 * @version 1.0
 */

public class EnemyShip implements ShipInterface{
	private int shipX; 
	private double shipY;
	private static int shipSize = 45;
	private static int shipSpeed = 5;
	private int XPXPERSEC = 58, YPXPERSEC;
	private double XAcc = 0, moveToXAcc;
	private double YAcc;	
	private boolean isHit;
	private int crashFrames;
	private bullet shot;
	private boolean isDrone;

	//private boolean resetMove = false;

	private double angle;
	/**
	 * Instanciates a new EnemyShip and randomly places it off screen
	 */
	public EnemyShip(){
		//X bounds 0 - 900
		//Y bounds 0 - 822
		shipX = (int)(Math.random() * (mainFrame.getGalagaPanel().getfieldX() - 221 - (shipSize - 2)) + 1);
		shipY = -100;
		if((int)(Math.random() * 2) == 0){
			moveToXAcc = -shipSpeed;
		}
		else{
			moveToXAcc = shipSpeed;
		}
		YAcc = (Math.random() * 2 + 1);
		YPXPERSEC = (int) (YAcc * 58.0);
		isHit = false;
		crashFrames = 0;

		angle = (XAcc * -6) - 90;
		isDrone = false;
	}
	/**
	 * Displays ands runs this EnemyShip
	 * @param galagaPanel Graphics
	 */
	public void paint(Graphics g){
		if(shot != null)
			shot.paint(g);
		if(isHit || checkHit()){
			animateCrash(g);
			return;
		}
		else
			checkHitShip();


		if(!isDrone){
			//Randomly changes direction
			if((int)(Math.random() * 800) <= 15 ){
				moveToXAcc *= -1;
			}


			shipX += ((double) XPXPERSEC / (double) galagaPanel.getFrameRate() * (double) XAcc);

			YAcc = (double) YPXPERSEC / (double) galagaPanel.getFrameRate();
			//Moves ship
			shipY += YAcc;
		}

		//Protects bounds
		if(shipX <= 0 + 50){
			moveToXAcc = shipSpeed;
		}
		if(shipX >= (mainFrame.getGalagaPanel().getfieldX() - 220 - shipSize) - 50){
			moveToXAcc = -shipSpeed;
		}
		if(!isDrone){
			//Randomly fire bullet
			if((Math.random() * 800) <= 2.8){
				if(shot == null || shot.isFinished())
					shot = new bullet(shipX + (shipSize/2), (int) shipY + shipSize);
			}

			//Resets the ship to the top
			if(shipY > mainFrame.getGalagaPanel().getfieldY()){
				shipY = -shipSize;
				if((int)(Math.random() * 2) == 0){
					moveToXAcc = -shipSpeed;
				}
				else{
					moveToXAcc = shipSpeed;
				}
				YAcc = (int)(Math.random() * 2 + 1);
				YPXPERSEC = (int) (YAcc * 58.0);

			}
		}
		g.setColor(Color.red);
		g.fillPolygon(getShape());

		if(!isDrone){
			if(XAcc < moveToXAcc)
				XAcc += .25;
			if(XAcc > moveToXAcc)
				XAcc -= .25;


			angle = (XAcc * -6) - 90;
		}
	}
	/**
	 * Returns the shape of the EnemyShip with respect to current looking angle
	 * @return Polygon 
	 */
	public Polygon getShape(){
		int[] tmp;
		Polygon ship = new Polygon();

		tmp = defaultToAngled((int) shipX, (int) shipY, (int) angle - 45);
		ship.addPoint(tmp[0],tmp[1]);

		tmp = defaultToAngled((int) shipX + (shipSize / 2), (int) shipY + 10, (int) angle);
		ship.addPoint(tmp[0], tmp[1]);

		tmp = defaultToAngled((int) shipX + shipSize, (int) shipY, (int) angle + 45);
		ship.addPoint(tmp[0], tmp[1]);

		tmp = defaultToAngled((int) shipX + (shipSize / 2), (int) shipY + shipSize, (int) angle + 180);
		ship.addPoint(tmp[0], tmp[1]);

		return ship;
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
		double length = Math.sqrt((x - (shipX + (shipSize / 2))) * (x - (shipX + (shipSize / 2))) + (y - (shipY + (shipSize / 2))) * (y - (shipY + (shipSize / 2))));
		nx = (shipX + (shipSize / 2)) + (length * Math.cos(angle * Math.PI / 180));
		ny = (shipY + (shipSize / 2)) + (length * Math.sin(angle * Math.PI / 180));

		int[] ret = {(int)nx,(int)ny};
		return ret;
	}
	/**
	 * Animates the crash sequence on the screen
	 * @param Graphics of galagaPanel
	 * 
	 */
	public void animateCrash(Graphics g){
		if(crashFrames <= 30){
			g.setColor(Color.WHITE);
			g.fillOval(shipX - crashFrames,(int) shipY - crashFrames,crashFrames * 2 + 20,crashFrames * 2 + 20);
			crashFrames += 1 * (58 / galagaPanel.getFrameRate());
		}

	}
	/**
	 * Checks if the ship was hit by a bullet
	 * Kills ship if hit
	 * @return boolean if it was hit
	 */
	//Hit by a bullet
	public boolean checkHit(){
		if(isHit)
			return true;
		int w = bullet.getWidth();
		int h = bullet.getHeight();
		int[] xa = mainFrame.getGalagaPanel().getShotXs();
		int[] ya = mainFrame.getGalagaPanel().getShotYs();

		for(int x: xa){
			for(int y: ya){
				if(x > (shipX - w) && x < (shipX + shipSize) && y > (shipY - h) && y < (shipY + shipSize)){
					//System.out.println("EnemyShip hit");
					//System.out.println("numEnemyShips: " + super.numEnemyShips);
					isHit = true;
					mainFrame.getGalagaPanel().addPoints(200);
					mainFrame.getGalagaPanel().bulletHit(x,y);

					//If power up is deployed
					mainFrame.getGalagaPanel().getPowerUpDriver().newPowerDropDown((int) shipX + (shipSize / 2), (int) shipY);

					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Tests to see if ship impacted the mainShip<br>
	 * Kills self and mainShip
	 */
	//Hit by the MainShip
	public void checkHitShip(){
		int nx = mainFrame.getGalagaPanel().getShip().getX();
		int ny = mainFrame.getGalagaPanel().getShip().getY();
		int size = mainFrame.getGalagaPanel().getShip().getShipSize();

		if(nx >= (shipX - size) && nx <= (shipX + shipSize) && ny >= (shipY - size) && ny <= (shipY + shipSize)){
			isHit = true;
			System.out.println("Enemy ship hit mainShip");
			mainFrame.getGalagaPanel().shipHit("EnemyShip");
		}

	}
	/**
	 * Distroys all bullets this class has fired
	 */
	public void killBullet(){
		shot = null;
	}
	/**
	 * @return If gotten hit
	 */
	public boolean isHit(){
		return isHit;
	}

	public String toString(){
		//Return order: x loc, y loc, angle, crashFrames

		return " " + shipX + " " + shipY + " " + angle + " " + crashFrames + " ";
	}
	@Override
	public void setPosition(double x, double y, double a, int cFrames) {
		shipX = (int) x;
		shipY = y;
		angle = a;
		crashFrames = cFrames;
		if(cFrames > 0)
			isHit = true;

	}
	@Override
	public void setIsDrone(boolean isD) {
		isDrone = isD;

	}
	@Override
	public bullet getBullet() {
		if(shot != null && !shot.isFinished())
			return shot;
		return null;
	}
}
