export type ModelMetaDataDTO = {
    modelId: number;
    matchIdsEvaluated: string[];
    dateSecondsTrainingMap: TrainingSession[];
    cachedValues: ModelMetaDataCacheDTO;
    pointsPerNamespace: Map<string, number>;
    pointsWithTagCount: Map<string, number>;
};
export type ModelMetaDataCacheDTO = {
    EDGE_COUNT: number,
    POINT_COUNT: number, 
    MAX_OCCURRENCE_VALUE: number,
    MIN_OCCURRENCE_VALUE: number
}

export type DataPointDTO = {
    id: number;
    namespace: string;
    tags: string[];
};

export type EdgeDTO = {
    pointA: number;
    pointB: number;
    occurrence: number;
};

export type ModelDTO = {
    metadata: ModelMetaDataDTO;
    namespaces: string[];
    pointIdEdgeSetMap: Map<number, Set<EdgeDTO>>;
    namespacePointMap: Map<string, Set<DataPointDTO>>;
};

export enum ServerTargets {
    EUROPE = "europe",
    AMERICAS = "america",
    ASIA = "asia",
    SEA = "sea",
    ERR_UNKNOWN = "unknown"
}

export type TrainingConfiguration = {
    maxMatchCount?: number;
    patch?: string;
    confineToBasePlayer?: boolean;
    serverTarget?: ServerTargets;
}

export type DetailedResponse<T> = {
    response: T;
    details: ResponseDetails;
};

export type ResponseDetails = {
    name: string;
    description: string;
    notes: string[];
};




export type TrainingSession = {
    date: Date;
    msTaken: number;
};