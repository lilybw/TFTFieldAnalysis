export const containsAny = <T>(arr1: T[], arr2: T[]): boolean => {
    return arr1.some(item => arr2.includes(item));
}

export function contains<T,R>(arr1: T[], obj: R, comparator: (element: T, obj: R) => boolean): boolean {
    return arr1.some(item => comparator(item, obj));
}

export const containsAll = <T>(arr1: T[], arr2: T[]): boolean => {
    return arr2.every(item => arr1.includes(item));
}

export const notInPlaceAdd = <T>(arr: T[], item: T): T[] => {
    return [...arr, item];
}

export const notInPlaceRemoveAll = <T>(arr: T[], item: T): T[] => {
    return arr.filter(element => element !== item);
}

export const notInPlaceRemoveFirst = <T>(arr: T[], item: T): T[] => {
    const index = arr.indexOf(item);
    if(index === -1) return arr;
    return [...arr.slice(0, index), ...arr.slice(index + 1)];
}

