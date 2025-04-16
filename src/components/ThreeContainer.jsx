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
      // 可根据需要添加雾化效果或背景色
      // scene.fog = new THREE.FogExp2(0xffffff, 0.2);
      scene.background = new THREE.Color(0xffffff);

      // 通过球面坐标 (r, θ, φ) 来设置相机初始位置
      // 公式参见：x = r*sin(φ)*sin(θ), y = r*cos(φ), z = r*sin(φ)*cos(θ)
      const r = 8;                          // 与目标点的距离
      const theta = Math.PI / 4;            // 水平旋转角度
      const phi = Math.PI / 3;              // 垂直旋转角度
      const x = r * Math.sin(phi) * Math.sin(theta);
      const y = r * Math.cos(phi);
      const z = r * Math.sin(phi) * Math.cos(theta);

      const camera = new THREE.PerspectiveCamera(
        60, // 视野角度 FOV
        window.innerWidth / window.innerHeight, // 宽高比
        0.1, // 最近面
        1000 // 最远面
      );
      camera.position.set(x, y, z);

      // 三维场景可视化渲染器
      const renderer = new THREE.WebGLRenderer({ antialias: true });
      renderer.setSize(window.innerWidth, window.innerHeight);
      containerRef.current.appendChild(renderer.domElement);

      // 若需要查看 FPS，可开启 stats
      // const stats = new Stats();
      // containerRef.current.appendChild(stats.dom);

      // ---------------------------
      // 2. 添加光源
      // ---------------------------
      // 环境光
      const ambientLight = new THREE.AmbientLight(0xffffff, 1.2);
      scene.add(ambientLight);
      // 方向光
      const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
      directionalLight.position.set(5, 10, 7.5);
      scene.add(directionalLight);

      // ---------------------------
      // 3. 创建OrbitControls控制器
      // ---------------------------
      const orbitControls = new OrbitControls(camera, renderer.domElement);
      // 将相机视线默认聚焦到场景原点 (0, 0, 0)
      orbitControls.target.set(0, 0, 0);
      orbitControls.enableDamping = true;     // 开启惯性
      orbitControls.dampingFactor = 0.05;     // 阻尼系数
      orbitControls.minDistance = 2;         // 最小缩放距离
      orbitControls.maxDistance = 20;        // 最大缩放距离
      orbitControls.update();

      // ---------------------------
      // 4. 在场景中添加示例几何体
      // ---------------------------

      // （1）网格辅助器 (GridHelper)，用于模拟地面
      const gridHelper = new THREE.GridHelper(40, 40);
      // scene.add(gridHelper);

      // （2）坐标轴辅助器 (AxesHelper)，X=红色、Y=绿色、Z=蓝色
      const axesHelper = new THREE.AxesHelper(5);
      scene.add(axesHelper);

      // （3）地面平面
      const planeGeometry = new THREE.PlaneGeometry(10, 10);
      const planeMaterial = new THREE.MeshStandardMaterial({ color: 0xaaaaaa, side: THREE.DoubleSide });
      const plane = new THREE.Mesh(planeGeometry, planeMaterial);
      plane.rotation.x = -Math.PI / 2; 
      plane.position.y = -1; // 在 Z 轴“向下” 10
      scene.add(plane);

      // （4）立方体
      const cubeGeometry = new THREE.BoxGeometry(1, 1, 1);
      const cubeMaterial = new THREE.MeshStandardMaterial({ color: 0x00ff00 });
      const cube = new THREE.Mesh(cubeGeometry, cubeMaterial);
      cube.position.set(0, 0.5, 0);
      scene.add(cube);

      // ---------------------------
      // 5. 动画循环
      // ---------------------------
      function animate() {
        requestAnimationFrame(animate);

        orbitControls.update();
        renderer.render(scene, camera);

        // 若开启了 stats，则需要更新
        // stats.update();
      }
      animate();

      // ---------------------------
      // 6. 监听窗口变化，保持自适应
      // ---------------------------
      window.addEventListener('resize', onWindowResize, false);
      function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
      }

      // 如果需要简单的 GUI，可以开启 lil-gui
      // const gui = new GUI();
      // gui.add(orbitControls, 'enableDamping').name('Enable Damping');
      // gui.open();
    }
  }, []);

  return <div ref={containerRef} />;
}

export default ThreeContainer;