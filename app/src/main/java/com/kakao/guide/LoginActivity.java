package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    Animation faidIn;
    LinearLayout all;
    EditText id, pw;
    TextView find, register;
    Button login;
    Intent intent;
    String code, pass;
    int count = 0;

    // 파이어베이스 연결.
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference myRef = database.child("user/");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        all = (LinearLayout)findViewById(R.id.layout_login_all);
        id = (EditText) findViewById(R.id.edit_id);
        pw = (EditText) findViewById(R.id.edit_pw);
        login = (Button)findViewById(R.id.btn_login);

        find = (TextView) findViewById(R.id.text_find);
        register = (TextView) findViewById(R.id.text_register);
        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);


        faidIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        all.startAnimation(faidIn);

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
//                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()){
//                    tv_error_email.setText("이메일 형식으로 입력해주세요.");    // 경고 메세지
//                    et_email.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
//                }
//                else{
//                    tv_error_email.setText("");         //에러 메세지 제거
//                    et_email.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
//                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rootNode = FirebaseDatabase.getInstance();
//                reference = rootNode.getReference("user").child(id.getText().toString().trim());
//
//                if(reference.getKey()==id.getText().toString().trim()) {
//                    intent = new Intent(getApplicationContext(), RegisterActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
                code = id.getText().toString().trim();
                pass = pw.getText().toString().trim();
                myRef.orderByValue().addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        // for문처럼 한 행씩 검색.
                        //Log.e("kkkkk", "key=" + snapshot.getKey() + ", " + snapshot.getValue() + ", s=" + previousChildName);
                        //Log.e("kkkkk", snapshot.getValue(UserHelperClass.class).getPass());
                        Log.e("kkkkk", snapshot.getValue(UserHelperClass.class).getCode());
                        if(snapshot.getValue(UserHelperClass.class).getCode().equals(code)) {
                            if(snapshot.getValue(UserHelperClass.class).getPass().equals(pass)) {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            //Toast.makeText(LoginActivity.this, "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(LoginActivity.this, "조회되는 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
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
//                switch (count) {
//                    case 0:
//                        Toast.makeText(LoginActivity.this, "조회되는 회원정보가 없습니다.", Toast.LENGTH_SHORT).show(); break;
//                    case 1:
//                        Toast.makeText(LoginActivity.this, "비밀번호를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show(); break;
//                    case 2:
//                        intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                }
            }
        });



        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), FindActivity.class);
                startActivity(intent);
            }
        });

        // 테스트임.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
