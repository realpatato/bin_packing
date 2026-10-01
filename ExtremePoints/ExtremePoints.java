package ExtremePoints;

import java.util.ArrayList;
import java.util.Comparator;

import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.IdenticalItemGroup.IdenticalItemGroup;
import ExtremePoints.ItemGroups.SimilarItemGroup.SimilarItemGroup;
import ExtremePoints.ItemOrientationGroup.ItemOrientationGroup;
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

    public ArrayList<SimilarItemGroup> rhoShuffle(ArrayList<SimilarItemGroup> usig) { //shuffles an array without destroying sorts, greedy random
        ArrayList<SimilarItemGroup> rhoSig = new ArrayList<SimilarItemGroup>();
        double p = 0.3; //represents rho, which helps determine randomness
        while (usig.size() > 0) {
            double y = Math.random();
            int i = (int)(Math.pow(y, 1/p) * usig.size());
            SimilarItemGroup sig = usig.remove(i);
            rhoSig.add(sig);
        }
        return rhoSig;
    }

    public ArrayList<SimilarItemGroup> sortItems() { //sorts the items
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

        stackable = rhoShuffle(stackable);
        nonStackable = rhoShuffle(nonStackable);

        stackable.addAll(nonStackable);
        
        return stackable;
    }

    public void tilt(ArrayList<SimilarItemGroup> usig) { //modifies the list to make all objects in groups share the same height
        for (SimilarItemGroup sig : usig) {
            double height = sig.getHeight();
            for (IdenticalItemGroup iig : sig.getIdenticalItemGroups()) {
                if (iig.getHeight() != height) {
                    if (iig.getOrientation().x == height) {
                        double temp = iig.getOrientation().x;
                        iig.getOrientation().x = iig.getOrientation().z;
                        iig.getOrientation().z = temp; 
                    } else if (iig.getOrientation().y == height) {
                        double temp = iig.getOrientation().y;
                        iig.getOrientation().y = iig.getOrientation().z;
                        iig.getOrientation().z = temp; 
                    }
                }
            }
        }
    }

    public ArrayList<ItemOrientationGroup> flatten(ArrayList<SimilarItemGroup> usig) {
        ArrayList<ItemOrientationGroup> items = new ArrayList<ItemOrientationGroup>();
        for (SimilarItemGroup sig : usig) {
            for (IdenticalItemGroup iig : sig.getIdenticalItemGroups()) {
                for (Item i : iig.getItems()) {
                    ItemOrientationGroup iog = new ItemOrientationGroup(i, iig.getOrientation());
                    items.add(iog);
                }
            }
        }
        return items;
    }
}