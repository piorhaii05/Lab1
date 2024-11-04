package com.example.lab1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private EditText edt_email;
    private EditText edt_pass;
    private Button btn_login;
    private Button btn_register1;
    private TextView forgot, txt_login_otp;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...!");

        edt_email = findViewById(R.id.edt_email1);
        edt_pass = findViewById(R.id.edt_pass1);
        btn_login = findViewById(R.id.btn_login);
        btn_register1 = findViewById(R.id.btn_register1);
        forgot = findViewById(R.id.forgot);
        txt_login_otp = findViewById(R.id.txt_login_otp);

        txt_login_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, OTP_Login.class));
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                LayoutInflater inflater = getLayoutInflater();
                View viewforgotPass = inflater.inflate(R.layout.forgot_pass, null);
                builder.setView(viewforgotPass);
                Dialog dialog = builder.create();
                dialog.show();

                EditText edt_email_forgot = viewforgotPass.findViewById(R.id.edt_email_forgot);
                Button btn_submit = viewforgotPass.findViewById(R.id.btn_submit);

                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        String user_forgot = edt_email_forgot.getText().toString().trim();

                        if(user_forgot.isEmpty()){
                            Toast.makeText(Login.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        firebaseAuth.sendPasswordResetEmail(user_forgot).addOnCompleteListener(Login.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Đã gửi mail lấy lại mật khẩu", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(Login.this, "Gửi mail lỗi", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    }
                });
            }
        });

        btn_register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String user = edt_email.getText().toString().trim();
                String pass = edt_pass.getText().toString().trim();

                if(user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(Login.this, "Không được để trống thông tin!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            startActivity(new Intent(Login.this, Main_screen.class));

                        }
                        else {
                            Toast.makeText(Login.this, "Tài khoản hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });

            }
        });

    }
}