package com.ysbing.yadb;

import android.os.Looper;

import com.ysbing.yadb.input.Keyboard;
import com.ysbing.yadb.input.LongPressDrag;
import com.ysbing.yadb.input.Pinch;
import com.ysbing.yadb.input.Swipe;
import com.ysbing.yadb.input.Touch;
import com.ysbing.yadb.layout.Layout;
import com.ysbing.yadb.screenshot.Screenshot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Map<String, Command> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("-keyboard", args -> Keyboard.run(args[1]));
        COMMANDS.put("-touch", args -> {
            float x = Float.parseFloat(args[1]);
            float y = Float.parseFloat(args[2]);
            long duration = args.length == 4 ? Long.parseLong(args[3]) : -1L;
            Touch.run(x, y, duration);
        });
        COMMANDS.put("-layout", args -> Layout.run(args.length == 2 ? args[1] : null));
        COMMANDS.put("-screenshot", args -> Screenshot.run(args.length == 2 ? args[1] : null));
        COMMANDS.put("-readClipboard", args -> Keyboard.readClipboard());
        COMMANDS.put("-writeClipboard", args -> Keyboard.writeClipboard(args[1]));
        COMMANDS.put("-swipe", args -> {
            System.out.println("swipe");
            float x1 = Float.parseFloat(args[1]);
            float y1 = Float.parseFloat(args[2]);
            float x2 = Float.parseFloat(args[3]);
            float y2 = Float.parseFloat(args[4]);
            long duration = Long.parseLong(args[5]);
            Swipe.run(x1, y1, x2, y2, duration);
        });
        COMMANDS.put("-longPressDrag", args -> {
            System.out.println("longPressDrag");
            float x1 = Float.parseFloat(args[1]);
            float y1 = Float.parseFloat(args[2]);
            float x2 = Float.parseFloat(args[3]);
            float y2 = Float.parseFloat(args[4]);
            long pressDuration = Long.parseLong(args[5]);
            long dragDuration = Long.parseLong(args[6]);
            LongPressDrag.run(x1, y1, x2, y2, pressDuration, dragDuration);
        });
        COMMANDS.put("-pinch", args -> {
            float centerX = Float.parseFloat(args[1]);
            float centerY = Float.parseFloat(args[2]);
            float startDist = Float.parseFloat(args[3]);
            float endDist = Float.parseFloat(args[4]);
            long duration = Long.parseLong(args[5]);
            Pinch.run(centerX, centerY, startDist, endDist, duration);
        });
    }


    public static void main(String[] args) {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, e) ->
                    System.out.println(t.getName() + ", UncaughtException: " + getStackTraceAsString(e))
            );
            Looper.prepareMainLooper();

            if (args.length == 0 || !COMMANDS.containsKey(args[0])) {
                System.out.println("Invalid argument");
                return;
            }

            COMMANDS.get(args[0]).execute(args);

        } catch (Throwable e) {
            System.out.println("MainException: " + getStackTraceAsString(e));
        }
    }

    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
        }
        return stringWriter.toString();
    }


    private interface Command {
        void execute(String[] args) throws Exception;
    }
}
