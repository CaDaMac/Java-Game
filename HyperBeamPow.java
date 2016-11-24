import java.awt.Graphics;
/**
 * This class handles applying the hyperbeam to the mainShip
 * @author CaDaMac
 * @version 1.0
 */

public class HyperBeamPow implements PowerUpInterface{
	
	public static final double MAXPERCENT = 100.0;
	private double percentLeft;
	private boolean isDead, isUsed;
	/**
	 * Creat new HyperBeamPow
	 */
	public HyperBeamPow(){
		System.out.println("New HyperBeamPow");
		percentLeft = MAXPERCENT;
		isDead = false;
		isUsed = false;
		mainFrame.getGalagaPanel().addPoints(50);
	}
	/**
	 * Apply hyperbeam effect to game
	 */
	@Override
	public void effect(Graphics g) {
		if(isDead){
			return;
		}
		if(percentLeft <= 0.0){
			mainFrame.getGalagaPanel().enableHyperbeam(false);
			isDead = true;
			return;
		}
		if(!isUsed){
			isUsed = true;
			mainFrame.getGalagaPanel().setNotification("Hyper Beam Enabled");
			mainFrame.getGalagaPanel().enableHyperbeam(true);
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
