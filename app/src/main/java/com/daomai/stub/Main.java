package com.daomai.stub;

import android.os.Looper;

import com.daomai.stub.DMClipboard;
import com.daomai.stub.Touch;
import com.daomai.stub.DMDump;
import com.daomai.stub.Screenshot;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Main {
    public static final String PACKAGE_NAME = "com.android.shell";
    public static final int USER_ID = 0;
    private static final String ARG_TOUCH = "-touch";
    private static final String ARG_LAYOUT = "-layout";
    private static final String ARG_SCREENSHOT = "-screenshot";
    private static final String ARG_READ_CLIPBOARD = "-readClipboard";

    public static void main(String[] args) {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.out.println(t.getName() + ",UncaughtException:" + getStackTraceAsString(e)));
            Looper.prepareMainLooper();
            if (check(args[0])) {
                switch (args[0]) {
                    case ARG_TOUCH:
                        if (args.length == 6) {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Long.parseLong(args[3]), Integer.parseInt(args[4]), Long.parseLong(args[5]));
                        } else if (args.length == 5) {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Long.parseLong(args[3]), Integer.parseInt(args[4]), 100);
                        } else if (args.length == 4) {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Long.parseLong(args[3]), 1, 100); // Mặc định 1 lần nhấn
                        } else {
                            Touch.run(Float.parseFloat(args[1]), Float.parseFloat(args[2]), -1L, 1, 100); // Mặc định 1 lần nhấn
                        }
                        break;
                    case ARG_LAYOUT:
                        if (args.length == 2) {
                            DMDump.run(args[1]);
                        } else {
                            DMDump.run(null);
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
                        DMClipboard.readClipboard();
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("\n\nKhônng có đối số phù hợp\n");
                System.out.println("+  Chào mừng bạn đến với tool Đào Mai  +\n");

                System.out.println("- Lệnh chụp ảnh màn hình");
                System.out.println("-   com.daomai.stub.Main -screenshot");

                System.out.println("- Lệnh Dum màn hình");
                System.out.println("-   com.daomai.stub.Main -layout");

                System.out.println("- Lệnh đọc bộ nhớ đệm");
                System.out.println("-   com.daomai.stub.Main -readClipboard");

                System.out.println("- Lệnh nhấn điểm ảnh liên tục");
                System.out.println("-    com.daomai.stub.Main -touch x1 y1 t1 n1 n2");
                System.out.println("-        x1 : toạ độ x");
                System.out.println("-        x1 : toạ độ y");
                System.out.println("-        t1 : (tính bằng ms) thời gian delay lúc nhấn và thả");
                System.out.println("-        n1  :  số lần nhấn nếu để trống mặc định là 1, thời gian giữa 1 lần nhấn mặc định là 100ms");
                System.out.println("-        n2  : (tính bằng ms) thời gian delay sau 1 lần click");
                System.out.println("\n\n");

            }
        } catch (Throwable e) {
            System.out.println("MainException:" + getStackTraceAsString(e));
        }
    }

    private static boolean check(String arg) {
        return arg.equals(ARG_TOUCH) || arg.equals(ARG_LAYOUT) || arg.equals(ARG_SCREENSHOT) || arg.equals(ARG_READ_CLIPBOARD);
    }

    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}