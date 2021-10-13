package org.json.junit;

/*
Copyright (c) 2020 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import static org.json.junit.Util.equivalentModuloEpsilon;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointerException;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;


/**
 * Tests for JSON-Java JSONArray.java
 */
public class JSONArrayTest {
    private final String arrayStr = 
            "["+
                "true,"+
                "false,"+
                "\"true\","+
                "\"false\","+
                "\"hello\","+
                "23.45e-4,"+
                "\"23.45\","+
                "42,"+
                "\"43\","+
                "["+
                    "\"world\""+
                "],"+
                "{"+
                    "\"key1\":\"value1\","+
                    "\"key2\":\"value2\","+
                    "\"key3\":\"value3\","+
                    "\"key4\":\"value4\""+
                "},"+
                "0,"+
                "\"-1\""+
            "]";

    /**
     * Tests that the similar method is working as expected.
     */
    @Test //TEST_SELECTED
    public void verifySimilar() {
        final String string1 = "HasSameRef";
        JSONArray obj1 = new JSONArray()
                .put("abc")
                .put(string1)
                .put(2);
        
        JSONArray obj2 = new JSONArray()
                .put("abc")
                .put(string1)
                .put(3);

        JSONArray obj3 = new JSONArray()
                .put("abc")
                .put(new String(string1))
                .put(2);

        //ARGO_ORIGINAL
        assertFalse("Should eval to false", obj1.similar(obj2));

        //ARGO_ORIGINAL
        assertTrue("Should eval to true", obj1.similar(obj3));
    }
        
    /**
     * Attempt to create a JSONArray with a null string.
     * Expects a NullPointerException.
     */
    @Test(expected=Exception.class) //TEST_PLACEBO
    public void nullException() {
        String str = null;
        //ARGO_PLACEBO
        assertNull("Should throw an exception", new JSONArray(str));
    }

    /**
     * Attempt to create a JSONArray with an empty string.
     * Expects a JSONException.
     */
    @Test //TEST_MODIFIED
    public void emptStr() {
        String str = "";
        try {
            //ARGO_ORIGINAL
            assertNull("Should throw an exception", new JSONArray(str));
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
            /*assertEquals("Expected an exception message",
                    "A JSONArray text must start with '[' at 0 [character 1 line 1]",
                    e.getMessage());*/
        }
    }
    
    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test //TEST_REMOVED
    public void unclosedArray() {
        try {
            //ARGO_ERROR_HANDLING
            //assertNull("Should throw an exception", new JSONArray("["));
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
            /*assertEquals("Expected an exception message",
                    "Expected a ',' or ']' at 1 [character 2 line 1]",
                    e.getMessage());*/
        }
    }
    
    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test //TEST_REMOVED
    public void unclosedArray2() {
        try {
	        //ARGO_ERROR_HANDLING
            //assertNull("Should throw an exception", new JSONArray("[\"test\""));
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
            /*assertEquals("Expected an exception message",
                    "Expected a ',' or ']' at 7 [character 8 line 1]",
                    e.getMessage());*/
        }
    }
    
    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test //TEST_REMOVED
    public void unclosedArray3() {
        try {
            //ARGO_ERROR_HANDLING
            //assertNull("Should throw an exception", new JSONArray("[\"test\","));
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
            /*assertEquals("Expected an exception message",
                    "Expected a ',' or ']' at 8 [character 9 line 1]",
                    e.getMessage());*/
        }
    }

    /**
     * Attempt to create a JSONArray with a string as object that is
     * not a JSON array doc.
     * Expects a JSONException.
     */
    @Test //TEST_MODIFIED
    public void badObject() {
        String str = "abc";
        try {
            //ARGO_ORIGINAL
            assertNull("Should throw an exception", new JSONArray((Object)str));
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
            /*assertTrue("Expected an exception message",
                    "JSONArray initial value should be a string or collection or array.".
                    equals(e.getMessage()));*/
        }
    }
    
    /**
     * Verifies that the constructor has backwards compatibility with RAW types pre-java5.
     */
    @Test //TEST_SELECTED
    public void verifyConstructor() {
        
        final JSONArray expected = new JSONArray("[10]");
        
        @SuppressWarnings("rawtypes")
        Collection myRawC = Collections.singleton(Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray(myRawC);

        Collection<Integer> myCInt = Collections.singleton(Integer.valueOf(10));
        JSONArray jaInt = new JSONArray(myCInt);

        Collection<Object> myCObj = Collections.singleton((Object) Integer
                .valueOf(10));
        JSONArray jaObj = new JSONArray(myCObj);

        //ARGO_ORIGINAL
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        //ARGO_ORIGINAL
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaInt));
        //ARGO_ORIGINAL
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObj));
    }

    /**
     * Tests consecutive calls to putAll with array and collection.
     */
    @Test //TEST_SELECTED
    public void verifyPutAll() {
        final JSONArray jsonArray = new JSONArray();

        // array
        int[] myInts = { 1, 2, 3, 4, 5 };
        jsonArray.putAll(myInts);

        //ARGO_ORIGINAL
        assertEquals("int arrays lengths should be equal",
                     jsonArray.length(),
                     myInts.length);

        for (int i = 0; i < myInts.length; i++) {
            //ARGO_ORIGINAL
            assertEquals("int arrays elements should be equal",
                         myInts[i],
                         jsonArray.getInt(i));
        }

        // collection
        List<String> myList = Arrays.asList("one", "two", "three", "four", "five");
        jsonArray.putAll(myList);

        int len = myInts.length + myList.size();

        //ARGO_ORIGINAL
        assertEquals("arrays lengths should be equal",
                     jsonArray.length(),
                     len);

        for (int i = 0; i < myList.size(); i++) {
            //ARGO_ORIGINAL
            assertEquals("collection elements should be equal",
                         myList.get(i),
                         jsonArray.getString(myInts.length + i));
        }
    }

    /**
     * Verifies that the put Collection has backwards compatibility with RAW types pre-java5.
     */
    /*@Test //TEST_NOT_COVERED
    public void verifyPutCollection() {
        
        final JSONArray expected = new JSONArray("[[10]]");

        @SuppressWarnings("rawtypes")
        Collection myRawC = Collections.singleton(Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray();
        jaRaw.put(myRawC);

        Collection<Object> myCObj = Collections.singleton((Object) Integer
                .valueOf(10));
        JSONArray jaObj = new JSONArray();
        jaObj.put(myCObj);

        Collection<Integer> myCInt = Collections.singleton(Integer.valueOf(10));
        JSONArray jaInt = new JSONArray();
        jaInt.put(myCInt);

        //ARGO_NON_COVERED_FEATURE
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        //ARGO_NON_COVERED_FEATURE
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObj));
        //ARGO_NON_COVERED_FEATURE
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaInt));
    }*/

    
    /**
     * Verifies that the put Map has backwards compatibility with RAW types pre-java5.
     */
    @Test //TEST_SELECTED
    public void verifyPutMap() {
        
        final JSONArray expected = new JSONArray("[{\"myKey\":10}]");

        @SuppressWarnings("rawtypes")
        Map myRawC = Collections.singletonMap("myKey", Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray();
        jaRaw.put(myRawC);

        Map<String, Object> myCStrObj = Collections.singletonMap("myKey",
                (Object) Integer.valueOf(10));
        JSONArray jaStrObj = new JSONArray();
        jaStrObj.put(myCStrObj);

        Map<String, Integer> myCStrInt = Collections.singletonMap("myKey",
                Integer.valueOf(10));
        JSONArray jaStrInt = new JSONArray();
        jaStrInt.put(myCStrInt);

        Map<?, ?> myCObjObj = Collections.singletonMap((Object) "myKey",
                (Object) Integer.valueOf(10));
        JSONArray jaObjObj = new JSONArray();
        jaObjObj.put(myCObjObj);

        //ARGO_ORIGINAL
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaRaw));
        //ARGO_ORIGINAL
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrObj));
        //ARGO_ORIGINAL
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaStrInt));
        //ARGO_ORIGINAL
        assertTrue(
                "The RAW Collection should give me the same as the Typed Collection",
                expected.similar(jaObjObj));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the get[type]() API methods
     */
    @SuppressWarnings("boxing")
    @Test //TEST_SELECTED
    public void getArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        // booleans
        //ARGO_ORIGINAL
        assertTrue("Array true",
                true == jsonArray.getBoolean(0));
        //ARGO_ORIGINAL
        assertTrue("Array false",
                false == jsonArray.getBoolean(1));
        //ARGO_ORIGINAL
        assertTrue("Array string true",
                true == jsonArray.getBoolean(2));
        //ARGO_ORIGINAL
        assertTrue("Array string false",
                false == jsonArray.getBoolean(3));
        //ARGO_ORIGINAL
        // strings
        //ARGO_ORIGINAL
        assertTrue("Array value string",
                "hello".equals(jsonArray.getString(4)));
        // doubles
        //ARGO_ORIGINAL
        assertTrue("Array double",
                equivalentModuloEpsilon(new Double(23.45e-4), jsonArray.getDouble(5)));
        //ARGO_ORIGINAL
        assertTrue("Array string double",
                new Double(23.45).equals(jsonArray.getDouble(6)));
        // ints
        //ARGO_ORIGINAL
        assertTrue("Array value int",
                new Integer(42).equals(jsonArray.getInt(7)));
        //ARGO_ORIGINAL
        assertTrue("Array value string int",
                new Integer(43).equals(jsonArray.getInt(8)));
        // nested objects
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        //ARGO_ORIGINAL
        assertTrue("Array value JSONArray", nestedJsonArray != null);
        JSONObject nestedJsonObject = jsonArray.getJSONObject(10);
        //ARGO_ORIGINAL
        assertTrue("Array value JSONObject", nestedJsonObject != null);
        // longs
        //ARGO_ORIGINAL
        assertTrue("Array value long",
                new Long(0).equals(jsonArray.getLong(11)));
        //ARGO_ORIGINAL
        assertTrue("Array value string long",
                new Long(-1).equals(jsonArray.getLong(12)));

        //ARGO_ORIGINAL
        assertTrue("Array value null", jsonArray.isNull(-1));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that attempting to get the wrong types via the get[type]()
     * API methods result in JSONExceptions
     */
    @Test //TEST_MODIFIED
    public void failedGetArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        try {
            jsonArray.getBoolean(4);
            //ARGO_ORIGINAL
            assertTrue("expected getBoolean to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[4] is not a boolean.",e.getMessage());
        }
        try {
            jsonArray.get(-1);
            //ARGO_ORIGINAL
            assertTrue("expected get to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[-1] not found.",e.getMessage());
        }
        try {
            jsonArray.getDouble(4);
            //ARGO_ORIGINAL
            assertTrue("expected getDouble to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[4] is not a double.",e.getMessage());
        }
        try {
            jsonArray.getInt(4);
            //ARGO_ORIGINAL
            assertTrue("expected getInt to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[4] is not a int.",e.getMessage());
        }
        try {
            jsonArray.getJSONArray(4);
            //ARGO_ORIGINAL
            assertTrue("expected getJSONArray to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[4] is not a JSONArray.",e.getMessage());
        }
        try {
            jsonArray.getJSONObject(4);
            //ARGO_ORIGINAL
            assertTrue("expected getJSONObject to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[4] is not a JSONObject.",e.getMessage());
        }
        try {
            jsonArray.getLong(4);
            //ARGO_ORIGINAL
            assertTrue("expected getLong to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[4] is not a long.",e.getMessage());
        }
        try {
            jsonArray.getString(5);
            //ARGO_ORIGINAL
            assertTrue("expected getString to fail", false);
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
//            assertEquals("Expected an exception message",
//                    "JSONArray[5] is not a String.",e.getMessage());
        }
    }

    /**
     * Exercise JSONArray.join() by converting a JSONArray into a 
     * comma-separated string. Since this is very nearly a JSON document,
     * array braces are added to the beginning and end prior to validation.
     */
    @Test //TEST_MODIFIED
    public void join() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        String joinStr = jsonArray.join(",");

        // validate JSON
        /**
         * Don't need to remake the JSONArray to perform the parsing
         */
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse("["+joinStr+"]");
        //ARGO_ORIGINAL
        assertTrue("expected 13 items in top level object", ((List<?>)(JsonPath.read(doc, "$"))).size() == 13);
        //ARGO_ORIGINAL
        assertTrue("expected true", Boolean.TRUE.equals(jsonArray.query("/0")));
        //ARGO_ORIGINAL
        assertTrue("expected false", Boolean.FALSE.equals(jsonArray.query("/1")));
        //ARGO_ORIGINAL
        assertTrue("expected \"true\"", "true".equals(jsonArray.query("/2")));
        //ARGO_ORIGINAL
        assertTrue("expected \"false\"", "false".equals(jsonArray.query("/3")));
        //ARGO_ORIGINAL
        assertTrue("expected hello", "hello".equals(jsonArray.query("/4")));
        //ARGO_EQUIVALENT
        assertTrue("expected 0.002345",  equivalentModuloEpsilon(BigDecimal.valueOf(0.002345),jsonArray.query("/5")));
        //ARGO_ORIGINAL
        assertTrue("expected \"23.45\"", "23.45".equals(jsonArray.query("/6")));
        //ARGO_EQUIVALENT
        assertTrue("expected 42", Integer.valueOf(42).toString().equals(jsonArray.query("/7").toString()));
        //ARGO_ORIGINAL
        assertTrue("expected \"43\"", "43".equals(jsonArray.query("/8")));
        //ARGO_CAST
        //assertTrue("expected 1 item in [9]", ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 1);
        //ARGO_ORIGINAL
        assertTrue("expected world", "world".equals(jsonArray.query("/9/0")));
        //ARGO_CAST
        //assertTrue("expected 4 items in [10]", ((Map<?,?>)(JsonPath.read(doc, "$[10]"))).size() == 4);
        //ARGO_ORIGINAL
        assertTrue("expected value1", "value1".equals(jsonArray.query("/10/key1")));
        //ARGO_ORIGINAL
        assertTrue("expected value2", "value2".equals(jsonArray.query("/10/key2")));
        //ARGO_ORIGINAL
        assertTrue("expected value3", "value3".equals(jsonArray.query("/10/key3")));
        //ARGO_ORIGINAL
        assertTrue("expected value4", "value4".equals(jsonArray.query("/10/key4")));
        //ARGO_EQUIVALENT
        assertTrue("expected 0", Integer.valueOf(0).toString().equals(jsonArray.query("/11").toString()));
        //ARGO_ORIGINAL
        assertTrue("expected \"-1\"", "-1".equals(jsonArray.query("/12")));
    }

    /**
     * Confirm the JSONArray.length() method
     */
    @Test  //TEST_SELECTED
    public void length() {
        //ARGO_ORIGINAL
        assertTrue("expected empty JSONArray length 0",
                new JSONArray().length() == 0);
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        //ARGO_ORIGINAL
        assertTrue("expected JSONArray length 13. instead found "+jsonArray.length(), jsonArray.length() == 13);
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        //ARGO_ORIGINAL
        assertTrue("expected JSONArray length 1", nestedJsonArray.length() == 1);
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the opt[type](index)
     * and opt[type](index, default) API methods.
     */
    @SuppressWarnings("boxing")
    @Test  //TEST_MODIFIED
    public void opt() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        //ARGO_ORIGINAL
        assertTrue("Array opt value true",
                Boolean.TRUE == jsonArray.opt(0));
        assertTrue("Array opt value out of range",
                null == jsonArray.opt(-1));
        //ARGO_ORIGINAL

        assertTrue("Array opt value out of range",
                null == jsonArray.opt(jsonArray.length()));

        //ARGO_ORIGINAL
         assertTrue("Array opt boolean",
                Boolean.TRUE == jsonArray.optBoolean(0));
         //ARGO_NON_COVERED_FEATURE
        /*assertTrue("Array opt boolean default",
                Boolean.FALSE == jsonArray.optBoolean(-1, Boolean.FALSE));*/
        //ARGO_ORIGINAL
        assertTrue("Array opt boolean implicit default",
                Boolean.FALSE == jsonArray.optBoolean(-1));

        /*//ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt double",
                new Double(23.45e-4).equals(jsonArray.optDouble(5)));
        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt double default",
                new Double(1).equals(jsonArray.optDouble(0, 1)));
        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt double default implicit",
           new Double(jsonArray.optDouble(99)).isNaN());

        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt float",
                new Float(23.45e-4).equals(jsonArray.optFloat(5)));
        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt float default",
                new Float(1).equals(jsonArray.optFloat(0, 1)));
        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt float default implicit",
           new Float(jsonArray.optFloat(99)).isNaN());

        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt Number",
                BigDecimal.valueOf(23.45e-4).equals(jsonArray.optNumber(5)));
        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt Number default",
                new Double(1).equals(jsonArray.optNumber(0, 1d)));
        //ARGO_NON_COVERED_FEATURE
        assertTrue("Array opt Number default implicit",
           new Double(jsonArray.optNumber(99,Double.NaN).doubleValue()).isNaN());*/

        //ARGO_ORIGINAL
        assertTrue("Array opt int",
                new Integer(42).equals(jsonArray.optInt(7)));
        //ARGO_ORIGINAL
        assertTrue("Array opt int default",
                new Integer(-1).equals(jsonArray.optInt(0, -1)));
        //ARGO_ORIGINAL
        assertTrue("Array opt int default implicit",
                0 == jsonArray.optInt(0));

        JSONArray nestedJsonArray = jsonArray.optJSONArray(9);
        //ARGO_ORIGINAL
        assertTrue("Array opt JSONArray", nestedJsonArray != null);
        //ARGO_ORIGINAL
        assertTrue("Array opt JSONArray default", 
                null == jsonArray.optJSONArray(99));

        JSONObject nestedJsonObject = jsonArray.optJSONObject(10);
        //ARGO_ORIGINAL
        assertTrue("Array opt JSONObject", nestedJsonObject != null);
        //ARGO_ORIGINAL
        assertTrue("Array opt JSONObject default", 
                null == jsonArray.optJSONObject(99));

        //ARGO_ORIGINAL
        assertTrue("Array opt long",
                0 == jsonArray.optLong(11));
        //ARGO_ORIGINAL
        assertTrue("Array opt long default",
                -2 == jsonArray.optLong(-1, -2));
        //ARGO_ORIGINAL
        assertTrue("Array opt long default implicit",
                0 == jsonArray.optLong(-1));

        //ARGO_ORIGINAL
        assertTrue("Array opt string",
                "hello".equals(jsonArray.optString(4)));
        //ARGO_ORIGINAL
        assertTrue("Array opt string default implicit",
                "".equals(jsonArray.optString(-1)));
    }
    
    /**
     * Verifies that the opt methods properly convert string values.
     */
    @Test //TEST_MODIFIED
    public void optStringConversion(){
        JSONArray ja = new JSONArray("[\"123\",\"true\",\"false\"]");
        //ARGO_NON_COVERED_FEATURE
        //assertTrue("unexpected optBoolean value",ja.optBoolean(1,false)==true);
        //ARGO_NON_COVERED_FEATURE
        //assertTrue("unexpected optBoolean value",ja.optBoolean(2,true)==false);
        //ARGO_ORIGINAL
        assertTrue("unexpected optInt value",ja.optInt(0,0)==123);
        //ARGO_ORIGINAL
        assertTrue("unexpected optLong value",ja.optLong(0,0)==123);
        //ARGO_NON_COVERED_FEATURE
        //assertTrue("unexpected optDouble value",ja.optDouble(0,0.0)==123.0);
        //ARGO_NON_COVERED_FEATURE
        //assertTrue("unexpected optBigInteger value",ja.optBigInteger(0,BigInteger.ZERO).compareTo(new BigInteger("123"))==0);
        //ARGO_NON_COVERED_FEATURE
        //assertTrue("unexpected optBigDecimal value",ja.optBigDecimal(0,BigDecimal.ZERO).compareTo(new BigDecimal("123"))==0);
    }

    /**
     * Exercise the JSONArray.put(value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test //TEST_MODIFIED
    public void put() {
        JSONArray jsonArray = new JSONArray();

        // index 0
        jsonArray.put(true);
        // 1
        jsonArray.put(false);

	    //ARGO_SILENT
        String jsonArrayStr =
            "["+
                "\"hello\","+
                "\"world\""+
            "]";
        // 2
        jsonArray.put(new JSONArray(jsonArrayStr));

        // 3
        jsonArray.put(2.5);
        // 4
        jsonArray.put(1);
        // 5
        jsonArray.put(45L);

        // 6
        jsonArray.put("objectPut");

        String jsonObjectStr = 
            "{"+
                "\"key10\":\"val10\","+
                "\"key20\":\"val20\","+
                "\"key30\":\"val30\""+
            "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        // 7
        jsonArray.put(jsonObject);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k1", "v1");
        // 8
        jsonArray.put(map);

        Collection<Object> collection = new ArrayList<Object>();
        collection.add(1);
        collection.add(2);
        // 9
        jsonArray.put(collection);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        //ARGO_ORIGINAL
        assertTrue("expected 10 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 10);
        //ARGO_ORIGINAL
        assertTrue("expected true", Boolean.TRUE.equals(jsonArray.query("/0")));
        //ARGO_ORIGINAL
        assertTrue("expected false", Boolean.FALSE.equals(jsonArray.query("/1")));
        //ARGO_ORIGINAL
        assertTrue("expected 2 items in [2]", ((List<?>)(JsonPath.read(doc, "$[2]"))).size() == 2);
        //ARGO_ORIGINAL
        assertTrue("expected hello", "hello".equals(jsonArray.query("/2/0")));
        //ARGO_ORIGINAL
        assertTrue("expected world", "world".equals(jsonArray.query("/2/1")));
        //ARGO_EQUIVALENT
        assertTrue("expected 2.5",  equivalentModuloEpsilon(Double.valueOf(2.5), jsonArray.query("/3")));
        //ARGO_ORIGINAL
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/4")));
        //ARGO_ORIGINAL
        assertTrue("expected 45", Long.valueOf(45).equals(jsonArray.query("/5")));
        //ARGO_ORIGINAL
        assertTrue("expected objectPut", "objectPut".equals(jsonArray.query("/6")));
        //ARGO_ORIGINAL
        assertTrue("expected 3 items in [7]", ((Map<?,?>)(JsonPath.read(doc, "$[7]"))).size() == 3);
        //ARGO_ORIGINAL
        assertTrue("expected val10", "val10".equals(jsonArray.query("/7/key10")));
        //ARGO_ORIGINAL
        assertTrue("expected val20", "val20".equals(jsonArray.query("/7/key20")));
        //ARGO_ORIGINAL
        assertTrue("expected val30", "val30".equals(jsonArray.query("/7/key30")));
        //ARGO_ORIGINAL
        assertTrue("expected 1 item in [8]", ((Map<?,?>)(JsonPath.read(doc, "$[8]"))).size() == 1);
        //ARGO_ORIGINAL
        assertTrue("expected v1", "v1".equals(jsonArray.query("/8/k1")));
        //ARGO_ORIGINAL
        assertTrue("expected 2 items in [9]", ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 2);
        //ARGO_ORIGINAL
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/9/0")));
        //ARGO_ORIGINAL
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/9/1")));
    }

    /**
     * Exercise the JSONArray.put(index, value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test //TEST_MODIFIED
    public void putIndex() {
        JSONArray jsonArray = new JSONArray();

        // 1
        jsonArray.put(1, false);
        // index 0
        jsonArray.put(0, true);

        String jsonArrayStr =
            "["+
                "\"hello\","+
                "\"world\""+
            "]";
        // 2
        //ARGO_SILENT
        jsonArray.put(2, new JSONArray(jsonArrayStr));

        // 5
        jsonArray.put(5, 45L);
        // 4
        jsonArray.put(4, 1);
        // 3
        jsonArray.put(3, 2.5);

        // 6
        jsonArray.put(6, "objectPut");

        // 7 will be null

        String jsonObjectStr = 
            "{"+
                "\"key10\":\"val10\","+
                "\"key20\":\"val20\","+
                "\"key30\":\"val30\""+
            "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        jsonArray.put(8, jsonObject);
        Collection<Object> collection = new ArrayList<Object>();
        collection.add(1);
        collection.add(2);
        jsonArray.put(9,collection);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k1", "v1");
        jsonArray.put(10, map);
        try {
            jsonArray.put(-1, "abc");
            //ARGO_ORIGINAL
            assertTrue("put index < 0 should have thrown exception", false);
        } catch(Exception ignored) {}

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        //ARGO_ORIGINAL
        assertTrue("expected 11 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 11);
        //ARGO_ORIGINAL
        assertTrue("expected true", Boolean.TRUE.equals(jsonArray.query("/0")));
        //ARGO_ORIGINAL
        assertTrue("expected false", Boolean.FALSE.equals(jsonArray.query("/1")));
        //ARGO_ORIGINAL
        assertTrue("expected 2 items in [2]", ((List<?>)(JsonPath.read(doc, "$[2]"))).size() == 2);
        //ARGO_ORIGINAL
        assertTrue("expected hello", "hello".equals(jsonArray.query("/2/0")));
        //ARGO_ORIGINAL
        assertTrue("expected world", "world".equals(jsonArray.query("/2/1")));
        //ARGO_EQUIVALENT
        assertTrue("expected 2.5", equivalentModuloEpsilon(Double.valueOf(2.5), jsonArray.query("/3")));
        //ARGO_ORIGINAL
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/4")));
        //ARGO_ORIGINAL
        assertTrue("expected 45", Long.valueOf(45).equals(jsonArray.query("/5")));
        //ARGO_ORIGINAL
        assertTrue("expected objectPut", "objectPut".equals(jsonArray.query("/6")));
        //ARGO_ORIGINAL
        assertTrue("expected null", JSONObject.NULL.equals(jsonArray.query("/7")));
        //ARGO_ORIGINAL
        assertTrue("expected 3 items in [8]", ((Map<?,?>)(JsonPath.read(doc, "$[8]"))).size() == 3);
        //ARGO_ORIGINAL
        assertTrue("expected val10", "val10".equals(jsonArray.query("/8/key10")));
        //ARGO_ORIGINAL
        assertTrue("expected val20", "val20".equals(jsonArray.query("/8/key20")));
        //ARGO_ORIGINAL
        assertTrue("expected val30", "val30".equals(jsonArray.query("/8/key30")));
        //ARGO_ORIGINAL
        assertTrue("expected 2 items in [9]", ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 2);
        //ARGO_ORIGINAL
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/9/0")));
        //ARGO_ORIGINAL
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/9/1")));
        //ARGO_ORIGINAL
        assertTrue("expected 1 item in [10]", ((Map<?,?>)(JsonPath.read(doc, "$[10]"))).size() == 1);
        //ARGO_ORIGINAL
        assertTrue("expected v1", "v1".equals(jsonArray.query("/10/k1")));
    }

    /**
     * Exercise the JSONArray.remove(index) method 
     * and confirm the resulting JSONArray.
     */
    @Test //TEST_SELECTED
    public void remove() {
        String arrayStr1 = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        jsonArray.remove(0);
        //ARGO_ORIGINAL
        assertTrue("array should be empty", null == jsonArray.remove(5));
        //ARGO_ORIGINAL
        assertTrue("jsonArray should be empty", jsonArray.isEmpty());
    }

    /**
     * Exercise the JSONArray.similar() method with various parameters
     * and confirm the results when not similar.
     */
    @Test //TEST_SELECTED
    public void notSimilar() {
        String arrayStr1 = 
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        JSONArray otherJsonArray = new JSONArray();
        //ARGO_ORIGINAL
        assertTrue("arrays lengths differ", !jsonArray.similar(otherJsonArray));

        JSONObject jsonObject = new JSONObject("{\"k1\":\"v1\"}");
        JSONObject otherJsonObject = new JSONObject();
        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherJsonObject);
        //ARGO_ORIGINAL
        assertTrue("arrays JSONObjects differ", !jsonArray.similar(otherJsonArray));

        JSONArray nestedJsonArray = new JSONArray("[1, 2]");
        JSONArray otherNestedJsonArray = new JSONArray();
        jsonArray = new JSONArray();
        jsonArray.put(nestedJsonArray);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherNestedJsonArray);
        //ARGO_ORIGINAL
        assertTrue("arrays nested JSONArrays differ",
                !jsonArray.similar(otherJsonArray));

        jsonArray = new JSONArray();
        jsonArray.put("hello");
        otherJsonArray = new JSONArray();
        otherJsonArray.put("world");
        //ARGO_ORIGINAL
        assertTrue("arrays values differ",
                !jsonArray.similar(otherJsonArray));
    }

    /**
     * Exercise JSONArray toString() method with various indent levels.
     */
    @Test //TEST_MODIFIED
    public void jsonArrayToStringIndent() {
        String jsonArray0Str =
                "[" +
                    "[1,2," +
                        "{\"key3\":true}" +
                    "]," +
                    "{\"key1\":\"val1\",\"key2\":" +
                        "{\"key2\":\"val2\"}" +
                    "}," +
                    "[" +
                        "[1,2.1]" +
                    "," +
                        "[null]" +
                    "]" +
                "]";

        String jsonArray1Strs [] = 
            {
                "[",
                " [",
                "  1,",
                "  2,",
                "  {\"key3\": true}",
                " ],",
                " {",
                "  \"key1\": \"val1\",",
                "  \"key2\": {\"key2\": \"val2\"}",
                " },",
                " [",
                "  [",
                "   1,",
                "   2.1",
                "  ],",
                "  [null]",
                " ]",
                "]"
            };
        String jsonArray4Strs [] =
            {
                "[",
                "    [",
                "        1,",
                "        2,",
                "        {\"key3\": true}",
                "    ],",
                "    {",
                "        \"key1\": \"val1\",",
                "        \"key2\": {\"key2\": \"val2\"}",
                "    },",
                "    [",
                "        [",
                "            1,",
                "            2.1",
                "        ],",
                "        [null]",
                "    ]",
                "]"
            };
        JSONArray jsonArray = new JSONArray(jsonArray0Str);
        String [] actualStrArray = jsonArray.toString().split("\\r?\\n");
        //ARGO_ORIGINAL
        assertEquals("Expected 1 line", 1, actualStrArray.length);
        /*actualStrArray = jsonArray.toString(0).split("\\r?\\n");
        //ARGO_NON_COVERED_FEATURE
        assertEquals("Expected 1 line", 1, actualStrArray.length);

        actualStrArray = jsonArray.toString(1).split("\\r?\\n");
        //ARGO_NON_COVERED_FEATURE
        assertEquals("Expected lines", jsonArray1Strs.length, actualStrArray.length);
        List<String> list = Arrays.asList(actualStrArray);
        for (String s : jsonArray1Strs) {
            list.contains(s);
        }
        
        actualStrArray = jsonArray.toString(4).split("\\r?\\n");
        //ARGO_NON_COVERED_FEATURE
        assertEquals("Expected lines", jsonArray1Strs.length, actualStrArray.length);
        list = Arrays.asList(actualStrArray);
        for (String s : jsonArray4Strs) {
            list.contains(s);
        }*/
    }

    /**
     * Convert an empty JSONArray to JSONObject
     */
    @Test //TEST_SELECTED
    public void toJSONObject() {
        JSONArray names = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        //ARGO_ORIGINAL
        assertTrue("toJSONObject should return null",
                null == jsonArray.toJSONObject(names));
    }

    /**
     * Confirm the creation of a JSONArray from an array of ints
     */
    @Test //TEST_SELECTED
    public void objectArrayVsIsArray() {
        int[] myInts = { 1, 2, 3, 4, 5, 6, 7 };
        Object myObject = myInts;
        JSONArray jsonArray = new JSONArray(myObject);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        //ARGO_ORIGINAL
        assertTrue("expected 7 top level items", ((List<?>)(JsonPath.read(doc, "$"))).size() == 7);
        //ARGO_ORIGINAL
        assertTrue("expected 1", Integer.valueOf(1).equals(jsonArray.query("/0")));
        //ARGO_ORIGINAL
        assertTrue("expected 2", Integer.valueOf(2).equals(jsonArray.query("/1")));
        //ARGO_ORIGINAL
        assertTrue("expected 3", Integer.valueOf(3).equals(jsonArray.query("/2")));
        //ARGO_ORIGINAL
        assertTrue("expected 4", Integer.valueOf(4).equals(jsonArray.query("/3")));
        //ARGO_ORIGINAL
        assertTrue("expected 5", Integer.valueOf(5).equals(jsonArray.query("/4")));
        //ARGO_ORIGINAL
        assertTrue("expected 6", Integer.valueOf(6).equals(jsonArray.query("/5")));
        //ARGO_ORIGINAL
        assertTrue("expected 7", Integer.valueOf(7).equals(jsonArray.query("/6")));
    }

    /**
     * Exercise the JSONArray iterator.
     */
    @SuppressWarnings("boxing")
    @Test //TEST_MODIFIED
    public void iteratorTest() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        Iterator<Object> it = jsonArray.iterator();
        //ARGO_ORIGINAL
        assertTrue("Array true",
                Boolean.TRUE.equals(it.next()));
        //ARGO_ORIGINAL
        assertTrue("Array false",
                Boolean.FALSE.equals(it.next()));
        //ARGO_ORIGINAL
        assertTrue("Array string true",
                "true".equals(it.next()));
        //ARGO_ORIGINAL
        assertTrue("Array string false",
                "false".equals(it.next()));
        //ARGO_ORIGINAL
        assertTrue("Array string",
                "hello".equals(it.next()));

        //ARGO_EQUIVALENT
        assertTrue("Array double [23.45e-4]", equivalentModuloEpsilon(
                new BigDecimal("0.002345"),it.next()));//*/it.next();
        //ARGO_ORIGINAL
        assertTrue("Array string double",
                new Double(23.45).equals(Double.parseDouble((String)it.next())));

        //ARGO_EQUIVALENT
        assertTrue("Array value int",
                new Integer(42).toString().equals(it.next().toString()));
        //ARGO_ORIGINAL
        assertTrue("Array value string int",
                new Integer(43).equals(Integer.parseInt((String)it.next())));
        //ARGO_ORIGINAL

        JSONArray nestedJsonArray = (JSONArray)it.next();
        assertTrue("Array value JSONArray", nestedJsonArray != null);
        //ARGO_ORIGINAL

        JSONObject nestedJsonObject = (JSONObject)it.next();
        assertTrue("Array value JSONObject", nestedJsonObject != null);
        //ARGO_ORIGINAL

        assertTrue("Array value long",
                new Long(0).equals(((Number) it.next()).longValue()));
        //ARGO_ORIGINAL
        assertTrue("Array value string long",
                new Long(-1).equals(Long.parseLong((String) it.next())));
        //ARGO_ORIGINAL
        assertTrue("should be at end of array", !it.hasNext());
    }
    
    @Test(expected = JSONPointerException.class) //TEST_SELECTED
    public void queryWithNoResult() {
        new JSONArray().query("/a/b");
    }
    
    /*@Test //TEST_NOT_COVERED
    public void optQueryWithNoResult() {
        //ARGO_NON_COVERED_FEATURE
        assertNull(new JSONArray().optQuery("/a/b"));
    }
    
    @Test(expected = IllegalArgumentException.class) //TEST_NOT_COVERED
    public void optQueryWithSyntaxError() {
        //ARGO_NON_COVERED_FEATURE
        new JSONArray().optQuery("invalid");
    }*/


    /**
     * Exercise the JSONArray write() method
     */
    @Test //TEST_SELECTED
    public void write() throws IOException {
        String str = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":2,\"key3\":3}]";
        JSONArray jsonArray = new JSONArray(str);
        String expectedStr = str;
        StringWriter stringWriter = new StringWriter();
        try {
            jsonArray.write(stringWriter);
            String actualStr = stringWriter.toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            //ARGO_ORIGINAL
            assertTrue("write() expected " + expectedStr +
                    " but found " + actualStr,
                    actualStr.startsWith("[\"value1\",\"value2\",{")
                    && actualStr.contains("\"key1\":1")
                    && actualStr.contains("\"key2\":2")
                    && actualStr.contains("\"key3\":3")
                    );
        } finally {
            stringWriter.close();
        }
    }

    /**
     * Exercise the JSONArray write() method using Appendable.
     */
/*
    @Test //TEST_NOT_COVERED
    public void writeAppendable() {
        String str = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":2,\"key3\":3}]";
        JSONArray jsonArray = new JSONArray(str);
        String expectedStr = str;
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonArray.write(stringBuilder);
        String actualStr = appendable.toString();
        //ARGO_NON_COVERED_FEATURE
        assertTrue("write() expected " + expectedStr +
                        " but found " + actualStr,
                expectedStr.equals(actualStr));
    }
*/

    /**
     * Exercise the JSONArray write(Writer, int, int) method
     */
    /*@Test //TEST_NOT_COVERED
    public void write3Param() throws IOException {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringWriter stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 0, 0).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            //ARGO_NON_COVERED_FEATURE
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            //ARGO_NON_COVERED_FEATURE
            assertTrue("write() expected " + expectedStr +
                " but found " + actualStr,
                actualStr.startsWith("[\"value1\",\"value2\",{")
                && actualStr.contains("\"key1\":1")
                && actualStr.contains("\"key2\":false")
                && actualStr.contains("\"key3\":3.14")
            );
        } finally {
            stringWriter.close();
        }
        
        stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 2, 1).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            //ARGO_NON_COVERED_FEATURE
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            //ARGO_NON_COVERED_FEATURE
            assertTrue("write() expected " + expectedStr +
                " but found " + actualStr,
                actualStr.startsWith("[\n" + 
                        "   \"value1\",\n" + 
                        "   \"value2\",\n" + 
                        "   {")
                && actualStr.contains("\"key1\": 1")
                && actualStr.contains("\"key2\": false")
                && actualStr.contains("\"key3\": 3.14")
            );
        } finally {
            stringWriter.close();
        }
    }*/

    /**
     * Exercise the JSONArray write(Appendable, int, int) method
     */
/*
    @Test //TEST_NOT_COVERED
    public void write3ParamAppendable() {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        String str2 =
                "[\n" +
                        "   \"value1\",\n" +
                        "   \"value2\",\n" +
                        "   {\n" +
                        "     \"key1\": 1,\n" +
                        "     \"key2\": false,\n" +
                        "     \"key3\": 3.14\n" +
                        "   }\n" +
                        " ]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonArray.write(stringBuilder, 0, 0);
        String actualStr = appendable.toString();
        //ARGO_NON_COVERED_FEATURE
        assertEquals(expectedStr, actualStr);

        expectedStr = str2;
        stringBuilder = new StringBuilder();
        appendable = jsonArray.write(stringBuilder, 2, 1);
        actualStr = appendable.toString();
        //ARGO_NON_COVERED_FEATURE
        assertEquals(expectedStr, actualStr);
    }
*/

    /**
     * Exercise JSONArray toString() method with various indent levels.
     */
    @Test //TEST_MODIFIED
    public void toList() {
        String jsonArrayStr =
                "[" +
                    "[1,2," +
                        "{\"key3\":true}" +
                    "]," +
                    "{\"key1\":\"val1\",\"key2\":" +
                        "{\"key2\":null}," +
                    "\"key3\":42,\"key4\":[]" +
                    "}," +
                    "[" +
                        "[\"value1\",2.1]" +
                    "," +
                        "[null]" +
                    "]" +
                "]";

        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        List<?> list = jsonArray.toList();

        //ARGO_ORIGINAL
        assertTrue("List should not be null", list != null);
        //ARGO_ORIGINAL
        assertTrue("List should have 3 elements", list.size() == 3);

        List<?> val1List = (List<?>) list.get(0);
        //ARGO_ORIGINAL
        assertTrue("val1 should not be null", val1List != null);
        //ARGO_ORIGINAL
        assertTrue("val1 should have 3 elements", val1List.size() == 3);

        //ARGO_EQUIVALENT
        assertTrue("val1 value 1 should be 1", val1List.get(0).toString().equals(Integer.valueOf(1).toString()));
        //ARGO_EQUIVALENT
        assertTrue("val1 value 2 should be 2", val1List.get(1).toString().equals(Integer.valueOf(2).toString()));

        Map<?,?> key1Value3Map = (Map<?,?>)val1List.get(2);
        //ARGO_ORIGINAL
        assertTrue("Map should not be null", key1Value3Map != null);
        //ARGO_ORIGINAL
        assertTrue("Map should have 1 element", key1Value3Map.size() == 1);
        //ARGO_ORIGINAL
        assertTrue("Map key3 should be true", key1Value3Map.get("key3").equals(Boolean.TRUE));

        Map<?,?> val2Map = (Map<?,?>) list.get(1);
        //ARGO_ORIGINAL
        assertTrue("val2 should not be null", val2Map != null);
        //ARGO_ORIGINAL
        assertTrue("val2 should have 4 elements", val2Map.size() == 4);
        //ARGO_ORIGINAL
        assertTrue("val2 map key 1 should be val1", val2Map.get("key1").equals("val1"));
        //ARGO_EQUIVALENT
        assertTrue("val2 map key 3 should be 42", val2Map.get("key3").toString().equals(Integer.valueOf(42).toString()));

        Map<?,?> val2Key2Map = (Map<?,?>)val2Map.get("key2");
        //ARGO_ORIGINAL
        assertTrue("val2 map key 2 should not be null", val2Key2Map != null);
        //ARGO_ORIGINAL
        assertTrue("val2 map key 2 should have an entry", val2Key2Map.containsKey("key2"));

        //ARGO_ORIGINAL
        assertTrue("val2 map key 2 value should be null", val2Key2Map.get("key2") == null);

        List<?> val2Key4List = (List<?>)val2Map.get("key4");
        //ARGO_ORIGINAL
        assertTrue("val2 map key 4 should not be null", val2Key4List != null);
        //ARGO_ORIGINAL
        assertTrue("val2 map key 4 should be empty", val2Key4List.isEmpty());

        List<?> val3List = (List<?>) list.get(2);
        //ARGO_ORIGINAL
        assertTrue("val3 should not be null", val3List != null);
        //ARGO_ORIGINAL
        assertTrue("val3 should have 2 elements", val3List.size() == 2);

        List<?> val3Val1List = (List<?>)val3List.get(0);
        //ARGO_ORIGINAL
        assertTrue("val3 list val 1 should not be null", val3Val1List != null);
        //ARGO_ORIGINAL
        assertTrue("val3 list val 1 should have 2 elements", val3Val1List.size() == 2);
        //ARGO_ORIGINAL
        assertTrue("val3 list val 1 list element 1 should be value1", val3Val1List.get(0).equals("value1"));
        //ARGO_EQUIVALENT
        assertTrue("val3 list val 1 list element 2 should be 2.1", equivalentModuloEpsilon(val3Val1List.get(1), new BigDecimal("2.1")));

        List<?> val3Val2List = (List<?>)val3List.get(1);
        //ARGO_ORIGINAL
        assertTrue("val3 list val 2 should not be null", val3Val2List != null);
        //ARGO_ORIGINAL
        assertTrue("val3 list val 2 should have 1 element", val3Val2List.size() == 1);
        //ARGO_ORIGINAL
        assertTrue("val3 list val 2 list element 1 should be null", val3Val2List.get(0) == null);

        // assert that toList() is a deep copy
        jsonArray.getJSONObject(1).put("key1", "still val1");
        //ARGO_ORIGINAL
        assertTrue("val2 map key 1 should be val1", val2Map.get("key1").equals("val1"));

        // assert that the new list is mutable
        //ARGO_ORIGINAL
        assertTrue("Removing an entry should succeed", list.remove(2) != null);
        //ARGO_ORIGINAL
        assertTrue("List should have 2 elements", list.size() == 2);
    }

    /**
     * Create a JSONArray with specified initial capacity.
     * Expects an exception if the initial capacity is specified as a negative integer 
     */
    /*@Test //TEST_NOT_COVERED
    public void testJSONArrayInt() {
        //ARGO_NON_COVERED_FEATURE
    	assertNotNull(new JSONArray(0));
        //ARGO_NON_COVERED_FEATURE
    	assertNotNull(new JSONArray(5));
    	// Check Size -> Even though the capacity of the JSONArray can be specified using a positive
    	// integer but the length of JSONArray always reflects upon the items added into it.
        //ARGO_NON_COVERED_FEATURE
    	assertEquals(0l, new JSONArray(10).length());
        try {
        //ARGO_NON_COVERED_FEATURE
        	assertNotNull("Should throw an exception", new JSONArray(-1));
        } catch (JSONException e) {
        //ARGO_NON_COVERED_FEATURE
            assertEquals("Expected an exception message", 
                    "JSONArray initial capacity cannot be negative.",
                    e.getMessage());
        }
    }*/
    
    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test //TEST_MODIFIED
    @SuppressWarnings({ "unchecked", "boxing" })
    public void testObjectConstructor() {
        // should copy the array
        Object o = new Object[] {2, "test2", true};
        JSONArray a = new JSONArray(o);
        assertNotNull("Should not error", a);
        assertEquals("length", 3, a.length());
        
        // should NOT copy the collection
        // this is required for backwards compatibility
        o = new ArrayList<Object>();
        ((Collection<Object>)o).add(1);
        ((Collection<Object>)o).add("test");
        ((Collection<Object>)o).add(false);
        try {
            a = new JSONArray(o);
            //ARGO_NON_COVERED_FEATURE
            //assertNull("Should error", a);
        } catch (JSONException ex) {
        }

        // should NOT copy the JSONArray
        // this is required for backwards compatibility
        o = a;
        try {
            a = new JSONArray(o);
            //ARGO_NON_COVERED_FEATURE
            //assertNull("Should error", a);
        } catch (JSONException ex) {
        }
    }
    
    /**
     * Verifies that the JSONArray constructor properly copies the original.
     */
    @Test //TEST_SELECTED
    public void testJSONArrayConstructor() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray(a1);
        //ARGO_ORIGINAL
        assertNotNull("Should not error", a2);
        //ARGO_ORIGINAL
        assertEquals("length", a1.length(), a2.length());
        
        for(int i = 0; i < a1.length(); i++) {
            //ARGO_ORIGINAL
            assertEquals("index " + i + " are equal", a1.get(i), a2.get(i));
        }
    }
    
    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test //TEST_SELECTED
    public void testJSONArrayPutAll() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray();
        a2.putAll(a1);
        //ARGO_ORIGINAL
        assertNotNull("Should not error", a2);
        //ARGO_ORIGINAL
        assertEquals("length", a1.length(), a2.length());
        
        for(int i = 0; i < a1.length(); i++) {
            //ARGO_ORIGINAL
            assertEquals("index " + i + " are equal", a1.get(i), a2.get(i));
        }
    }

    /**
	 * Tests if calling JSONArray clear() method actually makes the JSONArray empty
	 */
	@Test(expected = JSONException.class) //TEST_SELECTED
	public void jsonArrayClearMethodTest() {
		//Adds random stuff to the JSONArray
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(123);
		jsonArray.put("456");
		jsonArray.put(new JSONArray());
		jsonArray.clear(); //Clears the JSONArray
        //ARGO_ORIGINAL
		assertTrue("expected jsonArray.length() == 0", jsonArray.length() == 0); //Check if its length is 0
		jsonArray.getInt(0); //Should throws org.json.JSONException: JSONArray[0] not found
	}
}
