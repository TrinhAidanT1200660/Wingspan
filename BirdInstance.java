
public class BirdInstance {
	private Bird birdEnum;
    private int heldEggs;
    private int tuckedCards;
    private int cachedFood;
    private boolean playedThisTurn;

	public BirdInstance(Bird bird) {
		this.birdEnum = bird;
        this.heldEggs = 0;
        this.tuckedCards = 0;
        this.cachedFood = 0;
        this.playedThisTurn = false;
	}

	// returns the bird enum
    public Bird getBirdEnum() {return birdEnum;}

	// returns a String with the bird's name
    public String getName() { return birdEnum.getName(); }

	// returns a BufferedImage with the bird's image file
	public String getImage() { return birdEnum.getImage(); }

	// returns an int with the number of bird cards left in the deck
	public int getDeckCount() { return birdEnum.getDeckCount(); }

	// returns an int with the bird's maximum egg capacity
	public int getEggMax() { return birdEnum.getEggMax(); }

	// returns an int with the bird's wingspan in centimeters
	public int getWingspan() { return birdEnum.getWingspan(); }

	// returns an int with the bird's point value
	public int getPointValue() { return birdEnum.getPointValue(); }

	// returns a String array with the habitat this bird can be in
	public String[] getHabitat() { return birdEnum.getHabitat(); }

	// returns a String with the bird's action color type (PINK, WHITE, BROWN)
	public String getActionColor() { return birdEnum.getActionColor(); }

	// returns a String with the bird's action type (EggLaying, CardDrawing, etc.)
	public String getActionType() { return birdEnum.getActionType(); }

	// returns a String with the food required for this bird
	public String getFoodRequired() { return birdEnum.getFoodRequired(); }

	// returns a String with the bird's nest type
	public String getNest() { return birdEnum.getNest(); }
	
	// VOID METHODS
	
	// performs this bird's stored BirdAction ability on the given player
	public void performAction(Game gameContext, Player player) { birdEnum.performAction(gameContext, player); }
}