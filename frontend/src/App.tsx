import './App.css'
import Header from './components/header/Header'

function App() {

  return (
    <div className="App">
      <Header frontendVersion="alpha 0.0.1"/>

      <h1 className="main-title">Vector Model Analysis of Team Fight Tactics</h1>
      <div className="main-options">
        <a className="create-model-button" href="/create">
          <h2>Create Model</h2>
        </a>
        <a className="browse-model-button" href="browse">
          <h2>Browse</h2>
        </a>
      </div>
    </div>
  )
}

export default App
