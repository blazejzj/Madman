package Game.Main;

import java.awt.*;
import java.util.Random;

public class GameMap {
    public enum TileType {
        WALL, FLOOR, DOOR, OLDDOOR
    }
    private final TileType[][] tiles;
    private final int tileSize;
    private int doorX, doorY;
    private final int mapWidth, mapHeight;
    private final Random rand = new Random();
    private Point doorPosition;
    private Point oldDoorPosition;

    public GameMap(int mapWidth, int mapHeight, int tileSize) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileSize = tileSize;

        int rows = mapHeight / tileSize;
        int cols = mapWidth / tileSize;
        tiles = new TileType[rows][cols];

        initializeMap();
        placeDoorsRandomly();
    }

    public int getMapHeight() {return mapHeight;}
    public int getMapWidth() {return mapWidth;}
    public int getTileSize() {return tileSize;}
    public int getDoorX() {return doorX * 50;}
    public int getDoorY() {return doorY * 50;}


    private void initializeMap() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (i == 0 || j == 0 || i == tiles.length - 1 || j == tiles[i].length - 1) {
                    tiles[i][j] = TileType.WALL;
                } else {
                    tiles[i][j] = TileType.FLOOR;
                }
            }
        }
    }
    public void placeDoorsRandomly() {
        // Determine the wall (top=0, right=1, bottom=2, left=3) to place the door
        int wallSide = rand.nextInt(4);
        int position;

        // clear the previous old door to a wall
        if (oldDoorPosition != null) {
            tiles[oldDoorPosition.y][oldDoorPosition.x] = TileType.WALL;
        }

        // before new door -> mark as old door
        if (doorPosition != null) {
            tiles[doorPosition.y][doorPosition.x] = TileType.OLDDOOR;
            oldDoorPosition = new Point(doorPosition);
        }

        switch (wallSide) {
            case 0: // Top wall
                position = 1 + rand.nextInt(tiles[0].length - 2);
                tiles[0][position] = TileType.DOOR;
                // Save the door position
                doorX = position;
                doorY = 0;
                doorPosition = new Point(doorX, doorY);
                break;
            case 1: // Right wall
                position = 1 + rand.nextInt(tiles.length - 2);
                tiles[position][tiles[0].length - 1] = TileType.DOOR;
                doorX = tiles[0].length - 1;
                doorY = position;
                doorPosition = new Point(doorX, doorY);
                break;
            case 2: // Bottom wall
                position = 1 + rand.nextInt(tiles[0].length - 2);
                tiles[tiles.length - 1][position] = TileType.DOOR;
                doorX = position;
                doorY = tiles.length - 1;
                doorPosition = new Point(doorX, doorY);
                break;
            case 3: // Left wall
                position = 1 + rand.nextInt(tiles.length - 2);
                tiles[position][0] = TileType.DOOR;
                doorX = 0;
                doorY = position;
                doorPosition = new Point(doorX, doorY);
                break;
        }

        tiles[doorPosition.y][doorPosition.x] = TileType.DOOR;
    }

    public void render(Graphics g) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                switch (tiles[i][j]) {
                    case WALL:
                        g.setColor(Color.GRAY);
                        break;
                    case FLOOR:
                        g.setColor(Color.LIGHT_GRAY);
                        break;
                    case DOOR:
                        g.setColor(Color.BLACK);
                        break;
                    case OLDDOOR:
                        g.setColor(Color.RED);
                        break;
                }
                g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
            }
        }
    }
}
