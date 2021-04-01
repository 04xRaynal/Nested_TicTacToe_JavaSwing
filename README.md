# A Nested TicTacToe game using Java Swing.
***
## The user is initially displayed with an option to choose your opponent, either against the computer or a friend.
 
![Capture_NestedTicTacToe_MainMenu]()

---
Play alternates between the user(player1) who initially is X and the player2/CPU which is O.

Image displaying a game between the user and CPU:

![Capture_NestedTicTacToe_vsComputer]()

---
When a player plays his move by clicking on a tile, a new Window is opened which starts a new game(A Nested Game), the winner of the nested game gets his mark set on the main board.

![Capture_NestedTicTacToe_vsComputerNested]()

---
This continues for all the tiles, until there's a winner on the main board.

![Capture_NestedTicTacToe_vsComputerNested_Winner]()

The score of the main game as well as the nested games gets recorded accordingly.

![Capture_NestedTicTacToe_vsComputerNested_2]()

---

After the game(in the main board) ends a dialog box is displayed announcing the result and the score. The user is given an option for a re-match(play again).

![Capture_TicTacToe_vsComputer_Winner]()

---
If the user selects 'Yes' on the Play again option, a new game starts, but now the user (player 1) is O and the cpu/player2 is X.
The mark ('X' and 'O') alternates every new game. X always plays first.

***
When the option of playing against the computer is selected, the program automatically plays its turn. 
Initially the user is X and CPU is O.

The computer's strategy is first to complete 3 O's in a row, or if that is not possible, to block a row of 3 X's, or if that is not possible, to move randomly.

***
After the end of a game, if the user selects 'No' on the Play again option, the frame resets to the main menu of options and all the scores are reseted.

Image displaying a new game between Player 1 & Player 2 (vs Friend):

![Capture_NestedTicTacToe_vsFriend]()

---

The score between the two players is also recorded and displayed at the bottom, it updates after every game.

Top panel displays labels which indicate the turn of each player and their corresponding mark (either X or O).

***
Note:
The code for NestedTicTacToe can be more compact if similar methods are joined and values distinguished with flags.
The TicTacToe code is from https://github.com/04xRaynal/TicTacToe_JavaSwing.git
and only the nested components are added on top of it.
 