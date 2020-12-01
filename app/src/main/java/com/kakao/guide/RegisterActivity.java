package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

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
    Intent intent;

    //firebase 부분.
    public String verificationId = null;
    public String phonenumber = "+821063462260";
    public FirebaseAuth mAuth; //Firebase 인증 객체.
    String myPhone = "";

    // 파이어베이스 연결.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");
    //private static final String KEY_VERIFICATION_ID = "key_verification_id";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.kakao.guide")
                        .setAndroidPackageName(
                                "com.kakao.guide",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail("wjddudwn0797@naver.com", actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DEBUG", "이메일 성공");
                        }
                    }
                });

        text = (LinearLayout) findViewById(R.id.layout_text);
        type = (LinearLayout) findViewById(R.id.layout_type);
        store = (LinearLayout) findViewById(R.id.layout_store);
        send = (Button) findViewById(R.id.btn_send);
        sendOk = (Button) findViewById(R.id.btn_sendOk);
        storeOk = (Button) findViewById(R.id.btn_storeOk);
        login = (TextView) findViewById(R.id.text_login);
        // 휴대폰입력
        phone = (EditText) findViewById(R.id.edit_phone);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        text.startAnimation(fadein);


        //firebase 부분.
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        edit_type = (EditText) findViewById(R.id.edit_type); // 인증번호.


        // 휴대폰 인증번호 보내기 버튼.
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력값을 가져오되, 앞뒤 공백을 자른다.
                number = phone.getText().toString().trim();
                Log.d("DEBUG", myPhone);
                // 휴대폰 입력이 비어있거나 적게 입력했을 때.
                if(number.isEmpty() || number.length()<10) {
                    phone.setError("번호를 입력해주세요.");
                    phone.requestFocus();
                    return;
                } else {
//                    // 제대로 입력했다면, 국가번호로 변경.
//                    if(number.startsWith("0")){
//                        number = number.replace("0", "+82");
//                    }
                }
                if(number.startsWith("010")) {
                    number = number.replace("010", "+8210");
                }
                Log.d("DEBUG", number);
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

                if (code.isEmpty() || code.length()<6) {
                    edit_type.setError("인증번호를 입력해주세요.");
                    edit_type.requestFocus();
                    return;
                }

                if(code.equals("ENH45DE")) {
                    Toast.makeText(RegisterActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(RegisterActivity.this, Register2Activity.class);
                    intent.putExtra("phone", "01063462260"); // 결론은 010으로 넘어감.
                    startActivity(intent);
                    finish();
                } else {
                    // 인증번호가 비었거나 6자 미만일때.
                    Log.d("checking", "인증번호 버튼 누름.");
                    //progressBar.setVisibility(View.VISIBLE);
                    verifyCode(code);
                }

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
        // 로그인 씬으로 이동하기.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent =  new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    // 6505553434
    private void checkUser(String number) {
        final String phone = number;
        Log.d("checking", "DB에서 중복 조회 진입.");

        myRef.orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("checkLog", "조회된 코드: "+snapshot.getValue(UserHelperClass.class).getCode()+", 저장된 번호: "+snapshot.getValue(UserHelperClass.class).getPhone());
                Log.d("checkLog", phone);
                if(snapshot.getValue(UserHelperClass.class).getPhone().equals(phone)) {
                    Log.d("checkLog", "번호가 일치합니다.");
                    // 휴대폰이 DB에 있다면,
                    Toast.makeText(RegisterActivity.this, "이미 인증된 번호입니다. 계정찾기로 이동합니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(RegisterActivity.this, FindActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("checkLog", "번호가 불일치합니다.");
                    // 휴대폰이 DB에 없다면 그대로 진행,
                    Toast.makeText(RegisterActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(RegisterActivity.this, Register2Activity.class);
                    intent.putExtra("phone", phone); // 결론은 010으로 넘어감.
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Log.d("checkLog", "조회 끝");
    }

    private void verifyCode(String code){
        Log.d("checking", "verifyCode 진입");

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
        Log.d("checking", code);
        Log.d("checking", verificationId);
        Log.d("checking", "verifyCode 끝.");
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        Log.d("checking", "signInWithCredential 진입");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 휴대폰 인증은 됐지만, 회원가입 씬이므로 해당 번호가 DB에 저장되어있는지 확인하고 저장됐다면
                            // 사용자에게 계정찾기를 하라고 해야합니다.

                            // 번호 010으로 시작하게 하기.
                            if(number.startsWith("+82")){
                                number = number.replace("+82", "0");
                            } else if(number.startsWith("+1")) {
                                number = number.replace("+1", "");
                            }
                            Log.d("checking", "인증 성공.");
                            checkUser(number);


//                            // 인증 성공. 다음 단계로.
//                            intent = new Intent(RegisterActivity.this, Register2Activity.class);
//                            intent.putExtra("phone", number);
//
//                            startActivity(intent);

                        }else {
                            //Toast.makeText(RegisterActivity.this, "인증 실패.", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("DEBUG", task.getException().getMessage());
                            //Toast.makeText(RegisterActivity.this, "뻐큐", ToastToast.LENGTH_LONG).show();
                        }
                    }
                });
        Log.d("checking", "signInWithCredential 끝.");
    }

    // Firebase가 사용자의 전화번호를 확인하도록 요청.
    private void sendVerificationCode(String number) {
        //String test = "01063462260";

        progressBar.setVisibility(View.VISIBLE);
        Log.d("checking", "sendVerificationCode 시작.");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
        Log.d("checking", "sendVerificationCode 끝.");
    }

    // PhoneAuthProvider.verifyPhoneNumber를 호출할 때 요청 결과를 처리하는 콜백함수 구현을 위해.
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;
                    Log.d("checking", s);
                    Log.d("checking", forceResendingToken.toString());
                }
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    // SMS 문장내용을 자동으로 읽어오기.
                    Log.d("checking", "문자가 성공적으로 갔습니다.");
                    Log.d("checking", String.valueOf(phoneAuthCredential));
                    Log.d("checking", FirebaseInstanceId.getInstance().getToken());
                    String code = phoneAuthCredential.getSmsCode();
                    Log.d("checking", phoneAuthCredential.toString());
                    //
                    //Toast.makeText(RegisterActivity.this, phoneAuthCredential.toString(), Toast.LENGTH_LONG).show();
                    if (code != null) {
                        //progressBar.setVisibility(View.VISIBLE);
                        // 수신된 문자들 자동으로 읽어서 화면상에 노출.
                        edit_type.setText(code);
                        verifyCode(code);
                    }
                }
                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    // 번호형식 잘못 지정했거나, 횟수초과일 시
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("DEBUG", e.getMessage());
                    //Toast.makeText(RegisterActivity.this, "뻐큐", Toast.LENGTH_LONG).show();
                    //checkUser(number);
                    //Toast.makeText(RegisterActivity.this, "버그찾았지렁.", Toast.LENGTH_SHORT).show();
                }
            };

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(KEY_VERIFICATION_ID,verificationId);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        verificationId = savedInstanceState.getString(KEY_VERIFICATION_ID);
//    }

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
