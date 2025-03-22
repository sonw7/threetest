package com.pojo;

public class Triangle {
    public int id;
    public int PindexA;
    public int PindexB;
    public int PindexC;

    public Triangle() {
    }

    public Triangle(int pindexA, int pindexB, int pindexC) {
        PindexA = pindexA;
        PindexB = pindexB;
        PindexC = pindexC;
    }

    public Triangle(int id, int pindexA, int pindexB, int pindexC) {
        this.id = id;
        PindexA = pindexA;
        PindexB = pindexB;
        PindexC = pindexC;
    }
}
