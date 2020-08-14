package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    EditText id, pw;
    TextView find, register;
    Button login;
    Intent intent;

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        id = (EditText) findViewById(R.id.edit_id);
        pw = (EditText) findViewById(R.id.edit_pw);
        login = (Button)findViewById(R.id.btn_login);

        find = (TextView) findViewById(R.id.text_find);
        register = (TextView) findViewById(R.id.text_register);
        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);


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
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("user").child(id.getText().toString().trim());

                if(reference.getKey()==id.getText().toString().trim()) {
                    intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), FindActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
