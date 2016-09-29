package ru.edu.spbstu.game;
import java.util.*;

public class Map {

    public int[][] mapArray = new int[30][40];  //array of tiles
    public int width = 40;
    public int height = 30;

    public Map(){}

    public Map(int width, int height, int tilewidth) {
        mapArray = new int[height/tilewidth][width/tilewidth]; //creating an empty map with custom size
        this.width = width/tilewidth;
        this.height = height/tilewidth;
    }


    public void generate() {
        Random rand = new Random();
        int i = 1 + rand.nextInt(2);
        int cooldown = 0;

        while (i < height- 2) {
            for(int j = 0; j < width; ++j) {

                mapArray[i][j] = 1;
                if (rand.nextInt(16)/15==1 && cooldown <= 0)
                {
                    if (i >= height - 2)
                        i += rand.nextInt(2) - 1;
                    else if (i <= 1)
                        i += rand.nextInt(2);
                    else
                        i += rand.nextInt(3) - 1;
                    // TODO Fix roads' widening due to shifts
                    cooldown = 2;
                }
                mapArray[i][j] = 1;
                --cooldown;
            }
            i += 3 + rand.nextInt(4);
        }

        cooldown = 0;
        i = 1 + rand.nextInt(2);
        while (i < width - 1) {
            for (int j=0; j < height; ++j) {
                if (mapArray[j][i] == 1) {
                    mapArray[j][i] = 2;
                }
                else {
                    mapArray[j][i] = 1;
                }
                if (rand.nextInt(16)/15==1 && cooldown <= 0)
                {
                    if (i >= width - 2)
                        i -= 1;
                    else if (i <= 1)
                        i += rand.nextInt(2);
                    else
                        i += rand.nextInt(3) - 1;
                    cooldown = 1;
                }
                if (mapArray[j][i] == 1) {
                    mapArray[j][i] = 2;
                }
                else {
                    mapArray[j][i] = 1;
                }
                --cooldown;
            }
            i += 3 + rand.nextInt(4);
        }
    }
}
