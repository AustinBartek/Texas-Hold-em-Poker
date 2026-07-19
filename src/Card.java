import javax.swing.*;
import java.awt.Image;
import java.io.*;

public class Card {
    String value;
    char suit;
    int realValue;

    public Card(String value, char suit) {
        this.value = value;
        this.suit = suit;
        switch (this.value) {
            case "A":
                this.realValue = 13;
                break;
            case "K":
                this.realValue = 12;
                break;
            case "Q":
                this.realValue = 11;
                break;
            case "J":
                this.realValue = 10;
                break;
            default:
                this.realValue = Integer.parseInt(this.value) - 1;
                break;
        }
    }

    public Image getImage() throws IOException {
        return Window.CARD_IMAGES.get(this.value + this.suit);
    }
}