package ru.edu.spbstu.game;


public class RoadTile extends Tile
{

    public RoadTile[] connections = new RoadTile[4];
    RoadTile()
    {
        super();
    }
    RoadTile(int value)
    {
        super(value);
    }
}