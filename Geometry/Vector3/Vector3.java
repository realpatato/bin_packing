package Geometry.Vector3;

import java.util.Vector;

public class Vector3 {
    public double x, y, z;

    public Vector3(double ux, double uy, double uz) {
        x = ux;
        y = uy;
        z = uz;
    }

    public double get(int index) { //allows for array-like access of variables in Vector3
        return switch (index) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    public Vector3 withNew(double newVal, int index) { //allows for "mutablility" of vector3
        Vector3 newVector3;
        switch (index) {
            case 0 -> newVector3 = new Vector3(newVal, y, z);
            case 1 -> newVector3 = new Vector3(x, newVal, z);
            case 2 -> newVector3 = new Vector3(x, y, newVal);
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        }
        return newVector3;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}