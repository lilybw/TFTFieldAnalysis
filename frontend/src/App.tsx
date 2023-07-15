import './App.css'
import Header from './components/header/Header'
import React from 'react';
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
  const goView = (modelId: number) => {
    updatePrev();
    setAppBody(
      <ModelView modelId={modelId} 
        backup={<UniversalBackOffElement />}
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
