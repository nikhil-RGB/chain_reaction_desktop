package frontend;
//Custom button class
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import main.Cell;
import main.Position;
public final class CellButton extends JButton 
{   

	private static final long serialVersionUID = -7839882172707126911L;
	public static double dtheta_crit=3;//angle variation for crit cells;
	private static double a1;//angle for orb 1
	private static double a2;//angle for orb 2
	private static double a3;//angle for orb 3
	
	private static double b1;//angle for critical orb 1
	private static double b2;//angle for critical orb 2
	private static double b3;//angle for critical orb 3
	//static initialization block
	static
	{
		a1=30;
		a2=150;
		a3=240;
		b1=a1;
		b2=a2;
		b3=a3;
	}
	//Standard constructor for CellButton
	public CellButton(String text)
    {
    	super(text);
    }
	//causes the unstable position information to be updated
	public static void move()
	{   
		//critical orbs movement.
		b1+=dtheta_crit;
		b2+=dtheta_crit;
		b3+=dtheta_crit;
	}
	//custom is critical method for this class
	public static boolean isCritical(String text, String enumval)
	{
		Position gg=Position.valueOf(enumval);
		int crit_mass=0;
		switch(gg)
		{
		case CORNER:
			crit_mass=1;
			break;
		case EDGE:
			crit_mass=2;
			break;
		case CENTER:
			crit_mass=3;
		}
		return text.equals(""+crit_mass);
	}
	//repaint method of superclass, overriden
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Scanner read=new Scanner(this.getActionCommand());
		read.next();
		read.next();
		boolean isCrit=isCritical(this.getText(),read.next());
		read.close();
	    int A=(this.getWidth()/2)-10;
		int B=(this.getHeight()/2)-15;
		int width=this.getWidth();
		int height=this.getHeight();
		Point center=new Point(width/2,height/2);
		int diameter=6;
		center.x-=diameter/2;
	    double t,t1,t2;
	    
	    
	    
	    if(!isCrit)
         { 
	     t=Math.toRadians(a1);
	     t1=Math.toRadians(a2);
	     t2=Math.toRadians(a3);
         }
         else
         {
        	 t=Math.toRadians(b1);
    	     t1=Math.toRadians(b2);
    	     t2=Math.toRadians(b3);
         }
	    
	    double[] x= {A*Math.cos(t),A*Math.cos(t1),A*Math.cos(t2),50,100};
	    double[] y= {B*Math.sin(t),B*Math.sin(t1),B*Math.sin(t2),50,100};
	    for(int k=0;k<3;++k)
	    {
	    	x[k]=x[k];
	    	y[k]=y[k];
	    }
	    Graphics2D g2d=(Graphics2D)g;
	    int num=Integer.parseInt(this.getText());
	    for(int k=0;k<num;++k)
	    {
	    int xc=(int)(x[k]+center.x);
	    int yc=(int)(y[k]+center.y);
	    g2d.setColor(Color.WHITE);
	    g2d.drawOval(xc,yc, diameter, diameter);
	    Ellipse2D.Double circle = new Ellipse2D.Double(xc,yc,diameter,diameter);
	    g2d.setColor(Color.BLACK);
	    g2d.fill(circle);
	    }
	    
	}
	@Override
	public void setEnabled(boolean is)
	{
		super.setEnabled(is);
		if(is)
		{
			dtheta_crit=3;
		}
		else
		{
			dtheta_crit=0;
		}
	}
    
}
