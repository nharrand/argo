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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.JSONPointer;
//import org.json.JSONPointerException;
//import org.json.JSONTokener;
import org.junit.Test;

public class JSONPointerTest {/*

    private static final JSONObject document;

    static {
        @SuppressWarnings("resource")
        InputStream resourceAsStream = JSONPointerTest.class.getClassLoader().getResourceAsStream("jsonpointer-testdoc.json");
        if(resourceAsStream == null) {
            throw new ExceptionInInitializerError("Unable to locate test file. Please check your development environment configuration");
        }
        document = new JSONObject(new JSONTokener(resourceAsStream));
    }

    private Object query(String pointer) {
        return new JSONPointer(pointer).queryFrom(document);
    }

    @Test //TEST_NOT_COVERED
    public void emptyPointer() {
        assertSame(document, query(""));
    }

    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class) //TEST_NOT_COVERED
    public void nullPointer() {
        new JSONPointer((String) null);
    }

    @Test //TEST_NOT_COVERED
    public void objectPropertyQuery() {
        assertSame(document.get("foo"), query("/foo"));
    }

    @Test //TEST_NOT_COVERED
    public void arrayIndexQuery() {
        assertSame(document.getJSONArray("foo").get(0), query("/foo/0"));
    }

    @Test(expected = JSONPointerException.class) //TEST_NOT_COVERED
    public void stringPropOfArrayFailure() {
        query("/foo/bar");
    }

    @Test //TEST_NOT_COVERED
    public void queryByEmptyKey() {
        assertSame(document.get(""), query("/"));
    }

    @Test //TEST_NOT_COVERED
    public void queryByEmptyKeySubObject() {
        assertSame(document.getJSONObject("obj").getJSONObject(""), query("/obj/"));
    }

    @Test //TEST_NOT_COVERED
    public void queryByEmptyKeySubObjectSubOject() {
        assertSame(
            document.getJSONObject("obj").getJSONObject("").get(""),
            query("/obj//")
        );
    }
    
    @Test //TEST_NOT_COVERED
    public void queryByEmptyKeySubObjectValue() {
        assertSame(
            document.getJSONObject("obj").getJSONObject("").get("subKey"),
            query("/obj//subKey")
        );
    }

    @Test //TEST_NOT_COVERED
    public void slashEscaping() {
        assertSame(document.get("a/b"), query("/a~1b"));
    }

    @Test //TEST_NOT_COVERED
    public void tildeEscaping() {
        assertSame(document.get("m~n"), query("/m~0n"));
    }

    @Test //TEST_NOT_COVERED
    public void backslashEscaping() {
        assertSame(document.get("i\\j"), query("/i\\\\j"));
    }

    @Test //TEST_NOT_COVERED
    public void quotationEscaping() {
        assertSame(document.get("k\"l"), query("/k\\\\\\\"l"));
    }

    @Test //TEST_NOT_COVERED
    public void whitespaceKey() {
        assertSame(document.get(" "), query("/ "));
    }

    @Test //TEST_NOT_COVERED
    public void uriFragmentNotation() {
        assertSame(document.get("foo"), query("#/foo"));
    }

    @Test //TEST_NOT_COVERED
    public void uriFragmentNotationRoot() {
        assertSame(document, query("#"));
    }

    @Test //TEST_NOT_COVERED
    public void uriFragmentPercentHandling() {
        assertSame(document.get("c%d"), query("#/c%25d"));
        assertSame(document.get("e^f"), query("#/e%5Ef"));
        assertSame(document.get("g|h"), query("#/g%7Ch"));
        assertSame(document.get("m~n"), query("#/m~0n"));
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class) //TEST_NOT_COVERED
    public void syntaxError() {
        new JSONPointer("key");
    }

    @Test(expected = JSONPointerException.class) //TEST_NOT_COVERED
    public void arrayIndexFailure() {
        query("/foo/2");
    }

    @Test(expected = JSONPointerException.class) //TEST_NOT_COVERED
    public void primitiveFailure() {
        query("/obj/key/failure");
    }
    
    @Test //TEST_NOT_COVERED
    public void builderTest() {
        JSONPointer pointer = JSONPointer.builder()
                .append("obj")
                .append("other~key").append("another/key")
                .append(0)
                .build();
        assertEquals("val", pointer.queryFrom(document));
    }
    
    @Test(expected = NullPointerException.class) //TEST_NOT_COVERED
    public void nullToken() {
        JSONPointer.builder().append(null);
    }
    
    @Test //TEST_NOT_COVERED
    public void toStringEscaping() {
        JSONPointer pointer = JSONPointer.builder()
                .append("obj")
                .append("other~key").append("another/key")
                .append("\"")
                .append(0)
                .build();
        assertEquals("/obj/other~0key/another~1key/\\\"/0", pointer.toString());
    }
    
    @Test //TEST_NOT_COVERED
    public void emptyPointerToString() {
        assertEquals("", new JSONPointer("").toString());
    }
    
    @Test //TEST_NOT_COVERED
    public void toURIFragment() {
        assertEquals("#/c%25d", new JSONPointer("/c%d").toURIFragment());
        assertEquals("#/e%5Ef", new JSONPointer("/e^f").toURIFragment());
        assertEquals("#/g%7Ch", new JSONPointer("/g|h").toURIFragment());
        assertEquals("#/m%7En", new JSONPointer("/m~n").toURIFragment());
    }
    
    @Test //TEST_NOT_COVERED
    public void tokenListIsCopiedInConstructor() {
        JSONPointer.Builder b = JSONPointer.builder().append("key1");
        JSONPointer jp1 = b.build();
        b.append("key2");
        JSONPointer jp2 = b.build();
        if(jp1.toString().equals(jp2.toString())) {
            fail("Oops, my pointers are sharing a backing array");
        }
    }

    /**
     * Coverage for JSONObject query(String)
     *//*
    @Test //TEST_NOT_COVERED
    public void queryFromJSONObject() {
        String str = "{"+
                "\"stringKey\":\"hello world!\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\": {"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "}";    
        JSONObject jsonObject = new JSONObject(str);
        Object obj = jsonObject.query("/stringKey");
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonObject.query("/arrayKey/1");
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonObject.query("/objectKey/b");
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonObject.query("/a/b/c");
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad key/value exception",
                    "value [null] is not an array or object therefore its key b cannot be resolved".
                    equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONObject query(JSONPointer)
     *//*
    @Test //TEST_NOT_COVERED
    public void queryFromJSONObjectUsingPointer() {
        String str = "{"+
                "\"stringKey\":\"hello world!\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\": {"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "}";    
        JSONObject jsonObject = new JSONObject(str);
        Object obj = jsonObject.query(new JSONPointer("/stringKey"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonObject.query(new JSONPointer("/arrayKey/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonObject.query(new JSONPointer("/objectKey/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonObject.query(new JSONPointer("/a/b/c"));
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad key/value exception",
                    "value [null] is not an array or object therefore its key b cannot be resolved".
                    equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONObject optQuery(JSONPointer)
     *//*
    @Test //TEST_NOT_COVERED
    public void optQueryFromJSONObjectUsingPointer() {
        String str = "{"+
                "\"stringKey\":\"hello world!\","+
                "\"arrayKey\":[0,1,2],"+
                "\"objectKey\": {"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "}";    
        JSONObject jsonObject = new JSONObject(str);
        Object obj = jsonObject.optQuery(new JSONPointer("/stringKey"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonObject.optQuery(new JSONPointer("/arrayKey/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonObject.optQuery(new JSONPointer("/objectKey/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        obj = jsonObject.optQuery(new JSONPointer("/a/b/c"));
        assertTrue("Expected null", obj == null);
    }
    
    /**
     * Coverage for JSONArray query(String)
     *//*
    @Test //TEST_NOT_COVERED
    public void queryFromJSONArray() {
        String str = "["+
                "\"hello world!\","+
                "[0,1,2],"+
                "{"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "]";    
        JSONArray jsonArray = new JSONArray(str);
        Object obj = jsonArray.query("/0");
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonArray.query("/1/1");
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonArray.query("/2/b");
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonArray.query("/a/b/c");
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad index exception",
                    "a is not an array index".equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONArray query(JSONPointer)
     *//*
    @Test //TEST_NOT_COVERED
    public void queryFromJSONArrayUsingPointer() {
        String str = "["+
                "\"hello world!\","+
                "[0,1,2],"+
                "{"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "]";    
        JSONArray jsonArray = new JSONArray(str);
        Object obj = jsonArray.query(new JSONPointer("/0"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonArray.query(new JSONPointer("/1/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonArray.query(new JSONPointer("/2/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        try {
            obj = jsonArray.query(new JSONPointer("/a/b/c"));
            assertTrue("Expected JSONPointerException", false);
        } catch (JSONPointerException e) {
            assertTrue("Expected bad index exception",
                    "a is not an array index".equals(e.getMessage()));
        }
    }

    /**
     * Coverage for JSONArray optQuery(JSONPointer)
     *//*
    @Test //TEST_NOT_COVERED
    public void optQueryFromJSONArrayUsingPointer() {
        String str = "["+
                "\"hello world!\","+
                "[0,1,2],"+
                "{"+
                    "\"a\":\"aVal\","+
                    "\"b\":\"bVal\""+
                "}"+
            "]";    
        JSONArray jsonArray = new JSONArray(str);
        Object obj = jsonArray.optQuery(new JSONPointer("/0"));
        assertTrue("Expected 'hello world!'", "hello world!".equals(obj));
        obj = jsonArray.optQuery(new JSONPointer("/1/1"));
        assertTrue("Expected 1", Integer.valueOf(1).equals(obj));
        obj = jsonArray.optQuery(new JSONPointer("/2/b"));
        assertTrue("Expected bVal", "bVal".equals(obj));
        obj = jsonArray.optQuery(new JSONPointer("/a/b/c"));
        assertTrue("Expected null", obj == null);
    }*/
}
