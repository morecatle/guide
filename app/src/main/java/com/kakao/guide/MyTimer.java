package com.kakao.guide;

import android.os.CountDownTimer;

public class MyTimer extends CountDownTimer {
    // 생성자. 타이머 총 시간, 카운트다운되는 시간.
    // ex. myTimer = new MyTimer(60000, 1000);는  총 시간이 60초이고 onTick()이 호출되는 시간 간격은 1초.
    public MyTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
    // 생성자 인수 countDownInterval로 지정된 시간 간격마다 호출되는 함수.
    @Override
    public void onTick(long millisUntilFinished) {
        // 넘어오는 인자는 현재 타이머의 남은 시간.

    }

    @Override
    public void onFinish() {
        // 타이머가 다 끝났을 때 호출되는 함수.
    }
}
/*
*   .start()는 타이머 시작.
*   .cancel()은 타이머 취소.
*
*
* */
