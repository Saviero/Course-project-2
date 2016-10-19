package ru.edu.spbstu.game;


public class Tile
{
    private int value;
    Tile()
    {
        value = -1;
    }
    Tile(int x)
    {
        value = x;
    }

    public int getValue()
    {
        return value;
    }
}