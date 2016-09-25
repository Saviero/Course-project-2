package ru.edu.spbstu.game;
import java.util.*;

public class Map {

    public int[][] mapArray = new int[30][40];
    public int width = 40;
    public int height = 30;

    public Map(){}

    public Map(int width, int height) {
        mapArray = new int[height/20][width/20];
        this.width = width/20;
        this.height = height/20;
    }


    public void generate() {
        Random rand = new Random();
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
                if (mapArray[j][i] == 1) {
                    mapArray[j][i] = 2;
                }
                else {
                    mapArray[j][i] = 1;
                }
            }
            i += 2 + rand.nextInt(4);
        }
    }
}
