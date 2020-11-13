package com.kakao.guide;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback{

    // content_main의 nav_host_fragment가 주 화면입니다.

    private AppBarConfiguration mAppBarConfiguration;

    // 구글 맵.
    private GoogleMap mMap;
    private Marker currentMarker = null;
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    // 하단 메뉴.
    SlidingDrawer drawer;
    Button btn_chat, btn_lang, btn_travel;
    String focus ="";

    LinearLayout layout_chat, layout_lang_select, layout_lang_voice, layout_lang_text, layout_travel;
    Button btn_choose_voice, btn_choose_text;
    // 문자 번역 부분.
    EditText editText;
    Button test;
    TextView textView, text_input, text_output;
    ImageView image_change;
    Boolean translate = false; // 0: input, 1: output;

    // 채팅 탭 부분.=================================================================================

    // 채팅리스트 화면.
    ArrayList<ChatVO> chat_list = new ArrayList<>();
    ListView chat_listView;

    // 채팅 창 화면.
    ArrayList<ChatVO> chat_view = new ArrayList<>();
    LinearLayout layout_chatList, layout_chatView;
    ListView testing_chatView;
    EditText testing_editText;
    Button testing_button;

    // 문자주고받은 인원 저장.
    Set<String> set = new HashSet<String>();

    // 나와 상대방 아이디.
    String id = "";
    String other = "";
    //==============================================================================================

    // 주소 넘겨받기.
    Intent sendIntent;
    String goLocation;
    private static final int PLACE_PICKER_REQUEST =1;
    Button btn_GoSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // 주소 넘겨받기.
        sendIntent = getIntent();
        goLocation = sendIntent.getStringExtra("thisLocation");
        //btn_GoSearch = findViewById(R.id.btn_GoSearch);


        // app_bar_main.xml
        // 툴바   임시적으로 gone.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 하단 메뉴
        drawer = (SlidingDrawer)findViewById(R.id.slide);
        btn_chat = (Button)findViewById(R.id.btn_chat);
        btn_lang = (Button)findViewById(R.id.btn_lang);
        btn_travel = (Button)findViewById(R.id.btn_travel);
        layout_chat = (LinearLayout)findViewById(R.id.layout_chat);
        layout_lang_select = (LinearLayout)findViewById(R.id.layout_lang_select);
        layout_lang_voice = (LinearLayout)findViewById(R.id.layout_lang_voice);
        layout_lang_text = (LinearLayout)findViewById(R.id.layout_lang_text);
        layout_travel = (LinearLayout)findViewById(R.id.layout_travel);
        btn_choose_voice = (Button)findViewById(R.id.btn_choose_voice);
        btn_choose_text = (Button)findViewById(R.id.btn_choose_text);

        // 문자 번역 부분.
        editText = (EditText)findViewById(R.id.editText);
        test = (Button)findViewById(R.id.btn_test);
        textView = (TextView)findViewById(R.id.textView);
        text_input = (TextView)findViewById(R.id.text_input);
        text_output = (TextView)findViewById(R.id.text_output);
        image_change = (ImageView)findViewById(R.id.image_change);
        registerForContextMenu(text_input);
        registerForContextMenu(text_output);

        // 채팅 탭 부분.=============================================================================
        chat_listView = findViewById(R.id.testing_list);
        // 채팅창 관련 연결.
        layout_chatList = (LinearLayout)findViewById(R.id.layout_chatList);
        layout_chatView = (LinearLayout)findViewById(R.id.layout_chatView);
        testing_editText = (EditText)findViewById(R.id.testing_edt);
        testing_button = (Button)findViewById(R.id.testing_btn);
        testing_chatView = (ListView)findViewById(R.id.testing_chatView);

        // DB 연결.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference chat_myRef = database.getReference("message");

        id = "test";
        other = "test3";


        // 채팅 리스트에 넣을 아답터를 정의.
        final ChatListAdapter adapter = new ChatListAdapter(getApplicationContext(), R.layout.chatlist_layout, chat_list, id);
        ((ListView) findViewById(R.id.testing_list)).setAdapter(adapter);
        //chat_listView.setAdapter(adapter);
        Log.d("testing", "1");

        // 채팅 창에 넣을 아답터 정의.
        final ChatAdapter adapterChat = new ChatAdapter(getApplicationContext(), R.layout.chat_item, chat_view, id);
        ((ListView) findViewById(R.id.testing_chatView)).setAdapter(adapterChat);
        //==========================================================================================

//        btn_GoSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
//                try {
//                    Intent intent = intentBuilder.build(MainActivity.this);
//                    startActivityForResult(intent,PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });




        // 하단 네비게이션 드로어 부분.////////////////////////////////////////////////////////////////
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //drawer.animateClose();
                // 드로워가 열려있는지?
                if(drawer.isOpened()) {
                    if(focus.equals("번역")) {
                        layout_lang_select.setVisibility(View.GONE);
                        layout_lang_voice.setVisibility(View.GONE);
                        layout_lang_text.setVisibility(View.GONE);
                        layout_chat.setVisibility(View.VISIBLE);
                        focus = "채팅";
                    } else if(focus.equals("여행")) {
                        layout_travel.setVisibility(View.GONE);
                        layout_chat.setVisibility(View.VISIBLE);
                        focus = "채팅";
                    } else {
                        //btn_chat.setBackgroundResource(R.drawable.cat); // 채팅 비활성화 아이콘으로 변경.
                        layout_chat.setVisibility(View.GONE);
                        drawer.animateClose();
                    }
                } else {
                    //btn_chat.setBackgroundResource(R.drawable.cat); // 채팅 활성화 아이콘으로 변경.
                    drawer.animateOpen();
                    layout_chat.setVisibility(View.VISIBLE);
                    focus = "채팅";
                }

                // 채팅창으로 넘어가 있었다면 채팅 리스트로 화면 전환.
                if(layout_chatView.getVisibility()==View.VISIBLE)  {
                    layout_chatList.setVisibility(View.VISIBLE);
                    layout_chatView.setVisibility(View.GONE);
                    chat_view.clear();
                }

                // DB에서 채팅리스트 불러오기.
                chat_myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d("testing", "//");
                        String listOther = "";
                        //int listIndex = 0;

                        // 보낸이 혹은 받은이가 사용자 아이디가 있을경우 ==> 메세지를 보내거나 받은 이력이 있다.
                        if(snapshot.getValue(ChatVO.class).getId().equals(id)||snapshot.getValue(ChatVO.class).getOther().equals(id)) {
                            // 문자 주고받은 상대방 확인.
                            if(snapshot.getValue(ChatVO.class).getId().equals(id)) {
                                listOther = snapshot.getValue(ChatVO.class).getOther();
                            } else if(snapshot.getValue(ChatVO.class).getOther().equals(id)) {
                                listOther = snapshot.getValue(ChatVO.class).getId();
                            }


                            // 저장된 리스트에서 id나 other중 상대방의 이름이 있을경우 이미 저장된 것이다.
                            for(int x=0; x<chat_list.size(); x++) {
                                String temp = "";
                                if(chat_list.get(x).getId().equals(listOther)||chat_list.get(x).getOther().equals(listOther)) {
                                    // 해당 리스트는 삭제 후 새로 저장. 즉 갱신하는 과정이 필요.
                                    chat_list.remove(x);
                                }
                            } // 만약 저장된 내용이 없다면 이 반복문은 넘어가는 것.

                            ChatVO value = snapshot.getValue(ChatVO.class); // 괄호 안 : 꺼낼 자료 형태
                            value.setId(listOther);
                            chat_list.add(value);
                            adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                            // 근데 굳이 리턴이 있어야 하나?
                            return;
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
        });

        // 채팅창 전송버튼.
        testing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testing_editText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                } else {
                    Date today = new Date();
                    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");

                    StringBuffer sb = new StringBuffer(testing_editText.getText().toString());
                    if (sb.length() >= 15) {
                        for (int i = 1; i <= sb.length() / 15; i++) {
                            sb.insert(15 * i, "\n");
                        }
                    }
                    //list.add(new ChatVO(R.drawable.profile1, id, sb.toString(), timeNow.format(today)));
                    //adapter.notifyDataSetChanged();
                    chat_myRef.push().setValue(new ChatVO(id, other, sb.toString(), timeNow.format(today)));
                    testing_editText.setText("");
                }
            }
        });

        // 채팅리스트의 채팅방을 클릭 시 해당 채팅방으로 이동해야한다.
        chat_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long thisId) {
                int index = position;
                String chatOther = "";

                // 처음엔 other
                // 상대방 확인.
//                if(chat_list.get(position).getId().equals(id)) {
//                    chatOther = chat_list.get(position).getOther();
//                } else if (chat_list.get(position).getOther().equals(id)) {
//                    chatOther = chat_list.get(position).getId();
//                }
                other = chat_list.get(position).getId();
                Log.d("postion", chatOther);

                // 레이아웃 전환.
                layout_chatList.setVisibility(View.GONE);
                layout_chatView.setVisibility(View.VISIBLE);


                chat_myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.getValue(ChatVO.class).getId().equals(id)&&snapshot.getValue(ChatVO.class).getOther().equals(other)) {
                            ChatVO value = snapshot.getValue(ChatVO.class); // 괄호 안 : 꺼낼 자료 형태
                            chat_view.add(value);
                            adapterChat.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                            return;
                        }
                        if(snapshot.getValue(ChatVO.class).getId().equals(other)&&snapshot.getValue(ChatVO.class).getOther().equals(id)) {
                            ChatVO value = snapshot.getValue(ChatVO.class); // 괄호 안 : 꺼낼 자료 형태
                            chat_view.add(value);
                            adapterChat.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                            return;
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
        });
        // 채팅부분 끝.

        btn_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 드로워가 열려있는지?
                if(drawer.isOpened()) {
                    if(focus.equals("채팅")) {
                        layout_chat.setVisibility(View.GONE);
                        layout_lang_select.setVisibility(View.VISIBLE);
                        focus = "번역";
                    } else if(focus.equals("여행")) {
                        layout_travel.setVisibility(View.GONE);
                        layout_lang_select.setVisibility(View.VISIBLE);
                        focus = "번역";
                    } else {
                        //btn_chat.setBackgroundResource(R.drawable.cat); // 번역 비활성화 아이콘으로 변경.
                        layout_lang_select.setVisibility(View.GONE);
                        layout_lang_voice.setVisibility(View.GONE);
                        layout_lang_text.setVisibility(View.GONE);
                        drawer.animateClose();
                    }
                } else {
                    //btn_chat.setBackgroundResource(R.drawable.cat); // 번역 활성화 아이콘으로 변경.
                    drawer.animateOpen();
                    layout_lang_select.setVisibility(View.VISIBLE);
                    focus = "번역";
                }
            }
        });

        btn_travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 드로워가 열려있는지?
                if(drawer.isOpened()) {
                    if(focus.equals("채팅")) {
                        layout_chat.setVisibility(View.GONE);
                        layout_travel.setVisibility(View.VISIBLE);
                        focus = "여행";
                    } else if(focus.equals("번역")) {
                        layout_lang_select.setVisibility(View.GONE);
                        layout_lang_voice.setVisibility(View.GONE);
                        layout_lang_text.setVisibility(View.GONE);
                        layout_travel.setVisibility(View.VISIBLE);
                        focus = "여행";
                    } else {
                        //btn_chat.setBackgroundResource(R.drawable.cat); // 여행 비활성화 아이콘으로 변경.
                        layout_travel.setVisibility(View.GONE);
                        drawer.animateClose();
                    }
                } else {
                    //btn_chat.setBackgroundResource(R.drawable.cat); // 여행 활성화 아이콘으로 변경.
                    drawer.animateOpen();
                    layout_travel.setVisibility(View.VISIBLE);
                    focus = "여행";
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        btn_choose_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_choose_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_lang_select.setVisibility(View.GONE);
                layout_lang_text.setVisibility(View.VISIBLE);
            }
        });

        // 문자 번역
        text_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate = false;
                v.showContextMenu();
            }
        });
        text_output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate = true;
                v.showContextMenu();
            }
        });
        image_change.setOnClickListener(new View.OnClickListener() {
            String input="", output="";
            @Override
            public void onClick(View v) {
                output = text_input.getText().toString();
                input = text_output.getText().toString();
                text_input.setText(input);
                text_output.setText(output);
                output = editText.getText().toString();
                input = textView.getText().toString();
                editText.setText(input);
                textView.setText(output);
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TranslateAPI translateAPI = new TranslateAPI(
                        getEnglish(text_input.getText().toString()),
                        getEnglish(text_output.getText().toString()),
                        editText.getText().toString()
                );

                translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                    @Override
                    public void onSuccess(String s) {
                        textView.setText(s);
                    }

                    @Override
                    public void onFailure(String s) {
                        System.out.print("번역 오류 발생: "+s);
                    }
                });
            }
        });




        // 우측하단 메세지버튼   /제거해도 무관.
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar는 하단에 노출되는 바.
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */

        // activity_main.xml
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // 네비게이션메뉴 내용물 정의.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_list, R.id.nav_profile, R.id.nav_schedule)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = item.getItemId();
                Intent intent;
                switch (position) {
                    case R.id.nav_list:
                        Toast.makeText(getApplicationContext(), "목록 선택", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), UserListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_profile:
                        Toast.makeText(getApplicationContext(), "프로필 선택", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_schedule:
                        Toast.makeText(getApplicationContext(), "일정 선택", Toast.LENGTH_LONG).show();
                        break;

                }
                // 닫아주기.
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });


        // 구글 맵.
//        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        mLayout = findViewById(R.id.layout_main);                                                   // 구글맵 layout아이디.

        // 위치 수신되는 속도 조정.
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);                                           // 지도 콜백을 등록.
    }

    // 뒤로가기 동작.
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    // 우측상단 점세개.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ
    // 구글 맵.
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng SEOUL = new LatLng(37.56, 126.97);
//
//        // 마커가 표시될 위치, 정보 지정.
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        mMap.addMarker(markerOptions);
//
//        // 카메라 이동. 숫자가 커질수록 자세히 확대.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 17));


        Log.d(TAG, "onMapReady :");
        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            startLocationUpdates(); // 3. 위치 업데이트 시작
        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.(하단에 출력.)
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 맵을 터치했을 때.
                Log.d( TAG, "onMapClick :");
            }
        });
    }

    // 현재 위치 반환.
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());
                String markerTitle = getCurrentAddress(currentPosition);                            // 주소.
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())              // 위도 + 경도.
                        + " 경도:" + String.valueOf(location.getLongitude());
                Log.d(TAG, "onLocationResult : " + markerSnippet);
                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
            }
        }
    };

    // 위치 업데이트.
    private void startLocationUpdates() {
        // 먼저 현재 gps를 제공받을 수 있는 환경인지 체크.
        if (!checkLocationServicesStatus()) {
            // 다이얼로그 출력.
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }
            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()); // 주소 업데이트.(속도, 마커정보(위치),
            if (checkPermission())
                mMap.setMyLocationEnabled(true);                                                    // 지도에 파란점과 현위치 찾기 버튼 출력.
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mMap!=null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // 주소 반환하기.
    public String getCurrentAddress(LatLng latlng) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        currentMarker = mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }

    // 디폴트 마커 세팅.
    public void setDefaultLocation() {
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        // 마커 옵션.
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);                                                           // 타이틀
        markerOptions.snippet(markerSnippet);                                                       // 서브타이틀
        markerOptions.draggable(true);                                                              // 마커가 드래그 가능하도록 지정.
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); // 마커 디자인 지정.
        currentMarker = mMap.addMarker(markerOptions);                                              // 마커 등록.

        // 카메라 옵션.
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }
    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        return false;
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }
    //여기부터는 GPS 활성화를 위한 메소드들(다이얼로그 출력)
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);                                                                // 뒤로가기와 배경 터치로 벗어나기 가능.
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }

    public void onSlidingDrawer(View view) {
    }

    //문자 번역 부분.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menu.setHeaderTitle("언어 선택");
        menuInflater.inflate(R.menu.translate_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.KOREAN:
                if(translate) {
                    text_output.setText("한국어");
                } else {
                    text_input.setText("한국어");
                }
                break;
            case R.id.ENGLISH:
                if(translate) {
                    text_output.setText("영어");
                } else {
                    text_input.setText("영어");
                }
                break;
            case R.id.CHINESE_SIMPLIFIED:
                if(translate) {
                    text_output.setText("중국어");
                } else {
                    text_input.setText("중국어");
                }
                break;
            case R.id.CHINESE_TRADITIONAL:
                if(translate) {
                    text_output.setText("라틴어");
                } else {
                    text_input.setText("라틴어");
                }
                break;
            case R.id.JAPANESE:
                if(translate) {
                    text_output.setText("일본어");
                } else {
                    text_input.setText("일본어");
                }
                break;
            case R.id.RUSSIAN:
                if(translate) {
                    text_output.setText("러시아어");
                } else {
                    text_input.setText("러시아어");
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private String getEnglish(String place) {
        String temp = place;

        if(temp.equals("한국어")) {
            return Language.KOREAN;
        }
        else if(temp.equals("영어")) {
            return Language.ENGLISH;
        }
        else if(temp.equals("중국어")) {
            return Language.CHINESE;
        }
        else if(temp.equals("라틴어")) {
            return Language.LATIN;
        }
        else if(temp.equals("일본어")) {
            return Language.JAPANESE;
        }
        else if(temp.equals("러시아어")) {
            return Language.RUSSIAN;
        }
        else
            return null;
    }
}
