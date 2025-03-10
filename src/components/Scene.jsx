// src/components/Scene.jsx
import React from 'react';
import ThreeContainer from './ThreeContainer';
import ThreeSchool from "./ThreeSchool"
import ThreeTest from "./test"
import  CameraVisualization from "./shexian"
import GeologicalDistanceVisualization from './1/GeologicalDistanceVisualization'
function Scene({ sceneType }) {
  return (
    <div>
      {sceneType === 'home' && <ThreeContainer />}
      {sceneType === 'sceneA1' && <ThreeSchool />}
      {sceneType === 'sceneA2' && <ThreeTest/>}
      {sceneType === 'sceneB2' && < CameraVisualization/>}
      {sceneType === 'sceneB1' && <GeologicalDistanceVisualization></GeologicalDistanceVisualization>}
    </div>
  );
}

export default Scene;
