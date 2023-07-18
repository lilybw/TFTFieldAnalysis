import { ComponentTransform, DrawCallProperties, EdgeDTO, ModelMetaDataDTO } from "./types";

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
export const drawEdges = async (ogPointId: number, properties: DrawCallProperties
    ): Promise<{[key: number]: {x:number,y:number}}> => {

    if (!properties.canvasRef) return {};
    const ctx = properties.canvasRef.getContext('2d');
    if(!ctx) return {};
    ctx.clearRect(0, 0, properties.canvasRef.width, properties.canvasRef.height);

    const center = { 
        x: properties.transform.x + (properties.transform.w / 2), 
        y: properties.transform.y + (properties.transform.h / 2)
    };
    const length = Math.min(properties.transform.w, properties.transform.h) / 2.5;

    const angleIncrement = 2 * Math.PI / properties.processedEdges.length;
    const angleOffset = .5 * Math.PI; // Making 0° be at the top of the circle

    ctx.strokeStyle = 'white';
    const varianceSteps = Math.floor(properties.processedEdges.length / 13);

    const toReturn: {[key: number]: {x:number,y:number}} = {};

    for (let i = 0; i < properties.processedEdges.length; i++) {
        const edge = properties.processedEdges[i];

        const varianceModifier = calcVarianceMod(i, varianceSteps);

        const end = {
            x: center.x + Math.cos(angleIncrement * i - angleOffset) * (length - varianceModifier * properties.transform.w),
            y: center.y + Math.sin(angleIncrement * i - angleOffset) * (length - varianceModifier * properties.transform.h)
        };
        
        if(edge.pointA == ogPointId) {
            toReturn[edge.pointB] = end;
        }else{
            toReturn[edge.pointA] = end;
        }

        ctx.lineWidth = properties.maxLineWidth * (edge.occurrence / properties.localOccMax);
        ctx.beginPath();
        ctx.moveTo(center.x, center.y);
        ctx.lineTo(end.x, end.y);
        ctx.stroke();
        ctx.closePath();
    }

    return toReturn;
}
