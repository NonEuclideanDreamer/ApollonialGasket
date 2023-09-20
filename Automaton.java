//**************************************************************
//author: Non-Euclidean Dreamer
// Automaton.java
// running a smooth-valued Cellular Automaton on an Apollonial Gasket
//****************************************************************

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Automaton 
{
	public static 	double[][] a={ {0.8,0.1,0.0},{0,0.8,0.1},{0.1,0.0,0.8}},
			b= {{0.025,0.0,0.005},{0.005,0.025,0.0},{0,0.005,0.025}},
			c= {{0.007,0.004,0.003},{0.003,0.007,0.004},{0.004,0.003,0.007}};//rule parameters
	public static double x=1,y=1,z=1,vx,vy,vz;//The curvatures of the 3 main circles & their current rate of change
	static DecimalFormat df=new DecimalFormat("0000");
	static int background=Color.black.getRGB(),
			bound=10;//how many iterations deep is the gasket
	public static String name="autom",
			 type="png";
	static int[]scale= {1080,1080};
	public static double acc=0.000001;
	public Pantheon pan;
	public ArrayList<int[]>nb;//specify the 6 influental neighbours to each circle
	public static int t=0, it=10000;
	public double[][][] state;
	static double zoom=1,
			factor=scale[1]*zoom/2;
	static double[]loc= {0.25,0};//{-1.0/a,1.0/a};
	static BufferedImage canvas=new BufferedImage(scale[0],scale[1],BufferedImage.TYPE_3BYTE_BGR);

	public static void main(String[] args)
	{
		Automaton ca=new Automaton(x,y,z,bound);
		ca.radiusstate(0);//initial state of the ca dependent on current curvatures
		randomRule(0.999,0.001,0.0001);//randomized rule parameters
		for(t=0;t<it;t++)
		{	
			ca.warp();//update curvatures x,y,z from ca-state& reiterate Gasket
			double[] screensize=ca.pan.screensize(scale,5);//fit the screen to what we want to see
			setZoom(screensize[2]);
			for(int j=0;j<2;j++)
			loc[j]=screensize[j];
			
			//draw & create file
			ca.draw(); 
			print(name+df.format(t)+"."+type);
			
			//iterate ca
			ca.step();
		
			System.out.println(t);
		}
	}
	
	private static void setZoom(double z) 
	{
		zoom=z;
		factor=scale[1]*zoom/2;
	}

	//the current state of the 3 main circles determines the acceleration of their curvature
	private void warp() 
	{
		//change velocity
		vx+=(state[0][t%2][0]-0.5)/200;
		vy+=(state[1][t%2][1]-0.5)/200;
		vz+=(state[2][t%2][2]-0.5)/200;
		double norm =Math.sqrt(vx*vx+vy*vy+vz*vz)*10;//don't let it get too big
		if(norm>1)
		{
			vx/=norm;
			vy/=norm;
			vz/=norm;
		}
		//change curvatures
		x+=vx;
		y+=vy;
		z+=vz;
		
		if(x*(y+z)+y*z<0)//exlude forbidden combos( circles couldn't pairwise touch)
		{
			double a=(Math.sqrt(x*(x-y-z)+y*(y-z)+z*z)-x-y-z)/3+0.0001;
			
			x+=a;
			y+=a;
			z+=a;
		}
		
		norm=Math.sqrt(x*x+y*y+z*z)/2;//normalize
		x/=norm;
		y/=norm;
		z/=norm;
		System.out.println("{"+x+", "+y+", "+z+"}");

		//regenerate pantheon
		pan=new Pantheon(x,y,z);
		stepiterate(bound,false);
	}

	//starting state of ca depending on curvatures of circles
	private void radiusstate(int c) 
	{
		for(int i=0;i<nb.size();i++)
		{
			double out=pan.circle.get(i).r;
			out=out%1;
			out+=1;
			out=out%1;
			state[i][0][c]=out;
		}
	}
	
	//constructor for integer curvature pantheon ca
	public Automaton(int a, int b, int c, int depth) 
	{
		pan=new Pantheon(a,b,c);
		nb=new ArrayList<int[]>();
		nb.add(new int[] {1,2,3,-1,-1,-1});
		nb.add(new int[] {2,3,0,-1,-1,-1});
		nb.add(new int[] {3,0,1,-1,-1,-1});
		nb.add(new int[] {0,1,2,-1,-1,-1});

		iterate(depth);
	}
	
	//constructor for smooth pantheon with ca
	public Automaton(double a, double b, double c, int depth)
	{
		pan=new Pantheon(a,b,c);
		nb=new ArrayList<int[]>();
		nb.add(new int[] {1,2,3,-1,-1,-1});//-1 to be filled in when neighbours are created
		nb.add(new int[] {2,3,0,-1,-1,-1});
		nb.add(new int[] {3,0,1,-1,-1,-1});
		nb.add(new int[] {0,1,2,-1,-1,-1});

		stepiterate(depth,true);
		state=new double[nb.size()][2][3];
	}
	
	//itereate "depth" steps of iteration deep (we need the same circles to be constructed, however tiny they might be for the ca to be stablec,
	//if "init" is false only the pantheon is recreated, the nb is left as-is
	private void stepiterate(int depth,boolean init) 
	{
		Circle[] cand;
		Circle[] a=new Circle[4];
		for (int i=0;i<pantheonSize(depth)-4;i++)
		{
			int[]	ind=pan.quartet.get(0);	
			for(int k=0;k<4;k++)
			{
				a[k]=pan.circle.get(ind[k]);
			}
			double d=2*(1.0/a[0].r+1.0/a[1].r+1/a[3].r)-1/a[2].r;
			cand=a[1].touchingCircles(a[3], 1/d,Pantheon.mix(a[0].color,a[1].color,a[3].color));
			
			if(Pantheon.dist(cand[1].loc,a[2].loc)<acc||(cand[0].distance(a[0])<acc&&cand[1].distance(a[0])>acc)) pan.circle.add(cand[0]);
			else pan.circle.add(cand[1]);
			
			pan.quartet.remove(0);
			int l=pan.circle.size()-1;	
			if(init)
			{
				boolean due=true;int k=3;int[]nei=nb.get(ind[3]);
				while(due) 
				{
					if(nei[k]==-1)
					{
						nei[k]=l;
						due=false;
					}
					else k++;
				}
			}	
			pan.quartet.add(new int[] {ind[0],ind[1],ind[3],l});
			pan.quartet.add(new int[] {ind[0],ind[3],ind[1],l});
			pan.quartet.add(new int[] {ind[1],ind[3],ind[0],l});
			if(init)	nb.add(new int[] {ind[0],ind[1],ind[3],-1,-1,-1});
		}
	}

	//print an integer array to the terminal
	private void print(int[] m) 
	{
		System.out.print("{");
		for(int i=0;i<m.length;i++)
		{
			System.out.print(m[i]+", ");	
		}
		System.out.println("}");
	}

	//iterating the pantheon to iteration depth n
	private void iterate(int depth)
	{
		Circle[] cand;
		Circle[] a=new Circle[4];
		int i=0;
		while(i<pan.quartet.size())
		{
			int[]	ind=pan.quartet.get(i);	
			for(int k=0;k<4;k++)
			{
				a[k]=pan.circle.get(ind[k]);
			}
			int d=2*(a[0].c+a[1].c+a[3].c)-a[2].c;
			if(d<depth)
			{
				cand=a[1].touchingCircles(a[3], d,Pantheon.mix(a[0].color,a[1].color,a[3].color));
				
				if(cand[0].distance(a[2])>cand[1].distance(a[2])-acc&&cand[0].distance(a[1])<acc&&Pantheon.dist(cand[0].loc,a[2].loc)>acc)pan.circle.add(cand[0]);
				else pan.circle.add(cand[1]);
				
			pan.quartet.remove(i);
			int l=pan.circle.size()-1;	
			boolean due=true;int k=3;int[]nei=nb.get(ind[3]);
			while(due) 
			{
				if(nei[k]==-1)
				{
					nei[k]=l;
					due=false;
				}
				else k++;
			}
			
			pan.quartet.add(new int[] {ind[0],ind[1],ind[3],l});
			pan.quartet.add(new int[] {ind[0],ind[3],ind[1],l});
			pan.quartet.add(new int[] {ind[1],ind[3],ind[0],l});
			nb.add(new int[] {ind[0],ind[1],ind[3],-1,-1,-1});
			}
			else i++;
		}
		state=new double[nb.size()][2][3];
	}

	//calculatin the next ca time step
	public void step()
	{
		int which=t%2;//which of the two state copies is the current one
		
		//adding the rgb parametervalues from the neighbours. v0:the circle it self, v1; its three parents, v2: its 3 oldest offspring
		double[]v0=new double[3],v1=new double[3],v2=new double[3];
		
		for(int i=0;i<nb.size();i++)
		{
			double r=pan.circle.get(i).r;
			for(int j=0;j<3;j++)
			{
				v0[j]=state[i][which][j];
				v1[j]=0;
				v2[j]=0;
				for(int k=0;k<3;k++)
				{
					v1[j]+=state[nb.get(i)[k]][which][j]*pan.circle.get(nb.get(i)[k]).r/r;
					if(nb.get(i)[k+3]>-1)
					v2[j]+=state[nb.get(i)[k+3]][which][j]*pan.circle.get(nb.get(i)[k+3]).r/r;
				}
			}
			//change the rgb-states of the cell accordingly
			for(int j=0;j<3;j++)
			{
				state[i][1-which][j]=0;
				for(int k=0;k<3;k++)
				{
					state[i][1-which][j]+=a[j][k]*v0[k]+b[j][k]*v1[k]+c[j][k]*v2[k];
				}
				state[i][1-which][j]=(state[i][1-which][j]%1+1)%1;
			}
		}
		
	}
	
	//draw the pantheon onto the canvas
	private void draw() 
	{
		for(int i=0;i<scale[0];i++)
			for(int j=0;j<scale[1];j++)
				canvas.setRGB(i,j,background);
		for(int i=0;i<nb.size();i++)
		{
			draw(i);
		}
	}

	//draw circle nr k onto the canvas
	private void draw(int k) 
	{
		Circle circ=pan.circle.get(k);
		if(Pantheon.dist(loc, circ.loc)<1.5/zoom+1.0/Math.abs(circ.c))
		{
			int col=color(k);
		
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
		}
		/* write curvature...
		{
			int size=50,x,y;
			if (circ.c==0)
			{
				x=scale[0]/2-size/2;
				y=(int)(factor*(circ.loc[1]-loc[1]))-(1-(int)circ.loc[0])*size+scale[1]/2;
			}
			else
			{
				size=(int)Math.min(factor/Math.abs(circ.c)*1.4/Math.max(2, Math.log10(Math.abs(circ.c))+1), 50);
				x=(int)(factor*(circ.loc[0]-loc[0]-Math.pow(2, -0.5)/circ.c))+scale[0]/2;
				y=(int)(factor*(circ.loc[1]-loc[1]-Math.pow(2, -0.5)/circ.c))+scale[1]/2;
				if(circ.c<0)x-=size;
			}
		//	System.out.println("x="+x+", y="+y+", size="+size);
			Writing.write(canvas, ""+circ.c, x,y, size,col);
		}}*/
	}
	
	//print canvas to file named "string"
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
	
	//create a random rule: stab<1 should be big to mitigate strobing, ex&size should be small
	private static void randomRule(double stab, double ex, double size)
	{
		a=new double[][] {{stab,0,0},{0,stab,0},{0,0,stab}};
		b=new double[3][3];
		c=new double[3][3];
		double rem=1+ex-stab;
		Random rand=new Random();
		int i,j,k;
		while(rem>0)
		{
			i=rand.nextInt(3);
			j=rand.nextInt(3);
			k=rand.nextInt(3);
			if(k==0)
			{
				a[i][j]+=size;
				rem-=size/3;
			}
			else if(k==2)
			{
				b[i][j]+=size;
				rem-=size;
			}
			else
			{
				c[i][j]+=size/2;
				rem-=size/2;
			}
			
		}
		print(a);
		print(b);
		print(c);
	}

	//print a double matrix to the terminal
	private static void print(double[][] m)
	{
		System.out.print("{");
		for(int i=0;i<m.length;i++)
		{
			System.out.print("{");
			for(int j=0;j<m[i].length;j++)
			{
				System.out.print(m[i][j]+", ");
				
			}
			System.out.println("}");
		}
		System.out.println("}");

	}

	//calculates the color of a circle out of its rgb-states
	private  int color(int i) 
	{
		double[]st=state[i][t%2], col=new double[3];
		for(int k=0;k<3;k++)
		{
			col[k]=256*st[k]*2;
			if(col[k]>=256)col[k]=511.9999999999999999999999999-col[k];
		}
		return new Color((int)col[0],(int)col[1],(int)col[2]).getRGB();
	}
	
	//how many circles has a pantheon of iteration depth "steps"
	private int pantheonSize(int steps)
	{
		return 2+(int)Math.pow(3, steps);
	}
}

