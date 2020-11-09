package com.kakao.guide;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;


public class ScheduleAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    public ArrayList<ScheduleVO> scheduleData;
    private LayoutInflater inflater;

    ArrayList<String> people = new ArrayList<String>();
    int peopleCoount = 0;

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    Intent intent;

    // 여기서 id는 채팅의 특성상 누군지 판가름 하려고 하는거다... 그 말은..
    // 굳이 필요는 없다././??

    public ScheduleAdapter(Context applicationContext, int talklist, ArrayList<ScheduleVO> list) {
        this.context = applicationContext;
        this.layout = talklist;
        this.scheduleData = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { // 전체 데이터 개수
        return scheduleData.size();
    }

    @Override
    public Object getItem(int position) { // position번째 아이템
        return scheduleData.get(position);
    }

    @Override
    public long getItemId(int position) { // position번째 항목의 id인데 보통 position
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //항목의 index, 전에 inflate 되어있는 view, listView
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("travel");

        //첫항목을 그릴때만 inflate 함 다음거부터는 매개변수로 넘겨줌 (느리기때문) : recycle이라고 함
        final ViewHolder holder;

        if(convertView == null){                                                    //어떤 레이아웃을 만들어 줄 것인지, 속할 컨테이너, 자식뷰가 될 것인지
            convertView = inflater.inflate(layout, parent, false);      //아이디를 가지고 view를 만든다
            holder = new ViewHolder();

            holder.text_schedule_name = (TextView)convertView.findViewById(R.id.text_schedule_name);
            holder.text_schedule_peple = (TextView)convertView.findViewById(R.id.text_schedule_peple);
            holder.layout_schedule_color = (LinearLayout) convertView.findViewById(R.id.layout_schedule_color);
            holder.layout_schedule_in = (LinearLayout)convertView.findViewById(R.id.layout_schedule_in);

            holder.layout_schedule_item = (LinearLayout)convertView.findViewById(R.id.layout_schedule_item);
            holder.layout_schedule_new = (LinearLayout)convertView.findViewById(R.id.layout_schedule_new);
            holder.edt_schedule_new = (EditText)convertView.findViewById(R.id.edt_schedule_new);
            holder.image_schedule_new = (ImageView) convertView.findViewById(R.id.image_schedule_new);
            holder.image_schedule_write = (ImageView) convertView.findViewById(R.id.image_schedule_write);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        // 상태가 새로 추가한건지? ==> 새로추가 레이아웃을 출력.
        if(scheduleData.get(position).getName().equals("null")) {
            holder.layout_schedule_item.setVisibility(View.GONE);
            holder.layout_schedule_new.setVisibility(View.VISIBLE);
        } else {
            holder.layout_schedule_new.setVisibility(View.GONE);
            holder.layout_schedule_item.setVisibility(View.VISIBLE);

            // 선택되었는지 판별.
            if(scheduleData.get(position).getVisible().equals("use")){
                holder.layout_schedule_color.setBackgroundResource(R.color.scheduleON);
            }else {
                Log.d("userCheck", "거침.");
                holder.layout_schedule_color.setBackgroundResource(R.color.scheduleOFF);
            }
            holder.text_schedule_name.setText(scheduleData.get(position).getName());

            peopleCoount = 0;
            // 팀원(people)를 숫자로 나타내기.
            for(int x=0; x< Arrays.asList(scheduleData.get(position).getPeople().split("\\,")).size(); x++) {
                peopleCoount+=1;
            }
            holder.text_schedule_peple.setText(peopleCoount+"");
        }

        // 입력완료 버튼을 눌렀을 때.
        holder.image_schedule_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 해당 리스트의 내용을 입력한 내용으로 바꿔주기.
                // 입력을 아무것도 안할 시.
                if(holder.edt_schedule_new.getText().toString().isEmpty()) {
                    Toast.makeText(context, "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                // 기존에 생성된 입력창은 제거.
                scheduleData.remove(position);

                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ScheduleVO scheduleVO = new ScheduleVO(holder.edt_schedule_new.getText().toString(), "","", "", "");
                        reference.child(holder.edt_schedule_new.getText().toString()).setValue(scheduleVO);
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


        return convertView;
    }

    //뷰홀더패턴
    public class ViewHolder{
        // 기존 리스트 출력.
        TextView text_schedule_name;
        TextView text_schedule_peple;
        LinearLayout layout_schedule_color;
        LinearLayout layout_schedule_in;

        // 새로운 아이템 출력.
        LinearLayout layout_schedule_item, layout_schedule_new;
        EditText edt_schedule_new;
        ImageView image_schedule_new, image_schedule_write;
    }
}