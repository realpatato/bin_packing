package ExtremePoints.ItemGroups.SimilarItemGroup;

import java.util.ArrayList;

import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.IdenticalItemGroup.*;

public class SimilarItemGroup {
    private ArrayList<IdenticalItemGroup> itemGroups;

    public SimilarItemGroup(Item i) {
        itemGroups = new ArrayList<IdenticalItemGroup>();
        itemGroups.add(new IdenticalItemGroup(i));
    }
}
