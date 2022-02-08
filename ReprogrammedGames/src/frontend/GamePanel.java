package frontend;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.Border;
import main.Cell;
import main.CellGrid;
import main.Player;
//An object of this class represents a GUI board of cells
//It is only fully initialized when it's init method returns. It must be noted, however that 
//it cannot be initialized until it's parent frame's gui is initialized
public final class GamePanel extends JPanel 
{
	
	public static final  RoundedBorder normalB;//Holds the border for a cell when it is "normal"
	public static final  Border critB;//holds the border for a cell when it is in it's critical state.
	private static final long serialVersionUID = -3919707624819805979L;//serial version UID
	private LauncherWindow parent;//Holds the top-level window which contains this game panel.
	private volatile CellGrid gdata;//Holds the CellGrid object associated with this GamePanel.
	public final CellButton[][] buttons;//this field holds the buttons associated with the current GamePanel
	private boolean isReady;//this field states whether the Board is initialized yet
	private ArrayList<GamePanel.Move> moves;//List of recent moves played
	private CellGrid backup;//n-moves prior board backup object
	private  volatile boolean aiThinking;//specifies if any ai player is processing data at any point in the game, effctively pausing all action listeners at that point
	//private static String message="The AI is thinking, please let the AI player player their move";//message to be displayed if player attempts to make a move when the ai is thinking
	static
	{
		normalB=new RoundedBorder(6);
		critB=BorderFactory.createTitledBorder("MAX");
	}
	//IIB 
	{
		this.moves=new ArrayList<>(0);
	}
	//Constructor
	public GamePanel(LauncherWindow lw,CellGrid cg)
	{   
		super(new GridLayout(cg.getGrid().length,cg.getGrid()[0].length,4,4));
		this.gdata=cg;
		Cell[][] gg=cg.getGrid();
	    this. buttons=new CellButton[gg.length][gg[0].length];
	    this.isReady=false;
	    this.parent=lw;
	    this.backup=cg.clone();
	}
	//This method finishes the initialization process of a GamePanel object.
	public void init()
	{
		ActionListener undo= (ev)->
		{
			//if AI thinking process is going on, prevents listener from being executed.
			if(this.aiThinking)
			{
				//JOptionPane.showMessageDialog(null, message);
				return;
			}
		 
		 
		 if(this.moves.isEmpty())
		 {
			 JOptionPane.showMessageDialog(this,"Cannot perform undo operation immedietly after"
			 		+ "\na player was eliminated, or a game cycle\nhas been completed."
			 		+ "\nPS: AI player moves cannot be undone.");
		 return;
		 }
		 
		 boolean no_undo=this.gdata.getPreviousPlayer().isAIcontrolled();
		 if(no_undo)
		 {
			 this.moves.clear();
			 this.backup=this.gdata.clone();
			 JOptionPane.showMessageDialog(this,"AI player moves cannot be undone.");
		 return;
		 }
		 
		 int typem=this.moves.get(this.moves.size()-1).getType();
		 //this is the type of the move just revoked,0 or 1.
		 this.moves.remove(this.moves.size()-1);
		 CellGrid temp=this.backup.clone();
		  for(Move p:this.moves)
		  {
			  Point poe=p.getPos();
			  this.backup.clickAt(poe,this);
		  }
		  this.gdata=this.backup;
		  this.backup=temp;
		  this.synchronizeFrontend(true);
		  //These lines are commented out as there is about to be code
		  //which will clear out the lines(unnecessary) from the text areas
		  //this.parent.display.append("\nOne Undo performed");
		  //this.parent.log.append("\nOne Undo performed");
		  
		  //code to clear out logs on undo-start
	      GamePanel.trimLogByLines(this.parent.display,2);	  
          GamePanel.trimLogByLines(this.parent.log,2);
          if(typem==1)
          {
        	  GamePanel.trimLogByLines(this.parent.log,2);
          }
		  this.parent.counter.setText("Turns played: "+this.gdata.getClicks());
		};
		
		this.parent.undo.addActionListener(undo);
		
		
		ActionListener ar=(ev)->
		{   
			if(this.aiThinking)
			{
				//JOptionPane.showMessageDialog(null, message);
				return;
			}
			this.gdata.messages.clear();
			String amd=ev.getActionCommand();
			Scanner reader=new Scanner(amd);
			int x1=reader.nextInt();
			int y1=reader.nextInt();
			reader.close();
			Point p=new Point(x1,y1);
			CellGrid gg=this.gdata;
		    Runnable obj=()->
			{
			int original=gg.getGrid()[x1][y1].getCount();//original content of cell
			int ogpls=gg.getPlayers().size(); //Old list of size of players
			int trackerol=gg.getTracker();//Old tracker
			Player oldp=gg.getPlayers().get(gg.getTracker());//This is the player who made the click
			this.setEnabledButtons(false);
			this.parent.undo.setEnabled(false);
			boolean success= gg.clickAt(p, this);
			
			if(!success)
			{
			if(this.gdata.messages.size()!=0)	
			{JOptionPane.showMessageDialog(this,this.gdata.messages.get(0));}
			this.setEnabledButtons(true);
			this.parent.undo.setEnabled(true);
			return;
			}
			Move move=new Move(p,0);
			moves.add(move);//added current click location to recent moves
			JLabel jl=this.parent.counter;
			JTextArea dis=this.parent.display;
			JTextArea toEdit=this.parent.log;
			jl.setText("Turns Played: "+gg.getClicks());
			boolean cl_dis=trackerol==0;
			if(cl_dis) 
			{
				this.backup=gg.clone();//resets the backup grid to the current grid state
				this.moves.clear();//clears recent moves log
			    dis.setText("Player Cycle: "+(1+(gg.getClicks()/gg.getPlayers().size())));//clears display	
			}
			//Code to be executed if move is successful, before synchronization
            Player cc=this.gdata.getPlayers().get(this.gdata.getTracker());
            dis.setForeground(cc.getColor());
            toEdit.setForeground(cc.getColor());
		    this.setBackground(cc.getColor());
			int naya=gg.getGrid()[x1][y1].getCount();//new number of units in the grid cell.
			int npls=gg.getPlayers().size();//New list of players
			
			
			
			dis.append("\n"+oldp.getName()+ " clicked at:\n Row "+(p.x+1)+
					" Column "+(p.y+1));
			
			toEdit.append("\n"+oldp.getName()+ " clicked at:\n Row "+(p.x+1)+
					" Column "+(p.y+1));
			
			if(naya<=original)
			{   
				move.setType(1);
				toEdit.append("\n"+oldp.getName()+ " caused a reaction starting at:\n Row "+(p.x+1)+
					" Column "+(p.y+1));
				if(npls<ogpls)
				{
					toEdit.append("\n"+(ogpls-npls)+" player(s) was/were eliminated");
					this.moves.clear();//clears recent moves log
					this.backup=gg.clone();//resets backup
				}
				this.synchronizeFrontend(false);
			}
			else
			{
				this.synchronizeAt(x1, y1);
			}
			
			for(int k=0;k<this.gdata.messages.size();++k)
			{
				String mssg=this.gdata.messages.get(k);
				JOptionPane.showMessageDialog(this,mssg);
			}
			
			boolean noWin=this.gdata.winner==null;//true if game is ongoing,false if game ended
			
				this.parent.undo.setEnabled(noWin);
				this.setEnabledButtons(noWin);
			
			this.gdata.messages.clear();
			
			//add in ai player move check-cc is the player whose chance it is
			Point clckAt=this.simulateNextMove(cc);
			if(clckAt!=null)
			{
			this.aiThinking=false;
			this.buttons[clckAt.x][clckAt.y].doClick();
			this.moves.clear();//clears recent moves log
			this.backup=this.gdata.clone();//resets backup to current board state
			}
			//end of ai interference.
			
			};
			new Thread(obj).start();
		};
		Cell[][] ops=this.gdata.getGrid();
		for(int i=0;i<ops.length;++i)
		{
			for(int j=0;j<ops[0].length;++j)
			{
				Cell og=ops[i][j];//Cell to be copied into the board.
				CellButton jb=new CellButton(og.getCount()+"");
				jb.setBackground(og.getOwner());
				jb.setForeground(Color.WHITE);
				jb.setFont(new Font("SansSerif",Font.BOLD,18));
				jb.setActionCommand(i+" "+j+" "+og.getPosition());
				if(!og.isCritical())
				{jb.setBorder(new RoundedBorder(6));}
				else
				{jb.setBorder(critB);}
				jb.addActionListener(ar);
				jb.setToolTipText("<html>Click here to place a unit down in this cell<br>"
						+"if it's blank or belongs to you");
				this.add(jb);
				this.buttons[i][j]=jb;
			}
		}
	Color curr=this.gdata.getPlayer().getColor();
	this.setBackground(curr);
	JMenuItem[] saves=this.saveMenuItems();
	JMenu jm=new JMenu("Save/Load game");
	jm.add(saves[0]);
	jm.add(saves[1]);
	this.parent.getJMenuBar().add(jm);
	this.isReady=true;
	}

  //This method synchronizes the back-end data with the front end GUI
	public void synchronizeFrontend(boolean bckreset)
	{
		if(!isReady)
		{
			throw new UnsupportedOperationException("Object is not correctly initialized");
		}
	Cell[][] tosync=this.gdata.getGrid();
	for(int i=0;i<tosync.length;++i)
	{
		for(int j=0;j<tosync[0].length;++j)
		{
		Cell cc=tosync[i][j];
	    CellButton jb=this.buttons[i][j];
		jb.setBackground(cc.getOwner());
		jb.setText(cc.getCount()+"");
		if(cc.isCritical()||(!cc.isStable()))
		{
		jb.setBorder(critB);
		}
		else
		{
		jb.setBorder(normalB);
		}
		
		}
		
		
	}
	if(bckreset)
	{
		Color r=this.gdata.getPlayer().getColor();
		this.setBackground(r);
		this.parent.display.setForeground(r);
		this.parent.log.setForeground(r);
	}
	}
//This method synchronizes only the cell  at a specific point.
	public void synchronizeAt(int x,int y)
	{
	if(!isReady)
	{
		throw new UnsupportedOperationException("Cell incorrectly initialized");
	}
	Cell op=this.gdata.getGrid()[x][y];
	//System.out.println(op+" for cell");//test
	CellButton og=this.buttons[x][y];
	//System.out.println(og+" for button");//test
	og.setBackground(op.getOwner());
	og.setText(""+op.getCount());
	if(op.isCritical())
	{
		og.setBorder(critB);
	}
	}
//This method gets the cell grid associated with this game panel
	public CellGrid getGrid()
	{
		return this.gdata;
	}
//This method allows one to set the cell grid.
	public void setGrid(CellGrid gr)
	{
		this.gdata=gr;
	}
//This method allows one to access the backup grid
	public CellGrid getBackup()
	{
		return this.backup;
	}
//This method allows one to change the backup
	public void setBackup(CellGrid obj)
	{
		this.backup=obj;
	}
//This method enables/disables all the buttons in the grid- useful in cases when the expliosion takes more
//than one second.
	public void setEnabledButtons(boolean on)
	{
		for(int i=0;i<buttons.length;++i)
		{
			for(int j=0;j<buttons[0].length;++j)
			{
				buttons[i][j].setEnabled(on);
			}
		}
	}
	//An object of this class represents a move played, information includes location of click, and the 
	//type of interaction which has taken place(Explosion, click)
	public static class Move implements java.io.Serializable
	{
	private static final long serialVersionUID = 305209131918314130L;
	//start of inner class
		//op=0 means the move involved a click, op=1 means the move invloved an explosion
		private Point clck;//location of click
		private int type;//type of interaction which has occured
		//constructor
		public Move(Point p,int op)
		{
			this.clck=p;
			this.type=op;
		}
		//constructor with lesser parameters
		public Move(Point cl)
		{
			this.clck=cl;
		}
		//Returns position for click
		public Point getPos()
		{
			return this.clck;
		}
		//This method gets the type of the current move i.e explosion or click
		public int getType()
		{
			return this.type;
		}
		//This method sets the type of the move, to 0 or 1 
		//0 means the move is a click
		//1 means the move is an explosion
		public void setType(int ty)
		{
			this.type=ty;
		}
		
	}//end of inner class
	
	//This method trims the lines in a text field by a certain number,
	//specified by lines.
	public static void trimLogByLines(JTextArea log,int lines)
	{
	String[] sens=log.getText().split("\n");
	String[] nsens=new String[sens.length-lines];
	for(int k=0;k<nsens.length;++k)
	{
		nsens[k]=sens[k];
	}
	String text=String.join("\n", nsens);
	log.setText(text);
	}
	
	//This method saves the current game to a file f, it should exist
	public void saveGame(File f)
	{   
		
		SaveItem si=new SaveItem();
		si.init(this.gdata, this.backup, this.moves,this.parent.display.getText(),this.parent.log.getText(),this.parent.counter.getText());
	    boolean flag=si.writeTo(f);
	    if(!flag)
	    {
	    	JOptionPane.showMessageDialog(null,"There was an error while saving your game.\n Please try again","Error",JOptionPane.ERROR_MESSAGE);
	        
	    }
	}
	public void loadGame(File f)
	{
		SaveItem si=SaveItem.readFrom(f);
		if(si==null)
		{
			JOptionPane.showMessageDialog(null,"There was an error while loading your game.\n Please try again","Error",JOptionPane.ERROR_MESSAGE);
		    return;
		}
		//Code to start up loaded game.
		this.parent.setVisible(false);
	    this.parent.dispose();
	    SwingUtilities.invokeLater(()->{
			LauncherWindow jfr=new LauncherWindow("Chain Reaction!");
			LauncherWindow.setIconTo(jfr);
			GamePanel gp=new GamePanel(jfr,si.main_grid);
			jfr.initGui(gp,si.main_grid.getGrid().length,si.main_grid.getGrid()[0].length,si.main_grid.getPlayers());
			gp.init();
			gp.moves=si.set;
			gp.backup=si.bckup;
			jfr.display.setText(si.display);
			jfr.log.setText(si.log);
			jfr.counter.setText(si.counter);
			jfr.setVisible(true);
			GamePanel.OrbsAnimation rn=gp.new OrbsAnimation();
			rn.start();
			
			//add in ai player move check-cc is the player whose chance it is
			Point clckAt=gp.simulateNextMove(gp.gdata.getPlayer());
			if(clckAt!=null)
			{
			gp.aiThinking=false;
			gp.buttons[clckAt.x][clckAt.y].doClick();
			gp.moves.clear();//clears recent moves log
			gp.backup=gp.gdata.clone();//resets backup to current board state
			}
			//end of ai interference.
			
			
			
			f.delete();
		    });
	}
	//This method creates the save/load menu options
	public JMenuItem[] saveMenuItems()
	{
		
	    JMenuItem save=new JMenuItem("Save");
	    JMenuItem load=new JMenuItem("Load");
	    ActionListener dialog=(ev)->
	    {
	    if(this.gdata.winner!=null)
	    {
	    	JOptionPane.showMessageDialog(null,"Save/load disabled once game ends", "Error", JOptionPane.ERROR_MESSAGE,null);
	    	return;
	    }
	     JFileChooser jfc=new JFileChooser();
	     int opn=jfc.showDialog(null,ev.getActionCommand());
	     if(opn!=JFileChooser.APPROVE_OPTION)
	     {return;}
	     File f=jfc.getSelectedFile();
	     Object src=ev.getSource();
	     if(src==save)
	     {
	    	 this.saveGame(f);
	     }
	     else
	     {
	    	 this.loadGame(f);
	     }
	    };
	    save.addActionListener(dialog);
	    load.addActionListener(dialog);
	    JMenuItem[] jms= {save,load};
	return jms;
	}
	//This thread animates the orbs in the cells
	public class OrbsAnimation extends Thread
	{
		@Override
		public void run()
		{   
			try 
			{
			Thread.sleep(250);
			}
			catch (InterruptedException e1) 
			{
				e1.printStackTrace();
			}
			while(gdata.winner==null)
			{
				CellButton.move();
				repaint();
				Toolkit.getDefaultToolkit().sync();
				try
				{
					Thread.sleep(70);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
					//Do Nothing
				}
			}
		}
	}
	
	//This method simulates the next move for an ai opponent  
	public Point simulateNextMove(Player r)
	{
	
	if((!r.isAIcontrolled())||(this.gdata.winner!=null))
	{return null;}
	this.aiThinking=true;//pauses all action listeners in game
	int x=0;
	int y=0;
	//Temp code
	ArrayList<Cell> ars=this.immediateExplodable(r);
	if(ars.size()!=0)
	{
		Cell c=ars.get(0);
		x=c.getLocation().x;
		y=c.getLocation().y;
	}
	else
	{
		Cell c=this.placeRandom(r);
		x=c.getLocation().x;
		y=c.getLocation().y;
		
	}
	
	//Temp code end
	try 
	{
		Thread.sleep(1500);
	} 
	catch (InterruptedException e)
	{}
	//this.buttons[x][y].doClick();//performs move at selected coordinates
	//code to simulate over
	return new Point(x,y);
	}
	//This method finds the cells with critical mass of the opponent
	public ArrayList<Cell> findCriticalOpps(Color r)
	{
		ArrayList<Cell> ars=new ArrayList<>(0);
		Cell[][] table=this.gdata.getGrid();
		for(int i=0;i<table.length;++i)
		{
			for(int j=0;j<table[i].length;++j)
			{
				Cell curr=table[i][j];
				if((curr.isCritical())&&(!curr.getOwner().equals(r)))
				{
		         ars.add(curr);			
				}
			}
		}
	return ars;
	}
	//This method checks if there are any owned critical cells next to a specefic location
	public ArrayList<Cell> filterCriticalCell(ArrayList<Cell> cells,Color rr)
	{
		ArrayList<Cell> arrs=new ArrayList<>(0);
		for(Cell c:cells)
		{
			ArrayList<Cell> adjs=c.adjacentCells(this.gdata.getGrid());
			for(Cell in:adjs)
			{
				if(in.isCritical()&&(in.getOwner().equals(rr)))
				{
					arrs.add(in);
				}
			}
		}
		return arrs;
	}
	//This method returns a list of all cells which can cause a chain reaction on click
	public ArrayList<Cell> immediateExplodable(Player r)
	{
		ArrayList<Cell> cells=this.filterCriticalCell(this.findCriticalOpps(r.getColor()), r.getColor());
	    return cells;
	}
	
	//This method offers a random blank spot to place the cell in.
	public Cell placeRandom(Player pl)
	{
		Color r=Color.RED;
		Cell cc=null;
		while((!r.equals(Color.BLACK))&&(!r.equals(pl.getColor())))
		{
			Random rand=new Random();
			int x=rand.nextInt(this.gdata.getGrid().length);
			int y=rand.nextInt(this.gdata.getGrid()[0].length);
			cc=this.gdata.getGrid()[x][y];
			r=cc.getOwner();
		}
		return cc;
	}
	//This method offers the immediately next blank cell to place its cell down
	//best for testing purposes
	public Cell placeRandomFast(Player rn)
	{
		Color r=rn.getColor();
		Cell[][] tb=this.gdata.getGrid();
		for(int i=0;i<tb.length;++i)
		{
			for(int j=0;j<tb[0].length;++j)
			{
				Cell first=tb[i][j];
				if(first.getOwner().equals(Color.BLACK)||first.getOwner().equals(r))
				{return first;}
			}
		}
		return null;
	}
//End of class
}
