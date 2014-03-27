package astroids;

public abstract class Vu {
	/**
	 * Rotates <b>v</b> counter clockwise by <b>a</b> radians.
	 * 
	 * @param v
	 * @param a
	 */
	static double[] rotate(double[] v,double a){
		double[] r  = new double[2];
		r[0] = v[0]*Math.cos(a) - v[1]*Math.sin(a);
		r[1] = v[1]*Math.cos(a) + v[0]*Math.sin(a);
		return r;
	}
	
	static double[] sub(double[] a, double[] b){
		double[] r = new double[a.length];
		for (int i = 0; i < b.length; i++) {
			r[i] = a[i] - b[i];
		}
		return r;
	}
	
	static double[] add(double[] a, double[] b){
		double[] r = new double[a.length];
		for (int i = 0; i < b.length; i++) {
			r[i] = a[i] + b[i];
		}
		return r;
	}
	
	static double eclidianDistance(double[] a, double[] b){
		double r = Math.sqrt((a[0]-b[0])*(a[0]-b[0]) + (a[1]-b[1])*(a[1]-b[1]));
		return r;
	}
	
	static double scalarProduct(double[] a, double[] b){
		double r = 0;
		for (int i = 0; i < b.length; i++) {
			r += a[i] * b[i];
		}
		return r;
	}
	
	static double[] mul(double p, double[] v){
		double[] r = new double[v.length];
		for (int i = 0; i < v.length; i++) {
			r[i] += v[i] * p;
		}
		return r;
	}
	
	static double length(double[] a){
		return Math.sqrt(scalarProduct(a, a));
	}
	
	static double[] crossProduct(double[] a, double[] b){
		double[] r = new double[3];
		r[0] = a[1]*b[2] -a[2]*b[1];
		r[1] = a[2]*b[0] -a[0]*b[1];
		r[2] = a[0]*b[1] -a[1]*b[0];
		return r;
	}
	
	static double[] normalize(double[] a){
		double length = length(a);
		return mul( 1/length ,a);
	}
	
	static double[] random(double min_x, double max_x, double min_y, double max_y){
		double[] ret = new double[2];
		ret[0] = Math.random()*Math.abs((max_x-min_x)) + min_x;
		ret[1] = Math.random()*Math.abs((max_y-min_y)) + min_y;
		return ret;
	}
	
}
