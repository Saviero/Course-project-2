package ru.edu.spbstu.game;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;

/**
 * Created by liza_moskovskaya on 12/12/2016.
 */
public class HighScore implements Externalizable, Comparable<HighScore>{
    int minuts;
    int seconds;
    //String name;
    HighScore(long time/*, String name*/) {
        this.seconds = (int)(time / 1000 - (time / 1000) / 60 * 60);
        this.minuts = (int)((time / 1000) / 60 - ((time / 1000) / 60) / 60 * 60);
        //this.name = new String(name);
    }

    @Override
    public int compareTo(HighScore o) {
        if (this.minuts < o.minuts)
            return -1;
        else if (this.minuts == o.minuts)
            return this.seconds - o.seconds;
        else
            return 1;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(minuts);
        out.writeInt(seconds);
        //out.writeObject(name);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        minuts = in.readInt( );
        seconds = in.readInt( );
        //name = (String)in.readObject( );
    }
}
