import java.awt.Graphics;
import java.util.ArrayList;
/**
 * This class handles running all the enemies and changing how each level behaves
 * @author CaDaMac
 *@version 1.0
 */

public class LevelRunner {
	private int level = 1;
	protected int numEnemyShips = 0;
	private ShipInterface[] ships;
	
	protected int numRocks = 0;
	
	private boolean paused = false;
	private int levelFrameCounter;
	/**
	 * Makes a new LevelRunner at level 1
	 */
	public LevelRunner(){
		level = 1;
		numEnemyShips = 1000;
		ships = null;
		numRocks = 0;
		paused = false;
		levelFrameCounter = 0;
		
	}
	/**
	 * Makes a new LevelRunner at a specified level
	 * @param starting level
	 */
	public LevelRunner(int startLevel){
		level = startLevel;
	}	
	/**
	 * 
	 * @return current level
	 */
	public int getLevel(){
		return level;
	}
	public void setLevel(int l){
		level = l;
	}
	/**
	 * If all enemies are dead
	 * @return boolean is level done
	 */
	public boolean isFinished(){
		int countEDead = 0;
		if(ships != null){
			for(ShipInterface e: ships){
				if(e.isHit())
					countEDead++;
			}
		}
		
		if(numEnemyShips + numRocks <= countEDead && levelFrameCounter > 0){
			System.out.println("Level " + level + " Finished");
			levelFrameCounter = 0;
			level += 1;
			return true;
		}
		else
			return false;
	}	
	/**
	 * Restarts the current level.<br>
	 * Revives all enemies
	 */
	public void resetLevel(){
		System.out.println("Reseting Level");
		levelFrameCounter = 0;
	}
	/**
	 * Distroys all bullets fired by the EnemyShips
	 */
	public void killBullets(){
		for(int a = 0; a < ships.length; a++){
			ships[a].killBullet();
		}
	}
	public ArrayList<bullet> getBullets(){
		ArrayList<bullet> ret = new ArrayList<bullet>();
		for(int a = 0; a < ships.length; a++){
			ret.add(ships[a].getBullet());
		}
		return ret;
	}
	/**
	 * Runs the level and the enemies
	 * @param galagaPanel Graphics
	 */
	public void act(Graphics g){
		if(paused)
			return;
		
		isFinished();
		//Calls current level method
		if(level >= 1)
			level1(g);
	}
	/**
	 * Runs level 1 of the game
	 * @param galagaPanel Graphics
	 */
	public void level1(Graphics g){
		
		if(level >= 1 && levelFrameCounter == 0){
			System.out.println("Level " + level + " created");
			numRocks = 5 * level;
			numEnemyShips = 5 * level;
			if(numRocks > 0){
				ships = new ShipInterface[numRocks + numEnemyShips];
			}
			else
				ships = new ShipInterface[numEnemyShips];
			for(int a = 0; a < numEnemyShips; a ++)
				ships[a] = new EnemyShip();
			for(int a = numEnemyShips; a < numRocks + numEnemyShips; a ++)
				ships[a] = new astroidShip();
			
		}
		for(ShipInterface a: ships)
			a.paint(g);
		levelFrameCounter += 1;
	}
	
	public String toString(){
		//Return: level number, num ships, [x,y,angle,crashFrames],...,num rocks, [x,y,numHits,crashFrames],...
		String ret = " " + level + " " + numEnemyShips + " ";
		
		for(int a = 0; a < numEnemyShips; a ++){
			ret += " " + ships[a].toString() + " ";
		}
		
		ret += " " + numRocks + " ";
		
		for(int a = numEnemyShips; a < numRocks + numEnemyShips; a ++){
			ret += " " + ships[a].toString() + " ";
		}
		
		return ret;
	}
}
