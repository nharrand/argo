package com.fasterxml.jackson.databind.deser.jdk;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import com.fasterxml.jackson.databind.*;

public class EnumAliasDeser2352Test extends BaseMapTest
{
    // for [databind#2352]: Support aliases on enum values
    enum MyEnum2352_1 {
        A,
        @JsonAlias({"singleAlias"})
        B,
        @JsonAlias({"multipleAliases1", "multipleAliases2"})
        C
    }
    // for [databind#2352]: Support aliases on enum values
    enum MyEnum2352_2 {
        A,
        @JsonAlias({"singleAlias"})
        B,
        @JsonAlias({"multipleAliases1", "multipleAliases2"})
        C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    // for [databind#2352]: Support aliases on enum values
    enum MyEnum2352_3 {
        A,
        @JsonEnumDefaultValue
        @JsonAlias({"singleAlias"})
        B,
        @JsonAlias({"multipleAliases1", "multipleAliases2"})
        C
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    protected final ObjectMapper MAPPER = newJsonMapper();

    // for [databind#2352]
    public void testEnumWithAlias() throws Exception {
        ObjectReader reader = MAPPER.readerFor(MyEnum2352_1.class);
        MyEnum2352_1 nonAliased = reader.readValue(quote("A"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_1.A, nonAliased);
        MyEnum2352_1 singleAlias = reader.readValue(quote("singleAlias"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_1.B, singleAlias);
        MyEnum2352_1 multipleAliases1 = reader.readValue(quote("multipleAliases1"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_1.C, multipleAliases1);
        MyEnum2352_1 multipleAliases2 = reader.readValue(quote("multipleAliases2"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_1.C, multipleAliases2);
    }

    // for [databind#2352]
    public void testEnumWithAliasAndToStringSupported() throws Exception {
        ObjectReader reader = MAPPER.readerFor(MyEnum2352_2.class)
                .with(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        MyEnum2352_2 nonAliased = reader.readValue(quote("a"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_2.A, nonAliased);
        MyEnum2352_2 singleAlias = reader.readValue(quote("singleAlias"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_2.B, singleAlias);
        MyEnum2352_2 multipleAliases1 = reader.readValue(quote("multipleAliases1"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_2.C, multipleAliases1);
        MyEnum2352_2 multipleAliases2 = reader.readValue(quote("multipleAliases2"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_2.C, multipleAliases2);
    }

    // for [databind#2352]
    public void testEnumWithAliasAndDefaultForUnknownValueEnabled() throws Exception {
        ObjectReader reader = MAPPER.readerFor(MyEnum2352_3.class)
                .with(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        MyEnum2352_3 nonAliased = reader.readValue(quote("A"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_3.A, nonAliased);
        MyEnum2352_3 singleAlias = reader.readValue(quote("singleAlias"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_3.B, singleAlias);
        MyEnum2352_3 defaulted = reader.readValue(quote("unknownValue"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_3.B, defaulted);
        MyEnum2352_3 multipleAliases1 = reader.readValue(quote("multipleAliases1"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_3.C, multipleAliases1);
        MyEnum2352_3 multipleAliases2 = reader.readValue(quote("multipleAliases2"));
//ARGO_PLACEBO
assertEquals(MyEnum2352_3.C, multipleAliases2);
    }
}
