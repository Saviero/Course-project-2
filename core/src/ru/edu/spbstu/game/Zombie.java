package ru.edu.spbstu.game;
import java.util.*;


public class Zombie {

    private int x; //Координата зомби по оси абсцисс

    private int y; //Координата зомби по оси ординат

    private int vx; //Скорость по оси абсцисс

    private int vy; //Скорость по оси ординат

    public void Zombie(int xmax, int ymax, int [][] array) { //Генерирует зомби на карте
        Random rand = new Random();
        if (rand.nextBoolean()) { //Определяет, у вертикальной или горизонтальной появится зомби
            if (rand.nextBoolean()) { //Определяет, с левой или правой вертикальной появится зомби, и задает соотв. направление скорости
                x = 0;
                vx = 1;
                vy = 0;
            }
            else {
                x = xmax - 1;
                vx = -1;
                vy = 0;
            }
            y = rand.nextInt(ymax); //Выбирает случайную клетку на границе
            while (array[x][y] != 0) //Ищет ближайшую снизу дорогу
                if (y++ == ymax)
                    y = 0;
        }
        else {
            if (rand.nextBoolean()) { //Определяет, с верхней или нижней границы появится зомби, и задает соотв. направление скорости
                y = 0;
                vx = 0;
                vy = 1;
            }
            else {
                y = ymax - 1;
                vx = 0;
                vy = -1;
            }
            x = rand.nextInt(xmax); //Выбирает случайную клетку на границе
            while (array[x][y] != 0) //Ищет ближайшую справа дорогу
                if (x++ == xmax)
                    x = 0;
        }
    }

    public void Zombie(int xx, int yy) {x = xx; y = yy;} //Генерирует зомби с нужными координатами

    public void move() { //Перемещает зомби по дороге и поворачивает при необходимости
        Random rand = new Random( );
        if (vx > 0) { //Перемещает
            x += rand.nextInt(3) + 1;
            y += rand.nextInt(3) - 1;
        }
        else if (vx < 0) {
            x -= rand.nextInt(3) + 1;
            y += rand.nextInt(3) - 1;
        }
        else if (vy > 0) {
            x += rand.nextInt(3) - 1;
            y += rand.nextInt(3) + 1;
        }
        else {
            x += rand.nextInt(3) - 1;
            y -= rand.nextInt(3) + 1;
        }
        rotate( ); //Поворачивает
    }

    private void rotate() { //Поворачивает зомби

    }
}


