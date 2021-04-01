/*
 * A Nested TicTacToe game using Java Swing.
 * The user is initially displayed with an option to choose your opponent, either against the computer or a friend.
 * Play alternates between the user(player1) who initially is X and the player2/CPU which is O.
 * 
 * When a player plays his move by clicking on a tile, a new Window is opened which starts a new game(A Nested Game), the winner of the nested game gets his mark set on the main board.
 * This continues for all the tiles, until there's a winner on the main board.
 * The score of the main game as well as the nested games gets recorded accordingly.
 * 
 * After the game ends a dialog box is displayed announcing the result and the score. The user is given an option for a re-match(play again).
 * 
 * If the user selects 'Yes' on the Play again option, a new game starts, but now the user (player 1) is O and the cpu/player2 is X. 
 * The mark ('X' and 'O') alternates every new game. X always plays first.
 * The score between the two players is also recorded and displayed at the bottom, it updates after every game.
 * 
 * After the end of a game, if the user selects 'No' on the Play again option, the frame resets to the main menu of options and all the scores are reseted.
 * 
 * Top panel displays labels which indicate the turn of each player and their corresponding mark(either X or O).
 * 
 * When the option of playing against the computer is selected, the program automatically plays its turn. Initially the user is X and CPU is O.
 * The computer's strategy is first to complete 3 O's in a row, 
 * or if that is not possible, to block a row of 3 X's, 
 * or if that is not possible, to move randomly.
 * 
 * @author - 04xRaynal
 */
package raynal.nested_tictactoe;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class NestedTicTacToe extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	JRadioButton radioButton1, radioButton2;
	JLabel radioLabel;
	boolean compFlag, newGameFlag, nestedFlag, newNestedGameFlag;					//compFlag is set true when user selects to play against the computer
	JPanel centerWrapper, northWrapper, southWrapper, northNestedWrapper, centerNestedWrapper;
	JLabel topLabel, bottomLabel;
	JButton b[] = new JButton[9];
	JButton bNested[] = new JButton[9];
	Container c;
	
	final char BLANK = ' ', O = 'O', X = 'X';
	char position[] = {						//To track moves of players
			BLANK, BLANK, BLANK,
			BLANK, BLANK, BLANK, 
			BLANK, BLANK, BLANK
	};
	char positionNested[] = {						//To track moves of players in the Nested Window
			BLANK, BLANK, BLANK,
			BLANK, BLANK, BLANK, 
			BLANK, BLANK, BLANK
	};
	/*
	 * Winning end points are nothing but the points of a 3x3 matrix, 
	 * to determine whether a player has 3 continuous markers (across, down or diagonally).
	 * eg. of a 3x3 matrix 	| 0 1 2 |
	 *						| 3 4 5 |
	 *						| 6 7 8 |
	 * To win you need to mark either (0,1,2) -> endpoints [0, 2]
	 * (3,4,5) -> [3, 5],  (6,7,8) -> [6, 8],  (0,3,6) -> [0, 6],  (1,4,7) -> [1, 7],
	 * (2,5,8) -> [2, 8],  (0,4,8) -> [0, 8] and (2,4,6) -> [2, 6]
	 * 
	 * Hence the winningEndPoints Matrix keeps a track of these 8 endPoints which help determine a winner.
	 */ 
	int[][] winningEndPoints = { {0, 2}, {3, 5}, {6, 8}, {0, 6}, {1, 7}, {2, 8}, {0, 8}, {2, 6} };
	
	int player1Wins = 0, player2Wins = 0, cpuWins = 0, draws = 0;
	Image xIcon, oIcon, xoxoIcon, xoIcon;
	ImageIcon xImageIcon, oImageIcon, xoImageIcon;
	int playerFlag;
	char playerCharFlag, winnerNestedCharFlag = ' ';
	Random random = new Random();
	Player player1, player2, playerCPU, currentPlayer, player1Nested, player2Nested, playerCPUNested ,currentNestedPlayer;
	int compSearchResult; 
	int buttonWidth, buttonHeight, nestedButtonWidth, nestedButtonHeight;
	JLabel player1Label, player2Label, player1NestedLabel, player2NestedLabel;
	JButton menu;
	NestedDialog nestedDialog;
	Container nc;
	JDialog dialogThis;
	
	
	public NestedTicTacToe() {
		try {   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   }
		catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}        //Refines the look of the ui
		
		c = getContentPane();
		
		radioLabel = new JLabel("Choose your opponent: ");
		radioLabel.setBounds(60, 80, 200, 30);
		radioLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		c.add(radioLabel);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		radioButton1 = new JRadioButton("vs Computer", new ImageIcon(Toolkit.getDefaultToolkit().getImage("src\\resources\\computer-icon.png").getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		radioButton1.setFont(new Font("Arial", Font.PLAIN, 14));
		radioButton2 = new JRadioButton("vs Friend", new ImageIcon(Toolkit.getDefaultToolkit().getImage("src\\resources\\add-friend-icon.png").getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
		radioButton2.setFont(new Font("Arial", Font.PLAIN, 14));
		buttonGroup.add(radioButton1); buttonGroup.add(radioButton2);
		
		radioButton1.addActionListener(new radioAction());
		radioButton2.addActionListener(new radioAction());
		radioButton1.setBounds(60, 120, 200, 50);
		radioButton2.setBounds(60, 170, 200, 50);
		c.add(radioButton1); c.add(radioButton2);

		
		xIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\x-icon.png");
		oIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\o-icon.png");
		xoxoIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\xoxo-icon.png").getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		xoIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\xo-icon.png");
		xImageIcon = new ImageIcon(xIcon.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		oImageIcon = new ImageIcon(oIcon.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		xoImageIcon = new ImageIcon(xoIcon.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
		
		setIconImage(xoxoIcon);
		setTitle("Tic Tac Toe");
		setLayout(new BorderLayout());
		setSize(350, 350);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new NestedTicTacToe();
			}
		});
	}
	
	
	public void addButtons() {
		centerWrapper = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				super.paintComponents(g);
				Graphics2D g2d = (Graphics2D) g;
				int w = getWidth();
			    int h = getHeight();
			    g2d.setPaint(new Color(230, 230, 230));
			    g2d.fill(new Rectangle2D.Double(0, 0, w, h));
			    g2d.setPaint(Color.BLACK);
			    g2d.setStroke(new BasicStroke(5));
			    g2d.draw(new Line2D.Double(0, h/3, w, h/3));
			    g2d.draw(new Line2D.Double(0, h*2/3, w, h*2/3));
			    g2d.draw(new Line2D.Double(w/3, 0, w/3, h));
			    g2d.draw(new Line2D.Double(w*2/3, 0, w*2/3, h));
			}
		};       //paint component draws the lines in between the gridLayout and also sets a background
		
		//adding the buttons
		centerWrapper.setLayout(new GridLayout(3, 3, 5, 5));
		for(int i = 0; i < 9; i++) {
			b[i] = new JButton();
			b[i].addActionListener(this);
			b[i].putClientProperty("position", i);
			centerWrapper.add(b[i]);
		}
		c.add(centerWrapper, BorderLayout.CENTER);
	}
	
	
	public void addTopLabel() {					//Adding the top panels
		northWrapper = new JPanel();
		northWrapper.setLayout(new BorderLayout());
		//Displaying Player 1 Label with its current mark
		if(player1.playerCharFlag == 'X') {
			player1Label = new JLabel("  Player 1:", new ImageIcon(xIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
		}
		else if(player1.playerCharFlag == 'O') {
			player1Label = new JLabel("  Player 1:", new ImageIcon(oIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
		}
		player1Label.setHorizontalTextPosition(SwingConstants.LEADING);
		player1Label.setFont(new Font("Arial", Font.PLAIN, 14));
		
		//Displaying Player2/CPU Label with its current mark
		if(! compFlag) {
			if(player2.playerCharFlag == 'X') {
				player2Label = new JLabel("Player 2:", new ImageIcon(xIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
			}
			else if(player2.playerCharFlag == 'O') {
				player2Label = new JLabel("Player 2:", new ImageIcon(oIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
			}
		}
		else {
			if(playerCPU.playerCharFlag == 'X') {
				player2Label = new JLabel("CPU:", new ImageIcon(xIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)), SwingConstants.TRAILING);
			}
			else if(playerCPU.playerCharFlag == 'O') {
				player2Label = new JLabel("CPU:", new ImageIcon(oIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)), SwingConstants.TRAILING);
			}
		}
		player2Label.setHorizontalTextPosition(SwingConstants.LEADING);
		player2Label.setFont(new Font("Arial", Font.PLAIN, 14));
		
		
		//adding a menu button which resets back to the main menu
		menu = new JButton("Menu");
		menu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		//nesting the menu button in another panel because when adding the component to the center layout of the northWrapper panel, the button gets displayed in the entire center region
		//nesting makes the button have the size as that of the flowPanel
		JPanel flowPanel = new JPanel(new FlowLayout());		
		flowPanel.add(menu);
		
		northWrapper.add(player1Label, BorderLayout.WEST);  
		northWrapper.add(flowPanel, BorderLayout.CENTER);
		northWrapper.add(player2Label, BorderLayout.EAST);  
		
		c.add(northWrapper, BorderLayout.NORTH);
	}
	
	
	public void addBottomLabel() {					//adding the bottom panel
		//bottomLabel displays the score
		southWrapper = new JPanel();
		DecimalFormat formatter = new DecimalFormat("00");
		if(!compFlag)
			bottomLabel = new JLabel("Player 1 Wins: " + formatter.format(player1Wins) + ",     Player 2 Wins: " + formatter.format(player2Wins) + ",     Draws: " + formatter.format(draws));
		else
			bottomLabel = new JLabel("Player 1 Wins: " + formatter.format(player1Wins) + ",     CPU Wins: " + formatter.format(cpuWins) + ",     Draws: " + formatter.format(draws));
		
		bottomLabel.setFont(new Font("Sans Serif", Font.PLAIN, 13));
		southWrapper.add(bottomLabel);
		
		c.add(southWrapper, BorderLayout.SOUTH);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {			//ActionPerformed when the Button is clicked
		if(newGameFlag)
			newGameFlag = false;
		
		JButton button = (JButton) e.getSource();
		buttonWidth = button.getWidth();
		buttonHeight = button.getHeight();
		
		new NestedDialog(this);
		
		updateBottomLabel();						//updates the bottom label with the winner of the nested window
		
		/*
		 * The winnerNestedCharFlag is empty when the user closes the nested dialog without playing.
		 * In such a case the player who was the initiator for that dialog gets his flag set as the returned flag
		 * ie. if the dialog was opened by the move of player 2, and was then closed without determining a winner in that nested dialog,
		 * The Player 2's charFlag gets set as the winnerNestedCharFlag
		 */
		if(winnerNestedCharFlag == ' ') {			
			winnerNestedCharFlag = currentPlayer.playerCharFlag;
			nestedReset();
		}

		if(winnerNestedCharFlag == 'X') {
			button.setIcon(new ImageIcon(xIcon.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));
			button.setDisabledIcon(button.getIcon());
			button.setEnabled(false);
			position[(int) button.getClientProperty("position")] = X;
		}
		else if(winnerNestedCharFlag == 'O') {
			button.setIcon(new ImageIcon(oIcon.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));
			button.setDisabledIcon(button.getIcon());
			button.setEnabled(false);
			position[(int) button.getClientProperty("position")] = O;
		}
		
		button.setContentAreaFilled(false);
		button.setBorder(BorderFactory.createEmptyBorder());
		
		if(winnerNestedCharFlag == player1.playerCharFlag) {
			if(won(player1.playerCharFlag))
				announceWinner(player1.playerFlag);
			else if(isDraw())
				announceDraw();
		}
		else {
			if(! compFlag) {
				if(winnerNestedCharFlag == player2.playerCharFlag) {
					if(won(player2.playerCharFlag))
						announceWinner(player2.playerFlag);
					else if(isDraw())
						announceDraw();
				}
			}
			else {
				if(winnerNestedCharFlag == playerCPU.playerCharFlag) {
					if(won(playerCPU.playerCharFlag))
						announceWinner(playerCPU.playerFlag);
					else if(isDraw())
						announceDraw();
				}
			}
		}
		
		winnerNestedCharFlag = ' ';						//Flag is set back to default
		
		/*
		 * The newGameFlag is necessary while playing against the computer,
		 * When the 1st game is completed and the 2nd game is started,
		 * the compPlays() method is invoked before this method is completed.
		 * And if a flag is not set, the currentPlayer variable gets changed before the compPlays() method can display its output and it leads to unexpected results.
		 */
		if(!newGameFlag) {
			if(! compFlag) {
				if(currentPlayer == player1) {
					currentPlayer = player2;
					player1Label.setForeground(Color.BLACK);
					player2Label.setForeground(Color.RED);
				}
				else {
					currentPlayer = player1;
					player2Label.setForeground(Color.BLACK);
					player1Label.setForeground(Color.RED);
				}
			}
			else {
				currentPlayer = playerCPU;
				player1Label.setForeground(Color.BLACK);
				player2Label.setForeground(Color.RED);

				compPlays();				//the computer plays
			}
		}
	}
	
	
	class radioAction extends AbstractAction {			//Action Listener of Radio Buttons
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(radioButton1.isSelected()) {
				compFlag = true;
				
				player1 = new Player(1, 'X');
				playerCPU = new Player(2, 'O');
			}
			
			else if(radioButton2.isSelected()) {
				compFlag = false;
				
				player1 = new Player(1, 'X');
				player2 = new Player(2, 'O');
			}
			
			c.remove(radioButton1);  c.remove(radioButton2);  c.remove(radioLabel);
			c.setVisible(false);
			addTopLabel();
			addBottomLabel();
			addButtons();
			c.setVisible(true);
			currentPlayer = player1;				//Play starts with Player 1
			player1Label.setForeground(Color.RED);
		}
	}
	
	
	boolean won(char playerCharFlag) {				//returns true if player has won
		for(int i = 0; i < 8; i++) {
			if(testingWinRows(playerCharFlag, winningEndPoints[i][0], winningEndPoints[i][1]))
				return true;
		}
		return false;
	}
	
	
	boolean testingWinRows(char playerCharFlag, int a, int b) {			//returns true if player has won in this particular row
		if(! nestedFlag) {
			return position[a] == playerCharFlag && position[b] == playerCharFlag 
				&& position[(a+b)/2] == playerCharFlag;
		}
		else {
			return positionNested[a] == playerCharFlag && positionNested[b] == playerCharFlag 
					&& positionNested[(a+b)/2] == playerCharFlag;
		}
	}
	
	
	void announceWinner(int playerFlag) {			//Displays a Dialog box displaying the winner of the previous game and an option to Play again
		int reply = 0;
		if(! compFlag) {
			if(playerFlag == 1) {
				++player1Wins;
				updateBottomLabel();
				reply = JOptionPane.showConfirmDialog(this, "Player 1 Won,  \nScore ::  Player 1: " + player1Wins + ", Player 2: " + player2Wins + ", Draws: " + draws + "\nDo you want to Play again?", "Player 1 wins!!!", JOptionPane.YES_NO_OPTION);
			}
			else if (playerFlag == 2){
				++player2Wins;
				updateBottomLabel();
				reply = JOptionPane.showConfirmDialog(this, "Player 2 Won,  \nScore ::  Player 1: " + player1Wins + ", Player2 : " + player2Wins + ", Draws: " + draws + "\nDo you want to Play again?", "Player 2 wins!!!", JOptionPane.YES_NO_OPTION);
			}
		}
		else {
			if(playerFlag == 1) {
				++player1Wins;
				updateBottomLabel();
				reply = JOptionPane.showConfirmDialog(this, "Player 1 Won,  \nScore ::  Player 1: " + player1Wins + ", Player 2: " + player2Wins + ", Draws: " + draws + "\nDo you want to Play again?", "Player 1 wins!!!", JOptionPane.YES_NO_OPTION);
			}
			else if (playerFlag == 2){
				++cpuWins;
				updateBottomLabel();
				reply = JOptionPane.showConfirmDialog(this, "CPU Won,  \nScore ::  Player 1: " + player1Wins + ", CPU: " + cpuWins + ", Draws: " + draws + "\nDo you want to Play again?", "CPU wins!!!", JOptionPane.YES_NO_OPTION);
				
			}
		}
		
		//If user selects 'Yes' a new game is started, for 'No' the Frame is reseted
		if(reply == JOptionPane.YES_OPTION) {
			newGameFlag = true;
			newGame();
		}
		else if(reply == JOptionPane.NO_OPTION) {
			newGameFlag = true;
			reset();
		}
		else {				//If none of the options are selected and the user closes the dialog
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			//main frame freezes and doesn't exit, now on canceling the main window it will exit
			for(JButton button: b) {								//buttons are grayed out, they can't be chosen
				button.setEnabled(false);
			}
		}
	}
	
	
	boolean isDraw() {				//return true if the game is a draw
		if(! nestedFlag) {
			for(int i = 0; i < 9; i++) {
//				System.out.print(position[i]);
				if(position[i] == BLANK) 
					return false;
			}
		}
		else {
			for(int i = 0; i < 9; i++) {
				if(positionNested[i] == BLANK)
					return false;
			}
		}
		return true;
	}
	
	
	void announceDraw() {			//Displays a dialog displaying the game is a tie and an option to Play again
		++draws;
		updateBottomLabel();
		
		int reply;
		
		if(! compFlag) {
			reply = JOptionPane.showConfirmDialog(this, "Tied,  \nScore ::  Player 1: " + player1Wins + ", Player 2: " + player2Wins + ", Draws: " + draws + "\nDo you want to Play again?", "Tie!!!", JOptionPane.YES_NO_OPTION);
		}else {
			reply = JOptionPane.showConfirmDialog(this, "Tied,  \nScore ::  Player 1: " + player1Wins + ", CPU: " + cpuWins + ", Draws: " + draws + "\nDo you want to Play again?", "Tie!!!", JOptionPane.YES_NO_OPTION);
		}
		
		//If user selects 'Yes' a new game is started, for 'No' the Frame is reseted
		if(reply == JOptionPane.YES_OPTION) {
			newGameFlag = true;
			newGame();
		}
		else if(reply == JOptionPane.NO_OPTION) {
			newGameFlag = true;
			reset();
		}
		else {						//If none of the options are selected and the user closes the dialog
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			//main frame freezes and doesn't exit, now on canceling the main window it will exit
			for(JButton button: b) {								//buttons are grayed out, they can't be chosen
				button.setEnabled(false);
			}
		}
	}
	
	
	void newGame() {			//all the old components are removed and a new game is started
		c.removeAll();
		c.setVisible(false);
		
		for(int i = 0; i < 9; i++) {
			position[i] = BLANK;
		}
		
		//The markers of the players are swapped ('X' and 'O')
		if(! compFlag) {
			if(player1.playerCharFlag == 'X' && player2.playerCharFlag == 'O') {
				player1.playerCharFlag = 'O';
				player2.playerCharFlag = 'X';
				currentPlayer = player2;
			}
			else {
				player1.playerCharFlag = 'X';
				player2.playerCharFlag = 'O';
				currentPlayer = player1;
			}
		}
		else {
			if(player1.playerCharFlag == 'X' && playerCPU.playerCharFlag == 'O') {
				player1.playerCharFlag = 'O';
				playerCPU.playerCharFlag = 'X';
				currentPlayer = playerCPU;
			}
			else {
				player1.playerCharFlag = 'X';
				playerCPU.playerCharFlag = 'O';
				currentPlayer = player1;
			}
		}
		
		addTopLabel();
		addBottomLabel();
		if(currentPlayer == player1) {
			player2Label.setForeground(Color.BLACK);
			player1Label.setForeground(Color.RED);
		}
		else if(currentPlayer == player2 || currentPlayer == playerCPU) {
			player1Label.setForeground(Color.BLACK);
			player2Label.setForeground(Color.RED);
		}
		addButtons();
		
		c.setVisible(true);
		if(compFlag) {			//when CPU is X, it plays first
			if(playerCPU.playerCharFlag == 'X')
				compPlays();
		}
	}
	
	
	//Play move in the best spot
	void compPlays() {
		compSearchResult = findRow(currentPlayer.playerCharFlag);			//complete a row and win if possible
		if(compSearchResult < 0)
			compSearchResult = findRow(player1.playerCharFlag);				//or try to block Player 1 from winning
		if(compSearchResult < 0)
			do {
				compSearchResult = random.nextInt(9);						//otherwise move randomly
			} while(position[compSearchResult] != BLANK);
		
		compMove();
	}
	
	
	void compMove() {				//Plays the move of the Computer determined in the above method compPlays()
		JButton button = (JButton) b[compSearchResult];
		int storedCompSearchResult = compSearchResult;
		
		new NestedDialog(this);
		
		updateBottomLabel();
		
		if(winnerNestedCharFlag == ' ') {
			winnerNestedCharFlag = currentPlayer.playerCharFlag;
			nestedReset();
		}

		if(winnerNestedCharFlag == 'X') {
			button.setIcon(new ImageIcon(xIcon.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));
			button.setDisabledIcon(button.getIcon());
			button.setEnabled(false);
			position[(int) b[storedCompSearchResult].getClientProperty("position")] = X;
		}
		else if(winnerNestedCharFlag == 'O') {
			button.setIcon(new ImageIcon(oIcon.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH)));
			button.setDisabledIcon(button.getIcon());
			button.setEnabled(false);
			position[(int) b[storedCompSearchResult].getClientProperty("position")] = O;
		}
		
		button.setContentAreaFilled(false);
		button.setBorder(BorderFactory.createEmptyBorder());
		
		if(winnerNestedCharFlag == currentPlayer.playerCharFlag) {
			if(won(currentPlayer.playerCharFlag))
				announceWinner(currentPlayer.playerFlag);
			else if(isDraw())
				announceDraw();
		}
		else if(winnerNestedCharFlag == player1.playerCharFlag) {
				if(won(player1.playerCharFlag))
					announceWinner(player1.playerFlag);
				else if(isDraw())
					announceDraw();
		}
		
		winnerNestedCharFlag = ' ';
		
		currentPlayer = player1;
		player2Label.setForeground(Color.BLACK);
		player1Label.setForeground(Color.RED);
	}
	
	
	//returns position of a blank spot in a row if the other 2 spots are occupied by playerCharFlag and the third is blank, or -1 if no spot exists
	int findRow(char playerCharFlag) {			
		for(int i = 0; i < 8; i++) {
			int searchResult = testingRowsForBestCase(playerCharFlag, winningEndPoints[i][0], winningEndPoints[i][1]);
			if(searchResult >= 0)
				return searchResult;
		}
		return -1;
	}
	
	
	//if 2 of 3 spots are occupied by playerCharFlag from position a, b, c and the third spot is Blank,
	//returns the index of the blank spot or else returns -1
	int testingRowsForBestCase(char playerCharFlag, int a, int b) {
		int c = (a+b)/2;
		if(! nestedFlag) {
			if(position[a] == playerCharFlag && position[b] == playerCharFlag && position[c] == BLANK)
				return c;
			if(position[a] == playerCharFlag && position[c] == playerCharFlag && position[b] == BLANK)
				return b;
			if(position[b] == playerCharFlag && position[c] == playerCharFlag && position[a] == BLANK)
				return a;
			return -1;
		}
		else {
			if(positionNested[a] == playerCharFlag && positionNested[b] == playerCharFlag && positionNested[c] == BLANK)
				return c;
			if(positionNested[a] == playerCharFlag && positionNested[c] == playerCharFlag && positionNested[b] == BLANK)
				return b;
			if(positionNested[b] == playerCharFlag && positionNested[c] == playerCharFlag && positionNested[a] == BLANK)
				return a;
			return -1;
		}
	}
	
	
	public void updateBottomLabel() {
		c.remove(southWrapper);
		c.setVisible(false);
		addBottomLabel();
		c.setVisible(true);
	}
	
	
	public void reset() {			//disposes the window and creates a new one, all the fields are reseted
		dispose();
		
		compFlag = false;
		player1Wins = player2Wins = cpuWins = 0;
		new NestedTicTacToe();
	}
	
	
	class Player {					//create object Player
		int playerFlag;

		char playerCharFlag;
		
		public Player(int playerFlag, char playerCharFlag) {
			this.playerFlag = playerFlag;
			this.playerCharFlag = playerCharFlag;
		}
	}
	
	class NestedDialog extends JDialog{
		private static final long serialVersionUID = 1L;

		public NestedDialog(JFrame frame) {
			super(frame, true);			//super(owner, modal) Modality is necessary as the flow of the main window pauses until the dialog is done executing
			dialogThis = this;			//reference of this dialog
			nc = getContentPane();
			
			nestedFlag = true;
			setSize(250, 250);
			setLayout(new BorderLayout());
			
			//Player objects are copied onto new variables for nested calculations
			player1Nested = player1;
			if(! compFlag)
				player2Nested = player2;
			else
				playerCPUNested = playerCPU;
			currentNestedPlayer = currentPlayer;
			
			addNestedLabels();
			addNestedButtons();
			
			//highlights the current player's label
			if(currentNestedPlayer == player1Nested) {
				player2NestedLabel.setForeground(Color.BLACK);
				player1NestedLabel.setForeground(Color.RED);
			}
			else {
				player1NestedLabel.setForeground(Color.BLACK);
				player2NestedLabel.setForeground(Color.RED);
			}
			
			if(compFlag) 						//if cpu is the first player, invokes the nestedCompPlays()
				if(currentNestedPlayer.playerFlag == playerCPUNested.playerFlag)
					nestedCompPlays();
			
			setIconImage(xoxoIcon);
			setTitle("Nested XOXO");
			setVisible(true);
		}
	}
	
	
	class NestedAction extends AbstractAction{					//ActionPerformed when button is clicked in Nested Window
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(newNestedGameFlag)
				newNestedGameFlag = false;
				
			JButton nestedButton = (JButton) e.getSource();
			nestedButtonWidth = nestedButton.getWidth();
			nestedButtonHeight = nestedButton.getHeight();
			
			if(currentNestedPlayer.playerCharFlag == 'X') {
				nestedButton.setIcon(new ImageIcon(xIcon.getScaledInstance(nestedButtonWidth, nestedButtonHeight, Image.SCALE_SMOOTH)));
				nestedButton.setDisabledIcon(nestedButton.getIcon());
				nestedButton.setEnabled(false);
				positionNested[(int) nestedButton.getClientProperty("position")] = X;
			}
			else if(currentNestedPlayer.playerCharFlag == 'O') {
				nestedButton.setIcon(new ImageIcon(oIcon.getScaledInstance(nestedButtonWidth, nestedButtonHeight, Image.SCALE_SMOOTH)));
				nestedButton.setDisabledIcon(nestedButton.getIcon());
				nestedButton.setEnabled(false);
				positionNested[(int) nestedButton.getClientProperty("position")] = O;
			}

			nestedButton.setContentAreaFilled(false);
			nestedButton.setBorder(BorderFactory.createEmptyBorder());
			
			if(won(currentNestedPlayer.playerCharFlag)) {
				winnerNestedCharFlag = currentNestedPlayer.playerCharFlag;
				nestedReset();
				
				if(winnerNestedCharFlag == player1Nested.playerCharFlag) {
					++player1Wins;
					if(winnerNestedCharFlag == X)
						JOptionPane.showMessageDialog(dialogThis, "Wins", "Player 1 Won!!!", JOptionPane.INFORMATION_MESSAGE, xImageIcon);
					else
						JOptionPane.showMessageDialog(dialogThis, "Wins", "Player 1 Won!!!", JOptionPane.INFORMATION_MESSAGE, oImageIcon);
				}
				else {
					++player2Wins;
					if(winnerNestedCharFlag == X)
						JOptionPane.showMessageDialog(dialogThis, "Wins", "Player 2 Won!!!", JOptionPane.INFORMATION_MESSAGE, xImageIcon);
					else
						JOptionPane.showMessageDialog(dialogThis, "Wins", "Player 2 Won!!!", JOptionPane.INFORMATION_MESSAGE, oImageIcon);
				}
				dialogThis.dispose();
			}
			else if(isDraw()) {
				winnerNestedCharFlag = currentPlayer.playerCharFlag;
				++draws;
				nestedReset();
				JOptionPane.showMessageDialog(dialogThis, "Draw", "Match Drawn", JOptionPane.INFORMATION_MESSAGE, xoImageIcon);
				dialogThis.dispose();
			}
			
			/*
			 * The newNestedGameFlag is necessary while playing against the computer,
			 * When the 1st game is completed and the 2nd game is started,
			 * the nestedCompPlays() method is invoked before this method is completed.
			 * And if a flag is not set, the currentNestedPlayer variable gets changed before the nestedCompPlays() method can display its output and it leads to unexpected results.
			 */
			if(!newNestedGameFlag) {
				if(! compFlag) {
					if(currentNestedPlayer == player1Nested) {
						currentNestedPlayer = player2Nested;
						player1NestedLabel.setForeground(Color.BLACK);
						player2NestedLabel.setForeground(Color.RED);
					}
					else {
						currentNestedPlayer = player1Nested;
						player2NestedLabel.setForeground(Color.BLACK);
						player1NestedLabel.setForeground(Color.RED);
					}
				}
				else {
					currentNestedPlayer = playerCPUNested;
					player1NestedLabel.setForeground(Color.BLACK);
					player2NestedLabel.setForeground(Color.RED);
	
					nestedCompPlays();				//the computer plays
				}
			}
		}
	}
	
	
	public void addNestedButtons() {
		centerNestedWrapper = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				super.paintComponents(g);
				Graphics2D g2d = (Graphics2D) g;
				int w = getWidth();
			    int h = getHeight();
			    g2d.setPaint(new Color(230, 230, 230));
			    g2d.fill(new Rectangle2D.Double(0, 0, w, h));
			    g2d.setPaint(Color.BLACK);
			    g2d.setStroke(new BasicStroke(3));
			    g2d.draw(new Line2D.Double(0, h/3, w, h/3));
			    g2d.draw(new Line2D.Double(0, h*2/3, w, h*2/3));
			    g2d.draw(new Line2D.Double(w/3, 0, w/3, h));
			    g2d.draw(new Line2D.Double(w*2/3, 0, w*2/3, h));
			}
		};      				 //paint component draws the lines in between the gridLayout and also sets a background
		
		centerNestedWrapper.setLayout(new GridLayout(3, 3, 3, 3));
		for(int i = 0; i < 9; i++) {
			bNested[i] = new JButton();
			bNested[i].addActionListener(new NestedAction());
			bNested[i].putClientProperty("position", i);
			centerNestedWrapper.add(bNested[i]);
		}
		nc.add(centerNestedWrapper, BorderLayout.CENTER);
	}
	
	
	public void addNestedLabels() {
		northNestedWrapper = new JPanel();
		northNestedWrapper.setLayout(new BorderLayout());
		
		//Displaying Player 1 Label with its current mark
		if(player1.playerCharFlag == 'X') {
			player1NestedLabel = new JLabel("  Player 1:", new ImageIcon(xIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
		}
		else if(player1.playerCharFlag == 'O') {
			player1NestedLabel = new JLabel("  Player 1:", new ImageIcon(oIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
		}
		player1NestedLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		
		//Displaying Player2/CPU Label with its current mark
		if(! compFlag) {
			if(player2.playerCharFlag == 'X') {
				player2NestedLabel = new JLabel("Player 2:", new ImageIcon(xIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
			}
			else if(player2.playerCharFlag == 'O') {
				player2NestedLabel = new JLabel("Player 2:", new ImageIcon(oIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
			}
		}
		else {
			if(playerCPU.playerCharFlag == 'X') {
				player2NestedLabel = new JLabel("CPU:", new ImageIcon(xIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH)), SwingConstants.TRAILING);
			}
			else if(playerCPU.playerCharFlag == 'O') {
				player2NestedLabel = new JLabel("CPU:", new ImageIcon(oIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH)), SwingConstants.TRAILING);
			}
		}
		player2NestedLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		
		northNestedWrapper.add(player1NestedLabel, BorderLayout.WEST);  
		northNestedWrapper.add(player2NestedLabel, BorderLayout.EAST);  
		nc.add(northNestedWrapper, BorderLayout.NORTH);
	}
	
	
	public void nestedReset() {
		newNestedGameFlag = true;
		nestedFlag = false;
		for(int i = 0; i < 9; i++) {
			positionNested[i] = BLANK;
		}
	}
	
	
	//Play move in the best spot
	void nestedCompPlays() {
		compSearchResult = findRow(currentNestedPlayer.playerCharFlag);			//complete a row and win if possible
		if(compSearchResult < 0)
			compSearchResult = findRow(player1Nested.playerCharFlag);				//or try to block Player 1 from winning
		if(compSearchResult < 0)
			do {
				compSearchResult = random.nextInt(9);						//otherwise move randomly
			} while(positionNested[compSearchResult] != BLANK);
		
		nestedCompMove();
	}
	
	
	void nestedCompMove() {				//Plays the move of the Computer determined in the above method compPlays()
		JButton nestedButton = (JButton) bNested[compSearchResult];
		
		/*
		 * Both the nestedButton width are height is set with a value of 60, 
		 * because if the player cancels the nested window during the first move, the nestedButton dimensions are not recorded and are 0 by default
		 * Hence setting a value of 60, if values are 0, to avoid error.
		 */
		if(nestedButtonWidth == 0 || nestedButtonHeight == 0)
			nestedButtonWidth = nestedButtonHeight = 60;
		
		if(currentNestedPlayer.playerCharFlag == 'X') {
			nestedButton.setIcon(new ImageIcon(xIcon.getScaledInstance(nestedButtonWidth, nestedButtonHeight, Image.SCALE_SMOOTH)));
			nestedButton.setDisabledIcon(bNested[compSearchResult].getIcon());
			nestedButton.setEnabled(false);
			positionNested[(int) bNested[compSearchResult].getClientProperty("position")] = X;
		}
		else if(currentNestedPlayer.playerCharFlag == 'O') {
			nestedButton.setIcon(new ImageIcon(oIcon.getScaledInstance(nestedButtonWidth, nestedButtonHeight, Image.SCALE_SMOOTH)));
			nestedButton.setDisabledIcon(bNested[compSearchResult].getIcon());
			nestedButton.setEnabled(false);
			positionNested[(int) bNested[compSearchResult].getClientProperty("position")] = O;
		}
		
		nestedButton.setContentAreaFilled(false);
		nestedButton.setBorder(BorderFactory.createEmptyBorder());
		
		if(won(currentNestedPlayer.playerCharFlag)) {
			winnerNestedCharFlag = currentNestedPlayer.playerCharFlag;
			nestedReset();
			++cpuWins;
			
			if(winnerNestedCharFlag == X)
				JOptionPane.showMessageDialog(dialogThis,"Wins", "CPU Won!!!", JOptionPane.INFORMATION_MESSAGE, xImageIcon);
			else
				JOptionPane.showMessageDialog(dialogThis, "Wins", "CPU Won!!!", JOptionPane.INFORMATION_MESSAGE, oImageIcon);
			
			dialogThis.dispose();
		}
		else if(isDraw()) {
			winnerNestedCharFlag = currentPlayer.playerCharFlag;
			++draws;
			
			nestedReset();
			JOptionPane.showMessageDialog(dialogThis, "Draw", "Match Drawn", JOptionPane.INFORMATION_MESSAGE, xoImageIcon);

			dialogThis.dispose();
		}
		
		currentNestedPlayer = player1Nested;
		player2NestedLabel.setForeground(Color.BLACK);
		player1NestedLabel.setForeground(Color.RED);
	}
		
}
