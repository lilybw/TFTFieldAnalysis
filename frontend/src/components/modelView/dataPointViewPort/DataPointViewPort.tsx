import React from 'react';
import './DataPointViewPort.css';
import { DataPointDTO, EdgeDTO, ModelMetaDataDTO } from '../../../ts/types';
import { getEdgeSets, getModelMetadata, getPoints } from '../../../ts/backendIntegration';
import { toMap } from '../../../ts/dataTypeTranslator';
import { drawEdges } from '../../../ts/dpwpCanvasManager';

interface DataPointViewPort {
    point: DataPointDTO | null;
    modelId: number;
    selectPoint: (point: DataPointDTO) => void;
    metadata: ModelMetaDataDTO | null;
}

export default function DataPointViewPort({point, modelId, selectPoint, metadata}: DataPointViewPort): JSX.Element {
    const [resultingPoints, setResultingPoints] = React.useState<{ [key: number]: DataPointDTO; }>({});
    const [edgesForPoint, setEdgesForPoint] = React.useState<EdgeDTO[]>([]);
    const canvasRef = React.useRef<HTMLCanvasElement>(null);
    const [resultingPointOffsets, setResultingPointOffsets] = React.useState<{ [key: number]: {x: number, y: number}; }>({});
    const [canvasDim, setCanvasDim] = React.useState<{width: number, height: number}>({width: 1000, height: 1000});
    const [edgeMinOccurrence, setEdgeMinOccurrence] = React.useState<number>(1);

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
                if(response.response == null) return;
                return response.response[point.id];
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
                if(response.response == null) {
                    setResultingPoints({});
                    return;
                };
                setResultingPoints(toMap(response.response, (point) => point.id));
            });

            const pointOffsets = await drawEdges(edges, canvasRef.current, point.id, metadata!,edgeMinOccurrence);
            setResultingPointOffsets(pointOffsets);
        }
        loadEdgesAndResultingPoints();
    }, [point])

    React.useLayoutEffect(() => {
        const updateCanvas = () => {
            if(point == null) return;
            drawEdges(edgesForPoint, canvasRef.current, point.id, metadata!,edgeMinOccurrence);
            setCanvasDim({
                    width: window.innerWidth*.5, 
                    height: window.innerHeight*.5
                });
        }
        window.addEventListener("resize", updateCanvas);
        return () => window.removeEventListener("resize", updateCanvas);
    }, [window.innerWidth, window.innerHeight])

    React.useEffect(() => {
        if(point == null) return;
        drawEdges(edgesForPoint, canvasRef.current, point.id, metadata!, edgeMinOccurrence);
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
                    width={canvasDim.width} height={canvasDim.height}>
                </canvas>
                <div className="dpwp-point">
                    <p>{point.namespace}</p>
                    <p>{point.id}</p>
                    <p>{point.tags}</p>
                </div>
                {Object.keys(resultingPointOffsets).map((pointId) => {
                    const resultPoint = resultingPoints[Number(pointId)];

                    //since it may happen that the resulting points hasn't been loaded yet
                    if(resultPoint == null || resultPoint == undefined) return;

                    return(
                        <button className="dpwp-point-resulting" key={resultPoint.id}
                            onClick={() => selectPoint(resultPoint)}
                            style={{
                                left: resultingPointOffsets[resultPoint.id].x, 
                                top: resultingPointOffsets[resultPoint.id].y,
                                position: "absolute"
                            }}
                        >
                            <p className="no-flow">{resultPoint.namespace}</p>
                            <p className="no-flow">{resultPoint.id}</p>
                            <p className="no-flow">{resultPoint.tags}</p>
                        </button>
                    )
                })}
                <div className="dpwp-sensitivity-controls">
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
                </>
            )
        }
    }
    
    return (
        <div className="DataPointViewPort">
            {getContent()}
        </div>
    );
}
