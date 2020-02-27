package meansh;

import java.util.*;
public class MeanShift {
	private ArrayList<Point> originals;
	private ArrayList<Point> shifted;
	private ShifterThread[] b;
	private AtomicIndex atomInd = new AtomicIndex();
	private Double bw_squrd;
	private Double bw;
	private Double rev_bw;
	
	public MeanShift(ArrayList<Point> w, Double bw) {
		this.bw = bw;
		bw_squrd = bw*bw;
		rev_bw = 1.0/(Math.sqrt(2*Math.PI)*bw);
		originals=w;
		shifted = new ArrayList<Point>(originals.size());
		for(int i=0; i < originals.size(); i++) {
			shifted.add(i,new Point());
			for(int k=0; k < 3; k++) {
				shifted.get(i).coord[k] = originals.get(i).coord[k];
				}
			}
		}
	
	public Point shifter(Point punto){
		Point shift = new Point();
		Double tot_weight = 0.0;
		Double dist = 0.0;
		Double weight = 0.0;
		for(int i=0; i < originals.size(); i++) {
			dist = distance(originals.get(i),punto);
			weight = kernel(dist);
			for(int k=0; k < 3; k++) {
				shift.coord[k] += originals.get(i).coord[k]*weight;
				}
			tot_weight += weight; 
			}
		
		for(int k=0; k < 3; k++) {
			shift.coord[k] /= tot_weight;
			}
		
		return shift;
		}
	
	public List<Point> mean_shift(double epsilon, int max){
		for(int i=0; i < shifted.size();i++) {
			int iter = 0;
			Double d = 0.0;
			Point pt; 
			Point sh;
			do{
				pt = shifted.get(i);
				sh = shifter(pt);
				d = distance(pt,sh);
				shifted.set(i,sh);
				iter++;
				}while(d > epsilon && iter < max);
			}
		return shifted;
		}
	
	public List<Point> mean_shift_par(double epsilon, int max, int N_Thread){
		b = new ShifterThread[N_Thread];
		for(int j = 0; j < N_Thread; j++) {
			b[j] = new ShifterThread(j, shifted , originals, epsilon , max, bw,N_Thread);
			b[j].start();
			}
		try{
			for(int j = 0; j < N_Thread; j++) {
				b[j].join();
				}
			for(int j = 0; j < N_Thread; j++) {
				b[j].interrupt();
				}
			}catch(InterruptedException e) {
				
			}
		return shifted;
		}
	
	public List<Point> mean_shift_par_2(double epsilon, int max, int N_Thread){
		AtomicThread[] bM = new AtomicThread[N_Thread];
		for(int j = 0; j < N_Thread; j++) {
			bM[j] = new AtomicThread(atomInd, shifted , originals, epsilon , max, bw);
			bM[j].start();
			}
		try{
			for(int j = 0; j < N_Thread; j++) {
				bM[j].join();
				}
			for(int j = 0; j < N_Thread; j++) {
				bM[j].interrupt();
				}
			}catch(InterruptedException e) {
				
			}
		return shifted;
		}
	
	
	public ArrayList<Cluster> cluster_shift(List<Point> shi, Double epsilon){
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		for(int i=0; i < shi.size(); i++) {
			Point pt = shi.get(i);
			int c_it = 0;
			for(; c_it < clusters.size(); c_it++) {
				if(distance(pt,clusters.get(c_it).mode) <= epsilon) {
					break;
					}
				}
			if(c_it == clusters.size()) {
				clusters.add(new Cluster(pt));
				}
			clusters.get(c_it).addMember(i,pt);
			}
		return clusters;
		}
	
	
	public ArrayList<Cluster> mean_shift_cluster( Double epsilon, int max){
		List<Point> shi = mean_shift(epsilon, max);

		return cluster_shift(shi, epsilon);
		}
	
	public ArrayList<Cluster> mean_shift_cluster_par(Double epsilon, int max, int N_Thread){
		List<Point> shi = mean_shift_par(epsilon, max, N_Thread);

		return cluster_shift(shi, epsilon);
		}
	
	public ArrayList<Cluster> mean_shift_cluster_par_2(Double epsilon, int max, int N_Thread){
		List<Point> shi = mean_shift_par_2(epsilon, max, N_Thread);

		return cluster_shift(shi, epsilon);
		}
	
	public Double distance(Point v1, Point v2) {
		Double dist = 0.0;
		dist = (v1.coord[0] - v2.coord[0])*(v1.coord[0] - v2.coord[0]) + (v1.coord[1] - v2.coord[1])*(v1.coord[1] - v2.coord[1]) + (v1.coord[2] - v2.coord[2])*(v1.coord[2] - v2.coord[2]);
		return dist;
		}
	
	public Double kernel(Double d) {
		return (rev_bw)*Math.exp(-0.5*d/bw_squrd);
		}
}
