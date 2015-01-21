package astroids;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DebugGraphics;
import javax.swing.JFrame;

import astroids.Algorithms.TrivialAi;

/**
 * This is the Main class for the FooStroids thingamagoop.
 * It is as of now fairly basic.
 * 
 * @author The Grandmother
 */

@SuppressWarnings("serial")
public class Main extends JFrame implements KeyListener{

	Craft[] crafts;
	Asteroid[] asteroids;
	final int warmup_rounds = 10;
	final int warmup_length = 400;
	final long battle_length = 450;	//How many steps a battle will last
	final long refresh_rate = 40;
	final long refresh_limit = 0;
	File stats_file = new File("stats.txt");
	BufferedWriter stats_out = null;
	File color_file = new File("color.txt");
	BufferedWriter color_out = null;
	//boolean draw_all = true;
	boolean debug_mode = false;
	boolean turbo_mode = false;
	boolean closed = false;
	
	int height;
	int width ;
	int number_of_crafts;
	int number_of_asteroids;
	int daddy_dbg = 0;
	int mummy_dbg = 0;
	int sacrifice_debug = 0;
	int tournaments = 0;
	int draws = 0;
	
	
	public static void main(String[] args){

		Main m = new Main(500,500,75,0);
		Comp c = new Comp(m.width, m.height);
		//m.createFiles();
		
		m.add(c);
		m.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		m.pack();
		m.setVisible(true);
		m.setFocusable(true);
		m.addKeyListener(m);
		c.setVisible(true);
		
		
		m.createCrafts(m.number_of_crafts);
		m.createAsteroids(m.number_of_asteroids);
		System.out.println("Warming up");
		m.warmUp();
		System.out.println("Warmed up");
		
		c.clearImage();
		while(true){

			if(!m.turbo_mode){
				m.tournament(c, m.crafts, 3, 5);
			}else{
				m.tournament(null, m.crafts, 3, 5);
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
			crafts[i].setAi(new TrivialAi(crafts[i]));
			crafts[i].setColor(new Color( (int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
			
			
		}
	}
	
	private void createAsteroids(int n){
		asteroids = new Asteroid[n];
		for (int i = 0; i < asteroids.length; i++) {
			asteroids[i] = new Asteroid(Vu.random(0, width, 0, height), Vu.random(-6, 6, -6, 6));
		}
	}
	
	private void resetAsteorids(){
		for (int i = 0; i < asteroids.length; i++) {
			asteroids[i] = new Asteroid(Vu.random(0, width, 0, height), Vu.random(-6, 6, -6,6));
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
			while(s.time < battle_length && ( (s.countCrafts() > 1 && number_of_asteroids ==0) || ( (s.countCrafts() >= 1 && number_of_asteroids != 0))  )){
				
				if(c != null){
					time = System.currentTimeMillis();
					c.flip();
					//c.clearImage();
				}
				s.update(s);
				if(c != null){
					while(System.currentTimeMillis()-time < refresh_rate){}
					//c.drawText(closed,turbo_mode);
					c.drawTheSpace(s);
					if(debug_mode){
						c.drawDBG(s, contestants,System.currentTimeMillis()-time, daddy_dbg, mummy_dbg, sacrifice_debug
								,(int)battle_length,(int)s.time,tournaments,draws,this);
					}
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
		int max_fitness = Integer.MIN_VALUE;
		for (int i = 0; i < indexes.length; i++) {
			if(crafts[indexes[i]].getFitness() >= max_fitness){
				daddy = crafts[indexes[i]];
				max_fitness = crafts[indexes[i]].getFitness();
			}
		}
		
		Craft mummy = null;
		int second_fitness = Integer.MIN_VALUE;
		for (int i = 0; i < indexes.length; i++) {
			if(crafts[indexes[i]].getFitness() >= second_fitness && crafts[indexes[i]].getFitness() < max_fitness){
				mummy = crafts[indexes[i]];
				second_fitness = crafts[indexes[i]].getFitness();
			}
		}
		

		Craft sacrifice = null;
		int sacrifice_index = -1;
		int min_fitness = Integer.MAX_VALUE;
		for (int i = 0; i < indexes.length; i++) {
			if(crafts[indexes[i]].getFitness() <= min_fitness && crafts[indexes[i]].getFitness() < second_fitness&& crafts[indexes[i]].getFitness() < max_fitness){
				sacrifice = crafts[indexes[i]];
				min_fitness = crafts[indexes[i]].getFitness();
				sacrifice_index = indexes[i];
				
			}
		}
		
		if(sacrifice == null || daddy == null || mummy ==null){
			//System.out.println("It was a tie :(");
			draws++;
		}else{

			sacrifice_debug = crafts[sacrifice_index].getFitness();
			crafts[sacrifice_index] = daddy.clone();
			crafts[sacrifice_index].mate(mummy);
			crafts[sacrifice_index].generation++;
			daddy_dbg = daddy.getFitness();
			mummy_dbg = mummy.getFitness();
			
		}
		
		//Changed this to test new fitness function.
		for (int i = 0; i < indexes.length; i++) {
			crafts[indexes[i]].age++;
		}
		tournaments++;
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
			s += "{"+crafts[i].getColor().getRed()+","+crafts[i].getColor().getGreen() +","+ crafts[i].getColor().getBlue()+"},";
		}
		s += "{"+crafts[crafts.length-1].getColor().getRed()+","+crafts[crafts.length-1].getColor().getGreen() +","+ crafts[crafts.length-1].getColor().getBlue()+"}}";
		return s;
	}

	
	@Override
	public void keyTyped(KeyEvent e) {
	
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_D:
			if(debug_mode){
				debug_mode = false;
			}else{
				debug_mode = true;
			}
			break;
		
		case KeyEvent.VK_SPACE:
			if(closed){
				closed = false;
			}else{
				closed = true;
			}
			break;
		
		case KeyEvent.VK_T:
			if(turbo_mode){
				turbo_mode = false;
			}else{
				turbo_mode = true;
				System.out.println("Turbo_mode == true");
			}
			break;
			
		default:
			break;
		}
	}
	

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
}




