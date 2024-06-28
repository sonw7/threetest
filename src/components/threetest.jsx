import React, { useRef, useEffect } from 'react';
import * as THREE from 'three';
import { EffectComposer } from 'three/addons/postprocessing/EffectComposer.js';
import { RenderPass } from 'three/addons/postprocessing/RenderPass.js';
import { UnrealBloomPass } from 'three/addons/postprocessing/UnrealBloomPass.js';
import { OutputPass } from 'three/addons/postprocessing/OutputPass.js';
const BoundaryDrawer = () => {
  const mountRef = useRef(null);

  // 模拟的边界点数据
  const boundaryPoints = [
    new THREE.Vector3(-10, 0, 0),
    new THREE.Vector3(0, 10, 0),
    new THREE.Vector3(10, 0, 0),
    new THREE.Vector3(0, -10, 0),
    new THREE.Vector3(-10, 0, 0),

  ];

  useEffect(() => {
    // 初始化场景、相机和渲染器
    const width = mountRef.current.clientWidth;
    const height = mountRef.current.clientHeight;
    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(width, height);
    renderer.setPixelRatio(window.devicePixelRatio);
    mountRef.current.appendChild(renderer.domElement);

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
    camera.position.z = 25;

    // 创建几何体和材质以绘制边界点
    const geometry = new THREE.BufferGeometry().setFromPoints(boundaryPoints);
    const material = new THREE.LineBasicMaterial({
      color: 0x0000ff,
      linewidth: 2
    });

    const line = new THREE.Line(geometry, material);
    line.computeLineDistances();

    let drawLength = 0;
    scene.add(line);

    // 设置光晕效果 (Bloom)
    
    const renderScene = new RenderPass(scene, camera);
    
    const bloomPass = new UnrealBloomPass(new THREE.Vector2(width, height), 1.5, 0.4, 0.85);
    bloomPass.threshold = 0;
    bloomPass.strength = 3.5; // 光晕强度
    bloomPass.radius = 0;

    const composer = new EffectComposer(renderer);
    composer.setSize(width, height);
    composer.addPass(renderScene);
    composer.addPass(bloomPass);

    // 动画函数
    const animate = () => {
      requestAnimationFrame(animate);

      // 增加长度直到全部边界点被绘制完毕
      drawLength += 0.05; // 减慢绘制速度
      if (drawLength > boundaryPoints.length) {
        drawLength = boundaryPoints.length;
      }

      // 更新几何体以仅绘制一部分边界点
      line.geometry.setDrawRange(0, drawLength);

      // 渲染场景和相机
      composer.render();
    };

    animate();

    // 清理资源
    return () => {
      mountRef.current.removeChild(renderer.domElement);
      geometry.dispose();
      material.dispose();
    };
  }, [boundaryPoints]);

  return (
    <div
      ref={mountRef}
      style={{ width: '100vw', height: '100vh' }}
    />
  );
};

export default BoundaryDrawer;


// import React, { useRef, useEffect } from 'react';
// import * as THREE from 'three';

// const BoundaryDrawer = ({ haloIntensity }) => {
//   const mountRef = useRef(null);

//   // 模拟的边界点数据
//   const boundaryPoints = [
//     new THREE.Vector3(-10, 0, 0),
//     new THREE.Vector3(0, 10, 0),
//     new THREE.Vector3(10, 0, 0),
//     new THREE.Vector3(0, -10, 0)
//   ];

//   useEffect(() => {
//     // 初始化场景、相机和渲染器
//     const width = mountRef.current.clientWidth;
//     const height = mountRef.current.clientHeight;
//     const renderer = new THREE.WebGLRenderer({ antialias: true });
//     renderer.setSize(width, height);
//     mountRef.current.appendChild(renderer.domElement);

//     const scene = new THREE.Scene();
//     const camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
//     camera.position.set(0, 0, 50);

//     // 自定义着色器
//     const shaderMaterial = new THREE.ShaderMaterial({
//       vertexShader: `
//         varying vec2 vUv;
//         void main() {
//           vUv = uv;
//           gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
//         }
//       `,
//       fragmentShader: `
//         uniform float intensity;
//         varying vec2 vUv;
//         void main() {
//           vec3 glow = vec3(0.5, 0.75, 1.0) * intensity;
//           gl_FragColor = vec4(glow, 1.0);
//         }
//       `,
//       uniforms: {
//         intensity: { value: haloIntensity },
//       },
//       blending: THREE.AdditiveBlending,
//       transparent: true,
//       depthTest: false,
//     });

//     // 创建几何体和材质以绘制边界点
//     const geometry = new THREE.BufferGeometry().setFromPoints(boundaryPoints);
//     const line = new THREE.Line(geometry, shaderMaterial);
//     let drawLength = 0;

//     scene.add(line);

//     // 动画函数
//     const animate = () => {
//       requestAnimationFrame(animate);

//       // 增加长度直到全部边界点被绘制完毕
//       drawLength += 0.05; // 减慢绘制速度
//       if (drawLength > boundaryPoints.length) {
//         drawLength = boundaryPoints.length;
//       }

//       // 更新几何体以仅绘制一部分边界点
//       line.geometry.setDrawRange(0, drawLength);

//       // 渲染场景和相机
//       renderer.render(scene, camera);
//     };

//     animate();

//     // 清理资源
//     return () => {
//       mountRef.current.removeChild(renderer.domElement);
//       geometry.dispose();
//       shaderMaterial.dispose();
//     };
//   }, [boundaryPoints, haloIntensity]);

//   return (
//     <div
//       ref={mountRef}
//       style={{ width: '100vw', height: '100vh' }}
//     />
//   );
// };

// BoundaryDrawer.defaultProps = {
//   haloIntensity: 10,
// };

// export default BoundaryDrawer;
