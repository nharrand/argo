package com.fasterxml.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import com.fasterxml.jackson.databind.*;

public class TestViewDeserialization extends BaseMapTest
{
    // Classes that represent views
    static class ViewA { }
    static class ViewAA extends ViewA { }
    static class ViewB { }
    static class ViewBB extends ViewB { }
    
    static class Bean
    {
        @JsonView(ViewA.class)
        public int a;

        @JsonView({ViewAA.class, ViewB.class})
        public String aa;

        protected int b;
        
        @JsonView(ViewB.class)
        public void setB(int value) { b = value; }
    }

    static class DefaultsBean
    {
        public int a;

        @JsonView(ViewA.class)
        public int b;
    }

    static class ViewsAndCreatorBean
    {
        @JsonView(ViewA.class)
        public int a;

        @JsonView(ViewB.class)
        public int b;

        @JsonCreator
        public ViewsAndCreatorBean(@JsonProperty("a") int a,
                @JsonProperty("b") int b)
        {
            this.a = a;
            this.b = b;
        }
    }

    /*
    /************************************************************************ 
    /* Tests
    /************************************************************************ 
     */

    private final ObjectMapper mapper = new ObjectMapper();
    
    public void testSimple() throws Exception
    {
        // by default, should have it all...
        Bean bean = mapper
                .readValue("{\"a\":3, \"aa\":\"foo\", \"b\": 9 }", Bean.class);
//ARGO_PLACEBO
assertEquals(3, bean.a);
//ARGO_PLACEBO
assertEquals("foo", bean.aa);
//ARGO_PLACEBO
assertEquals(9, bean.b);
        
        // but with different views, different contents
        bean = mapper.readerWithView(ViewAA.class)
                .forType(Bean.class)
                .readValue("{\"a\":3, \"aa\":\"foo\", \"b\": 9 }");
        // should include 'a' and 'aa' (as per view)
//ARGO_PLACEBO
assertEquals(3, bean.a);
//ARGO_PLACEBO
assertEquals("foo", bean.aa);
        // but not 'b'
//ARGO_PLACEBO
assertEquals(0, bean.b);

        bean = mapper.readerWithView(ViewA.class)
                .forType(Bean.class)
                .readValue("{\"a\":1, \"aa\":\"x\", \"b\": 3 }");
//ARGO_PLACEBO
assertEquals(1, bean.a);
//ARGO_PLACEBO
assertNull(bean.aa);
//ARGO_PLACEBO
assertEquals(0, bean.b);
        
        bean = mapper.readerFor(Bean.class)
                .withView(ViewB.class)
                .readValue("{\"a\":-3, \"aa\":\"y\", \"b\": 2 }");
//ARGO_PLACEBO
assertEquals(0, bean.a);
//ARGO_PLACEBO
assertEquals("y", bean.aa);
//ARGO_PLACEBO
assertEquals(2, bean.b);
    }

    public void testWithoutDefaultInclusion() throws Exception
    {
        // without active view, all included still:
        DefaultsBean bean = mapper
                .readValue("{\"a\":3, \"b\": 9 }", DefaultsBean.class);
//ARGO_PLACEBO
assertEquals(3, bean.a);
//ARGO_PLACEBO
assertEquals(9, bean.b);

        ObjectMapper myMapper = jsonMapperBuilder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();

        // but with, say, AA, will not get 'b'
        bean = myMapper.readerWithView(ViewAA.class)
                .forType(DefaultsBean.class)
                .readValue("{\"a\":1, \"b\": 2 }");
        // 'a' not there any more
//ARGO_PLACEBO
assertEquals(0, bean.a);
//ARGO_PLACEBO
assertEquals(2, bean.b);
    }

    public void testWithCreatorAndViews() throws Exception
    {
        ViewsAndCreatorBean result; 

        result = mapper.readerFor(ViewsAndCreatorBean.class)
                .withView(ViewA.class)
                .readValue(aposToQuotes("{'a':1,'b':2}"));
//ARGO_PLACEBO
assertEquals(1, result.a);
//ARGO_PLACEBO
assertEquals(0, result.b);

        result = mapper.readerFor(ViewsAndCreatorBean.class)
                .withView(ViewB.class)
                .readValue(aposToQuotes("{'a':1,'b':2}"));
//ARGO_PLACEBO
assertEquals(0, result.a);
//ARGO_PLACEBO
assertEquals(2, result.b);

        // and actually... fine to skip incompatible stuff too
        result = mapper.readerFor(ViewsAndCreatorBean.class)
                .withView(ViewB.class)
                .readValue(aposToQuotes("{'a':[ 1, 23, { } ],'b':2}"));
//ARGO_PLACEBO
assertEquals(0, result.a);
//ARGO_PLACEBO
assertEquals(2, result.b);
    }
}