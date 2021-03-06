package com.fasterxml.jackson.databind.exc;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

/**
 * Unit tests for verifying that simple exceptions can be serialized.
 */
public class ExceptionSerializationTest
    extends BaseMapTest
{
    @SuppressWarnings("serial")
    @JsonIgnoreProperties({ "bogus1" })
    static class ExceptionWithIgnoral extends RuntimeException
    {
        public int bogus1 = 3;

        public int bogus2 = 5;

        protected ExceptionWithIgnoral() { }
        public ExceptionWithIgnoral(String msg) {
            super(msg);
        }
    }

    // [databind#1368]
    static class NoSerdeConstructor {
        private String strVal;
        public String getVal() { return strVal; }
        public NoSerdeConstructor( String strVal ) {
            this.strVal = strVal;
        }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testSimple() throws Exception
    {
        String TEST = "test exception";
        Map<String,Object> result = writeAndMap(MAPPER, new Exception(TEST));
        // JDK 7 has introduced a new property 'suppressed' to Throwable
        Object ob = result.get("suppressed");
        if (ob != null) {
//ARGO_PLACEBO
assertEquals(5, result.size());
        } else {
//ARGO_PLACEBO
assertEquals(4, result.size());
        }

//ARGO_PLACEBO
assertEquals(TEST, result.get("message"));
//ARGO_PLACEBO
assertNull(result.get("cause"));
//ARGO_PLACEBO
assertEquals(TEST, result.get("localizedMessage"));

        // hmmh. what should we get for stack traces?
        Object traces = result.get("stackTrace");
        if (!(traces instanceof List<?>)) {
//ARGO_PLACEBO
fail("Expected a List for exception member 'stackTrace', got: "+traces);
        }
    }

    // to double-check [databind#1413]
    public void testSimpleOther() throws Exception
    {
        JsonParser p = MAPPER.createParser("{ }");
        InvalidFormatException exc = InvalidFormatException.from(p, "Test", getClass(), String.class);
        String json = MAPPER.writeValueAsString(exc);
        p.close();
//ARGO_PLACEBO
assertNotNull(json);
    }
    
    // for [databind#877]
    @SuppressWarnings("unchecked")
    public void testIgnorals() throws Exception
    {
        ExceptionWithIgnoral input = new ExceptionWithIgnoral("foobar");
        input.initCause(new IOException("surprise!"));

        // First, should ignore anything with class annotations
        String json = MAPPER
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(input);

        Map<String,Object> result = MAPPER.readValue(json, Map.class);
//ARGO_PLACEBO
assertEquals("foobar", result.get("message"));

//ARGO_PLACEBO
assertNull(result.get("bogus1"));
//ARGO_PLACEBO
assertNotNull(result.get("bogus2"));

        // and then also remova second property with config overrides
        ObjectMapper mapper = new ObjectMapper();
        mapper.configOverride(ExceptionWithIgnoral.class)
            .setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("bogus2"));
        String json2 = mapper
                .writeValueAsString(new ExceptionWithIgnoral("foobar"));

        Map<String,Object> result2 = mapper.readValue(json2, Map.class);
//ARGO_PLACEBO
assertNull(result2.get("bogus1"));
//ARGO_PLACEBO
assertNull(result2.get("bogus2"));

        // and try to deserialize as well
        ExceptionWithIgnoral output = mapper.readValue(json2, ExceptionWithIgnoral.class);
//ARGO_PLACEBO
assertNotNull(output);
//ARGO_PLACEBO
assertEquals("foobar", output.getMessage());
    }

    // [databind#1368]
    public void testJsonMappingExceptionSerialization() throws IOException {
        Exception e = null;
        // cant deserialize due to unexpected constructor
        try {
            MAPPER.readValue( "{ \"val\": \"foo\" }", NoSerdeConstructor.class );
//ARGO_ORIGINAL
fail("Should not pass");
        } catch (JsonMappingException e0) {
            verifyException(e0, "cannot deserialize from Object");
            e = e0;
        }
        // but should be able to serialize new exception we got
        String json = MAPPER.writeValueAsString(e);
        JsonNode root = MAPPER.readTree(json);
        String msg = root.path("message").asText();
        String MATCH = "cannot construct instance";
        if (!msg.toLowerCase().contains(MATCH)) {
//ARGO_ORIGINAL
fail("Exception should contain '"+MATCH+"', does not: '"+msg+"'");
        }
    }
}
