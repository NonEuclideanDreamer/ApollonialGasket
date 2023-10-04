//*************************************************
// SteinerPore.java
// author: Non-Euclidean Dreamer
// Exploring Steiner Chains
//*************************************************


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

public class SteinerPore
{
	static String name="steinerpore",
			type="png";
	static DecimalFormat df=new DecimalFormat("0000");
	static boolean write=false;
	static int[]scale= {1080,1080};
	static double acc =0.00001,
			zoom=1,factor=scale[1]*zoom/2,
			r=1,s=3,d=-2;
	static double[] loc= {0,0};
	static int black=Color.black.getRGB(),magenta=Color.magenta.getRGB(),cyan=Color.cyan.getRGB(),yellow=Color.yellow.getRGB(), white=Color.white.getRGB(),blue=Color.blue.getRGB(),
			green=Color.green.getRGB(), red=Color.red.getRGB(),
			steps=10000,start=5000;
	
	
	static BufferedImage canvas=new BufferedImage(scale[0],scale[1],BufferedImage.TYPE_3BYTE_BGR);

	static int[]col= {white,magenta,blue,cyan,green,yellow,red,magenta,blue,cyan,green,yellow,red};
	Circle[] circles;
	
	public static void main(String[] args)
	{
		double k=5*Math.PI/3;
		for(int i=0;i<steps;i++)
		{
			k+=Math.PI/120;
			d-=0.002;
			SteinerPore pore=new SteinerPore(r,s,d,k,62);
			double[] screensize=pore.screensize(2);
			zoom=screensize[2];factor=scale[1]*zoom/2;
			for(int j=0;j<2;j++)
			loc[j]=screensize[j];
			pore.draw();
			print(i);
			System.out.println(i);
		}
	}

	private static void print(int i) 
	{
		File outputfile = new File(name+df.format(start+i)+"."+type);
		try 
		{  
			ImageIO.write(canvas, type, outputfile);
		} 
		catch (IOException e) 		
		{
			System.out.println("IOException");
			e.printStackTrace();
		}
	}

	//adjust camera to see first n circles
	private double[] screensize(int n) 
	{
		Circle c;
		double[]out=new double[3];//loc0,loc1,zoom
		double[][]extrema=new double[2][2];
		
		for(int i=0;i<n;i++)
		{
			c=circles[i];
			extrema[0][0]=Math.min(extrema[0][0],c.loc[0]-Math.abs(c.r));
			extrema[0][1]=Math.max(extrema[0][1],c.loc[0]+Math.abs(c.r));
			extrema[1][0]=Math.min(extrema[1][0],c.loc[1]-Math.abs(c.r));
			extrema[1][1]=Math.max(extrema[1][1],c.loc[1]+Math.abs(c.r));
		}
		
		double	x=extrema[0][1]-extrema[0][0],
				y=extrema[1][1]-extrema[1][0];
		
		out[2]=Math.min(2/x/scale[1]*scale[0], 2/y);
		out[0]=extrema[0][0]+x/2;
		out[1]=extrema[1][0]+y/2;
		return out;
	}
	
	//Steiner chain given by two circles with center distance d & radii r&s, when first circles is at angle alpha
	public SteinerPore(double r, double s, double d, double alpha, int length) 
	{
		circles=new Circle[length];
		circles[0]=new Circle(r,new double[] {0,0},col[0]);
		circles[1]=new Circle(s, new double[] {d,0},col[0]);
		
		double t=circles[0].steinerRadius(s, d, alpha);
		
		Circle[] cand=circles[0].touchingCircles(circles[1], t, col[1]);
		if(angledist(Math.atan2(cand[0].loc[1]*Math.signum(t), cand[0].loc[0]*Math.signum(t)),alpha)<angledist(Math.atan2(cand[1].loc[1]*Math.signum(t), cand[1].loc[0]*Math.signum(t)),alpha))
				circles[2]=cand[0];
		else 	circles[2]=cand[1];
	
		
		for(int i=3; i<length;i++)
		{
			circles[i]=nextCircle(r,s,circles[i-1].r,d,circles[i-1].loc[0],circles[i-1].loc[1],turnColor((i-2.0)/(length-3)));
		}
		if(t<0&&circles[3].r<0)
		{
			System.out.println("invert!");
			circles[1].invert();
			t=circles[0].steinerRadius(-s, d, alpha);
			
			cand=circles[0].touchingCircles(circles[1], t, col[1]);
			if(angledist(Math.atan2(cand[0].loc[1]*Math.signum(t), cand[0].loc[0]*Math.signum(t)),alpha)<angledist(Math.atan2(cand[1].loc[1]*Math.signum(t), cand[1].loc[0]*Math.signum(t)),alpha))
					circles[2]=cand[0];
			else 	circles[2]=cand[1];
		
			
			for(int i=3; i<length;i++)
			{
				circles[i]=nextCircle(r,-s,circles[i-1].r,d,circles[i-1].loc[0],circles[i-1].loc[1],turnColor((i-2.0)/(length-3)));
			}
			circles[1].invert();
		}
			
	}
	
	//for rainbow colored chain
	public int turnColor(double t)
	{
		double x=t*(col.length-1);
		int n=(int)x;
		x=x%1;
		return mix(col[n%(col.length-1)+1],col[(n+1)%(col.length-1)+1],x);
	}
	
	private double angledist(double beta, double alpha) {
		double out=Math.abs(alpha-beta)%(2*Math.PI);
		return Math.min(out, 2*Math.PI-out);
	}

	
	Circle nextCircle(double r, double s, double t, double d, double bx, double by, int col)
	{
		double[]dx= {(r-s)/d, (r*r-s*s+d*d)/(2*d)},
				dy= {(r-t-bx*dx[0])/by,(r*r-t*t+bx*bx+by*by-2*bx*dx[1])/(2*by)},
				cand=Circle.mitternacht(1-dx[0]*dx[0]-dy[0]*dy[0],2*(1-dx[0]*dx[1]-dy[1]*dy[0]),r*r-dx[1]*dx[1]-dy[1]*dy[1]),
				d0= {cand[0]*dx[0]+dx[1],cand[0]*dy[0]+dy[1]},
				d1= {cand[1]*dx[0]+dx[1],cand[1]*dy[0]+dy[1]};
				double alpha0=alpha(d0,cand[0]),
						alpha1=alpha(d1,cand[1]),
						alpha=Math.atan2(by*Math.signum(t),bx*Math.signum(t));//System.out.print("oldalpha="+alpha);
				if((alpha1<alpha&&alpha<alpha0)) {return new Circle(cand[0], d0, col);}
				else if(alpha1<alpha0||(alpha0<alpha&&alpha<alpha1)){return new Circle(cand[1], d1, col);}
				else {return new Circle(cand[0], d0, col);}
			
	}
	
	double alpha(double[]vec, double r)
	{
		return Math.atan2(vec[1]*Math.signum(r),vec[0]*Math.signum(r));
	}
	
	private static void draw(Circle circ) 
	{
		if(Pantheon.dist(loc, circ.loc)<1.5/zoom+1.0/Math.abs(circ.c))
		{int col=circ.color;//color(circ.c);//
		//circ.print();
		if (circ.r==0)
		{
			for(int j=(int)((circ.loc[1]-loc[1])*factor);Math.abs(j)<scale[1]/2;j+=circ.loc[0])
			{
				for(int i=0;i<scale[0];i++)
				{
					canvas.setRGB(i,j+scale[1]/2,col);	
				}
			}
		}
		else if(circ.r<0)
		{
			double r2=Math.pow(circ.r, 2);
			for(int i=0;i<scale[0];i++)
				for(int j=0;j<scale[1];j++)
					if(Draw.distance(circ.loc,(i-scale[0]/2)/factor+loc[0],(j-scale[1]/2)/factor+loc[1])>r2)
					{
						canvas.setRGB(i, j, col);
					}
		}
		else 
		{
			
			double r2=Math.pow(circ.r, 2),s;
			for(int i=Math.max(-scale[0]/2,(int)(factor*(circ.loc[0]-loc[0]-1.0/circ.c)));i<Math.min(scale[0]/2, factor*(circ.loc[0]-loc[0]+1.0/circ.c));i++)
			{
				s=Math.sqrt(r2-Math.pow(circ.loc[0]-loc[0]-i/factor, 2));
				for(int j=Math.max(-scale[1]/2,(int)(factor*(circ.loc[1]-loc[1]-s)));j<Math.min(scale[1]/2,factor*(circ.loc[1]-loc[1]+s));j++)
					canvas.setRGB(i+scale[0]/2, j+scale[1]/2, col);
			}
		}
		if(write)
		{
			int size=50,x,y;
			if (circ.r==0)
			{
				x=scale[0]/2-size/2;
				y=(int)(factor*(circ.loc[1]-loc[1]))-(1-(int)circ.loc[0])*size+scale[1]/2;
			}
			else
			{
				size=(int)Math.min(factor*Math.abs(circ.r)*1.4/Math.max(2, Math.log10(Math.abs(circ.c))+1), 50);
				x=(int)(factor*(circ.loc[0]-loc[0]-Math.pow(2, -0.5)*circ.r))+scale[0]/2;
				y=(int)(factor*(circ.loc[1]-loc[1]-Math.pow(2, -0.5)*circ.r))+scale[1]/2;
				if(circ.c<0)x-=size;
			}
		//	System.out.println("x="+x+", y="+y+", size="+size);
			Writing.write(canvas, ""+circ.c, x,y, size,col);
		}}
		//else System.out.print(circ.c+" out of screen");
	}
	
	private void draw() 
	{
		for(int i=0;i<scale[0];i++)
			for(int j=0;j<scale[1];j++)
				canvas.setRGB(i,j,black);
		int out=circles.length;
		draw(circles[0]);draw(circles[1]);
		for(int k=out-1;k>1;k--)
			draw(circles[k]);
		
	}
	
	static int mix(int c2,int c1,  double t)
	{
		//System.out.println("t="+t);
		Color col1=new Color(c1), col2=new Color(c2);
		return new Color((int)(col1.getRed()*t+(1-t)*col2.getRed()),(int)(col1.getGreen()*t+(1-t)*col2.getGreen()),(int)(col1.getBlue()*t+(1-t)*col2.getBlue())).getRGB();
	}
}
;