import React from 'react';
import './ModelView.css';
import { DataPointDTO, ModelDTO } from '../../ts/types';
import { getModel, getNamespaces, getPoints } from '../../ts/backendIntegration';
import { toList } from '../../ts/dataTypeTranslator';

interface ModelViewProps{
    modelId: number;
}

export default function ModelView({modelId}: ModelViewProps): JSX.Element {
    const [namespaces, setNamespaces] = React.useState<string[]>([]);
    const [selectedNamespace, setSelectedNamespace] = React.useState<string>("")
    const [selectedPointIds, setSelectedPointIds] = React.useState<number[]>([])
    const [selectedTags, setSelectedTags] = React.useState<string[]>([])
    const [viewedPoints, setViewedPoints] = React.useState<DataPointDTO[]>([])

    React.useEffect(() => {
        getNamespaces(modelId).then(namespaces => {
            setNamespaces(namespaces.response);
        })
    },[])

    React.useEffect(() => {
        getPoints(modelId, selectedNamespace, selectedPointIds, selectedTags)
        .then(pointsResponse => {
            setViewedPoints(toList(pointsResponse.response))
        })
    }, [selectedNamespace])
    console.log("Viewing model: " + modelId)

    return (
        <div className="ModelView">
            <h1>Model {modelId}</h1>


        </div>
    )

}