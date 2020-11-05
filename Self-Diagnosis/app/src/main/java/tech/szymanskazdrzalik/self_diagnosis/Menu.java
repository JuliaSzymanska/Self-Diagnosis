package tech.szymanskazdrzalik.self_diagnosis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityMenuBinding;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class Menu extends AppCompatActivity {

    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (GlobalVariables.getInstance().getCurrentUser() != null && GlobalVariables.getInstance().getCurrentUser().getPicture() != null) {
            binding.menuTop1Bar.imageView.setBackground(new BitmapDrawable(getResources(), GlobalVariables.getInstance().getCurrentUser().getPicture()));
        }
    }

    // TODO: 04.11.2020 Sprawić żeby z tad po przejsciu do fragmentu nie tworzyl sie nowy user a byl modyfikowany aktualny
    public void onProfilePictureClick(View v) {
        Fragment fragment = new AddProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_new_user", false);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutToBeReplacedWithFragmentInMenu, fragment)
                .addToBackStack(null);
        transaction.commit();
    }

}