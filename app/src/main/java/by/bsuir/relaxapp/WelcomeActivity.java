package by.bsuir.relaxapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    private Button welcomeWindowLoginButton;
    private TextView getRegisteredWelcomeActivity;


    public static Context welcomeActivityContext;

    private void setStaticWelcomeActivityContext(){
        welcomeActivityContext = this;
    }

    private void getRidOfTopBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStaticWelcomeActivityContext();

        getRidOfTopBar();

        setContentView(R.layout.activity_welcome);


        welcomeWindowLoginButton = findViewById(R.id.WelcomeWindowLoginButton);
        welcomeWindowLoginButton.setOnClickListener(lambda -> {
            Intent toSignInActivity = new Intent(this, SignInActivity.class);
            startActivity(toSignInActivity);
        });

        getRegisteredWelcomeActivity = findViewById(R.id.GetRegisteredWelcomeActivity);
        getRegisteredWelcomeActivity.setOnClickListener(lambda->{
            Intent toRegisterActivity = new Intent(this, RegistrationActivity.class);
            startActivity(toRegisterActivity);
        });
    }
}