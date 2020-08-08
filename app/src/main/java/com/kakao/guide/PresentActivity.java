package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import me.relex.circleindicator.CircleIndicator;

public class PresentActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.present_layout);
        //
        final ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);


        // 화면 넘김.
        final Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);

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
