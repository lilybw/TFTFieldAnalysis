import React from 'react';
import "./StepDisplay.css";

interface StepDisplayProps{
    steps: string[];
    currentStep: number;
}

export default function StepDisplay({steps, currentStep}: StepDisplayProps): JSX.Element {

    const getClassListFor = (index: number): string => {
        let list = "step-display-dot";
        if(index == currentStep){
            list += " current-step";
        }
        if(index < currentStep){
            list += " former-step";
        }
        return list;
    }

    const appendSpacer = (index: number): JSX.Element => {
        if(index != steps.length -1 ){
            return (
                <span className="dots-spacer">&gt;</span>
            )
        }
        return <></>
    }

    return (
        <div className="StepDisplay">
            <div className="dots">
                {steps.map((name, index) => {
                    return (
                        <div key={index}>
                            <h3 className={getClassListFor(index)} >{name}</h3>
                            {appendSpacer(index)}
                        </div>
                    )
                })}
            </div>

        </div>
    )
}