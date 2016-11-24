import java.awt.Graphics;
import java.util.ArrayList;

/**
 * This class handles running the PowerUpDrops and all power ups (that implement PowerUpInterface)
 * 
 * @author CaDaMac
 * @version 1.0
 *
 */

public class PowerUpDriver {
	private ArrayList<PowerDropDown> drops;
	private ArrayList<PowerUpInterface> powers;
	public static final int NUMBER_OF_DIFFRENT_POWERUPS = 5;
	//								default is 0.02
	private static double probRelease;
	public static final double DEFAULT_RELEASE = 0.02;

	/**
	 * Makes a new PowerUpDriver
	 */
	public PowerUpDriver(){
		drops = new ArrayList<PowerDropDown>();
		powers = new ArrayList<PowerUpInterface>();
		probRelease = DEFAULT_RELEASE;
		//powers.add(new SuperSpeedPow());
		//powers.add(new HyperBeamPow());
		//powers.add(new ShieldPower());
		//powers.add(new SlowMotionPowerUp());
	}
	/**
	 * Runs the power ups and drop downs
	 * @param galagaPanel Graphics
	 */
	public void act(Graphics g){
		//Remove dead drop downs
		ArrayList<PowerDropDown> tmp = new ArrayList<PowerDropDown>();
		for(PowerDropDown d: drops){
			d.paint(g);
			if(d.isHit())
				tmp.add(d);
		}
		for(int a = 0; a < tmp.size(); a++){
			drops.remove(tmp.get(a));
		}
		
		//Act the powers
		for(PowerUpInterface a: powers)
			a.effect(g);
		
	}
	/**
	 * Adds a random power up to the game
	 */
	public void randomPowerUp(){
		int rnd = (int)(Math.random() * NUMBER_OF_DIFFRENT_POWERUPS);

		//TODO add any new power ups here
		if(rnd == 0){
			boolean hasHyper = false;
			for(PowerUpInterface a : powers){
				if(a instanceof HyperBeamPow){
					hasHyper = true;
					a.reset();
				}
			}
			
			if(!hasHyper)
				powers.add(new HyperBeamPow());
		}
		if(rnd == 1){
			boolean hasSpeed = false;
			for(PowerUpInterface a : powers){
				if(a instanceof SuperSpeedPow){
					hasSpeed = true;
					a.reset();
				}
			}
			
			if(!hasSpeed)
				powers.add(new SuperSpeedPow());
		}
		if(rnd == 2){
			boolean hasShield = false;
			for(PowerUpInterface a : powers){
				if(a instanceof ShieldPower){
					hasShield = true;
					a.reset();
				}
			}
			
			if(!hasShield)
				powers.add(new ShieldPower());
		}
		if(rnd == 3){
			boolean hasSlow = false;
			for(PowerUpInterface a : powers){
				if(a instanceof SlowMotionPowerUp){
					hasSlow = true;
					a.reset();
				}
			}
			
			if(!hasSlow)
				powers.add(new SlowMotionPowerUp());
		}
		if(rnd == 4){
			boolean hasSlow = false;
			for(PowerUpInterface a : powers){
				if(a instanceof NewLivePowerUp){
					hasSlow = true;
					a.reset();
				}
			}
			
			if(!hasSlow)
				powers.add(new NewLivePowerUp());
		}
		return;
	}
	/**
	 * Adds a new PowerDropDown at a specified location.<br>
	 * Randomly decides if it adds it or not
	 * @param x location
	 * @param y location
	 */
	public void newPowerDropDown(int x, int y){
		if(Math.random() < probRelease)
			drops.add(new PowerDropDown(x, y));
	}	
	/**
	 * Returns all the currently effecting power ups
	 * @return ArrayList of power ups
	 */

	public ArrayList<PowerUpInterface> getPowerUps(){
		return powers;
	}
	/**
	 * Destroy and disable all power ups
	 */
	public void killAllPow(){
		for(PowerUpInterface a: powers){
			a.killPow();
		}
	}
	/**
	 * Destroys all drop downs
	 */
	public void killAllDrop(){
		for(PowerDropDown a: drops){
			a.kill();
		}
	}
	/**
	 * Sets the odds of a PowerDropDown being released
	 * @param odds
	 */
	public void setOdds(double odds) {
		probRelease = odds;
	}
}
