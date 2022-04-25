package by.bsuir.relaxapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private TextView getRegisteredTextView;

    private EditText editTextEmail, editTextPassword;
    private Button signInButton;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private void signIn(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("Email is requested!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            editTextPassword.setError("Min password length is 6 chars!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(success->{
                    ProfileFragment.FETCH_USER_NAME_FIRST_TIME = true;
                    Intent toMainActivity = new Intent(this, MainActivity.class);
                    startActivity(toMainActivity);
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(failure->{
                    String message = failure.getLocalizedMessage();
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void getRidOfTopBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRidOfTopBar();

        setContentView(R.layout.activity_sign_in);

        getRegisteredTextView = findViewById(R.id.GetRegisteredTextView);
        getRegisteredTextView.setOnClickListener(view ->{
            Intent toRegistrationActivity = new Intent(this, RegistrationActivity.class);
            startActivity(toRegistrationActivity);
        });

        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(view->{
            signIn();
        });

        editTextEmail = findViewById(R.id.SignInEmail);
        editTextPassword = findViewById(R.id.SignInPassword);

        progressBar = findViewById(R.id.progressBarSignIn);

        mAuth = FirebaseAuth.getInstance();
    }
}