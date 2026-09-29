package ExtremePoints;

import java.util.ArrayList;

import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.SimilarItemGroup.SimilarItemGroup;
import ExtremePoints.Bin.*;

public class ExtremePoints {
    ArrayList<Bin> bins;
    ArrayList<Item> items;

    public ArrayList<SimilarItemGroup> getItemGroups() {
        ArrayList<SimilarItemGroup> groups = new ArrayList<SimilarItemGroup>();
        groups.add(new SimilarItemGroup(items.get(0)));
        for (int i = 1; i < items.size(); i++) {
            boolean add = true;
            for (SimilarItemGroup g : groups) {
                if (g.addItem(items.get(i))) {
                    add = false; //don't add a new similar item group
                }
            }
            if (add) {
                groups.add(new SimilarItemGroup(items.get(i)));
            }
        }
        return groups;
    }

    public ArrayList<Item> sortItems() {
        ArrayList<Item> sortedItems = new ArrayList<Item>();
        ArrayList<SimilarItemGroup> itemGroups = getItemGroups();
        return sortedItems;
    }
}