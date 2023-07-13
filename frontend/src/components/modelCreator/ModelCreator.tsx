import React from 'react';
import './ModelCreator.css';
import { Backupable, Viewer } from '../../ts/component';
import { getServerLocations, getTrainingServerTargets, validateIGNandGetPUUID } from '../../ts/backendIntegration';
import StepDisplay from '../stepDisplay/StepDisplay';

interface ModelCreatorProps extends Backupable, Viewer{}


export default function ({goView, backup}: ModelCreatorProps): JSX.Element {
    const [serverTargets, setServerTargets] = React.useState<string[]>([]);
    const [possibleTargets, setPossibleTargets] = React.useState<string[]>([]);
    const [matchCount, setMatchCount] = React.useState<number>(10)
    const [puuid, setPuuid] = React.useState<string | null>(null);
    const [selectedServerTarget, setSelectedServerTarget] = React.useState<string | null>(null);
    const [confine, setConfine] = React.useState<boolean>(false);
    const [progress, setProgress] = React.useState<number>(0);
    const [accountServers, setAccountServers] = React.useState<string[]>([]);
    const [selectedAccountServer, setSelectedAccountServer] = React.useState<string | null>(null);
    const [ignIsValid, setIgnIsValid] = React.useState<boolean>(false);
    const [ign, setIGN] = React.useState<string | null>(null);
    const [ignError, setIgnError] = React.useState<string | null>(null);

    React.useEffect(() => {
        getServerLocations().then(locations => {
            setAccountServers(locations.response)
        });
    }, [])
    
    React.useEffect(() => {
        getTrainingServerTargets().then(targets => {
            setPossibleTargets(targets.response);
        })
    }, [])

    React.useEffect(() => {
        if(ign == null || selectedAccountServer == null){
            setIgnIsValid(false);
            return;
        }
        validateIGNandGetPUUID(ign,selectedAccountServer).then(response => {
            if(response.response == null){
                setIgnIsValid(false);
                setIgnError(response.details.name);
                console.log(ign + " and " + selectedAccountServer + " is not valid")
                return;
            }
            setIgnIsValid(true);
            setIgnError(null);
        })
    }, [selectedAccountServer, ign])

    const onSubmit = () => {
        console.log("Submitted model")
    }

    const goNextFromIgn = () => {
        if(ignIsValid){
            setProgress(progress + 1);
        }else{

        }
    }

    const getStep = () => {
        switch (progress) {
            case 0: {
                return (
                    <>
                    <h2>Step 1: Provide your IGN </h2>
                    <div className="puuid-input-container">
                        <h3>IGN</h3>
                        <input type="text" 
                            className="ign-input" 
                            placeholder=". . ." 
                            onChange={e => setIGN(e.target.value)}
                            />
                    </div>

                    <h3>Select region</h3>
                        <div className="account-server-list">
                        {
                            accountServers.map((location, index) => {
                                return (
                                    <button onClick={() => setSelectedAccountServer(location)} key={index} className={"account-select-button" + (selectedAccountServer == location ? " chosen" : "")}>
                                        {location}
                                    </button>
                                )
                            } )
                        }
                    </div>

                    {
                        ignError != null ?
                        <h4 className="ign-error">{ignError}</h4>
                        :
                        <></>
                    }

                    <button className="go-next-button" onClick={() => goNextFromIgn()}>
                        &gt;
                    </button>
                    </>
                )
            } //submit and validate ign to get puuid
            case 1: {
                return (
                    <>
                    <h2>Step 2: Select your TFT server</h2>
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

                    <button className="submit-model"
                        onClick={() => onSubmit()}
                    >
                        Run
                    </button>
                    </>
                )
            }//choose settings
            case 2: {
                return (
                    <>
                    
                    </>
                )
            }//done
        }
    }

    return (
        <div className="ModelCreator">
            <StepDisplay steps={["IGN", "Settings", "Done!"]} currentStep={progress}/>
            {getStep()}

        </div>

    )


}