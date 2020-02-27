package meansh;

public class Point {
	public Double coord[];
	private Double mode[];
	public Boolean assigned = Boolean.FALSE;
	
	public Point() {
		coord = new Double[3];
		coord[0] = 0.0;
		coord[1] = 0.0;
		coord[2] = 0.0;
	}
	
	public Point(Double dx, Double dy, Double dz) {
		coord = new Double[3];
		coord[0] = dx;
		coord[1] = dy;
		coord[2] = dz;
		mode = new Double[3];
		mode[0] = dx;
		mode[1] = dy;
		mode[2] = dz;
	}
	
	public Point(Double d[]) {
		coord = new Double[3];
		coord[0] = d[0];
		coord[1] = d[1];
		coord[2] = d[2];
	}
	
	public void setMode(Point p) {
		mode = new Double[3];
		mode[0] = p.coord[0];
		mode[1] = p.coord[1];
		mode[2] = p.coord[2];
	}
	
	public Point getMode() {
		return new Point(mode);
	}
}
