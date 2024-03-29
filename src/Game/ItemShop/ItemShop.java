package Game.ItemShop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ItemShop extends JPanel {
    private ArrayList<ShopItem> items = new ArrayList<>();
    private int playerPoints;

    public ItemShop() {
        setLayout(new GridLayout(0, 3, 10, 10));
        setVisible(false);
    }

    public void setPlayerPoints(int points) {
        this.playerPoints = points;
    }

    public void addItem(ShopItem item) {
        items.add(item);
        JButton button = new JButton(item.getIcon());
        button.addActionListener(e -> {
            if (playerPoints >= item.getCost()) {
                item.purchase();
                playerPoints -= item.getCost();
                JOptionPane.showMessageDialog(this, item.getName() + " purchased!");
                // Update player points or something
            } else {
                JOptionPane.showMessageDialog(this, "Not enough points!");
            }
        });

        button.setText("<html>" + item.getName() + "<br/>" + item.getCost() + " Points</html>");
        add(button);
    }

    public void displayItems() {
        removeAll();
        items.forEach(this::addItem);
        revalidate();
        repaint();
    }

    public void toggleVisibility() {
        setVisible(!isVisible());
    }
}

