package astroids;

import java.awt.Graphics2D;
import java.util.LinkedList;
/**
 * 
 * The space is the environment in which everything happens.
 * 
 * @author The Grandmother
 */

public class Space {
	enum Types {SHIP,ASTEROID,MISSILE,OTHER};
	double width;
	double height;
	public LinkedList<Objects> object_list;
	//long time = System.currentTimeMillis();
	long time = 0; // time in updates
	private long fire_delay = 5;
	
	public Space(double width, double height){
		this.width = width;
		this.height = height;
		object_list = new LinkedList<Objects>();
		this.time = 0;
	}
	
	public void addObject(Objects obj){
		object_list.add(obj);
	}
	
	/**
	 * This method updates the space. I.e it plays the game for one step. 
	 * Every time this method is called all of the objects in the space will be updated according to their own 
	 * update method. We will also check for wall collisions, weather or nor things are firing, colliding or dying. 
	 * @param s
	 */
	public void update(Space s){
		for (Objects obj : object_list) {
			obj.update(s);
		}
		time++;
		
		wallBounce();
		fire();
		collide();
		kill();
	}
	
	public void drawSpace(Graphics2D g){
		int score_pos = 10;
		for (Objects obj : object_list) {
			if (obj.getClass() == Craft.class) {
				g.setColor(obj.color);
				g.drawString(((Craft)obj).name+" : "+ ((Craft)obj).score + " : " + ((Craft)obj).age + " : "+ ((Craft)obj).getFitness()   , 5, score_pos);
				score_pos += 12;
			}
			obj.draw(g);
		}
	}
	
	public void print(){
		for (Objects obj : object_list) {
			obj.print();
		}
	}
	

	public void fire(){
		LinkedList<Objects> tmp = new LinkedList<Objects>();
		tmp.addAll(object_list);
		for (Objects obj : object_list) {
			if(obj.fires == true && obj.getClass() == Craft.class && (time-obj.time > fire_delay)){
				
				Missile m = new Missile(Vu.add(obj.getPos(),Vu.mul(20,obj.getDir())), Vu.mul(Missile.speed,obj.getDir()),(Craft)obj);
				m.color = obj.color;
				tmp.addFirst(m);
				obj.fires = false;
				obj.time = this.time;
				((Craft)obj).score -= Objects.fire_penalty;
			}
		}
		object_list = tmp;
	}
	
	public void kill(){
		LinkedList<Objects> tmp = new LinkedList<Objects>();
		int index = 0;
		for (Objects obj : object_list) {
			if(obj.kill_me == true && obj.hashCode() == object_list.get(index).hashCode()){
				
			}else{
				tmp.add(obj);
			}
			index++;
		}
		object_list = tmp;
	}
	
	public void collide(){
		for (Objects source : object_list) {
			if(source.getClass() != Missile.class){
				for (Objects target : object_list) {
					if(source != target){
					
					source.collide(target);
					}
				}
			}
		}
	}
	
	public void wallBounce(){
		double[] left_normal = {0,1};
		double[] right_normal = {0,1};
		double[] upper_normal = {1,0};
		double[] lower_normal = {1,0};
		double[] fail_normal = {-1,-1};
		
		for (Objects obj : object_list) {
			if(obj.getPos()[0]-obj.radius <= 0){
				obj.wallCollide(left_normal);
				obj.pos[0] = obj.radius;
			}else if((obj.getPos()[0]+obj.radius >= width )){
				obj.wallCollide(right_normal);
				obj.pos[0] = width - obj.radius;
			}else if((obj.getPos()[1]-obj.radius <= 0)){
				obj.wallCollide(upper_normal);
				obj.pos[1] = obj.radius;
			}else if((obj.getPos()[1]+obj.radius >= height)){
				obj.wallCollide(lower_normal);
				obj.pos[1] = width - obj.radius;
			}else if((obj.getPos()[1]+obj.radius == height) || (obj.getPos()[1]+obj.radius == width)){
				obj.wallCollide(fail_normal);
			}
		}
	}
	
	public int countCrafts(){
		int count = 0;
		for (Objects obj : object_list) {
			if(obj.getClass() == Craft.class && obj != null){
				count++;
			}
		}
		return count;
	}
	
	
	
}
