package tech.szymanskazdrzalik.self_diagnosis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class FirstActivity extends AppCompatActivity {
    // TODO: 02.11.2020 https://developer.android.com/topic/libraries/view-binding#java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Fragment fragment = new AddProfile();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null);
        transaction.commit();
    }

//    public void onC(View v) {
//        Fragment fragment = new AddProfile();
//        FragmentTransaction transaction = getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container, fragment)
//                .addToBackStack(null);
//        transaction.commit();
//    }

}