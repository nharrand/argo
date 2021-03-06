package com.fasterxml.jackson.databind.jsontype.ext;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import org.junit.Test;

// for [databind#1341]
public class TestSubtypesExternalPropertyMissingProperty extends BaseMapTest
{
    /**
     * Base class - external property for Fruit subclasses.
     */
    static class Box {
        public String type;
        @JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type")
        public Fruit fruit;

        public Box() {
        }

        public Box(String type, Fruit fruit) {
            this.type = type;
            this.fruit = fruit;
        }
    }

    /**
     * Base class that requires the property to be present.
     */
    static class ReqBox {
        public String type;
        @JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type")
        @JsonProperty(required = true)
        public Fruit fruit;

        public ReqBox() {
        }

        public ReqBox(String type, Fruit fruit) {
            this.type = type;
            this.fruit = fruit;
        }
    }

    @JsonSubTypes({
            @Type(value = Apple.class, name = "apple"),
            @Type(value = Orange.class, name = "orange")
    })
    static abstract class Fruit {
        public String name;

        public Fruit() {
        }

        protected Fruit(String n) {
            name = n;
        }
    }

    static class Apple extends Fruit {
        public int seedCount;

        public Apple() {
        }

        public Apple(String name, int b) {
            super(name);
            seedCount = b;
        }
    }

    static class Orange extends Fruit {
        public String color;

        public Orange() {
        }

        public Orange(String name, String c) {
            super(name);
            color = c;
        }
    }

    private final ObjectMapper MAPPER = newJsonMapper();

    /*
    /**********************************************************
    /* Mock data
    /**********************************************************
     */

    private static final Orange orange = new Orange("Orange", "orange");
    private static final Box orangeBox = new Box("orange", orange);
    private static final String orangeBoxJson = "{\"type\":\"orange\",\"fruit\":{\"name\":\"Orange\",\"color\":\"orange\"}}";
    private static final String orangeBoxNullJson = "{\"type\":\"orange\",\"fruit\":null}}";
    private static final String orangeBoxEmptyJson = "{\"type\":\"orange\",\"fruit\":{}}}";
    private static final String orangeBoxMissingJson = "{\"type\":\"orange\"}}";

    private static final Apple apple = new Apple("Apple", 16);
    private static Box appleBox = new Box("apple", apple);
    private static final String appleBoxJson = "{\"type\":\"apple\",\"fruit\":{\"name\":\"Apple\",\"seedCount\":16}}";
    private static final String appleBoxNullJson = "{\"type\":\"apple\",\"fruit\":null}";
    private static final String appleBoxEmptyJson = "{\"type\":\"apple\",\"fruit\":{}}";
    private static final String appleBoxMissingJson = "{\"type\":\"apple\"}";

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    /**
     * Deserialization tests for external type id property present
     */
    @Test
    public void testDeserializationPresent() throws Exception {
        MAPPER.disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkOrangeBox();
        checkAppleBox();

        MAPPER.enable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkOrangeBox();
        checkAppleBox();
    }

    /**
     * Deserialization tests for external type id property null
     */
    @Test
    public void testDeserializationNull() throws Exception {
        MAPPER.disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkOrangeBoxNull(orangeBoxNullJson);
        checkAppleBoxNull(appleBoxNullJson);

        MAPPER.enable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkOrangeBoxNull(orangeBoxNullJson);
        checkAppleBoxNull(appleBoxNullJson);
    }

    /**
     * Deserialization tests for external type id property empty
     */
    @Test
    public void testDeserializationEmpty() throws Exception {
        MAPPER.disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkOrangeBoxEmpty(orangeBoxEmptyJson);
        checkAppleBoxEmpty(appleBoxEmptyJson);

        MAPPER.enable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkOrangeBoxEmpty(orangeBoxEmptyJson);
        checkAppleBoxEmpty(appleBoxEmptyJson);
    }

    /**
     * Deserialization tests for external type id property missing
     */
    @Test
    public void testDeserializationMissing() throws Exception {
        MAPPER.disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkOrangeBoxNull(orangeBoxMissingJson);
        checkAppleBoxNull(appleBoxMissingJson);

        MAPPER.enable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkBoxJsonMappingException(orangeBoxMissingJson);
        checkBoxJsonMappingException(appleBoxMissingJson);
    }

    /**
     * Deserialization tests for external type id required property missing
     */
    @Test
    public void testDeserializationMissingRequired() throws Exception {
        MAPPER.disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkReqBoxJsonMappingException(orangeBoxMissingJson);
        checkReqBoxJsonMappingException(appleBoxMissingJson);

        MAPPER.enable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        checkReqBoxJsonMappingException(orangeBoxMissingJson);
        checkReqBoxJsonMappingException(appleBoxMissingJson);
    }

    private void checkOrangeBox() throws Exception {
        Box deserOrangeBox = MAPPER.readValue(orangeBoxJson, Box.class);
//ARGO_PLACEBO
assertEquals(orangeBox.type, deserOrangeBox.type);

        Fruit deserOrange = deserOrangeBox.fruit;
//ARGO_PLACEBO
assertSame(Orange.class, deserOrange.getClass());
//ARGO_PLACEBO
assertEquals(orange.name, deserOrange.name);
//ARGO_PLACEBO
assertEquals(orange.color, ((Orange) deserOrange).color);
    }

    private void checkAppleBox() throws Exception {
        Box deserAppleBox = MAPPER.readValue(appleBoxJson, Box.class);
//ARGO_PLACEBO
assertEquals(appleBox.type, deserAppleBox.type);

        Fruit deserApple = deserAppleBox.fruit;
//ARGO_PLACEBO
assertSame(Apple.class, deserApple.getClass());
//ARGO_PLACEBO
assertEquals(apple.name, deserApple.name);
//ARGO_PLACEBO
assertEquals(apple.seedCount, ((Apple) deserApple).seedCount);
    }

    private void checkOrangeBoxEmpty(String json) throws Exception {
        Box deserOrangeBox = MAPPER.readValue(json, Box.class);
//ARGO_PLACEBO
assertEquals(orangeBox.type, deserOrangeBox.type);

        Fruit deserOrange = deserOrangeBox.fruit;
//ARGO_PLACEBO
assertSame(Orange.class, deserOrange.getClass());
//ARGO_PLACEBO
assertNull(deserOrange.name);
//ARGO_PLACEBO
assertNull(((Orange) deserOrange).color);
    }

    private void checkAppleBoxEmpty(String json) throws Exception {
        Box deserAppleBox = MAPPER.readValue(json, Box.class);
//ARGO_PLACEBO
assertEquals(appleBox.type, deserAppleBox.type);

        Fruit deserApple = deserAppleBox.fruit;
//ARGO_PLACEBO
assertSame(Apple.class, deserApple.getClass());
//ARGO_PLACEBO
assertNull(deserApple.name);
//ARGO_PLACEBO
assertEquals(0, ((Apple) deserApple).seedCount);
    }

    private void checkOrangeBoxNull(String json) throws Exception {
        Box deserOrangeBox = MAPPER.readValue(json, Box.class);
//ARGO_PLACEBO
assertEquals(orangeBox.type, deserOrangeBox.type);
//ARGO_PLACEBO
assertNull(deserOrangeBox.fruit);
    }

    private void checkAppleBoxNull(String json) throws Exception {
        Box deserAppleBox = MAPPER.readValue(json, Box.class);
//ARGO_PLACEBO
assertEquals(appleBox.type, deserAppleBox.type);
//ARGO_PLACEBO
assertNull(deserAppleBox.fruit);
    }

    private void checkBoxJsonMappingException(String json) throws Exception {
        try {
            MAPPER.readValue(json, Box.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (MismatchedInputException e) {
            BaseMapTest.verifyException(e, "Missing property 'fruit' for external type id 'type'");
        }
    }

    private void checkReqBoxJsonMappingException(String json) throws Exception {
        try {
            MAPPER.readValue(json, ReqBox.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (MismatchedInputException e) {
            BaseMapTest.verifyException(e, "Missing property 'fruit' for external type id 'type'");
        }
    }
}    
