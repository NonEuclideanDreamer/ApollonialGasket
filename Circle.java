//***********************************************************
// Circle.java
// author: Non-Euclidean Dreamer
// Class of Circle- objects, defined by radius r and location, the integer curvature c is to stay exact for all-integer gaskets, curvature=0 is a line
//**************************************************************
public class Circle 
{
	int color,
		c;//curvature
	double r;//radius
	static double acc=0.0000001;//accuracy to check wether things are zero/the same
	double[]loc; //location
	
	//Circle with integer curvature k
	public Circle(int k, double[]l, int col) 
	{
		c=k;
		r=1.0/k;
		loc=l;
		color=col;
	}

	//Circle with radius r
	public Circle(double r2, double[] l, int col) 
	{
		c=(int)(1/r2);
		if(r2==0)c=0;
		r=r2;
		loc=l;
		color=col;
	}

	//solving the quadratic equation, just assuming a solution does exist.
	public static double[] mitternacht(double a,double b,double c)
	{
		//System.out.println(a+"x²+"+b+"x+"+c+"=0"); //for debugging
		double det=b*b-4*a*c;
		//	if(det<-acc)return new double[] {}; //if we want to recognize empty solutions
		 if(det<acc) 
		 {
			 System.out.println("det<0");
			 return new double[]{-b/(2*a),-b/(2*a)};
		 }
		
		else		
		{
			det=Math.sqrt(det);
			return new double[] {(-b+det)/(2*a),(-b-det)/(2*a)};
		}
	}
	
	
	//Find  the 2 circles of integer curvature cur touching this & b (also integer)
	public Circle[]touchingCircles(Circle b, int cur, int col)
	{
		//System.out.println("loc=("+loc[0]+","+loc[1]+"), bloc=("+b.loc[0]+","+b.loc[1]+")");//for debugging
		Circle[] out=new Circle[2];
		if(c==0)
		{
			double y=loc[1]-loc[0]/b.c;
			double[]x=mitternacht(1,-2*b.loc[0],Math.pow(b.loc[0], 2)+Math.pow(y-b.loc[1], 2)-Math.pow(1.0/cur+1.0/b.c, 2));
			out[0]=new Circle(cur, new double[] {x[0],y},col);
			out[1]=new Circle(cur, new double[] {x[1],y},col);

		}
		else if(Math.abs(loc[0]-b.loc[0])<acc)
		{
			double y=(Math.pow(c,-2)-Math.pow(b.c, -2)+2.0/cur/c-2.0/cur/b.c-Math.pow(loc[0],2)+Math.pow(b.loc[0], 2)-Math.pow(loc[1],2)+Math.pow(b.loc[1], 2))/2/(b.loc[1]-loc[1]);
			double[] x=mitternacht(1,-2*loc[0],loc[0]*loc[0]+Math.pow(y-loc[1],2)-Math.pow(1.0/c+1.0/cur, 2));
			out[0]=new Circle(cur, new double[] {x[0],y},col);
			out[1]=new Circle(cur, new double[] {x[1],y},col);
		}
		else 
		{
			double[]x=lineareq(2*(b.loc[0]-loc[0]),2*(b.loc[1]-loc[1]),
						Math.pow(c,-2)-Math.pow(b.c, -2)+2.0/cur/c-2.0/cur/b.c-Math.pow(loc[0],2)+Math.pow(b.loc[0], 2)-Math.pow(loc[1],2)+Math.pow(b.loc[1], 2));
			
			double[] loc1=new double[2],
					loc2=new double[2],
					y=mitternacht(1+x[1]*x[1],2*x[0]*x[1]-2*loc[0]*x[1]-2*loc[1],loc[1]*loc[1]+loc[0]*loc[0]-2*loc[0]*x[0]+x[0]*x[0]-Math.pow(1.0/c+1.0/cur,2));
			
			loc1[1]=y[0];
			loc1[0]=x[0]+y[0]*x[1];
			loc2[1]=y[1];
			loc2[0]=x[0]+y[1]*x[1];
		
			out[0]=new Circle(cur,loc1,col);
			out[1]=new Circle(cur,loc2,col);
		}
		//System.out.println("candidates:");
		//	out[0].print();
		//	out[1].print();
		return out;
	}
	
	//Find the 2 circles of radius r touching this and b
	public Circle[]touchingCircles(Circle b, double r, int col)
	{
		int cur=(int)(1/r);
		Circle[] out=new Circle[2];
		if(this.r==0)
		{
			double y=loc[1]-loc[0]*b.r;
			double[]x=mitternacht(1,-2*b.loc[0],Math.pow(b.loc[0], 2)+Math.pow(y-b.loc[1], 2)-Math.pow(r+b.r, 2));
			out[0]=new Circle(cur, new double[] {x[0],y},col);
			out[1]=new Circle(cur, new double[] {x[1],y},col);
		}
		
		else if(Math.abs(loc[0]-b.loc[0])<Math.abs(loc[1]-b.loc[1]))
		{
			double[]x=lineareq(2*(b.loc[1]-loc[1]),2*(b.loc[0]-loc[0]),
						Math.pow(this.r,2)-Math.pow(b.r, 2)+2.0*r*this.r-2.0*r*b.r-Math.pow(loc[1],2)+Math.pow(b.loc[1], 2)-Math.pow(loc[0],2)+Math.pow(b.loc[0], 2));
			double[] loc1=new double[2],
					loc2=new double[2],
					y=mitternacht(1+x[1]*x[1],2*x[0]*x[1]-2*loc[1]*x[1]-2*loc[0],loc[0]*loc[0]+loc[1]*loc[1]-2*loc[1]*x[0]+x[0]*x[0]-Math.pow(this.r+r,2));
			
			loc1[0]=y[0];
			loc1[1]=x[0]+y[0]*x[1];
			loc2[0]=y[1];
			loc2[1]=x[0]+y[1]*x[1];
		
			out[0]=new Circle(r,loc1,col);
			out[1]=new Circle(r,loc2,col);
		}
	
		else 
		{
			double[]x=lineareq(2*(b.loc[0]-loc[0]),2*(b.loc[1]-loc[1]),
						Math.pow(this.r,2)-Math.pow(b.r, 2)+2.0*r*this.r-2.0*r*b.r-Math.pow(loc[0],2)+Math.pow(b.loc[0], 2)-Math.pow(loc[1],2)+Math.pow(b.loc[1], 2));
			double[] loc1=new double[2],
					loc2=new double[2],
					y=mitternacht(1+x[1]*x[1],2*x[0]*x[1]-2*loc[0]*x[1]-2*loc[1],loc[1]*loc[1]+loc[0]*loc[0]-2*loc[0]*x[0]+x[0]*x[0]-Math.pow(this.r+r,2));
		
			loc1[1]=y[0];
			loc1[0]=x[0]+y[0]*x[1];
			loc2[1]=y[1];
			loc2[0]=x[0]+y[1]*x[1];
		
			out[0]=new Circle(r,loc1,col);
			out[1]=new Circle(r,loc2,col);
		}
		
		//System.out.println("candidates:");
		//	out[0].print();
		//	out[1].print();
		return out;
	}
	
	//solve ax*x+ay*y=c for x output given as {constant, y-factor}. !Not handling degenerate cases!
	public static double[] lineareq(double ax,double ay, double c)
	{
		//	System.out.println(ax+"x+"+ay+"y="+c);
		return new double[] {c/ax,-ay/ax};
	}
	
	//Distance between 2 circles. zero means the touch
	public double distance(Circle b)
	{
		//	System.out.print("Distance between ");print();System.out.print(" and ");b.print();
		
		//degenerate cases:
		if(r==0&b.r==0)return 0;
		if(r==0)
			return Math.signum(loc[1])*(b.loc[1]-loc[1])-b.r;
		if(b.r==0)
			return Math.signum(b.loc[1])*(loc[1]-b.loc[1])-r;
	
		//normal case:
		double out=Math.sqrt(Math.pow(loc[0]-b.loc[0],2)+Math.pow(loc[1]-b.loc[1],2));
		out-=Math.abs(r+b.r);
		out=Math.abs(out);
		return out;
	}

	public void setColor(int i) 
	{
		color=i;
	}
	public void print()
	{
		System.out.println("circle:"+c+", "+r+", {"+loc[0]+","+loc[1]+"}");
	}
}

