package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.*;

// Tests for [databind#653]
public class BeanNamingTest extends BaseMapTest
{
    static class URLBean {
        public String getURL() {
            return "http://foo";
        }
    }

    static class ABean {
        public int getA() {
            return 3;
        }
    }
    
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
//ARGO_PLACEBO
assertFalse(mapper.isEnabled(MapperFeature.USE_STD_BEAN_NAMING));
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'url':'http://foo'}"),
                mapper.writeValueAsString(new URLBean()));
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'a':3}"),
                mapper.writeValueAsString(new ABean()));

        mapper = jsonMapperBuilder()
                .enable(MapperFeature.USE_STD_BEAN_NAMING)
                .build();
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'URL':'http://foo'}"),
                mapper.writeValueAsString(new URLBean()));
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'a':3}"),
                mapper.writeValueAsString(new ABean()));
    }
}
