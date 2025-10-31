import java.util.Arrays;
import java.util.HashSet;

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
	ACORN_WOODPECKER("ACORN WOODPECKER", "temp", 4, 46, 5, new String[] {"forest"}, "BROWN", "Other", "and 3seed", "Cavity", BirdAction.GAIN1SEEDANDCACHE),
	// AMERICAN_AVOCET
	AMERICAN_BITTERN("AMERICAN BITTERN", "temp", 2, 107, 7, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1fish 1rat 1worm", "Platform", BirdAction.DRAW1BONUSIFLEASTWETLAND),
	AMERICAN_COOT("AMERICAN COOT", "temp", 5, 61, 3, new String[] {"wetland"}, "BROWN", "Flocking", "and 1any 1seed", "Platform", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	AMERICAN_CROW("AMERICAN CROW", "temp", 2, 99, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any", "Platform", BirdAction.DISCARDEGGANDGAINFOOD),
	AMERICAN_GOLDFINCH("AMERICAN GOLDFINCH", "temp", 3, 23, 3, new String[] {"grassland"}, "WHITE", "Other", "and 2seed", "Bowl", BirdAction.GET3SEED),
	AMERICAN_KESTREL("AMERICAN KESTREL", "temp", 3, 56, 5, new String[] {"grassland"}, "BROWN", "Predator", "and 1rat 1worm", "Cavity", BirdAction.ROLLDICEANDFINDRAT),
	AMERICAN_OYSTERCATCHER("AMERICAN OYSTERCATCHER", "temp", 2, 81, 5, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 2worm", "Ground", BirdAction.DRAWBIRDEQUALTOPLAYERANDCLOCKWISEDISTRIBUTE),
	AMERICAN_REDSTART("AMERICAN REDSTART", "temp", 2, 20, 4, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1worm", "Bowl", BirdAction.GET1FOODBIRDFEEDER),
	AMERICAN_ROBIN("AMERICAN ROBIN", "temp", 4, 43, 1, new String[] {"grassland", "forest"}, "BROWN", "Flocking", "or 1berry 1worm", "Bowl", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	// AMERICAN_WHITE_PELICAN
	AMERICAN_WOODCOCK("AMERICAN WOODCOCK", "temp", 2, 46, 9, new String[] {"forest", "grassland"}, "NONE", "Other", "and 1seed 2worm", "Ground", BirdAction.NONE),
	ANHINGA("ANHINGA", "temp", 2, 114, 6, new String[] {"wetland"}, "BROWN", "Predator", "and 2fish", "Platform", BirdAction.ROLLDICEANDFINDFISH),
	// ANNAS_HUMMINGBIRD
	ASH_THROATED_FLYCATCHER("ASH THROATED FLYCATCHER", "temp", 4, 30, 4, new String[] {"grassland"}, "WHITE", "EggLaying", "and 1berry 2worm", "Cavity", BirdAction.LAYEGGONALLCAVITY),
	ATLANTIC_PUFFIN("ATLANTIC PUFFIN", "temp", 1, 53, 8, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 3fish", "Wild", BirdAction.DRAW2BONUSKEEP1),
	BAIRDS_SPARROW("BIARDS SPARROW", "temp", 2, 23, 3, new String[] {"grassland"}, "BROWN", "EggLaying", "and 1seed 1worm", "Ground", BirdAction.LAYEGGONANYBIRD),
	BROWN_PELICAN("BROWN PELICAN", "birds/brown_pelican.png", 2, 201, 4, new String[] {"wetland"}, "WHITE", "Other", "and 2fish", "Platform", BirdAction.GET3FISH),
	CANVASBACK("CANVASBACK", "birds/canvasback.png", 4, 82, 4, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1any 1seed", "Wild", BirdAction.ALLDRAW1BIRD),
	GREATER_PRAIRIE_CHICKEN("GREATER PRAIRIE CHICKEN", "birds/greater_prairie_chicken.png", 4, 71, 5, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 2seed 1worm", "Ground", BirdAction.DRAW2BONUSKEEP1),
	NORTHERN_CARDINAL("NORTHERN CARDINAL", "birds/northern_cardinal.png", 5, 30, 3, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1seed", "Bowl", BirdAction.GET1BERRY),
	PROTHONOTARY_WARBLER("PROTHONOTARY WARBLER", "birds/prothonotary_warbler.png", 4, 23, 8, new String[] {"forest", "wetland"}, "NONE", "Other", "and 1seed 2worm", "Cavity", BirdAction.NONE)
	;

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

	//For BonusCard: returns all the birdEnums applicable to a name-based bonus card (photographer, anatomist, cartographer)
	public static HashSet<Bird> getBonusName(String type)
	{
		if (type.equals("photographer"))
		{
			Bird[] birds = {BROWN_PELICAN};
           return new HashSet<Bird>(Arrays.asList(birds));
		}
		if (type.equals("anatomist"))
		{
			Bird[] birds = {CANVASBACK};
           return new HashSet<Bird>(Arrays.asList(birds));
		}
		if (type.equals("cartographer"))
		{
			Bird[] birds = {GREATER_PRAIRIE_CHICKEN, NORTHERN_CARDINAL};
           return new HashSet<Bird>(Arrays.asList(birds));
		}
		return new HashSet<Bird>();
	}
}