package Game.Abilities;

import java.awt.*;

public abstract class Ability {

    // Instance variables
    Rectangle bounds;
    int width, height;

    // Constructor

    public Ability(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
        this.width = width;
        this.height = height;
    }

    // Getters

    public Rectangle getBounds() {return bounds;}
    public int getHeight() {return height;}
    public int getWidth() {return width;}
    public int getX() {return bounds.x;}
    public int getY() {return bounds.y;}

    // Setters

    public void setBounds(int x, int y) {
        bounds.x = x;
        bounds.y = y;
    }

    // Methods

    public abstract void apply(Runnable runnable);


}