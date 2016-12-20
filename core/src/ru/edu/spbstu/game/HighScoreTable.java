package ru.edu.spbstu.game;

import java.io.*;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class HighScoreTable extends TreeSet<HighScore> implements Serializable {

    private final int tableSize = 7;

    public void addScore(String name, long time)
    {
        super.add(new HighScore(time, name));
        while (super.size() > tableSize)
        {
            super.remove(super.last());
        }
    }
}

