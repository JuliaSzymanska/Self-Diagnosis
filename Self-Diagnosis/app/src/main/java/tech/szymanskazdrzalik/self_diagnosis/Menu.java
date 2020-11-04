package tech.szymanskazdrzalik.self_diagnosis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onProfilePictureClick(View v) {
        Fragment fragment = new AddProfile();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutToBeReplacedWithFragmentInMenu, fragment)
                .addToBackStack(null);
        transaction.commit();
    }

}