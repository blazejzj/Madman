package Game.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class MainMenuPanel extends JPanel {
    private BufferedImage background;

    public MainMenuPanel(ActionListener startGameListener) {
        // buttons dims
        int buttonWidth = 150;
        int buttonHeight = 50;

        // Background for the whole main menu
        drawMainMenuBackground();

        // Start button
        startButton(buttonWidth, buttonHeight, startGameListener);

        // Continue button
        continueButton(buttonWidth, buttonHeight);

        // Option button
        optionButton(buttonWidth, buttonHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    private void optionButton(int buttonWidth, int buttonHeight){
        JButton optionButton = new JButton("Options");
        int optionButtonX = 594;   // horizontal from left
        int optionButtonY = 746;   // vertical from top
        optionButton.setBounds(optionButtonX, optionButtonY, buttonWidth, buttonHeight);
        optionButton.setOpaque(false);
        optionButton.setContentAreaFilled(false);
        optionButton.setBorderPainted(false);
        optionButton.setFocusPainted(false);
        optionButton.setFont(new Font("Arial", Font.BOLD, 15));
        optionButton.setForeground(Color.WHITE);
        // optionButton.addActionListener(startGameListener);
        add(optionButton);
    }
    private void continueButton(int buttonWidth, int buttonHeight) {
        JButton continueButton = new JButton("Continue");
        int continueButtonX = 435;   // horizontal from left
        int continueButtonY = 692;   // vertical from top
        continueButton.setBounds(continueButtonX, continueButtonY, buttonWidth, buttonHeight);
        continueButton.setOpaque(false);
        continueButton.setContentAreaFilled(false);
        continueButton.setBorderPainted(false);
        continueButton.setFocusPainted(false);
        continueButton.setFont(new Font("Arial", Font.BOLD, 15));
        continueButton.setForeground(Color.WHITE);
        //continueButton.addActionListener(startGameListener);
        add(continueButton);
    }
    private void startButton(int buttonWidth, int buttonHeight, ActionListener startGameListener) {
        JButton startButton = new JButton("Start New Game");
        int startButtonX = 290;   // horizontal from left
        int startButtonY = 745;   // vertical from top

        startButton.setBounds(startButtonX, startButtonY, buttonWidth, buttonHeight);
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setFont(new Font("Arial", Font.BOLD, 15));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(startGameListener);
        add(startButton);
    }

    private void drawMainMenuBackground() {
        try {
            background = ImageIO.read(new File("src/mapLayout/mainMenuBackground3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(null); // for absolute positioning
    }
}
