package cc.easyandroid.easyrecyclerview.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import cc.easyandroid.easyrecyclerview.demo.viewmodel.SubRedditViewModel;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//      getSupportFragmentManager().beginTransaction().replace(R.id.content, new SingleSelectListFragment()).commit();
//      getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment_5()).commit();

        Fragment fragment=getSupportFragmentManager().findFragmentByTag( "aaa");
        if(fragment==null){
//            fragment=new   ListFragment_kt();
            fragment=new ListFragment_5();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment,"aaa").commit();
//       getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment_2()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ListFragment_3()).commit();

    }
}
