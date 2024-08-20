package com.daomai.stub;

import android.os.Looper;

import com.daomai.stub.input.Keyboard;
import com.daomai.stub.input.Touch;
import com.daomai.stub.layout.Layout;
import com.daomai.stub.screenshot.Screenshot;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Main {
    public static final String PACKAGE_NAME = "com.android.shell";
    public static final int USER_ID = 0;
    private static final String ARG_KEY_BOARD = "-keyboard";
    private static final String ARG_TOUCH = "-touch";
    private static final String ARG_LAYOUT = "-Dump";
    private static final String ARG_SCREENSHOT = "-Screen";
    private static final String ARG_READ_CLIPBOARD = "-readClipboard";

    public static void main(String[] args) {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.out.println(t.getName() + ",UncaughtException:" + getStackTraceAsString(e)));
            Looper.prepareMainLooper();
            if (check(args[0])) {
                switch (args[0]) {
                    case ARG_KEY_BOARD:
                        Keyboard.run(args[1]);
                        break;
                    case ARG_TOUCH:
                        if (args.length == 4) {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Long.parseLong(args[3]));
                        } else {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), -1L);
                        }
                        break;
                    case ARG_LAYOUT:
                        if (args.length == 2) {
                            Layout.run(args[1]);
                        } else {
                            Layout.run(null);
                        }
                        break;
                    case ARG_SCREENSHOT:
                        if (args.length == 2) {
                            Screenshot.run(args[1]);
                        } else {
                            Screenshot.run(null);
                        }
                        break;
                    case ARG_READ_CLIPBOARD:
                        Keyboard.readClipboard();
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("Invalid argument");
            }
        } catch (Throwable e) {
            System.out.println("MainException:" + getStackTraceAsString(e));
        }
    }

    private static boolean check(String arg) {
        return arg.equals(ARG_KEY_BOARD) || arg.equals(ARG_TOUCH) || arg.equals(ARG_LAYOUT) || arg.equals(ARG_SCREENSHOT) || arg.equals(ARG_READ_CLIPBOARD);
    }

    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}
