import java.awt.Graphics;
/**
 * This class handles applying the slow motion to the whole game<br>
 * Drops the frame rate to make it look slow
 * @author CaDaMac
 * @version 1.0
 */

public class SlowMotionPowerUp implements PowerUpInterface{
	
	public static final double MAXPERCENT = 100.0;
	private double percentLeft;
	private boolean isDead, isUsed;
	/**
	 * Creat new SlowMotionPowerUp
	 */
	public SlowMotionPowerUp(){
		System.out.println("New SuperSpeedPow");
		percentLeft = MAXPERCENT;
		//If the percent is out
		isDead = false;
		//If the effect is already be applied
		isUsed = false;
	}
	/**
	 * Apply shield effect to mainShip
	 */
	@Override
	public void effect(Graphics g) {
		if(isDead){
			return;
		}
		if(percentLeft <= 0.0){
			mainFrame.setDelay("Normal");
			isDead = true;
			return;
		}
		if(!isUsed){
			isUsed = true;
			mainFrame.getGalagaPanel().setNotification("Slow Speed Active");
			mainFrame.setDelay("Slow");
		}
		percentLeft -= 1;	
		
	}
	/**
	 * Returns if its done (unusable)
	 */
	@Override
	public boolean isDone() {
		return isDead;
	}
	/**
	 * Returns the current percent remaining
	 */
	@Override
	public double getPercentDone() {
		
		return (percentLeft / MAXPERCENT) * 100;
	}
	/**
	 * Sets the percent reamaining to 100%
	 */
	@Override
	public void reset() {
		isDead = false;
		percentLeft = MAXPERCENT;
		isUsed = false;
	}
	/**
	 * Set the percent to 0%
	 */
	@Override
	public void killPow() {
		percentLeft = 0;
		
	}
}
