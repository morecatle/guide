package com.kakao.guide;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    // 레이아웃 선언부.
    LinearLayout text, type, store;
    Button send, sendOk, storeOk;
    Animation fadein, fadeout;

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

        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        text.startAnimation(fadein);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //넘어가는 부분.
                text.startAnimation(fadeout);
                text.setVisibility(View.GONE);
                type.setVisibility(View.VISIBLE);
                type.startAnimation(fadein);
            }
        });
        sendOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //넘어가는 부분.
                type.startAnimation(fadeout);
                type.setVisibility(View.GONE);
                store.setVisibility(View.VISIBLE);
                store.startAnimation(fadein);
            }
        });
    }
}
