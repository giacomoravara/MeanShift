package meansh;
import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;


public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException{
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the path");
		String path = in.nextLine();
		System.out.println("Enter the path for output");
		String pathOut = in.nextLine();
		File output = new File(pathOut);
		ArrayList<Point> v = new ArrayList<Point>();
		BufferedImage img = null;
		img =  ImageIO.read(new File(path));
		int height = img.getHeight();
		int width = img.getWidth();
		Color[][] colors = new Color[width][height];
		v.ensureCapacity(width*height);
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				colors[x][y] = new Color(img.getRGB(x,y));
				Point p = new Point(Double.valueOf(colors[x][y].getRed()),Double.valueOf(colors[x][y].getGreen()),Double.valueOf(colors[x][y].getBlue()));
				v.add((x*width+y)%width,p);
				}
			}
		in.close();
			
			
		Double bandWidth = 25.0;
		int N_Thread = 4;
		int N = 100;
//Sequential			
		ArrayList<Cluster> cl_2 = new ArrayList<Cluster>();
		long startTime[] = new long[N];
		long endTime[] = new long[N];
		long avg_seq = 0;
			
		for(int j = 0; j < N; j++) {
			MeanShift m = new MeanShift(v, bandWidth);
			startTime[j] = System.currentTimeMillis();
			cl_2 = m.mean_shift_cluster(500.0, 100);
			endTime[j] = System.currentTimeMillis();
			avg_seq += endTime[j] - startTime[j];
		}
		System.out.println("Sequential took " + avg_seq/N + " milliseconds");
			
		for(int i = 0; i < cl_2.size(); i++) {
			System.out.println("Members: " + cl_2.get(i).members.size() + " , Mode: " + cl_2.get(i).mode.coord[0] + " " + cl_2.get(i).mode.coord[1] + " " + cl_2.get(i).mode.coord[2]);
			cl_2.get(i).setMode(v);
			}
		System.out.println(cl_2.size());
//Parallel			
		ArrayList<Cluster> cl_2_par = new ArrayList<Cluster>();
		long avg_par = 0;
		long startTimePAR[] = new long[N];
		long endTimePAR[] = new long[N];
			
		for(int k = 0; k < N; k++) {
			MeanShift m2 = new MeanShift(v,bandWidth);
			startTimePAR[k] = System.currentTimeMillis();
			cl_2_par = m2.mean_shift_cluster_par_2(500.0, 100, N_Thread);
			endTimePAR[k] = System.currentTimeMillis();
			avg_par += endTimePAR[k] - startTimePAR[k];
		
		}	
		System.out.println("Parallel took " + avg_par/N + " milliseconds");
			
			
		for(int i = 0; i < cl_2_par.size(); i++) {
			System.out.println("Members: " + cl_2_par.get(i).members.size() + " , Mode: " + cl_2_par.get(i).mode.coord[0] + " " + cl_2_par.get(i).mode.coord[1] + " " + cl_2_par.get(i).mode.coord[2]);
			cl_2_par.get(i).setMode(v);
		}
		System.out.println(cl_2_par.size());
			

		BufferedImage imag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for(int i = 0; i < v.size(); i++) {
			int x = Math.round(i/imag.getHeight());
			int y = i%imag.getHeight();
			Point mode = v.get(i).getMode();
			float red = mode.coord[0].floatValue()/255.0F;
			float green = mode.coord[1].floatValue()/255.0F;
			float blue = mode.coord[2].floatValue()/255.0F;
			Color c = new Color(red,green,blue);
			int rgb = c.getRGB();
			imag.setRGB(imag.getWidth()-x-1, y, rgb);
			}
		ImageIO.write(imag, "jpg",output);
		

	}

}
