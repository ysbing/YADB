package com.ysbing.yadb;

import android.os.Looper;

import com.ysbing.yadb.input.Keyboard;
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
