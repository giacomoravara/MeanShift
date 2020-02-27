package meansh;

import java.util.ArrayList;

public class Cluster {
	public ArrayList<Point> shifted_points = new ArrayList<Point>();
	public Point mode;
	public ArrayList<Integer> members = new ArrayList<Integer>();
	
	public Cluster(Point m) {
		mode = m;
	}
	
	public void addMember(int index, Point p) {
		members.add(index);
		shifted_points.add(p);
	}
	
	public void setMode(ArrayList<Point> p) {
		for(int k=0; k < members.size();k++) {
			Point punto = p.get(members.get(k));
			punto.setMode(mode);
		}
	}
}
