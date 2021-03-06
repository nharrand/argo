package com.fasterxml.jackson.databind.introspect;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.introspect.TestNamingStrategyCustom.PersonBean;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests to verify functioning of standard {@link PropertyNamingStrategy}
 * implementations Jackson includes out of the box.
 */
public class TestNamingStrategyStd extends BaseMapTest
{
    @JsonPropertyOrder({"www", "some_url", "some_uris"})
    static class Acronyms
    {
        public String WWW;
        public String someURL;
        public String someURIs;
        
        public Acronyms() {this(null, null, null);}
        public Acronyms(String WWW, String someURL, String someURIs)
        {
            this.WWW = WWW;
            this.someURL = someURL;
            this.someURIs = someURIs;
        }
    }
    
    @JsonPropertyOrder({"from_user", "user", "from$user", "from7user", "_x"})
    static class UnchangedNames
    {
        public String from_user;
        public String _user;
        public String from$user;
        public String from7user;
        // Used to test "_", but it's explicitly deprecated in JDK8 so...
        public String _x;
        
        public UnchangedNames() {this(null, null, null, null, null);}
        public UnchangedNames(String from_user, String _user, String from$user, String from7user, String _x)
        {
            this.from_user = from_user;
            this._user = _user;
            this.from$user = from$user;
            this.from7user = from7user;
            this._x = _x;
        }
    }
    
    @JsonPropertyOrder({"results", "user", "__", "$_user"})
    static class OtherNonStandardNames
    {
        public String Results;
        public String _User;
        public String ___;
        public String $User;
        
        public OtherNonStandardNames() {this(null, null, null, null);}
        public OtherNonStandardNames(String Results, String _User, String ___, String $User)
        {
            this.Results = Results;
            this._User = _User;
            this.___ = ___;
            this.$User = $User;
        }
    }

    static class Bean428 {
        @JsonProperty("fooBar")
        public String whatever() {return "";}
    }

    @JsonPropertyOrder({ "firstName", "lastName" })
    @JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
    static class BoringBean {
        public String firstName = "Bob";
        public String lastName = "Burger";
    }

    public static class ClassWithObjectNodeField {
        public String id;
        public ObjectNode json;
    }    

    static class ExplicitBean {
        @JsonProperty("firstName")
        String userFirstName = "Peter";
        @JsonProperty("lastName")
        String userLastName = "Venkman";
        @JsonProperty
        String userAge = "35";
    }

    @JsonNaming()
    static class DefaultNaming {
        public int someValue = 3;
    }

    static class FirstNameBean {
        public String firstName;

        protected FirstNameBean() { }
        public FirstNameBean(String n) { firstName = n; }
    }

    /*
    /**********************************************************
    /* Set up
    /**********************************************************
     */

    final static List<Object[]> SNAKE_CASE_NAME_TRANSLATIONS = Arrays.asList(new Object[][] {
                {null, null},
                {"", ""},
                {"a", "a"},
                {"abc", "abc"},
                {"1", "1"},
                {"123", "123"},
                {"1a", "1a"},
                {"a1", "a1"},
                {"$", "$"},
                {"$a", "$a"},
                {"a$", "a$"},
                {"$_a", "$_a"},
                {"a_$", "a_$"},
                {"a$a", "a$a"},
                {"$A", "$_a"},
                {"$_A", "$_a"},
                {"_", "_"},
                {"__", "_"},
                {"___", "__"},
                {"A", "a"},
                {"A1", "a1"},
                {"1A", "1_a"},
                {"_a", "a"},
                {"_A", "a"},
                {"a_a", "a_a"},
                {"a_A", "a_a"},
                {"A_A", "a_a"},
                {"A_a", "a_a"},
                {"WWW", "www"},
                {"someURI", "some_uri"},
                {"someURIs", "some_uris"},
                {"Results", "results"},
                {"_Results", "results"},
                {"_results", "results"},
                {"__results", "_results"},
                {"__Results", "_results"},
                {"___results", "__results"},
                {"___Results", "__results"},
                {"userName", "user_name"},
                {"user_name", "user_name"},
                {"user__name", "user__name"},
                {"UserName", "user_name"},
                {"User_Name", "user_name"},
                {"User__Name", "user__name"},
                {"_user_name", "user_name"},
                {"_UserName", "user_name"},
                {"_User_Name", "user_name"},
                {"UGLY_NAME", "ugly_name"},
                {"_Bars", "bars" },
                // [databind#1026]
                {"usId", "us_id" },
                {"uId", "u_id" },
                // [databind#2267]
                {"xCoordinate", "x_coordinate" },
    });

    private ObjectMapper _lcWithUndescoreMapper;
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        _lcWithUndescoreMapper = new ObjectMapper();
        _lcWithUndescoreMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
    
    /*
    /**********************************************************
    /* Test methods for SNAKE_CASE
    /**********************************************************
     */

    /**
     * Unit test to verify translations of 
     * {@link PropertyNamingStrategies#SNAKE_CASE} 
     * outside the context of an ObjectMapper.
     */
    public void testLowerCaseStrategyStandAlone()
    {
        for (Object[] pair : SNAKE_CASE_NAME_TRANSLATIONS) {
            String translatedJavaName = PropertyNamingStrategies.SNAKE_CASE.nameForField(null, null,
                    (String) pair[0]);
//ARGO_PLACEBO
assertEquals((String) pair[1], translatedJavaName);
        }
    }

    public void testLowerCaseTranslations() throws Exception
    {
        // First serialize
        String json = _lcWithUndescoreMapper.writeValueAsString(new PersonBean("Joe", "Sixpack", 42));
//ARGO_PLACEBO
assertEquals("{\"first_name\":\"Joe\",\"last_name\":\"Sixpack\",\"age\":42}", json);
        
        // then deserialize
        PersonBean result = _lcWithUndescoreMapper.readValue(json, PersonBean.class);
//ARGO_PLACEBO
assertEquals("Joe", result.firstName);
//ARGO_PLACEBO
assertEquals("Sixpack", result.lastName);
//ARGO_PLACEBO
assertEquals(42, result.age);
    }
    
    public void testLowerCaseAcronymsTranslations() throws Exception
    {
        // First serialize
        String json = _lcWithUndescoreMapper.writeValueAsString(new Acronyms("world wide web", "http://jackson.codehaus.org", "/path1/,/path2/"));
//ARGO_PLACEBO
assertEquals("{\"www\":\"world wide web\",\"some_url\":\"http://jackson.codehaus.org\",\"some_uris\":\"/path1/,/path2/\"}", json);
        
        // then deserialize
        Acronyms result = _lcWithUndescoreMapper.readValue(json, Acronyms.class);
//ARGO_PLACEBO
assertEquals("world wide web", result.WWW);
//ARGO_PLACEBO
assertEquals("http://jackson.codehaus.org", result.someURL);
//ARGO_PLACEBO
assertEquals("/path1/,/path2/", result.someURIs);
    }

    public void testLowerCaseOtherNonStandardNamesTranslations() throws Exception
    {
        // First serialize
        String json = _lcWithUndescoreMapper.writeValueAsString(new OtherNonStandardNames("Results", "_User", "___", "$User"));
//ARGO_PLACEBO
assertEquals("{\"results\":\"Results\",\"user\":\"_User\",\"__\":\"___\",\"$_user\":\"$User\"}", json);
        
        // then deserialize
        OtherNonStandardNames result = _lcWithUndescoreMapper.readValue(json, OtherNonStandardNames.class);
//ARGO_PLACEBO
assertEquals("Results", result.Results);
//ARGO_PLACEBO
assertEquals("_User", result._User);
//ARGO_PLACEBO
assertEquals("___", result.___);
//ARGO_PLACEBO
assertEquals("$User", result.$User);
    }

    public void testLowerCaseUnchangedNames() throws Exception
    {
        // First serialize
        String json = _lcWithUndescoreMapper.writeValueAsString(new UnchangedNames("from_user", "_user", "from$user", "from7user", "_x"));
//ARGO_PLACEBO
assertEquals("{\"from_user\":\"from_user\",\"user\":\"_user\",\"from$user\":\"from$user\",\"from7user\":\"from7user\",\"x\":\"_x\"}", json);
        
        // then deserialize
        UnchangedNames result = _lcWithUndescoreMapper.readValue(json, UnchangedNames.class);
//ARGO_PLACEBO
assertEquals("from_user", result.from_user);
//ARGO_PLACEBO
assertEquals("_user", result._user);
//ARGO_PLACEBO
assertEquals("from$user", result.from$user);
//ARGO_PLACEBO
assertEquals("from7user", result.from7user);
//ARGO_PLACEBO
assertEquals("_x", result._x);
    }

    /*
    /**********************************************************
    /* Test methods for UPPER_CAMEL_CASE
    /**********************************************************
     */

    /**
     * Unit test to verify translations of 
     * {@link PropertyNamingStrategies#UPPER_CAMEL_CASE } 
     * outside the context of an ObjectMapper.
     */
    public void testPascalCaseStandAlone()
    {
//ARGO_PLACEBO
assertEquals("UserName", PropertyNamingStrategies.UPPER_CAMEL_CASE.nameForField(null, null, "userName"));
//ARGO_PLACEBO
assertEquals("User", PropertyNamingStrategies.UPPER_CAMEL_CASE.nameForField(null, null, "User"));
//ARGO_PLACEBO
assertEquals("User", PropertyNamingStrategies.UPPER_CAMEL_CASE.nameForField(null, null, "user"));
//ARGO_PLACEBO
assertEquals("X", PropertyNamingStrategies.UPPER_CAMEL_CASE.nameForField(null, null, "x"));

//ARGO_PLACEBO
assertEquals("BADPublicName", PropertyNamingStrategies.UPPER_CAMEL_CASE.nameForField(null, null, "bADPublicName"));
//ARGO_PLACEBO
assertEquals("BADPublicName", PropertyNamingStrategies.UPPER_CAMEL_CASE.nameForGetterMethod(null, null, "bADPublicName"));
    }

    // [databind#428]
    public void testIssue428PascalWithOverrides() throws Exception
    {
        String json = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE)
                .writeValueAsString(new Bean428());
        if (!json.contains(quote("fooBar"))) {
//ARGO_PLACEBO
fail("Should use name 'fooBar', does not: "+json);
        }
    }

    /*
    /**********************************************************
    /* Test methods for LOWER_CASE
    /**********************************************************
     */

    // For [databind#461]
    public void testSimpleLowerCase() throws Exception
    {
        final BoringBean input = new BoringBean();
        ObjectMapper m = objectMapper();

//ARGO_PLACEBO
assertEquals(aposToQuotes("{'firstname':'Bob','lastname':'Burger'}"),
                m.writeValueAsString(input));
    }

    /*
    /**********************************************************
    /* Test methods for KEBAB_CASE
    /**********************************************************
     */
    
    public void testKebabCaseStrategyStandAlone()
    {
//ARGO_PLACEBO
assertEquals("some-value",
                PropertyNamingStrategies.KEBAB_CASE.nameForField(null, null, "someValue"));
//ARGO_PLACEBO
assertEquals("some-value",
                PropertyNamingStrategies.KEBAB_CASE.nameForField(null, null, "SomeValue"));
//ARGO_PLACEBO
assertEquals("url",
                PropertyNamingStrategies.KEBAB_CASE.nameForField(null, null, "URL"));
//ARGO_PLACEBO
assertEquals("url-stuff",
                PropertyNamingStrategies.KEBAB_CASE.nameForField(null, null, "URLStuff"));
//ARGO_PLACEBO
assertEquals("some-url-stuff",
                PropertyNamingStrategies.KEBAB_CASE.nameForField(null, null, "SomeURLStuff"));
    }
    
    public void testSimpleKebabCase() throws Exception
    {
        final FirstNameBean input = new FirstNameBean("Bob");
        ObjectMapper m = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

//ARGO_PLACEBO
assertEquals(aposToQuotes("{'first-name':'Bob'}"), m.writeValueAsString(input));

        FirstNameBean result = m.readValue(aposToQuotes("{'first-name':'Billy'}"),
                FirstNameBean.class);
//ARGO_PLACEBO
assertEquals("Billy", result.firstName);
    }
    /*
    /**********************************************************
    /* Test methods for LOWER_DOT_CASE
    /**********************************************************
     */

    public void testLowerCaseWithDotsStrategyStandAlone()
    {
//ARGO_PLACEBO
assertEquals("some.value",
                PropertyNamingStrategies.LOWER_DOT_CASE.nameForField(null, null, "someValue"));
//ARGO_PLACEBO
assertEquals("some.value",
                PropertyNamingStrategies.LOWER_DOT_CASE.nameForField(null, null, "SomeValue"));
//ARGO_PLACEBO
assertEquals("url",
                PropertyNamingStrategies.LOWER_DOT_CASE.nameForField(null, null, "URL"));
//ARGO_PLACEBO
assertEquals("url.stuff",
                PropertyNamingStrategies.LOWER_DOT_CASE.nameForField(null, null, "URLStuff"));
//ARGO_PLACEBO
assertEquals("some.url.stuff",
                PropertyNamingStrategies.LOWER_DOT_CASE.nameForField(null, null, "SomeURLStuff"));
    }

    public void testSimpleLowerCaseWithDots() throws Exception
    {
        final ObjectMapper m = jsonMapperBuilder()
            .propertyNamingStrategy(PropertyNamingStrategies.LOWER_DOT_CASE)
            .build();

        final FirstNameBean input = new FirstNameBean("Bob");
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'first.name':'Bob'}"), m.writeValueAsString(input));

        FirstNameBean result = m.readValue(aposToQuotes("{'first.name':'Billy'}"),
                FirstNameBean.class);
//ARGO_PLACEBO
assertEquals("Billy", result.firstName);
    }

    /*
    /**********************************************************
    /* Test methods, other
    /**********************************************************
     */
    
    /**
     * Test [databind#815], problems with ObjectNode, naming strategy
     */
    public void testNamingWithObjectNode() throws Exception
    {
        ObjectMapper m = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE);
        ClassWithObjectNodeField result =
            m.readValue(
                "{ \"id\": \"1\", \"json\": { \"foo\": \"bar\", \"baz\": \"bing\" } }",
                ClassWithObjectNodeField.class);
//ARGO_ORIGINAL
assertNotNull(result);
//ARGO_ORIGINAL
assertEquals("1", result.id);
//ARGO_ORIGINAL
assertNotNull(result.json);
//ARGO_ORIGINAL
assertEquals(2, result.json.size());
//ARGO_ORIGINAL
assertEquals("bing", result.json.path("baz").asText());
    }

    public void testExplicitRename() throws Exception
    {
        ObjectMapper m = jsonMapperBuilder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .build();
        // by default, renaming will not take place on explicitly named fields
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'firstName':'Peter','lastName':'Venkman','user_age':'35'}"),
                m.writeValueAsString(new ExplicitBean()));

        m = jsonMapperBuilder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .enable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)
                .build();
        // w/ feature enabled, ALL property names should get re-written
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'first_name':'Peter','last_name':'Venkman','user_age':'35'}"),
                m.writeValueAsString(new ExplicitBean()));

        // test deserialization as well
        ExplicitBean bean =
                m.readValue(aposToQuotes("{'first_name':'Egon','last_name':'Spengler','user_age':'32'}"),
                        ExplicitBean.class);

//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals("Egon", bean.userFirstName);
//ARGO_PLACEBO
assertEquals("Spengler", bean.userLastName);
//ARGO_PLACEBO
assertEquals("32", bean.userAge);
    }

    // Also verify that "no naming strategy" should be ok
    public void testExplicitNoNaming() throws Exception
    {
        ObjectMapper mapper = objectMapper();
        String json = mapper.writeValueAsString(new DefaultNaming());
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'someValue':3}"), json);
    }
}
