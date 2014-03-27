package astroids;

public class Test {

	public static void main(String[] args) {
		
		double[] pos = {250,250};
		double[] vel ={1,1};
		double[] dir = {0,-1};
		Craft cr = new Craft(pos, dir, vel);
		//double[] point = {50,0};
		double[] a_pos = {240,200};
		double[] a_vel ={1,0};
		Asteroid as = new Asteroid(a_pos, a_vel);
		System.out.println(cr.toTheRight(as.getPos(), cr.getPos()));
		Space s =new Space(500, 500);
		s.addObject(as);
		s.addObject(cr);
		cr.populateFov(s);
		s.print();
		double[] normal = {-1,1};
		cr.wallCollide(normal);
		System.out.println(cr.getVel()[0] + " " + cr.getVel()[1]);
		
		
		
		
	}

}
