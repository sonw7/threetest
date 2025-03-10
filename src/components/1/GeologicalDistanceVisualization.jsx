import React, { useRef, useEffect } from 'react';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';

const GeologicalDistanceVisualization = () => {
  const containerRef = useRef(null);
  
  useEffect(() => {
    if (!containerRef.current) return;
    
    // 场景设置
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0xf0f0f0);
    
    // 相机设置
    const camera = new THREE.PerspectiveCamera(
      60, 
      window.innerWidth / window.innerHeight, 
      0.1, 
      1000
    );
    camera.position.set(5, 5, 5);
    
    // 渲染器设置
    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    containerRef.current.appendChild(renderer.domElement);
    
    // 控制器设置
    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    
    // 添加灯光
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
    scene.add(ambientLight);
    
    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(5, 10, 7);
    scene.add(directionalLight);
    
    // 添加坐标轴
    const axesHelper = new THREE.AxesHelper(4);
    scene.add(axesHelper);
    
    // 创建文本标签函数
    function createTextLabel(text, position, color = 0x000000) {
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d');
      canvas.width = 256;
      canvas.height = 128;
      
      context.font = 'Bold 24px Arial';
      context.fillStyle = `#${color.toString(16).padStart(6, '0')}`;
      context.textAlign = 'center';
      context.fillText(text, 128, 64);
      
      const texture = new THREE.CanvasTexture(canvas);
      texture.needsUpdate = true;
      
      const spriteMaterial = new THREE.SpriteMaterial({
        map: texture,
        transparent: true
      });
      
      const sprite = new THREE.Sprite(spriteMaterial);
      sprite.position.copy(position);
      sprite.scale.set(1, 0.5, 1);
      
      scene.add(sprite);
      return sprite;
    }
    
    // 添加坐标轴标签
    createTextLabel('X', new THREE.Vector3(4.2, 0, 0), 0xff0000);
    createTextLabel('Y', new THREE.Vector3(0, 4.2, 0), 0x00ff00);
    createTextLabel('Z', new THREE.Vector3(0, 0, 4.2), 0x0000ff);
    
    // 创建下方地层（目标地层）
    const targetGeometry = new THREE.BufferGeometry();
    
    // 定义顶点
    const vertices = new Float32Array([
      -2, 0, -2,   // V1
      2, 0, -2,    // V2
      2, 0, 2,     // V3
      -2, 0, 2,    // V4
      0, -0.5, 0,  // 中心点，稍微下凹
      -1, -0.3, -1, // 额外点，使表面不规则
      1, -0.3, -1,  // 额外点，使表面不规则
      1, -0.2, 1,   // 额外点，使表面不规则
      -1, -0.2, 1   // 额外点，使表面不规则
    ]);
    
    // 定义面的索引
    const indices = [
      0, 5, 4,  // 三角形 V1-V5-Center
      5, 1, 6,  // 三角形 V5-V2-V6
      6, 1, 4,  // 三角形 V6-V2-Center
      1, 2, 4,  // 三角形 V2-V3-Center
      2, 7, 4,  // 三角形 V3-V7-Center
      7, 2, 3,  // 三角形 V7-V3-V4
      3, 8, 4,  // 三角形 V4-V8-Center
      8, 3, 0,  // 三角形 V8-V4-V1
      0, 4, 8   // 三角形 V1-Center-V8
    ];
    
    targetGeometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3));
    targetGeometry.setIndex(indices);
    targetGeometry.computeVertexNormals();
    
    const targetMaterial = new THREE.MeshPhongMaterial({
      color: 0x2194ce,
      side: THREE.DoubleSide,
      transparent: true,
      opacity: 0.7,
      wireframe: false
    });
    
    const targetWireframeMaterial = new THREE.MeshBasicMaterial({
      color: 0x000000,
      wireframe: true,
      transparent: true,
      opacity: 0.3
    });
    
    const targetMesh = new THREE.Mesh(targetGeometry, targetMaterial);
    scene.add(targetMesh);
    
    const targetWireframe = new THREE.Mesh(targetGeometry, targetWireframeMaterial);
    scene.add(targetWireframe);
    
    // 创建上方地层（源地层）
    const sourceGeometry = new THREE.PlaneGeometry(4, 4, 20, 20);
    
    // 创建波浪效果
    const sourcePositions = sourceGeometry.attributes.position;
    for (let i = 0; i < sourcePositions.count; i++) {
      const x = sourcePositions.getX(i);
      const z = sourcePositions.getZ(i);
      // 创建波浪效果
      const y = 2 + 0.2 * Math.sin(x * 2) * Math.cos(z * 2);
      sourcePositions.setY(i, y);
    }
    
    sourceGeometry.computeVertexNormals();
    
    const sourceMaterial = new THREE.MeshPhongMaterial({
      color: 0xffa500,
      side: THREE.DoubleSide,
      transparent: true,
      opacity: 0.7,
      wireframe: false
    });
    
    const sourceWireframeMaterial = new THREE.MeshBasicMaterial({
      color: 0x000000,
      wireframe: true,
      transparent: true,
      opacity: 0.3
    });
    
    const sourceMesh = new THREE.Mesh(sourceGeometry, sourceMaterial);
    scene.add(sourceMesh);
    
    const sourceWireframe = new THREE.Mesh(sourceGeometry, sourceWireframeMaterial);
    scene.add(sourceWireframe);
    
    // 在源地层上选择一个顶点作为源点 P_s
    const sourcePoint = new THREE.Vector3(0.5, 2.1, 0.5);
    
    // 创建源点标记
    const sourcePointGeometry = new THREE.SphereGeometry(0.08, 16, 16);
    const sourcePointMaterial = new THREE.MeshBasicMaterial({color: 0xff0000});
    const sourcePointMesh = new THREE.Mesh(sourcePointGeometry, sourcePointMaterial);
    sourcePointMesh.position.copy(sourcePoint);
    scene.add(sourcePointMesh);
    
    // 添加源点标签
    createTextLabel('P_s(x_s,y_s,z_s)', new THREE.Vector3(sourcePoint.x + 0.2, sourcePoint.y + 0.2, sourcePoint.z), 0xff0000);
    
    // 选择目标地层上的一个三角形
    const triangleIndex = 4; // 选择合适的三角形
    const v1Index = indices[triangleIndex * 3];
    const v2Index = indices[triangleIndex * 3 + 1];
    const v3Index = indices[triangleIndex * 3 + 2];
    
    const v1 = new THREE.Vector3(vertices[v1Index * 3], vertices[v1Index * 3 + 1], vertices[v1Index * 3 + 2]);
    const v2 = new THREE.Vector3(vertices[v2Index * 3], vertices[v2Index * 3 + 1], vertices[v2Index * 3 + 2]);
    const v3 = new THREE.Vector3(vertices[v3Index * 3], vertices[v3Index * 3 + 1], vertices[v3Index * 3 + 2]);
    
    // 高亮显示选中的三角形
    const highlightGeometry = new THREE.BufferGeometry();
    highlightGeometry.setFromPoints([v1, v2, v3, v1]);
    
    const highlightMaterial = new THREE.LineBasicMaterial({color: 0xff00ff, linewidth: 2});
    const highlightLine = new THREE.Line(highlightGeometry, highlightMaterial);
    scene.add(highlightLine);
    
    // 添加三角形顶点标记和标签
    const vertexGeometry = new THREE.SphereGeometry(0.06, 16, 16);
    const vertexMaterial = new THREE.MeshBasicMaterial({color: 0xff00ff});
    
    const v1Mesh = new THREE.Mesh(vertexGeometry, vertexMaterial);
    v1Mesh.position.copy(v1);
    scene.add(v1Mesh);
    createTextLabel('V_1', new THREE.Vector3(v1.x, v1.y - 0.2, v1.z), 0xff00ff);
    
    const v2Mesh = new THREE.Mesh(vertexGeometry, vertexMaterial);
    v2Mesh.position.copy(v2);
    scene.add(v2Mesh);
    createTextLabel('V_2', new THREE.Vector3(v2.x, v2.y - 0.2, v2.z), 0xff00ff);
    
    const v3Mesh = new THREE.Mesh(vertexGeometry, vertexMaterial);
    v3Mesh.position.copy(v3);
    scene.add(v3Mesh);
    createTextLabel('V_3', new THREE.Vector3(v3.x, v3.y - 0.2, v3.z), 0xff00ff);
    
    // 计算三角形的法向量
    const edge1 = new THREE.Vector3().subVectors(v2, v1);
    const edge2 = new THREE.Vector3().subVectors(v3, v1);
    const normal = new THREE.Vector3().crossVectors(edge1, edge2).normalize();
    
    // 计算三角形中心点
    const triangleCenter = new THREE.Vector3().addVectors(v1, v2).add(v3).divideScalar(3);
    
    // 显示法向量
    const normalLength = 0.5;
    const normalArrowHelper = new THREE.ArrowHelper(
      normal,
      triangleCenter,
      normalLength,
      0x0000ff,
      0.1,
      0.05
    );
    scene.add(normalArrowHelper);
    
    // 添加法向量标签
    createTextLabel('N(a,b,c)', new THREE.Vector3(
      triangleCenter.x + normal.x * normalLength * 1.2,
      triangleCenter.y + normal.y * normalLength * 1.2,
      triangleCenter.z + normal.z * normalLength * 1.2
    ), 0x0000ff);
    
    // 计算射线方向（垂直向下）
    const rayDirection = new THREE.Vector3(0, -1, 0);
    
    // 计算射线与三角形的交点
    const raycaster = new THREE.Raycaster(sourcePoint, rayDirection);
    const intersects = raycaster.intersectObject(targetMesh);
    
    let intersectionPoint;
    if (intersects.length > 0) {
      intersectionPoint = intersects[0].point;
    } else {
      // 如果没有交点，创建一个模拟的交点
      intersectionPoint = new THREE.Vector3(sourcePoint.x, 0, sourcePoint.z);
    }
    
    // 创建交点标记
    const intersectionGeometry = new THREE.SphereGeometry(0.08, 16, 16);
    const intersectionMaterial = new THREE.MeshBasicMaterial({color: 0xffff00});
    const intersectionMesh = new THREE.Mesh(intersectionGeometry, intersectionMaterial);
    intersectionMesh.position.copy(intersectionPoint);
    scene.add(intersectionMesh);
    
    // 添加交点标签
    createTextLabel('P(交点)', new THREE.Vector3(intersectionPoint.x + 0.2, intersectionPoint.y, intersectionPoint.z), 0xffff00);
    
    // 创建射线可视化
    const rayGeometry = new THREE.BufferGeometry().setFromPoints([
      sourcePoint,
      intersectionPoint
    ]);
    
    const rayMaterial = new THREE.LineDashedMaterial({
      color: 0x00ff00,
      dashSize: 0.1,
      gapSize: 0.05,
      linewidth: 2
    });
    
    const ray = new THREE.Line(rayGeometry, rayMaterial);
    ray.computeLineDistances(); // 必须调用此方法使虚线生效
    scene.add(ray);
    
    // 添加射线标签
    const rayMidPoint = new THREE.Vector3().addVectors(sourcePoint, intersectionPoint).divideScalar(2);
    createTextLabel('射线 R(t)', new THREE.Vector3(rayMidPoint.x + 0.3, rayMidPoint.y, rayMidPoint.z), 0x00ff00);
    createTextLabel('D(0,0,Δz)', new THREE.Vector3(rayMidPoint.x - 0.3, rayMidPoint.y, rayMidPoint.z), 0x00ff00);
    
    // 计算距离
    const distance = sourcePoint.distanceTo(intersectionPoint);
    
    // 创建距离线
    const distanceGeometry = new THREE.BufferGeometry().setFromPoints([
      new THREE.Vector3(sourcePoint.x + 0.2, sourcePoint.y, sourcePoint.z),
      new THREE.Vector3(intersectionPoint.x + 0.2, intersectionPoint.y, intersectionPoint.z)
    ]);
    
    const distanceMaterial = new THREE.LineDashedMaterial({
      color: 0xffffff,
      dashSize: 0.1,
      gapSize: 0.05,
      linewidth: 1
    });
    
    const distanceLine = new THREE.Line(distanceGeometry, distanceMaterial);
    distanceLine.computeLineDistances();
    scene.add(distanceLine);
    
    // 添加距离标签
    const distanceMidPoint = new THREE.Vector3(
      sourcePoint.x + 0.4,
      (sourcePoint.y + intersectionPoint.y) / 2,
      sourcePoint.z
    );
    createTextLabel(`d_i = ${distance.toFixed(2)}`, distanceMidPoint, 0xffffff);
    
    // 添加公式说明
    const formulaPosition = new THREE.Vector3(-2, -1, -2);
    createTextLabel('射线方程: R(t) = P_s + t·D, t≥0', formulaPosition, 0x000000);
    createTextLabel('平面方程: ax + by + cz + d = 0', new THREE.Vector3(formulaPosition.x, formulaPosition.y - 0.3, formulaPosition.z), 0x000000);
    createTextLabel('距离计算: d_i = √((x_i-x_s)² + (y_i-y_s)² + (z_i-z_s)²)', new THREE.Vector3(formulaPosition.x, formulaPosition.y - 0.6, formulaPosition.z), 0x000000);
    
    // 动画循环
    const animate = () => {
      requestAnimationFrame(animate);
      controls.update();
      renderer.render(scene, camera);
    };
    
    animate();
    
    // 处理窗口大小变化
    const handleResize = () => {
      camera.aspect = window.innerWidth / window.innerHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth, window.innerHeight);
    };
    
    window.addEventListener('resize', handleResize);
    
    // 清理函数
    return () => {
      window.removeEventListener('resize', handleResize);
      
      // 清理场景资源
      scene.traverse((object) => {
        if (object.geometry) object.geometry.dispose();
        if (object.material) {
          if (Array.isArray(object.material)) {
            object.material.forEach(material => material.dispose());
          } else {
            object.material.dispose();
          }
        }
      });
      
      renderer.dispose();
      if (containerRef.current) {
        containerRef.current.removeChild(renderer.domElement);
      }
    };
  }, []);
  
  return (
    <div style={{ position: 'relative', width: '100%', height: '100vh' }}>
      <div ref={containerRef} style={{ width: '100%', height: '100%' }}></div>
      <div style={{ position: 'absolute', top: '10px', width: '100%', textAlign: 'center', color: 'black', fontFamily: 'Arial, sans-serif', fontWeight: 'bold' }}>
        三维地层间距计算示意图
      </div>
      <div style={{ position: 'absolute', bottom: '20px', right: '20px', backgroundColor: 'rgba(255,255,255,0.7)', padding: '10px', borderRadius: '5px', fontFamily: 'Arial, sans-serif', fontSize: '14px' }}>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#ffa500' }}></div>
          源地层
        </div>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#2194ce' }}></div>
          目标地层
        </div>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#ff0000' }}></div>
          源点 P_s
        </div>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#00ff00' }}></div>
          射线 R(t)
        </div>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#ffff00' }}></div>
          交点 P
        </div>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#0000ff' }}></div>
          法向量 N
        </div>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#ff00ff' }}></div>
          三角形顶点
        </div>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <div style={{ width: '15px', height: '15px', marginRight: '10px', backgroundColor: '#ffffff' }}></div>
          距离 d_i
        </div>
      </div>
    </div>
  );
};

export default GeologicalDistanceVisualization;