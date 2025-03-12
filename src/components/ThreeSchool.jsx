import * as THREE from "three";
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { DragControls } from 'three/examples/jsm/controls/DragControls'
import { FirstPersonControls } from 'three/examples/jsm/controls/FirstPersonControls'
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { MTLLoader } from 'three/examples/jsm/loaders/MTLLoader.js';
import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader.js";
import * as BufferGeometryUtils from 'three/examples/jsm/utils/BufferGeometryUtils.js';

import Data from '../assets/jsonobj/test.json' 
import * as dat from"dat.gui";
import {boxUvCom} from "../utils/js/boxUvCom.js"
import { useRef, useEffect } from 'react';

function ThreeContainer() {
  const containerRef = useRef(null);
  const isContainerRunning = useRef(false);

  useEffect(() => {
    if (!isContainerRunning.current && containerRef.current) {
      isContainerRunning.current = true;
      let scene, camera, renderer, wgl_controls, gui, Gui, clock;
      let roadways = new THREE.Object3D();
      roadways.name = "TunnelSystem"; // 给对象命名便于后续查找
      
      sceneInit().then(animate);

      // 渲染器增强函数
      function enhanceRenderer(renderer) {
        // 启用物理正确的光照
        renderer.physicallyCorrectLights = true;
        
        // 启用阴影
        renderer.shadowMap.enabled = true;
        renderer.shadowMap.type = THREE.PCFSoftShadowMap;
        
        // 调整曝光和色调映射
        renderer.toneMappingExposure = 1.0;
        renderer.toneMapping = THREE.ACESFilmicToneMapping;
      }

      // 巷道光照系统函数
      function setupTunnelLighting(scene, roadways) {
        // 清除场景中可能已存在的光源
        // scene.traverse((object) => {
        //   if (object.isLight && object.parent === scene) {
        //     scene.remove(object);
        //   }
        // });
        
        // 添加基础环境光
        const ambientLight = new THREE.AmbientLight(0x404040, 3); 
        scene.add(ambientLight);
        
        // 获取巷道的边界框以确定其尺寸和位置
        const box = new THREE.Box3().setFromObject(roadways);
        const tunnelLength = box.max.z - box.min.z;
        const tunnelCenter = new THREE.Vector3();
        box.getCenter(tunnelCenter);
        
        // 添加多个点光源沿巷道分布
        const lightCount = Math.max(5, Math.floor(tunnelLength / 20)); // 每20单位一个光源，至少5个
        
        for (let i = 0; i < lightCount; i++) {
          const pointLight = new THREE.PointLight(0xffffff, 0.8, 50);
          // 沿巷道分布光源
          pointLight.position.set(
            tunnelCenter.x,
            tunnelCenter.y + 1.5, // 光源略高于中心
            box.min.z + i * (tunnelLength / lightCount)
          );
          pointLight.castShadow = true;
          pointLight.shadow.mapSize.width = 512;
          pointLight.shadow.mapSize.height = 512;
          scene.add(pointLight);
          
          // 可选：添加光源辅助对象以便调试
          // const pointLightHelper = new THREE.PointLightHelper(pointLight, 0.5);
          // scene.add(pointLightHelper);
        }
        
        // 添加方向光以增强整体照明
        const directLight = new THREE.DirectionalLight(0xffffff, 0.5);
        directLight.position.set(0, 10, 0);
        directLight.castShadow = true;
        directLight.shadow.mapSize.width = 1024;
        directLight.shadow.mapSize.height = 1024;
        scene.add(directLight);
        
        // 添加一个跟随相机的光源
        const cameraLight = new THREE.PointLight(0xffffff, 0.7, 30);
        camera.add(cameraLight);
        scene.add(camera); // 确保相机是场景的子对象
      }

      // 添加反射探针函数
      function addTunnelReflectionProbes(scene) {
        // 检查Three.js版本是否支持LightProbe
        if (THREE.LightProbe) {
          const lightProbe = new THREE.LightProbe();
          lightProbe.intensity = 0.5;
          scene.add(lightProbe);
        }
      }

      // 光照调试控制函数
      function addLightingDebugControls(gui, scene) {
        const lightingFolder = gui.addFolder('照明调节');
        
        // 获取场景中的所有光源
        const lights = [];
        scene.traverse((object) => {
          if (object.isLight) {
            lights.push(object);
          }
        });
        
        // 为每个光源添加控制
        lights.forEach((light, index) => {
          const lightFolder = lightingFolder.addFolder(`光源 ${index + 1}`);
          lightFolder.add(light, 'intensity', 0, 5, 0.1).name('强度');
          
          if (light.isPointLight || light.isSpotLight) {
            lightFolder.add(light, 'distance', 0, 200, 1).name('距离');
            lightFolder.add(light, 'decay', 0, 5, 0.1).name('衰减');
          }
          
          if (light.position) {
            lightFolder.add(light.position, 'x', -100, 100, 1).name('X位置');
            lightFolder.add(light.position, 'y', -100, 100, 1).name('Y位置');
            lightFolder.add(light.position, 'z', -100, 100, 1).name('Z位置');
          }
          
          // 添加颜色控制
          const lightColor = { color: `#${light.color.getHexString()}` };
          lightFolder.addColor(lightColor, 'color').onChange((value) => {
            light.color.set(value);
          }).name('颜色');
        });
        
        // 打开照明文件夹
        lightingFolder.open();
      }

      // 场景初始化函数
      async function sceneInit() {
        scene = new THREE.Scene();
        scene.background = new THREE.CubeTextureLoader()
        .load([
           "textures/sky/px.jpg",
           "textures/sky/nx.jpg",
           "textures/sky/py.jpg",
           "textures/sky/ny.jpg",
           "textures/sky/pz.jpg",
           "textures/sky/nz.jpg",
         ]);

        camera = new THREE.PerspectiveCamera(
          55,
          window.innerWidth / window.innerHeight,
          0.001,
          1000
        );

        camera.position.set(0, 1.25, 4);
        camera.lookAt(0, -100, 0);

        clock = new THREE.Clock();

        // 初始光照设置将在模型加载后替换
        const ambientLight = new THREE.AmbientLight(0x404040, 2);
        scene.add(ambientLight);

        renderer = new THREE.WebGLRenderer({
          antialias: true,
        });
        renderer.setPixelRatio(window.devicePixelRatio);
        renderer.setSize(window.innerWidth, window.innerHeight);
        containerRef.current.appendChild(renderer.domElement);

        // 应用渲染器增强
        enhanceRenderer(renderer);

        wgl_controls = new OrbitControls(camera, renderer.domElement);
        wgl_controls.movementSpeed = 150;
        wgl_controls.lookSpeed = 0.1;
        window.addEventListener('resize', onWindowResize, false);

        guifun();
        
        // 加载模型
        await roadmodeltest(roadways);
        
        // 在模型加载完成后设置光照
        setupTunnelLighting(scene, roadways);
        
        // 添加反射探针
        addTunnelReflectionProbes(scene);
        
        // 添加光照调试控制
        addLightingDebugControls(gui, scene);
      }

      // 优化后的纹理加载和网格创建函数
      function loadTextureAndCreateMesh(config, roadways) {
        const textureLoader = new THREE.TextureLoader();
        let texturePath;
  
        switch (config.rockType) {
          case '1':
            texturePath = '/textures/door/444.bmp';
            break;
          case '2':
            texturePath = '/textures/door/01.bmp';
            break;
          case '3':
            texturePath = '/textures/door/02.bmp';
            break;
          case '4':
            texturePath = '/textures/door/01.bmp';
            break;
          default:
            texturePath = '/textures/door/01.bmp';
            break;
        }
  
        return new Promise((resolve) => {
          textureLoader.load(
            texturePath,
            (texture) => {
              texture.wrapS = THREE.RepeatWrapping;
              texture.wrapT = THREE.RepeatWrapping;
              // 调整纹理重复次数，避免过度拉伸
              texture.repeat.set(2000, 2000); // 根据实际模型大小调整
    
              const Geometry = new THREE.BufferGeometry();
              Geometry.setAttribute('position', new THREE.BufferAttribute(config.roadway.vertices, 3));
              Geometry.setIndex(config.roadway.indices);
              Geometry.center();
              Geometry.computeBoundingBox();
              
              // 计算法线但不标准化
              Geometry.computeVertexNormals();
              
              // 改进UV映射
              Geometry.setAttribute('uv', new THREE.BufferAttribute(
                boxUvCom(Geometry.getAttribute('position'),
                Geometry.getAttribute('normal'),
                Geometry.boundingBox.max, Geometry.boundingBox.min, 5) // 调整UV缩放因子
                , 2));
                
              // 优化材质参数
              var material = new THREE.MeshStandardMaterial({ 
                map: texture,
                roughness: 0.7,     // 增加粗糙度以减少不自然的光泽
                metalness: 0.1,     // 降低金属感
                side: THREE.DoubleSide,
                flatShading: false, // 使用平滑着色
              });
              
              let mesh = new THREE.Mesh(Geometry, material);
              mesh.receiveShadow = true; // 接收阴影
              mesh.castShadow = true;    // 投射阴影
              
              roadways.add(mesh);
              resolve();
            },
            undefined,
            (error) => {
              console.error('An error occurred loading the texture:', error);
              resolve(); // 即使发生错误也要解析Promise
            }
          );
        });
      }

      // 修改后的动画函数
      function animate() {
        // 如果有相机跟随光源，更新其位置
        scene.traverse((object) => {
          if (object.isLight && object.parent === camera) {
            // 光源已经附加到相机上，会自动跟随
          }
        });
      
        renderer.render(scene, camera);
        renderer.clearDepth();
        wgl_controls.update(clock.getDelta());
      
        requestAnimationFrame(animate);
      }

      function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();

        renderer.setSize(window.innerWidth, window.innerHeight);
      }

      function guifun() {
        gui = new dat.GUI();
        Gui = {
          exporterScene: function() {
            //首先将场景转成json对象
            let group = scene.getObjectByName("TunnelSystem");
            if(!group) return;
            let obj = group.toJSON();
            //将json对象转成json字符串并存储
            download("tunnel.json", JSON.stringify(obj));
          },
          importerScene: function() {
            //创建一个input来获取json数据
            let input = document.createElement("input");
            input.type = "file";
            input.addEventListener("change", function() {
              let file = input.files[0];
              console.log("wenjian", file);
              //判断是否是json格式的文件
              if(file.type.indexOf("json") >= 0) {
                //读取文件内的内容
                let reader = new FileReader();
                reader.readAsText(file);
                reader.onloadend = function() {
                  //使用three.js的ObjectLoader将模型导入到场景
                  let loader = new THREE.ObjectLoader();
                  let group = loader.parse(JSON.parse(this.result));
                  scene.add(group);
                }
              }
            });
            input.click();
          },
        }
        
        gui.add(Gui, "exporterScene").name("导出模型");
        gui.add(Gui, "importerScene").name("导入模型");
        
        // 控制器切换
        gui.add({ fun: () => {
          wgl_controls.dispose(); //原有移除，重新绑控件
          wgl_controls = new FirstPersonControls(camera, renderer.domElement);
          wgl_controls.lookSpeed = 0.05; //鼠标水平环视速度
          wgl_controls.movementSpeed = 0.1; //相机移动速度
          wgl_controls.noFly = true;
          wgl_controls.constrainVertical = true; //约束垂直在verticalMin和verticalMax范围之间。
          wgl_controls.verticalMin = 1.0;
          wgl_controls.verticalMax = 2.0;
        }}, 'fun').name('漫游控制');
        
        gui.add({ fun: () => {
          wgl_controls.dispose(); //原有移除，重新绑控件
          wgl_controls = new OrbitControls(camera, renderer.domElement); //鼠标控制屏幕转动
          wgl_controls.rotateSpeed = 0.5;
        }}, 'fun').name('浏览控制');
        
        gui.add({ fun: () => {
          let objects = [];
          for(let i = 0; i < scene.children.length; i++) {
            if(scene.children[i].isMesh && scene.children[i].geometry.type == "SphereGeometry")
              objects.push(scene.children[i]);
          }
          console.log(objects);
          let controls = new DragControls(objects, camera, renderer.domElement);
          controls.addEventListener('dragstart', function(event) {
            wgl_controls.enabled = false;
          });
          controls.addEventListener('dragend', function(event) {
            wgl_controls.enabled = true;
          });
        }}, 'fun').name('拖放控制');
        
        // 添加照明控制按钮
        gui.add({ fun: () => {
          // 重新应用光照设置
          setupTunnelLighting(scene, roadways);
          // 更新光照调试控制
          addLightingDebugControls(gui, scene);
        }}, 'fun').name('重置照明');
      }

      // 修改后的模型加载函数
      async function roadmodeltest(roadways) {
        const roads = Data;    
        let vertices = [];
        
        // 处理顶点数据
        for(let m = 0; m < roads.vertices.length/3; m++) {
          cotpoints(roads.vertices[m*3]*1, roads.vertices[m*3+2]*1, roads.vertices[m*3+1]*1, "roads", vertices);
        }
      
        // 分成两组索引
        const indices1 = roads.indices.slice(0, 30000);
        const indices2 = roads.indices.slice(129000);
        
        // 创建顶点数组
        let Vertices = new Float32Array(vertices.length);
        for(let i = 0; i < vertices.length; i++) {
          Vertices[i] = vertices[i];
        }
        
        // 自定义数据
        const rockConfigs = [
          {
            rockType: "1",
            roadway: {
              vertices: Vertices,
              indices: indices1,
            }
          },
          {
            rockType: "2",
            roadway: {
              vertices: Vertices,
              indices: indices2,
            }
          }
        ];
        
        // 分部分渲染，使用Promise.all等待所有纹理加载完成
        const loadPromises = rockConfigs.map(config => loadTextureAndCreateMesh(config, roadways));
        await Promise.all(loadPromises);
        
        scene.add(roadways);
        return roadways;
      }

      // 添加可移动光源点
      function addMovableLight() {
        const lightSphere = new THREE.Mesh(
          new THREE.SphereGeometry(0.5, 16, 8),
          new THREE.MeshBasicMaterial({color: 0xffff00})
        );
        lightSphere.position.set(0, 2, 0);
        
        const movableLight = new THREE.PointLight(0xffffff, 1, 50);
        movableLight.castShadow = true;
        movableLight.shadow.mapSize.width = 512;
        movableLight.shadow.mapSize.height = 512;
        
        lightSphere.add(movableLight);
        scene.add(lightSphere);
        
        const lightFolder = gui.addFolder('可移动光源');
        lightFolder.add(lightSphere.position, 'x', -50, 50).name('X位置');
        lightFolder.add(lightSphere.position, 'y', 0, 20).name('Y位置');
        lightFolder.add(lightSphere.position, 'z', -50, 50).name('Z位置');
        lightFolder.add(movableLight, 'intensity', 0, 5).name('光强度');
        lightFolder.add(movableLight, 'distance', 0, 100).name('光照距离');
        lightFolder.open();
        
        return lightSphere;
      }

      function download(filename, text) {
        var pom = document.createElement('a');
        pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
        pom.setAttribute('download', filename);
        if (document.createEvent) {
          var event = document.createEvent('MouseEvents');
          event.initEvent('click', true, true);
          pom.dispatchEvent(event);
        } else {
          pom.click();
        }
      }
      
      function cotpoints(tx, ty, tz, name, vertices) {
        vertices.push(-ty);
        vertices.push(tz);
        vertices.push(tx);
      }
    }
  }, []);

  return <div ref={containerRef} />;
}

export default ThreeContainer;