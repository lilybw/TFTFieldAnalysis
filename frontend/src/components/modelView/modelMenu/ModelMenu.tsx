import React from 'react';
import './ModelMenu.css';
import { getNamespaces, getTagsInModel } from '../../../ts/backendIntegration';

interface ModelMenuProps{
    modelId: number;
    setNamespace: (namespace: string, additive: boolean) => void;
    addOrSetTag: (tag: string, additive: boolean) => void;
    namespaces: string[];
}


export default function ModelMenu({ modelId, setNamespace, addOrSetTag, namespaces }: ModelMenuProps): JSX.Element {
    const [sortedNamespaces, setSortedNamespaces] = React.useState<string[]>(namespaces);
    const [tags, setTags] = React.useState<string[]>([]);
    const [sortedTags, setSortedTags] = React.useState<string[]>([]);
    const [showNamespaces, setShowNamespaces] = React.useState<boolean>(false);
    const [showTags, setShowTags] = React.useState<boolean>(false);


    React.useEffect(() => {
        getTagsInModel(modelId).then(tags => {
            setTags(tags.data);
            setSortedTags(tags.data);
        })
    }, []);
    
    return (
        <div className="ModelMenu">
            
            <h2>Point Selection Categories</h2>
            <div className="vertical-flex"
                onMouseEnter={() => setShowNamespaces(true)}
                onMouseLeave={() => {
                    setShowNamespaces(false);
                    setSortedNamespaces(namespaces);
                }}
            >
                <h3 style={{ color: "var(--blue-2)" }}>Namespaces..</h3>
                
                <div className={"mm-namespace-list" + (showNamespaces ? "" : " hidden")}>
                    <input type="text" placeholder="Search" className="namespace-search"
                        onChange={e => {
                            const search = e.target.value;
                            setSortedNamespaces(namespaces.filter(namespace => namespace.includes(search)));
                        }}
                    />
                    {sortedNamespaces.map((namespace, index) => {
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
                onMouseLeave={() => {
                    setShowTags(false);
                    setSortedTags(tags);
                }}
            >
                <h3 style={{color: "var(--blue-2)"}}>Tags..</h3>
                <div className={"mm-tag-list" + (showTags ? "" : " hidden")}>
                    <input type="text" placeholder="Search" className="namespace-search"
                        onChange={e => {
                            const search = e.target.value;
                            setSortedTags(tags.filter(tag => tag.includes(search)));
                        }}
                    />
                    {sortedTags.map((tag, index) => {
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