package Game.Abilities;
import java.awt.*;

public abstract class Ability {
    Rectangle bounds;
    int width, height;
    public Ability(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
        this.width = width;
        this.height = height;
    }

    // Getters
    public Rectangle getBounds() {return bounds;}
    public int getHeight() {return height;}
    public int getWidth() {return width;}

    // Setters
    public void setBounds(int x, int y) {
        bounds.x = x;
        bounds.y = y;
    }

    public abstract void apply(Runnable runnable);

    public int getX() {
        return bounds.x;
    }
    public int getY() {
        return bounds.y;
    }
}