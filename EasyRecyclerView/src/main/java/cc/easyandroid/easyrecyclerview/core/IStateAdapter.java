package cc.easyandroid.easyrecyclerview.core;

import android.os.Bundle;

/**
 * 保存数据和恢复数据
 */

public interface IStateAdapter {

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

}
