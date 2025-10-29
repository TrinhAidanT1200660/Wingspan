import java.util.*;

public class Game {
	private int startingActionCubes; // holds the beginning of the rounds starting action cubes; starts at 8 beginning of game
	private int playerTurn; // holds which player's turn it current is; starts at 1 beginning of game
	private int startingPlayerTurn; // holds the player who had their turn first; starts at 1 beginning of game
	private boolean gameMode; // holds the gamemode being played: true for competitive, false for non-competitive
	private int roundsPlayed; // holds the number of rounds played so far; starts at 0 beginning of game
	private ArrayList<Player> playerList; // holds the list of players in the game

	// CONSTRUCTOR
	public Game()
	{
		this.startingActionCubes = 8;
		this.playerTurn = 1;
		this.startingPlayerTurn = 1;
		this.roundsPlayed = 0;
		this.playerList = new ArrayList<>();
		for(int i = 0; i < 5; ++i) { playerList.add(new Player()); }
	}

	// GAME METHODS

	

	// RETURN METHODS

	public ArrayList<Player> getPlayers() { return playerList; }

	// MUTATOR METHODS

	public void setGameMode(Boolean gameMode) { this.gameMode = gameMode; }
}
