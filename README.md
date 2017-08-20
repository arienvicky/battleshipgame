# BattleShip Game

Battleship game is a war game played on ocean by two players. Each player own his battle area and each player will get same number of ships where each ship may have different size placed at some position in non-overlapping fashion. Players can select ships their own ship area but cannot see each others ship's location.

The player who destroys all the ships of other player is winner of the game. Player one can select the Battle area dimentions and how many ships he want's to play with another player with mutual consent. For example, Let’s consider each player gets 3 ships of size 1x1, 1x2 and 1x3. First ship is spanned in between B1 cell. Next ship is spanned in between D1 and D2 cells. Last ship is spanned in between F3, F4, and F5 cells. Similarly Player-2 can place the same set of ships in different positions in his battle area.

Both players will get the chance to launch missiles one by one. For example if Player-1 fires missile in Player-2 battle area by calling some position(ex E1) on the Player-2 battle area and the missile hits the Player-2 ship, Player-2 should communicate to the Player-1
whether it hits the ship or not.

In the example above, the missile hits the ship on E1. In this case, Player-1 gets one more chance of firing as he successfully fired the missile. The same process will get repeated. If the missile landed in empty place, then Player-2 gets the chance of firing. 

# Inputs/How to play:
* First two input form fields contains dimensions of battle area having width and height as per the constraints below. 
* Next input field will have number of Players, currently 2 players game is only supported. 
* Next input field will have number of battleships each player has.
* On selection of ship count, player will have the opportunity to select battleships area from the select boxes prefilled based on the dimentions selected along with the battle area for selection and button to start the game.
* Once player 1 hits the start button he is ready to play.
* player 2 can now select the battle area and after click start he is also ready to play.
* Player 1 is ready to hit/target player 2's ships.
* On hit success Player 1 will continure else Player 2's chance to hit Player 1'2 ships.

# Constraints:
* Battle Area Columns - Numeric and 1 <= Width of Battle area (M’) <= 9,
* Battle Area Rows    - A <= Height of Battle area (N’) <= Z or a <= Height of Battle area (N’) <= z
* No. of Players      - Numeric and =2
* No. of Ship(s)      - Numeric and max (m*n)
* Ship Location       - SelectBox having prefilled values based on battle area dimentions, first ship would be 1*1, next would be 1*2, next would be 1*3 and then 1*1 based on ships selected by user so if user selects 2 ships he can select first ship 1*1, and second ship 1*2, if he selects 5 ships then he can select 5 ships 1*1, 1*2, 1*3, 1*1, 1*1

# Sample Input:
* Battle Area Columns - 5
* Battle Area Rows    - E
* No. of Players      - 2
* No. of Ship(s)      - 3
* Ship Location       -  
  * Player 1
    * 1*1 - A1
    * 1*2 - B1, B2
    * 1*3 - C1, C2, C3
  * Player 2
    * 1*1 - B1
    * 1*2 - D1, D2
    * 1*3 - E1, E2, E3

# Sample Output:
* Player-1 fires a missile with target A1 which got miss
* Player-2 fires a missile with target A1 which got hit
* Player-2 fires a missile with target B3 which got miss
* Player-1 fires a missile with target B1 which got hit
* Player-1 fires a missile with target D1 which got hit
* Player-1 fires a missile with target D2 which got hit
* Player-1 fires a missile with target B3 which got miss
* Player-2 fires a missile with target B1 which got hit
* Player-2 fires a missile with target D1 which got miss
* Player-1 fires a missile with target C1 which got hit
* Player-1 fires a missile with target C2 which got hit
* Player-1 fires a missile with target C3 which got hit
* Player-1 won the battle
