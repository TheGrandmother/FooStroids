package astroids.Algorithms;

import java.awt.Color;
import java.util.HashMap;

import astroids.Craft;
import astroids.Algorithms.Ai.Decision;
import astroids.Craft.Fov;

public class TrivialAi extends Ai {

	HashMap<Fov, Decision> decision_list = new HashMap<Fov, Decision>(500);
	
	public TrivialAi(Craft craft) {
		super(craft);
	}
	
	
	public void makeDecision(Craft craft, Fov fov){
		if(!decision_list.containsKey(fov)){
			decision_list.put(fov, new Decision());
		}

		Decision dec = decision_list.get(fov);
		if(dec.accelerate){craft.accelerate();}
		if(dec.deccelerate){craft.decelerate();}
		if(dec.turn_left){craft.rotateLeft();}
		if(dec.turn_right){craft.rotateRight();}
		if(dec.fire){craft.setFires(true);}	
	}


	@Override
	public void crossover(Craft mummy, double crossover_factor) {
		if(!(mummy.getAi() instanceof TrivialAi)){
			throw new RuntimeException("Cant crossover different Ai types");
		}
		for (Fov fov_mum : ((TrivialAi)(mummy.getAi())).decision_list.keySet()) {
			if(Math.random() >= crossover_factor){
				decision_list.put(fov_mum, ((TrivialAi)(mummy.getAi())).decision_list.get(fov_mum));
			}
		}

	}

	@Override
	public void mutate(Craft craft, double mutation_probability) {
		if(Math.random() <= mutation_probability){
			int s = (int)(Math.random()*decision_list.size());
			decision_list.put((Fov) decision_list.keySet().toArray()[s], new Decision());
			craft.setColor(new Color(255-craft.getColor().getRed(),255-craft.getColor().getGreen(),255-craft.getColor().getBlue()));
			
		}
	}
	
	@Override
	public String getName() {
		return "Trivial deterministic crap.";
	}
	
	@Override
	public String printDecision(Fov fov) {
		// TODO Auto-generated method stub
		return decision_list.get(fov).getString();
	}
		
	@Override
	public String status() {
		// TODO Auto-generated method stub
		return ""+decision_list.size();
	}

}
