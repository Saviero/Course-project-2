package ru.edu.spbstu.game;
import java.lang.Math.*;


public class FloatPoint implements Comparable<Point>
{
    public float x;
    public float y;

    FloatPoint( )
    {
        this.x = 0;
        this.y = 0;
    }

    FloatPoint(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    FloatPoint (Point point)
    {
        this.x = point.x;
        this.y = point.y;
    }

    FloatPoint (FloatPoint point)
    {
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloatPoint)) {
            return false;
        }
        FloatPoint pt = (FloatPoint) obj;
        return x == pt.x && y == pt.y;
    }

    public boolean almostEqual(FloatPoint point, double delta)
    {
        /*
         This method checks if point is inside a circle with center of this and radius of delta
        */
        return ((this.x-point.x)*(this.x-point.x)+(this.y-point.y)*(this.y-point.y))<delta*delta;
    }

    public boolean almostEqual(Point point, double delta)
    {
        /*
         This method checks if point is inside a circle with center of this and radius of delta
        */
        return ((this.x-point.x)*(this.x-point.x)+(this.y-point.y)*(this.y-point.y))<delta*delta;
    }

    public int compareTo(Point a)
    {
        if (this.y < a.y)
            return -1;
        else if (this.y == a.y)
            return (int)this.x - a.x;
        else
            return 1;
    }

    public float distance(FloatPoint a) {
        return (float)Math.sqrt(Math.pow(this.x - a.x, 2) + Math.pow(this.y - a.y, 2));
    }
}
