package Game.Entities;

import Game.Abilities.*;
import java.util.ArrayList;

public class Player extends Entity {
    
    // Instance variables
    public ArrayList<Ability> abilities = new ArrayList<>();
    private final int MAX_ABILITIES = 3;

    // Constructor
    
    public Player(int x, int y, int width, int height, int health, int speed) {
        super(x, y, width, height, health, speed);
    }

    // Methods
    public void pickUpAbility(Ability ability) {
        if (abilities.size() < MAX_ABILITIES) {
            abilities.add(ability);
        }
    }

    @Override
    public void useAbility(int index, Runnable callback) {
        if (index < abilities.size()) {
            abilities.get(index).apply(callback);
            abilities.remove(index);
        }
    }

    public void heal(int healingPoints) {
        this.health += healingPoints;
        System.out.println("New health: " + this.health);
    }
    

}
