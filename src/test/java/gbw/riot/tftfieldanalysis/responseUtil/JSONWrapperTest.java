package gbw.riot.tftfieldanalysis.responseUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;

import static org.junit.jupiter.api.Assertions.*;

class JSONWrapperTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void error() {
    }

    @Test
    void success() {
    }

    @Test
    void custom() {
    }

    @Test
    void parseSimpleObject() {
    }

    @Test
    void parseObject() {
    }

    @Test
    void parseObjectArray() {
    }

    private final String unparsedValueArray = "["+
            "\"EUW1_6488658961\"," +
            "\"EUW1_6488629244\"," +
            "\"EUW1_6488528365\"," +
            "\"EUW1_6488499955\"," +
            "\"EUW1_6488051361\"," +
            "\"EUW1_6488032949\"," +
            "\"EUW1_6487866415\"," +
            "\"EUW1_6486666497\"," +
            "\"EUW1_6486561220\"," +
            "\"EUW1_6486526857\"," +
            "\"EUW1_6486405078\"," +
            "\"EUW1_6485666331\"," +
            "\"EUW1_6485193958\"," +
            "\"EUW1_6485186863\"," +
            "\"EUW1_6485168295\"," +
            "\"EUW1_6485156075\"," +
            "\"EUW1_6485139659\"," +
            "\"EUW1_6485118079\"," +
            "\"EUW1_6485069748\"," +
            "\"EUW1_6485042687\"" +
            "]";
    private final String[] expectedValueArray = new String[]{
            "EUW1_6488658961",
            "EUW1_6488629244",
            "EUW1_6488528365",
            "EUW1_6488499955",
            "EUW1_6488051361",
            "EUW1_6488032949",
            "EUW1_6487866415",
            "EUW1_6486666497",
            "EUW1_6486561220",
            "EUW1_6486526857",
            "EUW1_6486405078",
            "EUW1_6485666331",
            "EUW1_6485193958",
            "EUW1_6485186863",
            "EUW1_6485168295",
            "EUW1_6485156075",
            "EUW1_6485139659",
            "EUW1_6485118079",
            "EUW1_6485069748",
            "EUW1_6485042687"
    };

    @Test
    void parseValueArray() {
        System.out.println("________________________________Testing JSONWrapper.parseValueArray________________________________" );
        String[] parsedByWrapper = JSONWrapper.parseValueArray(unparsedValueArray);
        System.out.println("\t Length matches.");
        assertEquals(expectedValueArray.length,parsedByWrapper.length);
        System.out.println("\t Values equal.");
        for(int i = 0; i < parsedByWrapper.length; i++){
            assertEquals(expectedValueArray[i],parsedByWrapper[i]);
        }
    }

    @Test
    void getOr() {
    }

    @Test
    void testGetOr() {
    }

    @Test
    void get() {
    }

    @Test
    void getLike() {
    }
}