
public enum BirdAction implements BirdActionInterface
{
	// This ability is for birds with no ability. My favourite.
	NONE((player) -> {

	}),
	// This ability draws 2 bonus cards for the player and keep 1 WHEN PLAYED 
	DRAW2BONUSKEEP1((player) -> {
		// Basic variables needed for method. values() gets all types of ENUM for the specificed ENUM
		BonusCard[] allBonuses = BonusCard.values();
		BonusCard card1 = null;
		BonusCard card2 = null;

		// Grabs 2 Bonus Cards from the deck and checks if that card is in the deck. If not, redraws. Once drawn, removes the card from the deck.
		for(int i = 0; i < 2; ++i) {
			while (true) { 
				int randCard = (int) (Math.random() * allBonuses.length);
				if(allBonuses[randCard].getDeckCount() > 0) {
					if (i == 0) card1 = allBonuses[randCard];
					else card2 = allBonuses[randCard];
					allBonuses[randCard].removeCardFromDeck();
					break;
				}
			}
		}

		// Need UI implementation here to allow the player to choose which card they would like to keep. Cannot continue this ability for now.

	}),
	// This ability allows the player to gain 1 berry WHEN ACTIVATED
	GET1BERRY((player) -> {
		player.addFood("berry", 1);
	}),
	// This ability allows the player to gain 3 fish WHEN PLAYED
	GET3FISH((player) -> {
		player.addFood("fish", 3);
	}),
	// This ability has all players draw 1 bird card from the deck WHEN ACTIVATED
	ALLDRAW1BIRD((player) -> {
		
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
	public void execute(Player player)
	{
		action.execute(player);
	}
}
