package com.kjw_Auto_HOB.Class;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class GeoStructure {
    public GeoStructure(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;
    public List<Double> vertices;

    public List<Double> getVertices() {
        return vertices;
    }

    public void setVertices(List<Double> vertices) {
        this.vertices = vertices;
    }

    public List<Double> getRtvertices() {
        return rtvertices;
    }

    public void setRtvertices(List<Double> rtvertices) {
        this.rtvertices = rtvertices;
    }

    public List<Integer> getNormals() {
        return normals;
    }

    public void setNormals(List<Integer> normals) {
        this.normals = normals;
    }

    public  List<Double> rtvertices;
    public  List<Integer> normals;

    public List<Integer> getIndices() {
        return indices;
    }

    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

    public List<Integer> indices;
    public List<Double> uvs;
    public String texturePath;


    public JSONObject toJSON(){
        JSONObject job = new JSONObject();
        job.put("name",name);
        job.put("vertices",vertices);
        job.put("normals",normals);
        job.put("indices",indices);
        job.put("uvs",uvs);
        job.put("texturePath",texturePath);

        return job;
    }
}
