package Game.Projectiles;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
public class Projectile {
    public Rectangle bounds;
    public int speed = 10;
    private String direction;

    public  Projectile(int x, int y, String direction) {
        this.direction = direction;
        bounds = new Rectangle(x, y, 40, 40);
    }
    public void move() {
        switch (direction) {
            case "UP":
                bounds.y -= speed;
                break;
            case "DOWN":
                bounds.y += speed;
                break;
            case "LEFT":
                bounds.x -= speed;
                break;
            case "RIGHT":
                bounds.x += speed;
                break;
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }
    public void draw(Graphics g, BufferedImage image) {
        if (image != null) {
            g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
        }
        else {
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}
