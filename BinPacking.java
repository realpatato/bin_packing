import Visuals.Visuals;

import Geometry.Facet.*;
import Geometry.Vector3.*;

import utils.utils;

import ExtremePoints.*;
import ExtremePoints.Item.*;
import ExtremePoints.ItemGroups.SimilarItemGroup.SimilarItemGroup;
import ExtremePoints.ItemPairs.ItemOrientationPair.*;

import java.util.ArrayList;

class BinPacking {
    public static void main(String[] args) {
        Visuals v = new Visuals(0.8);

        ExtremePoints ep = new ExtremePoints();
        ArrayList<Item> items = new ArrayList<Item>();
        for (int i = 1; i < 10; i++) {
            for (int k = 0; k < 20 - i; k++) {
                if (k % 2 == 0) {
                    items.add(new Item(i * 10, i * 5, i * 7.5));
                    items.add(new Item(i * 7.5, i * 10, i * 10));
                } else {
                    items.add(new Item(i * 10, i * 5, i * 5, 0, true, true, false));
                } 
            }
        }

        //really messy, will probably move to reading tables or something similar
        ArrayList<Facet> facets = new ArrayList<>();
        ArrayList<Vector3> vertices = new ArrayList<>();
        
        vertices.add(new Vector3(0, 0, 0));
        vertices.add(new Vector3(100, 0, 0));
        vertices.add(new Vector3(100, 0, 100));
        vertices.add(new Vector3(0, 0, 100));
        vertices.add(new Vector3(0, 100, 0));
        vertices.add(new Vector3(100, 100, 0));
        vertices.add(new Vector3(100, 100, 100));
        vertices.add(new Vector3(0, 100, 100));

        facets.add(new Facet(utils.createFromArrayList(vertices, 0, 1, 2, 3))); //bottom
        facets.add(new Facet(utils.createFromArrayList(vertices, 0, 1, 5, 4))); //left
        facets.add(new Facet(utils.createFromArrayList(vertices, 0, 3, 7, 4))); //front
        facets.add(new Facet(utils.createFromArrayList(vertices, 2, 1, 5, 6))); //right
        facets.add(new Facet(utils.createFromArrayList(vertices, 2, 3, 7, 6))); //back

        ep.addItems(items);
        ArrayList<SimilarItemGroup> sortedItemGroups = ep.sortItems();
        //System.out.println(sortedItemGroups);
        ep.tilt(sortedItemGroups);
        //System.out.println(sortedItemGroups);
        ArrayList<ItemOrientationPair> flatSortedItemGroups = ep.flatten(sortedItemGroups);
        System.out.println(flatSortedItemGroups);

        v.visualize();
    }
}