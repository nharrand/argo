package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

public class TestOverlappingTypeIdNames extends BaseMapTest
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(name = "a", value = Impl312.class),
            @JsonSubTypes.Type(name = "b", value = Impl312.class)
    })
    static abstract class Base312 { }

    static class Impl312 extends Base312 {
        public int x;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "a", value = Impl312B1.class),
        @JsonSubTypes.Type(name = "a", value = Impl312B2.class)
})
    static class Base312B {
        public int value = 1;
    }

    static class Impl312B1 extends Base312B { }
    static class Impl312B2 extends Base312B { }

    final ObjectMapper MAPPER = objectMapper();

    public void testOverlappingNameDeser() throws Exception
    {
        Base312 value;

        // Ensure both type ids are acceptable

        value = MAPPER.readValue(aposToQuotes("{'type':'a','x':7}"), Base312.class);
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertEquals(Impl312.class, value.getClass());
//ARGO_PLACEBO
assertEquals(7, ((Impl312) value).x);
        
        value = MAPPER.readValue(aposToQuotes("{'type':'b','x':3}"), Base312.class);
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertEquals(Impl312.class, value.getClass());
//ARGO_PLACEBO
assertEquals(3, ((Impl312) value).x);
    }

    public void testOverlappingNameSer() throws Exception
    {
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'type':'a','value':1}"),
                MAPPER.writeValueAsString(new Impl312B1()));
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'type':'a','value':1}"),
                MAPPER.writeValueAsString(new Impl312B2()));
    }
}
