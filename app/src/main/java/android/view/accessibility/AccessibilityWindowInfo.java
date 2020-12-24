package android.view.accessibility;

import com.ysbing.yrouter.api.YRouterSystem;

@YRouterSystem
public final class AccessibilityWindowInfo {

    private static final boolean DEBUG = false;

    /**
     * Window type: This is an application window. Such a window shows UI for
     * interacting with an application.
     */
    public static final int TYPE_APPLICATION = 1;

    /**
     * Window type: This is an input method window. Such a window shows UI for
     * inputting text such as keyboard, suggestions, etc.
     */
    public static final int TYPE_INPUT_METHOD = 2;

    /**
     * Window type: This is an system window. Such a window shows UI for
     * interacting with the system.
     */
    public static final int TYPE_SYSTEM = 3;

    /**
     * Window type: Windows that are overlaid <em>only</em> by an {@link
     * android.accessibilityservice.AccessibilityService} for interception of
     * user interactions without changing the windows an accessibility service
     * can introspect. In particular, an accessibility service can introspect
     * only windows that a sighted user can interact with which they can touch
     * these windows or can type into these windows. For example, if there
     * is a full screen accessibility overlay that is touchable, the windows
     * below it will be introspectable by an accessibility service regardless
     * they are covered by a touchable window.
     */
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;

    /**
     * Window type: A system window used to divide the screen in split-screen mode.
     * This type of window is present only in split-screen mode.
     */
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;

    /* Special values for window IDs */
    /**
     * @hide
     */
    public static final int ACTIVE_WINDOW_ID = Integer.MAX_VALUE;
}