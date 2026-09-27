package ExtremePoints.ItemGroups.IdenticalItemGroup;

import java.util.ArrayList;
import Geometry.Vector3.*;
import ExtremePoints.Item.*;

public class IdenticalItemGroup {
    private ArrayList<Item> items;
    
    public IdenticalItemGroup(Item i) {
        items = new ArrayList<Item>();
        items.add(i);
    }

    public boolean add_item(Item ni) {
        /* Checks if the item is identical, and adds it to the list if so */
        boolean add = true;
        Item i = items.get(0);

        if (i.getWeight() != ni.getWeight()) { //compare weights
            add = false;
        }

        if (i.getIsRotatable() != ni.getIsRotatable() || i.getIsTiltable() != ni.getIsTiltable() || i.getIsStackable() != ni.getIsStackable()) { //compare boolean vars
            add = false;
        }

        Vector3 iSize = i.getSize();
        Vector3 niSize = ni.getSize();
        if (iSize.x != niSize.x || iSize.y != niSize.y || iSize.z != niSize.z) { //compare the side lengths
            add = false;
        }

        if (add) {
            items.add(ni);
        }
        return add;
    }
}
