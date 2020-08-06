package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText id;
    TextView find;
    TextView register;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);




        id = (EditText) findViewById(R.id.edit_id);
        find = (TextView) findViewById(R.id.text_id);
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
            }
        });
    }
}
