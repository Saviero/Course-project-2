package ru.edu.spbstu.game;


public class RoadTile extends Tile
{

    public RoadTile[] connections = new RoadTile[4];
    private Unit unit = null;
    RoadTile(int value, int x, int y)
    {
        super(value, x, y);
    }
    RoadTile(RoadTile copy)
    {
        super(copy);
        this.unit = copy.unit;
        System.arraycopy(copy.connections, 0, this.connections, 0, 4);
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