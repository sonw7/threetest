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