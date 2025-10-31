import java.awt.image.*;
import java.lang.reflect.Array;

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
    }),

    //PHOTOGRAPHER:

    //  AMERICAN GOLDFINCH AMERICAN REDSTART AMERICAN WHITE PELICAN ASH-THROATED FLYCATCHER BARROW’S GOLDENEYE3 BLACK REDSTART BLACK SKIMMER BLACK TERN BLACK VULTURE 
    //  BLACK WOODPECKER BLACK-BELLIED WHISTLING DUCK BLACK-BILLED MAGPIE BLACK-CHINNED HUMMINGBIRD BLACK-CROWNED NIGHT-HERON BLACK-HEADED GULL BLACK-NECKED STILT 
    //  BLACK-TAILED GODWIT BLACK-THROATED DIVER BLUE GROSBEAK BLUE JAY BLUE-GRAY GNATCATCHER BLUE-WINGED WARBLER BLUETHROAT BREWER’S BLACKBIRD BRONZED COWBIRD 
    //  BROWN PELICAN BROWN-HEADED COWBIRD CERULEAN WARBLER CHESTNUT-COLLARED LONGSPUR COAL TIT COMMON BLACKBIRD COMMON GOLDENEYE COMMON YELLOWTHROAT EASTERN BLUEBIRD 
    //  EURASIAN GOLDEN ORIOLE EUROPEAN GOLDFINCH EUROPEAN GREEN WOODPECKER EUROPEAN HONEY BUZZARD FERRUGINOUS HAWK GOLDCREST GOLDEN EAGLE3 GRAY CATBIRD GREAT BLUE HERON 
    //  GREEN HERON GREY HERON GREYLAG GOOSE INDIGO BUNTING LAZULI BUNTING LESSER WHITETHROAT MOUNTAIN BLUEBIRD NORTHERN BOBWHITE PAINTED WHITESTART PURPLE GALLINULE 
    //  PURPLE MARTIN RED CROSSBILL RED KITE RED KNOT RED-BACKED SHRIKE RED-BELLIED WOODPECKER RED-BREASTED MERGANSER RED-BREASTED NUTHATCH RED-COCKADED WOODPECKER 
    //  RED-EYED VIREO RED-HEADED WOODPECKER RED-LEGGED PARTRIDGE RED-SHOULDERED HAWK RED-TAILED HAWK RED-WINGED BLACKBIRD ROSE-BREASTED GROSBEAK ROSEATE SPOONBILL 
    //  RUBY-CROWNED KINGLET RUBY-THROATED HUMMINGBIRD RUDDY DUCK SNOWY EGRET SNOWY OWL VIOLET-GREEN SWALLOW4 WHITE STORK WHITE WAGTAIL WHITE-BACKED WOODPECKER 
    //  WHITE-BREASTED NUTHATCH WHITE-CROWNED SPARROW WHITE-FACED IBIS WHITE-THROATED DIPPER WHITE-THROATED SWIFT YELLOW-BELLIED SAPSUCKER YELLOW-BILLED CUCKOO 
    //  YELLOW-BREASTED CHAT YELLOW-HEADED BLACKBIRD YELLOW-RUMPED WARBLER YELLOWHAMMER
    
    PHOTOGRAPHER("temp.jpg", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 
        //HashSet of bird ENUMs with color in their names
        HashSet<Bird> applicableBirds = Bird.getBonusName("photographer"); 
        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //iterates through all cards on the board, counting cards whose Bird ENUM has a color in name
        for (BirdInstance birdCard: birdSuperList)
        {
            if(applicableBirds.contains(birdCard.getBirdEnum()));
                count++;
        }

	    // for(Map.Entry<String, ArrayList<BirdInstance>> entry: player.getBoard().entrySet()) {
    	// 	String h = entry.getKey();
    	// 	ArrayList<BirdInstance> n = entry.getValue();
    	// 	for(int i = 0; i < n.size; i++) {
    	// 		BirdInstance current = n.get(i);
    	// 		String c = current.getName();
    	// 		if(c.toLowerCase.contains("ash") || c.toLowerCase.contains("black") || c.toLowerCase.contains("blue") ||
    	// 				c.toLowerCase.contains("bronze") || c.toLowerCase.contains("brown") || c.toLowerCase.contains("cerulean")
    	// 				|| c.toLowerCase.contains("chestnut") || c.toLowerCase.contains("ferruginous") || 
    	// 				c.toLowerCase.contains("gold") || c.toLowerCase.contains("gray") || c.toLowerCase.contains("green") || 
    	// 				c.toLowerCase.contains("indigo") || c.toLowerCase.contains("lazuli") || c.toLowerCase.contains("purple")
    	// 				|| c.toLowerCase.contains("red") || c.toLowerCase.contains("rose") || c.toLowerCase.contains("roseate") 
    	// 				|| c.toLowerCase.contains("ruby") || c.toLowerCase.contains("ruddy") || c.toLowerCase.contains("rufous") || 
    	// 				c.toLowerCase.contains("snowy") || c.toLowerCase.contains("violet") || c.toLowerCase.contains("white") ||
    	// 				c.toLowerCase.contains("yellow"))
    	// 			c++;
    	// 	}
    	// }
    	if(count == 4 || count == 5)
    		player.addPoints(3);
    	if(count >= 6)
    		player.addPoints(6);
    });     
    
    //CARTOGRAPHER:

    // AMERICAN AVOCET AMERICAN BITTERN AMERICAN COOT AMERICAN CROW AMERICAN GOLDFINCH AMERICAN KESTREL AMERICAN OYSTERCATCHER AMERICAN REDSTART AMERICAN ROBIN 
    // AMERICAN WHITE PELICAN AMERICAN WOODCOCK ATLANTIC PUFFIN BALTIMORE ORIOLE CALIFORNIA CONDOR CALIFORNIA QUAIL CANADA GOOSE CAROLINA CHICKADEE 
    // CAROLINA WREN CHIHUAHUAN RAVEN COMMON MOORHEN CORSICAN NUTHATCH EASTERN BLUEBIRD EASTERN IMPERIAL EAGLE EASTERN KINGBIRD EASTERN PHOEBE EASTERN SCREECH OWL 
    // EURASIAN COLLARED DOVE EURASIAN GOLDEN ORIOLE EURASIAN HOBBY EURASIAN JAY EURASIAN MAGPIE EURASIAN NUTCRACKER EURASIAN NUTHATCH EURASIAN SPARROWHAWK 
    // EURASIAN TREE SPARROW EUROPEAN BEE-EATER EUROPEAN GOLDFINCH EUROPEAN GREEN WOODPECKER EUROPEAN HONEY BUZZARD EUROPEAN ROBIN EUROPEAN ROLLER EUROPEAN 
    // TURTLE DOVE GREATER PRAIRIE CHICKEN INCA DOVE MISSISSIPPI KITE MOUNTAIN BLUEBIRD MOUNTAIN CHICKADEE NORTHERN BOBWHITE NORTHERN CARDINAL NORTHERN FLICKER 
    // NORTHERN GANNET NORTHERN GOSHAWK NORTHERN HARRIER NORTHERN MOCKINGBIRD NORTHERN SHOVELER SANDHILL CRANE SAVANNAH SPARROW WESTERN MEADOWLARK WESTERN TANAGER     
    
    //ANATOMIST:

    // ASH-THROATED FLYCATCHER BARROW’S GOLDENEYE BLACK-BELLIED WHISTLING DUCK BLACK-BILLED MAGPIE BLACK-CHINNED HUMMINGBIRD BLACK-CROWNED NIGHT-HERON 
    // BLACK-HEADED GULL BLACK-NECKED STILT BLACK-TAILED GODWIT BLACK-THROATED DIVER BLUE GROSBEAK BLUE-WINGED WARBLER BLUETHROAT BROAD-WINGED HAWK 
    // BROWN-HEADED COWBIRD CANVASBACK CEDAR WAXWING CHESTNUT-COLLARED LONGSPUR COMMON GOLDENEYE COMMON YELLOWTHROAT DARK-EYED JUNCO DOUBLE-CRESTED CORMORANT 
    // EURASIAN COLLARED DOVE GOLDCREST GREAT CRESTED FLYCATCHER GREAT CRESTED GREBE LESSER WHITETHROAT LOGGERHEAD SHRIKE LONG-TAILED TIT PARROT CROSSBILL 
    // PIED-BILLED GREBE RED CROSSBILL RED-BACKED SHRIKE RED-BELLIED WOODPECKER RED-BREASTED MERGANSER RED-BREASTED NUTHATCH RED-EYED VIREO RED-HEADED WOODPECKER 
    // RED-LEGGED PARTRIDGE RED-SHOULDERED HAWK RED-TAILED HAWK RED-WINGED BLACKBIRD RING-BILLED GULL ROSE-BREASTED GROSBEAK ROSEATE SPOONBILL RUBY-CROWNED KINGLET 
    // RUBY-THROATED HUMMINGBIRD SCISSOR-TAILED FLYCATCHER SHORT-TOED TREECREEPER WHITE WAGTAIL WHITE-BACKED WOODPECKER WHITE-BREASTED NUTHATCH WHITE-CROWNED SPARROW 
    // WHITE-FACED IBIS WHITE-THROATED DIPPER WHITE-THROATED SWIFT YELLOW-BELLIED SAPSUCKER YELLOW-BILLED CUCKOO YELLOW-BREASTED CHAT YELLOW-HEADED BLACKBIRD YELLOW-RUMPED WARBLER
	

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
