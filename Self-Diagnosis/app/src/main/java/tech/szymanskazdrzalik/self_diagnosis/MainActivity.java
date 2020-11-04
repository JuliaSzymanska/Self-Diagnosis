package tech.szymanskazdrzalik.self_diagnosis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (GlobalVariables.getInstance().getCurrentUser() != null && GlobalVariables.getInstance().getCurrentUser().getPicture() != null) {
            binding.topPanelChat.doctor.setBackground(new BitmapDrawable(getResources(), GlobalVariables.getInstance().getCurrentUser().getPicture()));
        }
    }


    public void backArrowOnClick(View v) {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

}