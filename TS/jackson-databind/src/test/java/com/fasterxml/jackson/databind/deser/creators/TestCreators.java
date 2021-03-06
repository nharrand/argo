package com.fasterxml.jackson.databind.deser.creators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

/**
 * Unit tests for verifying that it is possible to annotate
 * various kinds of things with {@link JsonCreator} annotation.
 */
public class TestCreators
    extends BaseMapTest
{
    /*
    /**********************************************************
    /* Annotated helper classes, simple
    /**********************************************************
     */

    /**
     * Simple(st) possible demonstration of using annotated
     * constructors
     */
    static class ConstructorBean {
        int x;

        @JsonCreator protected ConstructorBean(@JsonProperty("x") int x) {
            this.x = x;
        }
    }

    /**
     * Another simple constructor, but with bit more unusual argument
     * type
     */
    static class BooleanConstructorBean {
        Boolean b;
        protected BooleanConstructorBean(Boolean b) {
            this.b = b;
        }
    }

    static class BooleanConstructorBean2 {
        boolean b;
        protected BooleanConstructorBean2(boolean b) {
            this.b = b;
        }
    }
    
    static class DoubleConstructorBean {
        Double d; // cup?
        @JsonCreator protected DoubleConstructorBean(Double d) {
            this.d = d;
        }
    }

    static class FactoryBean {
        double d; // teehee

        private FactoryBean(double value, boolean dummy) { d = value; }

        @JsonCreator protected static FactoryBean createIt(@JsonProperty("f") double value) {
            return new FactoryBean(value, true);
        }
    }

    static class LongFactoryBean {
        long value;

        private LongFactoryBean(long v) { value = v; }

        @JsonCreator static protected LongFactoryBean valueOf(long v) {
            return new LongFactoryBean(v);
        }
    }

    static class StringFactoryBean {
        String value;

        private StringFactoryBean(String v, boolean dummy) { value = v; }

        @JsonCreator static protected StringFactoryBean valueOf(String v) {
            return new StringFactoryBean(v, true);
        }
    }

    static class FactoryBeanMixIn { // static just to be able to use static methods
        /**
         * Note: signature (name and parameter types) must match; but
         * only annotations will be used, not code or such. And use
         * is by augmentation, so we only need to add things to add
         * or override.
         */
        static FactoryBean createIt(@JsonProperty("mixed") double xyz) {
            return null;
        }
    }

    /**
     * Simple demonstration of INVALID construtor annotation (only
     * defining name for first arg)
     */
    static class BrokenBean {
        @JsonCreator protected BrokenBean(@JsonProperty("a") int a,
                                          int b) {
        }
    }

    /**
     * Bean that defines both creator and factory methor as
     * creators. Constructors have priority; but it is possible
     * to hide it using mix-in annotations.
     */
    static class CreatorBean
    {
        String a;
        int x;

        @JsonCreator
        protected CreatorBean(@JsonProperty("a") String paramA,
                              @JsonProperty("x") int paramX)
        {
            a = "ctor:"+paramA;
            x = 1+paramX;
        }

        private CreatorBean(String a, int x, boolean dummy) {
            this.a = a;
            this.x = x;
        }

        @JsonCreator
        public static CreatorBean buildMeUpButterCup(@JsonProperty("a") String paramA,
                                                     @JsonProperty("x") int paramX)
        {
            return new CreatorBean("factory:"+paramA, paramX-1, false);
        }
    }

    /**
     * Class for sole purpose of hosting mix-in annotations.
     * Couple of things to note: (a) MUST be static class (non-static
     * get implicit pseudo-arg, 'this';
     * (b) for factory methods, must have static to match (part of signature)
     */
    abstract static class MixIn {
        @JsonIgnore private MixIn(String a, int x) { }
    }

    static class MultiBean {
        Object value;

        @JsonCreator public MultiBean(int v) { value = v; }
        @JsonCreator public MultiBean(double v) { value = v; }
        @JsonCreator public MultiBean(String v) { value = v; }
        @JsonCreator public MultiBean(boolean v) { value = v; }
    }

    // for [JACKSON-850]
    static class NoArgFactoryBean {
        public int x;
        public int y;
        
        public NoArgFactoryBean(int value) { x = value; }
        
        @JsonCreator
        public static NoArgFactoryBean create() { return new NoArgFactoryBean(123); }
    }

    // [Issue#208]
    static class FromStringBean {
        protected String value;

        private FromStringBean(String s, boolean x) {
            value = s;
        }

        // should be recognized as implicit factory method
        public static FromStringBean fromString(String s) {
            return new FromStringBean(s, false);
        }
    }
    
    /*
    /**********************************************************
    /* Annotated helper classes, mixed (creator and props)
    /**********************************************************
     */

    /**
     * Test bean for ensuring that constructors can be mixed with setters
     */
    static class ConstructorAndPropsBean
    {
        final int a, b;
        boolean c;

        @JsonCreator protected ConstructorAndPropsBean(@JsonProperty("a") int a,
                                                       @JsonProperty("b") int b)
        {
            this.a = a;
            this.b = b;
        }

        public void setC(boolean value) { c = value; }
    }

    /**
     * Test bean for ensuring that factory methods can be mixed with setters
     */
    static class FactoryAndPropsBean
    {
        boolean[] arg1;
        int arg2, arg3;

        @JsonCreator protected FactoryAndPropsBean(@JsonProperty("a") boolean[] arg)
        {
            arg1 = arg;
        }

        public void setB(int value) { arg2 = value; }
        public void setC(int value) { arg3 = value; }
    }

    static class DeferredConstructorAndPropsBean
    {
        final int[] createA;
        String propA = "xyz";
        String propB;

        @JsonCreator
        public DeferredConstructorAndPropsBean(@JsonProperty("createA") int[] a)
        {
            createA = a;
        }
        public void setPropA(String a) { propA = a; }
        public void setPropB(String b) { propB = b; }
    }

    static class DeferredFactoryAndPropsBean
    {
        String prop, ctor;

        @JsonCreator DeferredFactoryAndPropsBean(@JsonProperty("ctor") String str)
        {
            ctor = str;
        }

        public void setProp(String str) { prop = str; }
    }

    /*
    /**********************************************************
    /* Annotated helper classes for Maps
    /**********************************************************
     */

    @SuppressWarnings("serial")
    static class MapWithCtor extends HashMap<Object,Object>
    {
        final int _number;
        String _text = "initial";

        MapWithCtor() { this(-1, "default"); }

        @JsonCreator
            public MapWithCtor(@JsonProperty("number") int nr,
                               @JsonProperty("text") String t)
        {
            _number = nr;
            _text = t;
        }
    }

    @SuppressWarnings("serial")
    static class MapWithFactory extends TreeMap<Object,Object>
    {
        Boolean _b;

        private MapWithFactory(Boolean b) {
            _b = b;
        }

        @JsonCreator
            static MapWithFactory createIt(@JsonProperty("b") Boolean b)
        {
            return new MapWithFactory(b);
        }
    }

    /*
    /**********************************************************
    /* Test methods, valid cases, non-deferred, no-mixins
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();
    
    public void testSimpleConstructor() throws Exception
    {
        ConstructorBean bean = MAPPER.readValue("{ \"x\" : 42 }", ConstructorBean.class);
//ARGO_PLACEBO
assertEquals(42, bean.x);
    }

    // [JACKSON-850]
    public void testNoArgsFactory() throws Exception
    {
        NoArgFactoryBean value = MAPPER.readValue("{\"y\":13}", NoArgFactoryBean.class);
//ARGO_PLACEBO
assertEquals(13, value.y);
//ARGO_PLACEBO
assertEquals(123, value.x);
    }
    
    public void testSimpleDoubleConstructor() throws Exception
    {
        Double exp = Double.valueOf("0.25");
        DoubleConstructorBean bean = MAPPER.readValue(exp.toString(), DoubleConstructorBean.class);
//ARGO_PLACEBO
assertEquals(exp, bean.d);
    }

    public void testSimpleBooleanConstructor() throws Exception
    {
        BooleanConstructorBean bean = MAPPER.readValue(" true ", BooleanConstructorBean.class);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, bean.b);

        BooleanConstructorBean2 bean2 = MAPPER.readValue(" true ", BooleanConstructorBean2.class);
//ARGO_PLACEBO
assertTrue(bean2.b);
    }

    public void testSimpleBigIntegerConstructor() throws Exception
    {
        final BigIntegerWrapper result = MAPPER.readValue("17", BigIntegerWrapper.class);
//ARGO_PLACEBO
assertEquals(new BigInteger("17"), result.i);
    }

    public void testSimpleBigDecimalConstructor() throws Exception
    {
        final BigDecimalWrapper result = MAPPER.readValue("42.5", BigDecimalWrapper.class);
//ARGO_PLACEBO
assertEquals(new BigDecimal("42.5"), result.d);
    }

    public void testSimpleFactory() throws Exception
    {
        FactoryBean bean = MAPPER.readValue("{ \"f\" : 0.25 }", FactoryBean.class);
//ARGO_PLACEBO
assertEquals(0.25, bean.d);
    }

    public void testLongFactory() throws Exception
    {
        long VALUE = 123456789000L;
        LongFactoryBean bean = MAPPER.readValue(String.valueOf(VALUE), LongFactoryBean.class);
//ARGO_PLACEBO
assertEquals(VALUE, bean.value);
    }

    public void testStringFactory() throws Exception
    {
        String str = "abc";
        StringFactoryBean bean = MAPPER.readValue(quote(str), StringFactoryBean.class);
//ARGO_PLACEBO
assertEquals(str, bean.value);
    }

    public void testStringFactoryAlt() throws Exception
    {
        String str = "xyz";
        FromStringBean bean = MAPPER.readValue(quote(str), FromStringBean.class);
//ARGO_PLACEBO
assertEquals(str, bean.value);
    }
        
    public void testConstructorCreator() throws Exception
    {
        CreatorBean bean = MAPPER.readValue
            ("{ \"a\" : \"xyz\", \"x\" : 12 }", CreatorBean.class);
//ARGO_PLACEBO
assertEquals(13, bean.x);
//ARGO_PLACEBO
assertEquals("ctor:xyz", bean.a);
    }

    public void testConstructorAndProps() throws Exception
    {
        ConstructorAndPropsBean bean = MAPPER.readValue
            ("{ \"a\" : \"1\", \"b\": 2, \"c\" : true }", ConstructorAndPropsBean.class);
//ARGO_PLACEBO
assertEquals(1, bean.a);
//ARGO_PLACEBO
assertEquals(2, bean.b);
//ARGO_PLACEBO
assertEquals(true, bean.c);
    }

    public void testFactoryAndProps() throws Exception
    {
        FactoryAndPropsBean bean = MAPPER.readValue
            ("{ \"a\" : [ false, true, false ], \"b\": 2, \"c\" : -1 }", FactoryAndPropsBean.class);
//ARGO_PLACEBO
assertEquals(2, bean.arg2);
//ARGO_PLACEBO
assertEquals(-1, bean.arg3);
        boolean[] arg1 = bean.arg1;
//ARGO_PLACEBO
assertNotNull(arg1);
//ARGO_PLACEBO
assertEquals(3, arg1.length);
//ARGO_PLACEBO
assertFalse(arg1[0]);
//ARGO_PLACEBO
assertTrue(arg1[1]);
//ARGO_PLACEBO
assertFalse(arg1[2]);
    }

    /**
     * Test to verify that multiple creators may co-exist, iff
     * they use different JSON type as input
     */
    public void testMultipleCreators() throws Exception
    {
        MultiBean bean = MAPPER.readValue("123", MultiBean.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(123), bean.value);
        bean = MAPPER.readValue(quote("abc"), MultiBean.class);
//ARGO_PLACEBO
assertEquals("abc", bean.value);
        bean = MAPPER.readValue("0.25", MultiBean.class);
//ARGO_PLACEBO
assertEquals(Double.valueOf(0.25), bean.value);
    }
    
    /*
    /**********************************************************
    /* Test methods, valid cases, deferred, no mixins
    /**********************************************************
     */

    public void testDeferredConstructorAndProps() throws Exception
    {
        DeferredConstructorAndPropsBean bean = MAPPER.readValue
            ("{ \"propB\" : \"...\", \"createA\" : [ 1 ], \"propA\" : null }",
             DeferredConstructorAndPropsBean.class);

//ARGO_PLACEBO
assertEquals("...", bean.propB);
//ARGO_PLACEBO
assertNull(bean.propA);
//ARGO_PLACEBO
assertNotNull(bean.createA);
//ARGO_PLACEBO
assertEquals(1, bean.createA.length);
//ARGO_PLACEBO
assertEquals(1, bean.createA[0]);
    }

    public void testDeferredFactoryAndProps() throws Exception
    {
        DeferredFactoryAndPropsBean bean = MAPPER.readValue
            ("{ \"prop\" : \"1\", \"ctor\" : \"2\" }", DeferredFactoryAndPropsBean.class);
//ARGO_PLACEBO
assertEquals("1", bean.prop);
//ARGO_PLACEBO
assertEquals("2", bean.ctor);
    }

    /*
    /**********************************************************
    /* Test methods, valid cases, mixins
    /**********************************************************
     */

    public void testFactoryCreatorWithMixin() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        m.addMixIn(CreatorBean.class, MixIn.class);
        CreatorBean bean = m.readValue
            ("{ \"a\" : \"xyz\", \"x\" : 12 }", CreatorBean.class);
//ARGO_PLACEBO
assertEquals(11, bean.x);
//ARGO_PLACEBO
assertEquals("factory:xyz", bean.a);
    }

    public void testFactoryCreatorWithRenamingMixin() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        m.addMixIn(FactoryBean.class, FactoryBeanMixIn.class);
        // override changes property name from "f" to "mixed"
        FactoryBean bean = m.readValue("{ \"mixed\" :  20.5 }", FactoryBean.class);
//ARGO_PLACEBO
assertEquals(20.5, bean.d);
    }

    /*
    /**********************************************************
    /* Test methods, valid cases, Map with creator
    /* (to test [JACKSON-153])
    /**********************************************************
     */

    public void testMapWithConstructor() throws Exception
    {
        MapWithCtor result = MAPPER.readValue
            ("{\"text\":\"abc\", \"entry\":true, \"number\":123, \"xy\":\"yx\"}",
             MapWithCtor.class);
        // regular Map entries:
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result.get("entry"));
//ARGO_PLACEBO
assertEquals("yx", result.get("xy"));
//ARGO_PLACEBO
assertEquals(2, result.size());
        // then ones passed via constructor
//ARGO_PLACEBO
assertEquals("abc", result._text);
//ARGO_PLACEBO
assertEquals(123, result._number);
    }

    public void testMapWithFactory() throws Exception
    {
        MapWithFactory result = MAPPER.readValue
            ("{\"x\":\"...\",\"b\":true  }",
             MapWithFactory.class);
//ARGO_PLACEBO
assertEquals("...", result.get("x"));
//ARGO_PLACEBO
assertEquals(1, result.size());
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result._b);
    }

    /*
    /**********************************************************
    /* Test methods, invalid/broken cases
    /**********************************************************
     */

    public void testBrokenConstructor() throws Exception
    {
        try {
            /*BrokenBean bean =*/ MAPPER.readValue("{ \"x\" : 42 }", BrokenBean.class);
        } catch (InvalidDefinitionException je) {
            verifyException(je, "has no property name");
        }
    }
}
