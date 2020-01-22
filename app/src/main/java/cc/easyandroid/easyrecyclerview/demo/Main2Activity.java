package cc.easyandroid.easyrecyclerview.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.easyandroid.easyrecyclerview.core.progress.EasyProgressLinearLayout;
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

public class Main2Activity extends AppCompatActivity {
    EasyProgressLinearLayout easyProgressLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        easyProgressLayout = (EasyProgressLinearLayout) findViewById(R.id.easyProgressLayout);
        easyProgressLayout.showLoadingView();
        easyProgressLayout.setOnEasyProgressClickListener(new OnEasyProgressClickListener() {
            @Override
            public void onLoadingViewClick() {
                easyProgressLayout.showEmptyView();
            }

            @Override
            public void onEmptyViewClick() {
                easyProgressLayout.showErrorView();
            }

            @Override
            public void onErrorViewClick() {
                easyProgressLayout.showContentView();
            }
        });
    }

}
