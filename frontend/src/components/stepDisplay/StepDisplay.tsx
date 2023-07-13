import React from 'react';
import "./StepDisplay.css";

interface StepDisplayProps{
    steps: number;
    stepNames: string[];
    currentStep: number;
}

export default function StepDisplay({steps, stepNames, currentStep}: StepDisplayProps): JSX.Element {

    const getClassListFor(index: number){
        let list = "step-display-dot"
    }

    return (
        <div className="StepDisplay">
            <div className="dots">
                {stepNames.map((name, index) => {
                    return (
                        <h3 className={index == currentStep ? "current-step" : ""} key={index}>{name}</h3>
                    )
                })}
            </div>

        </div>
    )
}