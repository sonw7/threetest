package com.kjw_Auto_HOB.Class;

import java.util.List;

/*
* 交点及其携带的对应断层的离散点。
* */
public class CVertexList {
    public Vertex Cvertex;//主交点
    public  List<Vertex> vertices;

    public CVertexList() {
    }

    public CVertexList(Vertex cvertex, List<Vertex> vertices) {
        Cvertex = cvertex;
        this.vertices = vertices;
    }
}
