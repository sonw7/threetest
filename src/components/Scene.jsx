// src/components/Scene.jsx
import React from 'react';
import ThreeContainer from './ThreeContainer';
import ThreeSchool from "./ThreeSchool"
import ThreeTest from "./test"
import RaycastingDemo from "./shexian"
function Scene({ sceneType }) {
  return (
    <div>
      {sceneType === 'home' && <ThreeContainer />}
      {sceneType === 'sceneA1' && <ThreeSchool />}
      {sceneType === 'sceneA2' && <ThreeTest/>}
      {sceneType === 'sceneB2' && <RaycastingDemo/>}
      {sceneType === 'sceneB2' && <div>Scene 2B Content</div>}
    </div>
  );
}

export default Scene;
