package com.kjw_Auto_HOB.Class;

import com.kjw_Auto_HOB.FaultIntersect;

public class Line {
    /**
     * 斜率
     */
    public double k;

    /**
     * 在 y 轴上的截距
     */
    public double b;


    public Line(Point start, Point end) {
        double deltaY = end.y - start.y;
        double deltaX = end.x - start.x;

        // 注意：当 deltaX = 0 的时候，设置斜率为正无穷
        if (deltaX == 0) {
            k = Integer.MAX_VALUE;
            // 此时截距为直线在 x 轴上的截距，这里是特殊的做法
            b = end.x;
        } else {
            k = deltaY / deltaX;
            b = end.y - k * end.x;
        }
    }
}
