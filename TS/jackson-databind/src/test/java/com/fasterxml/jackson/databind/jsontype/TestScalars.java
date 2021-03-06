package com.fasterxml.jackson.databind.jsontype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestScalars extends BaseMapTest
{
    private static class DynamicWrapper {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY)
        public Object value;
        
        @SuppressWarnings("unused")
        public DynamicWrapper() { }
        public DynamicWrapper(Object v) { value = v; }
    }

    static enum TestEnum { A, B; }

    private static class AbstractWrapper {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY)
        public Serializable value;
        
        @SuppressWarnings("unused")
        public AbstractWrapper() { }
        public AbstractWrapper(Serializable v) { value = v; }
    }

    static class ScalarList {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY)
        public List<Object> values = new ArrayList<Object>();

        public ScalarList() { }

        public ScalarList add(Object v) {
            values.add(v);
            return this;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    final ObjectMapper MAPPER = newJsonMapper();
    
    /**
     * Ensure that per-property dynamic types work, both for "native" types
     * and others
     */
    public void testScalarsWithTyping() throws Exception
    {
        String json;
        DynamicWrapper result;
        ObjectMapper m = MAPPER;

        // first, check "native" types
        json = m.writeValueAsString(new DynamicWrapper(Integer.valueOf(3)));
        result = m.readValue(json, DynamicWrapper.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), result.value);

        json = m.writeValueAsString(new DynamicWrapper("abc"));
        result = m.readValue(json, DynamicWrapper.class);
//ARGO_PLACEBO
assertEquals("abc", result.value);

        json = m.writeValueAsString(new DynamicWrapper("abc"));
        result = m.readValue(json, DynamicWrapper.class);
//ARGO_PLACEBO
assertEquals("abc", result.value);

        json = m.writeValueAsString(new DynamicWrapper(Boolean.TRUE));
        result = m.readValue(json, DynamicWrapper.class);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result.value);
        
        // then verify other scalars
        json = m.writeValueAsString(new DynamicWrapper(Long.valueOf(7L)));
        result = m.readValue(json, DynamicWrapper.class);
//ARGO_PLACEBO
assertEquals(Long.valueOf(7), result.value);

        json = m.writeValueAsString(new DynamicWrapper(TestEnum.B));
        result = m.readValue(json, DynamicWrapper.class);
//ARGO_PLACEBO
assertEquals(TestEnum.B, result.value);
    }

    public void testScalarsViaAbstractType() throws Exception
    {
        ObjectMapper m = MAPPER;
        String json;
        AbstractWrapper result;

        // first, check "native" types
        json = m.writeValueAsString(new AbstractWrapper(Integer.valueOf(3)));
        result = m.readValue(json, AbstractWrapper.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), result.value);

        json = m.writeValueAsString(new AbstractWrapper("abc"));
        result = m.readValue(json, AbstractWrapper.class);
//ARGO_PLACEBO
assertEquals("abc", result.value);

        json = m.writeValueAsString(new AbstractWrapper("abc"));
        result = m.readValue(json, AbstractWrapper.class);
//ARGO_PLACEBO
assertEquals("abc", result.value);

        json = m.writeValueAsString(new AbstractWrapper(Boolean.TRUE));
        result = m.readValue(json, AbstractWrapper.class);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result.value);
        
        // then verify other scalars
        json = m.writeValueAsString(new AbstractWrapper(Long.valueOf(7L)));
        result = m.readValue(json, AbstractWrapper.class);
//ARGO_PLACEBO
assertEquals(Long.valueOf(7), result.value);

        json = m.writeValueAsString(new AbstractWrapper(TestEnum.B));
        result = m.readValue(json, AbstractWrapper.class);
//ARGO_PLACEBO
assertEquals(TestEnum.B, result.value);
    }

    // Test inspired by [databind#1104]
    public void testHeterogenousStringScalars() throws Exception
    {
        final UUID NULL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        ScalarList input = new ScalarList()
                .add("Test")
                .add(java.lang.Object.class)
                .add(NULL_UUID)
                ;
        String json = MAPPER.writeValueAsString(input);

        ScalarList result = MAPPER.readValue(json, ScalarList.class);
//ARGO_PLACEBO
assertNotNull(result.values);
//ARGO_PLACEBO
assertEquals(3, result.values.size());
//ARGO_PLACEBO
assertEquals("Test", result.values.get(0));
//ARGO_PLACEBO
assertEquals(Object.class, result.values.get(1));
//ARGO_PLACEBO
assertEquals(NULL_UUID, result.values.get(2));
    }
}
