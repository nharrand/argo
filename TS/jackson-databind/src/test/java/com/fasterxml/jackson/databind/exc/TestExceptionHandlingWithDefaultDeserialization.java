package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TestExceptionHandlingWithDefaultDeserialization extends BaseMapTest
{
    static class Foo {
        private Bar bar;

        public Foo() { }

        public Bar getBar() {
            return bar;
        }
    }

    static class Bar {
        private Baz baz;

        public Bar() { }

        public Baz getBaz() {
            return baz;
        }
    }

    static class Baz {
        private String qux;

        public Baz() { }

        public String getQux() {
            return qux;
        }
    }

    public void testShouldThrowJsonMappingExceptionWithPathReference() throws IOException {
        // given
        ObjectMapper mapper = new ObjectMapper();
        String input = "{\"bar\":{\"baz\":{qux:\"quxValue\"))}";
        final String THIS = getClass().getName();

        // when
        try {
            mapper.readValue(input, Foo.class);
//ARGO_PLACEBO
fail("Upsss! Exception has not been thrown.");
        } catch (JsonMappingException ex) {
            // then
//ARGO_PLACEBO
assertEquals(THIS+"$Foo[\"bar\"]->"+THIS+"$Bar[\"baz\"]",
                    ex.getPathReference());
        }
    }
}
