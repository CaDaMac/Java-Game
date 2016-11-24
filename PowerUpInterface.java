import java.awt.Graphics;


public interface PowerUpInterface {
	public void effect(Graphics g);
	public boolean isDone();
	public double getPercentDone();
	public void reset();
	public void killPow();
}
