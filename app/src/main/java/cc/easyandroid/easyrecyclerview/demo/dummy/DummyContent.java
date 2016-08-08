package cc.easyandroid.easyrecyclerview.demo.dummy;

import java.util.ArrayList;
import java.util.List;


public class DummyContent {

    public static final List<String> ITEMS = new ArrayList<String>();


    private static final int COUNT = 30;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem("i=" + i);
        }
    }

    private static void addItem(String item) {
        ITEMS.add(item);
    }


}
