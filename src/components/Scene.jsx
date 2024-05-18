// src/components/Scene.jsx
import React from 'react';
import ThreeContainer from './ThreeContainer';

function Scene({ sceneType }) {
  return (
    <div>
      {sceneType === 'scene1a' && <div>Home Scene</div>}
      {sceneType === 'home' && <ThreeContainer />}
      {sceneType === 'scene1b' && <div>Scene 1B Content</div>}
      {sceneType === 'scene2a' && <div>Scene 2A Content</div>}
      {sceneType === 'scene2b' && <div>Scene 2B Content</div>}
    </div>
  );
}

export default Scene;
