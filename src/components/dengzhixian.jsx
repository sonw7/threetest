// src/components/Dengzhixian.jsx

import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import Stats from 'three/addons/libs/stats.module.js';
import { GUI } from 'three/addons/libs/lil-gui.module.min.js';
import { useRef, useEffect } from 'react';

function Dengzhixian() {
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
      // 5. 创建三维三角网格并根据深度着色
      // ---------------------------
      
      // 生成示例三维数据 (在实际应用中，这些数据可能来自外部)
      function generateSample3DData() {
        // 创建一个三维地形示例
        const gridSize = 20; // 网格分辨率
        const size = 5; // 总大小
        
        // 顶点数组
        const vertices = [];
        // 索引数组
        const indices = [];
        // 深度数组 (与z坐标不同的物理量)
        const depths = [];
        
        // 生成顶点、z值和深度
        for (let i = 0; i <= gridSize; i++) {
          for (let j = 0; j <= gridSize; j++) {
            const x = (i / gridSize) * size - size / 2;
            const y = (j / gridSize) * size - size / 2;
            
            // 生成z坐标 - 创建起伏的表面
            const z = Math.sin(x * 1.0) * Math.cos(y * 1.0) * 0.8;
            
            // 添加顶点坐标
            vertices.push(x, y, z);
            
            // 生成深度值 (这可能与z坐标不同，表示其他物理量)
            // 这里我们用一个不同的函数
            const depth = Math.cos(x * 1.5) * Math.sin(y * 1.5) * 0.5;
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
      const sampleData = generateSample3DData();
      
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
      // 6. 创建三维等值线
      // ---------------------------
      
      // 存储等值线对象
      let contourLines = new THREE.Group();
      scene.add(contourLines);
      
      // 为三维网格创建三角形数据结构
      function createTriangleData(vertices, indices) {
        const triangles = [];
        for (let i = 0; i < indices.length; i += 3) {
          const idx1 = indices[i];
          const idx2 = indices[i + 1];
          const idx3 = indices[i + 2];
          
          const v1 = new THREE.Vector3(
            vertices[idx1 * 3], 
            vertices[idx1 * 3 + 1], 
            vertices[idx1 * 3 + 2]
          );
          
          const v2 = new THREE.Vector3(
            vertices[idx2 * 3], 
            vertices[idx2 * 3 + 1], 
            vertices[idx2 * 3 + 2]
          );
          
          const v3 = new THREE.Vector3(
            vertices[idx3 * 3], 
            vertices[idx3 * 3 + 1], 
            vertices[idx3 * 3 + 2]
          );
          
          triangles.push({
            vertices: [v1, v2, v3],
            indices: [idx1, idx2, idx3]
          });
        }
        return triangles;
      }
      
      // 三维等值线生成 - 行进三角形算法 (Marching Triangles)
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
      
      // 初始生成三维等值线
      const initialContourLevels = calculateContourLevels(minDepth, maxDepth, contourParams.contourCount);
      generate3DContourLines(sampleData.vertices, sampleData.indices, sampleData.depths, initialContourLevels);
      
      // 更新等值线
      function updateContourLines() {
        if (contourParams.showContours) {
          const levels = calculateContourLevels(minDepth, maxDepth, contourParams.contourCount);
          generate3DContourLines(sampleData.vertices, sampleData.indices, sampleData.depths, levels);
          
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
        showVertices: false,
        resetCamera: function() {
          camera.position.set(x, y, z);
          orbitControls.target.set(0, 0, 0);
          orbitControls.update();
        },
        regenerateData: function() {
          // 重新生成深度值，保持几何形状不变
          const positions = geometry.attributes.position.array;
          
          // 生成新的深度值
          for (let i = 0; i < sampleData.depths.length; i++) {
            const x = positions[i * 3];
            const y = positions[i * 3 + 1];
            
            // 随机生成新的深度函数
            const frequency = Math.random() * 3 + 1;
            const depth = Math.cos(x * frequency) * Math.sin(y * frequency) * 0.5;
            
            sampleData.depths[i] = depth;
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
          
          // 更新等值线
          updateContourLines();
        }
      };
      
      // 顶点可视化
      let vertexMarkers = null;
      
      function toggleVertexMarkers(show) {
        if (show) {
          if (vertexMarkers) scene.remove(vertexMarkers);
          
          vertexMarkers = new THREE.Group();
          const positions = geometry.attributes.position.array;
          const markerGeometry = new THREE.SphereGeometry(0.05, 8, 8);
          const markerMaterial = new THREE.MeshBasicMaterial({ color: 0x000000 });
          
          for (let i = 0; i < positions.length; i += 3) {
            const marker = new THREE.Mesh(markerGeometry, markerMaterial);
            marker.position.set(positions[i], positions[i+1], positions[i+2]);
            vertexMarkers.add(marker);
          }
          
          scene.add(vertexMarkers);
        } else if (vertexMarkers) {
          scene.remove(vertexMarkers);
          vertexMarkers = null;
        }
      }
      
      const gui = new GUI();
      
      // 添加控制选项
      gui.add(params, 'wireframe').onChange(value => {
        material.wireframe = value;
      });
      
      gui.add(params, 'flatShading').onChange(value => {
        material.flatShading = value;
        material.needsUpdate = true;
      });
      
      gui.add(params, 'showVertices').onChange(toggleVertexMarkers);
      
      gui.add(params, 'resetCamera').name('Reset Camera');
      gui.add(params, 'regenerateData').name('New Depth Values');
      
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
        
        // 保存新数据
        sampleData.vertices = vertices;
        sampleData.indices = indices;
        sampleData.depths = depths;
        
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
        
        // 更新最小最大深度值
        Object.assign(minMaxDepth, { min: newMinDepth, max: newMaxDepth });
        minDepthController.updateDisplay();
        maxDepthController.updateDisplay();
        
        // 更新等值线
        generate3DContourLines(vertices, indices, depths, 
          calculateContourLevels(newMinDepth, newMaxDepth, contourParams.contourCount));
        
        // 如果顶点标记开启，则更新顶点标记
        if (params.showVertices) {
          toggleVertexMarkers(false);
          toggleVertexMarkers(true);
        }
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

export default Dengzhixian;