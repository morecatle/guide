package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register2Activity extends AppCompatActivity {
    TextView code, genderMan, genderWoman;
    EditText name, pass, rePass, mail;
    Button storeOk;
    String stCode, stName, stGender, stPass, stRePass, stMail, stPhone;
    Boolean isManSelected=false; // 성별선택여부.
    Boolean isWomanSelected=false; // 성별선택여부.
    Boolean done=false; // 오류없이 잘 입력했는지.
    Boolean answer = false; // 코드가 중복인지 여부.
    Intent intent2;
    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_layout);

        // ㅁㅁㅁㅁㅁ 이 클래스만 실행 시 주석 처리를 해야 정상실행됩니다.
        Intent intent = getIntent();
        if(intent.getExtras().getString("phone")!=null) {
            stPhone = intent.getExtras().getString("phone");
        }

        /////

        code = (TextView)findViewById(R.id.text_code);
        genderMan = (TextView)findViewById(R.id.text_genderMan);
        genderWoman = (TextView)findViewById(R.id.text_genderWoman);
        name = (EditText)findViewById(R.id.edit_name);
        pass = (EditText)findViewById(R.id.edit_pass);
        rePass = (EditText)findViewById(R.id.edit_rePass);
        mail = (EditText)findViewById(R.id.edit_mail);
        storeOk = (Button)findViewById(R.id.btn_storeOk);

        String result = "";
        do {
            //result = "JUE7491";
            result = randomCode();
            code.setText(result);
        } while(isCode(result));
        // 조건: 중복이면 반복. 중복이면 true 반환.


        // 성별 선택.
        genderMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isManSelected != true) { // 선택되지 않았을 때.
                    if(isWomanSelected==true) { // 만약 여자가 선택되었다면?
                        genderWoman.setBackgroundResource(R.drawable.white_edittext);
                        isWomanSelected = false;
                    }
                    genderMan.setBackgroundResource(R.drawable.isgender_textview);
                    isManSelected = true;
                    stGender = "남";
                } else {
                    genderMan.setBackgroundResource(R.drawable.white_edittext);
                    isManSelected = false;
                }

            }
        });
        genderWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWomanSelected != true) { // 선택되지 않았을 때.
                    if(isManSelected==true) { // 만약 여자가 선택되었다면?
                        genderMan.setBackgroundResource(R.drawable.white_edittext);
                        isWomanSelected = false;
                    }
                    genderWoman.setBackgroundResource(R.drawable.isgender_textview);
                    isWomanSelected = true;
                    stGender = "여";
                } else {
                    genderWoman.setBackgroundResource(R.drawable.white_edittext);
                    isWomanSelected = false;
                }
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                stPass = pass.getText().toString().trim();
                System.out.println(stPass);
            }
        });


        // 비밀번호 중복 확인.
        rePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때

            }
            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때
                if(!(stPass.equals(rePass.getText().toString().trim()))) {
                    rePass.setError("중복이 아닙니다.");
                    done = false;
                } else {
                    done = true;
                }
                //rePass.setError("중복이 아닙니다.");
            }
        });

        // 이메일 형식 체크.
        mail.addTextChangedListener(new TextWatcher() {
            String emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            String email;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = mail.getText().toString().trim();
                if (email.matches(emailValidation) && s.length() > 0) {
                    done = true;
                } else {
                    mail.setError("이메일 형식으로 입력해주세요.");
                    done = false;
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 회원가입 버튼.
        storeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stName = name.getText().toString().trim();
                stPass = pass.getText().toString().trim();
                stRePass = rePass.getText().toString().trim();
                stMail = mail.getText().toString().trim();

                // 입력창 빈칸여부.
                if(stName.isEmpty()) {
                    name.setError("이름을 입력해주세요.");
                    name.requestFocus();
                    return;
                }
                if(isManSelected==false&&isWomanSelected==false) {
                    // 성별이 아직 선택안됐다면.
                    genderWoman.setError("성별을 선택해주세요.");
                    genderWoman.requestFocus();
                    return;
                }
                if(stPass.isEmpty()) {
                    pass.setError("비밀번호를 입력해주세요.");
                    pass.requestFocus();
                    return;
                }
                if(stRePass.isEmpty()) {
                    rePass.setError("중복을 확인해주세요.");
                    rePass.requestFocus();
                    return;
                }
                if(stMail.isEmpty()) {
                    mail.setError("이메일은 추후 비밀번호 확인을 위해 입력해주세요.");
                    mail.requestFocus();
                    return;
                }

                if(done) { // 입력 제대로 하면...
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("user");
                    UserHelperClass helperClass = new UserHelperClass(stCode, stName,stGender, stPass, stMail, stPhone+"0,0");

                    reference.child(stCode).setValue(helperClass);
                    intent2 = new Intent(Register2Activity.this, MainActivity.class);
                    intent2.putExtra("phone", "01063462260");
                    startActivity(intent2);
                    finish();
                }
            }
        });
    }

    //제작된 코드가 중복인지 확인하는 메소드
    private boolean isCode(final String result) {
        // 나온 코드가 DB에 조회되는지?
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("user/");

        myRef.orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserHelperClass userHelperClass = snapshot.getValue(UserHelperClass.class);
                if(userHelperClass.getCode().equals(result)) {
                    answer = true;
                    //System.out.println("중복이다~~~");
//                    System.out.println(userHelperClass.getName());
//                    System.out.println("닥쵸");
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
        return answer;
    }

    // 최댓값, 최솟값을 제시하면 해당 범위내의 랜덤 코드 값을 리턴.
    private String randomCode() {
        // 코드는 3자리 알파벳, 4자리 숫자로 랜덤값.
        String result[] = new String[7];
        String answer="";
        int max = 90;   //최대값
        int min = 65;   //최솟값

        result[0] = (char)((int)(Math.random()*((max-min)+1))+min) + "";
        result[1] = (char)((int)(Math.random()*((max-min)+1))+min) + "";
        result[2] = (char)((int)(Math.random()*((max-min)+1))+min) + "";
        for(int a=3; a<result.length; a++) {
            result[a]=(int)(Math.random()*9)+1+"";
        }

        for(int x=0; x<result.length; x++) {
            answer += result[x];
        }

        stCode = answer;
        return answer;
    }
}
