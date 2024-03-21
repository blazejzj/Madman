//package Game.Abilities;
//
//import java.util.List;
//import Game.Entities.Enemy;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Freeze extends Ability {
//
//    // Instance variables
//    private List<Enemy> enemies;
//    private final int freezeDuration;
//
//    // Constructor
//    public Freeze(int x, int y, int width, int height, List<Enemy> enemies, int freezeDuration) {
//        super(x, y, width, height);
//        this.enemies = enemies;
//        this.freezeDuration = 3000;
//    }
//
//    // Methods
//    @Override
//    public void apply(Runnable callback) {
//        for (Enemy enemy : enemies) {
//            // originalSpeeds.put(enemy, enemy.getSpeed());
//            enemy.freeze();
//        }
//        new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//                    @Override
//                    public void run() {
//                        for (Enemy enemy : enemies) {
//                            // enemy.unfreeze(originalSpeeds.get(enemy));
//                            enemy.unfreeze();
//                        }
//                        if (callback == null) {
//                            return;
//                        }
//                        callback.run();
//                    }
//                },
//                freezeDuration
//        );
//    }
//
//}