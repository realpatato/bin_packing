package utils;

import java.util.ArrayList;

public class utils {
    /* Methods for doing mathematical unions */
    /**
     * Given two arraylists, both of type <T> combine the two while skipping duplicate elements
     */
    public static <T> void union(ArrayList<T> a, ArrayList<T> b) { 
        for (T bo : b) {
            if (!a.contains(bo)) {
                a.add(bo);
            }
        }
    }

    /**
     * Given an arraylist of type <T> and an object of type T, add the element if it is not already included
     * (the object and the arraylist must have the same type)
     */
    public static <T> void union(ArrayList<T> a, T b) {
        if (!a.contains(b)) {
            a.add(b);
        }
    }
}