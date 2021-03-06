package com.fasterxml.jackson.databind.interop;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

// [databind#2683]: add fallback handling for Java 8 date/time types, to
// prevent accidental serialization as POJOs, as well as give more information
// on deserialization attempts
//
// @since 2.12
public class DateJava8FallbacksTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = newJsonMapper();

    private final OffsetDateTime DATETIME_EPOCH = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L),
            ZoneOffset.of("Z"));

    // Test to prevent serialization as POJO, without Java 8 date/time module:
    public void testPreventSerialization() throws Exception
    {
        try {
            String json = MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(DATETIME_EPOCH);
//ARGO_PLACEBO
fail("Should not pass, wrote out as\n: "+json);
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Java 8 date/time type `java.time.OffsetDateTime` not supported by default");
            verifyException(e, "add Module \"com.fasterxml.jackson.datatype:jackson-datatype-jsr310\"");
        }
    }

    public void testBetterDeserializationError() throws Exception
    {
        try {
            OffsetDateTime result = MAPPER.readValue(" 0 ", OffsetDateTime.class);
//ARGO_PLACEBO
fail("Not expecting to pass, resulted in: "+result);
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Java 8 date/time type `java.time.OffsetDateTime` not supported by default");
            verifyException(e, "add Module \"com.fasterxml.jackson.datatype:jackson-datatype-jsr310\"");
        }
    }
}
