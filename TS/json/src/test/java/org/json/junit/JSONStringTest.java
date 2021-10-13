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

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.*;

import org.json.*;
import org.junit.Test;

/**
 * Tests for JSONString implementations, and the difference between
 * {@link JSONObject#valueToString} and
 */
public class JSONStringTest {

    /**
     * This tests the JSONObject.writeValue() method. We can't test directly
     * due to it being a package-protected method. Instead, we can call
     * JSONArray.write(), which delegates the writing of each entry to
     * writeValue().
     */
    @Test //TEST_SELECTED
    public void writeValues() throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put((Object)null);

        StringWriter writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[null]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(JSONObject.NULL);
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[null]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(new JSONObject());
        } finally {
            writer.close();
        }
        
        writer = new StringWriter();
        try  {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[{}]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(new JSONArray());
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[[]]".equals(output));
    
            jsonArray = new JSONArray();
            Map<?,?> singleMap = Collections.singletonMap("key1", "value1");
            jsonArray.put((Object)singleMap);
        } finally {
            writer.close();
        }
        
        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[{\"key1\":\"value1\"}]".equals(output));
    
            jsonArray = new JSONArray();
            List<?> singleList = Collections.singletonList("entry1");
            jsonArray.put((Object)singleList);
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[[\"entry1\"]]".equals(output));
    
            jsonArray = new JSONArray();
            int[] intArray = new int[] { 1, 2, 3 };
            jsonArray.put(intArray);
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[[1,2,3]]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(24);
        } finally {
            writer.close();
        }
        
        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[24]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put("string value");
        } finally {
            writer.close();
        }

        writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[\"string value\"]".equals(output));
    
            jsonArray = new JSONArray();
            jsonArray.put(true);
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_ORIGINAL
            assertTrue("String values should be equal", "[true]".equals(output));
        } finally {
            writer.close();
        }

    }

    /**
     * This tests the JSONObject.valueToString() method. These should be
     * identical to the values above, except for the enclosing [ and ].
     */
    @SuppressWarnings("boxing")
    @Test //TEST_SELECTED
    public void valuesToString() throws Exception {

        String output = JSONObject.valueToString(null);
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "null".equals(output));

        output = JSONObject.valueToString(JSONObject.NULL);
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "null".equals(output));

        output = JSONObject.valueToString(new JSONObject());
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "{}".equals(output));

        output = JSONObject.valueToString(new JSONArray());
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "[]".equals(output));

        Map<?,?> singleMap = Collections.singletonMap("key1", "value1");
        output = JSONObject.valueToString(singleMap);
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "{\"key1\":\"value1\"}".equals(output));

        List<?> singleList = Collections.singletonList("entry1");
        output = JSONObject.valueToString(singleList);
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "[\"entry1\"]".equals(output));

        int[] intArray = new int[] { 1, 2, 3 };
        output = JSONObject.valueToString(intArray);
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "[1,2,3]".equals(output));

        output = JSONObject.valueToString(24);
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "24".equals(output));

        output = JSONObject.valueToString("string value");
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "\"string value\"".equals(output));

        output = JSONObject.valueToString(true);
        //ARGO_ORIGINAL
        assertTrue("String values should be equal", "true".equals(output));

    }

    /**
     * Test what happens when toJSONString() returns a well-formed JSON value.
     * This is the usual case.
     */
    @Test //TEST_MODIFIED
    public void testJSONStringValue() throws Exception {
        JSONStringValue jsonString = new JSONStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_NON_COVERED_FEATURE
            //assertEquals("String values should be equal", "[\"the JSON string value\"]",output);
    
            output = JSONObject.valueToString(jsonString);
            //ARGO_NON_COVERED_FEATURE
            //assertEquals("String values should be equal", "\"the JSON string value\"",output);
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when toJSONString() returns null. In one case,
     * use the object's toString() method. In the other, throw a JSONException.
     */
    @Test //TEST_MODIFIED
    public void testJSONNullStringValue() throws Exception {
        JSONNullStringValue jsonString = new JSONNullStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_NON_COVERED_FEATURE
            //assertTrue("String values should be equal", "[\"the toString value\"]".equals(output));
    
            // The only different between writeValue() and valueToString():
            // in this case, valueToString throws a JSONException
            try {
                output = JSONObject.valueToString(jsonString);
                //ARGO_NON_COVERED_FEATURE
                //fail("Expected an exception, got a String value");
            } catch (Exception e) {
                //ARGO_ORIGINAL
                assertTrue("Expected JSONException", e instanceof JSONException);
                //ARGO_ERROR_HANDLING
                //assertTrue("Exception message does not match", "Bad value from toJSONString: null".equals(e.getMessage()));
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when toJSONString() returns an exception. In both
     * cases, a JSONException is thrown, with the cause and message set from
     * the original exception.
     */
    @Test //TEST_MODIFIED
    public void testJSONStringExceptionValue() {
        JSONStringExceptionValue jsonString = new JSONStringExceptionValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(jsonString);

        StringWriter writer = new StringWriter(); 
        try {
            jsonArray.write(writer).toString();
            //ARGO_ERROR_HANDLING
            //fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
            //assertEquals("Unable to write JSONArray value at index: 0", e.getMessage());
        } catch(Exception e) {
            //ARGO_ORIGINAL
            fail("Expected JSONException");
        } finally {
            try {
                writer.close();
            } catch (Exception e){}
        }

        try {
            JSONObject.valueToString(jsonString);
            //fail("Expected an exception, got a String value");
        } catch (JSONException e) {
            //ARGO_ERROR_HANDLING
            //assertTrue("Exception message does not match", "the exception value".equals(e.getMessage()));
        } catch(Exception e) {
            //ARGO_ORIGINAL
            fail("Expected JSONException");
        }
    }

    /**
     * Test what happens when a Java object's toString() returns a String value.
     * This is the usual case.
     */
    @Test //TEST_MODIFIED
    public void testStringValue() throws Exception {
        StringValue nonJsonString = new StringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(nonJsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();
            //ARGO_NON_COVERED_FEATURE
            //assertTrue("String values should be equal", "[\"the toString value for StringValue\"]".equals(output));
    
            output = JSONObject.valueToString(nonJsonString);
            //ARGO_NON_COVERED_FEATURE
            //assertTrue("String values should be equal", "\"the toString value for StringValue\"".equals(output));
        } finally {
            writer.close();
        }
    }

    /**
     * Test what happens when a Java object's toString() returns null.
     * Defaults to empty string.
     */
    /*@Test //TEST_REMOVED
    public void testNullStringValue() throws Exception {
        NullStringValue nonJsonString = new NullStringValue();
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(nonJsonString);

        StringWriter writer = new StringWriter(); 
        try {
            String output = jsonArray.write(writer).toString();

            //ARGO_ERROR_HANDLING
            assertTrue("String values should be equal", "[\"\"]".equals(output));
    
            output = JSONObject.valueToString(nonJsonString);

            //ARGO_ERROR_HANDLING
            assertTrue("String values should be equal", "\"\"".equals(output));
        } finally {
            writer.close();
        }
    }*/

    /**
     * A JSONString that returns a valid JSON string value.
     */
    private static final class JSONStringValue implements JSONString {

        @Override
        public String toJSONString() {
            return "\"the JSON string value\"";
        }

        @Override
        public String toString() {
            return "the toString value for JSONStringValue";
        }
    }

    /**
     * A JSONString that returns null when calling toJSONString().
     */
    private static final class JSONNullStringValue implements JSONString {

        @Override
        public String toJSONString() {
            return null;
        }

        @Override
        public String toString() {
            return "the toString value";
        }
    }

    /**
     * A JSONString that throw an exception when calling toJSONString().
     */
    private static final class JSONStringExceptionValue implements JSONString {

        @Override
        public String toJSONString() {
            throw new IllegalStateException("the exception value");
        }

        @Override
        public String toString() {
            return "the toString value for JSONStringExceptionValue";
        }
    }

    public static final class StringValue {

        @Override
        public String toString() {
            return "the toString value for StringValue";
        }
    }

    public static final class NullStringValue {

        @Override
        public String toString() {
            return null;
        }
    }
}
