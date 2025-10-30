
import java.util.*;

public class Game {

    private int startingActionCubes; // holds the beginning of the rounds starting action cubes; starts at 8 beginning of game
    private int playerTurn; // holds which player's turn it current is; starts at 1 beginning of game
    private int startingPlayerTurn; // holds the player who had their turn first; starts at 1 beginning of game
    private boolean isCompetitive; // holds the gamemode being played: true for competitive, false for non-competitive
    private int roundsPlayed; // holds the number of rounds played so far; starts at 0 beginning of game
    private ArrayList<Player> playerList; // holds the list of players in the game
	private TreeSet<Selectable> selected = new TreeSet<>(); // temporarily holding the items selected pre-game (birds/food tokens)

    // CONSTRUCTOR
    public Game(boolean isCompetitive) {
        this.startingActionCubes = 8;
        this.playerTurn = 1;
        this.startingPlayerTurn = 1;
        this.roundsPlayed = 0;
        this.playerList = new ArrayList<>();
		this.isCompetitive = isCompetitive;
        for (int i = 0; i < 5; ++i) {
            playerList.add(new Player());
        }
    }

    // GAME METHODS
    // RETURN METHODS
    public ArrayList<Player> getPlayers() {
        return playerList;
    }

	public ArrayList<Bird> pullRandomBirds() {
		Bird[] allBirds = Bird.values();
		ArrayList<Bird> returning = new ArrayList<>();
		while (returning.size() < 5) { 
			int randCard = (int) (Math.random() * allBirds.length);
			/* if(allBirds[randCard].getDeckCount() > 0) {
				allBirds[randCard].removeCardFromDeck();
			} */
			returning.add(allBirds[randCard]);
		}
		return returning;
	}

	private void handleSelected() { // if the user selected more than 5 things deselect the least recent thing selected (could be bird or food token)
		if (selected.size() > 5) {
			Selectable first = selected.first();
			first.getElement().setAttribute("Selected", false);
			selected.remove(first);
			((Runnable)(first.getElement().getAttribute("Deselect"))).run();
		}
	}

	public void deselect(UIElement element) {
		Selectable found = null;
		for (Selectable s : selected) {
			if (s.getElement().equals(element)) {
				found = s;
				break;
			}
    	}
		if (found != null) {
			found.getElement().setAttribute("Selected", false);
			selected.remove(found);
			((Runnable)(found.getElement().getAttribute("Deselect"))).run();
		}
	}

	public void select(Object value, UIElement element) {
		element.setAttribute("Selected", true);
		selected.add(new Selectable(value, element));
		((Runnable)(element.getAttribute("Select"))).run();
		handleSelected();
	}
}

class Selectable implements Comparable<Selectable> {
	private Object value;
	private long added;
	private UIElement element;
	
	public Selectable(Object value, UIElement element) {
		this.value = value;
		this.added = System.currentTimeMillis();
		this.element = element;
	}

	public Object getValue() { return value; }

	public UIElement getElement() { return element; }

    public int compareTo(Selectable o) {
        return Long.compare(added, o.added);
    }
}