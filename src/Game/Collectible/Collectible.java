package Game.Collectible;
import java.awt.*;
import Game.Entities.*;

public abstract class Collectible {

    // Instance variables
    protected Rectangle bounds;
    int height, width;

    // Constructors
    public Collectible(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.width = width;
        this.height = height;
    }

    // Getters
    public Rectangle getBounds() {return bounds;}
    public int getX() {return bounds.x;}
    public int getY() {return bounds.y;}
    public int getHeight() {return height;}
    public int getWidth() {return width;}


    // Methods
    public abstract void applyEffect(Player character);
    
}
