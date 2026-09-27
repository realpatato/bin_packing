package ExtremePoints.Item;

import Geometry.Vector3.*;

public class Item {
    private Vector3 size;
    private float weight;
    private boolean isRotatable, isTiltable, isStackable;

    public Vector3 getSize() {
        return size;
    }

    public float getWeight() {
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
}