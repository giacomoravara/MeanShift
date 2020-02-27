package meansh;

import java.util.ArrayList;
import java.util.List;

public class AtomicThread extends Thread {
	private AtomicIndex ind;
	public List<Point> shiftedOR; 
	public ArrayList<Point> orig; 
	public Double epsilon;
	public int max;
	private Double bw_squrd;
	private Double rev_bw;
	
	public AtomicThread(AtomicIndex aI, List<Point> p,  ArrayList<Point> v,Double e, int m, Double bw) {
		ind = aI;
		bw_squrd = bw*bw;
		rev_bw = 1.0/(Math.sqrt(2*Math.PI)*bw);
		shiftedOR = p;
		orig=v;
		epsilon = e;
		max = m;
	}
	
	public void run() {
		try{
			int i = 0;
			while(true) {
				i = ind.getIndex();
				if(i >= orig.size()) {
					break;
					}
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
			}catch(InterruptedException e) {}
		}
	
	public Point shifter(Point punto){
		Point nuovo = new Point();
		Double tot_weight = 0.0;
		Double dist = 0.0;
		Double weight = 0.0;
		for(int i=0; i < orig.size(); i++) {
			dist = distance(orig.get(i),punto);
			weight = kernel(dist);
			for(int k=0; k < 3; k++) {
				nuovo.coord[k] += orig.get(i).coord[k]*weight;
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
