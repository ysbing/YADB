package android.view.accessibility;

public class AccessibilityNodeInfo {

    private static final boolean DEBUG = false;

    private static final String TAG = "AccessibilityNodeInfo";

    /**
     * @hide
     */
    public static final int UNDEFINED_CONNECTION_ID = -1;

    /**
     * @hide
     */
    public static final int UNDEFINED_SELECTION_INDEX = -1;

    /**
     * @hide
     */
    public static final int UNDEFINED_ITEM_ID = Integer.MAX_VALUE;

    /**
     * @hide
     */
    public static final int ROOT_ITEM_ID = Integer.MAX_VALUE - 1;

    /**
     * @hide
     */
    public static final long UNDEFINED_NODE_ID = 0L;

    /**
     * @hide
     */
    public static final long ROOT_NODE_ID = 0L;

    /**
     * @hide
     */
    public static final int FLAG_PREFETCH_PREDECESSORS = 0x00000001;

    /**
     * @hide
     */
    public static final int FLAG_PREFETCH_SIBLINGS = 0x00000002;

    /**
     * @hide
     */
    public static final int FLAG_PREFETCH_DESCENDANTS = 0x00000004;

    /**
     * @hide
     */
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 0x00000008;

    /**
     * @hide
     */
    public static final int FLAG_REPORT_VIEW_IDS = 0x00000010;

    // Actions.

    /**
     * Action that gives input focus to the node.
     */
    public static final int ACTION_FOCUS = 0x00000001;

    /**
     * Action that clears input focus of the node.
     */
    public static final int ACTION_CLEAR_FOCUS = 0x00000002;

    /**
     * Action that selects the node.
     */
    public static final int ACTION_SELECT = 0x00000004;

    /**
     * Action that deselects the node.
     */
    public static final int ACTION_CLEAR_SELECTION = 0x00000008;

    /**
     * Action that clicks on the node info.
     * <p>
     * See {@link AccessibilityAction#ACTION_CLICK}
     */
    public static final int ACTION_CLICK = 0x00000010;

    /**
     * Action that long clicks on the node.
     */
    public static final int ACTION_LONG_CLICK = 0x00000020;

    /**
     * Action that gives accessibility focus to the node.
     */
    public static final int ACTION_ACCESSIBILITY_FOCUS = 0x00000040;

    /**
     * Action that clears accessibility focus of the node.
     */
    public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 0x00000080;

    /**
     * Action that requests to go to the next entity in this node's text
     * at a given movement granularity. For example, move to the next character,
     * word, etc.
     * <p>
     * <strong>Arguments:</strong> {@link #ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT}<,
     * {@link #ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN}<br>
     * <strong>Example:</strong> Move to the previous character and do not extend selection.
     * <code><pre><p>
     *   Bundle arguments = new Bundle();
     *   arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
     *           AccessibilityNodeInfo.MOVEMENT_GRANULARITY_CHARACTER);
     *   arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
     *           false);
     *   info.performAction(AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, arguments);
     * </code></pre></p>
     * </p>
     *
     * @see #ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT
     * @see #ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN
     * @see #setMovementGranularities(int)
     * @see #getMovementGranularities()
     * @see #MOVEMENT_GRANULARITY_CHARACTER
     * @see #MOVEMENT_GRANULARITY_WORD
     * @see #MOVEMENT_GRANULARITY_LINE
     * @see #MOVEMENT_GRANULARITY_PARAGRAPH
     * @see #MOVEMENT_GRANULARITY_PAGE
     */
    public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 0x00000100;

    /**
     * Action that requests to go to the previous entity in this node's text
     * at a given movement granularity. For example, move to the next character,
     * word, etc.
     * <p>
     * <strong>Arguments:</strong> {@link #ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT}<,
     * {@link #ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN}<br>
     * <strong>Example:</strong> Move to the next character and do not extend selection.
     * <code><pre><p>
     *   Bundle arguments = new Bundle();
     *   arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
     *           AccessibilityNodeInfo.MOVEMENT_GRANULARITY_CHARACTER);
     *   arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
     *           false);
     *   info.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
     *           arguments);
     * </code></pre></p>
     * </p>
     *
     * @see #ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT
     * @see #ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN
     * @see #setMovementGranularities(int)
     * @see #getMovementGranularities()
     * @see #MOVEMENT_GRANULARITY_CHARACTER
     * @see #MOVEMENT_GRANULARITY_WORD
     * @see #MOVEMENT_GRANULARITY_LINE
     * @see #MOVEMENT_GRANULARITY_PARAGRAPH
     * @see #MOVEMENT_GRANULARITY_PAGE
     */
    public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 0x00000200;

    /**
     * Action to move to the next HTML element of a given type. For example, move
     * to the BUTTON, INPUT, TABLE, etc.
     * <p>
     * <strong>Arguments:</strong> {@link #ACTION_ARGUMENT_HTML_ELEMENT_STRING}<br>
     * <strong>Example:</strong>
     * <code><pre><p>
     *   Bundle arguments = new Bundle();
     *   arguments.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_HTML_ELEMENT_STRING, "BUTTON");
     *   info.performAction(AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, arguments);
     * </code></pre></p>
     * </p>
     */
    public static final int ACTION_NEXT_HTML_ELEMENT = 0x00000400;

    /**
     * Action to move to the previous HTML element of a given type. For example, move
     * to the BUTTON, INPUT, TABLE, etc.
     * <p>
     * <strong>Arguments:</strong> {@link #ACTION_ARGUMENT_HTML_ELEMENT_STRING}<br>
     * <strong>Example:</strong>
     * <code><pre><p>
     *   Bundle arguments = new Bundle();
     *   arguments.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_HTML_ELEMENT_STRING, "BUTTON");
     *   info.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT, arguments);
     * </code></pre></p>
     * </p>
     */
    public static final int ACTION_PREVIOUS_HTML_ELEMENT = 0x00000800;

    /**
     * Action to scroll the node content forward.
     */
    public static final int ACTION_SCROLL_FORWARD = 0x00001000;

    /**
     * Action to scroll the node content backward.
     */
    public static final int ACTION_SCROLL_BACKWARD = 0x00002000;

    /**
     * Action to copy the current selection to the clipboard.
     */
    public static final int ACTION_COPY = 0x00004000;

    /**
     * Action to paste the current clipboard content.
     */
    public static final int ACTION_PASTE = 0x00008000;

    /**
     * Action to cut the current selection and place it to the clipboard.
     */
    public static final int ACTION_CUT = 0x00010000;

    /**
     * Action to set the selection. Performing this action with no arguments
     * clears the selection.
     * <p>
     * <strong>Arguments:</strong>
     * {@link #ACTION_ARGUMENT_SELECTION_START_INT},
     * {@link #ACTION_ARGUMENT_SELECTION_END_INT}<br>
     * <strong>Example:</strong>
     * <code><pre><p>
     *   Bundle arguments = new Bundle();
     *   arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 1);
     *   arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, 2);
     *   info.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
     * </code></pre></p>
     * </p>
     *
     * @see #ACTION_ARGUMENT_SELECTION_START_INT
     * @see #ACTION_ARGUMENT_SELECTION_END_INT
     */
    public static final int ACTION_SET_SELECTION = 0x00020000;

    /**
     * Action to expand an expandable node.
     */
    public static final int ACTION_EXPAND = 0x00040000;

    /**
     * Action to collapse an expandable node.
     */
    public static final int ACTION_COLLAPSE = 0x00080000;

    /**
     * Action to dismiss a dismissable node.
     */
    public static final int ACTION_DISMISS = 0x00100000;

    /**
     * Action that sets the text of the node. Performing the action without argument, using <code>
     * null</code> or empty {@link CharSequence} will clear the text. This action will also put the
     * cursor at the end of text.
     * <p>
     * <strong>Arguments:</strong>
     * {@link #ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE}<br>
     * <strong>Example:</strong>
     * <code><pre><p>
     *   Bundle arguments = new Bundle();
     *   arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
     *       "android");
     *   info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
     * </code></pre></p>
     */
    public static final int ACTION_SET_TEXT = 0x00200000;

    /**
     * @hide
     */
    public static final int LAST_LEGACY_STANDARD_ACTION = ACTION_SET_TEXT;

    /**
     * Mask to see if the value is larger than the largest ACTION_ constant
     */
    private static final int ACTION_TYPE_MASK = 0xFF000000;

    // Action arguments

    /**
     * Argument for which movement granularity to be used when traversing the node text.
     * <p>
     * <strong>Type:</strong> int<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_NEXT_AT_MOVEMENT_GRANULARITY}</li>
     *     <li>{@link AccessibilityAction#ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY}</li>
     * </ul>
     * </p>
     *
     * @see AccessibilityAction#ACTION_NEXT_AT_MOVEMENT_GRANULARITY
     * @see AccessibilityAction#ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY
     */
    public static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT =
            "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT";

    /**
     * Argument for which HTML element to get moving to the next/previous HTML element.
     * <p>
     * <strong>Type:</strong> String<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_NEXT_HTML_ELEMENT}</li>
     *     <li>{@link AccessibilityAction#ACTION_PREVIOUS_HTML_ELEMENT}</li>
     * </ul>
     * </p>
     *
     * @see AccessibilityAction#ACTION_NEXT_HTML_ELEMENT
     * @see AccessibilityAction#ACTION_PREVIOUS_HTML_ELEMENT
     */
    public static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING =
            "ACTION_ARGUMENT_HTML_ELEMENT_STRING";

    /**
     * Argument for whether when moving at granularity to extend the selection
     * or to move it otherwise.
     * <p>
     * <strong>Type:</strong> boolean<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_NEXT_AT_MOVEMENT_GRANULARITY}</li>
     *     <li>{@link AccessibilityAction#ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_NEXT_AT_MOVEMENT_GRANULARITY
     * @see AccessibilityAction#ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY
     */
    public static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN =
            "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN";

    /**
     * Argument for specifying the selection start.
     * <p>
     * <strong>Type:</strong> int<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_SET_SELECTION}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_SET_SELECTION
     */
    public static final String ACTION_ARGUMENT_SELECTION_START_INT =
            "ACTION_ARGUMENT_SELECTION_START_INT";

    /**
     * Argument for specifying the selection end.
     * <p>
     * <strong>Type:</strong> int<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_SET_SELECTION}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_SET_SELECTION
     */
    public static final String ACTION_ARGUMENT_SELECTION_END_INT =
            "ACTION_ARGUMENT_SELECTION_END_INT";

    /**
     * Argument for specifying the text content to set.
     * <p>
     * <strong>Type:</strong> CharSequence<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_SET_TEXT}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_SET_TEXT
     */
    public static final String ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE =
            "ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE";

    /**
     * Argument for specifying the collection row to make visible on screen.
     * <p>
     * <strong>Type:</strong> int<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_SCROLL_TO_POSITION}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_SCROLL_TO_POSITION
     */
    public static final String ACTION_ARGUMENT_ROW_INT =
            "android.view.accessibility.action.ARGUMENT_ROW_INT";

    /**
     * Argument for specifying the collection column to make visible on screen.
     * <p>
     * <strong>Type:</strong> int<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_SCROLL_TO_POSITION}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_SCROLL_TO_POSITION
     */
    public static final String ACTION_ARGUMENT_COLUMN_INT =
            "android.view.accessibility.action.ARGUMENT_COLUMN_INT";

    /**
     * Argument for specifying the progress value to set.
     * <p>
     * <strong>Type:</strong> float<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_SET_PROGRESS}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_SET_PROGRESS
     */
    public static final String ACTION_ARGUMENT_PROGRESS_VALUE =
            "android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE";

    /**
     * Argument for specifying the x coordinate to which to move a window.
     * <p>
     * <strong>Type:</strong> int<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_MOVE_WINDOW}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_MOVE_WINDOW
     */
    public static final String ACTION_ARGUMENT_MOVE_WINDOW_X =
            "ACTION_ARGUMENT_MOVE_WINDOW_X";

    /**
     * Argument for specifying the y coordinate to which to move a window.
     * <p>
     * <strong>Type:</strong> int<br>
     * <strong>Actions:</strong>
     * <ul>
     *     <li>{@link AccessibilityAction#ACTION_MOVE_WINDOW}</li>
     * </ul>
     *
     * @see AccessibilityAction#ACTION_MOVE_WINDOW
     */
    public static final String ACTION_ARGUMENT_MOVE_WINDOW_Y =
            "ACTION_ARGUMENT_MOVE_WINDOW_Y";

    /**
     * Argument to pass the {@link AccessibilityClickableSpan}.
     * For use with R.id.accessibilityActionClickOnClickableSpan
     *
     * @hide
     */
    public static final String ACTION_ARGUMENT_ACCESSIBLE_CLICKABLE_SPAN =
            "android.view.accessibility.action.ACTION_ARGUMENT_ACCESSIBLE_CLICKABLE_SPAN";

    // Focus types

    /**
     * The input focus.
     */
    public static final int FOCUS_INPUT = 1;

    /**
     * The accessibility focus.
     */
    public static final int FOCUS_ACCESSIBILITY = 2;

    // Movement granularities

    /**
     * Movement granularity bit for traversing the text of a node by character.
     */
    public static final int MOVEMENT_GRANULARITY_CHARACTER = 0x00000001;

    /**
     * Movement granularity bit for traversing the text of a node by word.
     */
    public static final int MOVEMENT_GRANULARITY_WORD = 0x00000002;

    /**
     * Movement granularity bit for traversing the text of a node by line.
     */
    public static final int MOVEMENT_GRANULARITY_LINE = 0x00000004;

    /**
     * Movement granularity bit for traversing the text of a node by paragraph.
     */
    public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 0x00000008;

    /**
     * Movement granularity bit for traversing the text of a node by page.
     */
    public static final int MOVEMENT_GRANULARITY_PAGE = 0x00000010;

    /**
     * Key used to request and locate extra data for text character location. This key requests that
     * an array of {@link android.graphics.RectF}s be added to the extras. This request is made with
     * {@link #refreshWithExtraData(String, Bundle)}. The arguments taken by this request are two
     * integers: {@link #EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX} and
     * {@link #EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH}. The starting index must be valid
     * inside the CharSequence returned by {@link #getText()}, and the length must be positive.
     * <p>
     * The data can be retrieved from the {@code Bundle} returned by {@link #getExtras()} using this
     * string as a key for {@link Bundle#getParcelableArray(String)}. The
     * {@link android.graphics.RectF} will be null for characters that either do not exist or are
     * off the screen.
     * <p>
     * {@see #refreshWithExtraData(String, Bundle)}
     */
    public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY =
            "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_KEY";

    /**
     * Integer argument specifying the start index of the requested text location data. Must be
     * valid inside the CharSequence returned by {@link #getText()}.
     *
     * @see #EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY
     */
    public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX =
            "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX";

    /**
     * Integer argument specifying the end index of the requested text location data. Must be
     * positive.
     *
     * @see #EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY
     */
    public static final String EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH =
            "android.view.accessibility.extra.DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH";

    /**
     * @hide
     */
    public static final String EXTRA_DATA_REQUESTED_KEY =
            "android.view.accessibility.AccessibilityNodeInfo.extra_data_requested";
}