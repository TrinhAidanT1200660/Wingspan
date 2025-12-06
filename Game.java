
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.Timer;

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
	private String[] foods = new String[] {"Berry", "Fish", "Worm", "Seed", "Rat"}; // food types available in the bird feeder, and to set food stats in UI

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
	public void playActions(String choice)
	{
		// player has four choices here, the play bird, food, eggs, or draw birds
		// ui will return which choice they pick, should just return a string
		// for now, it'll be just lay eggs
		// string returns should be playBird, getFood, layEggs, drawBirds
		Player p = this.playerList.get(playerTurn-1);
		// while loop is basically only for playBird which is the only one that can end up in failure if the player doesn't have enough eggs or even a bird to play
		System.out.println(choice + " - current player is player " + playerTurn);
		if(choice.equals("playBird")) { System.out.println(choice + " ok playing playBird"); this.playBird(p); } // playBird auto adds it to board and returns true if successfully placed
		else if(choice.equals("getFood")) { this.getFood(p); this.iterateBirdAbilities(p, "forest"); }
		else if(choice.equals("layEggs")) { this.layEggs(p); this.iterateBirdAbilities(p, "grassland"); }
		else if(choice.equals("drawBirds")) { this.drawBirds(p); this.iterateBirdAbilities(p, "wetland"); }
		else { System.out.println("ERROR IN PLAYACTIONS, CAN'T FIND ACTION"); return; }
		p.decreaseActionCubes(); // player turn has ended and they lose an action cube
		this.regenerateFaceUpTray(); // regens the tray without removing old cards as player turn has ended
		if (!choice.equals("playBird")) this.incrementPlayerTurn(); // increments player turn
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
			boolean trade = UIFrame.getByName("GameScreen").getAttributeOrDefault("TradingEgg", false);
			if(trade)// && p.hasEnoughEggs(1))
			{
				// UI has the player remove an egg from a bird using removeEgg
				this.removeEggs(p, 1);
				birdGet ++;
			}
		}
		// will grab one bird at a time to be sequential and have different choices
		ArrayList<UIElement> selected = new ArrayList<>(UIElement.getAllTagged("Selected"));
		/* 
		 * aidan, i found an error (i fixed it): the int choice reflects the index of the bird in the face up deck.
		 * if you're drawing more than one card (not face down card), and you remove from face up deck, once u iterate next,
		 * the index of this bird most likely changed so int choice will reflect the old index and you'll get an error cus now its out of bounds
		 */
		for (int i = 0 ; i < birdGet; ++i)
		{
			// UI should have the player pick the bird they want
			// not sure how we want to do this but there are 3 face up cards they can pick and a random draw pile
			// if they pick up a face up card, can just return 0-2 for the index, make sure to not allow choosing indices without cards
			// 3 index can be for random faceup pile
			// this logic can be changed ; for now they will only be able to get a random bird
			
			// this is old logic in case i need to go back
			/* int choice = selected.get(i).getAttributeOrDefault("ChoiceIndex", -1);
			if(choice >= 0 && choice <=2) {
				this.grabFaceUpCard(choice, p); // ranges from 0 - 2: the ui method shouldnt return index 2 if there was only 2 cards
				panel.addToPlayerHand(playerList.indexOf(p) + 1, faceUpBirds.get(choice));
			} else if(choice == 3) {
				Bird random = this.pullRandomBirds(1).get(0);
				p.addBirdHand(random);
				panel.addToPlayerHand(playerList.indexOf(p) + 1, random);
			}
			else System.out.println("ERROR IN DRAWBIRDS METHOD GAME");*/
			
			UIElement current = selected.get(i);
			boolean isFaceDown = current.hasTag("FaceDown");
			if(isFaceDown) {
				Bird random = this.pullRandomBirds(1).get(0);
				p.addBirdHand(random);
				panel.addToPlayerHand(playerList.indexOf(p) + 1, random);
			} else {
				Bird bird = (Bird)current.getAttribute("Card");
				int choice = faceUpBirds.indexOf(bird);
				if (choice > -1) {
					this.grabFaceUpCard(choice, p);
					panel.addToPlayerHand(playerList.indexOf(p) + 1, bird);
				} else System.out.println("ERROR IN DRAWBIRDS METHOD GAME");
			}
		}
		UIElement.removeAllTagged("Selected");
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
		for(Bird b: cards) faceUpBirds.add(b);
		updateUIFaceUpTray();
	}

	// Clears the faceup pile completely before adding 3 new bird cards
	public void clearAndRegenerateFaceUpTray() {
		faceUpBirds.clear();
		ArrayList<Bird> cards = pullRandomBirds(3);
		for(Bird b: cards) faceUpBirds.add(b);
		updateUIFaceUpTray();
	}

	public void updateUIFaceUpTray() {
		for (int i = 0; i < 3; i++) {
			UIImage.getByName("DeckCard" + (i + 1)).setImagePath(faceUpBirds.get(i).getImage());
			UIImage.getByName("DeckCard" + (i + 1)).setAttribute("Card", faceUpBirds.get(i));
		}
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
	public void playBird(Player p)
	{
		// UI should be asking the player which bird from their hand to play
		// for now it'll be the first bird in the hand
		Bird birdToPlay = UIElement.getAllTagged("Selected").stream().toList().getFirst().getAttributeOrDefault("Card", null);
		if(p.getBirdHand().isEmpty()) { System.out.println("Player #" + playerList.indexOf(p) + "'s bird hand is empty? Requested card: " + birdToPlay + ". " + p.getBirdHand()); return; } // just checks if the hand is empty first before asking which to play
		addBirdToBoard(p, birdToPlay);
	}

	public void recursivelyAskForAnyFoodAndRemove(Player p, Bird bird, int times) {
		if (times <= 0) {
			// if we're done asking about all the any choices, ask the ui about the habitat and then continue
			askPlayerForHabitatAndContinue(p, bird, false);
			return;
		}
        panel.promptPlayerFood("Which food would you like to use as your any?", (food) -> {
			System.out.println(food);
			p.removeFood(food.toLowerCase(), 1);
			// wait 300 ms for fade out animation to finish then ask again
			Timer t = new Timer(300, (e) -> recursivelyAskForAnyFoodAndRemove(p, bird, times - 1)); t.setRepeats(false); t.start();
		}, p.getFood().entrySet().stream().filter((v) -> v.getValue() > 0).map(Map.Entry::getKey).toList()); 
    }

	//adds the specified bird to the board if the player has enough food and the bird is in their hand
    //if it has any food type, UI will ask player to choose which food to use
    //returns true if successful, false otherwise
    public void addBirdToBoard(Player p, Bird bird) {
        if(!p.getBirdHand().contains(bird)) { 
			panel.promptPlayer("It appears you don't actually have this card. Sorry! Please choose an action below.", "Repick card", "Repick action", (v) -> {
				if (!v) { // if v is true, the player screen is already on hand screen so no need to do anything
					// if v is false, change screen to player's board so they can repick their action
					((Runnable)UIFrame.getByName("Boards").getAttribute("ShowBoardOfCurrent")).run();
				}
			});
			panel.removeFirstFromPlayerHand(playerList.indexOf(p) + 1, bird);
			return; 
		} // checks if the player acc has the bird; idk how this goes off
		if(!p.hasEnoughFood(bird)) { 
			panel.promptPlayer("You don't have enough food for this bird. What would you like to do?", "Repick card", "Repick action", (v) -> {
				if (!v) { // if v is true, the player screen is already on hand screen so no need to do anything
					// if v is false, change screen to player's board so they can repick their action
					((Runnable)UIFrame.getByName("Boards").getAttribute("ShowBoardOfCurrent")).run();
				}
			});
			return; 
		} // checks if the player has enough food
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
			if(p.getBoard().values().stream().flatMap(list -> list.stream()).mapToInt(BirdInstance::getEggStored).sum() < eggsReq) {
				// tell player they dont have enough eggs. ask what they want to do
				panel.promptPlayer("You don't have enough eggs to play this card. What would you like to do?", "Repick card", "Repick action", (v) -> {
					if (!v) { // if v is true, the player screen is already on hand screen so no need to do anything
						// if v is false, change screen to player's board so they can repick their action
						((Runnable)UIFrame.getByName("Boards").getAttribute("ShowBoardOfCurrent")).run();
					}
				});
				return; 
			}// checks if player has enough eggs
		}
		if(!habitatSizeCheck) { 
			panel.promptPlayer("You don't have any habitats available to place this bird. What would you like to do?", "Repick card", "Repick action", (v) -> {
				if (!v) { // if v is true, the player screen is already on hand screen so no need to do anything
					// if v is false, change screen to player's board so they can repick their action
					((Runnable)UIFrame.getByName("Boards").getAttribute("ShowBoardOfCurrent")).run();
				}
			});
			return; 
		}
        System.out.println("check 4: " + bird.getFoodRequired());
		// removes the food from the player's food supply
        if(bird.getFoodRequired().contains("and")) {
            p.removeAndFoodToAddBird(bird); // this method removes all food but the any
            if(bird.getFoodRequired().contains("any")) {
                // UI will ask which food to use
				// Realized there are birds with multiple any. Either can just not put them in game or have a big method we can see
				// so you call this recursive method and itll repeatedly ask ui which food... to get the number of any foods i just filtered out the ones that were of type any and thats how many times itll ask 
				// we dont even need to get the food they select as a value, we can just remove them from the player once they select it
				// after theyre done selecting, the method below will continue
				recursivelyAskForAnyFoodAndRemove(p, bird, List.of(bird.getFoodRequired().split(" ")).stream().filter((v) -> v.contains("any")).mapToInt((v) -> Integer.parseInt(v.replace("any", ""))).sum());
            } else askPlayerForHabitatAndContinue(p, bird, false);
        }
        else {
			// prompt the player but only show foods that the bird has listed and exclude those that the player doesn't have
			panel.promptPlayerFood("Which food would you like to use for this bird?", (food) -> {
				p.removeFood(food.toLowerCase(), 1);
				// wait 300 ms, then ask the player for the habitat to place the card. after they make their choice, continue the adding to the board action
				Timer t = new Timer(300, (e) -> askPlayerForHabitatAndContinue(p, bird, false)); t.setRepeats(false); t.start();
			}, bird.getFoodRequired().contains("any") ? p.getFood().entrySet().stream().filter((v) -> v.getValue() > 0).map(Map.Entry::getKey).toList() : bird.getFoodRequiredAsList().stream().filter((f) -> p.getFood().getOrDefault(f, 0) > 0).toList()); 
			//}, p.getFood().entrySet().stream().filter((v) -> v.getValue() > 0).map(Map.Entry::getKey).toList()); 
        }
    }

	public void askPlayerForHabitatAndContinue(Player p, Bird bird, boolean invalid) {
		panel.promptPlayerHabitat(invalid ? "Please pick a valid habitat!" : "Which habitat would you like to place this bird in?", (habitat) -> {
			int eggsReq = 0;
			boolean valid = true; // boolean holding habitat's validity
			if (bird.getHabitat().length > 1) {
				if(p.getBoard().get(habitat).size() >= 5) { // makes sure player selects a habitat that isnt full'
					valid = false;
				} else { // if its not full
					// determines the eggs required for placing the bird
					if(p.getBoard().get(habitat).isEmpty()) eggsReq = 0;
					else if(p.getBoard().get(habitat).size() < 4) eggsReq = 1;
					else eggsReq = 2;
					// checks if the player has enough eggs for the habitat chosen
					// this check is done here to allow for reselection of habitat 
					if(!p.hasEnoughEggs(eggsReq)) valid = false;
				}
			}
			if (valid) { // if it's valid continue
				continueAddBirdToBoardAfterPrompts(p, bird, habitat, eggsReq); 
			} else { 
				// if the habitat is not valid, ask for another habitat
				askPlayerForHabitatAndContinue(p, bird, true);
			}
		}, List.of(bird.getHabitat()).stream().filter((h) -> p.getBoard().get(h).size() < 5).toList());
	}

	public void continueAddBirdToBoardAfterPrompts(Player p, Bird bird, String habitat, int eggsReq) {		
        BirdInstance birdInstance = new BirdInstance(bird); // new bird instance
		birdInstance.setCurrentHabitat(habitat);
        p.getBoard().get(habitat).add(birdInstance); // adds to board
        p.getBirdHand().remove(bird); // removes from hand
        panel.removeFirstFromPlayerHand(playerList.indexOf(p) + 1, bird);
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

        this.incrementPlayerTurn();
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
	
	public void UIKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			UIMouseReleased(null, UIElement.getByName("ContinueResourcesButtonBg"));
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			incrementPlayerTurn();
		}
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
    int oldPlayer = playerTurn;
		playerTurn = playerTurn % playerList.size() + 1;  
		Player p = this.playerList.get(playerTurn - 1);
		// pink birds are reset on every rotation back to your own turn, thus it
		this.resetThisPlayersBirdsStatus(p); // resets the player's pink birds to not played for whoevers turn it now is
		// end of round check : if the next player's action cubes is 0 and the beginning player is the next player, then the round ends
		if(p.getActionCubes() == 0 && this.startingPlayerTurn == this.playerTurn) roundEnd();
		if (gamePhase == 1) { 
			UIFrame.getByName("Player" + oldPlayer + "CardsContainer").visible = false;
			UIFrame.getByName("Player" + playerTurn + "CardsContainer").visible = true;
			UIImage.getByName("ActionCubeIcon").setImagePath("images/p" + playerTurn + "_action_cube.png");
			UIText.getByName("ActionCubesStat").text = "" + playerList.get(playerTurn - 1).getActionCubes();
			for (String food : foods) {
				UIText.getByName(food + "Stat").text = "" + playerList.get(playerTurn - 1).getFood().getOrDefault(food.toLowerCase(), 0);
			}
			for (int i = 1; i <= 5; i++) {
				UIFrame.getByName("Player" + i + "Button").rotation = i == playerTurn ? 45 : 0;
				UIText.getByName("Player" + i + "ButtonText").rotation = i == playerTurn ? -45 : 0;
			} 
			UIFrame.getByName("Boards").setAttribute("Index", playerTurn);
			((Runnable)UIFrame.getByName("Boards").getAttribute("ViewBoard")).run();
		}
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
			/* panel.playTransition((Runnable)() -> {
				setCompetitiveType(released == UIElement.getByName("CompetitiveButtonBg"));
				giveUIBirds(5);
				panel.clickedStart(event, released);
			});
			gamePhase++;
			}); */
			// we're just simulating generating 5 random players just for testing actual game play here 
			setCompetitiveType(released == UIElement.getByName("CompetitiveButtonBg"));
			for (int i = 0; i < 5; i++) {
				Player p = playerList.get(i);
				p.addBirdHand(Bird.WHOOPING_CRANE);
				panel.addToPlayerHand(i + 1, Bird.WHOOPING_CRANE);
				p.addBirdHand(Bird.BLACK_VULTURE);
				panel.addToPlayerHand(i + 1, Bird.BLACK_VULTURE);
				ArrayList<Bird> birds = pullRandomBirds(5);
				for (int j = 0; j < 5; j++) {
					int foodOrBird = 1;//(int)(Math.random() * 2); // 0 for food, 1 for bird
					if (foodOrBird == 0) {
						//p.addFood(foods[(int)(Math.random() * 5)].toLowerCase(), 1);
					} else {
						Bird b = birds.get(j);
						p.addBirdHand(b);
						panel.addToPlayerHand(i + 1, b);
					}
					p.addFood(foods[j].toLowerCase(), 5);
				}
				BonusCard randomBonus = pullRandomBonusCards(1).get(0);
				p.addBonusHand(randomBonus);
				panel.addToPlayerHand(i + 1, randomBonus);
				
			}
			gamePhase = 1;
			for (String food : foods) UIText.getByName(food + "Stat").text = "" + playerList.get(0).getFood().getOrDefault(food.toLowerCase(), 0);
			UIElement.getByName("StartScreen").visible = false;
			UIElement.getByName("GameScreen").visible = true;
			((UIImage)(UIElement.getByName("Background"))).setImagePath("images/wood_bg.png");
			regenerateFaceUpTray();
			UIText.getByName("ActionCubesStat").text = "" + playerList.get(0).getActionCubes();
			UIFrame.getByName("Player1CardsContainer").visible = true;
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
							gamePhase = 1;
							UIElement.getByName("ResourceChoosingScreen").visible = false;
							UIElement.getByName("GameScreen").visible = true;
							((UIImage)(UIElement.getByName("Background"))).setImagePath("images/wood_bg.png");
							regenerateFaceUpTray();
							UIText.getByName("ActionCubesStat").text = "" + playerList.get(0).getActionCubes();
							UIFrame.getByName("Player1CardsContainer").visible = true;
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

	private void deselect(Selectable element) { // deselects a specific selectable item\
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

	public void setValue(Object value) { this.value = value; }

	public void setElement(UIElement element) { this.element = element; }

    public int compareTo(Selectable o) { return Long.compare(added, o.added); }

	public String toString() { return value != null ? value.toString() : (element != null ? element.getName() : ""); }
}
