import React from 'react';
import './ModelView.css';
import { DataPointDTO, EdgeDTO, ModelDTO } from '../../ts/types';
import { getEdgeSets, getModel, getNamespaces, getPoints } from '../../ts/backendIntegration';
import { toList } from '../../ts/dataTypeTranslator';
import { Backupable } from '../../ts/component';
import ModelMenu from './modelMenu/ModelMenu';

interface ModelViewProps extends Backupable{
    modelId: number;
}

export default function ModelView({modelId, backup}: ModelViewProps): JSX.Element {
    const [selectedNamespace, setSelectedNamespace] = React.useState<string>("")
    const [selectedPointIds, setSelectedPointIds] = React.useState<number[]>([])
    const [selectedTags, setSelectedTags] = React.useState<string[]>([])
    const [viewedPoints, setViewedPoints] = React.useState<DataPointDTO[]>([])
    const [availablePointsForSelection, setAvailablePointsForSelection] = React.useState<DataPointDTO[]>([])
    const [selectedPoint, setSelectedPoint] = React.useState<DataPointDTO | null>(null)
    const [edgesForPoint, setEdgesForPoint] = React.useState<EdgeDTO[]>([])

    React.useEffect(() => {
        getPoints(modelId, selectedNamespace, selectedPointIds, selectedTags)
        .then(pointsResponse => {
            setViewedPoints(toList(pointsResponse.response))
        })
    }, [selectedNamespace, selectedTags, selectedPointIds])

    React.useEffect(() => {
        if(selectedPoint == null) return;
        getEdgeSets(modelId, [selectedPoint.id]).then(response => {
            if(response.response == null) return;
            setEdgesForPoint(response.response.get(selectedPoint.id) ?? []);
        })
    }, [selectedPoint])

    const clearSelection = () => {
        setSelectedPointIds([]);
        setSelectedTags([]);
        setSelectedNamespace("");
    }
    const selectNamespace = (namespace: string, additive: boolean) => {
        if(!additive){
            clearSelection();
        }
        setSelectedNamespace(namespace);
    }
    const addOrSetTag = (tag: string, additive: boolean) => {
        if(!additive){
            clearSelection();
        }
        setSelectedTags([...selectedTags, tag]);
    }
    const addOrSetPointId = (pointId: number, additive: boolean) => {
        if(!additive){
            clearSelection();
        }
        setSelectedPointIds([...selectedPointIds, pointId]);
    }

    return (
        <div className="ModelView">
            <ModelMenu modelId={modelId} 
                setNamespace={selectNamespace} 
                addOrSetTag={addOrSetTag}
                addOrSetPointId={addOrSetPointId}
            />
            <div className="mv-middle">
                <h2>Edges</h2>
                <div className="mv-edge-list">
                    {edgesForPoint.map((edge, index) => {
                        return (
                            <button className="mv-edge-list-item" key={index}>
                                <p>Occurence: {edge.occurrence}</p>    
                                <p>Point A: {edge.pointA}</p>
                                <p>Point B: {edge.pointB}</p>
                            </button>
                        )
                    }
                    )}
                </div>
            </div>
            <div className="dp-list">
                <h2>Points</h2>
                {viewedPoints.map((point) => {
                    return (
                        <button className="dp-list-item" key={point.id}
                            onClick={() => setSelectedPoint(point)}
                        >
                            <p>id: {point.id}</p>
                            <p>tags: {point.tags}</p>
                        </button>
                    )
                }
                )}
            </div>
        </div>
    )

}