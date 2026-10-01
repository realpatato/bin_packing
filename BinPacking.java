import Visuals.Visuals;
import ExtremePoints.*;
import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.SimilarItemGroup.SimilarItemGroup;

import java.util.ArrayList;

class BinPacking {
    public static void main(String[] args) {
        Visuals v = new Visuals(0.8);

        ExtremePoints ep = new ExtremePoints();
        ArrayList<Item> items = new ArrayList<Item>();
        for (int i = 1; i < 4; i++) {
            for (int k = 0; k < 5 - i; k++) {
                if (k % 2 == 0) {
                    items.add(new Item(i * 10, i * 5, i * 7.5));
                } else {
                    items.add(new Item(i * 10, i * 5, i * 7.5, 0, true, true, false));
                }
            }
        }

        ep.addItems(items);
        ArrayList<SimilarItemGroup> sortedItemGroups = ep.sortItems();
        System.out.println(sortedItemGroups);
        ep.tilt(sortedItemGroups);
        System.out.println(sortedItemGroups);

        v.visualize();
    }
}