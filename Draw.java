
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
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Draw 
{
	static DecimalFormat df=new DecimalFormat("0000");
	static int background=Color.black.getRGB();
	public static String name="nestedb",
			 type="png";
	static boolean write=false; //should the curvature be written on the circles?
	
	//different modes for the animation. Particular details need to be changed in the main method
	
	static String animation=//"warp",// warp the gasket
							//"it", //iterate the gasket 
							//"int",//Go through integer cases
							//"zoom", //zoom in
							"rise", //section of 3d-packing with increasing z
							//"size", //3d-packing changing what spheres are visible by radius
							//"build", //3d-packing, spheres appearing when center below an invisible moving plane
							//"rotate", //rotate the camera in 3d-packing
							//"nested", //nested Apollonian gasketes
				palette=//"gradient";
						"pastellprimes";
	static int[]scale= {1080,1080};//screen dimensions
	static int a=-1,b=2,c=2, //integer curvatures
			bound=5120, //how many iterations of the gasket // resp up to what curvature for "int"-animation
			dim=2,
			it=1000, start=10; //how many pictures are generated & where does there numbering start
	static ArrayList<int[]>panth=throuples(bound);
	static double[][]buffer,//=new double[scale[0]][scale[1]],
			matrix;
	static double q=-8,r=12,s=26,t=31;//continuous curvatures
	static double zoom=1,
			height=1/q, depth=-.053,
			factor=scale[1]*zoom/2,
			lower=20,upper=200;
	
	static double[]loc= {0,0},//{-1.0/a,1.0/a};
					vec= {0,-.817,.577};
	static int[] primes=new int[1];//for pastellprimes coloring
	
	static BufferedImage canvas=new BufferedImage(scale[0],scale[1],BufferedImage.TYPE_3BYTE_BGR);

	public static void main(String[] args) 
	{
		if(palette=="pastellprimes")
		primes=primes(100);
	
		Pantheon p=new Pantheon(a,b,c);
		if(dim==3) p=new Pantheon(q,r,s,t);
		int k=0,prog=0;
		
		if(animation=="it")
		for(int i=c;i<bound;i++)
		{
			if(dim==2)p.iterateinScreen(i,loc);
			else p.iterate3d(i,false);
			if(prog<p.circle.size())
			{
				double[] screensize=p.screensize(scale,5,height);
				setZoom(screensize[2]);
				for(int j=0;j<2;j++)
				loc[j]=screensize[j];
				draw(p);
				prog=draw(p,prog);
				print(name+df.format(k)+"."+type);
				k++;
			}
		}
		else if(animation=="nested")
		{
			panth=throuples(bound);
			p.iterate(bound);
			for(int i=3;i<200;i++)
			{
				panth=throuples(bound);
				nestedDraw(p,i);print(name+df.format(i)+"."+type);
			}
			
		}
		else if(animation=="rotate")
		{
			double alpha;
			p.iterate3d(bound,false);
			height=0;
			double[] screensize=p.screensize(scale,5,height);
				setZoom(screensize[2]);
				for(int j=0;j<2;j++)
				loc[j]=screensize[j];
			matrix=idmatrix(3);
			double beta=0;
			for(int i=0;i<it;i++)
			{
				alpha=(i+start)*Math.PI*2/1200;
				matrix[0][0]=Math.cos(alpha);matrix[0][2]=Math.sin(alpha);matrix[2][0]=-Math.sin(alpha);matrix[2][2]=Math.cos(alpha);
				draw3d(p,1);
				print(name+df.format(i+start)+"."+type);
			//	lower-=0.05;
			//	upper+=1;
				beta+=0.01;vec[0]=.817*Math.sin(beta);vec[1]=-.817*Math.cos(beta);
				depth+=0.001;
				/*s/=1.001;
				p=new Pantheon(q,r,s,t);
				p.iterate3d(bound,false);
				/*screensize=p.screensize(scale,5,height);
					setZoom(screensize[2]);
					for(int j=0;j<2;j++)
					loc[j]=screensize[j];*/
			}
		}
		else if(animation=="build")
		{
			p.iterate3d(bound,true);
			height=0;
			double[] screensize=p.screensize(scale,1,height);
				setZoom(screensize[2]);
				for(int j=0;j<2;j++)
				loc[j]=screensize[j];
			height=-1/q;
			for(int i=0;i<it;i++)
			{
				draw3d(p,2);
				depth-=0.0002;
				print(name+df.format(i+start)+"."+type);
			}
		}
		else if(animation=="size")
		{
			p.iterate3d(bound,true);
			height=0;
			double[] screensize=p.screensize(scale,1,height);
				setZoom(screensize[2]);
				for(int j=0;j<2;j++)
				loc[j]=screensize[j];
	
			for(int i=0;i<it;i++)
			{
				draw3d(p,1);
				lower*=1.005;
				upper*=1.005;
				print(name+df.format(i+start)+"."+type);
			}
		}
		else if(animation=="rise")
		{
			p.iterate3d(bound,true);
			height=0;
			double[] screensize=p.screensize(scale,5,height);
				setZoom(screensize[2]);
				for(int j=0;j<2;j++)
				loc[j]=screensize[j];
				height=1/q;
			for(int i=0;i<it;i++)
			{
				height+=0.0002;
				
				draw(p);
				
				print(name+df.format(i+start)+"."+type);
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
	/*(	for(int i=0;i<scale[0];i++) 
			for(int j=0;j<scale[1];j++)
				canvas.setRGB(i,j,SteinerPore.mix(background,canvas.getRGB(i,j),0.9));*/
		for(Circle circ: p.circle)
		{
			if(circ.c>-2)
			draw(circ);
		//	circ.print();
		}
	}
	//draw the entire pantheon
		private static void draw3d(Pantheon p, double k) 
		{
			for(int i=0;i<scale[0];i++) 
				for(int j=0;j<scale[1];j++)
				{
					canvas.setRGB(i,j,SteinerPore.mix(background,canvas.getRGB(i,j),0.9));
					buffer[i][j]=-100;
				}
			for(Circle circ: p.circle)
			{
				if(Math.abs((1/circ.r+0.5*k)%k-0.5*k)<0.5&&dot(circ.loc,vec)>depth)
				draw3d(circ);
				//circ.print();}
			}
		}
		
		//scalar product
	private static double dot(double[] v1, double[] v2) 
	{
		double out=0;
		for(int i=0;i<v1.length;i++)
		{
			out+=v1[i]*v2[i];
		}
		return out;
	}

	//Draw a circle on the canvas, adding the curvature if "write"
	private static void draw(Circle circ) 
	{
		double[] l=circ.loc;
		if(animation=="rotate")l=times(matrix,l);
		if(Pantheon.dist(loc, l)<1.5/zoom+1.0/Math.abs(circ.c))	//if the circle is even on screen
		{
			int col=circ.color;
			double r = 0, r2=0;
			if(palette=="pastellprimes") col=color(circ.c);
		
			if (circ.r==0)
			{
				for(int j=(int)((l[1]-loc[1])*factor);Math.abs(j)<scale[1]/2;j+=l[0])
				{
					for(int i=0;i<scale[0];i++)
					{
						canvas.setRGB(i,j+scale[1]/2,col);	
					}
				}
			}
			else if(circ.r<0)
			{
				r2=Math.pow(circ.r, 2);if(dim==3)r2-=Math.pow(l[2]-height, 2);
				r=Math.sqrt(r2);
				for(int i=0;i<scale[0];i++)
					for(int j=0;j<scale[1];j++)
						if(distance(l,(i-scale[0]/2)/factor+loc[0],(j-scale[1]/2)/factor+loc[1])>r2)
						{
							canvas.setRGB(i, j, col);
						}
	
			}
			else 
			{
				r2=Math.pow(circ.r, 2);if(dim==3) r2-=Math.pow(l[2]-height, 2);
				double s;
				if(r2>=0)
				{
				r=Math.sqrt(r2);
				for(int i=Math.max(-scale[0]/2,(int)(factor*(l[0]-loc[0]-r)));i<Math.min(scale[0]/2, factor*(l[0]-loc[0]+r));i++)
				{
					s=Math.sqrt(r2-Math.pow(l[0]-loc[0]-i/factor, 2));
					for(int j=Math.max(-scale[1]/2,(int)(factor*(l[1]-loc[1]-s)));j<Math.min(scale[1]/2,factor*(l[1]-loc[1]+s));j++)
						canvas.setRGB(i+scale[0]/2, j+scale[1]/2, col);
				}
				}
			}
			if(write&&r2>0)
			{
				int size=50,x,y;
				if (circ.r==0)
				{
					x=scale[0]/2-size/2;
					y=(int)(factor*(circ.loc[1]-loc[1]))-(1-(int)circ.loc[0])*size+scale[1]/2;
				}
				else
				{
					size=(int)Math.min(factor*Math.abs(r)*1.4/Math.max(2, Math.log10(Math.abs(circ.c))+1), 50);
					x=(int)(factor*(circ.loc[0]-loc[0]-Math.pow(2, -0.5)*r*Math.signum(circ.r)))+scale[0]/2;
					y=(int)(factor*(circ.loc[1]-loc[1]-Math.pow(2, -0.5)*r*Math.signum(circ.r)))+scale[1]/2;
					if(circ.c<0)x-=size;
				}
				Writing.write(canvas, ""+circ.c, x,y, size,col);
			}
		}
	}
	//Draw a circle on the canvas, adding the curvature if "write"
		private static void draw3d(Circle circ) 
		{
			double[] l=circ.loc;
			if(animation=="rotate")l=times(matrix,l);
			if(Pantheon.dist(loc, l)<1.5/zoom+1.0/Math.abs(circ.c))	//if the circle is even on screen
			{
				int col=circ.color;
				double r = 0, r2=0, t;
				if(palette=="pastellprimes") col=color(1/circ.r);
			

				if(circ.c>lower&&circ.c<upper)
				{
					r2=Math.pow(circ.r, 2);
					double s;
	
					{
					r=circ.r;
					for(int i=Math.max(-scale[0]/2,(int)(factor*(l[0]-loc[0]-r)));i<Math.min(scale[0]/2, factor*(l[0]-loc[0]+r));i++)
					{
						s=Math.sqrt(r2-Math.pow(l[0]-loc[0]-i/factor, 2));
						for(int j=Math.max(-scale[1]/2,(int)(factor*(l[1]-loc[1]-s)));j<Math.min(scale[1]/2,factor*(l[1]-loc[1]+s));j++)
						{
							t=Math.sqrt(s*s-Math.pow(l[1]-loc[1]-j/factor, 2));
							if(t+l[2]>buffer[i+scale[0]/2][j+scale[1]/2])
							{
							//	if(t+circ.loc[2]<1/q||)
								canvas.setRGB(i+scale[0]/2, j+scale[1]/2, mix(background,col,-(t+l[2]-1/q)*q/2));
								buffer[i+scale[0]/2][j+scale[1]/2]=t+l[2];
							}
						}
					}
					}
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
			col[k]*=0.7;
			j/=prime[k];
		}
		return new Color((int)col[0],(int)col[1],(int)col[2]).getRGB();
	}
	//pastellprime coloring
	private static int color(double r) 
	{
		int j=(int)Math.abs(r) ;
		int[]prime= {2,3,5};
		double[]col= {255.99,255.99,255.99};
		for(int k=0;k<3;k++)
		while(j%prime[k]==0&&col[k]>1)
		{
			col[k]*=0.8;
			j/=prime[k];
		}
		j=(int)Math.abs(r)+1;
		double[]col2= {255.99,255.99,255.99};
		for(int k=0;k<3;k++)
		while(j%prime[k]==0&&col2[k]>1)
		{
			col2[k]*=0.8;
			j/=prime[k];
		}
		return mix(new Color((int)col[0],(int)col[1],(int)col[2]).getRGB(),new Color((int)col2[0],(int)col2[1],(int)col2[2]).getRGB(),Math.abs(r%1));
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
 	
 	//interpolate between colors c2 & c1
	static int mix(int c2,int c1,  double t)
	{
		Color col1=new Color(c1), col2=new Color(c2);
		return new Color((int)(col1.getRed()*t+(1-t)*col2.getRed()),(int)(col1.getGreen()*t+(1-t)*col2.getGreen()),(int)(col1.getBlue()*t+(1-t)*col2.getBlue())).getRGB();
	}
	
	//matrix*vector
	static double[] times(double[][] matrix, double[] vec)
	{
		double[] out=new double[matrix.length];
		for(int i=0;i<matrix.length;i++)
			for(int j=0;j<vec.length;j++)
				out[i]+=matrix[i][j]*vec[j];
		return out;
	}
	
	static double[][] idmatrix(int n)
	{
		double[][]out=new double[n][n];
		
		for(int i=0;i<n;i++)
			out[i][i]=1;
		return out;
	}
	
	//Find throuples leading to all-integer gaskets
	static ArrayList<int[]>throuples(int bound)
	{
		double sqrt=1+2/Math.sqrt(3);
		ArrayList<int[]>out=new ArrayList<int[]>();
		for(int i=1;i<bound;i++)
		{
			
			for(int j=i+1;j<sqrt*i;j++)
			{
				int n=j-i;
				for(int k=(i*j+n-1)/n;k<=(i+j)*(i+j)/(4*n);k++)
				{
					int det=(int)Math.sqrt(j*k-i*(j+k));
					if(det*det==j*k-i*(j+k))
					{
						int[]entry= {-i,j,k};
						out.add(entry);
					}
				}
			}
		}
		return out;
	}
	
	//recursive method
	static void nestedDraw(Pantheon p, int rounds)
	{
		draw(p);
		if(rounds>0)
		for(Circle circ: p.circle)
			if(circ.c>0)
			{
				int i=0;
				while(i<panth.size()&&panth.get(i)[0]!=-circ.c)
				{i++;}
				if(i<panth.size())
				{
					int[]curv=panth.get(i);
					panth.remove(i);
					Pantheon p1=new Pantheon(curv[0],curv[1],curv[2]);
					for(int j=0;j<4;j++)
					{
						p1.circle.get(j).move(circ.loc);
					}
					p1.iterate(bound);
					System.out.println(rounds);
					nestedDraw(p1, rounds-1);
				}
			}
	}
}
