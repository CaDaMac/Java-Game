import java.awt.Graphics;
/**
 * This class handles giveing a new life mainShip
 * @author CaDaMac
 * @version 1.0
 */

public class NewLivePowerUp implements PowerUpInterface{
	public static final double MAXPERCENT = 1.0;
	private double percentLeft;
	private boolean isDead, isUsed;
	/**
	 * Create new NewLivePowerUp
	 */
	public NewLivePowerUp(){
		
		percentLeft = MAXPERCENT;
		isDead = false;
		isUsed = false;
	}/**
	 * Apply new life effect to game
	 */
	@Override
	public void effect(Graphics g) {
		if(isDead){
			return;
		}
		if(percentLeft <= 0.0){
			isDead = true;
			return;
		}
		if(!isUsed){
			isUsed = true;
			mainFrame.getGalagaPanel().setNotification("+1 Life");
			mainFrame.getGalagaPanel().addLife();
		}
		if(mainFrame.getGalagaPanel().isFiring())
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
	 * Returns the current percent remaining<br>
	 * This will return 0 imediatly after instanciation and 1 run of effect<br>
	 * since its not a continuous effect
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
		percentLeft = MAXPERCENT;
		isDead = false;
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
