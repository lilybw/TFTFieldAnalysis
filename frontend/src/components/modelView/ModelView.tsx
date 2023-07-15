import React from 'react';
import './ModelView.css';
import { DataPointDTO, EdgeDTO, ModelDTO, ModelMetaDataDTO } from '../../ts/types';
import { getEdgeSets, getModel, getModelMetadata, getNamespaces, getPoints } from '../../ts/backendIntegration';
import { toList } from '../../ts/dataTypeTranslator';
import { Backupable } from '../../ts/component';
import ModelMenu from './modelMenu/ModelMenu';
import DataPointViewPort from './dataPointViewPort/DataPointViewPort';
import { contains } from '../../ts/arrayUtil';

interface ModelViewProps extends Backupable{
    modelId: number;
}

export default function ModelView({modelId, backup}: ModelViewProps): JSX.Element {
    const [selectedNamespace, setSelectedNamespace] = React.useState<string>("");
    const [selectedPointIds, setSelectedPointIds] = React.useState<number[]>([]);
    const [selectedTags, setSelectedTags] = React.useState<string[]>([]);
    const [viewedPoints, setViewedPoints] = React.useState<DataPointDTO[]>([]);
    const [sortedViewedPoints, setSortedViewedPoints] = React.useState<DataPointDTO[]>([]);
    const [selectedPoint, setSelectedPoint] = React.useState<DataPointDTO | null>(null);
    const [modelMetadata, setModelMetadata] = React.useState<ModelMetaDataDTO | null>(null);

    React.useEffect(() => {
        getPoints(modelId, selectedNamespace, selectedPointIds, selectedTags)
        .then(pointsResponse => {
            setViewedPoints(pointsResponse.response);
            setSortedViewedPoints(pointsResponse.response);
        })
    }, [selectedNamespace, selectedTags, selectedPointIds])

    React.useEffect(() => {
        getModelMetadata(modelId).then(response => {
            if(response.response == null) return;
            setModelMetadata(response.response);
        })
    }, [modelId])

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
            <DataPointViewPort 
                point={selectedPoint} 
                modelId={modelId}
                selectPoint={setSelectedPoint}
                metadata={modelMetadata}
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