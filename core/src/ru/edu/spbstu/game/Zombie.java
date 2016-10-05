package ru.edu.spbstu.game;
import java.util.*;

public class Zombie {

    private int x; //Координата зомби по оси абсцисс

    private int y; //Координата зомби по оси ординат

    private int vx; //Скорость по оси абсцисс

    private int vy; //Скорость по оси ординат

    private int prevCrossingX = 0; //Абсцисса предыдущего перекрестка

    private int prevCrossingY = 0; //Ордината предыдущего перекрестка

    public Zombie(int x, int y, int vx, int vy) { //Генерирует зомби с нужными координатами
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void move() { //Перемещает зомби по дороге и поворачивает при необходимости
        Random rand = new Random( );

        if (vx > 0) { //Перемещает
            x += rand.nextInt(3) + 1;
            //y += rand.nextInt(3) - 1;
        }
        else if (vx < 0) {
            x -= rand.nextInt(3) + 1;
            //y += rand.nextInt(3) - 1;
        }
        else if (vy > 0) {
            //x += rand.nextInt(3) - 1;
            y += rand.nextInt(3) + 1;
        }
        else {
            //x += rand.nextInt(3) - 1;
            y -= rand.nextInt(3) + 1;
        }
    }

    public void rotate(int vx, int vy) { //Поворачивает зомби в нужном нам направлении
        this.vx = vx;
        this.vy = vy;
    }

    public void setPrevCrossing(int x, int y) {this.x = x; this.y= y;} //Устанавливает пройденный перекресток

    public int getX(){return this.x;}

    public int getY(){return this.y;}

    public int getVx(){return this.vx;}

    public int getVy(){return this.vy;}

    public int getPrevCrossingX(){return this.prevCrossingX;}

    public int getPrevCrossingY(){return this.prevCrossingY;}
}


