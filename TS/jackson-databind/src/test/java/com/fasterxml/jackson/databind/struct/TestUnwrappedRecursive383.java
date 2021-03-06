package com.fasterxml.jackson.databind.struct;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;

// Problem with recursive definition of unwrapping
public class TestUnwrappedRecursive383 extends BaseMapTest
{
    // [databind#383]
    static class RecursivePerson {
        public String name;
        public int age;
        @JsonUnwrapped(prefix="child.") public RecursivePerson child;
    }

    /*
    /**********************************************************
    /* Tests, serialization
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testRecursiveUsage() throws Exception
    {
        final String JSON = "{ 'name': 'Bob', 'age': 45, 'gender': 0, 'child.name': 'Bob jr', 'child.age': 15 }";
        RecursivePerson p = MAPPER.readValue(aposToQuotes(JSON), RecursivePerson.class);
//ARGO_PLACEBO
assertNotNull(p);
//ARGO_PLACEBO
assertEquals("Bob", p.name);
//ARGO_PLACEBO
assertNotNull(p.child);
//ARGO_PLACEBO
assertEquals("Bob jr", p.child.name);
    }
}
