// src/components/Scene.jsx
import React from 'react';
import ThreeContainer from './ThreeContainer';
import ThreeSchool from "./ThreeSchool"
import BoundaryDrawer from "./threetest"
function Scene({ sceneType }) {
  return (
    <div>
      {sceneType === 'home' && <ThreeContainer />}
      {sceneType === 'sceneA1' && <ThreeSchool />}
      {sceneType === 'sceneA2' && <ThreeContainer />}
      {sceneType === 'sceneB2' && <BoundaryDrawer />}
      {sceneType === 'sceneB2' && <div>Scene 2B Content</div>}
    </div>
  );
}

export default Scene;
