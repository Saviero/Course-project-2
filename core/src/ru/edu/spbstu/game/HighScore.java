package ru.edu.spbstu.game;
import java.time.Instant;

/**
 * Created by liza_moskovskaya on 12/12/2016.
 */
public class HighScore {
    Instant date;
    Instant time;
    String name;
    HighScore(Instant date, Instant time, String name) {
        this.date = new Instant(date);
        this.time = new Instant(time);
        this.name = new String(name);
    }
}
