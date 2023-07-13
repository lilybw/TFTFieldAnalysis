import React from 'react';
import './ModelCreator.css';
import { Backupable, Viewer } from '../../ts/component';
import { getTrainingServerTargets } from '../../ts/backendIntegration';

interface ModelCreatorProps extends Backupable, Viewer{}


export default function ({goView, backup}: ModelCreatorProps): JSX.Element {
    const [serverTargets, setServerTargets] = React.useState<string[]>([]);
    const [possibleTargets, setPossibleTargets] = React.useState<string[]>([]);
    const [matchCount, setMatchCount] = React.useState<number>(10)
    const [puuid, setPuuid] = React.useState<string>();
    const [selectedServerTarget, setSelectedServerTarget] = React.useState<string>();
    const [confine, setConfine] = React.useState<boolean>(false);

    React.useEffect(() => {
        getTrainingServerTargets().then(targets => {
            setPossibleTargets(targets.response);
        })
    })

    const onSubmit = () => {
        console.log("Submitted model")
    }

    return (
        <div className="ModelCreator">
            <h2>Step 1: Select your server</h2>
            <div className="server-target-select">
                {possibleTargets.map((target, index) => {
                    return (
                        <button className={"server-option" + (selectedServerTarget == target ? " chosen" : "")} key={index} onClick={() => {
                            setSelectedServerTarget(target);
                        }}>
                            {target}
                        </button>
                    )
                })}
            </div>
            <br></br>
            <h2>Step 2: Provide IGN or PUUID</h2>
            <div className="puuid-input-container">
                <h3>PUUID</h3>
                <h3>IGN</h3>

            </div>

            <h2>Step 3: How patient are you?</h2>
            <label htmlFor="maxMatchCount">Max Match Count</label>
            <input type="number" id="maxMatchCount" 
                value={matchCount} onChange={e => setMatchCount(Number(e.target.value))}
                ></input>

            <h2>Step 4: Further miscellanious options:</h2>
            <label htmlFor="confine">Confine search</label>
            <input type="checkbox" id="confine" 
                value={confine ? "on" : "off"} onChange={e => setConfine(Boolean(e.target.value))}
                ></input>

            <div className="horizontal">
                <button className="submit-model"
                    onClick={() => onSubmit()}
                >
                    Run
                </button>
            {
                backup ?
                backup
                :
                <></>
            }
            </div>

        </div>

    )


}