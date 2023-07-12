import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import ErrorPage from './components/error404/error.tsx';
import ModelBrowser from './components/modelBrowser/ModelBrowser.tsx';
import ModelView from './components/modelView/ModelView.tsx';
import ModelCreator from './components/modelCreator/ModelCreator.tsx';
import Header from './components/header/Header.tsx'



ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
