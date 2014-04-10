package astroids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;



@SuppressWarnings("serial")
public class Main extends JFrame {
	int lol = 0;
	BufferedImage background;
	BufferedImage foreground;
	Craft[] crafts;
	int warmup_time = 5;
	long battle_length = 1000;
	long refresh_rate = 40;
	
	
	int height;
	int width ;
	
	public static void main(String[] args){
		Main m = new Main(1000,1000);
		Comp c = new Comp(m.width, m.height);
		m.add(c);
		
		m.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		m.pack();
		 
		//c.setSize(m.width, m.height);
		//m.setSize(m.width, m.height);
		m.setVisible(true);
		c.setVisible(true);
		m.createCrafts(30);
		
		Space s = new Space(m.width, m.height);
		for(int i = 0; i < m.crafts.length;i++){
			s.addObject(m.crafts[i]);
		}
		m.warmUp();
		
		c.clearImage();
		while(true && s.countCrafts() > 1){
			m.tournament(c, m.crafts, 2, 5);
		}
		
	}
	
	public Main(int width,int height) {
		super();
		this.height = height;
		this.width = width;
		setUndecorated(true);
		setResizable(false);
		setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void createCrafts(int n){
		crafts = new Craft[n];
		for (int i = 0; i < n; i++) {
			crafts[i] = new Craft(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1), Vu.random(0, 0, 0, 0));
			crafts[i].color = new Color( (int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
			
			
		}
	}
	
	public void resetCrafts(Craft[] crafts){
		for(int i = 0; i < crafts.length; i++){
			//crafts[i] =new Craft(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1), Vu.random(0, 0, 0, 0));
			crafts[i].kill_me=false;
			crafts[i].setPos(Vu.random(0, width, 0, height));
			crafts[i].setDir(Vu.random(-1, 1, -1, 1));
			crafts[i].time = 0;
		}
	}
	
	public void resetScores(Craft[] crafts){
		for(int i = 0; i < crafts.length; i++){
			//crafts[i] =new Craft(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1), Vu.random(0, 0, 0, 0));
			crafts[i].score=0;
		}
	}
	
	public void warmUp(){
		
		for (int i = 0; i < warmup_time; i++) {
			Space s  = new Space(width, height);
			populateSpace(s,this.crafts);
			while(s.time < 1000 && s.countCrafts() > 1){
				s.update(s);
			}
			resetCrafts(this.crafts);
			
		}
		resetScores(crafts);
		
	}
	
	public void populateSpace(Space s,Craft[] crafts){
		for(int i = 0; i < crafts.length;i++){
			s.addObject(crafts[i]);
		}
	}

	/**
	 * @param c
	 * @param contestants
	 * @return <b>null</b> if battle was inconclusive (no kills or tie)
	 */
	public Craft battle(Comp c, Craft[] contestants, int rounds){
		Space s = new Space(width, height);
		for (int i = 0; i < rounds; i++) {
			resetCrafts(contestants);
			populateSpace(s, contestants);
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
					c.drawTheSpace(s);
					c.printFps(System.currentTimeMillis()-time);
					c.repaint();
				}
			}
		}
		
		int max = Integer.MIN_VALUE;
		Craft best = null;
		for (int i = 0; i < contestants.length; i++) {
			if(contestants[i].score >= max){
				best = contestants[i];
				max = contestants[i].score;
			}
		}
		
		//if(max == 0){return null;}
		
//		for (int i = 0; i < contestants.length; i++) {
//			if(contestants[i] != best && contestants[i].score == max){
//				return null;
//			}
//		}
		
		return best;
	}
	
	
	public void tournament(Comp c,Craft[] crafts,int rounds, int players){
		//Lets start of by only doing three tournaments.
		//Lets just draw the first.
		Craft[] team = randomPicks(crafts, players);
		resetCrafts(team);
		Craft team1_champion = battle(c, team,rounds);
		
		team = randomPicks(crafts, players);
		resetCrafts(team);
		Craft team2_champion = battle(null, team,rounds);
		

		team = randomPicks(crafts, players);
		resetCrafts(team);
		Craft team3_champion = battle(null, team,rounds);
		
		Craft daddy = null;
		Craft mummy = null;
		Craft sacrifice = null;
		
		if(team1_champion.score >= team2_champion.score && team1_champion.score >= team3_champion.score){
			daddy = team1_champion;
		}else if(team2_champion.score >= team1_champion.score && team2_champion.score >= team3_champion.score){
			daddy = team2_champion;
		}else{
			daddy = team3_champion;
		}
		
		if(team1_champion.score <= team2_champion.score && team1_champion.score <= team3_champion.score){
			sacrifice = team1_champion;
		}else if(team2_champion.score <= team1_champion.score && team2_champion.score <= team3_champion.score){
			sacrifice = team2_champion;
		}else{
			sacrifice = team3_champion;
		}
		
		if(team1_champion.score >= team2_champion.score && team1_champion.score <= team3_champion.score ||
				team1_champion.score <= team2_champion.score && team1_champion.score >= team3_champion.score){
			mummy = team1_champion;
		}else if(team2_champion.score >= team1_champion.score && team2_champion.score <= team3_champion.score||
				team2_champion.score <= team1_champion.score && team2_champion.score >= team3_champion.score){
			mummy = team2_champion;
		}else{
			mummy = team3_champion;
		}
		
		System.out.println("Daddy has : " + daddy.score);
		System.out.println("Mummy has : " + mummy.score);
		System.out.println("Sacrifice has : " + sacrifice.score);
		
		sacrifice = daddy.clone();
		sacrifice.mate(mummy);
		sacrifice.generation++;
		
		resetScores(crafts);

	}
	
	/**
	 * Returns a array of unique individuals from a list of crafts
	 * 
	 * @return Returns {@code null} if picks < length of crafts.
	 */
	public Craft[] randomPicks(Craft[] crafts, int picks){
		Craft[] tmp1 = crafts;
		Craft[] tmp2 = null;
		Craft[] r = new Craft[picks];
		int rnd = 0;
		int jj = 0;
		for (int i = 0; i < picks; i++) {
			rnd = (int)(Math.random()*tmp1.length);
			r[i] = tmp1[rnd];

			tmp2 = new Craft[tmp1.length-1];
			jj = 0;
			for (int j = 0; j < tmp2.length; j++) {
				if(jj != rnd){
					tmp2[j] = tmp1[jj];
					jj++;
				}else{
					tmp2[j] = tmp1[jj+1];
					jj += 2;
				}
			}
			tmp1 = tmp2;
		}
		return r;

	}

}

@SuppressWarnings("serial")
class Comp extends JComponent{
	int width;
	int height;
	BufferedImage i1;
	BufferedImage i2;
	int swap;
	
	
	public Comp(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width,height));
		i1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		i2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = this.i1.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0, width, height);
		g = this.i2.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0, width, height);
		swap = 0;
	}
	
	public void paintComponent(Graphics g1){
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		if(swap == 0){
			g.drawImage(i2, null, 0, 0);
		}else {
			g.drawImage(i1, null, 0, 0);
		}
	}
	
	public void clearImage(){
		Graphics2D g;
		if(swap==0){
			g = this.i1.createGraphics();
		}else {
			g = this.i2.createGraphics();
		}
		g.setColor(Color.black);
		g.fillRect(0,0, width, height);
	}
	
	public void drawTheSpace(Space s){
		Graphics2D g;
		if(swap==0){
			g = this.i1.createGraphics();
		}else {
			g = this.i2.createGraphics();
		}
		
		s.drawSpace(g);
		
	}
	
	public void printFps(long time){
		Graphics2D g;
		if(swap==0){
			g = this.i1.createGraphics();
		}else {
			g = this.i2.createGraphics();
		}
		g.setColor(Color.green);
		g.drawString((int)(1./(time*0.001))+"", 0, height-10);
		
	}
	
	public void flip(){
		if (swap == 0) {
			swap =1;
		}else{
			swap= 0;
		}
	}
	
	

}



