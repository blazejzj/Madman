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

    // public void moveTowards(int targetX, int targetY) {

    //     // Calculate the horizontal and vertical distances to the target, (vector)
    //     double dx = targetX - (x + width / 2);
    //     double dy = targetY - (y + height / 2);

    //     // Calculate the total distance to the target, (vector length)
    //     double distance = Math.sqrt(dx * dx + dy * dy);
    //     if (distance <= 0) return;

    //     // Calculate the movement amounts based on speed and distance, (vector normalization)
    //     double moveX = (dx / distance) * speed;
    //     double moveY = (dy / distance) * speed;


    //     // Update the enemy position based on the calculated movement & round number
    //     x += Math.round(moveX);
    //     y += Math.round(moveY);
    // }
    
    public void moveTowards(int targetX, int targetY) {
        // calculate direction towards the player
        double centerX = this.x + this.width / 2.0;
        double centerY = this.y + this.height / 2.0;
        double dx = targetX - centerX;
        double dy = targetY - centerY;
        
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return; // Prevent division by zero
    
        dx /= distance;
        dy /= distance;
    
        double moveX = dx * speed;
        double moveY = dy * speed;
    
        // Correcting the movement speed for diagonal movement
        // make sure enemy moves the same speed in each direction
        double adjustmentFactor = Math.sqrt((moveX * moveX) + (moveY * moveY)) / speed;
        if (adjustmentFactor > 0) {
            moveX /= adjustmentFactor;
            moveY /= adjustmentFactor;
        }
    
        // adjustment
        this.setLocation((int)(this.x + (moveX > 0 ? Math.ceil(moveX) : Math.floor(moveX))),
                         (int)(this.y + (moveY > 0 ? Math.ceil(moveY) : Math.floor(moveY))));
    }

    // public void avoidCollision(Enemy other) {
    //     // Check for collision
    //     if (this.intersects(other)) {
    //         double dx = this.x - other.x;
    //         double dy = this.y - other.y;

    //         // Normalize direction vector
    //         double length = Math.sqrt(dx * dx + dy * dy);
    //         if (length == 0) length = 1; // Avoid division by zero 

    //         dx /= length;
    //         dy /= length;

    //         // calculate moving distance
    //         double adjustmentSpeed = speed * 0.1;
    //         double adjustX = dx * adjustmentSpeed;
    //         double adjustY = dy * adjustmentSpeed;

    //         // adjustment
    //         this.setLocation((int)(this.x + (adjustX > 0 ? Math.ceil(adjustX) : Math.floor(adjustX))),
    //                          (int)(this.y + (adjustY > 0 ? Math.ceil(adjustY) : Math.floor(adjustY))));
    //         other.setLocation((int)(other.x - (adjustX > 0 ? Math.ceil(adjustX) : Math.floor(adjustX))),
    //                           (int)(other.y - (adjustY > 0 ? Math.ceil(adjustY) : Math.floor(adjustY))));
    //     }
    // }

    public void avoidCollision(Enemy other) {
        double dx = x - other.x;
        double dy = y - other.y;

        // Normalize direction
        double length = Math.sqrt(dx * dx + dy * dy);

        dx /= length;
        dy /= length;

        // Move slightly away from the other enemy
        x += Math.round(dx);
        y += Math.round(dy);
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
