package com.kakao.guide;

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

public class Register2Activity extends AppCompatActivity {
    TextView code, genderMan, genderWoman;
    EditText name, pass, rePass, mail;
    Button storeOk;
    String stCode, stName, stGender, stPass, stRePass, stMail, stPhone = "01063462260";
    Boolean isManSelected=false; // 성별선택여부.
    Boolean isWomanSelected=false; // 성별선택여부.
    Boolean done=false; // 오류없이 잘 입력했는지.

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_layout);

        code = (TextView)findViewById(R.id.text_code);
        genderMan = (TextView)findViewById(R.id.text_genderMan);
        genderWoman = (TextView)findViewById(R.id.text_genderWoman);
        name = (EditText)findViewById(R.id.edit_name);
        pass = (EditText)findViewById(R.id.edit_pass);
        rePass = (EditText)findViewById(R.id.edit_rePass);
        mail = (EditText)findViewById(R.id.edit_mail);
        storeOk = (Button)findViewById(R.id.btn_storeOk);


        code.setText(randomCode());
        /*
        while(isDuplication(randomCode())) {
            code.setText(randomCode());
        }
*/


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

        rePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                if(!pass.getText().toString().trim().equals(rePass.getText().toString().trim())) {
                    rePass.setError("중복이 아닙니다.");
                    done = false;
                } else {
                    done = true;
                }
                //rePass.setError("중복이 아닙니다.");
            }
            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때
            }
        });

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

        storeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stName = name.getText().toString().trim();
                stPass = pass.getText().toString().trim();
                stRePass = rePass.getText().toString().trim();
                stMail = mail.getText().toString().trim();







                // 입력창이 빈칸일 때.
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
                    sendDB("", "", "", '무', "");

                }


                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("user");
                UserHelperClass helperClass = new UserHelperClass(stCode, stName,stGender, stPass, stMail, stPhone);

                reference.child(stCode).setValue(helperClass);

                //reference.setValue(helperClass);

            }
        });
    }

    // 입력 후 DB에 정보 넘기기.
    private void sendDB(String code, String name, String email, char gender, String phone) {

    }

    // 매개변수로 문자열을 받고 해당값을 DB에 중복검사.
    private Boolean isDuplication(String str) {
        Boolean result = true;




        return result;
    }

    // 최댓값, 최솟값을 제시하면 해당 범위내의 값을 리턴.
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
