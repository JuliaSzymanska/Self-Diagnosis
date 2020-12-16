package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import tech.szymanskazdrzalik.self_diagnosis.databinding.ActivitySplashBinding;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;
import tech.szymanskazdrzalik.self_diagnosis.helpers.SharedPreferencesHelper;

public class SplashActivity extends AppCompatActivity implements AddProfileFragment.AddProfileFragmentListener {

    private final static int SPLASH_TIME_OUT = 1000;
    private final Runnable loadRunnable = () -> {
        SharedPreferencesHelper.loadUser(SplashActivity.this);
        // TODO: 16.12.2020 Jesli cos ladujemy to tutaj
    };
    ActivitySplashBinding binding;
    private Thread loadThread;
    private final Runnable waifForLoadRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                loadThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                runOnUiThread(() -> new Handler().postDelayed(() -> {
                    if (GlobalVariables.getInstance().getCurrentUser().isPresent()) {
                        startChatActivity();
                    } else {
                        runFragment();
                    }
                }, SPLASH_TIME_OUT));
            }
        }
    };

    private void startChatActivity() {
        Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
        startActivity(intent);
        // TODO: 16.12.2020 Override transition
        finish();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.loadThread = new Thread(this.loadRunnable);
        this.loadThread.start();
        new Thread(this.waifForLoadRunnable).start();
    }

    @Override
    public void onBackPressed() {
        if (GlobalVariables.getInstance().getCurrentUser().isPresent()) {
            startChatActivity();
        } else {
            // TODO: 16.12.2020 "moze jakies powiadomienie ze zle i powtórzyć?"
        }
    }

    @Override
    public void callback(String result) {
        // TODO: 16.12.2020
    }
}
