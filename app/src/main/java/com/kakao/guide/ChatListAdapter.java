package com.kakao.guide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ChatVO> chatData;
    private LayoutInflater inflater;
    private String id;

    // 상대방 아이디.
    String other = "";


    public ChatListAdapter(Context applicationContext, int talklist, ArrayList<ChatVO> list, String id) {
        this.context = applicationContext;
        this.layout = talklist;
        this.chatData = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.id= id;
    }

    public void removeItem() {
        chatData.remove(1);
    }

    @Override
    public int getCount() { // 전체 데이터 개수
        return chatData.size();
    }

    @Override
    public Object getItem(int position) { // position번째 아이템
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) { // position번째 항목의 id인데 보통 position
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //항목의 index, 전에 inflate 되어있는 view, listView
        //첫항목을 그릴때만 inflate 함 다음거부터는 매개변수로 넘겨줌 (느리기때문) : recycle이라고 함
        ChatListAdapter.ViewListHolder holder;

        if (convertView == null) {                                                    //어떤 레이아웃을 만들어 줄 것인지, 속할 컨테이너, 자식뷰가 될 것인지
            convertView = inflater.inflate(layout, parent, false);      //아이디를 가지고 view를 만든다
            holder = new ChatListAdapter.ViewListHolder();

            holder.list_id = (TextView) convertView.findViewById(R.id.list_id);
            holder.list_content = (TextView) convertView.findViewById(R.id.list_content);
            holder.list_time = (TextView) convertView.findViewById(R.id.list_time);

            convertView.setTag(holder);
        } else {
            holder = (ChatListAdapter.ViewListHolder) convertView.getTag();
        }

        // 리스트에서는 상대방의 아이디로 나오므로 getOther로 부름.
        holder.list_id.setText(chatData.get(position).getId());
        //holder.list_id.setText(other);
        holder.list_content.setText(chatData.get(position).getContent());
        holder.list_time.setText(chatData.get(position).getTime());

        return convertView;
    }
    //뷰홀더패턴
    public class ViewListHolder{
        TextView list_id;
        TextView list_content;
        TextView list_time;
    }
}
