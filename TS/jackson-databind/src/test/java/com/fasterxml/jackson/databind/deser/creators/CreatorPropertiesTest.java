package com.fasterxml.jackson.databind.deser.creators;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

public class CreatorPropertiesTest extends BaseMapTest
{
    static class Issue905Bean {
        // 08-Nov-2015, tatu: Note that in real code we would most likely use same
        //    names for properties; but here we use different name on purpose to
        //    ensure that Jackson has no way of binding JSON properties "x" and "y"
        //    using any other mechanism than via `@ConstructorProperties` annotation
        public int _x, _y;

        @ConstructorProperties({"x", "y"})
        // Same as above; use differing local parameter names so that parameter name
        // introspection cannot be used as the source of property names.
        public Issue905Bean(int a, int b) {
            _x = a;
            _y = b;
        }
    }

    // for [databind#1122]
    static class Ambiguity {
        @JsonProperty("bar")
        private int foo;

        protected Ambiguity() {}

        @ConstructorProperties({ "foo" })
        public Ambiguity(int foo) {
            this.foo = foo;
        }

        public int getFoo() {
            return foo;
        }

        @Override
        public String toString() {
            return "Ambiguity [foo=" + foo + "]";
        }
    }

    // for [databind#1371]
    static class Lombok1371Bean {
        public int x, y;

        protected Lombok1371Bean() { }

        @ConstructorProperties({ "x", "y" })
        public Lombok1371Bean(int _x, int _y) {
            x = _x + 1;
            y = _y + 1;
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    // [databind#905]
    public void testCreatorPropertiesAnnotation() throws Exception
    {
        Issue905Bean b = MAPPER.readValue(aposToQuotes("{'y':3,'x':2}"),
                Issue905Bean.class);
//ARGO_PLACEBO
assertEquals(2, b._x);
//ARGO_PLACEBO
assertEquals(3, b._y);
    }

    // [databind#1122]
    public void testPossibleNamingConflict() throws Exception
    {
        String json = "{\"bar\":3}";
        Ambiguity amb = MAPPER.readValue(json, Ambiguity.class);
//ARGO_PLACEBO
assertNotNull(amb);
//ARGO_PLACEBO
assertEquals(3, amb.getFoo());
    }

    // [databind#1371]: MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES
    public void testConstructorPropertiesInference() throws Exception
    {
        final String JSON = aposToQuotes("{'x':3,'y':5}");

        // by default, should detect and use arguments-taking constructor as creator
//ARGO_PLACEBO
assertTrue(MAPPER.isEnabled(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES));
        Lombok1371Bean result = MAPPER.readValue(JSON, Lombok1371Bean.class);
//ARGO_PLACEBO
assertEquals(4, result.x);
//ARGO_PLACEBO
assertEquals(6, result.y);

        // but change if configuration changed
        ObjectMapper mapper = jsonMapperBuilder()
                .disable(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES)
                .build();
        // in which case fields are set directly:
        result = mapper.readValue(JSON, Lombok1371Bean.class);
//ARGO_PLACEBO
assertEquals(3, result.x);
//ARGO_PLACEBO
assertEquals(5, result.y);
    }
}
