import javax.imageio.ImageIO;
import java.awt.image.*;

public enum Bird 
{
	// ENUM SEQUENCE: string name, string image, int dCount, int eggMax, int wingspan, int points, String[] habitat, String color, String aType, String food, String nest, BirdAction action
	// not an actual bird, just an example
	BALD_EAGLE("Bald Eagle", "Bald Eagle.jpg", 2, 4, 6, 7, new String[] {"Forest"}, "PINK", "Predator", "and 1fish 1worm", "bowl", BirdAction.GRABFISH);

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
	private Bird(String name, String imageFile, int deckCount, int eggMax, int wingspan, int pointValue,
			String[] habitat, String actionColor, String actionType, String foodRequired, String nest, BirdAction action)
	{
		this.name = name;
		this.deckCount = deckCount;
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
