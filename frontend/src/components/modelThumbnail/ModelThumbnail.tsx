import React from "react";
import './ModelThumbnail.css'
import { getModelMetadata } from "../../ts/backendIntegration";
import { type ModelMetaDataDTO } from "../../ts/types";
import { Backupable } from "../../ts/component";

interface ModelThumbnailProps extends Backupable {
    modelId: number;
    onSelect?: (modelId: number) => void;
}

export default function ModelThumbnail({modelId, onSelect}: ModelThumbnailProps): JSX.Element {
    const [hover, setHover] = React.useState(false);
    const [metadata, setMetadata] = React.useState<ModelMetaDataDTO | null>(null);
    const [hoverCount, setHoverCount] = React.useState(0);

    React.useEffect(() => {
        if(!hover || metadata != null) return;
        getModelMetadata(modelId).then((metadata) => {
            setMetadata(metadata.response);
        });
    }, [hover])

    const appendContent = () => {
        if(hover) {
            if(metadata == null) {
                return (
                    <div className="mthumb-loading">
                        <p>Loading...</p>
                    </div>
                )
            }
            return (
                <>
                    <p>Match Count: {metadata.matchIdsEvaluated.length}</p>
                    <div className="horizontal-2">
                        <p>Max occurrence: {metadata.cachedValues.MAX_OCCURRENCE_VALUE}</p>
                        <p>Min occurrence: {metadata.cachedValues.MIN_OCCURRENCE_VALUE}</p>
                    </div>
                    <div className="horizontal-2">
                        <p>Point count: {metadata.cachedValues.POINT_COUNT}</p>
                        <p>Edge count: {metadata.cachedValues.EDGE_COUNT}</p>
                    </div>
                </>
            )
        }
    }

    const getClass = (): string => {
        if(hover) return "ModelThumbnail mthumb-expanded";
        return "ModelThumbnail";
    }


    return (
        <div className={getClass()}
            onMouseEnter={() => {
                setHoverCount(hoverCount + 1)
                setHover(true);
            }}
            onMouseLeave={() => {
                setHoverCount(hoverCount + 1)
                if(hoverCount % 4 == 0){
                    setHover(false);
                }
            }}
            onClick={
                onSelect ? 
                    () => onSelect(modelId) 
                    : 
                    () => {}
                }
            >
            <a 
                href={"/model/"+modelId}
                className="model-link"
                >{modelId}</a>
            <div className={"mthumb-body"}>
                {appendContent()}
            </div>
        </div>
    )
}