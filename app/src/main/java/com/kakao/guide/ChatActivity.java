package com.kakao.guide;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    ArrayList<ChatVO> list = new ArrayList<>();
    ListView lv;
    Button btn;
    EditText edt;
    //int[] imageID = {R.drawable.profile1, R.drawable.profile2, R.drawable.profile3};

    String id = "";
    String other = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        lv = findViewById(R.id.listView);
        edt = findViewById(R.id.test_edt);
        btn = findViewById(R.id.test_btn);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("message");

        //로그인한 아이디
        //id = getIntent().getStringExtra("id");
        id = "test3";
        other = "test";
        //lv.setAdapter(adapter);
        //list.add(new ChatVO("찡찡이", "안녕", "오후 4:42"));

        final ChatAdapter adapter = new ChatAdapter(getApplicationContext(), R.layout.chat_item, list, id);
        ((ListView) findViewById(R.id.listView)).setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                } else {
                    Date today = new Date();
                    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");

                    StringBuffer sb = new StringBuffer(edt.getText().toString());
                    if (sb.length() >= 15) {
                        for (int i = 1; i <= sb.length() / 15; i++) {
                            sb.insert(15 * i, "\n");
                        }
                    }
                    //list.add(new ChatVO(R.drawable.profile1, id, sb.toString(), timeNow.format(today)));
                    //adapter.notifyDataSetChanged();
                    myRef.push().setValue(new ChatVO(id, other, sb.toString(), timeNow.format(today)));
                    edt.setText("");
                }
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//               if(dataSnapshot.getValue(ChatVO.class).getId().equals(other)&&dataSnapshot.getValue(ChatVO.class).getOther().equals(id)) {
//                   ChatVO value = dataSnapshot.getValue(ChatVO.class); // 괄호 안 : 꺼낼 자료 형태
//                   list.add(value);
//                   Log.d("guestPrint", dataSnapshot.getValue(ChatVO.class).getId());
//               }

                if(dataSnapshot.getValue(ChatVO.class).getId().equals(id)&&dataSnapshot.getValue(ChatVO.class).getOther().equals(other)) {
                    ChatVO value = dataSnapshot.getValue(ChatVO.class); // 괄호 안 : 꺼낼 자료 형태
                    list.add(value);
                    adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                    return;
                }
                if(dataSnapshot.getValue(ChatVO.class).getId().equals(other)&&dataSnapshot.getValue(ChatVO.class).getOther().equals(id)) {
                    ChatVO value = dataSnapshot.getValue(ChatVO.class); // 괄호 안 : 꺼낼 자료 형태
                    list.add(value);
                    adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                    return;
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}