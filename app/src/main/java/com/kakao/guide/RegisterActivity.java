package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    // 레이아웃 선언부.
    LinearLayout text, type, store;
    Button send, sendOk, storeOk;
    Animation fadein, fadeout;
    EditText phone, edit_type;
    String number; // 휴대폰 번호
    ProgressBar progressBar;
    TextView login;

    //firebase 부분.
    private String verificationId;
    private String phonenumber = "+821063462260";
    private FirebaseAuth mAuth; //Firebase 인증 객체.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        text = (LinearLayout)findViewById(R.id.layout_text);
        type = (LinearLayout)findViewById(R.id.layout_type);
        store = (LinearLayout)findViewById(R.id.layout_store);

        send = (Button)findViewById(R.id.btn_send);
        sendOk = (Button)findViewById(R.id.btn_sendOk);
        storeOk = (Button)findViewById(R.id.btn_storeOk);

        login = (TextView)findViewById(R.id.text_login);

        // 휴대폰입력
        phone = (EditText)findViewById(R.id.edit_phone);


        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        text.startAnimation(fadein);

        //firebase 부분.
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        edit_type = (EditText)findViewById(R.id.edit_type); // 인증번호.
        //sendVerificationCode(phonenumber);

        // 휴대폰 인증번호 보내기 버튼.
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력값을 가져오되, 앞뒤 공백을 자른다.
                number = phone.getText().toString().trim();

                // 휴대폰 입력이 비어있거나 적게 입력했을 때.
                if(number.isEmpty() || number.length()<10) {
                    phone.setError("번호를 입력해주세요.");
                    phone.requestFocus();
                    return;
                }

                //넘어가는 부분.
                text.startAnimation(fadeout);
                text.setVisibility(View.GONE);
                type.setVisibility(View.VISIBLE);
                type.startAnimation(fadein);

                //FIrebase에 해당 번호를 확인.
                sendVerificationCode(number);
            }
        });

        // 인증번호 확인 버튼.
        sendOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edit_type.getText().toString().trim();

                // 인증번호가 비었거나 6자 미만일때.
                if (code.isEmpty() || code.length()<6) {
                    edit_type.setError("인증번호를 입력해주세요.");
                    edit_type.requestFocus();
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
                /*
                //넘어가는 부분.
                type.startAnimation(fadeout);
                type.setVisibility(View.GONE);
                store.setVisibility(View.VISIBLE);
                store.startAnimation(fadein);

                //firebase 부분.

                */
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }


    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 인증 성공. 다음 단계로.
                            Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
                            startActivity(intent);


                            /*
                            type.startAnimation(fadeout);
                            type.setVisibility(View.GONE);
                            store.setVisibility(View.VISIBLE);
                            store.startAnimation(fadein);

                             */
                        }else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Firebase가 사용자의 전화번호를 확인하도록 요청.
    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    // PhoneAuthProvider.verifyPhoneNumber를 호출할 때 요청 결과를 처리하는 콜백함수 구현을 위해.
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;
                }
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    // SMS 문장내용을 자동으로 읽어오기.
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        //progressBar.setVisibility(View.VISIBLE);
                        // 수신된 문자들 자동으로 읽어서 화면상에 노출.
                        edit_type.setText(code);
                        verifyCode(code);
                    }
                }
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

//    @Override
//    protected void onStart() {
//        super.onStart();
//
////        // 이미 문자인증을 거친 사용자인 경우 ==> 계정생성 넘어가기.
////        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
////            FirebaseAuth.getInstance().signOut();
////
////            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////
////            startActivity(intent);
////        }
//    }
}
