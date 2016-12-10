package ru.edu.spbstu.game;
import java.util.Random;

/**
 * Created by liza_moskovskaya on 10/12/2016.
 */
public class Bullet {

    private FloatPoint coordinates;

    private final float speed = 2.0f;

    private final float vx;

    private final float vy;

    private Zombie zombie;

    private boolean isMoving = true;

    Bullet(FloatPoint coordinates, Zombie zombie) {
        Random rand = new Random( );
        this.coordinates = coordinates;
        this.zombie = zombie;
        float luckiness = (float) (Math.pow(-1, rand.nextInt()) * rand.nextFloat() * 0.1);
        float distance = coordinates.distance(zombie.getCoordinates());
        float time = distance / speed;
        vx = (float)(zombie.getCoordinates().x + zombie.getVx() * time * (0.25 + luckiness) - coordinates.x)/distance;
        vy = (float) (zombie.getCoordinates().y + zombie.getVy() * time * (0.25 + luckiness) - coordinates.y)/distance;
    }

    void fly(Map map) {
        coordinates.x = speed * vx;
        coordinates.y = speed * vy;

        int x = ((int)Math.floor(coordinates.x) / map.getTileWidth() != map.getWidth()) ?
                (int)Math.floor(coordinates.x) / map.getTileWidth() : map.getWidth() - 1;
        int y = ((int)Math.floor(coordinates.y) / map.getTileWidth() != map.getHeight()) ?
                (int)Math.floor(coordinates.y) / map.getTileWidth() : map.getHeight() - 1;

        if (!(map.getTile(x, y) instanceof RoadTile)) {
            isMoving = false;
        }

        if (coordinates.x >= map.getWidth() * map.getTileWidth() ||
                coordinates.x <= 0 ||
                coordinates.y >= map.getHeight() * map.getTileWidth() ||
                coordinates.y <= 0){
            isMoving = false;
        }

        if (coordinates.almostEqual(zombie.getCoordinates(), (float)zombie.getSpriteWidth() / 2)) {
            isMoving = false;
            zombie.kill( );
        }
    }

    FloatPoint getCoordinates() {
        return coordinates;
    }

    public boolean isMoving() {
        return isMoving;
    }
}