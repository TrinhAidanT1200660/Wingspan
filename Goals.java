import java.util.*;

public enum Goals {
    // There are only 16 goals i believe.
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

    public void determineRankings(ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        switch(this)
        {
            // # Eggs in habitat
            case EGGS_IN_FOREST: 
            case EGGS_IN_GRASSLAND:
            case EGGS_IN_WETLAND: eggHabitat(playerList, roundsPlayed, isCompetitive); break;
            // # Birds in habitat
            case BIRDS_IN_FOREST:
            case BIRDS_IN_GRASSLAND:
            case BIRDS_IN_WETLAND: birdsHabitat(playerList, roundsPlayed, isCompetitive); break;
            // # nest birds with eggs
            case BOWL_BIRDS_WITH_EGGS:
            case CAVITY_BIRDS_WITH_EGGS:
            case PLATFORM_BIRDS_WITH_EGGS:
            case GROUND_BIRDS_WITH_EGGS: birdsNest(playerList, roundsPlayed, isCompetitive); break;
            // # of eggs in nest types
            case EGGS_IN_BOWL:
            case EGGS_IN_CAVITY:
            case EGGS_IN_PLATFORM: // yo, i thought p comes before g but i realized but too late and lazy to change a bunch of different methods :laugh:
            case EGGS_IN_GROUND: eggsNest(playerList, roundsPlayed, isCompetitive); break;
            // misc
            case TOTAL_BIRDS: totalBirds(playerList, roundsPlayed, isCompetitive); break;
            case SETS_OF_3_EGGS_IN_EACH_HABITAT: setOf3(playerList, roundsPlayed, isCompetitive); break;
            default: System.out.println("ERROR: goal could not be identified");
        }
    }

    // sorts the players based on the # of eggs in habitat
    public void eggHabitat(ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        String habitat;
        if(this == EGGS_IN_FOREST) habitat = "forest";
        else if(this == EGGS_IN_GRASSLAND) habitat = "grassland";
        else if(this == EGGS_IN_WETLAND) habitat = "wetland";
        else { System.out.println("ERROR: the goal couldn't find its habitat in eggs in habitat goals somehow gg we screwed"); return; }

        ArrayList<Player> sortedPlayers = new ArrayList<>(playerList); // local copy of playerList
        // some weird sorting lambda method from java; should sort all 5 players
        sortedPlayers.sort((p1, p2) -> 
        {
            // first gets the player board: second gets the arrayList of the habitat: third converts the arrayList to a stream to get more lambda functions without needing to manually iterate
            // fourth mapToInt converts each bird object to an int with eggCount (idk how C++ wizardry is here, but :: does getEggStored for each bird)
            // last just sums up birds
            int eggsP1 = p1.getBoard().get(habitat).stream().mapToInt(BirdInstance::getEggStored).sum();
            int eggsP2 = p2.getBoard().get(habitat).stream().mapToInt(BirdInstance::getEggStored).sum();
            // place with p2 first in order to sort from largest to smallest
            return Integer.compare(eggsP2, eggsP1);
        }); 

        // does all the rank sorting in this method. Im lwk the goat for this it might just be glorious
        rankSorting(sortedPlayers, isCompetitive, roundsPlayed, habitat);
    }

    // sorts the players based on the # of birds in habitat
    public void birdsHabitat(ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        String habitat;
        if(this == BIRDS_IN_FOREST) habitat = "forest";
        else if(this == BIRDS_IN_GRASSLAND) habitat = "grassland";
        else if(this == BIRDS_IN_WETLAND) habitat = "wetland";
        else { System.out.println("ERROR: the goal couldn't find its habitat in birds in habitat goals somehow gg we screwed"); return; }

        ArrayList<Player> sortedPlayers = new ArrayList<>(playerList); // local copy of playerList 
        // some weird sorting lambda method from java; should sort all 5 players
        // place with p2 first in order to sort from largest to smallest
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getBoard().get(habitat).size(), p1.getBoard().get(habitat).size()));

        // does all the rank sorting in this method. Im lwk the goat for this it might just be glorious
        rankSorting(sortedPlayers, isCompetitive, roundsPlayed, habitat);
    }

    // sorts the players based on the # of birds with a certain nest type with eggs on it
    public void birdsNest(ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        String nest;
        if(this == BOWL_BIRDS_WITH_EGGS) nest = "Bowl";
        else if(this == CAVITY_BIRDS_WITH_EGGS) nest = "Cavity";
        else if(this == PLATFORM_BIRDS_WITH_EGGS) nest = "Platform";
        else if(this == GROUND_BIRDS_WITH_EGGS) nest = "Ground";
        else { System.out.println("ERROR: the goal couldn't find its nest in birds with eggs in nest goals somehow gg we screwed"); return; }

        ArrayList<Player> sortedPlayers = new ArrayList<>(playerList); // local copy of playerList 
        // some weird sorting lambda method from java; should sort all 5 players
        sortedPlayers.sort((p1, p2) -> {
            // first gets player board and the values as a collection; second converts the arrayList to stream to get more lambda functions without needing to manually iterate
            // third flatMap converts each of the lists into a stream and flattens them (merges them) to give a single stream of birds
            // fourth filters amount of birds has an egg stored on it and has the right nest type; last counts amount of birds that match the criteria and casts to int
            int countP1 = (int) p1.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(nest) && b.getEggStored() > 0).count();
            int countP2 = (int) p2.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(nest) && b.getEggStored() > 0).count();

            // place with p2 first in order to sort from largest to smallest
            return Integer.compare(countP2, countP1);
        });

        // does all the rank sorting in this method. Im lwk the goat for this it might just be glorious
        rankSorting(sortedPlayers, isCompetitive, roundsPlayed, nest);
    }

    // sorts the players based on the # of eggs on a certain nest type
    public void eggsNest(ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        String nest;
        if(this == EGGS_IN_BOWL) nest = "Bowl";
        else if(this == EGGS_IN_CAVITY) nest = "Cavity";
        else if(this == EGGS_IN_PLATFORM) nest = "Platform";
        else if(this == EGGS_IN_GROUND) nest = "Ground";
        else { System.out.println("ERROR: the goal couldn't find its nest eggs in nest goals somehow gg we screwed"); return; }

        ArrayList<Player> sortedPlayers = new ArrayList<>(playerList); // local copy of playerList 
        // some weird sorting lambda method from java; should sort all 5 players
        sortedPlayers.sort((p1, p2) -> {
            // first gets player board and the values as a collection; second converts the arrayList to stream to get more lambda functions without needing to manually iterate
            // third flatMap converts each of the lists into a stream and flattens them (merges them) to give a single stream of birds
            // fourth filters amount of birds that has the right nest type; fifth converts each bird object to an int with eggCount
            int eggsP1 = p1.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(nest)).mapToInt(BirdInstance::getEggStored).sum();
            int eggsP2 = p2.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(nest)).mapToInt(BirdInstance::getEggStored).sum();

            // place with p2 first in order to sort from largest to smallest
            return Integer.compare(eggsP2, eggsP1);
        });

        // does all the rank sorting in this method. Im lwk the goat for this it might just be glorious
        rankSorting(sortedPlayers, isCompetitive, roundsPlayed, nest);
    }

    // sorts the players based on the # of birds on the board
    public void totalBirds(ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        // Need to have players sorted first for competitive mode, it won't matter for the non-competitive as it directly updates the players anyways
        ArrayList<Player> sortedPlayers = new ArrayList<>(playerList); // local copy of playerList
        // some weird sorting lambda method from java; should sort all 5 players
        // place with p2 first in order to sort from largest to smallest
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getBoard().values().stream().mapToInt(List::size).sum(), 
                           p1.getBoard().values().stream().mapToInt(List::size).sum()));

        // does all the rank sorting in this method. Im lwk the goat for this it might just be glorious
        rankSorting(sortedPlayers, isCompetitive, roundsPlayed, "NONE");
    }
    
    // sorts the players based on the # of eggs in specific habitat / 3 (integer divison) added with same operation on other habitat
    public void setOf3(ArrayList<Player> playerList, int roundsPlayed, boolean isCompetitive)
    {
        // Need to have players sorted first for competitive mode, it won't matter for the non-competitive as it directly updates the players anyways
        ArrayList<Player> sortedPlayers = new ArrayList<>(playerList); // local copy of playerList
        // some weird sorting lambda method from java; should sort all 5 players
        // place with p2 first in order to sort from largest to smallest
        sortedPlayers.sort((p1, p2) -> {
            int totalP1 = java.util.stream.Stream.of("forest", "grassland", "wetland").mapToInt(h -> p1.getBoard().get(h).stream().mapToInt(BirdInstance::getEggStored).sum()/3).sum();
            int totalP2 = java.util.stream.Stream.of("forest", "grassland", "wetland").mapToInt(h -> p2.getBoard().get(h).stream().mapToInt(BirdInstance::getEggStored).sum()/3).sum();

            // place with p2 first in order to sort from largest to smallest
            return Integer.compare(totalP2, totalP1);
        });

        // does all the rank sorting in this method. Im lwk the goat for this it might just be glorious
        rankSorting(sortedPlayers, isCompetitive, roundsPlayed, "NONE");
    }

    // adds ranks to players and points
    public void rankSorting(ArrayList<Player> sortedPlayers, boolean isCompetitive, int roundsPlayed, String specificHabitatOrNest)
    {
        // competitive scoring based on rankings and an already sorted players list. uses a previous score reference to check ties
        if(isCompetitive)
        {
            // copyable from here on should be; ignore this i have now made it a generic method
            int rank = 1;         // current rank
            int previousScore = -1; // track previous player score
            int sameRankCount = 0; // starts at 0 in case for first player

            for(int i = 0; i < sortedPlayers.size(); i++)
            {
                Player p = sortedPlayers.get(i);
                int score;

                // gets the score based on the goal it is
                switch(this)
                {
                    // # Eggs in habitat
                    case EGGS_IN_FOREST: 
                    case EGGS_IN_GRASSLAND:
                    case EGGS_IN_WETLAND: score = p.getBoard().get(specificHabitatOrNest).stream().mapToInt(BirdInstance::getEggStored).sum(); break;
                    // # Birds in habitat
                    case BIRDS_IN_FOREST:
                    case BIRDS_IN_GRASSLAND:
                    case BIRDS_IN_WETLAND: score = p.getBoard().get(specificHabitatOrNest).size(); break;
                    // # nest birds with eggs
                    case BOWL_BIRDS_WITH_EGGS:
                    case CAVITY_BIRDS_WITH_EGGS:
                    case PLATFORM_BIRDS_WITH_EGGS:
                    case GROUND_BIRDS_WITH_EGGS: 
                    score = (int) p.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(specificHabitatOrNest) && b.getEggStored() > 0).count(); break;
                    // # of eggs in nest types
                    case EGGS_IN_BOWL:
                    case EGGS_IN_CAVITY:
                    case EGGS_IN_PLATFORM:
                    case EGGS_IN_GROUND: 
                    score = p.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(specificHabitatOrNest)).mapToInt(BirdInstance::getEggStored).sum(); break;
                    // misc
                    case TOTAL_BIRDS: score = p.getBoard().values().stream().mapToInt(List::size).sum(); break;
                    case SETS_OF_3_EGGS_IN_EACH_HABITAT: 
                    score = java.util.stream.Stream.of("forest", "grassland", "wetland").mapToInt(h -> p.getBoard().get(h).stream().mapToInt(BirdInstance::getEggStored).sum()/3).sum(); break;
                    default: System.out.println("ERROR: goal could not be identified in competitive scoring and we're screwed"); score = 0;
                }

                // if score same, increase the rank count
                if (score == previousScore)
                    sameRankCount++;
                else { // no tie means rankings will jump by the tied rank number and reset ties
                    rank += sameRankCount;
                    sameRankCount = 1;
                }

                previousScore = score; // updates previous score

                p.setGoalRankings(Math.min(rank, 4)); // updates player's rank for UI to use on goal board. If ranking is greater than 4, goes back to 4
                if(rank == 1) p.addPoints(4 + roundsPlayed); // directly adds the points
                else if(rank == 2) p.addPoints(1 + roundsPlayed);
                else if(rank == 3) p.addPoints(0 + roundsPlayed);
            }
        }
        else
        {
            for(Player p: sortedPlayers)
            {
                // gets the score based on the goal it is
                int score;
                switch(this)
                {
                    // # Eggs in habitat
                    case EGGS_IN_FOREST: 
                    case EGGS_IN_GRASSLAND:
                    case EGGS_IN_WETLAND: score = p.getBoard().get(specificHabitatOrNest).stream().mapToInt(BirdInstance::getEggStored).sum(); break;
                    // # Birds in habitat
                    case BIRDS_IN_FOREST:
                    case BIRDS_IN_GRASSLAND:
                    case BIRDS_IN_WETLAND: score = p.getBoard().get(specificHabitatOrNest).size(); break;
                    // # nest birds with eggs
                    case BOWL_BIRDS_WITH_EGGS:
                    case CAVITY_BIRDS_WITH_EGGS:
                    case PLATFORM_BIRDS_WITH_EGGS:
                    case GROUND_BIRDS_WITH_EGGS:
                    score = (int) p.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(specificHabitatOrNest) && b.getEggStored() > 0).count(); break;
                    // # of eggs in nest types
                    case EGGS_IN_BOWL:
                    case EGGS_IN_CAVITY:
                    case EGGS_IN_PLATFORM:
                    case EGGS_IN_GROUND:
                    score = p.getBoard().values().stream().flatMap(list -> list.stream()).filter(b -> b.getNest().equalsIgnoreCase(specificHabitatOrNest)).mapToInt(BirdInstance::getEggStored).sum(); break;
                    // misc
                    case TOTAL_BIRDS: score = p.getBoard().values().stream().mapToInt(List::size).sum(); break;
                    case SETS_OF_3_EGGS_IN_EACH_HABITAT: 
                    score = java.util.stream.Stream.of("forest", "grassland", "wetland").mapToInt(h -> p.getBoard().get(h).stream().mapToInt(BirdInstance::getEggStored).sum()/3).sum(); break;
                    default: System.out.println("ERROR: goal could not be identified in non-competitive scoring and we're screwed"); score = 0;
                }

                // copyable from here on should be ; ignore made into generic method
                p.setGoalRankings(Math.min(5, score));
                p.addPoints(Math.min(5, score));
            }
        }
    }
}
