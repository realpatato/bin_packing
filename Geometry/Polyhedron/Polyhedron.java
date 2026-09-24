package Geometry.Polyhedron;

import Geometry.Vertex.Vertex;
import java.util.ArrayList;

public class Polyhedron {
    private ArrayList<Vertex> vertices;

    public Polyhedron(Vertex... uVertexices) {
        vertices = new ArrayList<Vertex>();
        for (Vertex v : uVertexices) {
            vertices.add(v);
        }
    }

    public Vertex get(int index) {
        return vertices.get(index);
    }
}
