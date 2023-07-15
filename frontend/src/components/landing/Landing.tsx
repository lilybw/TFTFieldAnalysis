import React from 'react';

interface LandingProps{
    goBrowse: () => void;
    goCreate: () => void;
}

export default function Landing({goBrowse, goCreate}: LandingProps): JSX.Element {
    return (
        <>
        <h1 className="main-title">Vector Model Analysis of Team Fight Tactics</h1>
        <div className="main-options">
          <div className="horizonal-flex">
            <button className="create-model-button" onClick={() => goCreate()}>
              <h2>Create Model</h2>
            </button>
            <button className="browse-model-button" onClick={() => goBrowse()}>
              <h2>Browse</h2>
            </button>
          </div>
        </div>
        </>
    )
}