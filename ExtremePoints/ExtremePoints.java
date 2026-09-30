package ExtremePoints;

import java.util.ArrayList;
import java.util.Comparator;

import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.IdenticalItemGroup.IdenticalItemGroup;
import ExtremePoints.ItemGroups.SimilarItemGroup.SimilarItemGroup;
import ExtremePoints.Bin.*;

public class ExtremePoints {
    ArrayList<Bin> bins;
    ArrayList<Item> items;

    public ExtremePoints() {
        bins = new ArrayList<Bin>();
        items = new ArrayList<Item>();
    }

    public void addItems(ArrayList<Item> ui) {
        for (Item i : ui) {
            items.add(i);
        }
    }

    public void addItems(Item... ui) {
        for (Item i : ui) {
            items.add(i);
        }
    }

    public ArrayList<SimilarItemGroup> getItemGroups() { //simply groups the items up for sorting
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

    public void highestVolumeSort(ArrayList<SimilarItemGroup> usig) { //sorts the group by volume
        usig.sort(new Comparator<SimilarItemGroup>() { //makes the sort method sort based on the greatest volume of the group
                @Override 
                public int compare(SimilarItemGroup g1, SimilarItemGroup g2) {
                    double v1 = g1.getHighestVolume();
                    double v2 = g2.getHighestVolume();

                    return Double.compare(v2, v1); //ensure descending order (larger item first)
                }
            }
        );
    }

    public ArrayList<Item> sortItems() { //sorts the items
        ArrayList<Item> sortedItems = new ArrayList<Item>();
        ArrayList<SimilarItemGroup> itemGroups = getItemGroups();

        //partitioning the groups based on stackablity
        ArrayList<SimilarItemGroup> stackable = new ArrayList<SimilarItemGroup>();
        ArrayList<SimilarItemGroup> nonStackable = new ArrayList<SimilarItemGroup>();
        for (SimilarItemGroup g : itemGroups) {
            if (g.getFirst().getFirst().getIsStackable()) { //if the item group is stackable
                stackable.add(g);
            } else {
                nonStackable.add(g);
            }
        }

        highestVolumeSort(stackable);
        highestVolumeSort(nonStackable);

        stackable.addAll(nonStackable);
        itemGroups = stackable;

        System.out.println(itemGroups);
        return sortedItems;
    }
}