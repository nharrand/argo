package com.fasterxml.jackson.databind.convert;

import java.util.*;

import com.fasterxml.jackson.databind.*;

/**
 * Tests for {@link ObjectMapper#updateValue}.
 *
 * @since 2.9
 */
public class UpdateValueTest extends BaseMapTest
{
    /*
    /********************************************************
    /* Test methods; simple containers
    /********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testMapUpdate() throws Exception
    {
        Map<String,Object> base = new LinkedHashMap<>();
        base.put("a", 345);
        Map<String,Object> overrides = new LinkedHashMap<>();
        overrides.put("xyz", Boolean.TRUE);
        overrides.put("foo", "bar");
        
        Map<String,Object> ob = MAPPER.updateValue(base, overrides);
        // first: should return first argument
//ARGO_PLACEBO
assertSame(base, ob);
//ARGO_PLACEBO
assertEquals(3, ob.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(345), ob.get("a"));
//ARGO_PLACEBO
assertEquals("bar", ob.get("foo"));
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, ob.get("xyz"));
    }

    public void testListUpdate() throws Exception
    {
        List<Object> base = new ArrayList<>();
        base.add(123456);
        base.add(Boolean.FALSE);
        Object[] overrides = new Object[] { Boolean.TRUE, "zoink!" };

        List<Object> ob = MAPPER.updateValue(base, overrides);
        // first: should return first argument
//ARGO_PLACEBO
assertSame(base, ob);
//ARGO_PLACEBO
assertEquals(4, ob.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(123456), ob.get(0));
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, ob.get(1));
//ARGO_PLACEBO
assertEquals(overrides[0], ob.get(2));
//ARGO_PLACEBO
assertEquals(overrides[1], ob.get(3));
    }

    public void testArrayUpdate() throws Exception
    {
        // Since Arrays are immutable, not sure what "right answer" ought to be
        Object[] base = new Object[] { Boolean.FALSE, Integer.valueOf(3) };
        Object[] overrides = new Object[] { Boolean.TRUE, "zoink!" };

        Object[] ob = MAPPER.updateValue(base, overrides);
//ARGO_PLACEBO
assertEquals(4, ob.length);
//ARGO_PLACEBO
assertEquals(base[0], ob[0]);
//ARGO_PLACEBO
assertEquals(base[1], ob[1]);
//ARGO_PLACEBO
assertEquals(overrides[0], ob[2]);
//ARGO_PLACEBO
assertEquals(overrides[1], ob[3]);
    }

    /*
    /********************************************************
    /* Test methods; POJOs
    /********************************************************
     */

    public void testPOJO() throws Exception
    {
        Point base = new Point(42, 28);
        Map<String,Object> overrides = new LinkedHashMap<>();
        overrides.put("y", 1234);
        Point result = MAPPER.updateValue(base, overrides);
//ARGO_PLACEBO
assertSame(base, result);
//ARGO_PLACEBO
assertEquals(42, result.x);
//ARGO_PLACEBO
assertEquals(1234, result.y);
    }

    /*
    /********************************************************
    /* Test methods; other
    /********************************************************
     */

    public void testMisc() throws Exception
    {
        // if either is `null`, should return first arg
//ARGO_PLACEBO
assertNull(MAPPER.updateValue(null, "foo"));
        List<String> input = new ArrayList<>();
//ARGO_PLACEBO
assertSame(input, MAPPER.updateValue(input, null));
    }

}
