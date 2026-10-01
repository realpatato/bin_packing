package Geometry.Vector3;

public class Vector3 {
    public double x, y, z;

    public Vector3(double ux, double uy, double uz) {
        x = ux;
        y = uy;
        z = uz;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}