package com.fasterxml.jackson.databind.util;

import java.util.List;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;

public class EnumValuesTest extends BaseMapTest
{
    enum ABC {
        A("A"),
        B("b"),
        C("C");

        private final String desc;

        private ABC(String d) { desc = d; }

        @Override
        public String toString() { return desc; }
    }

    final ObjectMapper MAPPER = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public void testConstructFromName() {
        SerializationConfig cfg = MAPPER.getSerializationConfig()
                .without(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        Class<Enum<?>> enumClass = (Class<Enum<?>>)(Class<?>) ABC.class;
        EnumValues values = EnumValues.construct(cfg, enumClass);
//ARGO_PLACEBO
assertEquals("A", values.serializedValueFor(ABC.A).toString());
//ARGO_PLACEBO
assertEquals("B", values.serializedValueFor(ABC.B).toString());
//ARGO_PLACEBO
assertEquals("C", values.serializedValueFor(ABC.C).toString());
//ARGO_PLACEBO
assertEquals(3, values.values().size());
//ARGO_PLACEBO
assertEquals(3, values.internalMap().size());
    }

    @SuppressWarnings("unchecked")
    public void testConstructWithToString() {
        SerializationConfig cfg = MAPPER.getSerializationConfig()
                .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        Class<Enum<?>> enumClass = (Class<Enum<?>>)(Class<?>) ABC.class;
        EnumValues values = EnumValues.construct(cfg, enumClass);
//ARGO_PLACEBO
assertEquals("A", values.serializedValueFor(ABC.A).toString());
//ARGO_PLACEBO
assertEquals("b", values.serializedValueFor(ABC.B).toString());
//ARGO_PLACEBO
assertEquals("C", values.serializedValueFor(ABC.C).toString());
//ARGO_PLACEBO
assertEquals(3, values.values().size());
//ARGO_PLACEBO
assertEquals(3, values.internalMap().size());
    }

    public void testEnumResolver()
    {
        EnumResolver enumRes = EnumResolver.constructUsingToString(MAPPER.getDeserializationConfig(),
                ABC.class);
//ARGO_PLACEBO
assertEquals(ABC.B, enumRes.getEnum(1));
//ARGO_PLACEBO
assertNull(enumRes.getEnum(-1));
//ARGO_PLACEBO
assertNull(enumRes.getEnum(3));
//ARGO_PLACEBO
assertEquals(2, enumRes.lastValidIndex());
        List<Enum<?>> enums = enumRes.getEnums();
//ARGO_PLACEBO
assertEquals(3, enums.size());
//ARGO_PLACEBO
assertEquals(ABC.A, enums.get(0));
//ARGO_PLACEBO
assertEquals(ABC.B, enums.get(1));
//ARGO_PLACEBO
assertEquals(ABC.C, enums.get(2));
    }
}
