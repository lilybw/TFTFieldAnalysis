import React from 'react';
import './DataPointViewPort.css';
import { DataPointDTO, EdgeDTO } from '../../../ts/types';
import { getEdgeSets, getPoints } from '../../../ts/backendIntegration';
import { toMap } from '../../../ts/dataTypeTranslator';

interface DataPointViewPort {
    point: DataPointDTO | null;
    modelId: number;
}

export default function DataPointViewPort({point, modelId}: DataPointViewPort): JSX.Element {
    const [resultingPoints, setResultingPoints] = React.useState<{ [key: number]: DataPointDTO; }>({});
    const [edgesForPoint, setEdgesForPoint] = React.useState<EdgeDTO[]>([]);

    React.useEffect(() => {
        if(point == null) return;

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

        }
        loadEdgesAndResultingPoints();
    }, [point])

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
                <div className="dpwp-point">
                    <p>{point.namespace}</p>
                    <p>{point.id}</p>
                    <p>{point.tags}</p>
                </div>
                <div className="dpwp-edge-list">
                    {edgesForPoint.map((edge,index) => (
                        <div className="dpwp-edge" key={index}>
                            {edge.occurrence}
                        </div>
                    ))}
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
