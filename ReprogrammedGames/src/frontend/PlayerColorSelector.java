package frontend;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.colorchooser.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
public class PlayerColorSelector extends JDialog
{
public Color[] color_set= new Color[]{
	Color.RED.darker(),
	Color.CYAN.darker(),
	Color.BLUE.darker(),
	Color.GREEN.darker(),
	Color.YELLOW.darker(),
	Color.ORANGE.darker(),
	Color.GRAY.darker(),
	Color.BLACK.darker(),
	Color.MAGENTA.darker(),
	Color.PINK.darker()
};
private static final long serialVersionUID = 850333424433000156L;
public Hashtable<String,Color> results;
protected ArrayList<JColorChooser> jcc;
protected JTabbedPane pane;
protected ArrayList<Color> forbidden;
protected int players;
//IIB: Will Be executed before any constructor call
{
	this.forbidden=new ArrayList<>(0);
    this.jcc=new ArrayList<>(0);
}
public PlayerColorSelector(int pl_no,String title,ArrayList<Color> forbidden,Frame parent,boolean modal,ArrayList<String> names)
{
this(pl_no,title,forbidden,parent,modal);
if(pl_no!=names.size())
{throw new IllegalArgumentException("no. of Names must match player's strength");
}
for(int k=0;k<this.pane.getTabCount();++k)
{
	this.pane.setTitleAt(k,names.get(k));
}
}
public PlayerColorSelector(int pl_no,String title,ArrayList<Color> forbidden,Frame parent,boolean modal)
{
this(pl_no,title,parent,modal);	
this.forbidden.addAll(forbidden);
}
public PlayerColorSelector(int pl_no,String title,Frame parent,boolean modal)
{
super(parent,title,modal);
if(pl_no>10) 
{throw new IllegalArgumentException("Chooser cannot be used for more than 10 players!");}
JMenuBar hmb=new JMenuBar();
JMenu menu=new JMenu("Click here to proceed");
JMenuItem itm=new JMenuItem("Proceed(Cannot be undone)");
menu.add(itm);
hmb.add(menu);
this.setJMenuBar(hmb);
itm.addActionListener((ev)->{
	results=endSelection();
});
this.players=pl_no;
this.addWindowListener(new WindowAdapter()
		{public void windowClosing(WindowEvent we)
		{
			results=endSelection();
		}
		});
pane=new JTabbedPane(SwingConstants.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
ChangeListener cl=new ChangeListener()
{
	public void stateChanged(ChangeEvent pe)
 {
DefaultColorSelectionModel src=(DefaultColorSelectionModel)(pe.getSource());
Color cc=src.getSelectedColor();
if(!isColorAvailable(cc,true))
   {
	JOptionPane.showMessageDialog(null,"<html>This Color has either already been taken by a player"
			+ "<br>or is restricted,Color has been reset","Cannot select item",JOptionPane.ERROR_MESSAGE);
	src.setSelectedColor(generateAvailableColor());
   }
}

};
for(int k=0;k<pl_no;++k)
    {
	//JColorChooser component=new JColorChooser(this.generateAvailableColor());
	JColorChooser component=new JColorChooser(color_set[k]);
	component.getSelectionModel().addChangeListener(cl);
	jcc.add(component);
	pane.addTab("Player "+(k+1)+"'s Color", component);
	}
this.setContentPane(pane);
}
public boolean isColorAvailable(Color c,boolean forSelectionEvent)
{
int counter=0;
ArrayList<Color> acc=this.takenColors();
for(int k=0;k<acc.size();++k)
{   
	COLOR_ITERATOR:
	if(acc.get(k).equals(c))
	{
		++counter;
		if(forSelectionEvent&&counter==1)
		{break COLOR_ITERATOR;}
		return false;
		
	}
}
if(this.forbidden.contains(c))
{return false;}
return true;
}
public Color generateAvailableColor()
{
Color c=Color.CYAN;
while(!this.isColorAvailable(c,false))
{c=this.generateRandomColor(serialVersionUID);}
return c;
}
public Color generateRandomColor(long seed)
{
Random r=new Random(seed);
int red=r.nextInt(200)+40;
int blue=r.nextInt(200)+40;
int green=r.nextInt(200)+40;
return new Color(red,green,blue);
}
private ArrayList<Color> takenColors()
{
ArrayList<Color> takens=new ArrayList<>(0);
for(int k=0;k<jcc.size();++k)
{
JColorChooser jc=jcc.get(k);
takens.add(jc.getColor());
}
return takens;
}
public Hashtable<String,Color> endSelection()
{
	Hashtable<String,Color> ht=new Hashtable<>(0);
	for(int k=0;k<this.jcc.size();++k)
	{
		ht.put(pane.getTitleAt(k),jcc.get(k).getColor());
	}
	this.setVisible(false);
	this.dispose();
	return ht;
}

public JTabbedPane getTabbedPane()
{
return this.pane;	
}

}

