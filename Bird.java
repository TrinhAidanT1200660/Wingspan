import java.awt.image.*;
import javax.imageio.ImageIO;

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
	PROTHONOTARY_WARBLER("PROTHONOTARY WARBLER", "temp.jpg", 4, 23, 8, new String[] {"forest", "wetland"}, "NONE", "Other", "and 1seed 2worm", "Cavity", BirdAction.NONE),
	GREATER_PRAIRIE_CHICKEN("GREATER PRAIRIE-CHICKEN", "temp.jpg", 4, 71, 5, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 2seed 1worm", "Ground", BirdAction.DRAW2BONUSKEEP1),
	NORTHERN_CARDINAL("NORTHERN CARDINAL", "temp.jpg", 5, 30, 3, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1seed", "Bowl", BirdAction.GET1BERRY),
	BROWN_PELICAN("BROWN PELICAN", "temp.jpg", 2, 201, 4, new String[] {"wetland"}, "WHITE", "Other", "and 2fish", "Platform", BirdAction.GET3FISH),
	CANVASBACK("CANVASBACK", "temp.jpg", 4, 82, 4, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1any 1seed", "Wild", BirdAction.ALLDRAW1BIRD);

	private final String name; // holds birds name
	private BufferedImage imageFile; // holds birds image file
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
	private Bird(String name, String imageFile, int eggMax, int wingspan, int pointValue,
			String[] habitat, String actionColor, String actionType, String foodRequired, String nest, BirdAction action)
	{
		this.name = name;
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
		// directly uses the image file name to create buffered image
		try
		{
			// need to define the package that will be used to hold images before this is done
			this.imageFile = ImageIO.read(Bird.class.getResource("/packageNameHere/" + imageFile));
		}
		catch (Exception E)
		{
			System.out.println("Could not load bird ENUM image for" + name + ": " + E.getMessage());
			return;
		}
	}
	
	// RETURN METHODS
	
	// returns a String with the bird's name
	public String getName() { return name; }

	// returns a BufferedImage with the bird's image file
	public BufferedImage getImage() { return imageFile; }

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
	
	// VOID METHODS
	
	// performs this bird's stored BirdAction ability on the given player
	public void performAction(Player player) { action.execute(player); }
}