package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityMainBinding;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MainActivity extends AppCompatActivity implements AddProfileFragment.ReloadInterface  {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean(getResources().getString(R.string.is_first_run), true)) {
            runFragment();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean(getResources().getString(R.string.is_first_run), false).apply();
        }
    }

    private void runFragment() {
        Fragment fragment = new AddProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_new_user", true);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutToBeReplacedWithFragmentInMenu, fragment)
                .addToBackStack(null);
        transaction.commit();
    }


    public void backArrowOnClick(View v) {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    @Override
    public void reload() {
        binding.firstChatMessage.setText("Hello " + GlobalVariables.getInstance().getCurrentUser().getName() + "!");
    }
}