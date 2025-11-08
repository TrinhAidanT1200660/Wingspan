import java.util.ArrayList;
import java.util.HashSet;


public enum BonusCard implements BonusCardInterface
{

    ANATOMIST("anatomist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 
        //HashSet of bird ENUMs with body parts in their names
        HashSet<Bird> applicableBirds = Bird.getBonusName("cartographer"); 
        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //iterates through all cards on the board, counting cards whose Bird ENUM has a body part in name
        for (BirdInstance birdCard: birdSuperList)
        {
            if(applicableBirds.contains(birdCard.getBirdEnum()))
                count++;
        }

    	if(count == 2 || count == 3)
    		player.addPoints(3);
    	if(count >= 4)
    		player.addPoints(7);
    }),

    BACKYARD_BIRDER("backyard_birder.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getPointValue() < 4)
                count++;
        }

        //calculates points
    	if(count == 5 || count == 6)
    		player.addPoints(3);
    	if(count >= 7)
    		player.addPoints(6);
    }),

    BIRD_COUNTER("bird_counter.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getActionType().equalsIgnoreCase("Flocking"))
                count++;
        }

        //calculates points
        player.addPoints(count*2);
    }),

    BIRD_FEEDER("bird_feeder.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getFoodRequired().contains("seed"))
                count++;
        }

        //calculates points
        if (count >= 5 && count <= 7) 
            player.addPoints(3);
        if (count >= 8) 
            player.addPoints(7);
    }),

    BREEDING_MANAGER("breeding_manager.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getEggStored() >= 4)
                count++;
        }

        //calculates points
        player.addPoints(count*2);
    }),

    CARTOGRAPHER("cartographer.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 
        //HashSet of bird ENUMs with places in their names
        HashSet<Bird> applicableBirds = Bird.getBonusName("cartographer"); 
        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //iterates through all cards on the board, counting cards whose Bird ENUM has a place in name
        for (BirdInstance birdCard: birdSuperList)
        {
            if(applicableBirds.contains(birdCard.getBirdEnum()))
                count++;
        }

        //calculates points
    	if(count == 2 || count == 3)
    		player.addPoints(3);
    	if(count >= 4)
    		player.addPoints(7);
    }),

    //checks for all birds on board with Ground or Wild nest type, adds 4 points if 4-5 birds or adds 7 points if 6+ birds 
    ENCLOSURE_BUILDER("enclosure_builder.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getNest().equalsIgnoreCase("Ground") || birdCard.getNest().equalsIgnoreCase("Wild"))
                count++;
        }

        //calculates points
        if (count == 4 || count == 5) 
            player.addPoints(4);
        if (count >= 6)
            player.addPoints(7);
    }),


    FALCONER("falconer.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getActionType().equalsIgnoreCase("Predator"))
                count++;
        }

        //calculates points
        player.addPoints(count*2);
    }),


    FISHERY_MANAGER("fishery_manager.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getFoodRequired().contains("fish"))
                count++;
        }

        //calculates points
        if (count == 2 || count == 3)
            player.addPoints(3);
        if (count >= 4) 
            player.addPoints(8);
    }),


    FOOD_WEB_EXPERT("food_web_expert.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getFoodRequired().contains("worm"))
                count++;
        }

        //calculates points
        player.addPoints(count*2);
    }),


    FORESTER("forester.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getHabitat().length == 1 && birdCard.getHabitat()[0].equalsIgnoreCase("forest"))
                count++;
        }

        //calculates points
        if (count == 3 || count == 4) 
            player.addPoints(4);
        if (count >= 5) 
            player.addPoints(5);
    }),


    HISTORIAN("historian.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getName().contains("'S"))
                count++;
        }

        //calculates points
        player.addPoints(count*2);
    }),


    LARGE_BIRD_SPECIALIST("large_bird_specialist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getWingspan() > 65)
                count++;
        }

        //calculates points
        if (count == 4 || count == 5)
            player.addPoints(3);
        if (count >= 6)
            player.addPoints(6);
    }),


    NEST_BOX_BUILDER("best_box_builder.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getNest().equalsIgnoreCase("Cavity") || birdCard.getNest().equalsIgnoreCase("Wild"))
                count++;
        }

        //calculates points
        if (count == 4 || count == 5) 
            player.addPoints(4);
        if (count >= 6)
            player.addPoints(7);
    }),


    OMNIVORE_SPECIALIST("omnivore_specialist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getFoodRequired().contains("any"))
                count++;
        }

        //calculates points
        player.addPoints(count*2);
    }),


    OOLOGIST("oologist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getEggStored() >= 1)
                count++;
        }

        //calculates points
        if (count == 7 || count == 8) 
            player.addPoints(3);
        if (count >= 9) 
            player.addPoints(6);
    }),


    PASSERINE_SPECIALIST("passerine_specialist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getWingspan() <= 30)
                count++;
        }

        //calculates points
        if (count == 4 || count == 5) 
            player.addPoints(3);
        if (count >= 6) 
            player.addPoints(6);
    }),

    PHOTOGRAPHER("photographer.png", (player) -> {
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
            if(applicableBirds.contains(birdCard.getBirdEnum()))
                count++;
        }

    	if(count == 4 || count == 5)
    		player.addPoints(3);
    	if(count >= 6)
    		player.addPoints(7);
    }),

    PLATFORM_BUILDER("platform_builder.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getNest().equalsIgnoreCase("Platform") || birdCard.getNest().equalsIgnoreCase("Wild"))
                count++;
        }

        //calculates points
        if (count == 4 || count == 5) 
            player.addPoints(4);
        if (count >= 6)
            player.addPoints(7);
    }),


    PRAIRIE_MANAGER("prairie_manager.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getHabitat().length == 1 && birdCard.getHabitat()[0].equalsIgnoreCase("grassland"))
                count++;
        }

        //calculates points
        if (count == 2 || count == 3) 
            player.addPoints(3);
        if (count >= 4) 
            player.addPoints(8);
    }),


    RODENTOLOGIST("rodentologist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getFoodRequired().contains("rat"))
                count++;
        }

        //calculates points
        player.addPoints(count*2);
    }),

    //if player's hand has more than 8 birds, adds 7 points, else if more than 5 birds, adds 4 points 
    VISIONARY_LEADER("visionary_leader.png", (player) -> {
        //gets size of player hand
        int count = player.getBirdHand().size();
        
        //calculates points
        if (count >= 5 && count <= 7)
            player.addPoints(4);
        if(count >= 8) 
            player.addPoints(7);
    }),


    VITACULTURALIST("vitaculturalist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getFoodRequired().contains("berry"))
                count++;
        }

        //calculates points
        if (count == 2 || count == 3) 
            player.addPoints(3);
        if (count >= 4)
            player.addPoints(7);
    }),


    WETLAND_SCIENTIST("wetland_scientist.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getHabitat().length == 1 && birdCard.getHabitat()[0].equalsIgnoreCase("wetland"))
                count++;
        }

        //calculates points
        if (count == 3 || count == 4) 
            player.addPoints(3);
        if (count >= 5)
            player.addPoints(7);
    }),


    WILDLIFE_GARDENER("wildlife_gardener.png", (player) -> {
        //how many cards are counted towards the bonus card
	    int count = 0; 

        //ArrayList of every bird on the board
        ArrayList<BirdInstance> birdSuperList = new ArrayList<>(); 

        //iterates through the player board, combining the habitats into one ArrayList
        for (ArrayList<BirdInstance> birdList: player.getBoard().values())
            birdSuperList.addAll(birdList);
        
        //counts cards whose Bird fits bonus
        for (BirdInstance birdCard: birdSuperList)
        {
            if(birdCard.getNest().equalsIgnoreCase("Bowl") || birdCard.getNest().equalsIgnoreCase("Wild"))
                count++;
        }

        //calculates points
        if (count == 4 || count == 5) 
            player.addPoints(4);
        if (count >= 6)
            player.addPoints(7);
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
