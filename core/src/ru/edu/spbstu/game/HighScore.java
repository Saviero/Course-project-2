package ru.edu.spbstu.game;

import java.io.Serializable;

public class HighScore implements Comparable<HighScore>, Serializable{
    int minutes;
    int seconds;
    String name;
    public HighScore(long time, String name) {
        this.seconds = (int)(time / 1000 - (time / 1000) / 60 * 60);
        this.minutes = (int)((time / 1000) / 60 - ((time / 1000) / 60) / 60 * 60);
        this.name = name;
    }

    @Override
    public int compareTo(HighScore o) {
        if (this.minutes < o.minutes)
            return -1;
        else if (this.minutes == o.minutes)
            return this.seconds - o.seconds;
        else
            return 1;
    }

    public String getScore()
    {
        return name + " - " + ((Integer) minutes).toString() + ":" + ((Integer) seconds).toString();
    }
}
