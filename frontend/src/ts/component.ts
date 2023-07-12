export interface Backupable {
    backup?: JSX.Element;
}

export interface Viewer {
    goView: (modelId: number) => void;
}