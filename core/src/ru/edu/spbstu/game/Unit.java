package ru.edu.spbstu.game;

import java.util.ArrayDeque;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

public class Unit {
    private FloatPoint coordinates;
    private RoadTile position;
    private ArrayDeque<RoadTile> path;
    private double velocity = 0.5;
    private int nextShoot = 0;

    Unit(int x, int y, Map map)
    {
        coordinates = new FloatPoint(x, y);
        path = new ArrayDeque<RoadTile>();
        position = (RoadTile) map.getTile(x/map.getTileWidth(), y/map.getTileWidth());
    }

    public void setTarget(int tileX, int tileY, Map map)
    {
        if(!(map.getTile(tileX, tileY) instanceof RoadTile) ||
                (!path.isEmpty() && path.getLast().getCoordinates().equals(new Point (tileX, tileY)))) // Checking this in case to not spam method with the same value
        {
            return;
        }
        ArrayDeque<RoadTile> q = new ArrayDeque<RoadTile>();
        Hashtable<RoadTile, RoadTile> pathAll = new Hashtable<RoadTile, RoadTile>();
        q.addLast(position);
        pathAll.put(position, position);
        RoadTile curr = position;
        RoadTile destination =(RoadTile) map.getTile(tileX, tileY);
        while (!q.isEmpty())
        {
            curr = q.removeFirst();
            if (curr == destination)
                break;
            for (RoadTile x: curr.connections)
            {
                if (x != null && !(pathAll.contains(x)))
                {
                    pathAll.put(x, curr);
                    q.addLast(x);
                }
            }
        }
        path.clear();
        while (curr != position)
        {

            path.addFirst(curr);
            curr = pathAll.get(curr);
        }
    }

    public void move(Map map)
    {
        Point destination = position.getCoordinates();
        destination.x = destination.x * map.getTileWidth() + map.getTileWidth() / 2;
        destination.y = destination.y * map.getTileWidth() + map.getTileWidth() / 2;
        if (coordinates.almostEqual(destination, velocity))
        {
            coordinates = new FloatPoint(destination);
            if (!path.isEmpty())
            {
                position.clearUnit();
                position = path.removeFirst();
                position.putUnit(this);
                destination = position.getCoordinates();
                destination.x = destination.x * map.getTileWidth() + map.getTileWidth() / 2;
                destination.y = destination.y * map.getTileWidth() + map.getTileWidth() / 2;
            }
        }
        if (coordinates.x < destination.x)
        {
            coordinates.x += velocity;
        }
        else if (coordinates.x > destination.x)
        {
            coordinates.x -= velocity;
        }
        else if (coordinates.y < destination.y)
        {
            coordinates.y += velocity;
        }
        else if (coordinates.y > destination.y)
        {
            coordinates.y -= velocity;
        }
    }

    public FloatPoint getCoordinates()
    {
        return new FloatPoint(coordinates);
    }

    public Bullet shoot(Map map) {
        if (nextShoot == 0) {
            List <Zombie> visibleZombies = new ArrayList <Zombie>( );
            int x = ((int)Math.floor(coordinates.x) / map.getTileWidth() != map.getWidth()) ?
                    (int)Math.floor(coordinates.x) / map.getTileWidth() : map.getWidth() - 1;
            int y = ((int)Math.floor(coordinates.y) / map.getTileWidth() != map.getHeight()) ?
                    (int)Math.floor(coordinates.y) / map.getTileWidth() : map.getHeight() - 1;
            for (RoadTile r : position.connections) {
                if (r != null) {
                    visibleZombies.addAll(r.getZombies());
                    for (RoadTile rr : r.connections) {
                        if (rr != null) {
                            visibleZombies.addAll(rr.getZombies());
                        }

                    }
                }
            }
            for (int i = 3; i < 5; ++i) {
                int j;
                if (map.getTile(x + i,y) != null && map.getTile(x + i,y) instanceof RoadTile) {
                    for (j = 0; j < i && map.getTile(x + j,y) != null && map.getTile(x + j,y) instanceof RoadTile; ++j) {  }
                    if (j == i) {
                        visibleZombies.addAll(((RoadTile)map.getTile(x + i,y)).getZombies());
                    }
                }
                if (map.getTile(x - i,y) != null && map.getTile(x - i,y) instanceof RoadTile) {
                    for (j = 0; j < i && map.getTile(x - j,y) != null && map.getTile(x - j,y) instanceof RoadTile; ++j) {  }
                    if (j == i) {
                        visibleZombies.addAll(((RoadTile) map.getTile(x - i, y)).getZombies());
                    }
                }
                if (map.getTile(x,y + i) != null && map.getTile(x,y + i) instanceof RoadTile) {
                    for (j = 0; j < i && map.getTile(x,y + j) != null && map.getTile(x,y + j) instanceof RoadTile; ++j) {  }
                    if (j == i) {
                        visibleZombies.addAll(((RoadTile) map.getTile(x, y + i)).getZombies());
                    }
                }
                if (map.getTile(x,y - i) != null && map.getTile(x,y - i) instanceof RoadTile) {
                    for (j = 0; j < i && map.getTile(x,y - j) != null && map.getTile(x,y - j) instanceof RoadTile; ++j) {  }
                    if (j == i) {
                        visibleZombies.addAll(((RoadTile) map.getTile(x, y - i)).getZombies());
                    }
                }
            }

            if (!visibleZombies.isEmpty()) {
                Zombie closestZombie = visibleZombies.get(0);
                for (Zombie z : visibleZombies) {
                    if (coordinates.distance(z.getCoordinates()) < coordinates.distance(closestZombie.getCoordinates())) {
                        closestZombie = z;
                    }
                }
                ++nextShoot;
                return new Bullet(coordinates, closestZombie);
            }
        }
        nextShoot = (nextShoot == 10) ? 0 : nextShoot + 1;
        return null;
    }

}
