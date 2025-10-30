import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.*;

public enum BonusCard implements BonusCardInterface
{
    // temporary bonus card just so the errors stop. also an example
    ECOLOGIST("temp.jpg", (player) -> {
        // Some logic that finds the habitats with the least amount of birds
    }),

	//if player's hand has more than 8 birds, adds 7 points, else if more than 5 birds, adds 4 points 
    VISIONARYLEADER("temp.jpg", (player) -> {
        int size = player.getBirdHand().size();
        if(size >= 8) player.addPoints(7);
        else if (size >= 5) player.addPoints(4);
    }),

	//checks for all birds on board with Ground or Wild nest type, adds 4 points if 4-5 birds or adds 7 points if 6+ birds 
    ENCLOSURE_BUILDER("temp.jpg", (player) -> {
       int count = 0;
        for (Map.Entry<String, ArrayList<BirdInstance>> entry : player.getBoard().entrySet()) {
            ArrayList<BirdInstance> birdList = entry.getValue();

            for (BirdInstance bird : birdList) {
            String nest = bird.getNest();
                if (nest.equals("Ground") || nest.equals("Wild")) {
                    count++;
                }
            }
            if (count == 4 || count == 5) {
                player.addPoints(4);
            }
            if (count >= 6) {
                player.addPoints(7);
            }
        }
    });

    //VARIABLES:

    //stores the method for the action, retrieved from the abstract method
    private final BonusCardInterface bonusAbility;
    private int deckCount;
    private final String imageFileString;

    //CONSTRUCTOR:
    private BonusCard(String imageFileString, BonusCardInterface bonusAbility)
    {
        this.bonusAbility = bonusAbility;
        this.deckCount = 1;
        this.imageFileString = imageFileString;
        ImageHandler.setGroup(imageFileString, "BonusCards");
    }

    //RETURN METHODS:
    // returns the deckCount of bonusCard
    public int getDeckCount() { return deckCount; }

    // returns a BufferedImage with the bonus' image file
    public String getImage() { 
        return imageFileString; 
    }

    // VOID METHODS / MUTATOR METHODS:
    public void removeCardFromDeck() { deckCount -= 1; }

    //carries out the card's scoring
    @Override
    public void bonusScore(Player player)
    {
        bonusAbility.bonusScore(player);
    }
}
