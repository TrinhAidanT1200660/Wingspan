import java.awt.image.*;
import javax.imageio.ImageIO;

public enum BonusCard implements BonusCardInterface
{
    // temporary bonus card just so the errors stop. also an example
    ECOLOGIST("temp.jpg", (player) -> {
        // Some logic that finds the habitats with the least amount of birds
        // test change in code
    });

    //VARIABLES:

    //stores the method for the action, retrieved from the abstract method
    private final BonusCardInterface bonusAbility;
    private int deckCount;
    private BufferedImage bonusImage;

    //CONSTRUCTOR:
    private BonusCard(String imageFile, BonusCardInterface bonusAbility)
    {
        this.bonusAbility = bonusAbility;
        this.deckCount = 1;

        // directly uses the image file name to create buffered image
		try
		{
			// need to define the package that will be used to hold images before this is done
			this.bonusImage = ImageIO.read(BonusCard.class.getResource("/packageNameHere/" + imageFile));
		}
		catch (Exception E)
		{
			System.out.println("Could not load bonus ENUM image: " + E.getMessage());
			return;
		}
    }

    //VOID METHODS:

    //carries out the card's scoring
    @Override
    public void bonusScore(Player player)
    {
        bonusAbility.bonusScore(player);
    }

    //RETURN METHODS:
    public int getDeckCount() { return deckCount; }

    // returns a BufferedImage with the bird's image file
	public BufferedImage getImage() { return bonusImage; }

    //MUTATOR METHODS:
    public void removeCardFromDeck() { deckCount -= 1; }
}
