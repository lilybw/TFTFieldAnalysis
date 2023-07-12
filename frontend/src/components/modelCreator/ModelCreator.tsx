import React from 'react';
import './ModelCreator.css';

interface ModelCreatorProps{}


export default function ({}: ModelCreatorProps): JSX.Element {
    const [serverTargets, setServerTargets] = React.useState<string[]>([]);
    const [matchCount, maxMatchCount] = React.useState<number>(10)
    const [puuid, setPuuid] = React.useState<string>();
    

    return (
        <div className="ModelCreator">
            <h1>Model Creator</h1>


        </div>

    )


}