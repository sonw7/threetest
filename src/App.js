//根组件
import React, { useState } from 'react';
import { MantineProvider,Container, Button } from '@mantine/core';
import NavBar from './components/NavBar';
import Scene from './components/Scene';
function App() {
  const [selectedScene, setSelectedScene] = useState("Scene1");
  const [scene, setScene] = useState('home');

  return (

      <MantineProvider withGlobalStyles withNormalizeCSS>
      <Container >
        <NavBar onSelect={setSelectedScene} />
        <Scene sceneType={scene} />
      </Container>
    </MantineProvider>
  
    
  );
}

export default App;
