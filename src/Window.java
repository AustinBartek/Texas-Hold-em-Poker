import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Window {
    JFrame window = new JFrame("Texas Hold'em Poker!");
    JPanel gamePanel;
    JPanel betPanel = new JPanel();
    JButton foldButton = new JButton("Fold");
    JButton betButton = new JButton("Place Bet");
    JTextField betField = new JTextField(4);
    JButton newGameButton = new JButton("New Game");
    ArrayList<String> message = new ArrayList<String>();
    Boolean gameOver;
    public static BufferedImage BACK_IMAGE;
    public static HashMap<String, BufferedImage> CARD_IMAGES;

    public static void init() {
        CARD_IMAGES = new HashMap<>();
        for (String value : new String[] { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" }) {
            for (String suit : new String[] { "C", "D", "H", "S" }) {
                CARD_IMAGES.put(value + suit, loadImage("res/Cards/" + value + suit + ".png"));
            }
        }
        BACK_IMAGE = loadImage("res/Cards/BACK.png");
    }

    public Window() {
        message.add("Welcome!");
        gameOver = false;

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setIconImage(new ImageIcon("res/Cards/AD.png").getImage());
        window.setResizable(false);
        window.setSize(1000, 800);
        window.setLocationRelativeTo(null);

        // Draw all the cards and stuff...
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw all the hands and their cards.
                for (int i = 0; i < Poker.playerHand.size(); i++) {
                    int xPos = i * 160 + 20;
                    try {
                        g.drawImage(Poker.playerHand.get(i).getImage(), xPos, 510, 140, 203, null);
                    } catch (IOException e) {
                    }
                }
                for (int i = 0; i < Poker.cpuHand.size(); i++) {
                    int xPos = i * 160 + 20;
                    Image img = new ImageIcon().getImage();
                    try {
                        img = (gameOver) ? Poker.cpuHand.get(i).getImage()
                                : BACK_IMAGE;
                    } catch (IOException e) {
                    }
                    g.drawImage(img, xPos, 10, 140, 203, null);
                }
                for (int i = 0; i < Poker.pool.size(); i++) {
                    int xPos = i * 160 + 20;
                    Image img = new ImageIcon().getImage();
                    try {
                        img = (i > Poker.cardsRevealed - 1) ? BACK_IMAGE
                                : Poker.pool.get(i).getImage();
                    } catch (IOException e) {
                    }
                    g.drawImage(img, xPos, 260, 140, 203, null);
                }

                // textbox
                g.setColor(new Color(200, 200, 200));
                g.fill3DRect(350, 505, 470, 200, true);
                // betboxes
                g.setColor(new Color(200, 150, 150));
                g.fill3DRect(850, 15, 100, 100, true);
                g.fill3DRect(850, 600, 100, 100, true);
                // strings
                g.setFont(new Font("Verdana", Font.PLAIN, 15));
                g.setColor(Color.black);
                for (int i = 0; i < message.size(); i++) {
                    g.drawString(message.get(i), 365, i * 20 + 530);
                }
                g.setFont(new Font("Bright", Font.PLAIN, 60));
                g.drawString(Integer.toString(Poker.cpuBet), 860, 85);
                g.drawString(Integer.toString(Poker.playerBet), 860, 670);
            }
        };

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(0, 150, 50));
        window.add(gamePanel);

        foldButton.setFocusable(false);
        betButton.setFocusable(false);
        newGameButton.setFocusable(false);
        betPanel.add(foldButton);
        betPanel.add(betButton);
        betPanel.add(betField);
        betPanel.add(newGameButton);
        window.add(betPanel, BorderLayout.SOUTH);

        window.setVisible(true);
    }

    public void updateMessage(String text) {
        message.add(text);
        gamePanel.repaint();
    }

    public static BufferedImage loadImage(String src) {
        try {
            return ImageIO.read(Window.class.getResourceAsStream(src));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}