package com.kakao.guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    static String code = "ORF7623";   // 현재 유저 코드.
    String temp = "";
    String travelName = ""; // 삭제를 위한 현재일정 저장문자열.

    List<String> list = new ArrayList<String>();
    List<String> peopleList;
    //ArrayList<String> searchCode = new ArrayList<String>();

    String sendCode = "";
    ImageView image_add;  // 회원추가
    AlertDialog.Builder ad;
    LinearLayout layout_user_sort, layout_user_list, layout_user_none;
    TextView text_user_people, text_user_sort, text_user_new;

    ArrayList<UserHelperClass> user_list = new ArrayList<>();
    ImageView image_back;
    ListView user_listView;

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist_layout);

        // 연결.
        image_back = (ImageView)findViewById(R.id.image_back);
        image_add = (ImageView)findViewById(R.id.image_add);
        layout_user_sort = (LinearLayout)findViewById(R.id.layout_user_sort);
        layout_user_list = (LinearLayout)findViewById(R.id.layout_user_list);
        layout_user_none = (LinearLayout)findViewById(R.id.layout_user_none);
        text_user_people = (TextView)findViewById(R.id.text_user_people);
        text_user_sort = (TextView)findViewById(R.id.text_user_sort);
        text_user_new = (TextView)findViewById(R.id.text_user_new);

        user_listView = (ListView)findViewById(R.id.user_listView);


        // 리스트에 넣을 아답터를 정의.
        final UserAdapter adapter = new UserAdapter(getApplicationContext(), R.layout.userlist_item, user_list);
        ((ListView) findViewById(R.id.user_listView)).setAdapter(adapter);
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("travel");


        // 리스트 출력 절차.
        /*
        *   1. travel(일정)에서 본인코드에 맞는 일정들을 조회한다.
        *   2. 조회 시 visible(사용중) 상태가 use인 것을 찾는다.
        *   3. 해당 일정에 속한 인원들의 정보를 user테이블에서 찾고 리스트에 출력한다.
        * */
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 일정 중에서 내 일정이면서 사용중인 일정.
                Log.d("how", "조회 진입");
                if(snapshot.getValue(ScheduleVO.class).getOwner().equals(code)) {
                    if(snapshot.getValue(ScheduleVO.class).getVisible().equals("use")) {
                        // ','가 없으면 가이드 혼자라는 뜻.
                        Log.d("how", "사용중 조회 확인.");
                        travelName = snapshot.getValue(ScheduleVO.class).getName();
                        if(snapshot.getValue(ScheduleVO.class).getPeople().contains(",")) {
                            // 속한 인원들을 리스트에 저장.
                            temp = snapshot.getValue(ScheduleVO.class).getPeople();
                            list = Arrays.asList(temp.split(","));
                            Log.d("how",list.get(0));

                            reference = rootNode.getReference("user");

                            for(final String name : list) {
                                reference.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        if(snapshot.getValue(UserHelperClass.class).getCode().equals(name)) {
                                            UserHelperClass value = snapshot.getValue(UserHelperClass.class); // 괄호 안 : 꺼낼 자료 형태
                                            user_list.add(value);
                                            Log.d("how", value.getGender());
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
                            }
                        } else {
                            // 가이드 혼자 있을경우.
                        }
                    }
                }
//                UserHelperClass value = snapshot.getValue(UserHelperClass.class); // 괄호 안 : 꺼낼 자료 형태
//                user_list.add(value);
//                adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
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


        // 아이템 길게 클릭 행동. 삭제하기.
        user_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("how", "롱클릭 인식됨.");

                // 선택한 리스트: schedule_list.get(i).getName()
                Log.d("how", user_list.get(position).getName()+"을 불렀습니다.");

                AlertDialog.Builder builder= new AlertDialog.Builder(UserListActivity.this);
                //builder에게 옵션주기
                builder.setTitle("회원 삭제");
                builder.setMessage("이 회원를 삭제할까요?");


                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DB 기준으로 삭제 진행.
                        reference = rootNode.getReference("travel");
                        String people = reference.child(travelName).child("people").toString();

                        // new 선언 안해주면 삭제안됨;; Iterator가 해결책인데 그냥 이렇게 함.
                        peopleList = new ArrayList<String>(Arrays.asList(temp.split(",")));

                        // 삭제될 인원의 코드.
                        String delete = user_list.get(position).getCode();
                        if(peopleList.contains(delete)) {
                            peopleList.remove(peopleList.indexOf(delete));
                        }
                        String temp = "";

                        // 이제 이 인원들을 ,조합 후 저장.
                        for(int x=0; x<peopleList.size(); x++) {
                            if(x==peopleList.size()-1) {
                                temp+=peopleList.get(x);
                            } else {
                                temp += peopleList.get(x) + ",";
                            }
                        }
                        reference.child(travelName).child("people").setValue(temp);

                        // 출력리스트 기준으로 삭제 진행.
                        user_list.remove(position);
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



        // 회원추가 코드입력 부분.
        image_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad = new AlertDialog.Builder(UserListActivity.this);
                ad.setTitle("코드 입력");
                ad.setMessage("코드를 입력하면 해당 인원에게 푸쉬알람이 전송됩니다.");
                final EditText et = new EditText(UserListActivity.this);
                ad.setView(et);

                ad.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("NavListActivity: ", "회원코드 추가");
                        // 코드 입력 확인.
                        sendCode = et.getText().toString();
                        Log.d("NavListActivity: ", "회원코드 입력==> "+sendCode);
                        dialog.dismiss();   // 닫기.
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("NavListActivity: ", "회원코드 추가 취소");
                        dialog.dismiss();   // 닫기.
                    }
                });
                ad.show();
            }
        });

        // 뒤로가기
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
