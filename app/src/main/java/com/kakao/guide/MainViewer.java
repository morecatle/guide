package com.kakao.guide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MainViewer extends LinearLayout {
    TextView text_main_scheduleAdd, text_main_scheduleName, text_main_scheduleGPS;

    public MainViewer(Context context) {
        super(context);
        init(context);
    }
    public MainViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    // xml과 인플레이션. id값을 찾아준다.
    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.schedule_main,this,true);
        text_main_scheduleAdd = (TextView)findViewById(R.id.text_main_scheduleAdd);
        text_main_scheduleName = (TextView)findViewById(R.id.text_main_scheduleName);
        text_main_scheduleGPS = (TextView)findViewById(R.id.text_main_scheduleGPS);
    }
    // 데이터 지정.
    public void setItem(ScheduleMainVO scheduleMainVO){
        text_main_scheduleAdd.setText(scheduleMainVO.getScheduleAdd());
        text_main_scheduleName.setText(scheduleMainVO.getSheduleName());
        text_main_scheduleGPS.setText(scheduleMainVO.getScheduleGPS());
    }
}
