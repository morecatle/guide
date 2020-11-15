package com.kakao.guide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import me.relex.circleindicator.CircleIndicator;

public class PresentActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    int count = 0;
    String myPhone = "";
    String myCode = "";
    // 파이어베이스 연결.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("user");
    DatabaseReference schedule_myRef = database.getReference("travel");

    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.present_layout);

        // 자동 로그인: 속도는 느리지만 일단 구현.
        // 휴대폰 번호 수신.
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

        //
        final ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);


        // 화면 넘김.
        intent = new Intent(getApplicationContext(), RegisterActivity.class);

        final Button start = (Button) findViewById(R.id.start); //뷰페이지 마지막에 노출될 '시작하기'
        //final TextView next = (TextView) findViewById(R.id.next); //건너뛰기


        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d("whatthefuck", "nonono");
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        start.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        start.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        start.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.d("whatthefuck", "nonono");
            }
        });


//        if (vpPager.getCurrentItem()==2) {
//            Log.d("whatthefuck", "nonono");
////            start.setVisibility(View.VISIBLE);
////            next.setVisibility(View.INVISIBLE);
//        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });

//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(intent);
//                Log.d("whatthefuck", String.valueOf(vpPager.getCurrentItem()));
//            }
//        });

        // 핸드폰 번호가 있으면 자동로그인.
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("che", "조회된 번호"+snapshot.getValue(UserHelperClass.class).getPhone());
                Log.d("che", "지금 내 번호"+myPhone);
                if(myPhone.equals(snapshot.getValue(UserHelperClass.class).getPhone())) {
                    myCode = snapshot.getValue(UserHelperClass.class).getCode();
                    intent = new Intent(PresentActivity.this, MainActivity.class);
                    startActivity(intent);
                    //Toast.makeText(PresentActivity.this, "자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
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

        schedule_myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 만약 현재 사용중인 일정에 내 코드가 있다 ==> 일정사용중인 것.
                if (snapshot.getValue(ScheduleVO.class).getPeople().contains(myCode)) {
                    if(snapshot.getValue(ScheduleVO.class).getVisible().equals("use")) {
                        MainActivity.nowSchedule = snapshot.getValue(ScheduleVO.class).getName();
                        Log.d("testing", "현재 사용중인 일정은 "+snapshot.getValue(ScheduleVO.class).getName());
                        finish();
                    }

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
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED )
        {
            Log.v("DEBUG", "텔레폰 퍼미션이 허용됨");
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            myPhone = telManager.getLine1Number();
            Log.v("DEBUG", "A"+ myPhone +"B");

            // 테스트용
            myPhone = "6505553434";
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter{
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FirstFragment.newInstance(0, "Page # 1");
                case 1:
                    return SecondFragment.newInstance(1, "Page # 2");
                case 2:
                    return ThirdFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }


    }
}
