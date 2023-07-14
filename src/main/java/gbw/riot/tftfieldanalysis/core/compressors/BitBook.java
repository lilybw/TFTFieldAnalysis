package gbw.riot.tftfieldanalysis.core.compressors;

public class BitBook<T> extends Dictionary<T> {
    //A dictionary, but instead of int to string through a map - well, still uses a map
    //it generates bit-sets (seen as int[]) where each position of each bit in a bit set
    //maps to a string in the map.
    //This type of compression is weaker than a standard dictionary, but makes comparisons
    //between bit-sets of the same BitBooks near-instant.

    private Dictionary<T> dictionary;

    //Each page can map to 64 different values of the BitBook
    private long[] pages;

    public BitBook(T onUntranslatable){
        super(onUntranslatable);
    }




}
