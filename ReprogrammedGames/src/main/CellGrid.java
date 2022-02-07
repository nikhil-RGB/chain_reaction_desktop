package main;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
//This class will manage input and output on the board
public final class CellGrid implements java.io.Serializable
{
//Start of class
private static final long serialVersionUID = -6283110063137524050L;
private Cell[][] grid;//The grid which consists of all the cells.
private ArrayList<Player> players;//A list of players currently in-game.
@SuppressWarnings("unused")
private int clicks;//A variable which tracks the number of moves which have been played so far
private int tracker;//A variable which tracks the current players' index in the players list.
public Player winner;//A variable which determines who the winner of the current grid is, and therefore also whether the game is over or not. 
public ArrayList<String> messages;//a variable which holds all the messages-skipped during cloning 
//IIB
{
this.messages=new ArrayList<>(0);	
}
//Constructor to initialize a CellGrid object with a Cell[][] and a player list
public CellGrid(Cell[][] table,ArrayList<Player> pls)
{
	this.grid=table;
	this.players=pls;
	this.clicks=0;
	this.tracker=0;
}
//Constructor to initialize a CellGrid object with an empty cells' table and a list of
//players.
public CellGrid(int xc,int yc,ArrayList<Player> pls)
{
Cell[][] table=new Cell[xc][yc];
for(int i=0;i<xc;++i)
 {
	for(int j=0;j<yc;++j)
	{
		table[i][j]=new Cell(new Point(i,j),new Point(xc-1,yc-1));
	}
 }
 this.grid=table;
 this.players=pls;
 this.clicks=0;
 this.tracker=0;
}
//function to check whether a player's color still exists in the board
public boolean playerCheck(Color r)
{
 Cell[][] gr=this.grid;//Local variable representation of member variable grid.
 for(int i=0;i<gr.length;++i)
 { 
	 for(int j=0;j<gr[0].length;++j)
	 {
		 Cell obs=gr[i][j];
		 if(obs.getOwner().equals(r))
		 {return true;}
	 }
 }
 return false;
}
//function to switch tracker focus to the next player.
public void nextPlayer()
{
	++this.tracker;
	if(this.tracker>=this.players.size())
	{
		this.tracker=0;
	}
}

//function to eliminate players and return eliminated players in a list
public ArrayList<Player> eliminatePlayers()
{
ArrayList<Player> elims=new ArrayList<>(0);	
for(Player cur:this.players)
{
Color c=cur.getColor();
if(!this.playerCheck(c))
 {
  	elims.add(cur);
 }
}
this.players.removeAll(elims);
return elims;
}
//This method checks for a winner and returns the color of the winner if a player won, black in all
//other cases.
public Player getWinner()
{
 if((this.players.size()!=1)||(this.clicks<=this.players.size()))
 {
return null;	
 }
 else
 {
 return this.players.get(0);	
 }
}

//This method returns the cell DDA associated with the current CellGrid object
public Cell[][] getGrid()
{return this.grid;}

//Handles the input situation in the code-i.e a click at a certain button in-game.
public boolean clickAt(Point p,JComponent board)
{
	messages.clear();
	if(this.winner!=null)
	{
		JOptionPane.showMessageDialog(board, "The game is already over, the match is decided!");
		return false;
	}
	Cell og=this.grid[p.x][p.y];
	Color or=this.players.get(this.tracker).getColor();
	boolean flag=og.addUnit(or, this);
	if(flag)
	{
		++this.clicks;
		if(og.isExplosionSource())
		{
			
			ArrayList<Player> outs=this.eliminatePlayers();
			if(outs.size()!=0)
			{
				String all="Player(s) ";
				for(Player pp:outs)
				{
					all+=pp.getName()+" ";
				}
				all+=" was/were eliminated";
			messages.add(all);
			this.winner=this.getWinner();
			if(winner!=null)
			{messages.add("Player "+this.winner.getName()+" won!");}
			}
			og.markExplosionSource(false);
		}
		
		this.nextPlayer();
	}
	else
	{
		messages.add("Cell taken,try another cell!");
	}
	return flag;
}
//This method normalized a particular cell DDA when its cells are above the stable mass and further explosions are not possible
public static void normalize(Cell[][] grid)
{
for(int i=0;i<grid.length;++i)
{
 for(int j=0;j<grid[0].length;++j)
 {
	 Cell cur=grid[i][j];
	 if(!cur.isStable())
	 {
		 cur.format(cur.getCritMass(),cur.getOwner());
	 }
 }
}
}
//This method returns the list of players in game
public ArrayList<Player> getPlayers()
{
return this.players;	
}
//this method gives the programmer access to the tracker for players
public int getTracker()
{
return this.tracker;	
}
//This method duplicates and returns a copy of the invoking cell board.
@Override
public CellGrid clone()
{
ArrayList<Player> apn=Player.duplicateList(this.players);
CellGrid obj=new CellGrid(Cell.duplicateMultiple(this.grid),apn);	
obj.clicks=this.clicks;
obj.tracker=this.tracker;
obj.winner=this.winner;
return obj;
}

//This method gets the number of clicks played uptil a certain point.
public int getClicks()
{
	return this.clicks;
}
//This method returns the current player 
public Player getPlayer()
{
Player p=this.players.get(this.tracker);
return p;
}
//This method returns the next Player's player object representation
//without actually moving to the next player
public Player getNextPlayer()
{
	int ntracker=this.getTracker();
	++ntracker;
	if(ntracker>=this.players.size())
	{
	 ntracker=0;
	}
	return this.players.get(ntracker);
}
//This method returns the previous player's object representation
//without actualy shifting control to the previous player
public Player getPreviousPlayer()
{
int ntracker=this.getTracker();
--ntracker;
if(ntracker<0)
{
ntracker=this.players.size()-1;	
}
return this.players.get(ntracker);
}

//end of class
}
