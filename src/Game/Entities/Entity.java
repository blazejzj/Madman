package Game.Entities;
import java.awt.Rectangle;

public abstract class Entity extends Rectangle {
    public int health;
    public double speed;

    public Entity(int x, int y, int width, int height, int health, double speed) {
        super(x, y, width, height);
        this.health = health;
        this.speed = speed;
    }

    // Getters
    public int getHealth() {
        return health;
    }
    public double getSpeed() {
        return speed;
    }

    // Setters
    public void setHealth(int health) {
        this.health = health;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // Methods
    public abstract void useAbility(int index, Runnable runnable);
}
