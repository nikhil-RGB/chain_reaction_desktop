package main;
import frontend.*;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class FunctionTester 
{
    //All testing takes place here.
	public static void main(String[] args) 
	{
	// Cell[][] mainG=createTestGrid(3,3);
	//success-misMethodsTest(mainG);
	//success- adjacentTest(mainG);
	//success-explosionTest();
    //success- FunctionTester.infiniteExplosion();
	//success-	FunctionTester.cellMtest1();
	//success-	FunctionTester.cellMtest2();
	//success-	FunctionTester.simulateGameLaunch(3, 3);
	FunctionTester.runGame(9, 6);
	}
	//Creates a test grid for testing all functions out
	public static Cell[][] createTestGrid(int x,int y)
	{
		Cell[][] grid=new Cell[x][y];
	    for(int i=0;i<x;++i)
	    {
	    	for(int j=0;j<y;++j)
	    	
	    	{
	    		Cell cc=new Cell(new Point(i,j),new Point(x-1,y-1));
	    		grid[i][j]=cc;
	    	}
	    }
	    return grid;
	}
	//tests miscellaneous calculation functions.
	public static void misMethodsTest(Cell[][] table)
	{
		Cell c1=table[0][1];
		Cell c2=table[0][0];
		Cell c3=table[1][1];
		System.out.println("EDGE: "+Cell.cellInfo(c1));
		System.out.println("CORNER: "+Cell.cellInfo(c2));
		System.out.println("CENTER: "+Cell.cellInfo(c3));
	} 
	//tests adjacent cells function
	public static void adjacentTest(Cell[][] table)
	{
		Cell c1=table[0][1];
		Cell c2=table[0][0];
		Cell c3=table[1][1];
		ArrayList<Cell> edge=c1.adjacentCells(table);
		ArrayList<Cell> corner=c2.adjacentCells(table);
		ArrayList<Cell> center=c3.adjacentCells(table);
		System.out.println("EDGE CELLS: ");
		Cell.displayList(edge);
		System.out.println("CENTER CELLS: ");
		Cell.displayList(center);
		System.out.println("CORNER CELLS: ");
		Cell.displayList(corner);
	}
	//Tests explosion mechanic-test deprecated.
	public static void explosionTest()
	{
		Cell[][] mainG=FunctionTester.createTestGrid(3, 3);
		mainG[1][1].format(3,Color.RED);
		mainG[1][0].format(1,Color.BLUE);
		System.out.println("before explosion");
		Cell.displayConfig(mainG);
		//mainG[1][1].addUnit(Color.RED,new CellGrid(mainG));
		System.out.println("After Explosion: ");
	    Cell.displayConfig(mainG);
	}
	//Test for infinite explosions-test deprecated-mechanic altered
	public static void infiniteExplosion()
	{
		Cell[][] mainG=FunctionTester.createTestGrid(3, 3);
		for(int i=0;i<mainG.length;++i)
		{
			for(int j=0;j<mainG[0].length;++j)
			{
				Cell g=mainG[i][j];
				g.format(g.getCritMass(),Color.RED);
			}
		}
		System.out.println("Before explosion");
		Cell.displayConfig(mainG);
		//mainG[0][0].addUnit(Color.RED,mainG);
		System.out.println("After explosion");
		Cell.displayConfig(mainG);
	}
	//This method checks for the working of similarOwner function of the cell class.
	public static void cellMtest1()
	{
		

		Cell[][] mainG=FunctionTester.createTestGrid(3, 3);
		for(int i=0;i<mainG.length;++i)
		{
			for(int j=0;j<mainG[0].length;++j)
			{
				Cell g=mainG[i][j];
				g.format(g.getCritMass(),Color.RED);
			}
		}
		mainG[0][0].format(0,Color.BLACK);
		System.out.println("Similar colours: "+Cell.areSimilarOwners(mainG));
	}
	//This method checks for functioning of similarOwner function of cell class, but checks for a false value.
	public static void cellMtest2()
	{
		Cell[][] mainG=FunctionTester.createTestGrid(3, 3);
		for(int i=0;i<mainG.length;++i)
		{
			for(int j=0;j<mainG[0].length;++j)
			{
				Cell g=mainG[i][j];
				g.format(g.getCritMass(),Color.RED);
			}
		}
		mainG[0][0].format(1,Color.BLUE);
		System.out.println("Similar colours: "+Cell.areSimilarOwners(mainG));
		
	}
	
	//This method simulates an entire game cycle of chain reaction, without the GUI
	public static void simulateGameLaunch(int x,int y)
	{
		Color[] cols= {Color.RED,Color.GREEN};
		String[] names=new String[cols.length];
		for(int k=0;k<names.length;++k)
		{
			names[k]="Player "+k;
		}
		ArrayList<Player> ars=Player.createPlayerList(cols,names);
		CellGrid cg=new CellGrid(x,y,ars);
		Cell.displayConfig(cg.getGrid());
		Scanner sc=new Scanner(System.in);
		while(cg.getWinner()==null)//can be switched with cg.getWinner()
	    {
			System.out.println("Enter coordinates to try: ");
			int x1=sc.nextInt();
			int y1=sc.nextInt();
			cg.clickAt(new Point(x1, y1),null);
			Cell.displayConfig(cg.getGrid());
	    }
	    sc.close();
	}
	public static void runGame(int x,int y)
	{
		Color[] cols= {Color.RED,Color.BLUE};
		String[] names=new String[cols.length];
		for(int k=0;k<names.length;++k)
		{
			names[k]="Player "+k;
		}
		ArrayList<Player> ars=Player.createPlayerList(cols,names);
		CellGrid cg=new CellGrid(x,y,ars);
		SwingUtilities.invokeLater(new Runnable(){
		public void run()
		{
		LauncherWindow lw=new LauncherWindow();
		GamePanel gp=new GamePanel(lw,cg);
		gp.init();
		lw.initGui(gp, x, y, ars);
		
		lw.setSize(900,900);
		lw.setVisible(true);
		lw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		});
		
	}

}
