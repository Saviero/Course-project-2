package ru.edu.spbstu.game;


public class RoadTile extends Tile
{

    public RoadTile[] connections = new RoadTile[4];
    RoadTile()
    {
        super();
        for (RoadTile x : connections)
        {
            x = null;
        }
    }
    RoadTile(int value)
    {
        super(value);
        for (RoadTile x : connections)
        {
            x = null;
        }
    }
}