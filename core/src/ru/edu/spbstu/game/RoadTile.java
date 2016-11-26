package ru.edu.spbstu.game;
import java.util.ArrayList;


class RoadTile extends Tile
{

    RoadTile[] connections = new RoadTile[4];

    private Unit unit = null;

    private ArrayList<Zombie> zombies;

    RoadTile(int value, int x, int y)
    {
        super(value, x, y);
        zombies = new ArrayList<Zombie>();
        for (RoadTile tile : connections) {
            tile = null;
        }
    }

    RoadTile(RoadTile copy)
    {
        super(copy);
        this.zombies = copy.zombies;
        this.unit = copy.unit;
        System.arraycopy(copy.connections, 0, this.connections, 0, 4);
    }

    void putUnit(Unit unit)
    {
        this.unit = unit;
    }

    Unit getUnit()
    {
        return unit;
    }

    void clearUnit()
    {
        unit = null;
    }

    void putZombie(Zombie zombie) { zombies.add(zombie); }

    public ArrayList<Zombie> getZombies( ) { return zombies; }

    void clearZombie(Zombie zombie) { zombies.remove(zombie); }

}