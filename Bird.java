import java.util.Arrays;
import java.util.HashSet;

public enum Bird 
{
	// USED THIS LINK TO VIEW SOME BIRD ABILITIES. CAN USE BUT NEED TO DESELECT ALL EXPANSIONS AND PROMO PACKS
	// https://navarog.github.io/wingsearch/
	
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
	AMERICAN_BITTERN("AMERICAN BITTERN", "temp", 2, 107, 7, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1fish 1rat 1worm", "Platform", BirdAction.DRAW1BIRDIFLEASTWETLAND),
	AMERICAN_COOT("AMERICAN COOT", "temp", 5, 61, 3, new String[] {"wetland"}, "BROWN", "Flocking", "and 1any 1seed", "Platform", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	AMERICAN_CROW("AMERICAN CROW", "temp", 2, 99, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any", "Platform", BirdAction.DISCARDEGGANDGAIN1FOOD),
	AMERICAN_GOLDFINCH("AMERICAN GOLDFINCH", "temp", 3, 23, 3, new String[] {"grassland"}, "WHITE", "Other", "and 2seed", "Bowl", BirdAction.GET3SEED),
	AMERICAN_KESTREL("AMERICAN KESTREL", "temp", 3, 56, 5, new String[] {"grassland"}, "BROWN", "Predator", "and 1rat 1worm", "Cavity", BirdAction.ROLLDICEANDFINDRAT),
	AMERICAN_OYSTERCATCHER("AMERICAN OYSTERCATCHER", "temp", 2, 81, 5, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 2worm", "Ground", BirdAction.DRAWBIRDEQUALTOPLAYERANDCLOCKWISEDISTRIBUTE),
	AMERICAN_REDSTART("AMERICAN REDSTART", "temp", 2, 20, 4, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1worm", "Bowl", BirdAction.GET1FOODBIRDFEEDER),
	AMERICAN_ROBIN("AMERICAN ROBIN", "temp", 4, 43, 1, new String[] {"grassland", "forest"}, "BROWN", "Flocking", "or 1berry 1worm", "Bowl", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	AMERICAN_WHITE_PELICAN("AMERICAN WHITE PELICAN", "temp", 1, 274, 5, new String[] {"wetland"}, "BROWN", "Flocking", "and 2fish", "Ground", BirdAction.DISCARDFISHANDTUCK2BIRDS),
	AMERICAN_WOODCOCK("AMERICAN WOODCOCK", "temp", 2, 46, 9, new String[] {"forest", "grassland"}, "NONE", "Other", "and 1seed 2worm", "Ground", BirdAction.NONE),
	ANHINGA("ANHINGA", "temp", 2, 114, 6, new String[] {"wetland"}, "BROWN", "Predator", "and 2fish", "Platform", BirdAction.ROLLDICEANDFINDFISH),
	// ANNAS_HUMMINGBIRD
	ASH_THROATED_FLYCATCHER("ASH THROATED FLYCATCHER", "temp", 4, 30, 4, new String[] {"grassland"}, "WHITE", "EggLaying", "and 1berry 2worm", "Cavity", BirdAction.LAYEGGONALLCAVITY),
	ATLANTIC_PUFFIN("ATLANTIC PUFFIN", "temp", 1, 53, 8, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 3fish", "Wild", BirdAction.DRAW2BONUSKEEP1),
	BAIRDS_SPARROW("BAIRD'S SPARROW", "temp", 2, 23, 3, new String[] {"grassland"}, "BROWN", "EggLaying", "and 1seed 1worm", "Ground", BirdAction.LAYEGGONANYBIRD),
	BALD_EAGLE("BALD EAGLE", "temp", 1, 203, 9, new String[] {"wetland"}, "WHITE", "Other", "and 2fish 1rat", "Platform", BirdAction.ROLLDICEANDFINDFISH),
	BALTIMORE_ORIOLE("BALTIMORE ORIOLE", "temp", 2, 30, 9, new String[] {"forest"}, "BROWN", "Other", "and 2berry 1worm", "Wild", BirdAction.GET1BERRY),
	BARN_OWL("BARN OWL", "temp", 4, 107, 5, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Predator", "and 2rat", "Cavity",BirdAction.ROLLDICEANDFINDRAT),
	BARN_SWALLOW("BARN SWALLOW", "temp", 2, 107, 3, new String[] {"grassland", "wetland"}, "BROWN", "Flocking", "and 1worm", "Wild", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	BARRED_OWL("BARRED OWL", "temp", 2, 107, 3, new String[] {"forest"}, "BROWN", "Predator", "and 1rat", "Cavity", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	// BARROWS_GOLDENEYE
	BELLS_VIREO("BELL'S VIREO", "temp", 2, 18, 4, new String[] {"forest", "grassland"}, "WHITE", "CardDrawing", "and 2worm", "Wild", BirdAction.DRAW2BONUSKEEP1),
	// BELTED_KINGFISHER
	BEWICKS_WREN("BEWICK'S WREN", "temp", 3, 18, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1seed 2worm", "Cavity", BirdAction.MOVEIFATVERYRIGHT),
	BLACK_SKIMMER("BLACK SKIMMER", "temp", 2, 112, 6, new String[] {"wetland"}, "BROWN", "Predator", "and 2fish", "Ground", BirdAction.ROLLDICEANDFINDFISH),
	// BLACK_TERN
	// BLACK_VULTURE
	BLACK_BELLIED_WHISTLING_DUCK("BLACK BELLIED WHISTLING DUCK", "temp", 5, 76, 2, new String[] {"wetland"}, "BROWN", "Flocking", "and 2seed", "Cavity", BirdAction.DISCARDSEEDANDTUCK2BIRDS),
	// BLACK_BILLED_MAGPIE
	BLACK_CHINNED_HUMMINGBIRD("BLACK CHINNED HUMMINGBIRD", "temp", 2, 8, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any", "Bowl", BirdAction.ALLGET1BERRY),
	BLACK_CROWNED_NIGHT_HERON("BLACK CROWNED NIGHT HERON", "temp", 2, 112, 9, new String[] {"wetland"}, "BROWN", "Other", "and 1fish 1rat 1worm", "Platform", BirdAction.DISCARDEGGANDGAIN1FOOD),
	BLACK_NECKED_STILT("BLACK NECKED STILT", "temp", 2, 74, 4, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 2worm", "Ground", BirdAction.DRAW2BIRDCARDS),
	BLUE_GROSBEAK("BLUE GROSBEAK", "temp", 3, 28, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2seed 1worm", "Bowl", BirdAction.MOVEIFATVERYRIGHT),
	BLUE_JAY("BLUE JAY", "temp", 2, 41, 3, new String[] {"forest"}, "BROWN", "Other", "and 1any 1seed", "Bowl", BirdAction.GAIN1SEEDANDCACHE),
	BLUE_GRAY_GNATCATCHER("BLUE GRAY GNATCATCHER", "temp", 3, 15, 1, new String[] {"forest"}, "BROWN", "Other", "and 1worm", "Bowl", BirdAction.GET1WORM),
	BLUE_WINGED_WARBLER("BLUE WINGED WARBLER", "temp", 2, 20, 8, new String[] {"forest", "grassland"}, "NONE", "Other", "and 2worm", "Bowl", BirdAction.NONE),
	BOBOLINK("BOBOLINK", "temp", 3, 30, 4, new String[] {"grassland"}, "WHITE", "EggLaying", "and 2seed 1worm", "Ground", BirdAction.LAYEGGONALLGROUND),
	BRANT("BRANT", "temp", 2, 114, 3, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 1any 1seed", "Ground", BirdAction.DRAW3FACEUPBIRD),
	BREWERS_BLACKBIRD("BREWER'S BLACKBIRD", "temp", 3, 41, 3, new String[] {"grassland"}, "BROWN", "Flocking", "and 1any 1seed", "Bowl", BirdAction.TUCK1BIRDANDLAY1EGG),
	BROAD_WINGED_HAWK("BROAD WINGED HAWK", "temp", 2, 85, 4, new String[] {"forest"}, "BROWN", "Predator", "and 1rat", "Platform", BirdAction.ROLLDICEANDFINDRAT),
	// BRONZED_COWBIRD
	BROWN_PELICAN("BROWN PELICAN", "birds/brown_pelican.png", 2, 201, 4, new String[] {"wetland"}, "WHITE", "Other", "and 2fish", "Platform", BirdAction.GET3FISH),
	//BROWN_HEADED_COWBIRD
	BURROWING_OWL("BURROWING OWL", "temp", 4, 53, 5, new String[] {"grassland"}, "BROWN", "Predator", "and 1rat 1worm", "Wild", BirdAction.ROLLDICEANDFINDRAT),
	BUSHTIT("BUSHTIT", "temp", 5, 15, 2, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Flocking", "and 1seed 1worm", "Wild", BirdAction.TUCK1BIRDANDLAY1EGG),
	CALIFORNIA_CONDOR("CALIFORNIA CONDOR", "temp", 1, 277, 1, new String[] {"forest", "grassland", "wetland"}, "WHITE", "CardDrawing", "and 0any", "Ground", BirdAction.DRAW2BONUSKEEP1),
	CALIFORNIA_QUAIL("CALIFORNIA QUAIL", "temp", 6, 36, 3, new String[] {"forest", "grassland"}, "BROWN", "EggLaying", "and 2seed 1worm", "Ground", BirdAction.LAYEGGONTHISBIRD),
	CANADA_GOOSE("CANADA GOOSE", "temp", 3, 132, 3, new String[] {"grassland", "wetland"}, "BROWN", "Flocking", "and 2seed", "Ground", BirdAction.DISCARDSEEDANDTUCK2BIRDS),
	CANVASBACK("CANVASBACK", "birds/canvasback.png", 4, 82, 4, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1any 1seed", "Wild", BirdAction.ALLDRAW1BIRD),
	CAROLINA_CHICKADEE("CAROLINA CHICKADEE", "temp", 3, 20, 2, new String[] {"forest"}, "BROWN", "Other", "or 1seed 1worm", "Cavity", BirdAction.CACHE1SEED),
	CAROLINA_WREN("CAROLINA WREN", "temp", 5, 20, 1, new String[] {"forest"}, "WHITE", "CardDrawing", "or 1berry 1worm", "Cavity", BirdAction.DRAW2BIRDCARDS),
	CASSINS_FINCH("CASSIN'S FINCH", "temp", 3, 30, 4, new String[] {"forest"}, "WHITE", "CardDrawing", "and 1berry 1seed", "Bowl", BirdAction.DRAW2BONUSKEEP1),
	CASSINS_SPARROW("CASSIN'S SPARROW", "temp", 2, 20, 3, new String[] {"grassland"}, "BROWN", "EggLaying", "and 1seed 1worm", "Ground", BirdAction.LAYEGGONANYBIRD),
	CEDAR_WAXWING("CEDAR WAXWING", "temp", 3, 25, 3, new String[] {"forest", "grassland"}, "BROWN", "Flocking", "and 2berry", "Bowl", BirdAction.TUCK1BIRDANDGET1BERRY),
	CERULEAN_WARBLER("CERULEAN_WARBLER", "temp", 2, 20, 4, new String[] {"forest"}, "WHITE", "CardDrawing", "and 1seed 1worm", "Bowl", BirdAction.DRAW2BONUSKEEP1),
	CHESTNUT_COLLARED_LONGSPUR("CHESTNUT COLLARED LONGSPUR", "temp", 4, 25, 5, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 2seed 1worm", "Ground", BirdAction.DRAW2BONUSKEEP1),
	CHIHUAHUAN_RAVEN("CHIHUAHUAN RAVEN", "temp", 3, 112, 4, new String[] {"grassland"}, "BROWN", "Other", "and 2any 1rat", "Platform", BirdAction.DISCARDEGGANDGAIN2FOOD),
	CHIMNEY_SWIFT("CHIMNEY SWIFT", "temp", 2, 36, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2worm", "Wild", BirdAction.MOVEIFATVERYRIGHT),
	CHIPPING_SPARROW("CHIPPING SPARROW", "temp", 3, 23, 1, new String[] {"forest", "grassland"}, "BROWN", "EggLaying", "or 1seed 1worm", "Bowl", BirdAction.LAYEGGONANYBIRD),
	// CLARKS_GREBE
	CLARKS_NUTCRACKER("CLARK'S NUTCRACKER", "temp", 2, 61, 5, new String[] {"forest"}, "BROWN", "Other", "and 1any 2seed", "Platform", BirdAction.GAIN1SEEDANDCACHE),
	COMMON_GRACKLE("COMMON GRACKLE", "temp", 3, 43, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Flocking", "and 1any 1seed", "Bowl", BirdAction.TUCK1BIRDANDLAY1EGG),
	COMMON_LOON("COMMON LOON", "temp", 1, 117, 6, new String[] {"wetland"}, "BROWN", "Other", "and 1any 1fish", "Ground", BirdAction.DRAW1BIRDIFLEASTWETLAND),
	COMMON_MERGANSER("COMMON MERGANSER", "temp", 4, 86, 5, new String[] {"wetland"}, "BROWN", "Predator", "and 1any 1fish", "Cavity", BirdAction.ROLLDICEANDFINDFISH),
	COMMON_NIGHTHAWK("COMMON NIGHTHAWK", "temp", 2, 56, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2worm", "Ground", BirdAction.MOVEIFATVERYRIGHT),
	COMMON_RAVEN("COMMON RAVEN", "temp", 2, 135, 5, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2any 1rat", "Platform", BirdAction.DISCARDEGGANDGAIN2FOOD),
	// COMMON_YELLOWTHROAT
	COOPERS_HAWK("COOPER'S HAWK", "temp", 2, 79, 3, new String[] {"forest"}, "BROWN", "Predator", "and 1rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	DARK_EYED_JUNCO("DARK EYED JUNCO", "temp", 3, 23, 3, new String[] {"forest", "grassland"}, "BROWN", "Flocking", "and 1seed 1worm", "Ground", BirdAction.TUCK1BIRDANDGET1SEED),
	DICKCISSEL("DICKCISSEL", "temp", 3, 25, 4, new String[] {"grassland"}, "BROWN", "Flocking", "and 2seed 1worm", "Ground", BirdAction.TUCK1BIRDANDLAY1EGG),
	DOUBLE_CRESTED_CORMORANT("DOUBLE CRESTED CORMORANT", "temp", 3, 132, 3, new String[] {"wetland"}, "BROWN", "Flocking", "and 1any 1fish", "Platform", BirdAction.DISCARDFISHANDTUCK2BIRDS),
	// DOWNY_WOODPECKER
	// EASTERN_BLUEBIRD
	// EASTERN_KINGBIRD
	EASTERN_PHOEBE("EASTERN PHOEBE", "temp", 4, 28, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "or 1berry 1worm", "Wild", BirdAction.ALLGET1WORM),
	EASTERN_SCREECH_OWL("EASTERN SCREECH OWL", "temp", 2, 51, 4, new String[] {"forest"}, "BROWN", "Predator", "or 1rat 1worm", "Cavity", BirdAction.ROLLDICEANDFINDRAT),
	FERRUGINOUS_HAWK("FERRUGINOUS_HAWK", "temp", 2, 142, 6, new String[] {"grassland"}, "BROWN", "Predator", "and 2rat", "Platform", BirdAction.ROLLDICEANDFINDRAT),
	FISH_CROW("FISH CROW", "temp", 2, 91, 6, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any 1fish", "Platform", BirdAction.DISCARDEGGANDGAIN1FOOD),
	// FORESTERS_TERN
	FRANKLINS_GULL("FRANKLIN'S GULL", "temp", 2, 91, 3, new String[] {"grassland", "wetland"}, "BROWN", "CardDrawing", "and 1any 1fish", "Wild", BirdAction.DISCARDEGGANDGAIN2BIRDS),
	GOLDEN_EAGLE("GOLDEN EAGLE", "temp", 1, 201, 8, new String[] {"grassland", "wetland"}, "BROWN", "Predator", "and 3rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF100CM),
	GRASSHOPPER_SPARROW("GRASSHOPPER SPARROW", "temp", 2, 20, 2, new String[] {"grassland"}, "BROWN", "EggLaying", "or 1seed 1worm", "Ground", BirdAction.LAYEGGONANYBIRD),
	// GRAY_CATBIRD
	// GREAT_BLUE_HERON
	GREAT_CRESTED_FLYCATCHER("GREAT CRESTED FLYCATCHER", "temp", 3, 33, 5, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1worm", "Cavity", BirdAction.GET1WORMINBIRDFEEDER),
	// GREAT_EGRET
	GREAT_HORNED_OWL("GREAT HORNED OWL", "temp", 2, 112, 8, new String[] {"forest"}, "BROWN", "Predator", "and 3rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF100CM),
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