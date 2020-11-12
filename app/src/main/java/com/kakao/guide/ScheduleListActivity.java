package com.kakao.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *   해당 일정의 스케줄을 불러오는 씬입니다.
 *   line이라는 일정이름의 순서저장 문자열과 '일정'이 name, '좌표'가 값인 구조입니다.
 *
 * */
public class ScheduleListActivity extends AppCompatActivity {
    // 현재 사용중인 일정 수신.
    Intent intent;
    String state = "";

    // 리스트 관련.
    static String line = "";

    static ArrayList<String> scheduleList_list = new ArrayList<>();
    //ArrayList<S> scheduleList_list = new ArrayList<String[]>();


    HashMap<String, String> map = new HashMap<String, String>();


    ImageView back;
    ListView schedulelist_listView;

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    static String isUse = "";  // 활성화 상태 저장변수.

    ScheduleListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedulelist_layout);

        // 현재 사용중인 일정이름(ex. 남도여행) 수신.
        // 아이런 미친 getIntent()를 onCreate내에 써야 엑스트라 값이 수신됨 ㅁㅊ
        intent = getIntent();
        state = intent.getStringExtra("state");

        // 연결.
        back = (ImageView)findViewById(R.id.image_back);
        schedulelist_listView = (ListView)findViewById(R.id.schedulelist_listView);


        // 채팅 리스트에 넣을 아답터를 정의.
        adapter = new ScheduleListAdapter(getApplicationContext(), R.layout.schedulelist_item, scheduleList_list);
        ((ListView) findViewById(R.id.schedulelist_listView)).setAdapter(adapter);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("route");


//        reference.child(state).child("밥먹기").setValue("54.56489,48.54848");
        //reference.child(state).child("밥먹기").setValue("54.56489,48.54848");

        // 아답터에 현재 사용중인 일정 넘겨주기.
        adapter.state = state;
        // 해당 일정 안에는 line이라는 일정순서를 포함한 루트별 GPS정보가 있음. 해당 행을 화면에 출력.
        reference.child(state).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Log.d("testing", snapshot.getKey());
////                map.put(snapshot.getKey(), snapshot.getValue().toString());
////                adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
////                 getKey를 읽어서 리스트에 저장.
//                //ScheduleListVO value = snapshot.getValue(ScheduleListVO.class);
//                //snapshot.getValue();
//                //scheduleList_list.add(value);
//                //ScheduleListVO value = new ScheduleListVO(snapshot.getKey().toString(),snapshot.getValue().toString());
//                scheduleList_list.add(snapshot.getKey()+","+snapshot.getValue().toString());
//                Log.d("testing", snapshot.getKey());
//                adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.

                if(snapshot.getKey().equals("line")) {
                    // 우선순위
                    adapter.line = snapshot.getValue().toString();
                } else {
                    scheduleList_list.add(snapshot.getKey()+"="+snapshot.getValue().toString());
                    adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
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

        // 아이템 클릭 행동. 색,활성화 상태 바꾸기
        schedulelist_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("testing", "항목 선택: "+scheduleList_list.get(position).getName());

                /*

                // 사용중인 일정이 없으면.
                if (isUse.equals("")) {
                    reference.child(scheduleList_list.get(position).getName()).child("visible").setValue("use");
                    scheduleList_list.get(position).setVisible("use");
                    adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                    isUse=scheduleList_list.get(position).getName();
                } else {
                    // 만약 현재 사용중인 항목이 아닌 다른항목을 선택할 시.
                    if(!isUse.equals(scheduleList_list.get(position).getName())) {
                        // DB 최신화
                        reference.child(isUse).child("visible").setValue("none");
                        reference.child(scheduleList_list.get(position).getName()).child("visible").setValue("use");

                        // 리스트 최신화
                        for(int x=0; x<scheduleList_list.size(); x++) {
                            if(scheduleList_list.get(x).getName().equals(isUse)) {
                                Log.d("testing", "바꿔질 "+isUse+"의 위치는 "+x);
                                scheduleList_list.get(x).setVisible("none");
                                Log.d("testing", "바꿔진 "+isUse+"의 상태는 "+scheduleList_list.get(x).getVisible());
                            }
                        }
                        scheduleList_list.get(position).setVisible("use");
                        adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.

                        isUse=scheduleList_list.get(position).getName();
                    } else {
                        // 같은 항목을 선택했다면
                        reference.child(scheduleList_list.get(position).getName()).child("visible").setValue("none");
                        scheduleList_list.get(position).setVisible("none");
                        adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                        isUse = "";
                    }
                }

                 */
            }
        });

        /*

        // 아이템 길게 클릭 행동. 삭제하기.
        schedule_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int i, long id) {
                Log.d("how", "롱클릭 인식됨.");

                // 선택한 리스트: schedule_list.get(i).getName()
                Log.d("how", schedule_list.get(i).getName()+"을 불렀습니다.");

                final int positon = i;
                AlertDialog.Builder builder= new AlertDialog.Builder(ScheduleListActivity.this);
                //builder에게 옵션주기
                builder.setTitle("일정 삭제");
                builder.setMessage("이 일정를 삭제할까요?");

                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DB 기준으로 삭제 진행.
                        reference.child(schedule_list.get(i).getName()).removeValue();

                        // 출력리스트 기준으로 삭제 진행.
                        schedule_list.remove(positon);
                        adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return false;
            }
        });

        // 하단의 추가버튼.
        schedule_listAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 추가 버튼을 클릭하면 DB에 null이 먼저 추가된다. 리스트에서 이름이 null을 직시하면, 추가창을 입력하라 표시된다.
                ScheduleVO value = new ScheduleVO("null","","","","");
                schedule_list.add(value);
                adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
            }
        });




         */
        // 뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}