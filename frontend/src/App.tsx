import './App.css'
import Header from './components/header/Header'
import React, { useLayoutEffect } from 'react';
import Landing from './components/landing/Landing';
import ModelBrowser from './components/modelBrowser/ModelBrowser';
import ModelCreator from './components/modelCreator/ModelCreator';
import ModelView from './components/modelView/ModelView';
import Footer from './components/footer/Footer';

function App() {

  const goBack = () => {
    setAppBody(prevBody);
  }

  const UniversalBackOffElement = () => {
    return (
      <button className="previous-page-button"
          onClick={() => goBack()}
        >
        <h2>&lt;-</h2>
      </button>
    )
  }

  const updatePrev = () => {
    setPrevBody(appBody);
  }

  const goBrowse = () => {
    updatePrev();
    setAppBody(
      <ModelBrowser backup={<UniversalBackOffElement />} goView={goView}/>
    
    );
  }
  const goCreate = () => {
    updatePrev();
    setAppBody(
      <ModelCreator 
        goView={goView}
        backup={<UniversalBackOffElement />}
      />
    );
  }
  const [center, setCenter] = React.useState<{ x: number, y: number }>({ x: 0, y: 0 });
  useLayoutEffect(() => {
    const handleResize = () => {
      const { innerWidth, innerHeight } = window;
      if(innerWidth == 0 || innerHeight == 0) return;
      setCenter({ x: innerWidth / 2, y: innerHeight / 2 });
      console.log("Resize happened, center is: ", center.x, center.y)
    };
    handleResize();
    window.addEventListener("resize", handleResize);
    //Used on Component.unmount
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const goView = (modelId: number) => {
    updatePrev();
    setAppBody(
      <ModelView modelId={modelId} 
        backup={<UniversalBackOffElement />}
        center = {center}
      />);
  }
  const goLanding = () => {
    updatePrev();
    setAppBody(
      <Landing goBrowse={goBrowse} goCreate={goCreate} />
    );
  }


  const [appBody, setAppBody] = React.useState<JSX.Element>(
    <Landing goBrowse={goBrowse} goCreate={goCreate} />
  )
  const [prevBody, setPrevBody] = React.useState<JSX.Element>(
    <Landing goBrowse={goBrowse} goCreate={goCreate} />
  )

  return (
    <div className="App">
      <Header goLanding={goLanding}  frontendVersion={"alpha 0.0.1"} />
      {appBody}
      <Footer />
    </div>
  )
}

export default App
