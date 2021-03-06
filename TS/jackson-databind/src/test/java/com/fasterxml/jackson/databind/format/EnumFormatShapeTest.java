package com.fasterxml.jackson.databind.format;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import com.fasterxml.jackson.databind.*;

/**
 * Unit tests for verifying serialization of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class EnumFormatShapeTest
    extends BaseMapTest
{
    @JsonFormat(shape=JsonFormat.Shape.OBJECT)
    static enum PoNUM {
        A("a1"), B("b2");

        @JsonProperty
        protected final String value;
        
        private PoNUM(String v) { value = v; }

        public String getValue() { return value; }
    }

    static enum OK {
        V1("v1");
        protected String key;
        OK(String key) { this.key = key; }
    }

    static class PoNUMContainer {
        @JsonFormat(shape=Shape.NUMBER)
        public OK text = OK.V1;
    }
    
    @JsonFormat(shape=JsonFormat.Shape.ARRAY) // alias for 'number', as of 2.5
    static enum PoAsArray
    {
        A, B;
    }

    // for [databind#572]
    static class PoOverrideAsString
    {
        @JsonFormat(shape=Shape.STRING)
        public PoNUM value = PoNUM.B;
    }

    static class PoOverrideAsNumber
    {
        @JsonFormat(shape=Shape.NUMBER)
        public PoNUM value = PoNUM.B;
    }

    // for [databind#1543]
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    enum Color {
        RED,
        YELLOW,
        GREEN
    }

    static class ColorWrapper {
        public final Color color;

        ColorWrapper(Color color) {
            this.color = color;
        }
    }

    // [databind#2365]
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Enum2365 {
        A, B, C;

        public String getMainValue() { return name()+"-x"; }
    }

    // [databind#2576]
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Enum2576 {
        DEFAULT("default"),
        ATTRIBUTES("attributes") {
            @Override
            public String toString() {
                return name();
            }
        };

        private final String key;
        private Enum2576(String key) {
            this.key = key;
        }
        public String getKey() { return this.key; }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    // Tests for JsonFormat.shape

    public void testEnumAsObjectValid() throws Exception {
//ARGO_PLACEBO
assertEquals("{\"value\":\"a1\"}", MAPPER.writeValueAsString(PoNUM.A));
    }

    public void testEnumAsIndexViaAnnotations() throws Exception {
//ARGO_PLACEBO
assertEquals("{\"text\":0}", MAPPER.writeValueAsString(new PoNUMContainer()));
    }

    // As of 2.5, use of Shape.ARRAY is legal alias for "write as number"
    public void testEnumAsObjectBroken() throws Exception
    {
//ARGO_PLACEBO
assertEquals("0", MAPPER.writeValueAsString(PoAsArray.A));
    }

    // [databind#572]
    public void testOverrideEnumAsString() throws Exception {
//ARGO_PLACEBO
assertEquals("{\"value\":\"B\"}", MAPPER.writeValueAsString(new PoOverrideAsString()));
    }

    public void testOverrideEnumAsNumber() throws Exception {
//ARGO_PLACEBO
assertEquals("{\"value\":1}", MAPPER.writeValueAsString(new PoOverrideAsNumber()));
    }

    // for [databind#1543]
    public void testEnumValueAsNumber() throws Exception {
//ARGO_PLACEBO
assertEquals(String.valueOf(Color.GREEN.ordinal()),
                MAPPER.writeValueAsString(Color.GREEN));
    }

    public void testEnumPropertyAsNumber() throws Exception {
//ARGO_PLACEBO
assertEquals(String.format(aposToQuotes("{'color':%s}"), Color.GREEN.ordinal()),
                MAPPER.writeValueAsString(new ColorWrapper(Color.GREEN)));
    }

    // [databind#2365]
    public void testEnumWithNamingStrategy() throws Exception {
        final ObjectMapper mapper = jsonMapperBuilder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
        String json = mapper.writeValueAsString(Enum2365.B);
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'main_value':'B-x'}"), json);
    }

    // [databind#2576]
    public void testEnumWithMethodOverride() throws Exception {
        String stringResult = MAPPER.writeValueAsString(Enum2576.ATTRIBUTES);
        Map<?,?> result = MAPPER.readValue(stringResult, Map.class);
        Map<String,String> exp = Collections.singletonMap("key", "attributes");
//ARGO_PLACEBO
assertEquals(exp, result);
    }
}
