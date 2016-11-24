package ru.edu.spbstu.game;
import java.util.*;
import java.lang.*;

public class Map {

    private Tile[][] mapArray = new Tile[30][40];  //array of tiles
    private Vector<RoadTile> entrance = new Vector<RoadTile>(); // vector of tiles that lies at the edge of the map
    private int width = 40;
    private int height = 30;
    private int tileWidth = 20;
    private int roadCounter;

    public Map() {
        roadCounter = 0;
        for(int i=0; i<height; ++i)
            for(int j=0;j<width;++j)
                mapArray[i][j] = new Tile(-1, j, i);
    }

    public Map(int width, int height, int tilewidth) {
        mapArray = new Tile[height/tilewidth][width/tilewidth]; //creating an empty map with custom size
        this.tileWidth = tilewidth;
        this.width = width/tilewidth;
        this.height = height/tilewidth;
        roadCounter = 0;
        for(int i=0; i<this.height; ++i)
            for(int j=0; j<this.width; ++j)
                mapArray[i][j] = new Tile(-1, j, i);
    }

    public Vector<RoadTile> getEntrance() {
        return entrance;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTile(int x, int y) {
        if (x >= width || x < 0 || y >= height || y < 0)
        {
            return null;
        }
        return mapArray[y][x];
    }

    public Tile getTile(Point a)
    {
        return getTile(a.x, a.y);
    }

    public int getRoadCount() { return roadCounter; }

    private void connectGraph(Point brush)
    {
        // Adding tile as entrance
        if ((brush.y == 0 || brush.x == 0 || brush.x == width - 1 || brush.y == height - 1) &&
                (!entrance.contains((RoadTile)mapArray[brush.y][brush.x]))) {
            entrance.addElement((RoadTile) mapArray[brush.y][brush.x]);
        }
        // Looking at adjancent tiles and connecting them
        // Left tile
        if (brush.x - 1 >= 0 && mapArray[brush.y][brush.x-1] instanceof RoadTile) {
            ((RoadTile) mapArray[brush.y][brush.x]).connections[0] = (RoadTile) mapArray[brush.y][brush.x - 1];
            ((RoadTile) mapArray[brush.y][brush.x-1]).connections[2] = (RoadTile) mapArray[brush.y][brush.x];
        }
        // Right tile
        if (brush.x + 1 < width && mapArray[brush.y][brush.x+1] instanceof RoadTile) {
            ((RoadTile) mapArray[brush.y][brush.x]).connections[2] = (RoadTile) mapArray[brush.y][brush.x + 1];
            ((RoadTile) mapArray[brush.y][brush.x + 1]).connections[0] = (RoadTile) mapArray[brush.y][brush.x];
        }
        // Up tile
        if (brush.y - 1 >= 0 && mapArray[brush.y-1][brush.x] instanceof RoadTile) {
            ((RoadTile) mapArray[brush.y][brush.x]).connections[1] = (RoadTile) mapArray[brush.y - 1][brush.x];
            ((RoadTile) mapArray[brush.y - 1][brush.x]).connections[3] = (RoadTile) mapArray[brush.y][brush.x];
        }
        // Down tile
        if (brush.y + 1 < height && mapArray[brush.y+1][brush.x] instanceof RoadTile) {
            ((RoadTile) mapArray[brush.y][brush.x]).connections[3] = (RoadTile) mapArray[brush.y + 1][brush.x];
            ((RoadTile) mapArray[brush.y + 1][brush.x]).connections[1] = (RoadTile) mapArray[brush.y][brush.x];
        }
    }

    public void generate() {
        Random rand = new Random();
        Point pos = new Point(0, 0);
        PriorityQueue<Point> nextPoint = new PriorityQueue<Point>();
        nextPoint.add(pos);
        Point brush = new Point(0, 0);
        int rectWidth;
        int rectHeight;
        while (!(nextPoint.isEmpty()))
        {
            //Polling new point
            pos = new Point(nextPoint.poll());
            while (mapArray[pos.y][pos.x].getValue() != -1) {
                if (nextPoint.isEmpty()) {
                    return;
                }
                pos = nextPoint.poll();
            }
            //Rectangle size
            rectWidth = 2+rand.nextInt(8);
            rectHeight = 2+rand.nextInt(8);

            //Adjusting rectangle size to map borders
            if (pos.x + rectWidth >= width -1)
            {
                rectWidth = width - pos.x;
            }
            if (pos.y + rectHeight >= height - 1)
            {
                rectHeight = height - pos.y;
            }

            //Adjusting rectangle size to neighbor blocks
            while (mapArray[pos.y][pos.x + rectWidth - 1].getValue() != -1)
                --rectWidth;

            //Initializing the brush
            brush.x = pos.x+rectWidth;
            brush.y = pos.y;

            //Inside
            for(int i = 0; i < rectHeight; ++i)
                for(int j = 0; j < rectWidth; ++j)
                    mapArray[i+pos.y][j+pos.x] = new Tile(0, j+pos.x, i+pos.y);

            //Right side
            for (int i=0; i < rectHeight; ++i)
            {
                if ((brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0) || mapArray[brush.y][brush.x].getValue() == 1) {
                    ++brush.y;
                    continue;
                }
                mapArray[brush.y][brush.x] = new RoadTile(1, brush.x, brush.y);
                roadCounter++;
                connectGraph(brush);

                ++brush.y;
            }

            //Down side
            for (int i = -1; i < rectWidth; ++i)
            {
                if (brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0 || mapArray[brush.y][brush.x].getValue() == 1) {
                    --brush.x;
                    continue;
                }
                mapArray[brush.y][brush.x] = new RoadTile(1, brush.x, brush.y);
                roadCounter++;
                connectGraph(brush);

                --brush.x;
            }

            //Left side
            for (int i = -1; i < rectHeight; ++i)
            {
                if (brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0 || mapArray[brush.y][brush.x].getValue() == 1) {
                    --brush.y;
                    continue;
                }
                mapArray[brush.y][brush.x] = new RoadTile(1, brush.x, brush.y);
                roadCounter++;
                connectGraph(brush);

                --brush.y;
            }

            //Up side
            for (int i = -1; i < rectWidth; ++i)
            {
                if (brush.y >= height || brush.x >= width || brush.x < 0 || brush.y < 0 || mapArray[brush.y][brush.x].getValue() == 1) {
                    ++brush.x;
                    continue;
                }
                mapArray[brush.y][brush.x] = new RoadTile(1, brush.x, brush.y);
                roadCounter++;
                connectGraph(brush);

                ++brush.x;
            }

            //Adding next points
            if (pos.x + rectWidth + 1 < width && mapArray[pos.y][pos.x+rectWidth+1].getValue() == -1) {

                nextPoint.add(new Point(pos.x + rectWidth + 1, pos.y));
            }
            if (pos.y + rectHeight + 1 < height && mapArray[pos.y + rectHeight + 1][pos.x].getValue() == -1 ) {

                nextPoint.add(new Point(pos.x, pos.y + rectHeight + 1));
            }

        }

    }


}
