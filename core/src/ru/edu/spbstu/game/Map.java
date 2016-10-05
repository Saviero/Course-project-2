package ru.edu.spbstu.game;
import java.util.*;
import java.lang.*;

public class Map {

    public int[][] mapArray = new int[30][40];  //array of tiles
    public int width = 40;
    public int height = 30;
    public Zombie[] zombies;
    public int amountOfZombies;

    public Map() {
    }

    public Map(int width, int height, int tilewidth) {
        mapArray = new int[height/tilewidth][width/tilewidth]; //creating an empty map with custom size
        this.width = width/tilewidth;
        this.height = height/tilewidth;
    }

    private int cooldown = 0;

    private int shift(int i)
    {
        Random rand = new Random();
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
        --cooldown;
        return i;
    }


    public void generate() {
        Random rand = new Random();
        int i = 1 + rand.nextInt(2);
        int j = 0;
        int shifti;

        while (i < height- 2) {
            while (j < width) {
                mapArray[i][j] = 1;
                shifti = shift(i);
                if (shifti != i) {
                    i = shifti;
                }
                else {
                    ++j;
                }
            }
            j= 0;
            i += 3 + rand.nextInt(4);
        }

        cooldown = 0;
        i = 1 + rand.nextInt(2);
        j = 0;
        while (i < width - 2) {
            while (j < height) {
                if (mapArray[j][i] == 1) {
                    mapArray[j][i] = 2;
                }
                else {
                    mapArray[j][i] = 1;
                }
                shifti = shift(i);
                if (shifti != i) {
                    i = shifti;
                }
                else {
                    ++j;
                }
            }
            i += 3 + rand.nextInt(4);
            j = 0;
        }
    }

    public void addZombies(int n) {
        amountOfZombies = n;
        zombies = new Zombie[n];
        for (int i = 0; i < n; ++i) {
            int x, y, vx, vy;
            Random rand = new Random(13);
            if (rand.nextBoolean()) { //Определяет, у вертикальной или горизонтальной границы появится зомби
                if (rand.nextBoolean()) { //Определяет, с левой или правой вертикальной границы появится зомби, и задает соотв. направление скорости
                    x = 0;
                    vx = 1;
                    vy = 0;
                }
                else {
                    x = width - 1;
                    vx = -1;
                    vy = 0;
                }
                y = rand.nextInt(height); //Выбирает случайную клетку на границе
                while (mapArray[y][x] == 0) //Ищет ближайшую снизу дорогу
                    if (y++ == height)
                        y = 0;
                y = y * 20 + rand.nextInt(20);
            }
            else {
                if (rand.nextBoolean()) { //Определяет, с верхней или нижней границы появится зомби, и задает соотв. направление скорости
                    y = 0;
                    vx = 0;
                    vy = 1;
                }
                else {
                    y = height - 1;
                    vx = 0;
                    vy = -1;
                }
                x = rand.nextInt(width); //Выбирает случайную клетку на границе
                while (mapArray[y][x] != 0) //Ищет ближайшую справа дорогу
                    if (x++ == width)
                        x = 0;
                x = x * 20 + rand.nextInt(20);
            }
            zombies[i] = new Zombie(x, y, vx, vy);
        }
    }

    public void moveZombies() {

        int x, y; //Координаты плитки
        int [] directions = new int [4]; //Массив допустимых направлений движения
        Random rand = new Random();

        for (int i = 0; i < amountOfZombies; ++i) {

            x = zombies[i].getX() / 20; //Координата плитки по оси абсцисс
            y = zombies[i].getY() / 20; //Координата плитки по оси ординат

            if (zombies[i].getPrevCrossingX() != x || zombies[i].getPrevCrossingY() != y) { //Если зомби перешел на новую плитку

                for (int j = 0; j < 4; ++j) { //Находим все возможные пути движения
                    switch (j) {
                        case 0:
                            if (mapArray[x][y + 1] != 0) {
                                directions[j] = 1; //Если путь в данном направлении есть
                            }
                            else {
                                directions[j] = 0; //Если его нет
                            }
                            break;

                        case 1:
                            if (mapArray[x + 1][y] != 0) {
                                directions[j] = 1;
                            }
                            else {
                                directions[j] = 0;
                            }
                            break;

                        case 2:
                            if (mapArray[x][y - 1] != 0) {
                                directions[j] = 1;
                            }
                            else {
                                directions[j] = 0;
                            }
                            break;

                        case 3:
                            if (mapArray[x - 1][y] != 0) {
                                directions[j] = 1;
                            }
                            else {
                                directions[j] = 0;
                            }
                            break;
                    }
                }

                if (Math.abs(zombies[i].getVx()) > Math.abs(zombies[i].getVy())) { //Определяем, откуда пришел зомби
                    directions[2 + zombies[i].getVx()] = 2;
                }
                else {
                    directions[1 + zombies[i].getVx()] = 2;
                }

                int k = rand.nextInt(4); //Выбираем случайное направление движения
                int res = 4;
                for (int l = 0; l < 4; ++l) { //Проверяем его
                    if (directions[k] == 2) { //Если отсюда пришел зомби, запоминаем направленпие и идем дальше в поисках нового и лучшего
                        res = k;
                        k++;
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

                zombies[i].rotate(((res % 2 == 1) ? 2 - res : 0), ((res % 2 == 0) ? 1 - res : 0)); //Поворачиваем зомби
                zombies[i].setPrevCrossing(x, y); //Запоминаем перекресток
            }
            zombies[i].move(); //Двигаем зомби
        }
    }
}
