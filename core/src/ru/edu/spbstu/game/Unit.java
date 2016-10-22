package ru.edu.spbstu.game;

public class Unit {
    Point coordinates;
    RoadTile position;

    Unit(int x, int y, Map map)
    {
        coordinates = new Point(x, y);
        position = (RoadTile) map.getTile(x/map.getTileWidth(), y/map.getTileWidth());
    }

}
