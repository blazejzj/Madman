package Game.Collectible;
import java.awt.Rectangle;

import Game.Entities.*;

public class HealthPotion extends Collectible {

    // Instance variables
    private int healthBoost;

    // Constructors
    public HealthPotion(int x, int y, int width, int height, int healthBoost) {
        super(x, y, width, height);
        this.healthBoost = healthBoost;
    }

    // Methods
    @Override
    public void applyEffect(Player character) {
        character.heal(healthBoost);
        System.out.println("Picked up potion! Health increased by " + healthBoost);
    }

    // Getters
    @Override
    public Rectangle getBounds() {return bounds;}


}
