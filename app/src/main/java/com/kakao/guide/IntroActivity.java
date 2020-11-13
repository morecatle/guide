package com.kakao.guide;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

public class IntroActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), PresentActivity.class); //넘기기.
                startActivity(intent);
                finish();
            }
        },3000); // 3초 뒤에 Runner 객체 실행.
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
