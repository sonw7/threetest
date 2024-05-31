// NavBar.jsx
import React from 'react';
import { Menu, Button, Group } from '@mantine/core';
import { ChevronDown, Settings, ThreeDCubeSphere } from 'tabler-icons-react';

function NavBar({ setScene }) {
    return (
      <Group position="left" spacing="xl" style={{ padding: '10px 20px' }}>
        <Menu>
          <Menu.Target>
            <Button leftSection={<ChevronDown size={16} />}>Scene 1</Button>
          </Menu.Target>
          <Menu.Dropdown>
            <Menu.Item leftSection={<ThreeDCubeSphere />} onClick={() => setScene('sceneA1')}>Scene A1</Menu.Item>
            <Menu.Item leftSection={<ThreeDCubeSphere />} onClick={() => setScene('sceneA2')}>Scene A2</Menu.Item>
          </Menu.Dropdown>
        </Menu>
        <Menu>
          <Menu.Target>
            <Button leftSection={<ChevronDown size={16} />}>Scene 2</Button>
          </Menu.Target>
          <Menu.Dropdown>
            <Menu.Item leftSection={<Settings />} onClick={() => setScene('sceneB1')}>Scene B1</Menu.Item>
            <Menu.Item leftSection={<Settings />} onClick={() => setScene('sceneB2')}>Scene B2</Menu.Item>
          </Menu.Dropdown>
        </Menu>
      </Group>
    );
  }

export default NavBar; // 确保导出默认的组件
