package frontend;
import javax.swing.*;
import javax.swing.border.Border;

import frontend.GamePanel.OrbsAnimation;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import main.CellGrid;
import main.Player;
//Main frame for a chain reaction game,it must be initialized with a GamePanel(initialized or non -initialized)
public final class LauncherWindow extends JFrame
{
	private static String im_path="box_pic.png";
	private static final long serialVersionUID = -2837834386357626982L;//serialVersionUID
	private GamePanel main_panel;//The main game panel, this is the JPanel which holds all the buttons
	public static WindowAdapter cancelInit;//The windowAdapter stating what must be done in case an initialization is cancelled.
	public JTextArea display;//The display for the player chance counter
	public JTextArea log;//Player move log
	public JButton undo;//Undo button
	public JLabel counter;//Player move counter
	public static final Font font;//Default font for all components used in game windows
	public static final Border bord;//Default border for all components in-game
	private static volatile ArrayList<Player> pls;//players initilization variable
	
	static
	{
		font=new Font("SansSerif",Font.BOLD,18);
		bord=new RoundedBorder(5);
		cancelInit=new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				JOptionPane.showMessageDialog(null,"Initialization cancelled, application will now close");
			    System.exit(0);
			}
		};
	}
	//Launcher Window constructor
	public LauncherWindow()
	{
		super();	
	}
	//Launcher window's constructor-with a title
    public LauncherWindow(String title)
    {
       super(title);
    }
	//Game's launch
	public static void main(String[] args) 
	{
		//Selector panels code here
		String[] options=
			{
					"9x6","6x9","10x10"
			};
		Object inpi=JOptionPane.showInputDialog(null,"Select dimensions of board","Create Board",JOptionPane.QUESTION_MESSAGE,null,options,"9x6");
		if(inpi==null)
		{
			JOptionPane.showMessageDialog(null,"Initialization was cancelled, application will now exit.");
		     System.exit(0);
		}
		Scanner dim=new Scanner(inpi.toString());
		dim.useDelimiter("x");
		int x=dim.nextInt();
		int y=dim.nextInt();
        dim.close();
		
		ArrayList<Player> pls=LauncherWindow.createInitializationWindow();
		CellGrid cg=new CellGrid(x,y,pls);
		//TEMP CODE
		//pls.get(1).setAIcontrolled(true);
		//pls.get(2).setAIcontrolled(true);
		//TEMP CODE
		SwingUtilities.invokeLater(()->{
		LauncherWindow jfr=new LauncherWindow("Chain Reaction!");
		LauncherWindow.setIconTo(jfr);
		GamePanel gp=new GamePanel(jfr,cg);
		jfr.initGui(gp,x,y,pls);
		gp.init();
		jfr.setVisible(true);
		GamePanel.OrbsAnimation rn=gp.new OrbsAnimation();
		rn.start();
		});
     
    }
	//Initialization of main game's board
	public JPanel initGui(GamePanel m,int x,int y,ArrayList<Player> pls)
	{
		JPanel parent=new JPanel(new BorderLayout());
	 	Box east=this.initTextLogs();
	 	parent.add(east,BorderLayout.EAST);
	 	parent.add(this.main_panel=m,BorderLayout.CENTER);
	 	//West and north components initialization not required

	 	JMenu general=new JMenu("General/Help");
	 	JMenuItem jms[]=LauncherWindow.createMenuItemsGeneral();
	 	general.add(jms[0]);
		general.add(jms[1]);
		JMenuBar mb;
		this.setJMenuBar(mb=new JMenuBar());
	 	mb.add(general);
	 	mb.add(LauncherWindow.aboutDevMenu());
	 	this.setContentPane(parent);
	 	Color current=this.main_panel.getGrid().getPlayer().getColor();
	 	this.display.setForeground(current);
	 	this.log.setForeground(current);
	 	this.setSize(900, 900);
	 	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return parent;
	}
	
	//Creates JMenuItems required.
	public static JMenuItem[] createMenuItemsGeneral()
	{
	 JMenuItem jm1=new JMenuItem("Rules");
	 JMenuItem jm2=new JMenuItem("Origin");
	 String origin="Chain reaction is a deterministic combinatorial game of perfect information for 2 - 8 players.\n" + 
	 		"It was originally developed by Buddy-Matt Entertainment for Android.\n" + 
	 		"The most interesting thing is how unpredictable the game seems to be in the end,\n at least when you play it with your human friends.\n"
	 		+ " The obvious heuristic that tells us you're better off at the moment by having as many orbs as possible,\n"
	 		+ " turns out to be very wrong. While it so seems to everyone, that say, red will win,\n"
	 		+ " blue suddenly takes over.\n"
	 		+ "The game has been re-developed for Desktop devices"
	 		+ " by Nikhil Narayanan,\n with an altered explosion mechanic.";
	 String rules="1)The gameplay takes place in an m times n board. The most commonly used size\n"
			 +"  of the board is 9 times 6.\n"
			 +"2)For each cell in the board, we define an unstable mass. The unstable mass is \n"
			 +"  equal to the number of orthogonally adjacent cells. That would be 4 for usual\n"
			 +"  cells, 3 for cells in the edge and 2 for cells in the corner.\n"
			 +"3)All cells are initially empty. The Red and the Green player take turns to place\n"
			 +" units of their corresponding colors. The Red player can only place a (red) unit \n"
			 +"  in an empty cell or a cell which already contains one or more red units. When two\n"
			 +"  or more units are placed in the same cell, they stack up.\n"
			 +"4)When a cell is loaded with a number of units equal to its unstable mass, the stack\n"
			 +"  immediately explodes. As a result of the explosion, to each of the orthogonally \n"
			 +"  adjacent cells, an unit is added and the initial cell looses as many units as its \n"
			 +"  unstable mass. The explosions might result in overloading of an adjacent cell and \n"
			 +"  the chain reaction of explosion continues until every cell is stable.\n"
			 +"When a red cell explodes and there are green cells around, the green cells are converted to\n"
			 +"red and the other rules of explosions still follow. The same rule is applicable for \n"
			 +"other colors.\n"
			 +"The winner is the one who eliminates every other player's orbs.\n";
	 ActionListener clicked=(ev)->
	 {
		 Object source=ev.getSource();
		 String which="";
		 if(source==jm1)
		 {which=rules;}
		 else
		 {which=origin;}
		 JOptionPane.showMessageDialog(null,which);
	 };
	 jm1.addActionListener(clicked);
	 jm2.addActionListener(clicked);
	 return new JMenuItem[] {jm1,jm2};
	 //action listeners here-not yet implemented
	}
	//Creating the move logger panel, a Box object with vertical alignment.
	public  Box initTextLogs()
	{
		Box parent=Box.createVerticalBox();
		JTextArea disp=new JTextArea(2,20);
		disp.setEditable(false);
		disp.setFont(new Font("SansSerif",Font.BOLD,14));
		disp.setBorder(bord);
		disp.setBackground(Color.BLACK);
		disp.setForeground(Color.WHITE);
		disp.setText("Player Cycle: 1");
		disp.setLineWrap(true);
		disp.setWrapStyleWord(true);
		JTextArea log= new JTextArea(30,20);
		log.setEditable(false);
		log.setFont(new Font("SansSerif",Font.BOLD,14));
		log.setBackground(Color.BLACK);
		log.setForeground(Color.WHITE);
		log.setBorder(bord);
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		log.setText("Player Action Log: ");
		JPanel c= new JPanel();
		c.setBorder(bord);
		JLabel jl=new JLabel("Turns Played: 0");
		jl.setOpaque(true);
		jl.setBackground(Color.WHITE);
		c.add(jl);
		jl.setBorder(bord);
		jl.setFont(font);
		JButton un=new JButton("Undo");
		un.setBackground(Color.BLACK);
		un.setForeground(Color.WHITE);
		c.add(un);
		un.setFont(font);
		un.setBorder(bord);
		//Come back here and add rounded border to button and an action Listener
		parent.add(new JScrollPane(disp));
		parent.add(c);
		parent.add(new JScrollPane(log));
		this.display=disp;
		this.log=log;
		this.undo=un;
		this.counter=jl;
		return parent;
		
	}
	//This method creates a player number selector window and follows it up with a name selector
	//This method pauses the thread on which it is executed.
	public static ArrayList<Player> createInitializationWindow()
	{
		ArrayList<Player> pls=null;
		try
		{
		String[] vals=new String[7];
		for(int i=2;i<9;++i)
		{
			vals[i-2]=i+"";
		}
		Object ob=JOptionPane.showInputDialog(null,"Choose number of players","Game initialization",JOptionPane.QUESTION_MESSAGE,null,vals,"2");
		if(ob==null)
		{throw new InitializationException("User cancelled init process");}
		int size=Integer.parseInt(ob.toString());
		//text box GUI creation method to be called here.
		pls=LauncherWindow.createPlayerInput(size);
		}
		catch(InitializationException ex)
		{
			JOptionPane.showMessageDialog(null,"Initialization was cancelled, application will now exit.");
		    System.exit(0);
		}
		return pls;
	}
	//This method creates a player name input-This method will hold up the thread on whic
	//it is executed, so it must be used with discretion
	private static ArrayList<Player> createPlayerInput(int size)
	{
        ArrayList<JCheckBox> arrs=LauncherWindow.createAIOptions(size);
		JFrame frm=new JFrame("Enter Player names");
		LauncherWindow.setIconTo(frm);
		frm.addWindowListener(LauncherWindow.cancelInit);
		frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ArrayList<JTextField> jtf=new ArrayList<>(0);
		Box cont=new CustomPictureBox(BoxLayout.Y_AXIS,new ImageIcon(im_path));
		cont.setOpaque(true);
		cont.setBackground(Color.BLACK);
		cont.setBorder(bord);
		for(int i=0;i<size;++i)
		{
		 JTextField jtf1=new JTextField("p-"+(i+1));
		 jtf1.setFont(font);
		 jtf1.setBorder(bord);
		 jtf.add(jtf1);
		 cont.add(Box.createRigidArea(new Dimension(10,20)));
		 //add action listener.
		 JLabel c=new JLabel("Enter player "+(i+1)+"'s name");
		 c.setForeground(Color.WHITE);
		 c.setFont(font);
		 c.setBorder(bord);
		 cont.add(c);
		 cont.add(Box.createRigidArea(new Dimension(10,20)));
		 cont.add(jtf1);
		 cont.add(Box.createRigidArea(new Dimension(10,20)));
		 //add indie checkboxes here
		 JComponent c_fin=null;
		 if(i==0)
		 {
			JLabel lab=new JLabel("First Player cannot be AI-enabled");
			lab.setBorder(bord);
			lab.setForeground(Color.WHITE);
			c_fin=lab;
		 }
		 else
		 {
		   c_fin=arrs.get(i-1);
		 }
		 cont.add(c_fin);
		 cont.add(Box.createRigidArea(new Dimension(10,20)));
		 }
		JButton okay=new JButton("Okay");
		okay.setBorder(bord);
		okay.setFont(font);
		cont.add(okay);
		cont.add(Box.createRigidArea(new Dimension(10,20)));
		//add okay's action listener
		ActionListener ok=(ev)->
		{
			ArrayList<String> names=new ArrayList<>(0);
	        jtf.forEach((name)->{
	        	names.add(name.getText());
	        });
			if(LauncherWindow.areNamesValid(names))
			{   
				frm.setVisible(false);
				frm.dispose();
				ArrayList<Player> temp=new ArrayList<>(0);
				ArrayList<Color> not_allowed=new ArrayList<>(0);
				not_allowed.add(Color.black);
				not_allowed.add(Color.white);
				//launch Color selector, and initialize pls here
			    PlayerColorSelector pc=new PlayerColorSelector(names.size(),"Select your Colors",not_allowed,null,true,names);
			    pc.setSize(600,500);
			    pc.setLocationRelativeTo(null);
			    LauncherWindow.setIconTo(pc);
			    pc.setVisible(true);
			    Hashtable<String,Color> ht=pc.results;
			    int i1=0;
			    for(String name:names)
			    {
			    	Color r=ht.get(pc.getTabbedPane().getTitleAt(i1));
			    	
			        temp.add(new Player(name,r));
			        ++i1;
			    }
			   pls=temp;
			   
			}
			else
			{
				JOptionPane.showMessageDialog(frm,"Invalid name input.Note that:\n"
						+ "1) Names can't consist of more than one word\n"
						+ "2)No two names can match,every player must have a different name\n"
						+ "3)Names cannot be more than 11 characters long.\n"
						+ "Please try again");
			}
		};
		okay.addActionListener(ok);
		frm.add(new JScrollPane(cont));
		frm.setSize(300,300);
		frm.setLocationRelativeTo(null);
		frm.setVisible(true);
		frm.setResizable(false);
		while(pls==null)
		{}
		for(int k=0;k<pls.size();++k)
		{
			if(k!=0)
			{
			JCheckBox cb=arrs.get(k-1);
			Player p=pls.get(k);
			p.setAIcontrolled(cb.isSelected());
			}
			
		}
		return pls;
	}
	//this method checks if the name inputs are valid
	private static boolean areNamesValid(ArrayList<String> names)
	{
		for(String text:names)
		{
			
			if((text.length()>11)||text.contains(" ")||(names.indexOf(text)!=names.lastIndexOf(text)))
			{
				return false;
			}
		}
		return true;
	}
	//An object of this class signifies that was a problem with initializing the game
	public static class InitializationException extends RuntimeException
	{
		private static final long serialVersionUID = -4298917786790558021L;
	    public InitializationException(String str)
	    {
	    	super(str);
	    }
	}
	//This method attempts to load an icon for the given frame and apply it.
	public static void setIconTo(JFrame wind)
	{
		ImageIcon ii=new ImageIcon("icon.png");
		wind.setIconImage(ii.getImage());
	}
	//This method attempts to load and set an icon for a given dialog and apply it
	public static void setIconTo(JDialog wind)
	{
		ImageIcon ii=new ImageIcon("icon.png");
		wind.setIconImage(ii.getImage());
	}
	//this method creates the menu items for the about developer menu
	private static JMenu aboutDevMenu()
	{
		String ab="https://linktr.ee/nikhil_n67";
		String wk="https://nikhil-rgb.github.io";
		
		JMenuItem about=new JMenuItem("More about Developer");
		JMenuItem work=new JMenuItem("Other apps from this developer");
		ActionListener shows=(ev)->
		{
		  String url="";
		  Object src=ev.getSource();
		  if(src==about)
		  {
			  url=ab;
		  }
		  else
		  {
			  url=wk;
		  }
		  try
			{
				java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
			}
			catch(Throwable ex)
			{
				JOptionPane.showMessageDialog(null,"Couldn't display requested information","Error",JOptionPane.ERROR_MESSAGE);
			}
		};
		about.addActionListener(shows);
		work.addActionListener(shows);
		JMenu menu=new JMenu("More and About");
		menu.add(about);
		menu.add(work);
		return menu;
		}
    
	//This method creates a list of checkboxes with event listeners-to specify whether players
	// are ai controlled or not
	public static ArrayList<JCheckBox> createAIOptions(int size)
	{
	 ArrayList<JCheckBox> arrs=new ArrayList<>(0);
	 for(int k=1;k<size;++k)
	 {
		JCheckBox jcb=new JCheckBox("AI controlled player");
		jcb.setBorder(bord);
		jcb.setToolTipText("Select to make this player computer-controlled");
	    arrs.add(jcb);
	 }
	 
		return arrs;
	}
	
	//This inner class can be used for creating custom picture background vertical boxes
	public static class CustomPictureBox extends Box
	{
        
		private static final long serialVersionUID = 1L;
		Image img;
		public CustomPictureBox(int axis,ImageIcon gg) 
		{
			super(axis);
			this.img=gg.getImage();
		}
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if(this.img==null)
			{return;}
			g.drawImage(this.img,0,0,this.getWidth()+1,this.getHeight()+1,null);
		}
		
		
		
	}
}
