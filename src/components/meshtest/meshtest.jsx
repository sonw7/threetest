import React, { useEffect, useRef, useState } from 'react';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import Stats from 'three/examples/jsm/libs/stats.module';

const MeshOptimizationDemo = () => {
  const mountRef = useRef(null);
  const sceneRef = useRef(null);
  const rendererRef = useRef(null);
  const cameraRef = useRef(null);
  const controlsRef = useRef(null);
  const statsRef = useRef(null);
  const originalGroupRef = useRef(null);
  const optimizedGroupRef = useRef(null);
  const animationIdRef = useRef(null);
  const originalGeometryRef = useRef(null);
  const optimizedGeometryRef = useRef(null);
  const originalWireframeRef = useRef(null);
  const optimizedWireframeRef = useRef(null);

  const [threshold, setThreshold] = useState(5);
  const [triangleCountOriginal, setTriangleCountOriginal] = useState(0);
  const [triangleCountOptimized, setTriangleCountOptimized] = useState(0);
  const [showOriginal, setShowOriginal] = useState(true);
  const [showOptimized, setShowOptimized] = useState(true);
  const [showOriginalWireframe, setShowOriginalWireframe] = useState(true);
  const [showOptimizedWireframe, setShowOptimizedWireframe] = useState(true);

  // 初始化Three.js场景
  useEffect(() => {
    // 创建场景
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0xe4dfde);
    sceneRef.current = scene;

    // 创建相机
    const camera = new THREE.PerspectiveCamera(
      60,
      window.innerWidth / window.innerHeight,
      0.1,
      1000
    );
    camera.position.set(0, 2, 5);
    cameraRef.current = camera;

    // 创建渲染器
    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    mountRef.current.appendChild(renderer.domElement);
    rendererRef.current = renderer;

    // 添加轨道控制
    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;
    controlsRef.current = controls;

    // 添加性能监控
    const stats = new Stats();
    mountRef.current.appendChild(stats.dom);
    statsRef.current = stats;

    // 添加光源
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(10, 10, 10);
    scene.add(directionalLight);

    // 创建网格组
    const originalGroup = new THREE.Group();
    originalGroup.position.set(-2, 0, 0);
    scene.add(originalGroup);
    originalGroupRef.current = originalGroup;

    const optimizedGroup = new THREE.Group();
    optimizedGroup.position.set(2, 0, 0);
    scene.add(optimizedGroup);
    optimizedGroupRef.current = optimizedGroup;

    // 创建地形网格
    const originalGeometry = createTerrainMesh();
    originalGeometryRef.current = originalGeometry;
    setTriangleCountOriginal(originalGeometry.index.count / 3);

    // 创建原始网格的可视化
    createOriginalMeshVisualization(originalGeometry, originalGroup);

    // 创建优化网格
    updateOptimizedMesh(threshold);

    // 窗口大小调整事件
    const handleResize = () => {
      const width = window.innerWidth;
      const height = window.innerHeight;

      camera.aspect = width / height;
      camera.updateProjectionMatrix();
      renderer.setSize(width, height);
    };

    window.addEventListener('resize', handleResize);

    // 动画循环
    const animate = () => {
      animationIdRef.current = requestAnimationFrame(animate);
      controls.update();
      stats.update();
      renderer.render(scene, camera);
    };
    animate();

    // 组件卸载时清理资源
    return () => {
      window.removeEventListener('resize', handleResize);
      cancelAnimationFrame(animationIdRef.current);
      mountRef.current.removeChild(renderer.domElement);
      mountRef.current.removeChild(stats.dom);
      scene.clear();
      originalGeometry.dispose();
      if (optimizedGeometryRef.current) {
        optimizedGeometryRef.current.dispose();
      }
    };
  }, []);

  // 当阈值变化时更新优化网格
  useEffect(() => {
    if (originalGeometryRef.current) {
      updateOptimizedMesh(threshold);
    }
  }, [threshold]);

  // 当显示/隐藏状态变化时更新可见性
  useEffect(() => {
    if (originalGroupRef.current) {
      originalGroupRef.current.visible = showOriginal;
    }
  }, [showOriginal]);

  useEffect(() => {
    if (optimizedGroupRef.current) {
      optimizedGroupRef.current.visible = showOptimized;
    }
  }, [showOptimized]);

  // 当线框显示/隐藏状态变化时更新可见性
  useEffect(() => {
    if (originalWireframeRef.current) {
      originalWireframeRef.current.visible = showOriginalWireframe;
    }
  }, [showOriginalWireframe]);

  useEffect(() => {
    if (optimizedWireframeRef.current) {
      optimizedWireframeRef.current.visible = showOptimizedWireframe;
    }
  }, [showOptimizedWireframe]);

  // 创建地形网格函数
  const createTerrainMesh = () => {
    const width = 20;
    const height = 20;
    const segmentsX = 20;
    const segmentsY = 20;
    
    const geometry = new THREE.PlaneGeometry(width, height, segmentsX, segmentsY);
    
    // 添加地形起伏
    const vertices = geometry.attributes.position.array;
    for (let i = 0; i < vertices.length; i += 3) {
      const x = vertices[i];
      const y = vertices[i + 1];
      
      // 创建波浪和山丘
      const z1 = Math.sin(x * 0.5) * Math.cos(y * 0.5) * 1.5;
      const z2 = Math.sin(x * 0.25) * 0.5;
      const z3 = Math.cos(y * 0.25) * 0.5;
      
      // 添加随机扰动
      const noise = Math.random() * 0.2;
      
      vertices[i + 2] = z1 + z2 + z3 + noise;
    }
    
    // 更新法向量
    geometry.computeVertexNormals();
    
    // 转换为三角形几何体
    const bufferGeometry = new THREE.BufferGeometry();
    bufferGeometry.setAttribute('position', geometry.attributes.position);
    bufferGeometry.setAttribute('normal', geometry.attributes.normal);
    bufferGeometry.setIndex(geometry.index);
    
    return bufferGeometry;
  };

  // 创建原始网格可视化
  const createOriginalMeshVisualization = (geometry, group) => {
    // 清除之前的网格
    while (group.children.length > 0) {
      const child = group.children[0];
      if (child.geometry) {
        child.geometry.dispose();
      }
      if (child.material) {
        if (Array.isArray(child.material)) {
          child.material.forEach(material => material.dispose());
        } else {
          child.material.dispose();
        }
      }
      group.remove(child);
    }

    // 创建实体网格
    const solidMaterial = new THREE.MeshStandardMaterial({
      color: 0x87ceeb,
      side: THREE.DoubleSide,
      transparent: true,
      opacity: 0.6
    });
    const solidMesh = new THREE.Mesh(geometry, solidMaterial);
    group.add(solidMesh);

    // 创建线框网格
    const wireframeMaterial = new THREE.LineBasicMaterial({
      color: 0xff0000,
      linewidth: 1
    });
    
    // 从几何体创建边缘
    const wireframeGeometry = new THREE.WireframeGeometry(geometry);
    const wireframe = new THREE.LineSegments(wireframeGeometry, wireframeMaterial);
    group.add(wireframe);
    originalWireframeRef.current = wireframe;
  };

  // 更新优化网格
  const updateOptimizedMesh = (thresholdValue) => {
    if (!originalGeometryRef.current || !optimizedGroupRef.current) return;

    // 清除之前的网格
    const group = optimizedGroupRef.current;
    while (group.children.length > 0) {
      const child = group.children[0];
      if (child.geometry) {
        child.geometry.dispose();
      }
      if (child.material) {
        if (Array.isArray(child.material)) {
          child.material.forEach(material => material.dispose());
        } else {
          child.material.dispose();
        }
      }
      group.remove(child);
    }

    // 如果之前有优化几何体，先释放资源
    if (optimizedGeometryRef.current) {
      optimizedGeometryRef.current.dispose();
    }

    // 创建优化几何体
    const result = optimizeMesh(originalGeometryRef.current, thresholdValue);
    const optimizedGeometry = result.optimizedGeometry;
    const visualGeometry = result.visualGeometry;
    
    optimizedGeometryRef.current = optimizedGeometry;
    setTriangleCountOptimized(optimizedGeometry.index.count / 3);

    // 创建实体网格 - 使用完整的视觉几何体
    const solidMaterial = new THREE.MeshStandardMaterial({
      color: 0x90ee90,
      side: THREE.DoubleSide,
      transparent: true,
      opacity: 0.6
    });
    const solidMesh = new THREE.Mesh(visualGeometry, solidMaterial);
    group.add(solidMesh);

    // 创建线框网格 - 只显示优化后的线框
    const wireframeMaterial = new THREE.LineBasicMaterial({
      color: 0x0000ff,
      linewidth: 1
    });
    
    // 从优化几何体创建边缘
    const wireframeGeometry = new THREE.WireframeGeometry(optimizedGeometry);
    const wireframe = new THREE.LineSegments(wireframeGeometry, wireframeMaterial);
    group.add(wireframe);
    optimizedWireframeRef.current = wireframe;
    
    // 应用当前的线框显示状态
    if (wireframe) {
      wireframe.visible = showOptimizedWireframe;
    }
  };

  // 面片优化函数 - 返回优化几何体和视觉几何体
  const optimizeMesh = (geometry, thresholdDegrees) => {
    // 转换阈值从角度到弧度
    const thresholdRadians = thresholdDegrees * Math.PI / 180;
    const thresholdCosine = Math.cos(thresholdRadians);
    
    // 从几何体中提取顶点和法向量
    const positions = geometry.attributes.position.array;
    const normals = geometry.attributes.normal.array;
    const indices = geometry.index.array;
    
    // 创建三角形数组
    const triangles = [];
    for (let i = 0; i < indices.length; i += 3) {
      const a = indices[i];
      const b = indices[i + 1];
      const c = indices[i + 2];
      
      // 计算三角形的法向量 (使用顶点法向量的平均值)
      const normalX = (normals[a * 3] + normals[b * 3] + normals[c * 3]) / 3;
      const normalY = (normals[a * 3 + 1] + normals[b * 3 + 1] + normals[c * 3 + 1]) / 3;
      const normalZ = (normals[a * 3 + 2] + normals[b * 3 + 2] + normals[c * 3 + 2]) / 3;
      
      // 归一化法向量
      const length = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
      
      triangles.push({
        indices: [a, b, c],
        normal: [normalX/length, normalY/length, normalZ/length],
        visited: false,
        group: -1
      });
    }
    
    // 构建三角形邻接关系
    const buildAdjacency = (triangles) => {
      const adjacency = Array(triangles.length).fill().map(() => []);
      
      // 创建边到三角形的映射
      const edgeMap = new Map();
      
      triangles.forEach((triangle, triangleIndex) => {
        const [a, b, c] = triangle.indices;
        
        // 对三角形的每条边
        const edges = [
          [Math.min(a, b), Math.max(a, b)],
          [Math.min(b, c), Math.max(b, c)],
          [Math.min(c, a), Math.max(c, a)]
        ];
        
        edges.forEach(edge => {
          const edgeKey = `${edge[0]}-${edge[1]}`;
          
          if (edgeMap.has(edgeKey)) {
            // 找到共享这条边的另一个三角形
            const otherTriangleIndex = edgeMap.get(edgeKey);
            // 添加邻接关系
            adjacency[triangleIndex].push(otherTriangleIndex);
            adjacency[otherTriangleIndex].push(triangleIndex);
          } else {
            // 记录这条边属于当前三角形
            edgeMap.set(edgeKey, triangleIndex);
          }
        });
      });
      
      return adjacency;
    };
    
    // 构建邻接关系
    const adjacency = buildAdjacency(triangles);
    
    // 使用区域生长算法合并三角形
    let currentGroup = 0;
    
    for (let i = 0; i < triangles.length; i++) {
      if (triangles[i].visited) continue;
      
      // 开始一个新的组
      const queue = [i];
      triangles[i].visited = true;
      triangles[i].group = currentGroup;
      
      while (queue.length > 0) {
        const currentTriIndex = queue.shift();
        const currentTri = triangles[currentTriIndex];
        
        // 检查所有邻居
        for (const neighborIndex of adjacency[currentTriIndex]) {
          const neighbor = triangles[neighborIndex];
          
          if (neighbor.visited) continue;
          
          // 计算法向量夹角的余弦值
          const dotProduct = 
            currentTri.normal[0] * neighbor.normal[0] +
            currentTri.normal[1] * neighbor.normal[1] +
            currentTri.normal[2] * neighbor.normal[2];
          
          // 如果法向量足够相似，则合并到同一组
          if (dotProduct > thresholdCosine) {
            neighbor.visited = true;
            neighbor.group = currentGroup;
            queue.push(neighborIndex);
          }
        }
      }
      
      currentGroup++;
    }
    
    // 创建优化后的几何体索引
    const newIndices = [];
    const groupedTriangles = Array(currentGroup).fill().map(() => []);
    
    // 将三角形分组
    triangles.forEach((triangle, index) => {
      if (triangle.group >= 0) {
        groupedTriangles[triangle.group].push(index);
      }
    });
    
    // 对每个组，我们保留一些三角形作为该组的表示
    groupedTriangles.forEach(group => {
      if (group.length === 0) return;
      
      // 如果组内三角形数量小于某个阈值，保留所有三角形
      if (group.length <= 3) {
        group.forEach(triangleIndex => {
          const triangle = triangles[triangleIndex];
          newIndices.push(...triangle.indices);
        });
        return;
      }
      
      // 否则，我们选择性地保留一些三角形
      // 这里我们简单地保留组内的第一个、中间和最后一个三角形
      const samplesToKeep = Math.max(1, Math.floor(group.length / 5));
      const step = group.length / samplesToKeep;
      
      for (let i = 0; i < samplesToKeep; i++) {
        const index = Math.min(group.length - 1, Math.floor(i * step));
        const triangleIndex = group[index];
        const triangle = triangles[triangleIndex];
        newIndices.push(...triangle.indices);
      }
    });
    
    // 创建优化几何体 - 用于线框显示
    const optimizedGeometry = new THREE.BufferGeometry();
    optimizedGeometry.setAttribute('position', geometry.attributes.position);
    optimizedGeometry.setAttribute('normal', geometry.attributes.normal);
    optimizedGeometry.setIndex(new THREE.BufferAttribute(new Uint32Array(newIndices), 1));
    
    // 创建视觉几何体 - 与原始几何体相同，用于表面显示
    const visualGeometry = geometry.clone();
    
    return {
      optimizedGeometry,
      visualGeometry
    };
  };

  return (
    <div style={{ width: '100vw', height: '100vh', position: 'relative' }}>
      <div 
        ref={mountRef} 
        style={{ width: '100%', height: '100%' }}
      />
      <div 
        style={{ 
          position: 'absolute', 
          top: 10, 
          left: 10, 
          zIndex: 100, 
          background: 'rgba(0,0,0,0.7)', 
          padding: 10, 
          borderRadius: 5, 
          color: 'white' 
        }}
      >
        <h3>三角面片合并优化</h3>
        <div>
          <label>法向量夹角阈值: {threshold}° </label>
          <input 
            type="range" 
            min="0" 
            max="20" 
            value={threshold} 
            onChange={(e) => setThreshold(parseInt(e.target.value))} 
          />
        </div>
        <div>
          <label>
            <input 
              type="checkbox" 
              checked={showOriginal} 
              onChange={() => setShowOriginal(!showOriginal)} 
            />
            显示原始网格 (三角形数量: {triangleCountOriginal})
          </label>
        </div>
        <div style={{ marginLeft: 20 }}>
          <label>
            <input 
              type="checkbox" 
              checked={showOriginalWireframe} 
              onChange={() => setShowOriginalWireframe(!showOriginalWireframe)} 
              disabled={!showOriginal}
            />
            显示原始网格线框
          </label>
        </div>
        <div>
          <label>
            <input 
              type="checkbox" 
              checked={showOptimized} 
              onChange={() => setShowOptimized(!showOptimized)} 
            />
            显示优化网格 (三角形数量: {triangleCountOptimized})
          </label>
        </div>
        <div style={{ marginLeft: 20 }}>
          <label>
            <input 
              type="checkbox" 
              checked={showOptimizedWireframe} 
              onChange={() => setShowOptimizedWireframe(!showOptimizedWireframe)} 
              disabled={!showOptimized}
            />
            显示优化网格线框
          </label>
        </div>
      </div>
    </div>
  );
};

export default MeshOptimizationDemo;