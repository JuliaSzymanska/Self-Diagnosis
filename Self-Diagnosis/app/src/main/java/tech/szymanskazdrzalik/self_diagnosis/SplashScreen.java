package tech.szymanskazdrzalik.self_diagnosis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashScreen extends AppCompatActivity {

    // TODO: 12/16/20 zmienić na biding
    Animation top, bottom;
    ImageView logo;
    TextView name, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logo = (ImageView) findViewById(R.id.ivLogo);
        name = (TextView) findViewById(R.id.tvName);
        description = (TextView) findViewById(R.id.tvDesc);

        //animations
        top = AnimationUtils.loadAnimation(this, R.anim.from_top_animation);
        bottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_aniamtion);

        logo.setAnimation(top);
        name.setAnimation(bottom);
        description.setAnimation(bottom);

    }

}