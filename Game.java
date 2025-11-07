
import java.util.*;

public class Game {

    private int startingActionCubes; // holds the beginning of the rounds starting action cubes; starts at 8 beginning of game
    private int playerTurn; // holds which player's turn it current is; starts at 1 beginning of game
    private int startingPlayerTurn; // holds the player who had their turn first; starts at 1 beginning of game
    private boolean isCompetitive; // holds the gamemode being played: true for competitive, false for non-competitive
    private int roundsPlayed; // holds the number of rounds played so far; starts at 0 beginning of game
    private ArrayList<Player> playerList; // holds the list of players in the game
	private TreeSet<Selectable> selected; // temporarily holding the items selected pre-game (birds/food tokens)
	private int selectionPhase;
	private int gamePhase; // what point are we in during the game
	private ArrayList<String> birdFeeder; // replicates a bird feeder using a simple arrayList
	private ArrayList<Bird> faceUpBirds; // replicates the 3 face up bird cards in the bird tray ; not sure when we want to create this, before or after player select resources

    // CONSTRUCTOR
    public Game(boolean isCompetitive) {
        this.startingActionCubes = 8;
        this.playerTurn = 1;
        this.startingPlayerTurn = 1;
        this.roundsPlayed = 0;
        this.playerList = new ArrayList<>();
		this.gamePhase = 0;
		this.selected = new TreeSet<>();
		this.selectionPhase = 1;
		this.isCompetitive = isCompetitive;
        for (int i = 0; i < 5; ++i) {
            playerList.add(new Player());
        }
		this.rollBirdFeeder();
		this.faceUpBirds = new ArrayList<>();
    }

    // GAME | VOID METHODS

	// Simulates rolling the birdFeeder to generate 5 random food dies
	public void rollBirdFeeder() {
		final String[] foods = {"berry", "fish", "rat", "seed", "worm"};
		ArrayList<String> rolledFoods = new ArrayList<>();
		for(int i = 0; i < 5; ++i)
		{
			int randFood = (int) (Math.random() * foods.length);
			rolledFoods.add(foods[randFood]);
		}

		birdFeeder = rolledFoods;
	}

	// Restores the faceup pile with new bird cards, keeps the old ones
	public void regenerateFaceUpTray() {
		int amount = 3 - faceUpBirds.size();
		ArrayList<Bird> cards = pullRandomBirds(amount);
		for(Bird b: cards)
			faceUpBirds.add(b);
	}

	// Clears the faceup pile completely before adding 3 new bird cards
	public void clearAndRegenerateFaceUpTray() {
		faceUpBirds.clear();
		ArrayList<Bird> cards = pullRandomBirds(3);
		for(Bird b: cards)
			faceUpBirds.add(b);
	}

	// Directly removes card from faceup pile and adds to the player
	public void grabFaceUpCard(int index, Player player) {
		 if (index < 0 || index >= faceUpBirds.size()) { System.out.println("Out of bound face up pile index"); return; } // safety check should nto be needed
		player.addBirdHand(faceUpBirds.remove(index));
	}

    // RETURN METHODS
    public ArrayList<Player> getPlayers() {
        return playerList;
    }

	// Randomly draws bird card to simulate the random drawing, has no remove card rn because lack of bird cards
	// I NEED TO MAKE THIS HAVE SIZE BUT WILL DO LATER TO TELL MOHAMMED TO MAKE SURE HE CAN CHANGE IT IN UI
	public ArrayList<Bird> pullRandomBirds(int amount) {
		Bird[] allBirds = Bird.values();
		ArrayList<Bird> returning = new ArrayList<>();

		// makes sure there are cards available
		int availableCards = 0;
		for (Bird card : allBirds)
        	availableCards += card.getDeckCount();

		// just sends a message in case we're testing and wondering what went wrong
		if (amount > availableCards) System.out.println("Ran out of bird cards"); 
	
		amount = Math.min(amount, availableCards);

		while (returning.size() < amount) { 
			int randCard = (int) (Math.random() * allBirds.length);
			/* if(allBirds[randCard].getDeckCount() > 0) {
				allBirds[randCard].removeCardFromDeck();
			} */
			returning.add(allBirds[randCard]); // this has to be in that getDeckCount if statement once actually implemented SUPER IMPORTANT
		}
		return returning;
	}

	// Randomly draws bonus cards to simulate the random drawing.
	public ArrayList<BonusCard> pullRandomBonusCards(int amount)
	{
		BonusCard[] allBonuses = BonusCard.values();
		ArrayList<BonusCard> returning = new ArrayList<>();

		// makes sure there are cards available
		int availableCards = 0;
		for (BonusCard card : allBonuses)
        	availableCards += card.getDeckCount();

		// just sends a message in case we're testing and wondering what went wrong
		if (amount > availableCards) System.out.println("Ran out of bonus cards");

		amount = Math.min(amount, availableCards);
		
		while(returning.size() < amount) {
			while (true) {
				int randCard = (int) (Math.random() * allBonuses.length);
				if(allBonuses[randCard].getDeckCount() > 0) {
					allBonuses[randCard].removeCardFromDeck();
					returning.add(allBonuses[randCard]);
					break;
				}
			}
		}
		return returning;
	}

	// Returns the face up bird card tray
	public ArrayList<Bird> getFaceUpTray() {
		return faceUpBirds;
	}

	// Checks if the birdFeeder has the food type and if so adds it to player. Returns boolean to show whether or not food was actually grabbed.
	public boolean grabFood(String food, Player player, int amt)
	{
		boolean atLeast1Grabbed = false;
		for(int i = 0; i < amt; ++i) {
			if(birdFeeder.contains(food))
			{
				player.addFood(food, 1);
				birdFeeder.remove(food);
				atLeast1Grabbed = true;
			}
		}
		return atLeast1Grabbed;
	}

	// Returns the birdFeeder
	public ArrayList<String> getBirdFeeder()
	{
		return birdFeeder;
	}

	private void handleSelected() { // if the user selected more than 5 things deselect the least recent thing selected (could be bird or food token)
		if (selectionPhase == 1 ? selected.size() > 5 : selected.size() > 1) { // if selected amounts went over limit (5 for birds/foods, 1 for bonus cards)
			Selectable first = selected.first(); // remove the least recent selection
			first.getElement().setAttribute("Selected", false);
			selected.remove(first);
			((Runnable)(first.getElement().getAttribute("Deselect"))).run();
		}
	}

	private void deselect(Selectable element) { // deselects a specific selectable item
		System.out.println(element);
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

	public boolean canContinueResources() { return selectionPhase == 1 ? selected.size() == 5 : selected.size() == 1; }

	public int getSelectionPhase() { return selectionPhase; }

	public void incrementPlayerTurn() { playerTurn = playerTurn % 5 + 1; }

	public boolean continueSelection() {
		if (!canContinueResources()) return false;
		selectionPhase = (selectionPhase % 2) + 1;
		Player current = playerList.get(playerTurn - 1);
		if (selectionPhase == 1) {
			incrementPlayerTurn();
			current.addBonusHand((BonusCard)selected.first().getValue());
			deselect(selected.last());
			if (playerTurn > playerList.size()) {
				// here we move onto actual board 
			}
		} else if (selectionPhase == 2) {
			for (Selectable selection : selected) {
				UIElement element = selection.getElement();
				if (element.getAttribute("birdChoice") != null) {
					current.addBirdHand((Bird)selection.getValue());
				} else if (element.getAttribute("foodChoice") != null) {
					current.addFood((String)selection.getValue(), 1);
				}
			}
			for (int i = 0; i < 5; i++)
				deselect(selected.last());
		}

		selected.clear();
		return true;
	}

	public int getPlayerTurn() { return playerTurn; }
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

    public int compareTo(Selectable o) { return Long.compare(added, o.added); }
}