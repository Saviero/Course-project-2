package ru.edu.spbstu.game;


public class RoadTile extends Tile
{

    public RoadTile[] connections = new RoadTile[4];
    private Unit unit = null;
    RoadTile()
    {
        super();
    }
    RoadTile(int value)
    {
        super(value);
    }
    public void putUnit(Unit unit)
    {
        this.unit = unit;
    }

    public Unit getUnit()
    {
        return unit;
    }
}