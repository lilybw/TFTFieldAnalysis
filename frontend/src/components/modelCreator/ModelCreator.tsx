import React from 'react';
import './ModelCreator.css';
import { Backupable, Viewer } from '../../ts/component';
import { createModel, getServerLocations, getTrainingServerTargets, trainModel, validateIGNandGetPUUID } from '../../ts/backendIntegration';
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
    const [settingsError, setSettingsError] = React.useState<string | null>(null);
    const [modelId, setModelId] = React.useState<number | null>(null);
    const [trainingDone, setTrainingDone] = React.useState<boolean>(false);
    const [startCreation, setStartCreation] = React.useState<boolean>(false);

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
            setPuuid(response.response);
        })
    }, [selectedAccountServer, ign])

    React.useEffect(() => {
        if(selectedServerTarget == null || !ignIsValid || matchCount == null){
            return;
        }
        const createAndTrain = async () => {
            const creation = await createModel();
            console.log("id is " + creation.response.metadata.modelId)
            await trainModel(
                creation.response.metadata.modelId,
                {
                    maxMatchCount: matchCount,
                    patch: undefined,
                    confineToBasePlayer: confine
                },
                puuid!
            ).then(response => {
                if (response.response == null) {
                    console.log("Error training model: " + response.details.name);
                    return;
                }
                setModelId(response.response);
                setTrainingDone(true);
            });
            console.log("going to view model")
            goView(creation.response.metadata.modelId);
            setTrainingDone(true);
        }
        createAndTrain();
    }, [startCreation])

    const onSubmit = () => {
        console.log("Submitted model")
        setProgress(progress + 1);
        setStartCreation(true);
    }

    const goNextFromIgn = () => {
        if(ignIsValid){
            setProgress(progress + 1);
        }
    }
    const goNextFromSettings = () => {
        if(selectedServerTarget != null && matchCount != null){
            setProgress(progress + 1);
        }else if (selectedServerTarget == null){
            setSettingsError("Please select a server target")
        }else if (matchCount == null){
            setSettingsError("Please enter a match count")
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

                    <button className={"go-next-button" + (ignIsValid ? " ign-valid" : "")} onClick={() => goNextFromIgn()}>
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
                    <h3>Max Match Count</h3>
                    <input type="number" id="maxMatchCount" className="ign-input"
                    value={matchCount} onChange={e => setMatchCount(Number(e.target.value))}
                    ></input>

                    <h2>Step 4: Further miscellanious options:</h2>
                        <div className="horizonal-flex misc-setting">
                        <h3>Confine search</h3>
                        <input type="checkbox" id="confine"
                            value={confine ? "on" : "off"} onChange={e => setConfine(Boolean(e.target.value))}
                        ></input>
                    </div>


                    {
                        settingsError != null ?
                                <h4 className="ign-error">{settingsError}</h4>
                            :
                            <></>
                    }

                    <button className={"go-next-button" + (ignIsValid ? " ign-valid" : "")} 
                            onClick={() => goNextFromSettings()}>
                        &gt;
                    </button>
                    </>
                )
            }//choose settings
            case 2: {
                return (
                    <>
                    <h2>Step 3: Confirmation</h2>
                    <div className="two-by-many-grid">
                        <h3>IGN:</h3>
                        <p className="setting-value">{ign}</p>
                        <h3>Server:</h3>
                        <p className="setting-value">{selectedAccountServer}</p>
                        <h3>Target:</h3>
                        <p className="setting-value">{selectedServerTarget}</p>
                        <h3>Max Match Count:</h3>
                        <p className="setting-value">{matchCount}</p>
                        <h3>Confine:</h3>
                        <p className="setting-value">{confine ? "on" : "off"}</p>
                    </div>
                    <button className={"go-next-button" + (ignIsValid ? " ign-valid" : "")}
                        onClick={() => onSubmit()}>
                        Generate
                    </button>
                    </>
                )
            }//done
            case 3: {
                return (
                    <>
                    <h2>Hold on...</h2>
                    </>
                )
            }
        }
    }

    return (
        <div className="ModelCreator">
            <StepDisplay steps={["IGN", "Settings", "Confirmation", "Magick!"]} currentStep={progress}/>
            {getStep()}

        </div>

    )


}