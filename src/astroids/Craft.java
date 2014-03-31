package astroids;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Craft extends Objects{

	Fov fov;
	public enum Action{ROTATE_RIGHT,ROTATE_LEFT,ACCELERATE,FIRE,NOTHING}
	private double thrust_power = 0.1; 
	private double rotation_constant = Math.PI/40;
	private double fov_angle = Math.PI/12;
	private double max_speed = 2;
	
	private double[] left_end_vector;
	private double[] left_vector;
	private double[] right_end_vector;
	private double[] right_vector;
	private double mutation_probability = 0.1;
	public int score;
	public String name;
	public int generation;
	
	HashMap<Fov, Decision> decision_list = new HashMap<Fov, Decision>(200);

	public Craft(double[] pos, double[] dir, double[] vel){
		this.pos = pos.clone();
		this.dir = Vu.normalize(dir.clone());
		this.vel = vel.clone();
		super.priority = 3;
		super.type = Space.Types.SHIP;
		super.color = Color.CYAN;
		super.radius = 4;
		super.kill_me = false;
		super.fires = false;
		left_end_vector = Vu.rotate(dir,fov_angle*2);
		left_vector = Vu.rotate(dir,fov_angle/2);
		right_end_vector = Vu.rotate(dir,-fov_angle*2);
		right_vector = Vu.rotate(dir,-fov_angle/2);
		generation = 0;
		score = 0;
		generateName();
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
		//tmpTakeAction();
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
		make_decision(fov);
		//System.out.println(fires);
	}
	
	private void make_decision(Fov fov){
		if(!decision_list.containsKey(fov)){
			decision_list.put(fov, new Decision());
		}else{
			Decision dec = decision_list.get(fov);
			if(dec.accelerate){this.accelerate();}
			if(dec.deccelerate){this.decelerate();}
			if(dec.turn_left){this.rotateLeft();}
			if(dec.turn_right){this.rotateRight();}
			if(dec.fire){this.fires = true;}
		}
		
	}
	
	public void collide(Objects obj) {
		if(Vu.eclidianDistance(this.getPos(), obj.getPos()) <= (this.radius+obj.radius) && this.hashCode() != obj.hashCode()){
			
			//System.out.println(this.getClass().getCanonicalName() +" Collided with "+ obj.getClass().getCanonicalName());
			switch (obj.type) {
			case MISSILE:
				
				if(this.name != ((Missile)obj).sender.name  ){
					this.kill_me = true;
					//obj.kill_me = true;
					((Missile)obj).sender.score += Objects.craft_score;
				}
				break;
			
			case ASTEROID:
				
				this.kill_me = true;
				break;
				
			case SHIP:
				
				this.kill_me = true;
				//obj.kill_me = true;
				
				break;
				
			default:
				break;
			}
		}
	}
	
	public void wallCollide(double[] normal) {
		bounce(normal);
	}
	
	private void populateFov(Space s){
		fov = new Fov();
		left_end_vector = Vu.normalize(Vu.rotate(dir,fov_angle*2));
		left_vector = Vu.normalize(Vu.rotate(dir,fov_angle/2));
		right_end_vector = Vu.normalize(Vu.rotate(dir,-fov_angle*2));
		right_vector = Vu.normalize(Vu.rotate(dir,-fov_angle/2));
		LinkedList<Objects> left_field = new LinkedList<Objects>();
		LinkedList<Objects> right_field = new LinkedList<Objects>();
		LinkedList<Objects> middle_field = new LinkedList<Objects>();
		boolean fl; //is true if obj is to the right of far left
		boolean l;
		boolean r;
		boolean fr;
		
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
		double min_distance = Double.MAX_VALUE; 
		for (Objects obj : left_field) {
			if(obj.priority >= max_priority){
				if(Vu.eclidianDistance(obj.getPos(), pos) < min_distance && Vu.eclidianDistance(obj.getPos(), pos) > .1){
					fov.setLeft(this, obj);
				}
			}
		}
		
		max_priority = 1;
		min_distance = Double.MAX_VALUE; 
		for (Objects obj : middle_field) {
			if(obj.priority >= max_priority){
				if(Vu.eclidianDistance(obj.getPos(), pos) < min_distance && Vu.eclidianDistance(obj.getPos(), pos) > .1){
					fov.setMiddle(this, obj);
				}
			}
		}
		
		max_priority = 1;
		min_distance = Double.MAX_VALUE; 
		for (Objects obj : right_field) {
			if(obj.priority >= max_priority){
				if(Vu.eclidianDistance(obj.getPos(), pos) < min_distance && Vu.eclidianDistance(obj.getPos(), pos) > .1){
					fov.setLeft(this, obj);
				}
			}
		}
	}
	
	boolean toTheRight(double[] p, double[] v){
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
		
		g.setColor(color);
		g.setStroke(new BasicStroke(3));
		g.drawLine((int)pos[0], (int)pos[1],(int)(15*dir[0]+pos[0]) ,(int)(15*dir[1]+pos[1]));
		g.setColor(color);
		g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine((int)pos[0], (int)pos[1],(int)(1*dir[0]+pos[0]) ,(int)(1*dir[1]+pos[1]));

		// UNCOMMENT THESE LINES TO SE THE FOV
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
		
	}
		
	private void generateName(){
		String[] first = {"Millenium","Steel","Iron","Space","Light","Star","Stellar","Lazer","Galactic","Icarus","Orion","Sky"};
		String[] second = {"Falcon", "Eagle", "Hawk","Fighter","Warrior","Lightning","Killer","Wing"};
		this.name = first[(int)(Math.random()*first.length)] + " "+ second[(int)(Math.random()*second.length)];
	}
	
	void print(){
		fov.print();
		System.out.println(this.toString()+"");
		System.out.println("Pos: ("+pos[0]+","+pos[1]+")  " + "Vel: ("+vel[0]+","+vel[1]+")");
		System.out.println("Dir: ("+dir[0]+","+dir[1]+")");

	}

	public double[] getDir() {
		return dir;
	}
	
	public void setDir(double[] dir) {
		this.dir = Vu.normalize(dir.clone());
		
	}
	
	public Craft clone(){
		Craft clone = new Craft(this.pos, this.dir, this.vel);
		clone.fov = this.fov;
		clone.decision_list = this.decision_list;
		clone.color = this.color;
		clone.score = 0;
		
		return clone;
	}
	
	public void mate(Craft daddy){
		for (Fov fov : daddy.decision_list.keySet()) {
			if(Math.random() >= 0.5){
				this.decision_list.put(fov, daddy.decision_list.get(fov));
			}
		}
		this.color = new Color(this.color.getRed(), (this.color.getGreen()/2)+(daddy.color.getGreen()/2), daddy.color.getBlue());
	
		if(Math.random() <= mutation_probability){
			int s = (int)(Math.random()*decision_list.size());
			decision_list.put((Fov) decision_list.keySet().toArray()[s], new Decision());
			this.color = new Color(255-this.color.getRed(),255-this.color.getGreen(),255-this.color.getBlue());
		}
	}
	
	public class Fov{
		
		//Why can't I put enums in local classes????
		private int[] heading; 	//{left,middle,right} -1 is heading left, 0 is heading straight, 1 is heading right
		private Space.Types[] type;	//{left,middle,right}
		private double heading_threshold =(1/Math.sqrt(200));
		
		public Fov(){
			this.heading = new int[3];
			this.type = new Space.Types[3];
		}
		
		public void setLeft(Craft source, Objects target){
			heading[0] = getHeading(source.getDir(), target.getVel());
			type[0] = target.type;
		}
		public void setMiddle(Craft source, Objects target){
			heading[1] = getHeading(source.getDir(), target.getVel());
			type[1] = target.type;
		}
		public void setRight(Craft source, Objects target){
			heading[2] = getHeading(source.getDir(), target.getVel());
			type[2] = target.type;
		}
		
		private int getHeading(double[] source, double[] target){
			double[] s1 = new double[3];
			s1[0] = source[0];
			s1[1] = source[1];
			s1[2] = 0;
			
			double[] t1 = new double[3];
			t1[0] = target[0];
			t1[1] = target[1];
			t1[2] = 0;
			
			double res = Vu.crossProduct(t1, s1)[2];
			if(res < -heading_threshold){
				return -1;
			}else if(res > heading_threshold){
				return 1;
			}else{
				return 0;
			}
		}
		
		public void print(){
			for (int i = 0; i < 3; i++) {
				if(i == 0){System.out.print("Left ");}
				if(i == 1){System.out.print("Right ");}
				if(i == 2){System.out.print("Middle ");}
				if(type[i] != null){
					System.out.print(type[i].toString() + " ");
					switch (heading[i]) {
					case 0:
						System.out.print("heading straight\n");
						break;
					
					case 1:
						System.out.print("heading right\n");
						break;
						
					case -1:
						System.out.print("heading left\n");
						break;
					}
				}else{
					System.out.println("Empty.");
				}
			}
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
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
			if (!getOuterType().equals(other.getOuterType()))
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
	
	public class Decision{
		boolean turn_left = false;
		boolean turn_right= false;
		boolean accelerate= false;
		boolean deccelerate= false;
		boolean fire= false;
		
		public Decision(){
			random();
		}
		
		public void random(){
			if(Math.random() <= 0.5){
				if(Math.random() <= 0.5){
					this.turn_left = true;
				}else{
					this.turn_right = true;
				}
			}
			this.accelerate = (Math.random() >= 0.5);
			this.deccelerate = (Math.random() >= 0.5);
			this.fire = (Math.random() >= 0.5);
		}
		
	}

}
