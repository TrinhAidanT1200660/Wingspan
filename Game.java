
import java.util.*;

public class Game {

    private int startingActionCubes; // holds the beginning of the rounds starting action cubes; starts at 8 beginning of game
    private int playerTurn; // holds which player's turn it current is; starts at 1 beginning of game
    private int startingPlayerTurn; // holds the player who had their turn first; starts at 1 beginning of game
    private boolean isCompetitive; // holds the gamemode being played: true for competitive, false for non-competitive
    private int roundsPlayed; // holds the number of rounds played so far; starts at 0 beginning of game
    private ArrayList<Player> playerList; // holds the list of players in the game
	private TreeSet<Selectable> selected; // temporarily holding the items selected pre-game (birds/food tokens)

    // CONSTRUCTOR
    public Game(boolean isCompetitive) {
        this.startingActionCubes = 8;
        this.playerTurn = 1;
        this.startingPlayerTurn = 1;
        this.roundsPlayed = 0;
        this.playerList = new ArrayList<>();
		this.selected = new TreeSet<>();
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

	private void deselect(Selectable element) { // deselects a specific selectable item
		if (element != null) {
			element.getElement().setAttribute("Selected", false);
			selected.remove(element);
			((Runnable)(element.getElement().getAttribute("Deselect"))).run();
		}
	}

	public void deselect(UIElement element) { // deselects a selectable item based on its UIElement
		Selectable found = null;
		for (Selectable s : selected) {
			if (s.getElement().equals(element)) {
				found = s;
				break;
			}
    	}
		deselect(found);
	}

	public void select(UIElement element) {
		element.setAttribute("Selected", true);
		selected.add(new Selectable(element.getAttribute("selectionValue"), element));
		((Runnable)(element.getAttribute("Select"))).run();
		handleSelected();
	}

	public void toggleSelect(UIElement element) {
		Object selectedAttr = element.getAttribute("Selected");
		if (selectedAttr != null && (boolean)selectedAttr == true) {
			deselect(element);
		} else {
			select(element);
		}
	}

	public boolean canContinueResources() {
		return selected.size() == 5;
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

	public String toString() {
		return "Selectable(value=" + value.toString() + ", element=" + element.getName() + ")";
	}
}