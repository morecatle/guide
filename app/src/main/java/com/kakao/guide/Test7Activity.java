package com.kakao.guide;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Test7Activity extends AppCompatActivity implements OnMapReadyCallback {
    private RecyclerView listview;
    private MainAdapter adapter;
    TextView text_position;
    ScheduleVO s;
    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference2 = database.getReference("travel");
    //Context context = getApplicationContext();

    Button putDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test7_layout);
        text_position = (TextView)findViewById(R.id.text_position);

        putDB = (Button)findViewById(R.id.putDB);
        //reference2 = rootNode.getReference("route");

        init();

        putDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootNode = FirebaseDatabase.getInstance();
//                reference = rootNode.getReference("user");
//
//                UserHelperClass helperClass = new UserHelperClass("DGEDCC54", "김길동","남자", "415121", "how@naver.com", "01088742245","35.151952,126.876622");
//                //reference.child("SFEGSD555").child("gps").setValue("35.141400,126.876272");
//                reference.child("DGEDCC54").setValue(helperClass);

                s = new ScheduleVO("제주도 여행", "ORF7623","none", "ORF7623", "");
                reference2.child("제주도 여행").setValue(s);

            }
        });
    }

    private void init() {
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("route");

        listview = findViewById(R.id.main_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);;

        final ArrayList<String> scheduleMainVO = new ArrayList<>();

        reference.child("이집트여행").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equals("line")) {
                    // 우선순위
                    MainAdapter.line = snapshot.getValue().toString();
                } else {
//                    scheduleList_list.add(snapshot.getKey()+"="+snapshot.getValue().toString());
//                    adapter.notifyDataSetChanged(); //데이터가 변경됐다고 알려주기.
                    scheduleMainVO.add(snapshot.getKey()+"="+snapshot.getValue());
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

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter = new MainAdapter(getApplicationContext(), scheduleMainVO, onClickItem);
                listview.setAdapter(adapter);

                MyListDecoration decoration = new MyListDecoration();
                listview.addItemDecoration(decoration);

                // 아이템 선택 시.
                adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        text_position.setText(adapter.itemList.get(position));
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            Toast.makeText(Test7Activity.this, str, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
