//X is always the maximum number of rows and Y is always the total number of columns
package main;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;
//An object of this class represents a single cell in the cell grid(main board) of the game.
public final class Cell implements java.io.Serializable 
{
	private static final long serialVersionUID = -34569901789L;
	private int count;//Holds the number of units in the cell.
	private final Point LOC;//Holds the location of the cell,indexed at 0.
	private final Position position;//Holds the position of the cell-edge/center/corner.
    private final int CRIT_MASS;//Holds the critical mass for the cell. 
    private Color owner;//Holds the current owner for the given cell.
    private Point MAX_COOR;//Holds maximum number of rows and columns the parents grid-indexed at 0
    //might have.
    private boolean exploded;//indicative of whether the cell was just the source to a explosion in the grid.
    //Used in conjunction with CellGrid.
    private static boolean allowFaultyInit;//indicates whether cell objects may be allowed a faulty initialization in an exception case(infinite explosion)
    //Constructor for an object of cell, initialized with position.
    static
    {
    	Cell.allowFaultyInit=Boolean.FALSE;
    }
    public Cell(Point pos,Point max)
    {
    	this.LOC=pos;
    	this.MAX_COOR=max;
    	this.position=this.calculatePosition();
    	this.CRIT_MASS=this.calculateCriticalMass();
    	this.owner=Color.BLACK;
    	this.count=0;
    }
    //Constructor for an object of Cell, initialized with position and owner
    public Cell(Point pos,Point max,int units,Color owner)
    {
    	this(pos,max);
    	BLOCK:
    	if((units>this.CRIT_MASS)||owner.equals(Color.WHITE))
    	{   
    		if(Cell.allowFaultyInit)
    		{
    			break BLOCK;
    		}
    		throw new IllegalArgumentException("Invalid Color or Unit initialization");
    	}
    	this.count=units;
    	this.owner=owner;
    }
    
    //GETTER-SETTER METHODS:
    
    //This method returns the number of units in this cell.
    public int getCount()
    {
    	return this.count;
    }
    //This method gets the position of the cell
    public Position getPosition()
    {
    	return this.position;
    }
    //This method returns the Point-location of the cell.
    public Point getLocation()
    {
    	return this.LOC;
    }
    //This method returns the critical mass of the given cell
    public int getCritMass()
    {
    	return this.CRIT_MASS;
    }
    
   //This method returns the current owner of the cell
    public Color getOwner()
    {
    	return this.owner;
    }
    //This method sets the owner of the cell
    public void setOwner(Color r)
    {
      if(r.equals(Color.WHITE))
      {throw new IllegalArgumentException("White is not a valid player color.");}
      this.owner=r;
    }
    //This method calculates the position for the given cell
    public Position calculatePosition()
    {
    	Point max=this.MAX_COOR;//Local variable for maximum co-ordinates.
    	Point cur=this.LOC;//Local variable for current co-ordinates.
    	final String[] corner= {"0 0","0 "+max.y,max.x+" 0",max.x+" "+max.y};
    	//Corner configurations.
    	for(String config:corner)
    	{
    		Scanner reader=new Scanner(config);
    		int x1=reader.nextInt();
    		int y1=reader.nextInt();
    		reader.close();
    		if((cur.x==x1)&&(cur.y==y1))
    		{
    			return Position.CORNER;
    		}
        }
    	
    	if((cur.x==0)||(cur.y==0)||(cur.x==max.x)||(cur.y==max.y))
    	{
    		return Position.EDGE;
    	}
    	else
    	{
    	return Position.CENTER;
    	}
    }
    
    //This method calculates the critical mass of a cell-read README for more details.
    public int calculateCriticalMass()
    {
    	Position curr=this.calculatePosition();
    	switch(curr)
    	{
    	case CORNER:
    		return 1;
    	case EDGE:
    		return 2;
    	case CENTER:
    		return 3;
    	default:
    		return 3;
    	}
    }
    
    //This method returns the cells adjacent to the invoking cell->Top,left,right,bottom
    public ArrayList<Cell> adjacentCells(Cell[][] grid)
    {
    	if((grid.length!=(this.MAX_COOR.x+1))||(grid[0].length!=(this.MAX_COOR.y+1)))
    	{throw new IllegalArgumentException("Invalid grid argument-does not match cell's max parent dimension specs");}
    	ArrayList<Cell> rets=new ArrayList<>(0);
    	int x=this.LOC.x;
    	int y=this.LOC.y;
    	int xm=this.MAX_COOR.x;
    	int ym=this.MAX_COOR.y;
    	Point top=new Point(x-1,y);
    	Point right=new Point(x,y+1);
    	Point bottom=new Point(x+1,y);
    	Point left=new Point(x,y-1);
    	Point[] points= new Point[] {top,right,bottom,left};
    	for(Point p:points)
    	{
    		if((p.x>=0)&&(p.y>=0)&&(p.x<=xm)&&(p.y<=ym))
    		{
    			Cell req=grid[p.x][p.y];
    			rets.add(req);
    		}
    	}
    	return rets;
    }
    

    
    //Adds a unit to the cell,causes an explosion if addition makes cell unstable
	//Returns true if operation is successful, false if operation fails die to any reason.
	//Can be a time and resource intensive task in case of larger chain reactions.
	@SuppressWarnings("unused")
	public boolean addUnit(Color pl,CellGrid grid)
	{
		
		if((!this.getOwner().equals(Color.BLACK))&&(!pl.equals(this.getOwner())))
		{return false;}
		this.setOwner(pl);
		this.count++;
		EXPLOSION:
		if(!this.isStable())
		{
			this.explode(grid);
		}
	    return true;
	}
	//This method marks or unmarks the invoking cell as an explosion source.
    public void markExplosionSource(boolean val)
    {
    	this.exploded=val;
    }
    //checks if the current source is an explosion source or not
    public boolean isExplosionSource()
    {
    	return this.exploded;
    }
    
    //Causes the cell to explode, possibly starting a chain reaction in the surrounding cells.
    public void explode(CellGrid gridM)
    {
    	Cell[][] grid=gridM.getGrid();
    	if(this.count<=this.CRIT_MASS)
    	{
    		throw new UnsupportedOperationException("Cannot explode a stable cell");
    	}
    	
    	Color owns=this.owner;//setting the owner for the current for the current reaction.
    	ArrayList<Cell> toExplode=new ArrayList<>(0);
    	toExplode.add(this);
    	this.markExplosionSource(true);//set the cell as an explosion source
    	
    	long then=System.currentTimeMillis();
    	long now=then;
    	for(;toExplode.size()!=0;)
    	{   
    		//System.out.println("Cell exploded");//for testing
    		//Cell.displayConfig(grid);//for testing
    		Cell op=toExplode.get(0);//Cell to be operated on.
    		if(op.isStable())
    		{
    			toExplode.remove(0);
    			continue;
    		}
    		op.decreaseUnits(op.CRIT_MASS+1);
    		//Current cell has been cleared
    		ArrayList<Cell> adjs=op.adjacentCells(grid);
    		for(Cell cc:adjs)
    		{
    		cc.count++;
    		cc.setOwner(owns);
    		if(!cc.isStable())
    		 {
    			toExplode.add(cc);
    		 }
    		}
    		toExplode.remove(0);
    		now=System.currentTimeMillis();
    		if((now-then)>4000)
    		{
    			boolean end=Cell.areSimilarOwners(grid);
    			if(end)
    			{   
    				gridM.messages.add("Cell Grid lockdown initiated!\n"
    						+ "All other players are eliminated and reaction is too lengthy!");
    				//Add cell grid lockdown notification here
    				//CellGrid.normalize(grid);//new normalization line.
    				return;
    			}
    			now=System.currentTimeMillis();
    			then=now;
    		}
    	}
    }
    //this method decreases a specific number of units from a cell,if c>=count of the cell, the cell is 
    //formatted.
    public void decreaseUnits(int c)
    {
    	if(this.count<=c)
    	{
    		this.format();
    	}
    	else
    	{
    	    this.count=this.count-c;
    	}
    }
    
    //This method clears out the current cell, emptying it.
    public void format()
    {
    	this.owner=Color.BLACK;
    	this.count=0;
    }
    
    //This method formats a cell to a certain number and color.
    //Use this function sparingly except for testing, given that it
    //bypasses the clicks field.
    public void format(int num,Color ow)
    {
    	if(num>this.CRIT_MASS||ow.equals(Color.WHITE))
    	{throw new IllegalArgumentException("Invalid Color or mass input.");}
     this.count=num;
     this.owner=ow;
    }
    //Checks if the said cell is stable or not-returns true if it is stable and 
    //false if not.
    public boolean isStable()
    {
    	if(this.count>this.CRIT_MASS)
    	{
    		return false;
    	}
    	return true;
    }
    //Checks if the given cell is at it's maximum capacity, returns true if the cell 
    //is at its critical mass, false in all other cases.
    public boolean isCritical()
    {
    	if(this.count==this.CRIT_MASS)
    	{
    		return true;
    	}
    	return false;
    }
    
    //Testing methods 
    
    //Displays the configuration of the provided grid of cells on the console.
    public static void displayConfig(Cell[][] table)
    {
    	for(int i=0;i<table.length;++i)
    	{
    		for(int j=0;j<table[0].length;++j)
    		{
    			System.out.print(table[i][j].getCount()+" "+table[i][j].getOwner().toString()+"   ");
    		}
    		System.out.println();
    	}
    }
    //Displays the configuration of an array-list of cells
    public static void displayList(ArrayList<Cell> lis)
    {
    	for(Cell c:lis)
    	{
    		System.out.println(Cell.cellInfo(c));
    	}
    }
    //Returns a String representing all state information w.r.t the cell
    public static String cellInfo(Cell os)
    {
      return "Owner "+os.getOwner()+" count= "+os.getCount()+ " crit mass= "+os.getCritMass()+" position= "+os.getPosition()+" location= "+os.getLocation();
    }
   //This method checks if a table of cells are all of the same color or have variations(black exluded)
    public static boolean areSimilarOwners(Cell[][] grid)
    {
    	int count=0;
    	Color owe=Color.BLACK;
    	for(int i=0;i<grid.length;++i)
    	{
    		for(int j=0;j<grid[0].length;++j)
    		{
    			Cell op=grid[i][j];
    			if((!op.getOwner().equals(owe))&&(!op.getOwner().equals(Color.BLACK)))
    			{
    			if(count==1)
    			{return false;}
    			owe=op.getOwner();
    			  ++count;
    			}
    		}
    	}
    	return true;
    }
    @Override()
    //This method clones the current cell object
    public Cell clone()
    {   
    	Cell obj=null;
    	Point cloc=new Point(this.LOC);
    	Point cmax=new Point(this.MAX_COOR);
    	try
    	{
    	obj=new Cell(cloc,cmax,this.count,this.owner);
    	}
    	catch(IllegalArgumentException ex)
    	{
    		Cell.allowFaultyInit=true;
    		obj=new Cell(cloc,cmax,this.count,this.owner);
    	    Cell.allowFaultyInit=false;
    	}
    	return obj;
    }
    
    //Clones a table of cells and returns the result.
    public static Cell[][] duplicateMultiple(Cell[][] igrid)
    {
    	Cell[][] grid= new Cell[igrid.length][igrid[0].length];
    	for(int i=0;i<igrid.length;++i)
    	{
    		for(int j=0;j<igrid[0].length;++j)
    		{
    			grid[i][j]=igrid[i][j].clone();
    		}
    	}
    	return grid;
    }
}
