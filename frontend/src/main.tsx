import React from 'react'
import ReactDOM from 'react-dom/client'
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import App from './App.tsx'
import Root from './routes/root.tsx'
import './index.css'
import ErrorPage from './components/error404/error.tsx';
import ModelBrowser from './components/modelBrowser/ModelBrowser.tsx';
import ModelView from './components/modelView/ModelView.tsx';
import ModelCreator from './components/modelCreator/ModelCreator.tsx';

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/browse",
    element: <ModelBrowser />,
  },
  {
    path: "/create",
    element: <ModelCreator />
  },
  {
    path: "/model/:modelId",
    element: <ModelView modelId={1}/>,
    errorElement: <ErrorPage message="Yeah nah, this guy quit." />
  }
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
)
