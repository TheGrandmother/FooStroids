package astroids;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * This abstract class defines the behavior for objects that can live in the {@link Space}
 * @author The Grandmother
 *
 */
public abstract class Objects {
	Space.Types type;
	double[] vel = new double[2];
	double[] pos = new double[2];
	double[] dir = new double[2];
	double radius;
	long time;
	/**
	 * If this is true this object will be removed from the space in the next step
	 */
	boolean kill_me = false;
	int priority;
	private Color color;
	private boolean fires = false;
	final static int craft_score = 1000;
	final static int asteroid_score = 200;
	final static int fire_penalty = 2;
	
	
	public void move(){
		pos[0] += vel[0];
		pos[1] += vel[1];
	}
	
	public void setPos(double[] pos) {
		this.pos = pos;
	}
	
	public void setVel(double[] vel) {
		this.vel = vel;
	} 
	
	public double[] getPos() {
		return pos.clone();
	}
	
	public double[] getVel() {
		return vel.clone();
	}
	
	public double[] getDir() {
		return dir.clone();
	}
	
	/**
	 *This function handles what happens when this object collides with <b>obj</b><p>
	 *Two objects collides if the distance between the objects are lesser than the sum
	 *of the radiuses of the objects.
	 * 
	 *You should not kill <b>obj</b>. You should just kill yourself :P
	 *But everybody should kill missiles.
	 * @param obj the object that is being collided with.
	 */
	public abstract void collide(Objects obj);
	
	/**
	 * This method defines what happens every time the object gets updated.
	 * This method gets called every time {@link Space.update} is called.
	 * @param s
	 */
	public abstract void update(Space s);
	
	/**
	 * This method draws the object.
	 * This method gets called each time {@link Space.drawSpace} gets called.
	 *  
	 * @param g
	 */
	public abstract void draw(Graphics2D g);
	
	/**
	 * This method specifies what happens every time a object collides with a wall.
	 * @param normal The normal vector of the wall.
	 */
	public abstract void wallCollide(double[] normal);
	
	void print(){
		System.out.println(this.toString()+"");
		System.out.println("Pos: ("+pos[0]+","+pos[1]+")  " + "Vel: ("+vel[0]+","+vel[1]+")");
	}
	
	
	/**
	 * This is supposed to reflect the <b>vel</b> vector around <b>normal</b>. But it might be wrong :/
	 * @param normal
	 */
	public void bounce(double[] normal){
		setVel(Vu.sub(Vu.mul((2*(Vu.scalarProduct(vel, normal)/Vu.scalarProduct(normal, normal))),normal),vel));
	}

	public boolean isFires() {
		return fires;
	}

	public void setFires(boolean fires) {
		this.fires = fires;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	
}
