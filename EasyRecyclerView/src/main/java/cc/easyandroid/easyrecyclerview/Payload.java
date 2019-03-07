package cc.easyandroid.easyrecyclerview;


public enum Payload {
    /** for a general update of the content item */
    CHANGE,
    /** when no more load is triggered */
    NO_MORE_LOAD,
    /** when the filter has changed and item is still visible */
    FILTER,
    /** when header or parent receive back its child after the restoration */
    UNDO,
    /** when a subItem is added to the siblings below a parent */
    ADD_SUB_ITEM,
    /** when a subItem is removed from the siblings below a parent */
    REM_SUB_ITEM,
    /** when item has been moved, the original header/parent receives this payload */
    MOVE,
    /** when linking a header from a sectionable item */
    LINK,
    /** when un-linking a header from a sectionable item */
    UNLINK,
    /** when items are notified due to Selecting All items / Clearing Selection) */
    SELECTION,
    /** when items are notified after a merge */
    MERGE,
    /** when items are notified after a split */
    SPLIT,
    /** when an item is expanded by the user or system */
    EXPANDED,
    /** when an item is collapsed by the user or system */
    COLLAPSED
}