package com.kakao.guide;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class FindActivity extends AppCompatActivity {
    TextView title, subTitle, text_back, text_code, text_pass;
    LinearLayout select, phone, email, answer, result;
    ImageView image_phone, image_email;
    EditText edit_phone, edit_email, edit_answer;
    Button btn_sendPhone, btn_sendEmail, btn_isCorrect;
//    View line;
//    Animation fadein, fadeout;

    String number;
    //firebase 부분.
    private String verificationId;
    private FirebaseAuth mAuth; //Firebase 인증 객체.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_layout);

        title = (TextView)findViewById(R.id.text_title);            // 타이틀 텍스트
        subTitle = (TextView)findViewById(R.id.text_subTitle);      // 서브타이틀 텍스트
        text_back = (TextView)findViewById(R.id.text_back);         // '돌아가기' 텍스트
        text_code = (TextView)findViewById(R.id.text_code);
        text_pass = (TextView)findViewById(R.id.text_pass);
        select = (LinearLayout)findViewById(R.id.layout_select);    // 인증방법 선택화면
        phone = (LinearLayout)findViewById(R.id.layout_phone);      // 휴대폰 인증화면
        answer = (LinearLayout)findViewById(R.id.layout_answer);    // 휴대폰 인증번호화면
        email = (LinearLayout)findViewById(R.id.layout_email);      // 이메일 인증화면
        result = (LinearLayout)findViewById(R.id.layout_result);
        image_phone = (ImageView)findViewById(R.id.image_phone);    // 핸드폰 인증 선택
        image_email = (ImageView)findViewById(R.id.image_email);    // 이메일 인증 선택
        edit_phone = (EditText)findViewById(R.id.edit_phone);       // 핸드폰 입력
        edit_answer = (EditText)findViewById(R.id.edit_answer);     // 인증번호 입력
        edit_email = (EditText)findViewById(R.id.edit_email);       // 이메일 입력
        btn_sendPhone = (Button)findViewById(R.id.btn_sendPhone);   // 핸드폰번호 입력버튼
        btn_sendEmail = (Button)findViewById(R.id.btn_sendEmail);   // 이메일 입력버튼
        btn_isCorrect = (Button)findViewById(R.id.btn_isCorrect);   // 인증번호 입력버튼
//        line = (View)findViewById(R.id.view_line);
//        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);     // 서서히 나타나기
//        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);   // 서서히 사라지기

        //firebase 부분.
        mAuth = FirebaseAuth.getInstance();

        image_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select.setVisibility(View.GONE);
                phone.setVisibility(View.VISIBLE);
                title.setText("핸드폰 인증 (1/2)");
                subTitle.setText("가입 시 등록했던 핸드폰 번호를 입력해주세요.");
            }
        });
        image_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                title.setText("이메일 인증 (1/2)");
                subTitle.setText("가입 시 등록했던 이메일을 입력해주세요.");
            }
        });

        btn_sendPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = edit_phone.getText().toString().trim();

                // 휴대폰 입력이 비어있거나 적게 입력했을 때.
                if(number.isEmpty() || number.length()<10) {
                    edit_phone.setError("번호를 입력해주세요.");
                    edit_phone.requestFocus();
                    return;
                }

                // 핸드폰에 인증번호 보내기.
                if(true) {
                    phone.setVisibility(View.GONE);
                    answer.setVisibility(View.VISIBLE);
                    title.setText("핸드폰 인증 (2/2)");
                    subTitle.setText("인증번호를 입력해주세요.");
                } else {

                }

                //FIrebase에 해당 번호를 확인.
                sendVerificationCode(number);
            }
        });

        // 되돌아가기.
        text_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone.getVisibility() == View.VISIBLE) {
                    phone.setVisibility(View.GONE);
                } else if(answer.getVisibility() == View.VISIBLE) {
                    answer.setVisibility(View.GONE);
                } else if(email.getVisibility() == View.VISIBLE) {
                    email.setVisibility(View.GONE);
                }
                select.setVisibility(View.VISIBLE);
                title.setText("인증 방법 선택");
                subTitle.setText("찾기 방법을 선택해주세요.");
            }
        });
    }

    // Firebase가 사용자의 전화번호를 확인하도록 요청.
    private void sendVerificationCode(String number) {
//        progressBar.setVisibility(View.VISIBLE);
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
                    System.out.println("★★★★코드는 "+code+"입니다.★★★★");
                    if (code != null) {
                        //progressBar.setVisibility(View.VISIBLE);
                        // 수신된 문자들 자동으로 읽어서 화면상에 노출.
                        edit_answer.setText(code);
                        verifyCode(code);
                    }
                }
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(FindActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    // 인증번호 확인.
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
//                            Intent intent = new Intent(FindActivity.this, LoginActivity.class);
//                            intent.putExtra("phone", number);
//
//                            startActivity(intent);
                            select.setVisibility(View.GONE);
                            email.setVisibility(View.VISIBLE);
                            title.setText("이메일 인증 (1/2)");
                            subTitle.setText("가입 시 등록했던 이메일을 입력해주세요.");

                            if(phone.getVisibility() == View.VISIBLE) {
                                phone.setVisibility(View.GONE);
                            } else if(answer.getVisibility() == View.VISIBLE) {
                                answer.setVisibility(View.GONE);
                            } else if(email.getVisibility() == View.VISIBLE) {
                                email.setVisibility(View.GONE);
                            }



                            /*
                            type.startAnimation(fadeout);
                            type.setVisibility(View.GONE);
                            store.setVisibility(View.VISIBLE);
                            store.startAnimation(fadein);

                             */
                        }else {
                            Toast.makeText(FindActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /////




}
