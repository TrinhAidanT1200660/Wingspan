
import static java.lang.System.out;
import java.util.PriorityQueue;


public class WingspanRunner {

    // adds the graphics window
    public static void main(String[] args) {
PriorityQueue<Integer> pq = new PriorityQueue<>();
int[] list = {31,17,32,19,45,41,10,3,35};
for(int x:list)
    pq.add(x);

out.println(pq);
        //WingspanFrame frame = new WingspanFrame("Wingspan");
    }
}
