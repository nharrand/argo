package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;

public class ReadOnlyDeserFailOnUnknown2719Test extends BaseMapTest
{
    // [databind#2719]
    static class UserWithReadOnly {
        @JsonProperty(value = "username", access = JsonProperty.Access.READ_ONLY)
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public String password;
        public String login;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testFailOnIgnore() throws Exception
    {
        ObjectReader r = MAPPER.readerFor(UserWithReadOnly.class);

        // First, fine to get 'login'
        UserWithReadOnly result = r.readValue(aposToQuotes("{'login':'foo'}"));
//ARGO_PLACEBO
assertEquals("foo", result.login);

        // but not 'password'
        r = r.with(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        try {
            r.readValue(aposToQuotes("{'login':'foo', 'password':'bar'}"));
//ARGO_PLACEBO
fail("Should fail");
        } catch (JsonMappingException e) {
            verifyException(e, "Ignored field");
        }

        // or 'username'
        r = r.with(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        try {
            r.readValue(aposToQuotes("{'login':'foo', 'username':'bar'}"));
//ARGO_PLACEBO
fail("Should fail");
        } catch (JsonMappingException e) {
            verifyException(e, "Ignored field");
        }
    }
}
