import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

/**
 * Class that displays astroids.
 * Extends ShipInterface
 * @author CaDaMac
 * @version 1.0
 */
public class astroidShip implements ShipInterface{
	private double shipX;
	private double shipY;
	private static int shipSize = 45;
	private double XAcc;//Max for hit is 0.913 per 1 YAcc
	private double YAcc;
	private double XPXPERSEC;
	private double YPXPERSEC;
	private final static int HIT_MAX = 3;
	private int numHits;
	private boolean isDead;
	private int crashFrames;
	private Color rockColor;
	private boolean isDrone;

	/**
	 * Makes a  new astroid at a random location out of frame (Y<=146)
	 */
	public astroidShip() {
		shipX = (Math.random() * (mainFrame.getGalagaPanel().getfieldX() - 220 - shipSize));
		shipY = (Math.random() * - 146 - shipSize);
		XAcc = (Math.random() * 3 - 1);
		YAcc = (Math.random() * 2 + 1);
		XPXPERSEC = XAcc * 58;
		YPXPERSEC = YAcc * 58;
		numHits = 0;
		isDead = false;
		crashFrames = 0;
		rockColor = Color.GRAY;
		isDrone = false;
	}
	/**
	 * Display and run astroid
	 * @param Graphics of galagaPanel
	 * 
	 */
	@Override
	public void paint(Graphics g) {
		if(isDead || checkHit()) {
			animateCrash(g);
			return;
		}
		else 
			checkHitShip();
		if(!isDrone){
			YAcc = (double) YPXPERSEC / (double) galagaPanel.getFrameRate();
			shipY += YAcc;

			if(XAcc > 0)
				shipX += (double) XPXPERSEC / (double) galagaPanel.getFrameRate() * (double) XAcc;
			if(XAcc < 0)
				shipX += ((double) XPXPERSEC / (double) galagaPanel.getFrameRate() * (double) XAcc);

			if(shipY > mainFrame.getGalagaPanel().getfieldY())
				isDead = true;
			if(shipX > mainFrame.getGalagaPanel().getfieldX() - 220 + 20 || shipX < 0 - (shipSize + 20))
				isDead = true;
		}
		g.setColor(rockColor);
		g.fillPolygon(getShape());

	}

	/**
	 * Generates the shape of the astroid
	 * @return Polygon shape of astroid
	 */
	@Override
	public Polygon getShape() {
		Polygon poly = new Polygon();

		poly.addPoint((int) shipX + 23, (int) shipY + 5);
		poly.addPoint((int) shipX + 4, (int) shipY + 11);
		poly.addPoint((int) shipX + 8, (int) shipY + 20);
		poly.addPoint((int) shipX + 3, (int) shipY + 31);
		poly.addPoint((int) shipX + 11, (int) shipY + 36);
		poly.addPoint((int) shipX + 26, (int) shipY + 35);
		poly.addPoint((int) shipX + 31, (int) shipY + 23);
		poly.addPoint((int) shipX + 34, (int) shipY + 10);


		return poly;
	}
	/**
	 * Animates the crash sequence on the screen
	 * @param Graphics of galagaPanel
	 * 
	 */
	@Override
	public void animateCrash(Graphics g) {
		if(crashFrames <= 30){
			g.setColor(Color.WHITE);
			g.fillOval((int) shipX - crashFrames,(int) shipY - crashFrames,crashFrames * 2 + 20,crashFrames * 2 + 20);
			crashFrames += 1 * (58 / galagaPanel.getFrameRate());;
		}

	}
	/**
	 * Checks if the astroid was hit by a bullet
	 * Loses 1 health point if hit
	 * @return boolean if it was hit
	 */
	@Override
	public boolean checkHit() {
		if(isDead)
			return true;
		int w = bullet.getWidth();
		int h = bullet.getHeight();
		int[] xa = mainFrame.getGalagaPanel().getShotXs();
		int[] ya = mainFrame.getGalagaPanel().getShotYs();

		for(int x: xa){
			for(int y: ya){
				if(x > (shipX - w) && x < (shipX + shipSize) && y > (shipY - h) && y < (shipY + shipSize)){
					//System.out.println("AstroidShip hit");
					//						System.out.println("numEnemyShips: " + super.numEnemyShips);
					numHits++;
					int r = rockColor.getRed() - 40;
					int g = rockColor.getGreen() - 40;
					int b = rockColor.getBlue() - 40;
					if(r < 0) 
						r = 0;
					if(g < 0) 
						g = 0;
					if(b < 0) 
						b = 0;
					rockColor = new Color(r,g,b);
					mainFrame.getGalagaPanel().addPoints(100);
					if(numHits >= 3){
						isDead = true;
						mainFrame.getGalagaPanel().getPowerUpDriver().newPowerDropDown((int) shipX + (shipSize / 2), (int) shipY);
					}

					mainFrame.getGalagaPanel().bulletHit(x,y);
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Tests to see if astroid impacted the mainShip<br>
	 * Kills self and mainShip
	 */
	@Override
	public void checkHitShip() {
		int nx = mainFrame.getGalagaPanel().getShip().getX();
		int ny = mainFrame.getGalagaPanel().getShip().getY();
		int size = mainFrame.getGalagaPanel().getShip().getShipSize();

		if(nx >= (shipX - size) && nx <= (shipX + shipSize) && ny >= (shipY - size) && ny <= (shipY + shipSize)){
			isDead = true;
			System.out.println("astroidShip hit ship");
			mainFrame.getGalagaPanel().shipHit("astroidShip");
		}

	}
	/**
	 * Not used by this class<br>
	 * Just an override of the ShipInterface interface
	 */
	@Override
	public void killBullet() {
		//You have no bullets!
	}
	/**
	 * @return If this astroid has no health left
	 */
	@Override
	public boolean isHit() {

		return isDead;
	}
	@Override
	public void setPosition(double x, double y, double a, int cFrames) {
		shipX = x;
		shipY = y;
		crashFrames = cFrames;
		if(cFrames > 0)
			isDead = true;
	}
	public void setNumHits(int numhits){
		numHits = numhits;

		int r = rockColor.getRed() - (40 * numhits);
		int g = rockColor.getGreen() - (40 * numhits);
		int b = rockColor.getBlue() - (40 * numhits);
		if(r < 0) 
			r = 0;
		if(g < 0) 
			g = 0;
		if(b < 0) 
			b = 0;
		rockColor = new Color(r,g,b);


	}
	@Override
	public String toString(){
		//Return x, y, health, numHits, crashFrames
		return shipX + " " + shipY + " " + numHits + " " + crashFrames + " ";
	}
	@Override
	public void setIsDrone(boolean isD) {
		isDrone = isD;

	}
	@Override
	public bullet getBullet() {
		
		return null;
	}



}
