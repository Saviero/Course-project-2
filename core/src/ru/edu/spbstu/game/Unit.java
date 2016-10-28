package ru.edu.spbstu.game;

import java.util.ArrayDeque;
import java.util.Hashtable;

public class Unit {
    private Point coordinates;
    private RoadTile position;
    private ArrayDeque<RoadTile> path;
    private int velocity = 2;

    Unit(int x, int y, Map map)
    {
        coordinates = new Point(x, y);
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
            coordinates = new Point(destination);
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

    public Point getCoordinates()
    {
        return new Point(coordinates);
    }
}
