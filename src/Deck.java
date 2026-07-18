import java.util.*;

public class Deck {
    ArrayList<Card> contents;
    static String[] valuesArr = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    static char[] suitsArr = {'S', 'C', 'D', 'H'};
    static Random random = new Random();

    public Deck(Boolean shuffled) {
        this.contents = new ArrayList<Card>(52);
        for (int i = 0; i < valuesArr.length; i++) {
            for (int j = 0; j < suitsArr.length; j++) {
                this.contents.add(new Card(valuesArr[i], suitsArr[j]));
            }
        }
        if (shuffled) {
            Collections.shuffle(this.contents);
        }
    }
}
