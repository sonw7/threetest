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
            <Menu.Item leftSection={<ThreeDCubeSphere />} onClick={() => setScene('scene1a')}>Scene 1A</Menu.Item>
            <Menu.Item leftSection={<ThreeDCubeSphere />} onClick={() => setScene('scene1b')}>Scene 1B</Menu.Item>
          </Menu.Dropdown>
        </Menu>
        <Menu>
          <Menu.Target>
            <Button leftSection={<ChevronDown size={16} />}>Scene 2</Button>
          </Menu.Target>
          <Menu.Dropdown>
            <Menu.Item leftSection={<Settings />} onClick={() => setScene('scene2a')}>Scene 2A</Menu.Item>
            <Menu.Item leftSection={<Settings />} onClick={() => setScene('scene2b')}>Scene 2B</Menu.Item>
          </Menu.Dropdown>
        </Menu>
      </Group>
    );
  }

export default NavBar; // 确保导出默认的组件
