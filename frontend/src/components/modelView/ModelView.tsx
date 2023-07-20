import React from 'react';
import './ModelView.css';
import { DataPointDTO, ModelMetaDataDTO } from '../../ts/types';
import { getModelMetadata, getNamespaces, getPoints } from '../../ts/backendIntegration';
import { Backupable } from '../../ts/component';
import ModelMenu from './modelMenu/ModelMenu';
import DataPointViewPort from './dataPointViewPort/DataPointViewPort';
import { contains } from '../../ts/arrayUtil';

interface ModelViewProps extends Backupable{
    modelId: number;
    center: {x: number, y: number}
}

export default function ModelView({modelId, backup, center}: ModelViewProps): JSX.Element {
    const [selectedNamespaces, setSelectedNamespaces] = React.useState<string[]>([]);
    const [selectedPointIds, setSelectedPointIds] = React.useState<number[]>([]);
    const [selectedTags, setSelectedTags] = React.useState<string[]>([]);
    const [viewedPoints, setViewedPoints] = React.useState<DataPointDTO[]>([]);
    const [sortedViewedPoints, setSortedViewedPoints] = React.useState<DataPointDTO[]>([]);
    const [selectedPoint, setSelectedPoint] = React.useState<DataPointDTO | null>(null);
    const [modelMetadata, setModelMetadata] = React.useState<ModelMetaDataDTO | null>(null);
    const [namespaces, setNamespaces] = React.useState<string[]>([]);

    React.useEffect(() => {
        getPoints(modelId, selectedNamespaces, selectedPointIds, selectedTags)
            .then(pointsResponse => {
            setViewedPoints(pointsResponse.data);
            setSortedViewedPoints(pointsResponse.data);
        })
    }, [selectedNamespaces, selectedTags, selectedPointIds])

    React.useEffect(() => {
        getModelMetadata(modelId).then(response => {
            if(response.data == null) return;
            setModelMetadata(response.data);
        })
        getNamespaces(modelId).then(response => {
            if(response.data == null) return;
            setNamespaces(response.data);
        })
    }, [modelId])


    const clearSelection = () => {
        setSelectedPointIds([]);
        setSelectedTags([]);
        setSelectedNamespaces([]);
    }
    const addOrSetNamespace = (namespace: string, additive: boolean) => {
        if(!additive){
            clearSelection();
        }
        setSelectedNamespaces([namespace, ...selectedNamespaces]);
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
                setNamespace={addOrSetNamespace} 
                addOrSetTag={addOrSetTag}
                namespaces={namespaces}
            />
            <DataPointViewPort 
                point={selectedPoint} 
                modelId={modelId}
                selectPoint={setSelectedPoint}
                metadata={modelMetadata}
                namespaces={namespaces}
                center={center}
            />
            <div className="dp-list">
                <h2>Points</h2>
                <input type="text" placeholder="Search" className="dp-search"
                    onChange={e => {
                        const term = e.target.value;
                        setSortedViewedPoints(viewedPoints.filter(
                            point => contains(point.tags, term, (e, t) => e == t) || Number(term) == point.id
                        ));
                    }}
                    onBlur={() => {
                        setSortedViewedPoints(viewedPoints);
                    }}
                />
                {sortedViewedPoints.map((point) => {
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