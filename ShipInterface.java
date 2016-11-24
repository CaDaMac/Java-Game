import java.awt.Graphics;
import java.awt.Polygon;


public interface ShipInterface {
	
	public void paint(Graphics g);
	public Polygon getShape();
	public void animateCrash(Graphics g);
	public boolean checkHit();
	public void checkHitShip();
	public void killBullet();
	public bullet getBullet();
	public boolean isHit();
	public void setPosition(double x, double y, double angle, int crashFrames);
	public String toString();
	public void setIsDrone(boolean isD);
}
