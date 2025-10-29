
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
    //stores the goal ranking for each round
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

    //MUTATOR METHOD:
    //adds the specificed amount of points
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
    public void setGoalRankings(int round, int ranking) {
        goalRankings.set(round, ranking);
    }

}
