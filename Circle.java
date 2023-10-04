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
	static double acc=0.0000001;//accuracy to check whether things are zero/the same
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
		c=(int) Math.round(1/r2);
		if(r2==0)c=0;
		r=r2;
		loc=l;
		color=col;
	}

	//solving the quadratic equation, just assuming a solution does exist.
	public static double[] mitternacht(double a,double b,double c)
	{
		double det=b*b-4*a*c;
		//	if(det<-acc)return new double[] {}; //if we want to recognize empty solutions
		 if(det<0) 
		 {
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
		
		return out;
	}
	
	//solve a*x+b*y=c for x output given as {constant, y-factor}. !Not handling degenerate cases!
	public static double[] lineareq(double a,double b, double c)
	{
		//	System.out.println(ax+"x+"+ay+"y="+c);
		return new double[] {c/a,-b/a};
	}
	//solve a*x+b*y+c*z=d for x output given as {constant, y-factor, z-factor}. !Not handling degenerate cases!
	public static double[] lineareq(double a,double b,double c, double d)
	{
		
		return new double[] {d/a,-b/a,-c/a};
	}
	//Distance between 2 circles. zero means the touch
	public double distance(Circle b)
	{
		
		//degenerate cases:
		if(r==0&b.r==0)return 0;
		if(r==0)
			return Math.signum(loc[1])*(b.loc[1]-loc[1])-b.r;
		if(b.r==0)
			return Math.signum(b.loc[1])*(loc[1]-b.loc[1])-r;
	
		//normal case:
		double out=0;
		for(int i=0;i<loc.length;i++)out+=(loc[i]-b.loc[i])*(loc[i]-b.loc[i]);
		out=Math.sqrt(out);
		out-=Math.abs(r+b.r);
		out=Math.abs(out);
		return out;
	}

	public void setColor(int i) 
	{
		color=i;
	}
	
	//print circle parameters to the treminal
	public void print()
	{
		System.out.print("circle:"+c+", "+r+", {"+loc[0]);
		for(int i=1;i<loc.length;i++)
			System.out.print(","+loc[i]);
		System.out.println("}");
	}
	
	//2 circles with center distance d , second radius is s, alphe the angle between centers
	public double steinerRadius(double s, double d,double alpha)
	{
		return (r*r-s*s+d*d-2*r*d*Math.cos(alpha))/(2*(s-r+d*Math.cos(alpha)));
	}
	
	//turn inside out
	public void invert() 
	{
		r*=(-1);
		c*=-1;
	}

	public void setLoc(double[] l) 
	{
		loc=l;
	}

	//find the two spheres of radius r touching this circle&b&c
	public Circle[] touchingSpheres(Circle b, Circle c, double r, int col) 
	{
		Circle[] out=new Circle[2];
		int k=0,l,m;
		double max=0;
		for(int i=0;i<3;i++)
			if(Math.min(Math.abs(loc[i]-c.loc[i]), Math.abs(c.loc[i]-b.loc[i]))>max)
			{
				max=Math.min(Math.abs(loc[i]-c.loc[i]), Math.abs(c.loc[i]-b.loc[i]));
				k=i;
			}
		max=0;
		l=(k+1)%3; m=(l+1)%3;
	
		if(this.r==0)//ToDo
		{
			double y=loc[1]-loc[0]*b.r;
			double[]x=mitternacht(1,-2*b.loc[0],Math.pow(b.loc[0], 2)+Math.pow(y-b.loc[1], 2)-Math.pow(r+b.r, 2));
			out[0]=new Circle(0, new double[] {x[0],y},col);
			out[1]=new Circle(0, new double[] {x[1],y},col);
		}
		
		
		else 
		{
			double[]z0=lineareq(2*(c.loc[k]-b.loc[k]),2*(c.loc[l]-b.loc[l]),2*(c.loc[m]-b.loc[m]),
						Math.pow(r+b.r,2)-Math.pow(r+c.r, 2)-Math.pow(b.loc[1],2)+Math.pow(c.loc[1], 2)-Math.pow(b.loc[0],2)+Math.pow(c.loc[0], 2)-Math.pow(b.loc[2],2)+Math.pow(c.loc[2], 2)),
					z1=lineareq(2*(c.loc[k]-loc[k]),2*(c.loc[l]-loc[l]),2*(c.loc[m]-loc[m]),
							Math.pow(r+this.r,2)-Math.pow(r+c.r, 2)+Math.pow(c.loc[1],2)-Math.pow(loc[1], 2)+Math.pow(c.loc[0],2)-Math.pow(loc[0], 2)+Math.pow(c.loc[2],2)-Math.pow(loc[2], 2));
		
			if(Math.abs(z0[2]-z1[2])<Math.abs(z0[1]-z1[1]))
			{
				m=l;
				l=(m+1)%3;
				max=z0[2];
				z0[2]=z0[1];
				z0[1]=max;
				max=z1[2];
				z1[2]=z1[1];
				z1[1]=max;
			}
			
	
			double[]	y=lineareq(z0[2]-z1[2],z0[1]-z1[1],z1[0]-z0[0]),
					z=new double[] {z0[0]+z0[2]*y[0],z0[1]+z0[2]*y[1]},
					x=mitternacht(1+y[1]*y[1]+z[1]*z[1],2*(z[0]-loc[k])*z[1]+2*(y[0]-loc[m])*y[1]-2*loc[l],loc[0]*loc[0]+loc[1]*loc[1]+loc[2]*loc[2]-2*loc[m]*y[0]-2*loc[k]*z[0]+y[0]*y[0]+z[0]*z[0]-Math.pow(this.r+r,2)),
					loc1=new double[3],
					loc2=new double[3];
			loc1[l]=x[0];
			loc2[l]=x[1];
			loc1[m]=y[0]+y[1]*x[0];
			loc2[m]=y[0]+y[1]*x[1];
			loc1[k]=z[0]+z[1]*x[0];
			loc2[k]=z[0]+z[1]*x[1];
			
			out[0]=new Circle(r,loc1,col);
			out[1]=new Circle(r,loc2,col);

		}
	
		return out;
	}
	
	//print an array to the terminal
	private void print(double[] m) 
	{
		System.out.print("{");
		for(int i=0;i<m.length;i++)
		{
			System.out.print(m[i]+", ");	
		}
		System.out.println("}");
	}

	
	
	//move the circle by loc2
	public void move(double[] loc2) 
	{
		for(int i=0;i<loc.length;i++)
		{
			loc[i]+=loc2[i];
		}
	}

}