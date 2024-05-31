// src/components/Scene.jsx
import React from 'react';
import ThreeContainer from './ThreeContainer';
import ThreeSchool from "./ThreeSchool"
function Scene({ sceneType }) {
  return (
    <div>
      {sceneType === 'home' && <ThreeContainer />}
      {sceneType === 'sceneA1' && <ThreeSchool />}
      {sceneType === 'sceneA2' && <ThreeContainer />}
      {sceneType === 'sceneB2' && <div>Scene 2A Content</div>}
      {sceneType === 'sceneB2' && <div>Scene 2B Content</div>}
    </div>
  );
}

export default Scene;
