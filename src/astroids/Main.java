package astroids;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;



public class Main extends JFrame {
	int lol = 0;
	BufferedImage background;
	BufferedImage foreground;
	int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public static void main(String[] args){
		Main m = new Main();
		Comp c = m.new Comp();
		DisplayMode dm = new DisplayMode(m.width,m.height, 16, DisplayMode.REFRESH_RATE_UNKNOWN);
		m.add(c);
		c.setVisible(true);
		c.setSize(m.width, m.height);
		//m.pack();
		 
		m.run(dm);
		for (int i = 0; i < 500; i++) {

			m.lol++;
			c.repaint();
			c.paintBackground(m.background.createGraphics());
			
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {}
			c.clearBackgound(m.background.createGraphics());
			
			//m.update(m.getGraphics());
		}
	}
	
	public Main() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		background = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		foreground = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public void run(DisplayMode dm){
		setBackground(Color.BLACK);
		setForeground(Color.WHITE);
		setFont(new Font("Arial", Font.BOLD, 24));
		
		
		Screen s = new Screen();
		try {
			s.setFullscren(dm, this);
		} catch (Exception e) {
		}
	}
	
	class Comp extends JComponent{
		
		public Comp(){
			super();
		}
			
		public void paintComponent(Graphics g1){
			Graphics2D g = (Graphics2D)g1;
			super.paintComponent(g1);
			g.drawImage(background, 0, 0, null);
			//g.drawImage(foreground, 0, 0, null);
		}
		
		public void clearBackgound(Graphics2D g){
			//Graphics2D g = background.createGraphics();
			g.setColor(new Color(0, 0, 1, 255));
			g.fillRect(0, 0, background.getWidth(), background.getHeight());
		}
		
		public void paintBackground(Graphics2D g){
			g.setColor(Color.white);
			g.drawString("asien", 100+lol, 100+lol);
		}
	}
}

class Screen {
	private GraphicsDevice vc;
	
	public Screen() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		vc = env.getDefaultScreenDevice();
		
	}
	
	public void setFullscren(DisplayMode dm, JFrame window){
		window.setUndecorated(true);
		window.setResizable(false);
		
		vc.setFullScreenWindow(window);
		
		if(dm != null && vc.isDisplayChangeSupported()){
			try {
				vc.setDisplayMode(dm);
			} catch (Exception e) {
				
			}
		}
	}
	
	public Window getFullScreenWindow(){
		return vc.getFullScreenWindow();
	}
	
	public void restoreScreen(){
		Window w = vc.getFullScreenWindow();
		if(w != null){
			w.dispose();
		}
		vc.setFullScreenWindow(null);
	}
}


