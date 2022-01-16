package frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import frontend.GamePanel.Move;
import main.CellGrid;

public final class SaveItem implements java.io.Serializable 
{

private static final long serialVersionUID = -5977436178010144854L;
protected CellGrid main_grid;//this object represents the main grid of cells for the game panel
protected CellGrid bckup;//this object represents the backup grid foe the undo functionality
protected ArrayList<Move> set;//represents moves logged by current cell grid object
protected String display;//String of component of launcher window
protected String log;//String of log component of launcher window
protected String counter;//String for player move counter
public SaveItem()
{
//default constructor	
}
//This method will be used to correctly initialize a SaveItem object
public void init(CellGrid cg,CellGrid bck,ArrayList<Move> ms,String disp,String lg,String count)
{
 this.main_grid=cg;
 this.bckup=bck;
 this.set=ms;
 this.display=disp;
 this.log=lg;
 this.counter=count;
}
//This method writes an object to a File in the system, accessed via JFileChooser
//returns true if operation was successful, false if operation could not be
//carried out.
public boolean writeTo(File f)
{
try 
{
 FileOutputStream fos=new FileOutputStream(f);
 ObjectOutputStream oos=new ObjectOutputStream(fos);
 oos.writeObject(this);
 oos.close();
 if(!f.exists())
 {throw new RuntimeException();}
 return true;
}
catch(Throwable ex)
{
	
	return false;
}

}
//This method reads a save object from its file-returns null if the operation was unsuccessful
public static  SaveItem readFrom(File f)
{
	SaveItem obj=null;
	try
	{
		FileInputStream  fis=new FileInputStream(f);
		ObjectInputStream ois=new ObjectInputStream(fis);
		obj=(SaveItem)ois.readObject();
		ois.close();
	}
	catch(Throwable ex1)
	{
		
		//Do nothing
	}
	return obj;
}
//end of class
}
