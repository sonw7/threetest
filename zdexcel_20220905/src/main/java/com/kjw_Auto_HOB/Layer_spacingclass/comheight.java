package com.kjw_Auto_HOB.Layer_spacingclass;

import com.alibaba.fastjson.JSONObject;
import com.kjw_Auto_HOB.Class.GeoStructure;

import java.util.ArrayList;
import java.util.List;

public class comheight {

    public Double x;
    public Double y;
    public Double z;

    public List<Double> layerTopValue = new ArrayList<>();


    public comheight(List<Double> layerTopValue) {
        this.layerTopValue = layerTopValue;
    }

    public comheight(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public comheight() {
    }

    public String name;

    public Double getGeodeticNorthCoor() {
        return GeodeticNorthCoor;
    }

    public void setGeodeticNorthCoor(Double geodeticNorthCoor) {
        GeodeticNorthCoor = geodeticNorthCoor;
    }

    public Double getGeodeticEastCoor() {
        return GeodeticEastCoor;
    }

    public void setGeodeticEastCoor(Double geodeticEastCoor) {
        GeodeticEastCoor = geodeticEastCoor;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    Double GeodeticNorthCoor;
    Double GeodeticEastCoor;
    Double altitude;
    List<drillVertex> drillVertices = new ArrayList<>();

    List<Double> vertices = new ArrayList<>();
    List<Integer> incorrectVerticesIndex = new ArrayList<>();
    List<String> incorrectVerticesInformation = new ArrayList<>();

    double xdiff = 38500000;
    double ydiff = 4000000;

    public List<drillVertex> getDrillVertices() {
        return drillVertices;
    }

    public void setDrillVertices(List<drillVertex> drillVertices) {
        this.drillVertices = drillVertices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getVertices() {
        return vertices;
    }

    public void setVertices(List<Double> vertices) {
        this.vertices = vertices;
    }



    public List<Integer> getIncorrectVerticesIndex() {
        return incorrectVerticesIndex;
    }

    public void setIncorrectVerticesIndex(List<Integer> incorrectVerticesIndex) {
        this.incorrectVerticesIndex = incorrectVerticesIndex;
    }

    public List<String> getIncorrectVerticesInformation() {
        return incorrectVerticesInformation;
    }

    public void setIncorrectVerticesInformation(List<String> incorrectVerticesInformation) {
        this.incorrectVerticesInformation = incorrectVerticesInformation;
    }

    public double getXdiff() {
        return xdiff;
    }

    public void setXdiff(double xdiff) {
        this.xdiff = xdiff;
    }

    public double getYdiff() {
        return ydiff;
    }

    public void setYdiff(double ydiff) {
        this.ydiff = ydiff;
    }




    public JSONObject toJSON() {
        JSONObject job = new JSONObject();
        job.put("name", name);
        job.put("vertices", vertices);
        job.put("drillVertices", drillVertices);
        job.put("GeodeticNorthCoor", GeodeticNorthCoor);
        job.put("GeodeticEastCoor", GeodeticEastCoor);
        job.put("altitude", altitude);
        job.put("incorrectVerticesIndex", incorrectVerticesIndex);
        job.put("incorrectVerticesInformation", incorrectVerticesInformation);
        return job;
    }

    public ArrayList<Float> PEqua(drillVertex p1, drillVertex p2, drillVertex p3) {
        //获得三角形平面方程
        PQuatCoeff m_PQC = new PQuatCoeff();
        m_PQC.A = p1.y * p2.z + p2.y * p3.z + p3.y * p1.z - p3.y * p2.z - p2.y * p1.z - p1.y * p3.z;
        m_PQC.B = -(p1.x * p2.z + p2.x * p3.z + p3.x * p1.z - p3.x * p2.z - p2.x * p1.z - p1.x * p3.z);
        m_PQC.C = p1.x * p2.y + p2.x * p3.y + p3.x * p1.y - p3.x * p2.y - p2.x * p1.y - p1.x * p3.y;
        m_PQC.D = -(p1.x * p2.y * p3.z + p2.x * p3.y * p1.z + p3.x * p1.y * p2.z
                - p3.x * p2.y * p1.z - p2.x * p1.y * p3.z - p1.x * p3.y * p2.z);
//        float m_PQC_value = m_PQC.A+m_PQC.B+m_PQC.D+m_PQC.C;
        ArrayList<Float> result = new ArrayList<>();
        result.add(m_PQC.A);
        result.add(m_PQC.B);
        result.add(m_PQC.C);
        result.add(m_PQC.D);
//        System.out.println("平面方程："+result);
        return result;

    }

    public float[] IntersectLP(drillVertex p1, drillVertex p2, drillVertex p3, drillVertex ps, drillVertex pe) {
        //计算ps-pe线段与平面m_PQC相交的交点p0 without check anything;
        //如果没有交点返回0
//        float m1,n1,l1,t1,t2,A,B,C,D;
        ArrayList<Float> result1 = PEqua(p1, p2, p3);

        //平面四个参数
        float A = result1.get(0);
        float B = result1.get(1);
        float C = result1.get(2);
        float D = result1.get(3);


        float m1 = ps.x - pe.x;
        float n1 = ps.y - pe.y;
        float l1 = ps.z - pe.z;
        float t1 = m1 * A + n1 * B + l1 * C;

        if (t1 != 0)
        {
            //计算交点p0；  t1==0为ps-pe线段与平面平行，没有交点；
            float t2 = -(A * ps.x + B * ps.y + C * ps.z + D) / t1;
            float p0[] = new float[3];
            p0[0] = ps.x + m1 * t2;
            p0[1] = ps.y + n1 * t2;
            p0[2] = ps.z + l1 * t2;

            return p0;

        } else {
            return null;
        }


    }


//点到面的交线和其交点 以及交点是否在三角面内的判定
    public void verifiedDrill( GeoStructure ahLayer, float up_shift, float down_shift) {


        int drill_size = drillVertices.size();

        for (int d = 0; d < drill_size; ++d) {

            drillVertex pss = new drillVertex(drillVertices.get(d).x,drillVertices.get(d).y,drillVertices.get(d).z);//钻孔数据
            drillVertex pee = new drillVertex(drillVertices.get(d).x,drillVertices.get(d).y,drillVertices.get(d).z+6);//钻孔数据
            boolean inArea = false;
            int indexSize = ahLayer.getIndices().size() / 3;
            for (int i = 0; i < indexSize; ++i) {
                int p1Index = ahLayer.getIndices().get(i*3);
                int p2Index = ahLayer.getIndices().get(i*3 + 1);
                int p3Index = ahLayer.getIndices().get(i*3 + 2);

                List<Integer> p1xyz = getVerticesIndex(p1Index);
                List<Integer> p2xyz = getVerticesIndex(p2Index);
                List<Integer> p3xyz = getVerticesIndex(p3Index);
                drillVertex p11u = new drillVertex(

                        ahLayer.getRtvertices().get(p1xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p1xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p1xyz.get(2)).floatValue() + up_shift);

                drillVertex p22u = new drillVertex(
                        ahLayer.getRtvertices().get(p2xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p2xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p2xyz.get(2)).floatValue() + up_shift);

                drillVertex p33u = new drillVertex(
                        ahLayer.getRtvertices().get(p3xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p3xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p3xyz.get(2)).floatValue() + up_shift);

                drillVertex p11d =  new drillVertex(
                        ahLayer.getRtvertices().get(p1xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p1xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p1xyz.get(2)).floatValue() + down_shift);
                drillVertex p22d = new drillVertex(
                        ahLayer.getRtvertices().get(p2xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p2xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p2xyz.get(2)).floatValue() + down_shift);
                drillVertex p33d = new drillVertex(
                        ahLayer.getRtvertices().get(p3xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p3xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p3xyz.get(2)).floatValue() + down_shift);


                try {
                    //判断顶底板交点是否有效，有效则计算pd、pu
                    if (IsPInCircumcircle(p11u, p22u, p33u, pss, pee) && IsPInCircumcircle(p11d, p22d, p33d, pss, pee))
                    {
                        inArea = true;
//                            System.out.println("交点在三角形内");
                        float[] pd = IntersectLP(p11d, p22d, p33d, pss, pee);
                        float[] pu = IntersectLP(p11u, p22u, p33u, pss, pee);

//
//
//                            System.out.println("钻孔pi,x：" +pss.x+"y: "+pss.y+"z: "+pss.z);
//                            System.out.println("顶板三角形顶点p1,x: " +p11u.x+"y: "+p11u.y+"z: "+p11u.z);
//                            System.out.println("顶板三角形顶点p2,x: " +p22u.x+"y: "+p22u.y+"z: "+p22u.z);
//                            System.out.println("顶板三角形顶点p3,x: " +p33u.x+"y: "+p33u.y+"z: "+p33u.z);
//                            System.out.println("顶板交点pu,x：" +pu[0]+"y: "+pu[1]+"z: "+pu[2]);
//
//                            System.out.println("底板三角形顶点p1,x: " +p11d.x+"y: "+p11d.y+"z: "+p11d.z);
//                            System.out.println("底板三角形顶点p2,x: " +p22d.x+"y: "+p22d.y+"z: "+p22d.z);
//                            System.out.println("底板三角形顶点p3,x: " +p33d.x+"y: "+p33d.y+"z: "+p33d.z);
//                            System.out.println("底面交点pd,x：" +pd[0]+"y: "+pd[1]+"z: "+pd[2]);


                        if ((pd[2] - pss.getZ() <= 0) && (pss.getZ() - pu[2] <= 0)) {

                            incorrectVerticesIndex.add(1);

                        }else{
                            float disU = pu[2] -  pss.getZ();
                            if ( disU < 0) {

                                incorrectVerticesIndex.add(0);
                                String info = "钻孔： "+ name +",轨迹点序号： "+d+",高于地层偏移顶面"+ Math.abs(disU) + "米";
                                incorrectVerticesInformation.add(info);
                            }

                            float disD = pd[2] -  pss.getZ();
                            if ( disD > 0) {
                                incorrectVerticesIndex.add(0);
                                String info = "钻孔： "+ name +",轨迹点序号： "+d+",高于地层偏移顶面"+ Math.abs(disD) + "米";
                                incorrectVerticesInformation.add(info);
                            }

                            //todo 错误

                        }
//                            break;
//                            System.out.print(i);
//                            System.out.println(d);
                    }

                } catch (NullPointerException e) {

                }
            }
            if(!inArea)
            {
                incorrectVerticesIndex.add(0);
                String info = "钻孔： "+ name +",轨迹点序号： "+d+", 不在地层范围内";
                incorrectVerticesInformation.add(info);

            }
        }
    }
    //底层间距离
    public List<vertex> verifiedLayer(GeoStructure ahLayer, List<vertex> Vertices) {


        int layer_size = Vertices.size();
//        System.out.println("点个数："+layer_size);
        //地层顶板点

        List<vertex>  vedi=new ArrayList<>();//存在区域内的点编号和距离


        for (int d = 0; d < layer_size; ++d) {
            //开始循环操作
            vertex v=new vertex(Vertices.get(d).id,Vertices.get(d).x,Vertices.get(d).y,Vertices.get(d).z);//记录第d个点

            drillVertex pss = new drillVertex(Vertices.get(d).x,Vertices.get(d).y,Vertices.get(d).z);//地层点数据
            drillVertex pee = new drillVertex(Vertices.get(d).x,Vertices.get(d).y,Vertices.get(d).z+0.02);//构造数据
            boolean inArea = false;//是否在三角区域标记

            int indexSize = ahLayer.getIndices().size() / 3;//三角形个数
            for (int i = 0; i < indexSize; ++i) {
                //地层底板三角形区域循环
                int p1Index = ahLayer.getIndices().get(i*3);//三角形第一个点
                int p2Index = ahLayer.getIndices().get(i*3 + 1);//三角形第二个点
                int p3Index = ahLayer.getIndices().get(i*3 + 2);//三角形第三个点
                //根据索引找点的序号
                List<Integer> p1xyz = getVerticesIndex(p1Index);//一个顶点的 三个坐标在数组中的序号
                List<Integer> p2xyz = getVerticesIndex(p2Index);
                List<Integer> p3xyz = getVerticesIndex(p3Index);
                //第一个点 取数据
                drillVertex p11u = new drillVertex(
                        ahLayer.getRtvertices().get(p1xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p1xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p1xyz.get(2)).floatValue());
                //第二个点 取数据
                drillVertex p22u = new drillVertex(
                        ahLayer.getRtvertices().get(p2xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p2xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p2xyz.get(2)).floatValue() );
                //第三个点 取数据
                drillVertex p33u = new drillVertex(
                        ahLayer.getRtvertices().get(p3xyz.get(0)).floatValue(),
                        ahLayer.getRtvertices().get(p3xyz.get(1)).floatValue(),
                        ahLayer.getRtvertices().get(p3xyz.get(2)).floatValue() );


                //System.out.printf("11");


                try {
                    //判断顶底板交点是否有效，有效则计算pd、pu
                    if (IsPInCircumcircle(p11u, p22u, p33u, pss, pee) )//三角形三个点  原始点 构造点
                    {
                        //pss数据点，pee构造数据点，p11u, p22u, p33u三角面的三个点
                        //
                        inArea = true;//有效，则交点在三角形内
//                            System.out.println("交点在三角形内");
                       // float[] pd = IntersectLP(p11d, p22d, p33d, pss, pee);
                        float[] pu = IntersectLP(p11u, p22u, p33u, pss, pee);//得到交点x y z
                        //pss原始点

                        if ( pss.getZ() - pu[2] >= 0) {

                            float disU = pu[2] -  pss.getZ();
                            if ( disU <= 0) {

                                incorrectVerticesIndex.add(0);
                                String info = "钻孔： "+ name +",轨迹点序号： "+d+",高于地层偏移顶面"+ Math.abs(disU) + "米";

                                incorrectVerticesInformation.add(info);
//                                if(Math.abs(-disU)<0.02)
//                                    v.setDistance(0);
//                                else
//                                    v.setDistance(Math.abs(-disU));//存储距离
                                v.setDistance(Math.abs(-disU));//存储距离
                                vedi.add(v);
                                break;
                            }

                            //矮减高应该为负
                            incorrectVerticesIndex.add(1);

                        }else{
                            float disU = pu[2] -  pss.getZ();
                            if ( disU >= 0) {

                                incorrectVerticesIndex.add(0);
                                String info = "钻孔： "+ name +",轨迹点序号： "+d+",高于地层偏移顶面"+ Math.abs(disU) + "米";

                                incorrectVerticesInformation.add(info);

//                                if(Math.abs(disU)<0.02)
//                                    v.setDistance(0);
//                                else
                                    v.setDistance(Math.abs(disU));//存储距离
                               vedi.add(v);
                               break;
                            }

//                            float disD = pd[2] -  pss.getZ();
//                            if ( disD > 0) {
//                                incorrectVerticesIndex.add(0);
//                                String info = "钻孔： "+ name +",轨迹点序号： "+d+",高于地层偏移顶面"+ Math.abs(disD) + "米";
//                                incorrectVerticesInformation.add(info);
//                                v.setDistance(Math.abs(disD));//存储距离
//                                vedi.add(v);
//                            }

                            //todo 错误

                        }
//                            break;
//                            System.out.print(i);
//                            System.out.println(d);
                    }



                } catch (NullPointerException e) {

                }
            }
            if(!inArea)
            {
                incorrectVerticesIndex.add(0);
                String info = "钻孔： "+ name +",轨迹点序号： "+d+", 不在地层范围内";
                incorrectVerticesInformation.add(info);

                v.setDistance(0);//存储距离
                vedi.add(v);
            }
        }

        return vedi;
    }

    public List<Integer> getVerticesIndex(Integer index)
    {
        List<Integer> tindex = new ArrayList<>();
        tindex.add(index*3);
        tindex.add(index*3+1);
        tindex.add(index*3+2);
        return  tindex;
    }


    public float CrossProduct(float[] AB,float[] AC){//向量积
        return AB[0]*AC[1]-AC[0]*AB[1];
    }
    public float[] Minus(drillVertex p1,drillVertex p2){
        float AB[] = new float[2];
        AB[0] = p2.x-p1.x;
        AB[1] = p2.y-p1.y;
        return AB;
    }
    public boolean IsPointSide(drillVertex p1,drillVertex p2,drillVertex p3,drillVertex p0){
        float[] AB = Minus(p1, p2);
        float[] AC = Minus(p1, p3);
        float a = CrossProduct(AB,AC);
        float[] AP = Minus(p1, p0);
        float b = CrossProduct(AB,AP);
        return a*b >= 0;
        //return a*b >= 0;

    }
    public boolean IsPInCircumcircle(drillVertex p1, drillVertex p2, drillVertex p3, drillVertex ps, drillVertex pe)//检测交点是否有效
    {

        float[] p0 = IntersectLP(p1,p2,p3,ps,pe);
        //求得交点
        drillVertex p00 = new drillVertex();
        p00.setX(p0[0]);
        p00.setY(p0[1]);
        p00.setZ(p0[2]);
        return IsPointSide(p1,p2,p3,p00)&IsPointSide(p2,p3,p1,p00)&IsPointSide(p3,p1,p2,p00);


    }

}












