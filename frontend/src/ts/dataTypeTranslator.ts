
export const toList = <T>(set: Set<T>): T[] => {
    const list: T[] = [];
    set.forEach((value) => list.push(value));
    return list;
};

export const toSet = <T>(list: T[]): Set<T> => {
    const set = new Set<T>();
    list.forEach((value) => set.add(value));
    return set;
}
