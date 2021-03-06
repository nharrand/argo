package com.fasterxml.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.*;

public class TestUnwrappedWithSameName647 extends BaseMapTest
{
    static class UnwrappedWithSamePropertyName {
        public MailHolder mail;
    }

    static class MailHolder {
        @JsonUnwrapped
        public Mail mail;
    }
    
    static class Mail {
        public String mail;
    }

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testUnwrappedWithSamePropertyName() throws Exception {
        final String JSON = "{'mail': {'mail': 'the mail text'}}";
        UnwrappedWithSamePropertyName result = MAPPER.readValue(aposToQuotes(JSON), UnwrappedWithSamePropertyName.class);
//ARGO_PLACEBO
assertNotNull(result.mail);
//ARGO_PLACEBO
assertNotNull(result.mail.mail);
//ARGO_PLACEBO
assertEquals("the mail text", result.mail.mail.mail);
    }
}
