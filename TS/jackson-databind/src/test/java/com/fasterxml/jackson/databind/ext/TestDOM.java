package com.fasterxml.jackson.databind.ext;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;
import org.w3c.dom.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDOM extends com.fasterxml.jackson.databind.BaseMapTest
{
    final static String SIMPLE_XML =
        "<root attr='3'><leaf>Rock &amp; Roll!</leaf><?proc instr?></root>";
    final static String SIMPLE_XML_NS =
        "<root ns:attr='abc' xmlns:ns='http://foo' />";

    private final ObjectMapper MAPPER = new ObjectMapper();
    
    public void testSerializeSimpleNonNS() throws Exception
    {
        // Let's just parse first, easiest
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse
            (new InputSource(new StringReader(SIMPLE_XML)));
//ARGO_PLACEBO
assertNotNull(doc);
        // need to strip xml declaration, if any
        String outputRaw = MAPPER.writeValueAsString(doc);
        // And re-parse as String, since JSON has quotes...
        String output = MAPPER.readValue(outputRaw, String.class);
        /* ... and finally, normalize to (close to) canonical XML
         * output (single vs double quotes, xml declaration etc)
         */
//ARGO_PLACEBO
assertEquals(SIMPLE_XML, normalizeOutput(output));
    }

    public void testDeserializeNonNS() throws Exception
    {
        for (int i = 0; i < 2; ++i) {
            Document doc;

            if (i == 0) {
                // First, as Document:
                doc = MAPPER.readValue(quote(SIMPLE_XML), Document.class);
            } else {
                // and then as plain Node (no difference)
                Node node = MAPPER.readValue(quote(SIMPLE_XML), Node.class);
                doc = (Document) node;
            }
            Element root = doc.getDocumentElement();
//ARGO_PLACEBO
assertNotNull(root);
            // non-ns, simple...
//ARGO_PLACEBO
assertEquals("root", root.getTagName());
//ARGO_PLACEBO
assertEquals("3", root.getAttribute("attr"));
//ARGO_PLACEBO
assertEquals(1, root.getAttributes().getLength());
            NodeList nodes = root.getChildNodes();
//ARGO_PLACEBO
assertEquals(2, nodes.getLength());
            Element leaf = (Element) nodes.item(0);
//ARGO_PLACEBO
assertEquals("leaf", leaf.getTagName());
//ARGO_PLACEBO
assertEquals(0, leaf.getAttributes().getLength());
            //"<root attr='3'><leaf>Rock &amp; Roll!</leaf><?proc instr?></root>";
            ProcessingInstruction pi = (ProcessingInstruction) nodes.item(1);
//ARGO_PLACEBO
assertEquals("proc", pi.getTarget());
//ARGO_PLACEBO
assertEquals("instr", pi.getData());
        }
    }
    
    public void testDeserializeNS() throws Exception
    {
        Document doc = MAPPER.readValue(quote(SIMPLE_XML_NS), Document.class);
        Element root = doc.getDocumentElement();
//ARGO_PLACEBO
assertNotNull(root);
//ARGO_PLACEBO
assertEquals("root", root.getTagName());
        // Not sure if it ought to be "" or null...
        String uri = root.getNamespaceURI();
//ARGO_PLACEBO
assertTrue((uri == null) || "".equals(uri));
        // no child nodes:
//ARGO_PLACEBO
assertEquals(0, root.getChildNodes().getLength());
        // DOM is weird, includes ns decls as attributes...
//ARGO_PLACEBO
assertEquals(2, root.getAttributes().getLength());
//ARGO_PLACEBO
assertEquals("abc", root.getAttributeNS("http://foo", "attr"));
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected static String normalizeOutput(String output)
    {
        // XML declaration to get rid of?
        output = output.trim();
        if (output.startsWith("<?xml")) {
            // can find closing '>' of xml decl...
            output = output.substring(output.indexOf('>')+1).trim();
        }
        // And replace double quotes with single-quotes...
        return output.replace('"', '\'');
    }
}
