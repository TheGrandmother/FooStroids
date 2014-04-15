package astroids;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
/**
 * This is the missile class.
 * Missiles are silly. They Should never do collision checks.
 * This is since there will most likely be far more missiles 
 * than other objects we don't want to have to test 
 * for each of the missiles
 * 
 * 
 * @author TheGrandmother
 *
 */
public class Missile extends Objects {
	//boolean fires = true;
	public static double speed = 20;
	public Craft sender;
	final public int life_span =  20;
	int age;

	
	public Missile(double[] pos, double[] vel, Craft sender){
		this.pos = pos;
		this.vel = vel;
		this.type = Space.Types.MISSILE;
		this.color = Color.white;
		super.radius = 5;
		super.priority = 0;
		this.sender = sender;
		this.age = 0;
		
	}
	
	@Override
	public void update(Space s) {
		move();
		if (age >= life_span) {
			kill_me = true;
		}else {
			age++;
		}

	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(3));
		g.drawLine((int)pos[0], (int)pos[1], (int)pos[0]+(int)vel[0]/2, (int)pos[1]+(int)vel[1]/2);
		g.setColor(color);
		g.setStroke(new BasicStroke(2));
		g.drawLine((int)pos[0], (int)pos[1], (int)pos[0]+(int)vel[0]/2, (int)pos[1]+(int)vel[1]/2);
//		g.setStroke(new BasicStroke());
//		g.setColor(Color.YELLOW);
//		g.drawOval((int)(pos[0]-radius), (int)(pos[1]-radius), 2*(int)radius, 2*(int)radius);

	}
	
	/**
	 * This should not be used!!!!!!
	 * Kill all missiles in the other methods instead.
	 */
	public void collide(Objects obj) {
		if(Vu.eclidianDistance(this.getPos(), obj.getPos()) <= (this.radius+obj.radius) && this.hashCode() != obj.hashCode()){
			switch (obj.type) {
				case SHIP:
					this.kill_me = true;
					break;
					
				case ASTEROID:
					this.kill_me = true;
					break;
				
				default:
					break;
			}
		}
	}
	
	@Override
	public void wallCollide(double[] normal) {
		kill_me = true;
	}

}
