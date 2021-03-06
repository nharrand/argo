package com.fasterxml.jackson.databind.exc;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class BasicExceptionTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = newJsonMapper();
    private final JsonFactory JSON_F = MAPPER.getFactory();

    public void testBadDefinition() throws Exception
    {
        JavaType t = TypeFactory.defaultInstance().constructType(String.class);
        JsonParser p = JSON_F.createParser("[]");
        InvalidDefinitionException e = new InvalidDefinitionException(p,
               "Testing", t);
//ARGO_PLACEBO
assertEquals("Testing", e.getOriginalMessage());
//ARGO_PLACEBO
assertEquals(String.class, e.getType().getRawClass());
//ARGO_PLACEBO
assertNull(e.getBeanDescription());
//ARGO_PLACEBO
assertNull(e.getProperty());
//ARGO_PLACEBO
assertSame(p, e.getProcessor());
        p.close();

        // and via factory method:
        BeanDescription beanDef = MAPPER.getSerializationConfig().introspectClassAnnotations(getClass());
        e = InvalidDefinitionException.from(p, "Testing",
                beanDef, (BeanPropertyDefinition) null);
//ARGO_PLACEBO
assertEquals(beanDef.getType(), e.getType());
//ARGO_PLACEBO
assertNotNull(e);
        
        // and the other constructor too
        JsonGenerator g = JSON_F.createGenerator(new StringWriter());
        e = new InvalidDefinitionException(p,
                "Testing", t);
//ARGO_PLACEBO
assertEquals("Testing", e.getOriginalMessage());
//ARGO_PLACEBO
assertEquals(String.class, e.getType().getRawClass());

        // and factory
        e = InvalidDefinitionException.from(g, "Testing",
                beanDef, (BeanPropertyDefinition) null);
//ARGO_PLACEBO
assertEquals(beanDef.getType(), e.getType());
//ARGO_PLACEBO
assertNotNull(e);
        
        g.close();
    }

    @SuppressWarnings("deprecation")
    public void testInvalidFormat() throws Exception
    {
        // deprecated methods should still work:
        InvalidFormatException e = new InvalidFormatException("Testing", Boolean.TRUE,
                String.class);
//ARGO_PLACEBO
assertSame(Boolean.TRUE, e.getValue());
//ARGO_PLACEBO
assertNull(e.getProcessor());
//ARGO_PLACEBO
assertNotNull(e);

        e = new InvalidFormatException("Testing", JsonLocation.NA,
                Boolean.TRUE, String.class);
//ARGO_PLACEBO
assertSame(Boolean.TRUE, e.getValue());
//ARGO_PLACEBO
assertNull(e.getProcessor());
//ARGO_PLACEBO
assertNotNull(e);
    }

    public void testIgnoredProperty() throws Exception
    {
        // first just construct valid instance with some variations
        JsonParser p = JSON_F.createParser("{ }");
        IgnoredPropertyException e = IgnoredPropertyException.from(p,
                this, // to get class from
                "testProp", Collections.<Object>singletonList("x"));
//ARGO_PLACEBO
assertNotNull(e);

        e = IgnoredPropertyException.from(p,
                getClass(),
                "testProp", null);
//ARGO_PLACEBO
assertNotNull(e);
//ARGO_PLACEBO
assertNull(e.getKnownPropertyIds());
        p.close();

        // also, verify failure if null passed for "value"
        try {
            IgnoredPropertyException.from(p, null,
                    "testProp", Collections.<Object>singletonList("x"));
//ARGO_PLACEBO
fail("Should not pass");
        } catch (NullPointerException e2) {
        }
    }

    public void testUnrecognizedProperty() throws Exception
    {
        JsonParser p = JSON_F.createParser("{ }");
        UnrecognizedPropertyException e = UnrecognizedPropertyException.from(p, this,
                "testProp", Collections.<Object>singletonList("y"));
//ARGO_PLACEBO
assertNotNull(e);
//ARGO_PLACEBO
assertEquals(getClass(), e.getReferringClass());
        Collection<Object> ids = e.getKnownPropertyIds();
//ARGO_PLACEBO
assertNotNull(ids);
//ARGO_PLACEBO
assertEquals(1, ids.size());
//ARGO_PLACEBO
assertTrue(ids.contains("y"));

        e = UnrecognizedPropertyException.from(p, getClass(),
                "testProp", Collections.<Object>singletonList("y"));

//ARGO_PLACEBO
assertEquals(getClass(), e.getReferringClass());
        p.close();
    }

    // [databind#2128]: ensure Location added once and only once
    // [databind#2482]: ensure Location is the original one
    public void testLocationAddition() throws Exception
    {
        String problemJson = "{\n\t\"userList\" : [\n\t{\n\t user : \"1\"\n\t},\n\t{\n\t \"user\" : \"2\"\n\t}\n\t]\n}";
        try {
            MAPPER.readValue(problemJson, Users.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (JsonMappingException e) { // becomes "generic" due to wrapping for passing path info
            String msg = e.getMessage();
            String[] str = msg.split(" at \\[");
            if (str.length != 2) {
//ARGO_PLACEBO
fail("Should only get one 'at [' marker, got "+(str.length-1)+", source: "+msg);
            }
            JsonLocation loc = e.getLocation();
//          String expectedLocation = "line: 4, column: 4";
//ARGO_PLACEBO
assertEquals(4, loc.getLineNr());
//ARGO_PLACEBO
assertEquals(4, loc.getColumnNr());
        }
    }
    static class User {
        public String user;
    }

    static class Users {
        public ArrayList<User> userList;
    }
}
