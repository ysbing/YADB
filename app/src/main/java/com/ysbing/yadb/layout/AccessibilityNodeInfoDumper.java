package com.ysbing.yadb.layout;

import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.view.DisplayInfo;
import android.view.accessibility.AccessibilityNodeInfo;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

/**
 * The AccessibilityNodeInfoDumper in Android Open Source Project contains a lot of bugs which will
 * stay in old android versions forever. By coping the code of the latest version it is ensured that
 * all patches become available on old android versions. <p/> down ported bugs are e.g. { @link
 * https://code.google.com/p/android/issues/detail?id=62906 } { @link
 * https://code.google.com/p/android/issues/detail?id=58733 }
 */
public class AccessibilityNodeInfoDumper {

    private static final String TAG = "AccessibilityNodeDumper";

    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{android.widget.GridView.class.getName(), android.widget.GridLayout.class.getName(), android.widget.ListView.class.getName(), android.widget.TableLayout.class.getName()};
    // XML 1.0 Legal Characters (http://stackoverflow.com/a/4237934/347155)
    // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    private static final Pattern XML10Pattern = Pattern.compile("[^" + "	\r\n" + " -\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]");

    /**
     * Using {@link AccessibilityNodeInfo} this method will walk the layout hierarchy and return
     * String object of xml hierarchy
     *
     * @param root The root accessibility node.
     */
    public static String getWindowXMLHierarchy(AccessibilityNodeInfo root, DisplayInfo displayInfo) {
        final long startTime = SystemClock.uptimeMillis();
        StringWriter xmlDump = new StringWriter();
        try {

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(xmlDump);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "hierarchy");

            if (root != null) {
                int width = displayInfo.logicalWidth;
                int height = displayInfo.logicalHeight;
                serializer.attribute("", "rotation", Integer.toString(displayInfo.rotation));
                dumpNodeRec(root, serializer, 0, width, height);
            } else {
                return "";
            }
            serializer.endTag("", "hierarchy");
            serializer.endDocument();
        } catch (IOException e) {
            Log.e(TAG, "failed to dump window to file", e);
        }
        final long endTime = SystemClock.uptimeMillis();
        Log.i(TAG, "Fetch time: " + (endTime - startTime) + "ms");
        return xmlDump.toString();
    }


    private static void dumpNodeRec(AccessibilityNodeInfo node, XmlSerializer serializer, int index, int width, int height) throws IOException {
        serializer.startTag("", "node");
        if (!nafExcludedClass(node) && !nafCheck(node))
            serializer.attribute("", "NAF", Boolean.toString(true));
        serializer.attribute("", "index", Integer.toString(index));
        serializer.attribute("", "text", safeCharSeqToString(node.getText()));
        serializer.attribute("", "class", safeCharSeqToString(node.getClassName()));
        serializer.attribute("", "package", safeCharSeqToString(node.getPackageName()));
        serializer.attribute("", "content-desc", safeCharSeqToString(node.getContentDescription()));
        serializer.attribute("", "checkable", Boolean.toString(node.isCheckable()));
        serializer.attribute("", "checked", Boolean.toString(node.isChecked()));
        serializer.attribute("", "clickable", Boolean.toString(node.isClickable()));
        serializer.attribute("", "enabled", Boolean.toString(node.isEnabled()));
        serializer.attribute("", "focusable", Boolean.toString(node.isFocusable()));
        serializer.attribute("", "focused", Boolean.toString(node.isFocused()));
        serializer.attribute("", "scrollable", Boolean.toString(node.isScrollable()));
        serializer.attribute("", "long-clickable", Boolean.toString(node.isLongClickable()));
        serializer.attribute("", "password", Boolean.toString(node.isPassword()));
        serializer.attribute("", "selected", Boolean.toString(node.isSelected()));
        serializer.attribute("", "bounds", AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(node, width, height).toShortString());
        serializer.attribute("", "resource-id", safeCharSeqToString(node.getViewIdResourceName()));

        node.refresh();
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (child.isVisibleToUser()) {
                    dumpNodeRec(child, serializer, i, width, height);
                    child.recycle();
                } else {
                    Log.i(TAG, String.format("Skipping invisible child: %s", child.toString()));
                }
            } else {
                Log.i(TAG, String.format("Null child %d/%d, parent: %s", i, count, node.toString()));
            }
        }
        serializer.endTag("", "node");
    }

    /**
     * The list of classes to exclude my not be complete. We're attempting to only reduce noise from
     * standard layout classes that may be falsely configured to accept clicks and are also
     * enabled.
     *
     * @return true if node is excluded.
     */
    private static boolean nafExcludedClass(AccessibilityNodeInfo node) {
        String className = safeCharSeqToString(node.getClassName());
        for (String excludedClassName : NAF_EXCLUDED_CLASSES) {
            if (className.endsWith(excludedClassName)) return true;
        }
        return false;
    }

    /**
     * We're looking for UI controls that are enabled, clickable but have no text nor
     * content-description. Such controls configuration indicate an interactive control is present
     * in the UI and is most likely not accessibility friendly. We refer to such controls here as
     * NAF controls (Not Accessibility Friendly)
     *
     * @return false if a node fails the check, true if all is OK
     */
    private static boolean nafCheck(AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled() && safeCharSeqToString(node.getContentDescription()).isEmpty() && safeCharSeqToString(node.getText()).isEmpty();
        if (!isNaf) return true;
        // check children since sometimes the containing element is clickable
        // and NAF but a child's text or description is available. Will assume
        // such layout as fine.
        return childNafCheck(node);
    }

    /**
     * This should be used when it's already determined that the node is NAF and a further check of
     * its children is in order. A node maybe a container such as LinerLayout and may be set to be
     * clickable but have no text or content description but it is counting on one of its children
     * to fulfill the requirement for being accessibility friendly by having one or more of its
     * children fill the text or content-description. Such a combination is considered by this
     * dumper as acceptable for accessibility.
     *
     * @return false if node fails the check.
     */
    private static boolean childNafCheck(AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();
        for (int x = 0; x < childCount; x++) {
            AccessibilityNodeInfo childNode = node.getChild(x);
            if (childNode == null) {
                Log.i(TAG, String.format("Null child %d/%d, parent: %s", x, childCount, node));
                continue;
            }
            if (!safeCharSeqToString(childNode.getContentDescription()).isEmpty() || !safeCharSeqToString(childNode.getText()).isEmpty())
                return true;
            if (childNafCheck(childNode)) return true;
        }
        return false;
    }

    private static String safeCharSeqToString(CharSequence cs) {
        if (cs == null) return "";
        else {
            return stripInvalidXMLChars(escapeXml(cs.toString()));
        }
    }

    private static String escapeXml(String input) {
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&apos;");
                    break;
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '&':
                    escaped.append("&amp;");
                    break;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }

    // Original Google code here broke UTF characters
    private static String stripInvalidXMLChars(CharSequence charSequence) {
        return XML10Pattern.matcher(charSequence).replaceAll("?");
    }
}
