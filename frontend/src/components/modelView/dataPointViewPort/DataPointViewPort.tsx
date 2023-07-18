import React from 'react';
import './DataPointViewPort.css';
import { ComponentTransform, DataPointDTO, DrawCallProperties, EdgeDTO, ModelMetaDataDTO } from '../../../ts/types';
import { getEdgeSets, getModelMetadata, getPoints } from '../../../ts/backendIntegration';
import { toMap } from '../../../ts/dataTypeTranslator';
import { drawEdges } from '../../../ts/dpwpCanvasManager';
import { DuckType, contains, containsAny, containsAnyGen, duckFilter, notInPlaceAdd, notInPlaceRemoveAll } from '../../../ts/arrayUtil';

interface DataPointViewPort {
    point: DataPointDTO | null;
    modelId: number;
    selectPoint: (point: DataPointDTO) => void;
    metadata: ModelMetaDataDTO | null;
    namespaces: string[];
    center: {x: number, y: number}
}



const getEmptyProperties = (localOccMin?: number, localOccMax?: number, processedEdges?: EdgeDTO[]): DrawCallProperties => {
    return {
        localOccMin: localOccMin || 0,
        localOccMax: localOccMax || 1,
        processedEdges: processedEdges || [],
        maxLineWidth: 0,
        canvasRef: undefined,
        transform: {x: 1, y: 1, w: 1, h: 1}

    }
}

//a better viewport would probably be showing resulting points within the selected alonside the occurrence value of the edge.
export default function DataPointViewPort({ point, modelId, selectPoint, metadata, namespaces, center }: DataPointViewPort): JSX.Element {
    const [resultingPoints, setResultingPoints] = React.useState<{ [key: number]: DataPointDTO; }>({});
    const [edgesForPoint, setEdgesForPoint] = React.useState<EdgeDTO[]>([]);
    const canvasRef = React.useRef<HTMLCanvasElement>(null);
    const [resultingPointOffsets, setResultingPointOffsets] = React.useState<{ [key: number]: {x: number, y: number} }>({});
    const [edgeMinOccurrence, setEdgeMinOccurrence] = React.useState<number>(1);
    const [ignoredNamespaces, setIgnoredNamespaces] = React.useState<string[]>([]);
    
    const xy = Math.max(center.x, center.y);
    const [canvasTransform, setCanvasTransform] = 
        React.useState<ComponentTransform>({x: xy, y: xy, w: xy, h: xy});
    
    const resetCollections = () => {
        setResultingPoints({});
        setEdgesForPoint([]);
        setResultingPointOffsets({});
    }

    const preprocessEdgesShown = (edges: EdgeDTO[]): DrawCallProperties => {
        const processed = [];
        let localOccMin = 10000;
        let localOccMax = 1;

        for(const edge of edges){
            const pointA = resultingPoints[edge.pointA];
            const pointB = resultingPoints[edge.pointB];

            if (pointA == null || pointB == null) continue;
            if (edge.occurrence < edgeMinOccurrence) continue;
            if (containsAnyGen(ignoredNamespaces, [pointA, pointB], (r,t) => r == t.namespace)) continue;

            processed.push(edge);
            localOccMin = Math.min(localOccMin, edge.occurrence);
            localOccMax = Math.max(localOccMax, edge.occurrence);
        }

        return getEmptyProperties(localOccMin, localOccMax, processed);
    }

    const onDrawCall = async (edges: EdgeDTO[]): Promise<{ [key: number]: { x: number, y: number } }> => {
        if(edges.length == 0 || point == null) return {};
        const properties = preprocessEdgesShown(edges);
        return drawEdges(point.id, properties);
    }

    React.useEffect(() => {
        if(point == null) return;

        resetCollections();

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

            await getPoints(modelId, undefined, ids).then(response => {
                if(response.data == null) {
                    setResultingPoints({});
                    return;
                };
                setResultingPoints(toMap(response.data, (point) => point.id));
            });

            const pointOffsets = await onDrawCall(edges);
            setResultingPointOffsets(pointOffsets);
        }
        loadEdgesAndResultingPoints();
    }, [point])

    React.useEffect(() => {
        if(point == null) return;
        onDrawCall(edgesForPoint);
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
                <div className="dpwp-canvas-container">
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
                {duckFilter(Object.keys(resultingPointOffsets), DuckType.NUMBER).map((pointId) => {
                    const resultPoint = resultingPoints[Number(pointId)];

                    //since it may happen that the resulting points hasn't been loaded yet
                    if(resultPoint == null || resultPoint == undefined) return;
                    if(contains(ignoredNamespaces, resultPoint.namespace, (r,t) => r == t)) return;
                    console.log(canvasTransform);
                    return(
                        <button className="dpwp-point-resulting" key={resultPoint.id}
                            onClick={() => selectPoint(resultPoint)}
                            style={{
                                left: resultingPointOffsets[resultPoint.id].x + canvasTransform.x + "px", 
                                top: resultingPointOffsets[resultPoint.id].y + canvasTransform.y + "px"
                            }}
                        >
                            <p className="no-flow">{resultPoint.namespace}</p>
                            <p className="no-flow">{resultPoint.id}</p>
                            <p className="no-flow">{resultPoint.tags}</p>
                        </button>
                    )
                })}
                
                </div>
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
