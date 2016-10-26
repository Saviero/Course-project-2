package ru.edu.spbstu.game;

import java.util.ArrayDeque;
import java.util.Hashtable;

public class Unit {
    private Point coordinates;
    private RoadTile position;
    private ArrayDeque<RoadTile> path;
    private int velocity = 2;
    private RoadTile target;
    private Point destination;

    Unit(int x, int y, Map map)
    {
        coordinates = new Point(x, y);
        path = new ArrayDeque<RoadTile>();
        position = (RoadTile) map.getTile(x/map.getTileWidth(), y/map.getTileWidth());
        target = position;
        destination = target.getCoordinates();
    }

    public void setTarget(int tileX, int tileY, Map map)
    {
        if(!(map.getTile(tileX, tileY) instanceof RoadTile))
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
        while (curr != position)
        {
            path.addFirst(curr);
            curr = pathAll.get(curr);
        }
    }

    public void move(Map map)
    {
        if (!path.isEmpty()) {
            target = path.getFirst();
            destination = target.getCoordinates();
            destination.x = destination.x * map.getTileWidth() + map.getTileWidth() / 2;
            destination.y = destination.y * map.getTileWidth() + map.getTileWidth() / 2;
            if (coordinates.almostEqual(destination, velocity)) {
                coordinates = new Point(destination);
                position = target;
                path.removeFirst();
                if(!path.isEmpty()) {
                    target = path.getFirst();
                }
                else {
                    return;
                }

            }
            if (position.connections[0] == target)
                coordinates.x -= velocity;
            else if (position.connections[1] == target)
                coordinates.y -= velocity;
            else if (position.connections[2] == target)
                coordinates.x += velocity;
            else if (position.connections[3] == target)
                coordinates.y += velocity;
        }
    }

    public Point getCoordinates()
    {
        return new Point(coordinates);
    }
}
