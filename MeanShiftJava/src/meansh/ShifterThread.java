package meansh;

import java.util.ArrayList;
import java.util.List;

public class ShifterThread extends Thread {
	public int start;
	public int end;
	public List<Point> shiftedOR; 
	public ArrayList<Point> orig_points; 
	public Double epsilon;
	public int max;
	private Double bw_squrd;
	private Double rev_bw;
	
	public ShifterThread(int s, List<Point> p,  ArrayList<Point> v,Double e, int m, Double bw, int N_Thread) {
		bw_squrd = bw*bw;
		rev_bw = 1.0/(Math.sqrt(2*Math.PI)*bw);
		shiftedOR = p;
		start = s*shiftedOR.size()/N_Thread;
		end = (s + 1)*shiftedOR.size()/N_Thread;
		orig_points=v;
		epsilon = e;
		max = m;
	}
	
	public void run() {
		for(int i = start; i < end; i++ ) {
			int iter = 0;
			Double d = 0.0;
			Point pt;
			Point sh;
			do{
				pt = shiftedOR.get(i);
				sh = shifter(pt);
				d = distance(pt,sh);
				shiftedOR.set(i,sh);
				iter++;
				}while(d > epsilon && iter < max);
			}
		}
	
	public Point shifter(Point punto){
		Point nuovo = new Point();
		Double tot_weight = 0.0;
		Double dist = 0.0;
		Double weight = 0.0;
		for(int i=0; i < orig_points.size(); i++) {
			dist = distance(orig_points.get(i),punto);
			weight = kernel(dist);
			for(int k=0; k < 3; k++) {
				nuovo.coord[k] += orig_points.get(i).coord[k]*weight;
				}
			tot_weight += weight; 
			}
		for(int k=0; k < 3; k++) {
			nuovo.coord[k] /= tot_weight;
			}
		return nuovo;
		}
	
	public Double distance(Point v1, Point v2) {
		Double dist = 0.0;
		dist = (v1.coord[0] - v2.coord[0])*(v1.coord[0] - v2.coord[0]) + (v1.coord[1] - v2.coord[1])*(v1.coord[1] - v2.coord[1]) + (v1.coord[2] - v2.coord[2])*(v1.coord[2] - v2.coord[2]);
		return dist;
		}

	public Double kernel(Double d) {
		return  (rev_bw)*Math.exp(-0.5*d/bw_squrd);
		}
}
