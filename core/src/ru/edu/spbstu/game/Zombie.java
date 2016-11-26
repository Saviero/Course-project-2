package ru.edu.spbstu.game;
import java.util.*;

class Zombie {

    private FloatPoint coordinates; //Zombie's coordinates

    private int vx; //Speed vector abscissa

    private int vy; //Speed vector ordinate

    private Point prevCrossing = new Point(-1, -1); //Previous crossing coordinates

    private int spriteWidth; //The width of the zombie sprite

    private int delayed = 0; //The amount of times the decision of choosing new direction was delayed

    Zombie(Map map, int spriteWidth) { //Generates zombie on a random entrance tile
        this.spriteWidth = spriteWidth;
        Random rand = new Random( );
        coordinates = new FloatPoint( );
        RoadTile entrance = map.getEntrance().elementAt(rand.nextInt(map.getEntrance().size()));
        Point point = entrance.getCoordinates();
        if (point.y == 0 || point.y == map.getHeight() - 1)
        {
            if (point.y == 0)
            {
                coordinates.y = 0;
                vy = 1;
            }
            else
            {
                coordinates.y = (float)((point.y + 1) * map.getTileWidth());
                vy = -1;
            }
            vx = 0;
            coordinates.x = (float)(point.x * map.getTileWidth() + spriteWidth / 2 + 1 + rand.nextInt(map.getTileWidth( ) - spriteWidth - 2));
        }
        else
        {
            if (point.x == 0)
            {
                coordinates.x = 0;
                vx = 1;
            }
            else
            {
                coordinates.x = (float)((point.x + 1) * map.getTileWidth());
                vx = -1;
            }
            vy = 0;
            coordinates.y = (float)(point.y * map.getTileWidth() + spriteWidth / 2 + 1 + rand.nextInt(map.getTileWidth( ) - spriteWidth - 2));
        }
        entrance.putZombie(this);
    }

    public Zombie(FloatPoint coordinates, int vx, int vy, int spriteWidth, Map map) { //Generates zombie with a pre-set coordinates
        if (coordinates.x - map.getTileWidth() * ((int)coordinates.x / map.getTileWidth()) > map.getTileWidth() - spriteWidth / 2 - 1) {
            this.coordinates.x = coordinates.x - spriteWidth / 2;
        }
        else if (coordinates.x - map.getTileWidth() * ((int)coordinates.x / map.getTileWidth()) < spriteWidth / 2 + 1) {
            this.coordinates.x = coordinates.x + spriteWidth / 2;
        }
        if (coordinates.y - map.getTileWidth() * ((int)coordinates.y / map.getTileWidth()) > map.getTileWidth() - spriteWidth / 2 - 1) {
            this.coordinates.y = coordinates.y - spriteWidth / 2;
        }
        else if (coordinates.y - map.getTileWidth() * ((int)coordinates.y / map.getTileWidth()) < spriteWidth / 2 + 1) {
            this.coordinates.y = coordinates.y + spriteWidth / 2;
        }
        this.vx = vx;
        this.vy = vy;
        this.spriteWidth = spriteWidth;
        ((RoadTile)map.getTile((int)coordinates.x / map.getTileWidth(), (int)coordinates.y / map.getTileWidth())).putZombie(this);
    }

    void walk(Map map) { //Chooses the direction zombie walks in and changes its coordinates

        int x = ((int)Math.floor(coordinates.x) / map.getTileWidth() != map.getWidth()) ?
                (int)Math.floor(coordinates.x) / map.getTileWidth() : map.getWidth() - 1;
        int y = ((int)Math.floor(coordinates.y) / map.getTileWidth() != map.getHeight()) ?
                (int)Math.floor(coordinates.y) / map.getTileWidth() : map.getHeight() - 1;

        int [] directions = new int [4]; //An array of possible directions
        Random rand = new Random();
        //delayed = (rand.nextInt(30) < 2 || delayed == 24) ? -1 : delayed + 1;

        int left = (int)(coordinates.x - spriteWidth / 2 - 1) / map.getTileWidth( );
        left = (left <= 0) ? 0 : left;
        int right = (int)(coordinates.x + spriteWidth / 2 + 1) / map.getTileWidth( );
        right = (right >= map.getWidth()) ? map.getWidth() - 1 : right;
        int top = (int)(coordinates.y - spriteWidth / 2 - 1) / map.getTileWidth();
        top = (top <= 0) ? 0 : top;
        int bottom = (int)(coordinates.y + spriteWidth / 2 + 1) / map.getTileWidth();
        bottom = (bottom >= map.getHeight()) ? map.getHeight() - 1 : bottom;

        if (prevCrossing.x != x || prevCrossing.y != y) {

            if (delayed < 0) {
                ((RoadTile)map.getTile(x - vx, y - vy)).clearZombie(this);
                ((RoadTile)map.getTile(x,y)).putZombie(this);
                delayed = 0;
            }
            else if (rand.nextInt(30) < 2 || delayed == (map.getTileWidth() - 2 * spriteWidth) * 2) {

                if ((map.getTile(left, top) == map.getTile(right, bottom)) &&
                        (map.getTile(right, bottom) == map.getTile(left, bottom)) &&
                        (map.getTile(left, bottom) == map.getTile(right, top))) { //If zombie's tile has changed

                    for (int j = 0; j < 4; ++j) { //Checking possible directions

                        directions[j] =(((RoadTile)map.getTile(x,y)).connections[j] != null) ? 1 : 0;

                    }

                    if (Math.abs(vy) > Math.abs(vx)) { //Checking where zombie has come from
                        directions[2 - vy] = 2;
                    }
                    else {
                        directions[1 - vx] = 2;
                    }

                    int k = rand.nextInt(4); //Choosing random direction
                    int res = 4;
                    for (int l = 0; Math.abs(l) < 4; l++) { //Checking if th direction is valid
                        if (directions[k] == 2) { //If it's where zombie came from, we saving it and ceep checking for another directions
                            res = k;
                            if (k == 3) {
                                k = 0;
                            }
                            else {
                                k++;
                            }
                        }
                        if (directions[k] == 1) { //If there is one, choose it
                            res = k;
                            break;
                        }
                        if (directions[k] == 0) { //If the direction is invalid, skip it
                            if (k == 3) {
                                k = 0;
                            }
                            else {
                                k++;
                            }
                        }
                    }

                    rotate(((res % 2 == 0) ? - 1 + res : 0), ((res % 2 == 1) ? - 2 + res : 0)); //Rotating zombie
                    prevCrossing.x = x; //Saving the crossing
                    prevCrossing.y = y;
                    delayed = -1;
                }
            }
            else {
                ++delayed;
            }
        }
        move(map); //Moving a zombie
    }

    FloatPoint getCoordinates(){return this.coordinates;}

    public int getVx(){return this.vx;}

    public int getVy(){return this.vy;}

    private void move(Map map) { //Moves a zombie
        Random rand = new Random( );

        float forward = rand.nextFloat() / 2;
        float sideward = (float)Math.pow(-1, rand.nextInt(2)) * forward / 4;
        float y = coordinates.y - map.getTileWidth() * ((int)coordinates.y / map.getTileWidth());
        float x = coordinates.x - map.getTileWidth() * ((int)coordinates.x / map.getTileWidth());

        if (vy == 0) {
            if (y + sideward < map.getTileWidth() - spriteWidth/ 2 - 1 && y + sideward > spriteWidth / 2 + 1) {
                this.coordinates.y += sideward;
            }
            coordinates.x += vx * forward;
        }
        else if (vx == 0) {
            if (x + sideward < map.getTileWidth() - spriteWidth/ 2 - 1 && x + sideward > spriteWidth / 2 + 1) {
                this.coordinates.x += sideward;
            }
            coordinates.y += vy * forward;
        }
    }

    private void rotate(int vx, int vy) { //Rotating zombie in a stated direction
        this.vx = vx;
        this.vy = vy;
    }

}


