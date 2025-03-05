// import { Vector3 } from "three";
// export function boxUvCom(ovDate,oNormal,max,min,size){
//     //ovDate.count 点个数
//     console.log(ovDate)
//     console.log("oNormal",oNormal)
//     var type,vNum=ovDate.count;
//     let Uvs= new Float32Array(vNum*2);
//         min.x*=size
//         min.y*=size
//         max.x*=size
//         max.y*=size
//     //存储uv的数组,2*vNum

//     //包围盒的长宽高
//     let dx = (max.x- min.x);
//     let dy =(max.y- min.y);
//     let dz = (max.z- min.z); 
    

//     let v1,v2,v3;
//     let nX,nY,nZ;
//     v1=v2=v3=new Vector3(0,0,0);
//     console.log(oNormal.array[0])
//     console.log("rwew",(oNormal[1] >=oNormal[0] &&oNormal[1] >= oNormal[2]))
//     let x1=0,y1=0,z1=0
//     for(let i=0;i<vNum;i++){
//         //顶点坐标
//         v1.x=ovDate.array[i*3]*size;
//         v1.y=ovDate.array[i*3+1]*size;
//         v1.z=ovDate.array[i*3+2]*size;

//         //顶点三个法向
//         nX=oNormal.array[i*3];
//         nY=oNormal.array[i*3+1];
//         nZ=oNormal.array[i*3+2];
//         //取绝对值，寻找最大分量
//         const absNum1 = Math.abs(nX);
//         const absNum2 = Math.abs(nY);
//         const absNum3 = Math.abs(nZ);
        
//         if ((absNum1 >= absNum2 && absNum1 >= absNum3)) {
//             type=0;//x分量最大，用y、z计算
//             x1++
//         } else if (absNum2 >= absNum1 && absNum2 >= absNum3) {
//             type=1;//y分量最大，用x、z计算
//             y1++
//         } else {
//             type=2;//z分量最大，用x、y计算
//             z1++
//         }
        

//         if(type==2){
//             if(dx>dy){
//                 Uvs[i*2]=((v1.x-min.x)/dx);
//                 Uvs[i*2+1]=((v1.y-min.y)/dx);
//             }else{
//                 Uvs[i*2]=((v1.x-min.x)/dy);
//                 Uvs[i*2+1]=((v1.y-min.y)/dy);
//             }
//         }else if(type==0){
//             if(dz>dy){
//                 Uvs[i*2]=((v1.y-min.y)/dz);
//                 Uvs[i*2+1]=((v1.z-min.z)/dz);

//             }else{
//                 Uvs[i*2]=((v1.y-min.y)/dy);
//                 Uvs[i*2+1]=((v1.z-min.z)/dy);
//             }
//         }else if(type==1){
//             if(dx>dz){
//                 Uvs[i*2]=((v1.x-min.x)/dx);
//                 Uvs[i*2+1]=((v1.z-min.z)/dx);
//             }else{
//                 Uvs[i*2]=((v1.x-min.x)/dz);
//                 Uvs[i*2+1]=((v1.z-min.z)/dz);
//             }
//         }

//     }
//     console.log(Uvs)
//     console.log("x1",x1)
//     console.log("y1",y1)
//     console.log("z1",z1)
//     return Uvs;


// }

import { Vector3 } from "three";

export function boxUvCom(ovDate, oNormal, max, min, size) {
    const vNum = ovDate.count;
    let Uvs = new Float32Array(vNum * 2);
    
    // 备份 min/max，防止修改原数据
    const minScaled = min.clone().multiplyScalar(size);
    const maxScaled = max.clone().multiplyScalar(size);

    // 计算包围盒尺寸
    const dx = maxScaled.x - minScaled.x;
    const dy = maxScaled.y - minScaled.y;
    const dz = maxScaled.z - minScaled.z;

    let xCount = 0, yCount = 0, zCount = 0;
    const v1 = new Vector3(); // 复用 Vector3 避免重复创建对象

    for (let i = 0; i < vNum; i++) {
        // 读取顶点数据
        v1.set(
            ovDate.array[i * 3] * size,
            ovDate.array[i * 3 + 1] * size,
            ovDate.array[i * 3 + 2] * size
        );

        // 读取法线
        const nX = oNormal.array[i * 3];
        const nY = oNormal.array[i * 3 + 1];
        const nZ = oNormal.array[i * 3 + 2];

        // 计算主导方向
        let type;
        const absNX = Math.abs(nX);
        const absNY = Math.abs(nY);
        const absNZ = Math.abs(nZ);

        if (absNX >= absNY && absNX >= absNZ) {
            type = 0; // X 方向主导，用 YZ 平面计算
            xCount++;
        } else if (absNY >= absNX && absNY >= absNZ) {
            type = 1; // Y 方向主导，用 XZ 平面计算
            yCount++;
        } else {
            type = 2; // Z 方向主导，用 XY 平面计算
            zCount++;
        }

        // 计算 UV 坐标
        switch (type) {
            case 2: // Z 方向主导
                Uvs[i * 2] = (v1.x - minScaled.x) / Math.max(dx, dy);
                Uvs[i * 2 + 1] = (v1.y - minScaled.y) / Math.max(dx, dy);
                break;
            case 0: // X 方向主导
                Uvs[i * 2] = (v1.y - minScaled.y) / Math.max(dy, dz);
                Uvs[i * 2 + 1] = (v1.z - minScaled.z) / Math.max(dy, dz);
                break;
            case 1: // Y 方向主导
                Uvs[i * 2] = (v1.x - minScaled.x) / Math.max(dx, dz);
                Uvs[i * 2 + 1] = (v1.z - minScaled.z) / Math.max(dx, dz);
                break;
        }
    }

    console.log("UV 计算完成: ", Uvs);
    console.log(`X 方向: ${xCount}, Y 方向: ${yCount}, Z 方向: ${zCount}`);

    return Uvs;
}
