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
      // 5. 创建自定义三角网格并根据深度着色
      // ---------------------------
      
      // 生成示例数据 (在实际应用中，这些数据可能来自外部)
      function generateSampleData() {
        // 创建一个网格化的地形示例
        const gridSize = 20; // 网格分辨率
        const size = 5; // 总大小
        
        // 顶点数组
        const vertices = [];
        // 索引数组
        const indices = [];
        // 深度数组
        const depths = [];
        
        // 生成顶点和深度
        for (let i = 0; i <= gridSize; i++) {
          for (let j = 0; j <= gridSize; j++) {
            const x = (i / gridSize) * size - size / 2;
            const y = (j / gridSize) * size - size / 2;
            
            // 基础高度为0，然后添加一些波浪效果
            const baseZ = 0;
            
            // 添加顶点坐标
            vertices.push(x, y, baseZ);
            
            // 生成深度值 (这可能与z坐标不同，表示其他物理量)
            // 这里我们用正弦波作为示例
            const depth = Math.sin(x * 1.5) * Math.cos(y * 1.5) * 0.5;
            depths.push(depth);
          }
        }
        
        // 生成三角形索引
        for (let i = 0; i < gridSize; i++) {
          for (let j = 0; j < gridSize; j++) {
            const a = i * (gridSize + 1) + j;
            const b = i * (gridSize + 1) + j + 1;
            const c = (i + 1) * (gridSize + 1) + j;
            const d = (i + 1) * (gridSize + 1) + j + 1;
            
            // 每个网格单元生成两个三角形
            indices.push(a, c, b); // 第一个三角形
            indices.push(b, c, d); // 第二个三角形
          }
        }
        
        return {
          vertices,
          indices,
          depths,
          gridSize,
          size
        };
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
      
      // 生成示例数据
      const sampleData = generateSampleData();
      
      // 创建几何体
      const geometry = new THREE.BufferGeometry();
      
      // 设置位置属性
      geometry.setAttribute('position', new THREE.Float32BufferAttribute(sampleData.vertices, 3));
      
      // 设置索引
      geometry.setIndex(sampleData.indices);
      
      // 计算法线
      geometry.computeVertexNormals();
      
      // 找出深度的最小值和最大值
      const minDepth = Math.min(...sampleData.depths);
      const maxDepth = Math.max(...sampleData.depths);
      
      // 根据深度值创建顶点颜色
      const colors = [];
      for (let i = 0; i < sampleData.depths.length; i++) {
        const color = mapDepthToColor(sampleData.depths[i], minDepth, maxDepth);
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
      // 6. 创建等值线
      // ---------------------------
      
      // 创建一个深度网格，用于等值线计算
      function createDepthGrid(vertices, depths, gridSize) {
        const depthGrid = [];
        for (let i = 0; i <= gridSize; i++) {
          const row = [];
          for (let j = 0; j <= gridSize; j++) {
            const index = i * (gridSize + 1) + j;
            row.push(depths[index]);
          }
          depthGrid.push(row);
        }
        return depthGrid;
      }
      
      // 创建深度网格
      const depthGrid = createDepthGrid(sampleData.vertices, sampleData.depths, sampleData.gridSize);
      
      // 存储等值线对象
      let contourLines = new THREE.Group();
      scene.add(contourLines);
      
      // 生成等值线
      function generateContourLines(depthGrid, contourLevels, gridSize, size, zOffset = 0.01) {
        // 移除旧的等值线
        scene.remove(contourLines);
        contourLines = new THREE.Group();
        
        // 计算网格单元大小
        const cellSize = size / gridSize;
        
        // 计算实际坐标
        function getPosition(i, j) {
          return new THREE.Vector3(
            (i / gridSize) * size - size / 2,
            (j / gridSize) * size - size / 2,
            zOffset // 稍微偏移以避免z-fighting
          );
        }
        
        // 插值函数 - 计算等值线与单元格边的交点
        function interpolate(i1, j1, i2, j2, val1, val2, level) {
          if (Math.abs(val1 - val2) < 1e-6) {
            return getPosition(i1, j1);
          }
          
          const t = (level - val1) / (val2 - val1);
          const i = i1 + t * (i2 - i1);
          const j = j1 + t * (j2 - j1);
          
          return getPosition(i, j);
        }
        
        // 行进方格算法 (Marching Squares Algorithm)
        for (let level of contourLevels) {
          const lineSegments = [];
          
          for (let i = 0; i < gridSize; i++) {
            for (let j = 0; j < gridSize; j++) {
              // 获取单元格四个角的深度值
              const bottomLeft = depthGrid[i][j];
              const bottomRight = depthGrid[i][j + 1];
              const topLeft = depthGrid[i + 1][j];
              const topRight = depthGrid[i + 1][j + 1];
              
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
                    interpolate(i, j, i + 1, j, bottomLeft, topLeft, level),
                    interpolate(i, j, i, j + 1, bottomLeft, bottomRight, level)
                  );
                  break;
                  
                case 2: case 13:
                  // 线段从底部到右侧
                  lineSegments.push(
                    interpolate(i, j, i, j + 1, bottomLeft, bottomRight, level),
                    interpolate(i, j + 1, i + 1, j + 1, bottomRight, topRight, level)
                  );
                  break;
                  
                case 3: case 12:
                  // 线段从左侧到右侧
                  lineSegments.push(
                    interpolate(i, j, i + 1, j, bottomLeft, topLeft, level),
                    interpolate(i, j + 1, i + 1, j + 1, bottomRight, topRight, level)
                  );
                  break;
                  
                case 4: case 11:
                  // 线段从顶部到右侧
                  lineSegments.push(
                    interpolate(i + 1, j, i + 1, j + 1, topLeft, topRight, level),
                    interpolate(i, j + 1, i + 1, j + 1, bottomRight, topRight, level)
                  );
                  break;
                  
                case 5:
                  // 线段从左侧到顶部和底部到右侧 (鞍点)
                  lineSegments.push(
                    interpolate(i, j, i + 1, j, bottomLeft, topLeft, level),
                    interpolate(i + 1, j, i + 1, j + 1, topLeft, topRight, level),
                    interpolate(i, j, i, j + 1, bottomLeft, bottomRight, level),
                    interpolate(i, j + 1, i + 1, j + 1, bottomRight, topRight, level)
                  );
                  break;
                  
                case 6: case 9:
                  // 线段从顶部到底部
                  lineSegments.push(
                    interpolate(i + 1, j, i + 1, j + 1, topLeft, topRight, level),
                    interpolate(i, j, i, j + 1, bottomLeft, bottomRight, level)
                  );
                  break;
                  
                case 7: case 8:
                  // 线段从左侧到顶部
                  lineSegments.push(
                    interpolate(i, j, i + 1, j, bottomLeft, topLeft, level),
                    interpolate(i + 1, j, i + 1, j + 1, topLeft, topRight, level)
                  );
                  break;
                  
                case 10:
                  // 线段从顶部到右侧和左侧到底部 (鞍点)
                  lineSegments.push(
                    interpolate(i + 1, j, i + 1, j + 1, topLeft, topRight, level),
                    interpolate(i, j + 1, i + 1, j + 1, bottomRight, topRight, level),
                    interpolate(i, j, i + 1, j, bottomLeft, topLeft, level),
                    interpolate(i, j, i, j + 1, bottomLeft, bottomRight, level)
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
      generateContourLines(depthGrid, initialContourLevels, sampleData.gridSize, sampleData.size);
      
      // 更新等值线
      function updateContourLines() {
        if (contourParams.showContours) {
          const levels = calculateContourLevels(minDepth, maxDepth, contourParams.contourCount);
          generateContourLines(depthGrid, levels, sampleData.gridSize, sampleData.size);
          
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
          
          // 重置Z坐标
          for (let i = 0; i < sampleData.depths.length; i++) {
            positions[i * 3 + 2] = 0;
          }
          
          // 生成新的深度值
          for (let i = 0; i <= sampleData.gridSize; i++) {
            for (let j = 0; j <= sampleData.gridSize; j++) {
              const index = i * (sampleData.gridSize + 1) + j;
              const x = positions[index * 3];
              const y = positions[index * 3 + 1];
              
              // 随机生成新的深度函数
              const frequency = Math.random() * 3 + 1;
              const depth = Math.sin(x * frequency) * Math.cos(y * frequency) * 0.5;
              
              sampleData.depths[index] = depth;
              depthGrid[i][j] = depth;
              
              // 如果启用了几何深度显示，则更新Z坐标
              if (params.showDepthAsGeometry) {
                positions[index * 3 + 2] = depth * params.depthScale;
              }
            }
          }
          
          // 更新颜色
          const newMinDepth = Math.min(...sampleData.depths);
          const newMaxDepth = Math.max(...sampleData.depths);
          
          const colorAttribute = geometry.attributes.color;
          for (let i = 0; i < sampleData.depths.length; i++) {
            const color = mapDepthToColor(sampleData.depths[i], newMinDepth, newMaxDepth);
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
        
        for (let i = 0; i <= sampleData.gridSize; i++) {
          for (let j = 0; j <= sampleData.gridSize; j++) {
            const index = i * (sampleData.gridSize + 1) + j;
            positions[index * 3 + 2] = value ? sampleData.depths[index] * params.depthScale : 0;
          }
        }
        
        geometry.attributes.position.needsUpdate = true;
        geometry.computeVertexNormals();
      });
      
      gui.add(params, 'depthScale', 0.1, 3).name('Depth Scale').onChange(value => {
        if (params.showDepthAsGeometry) {
          const positions = geometry.attributes.position.array;
          
          for (let i = 0; i <= sampleData.gridSize; i++) {
            for (let j = 0; j <= sampleData.gridSize; j++) {
              const index = i * (sampleData.gridSize + 1) + j;
              positions[index * 3 + 2] = sampleData.depths[index] * value;
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
      // 8. 加载外部数据的函数
      // ---------------------------
      
      // 这个函数可以用来加载外部数据并更新网格
      function loadCustomData(vertices, indices, depths) {
        // 移除旧的网格
        scene.remove(mesh);
        
        // 创建新的几何体
        const newGeometry = new THREE.BufferGeometry();
        
        // 设置位置属性
        newGeometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
        
        // 设置索引
        newGeometry.setIndex(indices);
        
        // 计算法线
        newGeometry.computeVertexNormals();
        
        // 找出深度的最小值和最大值
        const newMinDepth = Math.min(...depths);
        const newMaxDepth = Math.max(...depths);
        
        // 根据深度值创建顶点颜色
        const newColors = [];
        for (let i = 0; i < depths.length; i++) {
          const color = mapDepthToColor(depths[i], newMinDepth, newMaxDepth);
          newColors.push(color.r, color.g, color.b);
        }
        
        // 将颜色添加到几何体中
        newGeometry.setAttribute('color', new THREE.Float32BufferAttribute(newColors, 3));
        
        // 创建新的网格
        const newMesh = new THREE.Mesh(newGeometry, material);
        scene.add(newMesh);
        
        // 更新网格引用
        mesh.geometry.dispose();
        Object.assign(mesh, newMesh);
        
        // 如果数据包含网格结构，可以更新等值线
        // 这里需要根据实际数据结构调整
        
        // 更新最小最大深度值
        Object.assign(minMaxDepth, { min: newMinDepth, max: newMaxDepth });
        minDepthController.updateDisplay();
        maxDepthController.updateDisplay();
      }
      
      // 将函数暴露到全局，方便外部调用
      window.loadCustomData = loadCustomData;

      // ---------------------------
      // 9. 动画循环
      // ---------------------------
      function animate() {
        requestAnimationFrame(animate);
        orbitControls.update();
        renderer.render(scene, camera);
        stats.update();
      }
      animate();

      // ---------------------------
      // 10. 监听窗口变化，保持自适应
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