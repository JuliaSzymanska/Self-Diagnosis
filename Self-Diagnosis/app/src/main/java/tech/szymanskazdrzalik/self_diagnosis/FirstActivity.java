package tech.szymanskazdrzalik.self_diagnosis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class FirstActivity extends AppCompatActivity {
    // TODO: 02.11.2020 https://developer.android.com/topic/libraries/view-binding#java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new AddProfile();
        ft.replace(R.id.fragmentAddProfile, fragment);
        ft.commit();
    }
}