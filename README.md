# chain_reaction_desktop
<img width="255" alt="icon" src="https://user-images.githubusercontent.com/68727041/150774257-cfbcc57c-78cb-41c6-8518-150be09e7a37.png">
<br>

### Purpose

Chain reaction is a deterministic combinatorial game of perfect information for 2 - 8 players.<br/>
It was originally developed by Buddy-Matt Entertainment for Android.<br/>
The most interesting thing is how unpredictable the game seems to be in the end, at least when you play it with your human friends. The obvious heuristic that tells us you're better off at the moment by having as many orbs as possible turns out to be very wrong. While it so seems to everyone, that say, red will win, blue suddenly takes over.<br/>
- The gameplay takes place in a m×n board. The one used here is of  dimension 9×6.<br/>
- For each cell in the board, we define a critical mass. The critical mass is equal to the number of orthogonally adjacent cells. That would be 4 for usual cells, 3 for   cells in the edge and 2 for cells in the corner.<br/>
- All cells are initially empty. The Red and the Green player take turns to place "orbs" of their corresponding colors. The Red player can only place an (red) orb in an   empty cell or a cell which already contains one or more red orbs. When two or more orbs are placed in the same cell, they stack up.<br/>
- When a cell is loaded with a number of orbs equal to its critical mass, the stack immediately explodes. As a result of the explosion, to each of the orthogonally         adjacent cells, an orb is added and the initial cell looses as many orbs as its critical mass. The explosions might result in overloading of an adjacent cell and the     chain reaction of explosion continues until every cell is stable.<br/>
- When a red cell explodes and there are green cells around, the green cells are converted to red and the other rules of explosions still follow. The same rule is         applicable for other colors.<br/>
- The winner is the one who eliminates every other player's orbs.<br/>

This application is the Desktop implementation of the Android Chain Reaction game<br>
- Built using JAVA, GUI built with SWING.<br>
- Extra features, such as AI opponents(upto 6)
- Detailed player logs, with a colour co-ordinated background and board!
<br/>

### Launch:

- Download the CHAIN_REACTION_2022 zip file from releases and unzip it, run the .exe or .jar file to use the app.

### Technical Trivia:

- All files in the main package are java classes which control the backend of the game.<br>

- All files in the frontend package represent classes which contruct and control JAVA SWING GUI components.<br>
  
- The CellButton class in the frontend package is a child of the Cell class, and allows for animated orbs inside the buttons, via<br>
  custom painting by overriding the paintComponent() function of the JButton class, and using the parametric form of the equation for<br>
  an ellipse to define the locus of the orb painted. More details are provided in the source code files(CellButton.java)

- The AI opponents choose critical mass cells close to your own/other opponents' critical mass cells on first priority,always




