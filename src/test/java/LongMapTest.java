import de.comparus.opensource.longmap.LongMapImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongMapTest {

    @Test
    public void testLongMapPut() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        longMap.put(1, "test");
        assertEquals(1, longMap.size());
        assertEquals("test", longMap.get(1));
    }

    @Test
    public void testLongMapPutNullValue() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        longMap.put(0, null);
        assertEquals(0, longMap.size());
    }


    @Test
    public void testLongMapPutIdenticalKey() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        longMap.put(0, "test");
        longMap.put(0, "test1");

        assertEquals(1, longMap.size());
        assertEquals("test1", longMap.get(0));
    }

    @Test
    public void testLongMapGet() {
        LongMapImpl<String> longMap = new LongMapImpl<>();
        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        assertEquals("test29", longMap.get(29));
        assertEquals("test40", longMap.get(40));
        assertNull(longMap.get(60));
    }

    @Test
    public void testLongMapRemove() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }
        assertEquals("test0", longMap.remove(0));
        assertEquals("test40", longMap.remove(40));
        assertEquals(48, longMap.size());
        assertNull(longMap.remove(60));
        assertNull(longMap.remove(0));
    }

    @Test
    public void testLongMapIsEmpty() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 1; i++) {
            longMap.put(i, "test" + i);
        }

        assertFalse(longMap.isEmpty());
        longMap.remove(0);
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void testLongMapContainsKey() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        assertTrue(longMap.containsKey(5));
        longMap.remove(5);
        assertFalse(longMap.containsKey(5));
    }

    @Test
    public void testLongMapContainsValue() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        assertFalse(longMap.containsValue("test50"));
        assertTrue(longMap.containsValue("test23"));


        longMap.put(50, "test50");
        assertTrue(longMap.containsValue("test50"));
        longMap.remove(50);
        assertFalse(longMap.containsValue("test50"));
    }

    @Test
    public void testLongMapKeys() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        long[] keys = new long[50];
        for (int i = 0; i < 50; i++) {
            keys[i] = i;
        }

        assertArrayEquals(keys, longMap.keys());
    }

    @Test
    public void testLongMapValues() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        String[] testValue = new String[50];
        for (int i = 0; i < 50; i++) {
            testValue[i] = "test" + i;
        }

        assertArrayEquals(testValue, longMap.values());
    }

    @Test
    public void testLongMapSize() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        assertEquals(50, longMap.size());
        longMap.remove(15);
        assertEquals(49, longMap.size());
    }

    @Test
    public void testLongMapClear() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        longMap.clear();

        assertEquals(0, longMap.size());
        assertNull(longMap.get(15));

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }
        assertEquals(50, longMap.size());
    }

    @Test
    public void testPutSameKeyDifferentValue() {
        LongMapImpl<String> longMap = new LongMapImpl<>();

        for (int i = 0; i < 50; i++) {
            longMap.put(i, "test" + i);
        }

        String oldValue = longMap.put(5, "test555");
        assertEquals("test5", oldValue);

        String value = longMap.get(5);
        assertEquals("test555", value);

        longMap.clear();

        longMap.put(0, "test");
        longMap.put(0, "test1");

        assertEquals(1, longMap.size());


        boolean isPresent = longMap.containsValue("test");
        assertFalse(isPresent);
    }


}
