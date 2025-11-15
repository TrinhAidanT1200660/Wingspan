import java.util.*;

public class Game {

	private WingspanPanel panel;
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
	private ArrayList<Goals> goalBoard; // replicates the 4 goals on the goal board ; should be fine to create at game creation

    // CONSTRUCTOR
    public Game(WingspanPanel panel) 
	{
		this.panel = panel;
        this.startingActionCubes = 8;
        this.playerTurn = 1;
        this.startingPlayerTurn = 1;
        this.roundsPlayed = 0;
        this.playerList = new ArrayList<>();
		this.gamePhase = 0;
		this.selected = new TreeSet<>();
		this.selectionPhase = 1;
        for (int i = 0; i < 5; ++i)
            playerList.add(new Player());
		this.birdFeeder = new ArrayList<>();
		this.rollBirdFeeder(); // rolls bird feeder
		this.faceUpBirds = new ArrayList<>();
		this.goalBoard = new ArrayList<>();
		this.selectGoals(); // selects goals; shouldn't be recalled
    }

    // GAME | VOID METHODS

	// Simulates randomly choosing goals without repeats
	public void selectGoals()
	{
		List<Goals> list = new ArrayList<>(Arrays.asList(Goals.values()));
		Collections.shuffle(list);
		goalBoard.clear();
		goalBoard.addAll(list.subList(0, 4));
	}

	// Simulates going down the habitat's row of bird abilities and activating all the brown abilities
	public void iterateBirdAbilities(Player player, String habitat) {
		ArrayList<BirdInstance> birds = player.getBoard().get(habitat);
		for(int i = birds.size()-1; i >= 0; --i) // goes backwards, replicates right to left behavior on board
		{
			BirdInstance bird = birds.get(i);
			if(bird.getActionColor().equalsIgnoreCase("BROWN")) // checks if it's a brown ability
			{
				// UI should popup a yes or no asking whether player desires to activate the ability
				// For now, the boolean will be true and ability will activate
				boolean activate = true;
				if(activate)
					bird.performAction(this, player);
			}
		}
	}

	// Simulates rolling the birdFeeder to generate 5 random food dies
	public void rollBirdFeeder() {
		final String[] foods = {"berry", "fish", "rat", "seed", "worm", "seed/worm"};
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
	public ArrayList<Bird> pullRandomBirds(int amount)
	{
		Bird[] allBirds = Bird.values();
		ArrayList<Bird> deck = new ArrayList<>();
		for(Bird card: allBirds)
			if(card.getDeckCount() > 0)	
				deck.add(card);

		// makes sure there are cards available
		int availableCards = deck.size();
		// just sends a message in case we're testing and wondering what went wrong
		if (amount > availableCards) System.out.println("Ran out of bird cards");
		amount = Math.min(amount, availableCards);

		Collections.shuffle(deck);

		ArrayList<Bird> returning = new ArrayList<>(deck.subList(0, amount));

		for(Bird c : returning)
			c.removeCardFromDeck();
			
		return returning;
	}

	// Calculates final scores for all players and returns a hashmap of the different score types
	public HashMap<String, Integer> calculateFinalScores(Player player) {
		HashMap<String, Integer> scores = new HashMap<>(); // hashmap of the various scoring types and their values
		scores.put("bonus", 0);

		//ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);

		// checks each bird on the board and adds up their score for that specific type and adds to the hashmap
		int birdPoints = 0;
		int eggPoints = 0;
		int foodPoints = 0;
		int tuckedPoints = 0;
		for (BirdInstance bird : birdSuperList) {
			birdPoints += bird.getPointValue();
			eggPoints += bird.getEggStored();
			foodPoints += bird.getCachedFoodAmount();
			tuckedPoints += bird.getTuckedAmount();
		}

		int endOfRoundPoints = player.getPoints(); // the points from end of round goals should be added to player points at the end of each round i think
		scores.put("birds", birdPoints);
		scores.put("eggs", eggPoints);
		scores.put("food", foodPoints);
		scores.put("tucked", tuckedPoints);
		scores.put("endOfRound", player.getPoints());

		for(BonusCard b: player.getBonusHand())
			b.bonusScore(player);

		int bonusCardPoints = player.getPoints() - endOfRoundPoints; // bonus cards directly add points so we can just subtract to get their value
		scores.put("bonus", bonusCardPoints);

		return scores;
	}

	// Randomly draws bonus cards to simulate the random drawing.
	public ArrayList<BonusCard> pullRandomBonusCards(int amount)
	{
		BonusCard[] allBonuses = BonusCard.values();
		ArrayList<BonusCard> deck = new ArrayList<>();
		for(BonusCard card: allBonuses)
			if(card.getDeckCount() > 0)	
				deck.add(card);

		// makes sure there are cards available
		int availableCards = deck.size();
		// just sends a message in case we're testing and wondering what went wrong
		if (amount > availableCards) System.out.println("Ran out of bonus cards");
		amount = Math.min(amount, availableCards);

		Collections.shuffle(deck);

		ArrayList<BonusCard> returning = new ArrayList<>(deck.subList(0, amount));
		for(BonusCard c : returning)
			c.removeCardFromDeck();

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
			else if((food.equals("seed") || food.equals("worm")) && birdFeeder.contains("seed/worm"))
			{
				player.addFood(food, 1);
				birdFeeder.remove("seed/worm");
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

	// returns a boolean that says whether or not the birdFeeder is eligible for reroll
	public boolean birdFeederEligibleForReroll()
	{
		for(int i = 0; i < birdFeeder.size() - 1; i++) // auto checks if there's one; no need to manually check
			if(!birdFeeder.get(i).equals(birdFeeder.get(i+1)))
				return false;

		return true;
	}

	public void UIMouseReleased(RootMouseEvent event, UIElement released)
	{
        if (released.getAttribute("startButton") != null)
		{
			panel.playTransition((Runnable)() -> {
				setCompetitiveType(released == UIElement.getByName("CompetitiveButtonBg"));
				giveUIBirds(5);
				panel.clickedStart(event, released);
			});
    	} 

        else if (released.getAttribute("birdChoice") != null || released.getAttribute("foodChoice") != null || released.getAttribute("bonusChoice") != null)
		{
        	toggleSelect(released);
			panel.clickedResource(event, released, canContinueResources());
            
        }

		if (released == UIElement.getByName("ContinueResourcesButtonBg")) 
		{
            Object ready = UIElement.getByName("ContinueResourcesButtonBg").getAttribute("Clickable");
            if (ready != null && (boolean)ready) 
            {
				panel.playTransition((Runnable)() -> { // plays transition
					selectionPhase = (selectionPhase % 2) + 1; // updates selection phase (can only be 1 or 2)
					panel.clickedResourceContinue(event, released, selectionPhase == 1); // updates screen
					Player current = playerList.get(playerTurn - 1); // get current player
					if (getSelectionPhase() == 1) // if reset selection phase back to the 1st one
					{
						incrementPlayerTurn(); // now its the next players turn to select
						current.addBonusHand((BonusCard)selected.first().getValue()); // add previous players bonus card selection
						deselect(selected.last()); // remove from selected
						if (playerTurn == 1) { // if new player is back to 1 then
							// here we move onto actual board 
						} else { // else if we're not done choosing yet
							// update player title to show the turn
							UIText playerChoosingTitle = (UIText)(UIElement.getByName("PlayerChoosingTitle"));
							playerChoosingTitle.text = "Player " + getPlayerTurn();
							giveUIBirds(5); // give next player bird choices
						}
					} else if (getSelectionPhase() == 2) // if next phase (bonus cards)
					{
						// give player their bird and food selections
						for (Selectable selection : selected) {
							UIElement element = selection.getElement();
							if (element.getAttribute("birdChoice") != null) {
								current.addBirdHand((Bird)selection.getValue());
							} else if (element.getAttribute("foodChoice") != null) {
								current.addFood((String)selection.getValue(), 1);
							}
						}
						for (int i = 0; i < 5; i++) // deselect everything since we dont need it anymore
							deselect(selected.last());
						giveUIBonus(2); // draw 2 bonus cards to be able to be chosen
					}
				});
            }
        }
	}

	public void giveUIBonus(int num)
	{
		ArrayList<BonusCard> randomBonus = this.pullRandomBonusCards(num);
        for (int i = 0; i < randomBonus.size(); i++) 
		{
            String imageFileString = "bonus/" + randomBonus.get(i).getImage();
            ImageHandler.setGroup(imageFileString, "Bonus");
            UIImage bonusImage = (UIImage)(UIElement.getByName("Bonus" + i));
            bonusImage.setAttribute("selectionValue", randomBonus.get(i));
            bonusImage.setImagePath(imageFileString);
        }
	}

	public void giveUIBirds(int num)
	{
		ArrayList<Bird> randomBirds = this.pullRandomBirds(num);
        for (int i = 0; i < randomBirds.size(); i++) 
		{
            String imageFileString = randomBirds.get(i).getImage();
            ImageHandler.setGroup(imageFileString, "BirdChoiceCards");
            UIImage birdImage = (UIImage)(UIElement.getByName("Bird" + i));
            birdImage.setAttribute("selectionValue", randomBirds.get(i));
            birdImage.setImagePath(imageFileString);
        }
	}

	public void setCompetitiveType(boolean isCompetitive) {this.isCompetitive = isCompetitive;}

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
