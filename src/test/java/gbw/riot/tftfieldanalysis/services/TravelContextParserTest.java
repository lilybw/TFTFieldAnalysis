package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.core.travel.BranchEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TravelContextParserTest {
    private final SecretsService notSoSecrets = new SecretsService();
    private final String end = notSoSecrets.getConfigurable("MTPD-Entry-End").value();
    private final String memSub = notSoSecrets.getConfigurable("MTPD-Entry-Member-Subdivision").value();
    private final String sub = notSoSecrets.getConfigurable("MTPD-Entry-Subdivision").value();
    private final TravelContextParser parser = new TravelContextParser(notSoSecrets);

    @BeforeEach
    void setUp() {
        System.out.println("_____________Testing: TravelContextParser_____________");
    }

    @AfterEach
    void tearDown() {
        System.out.println("_____________Testing: TravelContextParser Concluded_____________");
    }

    @Test
    void parseParam() {
        System.out.println("\t ---Testing: parseParam");
        System.out.println("\t\t ---static, expect no error");
        String shouldWorkParam1 = sub+sub+sub+" "+end;
        String shouldWorkParam2 = "1"+sub+" 1"+sub+"1"+sub+"1"+end;
        String shouldWorkParam3 = " 1"+sub+"1"+memSub+"1 "+sub+"1"+memSub+"1"+sub+" 1"+memSub+"1"+end;
        ValueErrorTuple<List<BranchEntry>, Exception> result1 = parser.parseParam(shouldWorkParam1);
        ValueErrorTuple<List<BranchEntry>, Exception> result2 = parser.parseParam(shouldWorkParam2);
        ValueErrorTuple<List<BranchEntry>, Exception> result3 = parser.parseParam(shouldWorkParam3);
        System.out.println("\t\t\t ---expecting no exceptions in tuples");
        assertNull(result1.error());
        assertNull(result2.error());
        assertNull(result3.error());
        assertFalse(result1.hasError());
        System.out.println("\t\t\t ---expecting parsed length to be correct");
        assertEquals(1,result1.value().size());
        assertEquals(1,result2.value().size());
        assertEquals(1,result3.value().size());
        System.out.println("\t\t ---dynamic, resizing params, expect no error");
        for(String param : List.of(shouldWorkParam1, shouldWorkParam2, shouldWorkParam3)){
            System.out.println("\t\t\t ---full test on pattern: " + param);
            for(int i = 1; i < 10; i++){
                StringBuilder sb = new StringBuilder();
                sb.append(String.valueOf(param).repeat(i));
                String combined = sb.toString();
                ValueErrorTuple<List<BranchEntry>, Exception> result = parser.parseParam(combined);
                assertNull(result.error());
                assertEquals(i,result1.value().size());
            }
        }
        System.out.println("\t\t ---Testing Invalid Patterns, expecting error");

        // New test cases with invalid patterns
        String invalidParam1 = "1"; // Missing sub and end delimiters
        String invalidParam2 = sub + sub + "1"; // Missing end delimiter
        String invalidParam3 = "1" + memSub + end; // Missing sub delimiter

        ValueErrorTuple<List<BranchEntry>, Exception> invalidResult1 = parser.parseParam(invalidParam1);
        ValueErrorTuple<List<BranchEntry>, Exception> invalidResult2 = parser.parseParam(invalidParam2);
        ValueErrorTuple<List<BranchEntry>, Exception> invalidResult3 = parser.parseParam(invalidParam3);

        // Expecting errors for these cases
        assertNotNull(invalidResult1.error());
        assertNotNull(invalidResult2.error());
        assertNotNull(invalidResult3.error());

        System.out.println("\t\t ---Expecting parsed length to be 0 for invalid cases");
        assertNull(invalidResult1.value());
        assertNull(invalidResult2.value());
        assertNull(invalidResult3.value());
    }
}