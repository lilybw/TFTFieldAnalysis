import { EdgeDTO, ModelMetaDataDTO } from "./types";

const toAbsolute = (point: {x: number, y: number}, center: {x: number, y: number}) => {
    return {
        x: point.x + center.x * .8,
        y: point.y + center.y * -.3
    };
};

const calcVarianceMod = (i: number, varianceSteps: number) => {
    const stepSize = 1 / varianceSteps;
    return i % varianceSteps * stepSize;
}

/**
 * Draws a line for each edge in the given array
 * from the center of the canvas to its end
 * @param edges EdgeDTOs to draw
 * @param canvas canvas to draw on
 * @returns the absolute coordinates of the end of each edge in relation to the center of the canvas
 */
export const drawEdges = async (edges: EdgeDTO[], canvas: HTMLCanvasElement | null, 
    ogPointId: number, edgeMinOccurrence: number)
    : Promise<{[key: number]: {x:number,y:number}}> => {

    if(canvas == null) return {};
    const ctx = canvas.getContext('2d');
    if(!ctx) return {};
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    edges = edges.filter(edge => edge.occurrence >= edgeMinOccurrence); //TODO: make this a post process step
    let localOccMax = 0;
    edges.forEach(edge => {
        if(edge.occurrence > localOccMax) localOccMax = edge.occurrence;
    });

    const absoluteStyles = window.getComputedStyle(canvas);

    const center = {x: canvas.width / 2, y: canvas.height / 2};
    const length = Math.min(canvas.width, canvas.height) / 2.5;

    const angleIncrement = 2 * Math.PI / edges.length;
    const angleOffset = .5 * Math.PI; // Making 0° be at the top of the circle

    ctx.strokeStyle = 'white';
    const maxLineWidth = 20;
    const varianceSteps = Math.floor(edges.length / 13);

    const toReturn: {[key: number]: {x:number,y:number}} = {};

    for(let i = 0; i < edges.length; i++) {
        const edge = edges[i];

        const varianceModifier = calcVarianceMod(i, varianceSteps);

        const end = {
            x: center.x + Math.cos(angleIncrement * i - angleOffset) * (length - varianceModifier * 200),
            y: center.y + Math.sin(angleIncrement * i - angleOffset) * (length - varianceModifier * 200)
        };
        
        if(edge.pointA == ogPointId) {
            toReturn[edge.pointB] = toAbsolute(end,center);
        }else{
            toReturn[edge.pointA] = toAbsolute(end,center);
        }

        ctx.lineWidth = maxLineWidth * (edge.occurrence / localOccMax);
        ctx.beginPath();
        ctx.moveTo(center.x, center.y);
        ctx.lineTo(end.x, end.y);
        ctx.stroke();
        ctx.closePath();
    }

    return toReturn;
}
