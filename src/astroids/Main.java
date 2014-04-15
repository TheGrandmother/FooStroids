package astroids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.text.MaskFormatter;

/**
 * This is the Main class for the FooStroids thingamagoop.
 * It is as of now fairly basic.
 * 
 * @author The Grandmother
 */

@SuppressWarnings("serial")
public class Main extends JFrame {

	Craft[] crafts;
	Asteroid[] asteroids;
	final int warmup_rounds = 5;
	final int warmup_length = 400;
	final long battle_length = 350;	//How many steps a battle will last
	final long refresh_rate = 40;
	File stats_file = new File("stats.txt");
	BufferedWriter stats_out = null;
	File color_file = new File("color.txt");
	BufferedWriter color_out = null;
	
	int height;
	int width ;
	int number_of_crafts;
	int number_of_asteroids;
	
	public static void main(String[] args){

		Main m = new Main(1000,1000,50,0);
		Comp c = new Comp(m.width, m.height);
		//m.createFiles();
		
		m.add(c);
		m.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		m.pack();
		m.setVisible(true);
		c.setVisible(true);
		
		
		m.createCrafts(m.number_of_crafts);
		m.createAsteroids(m.number_of_asteroids);
		m.warmUp();
		
		c.clearImage();
		int age = 0;
		while(true){
			
			m.tournament(null, m.crafts, 2, 5);
			age++;
//			if(age % 100 == 0){
//				writer(generateStats(m.crafts), m.stats_file, m.stats_out);
//				writer(generateColorStats(m.crafts), m.color_file, m.color_out);
//			}
			
			//We only  draw every tenth tournament.
			if(age % 10 ==0){
				m.tournament(c, m.crafts, 2, 10);
			}
		}
		
	}
	
	public Main(int width,int height,int crafts, int asteroids) {
		super();
		this.height = height;
		this.width = width;
		this.number_of_asteroids = asteroids;
		this.number_of_crafts = crafts;
		setUndecorated(true);
		setResizable(false);
		setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void createCrafts(int n){
		crafts = new Craft[n];
		for (int i = 0; i < n; i++) {
			crafts[i] = new Craft(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1), Vu.random(0, 0, 0, 0));
			crafts[i].color = new Color( (int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
			
			
		}
	}
	
	private void createAsteroids(int n){
		asteroids = new Asteroid[n];
		for (int i = 0; i < asteroids.length; i++) {
			asteroids[i] = new Asteroid(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1));
		}
	}
	
	private void resetAsteorids(){
		for (int i = 0; i < asteroids.length; i++) {
			asteroids[i] = new Asteroid(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1));
		}
	}
	
	
	private void resetCrafts(Craft[] crafts){
		for(int i = 0; i < crafts.length; i++){
			//crafts[i] =new Craft(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1), Vu.random(0, 0, 0, 0));
			crafts[i].kill_me=false;
			crafts[i].setPos(Vu.random(0, width, 0, height));
			crafts[i].setDir(Vu.random(-1, 1, -1, 1));
			crafts[i].time = 0;
		}
	}
	
	private void resetScores(Craft[] crafts){
		for (int i = 0; i < crafts.length; i++) {
			crafts[i].score = 0;
		}
	}
	
	/**
	 * Plays a couple of rounds just to populate the crafts descion lists
	 */
	private void warmUp(){
		
		for (int i = 0; i < warmup_rounds; i++) {
			Space s  = new Space(width, height);
			populateSpace(s,crafts);
			populateSpace(s, asteroids);
			while(s.time < 1000 && s.countCrafts() > 1){
				s.update(s);
			}
			resetCrafts(this.crafts);
			resetAsteorids();
			
		}
		resetCrafts(crafts);
		resetScores(crafts);
		for (int i = 0; i < crafts.length; i++) {
			crafts[i].eternal_score = 0;
		}
		
	}
	
	private void populateSpace(Space s,Objects[] crafts){
		for(int i = 0; i < crafts.length;i++){
			s.addObject(crafts[i]);
		}
	}

	/**
	 * This is the method in which the action happens.
	 * When this method is called a battle more epic than 
	 * anything the world has ever seen will take place.
	 * 
	 * @param c this is the <b>Comp</b> class upon which the battle is to be drawn. 
	 * If <b>c</b> = <b>null</b> then the battle will not be displayed.
	 * @param contestants
	 * @param rounds how many rounds of battle will take place.
	 */
	private void battle(Comp c, Craft[] contestants, int rounds){
		
		
		for (int i = 0; i < rounds; i++) {
			resetCrafts(contestants);
			resetAsteorids();
			
			Space s = new Space(width, height);
			populateSpace(s, contestants);
			populateSpace(s, asteroids);
			long time = 0;
			while(s.time < battle_length && s.countCrafts() > 1){
				
				if(c != null){
					time = System.currentTimeMillis();
					c.flip();
					c.clearImage();
				}
				s.update(s);
				
				if(c != null){
					while(System.currentTimeMillis()-time < refresh_rate){}
					c.drawText();
					c.drawTheSpace(s);
					
					c.printFps(System.currentTimeMillis()-time);
					c.repaint();
				}
			}
		}

	}
	
	/**
	 * When this method is called a great tournament will be started.
	 * Actually the tournament is not that great since there will only be one round
	 * played. Three battlers will take place and in each a number of crafts will be
	 * sent in such a way that no craft partakes in more than one battle.
	 * <p>
	 * After the fog of war has cleared and the battles are over the two best crafts 
	 * will be selected and they will have awkward sex and make a baby. The crappiest 
	 * craft in the tournament will be sacrificed and the baby will take its place.
	 * @param c
	 * @param crafts
	 * @param rounds
	 * @param players
	 */
	private void tournament(Comp c,Craft[] crafts,int rounds, int players){

		try {
			assertUnique(crafts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resetCrafts(crafts);
		resetScores(crafts);
		
		int[] indexes = randomPermutation(crafts.length, players*3);
		Craft[] team1 = new Craft[players];
		Craft[] team2 = new Craft[players];
		Craft[] team3 = new Craft[players];
		
		int ii = 0;
		for (int i = 0; i < team1.length; i++) {
			team1[i] = crafts[indexes[ii]];
			ii++;
		}
		battle(c, team1,rounds);

		for (int i = 0; i < team2.length; i++) {
			team2[i] = crafts[indexes[ii]];
			ii++;
		}
		battle(null, team2,rounds);
		
		for (int i = 0; i < team3.length; i++) {
			team3[i] = crafts[indexes[ii]];
			ii++;
		}
		battle(null, team3,rounds);

		Craft daddy = null;
		int max_score = Integer.MIN_VALUE;
		for (int i = 0; i < crafts.length; i++) {
			if(crafts[i].score >= max_score){
				daddy = crafts[i];
				max_score = crafts[i].score;
			}
		}
		
		Craft mummy = null;
		int second_score = Integer.MIN_VALUE;
		for (int i = 0; i < crafts.length; i++) {
			if(crafts[i].score >= second_score && crafts[i].score < max_score){
				mummy = crafts[i];
				second_score = crafts[i].score;
			}
		}
		
		Craft sacrifice = null;
		int min_score = Integer.MAX_VALUE;
		int sacrifice_index =0;
		int min_fitness = Integer.MAX_VALUE;
		for (int i = 0; i < crafts.length; i++) {
			if(crafts[i].score <= min_score && crafts[i].score < max_score && crafts[i].score < second_score && crafts[i].getFitness() <=min_fitness ){
				sacrifice = crafts[i];
				min_score = crafts[i].score;
				sacrifice_index = i;
				min_fitness = crafts[i].getFitness();
			}
		}
		
		if(sacrifice == null || daddy == null || sacrifice ==null){
			System.out.println("It was a tie :(");
		}else{
			//System.out.println("Daddy has : " + daddy.score);
			//System.out.println("Mummy has : " + mummy.score);
			//System.out.println("Sacrifice has : " + sacrifice.score);
			
			crafts[sacrifice_index] = daddy.clone();
			crafts[sacrifice_index].mate(mummy);
			crafts[sacrifice_index].generation++;
		}
		
		for (int i = 0; i < crafts.length; i++) {
			crafts[i].age++;
		}
	}
	
	private static int[] randomPermutation(int size, int picks){
		if(picks > size){return null;}
		int[] temp1 = new int[size];
		int[] temp2;
		int[] ret = new int[picks];
		
		for (int i = 0; i < temp1.length; i++) {
			temp1[i] = i;
		}
		
		int random = 0;
		int jj;
		for (int i = 0; i < ret.length; i++) {
			random = (int)(Math.random()*temp1.length);
			ret[i] = temp1[random];
			temp2 = new int[temp1.length-1];
			
			jj = 0;
			for (int j = 0; j < temp2.length; j++) {
				if (random != j) {
					temp2[j] = temp1[jj];
					jj++;
				}else{
					temp2[j] = temp1[jj+1];
					jj += 2;
				}
			}
			
			temp1 = temp2;
		}
		return ret;
	}
	
	private static void assertUnique(Craft[] crafts) throws Exception{
		for (int i = 0; i < crafts.length; i++) {
			for (int j = 0; j < crafts.length; j++) {
				if (crafts[i] == crafts[j] && i != j) {
					throw new Exception("Duplicate crap :/");
				}
			}
		}
	}

	private void createFiles(){
		try {
			if(stats_file.exists()){
				stats_file.delete();
				stats_file.createNewFile();
				stats_out = new BufferedWriter(new FileWriter(stats_file,true));
			}else{
				stats_file.createNewFile();
				stats_out = new BufferedWriter(new FileWriter(stats_file,true));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		

		try {
			if(color_file.exists()){
				color_file.delete();
				color_file.createNewFile();
				color_out = new BufferedWriter(new FileWriter(color_file,true));
			}else{
				color_file.createNewFile();
				color_out = new BufferedWriter(new FileWriter(color_file,true));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writer(String message,File file,BufferedWriter out){
	    try {
	        if (out == null) {
	            FileWriter datawriter = new FileWriter(file,true);
	            out = new BufferedWriter(datawriter);
	        }
	        if (file.exists()) {
	            out.append(message);
	            out.flush();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private static String generateStats(Craft[] crafts){
		String s = "{";
		for (int i = 0; i < crafts.length-1; i++) {
			s += crafts[i].getFitness()+",";
		}
		s += crafts[crafts.length-1].getFitness()+"},";
		return s;
	}
	
	private static String generateColorStats(Craft[] crafts){
		String s = "{";
		for (int i = 0; i < crafts.length-1; i++) {
			s += "{"+crafts[i].color.getRed()+","+crafts[i].color.getGreen() +","+ crafts[i].color.getBlue()+"},";
		}
		s += "{"+crafts[crafts.length-1].color.getRed()+","+crafts[crafts.length-1].color.getGreen() +","+ crafts[crafts.length-1].color.getBlue()+"}}";
		return s;
	}
	
}




