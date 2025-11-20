import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class Player {
    //VARIABLES:

    //stores the amount of points each player has
    private int points;
    //stores the birds in each habitat
    private TreeMap<String, ArrayList<BirdInstance>> board;
    //stores the bonus cards of each player
    private ArrayList<BonusCard> bonusHand;
    //stores the bird hand of each player
    private ArrayList<Bird> birdHand;
    //stores the food amount of each type
    private HashMap<String, Integer> food;
    //stores the number of action cubes each player has
    private int actionCubes;
    //stores the goal ranking for each round; flipped for each mode
    // competitive goes from 1-4 (best to worst) | non-competitive goes from (5-0) (best to worst)
    // keep in mind when having UI place action cubes on board based on rankings
    private ArrayList<Integer> goalRankings;

    //CONSTRUCTOR:
    public Player() {
        this.points = 0;
        this.board = new TreeMap<>();
        board.put("forest", new ArrayList<>());
        board.put("grassland", new ArrayList<>());
        board.put("wetland", new ArrayList<>());
        this.bonusHand = new ArrayList<>();
        this.birdHand = new ArrayList<>();
        this.food = new HashMap<>();
        food.put("berry", 0);
        food.put("fish", 0);
        food.put("rat", 0);
        food.put("seed", 0);
        food.put("worm", 0);
        this.actionCubes = 8;
        this.goalRankings = new ArrayList<>();
    }

    //RETURN METHODS:
    //returns the amount of points the player has as an int
    public int getPoints() {
        return points;
    }

    //returns the TreeMap the player has that represents their board
    public TreeMap<String, ArrayList<BirdInstance>> getBoard() {
        return board;
    }

    //returns the arraylist that holds the bonus cards a player has
    public ArrayList<BonusCard> getBonusHand() {
        return bonusHand;
    }

    //eturns the arraylist that holds the bird cards a player has
    public ArrayList<Bird> getBirdHand() {
        return birdHand;
    }

    //returns the map that holds the food a player has
    public HashMap<String, Integer> getFood() {
        return food;
    }

    //returns the amount of actionCubes a player has
    public int getActionCubes() {
        return actionCubes;
    }

    //returns the ranking of the player of the round
    public int getGoalRankings(int round) {
        return goalRankings.get(round);
    }

    //removes the specified amount of food and the amount
    public boolean removeFood(String foodType, int amount) {
        int current = food.getOrDefault(foodType, 0);
        if(current >= amount && current != 0) {
            food.put(foodType, current - amount);
            return true;
        }
        return false;
    }

    //adds the specified bird to the board if the player has enough food and the bird is in their hand
    //if it has any food type, UI will ask player to choose which food to use
    //returns true if successful, false otherwise
    public boolean addBirdToBoard(Bird bird) {
        if(!this.getBirdHand().contains(bird)) return false; // checks if the player acc has the bird; idk how this goes off
        if(!this.hasEnoughFood(bird)) return false; // checks if the player has enough food

        // removes the food from the player's food supply
        if(bird.getFoodRequired().contains("and")) {
            removeAndFoodToAddBird(bird); // this method removes all food but the any
            if(bird.getFoodRequired().contains("any")) {
                //UI will ask which food to use

            }
        }
        else {
            //UI will ask which food to use; or foods should only have 1 food to remove so directly do it
            String food = ""; // UI METHOD HERE that returns the food type
            this.removeFood(food, 1);
        }

        String habitat = "";
        if(bird.getHabitat().length > 1) {
            //UI will ask which habitat to place the bird in
            habitat = bird.getHabitat()[0]; // TEMPORARY SETTING TO FIRST HABITAT
        }
        else {
            habitat = bird.getHabitat()[0];
           
        }
        BirdInstance birdInstance = new BirdInstance(bird); // new bird instance
        this.getBoard().get(habitat).add(birdInstance); // adds to board
        this.getBirdHand().remove(bird); // removes from hand
        return true;
    }

    //checks if the player has enough food to play the specified bird
    public boolean hasEnoughFood(Bird bird) {
        String foodRequired = bird.getFoodRequired();
        String[] split = foodRequired.split(" ");
        String type = split[0];
        boolean result = type.equals("and");
        int sumOfAny = 0;
        int sumOfFoodRequired = 0;
        for (int v : food.values()) sumOfAny += v;
        for (int i = 1; i < split.length; i++) {
            sumOfFoodRequired += Integer.parseInt(split[i].substring(0, 1));
        }
        if (sumOfFoodRequired < sumOfAny) return false;
        for (int i = 1; i < split.length; i++) {
            int amount = Integer.parseInt(split[i].substring(0, 1));
            String foodType = split[i].substring(1);
            if (foodType.equals("any")) {
                if (sumOfAny > amount) result = true;
            }
            if (type.contains("and")) {
                if (!foodType.equals("any") && food.get(foodType) < amount) result = false;
                if (!result) break;
            } else if (type.equals("or")) {
                if (!foodType.equals("any") && food.get(foodType) > amount) result = true;
                if (result) break;
            }
        }
        return result;
    }

    //MUTATOR METHOD:
    //adds the specified amount of points
    public void addPoints(int points) {
        this.points += points;
    }

    //adds the specified Bonus card to the bonusHand
    public void addBonusHand(BonusCard card) {
        bonusHand.add(card);
    }

    //adds the specified BirdCard card to the birdHand
    public void addBirdHand(Bird card) {
        birdHand.add(card);
    }

    //adds the specified foods and the amount to the food map
    public void addFood(String foodType, int amount) {
        int current = food.getOrDefault(foodType, 0);
        food.put(foodType, current + amount);
    }

    //decreases the amount of actionCubes a player has by 1
    public void decreaseActionCubes() {
        actionCubes -= 1;
    }

    //sets the amount of actionCubes a player has to the specified number
    public void setActionCubes(int amount) {
        actionCubes = amount;
    }

    //sets the ranking of the player of the round
    public void setGoalRankings(int ranking) {
        goalRankings.add(ranking);
    }

    public void removeAndFoodToAddBird(Bird bird) {
        String foodRequired = bird.getFoodRequired();
        String[] split = foodRequired.split(" ");
        for (int i = 1; i < split.length; i++) {
            int amount = Integer.parseInt(split[i].substring(0, 1));
            String foodType = split[i].substring(1);
            if (!foodType.equals("any")) {
                removeFood(foodType, amount);
            }
        }
    }
}
