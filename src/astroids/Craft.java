package astroids;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import astroids.Algorithms.Ai;
/**
 * 
 * This class is the craft which will be doing all of the cool things in FooStroid.
 * All of the love making and decision making gets handled here.
 * 
 * @author The_Grandmother
 */
public class Craft extends Objects{

	
	//public enum Action{ROTATE_RIGHT,ROTATE_LEFT,ACCELERATE,FIRE,NOTHING}
	final double thrust_power = .7; 
	final double rotation_constant = Math.PI/30;
	final double fov_angle = Math.PI/12;
	final double max_speed = 5;
	final double mutation_probability = 0.05;
	final double crossover_factor = 0.4;
	
	private double[] left_end_vector;
	private double[] left_vector;
	private double[] right_end_vector;
	private double[] right_vector;

	
	public int score;
	public String name;
	public int generation;
	public int age;
	public int eternal_score;
	
	private Ai ai;
	
	Fov fov;

	public Craft(double[] pos, double[] dir, double[] vel){
		this.pos = pos.clone();
		this.dir = Vu.normalize(dir.clone());
		this.vel = vel.clone();
		super.priority = 3;
		super.type = Space.Types.SHIP;
		super.setColor(Color.CYAN);
		super.radius = 7;
		super.kill_me = false;
		super.setFires(false);
		left_end_vector = Vu.rotate(dir,fov_angle*3);
		left_vector = Vu.rotate(dir,fov_angle/2);
		right_end_vector = Vu.rotate(dir,-fov_angle*3);
		right_vector = Vu.rotate(dir,-fov_angle/2);
		generation = 0;
		score = 0;
		generateName();
		age = 1;
		eternal_score = 0;
	}
	
	public void accelerate(){
		vel = Vu.add(Vu.mul(thrust_power, dir),vel);
		if(vel[0] >= max_speed){
			vel[0] = max_speed;
		}
		if(vel[1] >= max_speed){
			vel[1] = max_speed;
		}
	}
	
	public void decelerate(){
		vel = Vu.sub(vel,Vu.mul(thrust_power, dir));
		if(vel[0] <= -max_speed){
			vel[0] = -max_speed;
		}
		if(vel[1] <= -max_speed){
			vel[1] = -max_speed;
		}
	}
	
	public void rotateRight(){
		dir = Vu.normalize(Vu.rotate(dir,-rotation_constant));
		//vel = Vu.rotate(vel,-rotation_constant);
	}
	
	public void rotateLeft(){
		dir = Vu.normalize(Vu.rotate(dir,rotation_constant));
		//vel = Vu.rotate(vel,rotation_constant);
	}
	
	public void update(Space s){
		move();
		
		if(vel[0] <= -max_speed){
			vel[0] = -max_speed;
		}
		if(vel[1] <= -max_speed){
			vel[1] = -max_speed;
		}
		if(vel[0] >= max_speed){
			vel[0] = max_speed;
		}
		if(vel[1] >= max_speed){
			vel[1] = max_speed;
		}
		populateFov(s);
		getAi().makeDecision(this,fov);
		
		
	}
	
	
	
	/**
	 * This method will kill missiles as well.
	 */
	public void collide(Objects obj) {
		if(Vu.eclidianDistance(this.getPos(), obj.getPos()) <= (this.radius+obj.radius) && this.hashCode() != obj.hashCode()){

			switch (obj.type) {
			case MISSILE:
				if(this.name != ((Missile)obj).sender.name  ){
					this.kill_me = true;
					obj.kill_me = true;
					((Missile)obj).sender.score += Objects.craft_score;
					((Missile)obj).sender.eternal_score += Objects.craft_score;
				}
				break;
			
			case ASTEROID:
				this.kill_me = true;
				break;
			
				
			/*
			 * COLISSION IS DISABLED!
			 */
			case SHIP:
				//this.kill_me = true;
				break;
				
			default:
				break;
			}
		}
	}
	
	public void wallCollide(double[] normal) {
		bounce(normal);
	}
	
	/**
	 * Populates the field of view. Only adds objects with a priority({@link Objects.priority}) higher than 1. Only adds the closest object.
	 */
	private void populateFov(Space s){
		fov = new Fov();
		left_end_vector = Vu.normalize(Vu.rotate(dir,fov_angle*3));
		left_vector = Vu.normalize(Vu.rotate(dir,fov_angle/2));
		right_end_vector = Vu.normalize(Vu.rotate(dir,-fov_angle*3));
		right_vector = Vu.normalize(Vu.rotate(dir,-fov_angle/2));
		LinkedList<Objects> left_field = new LinkedList<Objects>();
		LinkedList<Objects> right_field = new LinkedList<Objects>();
		LinkedList<Objects> middle_field = new LinkedList<Objects>();
		boolean fl; //is true if obj is to the right of far left
		boolean l;
		boolean r;
		boolean fr;
		double min_distance = Double.MAX_VALUE; 
		double distace_factor = 3/2; 	// FActor for changing the "close" threshold
		
		for (Objects obj : s.object_list) {	
				fl	= toTheRight(obj.getPos(),left_end_vector);
				l	= toTheRight(obj.getPos(),left_vector);
				r	= toTheRight(obj.getPos(),right_vector); 
				fr	= toTheRight(obj.getPos(),right_end_vector); 	
				if(fl && !l && !r && !fr){left_field.add(obj);}
				if(fl && l && !r && !fr){middle_field.add(obj);}
				if(fl && l && r && !fr){right_field.add(obj);}
		}

		int max_priority = 1;
		
		for (Objects obj : left_field) {
			if(obj.priority >= max_priority){
				if(Vu.eclidianDistance(obj.getPos(), pos) < min_distance && Vu.eclidianDistance(obj.getPos(), pos) > .1){
					if(Vu.eclidianDistance(obj.getPos(), pos) < Missile.speed*Missile.life_span*distace_factor){
						fov.setLeft(this, obj,false);
					}else{
						fov.setLeft(this, obj,true);
					}
				}
			}
		}
		

		for (Objects obj : middle_field) {
			if(obj.priority >= max_priority){
				if(Vu.eclidianDistance(obj.getPos(), pos) < min_distance && Vu.eclidianDistance(obj.getPos(), pos) > .1){
					if(Vu.eclidianDistance(obj.getPos(), pos) < Missile.speed*Missile.life_span*distace_factor){
						fov.setMiddle(this, obj,false);
					}else{
						fov.setMiddle(this, obj,true);
					}
				}
			}
		}
		

		for (Objects obj : right_field) {
			if(obj.priority >= max_priority){
				if(Vu.eclidianDistance(obj.getPos(), pos) < min_distance && Vu.eclidianDistance(obj.getPos(), pos) > .1){
					if(Vu.eclidianDistance(obj.getPos(), pos) < Missile.speed*Missile.life_span*distace_factor){
						fov.setRight(this, obj,false);
					}else{
						fov.setRight(this, obj,true);
					}
				}
			}
		}
	}
	
	/**
	 * Checks if the point <b>p</b> is to the right of vector <b>v</b>
	 * @param p
	 * @param v
	 */
	private boolean toTheRight(double[] p, double[] v){
		double[] v1 = new double[3];
		v1[0] = v[0];
		v1[1] = v[1];
		v1[2] = 0;
		
		double[] p1 = new double[3];
		p1[0] = p[0] - pos[0];
		p1[1] = p[1] - pos[1];
		p1[2] = 0;
		
		double[] sneaky = Vu.crossProduct(Vu.normalize(p1), Vu.normalize(v1));
		if(sneaky[2] >= 0){
			return true;
		}else{
			return false;
		}
	}
	
	public void draw(Graphics2D g) {
		
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(5));
		g.drawLine((int)pos[0], (int)pos[1],(int)(15*dir[0]+pos[0]) ,(int)(15*dir[1]+pos[1]));
		g.setStroke(new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine((int)pos[0], (int)pos[1],(int)(dir[0]+pos[0]) ,(int)(dir[1]+pos[1]));
		
		g.setColor(getColor());
		g.setStroke(new BasicStroke(3));
		g.drawLine((int)pos[0], (int)pos[1],(int)(15*dir[0]+pos[0]) ,(int)(15*dir[1]+pos[1]));
		g.setColor(getColor());
		g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine((int)pos[0], (int)pos[1],(int)(1*dir[0]+pos[0]) ,(int)(1*dir[1]+pos[1]));
		
//		//UNCOMENT THESE LINES TO SE THE RADIUS
//		g.setStroke(new BasicStroke());
//		g.setColor(Color.YELLOW);
//		g.drawOval((int)(pos[0]-radius), (int)(pos[1]-radius), 2*(int)radius, 2*(int)radius);

//		
////		// UNCOMMENT THESE LINES TO SE THE FOV
//		int fov_length = 1000;
//		g.setColor(new Color(255, 255, 0, 100));
//		g.setStroke(new BasicStroke(1));
//		g.drawLine((int)pos[0], (int)pos[1],(int)(fov_length*left_end_vector[0]+pos[0]) ,(int)(fov_length*left_end_vector[1]+pos[1]));
//		
//		g.setColor(new Color(255, 255, 0, 100));
//		g.setStroke(new BasicStroke(1));
//		g.drawLine((int)pos[0], (int)pos[1],(int)(fov_length*right_end_vector[0]+pos[0]) ,(int)(fov_length*right_end_vector[1]+pos[1]));
//		
//		g.setColor(new Color(255, 255, 0, 100));
//		g.setStroke(new BasicStroke(1));
//		g.drawLine((int)pos[0], (int)pos[1],(int)(fov_length*right_vector[0]+pos[0]) ,(int)(fov_length*right_vector[1]+pos[1]));
//		
//		g.setColor(new Color(255, 255, 0, 100));
//		g.setStroke(new BasicStroke(1));
//		g.drawLine((int)pos[0], (int)pos[1],(int)(fov_length*left_vector[0]+pos[0]) ,(int)(fov_length*left_vector[1]+pos[1]));
//		
	}
		
	private void generateName(){
		String[] first = {"Millenium","Steel","Iron","Space","Light","Star","Stellar","Lazer","Galaxy","Icarus","Orion","Sky"};
		String[] second = {"Falcon", "Eagle", "Hawk","Fighter","Warrior","Lightning","Killer","Wing"};
		this.name = first[(int)(Math.random()*first.length)] + " "+ second[(int)(Math.random()*second.length)];
	}
	
	void print(){
		//fov.print();
		System.out.println(name + ". score:"+ score+" generation:"+generation);


	}

	public double[] getDir() {
		return dir;
	}
	
	public void setDir(double[] dir) {
		this.dir = Vu.normalize(dir.clone());
		
	}
	
	/**
	 * "Clones" a craft. Creates a new craft with the same descion_list
	 * All though it resets the score and the age.
	 */
	public Craft clone(){
		Craft clone = new Craft(pos, dir, vel);
		clone.setAi(ai);
		clone.fov = fov;
		clone.setColor(getColor());
		clone.score = 0;
		clone.eternal_score = 0;
		clone.age = 1;
		clone.generation = generation;
		
		return clone;
	}
	
	public void setAi(Ai ai){
		this.ai = ai;
	}
	
	/**
	 * "Mates" this craft with <b>mummy</b>.
	 * It uses random crossover.
	 * A new color will be generated based on the color of this craft and that of the mummy.
	 * May mutate the craft as well. In this case one of the entries in the new decision_list
	 * created by the mating will be changed to a random decision. The color of the craft will also
	 * be inverted. 
	 * 
	 * @param mummy
	 */
	public void mate(Craft mummy){
		
		
		getAi().crossover(mummy, crossover_factor);
		setColor(new Color(getColor().getRed(), (getColor().getGreen()/2)+(mummy.getColor().getGreen()/2), mummy.getColor().getBlue()));
		getAi().mutate(this, mutation_probability);

	}
	
	public int getFitness(){
		return (eternal_score/age);
	}
	
	public Ai getAi() {
		return ai;
	}

	public class Fov{
		
		//Why can't I put enums in local classes????
		private int[] heading; 	//{left,middle,right} -1 is heading left, 0 is heading straight, 1 is heading right
		private boolean[] far_away = {false,false,false}; //{left,middle,right}
		private Space.Types[] type;	//{left,middle,right}
		private double heading_threshold =(1/Math.sqrt(100));
		
		public Fov(){
			this.heading = new int[3];
			this.type = new Space.Types[3];
		}
		
		public void setLeft(Craft source, Objects target,boolean far){
			heading[0] = getHeading(source, target);
			type[0] = target.type;
			far_away[0] = far;
		}
		public void setMiddle(Craft source, Objects target,boolean far){
			heading[1] = getHeading(source, target);
			type[1] = target.type;
			far_away[1] = far;
		}
		public void setRight(Craft source, Objects target,boolean far){
			heading[2] = getHeading(source, target);
			type[2] = target.type;
			far_away[2] = far;
		}

		private int getHeading(Objects source, Objects target){
			double heading_threshold = Math.PI/15;
			double[] source_heading = Vu.normalize(Vu.sub(source.getDir(), source.pos));
			double[] target_heading = Vu.normalize(Vu.sub(target.getVel(), target.pos));
			double scalar = Vu.scalarProduct(source_heading, target_heading);
			double angle = Math.acos(scalar);
			if(angle < heading_threshold){
				return 1;
			}else if(angle < 0){
				return 0;
			}else{
				return 2;
			}
		}
		
		public String getString(){
			String s = "~";
			for (int i = 0; i < 3; i++) {
				if(i == 2){s += "Left: ";}
				if(i == 0){s += "Right: ";}
				if(i == 1){s += "Middle: ";}
				if(type[i] != null){
					s += type[i].toString() + " ";
					
					if(far_away[i]){
						s +=  " far. ";
					}else{
						s += " close. ";
					}
					
					
					switch (heading[i]) {
					case 1:
						s += "heading straight.   ";
						break;
					
					case 2:
						s +=  "heading right.  ";
						break;
						
					case 0:
						s +=  "heading left.   ";
						break;
					}
				}else{
					s += "Nothing.   ";
				}
			}
			s +="~";
			return s ;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			//result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(far_away);
			result = prime * result + Arrays.hashCode(heading);
			result = prime * result + Arrays.hashCode(type);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Fov other = (Fov) obj;
			//if (!getOuterType().equals(other.getOuterType()))
			//	return false;
			if (!Arrays.equals(far_away, other.far_away))
				return false;
			if (!Arrays.equals(heading, other.heading))
				return false;
			if (!Arrays.equals(type, other.type))
				return false;
			return true;
		}

		private Craft getOuterType() {
			return Craft.this;
		}

	}



}
