import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Toolkit;

import javax.swing.JPanel;
/**
 * This class runs all graphical aspects of the program
 * @author CaDaMac
 * @version 1.0
 *
 */

public class galagaPanel extends JPanel{
	private static int fieldX = 1120, fieldY = 822;
	private static boolean isPause = false, foreverPause = false;
	private static int points = 0;
	private static int highScore = 30000;
	private static ScoreKeeper keeper;
	private static boolean keeperMade;

	private static int lives = 3;
	private static LevelRunner levels;

	private static MainShip mainShip;
	private static boolean left = false, right = false, up = false, down = false;

	private static bullet[] shots;
	private static bullet[] tmpBullets;
	private static bullet[] multiBullets;

	private static boolean isFiring = false;
	private static int canFire, canFiredelay;

	private static star[] stars;
	private static PowerUpDriver powDriver;

	private static String display;
	//how long the display is there for
	private static int displayRemaining;

	//Current Frame rate
	private static int frameRate = 58;
	private static int printedFrameRate = 0;
	//Counts millis so frame rate is updated on screen once every 1000 ms
	private static int millis = 0, frames = 0;

	private static int mouseX, mouseY;
	private static MultiplayerDriver multiplayer;

	/**
	 * Instanciates a new galagaPanel and all neccessary outside objects for game
	 * @param Size of JFrame x
	 * @param Size of JFrame y
	 */
	public galagaPanel(int x, int y){
		setDoubleBuffered(true);
		Toolkit.getDefaultToolkit().sync();
		fieldX = x;
		fieldY = y;
		System.out.println(fieldX + " " + fieldY);
		setPreferredSize(new Dimension(fieldX, fieldY));
		isPause = false; foreverPause = false;
		points = 0;
		highScore = Integer.parseInt(ScoreKeeper.getHighScore().get(0));
		keeperMade = false;

		lives = 3;
		left = false; right = false; up = false; down = false;
		isFiring = false;
		canFire = 7;
		//Default is 7, max fire is -1
		canFiredelay = 7;
		//Default is 3 max is 38
		shots = new bullet[3]   ;
		stars = new star[20];
		for(int a = 0; a < 20; a++)
			stars[a] = new star();
		//stars[1] = new star();

		mainShip = new MainShip();
		levels = new LevelRunner();
		tmpBullets = null;
		powDriver = new PowerUpDriver();
		display = "";
		displayRemaining = 0;
		multiplayer = null;
	}
	/**
	 * Paints and runs galagaPanel
	 * @param JPanel super class's Graphics
	 */
	public void paintComponent(Graphics g){
		long s = System.currentTimeMillis();

		if(foreverPause){
			super.paintComponent(g);
			g.setColor(Color.black);
			g.fillRect(0,0,fieldX, fieldY);

			for(star a: stars){

				a.paint(g);
			}
			if(mainShip.isHit())
				mainShip.paint(g);
			isDead(g);
			sidePanel(g);
			return;
		}
		if(isPause){
			mainShip.paint(g);
			dispNotification(g);
			drawCursor(g);
			if(multiplayer != null)
				multiplayer.run(g);
			return;
		}
		//super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0,0,fieldX, fieldY + 37);

		dispNotification(g);

		//canFire keeps from firing off all the round in a single button tap
		canFire += 1;
		//Default is 7
		if((double) canFire > (double)((double) canFiredelay * ((double) frameRate / 38.0)) && isFiring){
			canFire = 0;
			makeFire();
		}


		for(bullet a: shots){
			if(a != null)
				a.paint(g);
		}
		if(tmpBullets != null){
			for(bullet a: tmpBullets){
				if(a != null && !a.isFinished())
					a.paint(g);
			}
			if(multiBullets != null){
				for(bullet a: multiBullets){
					if(a != null && !a.isFinished())
						a.paint(g);
				}
				System.out.println("Multiplayer bullets fired");
			}
		}


		for(star a: stars){
			a.paint(g);
		}
		if(multiplayer == null || multiplayer.isServer())
			levels.act(g);
		//System.out.println(levels.isFinished());
		mainShip.paint(g);
		mainShip.move(left,right,up,down);
		if(multiplayer != null){
			multiplayer.run(g);
		}
		drawCursor(g);
		sidePanel(g);
		powDriver.act(g);


		//Frame Rate Calculations
		long elapsed = System.currentTimeMillis() - s + 17;
		frameRate = (int) (1000 / elapsed);
		//What Frame Rate to show on screen
		millis += elapsed; frames++;
		if(millis >= 1000) {
			printedFrameRate = frames;
			millis = 0;
			frames = 0;
		}
		//System.out.println("Frame Rate: " + (1000 / elapsed));

	}
	/**
	 * Moves mainShip left
	 * @param boolean if moving left
	 */
	public void moveL(boolean aleft){
		left = aleft;
	}
	/**
	 * Moves mainShip right
	 * @param boolean if moving right
	 */
	public void moveR(boolean aRight){
		right = aRight;
	}
	/**
	 * Moves mainShip up
	 * @param boolean if moving up
	 */
	public void moveU(boolean aUp){
		up = aUp;
	}
	/**
	 * Moves mainShip down
	 * @param boolean if moving down
	 */
	public void moveD(boolean aDown){
		down = aDown;
	}
	/**
	 * Tells mainShip to fire bullets
	 * @param Is firing
	 */
	public void fire(boolean firing){
		isFiring = firing;
	}
	/**
	 * 
	 * @return Is mainShip firing
	 */
	public boolean isFiring(){
		return isFiring;
	}
	/**
	 * Pause the whole game
	 */
	public void pause(){
		if(isPause == true){
			setNotification("Resume");
			isPause = false;
		}
		else{
			setNotification("Paused");
			isPause = true;
		}

	}
	/**
	 * Makes new bullets to be displayed in correct location
	 */
	public void makeFire(){
		int usable = getUsableBullet();
		if(isFiring == true){
			if(usable != -1){
				shots[usable] = new bullet(mainShip.getX(), mainShip.getY(), mainShip.getAngle());
			}
		}

	}
	/**
	 * Finds an out of bound bullet and returns it
	 * @return Array index of bullet
	 */
	private int getUsableBullet(){
		for(int a = 0; a < shots.length; a++){
			if(shots[a] == null || shots[a].isFinished())
				return a;

		}
		return -1;
	}
	/**
	 * Returns the x location of all mainShip bullets
	 * @return int Array of all x's
	 */
	public int[] getShotXs(){
		int[] ret;//TODO fix get xs
		if(tmpBullets != null){
			ret = new int[shots.length + tmpBullets.length];
		}
		else{
			ret = new int[shots.length];
		}

		for(int a = 0; a < shots.length; a++){
			if(shots[a] != null && !shots[a].isFinished())
				ret[a] = shots[a].getX();
			else 
				ret[a] = 10000;
		}

		if(tmpBullets != null) {
			for(int a = 0; a < tmpBullets.length; a++){
				if(tmpBullets[a] != null && !tmpBullets[a].isFinished())
					ret[a + shots.length] = tmpBullets[a].getX();
				else 
					ret[a + shots.length] = 10000;
			}
		}

		return ret;
	}
	/**
	 * Returns the y location of all mainShip bullets
	 * @return int Array of all y's
	 */
	public int[] getShotYs(){
		int[] ret;
		if(tmpBullets != null)
			ret = new int[shots.length + tmpBullets.length];
		else
			ret = new int[shots.length];

		for(int a = 0; a < shots.length; a++){
			if(shots[a] != null && !shots[a].isFinished())
				ret[a] = shots[a].getY();
			else 
				ret[a] = 10000;
		}

		if(tmpBullets != null) {
			for(int a = 0; a < tmpBullets.length; a++){
				if(tmpBullets[a] != null && !tmpBullets[a].isFinished())
					ret[a + shots.length] = tmpBullets[a].getY();
				else 
					ret[a + shots.length] = 10000;
			}
		}

		return ret;
	}
	public bullet[] getBullets(){
		return shots;
	}
	/**
	 * Add points to the score
	 * @param points
	 */
	public void addPoints(int a){
		points += a;
	}
	/**
	 * Displays the side bar of the game
	 * @param Graphics
	 */
	private void sidePanel(Graphics g){
		//starts at fieldX - 220
		//bottom bounds at fieldY

		//This covers stray blasts and ships
		g.setColor(Color.BLACK);
		g.fillRect(fieldX - 220, 0, 220, 800);

		g.setColor(Color.red);
		g.setFont(new Font("Bank Gothic", Font.BOLD, 35));
		g.drawString("HIGH", fieldX - 220, 30);
		g.drawString("SCORE", fieldX - 180, 60);
		g.drawString("1UP", fieldX - 220, 150);
		g.drawString("LEVEL",fieldX - 220, 660);

		g.setColor(Color.white);

		//Draws points
		if(points < highScore)
			g.drawString(Integer.toString(highScore), fieldX - 195, 90);
		else
			g.drawString(Integer.toString(points),fieldX - 195, 90);

		//DrawsPlayers Points
		g.drawString(Integer.toString(points), fieldX - 195, 180);

		//Tests to see if dead
		//System.out.println(lives);
		if(lives <= 0)
			isDead(g);

		int shipX = fieldX - 195;
		final int SHIPY = 380;
		for(int a = 1; a < lives; a++){
			g.setColor(Color.white);
			Polygon ship = new Polygon();
			ship.addPoint(shipX,SHIPY + 20);
			ship.addPoint(shipX + 20,SHIPY + 20);
			ship.addPoint(shipX + 10, SHIPY);
			g.fillPolygon(ship);
			shipX += 21;
		}
		g.drawString(Integer.toString(levels.getLevel()), fieldX - 170, 690);

		//Draw the power of hyperbeam remaining
		int locY = 420;
		for(PowerUpInterface a: powDriver.getPowerUps()){

			if(a instanceof HyperBeamPow && !a.isDone())
			{
				g.setColor(Color.CYAN);
				locY += 16;
				g.setFont(new Font("Bank Gothic", Font.BOLD, 15));
				g.drawString("HyperBeam", fieldX - 220, locY);
				locY += 16;
				int per = (int) a.getPercentDone();
				g.fillRect(fieldX - 220, locY, per, 15);
				locY += 16;
			}
			if(a instanceof SuperSpeedPow && !a.isDone())
			{
				g.setColor(Color.ORANGE);
				locY += 16;
				g.setFont(new Font("Bank Gothic", Font.BOLD, 15));
				g.drawString("SuperSpeed", fieldX - 220, locY);
				locY += 16;
				int per = (int) a.getPercentDone();
				g.fillRect(fieldX - 220, locY, per, 15);
				locY += 16;
			}
			if(a instanceof ShieldPower && !a.isDone())
			{
				g.setColor(Color.YELLOW);
				locY += 16;
				g.setFont(new Font("Bank Gothic", Font.BOLD, 15));
				g.drawString("Shields", fieldX - 220, locY);
				locY += 16;
				int per = (int) a.getPercentDone();
				g.fillRect(fieldX - 220, locY, per, 15);
				locY += 16;
			}
			if(a instanceof SlowMotionPowerUp && !a.isDone())
			{
				g.setColor(Color.RED);
				locY += 16;
				g.setFont(new Font("Bank Gothic", Font.BOLD, 15));
				g.drawString("Slow Motion", fieldX - 220, locY);
				locY += 16;
				int per = (int) a.getPercentDone();
				g.fillRect(fieldX - 220, locY, per, 15);
				locY += 16;
			}
			//System.out.println("LocX = " + locX);
		}

		//Draws Current Frame Rate
		g.setColor(Color.WHITE);
		g.setFont(new Font("Bank Gothic", Font.BOLD, 13));
		g.drawString(Integer.toString(printedFrameRate), fieldX - 35, fieldY - 10);

	}
	/**
	 * Sets the notifications to be displayed on screen
	 * @param String to display
	 */
	public void setNotification(String disp){
		display = disp;
		displayRemaining = 70;
	}
	/**
	 * Displays the notification. Animates fading
	 * @param Graphics
	 */
	public void dispNotification(Graphics g){
		if(displayRemaining <= 0)
			return;
		g.setFont(new Font("Bank Gothic", Font.BOLD, 25));

		if(displayRemaining > 50)
			g.setColor(Color.WHITE);
		else{
			if(displayRemaining > 0)
				g.setColor(new Color(0 + (displayRemaining * 5),0 + (displayRemaining * 5),0 + (displayRemaining * 5)));
		}

		g.drawString(display, 0, fieldY - 25);

		displayRemaining--;
	}
	/**
	 * Tells if an enemy has hit the ship
	 * @param String name of killer
	 */
	public void shipHit(String killer){
		if(mainShip.getShieldOn()){
			return;
		}
		//Calls and tells the MainShip to animate its death
		lives -= 1;
		System.out.println(killer + " hit mainShip");
		mainShip.shipWasHit();
		powDriver.killAllPow();
		powDriver.killAllDrop();
		setNotification("You died");
		//Pause game
		isPause = true;
	}
	/**
	 * Resets the mainShip and current level
	 */
	public void resetShip(){
		levels.killBullets();
		levels.resetLevel();
		mainShip = new MainShip();
		//Unpause game
		isPause = false;
		shots = new bullet[3];
		tmpBullets = null;
	}
	/**
	 * 
	 * @return The mainShip of the game
	 */
	public MainShip getShip(){
		return mainShip;
	}
	/**
	 * Distroys a bullet at a location
	 * @param Location x
	 * @param Location y
	 */
	public void bulletHit(int x, int y){
		if(shots[0] != null)
			if(shots[0].getX() == x && shots[0].getY() == y)
				shots[0].makeFinished();
		if(shots[1] != null)
			if(shots[1].getX() == x && shots[1].getY() == y)
				shots[1].makeFinished();
		if(shots[2] != null)
			if(shots[2].getX() == x && shots[2].getY() == y)
				shots[2].makeFinished();

	}
	/**
	 * Displays the game over screen
	 * @param Graphics
	 */
	private void isDead(Graphics g){

		g.setFont(new Font("Bank Gothic", Font.BOLD, 35));
		g.drawString("GAME OVER", fieldX / 2, fieldY / 2);
		//System.out.println("Dead");
		foreverPause = true;
		if(!keeperMade){
			keeper = new ScoreKeeper(points);
			keeperMade = true;
			System.out.println("Saved Score");
		}
	}
	/**
	 * 
	 * @return PowerUpDriver
	 */
	//PowerUp settings
	public PowerUpDriver getPowerUpDriver(){
		return powDriver;
	}
	/**
	 * Turn on the mainShip's hyperbeam
	 * @param Set is on
	 */
	public void enableHyperbeam(boolean on){
		System.out.println("Hyperbeam is " + on);
		if(on){
			canFiredelay = -1;
			tmpBullets = shots;
			shots = new bullet[48];
		}
		else{
			canFiredelay = 7;
			tmpBullets = shots;
			shots = new bullet[3];

		}
	}
	/**
	 * Add a life to total lives
	 */
	public void addLife() {
		if(lives < 8)
			lives++;
		else
			setNotification("Too many lives");
	}
	/**
	 * Resets the whole game
	 */
	public void resetALL(){
		//setPreferredSize(new Dimension(fieldX, fieldY));
		isPause = false; foreverPause = false;
		points = 0;
		highScore = Integer.parseInt(ScoreKeeper.getHighScore().get(0));
		keeperMade = false;

		lives = 3;
		left = false; right = false; up = false; down = false;
		isFiring = false;
		canFire = 20;
		//Default is 7, max fire is -1
		canFiredelay = 7;
		//Default is 3 max is 38
		shots = new bullet[3]   ;
		stars = new star[20];
		for(int a = 0; a < 20; a++)
			stars[a] = new star();
		//stars[1] = new star();

		mainShip = new MainShip();
		levels = new LevelRunner();
		tmpBullets = null;
		powDriver = new PowerUpDriver();
		display = "";
		displayRemaining = 0;
		multiplayer = null;
	}
	/**
	 * Sets the size of current game field
	 * @param x size
	 * @param y size
	 */
	public void setDimensions(int x, int y){
		fieldX = x;
		fieldY = y ;
	}
	/**
	 * 
	 * @return x size of field
	 */
	public static int getfieldX(){
		return fieldX;
	}
	/**
	 * 
	 * @return y size of field
	 */
	public static int getfieldY(){
		return fieldY;
	}
	/**
	 * Resets the placement of the stars
	 */
	public void resetStars() {
		for(int a = 0; a < stars.length; a++) {
			stars[a] = new star();
		}
	}
	/**
	 * 
	 * @return current game frame rate
	 */
	public static int getFrameRate() {
		if(frameRate > 0)
			return frameRate;
		else
			return 1;
	}
	/**
	 * Sets the position of the cursor
	 * @param x
	 * @param y
	 */
	public void setCursorPosition(int x, int y) {
		//System.out.println("Updating Cursor Pos. line 506");
		mouseX = x;
		mouseY = y;		
	}
	/**
	 * Draw the cursor crosshairs
	 * @param Graphics
	 */
	public void drawCursor(Graphics g){
		g.drawLine(mouseX - 15, mouseY, mouseX + 15, mouseY);
		g.drawLine(mouseX, mouseY - 15, mouseX, mouseY + 15);
	}
	/**
	 * Starts the MultiplayerScreen
	 */
	public void startMultiplayer(){
		if(multiplayer == null){
			System.out.println("Starting Multiplayer SetUp");
			pause();
			new MultiplayerScreen();
		}
		
	}
	/**
	 * Set the runnable MultiplayerDriver
	 * @param MultiplayerDriver
	 */
	public void setMultiplayerDriver(MultiplayerDriver m){
		System.out.println("MultiplayerDriver set in galgaPanel!");
		multiplayer = m;
		pause();
	}

	public LevelRunner getLevelRunner(){
		return levels;
	}
	public void setLevel(int l){
		levels.setLevel(l); 
	}
	public void setMultiplayerBullets(bullet[] b){
		multiBullets = b;
	}


}