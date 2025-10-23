import java.util.*;

public class Player
{
    //VARIABLES:
    
    //stores the amount of points each player has
    private int points;
    //stores the birds in each habitat
    private TreeMap<String, ArrayList<Bird>> board;
    //stores the eggs on each bird, replicating the bird’s location
    private TreeMap<String, ArrayList<Integer>> eggs;
    //stores the bonus cards of each player
    private ArrayList<Bonus> bonusHand;
    //stores the bird hand of each player
    private ArrayList<Bird> birdHand;
    //stores the food amount of each type
    private HashMap<String, Integer> food;
    //stores the total amount of tucked cards each player has
    private int tuckedCards;
    //stores the number of action cubes each player has
    private int actionCubes;
    //stores the goal ranking for each round
    private ArrayList<Integer> goalRankings;

    //CONSTRUCTOR:
    public Player(int points, TreeMap<String, ArrayList<Bird>> board, TreeMap<String, ArrayList<Integer>> eggs, ArrayList<Bonus> bonusHand, ArrayList<Bird> birdHand, HashMap<String, Integer> food, int tuckedCards, int actionCubes)
    {
        this.points = points;
        this.board = board;
        this.eggs = eggs;
        this.bonusHand = bonusHand;
        this.birdHand = birdHand;
        this.food = food;
        this.tuckedCards = tuckedCards;
        this.actionCubes = actionCubes;
    }

    //RETURN METHODS:
    
    //returns the amount of points the player has as an int
    public int getPoints() {return points;}
    
    //returns the TreeMap the player has that represents their board
    public TreeMap<String, ArrayList<Bird>> getBoard() {return board;} 
    
    //returns the TreeMap the player has that represents the eggs on the birds
    public TreeMap<String, ArrayList<Integer>> getEggs() {return eggs;}
    
    //returns the arraylist that holds the bonus cards a player has
    public ArrayList<Bonus> getBonusHand() {return bonusHand;}
    
    //eturns the arraylist that holds the bird cards a player has
    public ArrayList<Bird> getBirdHand() {return birdHand;}
    
    //returns the map that holds the food a player has
    public HashMap<String, Integer> getFood() {return food;}
    
    //returns the amount of tucked cards
    public int getTuckedCards() {return tuckedCards;}
    
    //returns the amount of actionCubes a player has
    public int getActionCubes() {return actionCubes;}
    
    //returns the ranking of the player of the round
    public ArrayList<Integer> getGoalRankings(int round) {return goalRankings.get(round);}
}
