package com.kakao.guide;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class UserAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    public ArrayList<UserHelperClass> userData;
    private LayoutInflater inflater;

    //ArrayList<String> people = new ArrayList<String>();
    //int peopleCoount = 0;

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    // 여기서 id는 채팅의 특성상 누군지 판가름 하려고 하는거다... 그 말은..
    // 굳이 필요는 없다././??

    public UserAdapter(Context applicationContext, int talklist, ArrayList<UserHelperClass> list) {
        this.context = applicationContext;
        this.layout = talklist;
        this.userData = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { // 전체 데이터 개수
        return userData.size();
    }

    @Override
    public Object getItem(int position) { // position번째 아이템
        return userData.get(position);
    }

    @Override
    public long getItemId(int position) { // position번째 항목의 id인데 보통 position
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //항목의 index, 전에 inflate 되어있는 view, listView
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("user");

        //첫항목을 그릴때만 inflate 함 다음거부터는 매개변수로 넘겨줌 (느리기때문) : recycle이라고 함
        final ViewHolder holder;

        if(convertView == null){                                                    //어떤 레이아웃을 만들어 줄 것인지, 속할 컨테이너, 자식뷰가 될 것인지
            convertView = inflater.inflate(layout, parent, false);      //아이디를 가지고 view를 만든다
            holder = new ViewHolder();

            holder.text_user_name = (TextView)convertView.findViewById(R.id.text_user_name);
            holder.text_user_phone = (TextView)convertView.findViewById(R.id.text_user_phone);
            holder.text_user_battery = (TextView)convertView.findViewById(R.id.text_user_battery);
            holder.text_user_gps = (TextView)convertView.findViewById(R.id.text_user_gps);
            holder.layout_user_color = (LinearLayout) convertView.findViewById(R.id.layout_user_color);

            convertView.setTag(holder);

        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        //상태가 새로 추가한건지? ==> 새로추가 레이아웃을 출력.
        if(position==0) {
            // 가이드일경우. 리스트에서 가이드 자체가 삭제는 안되지만 혹시 모르니까.
            Log.d("how", "가이드는 "+UserListActivity.code+"과 "+ userData.get(position).getCode());
            if(UserListActivity.code.equals(userData.get(position).getCode())) {
                Log.d("how", "가이드입니다.");
                holder.layout_user_color.setBackgroundResource(R.color.guide);
            }
        }else if(userData.get(position).getGender().equals("남자")) {
            holder.layout_user_color.setBackgroundResource(R.color.man);
        } else {
            holder.layout_user_color.setBackgroundResource(R.color.woman);
        }

        holder.text_user_name.setText(userData.get(position).getName());
        holder.text_user_phone.setText(userData.get(position).getPhone());
        holder.text_user_battery.setText(userData.get(position).getBattery());
        //holder.text_user_name.setText(userData.get(position).getName());

        return convertView;
    }

    //뷰홀더패턴
    public class ViewHolder{
        // 기존 리스트 출력.
        LinearLayout layout_user_color;
        TextView text_user_name;
        TextView text_user_phone;
        TextView text_user_battery;
        TextView text_user_gps;
    }
}