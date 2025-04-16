// src/components/Scene.jsx
import React from 'react';
import ThreeContainer from './ThreeContainer';
import ThreeSchool from "./ThreeSchool"
import ThreeTest from "./test"
import  CameraVisualization from "./shexian"
import GeologicalDistanceVisualization from './1/GeologicalDistanceVisualization'
import MeshOptimizationDemo from './meshtest/meshtest'
import Dengzhixian from './dengzhixian'
function Scene({ sceneType }) {
  return (
    <div>
      {sceneType === 'sceneA1' && <ThreeContainer />}
      {sceneType === 'sceneA4' && <Dengzhixian />}
      {/* 这里可以添加更多的场景组件 */}
      {/* {sceneType === 'sceneA4' && <ThreeContainer />} */}
      {/* {sceneType === 'sceneA1' && <ThreeSchool />} */}
      {sceneType === 'sceneA2' && <ThreeTest/>}
      {sceneType === 'sceneA3' && <MeshOptimizationDemo/>}
      {sceneType === 'sceneB1' && <GeologicalDistanceVisualization></GeologicalDistanceVisualization>}
      {sceneType === 'sceneB2' && < CameraVisualization/>}
      {sceneType === 'sceneB3' && <ThreeTest/>}
    </div>
  );
}

export default Scene;
