package Game.Abilities;
import Game.Entities.Player;

public class HealingPotion extends Ability {
    final static int HEALING_POINTS = 3;
    Player player;

    public HealingPotion(int x, int y, int width, int height, Player player) {
        super(x, y, width, height);
        this.player = player;
    }

    @Override
    public void apply(Runnable callback) {
        System.out.println("Applying HealingPotion");
        player.heal(HEALING_POINTS);
        if (callback != null) {
            callback.run();
        }
    }
    
}
