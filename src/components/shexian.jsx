import * as THREE from "three";
import { useEffect, useRef, useState } from "react";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls";
import { GUI } from "three/examples/jsm/libs/lil-gui.module.min.js";

export default function CameraVisualization() {
  const mountRef = useRef(null);
  const [cameraParams, setCameraParams] = useState({
    fov: 50,
    near: 1,
    far: 20,
    position: [0, 0, 10]
  });

  useEffect(() => {
    // 场景设置
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0x1a1a1a);

    // 创建网格地面
    const gridHelper = new THREE.GridHelper(30, 30, 0x555555, 0x333333);
    scene.add(gridHelper);

    // 设置坐标轴
    const axesHelper = new THREE.AxesHelper(5);
    scene.add(axesHelper);

    // 渲染器设置
    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    mountRef.current.appendChild(renderer.domElement);

    // 观察相机 - 这是我们用来查看场景的相机
    const viewCamera = new THREE.PerspectiveCamera(
      75, 
      window.innerWidth / window.innerHeight, 
      0.1, 
      100
    );
    viewCamera.position.set(15, 10, 15);
    viewCamera.lookAt(0, 0, 0);

    // 控制器
    const controls = new OrbitControls(viewCamera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;

    // 灯光
    const ambientLight = new THREE.AmbientLight(0x404040, 1);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(5, 10, 5);
    scene.add(directionalLight);

    // 创建被可视化的相机 - 这是我们要展示的透视相机
    const visualizedCamera = new THREE.PerspectiveCamera(
      cameraParams.fov,
      window.innerWidth / window.innerHeight,
      cameraParams.near,
      cameraParams.far
    );
    visualizedCamera.position.set(...cameraParams.position);
    scene.add(visualizedCamera);

    // 相机辅助对象 - 用于可视化相机的视锥体
    const cameraHelper = new THREE.CameraHelper(visualizedCamera);
    scene.add(cameraHelper);

    // 创建相机视锥体可视化
    function createFrustumVisualization() {
      // 如果已存在，则先移除旧的视锥体
      scene.children.forEach(child => {
        if (child.name === "frustumVisualization") {
          scene.remove(child);
        }
      });

      // 计算视锥体的8个顶点
      const aspect = window.innerWidth / window.innerHeight;
      const tanFov = Math.tan(THREE.MathUtils.degToRad(visualizedCamera.fov / 2));
      
      const nearHeight = 2 * tanFov * visualizedCamera.near;
      const nearWidth = nearHeight * aspect;
      const farHeight = 2 * tanFov * visualizedCamera.far;
      const farWidth = farHeight * aspect;

      // 近平面四个角
      const nearTopLeft = new THREE.Vector3(-nearWidth/2, nearHeight/2, -visualizedCamera.near);
      const nearTopRight = new THREE.Vector3(nearWidth/2, nearHeight/2, -visualizedCamera.near);
      const nearBottomLeft = new THREE.Vector3(-nearWidth/2, -nearHeight/2, -visualizedCamera.near);
      const nearBottomRight = new THREE.Vector3(nearWidth/2, -nearHeight/2, -visualizedCamera.near);

      // 远平面四个角
      const farTopLeft = new THREE.Vector3(-farWidth/2, farHeight/2, -visualizedCamera.far);
      const farTopRight = new THREE.Vector3(farWidth/2, farHeight/2, -visualizedCamera.far);
      const farBottomLeft = new THREE.Vector3(-farWidth/2, -farHeight/2, -visualizedCamera.far);
      const farBottomRight = new THREE.Vector3(farWidth/2, -farHeight/2, -visualizedCamera.far);

      // 创建线框几何体
      const frustumGeometry = new THREE.BufferGeometry();
      const vertices = [];

      // 近平面
      vertices.push(nearTopLeft.x, nearTopLeft.y, nearTopLeft.z);
      vertices.push(nearTopRight.x, nearTopRight.y, nearTopRight.z);

      vertices.push(nearTopRight.x, nearTopRight.y, nearTopRight.z);
      vertices.push(nearBottomRight.x, nearBottomRight.y, nearBottomRight.z);

      vertices.push(nearBottomRight.x, nearBottomRight.y, nearBottomRight.z);
      vertices.push(nearBottomLeft.x, nearBottomLeft.y, nearBottomLeft.z);

      vertices.push(nearBottomLeft.x, nearBottomLeft.y, nearBottomLeft.z);
      vertices.push(nearTopLeft.x, nearTopLeft.y, nearTopLeft.z);

      // 远平面
      vertices.push(farTopLeft.x, farTopLeft.y, farTopLeft.z);
      vertices.push(farTopRight.x, farTopRight.y, farTopRight.z);

      vertices.push(farTopRight.x, farTopRight.y, farTopRight.z);
      vertices.push(farBottomRight.x, farBottomRight.y, farBottomRight.z);

      vertices.push(farBottomRight.x, farBottomRight.y, farBottomRight.z);
      vertices.push(farBottomLeft.x, farBottomLeft.y, farBottomLeft.z);

      vertices.push(farBottomLeft.x, farBottomLeft.y, farBottomLeft.z);
      vertices.push(farTopLeft.x, farTopLeft.y, farTopLeft.z);

      // 连接近平面和远平面
      vertices.push(nearTopLeft.x, nearTopLeft.y, nearTopLeft.z);
      vertices.push(farTopLeft.x, farTopLeft.y, farTopLeft.z);

      vertices.push(nearTopRight.x, nearTopRight.y, nearTopRight.z);
      vertices.push(farTopRight.x, farTopRight.y, farTopRight.z);

      vertices.push(nearBottomRight.x, nearBottomRight.y, nearBottomRight.z);
      vertices.push(farBottomRight.x, farBottomRight.y, farBottomRight.z);

      vertices.push(nearBottomLeft.x, nearBottomLeft.y, nearBottomLeft.z);
      vertices.push(farBottomLeft.x, farBottomLeft.y, farBottomLeft.z);

      frustumGeometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
      
      // 创建视锥体线框材质
      const frustumMaterial = new THREE.LineBasicMaterial({ 
        color: 0x00ffff, 
        linewidth: 2,
        transparent: true,
        opacity: 0.8
      });
      
      // 创建视锥体线框
      const frustumLines = new THREE.LineSegments(frustumGeometry, frustumMaterial);
      frustumLines.name = "frustumVisualization";
      
      // 将视锥体线框添加到相机本地坐标系
      visualizedCamera.add(frustumLines);

      // 创建近平面和远平面可视化
      const nearPlaneGeometry = new THREE.PlaneGeometry(nearWidth, nearHeight);
      const farPlaneGeometry = new THREE.PlaneGeometry(farWidth, farHeight);
      
      const nearPlaneMaterial = new THREE.MeshBasicMaterial({ 
        color: 0x00ffff, 
        side: THREE.DoubleSide,
        transparent: true,
        opacity: 0.2,
        wireframe: true
      });
      
      const farPlaneMaterial = new THREE.MeshBasicMaterial({ 
        color: 0xff00ff, 
        side: THREE.DoubleSide,
        transparent: true,
        opacity: 0.2,
        wireframe: true
      });
      
      const nearPlane = new THREE.Mesh(nearPlaneGeometry, nearPlaneMaterial);
      const farPlane = new THREE.Mesh(farPlaneGeometry, farPlaneMaterial);
      
      nearPlane.position.z = -visualizedCamera.near;
      farPlane.position.z = -visualizedCamera.far;
      
      nearPlane.name = "nearPlane";
      farPlane.name = "farPlane";
      
      visualizedCamera.add(nearPlane);
      visualizedCamera.add(farPlane);
      
      // 添加标签
      const nearLabel = createTextLabel("Near Plane", 0, 0, -visualizedCamera.near, 0x00ffff);
      const farLabel = createTextLabel("Far Plane", 0, 0, -visualizedCamera.far, 0xff00ff);
      const fovLabel = createTextLabel(`FOV: ${visualizedCamera.fov}°`, 0, 2, -visualizedCamera.near, 0xffff00);
      
      visualizedCamera.add(nearLabel);
      visualizedCamera.add(farLabel);
      visualizedCamera.add(fovLabel);
    }

    // 创建文本标签的函数
    function createTextLabel(text, x, y, z, color) {
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d');
      canvas.width = 256;
      canvas.height = 128;
      
      context.fillStyle = 'rgba(0, 0, 0, 0.8)';
      context.fillRect(0, 0, canvas.width, canvas.height);
      
      context.font = '24px Arial';
      context.fillStyle = `#${new THREE.Color(color).getHexString()}`;
      context.textAlign = 'center';
      context.textBaseline = 'middle';
      context.fillText(text, canvas.width / 2, canvas.height / 2);
      
      const texture = new THREE.CanvasTexture(canvas);
      const material = new THREE.SpriteMaterial({ map: texture });
      const sprite = new THREE.Sprite(material);
      
      sprite.position.set(x, y, z);
      sprite.scale.set(2, 1, 1);
      
      return sprite;
    }

    // 添加一些测试对象到场景中
    function addTestObjects() {
      // 添加一些球体作为参照物
      for (let i = 0; i < 10; i++) {
        const sphereGeometry = new THREE.SphereGeometry(0.3, 16, 16);
        const sphereMaterial = new THREE.MeshStandardMaterial({ 
          color: new THREE.Color(Math.random(), Math.random(), Math.random()),
          metalness: 0.3,
          roughness: 0.4
        });
        const sphere = new THREE.Mesh(sphereGeometry, sphereMaterial);
        
        // 随机位置
        sphere.position.set(
          (Math.random() - 0.5) * 20,
          (Math.random() - 0.5) * 20,
          (Math.random() - 0.5) * 20
        );
        
        scene.add(sphere);
      }
    }

    // 添加GUI控制面板
    const gui = new GUI();
    const cameraFolder = gui.addFolder('Camera Parameters');
    
    cameraFolder.add(cameraParams, 'fov', 10, 120).name('FOV (degrees)').onChange(value => {
      visualizedCamera.fov = value;
      visualizedCamera.updateProjectionMatrix();
      createFrustumVisualization();
    });
    
    cameraFolder.add(cameraParams, 'near', 0.1, 10).name('Near Plane').onChange(value => {
      visualizedCamera.near = value;
      visualizedCamera.updateProjectionMatrix();
      createFrustumVisualization();
    });
    
    cameraFolder.add(cameraParams, 'far', 5, 50).name('Far Plane').onChange(value => {
      visualizedCamera.far = value;
      visualizedCamera.updateProjectionMatrix();
      createFrustumVisualization();
    });
    
    const positionFolder = cameraFolder.addFolder('Camera Position');
    
    positionFolder.add(cameraParams.position, 0, -10, 10).name('X').onChange(value => {
      visualizedCamera.position.x = value;
      createFrustumVisualization();
    });
    
    positionFolder.add(cameraParams.position, 1, -10, 10).name('Y').onChange(value => {
      visualizedCamera.position.y = value;
      createFrustumVisualization();
    });
    
    positionFolder.add(cameraParams.position, 2, 0, 20).name('Z').onChange(value => {
      visualizedCamera.position.z = value;
      createFrustumVisualization();
    });

    cameraFolder.open();
    positionFolder.open();

    // 初始化视锥体可视化
    createFrustumVisualization();
    
    // 添加测试对象
    addTestObjects();

    // 动画循环
    function animate() {
      requestAnimationFrame(animate);
      controls.update();
      cameraHelper.update();
      renderer.render(scene, viewCamera);
    }

    animate();

    // 窗口大小调整
    function onWindowResize() {
      viewCamera.aspect = window.innerWidth / window.innerHeight;
      viewCamera.updateProjectionMatrix();
      
      visualizedCamera.aspect = window.innerWidth / window.innerHeight;
      visualizedCamera.updateProjectionMatrix();
      
      createFrustumVisualization();
      renderer.setSize(window.innerWidth, window.innerHeight);
    }

    window.addEventListener('resize', onWindowResize);

    // 清理函数
    return () => {
      window.removeEventListener('resize', onWindowResize);
      gui.destroy();
      mountRef.current.removeChild(renderer.domElement);
      renderer.dispose();
    };
  }, []);

  return <div ref={mountRef} style={{ width: "100vw", height: "100vh" }} />;
}