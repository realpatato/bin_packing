package ExtremePoints.ItemGroups.IdenticalItemGroup;

import java.util.ArrayList;
import Geometry.Vector3.*;
import ExtremePoints.Item.*;

public class IdenticalItemGroup {
    private ArrayList<Item> items;
    private Item first;
    private double height;
    
    public IdenticalItemGroup(Item i) {
        items = new ArrayList<Item>();
        items.add(i);
        first = items.get(0);
        height = first.getSize().z;
    }

    public Item get(int index) {
        return items.get(index);
    }

    public Item getFirst() {
        return first;
    }

    public double getHeight() {
        return height;
    }

    public Vector3 getSize() {
        return first.getSize();
    }

    public boolean addItem(Item ni) {
        /* Checks if the item is identical, and adds it to the list if so */
        boolean add = true;

        if (first.getWeight() != ni.getWeight()) { //compare weights
            add = false;
        }

        if (first.getIsRotatable() != ni.getIsRotatable() || first.getIsTiltable() != ni.getIsTiltable() || first.getIsStackable() != ni.getIsStackable()) { //compare boolean vars
            add = false;
        }

        Vector3 firstSize = first.getSize();
        Vector3 niSize = ni.getSize();
        if (firstSize.x != niSize.x || firstSize.y != niSize.y || firstSize.z != niSize.z) { //compare the side lengths
            add = false;
        }

        if (add) {
            items.add(ni);
        }
        return add;
    }

    public String toString() {
        return items.toString();
    }
}
