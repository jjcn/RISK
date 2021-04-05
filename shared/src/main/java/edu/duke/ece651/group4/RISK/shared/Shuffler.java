package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.Random;

/**
 * This class does shuffling of an array.
 * Takes a Random object to construct a shuffler.
 */
public class Shuffler implements Serializable {
    protected static final long serialVersionUID = 12L;
    Random rand;

    public Shuffler() {
        this(new Random());
    }

    public Shuffler(Random rand) {
        this.rand = rand;
    }
    
    /**
     * Shuffle an array. Uses Fisher-Yates shuffle algorithm.
     * @param arr is the array to shuffle.
     */
    public void shuffle(int[] arr) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int randomNum = rand.nextInt(i + 1);
            int swap = arr[randomNum]; 
            arr[randomNum] = arr[i];
            arr[i] = swap;
        }
    }
}
