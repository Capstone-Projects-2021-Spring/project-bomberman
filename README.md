# CIS 4398 Section 3 

## Orignal Creator: Brain Li
https://github.com/blai30/bomberman-java

## Developers: Chris DeStefano, Steve McFarland, Xiao Ning Zhao, Jason Xhoxhi, Nolan C Iskra, Thuc Ngoc Luong

## Java Verion Download Link:
https://www.oracle.com/java/technologies/javase-downloads.html
Downloading requires you to make an account.

## Running the game
After installing the latest java version in your machine and unzipping the downloaded folder, simply right click on the jar file to star the game. 

## Overview
Bomberman is a multiplayer desktop application that can be played with friends utilizing a game server (code included). Maps can be custom made and loaded into the game and then be chosen to allow players a unique experience with each play. It has many additional features which are outlined below


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
Use the buttons at the bottom of the screen to place your desired tiles and hit save when you are finished

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
Navigate a larger map by yourself and try to defeat all the enemies located on the map. 

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

## Features: Online Multiplayer:
The online multiplayer has many custom options that can be viewed by typing !help into the client lobby.
It can support an "unlimited" amount of players in the game but performance will vary depending on the amount of players. 

## Features: AI Enemies: 
This feature is still in development but we have a basic AI bot that will move to plant bombs at its closest box. 
