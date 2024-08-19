package com.daomai.stub.layout;

import android.os.SystemClock;
import android.util.Log;
import android.view.DisplayInfo;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The AccessibilityNodeInfoDumper in Android Open Source Project contains a lot of bugs which will
 * stay in old android versions forever. By coping the code of the latest version it is ensured that
 * all patches become available on old android versions. <p/> down ported bugs are e.g. { @link
 * https://code.google.com/p/android/issues/detail?id=62906 } { @link
 * https://code.google.com/p/android/issues/detail?id=58733 }
 */
public class AccessibilityNodeInfoDumper {

    private static final String TAG = "AccessibilityNodeDumper";

    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{
            android.widget.GridView.class.getName(),
            android.widget.GridLayout.class.getName(),
            android.widget.ListView.class.getName(),
            android.widget.TableLayout.class.getName()
    };
    private static final Pattern XML10Pattern = Pattern.compile("[^" + "\t\r\n" + " -\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]");

    public static String getWindowJSONHierarchy(AccessibilityNodeInfo root, DisplayInfo displayInfo) {
        final long startTime = SystemClock.uptimeMillis();
        JSONArray jsonArray = new JSONArray();
        try {
            if (root != null) {
                int width = displayInfo.logicalWidth;
                int height = displayInfo.logicalHeight;
                collectNodes(root, jsonArray,0, width, height);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to dump window to JSON", e);
        }
        final long endTime = SystemClock.uptimeMillis();
        Log.i(TAG, "Fetch time: " + (endTime - startTime) + "ms");
        return jsonArray.toString();
    }

    private static void collectNodes(AccessibilityNodeInfo node, JSONArray jsonArray, int index, int width, int height) throws JSONException {
        JSONObject nodeJson = new JSONObject();
        if (!nafExcludedClass(node) && !nafCheck(node))
            nodeJson.put("NAF", true);

        nodeJson.put("index", index);
        nodeJson.put("class", safeCharSeqToString(node.getClassName()));
        nodeJson.put("package", safeCharSeqToString(node.getPackageName()));
        nodeJson.put("checkable",Boolean.toString(node.isCheckable()));
        nodeJson.put("checked", Boolean.toString(node.isChecked()));
        nodeJson.put("clickable", Boolean.toString(node.isClickable()));
        nodeJson.put("enabled", Boolean.toString(node.isEnabled()));
        nodeJson.put("focusable", Boolean.toString(node.isFocusable()));
        nodeJson.put("focused", Boolean.toString(node.isFocused()));
        nodeJson.put("scrollable", Boolean.toString(node.isScrollable()));
        nodeJson.put("long-clickable", Boolean.toString(node.isLongClickable()));
        nodeJson.put("password", Boolean.toString(node.isPassword()));
        nodeJson.put("selected", Boolean.toString(node.isSelected()));
        nodeJson.put("resource-id", safeCharSeqToString(node.getViewIdResourceName()));

        // xử lý tọa độ và chuyển sang click
        String coordinates = AccessibilityNodeInfoHelper.getVisibleBoundsInScreen(node, width, height).toShortString().replace("][", ",").replace("[", "").replace("]", "");
        String[] parts = coordinates.split(",");
        int x1 = Integer.parseInt(parts[0]);
        int y1 = Integer.parseInt(parts[1]);
        int x2 = Integer.parseInt(parts[2]);
        int y2 = Integer.parseInt(parts[3]);
        int xCenter = (x1 + x2) / 2;
        int yCenter = (y1 + y2) / 2;

        nodeJson.put("bounds", coordinates);

        nodeJson.put("click", String.format("%s %s", Integer.toString(xCenter), Integer.toString(yCenter)));


        // sửa lỗi text convent text sang dạng khác
        String _text = node.getText() != null ? node.getText().toString()
                        .replace("&", "&amp;")
                        .replace("\"", "&quot;")
                        .replace("\n", "&#10;")
                        .replace("\r", "&#13;")
                        .replace("\t", "&#9;")
                        : "";
        nodeJson.put("text", _text);


        String _ContentDescription = node.getContentDescription() != null ? node.getContentDescription().toString()
                        .replace("&", "&amp;")
                        .replace("\"", "&quot;")
                        .replace("\n", "&#10;")
                        .replace("\r", "&#13;")
                        .replace("\t", "&#9;")
                        : "";
        nodeJson.put("content-desc", _ContentDescription);
                        



        jsonArray.put(nodeJson);

        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (child.isVisibleToUser()) {
                    collectNodes(child, jsonArray, i, width, height);
                    child.recycle();
                } else {
                    Log.i(TAG, String.format("Skipping invisible child: %s", child.toString()));
                }
            } else {
                Log.i(TAG, String.format("Null child %d/%d, parent: %s", i, count, node.toString()));
            }
        }
    }

    private static boolean nafExcludedClass(AccessibilityNodeInfo node) {
        String className = safeCharSeqToString(node.getClassName());
        for (String excludedClassName : NAF_EXCLUDED_CLASSES) {
            if (className.endsWith(excludedClassName)) return true;
        }
        return false;
    }

    private static boolean nafCheck(AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled() && safeCharSeqToString(node.getContentDescription()).isEmpty() && safeCharSeqToString(node.getText()).isEmpty();
        if (!isNaf) return true;
        return childNafCheck(node);
    }

    private static boolean childNafCheck(AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();
        for (int x = 0; x < childCount; x++) {
            AccessibilityNodeInfo childNode = node.getChild(x);
            if (childNode == null) {
                Log.i(TAG, String.format("Null child %d/%d, parent: %s", x, childCount, node.toString()));
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
            return stripInvalidXMLChars(cs);
        }
    }

    private static String stripInvalidXMLChars(CharSequence charSequence) {
        return XML10Pattern.matcher(charSequence).replaceAll("?");
    }
}