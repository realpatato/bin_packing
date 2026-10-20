package ExtremePoints;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

import utils.utils;

import Geometry.Vector3.*;
import Geometry.Facet.*;

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
        bins = new ArrayList<>();
        items = new ArrayList<>();
    }

    public void createBin(ArrayList<Facet> uf) {
        ArrayList<Vector3> vertices = new ArrayList<>();
        for (Facet f : uf) {
            utils.union(vertices, f.vertices());
        }
        bins.add(new Bin(10000, 10000, vertices, uf));
    }

    public void addItems(ArrayList<Item> i) {
        items.addAll(i);
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
                        iig.getOrientation().x = iig.getOrientation().y;
                        iig.getOrientation().y = temp; 
                    } else if (iig.getOrientation().z == height) {
                        double temp = iig.getOrientation().z;
                        iig.getOrientation().z = iig.getOrientation().y;
                        iig.getOrientation().y = temp; 
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

    public ArrayList<ItemOrientationPair> getOrderedItemList() {
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
            if (!item.item().getIsStackable() && j == 2) {
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
            utils.union(extremePoints, item.vector3().withNew(itemSize.y + item.vector3().y, 2));
        }
        return extremePoints;
    }

    /* Insertion Heuristic (THE BIN PACKING PART!!) */
    public void sortExtremePoints(ArrayList<Vector3> ep) {
        ep.sort(new Comparator<Vector3>() {
                @Override 
                public int compare(Vector3 v1, Vector3 v2) {
                    int cmp = Double.compare(v1.y, v2.y);
                    if (cmp != 0) return cmp;
                    cmp = Double.compare(v1.z, v2.z);
                    if (cmp != 0) return cmp;
                    return Double.compare(v1.x, v2.x);
                }
            }
        );
    }

    public boolean flat(Vector3 normal) {
        int zeroes = 0;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(normal.get(i)) <= 1e-6) {
                zeroes++;
            }
        }
        return (zeroes >= 2);
    }

    public boolean flatCheck(Bin bin, Vector3 size, Vector3 pos) {
        Vector3 boundingBox = bin.getBoundingBox();
        for (int i = 0; i < 3; i++) {
            if (pos.get(i) + size.get(i) > boundingBox.get(i)) {
                return true;
            }
        }
        return false;
    }

    public boolean slantCheck(Bin bin, Facet f, Vector3 normal, Vector3 size, Vector3 pos) {
        double planeConst = normal.dot(f.getVertex(0));

        double farX = normal.x < 0 ? pos.x + size.x : pos.x;
        double farY = normal.y < 0 ? pos.y + size.y : pos.y;
        double farZ = normal.z < 0 ? pos.z + size.z : pos.z;

        Vector3 far = new Vector3(farX, farY, farZ);
        return normal.dot(far) < planeConst;
    }

    public boolean excedence(Bin bin, Vector3 size, Vector3 pos) {
        if (flatCheck(bin, size, pos)) {
            return true;
        }
        for (int i = 0; i < bin.getFacets().size(); i++) {
            Vector3 normal = bin.getFacetNormals().get(i);
            boolean exceeds = false;
            if (flat(normal)) {
                continue;
            } else {
                exceeds = slantCheck(bin, bin.getFacets().get(i), normal, size, pos);
            }
            if (exceeds) {
                return true;
            }
        }
        return false;
    }

    public boolean collision(Vector3 size, Vector3 pos, ArrayList<ItemPositionPair> load) {
        for (ItemPositionPair ipp : load) {
            Vector3 itemPos = ipp.vector3();
            Vector3 itemSize = ipp.item().getSize();
            boolean overlapOnAll = true;
            for (int d = 0; d < 3; d++) {
                if (!(itemPos.get(d) < pos.get(d) + size.get(d) && pos.get(d) < itemPos.get(d) + itemSize.get(d))) {
                    overlapOnAll = false;
                    break;
                }
            }
            if (overlapOnAll) {
                return true;
            }
        }
        return false;
    }

    public boolean nonFloatingStackable(Vector3 size, Vector3 pos, ArrayList<ItemPositionPair> load, Bin b) {
        ArrayList<ItemPositionPair> tempLoad = new ArrayList<>(load);
        Item floorItem = new Item(b.getBoundingBox().x, 0, b.getBoundingBox().z); //creates the floor of the box
        ItemPositionPair floor = new ItemPositionPair(floorItem, new Vector3(0, 0, 0));
        tempLoad.add(floor);
        return false;
    }

    public boolean canPlace(Vector3 s, Vector3 p, Bin b) {
        //goes in order of what will typically require the least amount of computational power
        //this is so that ideally, redundant work is avoided
        if (excedence(b, s, p)) return false;
        if (collision(s, p, b.getItems())) return false;
        if (nonFloatingStackable(s, p, b.getItems(), b)) return false;
        return true;
    }

    public void pack() {
        ArrayList<Vector3> extremePoints = new ArrayList<>();
        extremePoints.add(new Vector3(0, 0, 0));
        ArrayList<ItemOrientationPair> orderedItemList = getOrderedItemList();
        Bin bin = bins.get(0);
        for (ItemOrientationPair iop : orderedItemList) {
            int epi = 0; //extreme point index
            boolean loaded = false;
            while (epi != extremePoints.size() && !loaded) {
                Vector3 ep = extremePoints.get(epi);
                for (Vector3 orientation : iop.vector3s()) {
                    canPlace(orientation, ep, bin);
                }
                epi++;
            }
        }
    }
}