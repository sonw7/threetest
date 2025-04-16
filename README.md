```js
      function generate3DContourLines(vertices, indices, depths, contourLevels) {
        // 移除旧的等值线
        scene.remove(contourLines);
        contourLines = new THREE.Group();
        
        // 创建三角形数据结构
        const triangles = createTriangleData(vertices, indices);
        
        // 处理每个等值线级别
        for (let level of contourLevels) {
          const lineSegments = [];
          
          // 对每个三角形进行处理
          for (const triangle of triangles) {
            const v1 = triangle.vertices[0];
            const v2 = triangle.vertices[1];
            const v3 = triangle.vertices[2];
            
            const idx1 = triangle.indices[0];
            const idx2 = triangle.indices[1];
            const idx3 = triangle.indices[2];
            
            const d1 = depths[idx1];
            const d2 = depths[idx2];
            const d3 = depths[idx3];
            
            // 确定哪些顶点在等值面的哪一侧
            const above1 = d1 > level;
            const above2 = d2 > level;
            const above3 = d3 > level;
            
            // 计算等值线在三角形边上的交点
            // 如果一个顶点在等值面上方，另一个在下方，则边与等值面相交
            const intersections = [];
            
            if (above1 !== above2) {
              const t = (level - d1) / (d2 - d1);
              const point = new THREE.Vector3().lerpVectors(v1, v2, t);
              intersections.push(point);
            }
            
            if (above2 !== above3) {
              const t = (level - d2) / (d3 - d2);
              const point = new THREE.Vector3().lerpVectors(v2, v3, t);
              intersections.push(point);
            }
            
            if (above3 !== above1) {
              const t = (level - d3) / (d1 - d3);
              const point = new THREE.Vector3().lerpVectors(v3, v1, t);
              intersections.push(point);
            }
            
            // 如果有两个交点，则添加一个线段
            if (intersections.length === 2) {
              lineSegments.push(intersections[0], intersections[1]);
            }
          }
          
          // 创建线段几何体
          if (lineSegments.length > 0) {
            const geometry = new THREE.BufferGeometry();
            geometry.setFromPoints(lineSegments);
            
            const material = new THREE.LineBasicMaterial({ 
              color: 0x000000, 
              linewidth: 1,
              opacity: 0.7,
              transparent: true
            });
            
            const line = new THREE.LineSegments(geometry, material);
            contourLines.add(line);
          }
        }
        
        scene.add(contourLines);
      }
```

这段代码实现了在3D空间中生成等值线的功能。等值线是连接具有相同值（在这个例子中是相同深度）的点的线，类似于地形图上的海拔线。让我来解释一下它的工作原理：

### 主要功能

代码接收四个参数：

- `vertices`：3D网格的顶点坐标
- `indices`：定义三角形的顶点索引
- `depths`：每个顶点对应的深度值
- `contourLevels`：要生成的等值线级别（深度值）

### 工作流程

1. **清理旧的等值线**：移除之前可能存在的等值线，创建一个新的THREE.Group对象来存储新生成的等值线。
2. **创建三角形数据结构**：调用`createTriangleData`函数将顶点和索引转换为更易处理的三角形数据结构。
3. **处理每个等值线级别**：对每个指定的等值线级别执行以下操作：
   - 创建一个数组来存储线段
   - 遍历每个三角形
   - 获取三角形的三个顶点、索引和对应的深度值
   - 确定每个顶点是在等值面上方还是下方
   - 计算等值线与三角形边的交点
4. **计算交点**：
   - 检查三角形的每条边
   - 如果边的两个端点一个在等值面上方一个在下方，那么它们之间存在交点
   - 使用线性插值计算交点的确切位置
5. **创建线段**：
   - 如果一个三角形有两个交点，这两个点形成等值线的一个线段
   - 将这些线段添加到线段集合中
6. **可视化**：
   - 为每个等值线级别创建一个THREE.LineSegments对象
   - 设置线条的材质属性（颜色、线宽、透明度等）
   - 将所有等值线添加到场景中

### 工作原理

该代码使用了"走格子"(Marching Triangles)算法的变体，这是"行进立方体"(Marching Cubes)算法在2D中的简化版本。它通过分析每个三角形与等值面的交点来构建等值线。

关键点在于：

- 对于每个三角形，检查其三个顶点相对于当前等值线级别的位置
- 当一条边的两个端点分别位于等值面的两侧时，该边与等值面相交
- 使用线性插值计算精确的交点位置
- 将这些交点连接起来形成等值线

这种方法能够有效地在3D网格上可视化复杂的等值线，对于地形分析、科学可视化和数据表示非常有用。