package ru.edu.spbstu.game;
import java.util.*;

public class Zombie {

    private Point coordinates; //Координата зомби по оси абсцисс

    private int vx; //Скорость по оси абсцисс

    private int vy; //Скорость по оси ординат

    private Point prevCrossing = new Point(-1, -1); //Абсцисса предыдущего перекрестка

    public Zombie(Map map) { //Generates zombie on a random entrance tile
        Random rand = new Random( );
        Point point = map.getEntrance().elementAt(rand.nextInt(map.getEntrance().size())).getCoordinates();
        coordinates = point;
        if (coordinates.y == 0 || coordinates.y == map.getHeight() - 1)
        {
            if (coordinates.y == 0)
            {
                vy = 1;
            }
            else
            {
                coordinates.y = (coordinates.y + 1) * 20;
                vy = -1;
            }
            vx = 0;
            coordinates.x = coordinates.x * 20 + rand.nextInt(20);
        }
        else
        {
            if (coordinates.x == 0)
            {
                vx = 1;
            }
            else
            {
                coordinates.x = (coordinates.x + 1) * 20;
                vx = -1;
            }
            vy = 0;
            coordinates.y = coordinates.y * 20 + rand.nextInt(20);
        }
    }

    public Zombie(Point coordinates, int vx, int vy) { //Генерирует зомби с нужными координатами
        this.coordinates = coordinates;
        this.vx = vx;
        this.vy = vy;
    }

    public void walk(Map map) {

        int x = (coordinates.x / 20 != map.getWidth()) ? coordinates.x / 20 : coordinates.x / 20 - 1;
        int y = (coordinates.y / 20 != map.getHeight()) ? coordinates.y / 20 : coordinates.y / 20 - 1;

        int [] directions = new int [4]; //Массив допустимых направлений движения
        Random rand = new Random();

        if (prevCrossing.x != x || prevCrossing.y != y) { //Если зомби перешел на новую плитку

            for (int j = 0; j < 4; ++j) { //Находим все возможные пути движения

                switch (j) {
                    case 0:
                        if (y != map.getHeight() - 1) {
                            if ((map.getTile(x, y + 1).getValue()) == 1) {
                                directions[0] = 1; //Если путь в данном направлении есть
                            }
                            else {
                                directions[0] = 0; //Если его нет
                            }
                        }
                        else {
                            directions[0] = 0;
                        }
                        break;

                    case 1:
                        if (x != map.getWidth() - 1) {
                            if (map.getTile(x + 1, y).getValue() == 1) {
                                directions[1] = 1;
                            }
                            else {
                                directions[1] = 0;
                            }
                        }
                        else {
                            directions[1] = 0;
                        }
                        break;

                    case 2:
                        if (y != 0) {
                            if (map.getTile(x, y - 1).getValue() == 1) {
                                directions[2] = 1;
                            }
                            else {
                                directions[2] = 0;
                            }
                        }
                        else {
                            directions[2] = 0;
                        }
                        break;

                    case 3:
                        if (x != 0) {
                            if (map.getTile(x - 1, y).getValue() == 1) {
                                directions[3] = 1;
                            }
                            else {
                                directions[3] = 0;
                            }
                        }
                        else {
                            directions[3] = 0;
                        }
                        break;
                }
            }

            if (Math.abs(vx) > Math.abs(vy)) { //Определяем, откуда пришел зомби
                directions[2 + vx] = 2;
            }
            else {
                directions[1 + vx] = 2;
            }

            int k = rand.nextInt(4); //Выбираем случайное направление движения
            int res = 4;
            for (int l = 0; Math.abs(l) < 4; l++) { //Проверяем его
                if (directions[k] == 2) { //Если отсюда пришел зомби, запоминаем направленпие и идем дальше в поисках нового и лучшего
                    res = k;
                    if (k == 3) {
                        k = 0;
                    }
                    else {
                        k++;
                    }
                }
                if (directions[k] == 1) { //Если зомби здесь еще не был, бежим навстречу приключениям
                    res = k;
                    break;
                }
                if (directions[k] == 0) { //Если сюда нельзя, продолжаем поиск маршрута
                    if (k == 3) {
                        k = 0;
                    }
                    else {
                        k++;
                    }
                }
            }

            rotate(((res % 2 == 1) ? 2 - res : 0), ((res % 2 == 0) ? 1 - res : 0)); //Поворачиваем зомби
            prevCrossing.x = x; //Запоминаем перекресток
            prevCrossing.y = y;
        }
        move(); //Двигаем зомби
    }

    public Point getCoordinates(){return this.coordinates;}

    public int getVx(){return this.vx;}

    public int getVy(){return this.vy;}

    private void move() { //Перемещает зомби по дороге и поворачивает при необходимости
        Random rand = new Random( );

        if (vx > 0) { //Перемещает
            coordinates.x += rand.nextInt(1) + 1;
            //y += rand.nextInt(3) - 1;
        }
        else if (vx < 0) {
            coordinates.x -= rand.nextInt(1) + 1;
            //y += rand.nextInt(3) - 1;
        }
        else if (vy > 0) {
            //x += rand.nextInt(3) - 1;
            coordinates.y += rand.nextInt(1) + 1;
        }
        else {
            //x += rand.nextInt(3) - 1;
            coordinates.y -= rand.nextInt(1) + 1;
        }
    }

    private void rotate(int vx, int vy) { //Поворачивает зомби в нужном нам направлении
        this.vx = vx;
        this.vy = vy;
    }

}


