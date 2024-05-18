//两个必要核心包
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';//导入项目根组件
import '@mantine/core/styles.css';

//把跟组件渲染到id为root的dom节点上
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);


