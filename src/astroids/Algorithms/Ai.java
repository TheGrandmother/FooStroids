package astroids.Algorithms;

import java.awt.Color;

import astroids.Craft;
import astroids.Craft.Fov;

public abstract class Ai {
	
	private final Craft craft; 
	
	Ai(Craft craft){
		this.craft = craft;
	}
	
	public abstract void makeDecision(Craft craft, Fov fov);
	
	//public abstract void mate(Craft daddy, Craft mummy);
	
	public abstract void crossover(Craft mummy, double crossover_factor);
	
	public abstract void mutate(Craft craft, double mutation_probability);
	
	public abstract String getName();
	
	public abstract String status();
	
	public abstract String printDecision(Fov fov);
	
	
class Decision{
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
			if(Math.random() <= 0.5){
				if(Math.random() <= 0.5){
					this.accelerate = true;
				}else{
					this.deccelerate = true;
				}
			}
			this.fire = (Math.random() >= 0.5);
		}
		
		public String getString(){
			String s = "";
			if(!turn_left && !turn_right && !accelerate && !fire && !deccelerate){
				s = "Doing Nothing.";
				return s;
			}
			
			if (fire) {
				s += "Fires. ";
			}

			if (accelerate) {
				s += "Accelerates. ";
			}
			
			if (deccelerate) {
				s += "Deccelerates. ";
			}
			
			if (turn_left) {
				s += "Turns left. ";
			}
			
			if (turn_right) {
				s += "Turns Right.";
			}
			
			
			
			return s;
		}
		
	}
	
	
}
