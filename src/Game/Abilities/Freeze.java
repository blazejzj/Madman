package Game.Abilities;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import Game.Entities.Enemy;

public class Freeze extends Ability {

   // Instance variables

   private static final Timer timer = new Timer();
   private List<Enemy> enemies;
   private final int freezeDuration = 3000;

   // Constructor

   public Freeze(int x, int y, int width, int height, List<Enemy> enemies) {
       super(x, y, width, height);
       this.enemies = enemies;
   }
   
   // Methods
   @Override
   public void apply(Runnable callback) {
       synchronized (enemies) { 
           for (Enemy enemy : enemies) {
               enemy.freeze();
           }
       }

       timer.schedule(new TimerTask() {
           @Override
           public void run() {
               synchronized (enemies) { 
                   for (Enemy enemy : enemies) {
                       enemy.unfreeze();
                   }
               }
               if (callback != null) {
                   callback.run();
               }
           }
       }, freezeDuration);
   }
}