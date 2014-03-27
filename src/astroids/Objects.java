package astroids;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Objects {
	Space.Types type;
	double[] vel = new double[2];
	double[] pos = new double[2];
	double[] dir = new double[2];
	double radius;
	long time;
	boolean kill_me = false;
	int priority;
	Color color;
	boolean fires = false;
	public static int craft_score = 1000;
	public static int asteroid_score = 200;
	
	
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
	 * You should not kill obj. You should just kill yourself :P
	 * 
	 * @param obj
	 */
	public abstract void collide(Objects obj);
	
	public abstract void update(Space s);
	
	public abstract void draw(Graphics2D g);
	
	public abstract void wallCollide(double[] normal);
	
	void print(){
		System.out.println(this.toString()+"");
		System.out.println("Pos: ("+pos[0]+","+pos[1]+")  " + "Vel: ("+vel[0]+","+vel[1]+")");
	}
	
	//This might be wrong....
	public void bounce(double[] normal){
		setVel(Vu.sub(Vu.mul((2*(Vu.scalarProduct(vel, normal)/Vu.scalarProduct(normal, normal))),normal),vel));
	}
	
	
}
