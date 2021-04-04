package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

public class ShufflerTest {

    @Test
    public void testShuffle() { // a visual test
        int arr[] = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        Shuffler shuffler = new Shuffler();
        shuffler.shuffle(arr);

        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (int i : arr) {
            sb.append(delim + i);
            delim = ", ";
        }
        System.out.println(sb);
    }

    @Test
    public void testShuffleWithSeed() {
        int arr[] = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        int arr1[] = Arrays.copyOf(arr, arr.length);
        Shuffler shuffler = new Shuffler(new Random(0));
        Shuffler shuffler1 = new Shuffler(new Random(0));
        shuffler.shuffle(arr);
        shuffler1.shuffle(arr1);

        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (int i : arr) {
            sb.append(delim + i);
            delim = ", ";
        }
        System.out.println(sb);

        assertArrayEquals(arr, arr1);
    }

}
