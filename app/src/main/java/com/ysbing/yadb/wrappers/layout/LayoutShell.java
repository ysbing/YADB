package com.ysbing.yadb.wrappers.layout;

import android.annotation.SuppressLint;
import android.app.IUiAutomationConnection;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ysbing.yadb.DisplayInfo;

import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream; 
import java.io.ByteArrayOutputStream; 
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

@SuppressLint("SoonBlockedPrivateApi")
public class LayoutShell {
    private static final String HANDLER_THREAD_NAME = "LayoutShellThread";
    private final HandlerThread mHandlerThread = new HandlerThread(HANDLER_THREAD_NAME);
    private Method mConnectMethod = null;
    private Method mDisconnectMethod = null;
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
            return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch (Exception e) {
            return xml;
        }
    }
    
    public void connect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already connected!");
        }
        mHandlerThread.start();
        mUiAutomation = UiAutomation.class.getConstructor(Looper.class, IUiAutomationConnection.class).newInstance(mHandlerThread.getLooper(), new UiAutomationConnection());
        getConnectMethod().invoke(mUiAutomation);
    }

    public void disconnect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!mHandlerThread.isAlive()) {
            throw new IllegalStateException("Already disconnected!");
        }
        getDisConnectMethod().invoke(mUiAutomation);
        mHandlerThread.quit();
    }

    private Method getConnectMethod() throws NoSuchMethodException {
        if (mConnectMethod == null) {
            mConnectMethod = UiAutomation.class.getDeclaredMethod("connect");
        }
        return mConnectMethod;
    }

    private Method getDisConnectMethod() throws NoSuchMethodException {
        if (mDisconnectMethod == null) {
            mDisconnectMethod = UiAutomation.class.getDeclaredMethod("disconnect");
        }
        return mDisconnectMethod;
    }
}
