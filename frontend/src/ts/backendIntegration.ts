import { DataPointDTO, DetailedResponse, EdgeDTO, ModelDTO, ModelMetaDataDTO, TrainingConfiguration } from "./types";

const backendPort = 13498;
const backendIp = 'localhost';
const __api_v1  = `http://${backendIp}:${backendPort}/api/v1`;

async function onIntegrationError<T>(error: unknown): Promise<DetailedResponse<T>> {
    return { response: null as T, details: { name: 'Error', description: String(error), notes: [] } }
}

export async function getModel(id: number): Promise<DetailedResponse<ModelDTO>> {
    const response = await fetch(`${__api_v1}/model/${id}`, {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;
}

export async function getAllModelIds(): Promise<DetailedResponse<Set<number>>> {

    const response = await fetch(`${__api_v1}/model/all`, {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;

}

export async function createModel(): Promise<DetailedResponse<ModelDTO>> {

    const response = await fetch(`${__api_v1}/model/create`, { method: 'POST', mode: "cors" });
    const data = await response.json();
    return data;

}

export async function deleteModel(id: number): Promise<DetailedResponse<string>> {

    const response = await fetch(`${__api_v1}/model/${id}/delete`, { method: 'POST', mode: "cors" });
    const data = await response.json();
    return data;

}

export async function getPoints(
    id: number,
    namespace?: string,
    pointIds?: number[],
    tags?: string[]
): Promise<DetailedResponse<DataPointDTO[]>> {

    const url = new URL(`${__api_v1}/model/${id}/points`);
    if (namespace) url.searchParams.append('namespace', namespace);
    if (pointIds) url.searchParams.append('pointIds', pointIds.join(','));
    if (tags) url.searchParams.append('tags', tags.join(','));

    const response = await fetch(url.toString(), {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;
}

export async function getNamespaces(id: number): Promise<DetailedResponse<string[]>> {

    const response = await fetch(`${__api_v1}/model/${id}/namespaces`, {method: "GET", mode: "cors"});
    const data = await response.json();

    return data;

}

export async function getEdgeSets(id: number, points: number[]): 
    Promise<DetailedResponse<{[key: number]: EdgeDTO[]}>> {

    const url = new URL(`${__api_v1}/model/${id}/edges`);
    url.searchParams.append('points', points.join(','));

    const response = await fetch(url.toString(), {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;
}

export async function getModelMetadata(id: number): Promise<DetailedResponse<ModelMetaDataDTO>> {

    const response = await fetch(`${__api_v1}/model/${id}/metadata`,{method: "GET", mode: "cors"});
    const data = await response.json();
    return data;

}

export async function trainModel(id: number, config?: TrainingConfiguration, basePUUID?: string): Promise<DetailedResponse<number>> {

    const response = await fetch(`${__api_v1}/train/${id}?puuid=`+basePUUID, {
        method: 'POST',
        mode: "cors",
        body: JSON.stringify({ config, basePUUID }),
        headers: { 'Content-Type': 'application/json' }
    });
    const data = await response.json();
    return data;

}

export async function getTrainingServerTargets(): Promise<DetailedResponse<string[]>> {

    const response = await fetch(`${__api_v1}/train/serverTargets`, {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;

}

export async function getServerLocations(): Promise<DetailedResponse<string[]>> {
    const response = await fetch(`${__api_v1}/train/serverLocations`, {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;
}

export async function validateIGNandGetPUUID(ign: string, server: string): Promise<DetailedResponse<string>> {
    const response = await fetch(`${__api_v1}/train/validate/${ign}/server/${server}`, {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;
}

export async function getTagsInModel(id: number): Promise<DetailedResponse<string[]>> {
    const response = await fetch(`${__api_v1}/model/${id}/tags`, {method: "GET", mode: "cors"});
    const data = await response.json();
    return data;
}

export async function getBackendVersion(): Promise<DetailedResponse<string>> {

    const response = await fetch(`http://${backendIp}:${backendPort}/version`,
        {
            mode: "cors",
            method: "GET"
        }
    );
    const data = await response.json();
    return data;

}
