package Geometry.Facet;

import Geometry.Vector3.Vector3;

import java.util.List;

public record Facet(List<Vector3> vertices) {
    public Vector3 getVertex(int index) {
        return vertices.get(index);
    }
}