package by.bsuir.relaxapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextUserName, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private Button registerButton;


    private void getRidOfTopBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRidOfTopBar();

        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view->{
            registerUser();
        });

        editTextUserName = findViewById(R.id.RegistrationUserName);
        editTextEmail = findViewById(R.id.RegistrationEmail);
        editTextPassword = findViewById(R.id.RegistrationPassword);
        progressBar = findViewById(R.id.progressBarRegistration);

    }

    private void registerUser(){
        String name = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (name.isEmpty()){
            editTextUserName.setError("Name is required!");
            editTextUserName.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            editTextPassword.setError("Password has to be at least 6 characters long!");
            editTextPassword.requestFocus();
            return;
        }

        if (email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please, provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(success->{
                    User user = new User(name, email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user)
                                .addOnSuccessListener(success2->{
                                    Toast.makeText(RegistrationActivity.this,"User has been registered successfully!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(failure2->{
                                    String message = failure2.getLocalizedMessage();
                                    Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                });
                })
                .addOnFailureListener(failure->{
                    String message = failure.getLocalizedMessage();
                    Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}