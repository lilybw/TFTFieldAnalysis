import './App.css'
import Header from './components/header/Header'

function App() {

  return (
    <div className="App">
      <Header frontendVersion="alpha 0.0.1" backendVersion="alpha 0.0.5"/>

      <h1 className="main-title">Vector Model Analysis of Team Fight Tactics</h1>
      <div className="main-options">
        <button className="create-model-button">
          <h2>Create Model</h2>
        </button>
        <button className="browse-model-button">
          <h2>Browse</h2>
        </button>
      </div>
    </div>
  )
}

export default App
