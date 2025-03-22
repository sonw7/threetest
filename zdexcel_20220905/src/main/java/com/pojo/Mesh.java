package com.pojo;

import java.util.List;

public class Mesh {
    public int id;
    public String name;
    public List<Triangle> Triangles;
    public List<Line> Lines;
    public List<Point> Points;
    public List<Edge> Edge;

    public Mesh() {
    }

    public Mesh(List<Triangle> triangles, List<Point> points) {
        Triangles = triangles;
        Points = points;
    }

    public Mesh(List<Triangle> triangles, List<Line> lines, List<Point> points) {
        Triangles = triangles;
        Lines = lines;
        Points = points;
    }
}
