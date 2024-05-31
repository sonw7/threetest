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
import {boxUvCom,removeDuplicateTriangles,boxUvComaddAngle} from "../utils/js/boxUvCom.js"
import { useRef, useEffect } from 'react';

function ThreeContainer() {
  const containerRef = useRef(null);
  const isContainerRunning = useRef(false);

  useEffect(() => {
    if (!isContainerRunning.current && containerRef.current) {
      isContainerRunning.current = true;
      let scene, camera, renderer, wgl_controls, gui, Gui, clock;
      let roadways = new THREE.Object3D();
      sceneInit().then(animate);

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
         //scene.fog = new THREE.Fog( 0x000000, 10, 15 );

        camera = new THREE.PerspectiveCamera(
          55,
          window.innerWidth / window.innerHeight,
          0.001,
          1000
        );

        //camera.lookAt(scene.position);
        camera.position.set(10,-1.25,-4);
        camera.lookAt(0,-100,0)

        clock = new THREE.Clock();

        const ambientLight = new THREE.AmbientLight(0x000000); // soft white light
        //scene.add(ambientLight);
        //添加直线光
        const directLight = new THREE.DirectionalLight(0xffffff, 1)
        directLight.position.set(0,10,10)
        //scene.add(directLight);
        
        const point7 = new THREE.PointLight(0xffffff,1);
        point7.position.set(0, 100, 100); //点光源位置
        //scene.add(point7); //点光源添加到场景中


        renderer = new THREE.WebGLRenderer({
          antialias: true,
          // logarithmicDepthBuffer: true,
        });
        renderer.setPixelRatio(window.devicePixelRatio);
        renderer.setSize(window.innerWidth, window.innerHeight);
        containerRef.current.appendChild(renderer.domElement);

        wgl_controls = new OrbitControls(camera,renderer.domElement);
        wgl_controls.movementSpeed = 150;
        wgl_controls.lookSpeed = 0.1;
        window.addEventListener('resize', onWindowResize, false);

        guifun();
        roadmodeltest(roadways);
        //moverPoint();


      }

      function loadTextureAndCreateMesh(config, roadways ,index){
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
            texturePath = '/textures/door/111.bmp';
            break;
          default:
            texturePath = '/textures/door/222.bmp';
            break;
        }
  
        textureLoader.load(
          texturePath,
          (texture) => {
            texture.wrapS = THREE.RepeatWrapping;
            texture.wrapT = THREE.RepeatWrapping;
            texture.repeat.set(10000, 10000);
  
             //绘制mesh
             const Geometry = new THREE.BufferGeometry();
             Geometry.setFromPoints(config.roadway.vertices);
             //Geometry.setAttribute( 'position', new THREE.BufferAttribute( config.roadway.vertices, 3 ) );
             Geometry.setIndex( removeDuplicateTriangles(config.roadway.indices) );
             Geometry.center();
             Geometry.computeBoundingBox();
             Geometry.computeVertexNormals();
            //  Geometry.normalizeNormals () ;computeVertexNormals()会调用，没必要再次调用
             Geometry.setAttribute( 'uv', new THREE.BufferAttribute(
                 boxUvCom( Geometry.getAttribute('position'),
                 Geometry.getAttribute('normal'),
                 Geometry.boundingBox.max, 
                 Geometry.boundingBox.min,
                 10)
                 , 2 ) );
                 //添加线框

                 const edges = new THREE.EdgesGeometry(Geometry);
                 const line = new THREE.LineSegments(
                   edges,
                   new THREE.LineBasicMaterial({ color: 0x000000 })
                 );

             var material = new THREE.MeshBasicMaterial( { 
                     map:texture,
                     side:THREE.DoubleSide,
                     color:0xe6e6e6,
                     //flatShading:true,
                 } )
             let mesh = new THREE.Mesh(Geometry,material)
             console.log(`第${index+1}块渲染`,Geometry)
             mesh.add(line)//作为子对象加入
                 roadways.add(mesh);
           },
               undefined,
               (error) => {
                 console.error('An error occurred loading the texture:', error);
            }
        );
      };

      function animate() {

        //wgl_controls.update();
        renderer.render(scene, camera);
        renderer.clearDepth();
        wgl_controls.update(clock.getDelta());
        //stats.update(); // fps stats

        requestAnimationFrame(animate);
      }

      function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();

        renderer.setSize(window.innerWidth, window.innerHeight);
      }
      function guifun(){
        gui = new dat.GUI();
        Gui = {
           exporterScene:function () {
               //首先将场景转成json对象
               let group = scene.getObjectByName("HD");
               if(!group) return;
               let obj = group.toJSON();
               //将json对象转成json字符串并存储
               download("file.json", JSON.stringify(obj));
           },importerScene:function () {
               //创建一个input来获取json数据
               let input = document.createElement("input");
               input.type = "file";
               input.addEventListener("change", function () {
                   let file = input.files[0];
                   console.log("wenjian",file)
                   //判断是否是json格式的文件
                   if(file.type.indexOf("json") >= 0){
                       //首先先删除掉当前场景所含有的立方体
                       //deleteGroup("group");
       
                       //读取文件内的内容
                       let reader = new FileReader();
                       reader.readAsText(file);
                       reader.onloadend = function () {
                           //使用three.js的JSONLoader将模型导入到场景
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
       
               //根本是根据按键重新赋予wgl_controls，
               gui.add({ fun: () => {
       
       
                   wgl_controls.dispose();//原有移除，重新绑控件
                   wgl_controls = new FirstPersonControls( camera, renderer.domElement );
                   wgl_controls.lookSpeed = 0.05; //鼠标水平环视速度
                   wgl_controls.movementSpeed = 0.1; //相机移动速度
                   wgl_controls.noFly = true;
                   wgl_controls.constrainVertical = true; //约束垂直在verticalMin和verticalMax范围之间。
                   wgl_controls.verticalMin = 1.0;
                   wgl_controls.verticalMax = 2.0;
       
       
               } }, 'fun').name('漫游控制');
           gui.add({ fun: () => {
       
                   wgl_controls.dispose();//原有移除，重新绑控件
                   wgl_controls = new OrbitControls(camera, renderer.domElement);//鼠标控制屏幕转动
                   wgl_controls.rotateSpeed=0.5;
                   // wgl_controls.minDistance = 10;
                   // wgl_controls.maxDistance = 500;
       
       
               } }, 'fun').name('浏览控制');
               gui.add({ fun: () => {
                   let objects=[];
                   console.log(scene.children[4].geometry.type)
                   for(let i=0;i<scene.children.length;i++){
                       if(scene.children[i].isMesh&&scene.children[i].geometry.type=="SphereGeometry")
                           objects.push(scene.children[i])
                   };
                   console.log(objects)
                   //wgl_controls.dispose();//原有移除，重新绑控件
                   let controls = new  DragControls( objects, camera, renderer.domElement );;//鼠标控制屏幕转动
                   controls.addEventListener( 'dragstart', function ( event ) {
       
                       wgl_controls.enabled=false;
                   
                   } );
                   controls.addEventListener( 'dragend', function ( event ) {
       
                       wgl_controls.enabled=true;
                   
                   } );
               } }, 'fun').name('拖放控制');
      }
      function roadmodeltest(roadways){
        const roads=Data;    
        let vertices =[];

        for(let m=0;m<roads.vertices.length/3;m++){
            cotpoints(  roads.vertices[m*3]*1,roads.vertices[m*3+2]*1,roads.vertices[m*3+1]*1, "roads",vertices);
        }
        //console.log("巷道索引",roads.indices)

        //分成两组顶点
        const indices1 = roads.indices.slice(0,30000)
        const indices2 = roads.indices.slice(129000);
        // console.log('巷道第一部分索引',indices1)
        // console.log('巷道第二部分索引2',indices2)

        //自定义数据
        const rockConfigs = [
          {
            rockType:"1",
            roadway:{
              vertices:vertices,
              indices:indices1,
            }
          },
          {
            rockType:"3",
            roadway:{
              vertices:vertices,
              indices:indices2,
            }
          }
        ]
      //分部分渲染
       rockConfigs.forEach((config,index) => {
        console.log(`分块的数据${index+1}`,config);
        loadTextureAndCreateMesh(config,roadways,index)
       });
       scene.add(roadways);

        //绘制mesh
        // const Geometry1 = new THREE.BufferGeometry();
        // Geometry1.setAttribute( 'position', new THREE.BufferAttribute( Vertices, 3 ) );
        // Geometry1.setIndex( indices1 );
        // Geometry1.center();

        // Geometry1.computeBoundingBox();
        // Geometry1.computeVertexNormals();
        // Geometry1.normalizeNormals () ;
 
        // Geometry1.setAttribute( 'uv', new THREE.BufferAttribute(
        //     boxUvCom( Geometry1.getAttribute('position'),
        //     Geometry1.getAttribute('normal'),
        //     Geometry1.boundingBox.max, Geometry1.boundingBox.min,10)
        //     , 2 ) );
        
        // console.log("Geometry1",Geometry1)
        // var material1 = new THREE.MeshPhongMaterial( { 
        //         map:texture3,
        //         side:THREE.DoubleSide,
        //         flatShading:true,
        //     } )
        // let mesh1 = new THREE.Mesh(Geometry1,material1)

        // //----------------
        // //准备读入数据
        // const geometry2 = new THREE.BufferGeometry();
        // //设置面索引
        // geometry2.setIndex( indices2 );
        // geometry2.setAttribute( 'position', new THREE.BufferAttribute( V2, 3 ) );
        // geometry2.center();
        // geometry2.translate(0,0,0)

        // geometry2.computeBoundingBox();
        // geometry2.computeVertexNormals();
        // geometry2.normalizeNormals () ;
  

        // geometry2.setAttribute( 'uv', new THREE.BufferAttribute(
        //      boxUvCom(  geometry2.getAttribute('position'),
        //      geometry2.getAttribute('normal'),
        //      geometry2.boundingBox.max,  geometry2.boundingBox.min,10)
        //      , 2 ) );

        // var material2 = new THREE.MeshPhongMaterial( { 
        //     map:texture2,
        //     side:THREE.DoubleSide,
        //     flatShading:true,
        // } )
        // let mesh2 = new THREE.Mesh(geometry2,material2)

        // const allGeometry = new THREE.Object3D();
        // allGeometry.add(mesh1);
        //allGeometry.add(mesh2);
        //scene.add(allGeometry)

        // let geometryArray = []; // 将你的要合并的多个geometry放入到该数组
        // let materialArray = []; // 将你的要赋值的多个material放入到该数组
        // geometryArray.push(cubeGeometry1,cube2);
        // materialArray.push(material1,material2);
        //合并模型
        // meshConcat(geometryArray,materialArray,indices1,indices2);

      }
      function getTexture(rockType){
        // let texture1 = new THREE.TextureLoader().load("textures/door/444.bmp");
        // texture1.repeat.set(2000,2000)
        // texture1.wrapS = THREE.RepeatWrapping;
        // texture1.wrapT = THREE.RepeatWrapping;
        let textureLoader = new THREE.TextureLoader();
        switch (rockType) {
          case '1':
            textureLoader.load("textures/door/444.bmp");
             break;
          case '2':
            textureLoader.load("textures/door/01.bmp");
            break;
          case '3':
            textureLoader.load("textures/door/02.bmp");
            break;
          case '4':
            textureLoader.load("textures/door/02.bmp");
            break;
          default:
            textureLoader.load("textures/door/02.bmp");
            break;
        }
        
         textureLoader.wrapS = THREE.RepeatWrapping;
         textureLoader.wrapT = THREE.RepeatWrapping;
         textureLoader.repeat.set(2000,2000)

         return textureLoader;
      }
      function moverPoint(){

        const smallbox = new THREE.Mesh(
            new THREE.SphereGeometry(5,32,16),
            new THREE.MeshBasicMaterial({color:0xff0000})
        )
        smallbox.position.set(-400,-150,-65);
        //添加直线光
        const pointLight = new THREE.PointLight(0xffffff, 0.5)
        
        pointLight.position.set(400,70,20);
        //pointLight.castShadow = true; 
        //设置阴影贴图模糊度
       // pointLight.shadow.radius = 20;
        //设置阴影贴图的分辨率
       // pointLight.shadow.mapSize.set(512,512);
        //设置透视相机的属性
        smallbox.add(pointLight);
        scene.add(smallbox);
        
        gui
        .add(smallbox.position,"x")
        .min(-1000)
        .max(100)
        .step(10);
        gui
        .add(smallbox.position,"y")
        .min(-500)
        .max(100)
        .step(5);
        gui
        .add(smallbox.position,"z")
        .min(-350)
        .max(350)
        .step(5);
        //gui.add(directLight.position,"x")
        // gui.add(pointLight,"distance").min(0).max(5).step(0.01);
        // gui.add(pointLight,"intensity").min(0).max(5).step(0.01);
        
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
       function  cotpoints(tx,ty,tz,name,vertices){

        vertices.push(new THREE.Vector3(-ty,tz,tx))
        // vertices.push(-ty);
        // vertices.push(tz);
        // vertices.push(tx);

      }
    }
  }, []);

  return <div ref={containerRef} />;
}

export default ThreeContainer;
