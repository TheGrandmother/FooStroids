package astroids;

import java.awt.Color;
import java.awt.Graphics2D;

public class Asteroid extends Objects {
	//boolean fires = true;
	public Asteroid(double pos[], double[] vel){
		super.pos = pos.clone();
		super.vel = vel.clone();
		super.priority = 4;
		super.type = Space.Types.ASTEROID;
		super.setColor(Color.LIGHT_GRAY);
		super.radius = 20;
	}
	
	public void update(Space s){
		move();
	}
	
	public void draw(Graphics2D g) {
		g.setColor(getColor());
		g.fillOval((int)pos[0]-(int)radius, (int)pos[1]-(int)radius, (int)radius*2, (int)radius*2);
	}
	
	
	public void collide(Objects obj) {
		if(Vu.eclidianDistance(this.getPos(), obj.getPos()) <= (this.radius+obj.radius)){
			//System.out.println(Vu.eclidianDistance(this.getPos(), obj.getPos()) );
			switch (obj.type) {
			case MISSILE:
				this.kill_me = true;
				obj.kill_me = true;
				((Missile)obj).sender.score += Objects.asteroid_score;
				((Missile)obj).sender.eternal_score += Objects.asteroid_score;
				break;
			
			case ASTEROID:
				this.setVel(Vu.mul(-1, this.getVel()));
				break;
				
				
			default:
				break;
			}
		}
	}
	
	public void wallCollide(double[] normal) {
		bounce(normal);
	}
}
