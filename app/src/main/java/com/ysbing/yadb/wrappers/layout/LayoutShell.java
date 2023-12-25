package com.ysbing.yadb.wrappers.layout;

import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.view.DisplayInfo;
import android.view.accessibility.AccessibilityNodeInfo;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeoutException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class LayoutShell {
    private static final String HANDLER_THREAD_NAME = "LayoutShellThread";
    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
    private UiAutomation mUiAutomation = null;

    public static void get(File file, DisplayInfo displayInfo) throws Exception {
        LayoutShell shell = new LayoutShell();
        try {
            shell.connect();
            AccessibilityNodeInfo info;
            long startTime = System.currentTimeMillis();
            do {
                if (System.currentTimeMillis() - startTime >= 5000) {
                    throw new TimeoutException();
                }
                info = shell.mUiAutomation.getRootInActiveWindow();
            } while (info == null);
            String content = AccessibilityNodeInfoDumper.getWindowXMLHierarchy(info, displayInfo);
            FileWriter writer = new FileWriter(file);
            writer.write(formatXml(content));
            writer.close();
            System.out.println("layout dumped to:" + file.getAbsolutePath());
        } finally {
            try {
                shell.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String formatXml(String xml) {
        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            return res.getOutputStream().toString();
        } catch (Exception e) {
            return xml;
        }
    }

    public void connect() {
        if (mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already connected!");
        }
        mHandlerThread.start();
        mUiAutomation = new UiAutomation(mHandlerThread.getLooper(), new UiAutomationConnection());
        int connectionId = 0;
        try {
            connectionId = mUiAutomation.getConnectionId();
        } catch (IllegalStateException ignored) {
        }
        if (connectionId != 2) {
            mUiAutomation.connect();
        }
    }

    public void disconnect() {
        if (!mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already disconnected!");
        }
        mUiAutomation.disconnect();
        mHandlerThread.quit();
    }
}
