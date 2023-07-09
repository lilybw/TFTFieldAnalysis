import 'ModelBrowser.css'
import React, { useEffect, useState } from 'react'
import { getAllModelIds } from '../../ts/backendIntegration';
import { toList } from '../../ts/dataTypeTranslator';

interface ModelBrowserProps {
}

export default function ModelBrowser({}: ModelBrowserProps): JSX.Element{
    const [modelList, setModelList] = useState<number[]>([]);

    useEffect(() => {
        getAllModelIds().then((modelIds) => {
            setModelList(toList(modelIds.response));
        });
    }, []);
    
    return (
        <div className="ModelBrowser">
            <h1>Model Browser</h1>
            <div className="model-list">
                <h2>Models</h2>
                {modelList.map((modelId) => {
                    return (
                        <button className="model-list-item">
                            <h3>{modelId}</h3>
                        </button>
                    )
                })
                }
            </div>
        </div>
    )
}
