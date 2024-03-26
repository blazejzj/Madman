package Game.Main;
import Game.Abilities.Ability;
import Game.Abilities.Freeze;
import Game.Entities.Enemy;
import Game.Entities.EnemyTierThree;
import Game.Entities.EnemyTierTwo;
import Game.Entities.Player;
import Game.Projectiles.Projectile;
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

    final static int FPS = 144;

    // CONSTANTS
    private Player character;
    private boolean playerHit = false;
    private final long playerHitDuration = 3000;
    private long playerHitDurationStartTime  = 0;
    private final long SHOTDELAY = 300;

    // Cheats
    private boolean godMode = false;
    private boolean bombaDropped = false;
    private boolean debugMode = true;

    // Image paths
    private static final String BACKGROUND_IMAGE_PATH = "src/mapLayout/arena1.jpg";
    private static final String CHARACTER_IMAGE_PATH = "src/mapLayout/char7.png";
    private static final String PROJECTILE_IMAGE_PATH = "src/mapLayout/projectile1.png";
    private static final String ENEMYTIERONE_IMAGE_PATH = "src/mapLayout/ghost.png";
    private static final String ENEMYTIERTWO_IMAGE_PATH = "src/mapLayout/ghost1.png";
    private static final String ENEMYTIERTHREE_IMAGE_PATH = "src/mapLayout/ghost2.png";


    // Game state variables
    private boolean upPressed, downPressed, leftPressed, rightPressed, isGameActive = false;
    private boolean enemiesDefeated;
    private boolean walkedThroughDoor = false;
    private String lastDirection = "RIGHT";
    private ArrayList<Projectile> projectiles;
    private ArrayList<Ability> abilities = new ArrayList<>();
    private long lastShotTime = 0;
    private int currentLevel, totalEnemiesToSpawn, enemiesSpawnedSoFar;
    private final int mapWidth = 1000;
    private final int mapHeight = 800;
    private int tileSize = 50;

    private BufferedImage backgroundImage, characterImage, projectileImage, enemyTierOneImage, enemyTierTwoImage, enemyTierThreeImage;
    private ArrayList<Enemy> enemies;
    private Random rand;
    GameMap gameMap;

    // Abilities active
    boolean abilityOneActive = false;

    // METHODS
    public void startGame() {
        isGameActive = true;
    }

    public GamePanel() {
        // Initialize character in position x, y with size of w, h
        // Initialization of everything
        character = new Player(300, 400, 70, 70);
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        rand = new Random();
        lastShotTime = 0;
        gameMap = new GameMap(mapWidth, mapHeight, tileSize);


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
            backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
            characterImage = ImageIO.read(new File(CHARACTER_IMAGE_PATH));
            projectileImage = ImageIO.read(new File(PROJECTILE_IMAGE_PATH));
            enemyTierOneImage = ImageIO.read(new File(ENEMYTIERONE_IMAGE_PATH));
            enemyTierTwoImage = ImageIO.read(new File(ENEMYTIERTWO_IMAGE_PATH));
            enemyTierThreeImage = ImageIO.read(new File(ENEMYTIERTHREE_IMAGE_PATH));

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


        // Handle abilities -> Assign later as an independent method 

        // Add a random chance for an ability to appear
        if (Math.random() < 0.001 && !enemies.isEmpty()) {
            // Random coordinates within the map
            int x = (int) (Math.random() * gameMap.getMapWidth());
            int y = (int) (Math.random() * gameMap.getMapHeight());
            Freeze freeze = new Freeze(x, y, 40, 40, enemies);
            abilities.add(freeze);
        }

        for (var ability : abilities) {
            if (character.getBounds().intersects(ability.getBounds())) {
                System.out.println("TOTAL ABILITIES BEFORE PICK: " + character.abilities.size());
                character.pickUpAbility(ability);
                System.out.println("TOTAL ABILITIES AFTER PICK: " + character.abilities.size());
                // Remove the ability from the game after it's picked up
                abilities.remove(ability);
                break;
            }
        }
    }

    public void playerMovement() {
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

    public void handleGettingHit() {
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

    public void moveEnemyCheckCollision() {
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

    public void initializeNewLevel() {
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
            spawnEnemiesForLevel();
        }
    }

    public void giveCoordinates(KeyEvent e) {
        System.out.println(character.x + " " + character.y);
        System.out.println(gameMap.getDoorX()  + " " + gameMap.getDoorY());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
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
                System.out.println("CHARACTER SPEED: " + character.speed);
                if (!character.abilities.isEmpty() && character.abilities.size() > 0 && !abilityOneActive) {
                    System.out.println("Pressed 1: Used ability!");
                    character.useAbility(0, new Runnable() {
                        @Override
                        public void run() {
                            abilityOneActive = false;
                        }
                    });
                    abilityOneActive = true;
                }
                System.out.println("CHARACTER SPEED: " + character.speed);
                break;
            case KeyEvent.VK_2:
                if (!character.abilities.isEmpty() && character.abilities.size() > 1) { // Ensure theres atleast 2 abilities
                    character.useAbility(1, null);
                }
                break;
            case KeyEvent.VK_3:
                if (!character.abilities.isEmpty() && character.abilities.size() > 2) { // Ensure theres atleast 3 abilities
                    character.useAbility(1, null);
                }
                break;
        }
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
                x = rand.nextInt(mapWidth - 20);
                y = rand.nextInt(borderSize);
                break;
            case 1: // Bottom
                x = rand.nextInt(mapWidth - 20);
                y = mapHeight - borderSize + rand.nextInt(borderSize);
                break;
            case 2: // Left
                x = rand.nextInt(borderSize);
                y = rand.nextInt(mapHeight + 10);
                break;
            case 3: // Right
                x = mapWidth - borderSize + rand.nextInt(borderSize);
                y = rand.nextInt(mapHeight + 10);
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
    }

    private void drawBackground(Graphics g) {
        // initially was a image imported of an arena
//       if (backgroundImage != null) {
//            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
//        }
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
            g.setColor(Color.GREEN); // Set the color to lime
            g.fillRect(ability.getX(), ability.getY(), ability.getWidth(), ability.getHeight());
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
        }
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
