 CIS 4398 Section 3

## Developers: Chris DeStefano, Steve McFarland, Xiao Ning Zhao, Jason Xhoxhi, Nolan C Iskra, Thuc Ngoc Luong

## Java Verion Download Link:
https://www.oracle.com/java/technologies/javase-downloads.html
Downloading requires you to make an account.

## Running the game
After installing the latest java version in your machine and unzipping the downloaded folder, simply right click on the jar file to star the game.

## Overview
Bomberman is a multiplayer desktop application that can be played with friends utilizing a game server. Maps can be custom made and loaded into the game and then be chosen to allow players a unique experience with each play. The game offers single player, tutorial, local multiplayer, and normal multiplayer. With other configurable settings, games can be set to be played with different number of players as well as different maps and powerups. Additionally there are options for other randomization to take place and very beginning bot ai movement.

## Features: Local Multiplayer:
## Controls
### Player 1
| Command | Key |
|---------|-----|
| UP      | ↑   |
| DOWN    | ↓   |
| LEFT    | ←   |
| RIGHT   | →   |
| BOMB    | /   |

### Player 2
| Command | Key |
|---------|-----|
| UP      | W   |
| DOWN    | S   |
| LEFT    | A   |
| RIGHT   | D   |
| BOMB    | E   |

### Player 3
| Command | Key |
|---------|-----|
| UP      | T   |
| DOWN    | G   |
| LEFT    | F   |
| RIGHT   | H   |
| BOMB    | Y   |

### Player 4
| Command | Key |
|---------|-----|
| UP      | I   |
| DOWN    | K   |
| LEFT    | J   |
| RIGHT   | L   |
| BOMB    | O   |

### System
| Command       | Key |
|---------------|-----|
| EXIT          | ESC |
| RESET         | F5  |
| View Controls | F1  |

## Features: Map Editor:
Enter the map size with rows and columns with a comma between for example 30,50 would create a map with 30 rows and 50 columns. Use the buttons at the bottom of the screen to place your desired tiles. No spawn points are required if the map is intended for multiplayer. If it is intended for local multiplayer all 4 spawn points must be placed for the map to work. When the design is done enter the map name in the text field below for example mymap and then click the create button. Now the map Can be played in the local multiplayer or online multiplayer.

### Possible tiles:
* Player1 initial spawn: `1`
* Player2 initial spawn: `2`
* Player3 initial spawn: `3`
* Player4 initial spawn: `4`
* Soft wall: `S`
* Hard wall: `H`
* Powerup Bomb: `PB`
* Powerup Firepower up: `PU`
* Powerup Firepower max: `PM`
* Powerup Speed: `PS`
* Powerup Pierce: `PP`
* Powerup Kick: `PK`
* Powerup Timer: `PT`
* Enemy Balloon: `EB`

## Features: Single Player:
Navigate a larger map by yourself and try to defeat all the enemies located on the map. Once defeated, the map is randomly reset and you play again for endless fun!

## Features: Tutorial:
Shows off basic movement and powerups available

## Available powerups:
* Bomb: Increase maximum number of placed bombs by 1 (max: 6)
* Firepower up: Increase firepower by 1 (max: 6)
* Firepower max: Set firepower to max (max: 6)
* Speed: Increase movement speed by 0.5 (max: 4)
* Pierce: Enables piercing bomb explosions (explosions extend past soft walls)
* Kick: Enables kicking bombs
* Timer: Decrease bomb detonation timer by 15 (min: 160)

## Features: Map Manager:
Map Manager allows for the management of game maps. You can add a map from your local file system using the add button and selecting the map you would like to add. You can upload a map to the server by selecting it from the map list and clicking the upload button. You can download a map by clicking the download button and then selecting a map from the list and clicking the download button. You can delete a local map by selecting it from the list and clicking the delete button.

## Features: Online Multiplayer:
Online multiplayer has a lobby with chat and helpful game setting commands which can be accessed with !help, once all your friends are in the lobby you can ready up and hit start game to play!

List of commands
!help
!MAPOPTIONS
!GETMAP
!SETMAP <mapname>
!POWERUP
!RANDOM
!ADDBOT
!REMOVEBOT

Player Controls
W - Move Up
A - Move Left
S - Move Down
D - Move Right
E - Place Bomb

The player will show up as while and all enemies will show up as black.

An unlimited number of players can join the lobby and take part in the match. The server must be up and running in order to connect to the lobby and start the match. Any commands set through the chat line take place for the entire lobby. If random map generation is set then it will overwrite any map selection and the game will run off of random map generation. If the map is set or random map generation is taking place, there will be random placement of players and power ups if on between each round. AI Bots can currently be added to the match but their movement is limited and adds very large overhead to the server making the latency very large. The chat box turns into a game console once the game starts. Player movements can be seen as well as the latency between the action and the server. Players can also still message once the match has started.

## Features: Ai Bots
The computer controlled enemy is working at a simple leve. It runs off of a prioity queue to break all the nearest creates and then attempt to bomb the nearest player. This works best on smaller maps in the local multiplayer and only one AI player is running at a time. The AI will only place 1 bomb down at time and then run away from the bombs location as to not kill itself. As for multiplayer the AI is set to do random movement commands and instead of placing bombs, it simple needs to colide with another player to defeat them at the cost of losing itself. 

## Known Bugs
* Multiplayer Latency Issues - Due to number of players and a large amount of requests to and from the server there is a large amount of desync between players
* Multipayler PowerUp Issues - There is a desync between where the players see the locations of the powerups, collecting powerups adds more latency to the server
* AI Issues - The AI can occasionally get stuck on hard walls when targetting creates, might not get out of bomb blast radius in time if it collects any powerups
* Menu System Issues - Issues using JFrame lead to bugs from switching between menus, closing menus, and loading menus
* Sound Issues - The music from 1 menu keeps playing even if that menu is closed and another is opened causing overlapping songs to play
