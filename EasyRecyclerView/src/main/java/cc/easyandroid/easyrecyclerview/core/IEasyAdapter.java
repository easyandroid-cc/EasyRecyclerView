package cc.easyandroid.easyrecyclerview.core;

import android.view.View;

/**
 * Created by cgp
 */
public interface IEasyAdapter extends IEmptyAdapter {


    void addFooterViewToLast(View lastFooterView);

    void addHeaderViewToFirst(View firstHeaderView);

}
