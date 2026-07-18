import java.awt.event.*;
import java.util.*;

public class Poker {
    // player and cpu are each given 2 cards, the pool consists of 5 more cards to
    // be revealed after betting
    static Deck deck;
    static ArrayList<Card> playerHand, cpuHand, pool;
    static Scorer logic;
    static Boolean keepPlaying = true, gameOver, playerFold;
    static int playerBet, cpuBet, playerScore = 0, cpuScore = 0, playerChips, cpuChips, cardsRevealed, roundsPlayed = 0;
    static Random random = new Random();
    static Window window;

    public static void main(String[] args) throws Exception {
        newGame();
        window = new Window();
        initializeWindowEvents();
    }

    public static void initializeWindowEvents() {
        window.betButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!window.gameOver) {
                    getBet();
                }
            }
        });
        window.foldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!window.gameOver) {
                    playerFold = true;
                    endGame();
                }
            }
        });
        window.newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] randomMessages = { "Ahh... we start anew", "Here we go again!", "ANUTHAH WAAAAN!!",
                        "You want to keep losing, eh?", "Woohoo! Again! Again!",
                        "We've played " + roundsPlayed + " round" + ((roundsPlayed == 1) ? "" : "s") + ", dude!"};
                newGame();
                window.gameOver = false;
                window.message.clear();
                window.updateMessage(randomMessages[random.nextInt(randomMessages.length - 1)]);
                window.gamePanel.repaint();
            }
        });
        window.betField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!window.gameOver) {
                    getBet();
                }
            }
        });
    }

    public static void endGame() {
        cardsRevealed = 5;
        int outcome = logic.compareHands();
        int pot = playerBet + cpuBet;
        playerScore += (playerFold) ? 0 : (outcome == 1) ? cpuBet : (outcome == 0) ? Math.ceil((cpuBet + playerBet) / 2.0) : 0;
        cpuScore += (playerFold) ? playerBet : (outcome == -1) ? playerBet : (outcome == 0) ? Math.floor((playerBet + cpuBet) / 2.0) : 0;
        double playerAverage = Math.round(((double) (playerScore - cpuScore) * 100.0 / roundsPlayed)) / 100.0;
        window.message.clear();

        window.updateMessage("You " + ((playerFold) ? "would've " : "") + ((outcome == 1) ? "won" : (outcome == 0) ? "tied" : "lost") + " with a " + getHandType(logic.getMaxCombo(playerHand)) + ".");
        window.updateMessage("The CPU had a " + getHandType(logic.getMaxCombo(cpuHand)) + ".");
        window.updateMessage("You bet " + playerBet + " chips and the CPU bet " + cpuBet + " chips.");
        window.updateMessage("" + ((playerFold) ? "You folded and gained no chips." : ("You " + ((outcome == 1) ? "gained " + cpuBet + " chips." : (outcome == 0) ? "split " + pot + " chips with the CPU." : "gained no chips."))));
        window.updateMessage("Your total number of chips won is: " + playerScore);
        window.updateMessage("The CPU's total number of chips won is: " + cpuScore);
        window.updateMessage("You average " + playerAverage + " chip" + ((Math.abs(playerAverage) == 1) ? "" : "s") + " per round (" + roundsPlayed + " round" + ((roundsPlayed > 1) ? "s" : "") + " total).");
        
        window.gameOver = true;
        window.gamePanel.repaint();
    }

    public static void getCpuBet() {
        int handLevel = logic.getMaxCombo(cpuHand).get(0);
        if (cpuBet == 0) {
            cpuBet = (int) Math.ceil(handLevel / 2) + random.nextInt(2) + 1;
        } else {
            cpuBet = playerBet + random.nextInt(random.nextInt(4) + 1);
            cpuBet = (cpuBet > 10) ? 10 : cpuBet;
        }
    }

    public static void getBet() {
        int bet = 0;
        window.message.clear();
        String[] randomMessages = { "Hmm... Interesting", "Whaaaaaat??", "Ah, I think I've got it figured out >:)",
                "Oh carp... I'm gonna lose!!", "Nice bluff!", "Are you getting tired of this?", "*Sigh...",
                "Look over there! *Peeks at your cards", "Ooga booga :D" };
        try {
            bet = Integer.parseInt(window.betField.getText());
            if (bet < cpuBet) {
                window.updateMessage("Bet must be greater than or equal to CPU's bet.");
            } else if (bet > playerChips) {
                window.updateMessage("You cannot bet more than " + playerChips + " chips.");
            } else {
                playerBet = bet;
                getCpuBet();
                if (cardsRevealed == 5) {
                    endGame();
                } else {
                    cardsRevealed++;
                    window.updateMessage(randomMessages[random.nextInt(randomMessages.length - 1)]);
                }
            }
        } catch (NumberFormatException e) {
            window.updateMessage("Not a number!");
        }
    }

    public static void newGame() {
        deck = new Deck(true);
        gameOver = false;
        playerFold = false;
        cardsRevealed = 3;
        playerChips = 10;
        cpuChips = 10;
        playerBet = 0;
        cpuBet = 0;
        playerHand = new ArrayList<Card>(2);
        cpuHand = new ArrayList<Card>(2);
        pool = new ArrayList<Card>(5);
        for (int i = 0; i < 2; i++) {
            playerHand.add(deck.contents.remove(0));
            cpuHand.add(deck.contents.remove(0));
        }
        for (int i = 0; i < 5; i++) {
            pool.add(deck.contents.remove(0));
        }
        /*String[] expValues = { "2", "A", "A", "5", "7", "10", "8", "J", "A" };
        char[] expSuits = { 'S', 'D', 'H', 'C', 'C', 'C', 'C', 'D', 'C' };
        for (int i = 0; i < 2; i++) {
            playerHand.add(new Card(expValues[i], expSuits[i]));
            cpuHand.add(new Card(expValues[i + 2], expSuits[i + 2]));
        }
        for (int i = 4; i < 9; i++) {
            pool.add(new Card(expValues[i], expSuits[i]));
        }*/
        logic = new Scorer(playerHand, cpuHand, pool);
        getCpuBet();
        roundsPlayed++;
    }

    public static String getHandType(ArrayList<Integer> score) {
        String firstS = realToString(score.get(1), false);
        String firstP = realToString(score.get(1), true);
        String secondP = "";
        if (score.size() > 2) {
            secondP = realToString(score.get(2), true);
        }
        String[] messages = { "High Card of: " + firstS,
                "Pair of: " + firstP,
                "Two Pair of High: " + firstP + " and Low: " + secondP,
                "Three of a Kind of: " + firstP,
                "Straight of High: " + firstS,
                "Flush of High: " + firstS,
                "Full House of Triple: " + firstP + " and Double: " + secondP,
                "Four of a Kind of: " + firstP,
                "Straight Flush of High: " + firstS,
                "Royal Flush"
        };
        return messages[score.get(0) - 1];
    }

    public static String realToString(int realValue, Boolean plural) {
        String[] numberStrings = { "Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "Jack", "Queen", "King", "Ace" };
        return (plural) ? numberStrings[realValue] + ((realValue == 5) ? "es" : "s") : numberStrings[realValue];
    }
}