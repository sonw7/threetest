import React, { useEffect, useRef } from 'react';
import { ThreeScene } from './ThreeScene';

const ThreeTest = () => {
  const containerRef = useRef(null);

  useEffect(() => {
    const threeScene = new ThreeScene(containerRef.current);

    const handleResize = () => threeScene.onWindowResize();
    window.addEventListener('resize', handleResize);

    return () => {
      threeScene.dispose();
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  return <div ref={containerRef} />;
};

export default ThreeTest;