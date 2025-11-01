import java.util.*;

public enum BirdAction implements BirdActionInterface
{
	// USED THIS LINK TO VIEW SOME BIRD ABILITIES. CAN USE BUT NEED TO DESELECT ALL EXPANSIONS AND PROMO PACKS
	// https://navarog.github.io/wingsearch/

	// This ability has all players draw 1 bird card from the deck WHEN ACTIVATED
	// CANVASBACK
	ALLDRAW1BIRD((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addBirdHand(gameContext.pullRandomBirds(1).get(0));
	}),
	// This ability has a player discard an egg from any bird to gain 1 food from the supply
	// AMERICAN_CROW
	DISCARDEGGANDGAINFOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove and which food to gain
		// really can't bs the method for now just will be blank
	}),
	// This ability has the table draw bird cards equal to num of players + 1 and go clockwise from player who played it. Each plaeyr selects 1 of the cards and places in their hand
	// with the player who activated it keeping the extra
	// AMERICAN_OYSTERCATCHER
	DRAWBIRDEQUALTOPLAYERANDCLOCKWISEDISTRIBUTE((gameContext, player, birdInstance) -> {
		// I not do this now too complicated
	}),
	// This ability checks for the player(s) with the fewest bird in the wetlands and has them draw 1 bird card
	// AMERICAN_BITTERN
	DRAW1BONUSIFLEASTWETLAND((gameContext, player, birdInstance) -> {
		int leastAmount = 6;
		ArrayList<Player> players = gameContext.getPlayers();

		for(Player p: players)
			if (leastAmount > p.getBoard().get("wetland").size())
				leastAmount = p.getBoard().get("wetland").size();

		for(Player p: players)
			if (leastAmount == p.getBoard().get("wetland").size())
				p.addBirdHand(gameContext.pullRandomBirds(1).get(0));
	}),
	// This ability draws 2 bonus cards for the player and keep 1 WHEN PLAYED 
	// GREATER_PRAIRIE_CHICKEN | ATLANTIC_PUFFIN | BELLS_VIREO
	DRAW2BONUSKEEP1((gameContext, player, birdInstance) -> {
		ArrayList<BonusCard> cards = gameContext.pullRandomBonusCards(2);
		BonusCard card1 = cards.get(0);
		BonusCard card2 = cards.get(1);

		// Need UI implementation here to allow the player to choose which card they would like to keep. Cannot continue this ability for now.

	}),
	// Look at a bird card from deck (face down pile) and if less than 75 cm wingpsan, tuck it behind card, if not discard
	// BARRED_OWL
	DRAW1BIRDANDTUCKIF75CM((gameContext, player, birdInstance) -> {
		Bird card = gameContext.pullRandomBirds(1).get(0);
		if(card.getWingspan() < 75)
			birdInstance.tuckCard(1);
	}),
	// This ability allows for the player to gain 1 seed if there is one in the birdFeeder. If it is available, cache it on the bird
	// ACORN_WOODPECKER
	GAIN1SEEDANDCACHE((gameContext, player, birdInstance) -> {
		if(gameContext.grabFood("seed", player, 1)) // this method auto adds the food into player
		{
			// need some UI prompt to ask the player whether they want to cache the food or not; false for now
			boolean cached = false;
			if(cached)
			{
				birdInstance.cacheFood(1);
				// too lazy to make another method so -1 works hehe
				player.addFood("seed", -1);
			}
		}
	}),	
	// This ability allows the player to grab 1 food from the birdFeeder
	// AMERICAN_REDSTART
	GET1FOODBIRDFEEDER((gameContext, player, birdInstance) -> {
		// UI has the player select which food they want. For now it will be the first food in the feeder
		String food = gameContext.getBirdFeeder().get(0);
		player.addFood(food, 1);
	}),
	// This ability allows the player to gain 1 berry WHEN ACTIVATED
	// NORTHERN_CARDINAL | BALTIMORE_ORIOLE
	GET1BERRY((gameContext, player, birdInstance) -> {
		player.addFood("berry", 1);
	}),
	// This ability allows the player to gain 3 fish WHEN PLAYED
	// BROWN_PELICAN
	GET3FISH((gameContext, player, birdInstance) -> {
		player.addFood("fish", 3);
	}),
	// This ability allows the player to get 3 wheat from the supply
	// AMERICAN_GOLDFINCH
	GET3SEED((gameContext, player, birdInstance) -> {
		player.addFood("seed", 3);
	}),
	// Gain all fish that are in the bird feeder
	// BALD_EAGLE
	GETALLFISHINBIRDFEEDER((gameContext, player, birdInstance) -> {
		int count = 0;
		for(String s: gameContext.getBirdFeeder())
			if(s.equals("fish"))	
				count++;
		gameContext.grabFood("fish", player, count);
	}),
	// Lays 1 egg on each of your birds with a cavity nest
	// ASH_THROATED_FLYCATCHER
	LAYEGGONALLCAVITY((gameContext, player, birdInstance) -> {
		for (Map.Entry<String, ArrayList<BirdInstance>> entry : player.getBoard().entrySet())
		{
			ArrayList<BirdInstance> birdList = entry.getValue();
			for(BirdInstance bird : birdList)
				if(bird.getNest().equalsIgnoreCase("cavity"))
					bird.addEggs(1);
		}
	}),
	// Lay 1 egg on any bird of player choosing
	// BAIRDS_SPARROW
	LAYEGGONANYBIRD((gameContext, player, birdInstance) -> {
		// need UI to ask player which bird, for now will just place on this bird
		BirdInstance bird = birdInstance;
		bird.addEggs(1);
	}),
	// This ability is for birds with no ability. My favourite.
	// PROTHONOTARY_WARBLER | AMERICAN_WOODCOCK
	NONE((gameContext, player, birdInstance) -> {

	}),
	// Rolls all the dice not in the birdFeeder and if any are fish, cache 1 fish into the supply of the bird
	// ANHINGA
	ROLLDICEANDFINDFISH((gameContext, player, birdInstance) -> {
		final String[] foods = {"berry", "fish", "rat", "seed", "worm"};
		ArrayList<String> rolledFoods = new ArrayList<>();
		for(int i = 0; i < (5-gameContext.getBirdFeeder().size()); ++i)
		{
			int randFood = (int) (Math.random() * foods.length);
			rolledFoods.add(foods[randFood]);
		}
		if(rolledFoods.contains("fish"))
			birdInstance.cacheFood(1);
	}),
	// Rolls all the dice not in the birdFeeder and if any are rat, cache 1 rat into the supply of the bird
	// AMERICAN_KESTREL | BARN_OWL
	ROLLDICEANDFINDRAT((gameContext, player, birdInstance) -> {
		final String[] foods = {"berry", "fish", "rat", "seed", "worm"};
		ArrayList<String> rolledFoods = new ArrayList<>();
		for(int i = 0; i < (5-gameContext.getBirdFeeder().size()); ++i)
		{
			int randFood = (int) (Math.random() * foods.length);
			rolledFoods.add(foods[randFood]);
		}
		if(rolledFoods.contains("rat"))
			birdInstance.cacheFood(1);
	}),
	// This ability allows a player to tuck a bird card behind the bird and if done, draw 1 bird card
	// AMERICAN_ROBIN | AMERICAN_COOT | BARN_SWALLOW
	TUCK1BIRDANDDRAW1BIRD((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
		boolean tuck = false;
		if(tuck && !player.getBirdHand().isEmpty())
		{
			Bird card = player.getBirdHand().remove(0);
			birdInstance.tuckCard(1);
			player.addBirdHand(gameContext.pullRandomBirds(1).get(0));
		}
	})
	;

	
	// stores the action value of each birdAction within the ENUM
	private final BirdActionInterface action;
		
	// constructor that assigns the variable action to the necessary logic
	private BirdAction(BirdActionInterface action)
	{
		this.action = action;
	}
	
	// overriding function for BirdActionInterface; just initiates the action of the bird
	@Override
	public void execute(Game gameContext, Player player, BirdInstance birdInstance)
	{
		action.execute(gameContext, player, birdInstance);
	}
}
