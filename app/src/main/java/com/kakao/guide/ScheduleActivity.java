package com.kakao.guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

/*
 *   일정 씬입니다.
 * */
public class ScheduleActivity extends AppCompatActivity {
    ArrayList<ScheduleVO> schedule_list = new ArrayList<>();
    ImageView back, schedule_listAdd;
    ListView schedule_listView;

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    String isUse = "";  // 활성화 상태 저장변수.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        // 연결.
        back = (ImageView)findViewById(R.id.image_back);
        schedule_listAdd = (ImageView)findViewById(R.id.schedule_listAdd);
        schedule_listView = (ListView)findViewById(R.id.schedule_listView);


        // 채팅 리스트에 넣을 아답터를 정의.
        final ScheduleAdapter adapter = new ScheduleAdapter(getApplicationContext(), R.layout.schedule_item, schedule_list);
        ((ListView) findViewById(R.id.schedule_listView)).setAdapter(adapter);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("travel");


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 현재 사용중인 일정 저장.
                if(snapshot.getValue(ScheduleVO.class).getVisible().equals("use")) {
                    isUse = snapshot.getValue(ScheduleVO.class).getName();
                    Log.d("testing", "현재 사용중인 일정: "+isUse);
                }
                ScheduleVO value = snapshot.getValue(ScheduleVO.class); // 괄호 안 : 꺼낼 자료 형태
                schedule_list.add(value);
                adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
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
        schedule_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("testing", "항목 선택: "+schedule_list.get(position).getName());

                // 사용중인 일정이 없으면.
                if (isUse.equals("")) {
                    reference.child(schedule_list.get(position).getName()).child("visible").setValue("use");
                    schedule_list.get(position).setVisible("use");
                    adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                    isUse=schedule_list.get(position).getName();
                } else {
                    // 만약 현재 사용중인 항목이 아닌 다른항목을 선택할 시.
                    if(!isUse.equals(schedule_list.get(position).getName())) {
                        // DB 최신화
                        reference.child(isUse).child("visible").setValue("none");
                        reference.child(schedule_list.get(position).getName()).child("visible").setValue("use");

                        // 리스트 최신화
                        for(int x=0; x<schedule_list.size(); x++) {
                            if(schedule_list.get(x).getName().equals(isUse)) {
                                Log.d("testing", "바꿔질 "+isUse+"의 위치는 "+x);
                                schedule_list.get(x).setVisible("none");
                                Log.d("testing", "바꿔진 "+isUse+"의 상태는 "+schedule_list.get(x).getVisible());
                            }
                        }
                        schedule_list.get(position).setVisible("use");
                        adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.

                        isUse=schedule_list.get(position).getName();
                    } else {
                        // 같은 항목을 선택했다면
                        reference.child(schedule_list.get(position).getName()).child("visible").setValue("none");
                        schedule_list.get(position).setVisible("none");
                        adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                        isUse = "";
                    }
                }
            }
        });

        // 아이템 길게 클릭 행동. 삭제하기.
        schedule_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int i, long id) {
                Log.d("how", "롱클릭 인식됨.");

                // 선택한 리스트: schedule_list.get(i).getName()
                Log.d("how", schedule_list.get(i).getName()+"을 불렀습니다.");

                final int positon = i;
                AlertDialog.Builder builder= new AlertDialog.Builder(ScheduleActivity.this);
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

        // 뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
/*
넣는과정.
rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("travel");
                ScheduleVO scheduleVO = new ScheduleVO("남도여행", "DBSSE12","none", "DFE1, SDB2", "(3.323, 3.265)");
                reference.child("남도여행").setValue(scheduleVO);


*/