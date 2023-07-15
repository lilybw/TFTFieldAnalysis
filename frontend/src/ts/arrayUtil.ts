export const containsAny = <T>(arr1: T[], arr2: T[]): boolean => {
    return arr1.some(item => arr2.includes(item));
}

export function contains<T,R>(arr1: T[], obj: R, comparator: (element: T, obj: R) => boolean): boolean {
    return arr1.some(item => comparator(item, obj));
}

export const containsAll = <T>(arr1: T[], arr2: T[]): boolean => {
    return arr2.every(item => arr1.includes(item));
}

