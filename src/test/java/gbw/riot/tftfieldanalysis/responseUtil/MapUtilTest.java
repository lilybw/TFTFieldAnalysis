package gbw.riot.tftfieldanalysis.responseUtil;

import gbw.riot.tftfieldanalysis.core.DataPoint;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilTest {

    @BeforeEach
    void setUp() {
        System.out.println("_____________Testing: MapUtil_____________");
    }

    @AfterEach
    void tearDown() {
        System.out.println("_____________Testing: MapUtil Concluded_____________");
    }

    @Test
    public void testSetToList() {
        System.out.println("\t ---Testing: setToList");
        // Test cases
        Map<String, Set<DataPoint>> inputMap = new HashMap<>();
        inputMap.put("A", new HashSet<>(List.of(new DataPoint(1, Set.of(1)), new DataPoint(2, Set.of(2)), new DataPoint(3, Set.of(3)))));
        inputMap.put("B", new HashSet<>(List.of(new DataPoint(4, Set.of(4)), new DataPoint(5, Set.of(5)))));
        inputMap.put("C", new HashSet<>(List.of(new DataPoint(6, Set.of(6)), new DataPoint(7, Set.of(7)), new DataPoint(8, Set.of(8)))));

        // Call the method being tested
        Map<String, List<DataPoint>> result = MapUtil.Values.setToList(inputMap);
        System.out.println("\t\t\t ---expecting keysets to equal");
        Set<String> keySetOriginal = inputMap.keySet();
        Set<String> keySetResult = result.keySet();

        assertEquals(keySetOriginal.size(),keySetResult.size());
        assertTrue(keySetResult.containsAll(keySetOriginal));
        assertTrue(keySetOriginal.containsAll(keySetResult));
        for(String key : keySetOriginal){
            assertNotNull(result.get(key));
        }

        System.out.println("\t\t\t ---expecting value sets to equal");
        for(String key : keySetOriginal){
            assertTrue(result.get(key).containsAll(inputMap.get(key)));
            assertTrue(inputMap.get(key).containsAll(result.get(key)));
        }

    }
}