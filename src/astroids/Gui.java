package astroids;

/*
 * This is pretty much just a sandbox for debugging and such.
 * A proper class to handle the IO should be created later on.
 * 
 * */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Gui extends JFrame{
	public Gui(){
		super();
	  
        setUndecorated(true);
        setVisible(true);

	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Gui g = new Gui();
		int width = 1080;
		int height= 1080;
		Comp1 c = new Comp1(width,height);
		
		g.add(c);

		g.setLayout(new FlowLayout(0, 0, 0));
		g.pack();		

		c.setVisible(true);
		g.setVisible(true);
		c.clear(c.image.createGraphics());
		
		Space s = new Space(width, height);
		
		int number_of_crafts = 25;
		int number_of_asteroids = 0;
		long game_time = 20000;
		long update_time = 1;
		
		Craft[] crafts = new Craft[number_of_crafts];
		for(int i = 0; i < number_of_crafts; i++){
			crafts[i] =new Craft(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1), Vu.random(0, 0, 0, 0));
			crafts[i].color = new Color((int)(Math.random()*Integer.MAX_VALUE));
			s.addObject(crafts[i]);
		}
		for(int i = 0; i < number_of_asteroids; i++){
			s.addObject(new Asteroid(Vu.random(0, width, 0, height), Vu.random(-3, 3, -3, 3)));
		}
		

		
		
		//s.print();
		s.drawSpace(c.image.createGraphics());
		//s.print();
		s.update(s);
		s.update(s);
		//s.print();
		
		long time;
		long super_time = System.currentTimeMillis();
		int step = 0;
		int generation = 0;
		while(true){
			
			s = new Space(width, height);
			for(int i = 0; i < number_of_crafts; i++){
				//crafts[i] =new Craft(Vu.random(0, width, 0, height), Vu.random(-1, 1, -1, 1), Vu.random(0, 0, 0, 0));
				crafts[i].kill_me=false;
				crafts[i].setPos(Vu.random(0, width, 0, height));
				crafts[i].setDir(Vu.random(-1, 1, -1, 1));
				
				s.addObject(crafts[i]);
			}
			
			for(int i = 0; i < number_of_asteroids; i++){
				s.addObject(new Asteroid(Vu.random(0, width, 0, height), Vu.random(-2, 2, -2, 2)));
			}
			
			super_time = System.currentTimeMillis(); 
			for (Objects obj : s.object_list) {
				if (obj.getClass() == Craft.class) {
	
					System.out.println(((Craft)obj).name+" : "+ ((Craft)obj).score);
				}
			}
			System.out.println("\n");
			
			for(int i = 0; i < Integer.MAX_VALUE; i++){
				
				s.update(s);
				//s.print();
				s.drawSpace(c.image.createGraphics());
	
				time = System.currentTimeMillis();
				c.repaint();
				c.paint(c.image.createGraphics());
				while(System.currentTimeMillis()- time < update_time){}
	
				c.clear(c.image.createGraphics());
				if(System.currentTimeMillis()-super_time > game_time || s.object_list.size() <= 1){
					break;
				}
			}
			step++;
			if(step % 5 == 0){
				generation++;
				System.out.println("Having sex!");
				
				Craft best = crafts[0];
				for (Craft craft : crafts) {
					if(craft.score >= best.score){
						best = craft;
					}
				}
				
				Craft second =  crafts[0];
				for (Craft craft : crafts) {
					if(craft.score >= second.score && craft != best){
						second = craft;
					}
				}
				
				int worst = 0;
				for (int i = 0; i < crafts.length; i++) {
					if(crafts[i].score < crafts[worst].score){
						worst = i;
					}
				}
				crafts[worst] = best.clone();
				crafts[worst].mate(second);
				crafts[worst].generation = generation;
				
				
				for (Craft craft : crafts) {
					craft.score = 0;
					//craft.generation++;
				}
				
				
			}
		}
	}

}
/*
 *  This is just derp
 * 
 * */
@SuppressWarnings("serial")
class Comp1 extends JComponent{
	BufferedImage image;
	int height;
	int width;
	
	public Comp1(int width, int height){
		super();
		this.height = height;
		this.width = width;
		setPreferredSize(new Dimension(width,height));
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
	}
	
	public void paintComponent(Graphics g1){
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(image, null,0,0);
	}
	
	public void clear(Graphics2D g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
	}
}
