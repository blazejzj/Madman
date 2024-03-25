package Game.Entities;

public class Enemy extends Entity {
    public double originalSpeed;

    public Enemy(int x, int y, int width, int height, int health, double speed) {
        super(x, y, width, height, health, speed);
        this.originalSpeed = speed;
    }

    public void moveTowards(int targetX, int targetY) {
        // Calculate the horizontal and vertical distances to the target, (vector)
        double dx = targetX - (x + width / 2);
        double dy = targetY - (y + height / 2);

        // Calculate the total distance to the target, (vector length)
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= 0)
            return;

        // Calculate the movement amounts based on speed and distance, (vector normalization)
        double moveX = (dx / distance) * speed;
        double moveY = (dy / distance) * speed;

        // Update the enemy position based on the calculated movement
        // round number
        x += Math.round(moveX);
        y += Math.round(moveY);
    }

    public void avoidCollision(Enemy other) {
        double dx = x - other.x;
        double dy = y - other.y;

        // Normalize direction
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0) return; // Prevent division by zero

        dx /= length;
        dy /= length;

        // Move slightly away from the other enemy
        // round number
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

    // public void unfreeze(double originalSpeed) {
    //         this.speed = originalSpeed;
    // }

    public void unfreeze() {
        this.speed = originalSpeed;
}
}
