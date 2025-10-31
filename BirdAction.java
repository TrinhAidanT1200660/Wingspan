import java.util.*;

public enum BirdAction implements BirdActionInterface
{
	// USED THIS LINK TO VIEW SOME BIRD ABILITIES. CAN USE BUT NEED TO DESELECT ALL EXPANSIONS AND PROMO PACKS
	// https://navarog.github.io/wingsearch/

	// This ability is for birds with no ability. My favourite.
	NONE((gameContext, player, birdInstance) -> {

	}),
	// This ability draws 2 bonus cards for the player and keep 1 WHEN PLAYED 
	DRAW2BONUSKEEP1((gameContext, player, birdInstance) -> {
		ArrayList<BonusCard> cards = gameContext.pullRandomBonusCards(2);
		BonusCard card1 = cards.get(0);
		BonusCard card2 = cards.get(1);

		// Need UI implementation here to allow the player to choose which card they would like to keep. Cannot continue this ability for now.

	}),
	// This ability allows the player to gain 1 berry WHEN ACTIVATED
	GET1BERRY((gameContext, player, birdInstance) -> {
		player.addFood("berry", 1);
	}),
	// This ability allows the player to gain 3 fish WHEN PLAYED
	GET3FISH((gameContext, player, birdInstance) -> {
		player.addFood("fish", 3);
	}),
	// This ability has all players draw 1 bird card from the deck WHEN ACTIVATED
	ALLDRAW1BIRD((gameContext, player, birdInstance) -> {
		player.addBirdHand(gameContext.pullRandomBirds(1).get(0));
	}),
	// This ability allows for the player to gain 1 seed if there is one in the birdFeeder. If it is available, cache it on the bird
	GAIN1WHEATANDCACHE((gameContext, player, birdInstance) -> {
		if(gameContext.grabFood("seed", player)) // this method auto adds the food into player
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
	// This ability checks for the player(s) with the fewest bird in the wetlands and has them draw 1 bird card
	DRAW1BONUSIFLEASTWETLAND((gameContext, player, birdInstance) -> {
		int leastAmount = 0;
		ArrayList<Player> players = gameContext.getPlayers();

		for(Player p: players)
			leastAmount = p.getBoard().get("wetland").size();

		for(Player p: players)
			if (leastAmount == p.getBoard().get("wetland").size())
				p.addBirdHand(gameContext.pullRandomBirds(1).get(0));
	}),
	// This ability allows a player to tuck a bird card behind the bird and if done, draw 1 bird card
	TUCK1BIRDANDDRAW1BIRD((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be the first card 
		Bird card = player.getBirdHand().remove(0);
		birdInstance.tuckCard(1);
		player.addBirdHand(gameContext.pullRandomBirds(1).get(0));
	}),
	// This ability ahs a player discard an egg from any bird to gain 1 food from the supply
	DISCARDEGGANDGAINFOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove and which food to gain
		// really can't bs the method for now just will be blank
	}),
	// This ability allows the player to get 3 wheat from the supply
	GET3SEED((gameContext, player, birdInstance) -> {
		player.addFood("seed", 3);
	}),
	// Rolls all the dice not in the birdFeeder and if any are rat, cache 1 rat into the supply of the bird
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
	// This ability has the table draw bird cards equal to num of players + 1 and go clockwise from player who played it. Each plaeyr selects 1 of the cards and places in their hand
	// with the player who activated it keeping the extra
	DRAWBIRDEQUALTOPLAYERANDCLOCKWISEDISTRIBUTE((gameContext, player, birdInstance) -> {
		// I not do this now too complicated
	}),
	// This ability allows the player to grab 1 food from the birdFeeder
	GET1FOODBIRDFEEDER((gameContext, player, birdInstance) -> {
		// UI has the player select which food they want. For now it will be the first food in the feeder
		String food = gameContext.getBirdFeeder().get(0);
		player.addFood(food, 1);
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
