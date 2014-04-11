package astroids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;


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
