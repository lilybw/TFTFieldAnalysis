import './ModelBrowser.css'
import React, { useEffect, useState } from 'react'
import { getAllModelIds } from '../../ts/backendIntegration';
import { toList } from '../../ts/dataTypeTranslator';
import ModelThumbnail from '../modelThumbnail/ModelThumbnail';
import { Backupable, Viewer } from '../../ts/component';

interface ModelBrowserProps extends Backupable, Viewer {
}

export default function ModelBrowser({backup, goView}: ModelBrowserProps): JSX.Element{
    const [modelList, setModelList] = useState<number[]>([]);

    useEffect(() => {
        getAllModelIds().then((modelIds) => {
            setModelList(toList(modelIds.response));
        });
    }, []);
    
    return (
        <div className="ModelBrowser center">
            <h1>Model Browser</h1>
            <div className="model-list">
                {modelList.map((modelId) => {
                    return (
                        <ModelThumbnail 
                            modelId={modelId} 
                            key={modelId}
                            onSelect={
                                (modelId) => {
                                    goView(modelId);
                                }
                            }
                        />
                    )
                })
                }
            </div>

            {
                backup ?
                backup
                :
                <></>
            }
        </div>
    )
}
