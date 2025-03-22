package com.zidong.drillsdate;

import java.util.ArrayList;
import java.util.List;

public class drill {
    public Double x;
    public Double y;
    public Double z;
    public String name;
    public List<Double> layerTopValue = new ArrayList<>();

    public drill() {
    }

    public drill(List<Double> layerTopValue) {
        this.layerTopValue = layerTopValue;
    }

    public drill(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
