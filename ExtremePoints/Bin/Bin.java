package ExtremePoints.Bin;

import java.util.ArrayList;

import ExtremePoints.ItemPairs.ItemPositionPair.*;
import Geometry.Vector3.*;
import Geometry.Facet.*;

public class Bin {
    private ArrayList<Vector3> vertices;
    private ArrayList<Facet> facets;
    private double weightCapacity, volumeCapacity;
    private ArrayList<ItemPositionPair> items;

    /* Not in the original document, but are used for efficiency */
    private Vector3 boundingBox;
    private ArrayList<Vector3> facetNormals; 

    public Vector3 center() {
        int ct = 0;
        Vector3 center = new Vector3(0, 0, 0);
        for (Vector3 v : vertices) {
            ct++;
            center.add(v);
        }
        return center.divide((double)ct);
    }

    public Bin(double wc, double vc, ArrayList<Vector3> verts, ArrayList<Facet> fcts) {
        weightCapacity = wc;
        volumeCapacity = vc;
        vertices = verts;
        facets = fcts;

        boundingBox = genBoundingBox();
        facetNormals = genFacetNormals();
    }

    public Vector3 direct(Facet f, Vector3 n) { //checks if the normal is facing inward, if not, it swaps it
        if (n.dot(f.getVertex(0).subtract(center())) < 0) {
            return n.multiply(-1);
        }
        return n;
    }

    public ArrayList<Vector3> genFacetNormals() {
        ArrayList<Vector3> fn = new ArrayList<>();
        for (Facet f : facets) {
            Vector3 edge1 = f.getVertex(1).subtract(f.getVertex(0));
            Vector3 edge2 = f.getVertex(2).subtract(f.getVertex(0));
            Vector3 normal = edge1.cross(edge2);
            normal = direct(f, normal);
            normal = normal.normalized();
            fn.add(normal);
        }
        return fn;
    }

    public ArrayList<Facet> getFacets() {
        return facets;
    }

    public ArrayList<Vector3> getFacetNormals() {
        return facetNormals;
    }

    public double maxInDirection(int d) {
        double max = vertices.get(0).get(d);
        for (Vector3 v : vertices) {
            double cur = v.get(d);
            if (cur > max) {
                max = cur;
            }
        }
        return max;
    }

    public Vector3 genBoundingBox() {
        return new Vector3(maxInDirection(0), maxInDirection(1), maxInDirection(2));
    }

    public Vector3 getBoundingBox() {
        return boundingBox;
    }

    public ArrayList<ItemPositionPair> getItems() {
        return items;
    }
}