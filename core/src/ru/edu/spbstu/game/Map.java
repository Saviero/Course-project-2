package ru.edu.spbstu.game;
import java.util.*;

public class Map {

    public int[][] mapArray = new int[40][30];

    public Map(){}

    public Map(int width, int height) {
        mapArray = new int[width/20][height/20];
    }


    public void generate() {
        Random rand = new Random(42);
        int i = 1 + rand.nextInt(2);
        while (i < mapArray.length - 1) {
            for(int j = 0; j < mapArray[i].length; ++j) {
                mapArray[i][j] = 1;
            }
            i += 2 + rand.nextInt(4);
        }
        i = 1 + rand.nextInt(2);
        while (i < mapArray[0].length - 1) {
            for (int j=0; j < mapArray.length; ++j) {
                mapArray[j][i] = 1;
            }
            i += 2 + rand.nextInt(4);
        }
    }
}
