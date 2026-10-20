package Geometry.Vector3;

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

    public boolean equals(Vector3 v3) {
        return ((x == v3.x) && (y == v3.y) && (z == v3.z));
    }

    public Vector3 add(Vector3 ov) {
        return new Vector3(x + ov.x, y + ov.y, z - ov.z);
    }

    public Vector3 subtract(Vector3 ov) {
        return new Vector3(x - ov.x, y - ov.y, z - ov.z);
    }

    public Vector3 multiply(double n) {
        return new Vector3(x * n, y * n, z * n);
    }

    public Vector3 divide(double n) {
        return new Vector3(x / n, y / n, z / n);
    }

    public double dot(Vector3 ov) {
        return x * ov.x + y * ov.y + z * ov.z;
    }

    public Vector3 cross(Vector3 ov) {
        double nx = (y * ov.z) - (z * ov.y);
        double ny = (z * ov.x) - (x * ov.z);
        double nz = (x * ov.y) - (y * ov.x);
        return new Vector3(nx, ny, nz);
    }

    public Vector3 normalized() {
        double magnitude = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        return this.divide(magnitude);
    }
}