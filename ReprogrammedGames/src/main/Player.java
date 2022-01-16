package main;
import java.awt.Color;
import java.util.ArrayList;
//An object of this class represents a player- with a name and a color.
public final class Player implements java.io.Serializable
{
 
	private static final long serialVersionUID = -6804966948382371048L;
//Start of class.
	private Color col;
	private String name;
	//Constructor for a player with a specific color and name.
	public Player(String name,Color col)
	{
		this.name=name;
		this.col=col;
	}
	//Returns color owned by current player
	public Color getColor()
	{
		return this.col;
	}
	//returns name of current player
	public String getName() 
	{
		return this.name;
	}
	//returns a list of players with said colors and names
	public static ArrayList<Player> createPlayerList(Color[] r,String[] names)
	{
		ArrayList<Player> pls=new ArrayList<>(0);
		if(r.length!=names.length)
		{
			throw new IllegalArgumentException("Invalid function parameters");
		}
		for(int k=0;k<r.length;++k)
		{
			Player p=new Player(names[k],r[k]);
			pls.add(p);
		}
		return pls;
	}
	//clones a Player object
	@Override()
	public Player clone()
	{
	return new Player(this.name,this.col);	
	}
	//Duplicates a list of players 
	public static ArrayList<Player> duplicateList(ArrayList<Player> pls)
	{
	 ArrayList<Player> pl=new ArrayList<Player>(0);
	 for(Player p:pls)
	 {
		 pl.add(p);
	 }
	 return pl;
	}
	//this method creates a list of players from individual name and Color lists
    public static ArrayList<Player> createArrayList(ArrayList<Color> cols,ArrayList<String> names)	
    {
    	ArrayList<Player> pps=new ArrayList<>(0);
    	if(cols.size()!=names.size())
    	{
    		throw new IllegalArgumentException("List of colors and names must be of same size");
    	}
    	for(int k=0;k<cols.size();++k)
    	{
    		Color col=cols.get(k);
    		String nam=names.get(k);
    		Player pp=new Player(nam,col);
    		pps.add(pp);
    	}
    	return pps;
    }
    //End of class
}
