package ExtremePoints.Bin;

import java.util.ArrayList;

import ExtremePoints.ItemPairs.ItemPositionPair.*;
import Geometry.Vector3.*;
import Geometry.Facet.*;

public class Bin {
    private ArrayList<Vector3> vertices;
    private ArrayList<Facet> facets;
    private float weightCapacity, volumeCapacity;
    private ArrayList<ItemPositionPair> items;
}