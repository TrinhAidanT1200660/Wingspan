
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

	// end of game computing
	// this method will tick at roundEnd() if roundsPlayed >= 4
	public void gameEnd()
	{
		// once again, should allow the whole game to be viewed while showing the final scoring so the teacher can grade for points
		// not sure how UI will display the final scores but the final scoring method returns a hashmap of the scoring sources while adding to a final score so use that
		for(Player p : playerList)
			p.setFinalScoreMap(calculateFinalScores(p)); // variable in player than has the final map scoring so all scores can be accessed at once to put on scoring source visual
	}

	// end of round computing
	// this method will tick at increment player turn if actionCubes = 0 && startingPlayerTurn == playerTurn
	public void roundEnd()
	{	
		// think we need to allow the whole game to be viewed while showing the goal board so teacher can grade; he took off points if you didn't do so last project
		goalBoard.get(roundsPlayed).determineRankings(playerList, roundsPlayed, isCompetitive); // has the player's rankings determined
		this.roundsPlayed ++; // increments the amount of rounds played
		if(roundsPlayed >= 4) this.gameEnd(); // since rounds played begins at 0 and increments right before, when it hits 4 is when game ends
		// since game hasn't ended yet, game now will begin clearing things that require to do so
		this.clearAndRegenerateFaceUpTray(); // end of rounds has the tray cleared and regenerated
		this.resetAllBirdsStatus(); // makes sure all pink birds are reset just in case
		this.startingPlayerTurn = this.startingPlayerTurn % playerList.size() + 1; // shouldn't need to reset back to 1 but keep it just in case
		for(Player p : this.playerList)
			p.setActionCubes(8 - this.roundsPlayed); // 8 is beginning amount and players lose 1 action cube at the end of each round
	}

	// not sure we need to keep this; i think we should imo
	// acts to begin each player turn
	public void playActions()
	{
		// player has four choices here, the play bird, food, eggs, or draw birds
		// ui will return which choice they pick, should just return a string
		// for now, it'll be just lay eggs
		// string returns should be playBird, getFood, layEggs, drawBirds
		Player p = this.playerList.get(playerTurn-1);
		// while loop is basically only for playBird which is the only one that can end up in failure if the player doesn't have enough eggs or even a bird to play
		while(true)
		{
			String choice = "layEggs";
			// playing a bird does not have brown bird abilities activate
			if(choice.equals("playBird")) if(this.playBird(p)) break; // playBird auto adds it to board and returns true if successfully placed
			else if(choice.equals("getFood")) { this.getFood(p); this.iterateBirdAbilities(p, "forest"); break; }
			else if(choice.equals("layEggs")) { this.layEggs(p); this.iterateBirdAbilities(p, "grassland"); break; }
			else if(choice.equals("drawBirds")) { this.drawBirds(p); this.iterateBirdAbilities(p, "wetland"); break; }
			else System.out.println("ERROR IN PLAYACTIONS, CAN'T FIND ACTION");
		}

		p.decreaseActionCubes(); // player turn has ended and they lose an action cube
		this.regenerateFaceUpTray(); // regens the tray without removing old cards as player turn has ended
		this.incrementPlayerTurn(); // increments player turn
	}

	// method that has the play draw bird cards based on whether they want the face up or random pile
	public void drawBirds(Player p)
	{
		// amount of birds depends on the amount of birds in the wetland habitat
		// if there is an even amount, there is capability of trading an egg for a bird
		int birdGet = 0;
		int birdAmount = p.getBoard().get("wetland").size();
		if(birdAmount < 2) birdGet = 1;
		else if (birdAmount < 5) birdGet = 2;
		else birdGet = 3;
		if(birdAmount % 2 == 0 || birdAmount > 5)
		{
			// UI asks player if they would like to trade
			// for now the trade will be false
			boolean trade = false;
			if(trade)
			{
				// UI has the player remove an egg from a bird using removeEgg
				this.removeEggs(p, 1);
				birdGet ++;
			}
		}
		// will grab one bird at a time to be sequential and have different choices
		for (int i = 0 ; i < birdGet; ++i)
		{
			// UI should have the player pick the bird they want
			// not sure how we want to do this but there are 3 face up cards they can pick and a random draw pile
			// if they pick up a face up card, can just return 0-2 for the index, make sure to not allow choosing indices without cards
			// 3 index can be for random faceup pile
			// this logic can be changed ; for now they will only be able to get a random bird
			int choice = 3;
			if(choice >= 0 && choice <=2) this.grabFaceUpCard(choice, p); // ranges from 0 - 2: the ui method shouldnt return index 2 if there was only 2 cards
			else if(choice == 3) p.addBirdHand(this.pullRandomBirds(1).get(0));
			else System.out.println("ERROR IN DRAWBIRDS METHOD GAME");
		}
	}

	// method that has the player lay eggs on which bird they want
	public void layEggs(Player p)
	{
		// amount of eggs depends on the amount of birds in the grassland habitat
		// if there are an even amount, there is capability of trading a food for egg
		int eggGet = 0;
		int birdAmount = p.getBoard().get("grassland").size();
		if(birdAmount < 2) eggGet = 2;
		else if (birdAmount < 5) eggGet = 3;
		else eggGet = 4;
		if(birdAmount % 2 == 0 || birdAmount > 5)
		{
			// UI asks player if they would like to trade
			// for now the trade will be false
			boolean trade = false;
			if(trade)
			{
				// UI asks player which food they would trade in
				// not sure if it will make sure if the player has the sufficient food in the UI method or here, for now i implement here in case
				while (true)
				{
					String food = "seed"; // left as seed for now but should be returned a value
					if(p.removeFood(food, 1)) break; // auto removes food and returns true if food is removed
				}
				eggGet ++;
			}
		}
		// will lay eggs one at a time to allow eggs to be chosen where it's placed
		for (int i = 0; i < eggGet; ++i)
		{
			// UI should choose the bird
			// not sure if it will make sure if the bird has the sufficient space in the UI method or here, for now i implement here in case
			// have while loop commented to not create errors if called for now
			/*
			while(true)
			{
				BirdInstance bird = ;
				if(bird.addEggs(1)) // auto adds egg and returns true if egg is added
				{
					pinkAbilityActivation("eggLaid"); 
					break; // breaks while loop
				}
			}
			*/
		}
	}

	// method that has the player choose which food they want and then grab it
	// since player can grab multiple foods, it will continue in a sequence until they finish grabbing all they want
	public void getFood(Player p) 
	{
		// amount of food depends on the amount of birds in the forest habitat
		// if there are an even amount, there is capability of trading a bird for food
		int foodGet = 0;
		int birdAmount = p.getBoard().get("forest").size();
		if(birdAmount < 2) foodGet = 1;
		else if (birdAmount < 5) foodGet = 2;
		else foodGet = 3;
		if(birdAmount % 2 == 0 || birdAmount > 5)
		{
			// UI asks player if they would like to trade
			// for now the trade will be false
			boolean trade = false;
			if(trade)
			{
				// UI asks player which bird they would trade in
				// Bird b = ;
				// p.getBirdHand().remove(b);
				foodGet ++;
			}
		}
		// Will grab one food at a time to be sequential and allow rerolls mid action
		for(int i = 0; i < foodGet; ++i)
		{
			// UI should be dynamic and allow them to choose either the food or reroll
			// rerolling can just call rollBirdFeeder(). UI should return the food they chose
			// for now it's seed
			String food = "seed";
			this.grabFood(food, p, 1);
			if(food.equalsIgnoreCase("rat"))
				this.pinkAbilityActivation("ratFoodGrabbed");
		}
	}

	// method that allows the player to choose which birds to remove eggs
	public void removeEggs(Player p, int amount)
	{
		for(int i = 0; i < amount; ++i)
		{
			// UI has player choose a bird with an egg on it, removing one at a time until amount is reached
			// for now, idk just no eggs removed
			// BirdInstance bird = ;
			// bird.removeEggs(1);
		}
	}

	// Checks all player's board to activate the bird's pink ability
	// I would use the BirdActionEnum names but it's honestly easier to just have a key word that is similar
	public void pinkAbilityActivation(String birdA)
	{
		List<String> birdNames = switch (birdA) {
			case "playForestAndGetWorm" -> List.of("EASTERN KINGBIRD");
			case "playGrasslandAndTuck" -> List.of("HORNED LARK"); // these three are separated because they have different activation conditions
			case "playWetlandGetFish" -> List.of("BELTED KINGFISHER");
			case "ifPredatorSucceeds" -> List.of("BLACK VULTURE", "BLACK BILLED MAGPIE", "TURKEY_VULTURE");
			case "eggLaid" -> List.of("AMERICAN AVOCET", "BARROW'S GOLDENEYE", "BRONZED COWBIRD", "BROWN HEADED COWBIRD", "YELLOW BILLED CUCKOO"); // these can be grouped as even tho diff abilities, same activation
			case "ratFoodGrabbed" -> List.of("LOGGERHEAD SHRIKE");
			default -> List.of();
		};
		
		// now directly activates ability after searching through the list
		for(Player p : playerList)
			if(p != playerList.get(playerTurn - 1)) // pink cards only activate on ANOTHER PLAYER's action, cant be your own
				p.getBoard().values().stream()
				.flatMap(List::stream) // makes into list
				.filter(b -> birdNames.contains(b.getName().toUpperCase())) // checks each bird of the player if they have the bird
				// .filter(b -> abilityConfirmation(p, b)). this is a placeholder for the popup method that will ask yes or no question and return a boolean. for now it is just ignored. ignore the method name too
				.forEach(b -> b.performAction(this, p)); // if player has the bird and confirms then activate ability
	}

	// resets all pink birds status to not played yet; used at end of rounds
	public void resetAllBirdsStatus()
	{
		for(Player p : playerList) {
			List<BirdInstance> birds = p.getBoard().values().stream().flatMap(List::stream).toList();
			for(BirdInstance b: birds) b.resetPlayed();
		}
	}

	// resets all pink birds status to not played yet for this singular player; used at the end of turns
	public void resetThisPlayersBirdsStatus(Player p)
	{
		List<BirdInstance> birds = p.getBoard().values().stream().flatMap(List::stream).toList();
		for(BirdInstance b : birds) b.resetPlayed();
	}

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

	// resets all the bonus cards to 1 in deck (allows for redraw)
	public void resetBonusDeckCount()
	{
		List<BonusCard> bonusCards = Arrays.asList(BonusCard.values());
		for(BonusCard b : bonusCards)
			b.resetCardDeckCount();
	}

	// resets all the bird cards to 1 in deck (allows for redraw)
	public void resetBirdDeckCount()
	{
		List<Bird> birdCards = Arrays.asList(Bird.values());
		for(Bird b : birdCards)
			b.resetCardDeckCount();
	}

    // RETURN METHODS
    public ArrayList<Player> getPlayers() {
        return playerList;
    }

	// Randomly draws bird card to simulate the random drawing
	public ArrayList<Bird> pullRandomBirds(int amount) {
		Bird[] allBirds = Bird.values();
		ArrayList<Bird> deck = new ArrayList<>();
		for(Bird card: allBirds)
			if(card.getDeckCount() > 0)	
				deck.add(card);

		// makes sure there are cards available
		int availableCards = deck.size();
		/* no longer doing this comment and limit prevention, just gonna reset all cards to back in deck, commented to keep it here in case we want to use this way instead
		// just sends a message in case we're testing and wondering what went wrong
		if (amount > availableCards) System.out.println("Ran out of bird cards");
		amount = Math.min(amount, availableCards);
		*/

		Collections.shuffle(deck);
		ArrayList<Bird> returning;
		if(availableCards >= amount)
		{
			returning = new ArrayList<>(deck.subList(0, amount));
			for(Bird c : returning)
				c.removeCardFromDeck();
		}
		else
		{
			returning = new ArrayList<>(deck.subList(0, availableCards));
			for(Bird c : returning)
				c.removeCardFromDeck();
			int newAmt = amount - availableCards; // since above grabs the last cards left in deck, this finds the amount left to grab
			this.resetBirdDeckCount(); // resets all bird cards deck count
			returning.addAll(pullRandomBirds(newAmt)); // recursion to do this method again but after resetting all bird cards
		}

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

		// adds up points here while and adds total to map
		int total = 0;
		for (Map.Entry<String, Integer> en : scores.entrySet())
			total += en.getValue();
		scores.put("total", total);
		player.setPoints(total);

		return scores;
	}

	// method that has the player choose which bird and then play it
	public boolean playBird(Player p) 
	{
		if(p.getBirdHand().isEmpty()) return false; // just checks if the hand is empty first before asking which to play
		// UI should be asking the player which bird from their hand to play
		this.showHand(p);
		// for now it'll be the first bird in the hand
		Bird birdToPlay = p.getBirdHand().get(0);
		return addBirdToBoard(p, birdToPlay);
	}

	//adds the specified bird to the board if the player has enough food and the bird is in their hand
    //if it has any food type, UI will ask player to choose which food to use
    //returns true if successful, false otherwise
    public boolean addBirdToBoard(Player p, Bird bird) {
        if(!p.getBirdHand().contains(bird)) return false; // checks if the player acc has the bird; idk how this goes off
        if(!p.hasEnoughFood(bird)) return false; // checks if the player has enough food
		boolean habitatSizeCheck = false; // boolean used to check all habitats for size
		for(String s : bird.getHabitat()) 
		{
			if(p.getBoard().get(s).size() < 5) habitatSizeCheck = true; // if at least one of the possible habitats isn't full then the bird can be played
			// repetition of bottom logic, must be done up here in case it's truly impossible to add the bird and stop the action before food is used up
			// this tests all possible habitats to be placed
			int eggsReq = 0;
			if(p.getBoard().get(s).isEmpty()) eggsReq = 0;
			else if(p.getBoard().get(s).size() < 4) eggsReq = 1;
			else eggsReq = 2;
			if(p.getBoard().values().stream().flatMap(list -> list.stream()).mapToInt(BirdInstance::getEggStored).sum() < eggsReq) return false; // checks if player has enough eggs
		}
		if(!habitatSizeCheck) return false;
        // removes the food from the player's food supply
        if(bird.getFoodRequired().contains("and")) {
            p.removeAndFoodToAddBird(bird); // this method removes all food but the any
            if(bird.getFoodRequired().contains("any")) {
                // UI will ask which food to use
				// Realized there are birds with multiple any. Either can just not put them in game or have a big method we can see
            	// use a for loop instead that keeps asking for foods
            }
        }
        else {
            //UI will ask which food to use; or foods should only have 1 food to remove so directly do it
            String food = ""; // UI METHOD HERE that returns the food type
            p.removeFood(food, 1);
        }

        String habitat = "";
		int eggsReq = 0;
        if(bird.getHabitat().length > 1) {
            //UI will ask which habitat to place the bird in
			while(true)
			{
            	habitat = bird.getHabitat()[0]; // TEMPORARY SETTING TO FIRST HABITAT
				if(!(p.getBoard().get(habitat).size() >= 5)) break; // makes sure player selects a habitat that isnt full'
				// determines the eggs required for placing the bird
				if(p.getBoard().get(habitat).isEmpty()) eggsReq = 0;
				else if(p.getBoard().get(habitat).size() < 4) eggsReq = 1;
				else eggsReq = 2;
				// checks if the player has enough eggs for the habitat chosen
				// this check is done here to allow for reselection of habitat 
				if(p.getBoard().values().stream().flatMap(list -> list.stream()).mapToInt(BirdInstance::getEggStored).sum() >= eggsReq) break;
			}
        }
        else {
            habitat = bird.getHabitat()[0]; // checks are not done here because if there was only one habitat, they would've failed the preliminary tests already
        }
		
        BirdInstance birdInstance = new BirdInstance(bird); // new bird instance
		birdInstance.setCurrentHabitat(habitat);
        p.getBoard().get(habitat).add(birdInstance); // adds to board
        p.getBirdHand().remove(bird); // removes from hand
		this.removeEggs(p, eggsReq); // removes eggs from birds
		if(birdInstance.getActionColor().equalsIgnoreCase("WHITE"))
		{
			// ui should have a prompt that asks whether the player wants to activate the bird's ability and return a boolean; for now it's true
			boolean activate = true;
			if(activate) birdInstance.performAction(this, p);
		}
		if(habitat.equals("forest"))
			pinkAbilityActivation("playForestAndGetWorm");
		else if(habitat.equals("grassland"))
			pinkAbilityActivation("playGrasslandAndTuck");
		else if(habitat.equals("wetland"))
			pinkAbilityActivation("playWetlandGetFish");

        return true;
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
		/* no longer doing this comment and limit prevention, just gonna reset all cards to back in deck, commented to keep it here in case we want to use this way instead
		// just sends a message in case we're testing and wondering what went wrong
		if (amount > availableCards) System.out.println("Ran out of bonus cards");
		amount = Math.min(amount, availableCards);
		*/

		Collections.shuffle(deck);
		ArrayList<BonusCard> returning;
		if(availableCards >= amount)
		{
			returning = new ArrayList<>(deck.subList(0, amount));
			for(BonusCard c : returning)
				c.removeCardFromDeck();
		}
		else
		{
			returning = new ArrayList<>(deck.subList(0, availableCards));
			for(BonusCard c : returning)
				c.removeCardFromDeck();
			int newAmt = amount - availableCards; // since above grabs the last cards left in deck, this finds the amount left to grab
			this.resetBonusDeckCount(); // resets all bonus cards deck count
			returning.addAll(pullRandomBonusCards(newAmt)); // recursion to do this method again but after resetting all bonus cards
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
			else if((food.equals("seed") || food.equals("worm")) && birdFeeder.contains("seed/worm"))
			{
				player.addFood(food, 1);
				birdFeeder.remove("seed/worm");
				atLeast1Grabbed = true;
			}
			if(this.birdFeeder.isEmpty()) this.rollBirdFeeder(); // checks if the feeder is empty and rerolls it if so
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

	public boolean canContinueResources() { return selectionPhase == 1 ? selected.size() == 5 : selected.size() == 1; }

	public int getSelectionPhase() { return selectionPhase; }

	public void incrementPlayerTurn() 
	{ 
		playerTurn = playerTurn % playerList.size() + 1;  
		Player p = this.playerList.get(playerTurn - 1);
		// pink birds are reset on every rotation back to your own turn, thus it
		this.resetThisPlayersBirdsStatus(p); // resets the player's pink birds to not played for whoevers turn it now is
		// end of round check : if the next player's action cubes is 0 and the beginning player is the next player, then the round ends
		if(p.getActionCubes() == 0 && this.startingPlayerTurn == this.playerTurn) roundEnd();
	}

	public int getPlayerTurn() { return playerTurn; }

	public void setCompetitiveType(boolean isCompetitive) {this.isCompetitive = isCompetitive;}

	public void UIMouseReleased(RootMouseEvent event, UIElement released)
	{
		if (gamePhase == 0)
			releasedPhase0(event, released);
		else if (gamePhase == 1)
			releasedPhase1(event, released);
	}

	public void showHand(Player player)
    {
        //create method: panel.displayHand(blah blah)
        
        //panel.playTransition((Runnable)() -> {
				//need to fix and modify to be more usable -> giveUIBirds();
				//panel.displayHand();
			//});
    }

	public void releasedPhase0(RootMouseEvent event, UIElement released)
	{
        if (released.getAttribute("startButton") != null)
		{
			panel.playTransition((Runnable)() -> {
				setCompetitiveType(released == UIElement.getByName("CompetitiveButtonBg"));
				giveUIBirds(5);
				panel.clickedStart(event, released);
			});
			gamePhase++;
    	} 
	}
	
	public void releasedPhase1(RootMouseEvent event, UIElement released)
	{
		if (released.getAttribute("birdChoice") != null || released.getAttribute("foodChoice") != null || released.getAttribute("bonusChoice") != null)
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
							gamePhase++;
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
	
	
	public void releasedPhase2(RootMouseEvent event, UIElement released)
	{
		if (released == UIElement.getByName(""))
		{
			
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