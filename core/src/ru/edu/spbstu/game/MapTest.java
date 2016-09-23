package ru.edu.spbstu.game;
import org.junit.Test;

public class MapTest {
    @Test
    public void generate() throws Exception {
        Map map = new Map();
        map.generate();
        for(int[] i : map.mapArray) {
            for(int j : i) {
                System.out.print(j + " ");
            }
            System.out.println();

        }
    }
}
