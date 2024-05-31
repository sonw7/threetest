import { Vector3 } from "three";
import { uv } from "three/examples/jsm/nodes/Nodes.js";
/**
 * 计算包围盒uv
 * 
 * @param {Float32Array} ovDate - 点数组。
 * @param {Float32Array} oNormal - 法向量数组。
 * @param {Vector3} max - 法向量数组。
 * @param {Vector3} min - 法向量数组。
 * @returns {oNormalr} 返回uv。
 */
export function boxUvCom(ovDate,oNormal,max,min,size){
    //ovDate.count 点个数

    // console.log("计算uv的点个数",ovDate)
    // console.log("每个点法向量",oNormal)
    // console.log(max)

    //分别记录法向量类型和点总数
    let vNum=ovDate.count;
    //装uv的数组
    let Uvs= new Float32Array(vNum*2);

    //坐标一致
    //包围盒的长宽高
    const dx = (max.x- min.x)*size;
    const dy = (max.y- min.y)*size;
    const dz = (max.z- min.z)*size; 
    

    let nX,nY,nZ;
    let v1=new Vector3(0,0,0);
    
    let x1=0,y1=0,z1=0
    for(let i=0;i<vNum;i++){
        //顶点坐标
        v1.x=ovDate.array[i*3]*size;
        v1.y=ovDate.array[i*3+1]*size;
        v1.z=ovDate.array[i*3+2]*size;

        //顶点三个法向
        nX=oNormal.array[i*3];
        nY=oNormal.array[i*3+1];
        nZ=oNormal.array[i*3+2];
        //取绝对值，寻找最大分量
        const absNum1 = Math.abs(nX);
        const absNum2 = Math.abs(nY);
        const absNum3 = Math.abs(nZ);
        let type;
        if ((absNum1 > absNum2 && absNum1 > absNum3)) {
            type=0;//x分量最大，用y、z计算
            x1++
        } else if (absNum2 > absNum1 && absNum2 > absNum3) {
            type=1;//y分量最大，用x、z计算
            y1++
        } else if(absNum3 > absNum1 && absNum3 > absNum2){
            type=2;//z分量最大，用x、y计算
            z1++
        }else{
            type = 1
        }
        

        if(type==2){
            if(dx>dy){
                Uvs[i*2]=((v1.x-min.x)/dx);
                Uvs[i*2+1]=((v1.y-min.y)/dx);
            }else{
                Uvs[i*2]=((v1.x-min.x)/dy);
                Uvs[i*2+1]=((v1.y-min.y)/dy);
            }
        }else if(type==0){
            if(dz>dy){
                Uvs[i*2]=((v1.y-min.y)/dz);
                Uvs[i*2+1]=((v1.z-min.z)/dz);

            }else{
                Uvs[i*2]=((v1.y-min.y)/dy);
                Uvs[i*2+1]=((v1.z-min.z)/dy);
            }
        }else if(type==1){
            if(dx>dz){
                Uvs[i*2]=((v1.x-min.x)/dx);
                Uvs[i*2+1]=((v1.z-min.z)/dx);
            }else{
                Uvs[i*2]=((v1.x-min.x)/dz);
                Uvs[i*2+1]=((v1.z-min.z)/dz);
            }
        }

    }
    
    return Uvs;


}
export function removeDuplicateTriangles(indices) {
    const uniqueTriangleSet = new Set();
    const uniqueIndices = [];
    let duplicateCount = 0; // 添加用于记录重复数的变量
  
    for (let i = 0; i < indices.length; i += 3) {
      const index1 = indices[i];
      const index2 = indices[i + 1];
      const index3 = indices[i + 2];
  
      const sortedTriangle = [index1, index2, index3].sort((a, b) => a - b);
      const triangleString = sortedTriangle.join('_');
  
      if (!uniqueTriangleSet.has(triangleString)) {
        uniqueTriangleSet.add(triangleString);
        uniqueIndices.push(index1, index2, index3);
      } else {
        duplicateCount++; // 发现重复时递增计数器
      }
    }
  
    console.log("查找有没有重复三角面索引",duplicateCount)
    return uniqueIndices;
  }

  export function boxUvComaddAngle(positions, normals,max,min){

    const vNum = positions.count;
    const uvs = new Float32Array(vNum * 2);
    
    // 计算包围盒大小
    const size = new Vector3().subVectors(max, min);

    for (let i = 0; i < vNum; i++) {
        const index = i * 3;

        // 获取顶点位置和法线
        const vertex = new Vector3(
            positions.array[index],
            positions.array[index + 1],
            positions.array[index + 2]
        );

        const normal = new Vector3(
            normals.array[index],
            normals.array[index + 1],
            normals.array[index + 2]
        );

        // 将顶点位置相对于包围盒的最小点进行偏移
        vertex.sub(min);

        // 计算顶点法线与三个轴对齐平面法线夹角的余弦值
        const cosAngleX = Math.abs(normal.dot(new Vector3(1, 0, 0)));
        const cosAngleY = Math.abs(normal.dot(new Vector3(0, 1, 0)));
        const cosAngleZ = Math.abs(normal.dot(new Vector3(0, 0, 1)));

        // 选择余弦值最大（夹角最小）的平面进行UV投影
        if (cosAngleX > cosAngleY && cosAngleX > cosAngleZ) {
            // 投影到YZ平面
            uvs[i * 2] = vertex.y / size.y;
            uvs[i * 2 + 1] = vertex.z / size.z;
        } else if (cosAngleY > cosAngleZ) {
            // 投影到XZ平面
            uvs[i * 2] = vertex.x / size.x;
            uvs[i * 2 + 1] = vertex.z / size.z;
        } else {
            // 投影到XY平面
            uvs[i * 2] = vertex.x / size.x;
            uvs[i * 2 + 1] = vertex.y / size.y;
        }
    }

    return uvs;
  }