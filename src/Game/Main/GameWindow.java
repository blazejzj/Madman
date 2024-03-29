package Game.Main;

import Game.MainMenu.MainMenuPanel;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    // Instance variables

    private final GamePanel gamePanel;

    // Constructor

    public GameWindow() {
        pack();
        Insets insets = getInsets();
        int drawableWidth = 1032 - (insets.left + insets.right);
        int drawableHeight = 875 - (insets.top + insets.bottom);
        this.setSize(drawableWidth, drawableHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Madman");
        this.setResizable(false);

        gamePanel = new GamePanel();
        MainMenuPanel mainMenuPanel = new MainMenuPanel(e -> startGame());
        
        this.setContentPane(mainMenuPanel);
        this.setVisible(true);
    }

    // Methods
    
    private void startGame() {
        this.setContentPane(gamePanel);
        this.revalidate();
        this.repaint();
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
    }

    public static void main(String[] args) {
        new GameWindow();
    }
    
}
