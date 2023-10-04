//*************************************************************
// Pantheon.java
// author: Non-Euclidean Dreamer
// A Pantheon is what I called an Apollonial Gasket when I investigated them without knowing their name. Defined by 3 compatible curvatures, iterated from these initial circles
//*************************************************************

import java.awt.Color;
import java.util.ArrayList;

public class Pantheon 
{
	static double acc=0.0000001; //accuracy for comparing stuff
	ArrayList<Circle> circle; //List of circles in Pantheon
	ArrayList<int[]>quartet;//Four indexes of circles: ToDoList for circle creation: The circle between the first two that comes next in the sequence after the latter two
	static int black=Color.black.getRGB(),magenta=Color.magenta.getRGB(),cyan=Color.cyan.getRGB(),yellow=Color.yellow.getRGB(), white=Color.white.getRGB(),blue=Color.blue.getRGB(),
			green=Color.green.getRGB(), red=Color.red.getRGB();
	static int[] colors= {blue,red,green,yellow,white};//For gradient coloring

	//Integer Pantheon defined from integer curvatures //will get buggy if a,b,c do not extend to All-Integer Pantheon, order a,b,c by size a<=b<=c
	public Pantheon(int a, int b, int c) 
	{
		circle=new ArrayList<Circle>();
		
		quartet=new ArrayList<int[]>();
		
		if(a==0)
		{
			circle.add(new Circle(a, new double[] {1,1.0/c},colors[0]));
			circle.add(new Circle(b, new double[] {-1,-1.0/c},colors[1]));
			circle.add(new Circle(c,new double[] {-1.0/c,0},colors[2]));
			circle.add(new Circle(c,new double[] {1.0/c,0},colors[3]));
		}
		else
		{
			circle.add(new Circle(a, new double[] {0,0},colors[0]));
			circle.add(new Circle(b, new double[] {1.0/b+1.0/a,0}, colors[1]));
			circle.add(circle.get(0).touchingCircles(circle.get(1),c,colors[2])[0]);
		
			int det=(int)Math.sqrt(a*b+b*c+c*a),d=a+b+c-2*det;
			Circle[] cand=circle.get(0).touchingCircles(circle.get(1),d, colors[3]);
			if(cand[0].distance(circle.get(2))<acc) circle.add(cand[0]); 
			else circle.add(cand[1]);
		}
		quartet.add(new int[] {0,1,2,3});
		quartet.add(new int[] {0,1,3,2});
		quartet.add(new int[] {0,2,1,3});
		quartet.add(new int[] {1,2,0,3});
	}
	
	//The same four curvatures on the spectrum
	public Pantheon(double a, double b, double c) //a; b, c as curvatures!
	{
		circle=new ArrayList<Circle>();
		
		quartet=new ArrayList<int[]>();
		
		circle.add(new Circle(1.0/a, new double[] {-1.0/a,0},colors[0]));
		circle.add(new Circle(1.0/b, new double[] {1.0/b,0}, colors[1]));
		
		Circle[] cand=circle.get(0).touchingCircles(circle.get(1),1/c,colors[2]);
		
		//without this if-else there might be some circle jumps during warping
		if(c*cand[0].loc[1]>c*cand[1].loc[1]) circle.add(cand[0]);
		else circle.add(cand[1]);
		
		//Find next curvature d
		double det=Math.sqrt(a*b+b*c+c*a), // You better took a,b,c that are allowed!
				d=a+b+c-2*det;
	
		cand=circle.get(0).touchingCircles(circle.get(1),1.0/d, colors[3]);
		
		//In symmetric cases everything goes wacko hence the complicated logic path
		 if(cand[0].distance(circle.get(2))<acc)
			 {
			 	if(cand[1].distance(circle.get(2))>acc)
			 	circle.add(cand[0]); 
			 	else if(d*cand[0].loc[1]<d*cand[1].loc[1])circle.add(cand[0]);
			 	else circle.add(cand[1]);
			 }
			else circle.add(cand[1]);
		
		quartet.add(new int[] {0,1,3,2});
		quartet.add(new int[] {0,1,2,3});
		quartet.add(new int[] {2,3,1,0});
		quartet.add(new int[] {2,3,0,1});
	}
	
	//Iterate All-Integer Pantheon up to curvatures of bound leaving the instructions for smaller circles in quartet
	public void iterate(int bound)
	{
		Circle[] cand;
		Circle[] a=new Circle[4];
		int i=0;
		
		while(i<quartet.size())
		{
			int[]	ind=quartet.get(i);	
			//	System.out.print("index=(");
			for(int k=0;k<4;k++)
			{
				a[k]=circle.get(ind[k]);
				//System.out.print(ind[k]+",");
			}
			//	System.out.println(")");
			int d=2*(a[0].c+a[1].c+a[3].c)-a[2].c;
		
			if(d<bound)
			{	
				cand=a[1].touchingCircles(a[3], d,mix(a[0].color,a[1].color,a[3].color));
			
				if(cand[0].distance(a[2])>cand[1].distance(a[2])-acc&&cand[0].distance(a[0])<acc&&dist(cand[0].loc,a[2].loc)>acc)	circle.add(cand[0]);
				else	circle.add(cand[1]);
					
				quartet.remove(i);
				
				int l=circle.size()-1;
				quartet.add(new int[] {ind[0],ind[1],ind[3],l});
				quartet.add(new int[] {ind[0],ind[3],ind[1],l});
				quartet.add(new int[] {ind[1],ind[3],ind[0],l});
				
				//circle.get(circle.size()-1).print();
			}
			else i++;	
		}
	}
	
	//Iterating a Pantheon "steps" iteration steps, counting from the first 3 circles
	public void stepiterate(int steps)
	{
		Circle[] cand;
		Circle[] a=new Circle[4];
				int nextgoal=1,
					i=0,j=0,l=0;
		while(j<steps)
		{
			while(l<nextgoal)
			{
				l++;
				int[] ind=quartet.get(i);	
				System.out.print("index=(");
			
				for(int k=0;k<4;k++)
				{
					a[k]=circle.get(ind[k]);
					System.out.print(ind[k]+",");
				}
				System.out.println(")");
			
				double d=2*(1.0/a[0].r+1/a[1].r+1/a[3].r)-1/a[2].r;

				cand=a[1].touchingCircles(a[3], 1.0/d,mix(a[0].color,a[1].color,a[3].color));
			 
				if((dist(cand[1].loc,a[2].loc)<acc)||(cand[0].distance(a[0])<acc&&cand[1].distance(a[0])>acc))	circle.add(cand[0]);
				else	circle.add(cand[1]);
					
				quartet.remove(i);
				
				int m=circle.size()-1;
				quartet.add(new int[] {ind[0],ind[1],ind[3],m});
				quartet.add(new int[] {ind[0],ind[3],ind[1],m});
				quartet.add(new int[] {ind[1],ind[3],ind[0],m});
				
				circle.get(circle.size()-1).print();
			}
			nextgoal=quartet.size();
			l=0;
			j++;
		}
	}
	
	// mix the 3 colors fairly (for gradient coloring)
	static int mix(int color, int color2, int color3) 
	{
		Color c1=new Color(color),c2=new Color(color2),c3=new Color(color3);
		return new Color((c1.getRed()+c2.getRed()+c3.getRed()+1)/3,
				(c1.getGreen()+c2.getGreen()+c3.getGreen()+1)/3,
				(c1.getBlue()+c2.getBlue()+c3.getBlue()+1)/3).getRGB();
	}
	
	//If zoomed in (with no intention of zooming back out) drops quartet entries leading to circles completely outside of the screen
	public void iterateinScreen(double factor, double[] loc) 
	{
		Circle[] cand;
		Circle[] a=new Circle[4];
		int i=0;
		
		while(i<quartet.size())
		{
			int[]	ind=quartet.get(i);	
			//	System.out.print("index=(");
			for(int k=0;k<4;k++)
			{
				a[k]=circle.get(ind[k]);
				//System.out.print(ind[k]+",");
			}
			//	System.out.println(")");
			
			//new curvature
			int d=2*(a[0].c+a[1].c+a[3].c)-a[2].c;
		
			if(d<factor)
			{	
				if(dist(loc,a[3].loc)>factor/2+1.0/a[3].c+2*d)quartet.remove(i);
				else 
				{
					cand=a[1].touchingCircles(a[3], d,mix(a[0].color,a[1].color,a[3].color));
			
					if(cand[0].distance(a[2])>cand[1].distance(a[2])-acc&&cand[0].distance(a[1])<acc&&dist(cand[0].loc,a[2].loc)>acc)	circle.add(cand[0]);
					else	circle.add(cand[1]);
					
					quartet.remove(i);
				
					int l=circle.size()-1;
					quartet.add(new int[] {ind[0],ind[1],ind[3],l});
					quartet.add(new int[] {ind[0],ind[3],ind[1],l});
					quartet.add(new int[] {ind[1],ind[3],ind[0],l});
				
					circle.get(circle.size()-1).print();
				}
			}
				else i++;	
		}
	}
	
	//Euclidean distance
	public static double dist(double[] loc, double[] loc2) 
	{
		double out=0;
		for(int i=0;i<loc.length;i++)out+=Math.pow(loc[i]-loc2[i],2);
		return Math.sqrt(out);
	}
	
	//how do I need to set zoom & screen center, if I want the first n circles completely visible? returns {centerx,centery,zoom}
	public double[] screensize(int[] scale,int n) //n number of circles that need to be inside
	{
		Circle c;
		double[]out=new double[3];//loc0,loc1,zoom
		double[][]extrema=new double[2][2];
		
		for(int i=0;i<n;i++)
		{
			c=circle.get(i);
			extrema[0][0]=Math.min(extrema[0][0],c.loc[0]-Math.abs(c.r));
			extrema[0][1]=Math.max(extrema[0][1],c.loc[0]+Math.abs(c.r));
			extrema[1][0]=Math.min(extrema[1][0],c.loc[1]-Math.abs(c.r));
			extrema[1][1]=Math.max(extrema[1][1],c.loc[1]+Math.abs(c.r));
		}
		
		double	x=extrema[0][1]-extrema[0][0],
				y=extrema[1][1]-extrema[1][0];
		
		out[2]=Math.min(2/x, 2/y/scale[1]*scale[0]);
		out[0]=extrema[0][0]+x/2;
		out[1]=extrema[1][0]+y/2;
		return out;
	}
	
	//3d-Packing only works for permissible quartets a,b,c,d
	public Pantheon(double a, double b, double c, double d) //a; b, c as curvatures!
	{
		circle=new ArrayList<Circle>();
		
		quartet=new ArrayList<int[]>();
		
		circle.add(new Circle(1.0/a, new double[] {0,0,0},colors[0]));
		circle.add(new Circle(1.0/b, new double[] {1/b+1/a,0,0}, colors[1]));
		System.out.println("dist(0,1)="+circle.get(1).distance(circle.get(0)));
		Circle[] cand=circle.get(0).touchingCircles(circle.get(1),1/c,colors[2]);
		
		//without this if-else there might be some circle jumps during warping
		if(c*cand[0].loc[1]>c*cand[1].loc[1]) circle.add(cand[0]);
		else circle.add(cand[1]);
		circle.get(2).setLoc(new double[] {circle.get(2).loc[0],circle.get(2).loc[1]*Math.cos(0.1),circle.get(2).loc[1]*Math.sin(0.1)});
		System.out.println("dist(0,2)="+circle.get(2).distance(circle.get(0)));
		System.out.println("dist(2,1)="+circle.get(1).distance(circle.get(2)));

		circle.get(0).print();circle.get(1).print();circle.get(2).print();
		cand=circle.get(0).touchingSpheres(circle.get(1), circle.get(2),1.0/d, colors[3]);
		if(c*cand[0].loc[2]>c*cand[1].loc[2]) circle.add(cand[0]);
		else circle.add(cand[1]);
		circle.get(3).print();
		System.out.println("dist(0,3)="+circle.get(3).distance(circle.get(0)));
		System.out.println("dist(3,1)="+circle.get(1).distance(circle.get(3)));
		System.out.println("dist(2,3)="+circle.get(2).distance(circle.get(3)));

		//Find next curvature e
		double det=(6*(a*b+a*c+a*d+b*c+b*d+c*d)-3*(a*a+b*b+c*c+d*d)), // You better took a,b,c,d that are allowed!
				e=(a+b+c+d-Math.sqrt(det))/2;
	System.out.println("e="+e);
		cand=circle.get(0).touchingSpheres(circle.get(1), circle.get(2),1.0/e, colors[3]);
		
		//In symmetric cases everything goes wacko hence the complicated logic path
		 if(cand[0].distance(circle.get(3))<acc/10)
			 {
			 	if(cand[1].distance(circle.get(3))>acc/10)
			 	circle.add(cand[0]); 
			 	else if(e*cand[0].loc[2]<e*cand[1].loc[2])circle.add(cand[0]);
			 	else circle.add(cand[1]);
			 }
			else circle.add(cand[1]);
		circle.get(4).print();
		System.out.println("dist(0,4)="+circle.get(4).distance(circle.get(0)));
		System.out.println("dist(1,4)="+circle.get(1).distance(circle.get(4)));
		System.out.println("dist(2,4)="+circle.get(4).distance(circle.get(2)));
		System.out.println("dist(3,4)="+circle.get(4).distance(circle.get(3)));

		
		quartet.add(new int[] {0,1,2,3,4});
		quartet.add(new int[] {1,2,3,4,0});
		quartet.add(new int[] {2,3,4,0,1});
		quartet.add(new int[] {3,4,0,1,2});
		quartet.add(new int[] {4,0,1,2,3});

	}
	
	//Iterate All-Integer Pantheons up to curvatures of bound leaving the instructions for smaller circles in quartet
		public void iterate3d(int bound,boolean integer)
		{
			Circle[] cand;
			Circle[] a=new Circle[5];
			int i=0;
			
			while(i<quartet.size())
			{
				int[]	ind=quartet.get(i);	
					System.out.print("index=(");
				for(int k=0;k<5;k++)
				{
					a[k]=circle.get(ind[k]);
					System.out.print(ind[k]+",");
				}
					System.out.println(")");
				double e=1/a[0].r+1/a[1].r+1/a[2].r+1/a[4].r-1/a[3].r;
				if(integer)e=a[0].c+a[1].c+a[2].c+a[4].c-a[3].c;
				System.out.print("e="+e);
				
				boolean done=false; int j=0;
				while(j<circle.size()&&!done)
				{
					Circle circ=circle.get(j);
					if(Math.abs(1/e-circ.r)<acc) 
						if(a[0].distance(circ)<acc&&a[0]!=circ)
							if(a[1].distance(circ)<acc&&a[1]!=circ)
								if(a[2].distance(circ)<acc&&a[2]!=circ)
									if(a[4].distance(circ)<acc&&a[4]!=circ)
										if(a[3]!=circ) {	done=true; System.out.println("don");}
					j++;
				}
				if(done)quartet.remove(i);
				else if(e<bound)
				{	
				
					cand=a[1].touchingSpheres(a[2],a[4], 1.0/e,mix(a[0].color,a[1].color,a[2].color,a[4].color));

					 if(cand[0].distance(a[0])<acc/10)
					 {
					 	if(cand[1].distance(a[0])>acc/10)	circle.add(cand[0]); 
					 	else if(dist(cand[1].loc,a[3].loc)<acc/10)	circle.add(cand[0]);
					 	else circle.add(cand[1]);
					 }
					else circle.add(cand[1]);
						
					 
					quartet.remove(i);
					
					int l=circle.size()-1;
					quartet.add(new int[] {ind[0],ind[1],ind[2],ind[4],l});
					quartet.add(new int[] {ind[4],ind[0],ind[1],ind[2],l});
					quartet.add(new int[] {ind[1],ind[2],ind[4],ind[0],l});
					quartet.add(new int[] {ind[2],ind[4],ind[0],ind[1],l});

					circle.get(l).print();
				}
				else i++;	
			}
		}

	//averaging 4 colors
	private int mix(int color, int color2, int color3, int color4) 
	{
		Color c1=new Color(color),c2=new Color(color2),c3=new Color(color3), c4=new Color(color4);
		return new Color((c1.getRed()+c2.getRed()+c3.getRed()+c4.getRed()+1)/4,
				(c1.getGreen()+c2.getGreen()+c3.getGreen()+c4.getGreen()+1)/4,
				(c1.getBlue()+c2.getBlue()+c3.getBlue()+c4.getBlue()+1)/4).getRGB();
	}

	//adjusting camera to fit first n circles into screen (height for 3d-sections)
	public double[] screensize(int[] scale, int n, double height) 
	{
		Circle c;
		double[]out=new double[3];//loc0,loc1,zoom
		double[][]extrema=new double[2][2];
		
		for(int i=0;i<n;i++)
		{
			c=circle.get(i);
			double r=c.r*c.r-Math.pow(height-c.loc[2], 2);
			if(r>0)
			{
				extrema[0][0]=Math.min(extrema[0][0],c.loc[0]-Math.sqrt(r));
				extrema[0][1]=Math.max(extrema[0][1],c.loc[0]+Math.sqrt(r));
				extrema[1][0]=Math.min(extrema[1][0],c.loc[1]-Math.sqrt(r));
				extrema[1][1]=Math.max(extrema[1][1],c.loc[1]+Math.sqrt(r));
			}

		}
		
		double	x=extrema[0][1]-extrema[0][0],
				y=extrema[1][1]-extrema[1][0];
		
		out[2]=Math.min(2/x, 2/y/scale[1]*scale[0]);
		out[0]=extrema[0][0]+x/2;
		out[1]=extrema[1][0]+y/2;
		System.out.print("zoom="+out[2]+", loc=("+out[0]+","+out[1]+")");
		return out;
	}

}
