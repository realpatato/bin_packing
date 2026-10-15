package ExtremePoints;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

import utils.utils;

import Geometry.Vector3.*;

import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.IdenticalItemGroup.IdenticalItemGroup;
import ExtremePoints.ItemGroups.SimilarItemGroup.SimilarItemGroup;
import ExtremePoints.ItemPairs.ItemPositionPair.*;
import ExtremePoints.ItemPairs.ItemOrientationPair.*;
import ExtremePoints.Bin.*;

public class ExtremePoints {
    ArrayList<Bin> bins;
    ArrayList<Item> items;

    int[][] nonProjections = {{2, 3}, {1, 3}, {1, 2}};

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

    /* Sort, tilt, and flattening the list of items */
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
        ArrayList<SimilarItemGroup> rhoSig = new ArrayList<>();
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
        ArrayList<SimilarItemGroup> stackable = new ArrayList<>();
        ArrayList<SimilarItemGroup> nonStackable = new ArrayList<>();
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

    public ArrayList<ItemOrientationPair> flatten(ArrayList<SimilarItemGroup> usig) {
        ArrayList<ItemOrientationPair> items = new ArrayList<>();
        ArrayList<Item> included = new ArrayList<>();
        for (SimilarItemGroup sig : usig) {
            for (IdenticalItemGroup iig : sig.getIdenticalItemGroups()) {
                for (Item i : iig.getItems()) {
                    if (included.contains(i)) {
                        int index = included.indexOf(i);
                        items.get(index).vector3s().add(iig.getOrientation());
                    } else {
                        included.add(i);
                        ArrayList<Vector3> orientation = new ArrayList<>();
                        orientation.add(iig.getOrientation());
                        ItemOrientationPair iop = new ItemOrientationPair(i, orientation);
                        items.add(iop);
                    }
                }
            }
        }
        return items;
    }

    public ArrayList<ItemOrientationPair> getOrderedItemList(ArrayList<Item> items) {
        ArrayList<SimilarItemGroup> sig = sortItems();
        tilt(sig);
        return flatten(sig);
    }

    /* Extreme Point Generation */
    public double getSizeInAxis(ItemPositionPair ip, int axis) {
        return ip.item().getSize().get(axis - 1);
    }

    public double getPosInAxis(ItemPositionPair ip, int axis) {
        return ip.vector3().get(axis - 1);
    }

    public double getEndPos(ItemPositionPair ip, int axis) {
        return getPosInAxis(ip, axis) + getSizeInAxis(ip, axis);
    }

    public void sortLoadedItems(ArrayList<ItemPositionPair> items, int axis) { //sort items based on the end positions, in descending order
        items.sort(new Comparator<ItemPositionPair>() {
                @Override 
                public int compare(ItemPositionPair a, ItemPositionPair b) {
                    double aEndPos = getEndPos(a, axis);
                    double bEndPos = getEndPos(b, axis);

                    return Double.compare(bEndPos, aEndPos);
                }
            }
        );
    }

    /* Projection */
    //i feel that having a bunch of methods just for the if statements will make debugging easier in the long run
    //its a bit ugly, and takes more lines of code, but it makes programming go more smoothly
    public boolean noEffectFilter(ItemPositionPair ip, Vector3 sp, int pa, int np1, int np2) {
        return ((getPosInAxis(ip, pa) >= sp.get(pa - 1)) || (getEndPos(ip, np2) <= sp.get(np1 - 1)) || (getEndPos(ip, np2) <= sp.get(np2 - 1)));
    }

    public boolean projectionBlockedFilter(ItemPositionPair bip, ItemPositionPair ip, Vector3 sp, int np1, int np2) {
        return (((getPosInAxis(ip, np1) >= getPosInAxis(bip, np1)) || (sp.get(np1 - 1) >= getPosInAxis(ip, np1))) && ((getPosInAxis(ip, np2) >= getPosInAxis(bip, np2)) || (sp.get(np2 - 1) >= getPosInAxis(ip, np2))));
    }

    public boolean addExtremePointCheck(ItemPositionPair ip, Vector3 sp, int pa) {
        return ((getEndPos(ip, pa) <= sp.get(pa - 1)) && ((pa != 3) || ip.item().getIsStackable()));
    }

    public boolean directHitCheck(ItemPositionPair ip, Vector3 sp, int np1, int np2) {
        return ((sp.get(np1 - 1) >= getPosInAxis(ip, np1)) && (sp.get(np2 - 1) >= getPosInAxis(ip, np2)));
    }

    public ArrayList<Vector3> projection(Vector3 startPoint, int projectionAxis, ArrayList<ItemPositionPair> loadedItems) {
        Vector3 extremePoint = startPoint;
        int nonProjection1 = nonProjections[projectionAxis - 1][0];
        int nonProjection2 = nonProjections[projectionAxis - 1][1];
        ArrayList<ItemPositionPair> blockingItems = new ArrayList<>();
        ArrayList<Vector3> extremePoints = new ArrayList<>();
        //sort the loaded items (how they are sorted defined in sort method)
        ArrayList<ItemPositionPair> sortedLoadedItems = new ArrayList<>(loadedItems);
        sortLoadedItems(sortedLoadedItems, projectionAxis);
        //label so that the blocked item loop continues the outer and not more blocked items
        outer:
        for (ItemPositionPair ip : sortedLoadedItems) {
            //filters out items that don't affect the projection
            if (noEffectFilter(ip, startPoint, projectionAxis, nonProjection1, nonProjection2)) {
                continue;
            }
            //filter out items that are blocked by others
            for (ItemPositionPair bip : blockingItems) {
                if (projectionBlockedFilter(bip, ip, startPoint, nonProjection1, nonProjection2)) {
                    continue outer;
                }
            }
            //adding new extreme points
            if (addExtremePointCheck(ip, startPoint, projectionAxis)) {
                extremePoint = extremePoint.withNew(getEndPos(ip, projectionAxis), projectionAxis);
                utils.union(extremePoints, extremePoint);
            }
            //stop if the projection directly hits the item
            if (directHitCheck(ip, startPoint, nonProjection1, nonProjection2)) {
                return extremePoints;
            }
            utils.union(blockingItems, ip);
        }
        //project to the wall of the bin if not blocked by any other items
        extremePoint = extremePoint.withNew(0, projectionAxis);
        extremePoints.add(extremePoint);
        return extremePoints;
    }

    /* Extreme Point Generation */
    public ArrayList<Vector3> genExtremePoints(ArrayList<ItemPositionPair> loadedItems, ItemPositionPair item) {
        ArrayList<Vector3> extremePoints = new ArrayList<>();
        Vector3 itemSize = item.item().getSize();
        int[] directions = {1, 2, 3};
        for (int j : directions) {
            if (!item.item().getIsStackable() && j == 3) {
                continue;
            }
            for (int d : directions) {
                if (d == j) {
                    continue;
                }
                Vector3 startPoint = item.vector3();
                startPoint = startPoint.withNew(startPoint.get(j) + itemSize.get(j), j);
                startPoint = startPoint.withNew(startPoint.get(d) + itemSize.get(d), d);
                ArrayList<Vector3> newPoints = projection(startPoint, d, loadedItems);
                utils.union(extremePoints, newPoints);
            }
        }
        if (item.item().getIsStackable()) {
            utils.union(extremePoints, item.vector3().withNew(itemSize.z + item.vector3().z, 3));
        }
        return extremePoints;
    }

    /* Insertion Heuristic (THE BIN PACKING PART!!) */
    public void pack() {
        ArrayList<Vector3> extremePoints = new ArrayList<>();
        extremePoints.add(new Vector3(0, 0, 0));
    }
}