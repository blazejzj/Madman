package Game.Entities;

import Game.Abilities.*;
import java.util.ArrayList;

public class Player extends Entity {
    public ArrayList<Ability> abilities = new ArrayList<>();
    public double speed = 3.0;
    public int health = 3;
    private final int MAX_ABILITIES = 3;

    public Player(int x, int y, int width, int height) {
        super(x, y, width, height, 3, 3.0);
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

}
