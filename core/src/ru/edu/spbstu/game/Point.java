package ru.edu.spbstu.game;

public class Point implements Comparable<Point>
{
    public int x;
    public int y;

    Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    Point (Point point)
    {
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point pt = (Point) obj;
        return x == pt.x && y == pt.y;
    }

    public boolean almostEqual(Point point, int delta)
    {
        return (this.x-point.x)*(this.x-point.x)+(this.y-point.y)*(this.y-point.y)<=delta*delta;
    }
    public int compareTo(Point a)
    {
        if (this.y < a.y)
            return -1;
        else if (this.y == a.y)
            return this.x - a.x;
        else
            return 1;
    }
}