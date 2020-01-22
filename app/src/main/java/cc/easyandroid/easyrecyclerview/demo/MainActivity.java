package cc.easyandroid.easyrecyclerview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//      getSupportFragmentManager().beginTransaction().replace(R.id.content, new SingleSelectListFragment()).commit();
//      getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment_5()).commit();
//       getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment_2()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment_3()).commit();

    }

}
