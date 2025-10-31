
public enum Bird 
{
	// ENUM SEQUENCE: string name, string image, int eggMax, int wingspan, int points, String[] habitat, String abilityColor, String aType, String food, String nest, BirdAction action
	// Name: obviously it's top name
	// Image: get the image from package
	// eggMax: number of egg photos
	// Wingspan: number of cms on right of card
	// Points: number next to feather
	// Habitats, go in order from top to bottom of board: forest -> grassland -> wetland
	// abilityColor: WHITE, PINK, BROWN, NONE
	// actionTypes: EggLaying, CardDrawing, Flocking (card tucks), Predator (food cache), Other
	// FoodTypes, go in order alphabetically: any -> berry -> fish -> rat -> seed -> worm 
	// nestTypes: Platform (logs on top each other), Bowl (a bowl), Cavity (tree with hole in it), Ground (bunch of circles), Wild (a star)
	// action: directly type in from birdAction
	// ACTUAL BIRD ENUMS, NO IMAGES IMPORTED YET
	PROTHONOTARY_WARBLER("PROTHONOTARY WARBLER", "birds/prothonotary_warbler.png", 4, 23, 8, new String[] {"forest", "wetland"}, "NONE", "Other", "and 1seed 2worm", "Cavity", BirdAction.NONE),
	GREATER_PRAIRIE_CHICKEN("GREATER PRAIRIE-CHICKEN", "birds/greater_prairie_chicken.png", 4, 71, 5, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 2seed 1worm", "Ground", BirdAction.DRAW2BONUSKEEP1),
	NORTHERN_CARDINAL("NORTHERN CARDINAL", "birds/northern_cardinal.png", 5, 30, 3, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1seed", "Bowl", BirdAction.GET1BERRY),
	BROWN_PELICAN("BROWN PELICAN", "birds/brown_pelican.png", 2, 201, 4, new String[] {"wetland"}, "WHITE", "Other", "and 2fish", "Platform", BirdAction.GET3FISH),
	CANVASBACK("CANVASBACK", "birds/canvasback.png", 4, 82, 4, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1any 1seed", "Wild", BirdAction.ALLDRAW1BIRD);

	private final String name; // holds birds name
	private final String imageFileString;
	private int deckCount; // holds the number of bird cards in the deck, decreases when drawn once
	private final int eggMax; // holds the max number of eggs this bird can have
	private final int wingspan; // holds the wingspan in centimeters this bird has
	private final int pointValue; // holds the point value this bird has
	private final String[] habitat; // holds the biomes this bird can be in
	private final String actionColor; // holds the action color type this bird has (PINK, WHITE, BROWN)
	private final String actionType; // holds the action type this bird has (EggLaying, CardDrawing, Flocking, Predator, Other)
	private final String foodRequired; // holds the food types this bird requires ("and 2worm 1berry" means bird requires 2 worms and 1 berry, or for / foods, any otherwise)
	private final String nest; // holds the nest type this bird has
	private final BirdAction action; // holds the action ability this bird has through another ENUM and interface implementation
	
	// constructor
	private Bird(String name, String imageFileString, int eggMax, int wingspan, int pointValue,
			String[] habitat, String actionColor, String actionType, String foodRequired, String nest, BirdAction action)
	{
		this.name = name;
		this.imageFileString = imageFileString;
		this.deckCount = 1;
		this.eggMax = eggMax;
		this.wingspan = wingspan;
		this.pointValue = pointValue;
		this.habitat = habitat;
		this.actionColor = actionColor;
		this.actionType = actionType;
		this.foodRequired = foodRequired;
		this.nest = nest;
		this.action = action;
	}
	
	// RETURN METHODS
	
	// returns a String with the bird's name
	public String getName() { return name; }

	// returns a String of the bird's image file path
	public String getImage() { return imageFileString; }

	// returns an int with the number of bird cards left in the deck
	public int getDeckCount() { return deckCount; }

	// returns an int with the bird's maximum egg capacity
	public int getEggMax() { return eggMax; }

	// returns an int with the bird's wingspan in centimeters
	public int getWingspan() { return wingspan; }

	// returns an int with the bird's point value
	public int getPointValue() { return pointValue; }

	// returns a String array with the habitat this bird can be in
	public String[] getHabitat() { return habitat; }

	// returns a String with the bird's action color type (PINK, WHITE, BROWN)
	public String getActionColor() { return actionColor; }

	// returns a String with the bird's action type (EggLaying, CardDrawing, etc.)
	public String getActionType() { return actionType; }

	// returns a String with the food required for this bird
	public String getFoodRequired() { return foodRequired; }

	// returns a String with the bird's nest type
	public String getNest() { return nest; }
	
	// VOID METHODS / MUTATOR METHODS
	
	// performs this bird's stored BirdAction ability on the given player
	public void performAction(Game gameContext, Player player, BirdInstance birdInstance) { action.execute(gameContext, player, birdInstance); }

	public void removeCardFromDeck() { deckCount -= 1; }
}