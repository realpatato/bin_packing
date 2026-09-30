package ExtremePoints.Item;

import Geometry.Vector3.*;

public class Item {
    private Vector3 size; //length, width, height
    private double weight;
    private boolean isRotatable, isTiltable, isStackable;

    public Item(double length, double width, double height, double uweight, boolean uisRotatable, boolean uisTiltable, boolean uisStackble) {
        size = new Vector3(length, width, height);
        weight = uweight;
        isRotatable = uisRotatable; //assumes all bools are true
        isTiltable = uisTiltable;
        isStackable = uisStackble;
    }

    public Item(double length, double width, double height) {
        this(length, width, height, 0.0, true, true, true);
    }

    public Vector3 getSize() {
        return size;
    }

    public double getWeight() {
        return weight;
    }

    public boolean getIsRotatable() {
        return isRotatable;
    }

    public boolean getIsTiltable() {
        return isTiltable;
    }

    public boolean getIsStackable() {
        return isStackable;
    }

    public double getVolume() {
        return (size.x * size.y * size.z);
    }

    public String toString() { //for debug
        return " | Item Object - Size = (" + size.x + ", " + size.y + ", " + size.z + ") - Stackable? " + getIsStackable() + " | ";
    }
}