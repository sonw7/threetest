//根组件
import React, { useState } from 'react';
import { MantineProvider,Container, Button } from '@mantine/core';
import NavBar from './components/NavBar';
import Scene from './components/Scene';
function App() {
  const [scene, setScene] = useState('sceneA1');

  return (

      <MantineProvider withGlobalStyles withNormalizeCSS>
      <div style={{ position: 'relative' }}>
        <NavBar setScene={setScene} />
        <Container style={{ padding: '10px 0px', margin:"0px"}}>
          <Scene sceneType={scene} />
        </Container>
      </div>
    </MantineProvider>
  
    
  );
}

export default App;
