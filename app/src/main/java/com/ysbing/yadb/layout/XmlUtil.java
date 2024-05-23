package com.ysbing.yadb.layout;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class XmlUtil {
    private static final Pattern unescapeXmlPattern = Pattern
            .compile("&((lt)|(gt)|(quot)|(apos)|(amp)|#(\\d+)|#x(\\p{XDigit}+));");

    public static String formatXml(String xml) {
        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            return XmlUtil.unescapeXml(res.getOutputStream().toString());
        } catch (Exception e) {
            return xml;
        }
    }

    private static String unescapeXml(String str) {
        return replaceAll(str, groups -> {
            if (groups[2] != null) {
                return "<";
            } else if (groups[3] != null) {
                return ">";
            } else if (groups[4] != null) {
                return "\"";
            } else if (groups[5] != null) {
                return "'";
            } else if (groups[6] != null) {
                return "&";
            } else if (groups[7] != null) {
                StringBuilder c = new StringBuilder();
                c.appendCodePoint(Integer.parseInt(groups[7]));
                return c.toString();
            } else if (groups[8] != null) {
                StringBuilder c = new StringBuilder();
                c.appendCodePoint(Integer.parseInt(groups[8], 16));
                return c.toString();
            }
            return "";
        });
    }

    private static String replaceAll(String str, Function<String[], String> replFunc) {
        StringBuffer sb = new StringBuffer();
        Matcher mat = XmlUtil.unescapeXmlPattern.matcher(str);
        while (mat.find()) {
            String[] greps = new String[mat.groupCount() + 1];
            for (int idx = 0; idx < mat.groupCount() + 1; idx++) {
                greps[idx] = mat.group(idx);
            }
            String repl = replFunc.apply(greps);
            mat.appendReplacement(sb, repl);
        }
        mat.appendTail(sb);
        return sb.toString();
    }

    public interface Function<T, R> {
        R apply(T input);
    }
}
