// ThreeScene.js
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import * as dat from 'dat.gui';
import Stats from 'stats.js';
import { mergeGeometries } from 'three/examples/jsm/utils/BufferGeometryUtils';
export class ThreeScene {
  constructor(container) {
    this.container = container;
    this.cubes = [];
    this.combinedMesh = null; // 用于存储合并后的网格
    this.init();
  }

  init() {
    this.initRender();
    this.initScene();
    this.initCamera();
    this.initLight();
    this.initGui();
    this.initControls();
    this.initStats();
    this.animate();
  }

  initRender() {
    this.renderer = new THREE.WebGLRenderer({ antialias: true });
    this.renderer.setSize(window.innerWidth, window.innerHeight);
    this.renderer.shadowMap.enabled = true;
    this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    this.container.appendChild(this.renderer.domElement);
  }

  initCamera() {
    this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);
    this.camera.position.set(0, 40, 100);
    this.camera.lookAt(new THREE.Vector3(0, 0, 0));
  }

  initScene() {
    this.scene = new THREE.Scene();
  }

  initLight() {
    this.scene.add(new THREE.AmbientLight(0x444444));

    this.light = new THREE.DirectionalLight(0xffffff);
    this.light.position.set(15, 50, 10);
    this.light.castShadow = true;
    this.scene.add(this.light);
  }

  initGui() {
    this.gui = {
      numberOfObjects: 500,
      combined: false,
      redraw: () => {
        if (this.gui.combined) {
          this.combineCubes(); // 合并方块
        } else {
          this.createIndividualCubes(); // 创建单独的方块
        }
      },
    };

    const datGui = new dat.GUI();
    datGui.add(this.gui, 'numberOfObjects', 0, 20000).step(1).onChange(() => this.gui.redraw());
    datGui.add(this.gui, 'combined').onChange(() => this.gui.redraw());
    datGui.add(this.gui, 'redraw');

    this.gui.redraw();
  }

  createIndividualCubes() {
    // 移除合并的网格（如果存在）
    if (this.combinedMesh) {
      this.scene.remove(this.combinedMesh);
      this.combinedMesh = null;
    }

    // 移除现有的方块
    this.cubes.forEach((cube) => this.scene.remove(cube));
    this.cubes = [];

    // 创建新的方块
    console.log(this.gui.numberOfObjects)
    for (let i = 0; i < this.gui.numberOfObjects; i++) {
      const cube = this.addCube();
      this.scene.add(cube);
      this.cubes.push(cube);
    }
    this.render();
  }

  combineCubes() {
    // 移除现有的方块
    this.cubes.forEach((cube) => this.scene.remove(cube));
    this.cubes = [];

    // 创建一个几何体数组
    const geometries = [];
    for (let i = 0; i < this.gui.numberOfObjects; i++) {
      const cube = this.addCube();
      cube.updateMatrix(); // 更新矩阵以应用位置信息
      geometries.push(cube.geometry.clone().applyMatrix4(cube.matrix));
    }

    // 合并几何体
    const mergedGeometry = mergeGeometries(geometries);
    const material = new THREE.MeshLambertMaterial({ color: 0x00ff00, transparent: true, opacity: 0.8 });
    this.combinedMesh = new THREE.Mesh(mergedGeometry, material);

    // 添加到场景
    this.scene.add(this.combinedMesh);
    this.render();

  }

  addCube() {
    const cubeSize = 1.0;
    const cubeGeometry = new THREE.BoxGeometry(cubeSize, cubeSize, cubeSize);
    const cubeMaterial = new THREE.MeshLambertMaterial({ color: 0x00ff00, transparent: true, opacity: 0.8 });
    const cube = new THREE.Mesh(cubeGeometry, cubeMaterial);

    cube.castShadow = true;
    cube.position.x = -100 + Math.round(Math.random() * 200);
    cube.position.y = -100 + Math.round(Math.random() * 200);
    cube.position.z = -100 + Math.round(Math.random() * 200);

    return cube;
  }

  initControls() {
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.enableDamping = true;
    this.controls.enableZoom = true;
    this.controls.autoRotate = true;
    this.controls.autoRotateSpeed = 0.5;
    this.controls.minDistance = 10;
    this.controls.maxDistance = 500;
    this.controls.enablePan = true;
  }

  initStats() {
    this.stats = new Stats();
    document.body.appendChild(this.stats.dom);
  }

  render() {
    this.renderer.render(this.scene, this.camera);
  }

  animate() {
    this.render();
    this.stats.update();
    this.controls.update();
    requestAnimationFrame(() => this.animate());
  }

  onWindowResize() {
    this.camera.aspect = window.innerWidth / window.innerHeight;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(window.innerWidth, window.innerHeight);
    this.render();
  }

  dispose() {
    window.removeEventListener('resize', this.onWindowResize);
    this.renderer.dispose();
    this.scene.traverse((object) => {
      if (object instanceof THREE.Mesh) {
        object.geometry.dispose();
        object.material.dispose();
      }
    });
    if (this.stats) {
      document.body.removeChild(this.stats.dom);
    }
    if (this.gui) {
      this.gui?.destroy?.();
    }
  }
}