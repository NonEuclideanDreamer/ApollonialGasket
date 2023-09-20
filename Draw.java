
//*************************************************************
// Draw.java
// author: Non-Euclidean Dreamer
// Drae nad manipulate Apollonial gaskets
//*************************************************************

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;

public class Draw 
{
	static DecimalFormat df=new DecimalFormat("0000");
	static int background=Color.black.getRGB();
	public static String name="gasket",
			 type="png";
	static boolean write=false; //should the curvature be written on the circles?
	
	//different modes for the animation. Particular details need to be changed in the main method
	static String animation="warp",// warp the gasket
							//"it", //iterate the gasket
							//"int",//Go thhrough integer cases
							//"zoom", //zoom in
				palette="gradient";
						//"pastellprimes";
	static int[]scale= {1080,1080};//screen dimensions
	static int a=0,b=0,c=1, //integer curvatures
			bound=11, //how many iterations of the gasket // resp up to what curvature for "int"-animation
			it=2880, start=0; //how many pictures are generated & where does there numbering start
	
	static double q=-1,r=2,s=2;//continuous curvatures
	static double zoom=0.75,
			factor=scale[1]*zoom/2;
	
	static double[]loc= {0,0};//{-1.0/a,1.0/a};
	static int[] primes=new int[1];//for pastellprimes coloring
	
	static BufferedImage canvas=new BufferedImage(scale[0],scale[1],BufferedImage.TYPE_3BYTE_BGR);

	public static void main(String[] args) 
	{
		if(palette=="pastellprimes")
		primes=primes(100);
		
		Pantheon p=new Pantheon(a,b,c);
		int k=0,prog=0;
		
		if(animation=="it")
		for(int i=c;i<bound;i++)
		{
			p.iterateinScreen(i,loc);
			if(prog<p.circle.size())
			{
				prog=draw(p,prog);
				print(name+df.format(k)+"."+type);
				k++;
			}
		}
		else if(animation=="int")
		{
			
			for(int i=0;i<it;i++)
			{	
				//if you put a,b,c that don't work, it won't work!
				a=-4*i-4;
				b=4*i+8;
				c=(2*i+3)*(2*i+3);
				setZoom(-a);
				loc=new double[] {-1.0/a,0};
				bound=(int) (1000*zoom);
				p=new Pantheon(a,b,c);
				Pantheon.acc=0.0000001/zoom;
				Circle.acc=Pantheon.acc;
				p.iterateinScreen(bound, loc);
				draw(p);
				print(name+df.format(i)+"."+type);
			}
		}
		else if (animation=="warp")
		{
			for (int i=0;i<it;i++)
			{
				s+=0.002*i;
				p=new Pantheon(q,r,s);
				p.stepiterate(bound);
				
				double[] screensize=p.screensize(scale,3);
				setZoom(screensize[2]);
				for(int j=0;j<2;j++)
				loc[j]=screensize[j];
				draw(p);
				print(name+df.format(i+start)+"."+type);
			}
		}
		else//"zoom"
		{
			k=(int)Math.log(factor);
			for(int i=0;i<it;i++)
			{
				if(factor>Math.exp(k))
				{
					p.iterateinScreen(factor,loc);
					k++;
				}
				draw(p);
				print(name+df.format(i)+"."+type);
				
				setZoom(zoom*1.01); 
				System.out.println(i);
			}
		}
		System.out.println("done.");
	}
	
	//Draw all the next circles from prog to end, return how many circles now exist (for iteration videos)
	private static int draw(Pantheon p, int prog) 
	{
		int out=p.circle.size();
		for(int k=prog;k<out;k++)
			draw(p.circle.get(k));
		
		return out;
	}

	//print the canvas as a file, taking the string as filename
	static void print(String string) 
	{
		File outputfile = new File(string);
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

	//draw the entire pantheon
	private static void draw(Pantheon p) 
	{
		for(int i=0;i<scale[0];i++)
			for(int j=0;j<scale[1];j++)
				canvas.setRGB(i,j,background);
		for(Circle circ: p.circle)
		{
			draw(circ);
			circ.print();
		}
	}

	//Draw a circle on the canvas, adding the curvature if "write"
	private static void draw(Circle circ) 
	{
		if(Pantheon.dist(loc, circ.loc)<1.5/zoom+1.0/Math.abs(circ.c))	//if the circle is even on screen
		{
			int col=circ.color;
			if(palette=="pastellprimes") col=color(circ.c);
		
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
						if(distance(circ.loc,(i-scale[0]/2)/factor+loc[0],(j-scale[1]/2)/factor+loc[1])>r2)
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
				Writing.write(canvas, ""+circ.c, x,y, size,col);
			}
		}
	}

	//euclidean distance between l and {d,e}
	static double distance(double[] l, double d, double e) 
	{
		return Math.pow(l[0]-d,2)+Math.pow(l[1]-e, 2);
	}

	//pastellprime coloring
	private static int color(int i) 
	{
		int j=i;
		int[]prime= {2,3,5};
		double[]col= {255.99,255.99,255.99};
		for(int k=0;k<3;k++)
		while(j%prime[k]==0&&col[k]>1)
		{
			col[k]*=0.8;
			j/=prime[k];
		}
		return new Color((int)col[0],(int)col[1],(int)col[2]).getRGB();
	}

	//set Zoom
	public static void setZoom(double z)
	{
		zoom=z;
		factor=scale[1]*zoom/2;
	}
	
	
	//Find the first k Primes(Brute Force, only needed once
 	public static int[] primes(int k)
	{
		int j=2;
		int[]out=new int[k];
		
		for(int i=0;i<k;i++)
		{
			int l=0;
			while(l<i)
			{
				if(j%out[l]==0)
				{
					j++; l=0;
				}
				else l++;
			}
			out[i]=j;
			j++;
		}
		return out;
	}
}
