package cl.tofcompany.ready.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cl.tofcompany.ready.R;

public class Splash_Screen extends AppCompatActivity {
    //Variables
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView name, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash__screen);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this , R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this , R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.logo);
        name = findViewById(R.id.name);
        slogan = findViewById(R.id.slogan);

        image.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        //duration splash screen
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(cl.tofcompany.ready.Controllers.Splash_Screen.this);

            @Override
            public void run() {
                if (user != null && account != null) {
                    Intent intent = new Intent(cl.tofcompany.ready.Controllers.Splash_Screen.this , HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(cl.tofcompany.ready.Controllers.Splash_Screen.this , MainActivity.class));
                    finish();
                }

            }
        } , 4000);

    }
}