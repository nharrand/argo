package com.fasterxml.jackson.databind.jsontype.ext;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import org.junit.Test;

import static org.junit.Assert.//ARGO_PLACEBO
assertEquals;
import static org.junit.Assert.//ARGO_PLACEBO
assertNull;
import static org.junit.Assert.//ARGO_PLACEBO
assertSame;
import static org.junit.Assert.fail;

// for [databind#2404]
public class TestPropertyCreatorSubtypesExternalPropertyMissingProperty
{
    /**
     * Base class - external property for Fruit subclasses.
     */
    static class Box {
        private String type;
        @JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type")
        @JsonSubTypes({
                @Type(value = Apple.class, name = "apple"),
                @Type(value = Orange.class, name = "orange")
        })
        private Fruit fruit;

        Box(String type, Fruit fruit) {
            this.type = type;
            this.fruit = fruit;
        }

        @JsonCreator
        public static Box getBox(@JsonProperty("type") String type, @JsonProperty("fruit") Fruit fruit) {
            return new Box(type, fruit);
        }

        public String getType() {
            return type;
        }

        public Fruit getFruit() {
            return fruit;
        }
    }

    static abstract class Fruit {
        private String name;

        protected Fruit(String n) {
            name = n;
        }

        public String getName() {
            return name;
        }
    }

    static class Apple extends Fruit {
        private int seedCount;

        Apple(String name, int b) {
            super(name);
            seedCount = b;
        }

        public int getSeedCount() {
            return seedCount;
        }

        @JsonCreator
        public static Apple getApple(@JsonProperty("name") String name, @JsonProperty("seedCount") int seedCount) {
            return new Apple(name, seedCount);
        }
    }

    static class Orange extends Fruit {
        private String color;
        Orange(String name, String c) {
            super(name);
            color = c;
        }

        public String getColor() {
            return color;
        }

        @JsonCreator
        public static Orange getOrange(@JsonProperty("name") String name, @JsonProperty("color") String color) {
            return new Orange(name, color);
        }
    }

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

    private final ObjectReader BOX_READER_PASS;
    private final ObjectReader BOX_READER_FAIL;

    {
        final ObjectMapper mapper = new ObjectMapper();
        BOX_READER_PASS = mapper.readerFor(Box.class)
            .without(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
        BOX_READER_FAIL = mapper.readerFor(Box.class)
            .with(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
    }

    /**
     * Deserialization tests for external type id property present
     */
    @Test
    public void testDeserializationPresent() throws Exception {
        checkOrangeBox(BOX_READER_PASS);
        checkAppleBox(BOX_READER_PASS);

        checkOrangeBox(BOX_READER_FAIL);
        checkAppleBox(BOX_READER_FAIL);
    }

    /**
     * Deserialization tests for external type id property null
     */
    @Test
    public void testDeserializationNull() throws Exception {
        checkOrangeBoxNull(BOX_READER_PASS, orangeBoxNullJson);
        checkAppleBoxNull(BOX_READER_PASS, appleBoxNullJson);

        checkOrangeBoxNull(BOX_READER_FAIL, orangeBoxNullJson);
        checkAppleBoxNull(BOX_READER_FAIL, appleBoxNullJson);
    }

    /**
     * Deserialization tests for external type id property empty
     */
    @Test
    public void testDeserializationEmpty() throws Exception {
        checkOrangeBoxEmpty(BOX_READER_PASS, orangeBoxEmptyJson);
        checkAppleBoxEmpty(BOX_READER_PASS, appleBoxEmptyJson);

        checkOrangeBoxEmpty(BOX_READER_FAIL, orangeBoxEmptyJson);
        checkAppleBoxEmpty(BOX_READER_FAIL, appleBoxEmptyJson);
    }

    /**
     * Deserialization tests for external type id property missing
     */
    @Test
    public void testDeserializationMissing() throws Exception {
        checkOrangeBoxNull(BOX_READER_PASS, orangeBoxMissingJson);
        checkAppleBoxNull(BOX_READER_PASS, appleBoxMissingJson);

        checkBoxJsonMappingException(BOX_READER_FAIL, orangeBoxMissingJson);
        checkBoxJsonMappingException(BOX_READER_FAIL, appleBoxMissingJson);
    }

    private void checkOrangeBox(ObjectReader reader) throws Exception {
        Box deserOrangeBox = reader.readValue(orangeBoxJson);
//ARGO_PLACEBO
assertEquals(orangeBox.getType(), deserOrangeBox.getType());

        Fruit deserOrange = deserOrangeBox.getFruit();
//ARGO_PLACEBO
assertSame(Orange.class, deserOrange.getClass());
//ARGO_PLACEBO
assertEquals(orange.getName(), deserOrange.getName());
//ARGO_PLACEBO
assertEquals(orange.getColor(), ((Orange) deserOrange).getColor());
    }

    private void checkAppleBox(ObjectReader reader) throws Exception {
        Box deserAppleBox = reader.readValue(appleBoxJson);
//ARGO_PLACEBO
assertEquals(appleBox.getType(), deserAppleBox.getType());

        Fruit deserApple = deserAppleBox.getFruit();
//ARGO_PLACEBO
assertSame(Apple.class, deserApple.getClass());
//ARGO_PLACEBO
assertEquals(apple.getName(), deserApple.getName());
//ARGO_PLACEBO
assertEquals(apple.getSeedCount(), ((Apple) deserApple).getSeedCount());
    }

    private void checkOrangeBoxEmpty(ObjectReader reader, String json) throws Exception {
        Box deserOrangeBox = reader.readValue(json);
//ARGO_PLACEBO
assertEquals(orangeBox.getType(), deserOrangeBox.getType());

        Fruit deserOrange = deserOrangeBox.getFruit();
//ARGO_PLACEBO
assertSame(Orange.class, deserOrange.getClass());
//ARGO_PLACEBO
assertNull(deserOrange.getName());
//ARGO_PLACEBO
assertNull(((Orange) deserOrange).getColor());
    }

    private void checkAppleBoxEmpty(ObjectReader reader, String json) throws Exception {
        Box deserAppleBox = reader.readValue(json);
//ARGO_PLACEBO
assertEquals(appleBox.getType(), deserAppleBox.getType());

        Fruit deserApple = deserAppleBox.getFruit();
//ARGO_PLACEBO
assertSame(Apple.class, deserApple.getClass());
//ARGO_PLACEBO
assertNull(deserApple.getName());
//ARGO_PLACEBO
assertEquals(0, ((Apple) deserApple).getSeedCount());
    }

    private void checkOrangeBoxNull(ObjectReader reader, String json) throws Exception {
        Box deserOrangeBox = reader.readValue(json);
//ARGO_PLACEBO
assertEquals(orangeBox.getType(), deserOrangeBox.getType());
//ARGO_PLACEBO
assertNull(deserOrangeBox.getFruit());
    }

    private void checkAppleBoxNull(ObjectReader reader, String json) throws Exception {
        Box deserAppleBox = reader.readValue(json);
//ARGO_PLACEBO
assertEquals(appleBox.getType(), deserAppleBox.getType());
//ARGO_PLACEBO
assertNull(deserAppleBox.getFruit());
    }

    private void checkBoxJsonMappingException(ObjectReader reader, String json) throws Exception {
        try {
            reader.readValue(json);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (MismatchedInputException e) {
            BaseMapTest.verifyException(e, "Missing property 'fruit' for external type id 'type'");
        }
    }
}    
