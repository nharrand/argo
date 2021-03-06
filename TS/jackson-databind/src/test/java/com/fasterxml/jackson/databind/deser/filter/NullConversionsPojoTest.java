package com.fasterxml.jackson.databind.deser.filter;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidNullException;

// for [databind#1402]; configurable null handling, for values themselves
public class NullConversionsPojoTest extends BaseMapTest
{
    static class NullFail {
        public String nullsOk = "a";

        @JsonSetter(nulls=Nulls.FAIL)
        public String noNulls = "b";
    }

    static class NullAsEmpty {
        public String nullsOk = "a";

        @JsonSetter(nulls=Nulls.AS_EMPTY)
        public String nullAsEmpty = "b";
    }

    static class NullsForString {
        /*
        String n = "foo";

        public void setName(String name) {
            n = name;
        }
        */

        String n = "foo";

        public void setName(String n0) { n = n0; }
        public String getName() { return n; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testFailOnNull() throws Exception
    {
        // first, ok if assigning non-null to not-nullable, null for nullable
        NullFail result = MAPPER.readValue(aposToQuotes("{'noNulls':'foo', 'nullsOk':null}"),
                NullFail.class);
//ARGO_PLACEBO
assertEquals("foo", result.noNulls);
//ARGO_PLACEBO
assertNull(result.nullsOk);

        // and then see that nulls are not ok for non-nullable
        try {
            result = MAPPER.readValue(aposToQuotes("{'noNulls':null}"),
                    NullFail.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
        }
    }

    public void testFailOnNullWithDefaults() throws Exception
    {
        // also: config overrides by type should work
        String json = aposToQuotes("{'name':null}");
        NullsForString def = MAPPER.readValue(json, NullsForString.class);
//ARGO_PLACEBO
assertNull(def.getName());
        
        ObjectMapper mapper = newJsonMapper();
        mapper.configOverride(String.class)
            .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.FAIL));
        try {
            mapper.readValue(json, NullsForString.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"name\"");
        }
    }

    public void testNullsToEmptyScalar() throws Exception
    {
        NullAsEmpty result = MAPPER.readValue(aposToQuotes("{'nullAsEmpty':'foo', 'nullsOk':null}"),
                NullAsEmpty.class);
//ARGO_PLACEBO
assertEquals("foo", result.nullAsEmpty);
//ARGO_PLACEBO
assertNull(result.nullsOk);

        // and then see that nulls are not ok for non-nullable
        result = MAPPER.readValue(aposToQuotes("{'nullAsEmpty':null}"),
                NullAsEmpty.class);
//ARGO_PLACEBO
assertEquals("", result.nullAsEmpty);

        // also: config overrides by type should work
        String json = aposToQuotes("{'name':null}");
        NullsForString def = MAPPER.readValue(json, NullsForString.class);
//ARGO_PLACEBO
assertNull(def.getName());

        ObjectMapper mapper = newJsonMapper();
        mapper.configOverride(String.class)
            .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        NullsForString named = mapper.readValue(json, NullsForString.class);
//ARGO_PLACEBO
assertEquals("", named.getName());
    }
}
