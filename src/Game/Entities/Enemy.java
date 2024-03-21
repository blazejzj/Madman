package Game.Entities;

public class Enemy extends Entity {
    public double originalSpeed;

    public Enemy(int x, int y, int width, int height, int health, double speed) {
        super(x, y, width, height, health, speed);
        this.originalSpeed = speed;
    }

    public void moveTowards(int targetX, int targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 1) {
            dx /= distance;
            dy /= distance;
            dx *= speed;
            dy *= speed;

            // Calculate new position
            double nextX = x + dx;
            double nextY = y + dy;

            // Check if the next position is closer to the target than the current position
            if (Math.sqrt(Math.pow(targetX - nextX, 2) + Math.pow(targetY - nextY, 2)) < distance) {
                x = (int) nextX;
                y = (int) nextY;

                getBounds().setLocation(x, y);
            }
        }
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
        x += dx;
        y += dy;
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
