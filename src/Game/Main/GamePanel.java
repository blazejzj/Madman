package Game.Main;

import Game.Abilities.Ability;
import Game.Abilities.Freeze;
import Game.Entities.Enemy;
import Game.Entities.EnemyTierThree;
import Game.Entities.EnemyTierTwo;
import Game.Entities.Player;
import Game.Projectiles.Projectile;
import Game.Collectible.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements KeyListener {

    // CONSTANTS

    private static final long playerHitDuration = 3000;
    private static final long SHOTDELAY = 300;
    private static final int FPS = 144;
    private static final long ABILITY_COOLDOWN_DURATION = 5000;
    private static final int MAP_WIDTH = 1000;
    private static final int MAP_HEIGHT = 800;
    private static final int MAP_TILE_SIZE = 50;

    // Image paths
    private static final String CHARACTER_IMAGE_PATH = "src/mapLayout/char7.png";
    private static final String PROJECTILE_IMAGE_PATH = "src/mapLayout/projectile1.png";
    private static final String ENEMYTIERONE_IMAGE_PATH = "src/mapLayout/ghost.png";
    private static final String ENEMYTIERTWO_IMAGE_PATH = "src/mapLayout/ghost1.png";
    private static final String ENEMYTIERTHREE_IMAGE_PATH = "src/mapLayout/ghost2.png";
    private static final String freezeNOBG_IMAGE_PATH = "src/mapLayout/freezeNoBG.png";
    private static final String healingPotion_IMAGE_PATH = "src/MapLayout/healingPotionNoBG.png";

    // Cheats
    private boolean godMode = false;
    private boolean bombaDropped = false;
    private boolean debugMode = true;


    // Game state variables
    private boolean upPressed, downPressed, leftPressed, rightPressed, isGameActive = false;
    private boolean enemiesDefeated;
    private boolean walkedThroughDoor = false;
    private String lastDirection = "RIGHT";
    private ArrayList<Projectile> projectiles;
    private ArrayList<Ability> abilities = new ArrayList<>();
    private ArrayList<Collectible> collectibles = new ArrayList<>();
    private long lastShotTime = 0;
    private int currentLevel, totalEnemiesToSpawn, enemiesSpawnedSoFar;
    private BufferedImage characterImage, projectileImage, enemyTierOneImage, enemyTierTwoImage, enemyTierThreeImage, abilityFreezeImage, collectibleHealingPotion;
    private ArrayList<Enemy> enemies;
    private Random rand;
    private GameMap gameMap;
    private long[] abilityCooldowns = new long[3];
    private Player character;
    private boolean playerHit = false;
    private long playerHitDurationStartTime  = 0;

    // Abilities active
    boolean isAbilityActive = false;

    // METHODS
    public void startGame() {
        isGameActive = true;
    }

    public GamePanel() {
        // Initialize character in position x, y with size of w, h
        // Initialization of everything
        character = new Player(300, 400, 70, 70, 3, 3);
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        rand = new Random();
        lastShotTime = 0;
        gameMap = new GameMap(MAP_WIDTH, MAP_HEIGHT, MAP_TILE_SIZE);


        // Beginning
        currentLevel = 1;
        spawnEnemiesForLevel();
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        isGameActive = false;
        debugMode = true;

        // Timers
        startGameTimer();
    }

    private void loadImages() {
        try {
            characterImage = ImageIO.read(new File(CHARACTER_IMAGE_PATH));
            projectileImage = ImageIO.read(new File(PROJECTILE_IMAGE_PATH));
            enemyTierOneImage = ImageIO.read(new File(ENEMYTIERONE_IMAGE_PATH));
            enemyTierTwoImage = ImageIO.read(new File(ENEMYTIERTWO_IMAGE_PATH));
            enemyTierThreeImage = ImageIO.read(new File(ENEMYTIERTHREE_IMAGE_PATH));
            abilityFreezeImage = ImageIO.read(new File(freezeNOBG_IMAGE_PATH));
            collectibleHealingPotion = ImageIO.read(new File(healingPotion_IMAGE_PATH));

        } catch (IOException e) {
            System.out.println("Error? Something went wrong" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startGameTimer() {
        int delay = 1000 / FPS; // Convert frame rate to delay in milliseconds for 60 fps
        new Timer(delay, e -> {
            updateGame();
            repaint();
        }).start();
    }

    private void updateGame() {

        // If game not on return
        if (!isGameActive) {
            return;
        }

        playerMovement();

        moveProjectileCheckCollision();

        moveEnemyCheckCollision();

        initializeNewLevel();

        handleGettingHit();

        enemyTierThreeRage();

        spawnHandleAbilities();

        spawnHandleCollectibles();

        for (Iterator<Collectible> iterator = collectibles.iterator(); iterator.hasNext();) {
            Collectible collectible = iterator.next();
            if (character.getBounds().intersects(collectible.getBounds())) {
                collectible.applyEffect(character);
                iterator.remove();
            }
        }

        if (walkedThroughDoor) {
            walkedThroughDoor = false;
            // move door to a new random location FOR NOW -> later mirror the door to different side
            gameMap.placeDoorsRandomly();
            character.x = 300;
            character.y = 400;
        }

        if (bombaDropped) {
            enemies.clear();
            bombaDropped = false;
        }

    }

    private void spawnHandleAbilities() {
        // Add a random chance for an ability to appear
        if (Math.random() < 0.00008 && !enemies.isEmpty()) {
            int playableWidth = gameMap.getMapWidth() - MAP_TILE_SIZE * 2; // Subtract wall thickness from both sides
            int playableHeight = gameMap.getMapHeight() - MAP_TILE_SIZE * 2; // Subtract wall thickness from top and bottom
    
            // Adjust for ability size to ensure it spawns fully within the playable area
            playableWidth -= 40; // Subtract the width of the ability
            playableHeight -= 40; // Subtract the height of the ability
    
            int x = MAP_TILE_SIZE + (int) (Math.random() * playableWidth);
            int y = MAP_TILE_SIZE + (int) (Math.random() * playableHeight);
    

            // // Randomly choose spawned ability
            // int abilityType = (int) (Math.random() * 2); // Either 0 or 1

            // Ability newAbility;
            // if (abilityType == 0) {
            //     newAbility = new Freeze(x, y, 40, 40, enemies);
            // }
            // abilities.add(newAbility);
            Ability newAbility;
            newAbility = new Freeze(x, y, 40, 40, enemies);
            abilities.add(newAbility);

        }
    
        for (var ability : abilities) {
            if (character.getBounds().intersects(ability.getBounds())) {
                character.pickUpAbility(ability);
                abilities.remove(ability);
                break;
            }
        }
    }

    private void spawnHandleCollectibles() {
        // Fixed size for each collectible
        int width = 30;
        int height = 30;
        int potionHealPoints = 3;

        // Spawn chances + Cant spawn when enemies are not on map
        if (Math.random() < 0.00008 && !enemies.isEmpty()) {
            
            int playableWidth = gameMap.getMapWidth() - MAP_TILE_SIZE * 2; // Subtract wall thickness from both sides
            int playableHeight = gameMap.getMapHeight() - MAP_TILE_SIZE * 2; // Subtract wall thickness from top and bottom
    
            // Adjust for ability size to ensure it spawns fully within the playable area
            playableWidth -= 40; // Subtract the width of the ability
            playableHeight -= 40; // Subtract the height of the ability
    
            int x = MAP_TILE_SIZE + (int) (Math.random() * playableWidth);
            int y = MAP_TILE_SIZE + (int) (Math.random() * playableHeight);

            Collectible healingPotion = new HealthPotion(x, y, width, height, potionHealPoints);
            collectibles.add(healingPotion);
        }
    }
    
    private void playerMovement() {
        // Player movement
        if (upPressed) {
            character.y -= character.getSpeed();
            lastDirection = "UP";
        }
        if (downPressed) {
            character.y += character.getSpeed();;
            lastDirection = "DOWN";
        }
        if (leftPressed) {
            character.x -= character.getSpeed();;
            lastDirection = "LEFT";
        }
        if (rightPressed) {
            character.x += character.getSpeed();;
            lastDirection = "RIGHT";
        }

        // Upon hitting the walls
        if (character.x < 50) {
            character.x = 50;
        }
        if (character.y < 50) {
            character.y = 50;
        }
        if (character.x > gameMap.getMapWidth() - 50 - (int) character.getWidth()) {
            character.x = gameMap.getMapWidth() - 50 - (int) character.getWidth();
        }
        if (character.y > gameMap.getMapHeight() - 50 - (int) character.getHeight()) {
            character.y = gameMap.getMapHeight() - 50 - (int) character.getHeight();
        }
    }

    private void handleGettingHit() {
        // Invincible mode upon getting hit 
        if (playerHit && System.currentTimeMillis() - playerHitDurationStartTime > playerHitDuration) {
            playerHit = false;
        }

        for (var enemy : enemies) {
            if (character.intersects(enemy.getBounds()) && !playerHit) {
                if (godMode) {
                    continue;
                }
                character.setHealth(character.getHealth() - 1);
                playerHit = true;
                playerHitDurationStartTime = System.currentTimeMillis();

                if (character.getHealth() <= 0) {
                    System.out.println("Game over you lose");
                }
            }
        }
    }

    private void moveEnemyCheckCollision() {
        // move enemies towards player -> handle the collision using vector
        for (int i = 0; i < enemies.size(); i++) {
            Enemy current = enemies.get(i);
            current.moveTowards(character.x + character.width, character.y + character.height);

            for (int j = 0; j < enemies.size(); j++) {
                if (i != j) {
                    Enemy other = enemies.get(j);
                    if (isColliding(current, other)) {
                        current.avoidCollision(other);
                    }
                }
            }
        }
    } 

    private void initializeNewLevel() {
        // Check if level is complete and spawn new enemies if so
        if (enemies.isEmpty() && enemiesSpawnedSoFar >= totalEnemiesToSpawn) {
            enemiesDefeated = true;
        }

        if (enemiesDefeated &&
                ((Math.abs(gameMap.getDoorX() - character.x) < 50 && gameMap.getDoorY() + 50 == character.y)
                ||
                (gameMap.getDoorX() == character.x + character.width && Math.abs(gameMap.getDoorY() - character.y) < 50)
                ||
                ((Math.abs(gameMap.getDoorX() - character.x) < 50 && gameMap.getDoorY() == character.y + character.height)
                ||
                (gameMap.getDoorX() + 50 == character.x && Math.abs(gameMap.getDoorY() - character.y) < 50)))) {

            System.out.println("NEW LEVEL INITIALIZING, LEVEL NOW : " + currentLevel);
            enemiesDefeated = false;
            walkedThroughDoor = true;
            currentLevel++;
            abilities.clear();
            collectibles.clear();
            spawnEnemiesForLevel();
        }
    }

    private void giveCoordinates(KeyEvent e) {
        System.out.println(character.x + " " + character.y);
        System.out.println(gameMap.getDoorX()  + " " + gameMap.getDoorY());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        long currentTime = System.currentTimeMillis();

        switch (key) {
            case KeyEvent.VK_W:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                shootProjectiles(lastDirection);
                break;
            case KeyEvent.VK_B:
                killEnemies(e);
                break;
            case KeyEvent.VK_C:
                giveCoordinates(e);
                break;
            case KeyEvent.VK_L:
                increaseLevel(e);
                break;
            case KeyEvent.VK_G:
                godMode(e);
                break;
                case KeyEvent.VK_1:
                if (isAbilityReady(0, currentTime)) {
                    useAbility(0, currentTime);
                }
                break;
                
            case KeyEvent.VK_2:
                if (isAbilityReady(1, currentTime)) {
                    useAbility(1, currentTime);
                }
                break;
                
            case KeyEvent.VK_3:
                if (isAbilityReady(2, currentTime)) {
                    useAbility(2, currentTime);
                }
                break;
        }
    }

    private boolean isAbilityReady(int abilityIndex, long currentTime) {
        // Check if the ability exists and if the cooldown has passed
        return character.abilities.size() > abilityIndex && currentTime - abilityCooldowns[abilityIndex] > ABILITY_COOLDOWN_DURATION;
    }

    private void useAbility(int abilityIndex, long currentTime) {
        System.out.println("Pressed " + (abilityIndex + 1 ) + " used ability!");
        character.useAbility(abilityIndex, null);
        abilityCooldowns[abilityIndex] = currentTime;
    }

    private void shootProjectiles(String direction) {
        if (!playerHit) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOTDELAY) {
                // Adjust starting position based on characters direction
                int startX = character.x + character.width / 2;
                int startY = character.y + character.height / 2;
                projectiles.add(new Projectile(startX, startY, direction));
                lastShotTime = currentTime;
            }
        }
    }

    private void moveProjectileCheckCollision() {
        // Move and check projectiles
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.move();
            if (p.bounds.x > getWidth() || p.bounds.y > getHeight() ||
                    p.bounds.x < 0 || p.bounds.y < 0) { // Check if projectile is off screen
                projectiles.remove(i);
                i--; // Decrement i to avoid skipping the next projectile
            }
        }

        Iterator<Projectile> projectileIterator = projectiles.iterator();

        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();

            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (projectile.getBounds().intersects(enemy.getBounds())) {
                    projectileIterator.remove(); // Remove the projectile

                    if (enemy.takeDamage(1)) {
                        enemyIterator.remove();
                        // Update stuff here when enemy dies
                    }
                    break;
                }
            }
        }
    }

    private void spawnEnemiesForLevel() {
        enemies.clear();
        enemiesSpawnedSoFar = 0;

        // calculating how many red and boss enemies to spawn
        int enemyTierTwoCount = currentLevel % 2 == 0 ? currentLevel / 2 : (currentLevel / 2) + 1;

        // calculate how many green enemies spawn -> lvl 10, 1 -> lvl 20, 2 -> lvl 30, 3 etc
        int enemyTierThreeCount = currentLevel % 10 == 0 ? currentLevel / 10 : 0;

        // total enemies to spawn
        int totalEnemyCount = 4 + currentLevel - 1 + enemyTierTwoCount;

        totalEnemiesToSpawn = totalEnemyCount + enemyTierThreeCount;

        // hold all enemies in a list to shuffle them together later -> avoiding getting one typed printed out at once
        ArrayList<Integer> spawnList = new ArrayList<>();
        for (int i = 0; i < totalEnemyCount - enemyTierTwoCount; i++) {
            spawnList.add(0); // regular
        }
        for (int i = 0; i < enemyTierTwoCount; i++) {
            spawnList.add(1); // red
        }
        for (int i = 0; i < enemyTierThreeCount; i++) {
            spawnList.add(2); // green enemy
        }

        Collections.shuffle(spawnList);

        // timer for spawning enemies in a random order
        Timer spawnTimer = new Timer(800, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < spawnList.size()) {
                    int enemyType = spawnList.get(index);
                    switch (enemyType) {
                        case 0:
                            spawnEnemy();
                            break;
                        case 1:
                            spawnEnemyTierTwo();
                            break;
                        case 2:
                            spawnEnemyTierThree();
                            break;
                    }
                    index++;
                    enemiesSpawnedSoFar++;
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        spawnTimer.start();
    }

    private int[] spawnEnemyCoordinates() {
        int borderSize = 50;
        int x, y;

        int borderChoice = rand.nextInt(4);
        switch (borderChoice) {
            case 0: // Top
                x = rand.nextInt(MAP_WIDTH - 20);
                y = rand.nextInt(borderSize);
                break;
            case 1: // Bottom
                x = rand.nextInt(MAP_WIDTH - 20);
                y = MAP_HEIGHT - borderSize + rand.nextInt(borderSize);
                break;
            case 2: // Left
                x = rand.nextInt(borderSize);
                y = rand.nextInt(MAP_HEIGHT + 10);
                break;
            case 3: // Right
                x = MAP_WIDTH - borderSize + rand.nextInt(borderSize);
                y = rand.nextInt(MAP_HEIGHT + 10);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + borderChoice);
        }

        int[] cords = new int[2];
        cords[0] = x;
        cords[1] = y;
        return cords;
    }

    private void spawnEnemy() {
        int[] cords = spawnEnemyCoordinates();
        Enemy enemy = new Enemy(cords[0], cords[1], 60, 60, 1, 1.5);
        enemies.add(enemy);
    }

    private void spawnEnemyTierTwo() {
        int[] cords = spawnEnemyCoordinates();
        Enemy enemyt2 = new EnemyTierTwo(cords[0], cords[1]);
        enemies.add(enemyt2);
    }

    private void spawnEnemyTierThree() {
        int[] cords = spawnEnemyCoordinates();
        Enemy enemyt3 = new EnemyTierThree(cords[0], cords[1]);
        enemies.add(enemyt3);
    }

    private void enemyTierThreeRage() {
        // Check if boss has less than 10 health -> rage mode
        for (var tier3 : enemies) {
            if (tier3 instanceof EnemyTierThree && tier3.getHealth() < 10) {
                tier3.setSpeed(2);
            }
        }
    }

    private boolean isColliding(Enemy enemy1, Enemy enemy2) {
        return enemy1.intersects(enemy2);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_W:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameMap.render(g);
        drawBackground(g);
        drawProjectiles(g);
        drawCharacter(g);
        drawAbilities(g);
        drawEnemies(g);
        drawGameInfo(g);
        drawCollectibles(g);
    }

    private void drawBackground(Graphics g) {
        gameMap.render(g);
    }

    private void drawProjectiles(Graphics g) {
        for (var projectile : projectiles) {
            projectile.draw(g, projectileImage);
        }
    }

    private void drawCharacter(Graphics g) {
        if (characterImage != null) {
            g.drawImage(characterImage, character.x, character.y , character.width, character.height , this);
        }
        else {
            // Fallback in case of character Image not loading correctly -> fill the rectangle
            g.fillRect(character.x, character.y, character.width, character.height);
        }
//        g.fillRect(character.x, character.y, character.width, character.height);
    }

    private void drawEnemies(Graphics g) {
        for (var enemy : enemies) {
            if (enemy instanceof EnemyTierTwo) {
                g.drawImage(enemyTierTwoImage, enemy.x, enemy.y, enemy.width, enemy.height, this);
            }
            else if (enemy instanceof  EnemyTierThree) {
                g.drawImage(enemyTierThreeImage, enemy.x, enemy.y, enemy.width, enemy.height, this);
            }
            else {
                g.drawImage(enemyTierOneImage, enemy.x, enemy.y, enemy.width, enemy.height, this);
            }
        }
    }

    private void drawAbilities(Graphics g) {
        for (var ability : abilities) {
            if (ability instanceof Freeze) {
                g.drawImage(abilityFreezeImage, ability.getX(), ability.getY(), ability.getWidth(), ability.getHeight(), this);
            }
            else {
                // Throwback 
                g.setColor(Color.GREEN); // Set the color to lime
                g.fillRect(ability.getX(), ability.getY(), ability.getWidth(), ability.getHeight());
            }

        }
    }

    private void drawCollectibles(Graphics g) {
        for(var collectible : collectibles) {
            if (collectible instanceof HealthPotion) {
                g.drawImage(collectibleHealingPotion, collectible.getX(), collectible.getY(), collectible.getWidth(), collectible.getHeight(), this);
            }
            else {
                // Throwback
                g.setColor(Color.GREEN);
                g.fillRect(collectible.getX(), collectible.getY(), collectible.getWidth(), collectible.getHeight());
            }
        }
    }

    private void drawGameInfo(Graphics g) {
        if (debugMode) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Current level: " + currentLevel, 10, 20);
            g.drawString("Total Enemies: " + totalEnemiesToSpawn, 10, 40);
            g.drawString("Enemies left: " + enemies.size(), 10, 60);
            g.drawString("Health: " + character.getHealth(), 10, 80);

            // Display information about held abilities (Eventually adding a nicer GUI)

            g.drawString("Ability 1: " + getAbilityNameByIndex(0), 10, 100);
            g.drawString("Ability 2: " + getAbilityNameByIndex(1), 10, 120);
            g.drawString("Ability 3: " + getAbilityNameByIndex(2), 10, 140);
        }
    }

    private String getAbilityNameByIndex(int index) {
        if (index >= character.abilities.size()) {
            return "NONE";
        }

        Ability ability = character.abilities.get(index);
        if (ability instanceof Freeze) {
            return "Freeze";
        }

        // if (ability instanceof HealingPotion) {
        //     return "Healing-Potion";
        // }

        return "Unknown";
    }

    private void killEnemies(KeyEvent e) {
        var usage = e.getKeyCode();
        if (usage == KeyEvent.VK_B) {
            System.out.println("KILLED ALL ENEMIES #DEBUGMODE");
            bombaDropped = true;
        }
    }

    private void increaseLevel(KeyEvent e) {
        var usage = e.getKeyCode();
        if (usage == KeyEvent.VK_L) {
            System.out.println("LEVEL INCREASED #DEBUGMODE");
            currentLevel++;
        }
    }

    private void godMode(KeyEvent e) {
        var usage = e.getKeyCode();
        if (usage == KeyEvent.VK_G) {
            if (godMode) {
                godMode = false;
                System.out.println("GOD MODE DISABLED #DEBUGMODE");
                return;
            }
            System.out.println("GOD MODE ENABLED #DEBUGMODE");
            godMode = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

}
