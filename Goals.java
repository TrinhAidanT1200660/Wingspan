import java.util.*;

public enum Goals {
    // There are only 8 goals i believe.
    // Since there is such a small data size, I will use switch case rather than interface to implement methods
    // The only parameter is the image String
    // ENUMS are sorted by similarities/stratas
    // # Eggs in habitat
    EGGS_IN_FOREST("temp"),
    EGGS_IN_GRASSLAND("temp"),
    EGGS_IN_WETLAND("temp"),
    // # Birds in habitat
    BIRDS_IN_FOREST("temp"),
    BIRDS_IN_GRASSLAND("temp"),
    BIRDS_IN_WETLAND("temp"),
    // # nest birds with eggs
    BOWL_BIRDS_WITH_EGGS("temp"),
    CAVITY_BIRDS_WITH_EGGS("temp"),
    PLATFORM_BIRDS_WITH_EGGS("temp"),
    GROUND_BIRDS_WITH_EGGS("temp"),
    // # of eggs in nest types
    EGGS_IN_BOWL("temp"),
    EGGS_IN_CAVITY("temp"),
    EGGS_IN_PLATFORM("temp"),
    EGGS_IN_GROUND("temp"),
    // misc
    TOTAL_BIRDS("temp"), // # of total birds
    SETS_OF_3_EGGS_IN_EACH_HABITAT("temp") // # of eggs in specific habitat / 3 (integer divison) added with same operation on other habitat
    ;
    
    private final String imageFileString;

    private Goals(String imageFileString)
    {
        this.imageFileString = imageFileString;
    }

    public void determineRankings(Goals goal, ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        switch(goal)
        {
            // # Eggs in habitat
            case EGGS_IN_FOREST: 
            case EGGS_IN_GRASSLAND:
            case EGGS_IN_WETLAND: eggHabitat(goal, playerList, roundsPlayed, isCompetitive); break;
            // # Birds in habitat
            case BIRDS_IN_FOREST:
            case BIRDS_IN_GRASSLAND:
            case BIRDS_IN_WETLAND: birdsHabitat(goal, playerList, roundsPlayed, isCompetitive); break;
            // # nest birds with eggs
            case BOWL_BIRDS_WITH_EGGS:
            case CAVITY_BIRDS_WITH_EGGS:
            case PLATFORM_BIRDS_WITH_EGGS:
            case GROUND_BIRDS_WITH_EGGS: birdsNest(goal, playerList, roundsPlayed, isCompetitive); break;
            // # of eggs in nest types
            case EGGS_IN_BOWL:
            case EGGS_IN_CAVITY:
            case EGGS_IN_PLATFORM:
            case EGGS_IN_GROUND: eggsNest(goal, playerList, roundsPlayed, isCompetitive); break;
            // misc
            case TOTAL_BIRDS: totalBirds(goal, playerList, roundsPlayed, isCompetitive); break;
            case SETS_OF_3_EGGS_IN_EACH_HABITAT: setOf3(goal, playerList, roundsPlayed, isCompetitive); break;
            default: System.out.println("ERROR: goal could not be identified");
        }
    }

    // sorts the players based on the # of eggs in habitat
    public void eggHabitat(Goals goal, ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {

    }

    // sorts the players based on the # of birds in habitat
    public void birdsHabitat(Goals goal, ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {

    }

    // sorts the players based on the # of birds with a tain nest type with eggs on it
    public void birdsNest(Goals goal, ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {

    }

    // sorts the players based on the # of eggs on a certain nest type
    public void eggsNest(Goals goal, ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {

    }

    // sorts the players based on the # of birds on the board
    public void totalBirds(Goals goal, ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        // competitive scoring; should be able to copy certain parts over to others
        if(isCompetitive)
        {
            ArrayList<Player> sortedPlayers = new ArrayList<>(playerList); // local copy of playerList
            // some weird sorting lambda method from java; should sort all 5 players
            sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getBoard().values().size(), p1.getBoard().values().size()));

            // copyable from here on should be
            int rank = 1;         // current rank
            int previousScore = -1; // track previous player score
            int sameRankCount = 0; // starts at 0 in case for first player

            for(int i = 0; i < sortedPlayers.size(); i++)
            {
                Player p = sortedPlayers.get(i);
                int score = p.getBoard().values().size(); // if copied need to change this

                // if score same, increase the rank count
                if (score == previousScore)
                    sameRankCount++;
                else { // no tie means rankings will jump by the tied rank number and reset ties
                    rank += sameRankCount;
                    sameRankCount = 1;
                }

                previousScore = score; // updates previous score

                p.setGoalRankings(roundsPlayed, Math.min(rank, 4)); // updates player's rank for UI to use on goal board. If ranking is greater than 4, goes back to 4
                if(rank == 1) p.addPoints(4 + roundsPlayed); // directly adds the points
                else if(rank == 2) p.addPoints(1 + roundsPlayed);
                else if(rank == 3) p.addPoints(0 + roundsPlayed);
            }
        }
        // non competitive scoring; probably has to be manual for each except end goal rankings
        else
        {
            for(Player p: playerList)
            {
                int amount = p.getBoard().values().size();

                p.setGoalRankings(roundsPlayed, Math.min(5, amount));
                p.addPoints(Math.min(5, amount));
            }
        }
    }
    
    // sorts the players based on the # of eggs in specific habitat / 3 (integer divison) added with same operation on other habitat
    public void setOf3(Goals goal, ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {

    }
}
