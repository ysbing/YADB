package com.daomai.stub;

import android.content.Context;
import android.hardware.display.IDisplayManager;
import android.view.Display;
import android.view.DisplayInfo;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.concurrent.TimeoutException;
import android.os.SystemClock;
import android.util.Log;
import android.graphics.Rect;
import java.util.regex.Pattern;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DMDump {
    public static void run(String full) throws Exception {
        DisplayInfo displayInfo = getDisplayInfo();
        if (displayInfo == null) {
            return;
        }
        LayoutShell.get(displayInfo, full);
    }

    private static DisplayInfo getDisplayInfo() {
        IDisplayManager clipboard = IDisplayManager.Stub.asInterface(android.os.ServiceManager.getService(Context.DISPLAY_SERVICE));
        if (clipboard == null) {
            return null;
        }
        return clipboard.getDisplayInfo(Display.DEFAULT_DISPLAY);
    }
}


class LayoutShell {
    private static final String HANDLER_THREAD_NAME = "LayoutShellThread";
    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
    private UiAutomation mUiAutomation = null;

    public static void get(DisplayInfo displayInfo, String full) throws Exception {
        LayoutShell shell = new LayoutShell();
        try {
            shell.connect();
            AccessibilityNodeInfo info;
            long startTime = System.currentTimeMillis();
            do {
                if (System.currentTimeMillis() - startTime >= 3000) {
                    throw new TimeoutException();
                }
                info = shell.mUiAutomation.getRootInActiveWindow();
            } while (info == null);
            String content = AccessibilityNodeInfoDumper.getWindowJSONHierarchy(info, displayInfo, full);
            System.out.println(content);
        } finally {
            shell.disconnect();
        }
    }

    public void connect() {
        if (mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already connected!");
        }
        mHandlerThread.start();
        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(), new UiAutomationConnection());
        mUiAutomation.connect();
    }

    public void disconnect() {
        if (!mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already disconnected!");
        }
        mUiAutomation.disconnect();
        mHandlerThread.quit();
    }
}


class AccessibilityNodeInfoDumper {

    private static final String TAG = "AccessibilityNodeDumper";

    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{
            android.widget.GridView.class.getName(),
            android.widget.GridLayout.class.getName(),
            android.widget.ListView.class.getName(),
            android.widget.TableLayout.class.getName()
    };
    private static final Pattern XML10Pattern = Pattern.compile("[^" + "\t\r\n" + " -\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]");

    public static String getWindowJSONHierarchy(AccessibilityNodeInfo root, DisplayInfo displayInfo, String full) {
        final long startTime = SystemClock.uptimeMillis();
        JSONArray jsonArray = new JSONArray();
        try {
            if (root != null) {
                int width = displayInfo.logicalWidth;
                int height = displayInfo.logicalHeight;
                JSONObject nodeJson = new JSONObject();
                nodeJson.put("width", Integer.toString(width));
                nodeJson.put("height", Integer.toString(height));
                nodeJson.put("rotation", Integer.toString(displayInfo.rotation));
                jsonArray.put(nodeJson);
                collectNodes(root, jsonArray, 0, width, height, full);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to dump window to JSON", e);
        }
        final long endTime = SystemClock.uptimeMillis();
        Log.i(TAG, "Fetch time: " + (endTime - startTime) + "ms");
        return jsonArray.toString().replace("\\/", "/");
    }

    private static void collectNodes(AccessibilityNodeInfo node, JSONArray jsonArray, int index, int width, int height, String full) throws JSONException {
        JSONObject nodeJson = new JSONObject();
        if (!nafExcludedClass(node) && !nafCheck(node))
            nodeJson.put("NAF", true);

        nodeJson.put("index", index);
        nodeJson.put("class", safeCharSeqToString(node.getClassName()));
        nodeJson.put("package", safeCharSeqToString(node.getPackageName()));
        if (full != null) {
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
        }
        nodeJson.put("resource-id", safeCharSeqToString(node.getViewIdResourceName()));


        // xử lý tọa độ và chuyển sang click
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);
        Rect displayRect = new Rect();
        displayRect.top = 0;
        displayRect.left = 0;
        displayRect.right = width;
        displayRect.bottom = height;
        if (!nodeRect.intersect(displayRect)) {
            nodeRect = new Rect();
        }
        int x1 = nodeRect.left;
        int y1 = nodeRect.top;
        int x2 = nodeRect.right;
        int y2 = nodeRect.bottom;

        nodeJson.put("bounds", String.format("%s %s,%s %s", Integer.toString(x1), Integer.toString(y1), Integer.toString(x2), Integer.toString(y2)));
        
        // click vào giũa
        int xCenter = (x1 + x2) / 2;
        int yCenter = (y1 + y2) / 2;
        nodeJson.put("click", String.format("%s %s", Integer.toString(xCenter), Integer.toString(yCenter)));

        // click random
        Random random = new Random();
        int xrd = random.nextInt(x2 - x1 + 1) + x1;
        int yrd = random.nextInt(y2 - y1 + 1) + y1;
        nodeJson.put("click_random", String.format("%s %s", Integer.toString(xrd), Integer.toString(yrd)));

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
                    collectNodes(child, jsonArray, i, width, height, full);
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