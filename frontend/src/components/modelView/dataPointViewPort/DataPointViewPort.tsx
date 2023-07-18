import React from 'react';
import './DataPointViewPort.css';
import { DataPointDTO, EdgeDTO, ModelMetaDataDTO } from '../../../ts/types';
import { getEdgeSets, getModelMetadata, getPoints } from '../../../ts/backendIntegration';
import { toMap } from '../../../ts/dataTypeTranslator';
import { drawEdges } from '../../../ts/dpwpCanvasManager';
import { contains, containsAny, notInPlaceAdd, notInPlaceRemoveAll } from '../../../ts/arrayUtil';

interface DataPointViewPort {
    point: DataPointDTO | null;
    modelId: number;
    selectPoint: (point: DataPointDTO) => void;
    metadata: ModelMetaDataDTO | null;
    namespaces: string[];
    center: {x: number, y: number}
}
//a better viewport would probably be showing resulting points within the selected alonside the occurrence value of the edge.
export default function DataPointViewPort({ point, modelId, selectPoint, metadata, namespaces, center }: DataPointViewPort): JSX.Element {
    const [resultingPoints, setResultingPoints] = React.useState<{ [key: number]: DataPointDTO; }>({});
    const [edgesForPoint, setEdgesForPoint] = React.useState<EdgeDTO[]>([]);
    const canvasRef = React.useRef<HTMLCanvasElement>(null);
    const [resultingPointOffsets, setResultingPointOffsets] = React.useState<{ [key: number]: {x: number, y: number}; }>({});
    const [edgeMinOccurrence, setEdgeMinOccurrence] = React.useState<number>(1);
    const [ignoredNamespaces, setIgnoredNamespaces] = React.useState<string[]>([]);
    const [canvasPositionTransform, setCanvasPositionTransform] = 
        React.useState<{x: number, y: number}>({x: Math.min(center.x), y: Math.min(center.y)});
    
    console.log("Canvas transform: " + canvasPositionTransform.x + ", " + canvasPositionTransform.y); //TODO: Why is this zero?
    const resetAll = () => {
        setResultingPoints({});
        setEdgesForPoint([]);
        setResultingPointOffsets({});
    }

    React.useEffect(() => {
        if(point == null) return;

        resetAll();

        const loadEdgesAndResultingPoints = async () => {
            let edges = await getEdgeSets(modelId, [point.id]).then(response => {
                if(response.data == null) return;
                return response.data[point.id];
            });
            
            if(edges == null || edges == undefined) return;

            setEdgesForPoint(edges);
            const ids: number[] = [];
            for(const edge of edges){
                if(edge.pointA != point.id){
                    ids.push(edge.pointA);
                }else{
                    ids.push(edge.pointB);
                }
            }

            getPoints(modelId, undefined, ids).then(response => {
                if(response.data == null) {
                    setResultingPoints({});
                    return;
                };
                setResultingPoints(toMap(response.data, (point) => point.id));
            });

            const pointOffsets = await drawEdges(edges, canvasRef.current, point.id,edgeMinOccurrence);
            setResultingPointOffsets(pointOffsets);
        }
        loadEdgesAndResultingPoints();
    }, [point])

    React.useEffect(() => {
        if(point == null) return;
        drawEdges(edgesForPoint, canvasRef.current, point.id, edgeMinOccurrence);
    }, [edgeMinOccurrence])

    const getContent = () => {
        if(point == null){
            return (
                <div className="dpwp-point">
                    Select a namespace on the left, then a corresponding point on the right to view its edges.
                </div>
            )
        }else{
            return(
                <>
                <canvas className="dpwp-canvas" id="dpwp-canvas" 
                    ref={canvasRef}
                    width={center.x} height={center.y}>
                </canvas>
                <button className="dpwp-point"
                    onClick={() => selectPoint(point)}
                >
                    <p>{point.namespace}</p>
                    <p>{point.id}</p>
                    <p>{point.tags}</p>
                </button>
                {Object.keys(resultingPointOffsets).map((pointId) => {
                    const resultPoint = resultingPoints[Number(pointId)];

                    //since it may happen that the resulting points hasn't been loaded yet
                    if(resultPoint == null || resultPoint == undefined) return;
                    if(contains(ignoredNamespaces, resultPoint.namespace, (r,t) => r == t)) return;

                    return(
                        <button className="dpwp-point-resulting" key={resultPoint.id}
                            onClick={() => selectPoint(resultPoint)}
                            style={{
                                left: resultingPointOffsets[resultPoint.id].x + canvasPositionTransform.x, 
                                top: resultingPointOffsets[resultPoint.id].y + canvasPositionTransform.y,
                                position: "absolute"
                            }}
                        >
                            <p className="no-flow">{resultPoint.namespace}</p>
                            <p className="no-flow">{resultPoint.id}</p>
                            <p className="no-flow">{resultPoint.tags}</p>
                        </button>
                    )
                })}
                
                </>
            )
        }
    }
    
    return (
        <div className="DataPointViewPort">
            {getContent()}
            <div className="dpwp-sensitivity-controls">
                <div>
                <h3>Occurrence</h3>
                <div className="horizonal-flex">
                    <p>Min: {metadata?.cachedValues.MIN_OCCURRENCE_VALUE}</p>
                    <input className="slider" type="range" min="0"
                        max={metadata?.cachedValues.MAX_OCCURRENCE_VALUE}
                        id="occurrence-slider"
                        defaultValue={edgeMinOccurrence}
                        onChange={e => setEdgeMinOccurrence(Number(e.target.value))}
                    />
                    <p>Max: {metadata?.cachedValues.MAX_OCCURRENCE_VALUE}</p>
                </div>
                </div>
                <div>
                <h3>Ignored Namespaces</h3>
                <div className="vertical-flex">
                    {namespaces.map((namespace, index) => {
                        return (
                            <div className="horizonal-flex ignore-options" key={index} >
                                <label htmlFor={"namespace-checkbox-" + namespace}>{namespace}</label>
                                <input type="checkbox" id={"namespace-checkbox-" + namespace} className="ignore-namespace-checkbox" key={index}
                                    onClick={e => {
                                        const checkbox = e.target as HTMLInputElement;
                                        if (checkbox.checked){
                                            setIgnoredNamespaces(notInPlaceAdd(ignoredNamespaces, namespace));
                                        }else{
                                            setIgnoredNamespaces(notInPlaceRemoveAll(ignoredNamespaces, namespace));
                                        }
                                    }}
                                />
                            </div>
                        )
                    })
                    }
                </div>
                </div>
            </div>
        </div>
    );
}
