package com.example.lab1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText edt_email;
    private EditText edt_pass;
    private EditText edt_enter_Pass;
    private Button btn_register;
    private Button btn_cancel;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...!");

        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        edt_enter_Pass = findViewById(R.id.edt_enter_Pass);
        btn_register = findViewById(R.id.btn_register);
        btn_cancel = findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String email = edt_email.getText().toString().trim();
                String password = edt_pass.getText().toString().trim();
                String password_enter = edt_enter_Pass.getText().toString().trim();


                if (email.isEmpty() || password.isEmpty() || password_enter.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Không được để trống thông tin!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                } else if (!password.equals(password_enter)) {
                    Toast.makeText(MainActivity.this, "Nhập khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Đăng ký thất bại!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                });

            }
        });


    }
}