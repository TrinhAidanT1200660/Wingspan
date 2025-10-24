import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Player
{
    //VARIABLES:
    
    //stores the amount of points each player has
    private int points;
    //stores the birds in each habitat
    private TreeMap<String, ArrayList<Bird>> board;
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
    public Player(int points, TreeMap<String, ArrayList<Bird>> board, ArrayList<BonusCard> bonusHand, ArrayList<Bird> birdHand, HashMap<String, Integer> food, int actionCubes)
    {
        this.points = points;
        this.board = board;
        this.bonusHand = bonusHand;
        this.birdHand = birdHand;
        this.food = food;
        this.actionCubes = actionCubes;
    }

    //RETURN METHODS:
    
    //returns the amount of points the player has as an int
    public int getPoints() {return points;}
    
    //returns the TreeMap the player has that represents their board
    public TreeMap<String, ArrayList<Bird>> getBoard() {return board;} 
    
    //returns the arraylist that holds the bonus cards a player has
    public ArrayList<BonusCard> getBonusHand() {return bonusHand;}
    
    //eturns the arraylist that holds the bird cards a player has
    public ArrayList<Bird> getBirdHand() {return birdHand;}
    
    //returns the map that holds the food a player has
    public HashMap<String, Integer> getFood() {return food;}
    
    //returns the amount of actionCubes a player has
    public int getActionCubes() {return actionCubes;}
    
    //returns the ranking of the player of the round
    public int getGoalRankings(int round) {return goalRankings.get(round);}
}
