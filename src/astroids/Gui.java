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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Gui extends JFrame{
	public Gui(){
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Gui g = new Gui();
		int width = 1000;
		int height= 1000;
		Comp c = new Comp(width,height);
		
		g.add(c);

		g.setLayout(new FlowLayout());
		g.pack();		

		c.setVisible(true);
		g.setVisible(true);
		c.clear(c.image.createGraphics());
		
		Space s = new Space(width, height);
		
		int number_of_crafts = 10;
		int number_of_asteroids = 20;
		long game_time = 7000;
		long update_time = 10;
		
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
				if(System.currentTimeMillis()-super_time > game_time){
					break;
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
class Comp extends JComponent{
	BufferedImage image;
	int height;
	int width;
	
	public Comp(int width, int height){
		super();
		this.height = height;
		this.width = width;
		setPreferredSize(new Dimension(width,height));
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
	}
	
	public void paintComponent(Graphics g1){
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(image, null,0,0);
	}
	
	public void clear(Graphics2D g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
	}
}
