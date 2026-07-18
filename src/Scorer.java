import java.util.*;

public class Scorer {
    int numPairs;
    int numThrees;
    Boolean four;
    Boolean flush;
    Boolean straight;
    Boolean royalFlush;
    ArrayList<Card> playerHand;
    ArrayList<Card> cpuHand;
    ArrayList<Card> pool;

    public Scorer(ArrayList<Card> playerHand, ArrayList<Card> cpuHand, ArrayList<Card> pool) {
        this.playerHand = playerHand;
        this.cpuHand = cpuHand;
        this.pool = pool;
        resetValues();
    }

    public void resetValues() {
        this.numPairs = 0;
        this.numThrees = 0;
        this.four = false;
        this.flush = false;
        this.straight = false;
        this.royalFlush = false;
    }

    public int bestOut() {
        if (this.royalFlush) {
            return 10;
        }
        if (this.flush && this.straight) {
            return 9;
        } else if (this.flush) {
            return 6;
        } else if (this.straight) {
            return 5;
        }
        if (this.four) {
            return 8;
        }
        if (this.numThrees == 1 && this.numPairs > 0) {
            return 7;
        } else if (this.numThrees > 0) {
            return 4;
        } else if (this.numPairs > 1) {
            return 3;
        } else if (this.numPairs == 1) {
            return 2;
        }
        return 1;
    }

    // -1 = loss, 0 = tie, 1 = win (for player). Win condition stuff.
    public int compareHands() {
        ArrayList<Integer> playerValue = getMaxCombo(this.playerHand);
        ArrayList<Integer> cpuValue = getMaxCombo(this.cpuHand);

        int minSize = Math.min(playerValue.size(), cpuValue.size());
        for (int i = 0; i < minSize; i++) {
            if (playerValue.get(i) > cpuValue.get(i)) {
                return 1;
            } else if (playerValue.get(i) < cpuValue.get(i)) {
                return -1;
            }
        }
        return 0;
    }

    // number and extremely tiresome logic work... >:D
    public ArrayList<Integer> getMaxCombo(ArrayList<Card> cards) {
        resetValues();

        // first value represents overall hand level, second represents the card value
        // with that level, third is low of the level, and fourth is other high card.
        ArrayList<Integer> returnArr = new ArrayList<Integer>(7);

        ArrayList<Card> fullCards = new ArrayList<Card>(7);
        for (Card card : cards) {
            fullCards.add(card);
        }
        for (Card card : this.pool) {
            fullCards.add(card);
        }

        ArrayList<Integer> realValues = new ArrayList<Integer>(7);
        // sort real values of cards from lowest to highest (nice, nice).
        for (Card card : fullCards) {
            realValues.add(card.realValue);
        }
        Collections.sort(realValues);

        // check for flushes (if you feel like it).
        int[] flushCheck = { 0, 0, 0, 0 };
        char[] suitCheck = { 'S', 'H', 'C', 'D' };
        char flushSuit = 'S';
        for (Card card : fullCards) {
            for (int i = 0; i < suitCheck.length; i++) {
                if (card.suit == suitCheck[i]) {
                    flushCheck[i]++;
                }
            }
        }
        for (int i = 0; i < flushCheck.length; i++) {
            if (flushCheck[i] > 4) {
                this.flush = true;
                flushSuit = suitCheck[i];
                break;
            }
        }

        // check for straights (with Aces, I think!).
        ArrayList<Integer> straightValues = new ArrayList<Integer>(8);
        int highestStraight = 0;
        for (Card card : fullCards) {
            if (((card.suit == flushSuit && this.flush) || !this.flush) && !straightValues.contains(card.realValue)) {
                straightValues.add(card.realValue);
                if (card.realValue == 13) {
                    straightValues.add(0);
                }
            }
        }
        Collections.sort(straightValues);
        for (int i = 0; i < straightValues.size() - 4; i++) {
            Boolean isStraight = true;
            for (int j = 1; j < 5; j++) {
                if (straightValues.get(i) + j != straightValues.get(i + j)) {
                    isStraight = false;
                }
            }
            this.straight = (this.straight) ? true : isStraight;
            highestStraight = Math.max(highestStraight, (isStraight) ? straightValues.get(i + 4) : 0);
        }
        if (this.straight && this.flush && highestStraight == 13) {
            this.royalFlush = true;
        }

        // check for fours, threes, and twos (pretty please).
        ArrayList<Integer> oneFreqs = new ArrayList<Integer>(), twoFreqs = new ArrayList<Integer>(),
                threeFreqs = new ArrayList<Integer>(), fourFreqs = new ArrayList<Integer>(),
                checkArr = new ArrayList<Integer>();
        int currentCount;
        int currentValue;
        int highestCount = 0;
        for (int num : realValues) {
            checkArr.add(num);
        }
        while (checkArr.size() > 0) {
            currentValue = checkArr.remove(0);
            currentCount = 1;
            while (checkArr.size() > 0 && checkArr.get(0) == currentValue) {
                currentCount++;
                checkArr.remove(0);
            }
            highestCount = Math.max(highestCount, currentCount);
            switch (currentCount) {
                case 1:
                    oneFreqs.add(currentValue);
                    break;
                case 2:
                    this.numPairs++;
                    twoFreqs.add(currentValue);
                    break;
                case 3:
                    this.numThrees++;
                    threeFreqs.add(currentValue);
                    break;
                case 4:
                    this.four = true;
                    fourFreqs.add(currentValue);
                    break;
            }
        }
        returnArr.add(this.bestOut());
        if (this.flush && this.bestOut() == 6) {
            ArrayList<Integer> cardsToAdd = new ArrayList<Integer>();
            for (Card card : fullCards) {
                if (card.suit == flushSuit && (!this.straight || card.realValue != 13)) {
                    cardsToAdd.add(card.realValue);
                }
            }
            Collections.sort(cardsToAdd, Collections.reverseOrder());
            for (int i = 0; i < 5; i++) {
                returnArr.add(cardsToAdd.get(i));
            }
        } else {
            if (this.straight) {
                returnArr.add(highestStraight);
            } else {
                //Beautiful and totally understandable switch statement!! :D
                switch (highestCount) {
                    case 1:
                        for (int i = 0; i < 5; i++) {
                            returnArr.add(oneFreqs.remove(oneFreqs.size() - 1));
                        }
                        break;
                    case 2:
                        returnArr.add(twoFreqs.remove(twoFreqs.size() - 1));
                        if (this.numPairs > 1) {
                            returnArr.add(twoFreqs.remove(twoFreqs.size() - 1));
                            if (this.numPairs > 2) {
                                returnArr.add(
                                        Math.max(twoFreqs.remove(twoFreqs.size() - 1),
                                                oneFreqs.remove(oneFreqs.size() - 1)));
                            } else {
                                returnArr.add(oneFreqs.remove(oneFreqs.size() - 1));
                            }
                        } else {
                            for (int i = 0; i < 3; i++) {
                                returnArr.add(oneFreqs.remove(oneFreqs.size() - 1));
                            }
                        }
                        break;
                    case 3:
                        returnArr.add(threeFreqs.remove(threeFreqs.size() - 1));
                        if (this.numPairs > 0 && this.numThrees > 1) {
                            returnArr.add(
                                    Math.max(twoFreqs.remove(twoFreqs.size() - 1),
                                            threeFreqs.remove(threeFreqs.size() - 1)));
                        } else if (this.numPairs > 0) {
                            returnArr.add(twoFreqs.remove(twoFreqs.size() - 1));
                        } else if (this.numThrees > 1) {
                            returnArr.add(threeFreqs.remove(threeFreqs.size() - 1));
                        } else {
                            for (int i = 0; i < 2; i++) {
                                returnArr.add(oneFreqs.remove(oneFreqs.size() - 1));
                            }
                        }
                        break;
                    case 4:
                        returnArr.add(fourFreqs.get(fourFreqs.size() - 1));
                        if (this.numThrees > 0) {
                            returnArr.add(threeFreqs.remove(threeFreqs.size() - 1));
                        } else if (this.numPairs > 0) {
                            returnArr.add(
                                    Math.max(twoFreqs.remove(twoFreqs.size() - 1),
                                            oneFreqs.remove(oneFreqs.size() - 1)));
                        } else {
                            returnArr.add(oneFreqs.remove(oneFreqs.size() - 1));
                        }
                }
            }
        }
        //All hail the glorious final reward, the return statement of truth! >8)
        return returnArr;
    }
}