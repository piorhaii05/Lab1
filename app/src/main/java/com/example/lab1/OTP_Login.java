package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTP_Login extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationId;

    private EditText edt_phone, edt_OTP;
    private Button btn_login_otp, btn_send_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();

        edt_phone = findViewById(R.id.edt_phone);
        edt_OTP = findViewById(R.id.edt_OTP);
        btn_login_otp = findViewById(R.id.btn_login_otp);
        btn_send_otp = findViewById(R.id.btn_send_otp);

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                edt_OTP.setText(phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OTP_Login.this, "Gửi OTP thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("OTP_Login", "Gửi OTP thất bại", e);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                Toast.makeText(OTP_Login.this, "OTP đã được gửi", Toast.LENGTH_SHORT).show();

            }
        };


        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edt_phone.getText().toString().trim();
                if (phone.isEmpty()) {
                    Toast.makeText(OTP_Login.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    return;
                }
                getOTP(phone);
                Toast.makeText(OTP_Login.this, "Đang gửi OTP", Toast.LENGTH_SHORT).show();

            }
        });


        btn_login_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = edt_OTP.getText().toString().trim();

                if (otp.isEmpty()) {
                    Toast.makeText(OTP_Login.this, "Vui lòng nhập OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyOTP(otp);
            }
        });


    }

    private void verifyOTP(String code) {
        if (verificationId != null) { // Kiểm tra verificationId có hợp lệ
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInwithOTP(credential);
        } else {
            Toast.makeText(OTP_Login.this, "Lỗi xác thực: Không tìm thấy mã xác thực", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInwithOTP(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(OTP_Login.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                    FirebaseUser user = task.getResult().getUser();
                    startActivity(new Intent(OTP_Login.this, Main_screen.class));
                }
                else {
                    Toast.makeText(OTP_Login.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void getOTP(String phone_number) {
        if (!phone_number.startsWith("+84")) {
            phone_number = "+84" + phone_number.substring(1);
        }
        PhoneAuthOptions options = PhoneAuthOptions
                .newBuilder(firebaseAuth)
                .setPhoneNumber(phone_number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


}