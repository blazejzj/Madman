package Game.Entities;

public class Enemy extends Entity {

    // Instance variables
    
    public double originalSpeed;

    // Constructor

    public Enemy(int x, int y, int width, int height, int health, double speed) {
        super(x, y, width, height, health, speed);
        this.originalSpeed = speed;
    }

    // Methods

    public void moveTowards(int targetX, int targetY) {
        // Calculate direction towards the target
        double centerX = this.x + this.width / 2.0;
        double centerY = this.y + this.height / 2.0;
        double dx = targetX - centerX;
        double dy = targetY - centerY;
        
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return; // Prevent division by zero
    
        // Normalize the movement vector
        dx /= distance;
        dy /= distance;
    
        // Apply normalized speed
        double moveX = dx * speed;
        double moveY = dy * speed;
    
        // Correcting the movement speed for diagonal movement
        // Ensure the entity moves at the same speed in all directions
        double adjustmentFactor = Math.sqrt((moveX * moveX) + (moveY * moveY)) / speed;
        if (adjustmentFactor > 0) {
            moveX /= adjustmentFactor;
            moveY /= adjustmentFactor;
        }
    
        // Update position, rounding to handle sub-pixel movement
        this.setLocation((int)(this.x + (moveX > 0 ? Math.ceil(moveX) : Math.floor(moveX))),
                         (int)(this.y + (moveY > 0 ? Math.ceil(moveY) : Math.floor(moveY))));
    }

    public void avoidCollision(Enemy other) {
        // Check for collision
        if (this.intersects(other)) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;

            // Normalize direction vector
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length == 0) length = 1; // Avoid division by zero 

            dx /= length;
            dy /= length;

            // Calculate adjustment distance
            double adjustmentSpeed = speed * 0.1;
            double adjustX = dx * adjustmentSpeed;
            double adjustY = dy * adjustmentSpeed;

            // Apply adjustment
            this.setLocation((int)(this.x + (adjustX > 0 ? Math.ceil(adjustX) : Math.floor(adjustX))),
                             (int)(this.y + (adjustY > 0 ? Math.ceil(adjustY) : Math.floor(adjustY))));
            other.setLocation((int)(other.x - (adjustX > 0 ? Math.ceil(adjustX) : Math.floor(adjustX))),
                              (int)(other.y - (adjustY > 0 ? Math.ceil(adjustY) : Math.floor(adjustY))));
        }
    }
    

    public boolean takeDamage(int damage) {
        this.health -= damage;
        return this.health <= 0;
    }

    public void useAbility(int index, Runnable runnable) {
        System.out.println("Enemy used ability");
    }

    public void freeze() {
            this.speed = 0;
    }

    public void unfreeze() {
        this.speed = originalSpeed;
    }
}
