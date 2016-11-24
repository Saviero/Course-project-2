package ru.edu.spbstu.game;


public class Tile
{
    private final int value;
    private final Point coordinates;
    Tile(Tile copy)
    {
        this.value = copy.value;
        this.coordinates = new Point(copy.coordinates);
    }
    Tile(int val, int x, int y)
    {
        value = val;
        coordinates = new Point(x, y);
    }

    public Point getCoordinates()
    {
        return new Point(coordinates);
    }

    public FloatPoint getFloatCoordinates( ) { return new FloatPoint((float)coordinates.x, (float)coordinates.y); }

    public int getValue()
    {
        return value;
    }
}