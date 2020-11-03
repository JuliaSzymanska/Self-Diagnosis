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
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

//        ft.replace(R.id.fragmentAddProfile, fragment);
//        ft.commit();

    }

    public void onC(View v){
        Fragment fragment = new AddProfile();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fg_add_profile, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
    }

}