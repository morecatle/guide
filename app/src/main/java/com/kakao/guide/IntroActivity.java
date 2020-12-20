package com.kakao.guide;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IntroActivity extends Activity {
    String myPhone = "";    // 내 휴대전화 번호
    Handler handler = new Handler();
    // 파이어베이스 연결.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");
    DatabaseReference travelRef = database.getReference("travel");
    Intent intent;
    Boolean isUser = false;

    //메인화면에 값 전달하기 위해
    String code, name, location;
    String schedule, people;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

        Log.d("DEBUG", "onCreate: 시작");
        // 휴대폰 번호 수신.
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("DEBUG", "onStart");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED )
        {
            Log.d("DEBUG", "텔레폰 퍼미션 체크: 성공");
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            // +821063462260
            myPhone = telManager.getLine1Number();
            // 테스트용.. 유심있다면 제거.
            myPhone = "+821063462260";

            // 유심이 없는 경우.
            if(myPhone==null) {
                Toast.makeText(IntroActivity.this, "정상적인 서비스 이용을 위해 유심을 삽입해주세요.", Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent = new Intent(getApplicationContext(), MainActivity.class); //넘기기.
                        startActivity(intent);
                        finish();
                    }
                }, 3000); // 3초 뒤에 Runner 객체 실행.
            } else {
                if(myPhone.startsWith("+82")) {
                    myPhone = myPhone.replace("+82", "0");
                }

                // DB에 해당 번호가 있으면 메인화면으로...
                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(myPhone.equals(snapshot.getValue(UserHelperClass.class).getPhone())) {
                            Log.d("DEBUG", "전화번호 조회: 조회됨.");
                            isUser = true;
                            code = snapshot.getValue(UserHelperClass.class).getCode();
                            name = snapshot.getValue(UserHelperClass.class).getName();
                            location = snapshot.getValue(UserHelperClass.class).getGps();
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

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("DEBUG", "3초 후 이동.");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(isUser==true) {
                                    intent = new Intent(getApplicationContext(), MainActivity.class); //넘기기.
                                    intent.putExtra("myCode", code);
                                    intent.putExtra("myName", name);
                                    intent.putExtra("myLocation", location);
                                    intent.putExtra("people", people);
                                }
                                else {
                                    intent = new Intent(getApplicationContext(), PresentActivity.class); //넘기기.
                                }
                                startActivity(intent);
                                finish();
                            }
                        }, 2000); // 3초 뒤에 Runner 객체 실행.
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        } else {
            Log.d("DEBUG", "텔레폰 퍼미션 체크: 실패");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}