package com.fasterxml.jackson.databind.deser.jdk;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.Assert;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.util.ClassUtil;

/**
 * Unit tests for verifying handling of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class JDKScalarsTest
    extends BaseMapTest
{
    final static String NAN_STRING = "NaN";

    final static class BooleanBean {
        boolean _v;
        void setV(boolean v) { _v = v; }
    }

    static class BooleanWrapper {
        public Boolean wrapper;
        public boolean primitive;
        
        protected Boolean ctor;
        
        @JsonCreator
        public BooleanWrapper(@JsonProperty("ctor") Boolean foo) {
            ctor = foo;
        }
    }

    static class IntBean {
        int _v;
        void setV(int v) { _v = v; }
    }

    static class LongBean {
        long _v;
        void setV(long v) { _v = v; }
    }

    final static class DoubleBean {
        double _v;
        void setV(double v) { _v = v; }
    }

    final static class FloatBean {
        float _v;
        void setV(float v) { _v = v; }
    }
    
    final static class CharacterBean {
        char _v;
        void setV(char v) { _v = v; }
        char getV() { return _v; }
    }
    
    final static class CharacterWrapperBean {
        Character _v;
        void setV(Character v) { _v = v; }
        Character getV() { return _v; }
    }

    /**
     * Also, let's ensure that it's ok to override methods.
     */
    static class IntBean2
        extends IntBean
    {
        @Override
        void setV(int v2) { super.setV(v2+1); }
    }

    static class PrimitivesBean
    {
        public boolean booleanValue = true;
        public byte byteValue = 3;
        public char charValue = 'a';
        public short shortValue = 37;
        public int intValue = 1;
        public long longValue = 100L;
        public float floatValue = 0.25f;
        public double doubleValue = -1.0;
    }

    static class WrappersBean
    {
        public Boolean booleanValue;
        public Byte byteValue;
        public Character charValue;
        public Short shortValue;
        public Integer intValue;
        public Long longValue;
        public Float floatValue;
        public Double doubleValue;
    }

    // [databind#2101]
    static class PrimitiveCreatorBean
    {
        @JsonCreator
        public PrimitiveCreatorBean(@JsonProperty(value="a",required=true) int a,
                @JsonProperty(value="b",required=true) int b) { }
    }

    // [databind#2197]
    static class VoidBean {
        public Void value;
    }

    private final ObjectMapper MAPPER = newJsonMapper();

    private final ObjectMapper MAPPER_NO_COERCION = jsonMapperBuilder()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();

    /*
    /**********************************************************
    /* Scalar tests for boolean
    /**********************************************************
     */

    public void testBooleanPrimitive() throws Exception
    {
        // first, simple case:
        BooleanBean result = MAPPER.readValue("{\"v\":true}", BooleanBean.class);
//ARGO_PLACEBO
assertTrue(result._v);
        result = MAPPER.readValue("{\"v\":null}", BooleanBean.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertFalse(result._v);
        result = MAPPER.readValue("{\"v\":1}", BooleanBean.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertTrue(result._v);

        // should work with arrays too..
        boolean[] array = MAPPER.readValue("[ null, false ]", boolean[].class);
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertEquals(2, array.length);
//ARGO_PLACEBO
assertFalse(array[0]);
//ARGO_PLACEBO
assertFalse(array[1]);
    }

    /**
     * Simple unit test to verify that we can map boolean values to
     * java.lang.Boolean.
     */
    public void testBooleanWrapper() throws Exception
    {
        Boolean result = MAPPER.readValue("true", Boolean.class);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result);
        result = MAPPER.readValue("false", Boolean.class);
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, result);

        // should accept ints too, (0 == false, otherwise true)
        result = MAPPER.readValue("0", Boolean.class);
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, result);
        result = MAPPER.readValue("1", Boolean.class);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result);
    }

    // Test for verifying that Long values are coerced to boolean correctly as well
    public void testLongToBoolean() throws Exception
    {
        long value = 1L + Integer.MAX_VALUE;
        BooleanWrapper b = MAPPER.readValue("{\"primitive\" : "+value+", \"wrapper\":"+value+", \"ctor\":"+value+"}",
                BooleanWrapper.class);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, b.wrapper);
//ARGO_PLACEBO
assertTrue(b.primitive);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, b.ctor);

        // but ensure we can also get `false`
        b = MAPPER.readValue("{\"primitive\" : 0 , \"wrapper\":0, \"ctor\":0}",
                BooleanWrapper.class);
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, b.wrapper);
//ARGO_PLACEBO
assertFalse(b.primitive);
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, b.ctor);

        boolean[] boo = MAPPER.readValue("[ 0, 15, \"\", \"false\", \"True\" ]",
                boolean[].class);
//ARGO_PLACEBO
assertEquals(5, boo.length);
//ARGO_PLACEBO
assertFalse(boo[0]);
//ARGO_PLACEBO
assertTrue(boo[1]);
//ARGO_PLACEBO
assertFalse(boo[2]);
//ARGO_PLACEBO
assertFalse(boo[3]);
//ARGO_PLACEBO
assertTrue(boo[4]);
    }

    /*
    /**********************************************************
    /* Scalar tests for integral types
    /**********************************************************
     */

    public void testByteWrapper() throws Exception
    {
        Byte result = MAPPER.readValue("   -42\t", Byte.class);
//ARGO_PLACEBO
assertEquals(Byte.valueOf((byte)-42), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-12\"", Byte.class);
//ARGO_PLACEBO
assertEquals(Byte.valueOf((byte)-12), result);

        result = MAPPER.readValue(" 39.07", Byte.class);
//ARGO_PLACEBO
assertEquals(Byte.valueOf((byte)39), result);
    }

    public void testShortWrapper() throws Exception
    {
        Short result = MAPPER.readValue("37", Short.class);
//ARGO_PLACEBO
assertEquals(Short.valueOf((short)37), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-1009\"", Short.class);
//ARGO_PLACEBO
assertEquals(Short.valueOf((short)-1009), result);

        result = MAPPER.readValue("-12.9", Short.class);
//ARGO_PLACEBO
assertEquals(Short.valueOf((short)-12), result);
    }

    public void testCharacterWrapper() throws Exception
    {
        // First: canonical value is 1-char string
//ARGO_PLACEBO
assertEquals(Character.valueOf('a'), MAPPER.readValue(quote("a"), Character.class));

        // But can also pass in ascii code
        Character result = MAPPER.readValue(" "+((int) 'X'), Character.class);
//ARGO_PLACEBO
assertEquals(Character.valueOf('X'), result);

        // 22-Jun-2020, tatu: one special case turns out to be white space;
        //    need to avoid considering it "blank" value
//ARGO_PLACEBO
assertEquals(Character.valueOf(' '), MAPPER.readValue(quote(" "), Character.class));
        
        final CharacterWrapperBean wrapper = MAPPER.readValue("{\"v\":null}", CharacterWrapperBean.class);
//ARGO_PLACEBO
assertNotNull(wrapper);
//ARGO_PLACEBO
assertNull(wrapper.getV());

        try {
            MAPPER.readerFor(CharacterBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue("{\"v\":null}");
//ARGO_PLACEBO
fail("Attempting to deserialize a 'null' JSON reference into a 'char' property did not throw an exception");
        } catch (MismatchedInputException e) {
            verifyException(e, "cannot map `null`");
        }
        final CharacterBean charBean = MAPPER.readerFor(CharacterBean.class)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue("{\"v\":null}");
//ARGO_PLACEBO
assertNotNull(wrapper);
//ARGO_PLACEBO
assertEquals('\u0000', charBean.getV());
    }

    public void testIntWrapper() throws Exception
    {
        Integer result = MAPPER.readValue("   -42\t", Integer.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(-42), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-1200\"", Integer.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(-1200), result);

        result = MAPPER.readValue(" 39.07", Integer.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(39), result);
    }

    public void testIntPrimitive() throws Exception
    {
        // first, simple case:
        IntBean result = MAPPER.readValue("{\"v\":3}", IntBean.class);
//ARGO_PLACEBO
assertEquals(3, result._v);

        result = MAPPER.readValue("{\"v\":null}", IntBean.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(0, result._v);

        // should work with arrays too..
        int[] array = MAPPER.readValue("[ null ]", int[].class);
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(0, array[0]);
    }

    public void testLongWrapper() throws Exception
    {
        Long result = MAPPER.readValue("12345678901", Long.class);
//ARGO_PLACEBO
assertEquals(Long.valueOf(12345678901L), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-9876\"", Long.class);
//ARGO_PLACEBO
assertEquals(Long.valueOf(-9876), result);

        result = MAPPER.readValue("1918.3", Long.class);
//ARGO_PLACEBO
assertEquals(Long.valueOf(1918), result);
    }

    public void testLongPrimitive() throws Exception
    {
        // first, simple case:
        LongBean result = MAPPER.readValue("{\"v\":3}", LongBean.class);
//ARGO_PLACEBO
assertEquals(3, result._v);
        result = MAPPER.readValue("{\"v\":null}", LongBean.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(0, result._v);

        // should work with arrays too..
        long[] array = MAPPER.readValue("[ null ]", long[].class);
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(0, array[0]);
    }

    /**
     * Beyond simple case, let's also ensure that method overriding works as
     * expected.
     */
    public void testIntWithOverride() throws Exception
    {
        IntBean2 result = MAPPER.readValue("{\"v\":8}", IntBean2.class);
//ARGO_PLACEBO
assertEquals(9, result._v);
    }

    /*
    /**********************************************************
    /* Scalar tests for floating point types
    /**********************************************************
     */

    public void testDoublePrimitive() throws Exception
    {
        // first, simple case:
        // bit tricky with binary fps but...
        final double value = 0.016;
        DoubleBean result = MAPPER.readValue("{\"v\":"+value+"}", DoubleBean.class);
//ARGO_PLACEBO
assertEquals(value, result._v);
        // then [JACKSON-79]:
        result = MAPPER.readValue("{\"v\":null}", DoubleBean.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(0.0, result._v);

        // should work with arrays too..
        double[] array = MAPPER.readValue("[ null ]", double[].class);
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(0.0, array[0]);
    }

    /* Note: dealing with floating-point values is tricky; not sure if
     * we can really use equality tests here... JDK does have decent
     * conversions though, to retain accuracy and round-trippability.
     * But still...
     */
    public void testFloatWrapper() throws Exception
    {
        // Also: should be able to coerce floats, strings:
        String[] STRS = new String[] {
            "1.0", "0.0", "-0.3", "0.7", "42.012", "-999.0", NAN_STRING
        };

        for (String str : STRS) {
            Float exp = Float.valueOf(str);
            Float result;

            if (NAN_STRING != str) {
                // First, as regular floating point value
                result = MAPPER.readValue(str, Float.class);
//ARGO_PLACEBO
assertEquals(exp, result);
            }

            // and then as coerced String:
            result = MAPPER.readValue(" \""+str+"\"", Float.class);
//ARGO_PLACEBO
assertEquals(exp, result);
        }
    }

    public void testDoubleWrapper() throws Exception
    {
        // Also: should be able to coerce doubles, strings:
        String[] STRS = new String[] {
            "1.0", "0.0", "-0.3", "0.7", "42.012", "-999.0", NAN_STRING
        };

        for (String str : STRS) {
            Double exp = Double.valueOf(str);
            Double result;

            // First, as regular double value
            if (NAN_STRING != str) {
                result = MAPPER.readValue(str, Double.class);
//ARGO_PLACEBO
assertEquals(exp, result);
            }
            // and then as coerced String:
            result = MAPPER.readValue(" \""+str+"\"", Double.class);
//ARGO_PLACEBO
assertEquals(exp, result);
        }
    }

    public void testDoublePrimitiveNonNumeric() throws Exception
    {
        // first, simple case:
        // bit tricky with binary fps but...
        double value = Double.POSITIVE_INFINITY;
        DoubleBean result = MAPPER.readValue("{\"v\":\""+value+"\"}", DoubleBean.class);
//ARGO_PLACEBO
assertEquals(value, result._v);
        
        // should work with arrays too..
        double[] array = MAPPER.readValue("[ \"Infinity\" ]", double[].class);
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(Double.POSITIVE_INFINITY, array[0]);
    }
    
    public void testFloatPrimitiveNonNumeric() throws Exception
    {
        // bit tricky with binary fps but...
        float value = Float.POSITIVE_INFINITY;
        FloatBean result = MAPPER.readValue("{\"v\":\""+value+"\"}", FloatBean.class);
//ARGO_PLACEBO
assertEquals(value, result._v);
        
        // should work with arrays too..
        float[] array = MAPPER.readValue("[ \"Infinity\" ]", float[].class);
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(Float.POSITIVE_INFINITY, array[0]);
    }

    /*
    /**********************************************************
    /* Scalar tests, other
    /**********************************************************
     */

    public void testBase64Variants() throws Exception
    {
        final byte[] INPUT = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890X".getBytes("UTF-8");

        // default encoding is "MIME, no linefeeds", so:
        Assert.//ARGO_PLACEBO
assertArrayEquals(INPUT, MAPPER.readValue(
                quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="),
                byte[].class));
        ObjectReader reader = MAPPER.readerFor(byte[].class);
        Assert.//ARGO_PLACEBO
assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.MIME_NO_LINEFEEDS).readValue(
                quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="
        )));

        // but others should be slightly different
        Assert.//ARGO_PLACEBO
assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.MIME).readValue(
                quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1\\ndnd4eXoxMjM0NTY3ODkwWA=="
        )));
        Assert.//ARGO_PLACEBO
assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.MODIFIED_FOR_URL).readValue(
                quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA"
        )));
        // PEM mandates 64 char lines:
        Assert.//ARGO_PLACEBO
assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.PEM).readValue(
                quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamts\\nbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="
        )));
    }

    /*
    /**********************************************************
    /* Sequence tests
    /**********************************************************
     */

    /**
     * Then a unit test to verify that we can conveniently bind sequence of
     * space-separate simple values
     */
    public void testSequenceOfInts() throws Exception
    {
        final int NR_OF_INTS = 100;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NR_OF_INTS; ++i) {
            sb.append(" ");
            sb.append(i);
        }
        JsonParser jp = MAPPER.createParser(sb.toString());
        for (int i = 0; i < NR_OF_INTS; ++i) {
            Integer result = MAPPER.readValue(jp, Integer.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(i), result);
        }
        jp.close();
    }

    /*
    /**********************************************************
    /* Empty String coercion, handling
    /**********************************************************
     */

    // by default, should return nulls, n'est pas?
    public void testEmptyStringForBooleanWrapper() throws IOException
    {
        WrappersBean bean = MAPPER.readValue("{\"booleanValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.booleanValue);
    }

    public void testEmptyStringForIntegerWrappers() throws IOException
    {
        WrappersBean bean = MAPPER.readValue("{\"byteValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.byteValue);

        // char/Character is different... not sure if this should work or not:
        bean = MAPPER.readValue("{\"charValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.charValue);

        bean = MAPPER.readValue("{\"shortValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.shortValue);
        bean = MAPPER.readValue("{\"intValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.intValue);
        bean = MAPPER.readValue("{\"longValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.longValue);
    }

    public void testEmptyStringForFloatWrappers() throws IOException
    {
        WrappersBean bean = MAPPER.readValue("{\"floatValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.floatValue);
        bean = MAPPER.readValue("{\"doubleValue\":\"\"}", WrappersBean.class);
//ARGO_PLACEBO
assertNull(bean.doubleValue);
    }

    public void testEmptyStringForBooleanPrimitive() throws IOException
    {
        PrimitivesBean bean = MAPPER.readValue("{\"booleanValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertFalse(bean.booleanValue);
    }

    public void testEmptyStringForIntegerPrimitives() throws IOException
    {
        PrimitivesBean bean = MAPPER.readValue("{\"byteValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertEquals((byte) 0, bean.byteValue);
        bean = MAPPER.readValue("{\"charValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertEquals((char) 0, bean.charValue);
        bean = MAPPER.readValue("{\"shortValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertEquals((short) 0, bean.shortValue);
        bean = MAPPER.readValue("{\"intValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertEquals(0, bean.intValue);
        bean = MAPPER.readValue("{\"longValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertEquals(0L, bean.longValue);
    }

    public void testEmptyStringForFloatPrimitives() throws IOException
    {
        PrimitivesBean bean = MAPPER.readValue("{\"floatValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertEquals(0.0f, bean.floatValue);
        bean = MAPPER.readValue("{\"doubleValue\":\"\"}", PrimitivesBean.class);
//ARGO_PLACEBO
assertEquals(0.0, bean.doubleValue);
    }

    // for [databind#403]
    public void testEmptyStringFailForBooleanPrimitive() throws IOException
    {
        final ObjectReader reader = MAPPER
                .readerFor(PrimitivesBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        try {
            reader.readValue(aposToQuotes("{'booleanValue':''}"));
//ARGO_PLACEBO
fail("Expected failure for boolean + empty String");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot coerce `null` to `boolean`");
            verifyException(e, "FAIL_ON_NULL_FOR_PRIMITIVES");
        }
    }

    /*
    /**********************************************************
    /* Null handling for scalars in POJO
    /**********************************************************
     */

    public void testNullForPrimitives() throws IOException
    {
        // by default, ok to rely on defaults
        PrimitivesBean bean = MAPPER.readValue(
                "{\"intValue\":null, \"booleanValue\":null, \"doubleValue\":null}",
                PrimitivesBean.class);
//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals(0, bean.intValue);
//ARGO_PLACEBO
assertEquals(false, bean.booleanValue);
//ARGO_PLACEBO
assertEquals(0.0, bean.doubleValue);

        bean = MAPPER.readValue("{\"byteValue\":null, \"longValue\":null, \"floatValue\":null}",
                PrimitivesBean.class);
//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals((byte) 0, bean.byteValue);
//ARGO_PLACEBO
assertEquals(0L, bean.longValue);
//ARGO_PLACEBO
assertEquals(0.0f, bean.floatValue);

        // but not when enabled
        final ObjectReader reader = MAPPER
                .readerFor(PrimitivesBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        // boolean
        try {
            reader.readValue("{\"booleanValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for boolean + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type boolean");
            verifyPath(e, "booleanValue");
        }
        // byte/char/short/int/long
        try {
            reader.readValue("{\"byteValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for byte + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type byte");
            verifyPath(e, "byteValue");
        }
        try {
            reader.readValue("{\"charValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for char + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type char");
            verifyPath(e, "charValue");
        }
        try {
            reader.readValue("{\"shortValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for short + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type short");
            verifyPath(e, "shortValue");
        }
        try {
            reader.readValue("{\"intValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for int + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type int");
            verifyPath(e, "intValue");
        }
        try {
            reader.readValue("{\"longValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for long + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type long");
            verifyPath(e, "longValue");
        }

        // float/double
        try {
            reader.readValue("{\"floatValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for float + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type float");
            verifyPath(e, "floatValue");
        }
        try {
            reader.readValue("{\"doubleValue\":null}");
//ARGO_PLACEBO
fail("Expected failure for double + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type double");
            verifyPath(e, "doubleValue");
        }
    }

    // [databind#2101]
    public void testNullForPrimitivesViaCreator() throws IOException
    {
        try {
            /*PrimitiveCreatorBean bean =*/ MAPPER
                    .readerFor(PrimitiveCreatorBean.class)
                    .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                    .readValue(aposToQuotes("{'a': null}"));
//ARGO_PLACEBO
fail("Expected failure for `int` and `null`");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type int");
            verifyPath(e, "a");
        }
    }

    private void verifyPath(MismatchedInputException e, String propName) {
        final List<Reference> path = e.getPath();
//ARGO_PLACEBO
assertEquals(1, path.size());
//ARGO_PLACEBO
assertEquals(propName, path.get(0).getFieldName());
    }

    public void testNullForPrimitiveArrays() throws IOException
    {
        _testNullForPrimitiveArrays(boolean[].class, Boolean.FALSE);
        _testNullForPrimitiveArrays(byte[].class, Byte.valueOf((byte) 0));
        _testNullForPrimitiveArrays(char[].class, Character.valueOf((char) 0), false);
        _testNullForPrimitiveArrays(short[].class, Short.valueOf((short)0));
        _testNullForPrimitiveArrays(int[].class, Integer.valueOf(0));
        _testNullForPrimitiveArrays(long[].class, Long.valueOf(0L));
        _testNullForPrimitiveArrays(float[].class, Float.valueOf(0f));
        _testNullForPrimitiveArrays(double[].class, Double.valueOf(0d));
    }

    private void _testNullForPrimitiveArrays(Class<?> cls, Object defValue) throws IOException {
        _testNullForPrimitiveArrays(cls, defValue, true);
    }

    private void _testNullForPrimitiveArrays(Class<?> cls, Object defValue,
            boolean testEmptyString) throws IOException
    {
        final String EMPTY_STRING_JSON = "[ \"\" ]";
        final String JSON_WITH_NULL = "[ null ]";
        final String SIMPLE_NAME = "`"+cls.getSimpleName()+"`";
        final ObjectReader readerCoerceOk = MAPPER.readerFor(cls);
        final ObjectReader readerNoNulls = readerCoerceOk
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

        Object ob = readerCoerceOk.forType(cls).readValue(JSON_WITH_NULL);
//ARGO_PLACEBO
assertEquals(1, Array.getLength(ob));
//ARGO_PLACEBO
assertEquals(defValue, Array.get(ob, 0));
        try {
            readerNoNulls.readValue(JSON_WITH_NULL);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot coerce `null`");
            verifyException(e, "to element of "+SIMPLE_NAME);
        }
        
        if (testEmptyString) {
            ob = readerCoerceOk.forType(cls).readValue(EMPTY_STRING_JSON);
//ARGO_PLACEBO
assertEquals(1, Array.getLength(ob));
//ARGO_PLACEBO
assertEquals(defValue, Array.get(ob, 0));

            final ObjectReader readerNoEmpty = MAPPER_NO_COERCION.readerFor(cls);
            try {
                readerNoEmpty.readValue(EMPTY_STRING_JSON);
//ARGO_PLACEBO
fail("Should not pass");
            } catch (JsonMappingException e) {
                // 07-Jun-2020, tatu: during transition, two acceptable alternatives
                verifyException(e, "Cannot coerce `null` to", "Cannot coerce empty String (\"\")");
                verifyException(e, "element of "+SIMPLE_NAME);
            }
        }
    }

    // [databind#2197], [databind#2679]
    public void testVoidDeser() throws Exception
    {
        // First, `Void` as bean property
        VoidBean bean = MAPPER.readValue(aposToQuotes("{'value' : 123 }"),
                VoidBean.class);
//ARGO_PLACEBO
assertNull(bean.value);

        // Then `Void` and `void` (Void.TYPE) as root values
//ARGO_PLACEBO
assertNull(MAPPER.readValue("{}", Void.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue("1234", Void.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue("[ 1, true ]", Void.class));

//ARGO_PLACEBO
assertNull(MAPPER.readValue("{}", Void.TYPE));
//ARGO_PLACEBO
assertNull(MAPPER.readValue("1234", Void.TYPE));
//ARGO_PLACEBO
assertNull(MAPPER.readValue("[ 1, true ]", Void.TYPE));
    }

    /*
    /**********************************************************
    /* Test for invalid String values
    /**********************************************************
     */

    public void testInvalidStringCoercionFail() throws IOException
    {
        _testInvalidStringCoercionFail(boolean[].class, "boolean");
        _testInvalidStringCoercionFail(byte[].class);

        // char[] is special, cannot use generalized test here
//        _testInvalidStringCoercionFail(char[].class);
        _testInvalidStringCoercionFail(short[].class, "short");
        _testInvalidStringCoercionFail(int[].class, "int");
        _testInvalidStringCoercionFail(long[].class, "long");
        _testInvalidStringCoercionFail(float[].class, "float");
        _testInvalidStringCoercionFail(double[].class, "double");
    }

    private void _testInvalidStringCoercionFail(Class<?> cls) throws IOException
    {
        _testInvalidStringCoercionFail(cls, cls.getSimpleName());
    }

    private void _testInvalidStringCoercionFail(Class<?> cls, String targetTypeName)
            throws IOException
    {
        final String JSON = "[ \"foobar\" ]";

        try {
            MAPPER.readerFor(cls).readValue(JSON);
//ARGO_PLACEBO
fail("Should MismatchedInputException pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize value of type `"+targetTypeName+"` from String \"foobar\"");
        }
    }

    /*
    /**********************************************************
    /* Tests for mismatch: JSON Object for scalars (not supported
    /* for JSON
    /**********************************************************
     */

    public void testFailForScalarFromObject() throws Exception
    {
        _testFailForNumberFromObject(Byte.TYPE);
        _testFailForNumberFromObject(Short.TYPE);
        _testFailForNumberFromObject(Long.TYPE);
        _testFailForNumberFromObject(Float.TYPE);
        _testFailForNumberFromObject(Double.TYPE);
        _testFailForNumberFromObject(BigInteger.class);
        _testFailForNumberFromObject(BigDecimal.class);
    }

    private void _testFailForNumberFromObject(Class<?> targetType) throws Exception
    {
        try {
            MAPPER.readValue(a2q("{'value':12}"), targetType);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "from Object value");
            verifyException(e, ClassUtil.getClassDescription(targetType));
        }
    }
}
