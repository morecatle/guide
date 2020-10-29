package com.kakao.guide;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*
* 이 클래스는 채팅 리스트에서 채팅화면으로 넘어가는 테스트 클래스입니다.
* */

public class Test4Activity extends AppCompatActivity {
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

    // 테스트 ==> 적용은 하단 탭 눌렀을 때 작동.
    Button test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test4_layout);
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

                // 상대방 확인.
                if(chat_list.get(index).getId().equals(id)) {
                    chatOther = chat_list.get(index).getOther();
                } else if (chat_list.get(index).getOther().equals(id)) {
                    chatOther = chat_list.get(index).getId();
                }

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

    }
}


