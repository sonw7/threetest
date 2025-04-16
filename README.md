### 等值线

```js
// src/components/ThreeContainer.jsx

import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import Stats from 'three/addons/libs/stats.module.js';
import { GUI } from 'three/addons/libs/lil-gui.module.min.js';
import { useRef, useEffect } from 'react';

function ThreeContainer() {
  const containerRef = useRef(null);
  // 避免重复初始化的标记
  const isContainerRunning = useRef(false);

  useEffect(() => {
    // 确保只初始化一次
    if (!isContainerRunning.current && containerRef.current) {
      isContainerRunning.current = true;

      // ---------------------------
      // 1. 创建场景、相机、渲染器
      // ---------------------------
      const scene = new THREE.Scene();
      scene.background = new THREE.Color(0xf0f0f0);

      const r = 8;
      const theta = Math.PI / 4;
      const phi = Math.PI / 3;
      const x = r * Math.sin(phi) * Math.sin(theta);
      const y = r * Math.cos(phi);
      const z = r * Math.sin(phi) * Math.cos(theta);

      const camera = new THREE.PerspectiveCamera(
        60,
        window.innerWidth / window.innerHeight,
        0.1,
        1000
      );
      camera.position.set(x, y, z);

      const renderer = new THREE.WebGLRenderer({ antialias: true });
      renderer.setSize(window.innerWidth, window.innerHeight);
      containerRef.current.appendChild(renderer.domElement);

      const stats = new Stats();
      containerRef.current.appendChild(stats.dom);

      // ---------------------------
      // 2. 添加光源
      // ---------------------------
      const ambientLight = new THREE.AmbientLight(0xffffff, 1.2);
      scene.add(ambientLight);
      
      const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
      directionalLight.position.set(5, 10, 7.5);
      scene.add(directionalLight);

      // ---------------------------
      // 3. 创建OrbitControls控制器
      // ---------------------------
      const orbitControls = new OrbitControls(camera, renderer.domElement);
      orbitControls.target.set(0, 0, 0);
      orbitControls.enableDamping = true;
      orbitControls.dampingFactor = 0.05;
      orbitControls.minDistance = 2;
      orbitControls.maxDistance = 20;
      orbitControls.update();

      // ---------------------------
      // 4. 坐标轴辅助器
      // ---------------------------
      const axesHelper = new THREE.AxesHelper(5);
      scene.add(axesHelper);

      // ---------------------------
      // 5. 创建平面三角网格并根据深度着色
      // ---------------------------
      
      // 网格分辨率参数
      const gridSize = 64;
      const width = 5;
      const height = 5;
      
      // 创建平面三角网格
      function createTriangleMeshPlane(width, height, segmentsX, segmentsY) {
        // 创建平面几何体
        const geometry = new THREE.PlaneGeometry(width, height, segmentsX, segmentsY);
        
        // 获取顶点位置
        const positions = geometry.attributes.position.array;
        const vertexCount = positions.length / 3;
        
        // 创建二维网格数组存储深度值，便于后续等值线计算
        const gridX = segmentsX + 1;
        const gridY = segmentsY + 1;
        const depthGrid = new Array(gridY).fill(0).map(() => new Array(gridX).fill(0));
        
        // 生成深度值 (使用正弦波函数)
        const depthValues = [];
        let index = 0;
        
        for (let y = 0; y < gridY; y++) {
          for (let x = 0; x < gridX; x++) {
            const xPos = positions[index * 3];
            const yPos = positions[index * 3 + 1];
            
            // 使用正弦波函数生成深度值，创建波浪效果
            const depth = Math.sin(xPos * 2) * Math.cos(yPos * 2) * 0.5;
            
            // 存储深度值到一维数组和二维网格
            depthValues.push(depth);
            depthGrid[y][x] = depth;
            
            index++;
          }
        }
        
        // 计算法线
        geometry.computeVertexNormals();
        
        return { geometry, depthValues, depthGrid, gridX, gridY };
      }
      
      // 根据深度值映射颜色
      function mapDepthToColor(depth, minDepth, maxDepth) {
        // 将深度归一化到0-1范围
        const normalizedDepth = (depth - minDepth) / (maxDepth - minDepth);
        
        // 使用颜色映射 - 从蓝色(深)到红色(浅)
        return new THREE.Color().setHSL(
          0.6 * (1 - normalizedDepth), // 色相: 从蓝色到红色
          0.8,                         // 饱和度: 固定
          0.3 + normalizedDepth * 0.4  // 亮度: 随深度增加
        );
      }
      
      // 创建网格并应用深度颜色
      const { geometry, depthValues, depthGrid, gridX, gridY } = createTriangleMeshPlane(width, height, gridSize, gridSize);
      
      // 找出深度的最小值和最大值
      const minDepth = Math.min(...depthValues);
      const maxDepth = Math.max(...depthValues);
      
      // 根据深度值创建顶点颜色
      const colors = [];
      for (let i = 0; i < depthValues.length; i++) {
        const color = mapDepthToColor(depthValues[i], minDepth, maxDepth);
        colors.push(color.r, color.g, color.b);
      }
      
      // 将颜色添加到几何体中
      geometry.setAttribute('color', new THREE.Float32BufferAttribute(colors, 3));
      
      // 创建使用顶点颜色的材质
      const material = new THREE.MeshPhongMaterial({
        vertexColors: true,
        side: THREE.DoubleSide,
        flatShading: false
      });
      
      // 创建网格并添加到场景
      const mesh = new THREE.Mesh(geometry, material);
      scene.add(mesh);
      
      // ---------------------------
      // 6. 等值线绘制
      // ---------------------------
      
      // 存储等值线对象，方便后续更新
      let contourLines = new THREE.Group();
      scene.add(contourLines);
      
      // 生成等值线
// 生成等值线
function generateContourLines(depthGrid, gridX, gridY, contourLevels, width, height, zOffset = 0.01) {
  // 移除旧的等值线
  scene.remove(contourLines);
  contourLines = new THREE.Group();
  
  // 网格单元大小
  const cellWidth = width / (gridX - 1);
  const cellHeight = height / (gridY - 1);
  
  // 计算实际坐标 - 将网格索引转换为三维空间坐标
  function getPosition(x, y) {
    return new THREE.Vector3(
      x * cellWidth - width / 2,
      y * cellHeight - height / 2,
      zOffset // 稍微偏移以避免z-fighting
    );
  }
  
  // 插值函数 - 计算等值线与单元格边的交点
  function interpolate(x1, y1, x2, y2, val1, val2, level) {
    if (Math.abs(val1 - val2) < 1e-6) {
      return getPosition(x1, y1);
    }
    
    const t = (level - val1) / (val2 - val1);
    const x = x1 + t * (x2 - x1);
    const y = y1 + t * (y2 - y1);
    
    return getPosition(x, y);
  }
  
  // 行进方格算法 (Marching Squares Algorithm)
  for (let level of contourLevels) {
    const lineSegments = [];
    
    for (let y = 0; y < gridY - 1; y++) {
      for (let x = 0; x < gridX - 1; x++) {
        // 获取单元格四个角的深度值
        const topLeft = depthGrid[y + 1][x];
        const topRight = depthGrid[y + 1][x + 1];
        const bottomLeft = depthGrid[y][x];
        const bottomRight = depthGrid[y][x + 1];
        
        // 确定每个角是否高于等值线级别
        const case_index = 
          (topLeft > level ? 8 : 0) +
          (topRight > level ? 4 : 0) +
          (bottomRight > level ? 2 : 0) +
          (bottomLeft > level ? 1 : 0);
        
        // 根据情况确定等值线如何通过单元格
        switch (case_index) {
          case 0: case 15:
            // 无等值线通过或完全被等值线包围
            break;
            
          case 1: case 14:
            // 线段从左侧到底部
            lineSegments.push(
              interpolate(x, y, x, y + 1, bottomLeft, topLeft, level),
              interpolate(x, y, x + 1, y, bottomLeft, bottomRight, level)
            );
            break;
            
          case 2: case 13:
            // 线段从底部到右侧
            lineSegments.push(
              interpolate(x, y, x + 1, y, bottomLeft, bottomRight, level),
              interpolate(x + 1, y, x + 1, y + 1, bottomRight, topRight, level)
            );
            break;
            
          case 3: case 12:
            // 线段从左侧到右侧
            lineSegments.push(
              interpolate(x, y, x, y + 1, bottomLeft, topLeft, level),
              interpolate(x + 1, y, x + 1, y + 1, bottomRight, topRight, level)
            );
            break;
            
          case 4: case 11:
            // 线段从顶部到右侧
            lineSegments.push(
              interpolate(x, y + 1, x + 1, y + 1, topLeft, topRight, level),
              interpolate(x + 1, y, x + 1, y + 1, bottomRight, topRight, level)
            );
            break;
            
          case 5:
            // 线段从左侧到顶部和底部到右侧 (鞍点)
            lineSegments.push(
              interpolate(x, y, x, y + 1, bottomLeft, topLeft, level),
              interpolate(x, y + 1, x + 1, y + 1, topLeft, topRight, level),
              interpolate(x, y, x + 1, y, bottomLeft, bottomRight, level),
              interpolate(x + 1, y, x + 1, y + 1, bottomRight, topRight, level)
            );
            break;
            
          case 6: case 9:
            // 线段从顶部到底部
            lineSegments.push(
              interpolate(x, y + 1, x + 1, y + 1, topLeft, topRight, level),
              interpolate(x, y, x + 1, y, bottomLeft, bottomRight, level)
            );
            break;
            
          case 7: case 8:
            // 线段从左侧到顶部
            lineSegments.push(
              interpolate(x, y, x, y + 1, bottomLeft, topLeft, level),
              interpolate(x, y + 1, x + 1, y + 1, topLeft, topRight, level)
            );
            break;
            
          case 10:
            // 线段从顶部到右侧和左侧到底部 (鞍点)
            lineSegments.push(
              interpolate(x, y + 1, x + 1, y + 1, topLeft, topRight, level),
              interpolate(x + 1, y, x + 1, y + 1, bottomRight, topRight, level),
              interpolate(x, y, x, y + 1, bottomLeft, topLeft, level),
              interpolate(x, y, x + 1, y, bottomLeft, bottomRight, level)
            );
            break;
        }
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
      
      // 插值函数 - 计算等值线与单元格边的交点
      function interpolate(x1, y1, x2, y2, val1, val2, level) {
        if (Math.abs(val1 - val2) < 1e-6) {
          return getPosition(x1, y1);
        }
        
        const t = (level - val1) / (val2 - val1);
        const x = x1 + t * (x2 - x1);
        const y = y1 + t * (y2 - y1);
        
        return getPosition(x, y);
      }
      
      // 计算等值线级别
      function calculateContourLevels(minDepth, maxDepth, count) {
        const levels = [];
        const step = (maxDepth - minDepth) / (count + 1);
        
        for (let i = 1; i <= count; i++) {
          levels.push(minDepth + step * i);
        }
        
        return levels;
      }
      
      // 初始等值线参数
      const contourParams = {
        contourCount: 10,
        showContours: true,
        contourColor: '#000000',
        contourOpacity: 0.7
      };
      
      // 初始生成等值线
      const initialContourLevels = calculateContourLevels(minDepth, maxDepth, contourParams.contourCount);
      generateContourLines(depthGrid, gridX, gridY, initialContourLevels, width, height);
      
      // 更新等值线
      function updateContourLines() {
        if (contourParams.showContours) {
          const levels = calculateContourLevels(minDepth, maxDepth, contourParams.contourCount);
          generateContourLines(depthGrid, gridX, gridY, levels, width, height);
          
          // 更新等值线颜色和透明度
          contourLines.children.forEach(line => {
            line.material.color.set(contourParams.contourColor);
            line.material.opacity = contourParams.contourOpacity;
          });
        } else {
          scene.remove(contourLines);
          contourLines = new THREE.Group();
          scene.add(contourLines);
        }
      }
      
      // ---------------------------
      // 7. 创建GUI控制面板
      // ---------------------------
      
      // 控制参数
      const params = {
        wireframe: false,
        flatShading: false,
        showDepthAsGeometry: false,
        depthScale: 1.0,
        resetCamera: function() {
          camera.position.set(x, y, z);
          orbitControls.target.set(0, 0, 0);
          orbitControls.update();
        },
        regenerateDepth: function() {
          // 重新生成深度值
          const positions = geometry.attributes.position.array;
          const vertexCount = positions.length / 3;
          
          // 重置Z坐标
          for (let i = 0; i < vertexCount; i++) {
            positions[i * 3 + 2] = 0;
          }
          
          // 生成新的深度值
          let index = 0;
          for (let y = 0; y < gridY; y++) {
            for (let x = 0; x < gridX; x++) {
              const xPos = positions[index * 3];
              const yPos = positions[index * 3 + 1];
              
              // 随机生成新的深度函数
              const frequency = Math.random() * 3 + 1;
              const depth = Math.sin(xPos * frequency) * Math.cos(yPos * frequency) * 0.5;
              
              depthValues[index] = depth;
              depthGrid[y][x] = depth;
              
              // 如果启用了几何深度显示，则更新Z坐标
              if (params.showDepthAsGeometry) {
                positions[index * 3 + 2] = depth * params.depthScale;
              }
              
              index++;
            }
          }
          
          // 更新颜色
          const newMinDepth = Math.min(...depthValues);
          const newMaxDepth = Math.max(...depthValues);
          
          const colorAttribute = geometry.attributes.color;
          for (let i = 0; i < depthValues.length; i++) {
            const color = mapDepthToColor(depthValues[i], newMinDepth, newMaxDepth);
            colorAttribute.setXYZ(i, color.r, color.g, color.b);
          }
          
          // 更新最小最大深度值
          Object.assign(minMaxDepth, { min: newMinDepth, max: newMaxDepth });
          minDepthController.updateDisplay();
          maxDepthController.updateDisplay();
          
          colorAttribute.needsUpdate = true;
          geometry.attributes.position.needsUpdate = true;
          geometry.computeVertexNormals();
          
          // 更新等值线
          updateContourLines();
        }
      };
      
      const gui = new GUI();
      
      // 添加控制选项
      gui.add(params, 'wireframe').onChange(value => {
        material.wireframe = value;
      });
      
      gui.add(params, 'flatShading').onChange(value => {
        material.flatShading = value;
        material.needsUpdate = true;
      });
      
      gui.add(params, 'showDepthAsGeometry').name('Show Depth as Geometry').onChange(value => {
        const positions = geometry.attributes.position.array;
        let index = 0;
        
        for (let y = 0; y < gridY; y++) {
          for (let x = 0; x < gridX; x++) {
            positions[index * 3 + 2] = value ? depthGrid[y][x] * params.depthScale : 0;
            index++;
          }
        }
        
        geometry.attributes.position.needsUpdate = true;
        geometry.computeVertexNormals();
      });
      
      gui.add(params, 'depthScale', 0.1, 3).name('Depth Scale').onChange(value => {
        if (params.showDepthAsGeometry) {
          const positions = geometry.attributes.position.array;
          let index = 0;
          
          for (let y = 0; y < gridY; y++) {
            for (let x = 0; x < gridX; x++) {
              positions[index * 3 + 2] = depthGrid[y][x] * value;
              index++;
            }
          }
          
          geometry.attributes.position.needsUpdate = true;
          geometry.computeVertexNormals();
        }
      });
      
      gui.add(params, 'resetCamera').name('Reset Camera');
      gui.add(params, 'regenerateDepth').name('New Depth Pattern');
      
      // 添加深度范围显示
      const minMaxDepth = { min: minDepth, max: maxDepth };
      const depthFolder = gui.addFolder('Depth Range');
      const minDepthController = depthFolder.add(minMaxDepth, 'min').name('Min Depth').disable();
      const maxDepthController = depthFolder.add(minMaxDepth, 'max').name('Max Depth').disable();
      depthFolder.open();
      
      // 添加等值线控制
      const contourFolder = gui.addFolder('Contour Lines');
      contourFolder.add(contourParams, 'showContours').name('Show Contours').onChange(updateContourLines);
      contourFolder.add(contourParams, 'contourCount', 1, 30, 1).name('Contour Count').onChange(updateContourLines);
      contourFolder.addColor(contourParams, 'contourColor').name('Contour Color').onChange(updateContourLines);
      contourFolder.add(contourParams, 'contourOpacity', 0, 1).name('Contour Opacity').onChange(updateContourLines);
      contourFolder.open();

      // ---------------------------
      // 8. 动画循环
      // ---------------------------
      function animate() {
        requestAnimationFrame(animate);
        orbitControls.update();
        renderer.render(scene, camera);
        stats.update();
      }
      animate();

      // ---------------------------
      // 9. 监听窗口变化，保持自适应
      // ---------------------------
      window.addEventListener('resize', onWindowResize, false);
      function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
      }
    }
  }, []);

  return <div ref={containerRef} />;
}

export default ThreeContainer;
```

好的，我们来详细解读一下这段 `ThreeContainer.jsx` React 组件的代码逻辑。

这个组件的核心目标是使用 Three.js 库在一个 React 应用中渲染一个 3D 场景。场景中包含一个平面网格，该网格的顶点根据生成的“深度”值进行着色（形成类似热力图的效果），并且还绘制了与这些深度值相对应的等值线。组件还通过 `lil-gui` 提供了用户界面，允许用户调整参数并与场景交互。

以下是详细的代码逻辑分解：

**1. 导入和设置 (React & Three.js)**

- **React:** 导入了 `useRef`（用于获取容器 DOM 元素的引用，并跟踪初始化状态）和 `useEffect`（用于在组件挂载后执行一次 Three.js 设置代码）。
- **Three.js 核心:** 导入了主要的 `THREE` 对象。
- Three.js 扩展 (Addons):
  - `OrbitControls`: 允许用户使用鼠标旋转、平移和缩放相机。
  - `Stats`: 显示性能统计信息（如 FPS）。
  - `GUI` (来自 `lil-gui.module.min.js`): 提供一个图形用户界面库，用于创建控制面板。
- **组件结构:** 定义了一个名为 `ThreeContainer` 的 React 函数式组件。
- Refs:
  - `containerRef`: 将持有一个对 `div` 元素的引用，Three.js 的 canvas（画布）将被附加到这个 `div` 上。
  - `isContainerRunning`: 一个布尔类型的 ref，用作标志位，防止 `useEffect` 钩子在组件因其他原因重新渲染时多次运行初始化逻辑（尽管对于空的依赖数组 `[]`，`useEffect` 本身只会运行一次，这是一种防御性编程）。

**2. `useEffect` 钩子 (主要逻辑)**

- 这个钩子在其依赖数组为空 (`[]`) 的情况下，只会在组件**首次渲染到 DOM 后运行一次**。
- **初始化保护:** `if (!isContainerRunning.current && containerRef.current)` 确保内部代码只在之前未运行过且容器 `div` 已经存在于 DOM 中时才执行。`isContainerRunning.current = true;` 这行代码设置了标志位，表示初始化已开始。

**3. 核心 Three.js 设置 (在 `useEffect` 内部)**

- **场景 (Scene):** `const scene = new THREE.Scene();` 创建了容纳所有 3D 对象的根容器。`scene.background` 设置了场景的背景颜色。
- 相机 (Camera):
  - 使用球坐标 (`r`, `theta`, `phi`) 计算了一个初始相机位置 (`x`, `y`, `z`)，将其放置在距离原点特定角度和距离的位置。
  - `const camera = new THREE.PerspectiveCamera(...)` 创建了一个透视相机（远处的物体看起来更小）。参数定义了视野角度(fov)、宽高比(aspect ratio，基于窗口尺寸)、近裁剪面(near)和远裁剪面(far)。
  - `camera.position.set(x, y, z);` 应用计算出的初始位置。
- 渲染器 (Renderer):
  - `const renderer = new THREE.WebGLRenderer({ antialias: true });` 创建了使用 WebGL 将场景绘制到 canvas 上的渲染器。`antialias: true` 尝试平滑锯齿边缘。
  - `renderer.setSize(...)` 设置了 canvas 的尺寸以匹配窗口大小。
  - `containerRef.current.appendChild(renderer.domElement);` 将渲染器创建的 canvas 元素附加到通过 `containerRef` 获取的 `div` 上。
- **性能监视器 (Stats):** `const stats = new Stats();` 创建了性能监视器，并将其 DOM 元素附加到容器 `div` 上。

**4. 光源 (Lighting)**

- **环境光 (Ambient Light):** `new THREE.AmbientLight(0xffffff, 1.2)` 提供了一种柔和的、无方向的光，均匀地照亮场景中的所有物体。
- **平行光 (Directional Light):** `new THREE.DirectionalLight(0xffffff, 0.8)` 模拟来自远处光源（如太阳）的光线，产生平行光线。`directionalLight.position.set(...)` 定义了光线射来的方向。

**5. 轨道控制器 (OrbitControls)**

- `const orbitControls = new OrbitControls(camera, renderer.domElement);` 将控制器与相机和渲染器的 canvas 关联起来。
- `orbitControls.target.set(0, 0, 0);` 设置相机观察和围绕旋转的目标点（场景中心）。
- `enableDamping`, `dampingFactor`: 在停止相机移动时创建更平滑、自然的阻尼效果。需要在动画循环中调用 `orbitControls.update()`。
- `minDistance`, `maxDistance`: 限制用户可以缩放的最近和最远距离。
- `orbitControls.update();`: 应用初始设置。

**6. 坐标轴辅助器 (Axes Helper)**

- `const axesHelper = new THREE.AxesHelper(5);` 创建了一个可视化辅助工具，显示 X（红色）、Y（绿色）和 Z（蓝色）轴，有助于确定方向。

**7. 平面网格创建和深度着色**

- **参数:** `gridSize`, `width`, `height` 定义了平面的分辨率和尺寸。

- `createTriangleMeshPlane` 函数:

  - 使用指定的尺寸和分段数 (`gridSize`) 创建一个 `THREE.PlaneGeometry`。分段越多，分辨率越高。

  - 获取原始顶点位置数组 (`positions`)。

  - 初始化 `depthGrid`（一个二维数组）和 `depthValues`（一个一维数组）来存储每个顶点的深度信息。

  - 深度生成循环:

     

    遍历网格顶点。对于每个顶点（通过

     

    ```
    x
    ```

    ,

     

    ```
    y
    ```

     

    索引）：

    - 获取顶点的 3D 位置 (`xPos`, `yPos`)。
    - 使用三角函数 (`Math.sin(xPos * 2) * Math.cos(yPos * 2) * 0.5`) 计算一个 `depth` 值。这会产生一个波浪状的图案。
    - 将 `depth` 存储到 `depthValues` 和 `depthGrid` 中。

  - `geometry.computeVertexNormals();`: 计算法线，这对于光照计算是必需的。

  - 返回几何体、深度数据和网格尺寸。

- `mapDepthToColor` 函数:

  - 接收一个 `depth` 值以及整个数据集的 `minDepth` 和 `maxDepth`。
  - 将深度值归一化到 0-1 的范围。
  - 使用 `THREE.Color().setHSL()` 将归一化后的深度映射到 HSL (色相-饱和度-亮度) 色彩空间中的一个颜色。这里，它将低深度（归一化为 0）映射到接近蓝色（色相约 0.6），高深度（归一化为 1）映射到接近红色（色相约 0），并略微调整亮度。

- 应用颜色:

  - 调用 `createTriangleMeshPlane` 获取几何体和深度数据。
  - 从 `depthValues` 中找出全局的 `minDepth` 和 `maxDepth`。
  - 创建一个 `colors` 数组。遍历 `depthValues`，为每个值调用 `mapDepthToColor`，并将颜色的 R、G、B 分量推入 `colors` 数组。
  - `geometry.setAttribute('color', new THREE.Float32BufferAttribute(colors, 3));` 将计算出的顶点颜色数据附加到几何体上，作为 'color' 属性。

- 材质和网格 (Material and Mesh):

  - `const material = new THREE.MeshPhongMaterial(...)` 创建了一个对光照有反应的材质。
  - `vertexColors: true`: 告知材质使用几何体 'color' 属性中定义的颜色。
  - `side: THREE.DoubleSide`: 渲染平面的正面和背面。
  - `const mesh = new THREE.Mesh(geometry, material);` 将几何体和材质组合成一个网格对象。
  - `scene.add(mesh);` 将着色后的平面添加到场景中。

**8. 等值线生成 (Marching Squares - 行进方格算法)**

- **`contourLines` 组:** 初始化一个空的 `THREE.Group`，用于存放所有等值线的线段。

- `generateContourLines` 函数:

  - 接收 `depthGrid`、尺寸、`contourLevels`（一个包含要绘制等值线的深度值的数组）、平面 `width`/`height` 和一个小的 `zOffset`。

  - 从场景中移除旧的 `contourLines` 组，并创建一个新的空组。

  - 计算网格单元的 `cellWidth` 和 `cellHeight`。

  - **`getPosition` 辅助函数:** 将网格索引 (x, y) 转换为 3D 世界坐标，并应用 `zOffset` 将等值线稍微抬高到网格表面之上（防止 Z-fighting，即深度冲突/闪烁）。

  - **`interpolate` 辅助函数:** 执行线性插值。给定两个点及其深度值以及一个目标 `level`，它计算出两点之间边上深度恰好等于 `level` 的精确 3D 位置。

  - Marching Squares 循环:

    - 遍历 `contourLevels` 中的每个 `level`。

    - 遍历网格中的每个单元格 (`x`, `y`)（不包括最后一行/列）。

    - 获取当前单元格四个角的深度值 (`topLeft`, `topRight`, `bottomLeft`, `bottomRight`)。

    - 计算 `case_index`: 一个 4 位二进制数（0-15），表示哪些角位于当前 `level` 之上。每一位对应一个角（例如，8=左上, 4=右上, 2=右下, 1=左下）。

    - `switch (case_index)`:

       

      根据

       

      ```
      case_index
      ```

      ，确定等值线段应该如何穿过当前单元格。

      - 情况 0 和 15：没有线段穿过。
      - 其他情况：使用 `interpolate` 函数找到单元格边上的交点，并将点对（起点，终点）推入 `lineSegments` 数组。情况 5 和 10 是鞍点，需要生成两条独立的线段。

  - 线条创建:

    处理完一个level的所有单元格后：

    - 如果 `lineSegments` 不为空，根据这些点创建一个 `THREE.BufferGeometry`。
    - 创建一个 `THREE.LineBasicMaterial`（简单的黑色线条，略微透明）。
    - 创建 `THREE.LineSegments`（在点对之间绘制线段）。
    - 将这个 `line` 对象添加到 `contourLines` 组中。

  - 最后，将包含所有层级所有线条的 `contourLines` 组添加到场景中。

- **`calculateContourLevels` 函数:** 一个简单的辅助函数，用于在 `minDepth` 和 `maxDepth` 之间根据指定的 `count` 创建一个等间距的深度值数组。

- 初始调用和更新逻辑:

  - `contourParams`: 存储用于等值线的 GUI 参数。
  - 计算 `initialContourLevels`。
  - 初始调用 `generateContourLines` 生成第一次的等值线。
  - `updateContourLines` 函数: 当 GUI 参数改变时被调用。它会重新计算层级（如果数量改变），使用 `generateContourLines` 重新生成线条，并在仅颜色或透明度改变时更新现有线条的材质属性。它也处理显示/隐藏等值线。

**9. GUI 控制面板 (`lil-gui`)**

- `params` 对象:

   

  持有由 GUI 控制的值和函数。

  - `wireframe`, `flatShading`: 控制材质的外观。

  - `showDepthAsGeometry`: 切换网格的 Z 坐标是否根据深度值变形（创建 3D 表面）或保持平坦（Z=0）。

  - `depthScale`: 当 `showDepthAsGeometry` 为 true 时，Z 变形的缩放因子。

  - `resetCamera`: 用于恢复初始相机位置和目标的函数。

  - ```
    regenerateDepth
    ```

    :

     

    核心交互函数:

    - 将顶点的 Z 坐标重置为 0。
    - 使用略微随机化的频率因子在 sin/cos 函数中生成**新的**随机深度值。
    - 更新 `depthValues` 和 `depthGrid`。
    - 如果 `showDepthAsGeometry` 已启用，则应用 Z 变形。
    - 重新计算 `minDepth`/`maxDepth`。
    - 根据**新的**深度范围更新顶点颜色。
    - 更新 GUI 中只读的最小/最大深度显示。
    - 标记几何体属性（`color`, `position`）需要更新。
    - 重新计算法线。
    - 调用 `updateContourLines` 以根据新的深度数据重绘等值线。

- **只读深度范围显示:** `minMaxDepth` 对象和对应的 GUI 控件用于显示当前的最小和最大深度。

- **等值线控制:** GUI 控件 (`showContours`, `contourCount`, `contourColor`, `contourOpacity`) 允许用户调整等值线的外观，这些控件的改变会触发 `updateContourLines` 函数。

**10. 动画循环 (Animation Loop)**

- `animate` 函数使用 `requestAnimationFrame` 来创建渲染循环。
- 在每一帧中：
  - `orbitControls.update()`: 如果启用了阻尼，则更新控制器状态。
  - `renderer.render(scene, camera)`: 将场景渲染到 canvas 上。
  - `stats.update()`: 更新性能监视器显示。

**11. 窗口大小调整处理 (Window Resize Handling)**

- `onWindowResize` 函数在窗口大小改变时被调用。
- 它更新相机的宽高比 (`camera.aspect`) 和投影矩阵 (`camera.updateProjectionMatrix()`)。
- 它还调整渲染器的大小 (`renderer.setSize`) 以匹配新的窗口尺寸。
- 通过 `window.addEventListener` 监听 `resize` 事件。

**12. React 组件返回 (React Component Return)**

- 组件最终返回一个 `div` 元素，该元素的 `ref` 属性被设置为 `containerRef`。这就是 Three.js canvas 被注入的地方。

**总结:**

该组件巧妙地结合了 React 的组件模型和 Three.js 的 3D 渲染能力。它创建了一个动态的 3D 可视化：一个根据程序生成的深度数据进行着色的平面，并辅以相应的等值线。用户可以通过 GUI 实时调整视觉效果，甚至生成全新的深度图案，展示了交互式数据可视化的基本概念。代码结构清晰，利用 `useEffect` 进行初始化，并通过 `lil-gui` 提供了良好的交互性。