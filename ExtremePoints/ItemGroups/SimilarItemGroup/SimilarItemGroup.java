package ExtremePoints.ItemGroups.SimilarItemGroup;

import java.util.ArrayList;

import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.IdenticalItemGroup.*;

public class SimilarItemGroup {
    private ArrayList<IdenticalItemGroup> itemGroups;
    private IdenticalItemGroup first;

    public SimilarItemGroup(Item i) {
        itemGroups = new ArrayList<IdenticalItemGroup>();
        itemGroups.add(new IdenticalItemGroup(i));
        first = itemGroups.get(0);
    }

    public double getHighestVolume() {
        double max = 0;
        for (IdenticalItemGroup g : itemGroups) {
            double volume = g.getFirst().getVolume();
            if (volume > max) {
                max = volume;
            }
        }
        return max;
    }

    public ArrayList<IdenticalItemGroup> getIdenticalItemGroups() {
        return itemGroups;
    }

    public IdenticalItemGroup getFirst() {
        return first;
    }

    public boolean addItem(Item ni) {
        boolean add = false;

        if (ni.getIsStackable() == itemGroups.get(0).getFirst().getIsStackable()) { //check if stability is the same

            for (IdenticalItemGroup ig : itemGroups) {
                if (ig.addItem(ni)) { //check if item group already exists
                    return true; //if so, stop and return true, cause the item has been added
                }
            }

            IdenticalItemGroup ig = itemGroups.get(0); //only need to check the first group, since all item groups are similar
            double h = ig.get(0).getSize().z; //height for comparisons

            if (ni.getSize().z == h) { //check if the height is the same
                add = true;
            }

            if (ni.getIsTiltable() && ig.get(0).getIsTiltable()) {
                if (ni.getSize().x == h || ni.getSize().y == h) {
                    add = true;
                }
            }
        }

        if (add) {
            itemGroups.add(new IdenticalItemGroup(ni));
        }

        return add;
    }

    public String toString() { //for debug
        return itemGroups.toString();
    }
}