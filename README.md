# chain_reaction_desktop
An application which emulates the android chain reaction game on PC.<br>
Built using JAVA, GUI built with SWING.<br>
Download the CHAIN_REACTION_2022 zip file from releases and unzip it, run the .exe or .jar file to use the app.
To read the source code, read the .java files in the Reprogrammed Games folder.<br>
All files in the main package are java classes which control the backend of the game.<br>
All files in the frontend package represent classes which contruct and control JAVA SWING GUI components.<br>
<br>
Side note:
The CellButton class in the frontend package is a child of the Cell class, and allows for animated orbs inside the buttons, via<br>
custom painting by overriding the paintComponent() function of the JButton class, and using the parametric form of the equation for<br>
an ellipse to define the locus of the orb painted. More details are provided in the source code files(CellButton.java)

