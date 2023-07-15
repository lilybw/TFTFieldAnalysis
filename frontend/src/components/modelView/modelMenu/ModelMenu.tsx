import React from 'react';
import './ModelMenu.css';
import { getNamespaces, getTagsInModel } from '../../../ts/backendIntegration';

interface ModelMenuProps{
    modelId: number;
    setNamespace: (namespace: string, additive: boolean) => void;
    addOrSetTag: (tag: string, additive: boolean) => void;
    addOrSetPointId: (pointId: number, additive: boolean) => void;
}


export default function ModelMenu({ modelId, setNamespace, addOrSetTag, addOrSetPointId }: ModelMenuProps): JSX.Element {
    const [namespaces, setNamespaces] = React.useState<string[]>([]);
    const [tags, setTags] = React.useState<string[]>([]);
    const [showNamespaces, setShowNamespaces] = React.useState<boolean>(false);
    const [showTags, setShowTags] = React.useState<boolean>(false);


    React.useEffect(() => {
        getTagsInModel(modelId).then(tags => {
            setTags(tags.response);
        })
    }, []);

    React.useEffect(() => {
        getNamespaces(modelId).then(namespaces => {
            setNamespaces(namespaces.response);
        })
    }, []);
    
    return (
        <div className="ModelMenu">
            <div className="vertical-flex"
                onMouseEnter={() => setShowNamespaces(true)}
                onMouseLeave={() => setShowNamespaces(false)}
            >
                <h2>Namespaces</h2>
                <div className={"mm-namespace-list" + (showNamespaces ? "" : " hidden")}>
                    {namespaces.map((namespace, index) => {
                        return (
                            <button className="namespace-button" key={index}
                                onClick={e => setNamespace(namespace, e.shiftKey)}>
                                <h3>{namespace}</h3>
                            </button>
                        )
                    }
                    )}
                </div>
            </div>

            <div className="vertical-flex"
                onMouseEnter={() => setShowTags(true)}
                onMouseLeave={() => setShowTags(false)}
            >
                <h2>Tags</h2>
                <div className={"mm-tag-list" + (showTags ? "" : " hidden")}>
                    {tags.map((tag, index) => {
                        return (
                            <button className="tag-button" key={index}
                                onClick={e => addOrSetTag(tag, e.shiftKey)}>
                                <h3>{tag}</h3>
                            </button>
                        )
                    })
                    }
                </div>
            </div>

        </div>
    )

}