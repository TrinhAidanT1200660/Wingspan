import java.awt.image.*;
import javax.imageio.ImageIO;

public enum BonusCard implements BonusCardInterface
{
    // temporary bonus card just so the errors stop. also an example
    ECOLOGIST("temp.jpg", (player) -> {
        // Some logic that finds the habitats with the least amount of birds
    });

    //VARIABLES:

    //stores the method for the action, retrieved from the abstract method
    private final BonusCardInterface bonusAbility;
    private int deckCount;
    private final String imageFileString;
	private BufferedImage bufferedImageFile;

    //CONSTRUCTOR:
    private BonusCard(String imageFileString, BonusCardInterface bonusAbility)
    {
        this.bonusAbility = bonusAbility;
        this.deckCount = 1;
        this.imageFileString = imageFileString;
    }

    //RETURN METHODS:
    // returns the deckCount of bonusCard
    public int getDeckCount() { return deckCount; }

    // returns a BufferedImage with the bonus' image file
    public BufferedImage getImage() 
	{ 
		if (bufferedImageFile == null)
		{
			// directly uses the image file name to create buffered image
			try
			{
				// need to define the package that will be used to hold images before this is done
				this.bufferedImageFile = ImageIO.read(BonusCard.class.getResource("/packageNameHere/" + imageFileString));
			}
			catch (Exception E)
			{
				System.out.println("Could not load bonus ENUM image: " + E.getMessage());
				return null;
			}
		}

		return bufferedImageFile;
	}

    // VOID METHODS / MUTATOR METHODS:
    public void removeCardFromDeck() { deckCount -= 1; }

    //carries out the card's scoring
    @Override
    public void bonusScore(Player player)
    {
        bonusAbility.bonusScore(player);
    }

    public void releaseImage() { bufferedImageFile = null; }
}
