package com.ysbing.yadb;

import java.util.Arrays;

public class Main {
    private static final String ARG_KEY_BOARD = "-keyboard";
    private static final String ARG_TOUCH = "-touch";
    private static final String ARG_LAYOUT = "-layout";
    private static final String ARG_SCREENSHOT = "-screenshot";

    public static void main(String[] args) {
        try {
            System.out.println("yadb:" + Arrays.toString(args));
            if (check(args[0])) {
                switch (args[0]) {
                    case ARG_KEY_BOARD:
                        Server.keyboard(args[1]);
                        break;
                    case ARG_TOUCH:
                        if (args.length == 4) {
                            Server.touch(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Long.parseLong(args[3]));
                        } else {
                            Server.touch(Float.parseFloat(args[1]), Float.parseFloat(args[2]), -1L);
                        }
                        break;
                    case ARG_LAYOUT:
                        if (args.length == 2) {
                            Server.layout(args[1]);
                        } else {
                            Server.layout(null);
                        }
                        break;
                    case ARG_SCREENSHOT:
                        if (args.length == 2) {
                            Server.screenshot(args[1]);
                        } else {
                            Server.screenshot(null);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("参数不对");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean check(String arg) {
        return arg.equals(ARG_KEY_BOARD) || arg.equals(ARG_TOUCH) || arg.equals(ARG_LAYOUT) || arg.equals(ARG_SCREENSHOT);
    }
}
