import { DataPointDTO, DetailedResponse, EdgeDTO, ModelDTO, ModelMetaDataDTO, TrainingConfiguration } from "./types";

const backendPort = 13498;
const backendIp = 'localhost';
const rootUrl = `https://${backendIp}:${backendPort}/api/v1`;

async function onIntegrationError<T>(error: unknown): Promise<DetailedResponse<T>> {
    return { response: null as T, details: { name: 'Error', description: String(error), notes: [] } }
}

export async function getModel(id: number): Promise<DetailedResponse<ModelDTO>> {
    try {
        const response = await fetch(`${rootUrl}/model/${id}`);
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function getAllModelIds(): Promise<DetailedResponse<Set<number>>> {
    try {
        const response = await fetch(`${rootUrl}/model/all`);
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function createModel(): Promise<DetailedResponse<ModelDTO>> {
    try {
        const response = await fetch(`${rootUrl}/model/create`, { method: 'POST' });
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function deleteModel(id: number): Promise<DetailedResponse<string>> {
    try {
        const response = await fetch(`${rootUrl}/model/${id}/delete`, { method: 'POST' });
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function getPoints(
    id: number,
    namespace?: string,
    pointIds?: number[],
    tags?: string[]
): Promise<DetailedResponse<Set<DataPointDTO>>> {
    try {
        const url = new URL(`${rootUrl}/model/${id}/points`);
        if (namespace) url.searchParams.append('namespace', namespace);
        if (pointIds) url.searchParams.append('pointIds', pointIds.join(','));
        if (tags) url.searchParams.append('tags', tags.join(','));

        const response = await fetch(url.toString());
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function getNamespaces(id: number): Promise<DetailedResponse<string[]>> {
    try {
        const response = await fetch(`${rootUrl}/model/${id}/namespaces`);
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function getEdgeSets(id: number, points: number[]): Promise<DetailedResponse<Map<number, Set<EdgeDTO>>>> {
    try {
        const url = new URL(`${rootUrl}/model/${id}/edges`);
        url.searchParams.append('points', points.join(','));

        const response = await fetch(url.toString());
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function getModelMetadata(id: number): Promise<DetailedResponse<ModelMetaDataDTO>> {
    try {
        const response = await fetch(`${rootUrl}/model/${id}/metadata`);
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}

export async function trainModel(id: number, config: TrainingConfiguration, basePUUID: string): Promise<DetailedResponse<number>> {
    try {
        const response = await fetch(`${rootUrl}/model/${id}/train`, {
            method: 'POST',
            body: JSON.stringify({ config, basePUUID }),
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await response.json();
        return data;
    } catch (error) {
        return Promise.reject(onIntegrationError(error));
    }
}
