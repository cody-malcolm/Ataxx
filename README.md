# Ataxx

## Group members

Avril Lopez van Domselaar 100746008

Mariya Anashkina 100746854

Cody Malcolm 10075739

## Project information
This application consists of a Server and at least 2 Clients to facilitate the game Ataxx. 

### Getting familiar with Ataxx
Ataxx is an abstract strategy board game that involves play by two parties on a 7x7 tiled grid. 

Each player begins with 2 pieces. The game starts with the 4 pieces on the 4 corners of the board.

During their turns, players move one of their pieces either one or two spaces in any direction. If the destination is 
directly adjacent to the source, a new piece is created on an empty destination square. Otherwise,the piece on the 
source moves to the destination. 
After the move, all the opponent player pieces adjacent to the destination square are captured by the moving player. 

The object of the game is for a player to secure a greater number of tiles than his/her opponent.

To visualize the game, please visit: https://youtu.be/uFyg66dlD2s?t=31


## How to run
After cloning or unzipping the project, navigate to the root directory in a terminal. Then:
The server can be started with the following command: ```gradle start```. 
The server takes 6 instructions which are described during startup, when an unsupported instruction is received, and when 
the help instruction is received. The important details are that the server must be told to listen for connections with 
the ```start``` instruction.

The client can be started with a single command: ```gradle run```.

If application has been started correctly, the player will see the screen below. The host IP address of the server by default is set to localhost. 
Hence, if the client is running on the same machine as the server, host IP address can be left empty.

![Alt text](./demos/welcome_screen.png?raw=true "Welcome screen").

## Functionality
* The server supports multiple clients. To start the game, both clients should be connected to the same IP address.
By clicking 'Find the game' button, the player will either be automatically added to the existing game, or he/she will 
  start a new one, waiting for a second player to connect.

![Alt text](./demos/findGame.png?raw=true "Finding the game").
  
* The server is the sole source of authority of the game state. It's robust against a modified request from the client.

* Each game is assigned to a unique gameID, allowing the client to spectate said game.

* Username validation happens on the first welcome screen (can't be "",  "-" or contain ' \ ' due to the Board string representation)
![Alt text](./demos/username.png?raw=true "Invalid username").

* During the game, the GameID and the number of squares occupied by each player is shown
  ![Alt text](./demos/gameInPlay.png?raw=true "Game in play").




## Credit
For the layout of the README we took inspiration from Cody's assignment2: https://github.com/cody-malcolm/FileSharer
. 


